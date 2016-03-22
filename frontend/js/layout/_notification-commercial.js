'use strict';

export default class Widget {
    /**
     * Filter history
     * @param {String} selector
     */
    constructor(selector) {
        this.$root = $(selector);
        this.locals = this._getDom();
        this.uniqueKey = 'notifiction';

        if (!this._isShowed()) {
            this.$root.slideDown();
        }
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
        this.$root.on('click', '[data-notif]', this._onClickBtn.bind(this));
    }

    _onClickBtn(e){
        e.preventDefault();
        const self = this;
        const $link = $(e.currentTarget);
        const url = $link.attr('href');

        self._sendIsShowed(url)
            .done(function(){
                self.$root.addClass('b-notification_state_thank');
                
                setTimeout(function(){
                    self.hide();
                }, 3000);
            });
    }

    _isShowed() {
        const value = localStorage.getItem(this.uniqueKey);
        return value && value == 'showed';
    }

    _sendIsShowed(url) {
        const self = this;
        let defer = $.Deferred();

        $.post(url, function(){
            localStorage.setItem(self.uniqueKey, 'showed');
            defer.resolve();
        });
        
        return defer.promise();
    }

    hide() {
        this.$root.slideUp();
    }

    // static
    static plugin(selector) {
        const $elems = $(selector);
        if (!$elems.length) return;

        return $elems.each(function (index, el) {
            let $element = $(el);
            let data = $element.data('widget');

            if (!data) {
                data = new Widget(el);
                $element.data('widget', data);
            }
        })
    }
}
