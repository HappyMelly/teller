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
 * Updates link text and source for adding/removing endorsements
 * @param url Action link
 * @param type if true, add action; if false, remove action;
 */
function updateEndorsementAction(url, type) {
    if (type) {
        $('#endorsement').addClass('add').removeClass('remove').
            attr('href', url).text('Add as Endorsement');
    } else {
        $('#endorsement').addClass('remove').removeClass('add').
            attr('href', url).text('Remove Endorsement');
    }
}

$(document).ready( function() {
    initializeParticipantActions("table");
    initializeParticipantActionsInDetails();
    $("#endorsement").on('click', function(e) {
        e.preventDefault();
        if ($(this).hasClass("add")) {
            $.post($(this).attr('href'), {}, function(data) {
                _serverData.endorsementId = data.endorsementId;
                var url = jsRoutes.controllers.Endorsements.remove(_serverData.personId, _serverData.endorsementId).url;
                updateEndorsementAction(url, false);
            }, "json");
        } else {
            $.ajax({
                type: "DELETE",
                url: $(this).attr('href'),
                dataType: "json"
            }).done(function(data) {
                var url = jsRoutes.controllers.Endorsements.createFromEvaluation(_serverData.eventId, _serverData.evaluationId).url;
                updateEndorsementAction(url, true);
            });
        }
        return false;
    });
});

