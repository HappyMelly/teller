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
        
        App.events.sub('hmt.sendCredit.success', this._onAddNewItem.bind(this));
    }

    _onClickFilter(e) {
        e.preventDefault();
        const $link = $(e.currentTarget);

        if ($link.hasClass('state_selected')) return;        
        this.filterByLink($link);
    }

    filterByLink($link){
        const filterText = $link.data('filter-link');
        
        this.setActiveLink($link);
        this.filterList(filterText);
    }

    _onAddNewItem(data){
        
    }

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
        if ($el.hasClass('state_selected')) return;
        
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


