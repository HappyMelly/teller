'use strict';

import FormHelper from './../../common/_form-helper';

export default class Widget {
    constructor(selector, options) {
        this.$root = $(selector);
        this.options = options;
        this.locals = this._getDom();

        this._init();
        this._assignEvents();
    }

    _getDom(){
        const $root = this.$root;
        
        return {
            $modalDisableInteg: $root.find('[data-integ-disable-dlg]'),
            $importingList:     $root.find('[data-integ-list]'),
            $modalDisableImport:$root.find('[data-integimport-dlg]'),
            $modalCreateImport: $root.find('[data-integcreate-dlg]')
        }
    }
    
    _init(){
        this.$currentImport = null;

        if (this.isIntegrationActive) {
            this._checkImportingList()
        }    
    }

    _assignEvents(){
        this.$root  
            .on('click', '[data-integ-activate]', this._onClickActivate.bind(this))
            .on('click', '[data-integdisable-yes]', this._ClickDeactivate.bind(this))

            .on('click', '[data-integ-import]', this._onClickDisableImport.bind(this))
            .on('click', '[data-integimport-disable]', this._onEventDisableList.bind(this))

            .on('click', '[data-integcreate-btn]', this._onClickImportList.bind(this));
    }

    _onClickActivate(e){
        e.preventDefault();
        const $root = this.$root;        
        
        this._sendActivate(this.options.activate)
            .done(()=>{
                $root.addClass('b-integr_state_active');
                this._checkImportingList();
                
                success('You are successfully activate integration')
            })
    }

    _ClickDeactivate(e){
        const $root = this.$root;

        this._sendDeactivate(this.options.deactivate)
            .done(()=>{
                $root.removeClass('b-integr_state_active b-integr_state_import b-integr_state_nolist');
                success('You are successfully deactivate integration')
            })
    }

    _onClickDisableImport(e){
        e.preventDefault();
        const $currentImport = $(e.currentTarget).closest('.b-integrlist');
        const title = $currentImport.find('[data-integ-import-name]').text();

        this.$currentImport = $currentImport;
        this.locals.$modalDisableImport
            .find('[data-integimport-name]').text(title || '').end()
            .modal('show');
    }

    _onEventDisableList(e){
        const $currentImport = this.$currentImport;
        const id = $currentImport.data('list-id');
        const text = $currentImport.find('[data-integ-import-name]').text();

        this._sendDisableList(this.options.disableList, id)
            .done(() => {
                this.$currentImport = null;
                success(`You are successfully disable ${text}`);

                $currentImport.slideUp(function(){
                    $currentImport.remove();
                })
            })
            .fail(()=>{
                this.$currentImport = null;

                error('Something is going wrong');
            })
    }

    _onClickImportList(){
        const formData = {};
        this._sendCreatingImport(this.options.createList, formData)
            .done(()=>{
                success('You are successfully import list')
            })
    }

    _checkImportingList(){
        const $listItems = this.locals.$importingList.children();
        
        if ($listItems.length){
            this.$root.addClass('b-integr_state_import')
        } else {
            this.$root.addClass('b-integr_state_nolist')
        }
    }
    
    isIntegrationActive(){
        return this.$root.hasClass('b-integr_state_active');
    }
    
    //transport
    _sendActivate(url){
        return $.post(url, {});
    }

    _sendDeactivate(url){
        return $.post(url, {});
    }

    _sendDisableList(url, id){
        return $.post(url, {});
    }

    _sendCreatingImport(url, data){
        return $.post(url, data);
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





