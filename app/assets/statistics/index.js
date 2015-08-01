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
 * Loads tab content (if needed) and shows it to a user
 * @param elem Tab button
 * @param type Tab type
 * @returns {boolean}
 */
function showTab(elem, type) {
    var brandId = 1;
    var url = jsRoutes.controllers.Statistics.byFacilitators(brandId).url;
    $.get(url, function(data) {
        var ctx = $("#facilitatorChart").get(0).getContext("2d");
        new Chart(ctx).Line(data, {});
        var stats = data.datasets[0].data;
        $("#facilitatorTotal").text(stats[stats.length - 1]);
        $('#facilitatorJoined').text(data.joined);
        $('#facilitatorLeft').text(data.left);
    });
    url = jsRoutes.controllers.Statistics.byEvents(brandId).url;
    $.get(url, function(data) {
        var ctx = $("#eventChart").get(0).getContext("2d");
        new Chart(ctx).Line(data, {});
        var stats = data.datasets[0].data;
        $("#eventTotal").text(stats[stats.length - 1]);
        $('#futurePaid').text(data.future.paid);
        $('#futureFree').text(data.future.free);
        $('#confirmedPaid').text(data.confirmed.paid);
        $('#confirmedFree').text(data.confirmed.free);
        $('#canceledPaid').text(data.canceled.paid);
        $('#canceledFree').text(data.canceled.free);
        $('#rating').text(data.rating.toFixed(2));
    });
    url = jsRoutes.controllers.Statistics.byCountries(brandId).url;
    $.get(url, function(data) {
        var ctx = $("#countryChart").get(0).getContext("2d");
        new Chart(ctx).Doughnut(data, {
            segmentShowStroke : false,
            animateScale: true
        });
        $("#countryTotal").text(data.length);
        for (var i = 0; i < data.length; i++) {
            $("#countryList").append(
                $("<tr>")
                    .append($("<td>").append(i + 1))
                    .append($("<td>").append(data[i].label))
                    .append($("<td>").append(data[i].value)))
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