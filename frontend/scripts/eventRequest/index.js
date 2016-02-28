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
 * Renders event request details
 * @param row DataTable row object
 */
function loadDetails(row) {
    var requestId = $(row.node()).data('id');
    var brandId = $(row.node()).data('brand');
    var url = jsRoutes.controllers.EventRequests.details(brandId, requestId).url;
    $.get(url).done(function (content) {
        row.child(content, 'active').show();
    }).fail(function (jqXHR, textStatus, errorThrown) {
        //show error
    });
}

/**
 * Filter requests by participants
 */
function filterByParticipants(oSettings, aData, iDataIndex) {
    var type = $('.filter > a.active').data('type');
    switch (type) {
        case 'group':
            return aData[4] != '1';
        default:
            return true;
    }
}

$(document).ready(function () {
    $.fn.dataTableExt.afnFiltering.push(filterByParticipants);

    var requests = $('#requests')
        .dataTable({
            "sDom": '<"toolbar">rtip',
            "iDisplayLength": 25,
            "asStripeClasses": [],
            "aaSorting": [],
            "bLengthChange": false,
            "order": [[5, "desc"]],
            "columnDefs": [{
                "targets": 7,
                "bSortable": false
            }]
        });
    $("div.toolbar").html($('#filter-container').html());
    $('#filter-container').empty();

    $('.filter > a').on('click', function (e) {
        e.preventDefault();
        $('.filter > a').removeClass('active');
        $(this).addClass('active');
        requests.fnDraw();
    });

    (new TableWithDetails(requests, 'requests', loadDetails));

});

