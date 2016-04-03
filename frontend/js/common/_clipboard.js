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
        this.$client = $('.global-zeroclipboard-container').first();

        this._assignEvents();
    }

    _assignEvents() {
        const self = this;

        this.client.on('aftercopy', this._onEventAfter.bind(this));
        this.$client.on('mouseenter', () =>{
                self.$client
                    .attr('title', 'Copy link')
                    .tooltip('show');
            })
    }

    _onEventAfter(){
        const $root = this.$root;
        this.$client.tooltip('hide');

        $root.attr('title', 'Copied')
            .tooltip('show');

        setTimeout(()=>{
            $root.tooltip('hide')
                .attr('title', '');
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


