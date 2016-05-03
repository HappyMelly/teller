'use strict';

import FormHelper from './../../common/_form-helper';
import ImportingItem from './_integration-import';


export default class Widget {
    constructor(selector, options) {
        this.$root = $(selector);
        this.options = options;
        this.locals = this._getDom();

        this.createDlgHelper = new FormHelper(this.locals.$createDlgControls);
        this.editDlgHelper = new FormHelper(this.locals.$editDlgControls);

        this._init();
        this._assignEvents();
    }

    _getDom(){
        const $root = this.$root;
        
        return {
            $list: $root.find('[data-integ-list]'),
            $createDlg: $root.find('[data-create-dlg]'),
            $createDlgControls: $root.find('[data-create-dlg]').find('[data-control]'),
            $editDlg: $root.find('[data-integcreate-dlg]'),
            $editDlgControls: $root.find('[data-integcreate-dlg]').find('[data-control]'),
            $availableListSelect: $root.find('[data-integcreate-list]'),
            $availableListInput: $root.find('[data-list-name]'),
            $modalDisableInteg: $root.find('[data-integdisable-dlg]')            
        }
    }
    
    _init(){
        if (this.isIntegrationActive) {
            this._checkAndInitExporting()
        }    
    }

    _assignEvents(){
        const self = this;
        this.$root
            .on('click', '[data-integdisable-yes]', this._ClickDeactivate.bind(this))
            .on('click', '[data-integ-create-btn]', this._onClickShowCreate.bind(this))
            .on('click', '[data-integ-import-btn]', this._onClickShowImport.bind(this))
            .on('click', '[data-integcreate-btn]', this._onEventSubmitEdit.bind(this))
            .on('click', '[data-create-btn]', this._onEventSubmitCreate.bind(this))
            .on('click', '[data-integcreate-cancel]', this._onEventCancelEdit.bind(this))
            .on('click', '[data-create-cancel]', this._onEventCancelCreate.bind(this))
            .on('change','[data-integcreate-list]', this._onChangeSelectList.bind(this))
            .on('availableList.update', this._onEventUpdateAvailableList.bind(this));

        this.$root.on('change', '[data-type-value]', function(e) {
            self.$root.find('[data-type-control]').val($(this).val());
        });

        App.events.sub('hmt.mailchimp.renderblock', function(){
            ImportingItem.plugin(self.locals.$list.children(), self.options);
            self.$root.trigger('availableList.update');
        })
    }

    _ClickDeactivate(e){
        e.preventDefault();
        const $root = this.$root;

        this._sendDeactivate(this.options.deactivate)
            .done((data)=>{
                $root.removeClass('b-integr_state_active b-integr_state_import b-integr_state_nolist');
                success(data.message);
            })
    }

    _onClickShowCreate(e){
        e.preventDefault();
        this.locals.$createDlg.modal('show');
    }

    _onClickShowImport(e){
        e.preventDefault();
        this.locals.$editDlg.modal('show');
    }

    _onEventSubmitCreate(e){
        e.preventDefault();
        const self = this;
        const locals = self.locals;

        const formData = self.createDlgHelper.getFormData();

        self._createImportList(this.options.create, formData)
            .done((data) => {
                locals.$list.append(data.body);
                App.events.pub('hmt.mailchimp.renderblock');
                locals.$createDlg.modal('hide');

                success(data.message)
            })
            .fail((jqXHR, textStatus, errorThrown) => {
                const msg = JSON.parse(jqXHR.responseText);
                error(msg.message);
            })
    }

    _onEventSubmitEdit(e){
        e.preventDefault();
        const self = this;
        const locals = self.locals;

        const formData = self.editDlgHelper.getFormData();

        self._createImportList(this.options.createImport, formData)
            .done((data) => {
                locals.$list.append(data.body);
                App.events.pub('hmt.mailchimp.renderblock');
                locals.$editDlg.modal('hide');

                success(data.message)
            })
            .fail((jqXHR, textStatus, errorThrown) => {
                const msg = JSON.parse(jqXHR.responseText);
                error(msg.message);
            })
    }

    _onEventCancelCreate(e){
        e.preventDefault();
        this.locals.$createDlg.modal('hide');
    }

    _onEventCancelEdit(e){
        e.preventDefault();
        this.locals.$editDlg.modal('hide');
    }

    _onChangeSelectList(){
        const locals = this.locals;
        const listName = locals.$availableListSelect.find('option:selected').text();
        
        locals.$availableListInput.val(listName);
    }

    _onEventUpdateAvailableList(){
        const self = this;

        self._getAvailableList(this.options.getAvailableLists)
            .done((list)=>{
                self._prepareSelect(list);
            });
    }

    _checkAndInitExporting(){
        const self = this;
        const locals = self.locals;
        const $listItems = locals.$list.children();

        self.$root.addClass('b-integr_state_loading');

        self._getAvailableList(this.options.getAvailableLists)
            .done((list)=>{
                self.$root.removeClass('b-integr_state_loading');
                self._prepareSelect(list);

                if (list.length) {
                    self.$root.addClass('b-integr_state_import');
                } else {
                    self.$root.addClass('b-integr_state_nolist');
                }

                ImportingItem.plugin($listItems, this.options);
            });
    }
    
    isIntegrationActive(){
        return this.$root.hasClass('b-integr_state_active');
    }

    /**
     * Add available lists from mailchimp to select control
     * @param {Array} list
     * @private
     */
    _prepareSelect(list){
        const $select = this.locals.$availableListSelect;
        const $input = this.locals.$availableListInput;
        const availableList = this._deleteImportedLists(list);

        $select.children().remove();
        
        if (!availableList.length){
            $select.append(`<option value="" disabled checked>No available lists </option>`);
            return;
        }

        availableList.forEach((item)=>{
            var channelType = 'private';
            if (item.public) {
                channelType = 'public';
            }
            $select.append(`<option value="${item.id}">${item.name}<span> &mdash; ${channelType}</span></option>`)
        });

        $input.val($select.find('option:selected').text());        
    }

    /**
     * Filter list from server
     * @param {Array} list
     * @returns {Array} - filtered list;
     * @private
     */
    _deleteImportedLists(list){
        const $importedLists = this.locals.$list.children();
        let index = -1;

        $importedLists.each((i, elem) =>{
            const id = $(elem).data('import-id');

            for(let i = 0, n = list.length; i < n; i++ ){
                if (list[i].id == id){
                    index = i;
                    break;
                }
            }

            if (~index){
                list.splice(index, 1);
            }
        });

        return list;
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





