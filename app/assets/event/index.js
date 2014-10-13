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
 * Filter events by brand
 */
function filterByBrand(oSettings, aData, iDataIndex) {
    var index = 1;
    var filter = $('#brands').find(':selected').val();
    if (filter == 'all') {
        return true;
    }
    if (aData[index].indexOf(filter) < 0) {
        return false;
    } else {
        return true;
    }
}

/**
 * Filter events by facilitators
 */
function filterByFacilitator(oSettings, aData, iDataIndex) {
    var index = 2;
    var filter = $('#facilitators').find(':selected').val();
    if (filter == 'all') {
        return true;
    }
    if (aData[index].indexOf(filter) < 0) {
        return false;
    } else {
        return true;
    }
}

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
    var start = moment(dates[0]);
    var end = moment(dates[1]);
    var today = moment();
    if (filter == 'past') {
        return (today.isAfter(end, 'day'));
    } else {
        return (today.isBefore(start, 'day') || today.isSame(start, 'day'));
    }
}

/**
 * Filter events checking if they are public or private
 */
function filterByPublicity(oSettings, aData, iDataIndex) {
    var index = 8;
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
    var index = 9;
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
$.fn.dataTableExt.afnFiltering.push(filterByBrand);
$.fn.dataTableExt.afnFiltering.push(filterByFacilitator);
$.fn.dataTableExt.afnFiltering.push(filterByDate);
$.fn.dataTableExt.afnFiltering.push(filterByPublicity);
$.fn.dataTableExt.afnFiltering.push(filterArchived);

/**
 * Initialize a facilitator filter
 * @param events {DataTable}
 */
function initFacilitatorsFilter(events) {
    var facilitators = [];
    events.api().column(2).data().unique().each(function(d, j) {
        var regexp = /<a.*>(.*)<\/a>/img;
        while ( (result = regexp.exec(d)) ) {
            var value = result[1];
            if (facilitators.indexOf(value) < 0) {
                facilitators.push(value);
            }
        }
    });
    facilitators.sort();
    var name = $('#userName').val();
    for (var i = 0; i < facilitators.length; i++) {
        var option = $("<option></option>").attr("value", facilitators[i]).text(facilitators[i]);
        if (name == facilitators[i]) {
            option.attr('selected', 'selected');
        }
        $('#facilitators').append(option);
    }
}

/**
 * Initialize a brand filter
 * @param events {DataTable}
 */
function initBrandsFilter(events) {
    var brands = [];
    events.api().column(1).data().unique().each(function(d, j) {
        var regexp = /<a.*brand\/([a-zA-Z0-9_]{1,5}).*>(.*)<\/a>/img;
        while ( (result = regexp.exec(d)) ) {
            var code = result[1];
            var name = result[2];
            if (!(code in brands)) {
                brands[code] = name;
            }
        }
    });
    brands.sort();
    var personalLicense = $('#personalLicense').val();
    for (var key in brands) {
        var option = $("<option></option>").attr("value", brands[key]).text(brands[key]);
        if (personalLicense == key) {
            option.attr('selected', 'selected');
        }
        $('#brands').append(option);
    }
}

$(document).ready( function() {
    $.extend( $.fn.dataTableExt.oStdClasses, {
        "sWrapper": "dataTables_wrapper form-inline"
    } );
    var events = $('#events').dataTable( {
        "sDom": '<"toolbar">rtip',
        "iDisplayLength": 25,
        "asStripeClasses":[],
        "aaSorting": [],
        "bLengthChange": false,
        "order": [[ 4, "asc" ]]
    });
    initFacilitatorsFilter(events);
    initBrandsFilter(events);


    $("div.toolbar").html($('#filter-containter').html());
    $('#filter-containter').empty();
    $('#past-future').on('change', function() { events.fnDraw(); } );
    $('#private').on('change', function() { events.fnDraw(); } );
    $('#archived').on('change', function() { events.fnDraw(); } );
    $('#facilitators').on('change', function() { events.fnDraw(); } );
    $('#brands').on('change', function() { events.fnDraw(); } );

    events.fnDraw();
});
