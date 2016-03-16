'use strict';

import FormHelper from "./../../common/_form-helper";

/**
 * Form for sending credit
 */
export default class FormCredit {

    constructor(selector) {
        this.$root = $(selector);
        this.locals = this._getDom();
        this.validation = new FormHelper(this.$root.find('input'));

        this._assignEvents();
    }

    _getDom() {
        const $root = this.$root;

        return {
            $count: $root.find('[data-credict-count]'),
            $value: $root.find('[data-credit-value]'),
            $to: $root.find('[data-credit-to]'),
            $message: $root.find('[data-credit-message]'),
            $error: $root.find('[data-credit-error]')
        };
    }

    _assignEvents() {
        this.$root
            .on('input', 'input', (e) => this.locals.$error.text(''))
            .on('submit', this._onSubmitForm.bind(this));
    }

    _onSubmitForm(e) {
        e.preventDefault();

        if (!this._isFormValid()) return false;

        this._sendRequest()
            .done(() => {
                this.validation.clearForm();

                this.$root.addClass('b-credits_state_send');
                setTimeout(()=> {
                    this.$root.removeClass('b-credits_state_send');
                }, 3000)
            })
            .fail((response) => {
                const data = $.parseJSON(response.responseText).data;
                const errorText = this.validation.getErrorsText(data.errors);

                if (!data.errors) return;
                
                this.locals.$error.text(errorText);
                this.validation.setErrors(data.errors);
            })
    }

    _isFormValid() {
        const locals = this.locals;
        const isEnoughCredits = Number(locals.$value.val()) <= Number(locals.$count.text());
        let valid = true;
        let errorText = '';

        if (!this.validation.isValidInputs()) {
            valid = false;
            errorText += this.validation.getErrorsText();
        }

        if (!isEnoughCredits) {
            valid = false;
            errorText += 'You cann’t give more than ' + locals.$count.text() + ' credits. ';
        }

        if (!valid) {
            locals.$error.text(errorText);
        }

        return valid;
    }

    _sendRequest() {
        return $.post(this.$root.attr('action'),
            {
                give: this.locals.$value.val(),
                to: this.locals.$to.val(),
                message: this.locals.$message.val()
            }
        );
    }
}
