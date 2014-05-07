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

// /**
//  * Filter events checking if they are future or past
//  */
// function filterByDate(oSettings, aData, iDataIndex) {
//     var index = 4;
//     var filter = $('#past-future').find(':selected').val();
//     if (filter == 'all') {
//         return true;
//     }
//     var dates = aData[index].split('/')
//     if (dates.length != 2) {
//         return true;
//     }
//     var end = Date.parse(dates[1]);
//     var today = Date.today();
//     if (filter == 'past') {
//         return (today > end);
//     } else {
//         return (today <= end);
//     }
// }

/**
 * Filter evaluations checking if they are pending, approved or rejected
 */
function filterByStatus(oSettings, aData, iDataIndex) {
    var index = 0;
    var filter = $('#status').find(':selected').val();
    if (filter == 'all') {
        return true;
    }
    var value = $(aData[index]).attr('value');
    return value == filter;
}

// /**
//  * Filter events checking if they are archived or not
//  */
// function filterArchived(oSettings, aData, iDataIndex) {
//     var index = 9;
//     var filter = $('#archived').find(':selected').val();
//     if (filter == 'all') {
//         return true;
//     }
//     if (filter == 'archived') {
//         return aData[index] == 'true';
//     } else {
//         return aData[index] == 'false';
//     }
// }
// $.fn.dataTableExt.afnFiltering.push(filterByDate);
$.fn.dataTableExt.afnFiltering.push(filterByStatus);
// $.fn.dataTableExt.afnFiltering.push(filterArchived);

$.extend( $.fn.dataTableExt.oStdClasses, {
    "sWrapper": "dataTables_wrapper form-inline"
} );

function renderDropdown(data) {
    var html = '<div class="dropdown">';
    html += '<a class="dropdown-toggle" data-toggle="dropdown" href="#"><i class=" icon-tasks"></i></a>';
    html += '<ul class="dropdown-menu pull-right" aria-labelledby="dLabel">';
    if ('edit' in data && data.edit) {
        html += '<li><a tabindex="-1" href="' + data.edit;
        html += '" title="Edit Evaluation"><i class="icon-pencil"></i> Edit Evaluation</a></li>';
    }
    if ('view' in data && data.view) {
        html += '<li><a tabindex="-1" href="' + data.view;
        html += '" title="View Evaluation"><i class="icon-eye-open"></i> View Evaluation</a></li>';
    }
    html += '</ul></div>';
    return html;
}

$(document).ready( function() {
    var currentBrand = $('#brands').find(':selected').val();

    var participantTable = $('#participants').dataTable({
        "sPaginationType": "bootstrap",
        "sDom": '<"toolbar">rtip',
        "iDisplayLength": 25,
        "asStripeClasses":[],
        "aaSorting": [],
        "bLengthChange": false,
        "ajax": {
            "url" : "participants/brand/" + currentBrand,
            "dataSrc": ""
        },
        "columns": [
            { "data": "status" },
            { "data": "person" },
            { "data": "event" },
            { "data": "location" },
            { "data": "schedule" },
            { "data": "impression" },
            { "data": "creation" },
            { "data": "handled" },
            { "data": "certificate" },
            { "data": "actions" }
        ],
        "columnDefs": [{
                "render": function(data, type, row) {
                    var style = [
                        { badge: '', icon: 'icon-hand-right' },
                        { badge: 'badge-success', icon: 'icon-thumbs-up' },
                        { badge: 'badge-important', icon: 'icon-thumbs-down' }
                    ];
                    if (data) {
                        var html = '<span class="badge ' + style[data.value].badge + '"';
                        html += ' value="' + data.value + '" ';
                        html += 'title="Status: ' + data.label + '">';
                        html += '<i class="' + style[data.value].icon + '"></i></span>';
                        return html;
                    }
                    return '';
                },
                "targets": 0
            }, {
                "render": function(data, type, row) {
                    return '<a href="' + data.url + '">' + data.name + '</a>';
                },
                "targets": 1
            }, {
                "render": function(data, type, row) {
                    return '<a href="' + data.url + '">' + data.title + '</a>';
                },
                "targets": 2
            }, {
                "render": function(data, type, row) {
                    return '<img align="absmiddle" width="16" src="/assets/images/flags/16/' +
                        data.country + '.png"/> ' + data.city;
                },
                "targets": 3
            }, {
                "render": function(data, type, row) {
                    return data.start + ' / ' + data.end;
                },
                "targets": 4
            }, {
                "render": function(data, type, row) {
                    if (data) {
                        return '<strong>' + data + '</strong>';
                    }
                    return '';
                },
                "targets": 5
            }, {
                "render": function(data, type, row) {
                    return renderDropdown(data);
                },
                "targets": 9
            }
        ]
    });
    $("div.toolbar").html($('#filter-containter').html());
    $('#filter-containter').empty();
    $('#status').on('change', function() { participantTable.fnDraw(); } );

    $("#brands").change(function() {
        var brandCode = $(this).find(':selected').val();
        participantTable.ajax.url("participants/brand/" + brandCode).load();
    });
});
