(function ($, App) {
    'use strict';

    /**
     * Form for updating card information
     * @param selector
     * @constructor
     */
    function CardForm(selector){
        var self = this;

        self.$root = $(selector);
        self.locals = self._getDom();
        self.apikey = self.$root.data('apikey');

        self._getStripeScript()
            .done(function(){
                Stripe.setPublishableKey(self.apikey);
                self._init();
                self._assignEvents();
            })
    }

    CardForm.prototype._getStripeScript = function(){
        return $.ajax({
            url: 'https://js.stripe.com/v2/',
            dataType: "script"
        });
    };

    CardForm.prototype._getDom = function(){
        var $root = this.$root;

        return {
            $inputNumber: $root.find('[data-card-number]'),
            $inputName: $root.find('[data-card-name]'),
            $inputMonth: $root.find('[data-card-month]'),
            $inputYear: $root.find('[data-card-year]'),
            $inputCVC: $root.find('[data-card-cvc]'),
            $submit: $root.find('[data-card-submit]')
        }
    };

    CardForm.prototype._init = function(){
        if (!$.fn.payment){
            console.log('There is no payment plugin on this page');
            return;
        }

        this.locals.$inputNumber.payment('formatCardNumber');
        this.locals.$inputCVC.payment('formatCardCVC');

    };

    CardForm.prototype._assignEvents = function(){
        var self = this;

        self.$root
            .on('keyup', 'input', function(){
                self._removeError($(this));
            })
            .on('change paste keyup', '[data-card-name]', function(e){
                var $this = $(this);
                $this.val($this.val().toUpperCase())
            })
            .on('submit', self._onSubmitHandler.bind(self));
    };

    CardForm.prototype._setError = function($el){
        $el.parent().addClass('has-error');
    };

    CardForm.prototype._removeError = function($el){
        $el.parent().removeClass('has-error');
    };

    CardForm.prototype._disabledForm = function(){
        this.locals.$submit.prop('disabled', true);
        $("body").css("cursor", "progress");
    };

    CardForm.prototype._enabledForm = function(){
        this.locals.$submit.prop('disabled', false);
        $("body").css("cursor", "default");
    };

    CardForm.prototype._addTokenInput = function(token){
        var $root = this.$root,
            template = '<input type="hidden" value="' +  token +'" name="token" />';

        $root.find('input[name="token"]').remove()
        $root.append(template);
    };

    CardForm.prototype._onSubmitHandler = function(e){
        var self = this;
        e.preventDefault();

        if (!this.isValidForm()) return;
        this._disabledForm();

        Stripe.card.createToken(self.$root, self._stripeHandler.bind(self));
    };

    CardForm.prototype._stripeHandler = function (status, response) {
        var self = this,
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
                        error(msg);
                    }
                })
                .fail(function (jqXHR, status) {
                    if (status == "error") {
                        errorMsg = JSON.parse(jqXHR.responseText);
                        error(errorMsg.message);
                    } else {
                        error("Internal error #2000. Your card has not been charged. Please try again.");
                    }
                })
                .complete(function () {
                    self._enabledForm();
                });
        }
    };

    CardForm.prototype._sendFormData = function(dataForm){
        return $.ajax({
            type: "POST",
            url: this.$root.attr("action"),
            data: dataForm
        })
    };

    CardForm.prototype.isValidForm = function(){
        var self = this,
            valid = true,
            locals = this.locals,
            isValidNumber = $.payment.validateCardNumber(locals.$inputNumber.val()),
            isValidExpiry = $.payment.validateCardExpiry(locals.$inputMonth.val(), locals.$inputYear.val()),
            isValidCVC = $.payment.validateCardCVC(locals.$inputCVC.val()),
            isValidName = +locals.$inputName.val().length;

        if (!isValidNumber){
            self._setError(locals.$inputNumber);
            valid = false;
        }

        if (!isValidExpiry){
            self._setError(locals.$inputMonth);
            self._setError(locals.$inputYear);
            valid = false;
        }

        if (!isValidCVC){
            self._setError(locals.$inputCVC);
            valid = false;
        }

        if (!isValidName){
            self._setError(locals.$inputName);
            valid = false;
        }

        return valid;
    };


    App.widgets.CardForm = CardForm;
})(jQuery, App);