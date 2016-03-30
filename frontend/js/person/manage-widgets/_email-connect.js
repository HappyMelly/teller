'use strict';

import FormHelper from "./../../common/_form-helper";

export default class Widget {
    constructor(selector, options) {
        this.$root = $(selector);
        this.locals = this._getDom();
        this.options = $.extend({}, options, this.$root.data());

        this.validation = new FormHelper(this.locals.$inputs);
        this._assignEvents();
    }

    _getDom(){
        const $root = this.$root;
        return {
            $form: $root.find('form'),
            $inputs: $root.find('form input')
        };
    }

    _assignEvents(){
        this.locals.$form.on('submit', this._onSubmitForm.bind(this));
        this.$root.on('hide.bs.modal', this._onHideModal.bind(this));
    }

    _onSubmitForm(e){
        const self = this;
        e.preventDefault();

        if(!self.validation.isValidInputs()) return;

        const formData = self.validation.getFormData();
        self._sendData(formData)
            .done(function(){
                self.$root.modal('hide');
                self.validation.clearForm();

                self.$root.trigger('hmt.emailconnect.success');
            })
            .fail(function(response){
                const data = $.parseJSON(response.responseText).data;

                if (!data.errors) return;
                self.validation.setErrors(data.errors);
            })
    }

    _onHideModal(){
        this.validation.clearForm();
        this.validation.removeErrors();
    }

    _sendData(data){
        return $.post(this.options.url, data);
    }

    // static
    static plugin(selector, options) {
        const $elems = $(selector);
        if (!$elems.length) return;

        return $elems.each(function (index, el) {
            let $element = $(el);
            let data     = $element.data('widget.scrollto');

            if (!data) {
                data = new Widget(el, options);
                $element.data('widget', data);
            }
        })
    }
}





