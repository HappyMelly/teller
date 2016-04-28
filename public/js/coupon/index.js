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
 * Filter brands by status
 */
function filterByStatus(oSettings, aData, iDataIndex) {
    var type = $('.filter > a.active').data('type');
    switch(type) {
        case 'active':
            return aData[4] == 'true';
        default:
            return true;
    }
}

$(document).ready( function() {
    $.fn.dataTableExt.afnFiltering.push(filterByStatus);

    var coupons = $('#coupons').dataTable({
        "sDom": '<"toolbar">rtip',
        "iDisplayLength": 25,
        "asStripeClasses":[],
        "aaSorting": [],
        "bLengthChange": false,
        "order": [[ 0, "asc" ]],
        "columnDefs": [{
            "targets": 2,
            "bSortable": false
        }, {
            "targets": 4,
            "visible": false
        }]
    });

    $("div.toolbar").html($('#filter-container').html());
    $('#filter-container').empty();

    $('.filter > a').on('click', function(e) {
        e.preventDefault();
        $('.filter > a').removeClass('active');
        $(this).addClass('active');
        coupons.fnDraw();
    });
    coupons.fnDraw();

});
