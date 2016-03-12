(function ($, App) {
    'use strict';

    function FeeForm(selector) {
        this.$root = $(selector);
        this.locals = this._getDom();

        this._assignEvents();
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
        e.preventDefault();
        
        if (!this.isValidForm()) return;

        this._sendFeeData()
            .done(function () {
                success('Succefully update payment');
            })
            .fail(function(data){
                error('Something wrong just happend');
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
    FeeForm.prototype._sendFeeData = function(){
        var url = this.$root.attr('action'),
            feeAmount = this.locals.$inputFee.val();
        
        return $.post(url, {fee: feeAmount})
    };

    App.widgets.FeeForm = FeeForm;
})(jQuery, App);