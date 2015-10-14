/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2015, Happy Melly http://www.happymelly.com
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
 * If you have questions concerning this license or the applicable additional
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */

/**
 * Cancels the event and removes a tile from the page
 * @param object {object} Link or button
 */
function cancelEvent(object) {
    var url = jsRoutes.controllers.Events.reason($(object).data('id')).url;
    $.get(url, {}, function(data) {
        $('#cancelDialog').remove();
        var dialog = 'cancelDialog';
        $('body').append(
            $('<div id="cancelDialog" class="modal fade" tabindex="-1">').
                attr('id', dialog).attr('role', 'dialog').
                attr('aria-hidden', 'true').append(data));
        $('#' + dialog).modal('show');
        $('#eventCancelButton').on('click', function(e) {
            e.preventDefault();
            var url = jsRoutes.controllers.Events.cancel($(object).data('id')).url;
            $.post(url, {}, function(data) {
                $('#' + dialog).modal('hide');
                afterEventCancellation(object);
                var msg = "Event was successfully canceled";
                success(msg);
            });
        });
    });
}

/**
 * Sends confirmation request and updates action element
 * @param object {object} Link or button
 */
function confirmEvent(object) {
    var url = jsRoutes.controllers.Events.confirm($(object).data('id')).url;
    $.post(url, {}, function(data) {
        var msg = "Event was successfully confirmed";
        $(object).addClass('disabled').text('Confirmed').off('click');
        success(msg);
    });
}

function initializeEventActions() {
    $('.event-confirm').on('click', function(e) {
        e.preventDefault();
        confirmEvent($(this))
    });
    $('.event-cancel').on('click', function(e) {
        e.preventDefault();
        cancelEvent($(this))
    });
}

$(document).ready(function() {
    initializeEventActions();
});
