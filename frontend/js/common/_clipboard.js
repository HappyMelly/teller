'use strict';

export default class Widget {

    constructor(element) {
        this.root = element;
        this.$root = $(element);

        if (!ZeroClipboard) {
            console.log('there is no zeroclipboard dependency');
            return;
        }

        this.client = new ZeroClipboard(element);
        this._assignEvents();
    }

    _assignEvents() {
        this.client
            .on('ready', this._onEventReady.bind(this))
            .on('aftercopy', this._onEventAfter.bind(this));
    }

    _onEventReady(){

    }

    _onEventAfter(){
        const self = this;
        const $root = self.$root;

        $root.addClass('state_copied')
            .attr('title', 'Copied')
            .tooltip('show');

        setTimeout(()=>{
            $root.tooltip('hide')
                .attr('title', '')
                .attr('data-original-title', '')
                .removeClass('state_copied');
        }, 2500)
    }

    // static
    static plugin(selector) {
        const $elems = $(selector);
        if (!$elems.length) return;

        return $elems.each(function (index, el) {
            let $element = $(el);
            let data     = $element.data('widget-clipboard');

            if (!data) {
                data = new Widget(el);
                $element.data('widget', data);
            }
        })
    }
}


