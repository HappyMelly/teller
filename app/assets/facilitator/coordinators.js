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
 * Filter facilitators by license
 */
function filterByLicense(oSettings, aData, iDataIndex) {
    var type = $('.filter > a.active').data('type');
    switch(type) {
        case 'active':
            return aData[6] == 'true';
        case 'joined':
            return aData[7] == 'true';
        case 'left':
            return aData[8] == 'true';
        case 'expiring':
            return aData[9] == 'true';
        default:
            return true;
    }
}

/**
 * Adds/removes badge to/from facilitator
 * @param obj Checkbox object
 */
function toggleBadge(obj) {
    var badgeId = $(obj).data('id');
    var brandId = $(obj).data('brand');
    var personId = $(obj).data('person');
    if (obj.checked) {
        var url = jsRoutes.controllers.Facilitators.addBadge(personId, brandId, badgeId).url;
        $.post(url, {}, function(data) {
            success(data.message)
        }, "json");
    } else {
        $.ajax({
            type: "DELETE",
            url: jsRoutes.controllers.Facilitators.deleteBadge(personId, brandId, badgeId).url,
            data: {},
            dataType: "json"
        }).done(function(data) {
            success(data.message);
        })
    }
}

/**
 *   Writes the html for facilitator details.
 *   @param row {object} DataTable row object
 */
function renderDetails(row) {
    var facilitatorId = $(row.node()).data('id');
    var brandId = $(row.node()).data('brand');
    var url = jsRoutes.controllers.Facilitators.details(facilitatorId, brandId).url;
    $.get(url).done(function (content) {
        row.child(content, 'active').show();
        $('.badge-switcher').on('click', function(e) {
            toggleBadge(this);
        });
    });
}

$.fn.dataTableExt.afnFiltering.push(filterByLicense);

$(document).ready( function() {
    $.fn.dataTable.moment('d MMM yyyy');

    var facilitators = $('#facilitators')
        .dataTable({
            "sDom": '<"toolbar">rtip',
            "iDisplayLength": 25,
            "asStripeClasses":[],
            "bLengthChange": false,
            "order": [[ 0, "asc" ]],
            "columnDefs": [{
                "visible": false,
                "targets": [6, 7, 8, 9]
            }, {
                "bSortable": false,
                "targets": 10
            }]
        });

    (new TableWithDetails(facilitators, 'facilitators', renderDetails));

    $("div.toolbar").html($('#filter-containter').html());
    $('#filter-containter').empty();

    facilitators.fnDraw();

    $('.filter > a').on('click', function(e) {
        e.preventDefault();
        $('.filter > a').removeClass('active');
        $(this).addClass('active');
        facilitators.fnDraw();
    });
});