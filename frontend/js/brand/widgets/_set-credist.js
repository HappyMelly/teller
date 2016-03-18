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
        this.validation = new FormHelper(this.locals.$input)

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
        this.$root
            .on('click', '[data-setcredit-activate]', this._onClickActivate.bind(this))
            .on('click', '[data-setcredit-deactivate]', this._onClickDeActivate.bind(this))
            .on('click', '[data-setcredit-form]', this._onClickSaveCredit.bind(this))
    }

    _onClickActivate(e) {
        e.preventDefault();

        this._sendActivate(this.brandId)
            .done(()=> {
                this.$root.addClass('b-setcredit_state_active');
            })
    }

    _onClickDeActivate(e) {
        e.preventDefault();

        this._sendDeActivate(this.brandId)
            .done(()=> {
                this.$root.removeClass('b-setcredit_state_active');
            })
    }

    _onClickSaveCredit(e) {
        e.preventDefault();

        if (!this.isFormValid()) return;

        this._sendFormData()
            .done(()=> {
                this.validation.clearForm();
                success('Set credits was successfull');
            })
            .fail((response)=> {
                const data = $.parseJSON(response.responseText).data;
                const errorText = this.validation.getErrorsText(data.errors);

                if (!data.errors) return;

                this.locals.$error.text(errorText);
                this.validation.setErrors(data.errors);
            })
    }

    isFormValid() {
        const locals = this.locals;
        const isValidCredit =  (locals.$input.val().length > 0);
        let valid = true;
        let errorText = '';

        if (isValidCredit){
            valid = false;
            errorText += 'Spending limit has to be above 0. We recomend set in 100.';
            this.validation._setError(locals.$input);
        }
        
        if (!valid){
            locals.$errors.text(errorText);
        }
        
        return valid;
    }

    // transport
    _sendActivate(brandId) {
        var url = '/activate/' + brandId;
        return $.post(url, {brandid: brandId});
    }

    _sendDeActivate(brandId) {
        var url = '/deactivate/' + brandId;
        return $.post(url, {brandid: brandId});
    }

    _sendFormData() {
        const locals = this.locals;

        return $.post(locals.$form.attr('action'), {
            credits: locals.$input.val()
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


