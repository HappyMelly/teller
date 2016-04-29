'use strict';

import FormHelper from './../../common/_form-helpers';

export default class Widget {
    constructor(selector) {
        this.$root = $(selector);
        this.locals = this._getDom();

        this.formHelper = new FormHelper({
            $controls: this.locals.$controls,
            rules: this._getRules()
        });
        this._assignEvents();
    }

    _getDom(){
        const $root = this.$root;

        return {
            $form: $root.find('form'),
            $controls: $root.find('input, select, textarea')
        }
    }

    _getRules(){
        return {
            name: {required: true},
            "defaults.fromEmail":   {required: true},
            "defaults.fromName":    {required: true},
            "defaults.subject":     {required: true},
            "defaults.language":    {required: true},
            reminder:               {required: true},
            "company.name":         {required: true},
            "company.address1":     {required: true},
            "company.zip":          {required: true},
            "company.city":         {required: true},
            "company.state":        {required: true},
            "company.countryCode":  {required: true},
            "allAttendees":         {required: true}
        }
    }

    _assignEvents(){
        this.locals.$form
            .on('submit', this._onSubmitForm.bind(this))
    }

    _onSubmitForm(e){
        if (!this.formHelper.isValidFormData()){
            e.preventDefault();
            return false;
        }
    }

    // static
    static plugin(selector, options) {
        const $elems = $(selector);
        if (!$elems.length) return;

        return $elems.each(function (index, el) {
            let $element = $(el);
            let data     = $element.data('widget.integration.mailchimp');

            if (!data) {
                data = new Widget(el, options);
                $element.data('widget.integration.mailchimp', data);
            }
        })
    }
}





