(function ($, App) {
    'use strict';

    /**
     * Form for updating contribution level
     * @param selector
     * @constructor
     */
    function FeeForm(selector) {
        this.$root = $(selector);
        this.locals = this._getDom();

        this._assignEvents();
        this._updateAmount(this.locals.$inputFee);
    }

    FeeForm.prototype._getDom = function () {
        var $root = this.$root;

        return {
            $inputFee: $root.find('.b-feestrip__input').first(),
            $taxPlace: $root.find('[data-fee-tax]'),
            $amountPlace: $root.find('[data-fee-amount]'),
            $payPlace: $root.find('[data-fee-pay]')
        }
    };

    FeeForm.prototype._assignEvents = function () {
        var self = this;

        self.$root
            .on('change paste keyup', '.b-feestrip__input', function (e) {
                var $this = $(this);
                
                self._removeError($this);
                self._updateAmount($this);
            })
            .on('submit', self._onSubmitForm.bind(self));
    };

    FeeForm.prototype._onSubmitForm = function(e){
        var self = this;
        e.preventDefault();
        
        if (!this.isValidForm()) return;

        var feeAmount = this.locals.$inputFee.val();

        this._sendFeeData(feeAmount)
            .done(function (data) {
                if (data.hasOwnProperty("message")) {
                    success(data.message);
                }
            })
            .fail(function(data){
                var errorMsg = JSON.parse(jqXHR.responseText);
                error(errorMsg.message);
            })
    };

    FeeForm.prototype._setError = function($el){
        var $parent = $el.parent();

        if (!$parent.hasClass('has-error')){
            $parent.addClass('has-error');
        }
    };

    FeeForm.prototype._removeError = function($el){
        $el.parent().removeClass('has-error');
    };

    /**
     * Update tax and pay price on the from, based on input value $el
     * @param {jQuery} $el - $(input)
     * @private
     */
    FeeForm.prototype._updateAmount = function($el){
        var locals = this.locals,
            amount = $el.val() < 1? 0.00: parseInt($el.val()),            
            taxPercent = parseFloat($el.data('tax')),
            tax, amountWithTax;
    
        tax = (amount * taxPercent) / 100;
        amountWithTax = amount + tax;
        
        locals.$amountPlace.text(amount);
        locals.$taxPlace.text(tax);
        locals.$payPlace.text(amountWithTax);
    };

    /**
     * Check, is Form valid?
     * @returns {boolean}
     */
    FeeForm.prototype.isValidForm = function () {
        var valid = true,
            $inputFee = this.locals.$inputFee,
            isValidAmount = ($inputFee.val().length > 0) && (!isNaN($inputFee.val()));

        if (!isValidAmount){
            valid = false;
            this._setError($inputFee);
        }

        return valid;
    };    
    
    //transport 
    /**
     * 
     * @param {Number} feeAmount
     * @returns {$.Deffered} - promise
     * @private
     */
    FeeForm.prototype._sendFeeData = function(feeAmount){
        return $.ajax({
            type: "POST",
            url: this.$root.attr("action"),
            data: {fee: feeAmount},
            dataType: "json"
        })
    };

    App.widgets.FeeForm = FeeForm;
})(jQuery, App);