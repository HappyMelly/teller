'use strict';

export default class Widget {
    constructor(selector) {
        this.$root = $(selector);
        this.locals = this._getDom();

        const $firstLink = this.locals.$links.first();
        this._setInitialValues();
        this.filterListByLink($firstLink);
        this._assignEvents();
    }

    _getDom() {
        return {
            $links: this.$root.find('[data-upevent-link]'),
            $total: this.$root.find('[data-upevent-total]'),
            $now: this.$root.find('[data-upevent-now]'),
            $items: this.$root.find('.b-eventfut'),
            $text: this.$root.find('[data-upevent-text]'),
            $switcher: this.$root.find('[data-schedule-switcher]')
        };
    }

    _setInitialValues(){
        const locals = this.locals;
        const currentCount = locals.$items.filter('.current').length;

        locals.$total.text(locals.$items.length);
        locals.$now.text(locals.$items.filter('.current').length);

        switch (currentCount) {
            case 0:
                locals.$switcher.hide();
                break;
            case 1:
                locals.$text.text('is running now');
                break;
            default:
                locals.$text.text('are running now');
                break;
        }
    }

    _assignEvents() {
        this.$root.on('click', '[data-upevent-link]', this._onClickFilter.bind(this));
    }

    _onClickFilter(e){
        e.preventDefault();
        const $link = $(e.target).closest('[data-upevent-link]');

        if ($link.hasClass('state_active')) return;
        this.filterListByLink($link);
    }

    filterListByLink($el){
        const locals = this.locals;
        const filterClass = $el.data('upevent-link');
        const $filtered = filterClass? locals.$items.filter('.' + filterClass): null ;

        locals.$items.removeClass('b-eventfut_state_disabled');
        if ($filtered) {
            locals.$items.addClass('b-eventfut_state_disabled');
            $filtered.removeClass('b-eventfut_state_disabled');
        }

        locals.$links.removeClass('state_active');
        $el.addClass('state_active');
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

