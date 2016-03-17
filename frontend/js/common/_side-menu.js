'use strict';

export default class Widget {

    constructor(selector, options) {
        let hashSelector;
        let defaultOptions = {
                hashDefault: ''
            };
        
        this.$root = $(selector);
        this.options = $.extend({}, defaultOptions, options);
        this.loadedTabs = [];

        let hash = window.location.hash.substring(1) || this.options.hashDefault;

        if (hash){
            hashSelector = '[href="#'+ hash +'"][data-menuside]';
            this._showTabByLink($(hashSelector));
        }
        this._assignEvents();
    }

    _assignEvents() {
        this.$root.on('click', '[data-menuside]', (e) => {
            var $link = $(e.target);

            this.showTabByLink($link);
            e.preventDefault();
        })
    }

    /**
     * 
     * @param {jQuery} $link - clicked link
     * @private
     */
    showTabByLink($link){
        const url = $link.attr('data-href');
        const target = $link.attr('href');

        if ($link.hasClass('active')) return;

        this._loadContentForTab(url, target, () => {
            this.$root.find('[data-menuside]').removeClass('active');
            $link.addClass('active').tab('show');

            this.$root.trigger('hmt.menuLoadTab');
        })
    }

    /**
     * 
     * @param {String} url              - url of loaded content
     * @param {jQuery} targetSelector   - div where we should insert content
     * @param {Function} cb             - callback function
     */
    _loadContentForTab(url, targetSelector, cb){
        const self = this;

        if ($.inArray(targetSelector, self.loadedTabs) < 0 && url) {
            $.get(url, (data) => {
                $(targetSelector).html(data);
                self.loadedTabs.push(targetSelector);

                cb && cb();
            });
        } else {
            cb && cb();
        }
    }    

    // static
    static interface(selector, options) {
        const $elems = $(selector);
        if (!$elems.length) return;

        return $elems.each(function (index, el) {
            let $element = $(el);
            let data     = $element.data('widget');

            if (!data) {
                data = new Widget(el, options);
                $element.data('widget', data);
            }
        })
    }
}


