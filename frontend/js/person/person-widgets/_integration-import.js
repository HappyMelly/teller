'use strict';

import formHelper from "./../../common/_form-helper";

export default class Importing {
    constructor(selector, options) {
        this.$root = $(selector);
        this.options = $.extend({}, options, {
            id: this.$root.data('import-id')
        });
        this.locals = this._getDom();
        this.editForm = null;

        debugger;

        this._assignEvents();
    }

    _getDom(){
        const $root = this.$root;

        return {
            $view: $root.find('[data-import-view]'),
            $disableDlg: $root.find('[data-import-tooltip]'),
            $editDlg: $root.find('[data-import-dlg]')
        }
    }

    _assignEvents(){
        this.$root
            .on('click', '[data-import-btn-edit]', this._onClickEditImport.bind(this))
            .on('click', '[data-import-btn-disable]', this._onClickShowTooltip.bind(this))
            .on('click', '[data-import-disable]', this._onEventDisableImport.bind(this))
            .on('click', '[data-import-cancel]', this._onClickCancelEdit.bind(this))
            .on('click', '[data-import-submit]', this._onClickSubmitEdit.bind(this))
    }

    _onClickEditImport(e){
        e.preventDefault();
        
        this.editForm = {test: 1}; /** save form edit data*/
        
        this._getAvailableList(this.options._getAvailableList)
            .done((data)=>{
                const select = $.parseJSON(data);
                /** set availavel list import **/

                this.locals.$editDlg.modal('show');
            });
    }

    _onClickShowTooltip(e){
        e.preventDefault();
        this.locals.$disableDlg.modal('show');
    }

    _onEventDisableImport(e){
        const self = this;
        
        self._sendDisableList(self.options.disableImport, self.options.id)
            .done(() => {
                self.$view.slideUp(function(){
                    self.$root.remove();
                });
                success(`You are successfully disable ${text}`);
            })
            .fail(()=>{
                error('Something is going wrong');
            })
    }

    _onClickCancelEdit(e){
        e.preventDefault();
        
        /** return default values **/
        this.locals.$editDlg.modal('hide');
    }

    _onClickSubmitEdit(e){
        e.preventDefault();
        const self = this;
        
        self.editForm = {test: 2} /** set new data**/
        this._sendUpdateList(self.options.updateImport, self.options.id, self.editForm)
            .done(() =>{
                self.locals.$editDlg.modal('hide');
                success('You are successfully update importing list')
            })
    }

    //transport
    _getAvailableList(url){
        return $.get(url)
    }

    _sendDisableList(url, id){
        return $.post(url, {});
    }

    _sendUpdateList(url, id, data){
        return $.post(url, {data: id});
    }
}
