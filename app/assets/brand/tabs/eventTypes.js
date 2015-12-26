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
 * If you have questions concerning this license or the applicable additional terms, you may contact
 * by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */

/**
 * Returns data to update event type retrieved from a event type row
 *
 * @param element {object} jQuery table row representation
 */
function collectEventTypePostData(element) {
    var data = [];
    data[0] = { name: 'brandId', value: $(element).data('brandid') };
    var i = 1;
    $(element).children('td').each(function() {
        data[i] = { name: $(this).data('name'), value: $(this).text() };
        i += 1;
    });
    var free = $(element).find('[type="checkbox"]').prop('checked');
    data[i] = { name: 'free', value: free };
    return data;
}

/**
 * Updates event type
 *
 * @param object Changed field
 * @param value New parameter value
 */
function updateEventType(object, value) {
    $('#notification').html("");
    var id = $(object).parent().data('id');
    var brandId = $(object).parent().data('brandid');
    var data = collectEventTypePostData($(object).parent());
    $("body").css("cursor", "progress");
    $.ajax({
        type: "POST",
        url: jsRoutes.controllers.EventTypes.update(brandId, id).url,
        data: data
    }).done(function(data) {
        success("You successfully updated the event type");
        if (value != null) {
            $(object).text(value);
        }
    }).fail(function(jqXHR, status, errorCode) {
        if (status == "error") {
            var response = JSON.parse(jqXHR.responseText);
            error(response.message);
        } else {
            var msg = "Unexpected error. Please contact the support team.";
            error(msg);
        }
    }).complete(function() {
        $("body").css("cursor", "default");
    });
    return true;
}

function initializeEventTypesActions() {
    $('#eventTypes').editableTableWidget();
    $('#eventTypes td').on('validate', function(evt, newValue) {
        if (newValue == "") {
            return false; // mark cell as invalid
        }
    }).on('change', function(evt, newValue) {
        return updateEventType($(this), newValue);
    });
    $('#eventTypes input').on('change', function(e) {
        return updateEventType($(this).parent(), null);
    });
}

$(document).ready( function() {
    initializeEventTypesActions();
});