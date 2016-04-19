
'use strict';

export default class FormHelper {
    /**
     * Validate given controls
     * @param {Object} options
     * @property {jQuery } initialData.$controls        - optional list of validating controls
     * @property { Object } initialData.rules           - optional list of validating controls;
     * @param {Object} messages
     */
    constructor(options, messages) {
        this.$controls = options.$controls;
        this.$errorUnderSubmit = options.$errorUnderSubmit;

        this.rules = $.extend({}, options.rules, this._getRulesFromHtml(this.$controls));
        this.restriction = $.extend({}, options.restriction, this._getRestrictionFromHtml(this.$controls))
        this.messages = messages || this._getDefaultMessages();
        this.errors = [];

        this._assignEvents();
    }

    _getDefaultMessages(){
        return {
            required: "This field is required.",
            email: "Please enter a valid email address.",
            url: "Please enter a valid URL.",
            date: "Please enter a valid date.",
            dateiso: "Please enter a valid date (ISO).",
            nospace: "Please enter a valid number.",
            digits: "Please enter only digits."
        }
    }

    /**
     * @param $controls
     * @returns {Object} - list of rules
     * @private
     */
    _getRulesFromHtml($controls){
        const self = this;
        let rules = {};

        $controls.each( (index, item)=>{
            const $item = $(item);
            const nameField = $item.attr('name');
            const possibleRules = self.messages;

            if (!$item.attr('class').match(/_validate-/i)) return;
            if (!rules[nameField]) rules[nameField] = {};

            for(let rule in possibleRules){
                const ruleClass = `_validate-${rule}`;

                if ($item.hasClass(ruleClass)){
                    rules[nameField][rule] = true;
                }
            }
        });
        return rules;
    }

    /**
     * @param $controls
     * @returns {Object} - list of rules
     * @private
     */
    _getRestrictionFromHtml($controls){
        const self = this;
        let restriction = {};

        $controls.each( (index, item)=>{
            const $item = $(item);
            const nameField = $item.attr('name');
            const possibleRestrict = self.messages;

            if (!$item.attr('class').match(/_restrict-/i)) return;
            if (!restriction[nameField]) restriction[nameField] = {};

            for(let restrict in possibleRestrict){
                const restrictClass = `_validate-${restrict}`;

                if ($item.hasClass(restrictClass)){
                    restriction[nameField][restrict] = true;
                }
            }
        });
        return restriction;
    }

    _assignEvents() {
        this.$controls
            .on('focus', this._onFocusControl.bind(this))
            .on('blur', this._onBlurControl.bind(this))
            .on('input', this._onInputControl.bind(this))
    }

    _onFocusControl(){
        const $el = $(e.currentTarget);
        this.$currentInput = $el;

    }

    _onBlurControl(){
        const $el = $(e.currentTarget);
        this._checkControl($el);
    }

    _onInputControl(){
        const $control = $(e.currentTarget);
        this._removeError($control);
        this._restrictInput($control);
    }

    _checkControl($control){
        const validation = this._validationControl($control);

        if (validation.isValid) {
            this._removeError($control);
            return;
        }

        this._setError($control, validation.message);
    }

    _restrictInput($control){
        const name = $control.attr('name');
        const restriction = this.restriction[name];
        let value = this._getValue($control);

        if (!restriction.length) return;

        for (let restict in restriction){
            value = this[`${restict}Restrict`](value);
        }
        this._setValue($control, value);
    }

    /**
     * Validate given control
     * @param {jQuery} $control - element
     * @returns {Object} = isValid(Boolean), message(String)
     * @private
     */
    _validationControl($control){
        const name = $control.attr('name');
        const rules = this.rules[name];
        const valueControl = this._getValue($control);
        let valid;

        if (!rules.length) return {
            isValid: false
        };

        for (let rule in rules){
            valid = this[`${rule}Validator`](valueControl, $control);

            if (!valid) return {
                isValid: false,
                message: this.messages[rule]
            };
        }

        return {
            isValid: true
        };
    }

    _getValue($control){
        return $control.val();
    }

    _setValue($control, value){
        $control.val(value);
    }

    isValidControls(){
        return !Boolean(this.errors.length);
    }

    /**
     * Set error for control
     * @param {jQuery} $control
     * @param {String} errorText
     * @param {Boolean} showBubble
     */
    _setError($control, errorText, showBubble = true) {
        const $parent = $control.parent();
        const $error = $parent.find('.b-error');

        if ($error.length) {
            $error.text(errorText);
        } else {
            $('<div class="b-error" />')
                .text(errorText)
                .appendTo($parent);
        }

        $parent.addClass(showBubble?'b-error_state_error': 'b-error_state_high');

        this.arrErrors.push({
            name: $control.attr('name'),
            error: errorText
        })
    }

    _removeError($control){
        const $parent = $control.parent();

        $parent.removeClass('b-error_state_error');
        setTimeout(() =>{
            $parent.find('b-error').remove();
        }, 300);

        this.errors = this.errors.filter(function (item) {
            return item.name !== $control.attr('name')
        })
    }

    /**
     * Show or hide last error
     * @param {Boolean} condition - true/show, false/hide
     * @private
     */
    _toggleLastError(condition){
        if (!this.errors.length) return;

        const lastError = this.errors[this.errors.length - 1];
        const $lastControl = $(`[name="${lastError.name}"]`).first().parent();

        if (!$lastControl.length) return;

        $lastControl
            .toggleClass('b-error_state_high', !condition)
            .toggleClass('b-error_state_error', condition)
    }

    /**
     * Set errors
     * @param {Array} errors - [{name: "email", error: "empty"}, {name: "password", error: "empty"}]
     */
    setErrors(errors) {
        let index;

        errors.forEach((item) => {
            const $currentControl = this.$controls.filter('[name="' + item.name + '"]').first();

            if (!$currentControl.length) return;

            if (index == 0){
                this._setError($currentControl, item.error);
                $('html, body').animate({
                    scrollTop: $currentControl.offset().top - 50
                }, 400)
            } else {
                this._setError($currentControl, item.error, false);
            }
        })
    }

    // validators
    requiredValidator(value, $el){
        if ($el.is('select')) {
            var val = $el.val();
            return val && val.length > 0;
        }
        return value.length > 0;
    }

    emailValidator(value, $el) {
        return /^[a-zA-Z0-9.!#$%&'*+\/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$/.test(value);
    }

    urlValidator(value, $el) {
        return /^(?:(?:(?:https?|ftp):)?\/\/)(?:\S+(?::\S*)?@)?(?:(?!(?:10|127)(?:\.\d{1,3}){3})(?!(?:169\.254|192\.168)(?:\.\d{1,3}){2})(?!172\.(?:1[6-9]|2\d|3[0-1])(?:\.\d{1,3}){2})(?:[1-9]\d?|1\d\d|2[01]\d|22[0-3])(?:\.(?:1?\d{1,2}|2[0-4]\d|25[0-5])){2}(?:\.(?:[1-9]\d?|1\d\d|2[0-4]\d|25[0-4]))|(?:(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)(?:\.(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)*(?:\.(?:[a-z\u00a1-\uffff]{2,})).?)(?::\d{2,5})?(?:[/?#]\S*)?$/i.test(value);
    }

    dateValidator(value, $el) {
        return !/Invalid|NaN/.test(new Date(value).toString());
    }

    dateisoValidator(value, $el) {
        return /^\d{4}[\/\-](0?[1-9]|1[012])[\/\-](0?[1-9]|[12][0-9]|3[01])$/.test(value);
    }

    // restriction
    digistsRestrict(value){
        return value.replace(/[^\d]+/g, '');
    }

    nospaceRestrict(value){
        return value.replace(/\s/g, '');
    }

    // Helper for form
    removeErrors() {
        this.$controls.each((index, el) => {
            const $el = $(el);
            this._removeError($el)
        })
    }

    getFormData(){
        let formData = {};

        this.$controls.map((index, el) => {
            const $el = $(el);
            const name = $el.attr('name');

            name && (formData[name] = $el.val())
        });

        return formData;
    }

    clearForm() {
        this.$controls.each((index, el) => {
            const $el = $(el);
            if (!$el.attr("disabled"))  $el.val('');
        })
    }

}
