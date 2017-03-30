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
        this._updateAmount(this.locals.$plan.filter(':checked'));
    }

    _getDom() {
        const $root = this.$root;

        return {
            $plan: $root.find('[data-payment-plan]'),
            $taxPlace: $root.find('[data-fee-tax]'),
            $amountPlace: $root.find('[data-fee-amount]'),
            $payPlace: $root.find('[data-fee-pay]')
        }
    }

    _assignEvents() {
        const self = this;

        self.$root
            .on('change', '[data-payment-plan]', function (e) {
                let $this = $(this);

                self._removeError($this);
                self._updateAmount($this);
            })
            .on('submit', self._onSubmitForm.bind(self));
    }

    _onSubmitForm(e) {
        const self = this;
        e.preventDefault();

        let yearly = this.locals.$plan.filter(':checked').val();

        this._sendFeeData(yearly)
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
            amount = parseFloat($el.data('amount')),
            taxPercent = parseFloat($el.data('tax')),
            tax, amountWithTax;

        tax = (amount * taxPercent) / 100;
        amountWithTax = amount + tax;

        locals.$amountPlace.text(amount);
        locals.$taxPlace.text(tax.toFixed(2));
        locals.$payPlace.text(amountWithTax.toFixed(2));
    }


    //transport
    /**
     * @param {Boolean} yearly
     * @returns {$.Deffered} - promise
     * @private
     */
    _sendFeeData(yearly) {
        return $.ajax({
            type: "POST",
            url: this.$root.attr("action"),
            data: {yearly: yearly},
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






