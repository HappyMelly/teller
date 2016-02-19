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

App.pages.Common = {
    initPlugins: function(){
        var $scroll = $('.js-link-target');
        if ($scroll.length){
            $scroll.scrollToEl();
        }

        var $markdown = $('[markdownpreview]');
        if ($markdown.length) {
            $markdown.previewMarkdown();
        }

        var $dataField = $('[data-type="date"]');
        if ($dataField.length){
            $dataField.datetimepicker({
                useCurrent: false,
                pickTime: false
            });
        }
    },
    initWidgets: function(){
        var $completionWidget = $('.js-completion-widget');
        if ($completionWidget.length){
            new App.widgets.CompletionWidget('.js-completion-widget');
        }

        var $notificationWidget = $('.js-notification-widget');
        if ($notificationWidget.length){
            new App.widgets.TopNotification('.js-notification-widget');
        }

        var $notificationListWidget = $('.js-notification-list');
        if ($notificationListWidget.length){
            new App.widgets.NotificationList('.js-notification-list');
        }
    }
};

function getPersonId() {
    return $('#personId').val();
}

function initializeFileUploadField() {
    $('.js-fileupload').each(function(e) {
        var wrapper = $(this),
            input = wrapper.find("input"),
            button = wrapper.find("button"),
            label = wrapper.find("div");

        button.focus(function(){
            input.focus()
        });

        // Crutches for the :focus style:
        input.focus(function(){
            wrapper.addClass("focus");
        }).blur(function(){
            wrapper.removeClass("focus");
        });

        var fileAPI = (window.File && window.FileReader && window.FileList && window.Blob) ? true : false;
        input.change(function(){
            var filename;
            if(fileAPI && input[ 0 ].files[ 0 ])
                filename = input[ 0 ].files[ 0 ].name;
            else
                filename = input.val();
            if (!filename.length)
                return;

            if (label.is(":visible")) {
                label.text(filename);
                button.text("Choose File");
            } else {
                button.text(filename);
            }
        }).change();
    });
}


$(document).ready(function() {
    var common = App.pages.Common;

    common.initPlugins();
    common.initWidgets();

    initializeFileUploadField();
});