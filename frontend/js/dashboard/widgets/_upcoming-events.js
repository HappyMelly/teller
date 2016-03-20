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

    // static
    static plugin(selector) {
        const $elems = $(selector);
        if (!$elems.length) return;

        return $elems.each(function (index, el) {
            let $element = $(el);
            let data     = $element.data('hmt.events.upcoming');

            if (!data) {
                data = new Widget(el);
                $element.data('widget', data);
            }
        })
    }
}

