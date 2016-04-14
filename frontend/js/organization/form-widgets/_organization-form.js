'use strict';

import InputChecking from './../../common/_input-checking';
import FormHelper from './../../common/_form-helper';

export default class Widget {
    /**
     * @param {String} selector
     */
    constructor(selector) {
        this.$root = $(selector);
        this.locals = this._getDom();
        this._correctOldVatInput();

        this.inputVAT = new InputChecking({
            $root: this.$root.find('.b-inputcheck'),
            url: jsRoutes.controllers.Utilities.validateVAT
        });
        this.formHelper = new FormHelper(this.locals.$vatInput);

        this._assignEvents();
    }

    _correctOldVatInput(){
        const $vatInput = this.locals.$vatInput;

        $vatInput
            .addClass('type-nospace')
            .val($vatInput.val().replace(/\s/g, ''));
    }

    _getDom() {
        const $root = this.$root;

        return {
            $cancel: $root.find('[data-form-cancel]'),
            $submit: $root.find('[data-form-submit]'),
            $vatInput: $root.find('.b-inputcheck input').first()
        };
    }

    _assignEvents() {
        this.$root
            .on('input_checking.change', this._onBlurVatInput.bind(this));
    }

    _onBlurVatInput(){
        if (this.inputVAT.isValid()){
            this._enabledForm();
        } else {
            this._disabledForm();
        }
    }

    _disabledForm(){
        this.locals.$submit.prop('disabled', true);
    }

    _enabledForm(){
        this.locals.$submit.prop('disabled', false);
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


