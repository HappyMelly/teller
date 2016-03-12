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
 * Filter members by their type
 */
function filterByMembership(oSettings, aData, iDataIndex) {
    var type = $('.membership > .filter > a.active').data('type');
    var index = 3;
    if (type == 'all') {
        return true;
    } else {
        return aData[index] == type;
    }
}

/**
 * Filter members by their object type
 */
function filterByType(oSettings, aData, iDataIndex) {
    var type = $('.type > .filter > a.active').data('type');
    var index = 6;
    switch(type) {
        case 'org': return aData[index] == 'false';
        case 'person': return aData[index] == 'true';
        default: return true;
    }
}

$.fn.dataTableExt.afnFiltering.push(filterByMembership);
$.fn.dataTableExt.afnFiltering.push(filterByType);

jQuery.extend( jQuery.fn.dataTableExt.oSort, {
    "url-text-pre": function ( a ) {
        return String(a).replace( /<[\s\S]*?>/g, "" );
    },

    "url-text-asc": function ( a, b ) {
        return ((a < b) ? -1 : ((a > b) ? 1 : 0));
    },

    "url-text-desc": function ( a, b ) {
        return ((a < b) ? 1 : ((a > b) ? -1 : 0));
    }
} );

$(document).ready( function() {
    var members = $('#people').dataTable({
        "dom": '<"toolbar pull-left">frtip',
        "iDisplayLength": 25,
        "asStripeClasses":[],
        //"aaSorting": [],
        "bLengthChange": false,
        "order": [[ 1, "asc" ]],
        "columnDefs": [{
            bSortable: false,
            targets: 0
        }, {
            type: "url-text",
            targets: 1
        }, {
            visible: false,
            targets: 6
        }]
    });
    $('div.toolbar').html($('#filter-containter').html());
    $('#filter-containter').empty();

    $('.membership > .filter > a').on('click', function(e) {
        e.preventDefault();
        $('.membership > .filter > a').removeClass('active');
        $(this).addClass('active');
        members.fnDraw();
    });
    $('.type > .filter > a').on('click', function(e) {
        e.preventDefault();
        $('.type > .filter > a').removeClass('active');
        $(this).addClass('active');
        members.fnDraw();
    })
});
