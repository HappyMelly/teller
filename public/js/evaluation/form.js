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

function showError(message) {
    $('#error').append(
        $('<div class="alert alert-danger">')
            .text(message)
            .append('<button type="button" class="close" data-dismiss="alert">&times;</button>')
    );
}

/**
 * Retrieve a list of participants for the event
 * @param eventId String
 */
function getParticipants(eventId) {
    var url = jsRoutes.controllers.Participants.participants(eventId).url;
    $.ajax({
        url: url,
        dataType: "json"
    }).done(function(data) {
        var selector = "#participantId";
        var value = parseInt($("#currentParticipantId").val());
        $(selector)
            .empty()
            .append($("<option></option>").attr("value", 0).text("Choose a participant"));
        for(var i = 0; i < data.length; i++) {
            var option = $("<option></option>")
                .attr("value", data[i].id)
                .text(data[i].name);
            if (value == data[i].id) {
                option.attr('selected', 'selected');
            }
            $(selector).append(option);
        }
    }).fail(function() {
        showError("Sorry we don't know anything about the event you try to request")
    });
}

$(document).ready( function() {
    // Binds
    $("#eventId").change(function() {
        var eventId = $(this).find(':selected').val();
        getParticipants(eventId);
    });
    var eventId = $('#currentEventId').val();
    if (eventId) {
        $("#eventId").find('option[value=' + eventId + ']').attr('selected', 'selected');
        getParticipants(eventId);
    }
    var status = $('#status').find(':selected').val();
    $('#hiddenStatus').val(status);
});

