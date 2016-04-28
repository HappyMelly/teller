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
            $availableLists: $root.find('[data-integcreate-list]'),
            $modalDisableInteg: $root.find('[data-integdisable-dlg]'),
            $listName: $root.find('[data-list-name]')
        }
    }
    
    _init(){
        if (this.isIntegrationActive) {
            this._checkAndInitExporting()
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
            .done((data)=>{
                $root.removeClass('b-integr_state_active b-integr_state_import b-integr_state_nolist');
                success(data.message);
            })
    }

    _onClickShowImport(e){
        e.preventDefault();
        const self = this;

        self.locals.$editDlg.modal('show');
    }

    _onEventSubmitEdit(e){
        e.preventDefault();
        const self = this;
        const data = self.importDlgHelper.getFormData();
        self._createImportList(this.options.createImport, data)
            .done((data) => {
                self.locals.$list.append(data.body);
                self.locals.$editDlg.modal('hide');
                success(data.message)
            })
            .fail((jqXHR, textStatus, errorThrown) => {
                var msg = JSON.parse(jqXHR.responseText);
                error(msg.message);
            })
    }

    _onEventCancelEdit(e){
        e.preventDefault();
        this.locals.$editDlg.modal('hide');
    }

    _checkAndInitExporting(){
        const $listItems = this.locals.$list.children();
        const self = this;

        if (!$listItems.length){
            this.$root.addClass('b-integr_state_loading');
            self._getAvailableList(this.options.getAvailableLists)
                .done((list)=>{
                    integrationHelpers._prepareSelectWithLists(self.locals.$availableLists, list, self.locals.$listName);
                    self.$root.removeClass('b-integr_state_loading');
                    if (list.length) {
                        self.$root.addClass('b-integr_state_import');
                    } else {
                        self.$root.addClass('b-integr_state_nolist');
                    }
                });
            return;
        } else {
            self._getAvailableList(this.options.getAvailableLists)
                .done((list)=> {
                    integrationHelpers._prepareSelectWithLists(self.locals.$availableLists, list, self.locals.$listName);
                });
        }

        this.$root.addClass('b-integr_state_import');
        ImportingItem.plugin($listItems, this.options);      
    }
    
    isIntegrationActive(){
        return this.$root.hasClass('b-integr_state_active');
    }   
    
    //transport
    _sendDeactivate(url){
        return $.ajax({
            type: "POST",
            url: url,
            data: {},
            dataType: "json"
        });
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
        return $.ajax({
            type: "POST",
            url: url,
            data: data,
            dataType: "json"
        });
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





