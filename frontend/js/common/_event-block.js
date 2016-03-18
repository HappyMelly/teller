'use strict';


export default class Widget {
    constructor(selector) {
        this.$root = $(selector);
        this.locals = this._getDom();

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
        const eventId = this.locals.$confirm.data('id');

        this._sendConfirm(eventId)
            .done(()=>{
                this.locals.$confirm.addClass('disabled').text('Confirmed').off('click');
                success("Event was successfully confirmed");
            })
    }

    _onClickCancel(e) {
        e.preventDefault();
        const eventId = this.locals.$cancel.data('id');

        this._sendCancel(eventId)
            .done((response)=>{
                this.$confirmDialog = this._createDialog(response);

                this.$confirmDialog.modal('show');
            })
    }

    _onClickAcceptCancel(e){
        e.preventDefault();

        const formData = $("#cancelForm").serialize();
        const eventId = this.locals.$cancel.data('id');
        this._sendAcceptCancel(formData, eventId)
            .done(()=>{
                this.$confirmDialog.modal('hide');
                this.$root.trigger('hmt.event.cancel');

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
        return $dialog
    }

    //transport
    _sendConfirm(eventId){
        const url = jsRoutes.controllers.Events.confirm(eventId).url;
        return $.post(url, {});
    }

    _sendCancel(eventId){
        const url = jsRoutes.controllers.Events.reason(eventId).url;
        return $.get(url, {});
    }

    _sendAcceptCancel(data, id){
        const url = jsRoutes.controllers.Events.cancel(id).url;
        return $.post(url, data);
    }

    // static
    static plugin(selector) {
        const $elems = $(selector);
        if (!$elems.length) return;

        return $elems.each(function (index, el) {
            let $element = $(el);
            let data     = $element.data('widget');

            if (!data) {
                data = new Widget(el);
                $element.data('widget', data);
            }
        })
    }
}

