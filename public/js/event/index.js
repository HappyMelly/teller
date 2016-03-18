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
 * This function collects data from all filters and provides a requests based on their values
 * @returns {string}
 */
function makeRequestUrl() {
    var request = 'events/filtered?';
    var filter = $('#past-future').find(':selected').val();
    var counter = 0;
    if (filter != 'all') {
        request += 'future=' + ((filter == 'past') ? 'false' : 'true');
        counter += 1;
    }
    filter = $('#private').find(':selected').val();
    if (filter != 'all') {
        request += ((counter > 0) ? '&' : '') + 'public=' + ((filter == 'private') ? 'false' : 'true');
        counter += 1;
    }
    filter = $('#archived').find(':selected').val();
    if (filter != 'all') {
        request += ((counter > 0) ? '&' : '') + 'archived=' + ((filter == 'archived') ? 'true' : 'false');
        counter += 1;
    }
    filter = $('#activeBrandId').val();
    request += ((counter > 0) ? '&' : '') + 'brandId=' + filter;
    counter += 1;

    filter = $('#facilitators').find(':selected').val();
    if (filter == undefined) {
        request += ((counter > 0) ? '&' : '') + 'facilitator=' + $('#activeUserId').val();
    } else {
        if (filter != 'all') {
            request += ((counter > 0) ? '&' : '') + 'facilitator=' + filter;
        }
    }
    return request;
}


/**
 *   Writes the html for events details.
 *   @param row {object} DataTable row object
 */
function format(row) {
    var url = jsRoutes.controllers.Events.detailsButtons(row.data().event.id).url;
    $.get(url).done(function (content) {
        row.child(content, 'active').show();
        $('.js-event-list').trigger('hmt.eventList.show')
    });
}

$(document).ready( function() {

    $.extend( $.fn.dataTableExt.oStdClasses, {
        "sWrapper": "dataTables_wrapper",
    });

    var events = $('#events')
      .dataTable({
        "sDom": '<"toolbar">rtip',
        "iDisplayLength": 25,
        "asStripeClasses":[],
        "aaSorting": [],
        "bLengthChange": false,
        "ajax": {
            "url" : makeRequestUrl(),
            "dataSrc": "",
            "deferRender": true
        },
        "order": [[ 3, "asc" ]],
        "columns": [
            { "data": "event" },
            { "data": "facilitators" },
            { "data": "location" },
            { "data": "schedule" },
            { "data": "totalHours" },
            { "data": "materials" },
            { "data": "invoice" },
            { "data": "confirmed" },
            { "data": "actions",
              "className": 'details-control',
              "orderable": false
            },
            { "data": "schedule" }
        ],
        "columnDefs": [{
            "render": function(data) {
                return '<a href="' + data.url + '">' + data.title + '</a>';
            },
            "targets": 0
        },{
             "render": function(data) {
                 var html = '';
                 for (var i = 0; i < data.length; i++) {
                     html += '<div><a href="' + data[i].url + '">' + data[i].name + '</a><br/></div>';
                 }
                 return html;
             },
             "targets": 1
        },{
            "render": function(data) {
                return data.city + ", " + data.countryName;
            },
            "targets": 2
        },{
            "render": function(data) {
                return data.formatted;
            },
            "iDataSort": 9,
            "targets": 3
        },{
            "render": function(data) {
              if(data.free)
                return '<span class="glyphicon glyphicon-ok"/> Free';
              return (data.invoice === "Yes") ? '<span class="glyphicon glyphicon-ok"/> Yes' : '<span class="glyphicon"/> No';
            },
            "targets": 6
        },{
            "render": function(data) {
              return data ? '<span class="glyphicon glyphicon-ok"/> Yes' : '<span class="glyphicon" aria-hidden="true"/> No';
            },
            "targets": 7
        },{
            "render": function(data) {
                var html = '<div class="circle-show-more" data-event="' + data.event_id + '"';
                html += ' data-person="' + data.person + '">';
                html += '<span class="glyphicon glyphicon-chevron-down"></span></div>';
                return html;
            },
            "targets": 8,
            "bSortable": false
        }, {
            "render": function(data) {
                return data.start;
            },
            "targets": 9,
            "visible": false
        }]
    });

    events
      .on('xhr.dt', function(){
          $("body").css("cursor", "default");
      });

    (new TableWithDetails(events, 'events', format));

    $("body").css("cursor", "progress");
    $("div.toolbar").html($('#filter-containter').html());
    $('#filter-containter').empty();

    function updateTable() {
        $("body").css("cursor", "progress");
        events
            .api()
            .ajax
            .url(makeRequestUrl())
            .load(function(){
                $("body").css("cursor", "default");
            });
    }

    // filtering events for "Events"
    $('#past-future').on('change', function() { updateTable();});
    $('#private').on('change', function() { updateTable(); });
    $('#archived').on('change', function() {  updateTable(); });
    $('#facilitators').on('change', function() { updateTable(); });

    events.fnDraw();

    $("#events").on('click', '.delete', function(){
        $("#cancelLink").attr('href', $(this).data('href'));
    });
});
