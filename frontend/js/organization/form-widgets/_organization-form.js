'use strict';

import FormHelper from './../../common/_form-helper';

export default class Widget {
    /**
     * @param {String} selector
     */
    constructor(selector) {
        this.$root = $(selector);
        this.locals = this._getDom();

        this.formHelper = new FormHelper(this.locals.$vatInput);
        
        this._checkVat();
        this._assignEvents();
    }

    _getDom() {
        const $root = this.$root;

        return {
            $cancel: $root.find('[data-form-cancel]'),
            $submit: $root.find('[data-form-submit]'),
            $vat: $root.find('[data-vat-block]'),
            $vatInput: $root.find('[data-vat-input]'),
            $vatText: $root.find('[data-vat-text]')
        };
    }

    _assignEvents() {
        this.locals.$vatInput
            .on('blur', this._checkVat.bind(this))
            .on('focus', this._hideVatError.bind(this))
    }

    _checkVat() {
        const self = this;
        const locals = self.locals;
        const valueVat = locals.$vatInput.val();

        locals.$vat.addClass('b-vat_state_checking');
        self._sendCheckVat(valueVat)
            .done(function(response){
                console.log(response);
                self._completeVat();
            })
            .fail(function(response){
                console.log(response);
                self._showVatError();
            })
    }

    _showVatError(){
        const locals = this.locals;

        locals.$submit.prop('disabled', true);
        locals.$vat
            .removeClass('b-vat_state_checking')
            .addClass('b-vat_state_error');
    }

    _hideVatError(){
        const locals = this.locals;

        locals.$vat.removeClass('b-vat_state_error');
    }

    _completeVat(){
        const locals = this.locals;

        locals.$submit.prop('disabled', false);
        locals.$vat
            .removeClass('b-vat_state_checking')
            .addClass('b-vat_state_complete');

        locals.$vatText.text('text');
    }

    //transport
    _sendCheckVat(value){
        const url = jsRoutes.controllers.Utilities.validateVAT(value).url;
        return $.post(url)
    }

    // static
    static plugin(selector) {
        const $elems = $(selector);
        if (!$elems.length) return;

        return $elems.each(function (index, el) {
            let $element = $(el);
            let data = $element.data('widget');

            if (!data) {
                data = new Widget(el);
                $element.data('widget', data);
            }
        })
    }
}


