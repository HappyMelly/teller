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
 * Show success message
 * @param message {string}
 */
function showSuccess(message) {
    $('.alert-block').append(
        $('<div class="alert alert-success">')
            .text(message)
            .append('<button type="button" class="close" data-dismiss="alert">&times;</button>')
    );
}

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

$(document).ready( function() {

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
           }
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
            "targets": 3
        },{
            "render": function(data) {
              if(data.free)
                return '<span class="glyphicon glyphicon-ok"/> Free';
              return (data.invoice === "Yes") ? '<span class="glyphicon glyphicon-ok"/> Yes' : '<span class="glyphicon glyphicon-minus"/> No';
            },
            "targets": 6
        },{
            "render": function(data) {
              return data ? '<span class="glyphicon glyphicon-ok"/> Yes' : '<span class="glyphicon glyphicon-minus" aria-hidden="true"/> No';
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
        }]
    });

    events
      .on('xhr.dt', function(){
          $("body").css("cursor", "default");
      });


      $('#events tbody').on('click', 'td.details-control', function(){
          var tr = $(this).closest('tr');
          var row = events.api().row(tr);

          if(row.child.isShown()){
            row.child.hide();
            tr.children('.details-control').children('.circle-show-more').children('span').removeClass('glyphicon-chevron-up');
            tr.children('.details-control').children('.circle-show-more').children('span').addClass('glyphicon-chevron-down');
            tr.children('.details-control').children('.circle-show-more').removeClass('active');
            tr.removeClass('shown active');
          } else {
            // Open this row
            var details = row.child;
            details('').show();
            format(details, row.data());
            tr.children('.details-control').children('.circle-show-more').children('span').removeClass('glyphicon-chevron-down');
            tr.children('.details-control').children('.circle-show-more').children('span').addClass('glyphicon-chevron-up');
            tr.children('.details-control').children('.circle-show-more').addClass('active');
            tr.addClass('shown active');
          }
      });

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

    $('#cancelLink').on('click', function(e) {
        e.preventDefault();
        $.ajax({
            type: "POST",
            url: $(this).attr("href"),
            data: $("#cancelForm").serialize()
        }).done(function(data){
            updateTable();
            $("#cancelDialog").modal('hide');
            showSuccess("You successfully cancelled the event")
        });
    });
});
