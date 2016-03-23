'use strict';

import FormHelper from "./../../common/_form-helper";


export default class Widget {
    /**
     * Filter history
     * @param {String} selector
     */
    constructor(selector) {
        this.$root = $(selector);
        this.locals = this._getDom();
        this.brandId = this.$root.data('brand-id');
        this.validation = new FormHelper(this.locals.$input);

        this._assignEvents();
    }

    _getDom() {
        const $root = this.$root;

        return {
            $activateBtn: $root.find('[data-setcredit-activate]'),
            $deActivateBtn: $root.find('[data-setcredit-deactivate]'),
            $form: $root.find('[data-setcredit-form]'),
            $input: $root.find('[data-setcredit-input]'),
            $errors: $root.find('[data-setcredit-errors]')
        };
    }

    _assignEvents() {
        const self = this;

        this.$root
            .on('click', '[data-setcredit-activate]', this._onClickActivate.bind(this))
            .on('click', '[data-setcredit-deactivate]', this._onClickDeActivate.bind(this))
            .on('submit', '[data-setcredit-form]', this._onClickSaveCredit.bind(this))
            .on('input', '[data-setcredit-input]', (e) => self.locals.$errors.text(''))
    }

    _onClickActivate(e) {
        const self = this;
        e.preventDefault();

        self._sendActivate(self.brandId)
            .done(()=> {
                self.$root.addClass('b-setcredit_state_active');
            })
    }

    _onClickDeActivate(e) {
        const self = this;
        e.preventDefault();

        self._sendDeActivate(self.brandId)
            .done(()=> {
                self.$root.removeClass('b-setcredit_state_active');
            })
    }

    _onClickSaveCredit(e) {
        const self = this;
        e.preventDefault();

        if (!self.isFormValid()) return;

        self._sendFormData()
            .done(()=> {
                self.validation.clearForm();
                success('Credit limit was updated');
            })
            .fail((response)=> {
                const data = $.parseJSON(response.responseText).data;
                const errorText = self.validation.getErrorsText(data.errors);

                if (!data.errors) return;

                self.locals.$error.text(errorText);
                self.validation.setErrors(data.errors);
            })
    }

    isFormValid() {
        const locals = this.locals;
        const isValidCredit = (locals.$input.val() > 0);
        let valid = true;
        let errorText = '';

        if (!isValidCredit){
            valid = false;
            errorText += 'Spending limit has to be above 0. We recommend set in 100.';
            this.validation._setError(locals.$input);
        }
        
        if (!valid){
            locals.$errors.text(errorText);
        }
        
        return valid;
    }

    // transport
    _sendActivate(brandId) {
        var url = jsRoutes.controllers.cm.brand.Credits.activate(brandId).url;
        return $.post(url, {brandid: brandId});
    }

    _sendDeActivate(brandId) {
        var url = jsRoutes.controllers.cm.brand.Credits.deactivate(brandId).url;
        return $.post(url, {brandid: brandId});
    }

    _sendFormData() {
        const locals = this.locals;

        return $.post(locals.$form.attr('action'), {
            limit: locals.$input.val()
        })
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


