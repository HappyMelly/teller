'use strict';

import FormHelper from './../../common/_form-helper';
import ImportingItem from './_integration-import';


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
            $modalDisableInteg: $root.find('[data-integdisable-dlg]'),
            $editDlg: $root.find('[data-integcreate-dlg]'),
            $list: $root.find('[data-integ-list]')
        }
    }
    
    _init(){
        if (this.isIntegrationActive) {
            this._checkAndInitImporting()
        }    
    }

    _assignEvents(){
        this.$root  
            .on('click', '[data-integ-activate]', this._onClickActivate.bind(this))
            .on('click', '[data-integdisable-yes]', this._ClickDeactivate.bind(this))
            .on('click', '[data-integ-import-btn]', this._onClickShowImport.bind(this))
            .on('click', '[data-integcreate-btn]', this._onEventSubmitEdit.bind(this))
    }

    _onClickActivate(e){
        e.preventDefault();
        const self = this;
        const $root = self.$root;

        self._sendActivate(this.options.activate)
            .done(()=>{
                $root.addClass('b-integr_state_active');
                self._checkAndInitImporting();
                
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

    _onClickShowImport(e){
        e.preventDefault();
        const self = this;
        
        self._getAvailableList(this.options.getAvailableLists)
            .done((data)=>{
                /** set select data**/
                
                self.locals.$editDlg.modal('show')
            })
    }

    _onEventSubmitEdit(e){
        e.preventDefault();
        const self = this;
        const data = {test: 1}  /** get controls inputs**/

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

    _checkAndInitImporting(){
        const self = this;
        const $listItems = self.locals.$list.children();
        
        if (!$listItems.length){
            this.$root.addClass('b-integr_state_nolist');
            return;
        }

        this.$root.addClass('b-integr_state_import');
        $listItems.each((index, item) => {
            const $item = $(item);
            let data;

            if (!$item.data('importing')){
                data = new ImportingItem(item, self.options);
                $item.data('widget.importing', data);
            }
        })
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

    _getAvailableList(url){
        return $.get(url);
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
            let data     = $element.data('widget.scrollto');

            if (!data) {
                data = new Widget(el, options);
                $element.data('widget', data);
            }
        })
    }
}





