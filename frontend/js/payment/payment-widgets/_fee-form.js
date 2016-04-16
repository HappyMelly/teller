'use strict';

/**
 * Form for updating contribution level
 * @param selector
 * @constructor
 */

export default class Widget {
    constructor(selector) {
        this.$root = $(selector);
        this.locals = this._getDom();

        this._assignEvents();
        this._updateAmount(this.locals.$inputFee);
    }

    _getDom() {
        const $root = this.$root;

        return {
            $inputFee: $root.find('.b-feestrip__input').first(),
            $taxPlace: $root.find('[data-fee-tax]'),
            $amountPlace: $root.find('[data-fee-amount]'),
            $payPlace: $root.find('[data-fee-pay]')
        }
    }

    _assignEvents() {
        const self = this;

        self.$root
            .on('change paste keyup', '.b-feestrip__input', function (e) {
                let $this = $(this);

                self._removeError($this);
                self._updateAmount($this);
            })
            .on('submit', self._onSubmitForm.bind(self));
    }

    _onSubmitForm(e) {
        const self = this;
        e.preventDefault();

        if (!this.isValidForm()) return;

        let feeAmount = this.locals.$inputFee.val();

        this._sendFeeData(feeAmount)
            .done(function (data) {
                if (data.hasOwnProperty("message")) {
                    success(data.message);
                }
            })
            .fail(function (data) {
                const errorMsg = JSON.parse(jqXHR.responseText);
                error(errorMsg.message);
            })
    }

    _setError($el) {
        const $parent = $el.parent();

        if (!$parent.hasClass('has-error')) {
            $parent.addClass('has-error');
        }
    }

    _removeError($el) {
        $el.parent().removeClass('has-error');
    }

    /**
     * Update tax and pay price on the from, based on input value $el
     * @param {jQuery} $el - $(input)
     * @private
     */
    _updateAmount($el) {
        let locals = this.locals,
            amount = $el.val() < 1 ? 0.00 : parseInt($el.val()),
            taxPercent = parseFloat($el.data('tax')),
            tax, amountWithTax;

        tax = (amount * taxPercent) / 100;
        amountWithTax = amount + tax;

        locals.$amountPlace.text(amount);
        locals.$taxPlace.text(tax);
        locals.$payPlace.text(amountWithTax);
    }

    /**
     * Check, is Form valid?
     * @returns {Boolean}
     */
    isValidForm() {
        let valid = true,
            $inputFee = this.locals.$inputFee,
            isValidAmount = ($inputFee.val().length > 0) && (!isNaN($inputFee.val()));

        if (!isValidAmount) {
            valid = false;
            this._setError($inputFee);
        }

        return valid;
    }

    //transport
    /**
     * @param {Number} feeAmount
     * @returns {$.Deffered} - promise
     * @private
     */
    _sendFeeData(feeAmount) {
        return $.ajax({
            type: "POST",
            url: this.$root.attr("action"),
            data: {fee: feeAmount},
            dataType: "json"
        })
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






