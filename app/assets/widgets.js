/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2016, Happy Melly http://www.happymelly.com
 *
 * This file is part of the Happy Melly Teller.
 *
 * Happy Melly Teller is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Happy Melly Teller is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Happy Melly Teller.  If not, see <http://www.gnu.org/licenses/>.
 *
 * If you have questions concerning this license or the applicable additional terms, you may contact
 * by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */

/**
 *  Profile widget with completion information
 */
(function ($, App) {
    'use strict';

    function CompletionWidget(selector){
        var self = this;

        self.selector = selector;
        self.$el = $(selector);
        self.currentUserId = $("#activeUserId").val();

        self.assignEvents();
        self.reload();
    }

    CompletionWidget.prototype.assignEvents = function(){
        var self = this;

        App.events
            .sub('hmtReloadCompletionWidget', function () {
                self.reload();
            });

        self.$el
            // show tab in profile page and dialog
            .on('click', '.js-completion-tab', function (e) {
                var $this = $(this),
                    menuTab = $this.data('tab'),
                    modalDialog = $this.data('popup');

                if (!self.isProfilePage()) {
                    App.history.add('hmtShowTabAndDialog', [menuTab, modalDialog]);

                    window.location = jsRoutes.controllers.core.People.details(self.currentUserId).url;
                    return false;
                }

                App.events.pub('hmtShowTabAndDialog', [menuTab, modalDialog]);
                e.preventDefault();
            })
            // show profile page and photo upload dialog
            .on('click', '.js-completion-photo', function (e) {
                var $this = $(this),
                    modalDialog = $this.data('popup');

                if (!self.isProfilePage()) {
                    App.history.add('hmtShowSelectPhotoForm', [modalDialog]);

                    window.location = jsRoutes.controllers.core.People.details(self.currentUserId).url;
                    return false;
                }

                App.events.pub('hmtShowSelectPhotoForm', [modalDialog]);
                e.preventDefault();
            });
    };

    CompletionWidget.prototype.reload = function(){
        var self = this,
            url = jsRoutes.controllers.core.ProfileStrengths.personWidget(self.currentUserId, true).url;

        $.get(url, function(data){
            self.$el.html(data);
            self.checkStatus();
        });
    };

    CompletionWidget.prototype.isProfilePage = function(){
        var personId = $('#personId').val();

        return (this.currentUserId == personId);
    };

    CompletionWidget.prototype.checkStatus = function(){
        var $el = this.$el,
            value = $.trim($el.find('[data-completion-value]').text());

        $el.toggleClass('b-completion_state_progress', value !== '100%');
    }

    App.widgets.CompletionWidget = CompletionWidget;

})(jQuery, App);


/**
 *  Upload photo widget
 */
(function ($, App) {
    'use strict';

    function UploadPhotoWidget(options){
        var self = this;

        self.$el = $(options.selector);

        self.defaults = {
            urlPersonUpdate: '',
            urlDelete: '',
            urlContent: '',
            uploadedPhoto: null
        }

        self.locals = {
            selector: options.selector,
            $el: $(options.selector),
            $modalContent: self.$el.find('#uploadPhotoContent'),
            $modalDialog: self.$el.find('#uploadPhotoDialog')
        };

        $.extend(this, self.defaults, options);

        self.assignEvents();
    }

    UploadPhotoWidget.prototype.assignEvents = function(){
        var self = this;

        self.locals.$el
            .on('click', '.js-choose-link', function(e){
                self.getModalContent();
                self.locals.$modalDialog.modal('show');

                e.preventDefault();
            })
            .on('click', '.b-photoupload__item', function () {
                self.switchActivePhoto($(this));
            })
            .on('click', '#uploadBtnSave', function (e) {
                self.updatePhoto();
                e.preventDefault();
            })
            .on('click', '#uploadBtnDelete', function (e) {
                self.deletePhoto();
                e.preventDefault();
            })

        App.events.sub('hmtShowSelectPhotoForm', function(){
            self.getModalContent();
            self.locals.$modalDialog.modal('show');
        });
    };

    UploadPhotoWidget.prototype.getModalContent = function(){
        var self = this;

        if (self.urlContent){
            $.get( self.urlContent, function(data) {
                self.locals.$modalContent.html(data);
                self.setupCustomPhotoUpload();
            });
        } else {
            self.setupCustomPhotoUpload();
        }
    };

    UploadPhotoWidget.prototype.switchActivePhoto = function($object){
        this.locals.$el
            .find('.b-photoupload__item').removeClass('type_active');
        $object.addClass('type_active');
    };

    UploadPhotoWidget.prototype.setupCustomPhotoUpload = function(){
        var self = this;
        self.src = '';

        $('#photoUpload').fileupload({
            dataType: 'json',
            disableImageResize: false,
            imageMaxWidth: 300,
            imageMaxHeight: 300,
            imageCrop: false,
            autoUpload: false,
            replaceFileInput: false
        }).on('fileuploadadd', function (e, data) {
            self.uploadedPhoto = data;
            self.setCustomImage(URL.createObjectURL(data.files[0]));
        })
    }

    UploadPhotoWidget.prototype.setCustomImage = function(srcImage){
        this.isSetCustomImage = true;
        $('#uploadCustomPhoto').attr('src', srcImage)
    }

    UploadPhotoWidget.prototype.deleteCustomImage = function(){
        this.isSetCustomImage = false;
        $('#uploadCustomPhoto').attr('src', jsRoutes.controllers.Assets.at('images/happymelly-face-white.png'))
    }

    UploadPhotoWidget.prototype.updatePhoto = function(){
        var self = this,
            $uploadType = self.locals.$modalContent.find('.type_active'),
            type = $uploadType.attr('id'),
            srcImg = $uploadType.find('.b-photoupload__img').attr('src');

        if (!self.isSetCustomImage && type != "gravatar"){
            self.locals.$modalDialog.modal('hide');
            return;
        }

        if (type == "custom") {
            self.uploadedPhoto.submit();
        }

        if (self.urlPersonUpdate){
            self.updatePersonField(type, srcImg);
        } else {
            self.setPhotoOnPage(srcImg);
            self.locals.$modalDialog.modal('hide');
        }
    };

    UploadPhotoWidget.prototype.updatePersonField = function(type, srcImage){
        var self = this;

        $.post(
            self.urlPersonUpdate,
            { type: type },
            null,
            "json"
        ).done( function(data){
            self.setPhotoOnPage(srcImage);
            self.locals.$modalDialog.modal('hide');
        })
    }

    UploadPhotoWidget.prototype.setPhotoOnPage = function(src){
        var self = this;

        $('.b-avatar__img-real').attr('src', src);
        self.$el.addClass('b-avatar_stat_real');

        App.events.pub('hmtReloadCompletionWidgethmt');
    }

    UploadPhotoWidget.prototype.deletePhoto = function(){
        var self = this;

        $.ajax({
            type: "DELETE",
            url: self.urlDelete,
            dataType: "json"
        }).done(function() {
            self.$el.removeClass('b-avatar_stat_real');
            self.deleteCustomImage();

            App.events.pub('hmtReloadCompletionWidget');
        });
        return false;
    };

    App.widgets.UploadPhotoWidget = UploadPhotoWidget;

})(jQuery, App);


/**
 *  Widget for sidebar menu and tabs
 */
(function ($, App) {
    'use strict';

    function Sidemenu(selector, options){
        var self = this,
            hash = '',
            hashSelector;

        self.options = $.extend({
            hashDefault: '',
            afterShowTab: null
        }, options)

        self.selector = selector;
        self.$el = $(selector);
        self.loadedTabs = [];

        // get the id of active tab from url or use default id
        hash = window.location.hash.substring(1) || self.options.hashDefault;

        if (hash){
            hashSelector = '[href="#'+ hash +'"].b-sidemenu__link';
            self.showTabByLink($(hashSelector));
        }

        self.assignEvents();
    }

    Sidemenu.prototype.assignEvents = function(){
        var self = this;

        self.$el
            .on('click', '.b-sidemenu__link', function (e) {
                var $this = $(this);

                self.showTabByLink($this);
                e.preventDefault();
            })
    };

    Sidemenu.prototype.loadContentForTab = function(url, targetSelector, cb){
        var self = this;

        if ($.inArray(targetSelector, self.loadedTabs) < 0 && url) {
            $.get(url, function(data) {
                $(targetSelector).html(data);
                self.loadedTabs.push(targetSelector);

                cb && cb();
            });
        } else {
            cb && cb();
        }
    };

    Sidemenu.prototype.showTabByLink = function($selector){
        var self = this,
            $link = $selector,
            url = $link.attr('data-href'),
            target = $link.attr('href');

        if ($link.hasClass('active')) return;

        self.loadContentForTab(url, target, function(){
            self.$el.find('.b-sidemenu__link').removeClass('active');
            $link.addClass('active').tab('show');

            self.options.afterShowTab && self.options.afterShowTab();
        })
    };

    App.widgets.Sidemenu = Sidemenu;

})(jQuery, App);

/**
 *  Widget for Datatable with filtering
 */
(function ($, App) {
    'use strict';

    function DataTable(selector, options, arrFuncFilters){
        var self = this,
            $el = $(selector);

        self.el = selector;
        self.$el = $el;
        self.$table = $el.find('.js-table-list');
        self.options = $.extend({}, options);
        self.arrFuncFilters = arrFuncFilters;

        self.assignEvents();
        self.init();
    }

    DataTable.prototype.init = function(){
        var self = this;

        $.fn.dataTable.moment('d MMM yyyy');
        Array.prototype.push.apply($.fn.dataTableExt.afnFiltering, self.arrFuncFilters);
        self.dataPlugin = self.$table.dataTable(self.options);

        self.dataPlugin.fnDraw();
    }

    DataTable.prototype.assignEvents = function(){
        var self = this;

        self.$el
            .on('click', '.js-filter-link', function (e) {
                var $this = $(this);

                self.filterTableByLink($this);
                e.preventDefault();
            })
    };

    DataTable.prototype.filterTableByLink = function($link){
        var self = this,
            $filtersBlock = $link.closest('.table-filters');

        if ($link.hasClass('active')) return;

        $filtersBlock.find('.js-filter-link').removeClass('active');
        $link.addClass('active');
        self.dataPlugin.fnDraw();
    };

    App.widgets.DataTable = DataTable;

})(jQuery, App);


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


/**
 *  Notification list
 */
(function ($, App) {
    'use strict';

    function NotificationList(selector, options){
        var self = this;

        self.$root = $(selector);
        self.options = $.extend({}, options, self.$root.data());

        self.locals = {
            $list: self.$root.find('[data-notiflist-list]'),
            $close: self.$root.find('[data-notiflist-close]'),
            $link: self.$root.find('[data-notiflist-show]'),
            $load: self.$root.find('[data-notiflist-load]'),
            $count: self.$root.find('[data-notiflist-count]'),
            $listOld: self.$root.find('[data-notiflist-old-list]')
        };

        self.init();
        self.assignEvents();
    }

    NotificationList.prototype.init = function(){
        this.isPopupVisible = false;
        this.isLoaded = null;
        this.unreadCount = 0;
        this.offset = 0;

        this.getCountUnread();
    }

    NotificationList.prototype.assignEvents = function(){
        var self = this;

        self.$root
            .on('click', '[data-notiflist-show], [data-notiflist-close]', function (e) {
                self.togglePopup();
                e.preventDefault();
            })
            .on('click', '[data-notiflist-load]', function (e) {
                self.getNotification(self.offset);
                e.preventDefault();
            })
    };

    NotificationList.prototype.getNotification = function(offset, limit){
        var self = this,
            limitNotification = (!limit || limit > 5)? 5: limit,
            url = jsRoutes.controllers.core.Notifications.list(offset, limitNotification).url,
            length, unread;


        $.get(url, function(data){
            data = $.parseJSON(data)[0];
            length = data.length;

            if (!length) {
                self.$root.addClass('b-notiflist_load_all');
                return;
            };

            self.isLoaded = true;
            self.offset += length;
            self.unreadCount -= length;

            self.setUnreadCount(unread);
            self.setReaded(data);
            self.renderNotification(data)
        })
    };

    NotificationList.prototype.setReaded = function(data){
        var ids = [],
            url = jsRoutes.controllers.core.Notifications.read().url

        data.forEach(function(item){
            ids.push(item.id);
        });

        $.post(url, {
            ids: ids
        }, function(){

        })
    }

    NotificationList.prototype.renderNotification = function(data){
        var self = this;

        data.forEach(function(item){
            var $notif = $(item.body)
                .addClass(item.type)
                .data('id', item.id)
                .appendTo(self.locals.$list);
        })
    }

    NotificationList.prototype.getCountUnread = function(){
        var self = this,
            url = jsRoutes.controllers.core.Notifications.unread().url;

        $.get(url, function (data) {
            self.unreadCount = ($.parseJSON(data)).unread;
            self.setUnreadCount(self.unreadCount);
        })
    };

    NotificationList.prototype.showPopup = function(){
        var self = this;

        if (!self.isLoaded) self.getNotification(0,self.unreadCount);

        if (self.isPopupVisible) return;
        self.isPopupVisible = true;
        self.$root.addClass('b-notiflist_show');
    }

    NotificationList.prototype.hidePopup = function(){
        var self = this;

        if (!self.isPopupVisible) return;

        self.isPopupVisible = false;
        self.$root.removeClass('b-notiflist_show');
    }

    NotificationList.prototype.togglePopup = function(){
        this.isPopupVisible? this.hidePopup(): this.showPopup();
    }

    NotificationList.prototype.setUnreadCount = function(count){
        this.unreadCount = (count > 0)? count: 0;

        this.locals.$count.text(this.unreadCount);
        this.$root.toggleClass('b-notiflist_have_notification', Boolean(this.unreadCount));
    }

    App.widgets.NotificationList = NotificationList;
})(jQuery, App);