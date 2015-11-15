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
 * Draw a table containing all participants of the event
 */
function drawRequestEvaluationTable(table) {
    var rows = table._('tr', {});
    var body = $('#participantList').find('tbody');
    body.empty();
    var tr = null;
    for (var i = 0; i < rows.length; i++) {
        var column = i % 2;
        if (column == 0) {
            tr = $('<tr>');
            body.append(tr);
        }
        var input = $('<input type="checkbox" class="participant">')
            .attr('name', 'participantIds[' + i + ']')
            .attr('value', rows[i].person.id);
        var label = $('<label>')
            .text(rows[i].person.name + "  ")
            .append(input);
        var div = $('<div class="checkbox">').append(label);
        div.append(rows[i].evaluation.status);
        var td = $('<td>').append(div);
        tr.append(td);
    }
    if (rows.length % 2 > 0) {
        tr.append($('<td>'));
    }
}

/**
 * Disable/enable 'Send' button by checking if the participants are selected
 *  and the letter's body contains an url
 */
function toggleSentButton() {
    var noParticipants = true;
    $('.participant').each(function() {
        if (this.checked) {
            noParticipants = false;
        }
    });
    var wrongBody = true;
    var body = $('textarea[name=body]').val();
    if (/https?:/i.test(body)) {
        wrongBody = false;
    }
    if (noParticipants || wrongBody) {
        $('#requestButton').attr('disabled', 'disabled');
    } else {
        $('#requestButton').removeAttr('disabled');
    }
}

/**
 * Loads organizer's name
 *
 * @param id Organizer id
 */
function updateOrganizer(id) {
    if (id != 0) {
        var url = jsRoutes.controllers.Organisations.name(id).url
        $.get(url, function(data) {
            $('#organizer').text(data.name);
        }, "json");
    } else {
        $('#organizer').text("no organizer");
    }
}

/**
 * Functions is called from 'cancelEvent' function
 */
function afterEventCancellation() {
    window.location.replace(jsRoutes.controllers.Events.index($('#brandId').val()).url);
}

$(document).ready( function() {

    $('#details a').click(function (e) {
      e.preventDefault();
      $(this).tab('show');
    });
    var hash = window.location.hash.substring(1);
    if (!hash) {
        hash = 'description';
    }
    $('#details a[href="#' + hash + '"]').tab('show');

    // Datatables
    $.extend( $.fn.dataTableExt.oStdClasses, {
        "sWrapper": "dataTables_wrapper form-inline"
    } );
    var participantTable = $('#participants').dataTable({
        "sDom": '<"toolbar">rtip',
        "iDisplayLength": 25,
        "asStripeClasses":[],
        "aaSorting": [],
        "bLengthChange": false,
        "ajax": {
            "url" : "/participants/event/" + $("#eventId").val(),
            "dataSrc": ""
        },
        "order": [[ 1, "asc" ]],
        "columns": [
            { "data": "person" },
            { "data": "evaluation.impression" },
            { "data": "evaluation.creation" },
            { "data": "evaluation" },
            { "data": "participant" },
            { "data": "participant" }
        ],
        "columnDefs": [{
                "render": function(data) {
                    return '<a href="' + data.url + '">' + data.name + '</a>';
                },
                "targets": 0
            }, {
                "className": "evaluation-field",
                "targets": [1, 2]
            },{
                "render": function(data) { return drawStatus(data); },
                "targets": 3,
                "className": "status evaluation-field"
            }, {
                "render": function(data) { return drawCertificate(data); },
                "targets": 4,
                "className": "certificate evaluation-field"
            }, {
                "render": function(data) {
                    var html = '<div class="circle-show-more" data-event="' + data.event + '"';
                    html += ' data-person="' + data.person + '">';
                    html += '<span class="glyphicon glyphicon-chevron-down"></span></div>';
                    return html;
                },
                "targets": 5,
                "bSortable": false
            }
        ]
    });
    participantTable.api().on('init.dt', function () {
        initializeParticipantActions("table");
    });

    $("div.toolbar").html($('#filter-containter').html());
    $('#filter-containter').empty();
    $('#participants').on('draw.dt', function() {
        calculateAverageImpression(participantTable);
        drawRequestEvaluationTable(participantTable);
        initializeParticipantActions("table");
    });
    $('#exportLink').on('click', function() {
        buildExportLink(true)
    });
    $('#participantList').on('change', '.participant', toggleSentButton);
    $('textarea[name=body]').on('input propertychange', toggleSentButton);
    $('[data-toggle="tooltip"]').tooltip();

    updateOrganizer($('#organizer').data('id'));
    $('[data-toggle="popover"]').popover();
});

