'use strict';


export default class Widget {
    constructor(selector) {
        this.$root = $(selector);
        this.locals = this._getDom();
        this.$confirmDialog = null;

        this._assignEvents();
    }

    _getDom() {
        return {
            $confirm: this.$root.find('[data-event-confirm]'),
            $cancel: this.$root.find('[data-event-cancel]')
        };
    }

    _assignEvents() {
        this.$root
            .on('click', '[data-event-confirm]', this._onClickConfirm.bind(this))
            .on('click', '[data-event-cancel]', this._onClickCancel.bind(this))
            .on('click', '#eventCancelButton', this._onClickAcceptCancel.bind(this));
    }

    _onClickConfirm(e) {
        e.preventDefault();
        const self = this;
        const eventId = self.locals.$confirm.data('id');

        self._sendConfirm(eventId)
            .done(()=>{
                self.locals.$confirm.addClass('disabled').text('Confirmed').off('click');
                success("Event was successfully confirmed");
            })
    }

    _onClickCancel(e) {
        e.preventDefault();
        const self = this;
        const eventId = self.locals.$cancel.data('id');

        self._sendCancel(eventId)
            .done((response)=>{
                self.$confirmDialog = self._createDialog(response);
                self.$confirmDialog.modal('show');
            })
    }

    _onClickAcceptCancel(e){
        e.preventDefault();

        const self = this;
        const formData = $("#cancelForm").serialize();
        const eventId = self.locals.$cancel.data('id');

        self._sendAcceptCancel(formData, eventId)
            .done(()=>{
                self.$confirmDialog
                    .on('hidden.bs.modal', ()=>{
                        App.events.pub('hmt.event.cancel');
                    })
                    .modal('hide');

                success("Event was successfully canceled");
            })
    }

    _createDialog(content){
        const selector = '#cancelDialog';
        let $dialog;

        $(selector).remove();
        $dialog =  $('<div id="' + selector + '" class="b-modal modal fade" tabindex="-1">')
            .attr('role', 'dialog')
            .attr('aria-hidden', 'true')
            .append(content);

        $dialog.appendTo(this.$root);
        return $dialog;
    }

    //transport
    _sendConfirm(eventId){
        const url = jsRoutes.controllers.cm.Events.confirm(eventId).url;
        return $.post(url, {});
    }

    _sendCancel(eventId){
        const url = jsRoutes.controllers.cm.Events.reason(eventId).url;
        return $.get(url, {});
    }

    _sendAcceptCancel(data, id){
        const url = jsRoutes.controllers.cm.Events.cancel(id).url;
        return $.post(url, data);
    }

    // static
    static plugin(selector) {
        const $elems = $(selector);
        if (!$elems.length) return;

        return $elems.each(function (index, el) {
            let $element = $(el);
            let data     = $element.data('hmt.event.block');

            if (!data) {
                data = new Widget(el);
                $element.data('widget', data);
            }
        })
    }
}

