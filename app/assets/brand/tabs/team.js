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
 * Updates the view after the person was added as a team member
 * @param personId {int} Person id
 * @param name {string} Person name
 * @param brandId {int} Brand id
 */
function addMember(personId, name, brandId) {
    $('#members').append(
        $('<tr>')
            .attr('data-id', personId)
            .attr('data-brandid', brandId)
            .append($('<td>')
                .append($('<a>')
                    .attr('href', jsRoutes.controllers.People.details(personId).url)
                    .append(name)))
            .append('<td><input type="checkbox" value="event"/></td>')
            .append('<td><input type="checkbox" value="evaluation"/></td>')
            .append('<td><input type="checkbox" value="certificate"/></td>')
            .append($('<td>')
                .append($('<a>')
                    .append('Remove')
                    .addClass('remove font-sm')
                    .attr('href', '#')
                    .attr('data-id', personId)
                    .attr('data-name', name)
                    .attr('data-href',
                        jsRoutes.controllers.Brands.removeCoordinator(brandId, personId).url)))
    );
    $('select[name="personId"]').children('option[value=' + personId + ']').remove();
}

/**
 * Updates the view after the person was deleted from the team
 * @param personId {int} Person id
 * @param name {string} Person full name
 */
function removeMember(personId, name) {
    $('tr[data-id="' + personId + '"]').remove();
    var people = [];
    var select = 'select[name="personId"]';
    $(select).children('option').each(function() {
        people.push({ id: $(this).val(), name: $(this).text()});
    });
    people.push({ id: personId, name: name });
    people.sort(function(left, right) { return left.name.localeCompare(right.name); });
    $(select).empty();
    for(var i = 0; i < people.length; i++) {
        var data = people[i];
        $(select).append($('<option>').append(data.name).attr('value', data.id));
    }
}


function initializeTeamActions() {
    $('#addMemberForm').submit(function(e) {
        $.post($(this).attr("action"), $(this).serialize(), null, "json").done(function(data) {
            addMember(data.data.personId, data.data.name, data.data.brandId);
            success(data.message);
        }).fail(function(jqXHR, status, errorCode) {
            if (status == "error") {
                var response = JSON.parse(jqXHR.responseText);
                error(response.message);
            } else {
                var msg = "Internal error. Please try again or contant the support team.";
                error(msg);
            }
        });
        // Prevent the form from submitting with the default action
        return false;
    });
    $('#members').on('click', 'a.remove', function(e) {
        var personId = $(this).data('id'),
            name = $(this).data('name');
        $.ajax({
            type: "DELETE",
            url: $(this).data('href'),
            dataType: "json"
        }).done(function(data) {
            removeMember(personId, name);
            success(data.message);
        }).fail(function(jqXHR, status, errorCode) {
            if (status == "error") {
                var response = JSON.parse(jqXHR.responseText);
                error(response.message);
            } else {
                var msg = "Internal error. Please try again or contant the support team.";
                error(msg);
            }
        });
        return false;
    });
    $('#members').on('change', 'input', function(e) {
        var brandId = $(this).parents('tr').data('brandid');
        var personId = $(this).parents('tr').data('id');
        var type = $(this).val();
        var url = jsRoutes.controllers.Brands.turnNotificationOff(brandId, personId, type).url;
        if (this.checked) {
            url = jsRoutes.controllers.Brands.turnNotificationOn(brandId, personId, type).url;
        }
        var that = $(this);
        $.post(url, {}, null, "json").done(function(data) {
            success(data.message);
        }).fail(function(jqXHR, status, errorCode) {
            if (status == "error") {
                var response = JSON.parse(jqXHR.responseText);
                error(response.message);
            } else {
                var msg = "Internal error. Please try again or contant the support team.";
                error(msg);
            }
            that.attr('checked', false);
        });
    });
    $('#members .glyphicon-info-sign').tooltip();
}

$(document).ready( function() {
    initializeTeamActions();
});