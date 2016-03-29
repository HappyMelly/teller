'use strict';

export default class FormHelper {
    /**
     * Validate form through inputs
     * @param {jQuery} $inputs
     */
    constructor($inputs) {
        this.$inputs = $inputs;
        this.arrErrors = [];
        this._assignEvents();
    }

    _assignEvents() {
        this.$inputs.on('input', (e) => {
            const $input = $(e.currentTarget);

            this._validateImmediate($input);
            this._removeError($input);
        });
    }

    _validateImmediate($input){
        if ($input.hasClass('type-numeric')) {
            $input.val($input.val().replace(/[^\d]+/g, ''));
        }
    }

    isValidInputs() {
        const $inputs = this.$inputs;
        let error = 0;

        $inputs.each((index, input) => {
            const $input = $(input);

            if (!this._isValidInput($input)) error += 1;
        });
        return Boolean(!error);
    }

    /**
     * Check given input, is it valid?
     * @param {jQuery} $input
     * @returns {boolean} - Is valid input?
     */
    _isValidInput($input) {
        const value = $.trim($input.val());

        if (!value) {
            this._setError($input, 'Empty');
            return false;
        }

        if (($input.hasClass('type-email')) && !this._isValidEmail(value)) {
            this._setError($input, 'Email is not valid');
            return false;
        }

        return true;
    }

    /**
     * Is Email valid?
     * @param {string} email
     * @returns {boolean}
     */
    _isValidEmail(email) {
        var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        return re.test(email);
    }

    /**
     * Set error for input
     * @param {jQuery} $input
     * @param {string} errorText
     */
    _setError($input, errorText) {
        const $parent = $input.parent();
        const $error = $parent.find('.b-error');

        if ($error.length) return;

        $parent.addClass('b-error_show');
        $('<div class="b-error" />')
            .text(errorText)
            .appendTo($parent);

        this.arrErrors.push({
            name: $input.attr('name'),
            error: errorText
        })
    }

    /**
     * Remove error for input
     * @param {jQuery} $input
     */
    _removeError($input) {
        const $parent = $input.parent();

        $parent
            .removeClass('b-error_show')
            .find('.b-error').remove();

        this.arrErrors = this.arrErrors.filter(function (item) {
            return item.name !== $input.attr('name')
        })
    }

    /**
     * Set errors
     * @param {Array} errors - [{name: "email", error: "empty"}, {name: "password", error: "empty"}]
     */
    setErrors(errors) {
        errors.forEach((item) => {
            const $currentInput = this.$inputs.filter('[name="' + item.name + '"]').first();

            if ($currentInput.length) this._setError($currentInput, item.error)
        })
    }

    /**
     * Get txt version of all errors
     */
    getErrorsText(errors) {
        const arrErrors = errors || this.arrErrors;
        let errorTxt = '';

        arrErrors.forEach((item) => {
            const name = item.name[0].toUpperCase() + item.name.substr(1);

            errorTxt += `${name}: ${item.error}. `;
        });

        return errorTxt;
    }

    getFormData(){
        let ajaxData = {};

        this.$inputs.map((index, el) => {
            const $el = $(el);
            const name = $el.attr('name');

            name && (ajaxData[name] = $el.val())
        });

        return ajaxData;
    }

    /**
     * Remove all errors
     */
    removeErrors() {
        this.$inputs.each((index, el) => {
            const $el = $(el);
            this._removeError($el)
        })
    }

    clearForm() {
        this.$inputs.each((index, el) => {
            const $el = $(el);
            if (!$el.attr("disabled"))  $el.val('');
        })
    }
}
