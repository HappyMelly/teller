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

        this._assignEvents();

        this._correctOldVatInput();
        this._checkVat();
    }

    _getDom() {
        const $root = this.$root;

        return {
            $cancel: $root.find('[data-form-cancel]'),
            $submit: $root.find('[data-form-submit]'),
            $vat: $root.find('[data-vat-block]'),
            $vatInput: $root.find('[data-vat-input]'),
            $vatError: $root.find('[data-vat-error]'),
            $vatText: $root.find('[data-vat-text]')
        };
    }

    _assignEvents() {
        this.locals.$vatInput
            .on('blur', this._checkVat.bind(this))
            .on('focus', this._hideVatError.bind(this))
    }

    _correctOldVatInput(){
        const $vatInput = this.locals.$vatInput;

        $vatInput.val($vatInput.val().replace(/\s/g, ''));
    }

    _checkVat() {
        const self = this;
        const locals = self.locals;
        const valueVat = locals.$vatInput.val();

        if (!locals.$vatInput.val()){
            self._showVatError('Empty vat value');
            return;
        }

        locals.$vat
            .removeClass('b-vat_state_complete b-vat_state_error')
            .addClass('b-vat_state_checking');

        self._sendCheckVat(valueVat)
            .done(function(response){
                const vatText = $.parseJSON(response).message;
                self._completeVat(vatText);
            })
            .fail(function(response){
                const error = $.parseJSON(response.responseText).message;
                self._showVatError(error);
            })
    }

    /**
     *
     * @param {String} error
     * @private
     */
    _showVatError(error){
        const locals = this.locals;

        this._disabledForm();
        locals.$vat
            .removeClass('b-vat_state_checking')
            .addClass('b-vat_state_error');

        locals.$vatError.text(error);
    }

    _hideVatError(){
        const locals = this.locals;

        locals.$vat.removeClass('b-vat_state_error');
    }

    /**
     * Show vat text
     * @param {String} vatText
     * @private
     */
    _completeVat(vatText){
        const locals = this.locals;

        this._enabledForm();
        locals.$vat
            .removeClass('b-vat_state_checking')
            .addClass('b-vat_state_complete');

        locals.$vatText.text(vatText);
    }

    _disabledForm(){
        locals.$submit.prop('disabled', true);
    }

    _enabledForm(){
        locals.$submit.prop('disabled', false);
    }

    //transport
    _sendCheckVat(value){
        const url = jsRoutes.controllers.Utilities.validateVAT(value).url;
        return $.get(url)
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


