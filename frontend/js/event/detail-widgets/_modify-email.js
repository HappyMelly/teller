'use strict';

import FormHelper from "./../../common/_form-helper";

export default class Widget {
    constructor(selector){
        this.$root = $(selector);
        this.locals = this._getDom();
        this.formHelper = new FormHelper(this.locals.$textarea);

        this._assignEvent();
    }

    _getDom(){
        const $root = this.$root;

        return {
            $link: $root.find('[data-emailmod-link]'),
            $modal: $root.find('[data-emailmod-dlg]'),
            $form: $root.find('[data-emailmod-form]'),
            $textarea: $root.find('[data-emailmod-textarea]'),
            $cancel: $root.find('[data-emailmod-cancel]')
        }
    }

    _assignEvent(){
        this.$root
            .on('click', '[data-emailmod-link]', this._onClickShowModal.bind(this))
            .on('click', '[data-emailmod-mark]', this._onClickUseTemplate.bind(this))
            .on('click', '[data-emailmod-cancel]', this._onClickCancel.bind(this))

        this.locals.$form.on('submit', this._onSubmitForm.bind(this));
    }

    _onClickShowModal(e){
        e.preventDefault();
        this.locals.$modal.modal('show');
    }

    _onClickUseTemplate(e){
        e.preventDefault();
    }

    _onClickCancel(e){
        e.preventDefault();

        this.formHelper.clearForm();
        this.formHelper.removeErrors();
        this.locals.$modal.modal('hide');
    }

    _onSubmitForm(e){
        e.preventDefault();
        const self = this;

        if (!self.formHelper.isValidInputs()) return;
        const formData = self.formHelper.getFormData();

        self._sendEmailContent(formData)
            .done(()=>{
                self.formHelper.clearForm();
                self.locals.$modal.modal('hide');

                success('Email is modified');
            })
            .fail((response)=>{
                const data = $.parseJSON(response.responseText).data;

                if (!data.errors) return;

                self.formHelper.setErrors(data.errors);
            })
    }

    //transport
    _sendEmailContent(formData){
        const url = this.locals.$form.attr('action');
        return $.post(url, formData);
    }

    // static
    static plugin(selector) {
        const $elems = $(selector);
        if (!$elems.length) return;

        return $elems.each(function (index, el) {
            let $element = $(el);
            let data     = $element.data('hmt.events.modify_email');

            if (!data) {
                data = new Widget(el);
                $element.data('widget', data);
            }
        })
    }
}

