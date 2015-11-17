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
 * Show an error
 * @param message {string}
 */
function showError(message) {
    $('#error').append(
        $('<div class="alert alert-danger">')
            .text(message)
            .append('<button type="button" class="close" data-dismiss="alert">&times;</button>')
    );
}

/**
 * Retrieve all events for a specified brand and fill in the selector
 * @param brand {string}
 */
function getPastEvents(brand) {
    $.ajax({
        url: '/brand/' + brand + '/events?future=false',
        dataType: "json"
    }).done(function(data) {
        var selector = "#eventIdMoveForm";
        $(selector)
            .empty()
            .append($("<option></option>").attr("value", 0).text(""));
        for(var i = 0; i < data.length; i++) {
            var option = $("<option></option>")
                .attr("value", data[i].id)
                .text(data[i].title);
            $(selector).append(option);
        }
    }).fail(function() {
        showError("Sorry we don't know anything about the brand you try to request");
    });
}

function addDetailsToTable(object, data) {
    var row = $(object).parents('tr').first();
    var tableContainer = $("<tr class='event-details active'>").append(
        $("<td colspan='10'>").append(data));
    $(row).addClass('active').after(tableContainer);
}

function addDetailsToList(object, data) {
    var body = $(object).parents('.list-group-item-body').first();
    var div = $("<div class='row list-group-item-text event-details'>").append(
        $("<div class='evaluation-overview col-md-12'>").append(data));
    $(body).append(div);
    $(object).parents('.list-group-item').addClass('active');
}

function removeDetailsFromTable(object) {
    $(object).parents('tr').first().removeClass('active');
}

function removeDetailsFromList(object) {
    $(object).parents('.list-group-item').removeClass('active');
}

/**
 * Shows details  of events
 * @param object {object} Action button
 * @param container {string} Type of container (table or link)
 */
function showDetails(object, container) {
    var url = jsRoutes.controllers.Events.detailsButtons($(object).data('event')).url;
    $.get(url, {}, function(data) {
        if (container == "table") {
            addDetailsToTable(object, data);
        } else {
            addDetailsToList(object,data);
        }
        $(object).children('span').removeClass('glyphicon-chevron-down').
        addClass('glyphicon-chevron-up');
        $(object).addClass('active');
        // initializeParticipantActionsInDetails(container);
    });
}

function hideDetails(object, container) {
    if (container == "table") {
        removeDetailsFromTable(object);
    } else {
        removeDetailsFromList(object);
    }
    $(object).children('span').removeClass('glyphicon-chevron-up').
        addClass('glyphicon-chevron-down');
    $('.event-details').remove();
    $(object).removeClass('active');
}

function hideAllDetails() {
    $('.circle-show-more').removeClass('active').find('span').
        removeClass('glyphicon-chevron-up').addClass('glyphicon-chevron-down');
    $('tr.active').removeClass('active');
    $('div.active').removeClass('active');
    $('.event-details').remove();
}

/**
 * Shows/hides details
 * @param object {object} Action button
 * @param container {string} Type of container (table or list)
 */
function toggleDetails(object, container) {
    if ($(object).hasClass('active')) {
        hideDetails(object, container);
    } else {
        hideAllDetails();
        showDetails(object, container);
    }
}

/**
*/
function initializeEventActions(container) {
  $('.circle-show-more').on('click', function() {
      toggleDetails($(this), container);
  });
}
