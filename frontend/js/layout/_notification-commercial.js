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



/**
 *  Header notification
 */
(function ($, App) {
    'use strict';

    function TopNotification(selector, options){
        var self = this;

        self.$root = $(selector);
        self.options = $.extend({}, options, self.$root.data());
        self.options.uniqueKey = 'topNotifiction';

        if (!self.isShowed()) {
            self.show();
        };
        self.assignEvents();
    }

    TopNotification.prototype.assignEvents = function(){
        var self = this;

        self.$root
            .on('click', '[data-notification-close]', function (e) {
                self.setIsShowed({
                    status: 'close'
                });
                self.hide();
                e.preventDefault();
            })
            .on('click', '[data-notification-accept]', function () {
                self.setIsShowed({
                    status: 'accept'
                });
            })
    };

    TopNotification.prototype.isShowed = function(){
        var self = this,
            value = localStorage.getItem(self.options.uniqueKey);

        return value && value == 'showed';
    }

    TopNotification.prototype.setIsShowed = function(data){
        var self = this;

        localStorage.setItem(self.options.uniqueKey, 'showed');
        $.post('/', {
            status: data.status
        })
    }

    TopNotification.prototype.hide = function(){
        this.$root.removeClass('state_show');
    }

    TopNotification.prototype.show = function(){
        this.$root.addClass('state_show');
    }

    App.widgets.TopNotification = TopNotification;

})(jQuery, App);

