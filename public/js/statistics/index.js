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
 * Renders doughnut chart
 * @param id {string} DOM element's id to insert the rendered chart into
 * @param data {array} Array with data
 */
function drawDoughnutChart(id, data) {
    var ctx = $(id).get(0).getContext("2d");
    new Chart(ctx).Doughnut(data, {
        segmentShowStroke : false,
        animateScale: true
    });
}

/**
 * Renders table with data
 * @param id {string} DOM element's id to insert the rendered chart into
 * @param data {array} Array with data
 * @param renderer {function} Function to render the middle column's content
 */
function drawTable(id, data, renderer) {
    for (var i = 0; i < data.length; i++) {
        $(id).append(
            $("<tr>")
                .append($("<td>").append(i + 1))
                .append($("<td>").append(renderer(data[i])))
                .append($("<td>").append(" " + data[i].value)))
    }
}

/**
 * Loads tab content (if needed) and shows it to a user
 * @param elem Tab button
 * @param type Tab type
 * @returns {boolean}
 */
function showTab(elem, type) {
    var brandId = $('#activeBrandId').val();
    var url = jsRoutes.controllers.cm.Statistics.byFacilitators(brandId).url;
    $.get(url, function(data) {
        var ctx = $("#facilitatorChart").get(0).getContext("2d");
        new Chart(ctx).Line(data, {});
        var stats = data.datasets[0].data;
        $("#facilitatorTotal").text(stats[stats.length - 1]);
        $('#facilitatorWithGoodProfile').text(data.withGoodProfiles);
        $('#facilitatorJoined').text(data.joined);
        $('#facilitatorLeft').text(data.left);
    });
    url = jsRoutes.controllers.cm.Statistics.byEvents(brandId).url;
    $.get(url, function(data) {
        var ctx = $("#eventChart").get(0).getContext("2d");
        new Chart(ctx).Line(data.events, {});
        var stats = data.events.datasets[0].data;
        $("#eventTotal").text(stats[stats.length - 1]);
        $('#futurePaid').text(data.events.future.paid);
        $('#futureFree').text(data.events.future.free);
        $('#confirmedPaid').text(data.events.confirmed.paid);
        $('#confirmedFree').text(data.events.confirmed.free);
        $('#canceledPaid').text(data.events.canceled.paid);
        $('#canceledFree').text(data.events.canceled.free);
        $('#rating').text(data.events.rating.toFixed(2));
        $('#nps').text(data.events.nps.toFixed(2) + "%");
        $('#facilitatorActive').text(data.activeFacilitators);
        $('#organizers').text(data.organizers);
        drawDoughnutChart("#topFacilitatorsChart", data.topFacilitators);
        drawTable("#facilitatorList", data.topFacilitators, function(facilitator) {
            return $("<a>").
                attr("href", jsRoutes.controllers.core.People.details(facilitator.id).url).
                append(facilitator.label);
        });
    });
    url = jsRoutes.controllers.cm.Statistics.byCountries(brandId).url;
    $.get(url, function(data) {
        drawDoughnutChart("#countryChart", data);
        drawTable("#countryList", data, function(country) {
            return " " + country.label
        });
    });
    url = jsRoutes.controllers.cm.Statistics.byParticipants(brandId).url;
    $.get(url, function(data) {
        var ctx = $("#participantChart").get(0).getContext("2d");
        new Chart(ctx).Line(data, {});
        var stats = data.datasets[0].data;
        $("#participantsTotal").text(stats[stats.length - 1]);
        drawDoughnutChart("#rolesChart", data.roles);
        for (var i = 0; i < data.roles.length; i++) {
            $("#rolesList").append(
                $("<tr>")
                    .append($("<td>").append(data.roles[i].label))
                    .append($("<td>").append(" " + data.roles[i].value)))
        }
    });
    return false;
}

$(document).ready( function() {
    $('#sidemenu a').click(function (e) {
        showTab($(this));
    });
    var hash = window.location.hash.substring(1);
    if (!hash) {
        hash = 'facilitators';
    }
    showTab($('#sidemenu a[href="#' + hash + '"]'), hash);
});