'use strict';

import FormHelper from './../../common/_form-helper';
import integrationHelpers from './_intergration-helpers';
import ImportingItem from './_integration-import';


export default class Widget {
    constructor(selector, options) {
        this.$root = $(selector);
        this.options = options;
        this.locals = this._getDom();

        this.importDlgHelper = new FormHelper(this.locals.$controls);

        this._init();
        this._assignEvents();
    }

    _getDom(){
        const $root = this.$root;
        
        return {
            $list: $root.find('[data-integ-list]'),
            $controls: $root.find('[data-control]'),
            $editDlg: $root.find('[data-integcreate-dlg]'),
            $availableThemes: $root.find('[data-integcreate-list]'),
            $modalDisableInteg: $root.find('[data-integdisable-dlg]'),
        }
    }
    
    _init(){
        if (this.isIntegrationActive) {
            this._checkAndInitImporting()
        }    
    }

    _assignEvents(){
        this.$root  
            .on('click', '[data-integdisable-yes]', this._ClickDeactivate.bind(this))
            .on('click', '[data-integ-import-btn]', this._onClickShowImport.bind(this))
            .on('click', '[data-integcreate-btn]', this._onEventSubmitEdit.bind(this))
            .on('click', '[data-integcreate-cancel]', this._onEventCancelEdit.bind(this))
    }

    _ClickDeactivate(e){
        const $root = this.$root;

        this._sendDeactivate(this.options.deactivate)
            .done(()=>{
                $root.removeClass('b-integr_state_active b-integr_state_import b-integr_state_nolist');
                success('You are successfully deactivate integration')
            })
    }

    _onClickShowImport(e){
        e.preventDefault();
        const self = this;
        
        self._getAvailableList(this.options.getAvailableLists)
            .done((list)=>{
                integrationHelpers._prepareSelectWithThemes(this.locals.$availableThemes, list);
                self.locals.$editDlg.modal('show')
            })
    }

    _onEventSubmitEdit(e){
        e.preventDefault();
        const self = this;
        const data = self.importDlgHelper.getFormData();

        self._createImportList(this.options.createImport, data)
            .done((data) =>{
                const html = $.parseJSON(data).body;

                self.locals.$list.append(html);
                success('You are succefully import new list from mailchimp')
            })
            .fail(() => {
                error('Something is going wrong');
            })
    }

    _onEventCancelEdit(e){
        e.preventDefault();
        this.locals.$editDlg.modal('hide');
    }

    _checkAndInitImporting(){
        const $listItems = this.locals.$list.children();
        
        if (!$listItems.length){
            this.$root.addClass('b-integr_state_nolist');
            return;
        }

        this.$root.addClass('b-integr_state_import');
        ImportingItem.plugin($listItems, this.options);      
    }
    
    isIntegrationActive(){
        return this.$root.hasClass('b-integr_state_active');
    }   
    
    //transport
    _sendDeactivate(url){
        return $.post(url, {});
    }

    _getAvailableList(url){
        let defer = $.Deferred();

        $.get(url)
            .done((data) => {
                const list = $.parseJSON(data).lists;
                defer.resolve(list);
            });
        return defer.promise();
    }

    _createImportList(url, data){
        return $.post(url, data);
    }

    // static
    static plugin(selector, options) {
        const $elems = $(selector);
        if (!$elems.length) return;

        return $elems.each(function (index, el) {
            let $element = $(el);
            let data     = $element.data('widget.integration');

            if (!data) {
                data = new Widget(el, options);
                $element.data('widget.integration', data);
            }
        })
    }
}





