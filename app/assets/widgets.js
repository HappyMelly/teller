/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2014, Happy Melly http://www.happymelly.com
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
            .on('hmtReloadCompletionWidget', function () {
                self.reload();
            });

        self.$el
            .on('click', '.js-completion-tab', function (e) {
                var $this = $(this),
                    sidebarTab = $this.data('tab'),
                    popupSelector = $this.data('popup');

                if (!self.isProfilePage()) {
                    App.history.add('hmtShowTab', [sidebarTab]);
                    App.history.add('hmtShowProfileDialog', [popupSelector]);

                    window.location = jsRoutes.controllers.People.details(self.currentUserId).url;
                    return false;
                }

                App.events.trigger('hmtShowTab', [sidebarTab]);
                App.events.trigger('hmtShowProfileDialog', [popupSelector]);
                e.preventDefault();
            })
            .on('click', '#addPhotoLink', function (e) {
                if (!self.isProfilePage()) return false;

                App.events.trigger('hmtShowSelectPhotoForm');
                e.preventDefault();
            });
    };

    CompletionWidget.prototype.reload = function(){
        var self = this,
            url = jsRoutes.controllers.ProfileStrengths.personWidget(self.currentUserId, true).url;

        $.get(url, function(data){
            self.$el.html(data);
        });
    };

    CompletionWidget.prototype.isProfilePage = function(){
        var personId = $('#personId').val();

        return (this.currentUserId == personId);
    };

    App.widgets.CompletionWidget = CompletionWidget;

})(jQuery, App);





