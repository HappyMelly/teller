'use strict';

import FormHelper from './../../common/_form-helpers';

export default class Widget {
    constructor(selector, options) {
        this.$root = $(selector);
        this.locals = this._getDom();
        this.options = options;

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
        const self = this;

        e.preventDefault();
        if (!this.formHelper.isValidFormData()){
            return false;
        }

        const formData = self.formHelper.getFormData();
        self._createMailChimpList(this.options.url, formData)
            .done((data) => {
                window.location = data.redirect;
                success(data.message)
            })
            .fail((response) => {
                const jsonResponse = JSON.parse(response.responseText);
                const data = jsonResponse.data;

                if (!data.errors) return;
                self.formHelper.setErrors(data.errors);
            })
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

    _createMailChimpList(url, data){
        return $.ajax({
            type: "POST",
            url: url,
            data: data,
            dataType: "json"
        });
    }
}





