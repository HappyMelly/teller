/*
 * Happy Melly Teller
 * Copyright (C) 2013, Happy Melly http://www.happymelly.com
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
 * Filter events checking if they are future or past
 */
function filterByDate(oSettings, aData, iDataIndex) {
    var index = 4;
    var filter = $('#past-future').find(':selected').val();
    if (filter == 'all') {
        return true;
    }
    var dates = aData[index].split('/')
    if (dates.length != 2) {
        return true;
    }
    var end = Date.parse(dates[1]);
    var today = Date.today();
    if (filter == 'past') {
        return (today > end);
    } else {
        return (today <= end);
    }
}

/**
 * Filter events checking if they are public or private
 */
function filterByPublicity(oSettings, aData, iDataIndex) {
    var index = 7;
    var filter = $('#private').find(':selected').val();
    if (filter == 'all') {
        return true;
    }
    if (filter == 'private') {
        return aData[index] == 'true';
    } else {
        return aData[index] == 'false';
    }
}

/**
 * Filter events checking if they are archived or not
 */
function filterArchived(oSettings, aData, iDataIndex) {
    var index = 8;
    var filter = $('#archived').find(':selected').val();
    if (filter == 'all') {
        return true;
    }
    if (filter == 'archived') {
        return aData[index] == 'true';
    } else {
        return aData[index] == 'false';
    }
}
$.fn.dataTableExt.afnFiltering.push(filterByDate);
$.fn.dataTableExt.afnFiltering.push(filterByPublicity);
$.fn.dataTableExt.afnFiltering.push(filterArchived);

$(document).ready( function() {
    $.extend( $.fn.dataTableExt.oStdClasses, {
        "sWrapper": "dataTables_wrapper form-inline"
    } );
    var oTable = $('#events').dataTable( {
        "sPaginationType": "bootstrap",
        "sDom": '<"toolbar">rtip',
        "iDisplayLength": 25,
        "asStripeClasses":[],
        "aaSorting": [],
        "bLengthChange": false
    });
    $("div.toolbar").html($('#filter-containter').html());
    $('#filter-containter').empty();
    $('#past-future').on('change', function() { oTable.fnDraw(); } );
    $('#private').on('change', function() { oTable.fnDraw(); } );
    $('#archived').on('change', function() { oTable.fnDraw(); } );
});
