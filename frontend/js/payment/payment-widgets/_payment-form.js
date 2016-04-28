'use strict';

import InputChecking from './../../common/_input-checking';

/**
 * Form for payment with fee
 * @param selector
 * @constructor
 */
export default class Widget {
    constructor(selector) {
        const self = this;

        self.$root = $(selector);
        self.locals = self._getDom();
        self.apikey = self.$root.data('apikey');

        self.coupon = new InputChecking({
            $root: this.$root.find('.b-inputcheck'),
            url: jsRoutes.controllers.core.Coupons.get
        });

        self._getStripeScript()
            .done(function () {
                Stripe.setPublishableKey(self.apikey);
                self._init();

                self._updateAmount(self.locals.$inputFee);
                self._assignEvents();
            })
    }

    _getStripeScript() {
        return $.ajax({
            url: 'https://js.stripe.com/v2/',
            dataType: "script"
        });
    }

    _getDom() {
        const $root = this.$root;

        return {
            $inputNumber: $root.find('[data-card-number]'),
            $inputName: $root.find('[data-card-name]'),
            $inputMonth: $root.find('[data-card-month]'),
            $inputYear: $root.find('[data-card-year]'),
            $inputCVC: $root.find('[data-card-cvc]'),
            $inputCoupon: $root.find('[data-payment-coupon]'),
            $submit: $root.find('[data-payment-submit]'),
            $inputFee: $root.find('[data-payment-fee]'),
            $taxPlace: $root.find('[data-payment-tax]'),
            $amountPlace: $root.find('[data-payment-amount]'),
            $payPlace: $root.find('[data-payment-price]'),
            $discountCaption: $root.find('[data-payment-discount]'),
            $discountPlace: $root.find('[data-payment-discount-amount]')
        }
    }

    _init() {
        if (!$.fn.payment) {
            console.log('There is no payment plugin on this page');
            return;
        }

        this.locals.$inputNumber.payment('formatCardNumber');
        this.locals.$inputCVC.payment('formatCardCVC');
        this.locals.$discountCaption.hide();
    }

    _assignEvents() {
        const self = this;

        self.$root
            .on('change paste keyup', '[data-payment-fee]', function (e) {
                var $this = $(this);

                self._removeError($this);
                self._updateAmount($this);
            })
            .on('change paste keyup', '[data-payment-coupon]', function (e) {
                self._updateAmount(self.locals.$inputFee);
            })
            .on('keyup', 'input', function () {
                self._removeError($(this));
            })
            .on('change paste keyup', '[data-card-name]', function (e) {
                var $this = $(this);
                $this.val($this.val().toUpperCase())
            })
            .on('submit', self._onSubmitHandler.bind(self));

        App.events
            .sub('hmt.inputcheck.complete', ()=> {
                self._updateAmount(self.locals.$inputFee);
            });
    }

    _updateAmount($el) {
        let locals = this.locals,
            amount = $el.val() < 1 ? 0.00 : parseInt($el.val()),
            taxPercent = parseFloat($el.data('tax')),
            discount = this._getDiscount(),
            revertDiscount = 1 - discount / 100,
            tax, amountWithTax;

        tax = ((amount * taxPercent) / 100) * revertDiscount;
        amountWithTax = amount * revertDiscount + tax;

        locals.$amountPlace.text(amount);
        locals.$taxPlace.text(tax);
        locals.$payPlace.text(amountWithTax);
        locals.$discountPlace.text(discount);
        if (discount > 0) {
            locals.$discountCaption.show();
        } else {
            locals.$discountCaption.hide();
        }
    }

    _getDiscount() {
        if (this.coupon.data.hasOwnProperty("discount")) {
            return this.coupon.data.discount;
        } else {
            return 0;
        }
    }

    _setError($el) {
        $el.parent().addClass('has-error');
    }

    _removeError($el) {
        $el.parent().removeClass('has-error');
    }

    _disabledForm() {
        this.locals.$submit.prop('disabled', true);
        $("body").css("cursor", "progress");
    }

    _enabledForm() {
        this.locals.$submit.prop('disabled', false);
        $("body").css("cursor", "default");
    }

    _addTokenInput(token) {
        let $root = this.$root,
            template = '<input type="hidden" value="' + token + '" name="token" />';

        $root.find('input[name="token"]').remove();
        $root.append(template);
    };

    _onSubmitHandler(e) {
        const self = this;
        e.preventDefault();

        if (!this.isValidForm()) return;
        this._disabledForm();

        Stripe.card.createToken(self.$root, self._stripeHandler.bind(self));
    };

    _stripeHandler(status, response) {
        let self = this,
            data, errorMsg;

        if (response.error) {
            self._enabledForm();
        } else {

            self._addTokenInput(response.id);

            data = this.$root.serialize();
            self._sendFormData(data)
                .done(function (data) {
                    if (data.hasOwnProperty("redirect")) {
                        window.location = data.redirect;
                    } else {
                        var msg = "Internal error #2001. Your card has been charged. ";
                        msg += "Do not make payment again. Please proceed to your profile directly.";
                        error(msg, 4500);
                    }
                })
                .fail(function (jqXHR, status) {
                    if (status == "error") {
                        errorMsg = JSON.parse(jqXHR.responseText);
                        error(errorMsg.message, 4500);
                    } else {
                        error("Internal error #2000. Your card has not been charged. Please try again.", 4500);
                    }
                })
                .complete(function () {
                    self._enabledForm();
                });
        }
    };

    _sendFormData(dataForm) {
        return $.ajax({
            type: "POST",
            url: this.$root.attr("action"),
            data: dataForm
        })
    };

    isValidForm() {
        let self = this,
            valid = true,
            locals = this.locals,
            isValidNumber = $.payment.validateCardNumber(locals.$inputNumber.val()),
            isValidExpiry = $.payment.validateCardExpiry(locals.$inputMonth.val(), locals.$inputYear.val()),
            isValidCVC = $.payment.validateCardCVC(locals.$inputCVC.val()),
            isValidName = +locals.$inputName.val().length,
            isValidAmount = (locals.$inputFee.val().length > 0) && (!isNaN(locals.$inputFee.val()));

        if (!isValidNumber) {
            self._setError(locals.$inputNumber);
            valid = false;
        }

        if (!isValidExpiry) {
            self._setError(locals.$inputMonth);
            self._setError(locals.$inputYear);
            valid = false;
        }

        if (!isValidCVC) {
            self._setError(locals.$inputCVC);
            valid = false;
        }

        if (!isValidName) {
            self._setError(locals.$inputName);
            valid = false;
        }

        if (!isValidAmount) {
            valid = false;
            this._setError(locals.$inputFee);
        }

        return valid;
    }

    // static
    static plugin(selector, options) {
        const $elems = $(selector);
        if (!$elems.length) return;

        return $elems.each(function (index, el) {
            let $element = $(el);
            let data = $element.data('widget.scrollto');

            if (!data) {
                data = new Widget(el, options);
                $element.data('widget', data);
            }
        })
    }
}

