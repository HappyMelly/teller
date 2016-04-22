'use strict';

import FormHelper from './../../common/_form-helper';
import integrationHelpers from './_intergration-helpers';


export default class Widget {
    constructor(selector, options) {
        this.$root = $(selector);
        this.options = $.extend({}, options, {
            id: this.$root.data('import-id')
        });
        this.locals = this._getDom();

        this.editDlgData = null;
        this.editDlgHelper = new FormHelper(this.locals.$controls);

        this._assignEvents();
    }

    _getDom(){
        const $root = this.$root;

        return {
            $view: $root.find('[data-import-view]'),
            $controls: $root.find('[data-control]'),
            $editDlg: $root.find('[data-import-dlg]'),
            $availableThemes: $root.find('[data-import-select]'),
            $disableDlg: $root.find('[data-import-tooltip]'),
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
        const self = this;

        self._getAvailableList(self.options.getAvailableLists)
            .done((list)=>{
                integrationHelpers._prepareSelectWithThemes(self.locals.$availableThemes, list);
                this.editDlgData = self.editDlgHelper.getFormData();

                self.locals.$editDlg.modal('show');
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
                self.locals.$view.slideUp(400, ()=>{
                    self.$root.remove();
                });
                success(`You are successfully disable list`);
            })
            .fail(()=>{
                error('Something is going wrong');
            })
    }

    _onClickCancelEdit(e){
        e.preventDefault();

        this._setDefaultValues();
        this.locals.$editDlg.modal('hide');
    }

    _onClickSubmitEdit(e){
        e.preventDefault();

        const self = this;
        let update = {
            url: self.options.updateImport,
            id: self.options.id,
            formData: self.editDlgHelper.getFormData()
        };
        self.editDlgData = update.formData;

        this._sendUpdateList(sendData)
            .done(() =>{
                self.locals.$editDlg.modal('hide');
                success('You are successfully update importing list')
            })
            .fail(() =>{
                self.locals.$editDlg.modal('hide');
                error('Something is happened wrong');
            })
    }

    _setDefaultValues(){
        const $controls = this.locals.$controls;
        const data = this.editDlgData;

        for( let field in data){
            if (data.hasOwnProperty(field)){
                let $control = $controls.filter(`[name="${field}"]`).first();

                if (!$control.length) return;

                if ($control.is(':checkbox')){
                    $control.prop('checked', data[field])
                } else{
                    $control.val(data[field]);
                }
            }
        }
    }

    //transport
    _getAvailableList(url){
        let defer = $.Deferred();

        $.get(url)
            .done((data) => {
                const list = $.parseJSON(data).lists;
                defer.resolve(list);
            });
        return defer.promise();
    }

    _sendDisableList(url, id){
        return $.post(url, {
            list_id: id
        });
    }

    /**
     * Update Info about list
     * @param {Object} data
     * @param {String} data.url
     * @param {Number} data.id
     * @param {Object} data.formData - object with field/value
     * @returns {Promise}
     * @private
     */
    _sendUpdateList(data){
        return $.post(data.url, {
            list_id: data.id,
            data: data.formData
        });
    }

    static plugin($elems, options) {
        if (!$elems.length) return;

        return $elems.each(function (index, el) {
            let $element = $(el);
            let data = $element.data('widget.importing');

            if (!data) {
                data = new Widget(el, options);
                $element.data('widget.importing', data);
            }
        })
    }
}
