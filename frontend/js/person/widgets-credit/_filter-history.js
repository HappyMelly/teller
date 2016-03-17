'use strict';


export default class Widget {
    /**
     * Filter history
     * @param {String} selector
     */
    constructor(selector) {
        this.$root = $(selector);
        this.locals = this._getDom();

        this._assignEvents();
    }

    _getDom() {
        const $root = this.$root;

        return {
            $list: $root.find('[data-filter-list]'),
            $items: $root.find('[data-filter-text]'),
        };
    }

    _assignEvents() {
        this.$root.on('click', '[data-filter-link]', this._onClickFilter.bind(this));
    }

    _onClickFilter(e) {
        const $link = $(e.target);
        const filterText = $link.data('filter-link');

        e.preventDefault();

        if ($link.hasClass('state_selected')) return;

        this.setActiveLink($link);
        this.filterList(filterText);
    };

    /**
     * Filter list through text
     * @param {String} filterText
     */
    filterList(filterText) {
        const $items = this.locals.$items;

        if (filterText == 'all') {
            $items.removeClass('state_hidden');
            return;
        }

        $items.each((index, el) => {
            const $el = $(el);
            const isHidden = $el.data('filter-text').indexOf(filterText) === -1;

            $el.toggleClass('state_hidden', isHidden);
        });
    };

    /**
     * Set link to active and deactivate other
     * @param {jQuery} $el
     */
    setActiveLink($el) {
        $el.addClass('state_selected')
            .siblings().removeClass('state_selected');
    };
    
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


