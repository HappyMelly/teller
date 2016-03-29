'use strict';

import FormHelper from "./../../common/_form-helper";


export default class Widget {
    /**
     * Filter history
     * @param {String} selector
     */
    constructor(selector) {
        this.$root = $(selector);
        this.locals = this._getDom();
        this.brandId = this.$root.data('brand-id');
        this.formData = {};
        this.formHelper = new FormHelper(this.locals.$inputs);

        this._prepareView(this.locals.$inputs);
        this._saveFormData(this.locals.$inputs);

        this._assignEvents();
    }

    _getDom() {
        const $root = this.$root;

        return {
            $form: $root.find('[data-setapi-form]'),
            $view: $root.find('[data-setapi-view]'),
            $modal: $root.find('[data-setapi-modal]'),
            $inputs: $root.find('.b-apiform__input')
        };
    }

    _assignEvents() {
        this.$root
            .on('click', '[data-setapi-activate]', this._onClickActivate.bind(this))
            .on('click', '[data-setapi-promptbtn]', this._onClickShowPrompt.bind(this))
            .on('click', '[data-setapi-specify]', this._onClickSpecify.bind(this))
            .on('click', '[data-setapi-deacticate]', this._onClickDeactivate.bind(this))

            .on('submit', '[data-setapi-form]', this._onClickSubmit.bind(this))
            .on('click', '[data-seatpi-form-save]', this._onClickSubmit.bind(this))
            .on('click', '[data-seatpi-form-cancel]', this._onClickFormCancel.bind(this))

            .on('click', '[data-setapi-editform]', this._onClickEditForm.bind(this))
            .on('click', '.b-apiview__link', this._onClickAddUrl.bind(this));
    }

    _onClickActivate(e){
        e.preventDefault();
        const self = this;
        const $root = self.$root;
        
        self._sendActivate(self.brandId)
            .done(()=>{
                if (!$root.hasClass('b-setapi_state_active')){
                    $root.addClass('b-setapi_state_active');
                }
                success('You are successfully activate api')
            })
    }

    _onClickShowPrompt(e){
        e.preventDefault();
        this.locals.$modal.modal('show');
    }

    _onClickDeactivate(e){
        const self = this;
        const $root = self.$root;
        
        e.preventDefault();
        e.stopPropagation();

        self._sendDeactivate(self.brandId)
            .done(()=>{
                $root.removeClass('b-setapi_state_active');
                success('You are successfully deactivate api')
            })
    }

    _onClickSpecify(e){
        e.preventDefault();
        const self = this;
        const $root = self.$root;

        self._sendSpecify(self.brandId)
            .done(()=>{
                if (!$root.hasClass('b-setapi_state_form')){
                    $root.addClass('b-setapi_state_form');
                }
            })
    }

    _onClickSubmit(e){
        e.preventDefault();
        const self = this;
        const $inputs = this.locals.$inputs;

        if (!self.isFormValid()) return;
        
        const formData = this.formHelper.getFormData();
        self._sendUrlsData(formData)
            .done(()=>{
                this._saveFormData($inputs);
                this._prepareView($inputs);
                this._showView();

                success('You are successfully update urls')
            })
            .fail((response)=>{
                const data = $.parseJSON(response.responseText).data;

                if (!data.errors) return;
                self.formHelper.setErrors(data.errors);
            })
    }

    _onClickFormCancel(e){
        e.preventDefault();
        this._restoreFormData(this.locals.$inputs);
        this.formHelper.removeErrors();
        this._showView();
    }

    _onClickEditForm(e){
        e.preventDefault();
        this._saveFormData(this.locals.$inputs);
        this._showForm();
    }

    _onClickAddUrl(e){
        e.preventDefault();
        const field = $(e.currentTarget).closest('[data-setapi-field]').data('setapi-field');

        this._showForm($.trim(field));
    }

    /**
     * Show form with api urls
     * @param {String} field - name of the field that you need to hightlight
     * @private
     */
    _showForm(field = '') {
        const $root = this.$root;
        const locals = this.locals;

        if (!$root.hasClass('b-setapi_state_form')) {
            $root.addClass('b-setapi_state_form')
                .removeClass('b-setapi_state_view');
        }

        const selector = field? `input[name="${field}"]`: 'input';

        locals.$form
            .find(selector).first()
            .trigger('focus');
    }

    _showView(){
        const $root = this.$root;

        if (!$root.hasClass('b-setapi_state_view')) {
            $root.addClass('b-setapi_state_view')
                .removeClass('b-setapi_state_form');
        }
    }

    /**
     * Restore form data, if you cancel editing form
     * @param {jQuery} $inputs - list of given inputs
     * @private
     */
    _restoreFormData($inputs){
        let data = this.formData;
        $inputs.each( (index, el) =>{
            const $el = $(el);
            const value = data[$el.attr('name')];
            $el.val(value);
        })    
    }

    /**
     * Save form data, if you start editing form
     * @param {jQuery} $inputs - list of given inputs
     * @private
     */
    _saveFormData($inputs){
        let data = {};
        $inputs.each( (index, el) =>{
            const $el = $(el);
            const name = $el.attr('name');
            
            if (name){
                data[name] = $el.val();
            }            
        });

        this.formData = data;
    }

    /**
     * Prepare view based on given inputs;
     * @param $inputs
     * @private
     */
    _prepareView($inputs){
        const $view = this.locals.$view;
        
        $inputs.each((index, el) => {
            const $el = $(el);
            const name = $el.attr('name');
            const $viewPlace = $view.find(`[data-setapi-field="${name}"]`);
            
            if (!$viewPlace.length) return false;
            
            const valueInput = $el.val();
            if ($.trim(valueInput)){
                $viewPlace.removeClass('state_url')
                    .find('.b-apiview__text')
                    .text(valueInput)
            } else {
                $viewPlace.addClass('state_url')
                    .find('.b-apiview__text')
                    .text('')
            }
        })
    }

    /**
     * Check, is form valid through form helper;
     * @returns {boolean}
     */
    isFormValid(){
        const locals = this.locals;
        let valid = true;

        if (!this.formHelper.isValidInputs()){
            valid = false;
        }

        return valid;
    }
    
    
    // transport
    _sendActivate(brandId){
        const url = jsRoutes.controllers.cm.brand.API.activate(brandId).url;
        return $.post(url);
    }

    _sendDeactivate(brandId){
        const url =  jsRoutes.controllers.cm.brand.API.deactivate(brandId).url;
        return $.post(url);
    }

    _sendSpecify(brandId){
        const url = '/specifybtn/' + brandId;
        return $.post(url);
    }

    _sendUrlsData(formData){
        const url = '/send/form';
        return $.post(url, formData);
    }

    // static
    static plugin(selector) {
        const $elems = $(selector);
        if (!$elems.length) return;

        return $elems.each(function (index, el) {
            let $element = $(el);
            let data = $element.data('widget');

            if (!data) {
                data = new Widget(el);
                $element.data('widget', data);
            }
        })
    }
}


