'use strict';

/**
 * @param selector
 * @constructor
 */
export default class Widget {
    constructor(selector) {
        this.$root = $(selector);
        this._assignEvents();
    }

    _assignEvents() {
        const self = this;

        self.$root.on('click', '.dlg-hmfees__link ', function(e){
            var $this = $(this);
            e.preventDefault();

            if ($this.hasClass('state_active')) return;

            self._switchTab($this);
        });
    };

    _switchTab($link){
        const $target = this._getTarget($link);

        if (!$target.length) return;

        $target.show()
            .siblings('.table').hide();
        $link.addClass('state_active')
            .siblings('.dlg-hmfees__link').removeClass('state_active');
    }

    _getTarget($el){
        return this.$root.find($el.attr('href'));
    }

    // static
    static plugin(selector, options) {
        const $elems = $(selector);
        if (!$elems.length) return;

        return $elems.each(function (index, el) {
            let $element = $(el);
            let data = $element.data('widget.scrollto');

            if (!data) {
                data = new Widget(el, options);
                $element.data('widget', data);
            }
        })
    }
}
