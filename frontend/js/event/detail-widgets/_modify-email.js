'use strict';

import FormHelper from "./../../common/_form-helper";

export default class Widget {
    constructor(selector){
        this.$root = $(selector);
        this.locals = this._getDom();
        this.formHelper = new FormHelper(this.locals.$textarea);
        this.template = this.locals.$textarea.val();

        this._assignEvent();
    }

    _getDom(){
        const $root = this.$root;

        return {
            $link: $root.find('[data-emailmod-link]'),
            $defaultTemplate: $root.find('[data-emailmod-default]'),
            $modal: $root.find('[data-emailmod-dlg]'),
            $form: $root.find('[data-emailmod-form]'),
            $textarea: $root.find('[data-emailmod-textarea]'),
            $cancel: $root.find('[data-emailmod-cancel]')
        }
    }

    _assignEvent(){
        const self = this;

        this.$root
            .on('click', '[data-emailmod-link]', this._onClickShowModal.bind(this))
            .on('click', '[data-emailmod-mark]', this._onClickUseTemplate.bind(this))
            .on('hide.bs.modal', (e)=>{
                e.stopPropagation();
                self._onCloseModal();
            });

        this.locals.$form.on('submit', this._onSubmitForm.bind(this));
    }

    _onClickShowModal(e){
        e.preventDefault();
        this.locals.$modal.modal('show');
    }

    _onClickUseTemplate(e){
        e.preventDefault();
        const locals = this.locals;

        locals.$textarea.val(locals.$defaultTemplate.text());
    }

    _onCloseModal(){
        this.locals.$textarea.val(this.template);
        this.formHelper.removeErrors();
    }

    _onSubmitForm(e){
        e.preventDefault();
        const self = this;
        const locals = this.locals;

        if (!self.formHelper.isValidInputs()) return;
        const formData = self.formHelper.getFormData();

        self._sendEmailContent(formData)
            .done(()=>{
                self.template = locals.$textarea.val();
                locals.$modal.modal('hide');

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

