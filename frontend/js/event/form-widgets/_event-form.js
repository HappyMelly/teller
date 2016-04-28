'use strict';

import InputChecking from './../../common/_input-checking';

export default class Widget {
    /**
     * @param {String} selector
     */
    constructor(selector) {
        this.$root = $(selector);
        this.locals = this._getDom();

        this.inputOrg = new InputChecking({
            $root: this.$root.find('.js-formgroup-org').first(),
            url: jsRoutes.controllers.Utilities.validate
        });
        this.inputReg = new InputChecking({
            $root: this.$root.find('.js-formgroup-reg').first(),
            url: jsRoutes.controllers.Utilities.validate
        });

        this._assignEvents();
    }

    _getDom() {
        const $root = this.$root;

        return {
            $cancel: $root.find('[data-form-cancel]'),
            $submit: $root.find('[data-form-submit]')
        };
    }

    _assignEvents() {
        this.$root
            .on('input_checking.change', this._onBlurVatInput.bind(this));
    }

    _onBlurVatInput(){
        this._enabledForm();
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


