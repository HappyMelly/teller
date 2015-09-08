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
function filterByType(oSettings, aData, iDataIndex) {
    var index = 2;
    var filter = $('#type').find(':selected').val();
    if (filter == 'all') {
        return true;
    }
    return aData[index] == filter;
}

/**
 * Filter members by their object type
 */
function filterByObjectType(oSettings, aData, iDataIndex) {
    var index = 0;
    var filter = $('#objectType').find(':selected').val();
    if (filter == 'all') {
        return true;
    }
    return aData[index].indexOf(filter) != -1;
}
$.fn.dataTableExt.afnFiltering.push(filterByType);
$.fn.dataTableExt.afnFiltering.push(filterByObjectType);

jQuery.extend( jQuery.fn.dataTableExt.oSort, {
    "url-text-pre": function ( a ) {
        var x = String(a).replace( /<[\s\S]*?>/g, "" );
        return x;
    },

    "url-text-asc": function ( a, b ) {
        return ((a < b) ? -1 : ((a > b) ? 1 : 0));
    },

    "url-text-desc": function ( a, b ) {
        return ((a < b) ? 1 : ((a > b) ? -1 : 0));
    }
} );

$(document).ready( function() {
    $.extend( $.fn.dataTableExt.oStdClasses, {
        "sWrapper": "dataTables_wrapper form-inline"
    } );
    var memberTable = $('#people').dataTable({
        "dom": '<"toolbar pull-left">frtip',
        "iDisplayLength": 25,
        "asStripeClasses":[],
        //"aaSorting": [],
        "bLengthChange": false,
        "order": [[ 1, "asc" ]],
        "columnDefs": [
            { "type": "url-text", targets: 1 }
        ]
    });
    $('div.toolbar').html($('#filter-containter').html());
    $('#filter-containter').empty();
    $('#type').on('change', function() {
        memberTable.fnDraw();
    });
    $('#objectType').on('change', function() {
        memberTable.fnDraw();
    });
});
