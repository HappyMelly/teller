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
 * Filter evaluations by an event
 */
function filterByEvent(oSettings, aData, iDataIndex) {
    var index = 9;
    var filter = $('#events').find(':selected').val();
    if (filter == '') {
        return true;
    }
    return aData[index] == filter;
}

/**
 * Filter evaluations checking if they are pending, approved or rejected
 */
function filterByStatus(oSettings, aData, iDataIndex) {
    var index = 6;
    var filter = $('#status').find(':selected').val();
    if (filter == 'all') {
        return true;
    }
    var value = $(aData[index]).attr('value');
    return value == filter;
}

$.fn.dataTableExt.afnFiltering.push(filterByStatus);
$.fn.dataTableExt.afnFiltering.push(filterByEvent);

function loadEventList(events) {
    $('#events').empty().append($("<option></option>").attr("value", "").text("Select an event"));
    for(var i = 0; i < events.length; i++) {
        var event = events[i];
        $('#events').append( $('<option value="'+ event.id +'">' + event.longTitle +'</option>') );
    }
}


$(document).ready( function() {
    var currentBrand = $('#activeBrandId').val();
    var events = [];
    var participantTable = $('#participants').dataTable({
        "sDom": '<"toolbar">rtip',
        "iDisplayLength": 25,
        "asStripeClasses":[],
        "aaSorting": [],
        "bLengthChange": false,
        "ajax": {
            "url" : "participants/brand/" + currentBrand,
            "dataSrc": ""
        },
        "order": [[ 6, "desc" ]],
        "columns": [
            { "data": "person" },
            { "data": "event" },
            { "data": "location" },
            { "data": "schedule" },
            { "data": "evaluation.impression" },
            { "data": "evaluation.creation" },
            { "data": "evaluation" },
            { "data": "evaluation.certificate" },
            { "data": "event" },
            { "data": "participant" }
        ],
        "columnDefs": [{
                "render": function(data) {
                    return '<a href="' + data.url + '">' + data.name + '</a>';
                },
                "targets": 0
            }, {
                "render": function(data) {
                    var result = $.grep(events, function(e){ return e.url == data.url; });
                    if (result.length == 0) {
                        events.push(data);
                    }
                    return '<a href="' + data.url + '">' + data.title + '</a>';
                },
                "targets": 1,
                "orderable": false
            }, {
                "targets": 2,
                "orderable": false
            }, {
                "render": function(data) { return drawStatus(data); },
                "targets": 6,
                "orderable": false
            }, {
                "render": function(data) { return drawCertificate(data); },
                "targets": 7,
                "orderable": false
            }, {
                "render": function(data) { return data.id; },
                "visible": false,
                "targets": 8
            }, {
               "render": function(data) {
                   var html = '<div class="circle-show-more" data-event="' + data.event + '"';
                   html += ' data-person="' + data.person + '">';
                   html += '<span class="glyphicon glyphicon-chevron-down"></span></div>';
                   return html;
               },
               "targets": 9,
               "bSortable": false
            }
        ]
    });
    participantTable
        .api()
        .on('init.dt', function (e, settings, data) {
            loadEventList(events);
            $('.circle-show-more').on('click', function() {
                toggleParticipantDetails($(this));
            });

        });

    $("div.toolbar").html($('#filter-containter').html());
    $('#filter-containter').empty();
    $('#status').on('change', function() {
        participantTable.fnDraw();
    });
    $("#events").on('change', function() {
        participantTable.fnDraw();
    });

    $('#participants').on('draw.dt', function() {
        calculateAverageImpression(participantTable);
    });
    $('#exportLink').on('click', function() {
        buildExportLink(false)
    });

});
