'use strict';

/**
 * Errors
 * @typedef {Object} ListErrors
 * @property {String} name - name of field
 * @property {String} error - error description
 */

export default class FormHelper {
    /**
     * Validate form through inputs
     * @param {jQuery} $controls
     */
    constructor($controls) {
        this.$controls = $controls;
        this.arrErrors = [];
        this._assignEvents();
    }

    _assignEvents() {
        this.$controls.on('input change', (e) => {
            const $control = $(e.currentTarget);

            this._validateImmediate($control);
            this._removeError($control);
        });
    }

    _validateImmediate($control){
        if ($control.hasClass('type-numeric')) {
            $control.val($control.val().replace(/[^\d]+/g, ''));
        }

        if ($control.hasClass('type-nospace')) {
            $control.val($control.val().replace(/\s/g, ''));
        }
    }

    isValidInputs() {
        const $controls = this.$controls;
        let error = 0;

        $controls.each((index, control) => {
            const $control = $(control);

            if (!this._isValidInput($control)) {
                error += 1;
            }
        });
        return Boolean(!error);
    }

    /**
     * Check given control, is it valid?
     * @param {jQuery} $control
     * @returns {boolean} - Is valid control?
     */
    _isValidInput($control) {
        const value = $.trim($control.val());

        if (!value && !$control.hasClass('type-optional')) {
            this._setError($control, 'Empty');
            return false;
        }

        if (($control.hasClass('type-email')) && !this._isValidEmail(value)) {
            this._setError($control, 'Email is not valid');
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
     * Set error for control
     * @param {jQuery} $control
     * @param {String} errorText
     * @param {Boolean} insertError
     */
    _setError($control, errorText, insertError = true) {
        const $parent = $control.parent();
        const $error = $parent.find('.b-error');

        if ($error.length) return;

        $parent.addClass('b-error_show');
        
        insertError && $('<div class="b-error" />')
            .text(errorText)
            .appendTo($parent);

        this.arrErrors.push({
            name: $control.attr('name'),
            error: errorText
        })
    }

    /**
     * Remove error for control
     * @param {jQuery} $control
     */
    _removeError($control) {
        const $parent = $control.parent();

        $parent
            .removeClass('b-error_show')
            .find('.b-error').remove();

        this.arrErrors = this.arrErrors.filter(function (item) {
            return item.name !== $control.attr('name')
        })
    }

    /**
     * Set errors
     * @param {Array} errors - [{name: "email", error: "empty"}, {name: "password", error: "empty"}]
     * @param {Boolean} insertError - insert error description to the Dom 
     */
    setErrors(errors, insertError = true) {
        errors.forEach((item) => {
            const $currentControl = this.$controls.filter('[name="' + item.name + '"]').first();

            if ($currentControl.length) this._setError($currentControl, item.error, insertError)
        })
    }

    /**
     * Get text version of errors in one line.
     * @param {ListErrors} errors
     * @returns {string}
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

    /**
     * Get list of errors with full title (from control title attribute)
     * @param {ListErrors} errors - list of errors
     * @returns {string}
     */
    getErrorsFull(errors) {
        const self = this;
        const arrErrors = errors || this.arrErrors;
        let errorTxt = '';

        arrErrors.forEach((item) => {
            const $control = self.$controls.filter(`[name="${item.name}"]`).first();
            const name = $control.length? $control.attr('title'): item.name;

            errorTxt += `<b>${name}</b>: ${item.error}.  <br><br>`;
        });

        return errorTxt;
    }

    getFormData(){
        let ajaxData = {};

        this.$controls.map((index, el) => {
            const $el = $(el);
            const name = $el.attr('name');

            if (!name) return;

            if ($el.is(':checkbox')){
                ajaxData[name] = $el.prop('checked')
            } else {
                ajaxData[name] = $el.val()
            }
        });

        return ajaxData;
    }

    /**
     * Remove all errors
     */
    removeErrors() {
        this.$controls.each((index, el) => {
            const $el = $(el);
            this._removeError($el)
        })
    }

    clearForm() {
        this.$controls.each((index, el) => {
            const $el = $(el);
            if (!$el.attr("disabled"))  $el.val('');
        })
    }
}
