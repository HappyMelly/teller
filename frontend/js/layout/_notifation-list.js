'use strict';

/**
 * Notification for user about new events
 */

/**
 * Notification object
 * @typedef {Object} NotificationItem
 * @property {String} body      - html of the notification
 * @property {Boolen} unread    - is current notification already viewed?
 * @property {Number} id        - id of the notification
 * @property {String} type      - type of the notification
 */

export default class Widget {
    /**
     *
     * @param {(String|domElement)} selector  - selector or domElement as root element of the widget
     * @constructor
     */
    constructor(selector) {
        const self = this;

        self.$root = $(selector);
        self.locals = self._getDom();

        self._init();
        self._assignEvents();
    }

    /**
     * Get $ elements of the widget
     * @returns {Object} - jQuery links ot the elements of the widget
     * @private
     */
    _getDom() {
        const $root = this.$root;

        return {
            $list: $root.find('[data-notiflist-list]'),
            $close: $root.find('[data-notiflist-close]'),
            $link: $root.find('[data-notiflist-show]'),
            $load: $root.find('[data-notiflist-load]'),
            $count: $root.find('[data-notiflist-count]'),
        };
    }

    /**
     * Initiate base variables for widget
     * @private
     */
    _init() {
        const self = this;

        self.isLoaded = false;
        self.offset = 0;
        self.isVisible = false;

        self._recieveUnreadCount()
            .done(function (count) {
                self.setUnreadCount(count);
            });
    }

    _assignEvents() {
        const self = this;

        self.$root
            .on('click', '[data-notiflist-show], [data-notiflist-close]', self._onClickToggleShow.bind(self))
            .on('click', '[data-notiflist-load]', self._onClickLoadNotification.bind(self))
    }

    /**
     * Event handler for showing and hiding popup
     * @param {Event} e - Event Object
     * @private
     */
    _onClickToggleShow(e){
        this.togglePopup();

        if (!this.isLoaded){
            this.isLoaded  = true;
            this.loadNotification(this.offset);
        }

        e.preventDefault();
    };

    /**
     * Event handler for the button "load more notification"
     * @param {Event} e - Event Object
     * @private
     */
    _onClickLoadNotification(e){
        e.preventDefault();

        this.loadNotification(this.offset);
    };

    /**
     * Add to the dom
     * @param {NotificationItem[]} notifList - list of the notification
     * @private
     */
    _renderNotification(notifList){
        const self = this;

        notifList.forEach(function(item){
            $(item.body)
                .addClass(item.type)
                .toggleClass('is-new', Boolean(item.unread))
                .data('id', item.id)
                .appendTo(self.locals.$list);
        })
    }

    /**
     * Filter notification and return only new
     * @param {NotificationItem[]} notifList - list of the notification
     * @returns {NotificationItem[]} notifList - only new notifications
     * @private
     */
    _filterOnlyNew(notifList){
        return notifList.filter(function(item){
            return item.unread;
        })
    };

    _isHaveNotification(notifList){
        if (!notifList.length && !this.offset) {
            this.$root.addClass('b-notiflist_empty');
            return false;
        }

        if (notifList.length < 5){
            this.$root.addClass('b-notiflist_load_all');
        }
        return true;
    }

    /**
     * Get new load notification and render them
     * @param {Number} offset -
     */
    loadNotification(offset){
        const self = this;

        self._recieveNotification(offset)
            .done(function(notifList){
                if (!self._isHaveNotification(notifList)) return;

                self.offset += notifList.length;
                self._renderNotification(notifList);

                var newNotifList = self._filterOnlyNew(notifList);

                if (!newNotifList.length) return;

                self._sendViewedNewNotif(newNotifList);
                self.setUnreadCount(self.unreadCount - newNotifList.length);
            });
    }

    /**
     * Set unread count
     * @param {Number} count - new value for unread count
     */
    setUnreadCount(count){
        this.unreadCount = (count > 0)? count: 0;

        this.locals.$count.text(this.unreadCount);
        this.$root.toggleClass('b-notiflist_have_notification', Boolean(this.unreadCount));
    }

    showPopup(){
        if (this.isVisible) return;

        this.isVisible = true;
        this.$root.addClass('b-notiflist_show');
    }

    hidePopup(){
        if (!this.isVisible) return;

        this.isVisible = false;
        this.$root.removeClass('b-notiflist_show');
    }

    togglePopup(){
        this.isVisible? this.hidePopup(): this.showPopup();
    }

    // transport
    _recieveUnreadCount(){
        let defer = $.Deferred();
        const url = jsRoutes.controllers.core.Notifications.unread().url;

        $.get(url, function (data) {
            var count = ($.parseJSON(data)).unread;
            defer.resolve(count);
        });

        return defer.promise();
    }

    _recieveNotification(offset){
        let defer = $.Deferred();
        const limit = 5;
        const url = jsRoutes.controllers.core.Notifications.list(offset, limit).url;

        $.get(url, function(data){
            var notifList = $.parseJSON(data)[0];

            defer.resolve(notifList);
        });

        return defer.promise();
    }

    /**
     * Send to the server id of the viewed notification
     * @param {NotificationItem[]} notifList
     * @private
     */
    _sendViewedNewNotif(notifList){
        let ids = [];
        const url = jsRoutes.controllers.core.Notifications.read().url;

        notifList.forEach(function(item){
            ids.push(item.id);
        });

        $.post(url, {ids: ids})
    };

  
    // static
    static plugin(selector) {
        const $elems = $(selector);
        if (!$elems.length) return;

        return $elems.each(function (index, el) {
            let $element = $(el);
            let data     = $element.data('widget.scrollto');

            if (!data) {
                data = new Widget(el);
                $element.data('widget', data);
            }
        })
    }
}
