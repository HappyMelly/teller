
'use strict';

export default class FormHelper {
    /**
     * Validate given controls
     * @param {Object} options
     * @property {jQuery } initialData.$controls        - optional list of validating controls
     * @property { Object } initialData.rules           - optional list of validating controls;
     * @property { jQuery } initialData.$errorUnderSubmit - placeholder for error under submit button
     * @param {Object} messages
     */
    constructor(options, messages) {
        this.$controls = options.$controls;
        this.$errorUnderSubmit = options.$errorUnderSubmit;

        this.rules = $.extend({}, options.rules, this._getRulesFromHtml(this.$controls));
        this.messages = messages || this._getDefaultMessages();

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
                    rules[nameField].rule = true;
                }
            }
        });
        return rules;
    }

    _assignEvents() {
        this.$controls
            .on('focus', this._onFocusControl.bind(this))
            .on('blur', this._onBlurControl.bind(this))
            .on('input change', this._onChangeControl.bind(this))
    }

    _onFocusControl(){
        
    }

    _onBlurControl(){
        
    }

    _onChangeControl(){
        
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
    digistsRestrict


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
