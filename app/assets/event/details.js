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
        var label = $('<label>')
            .text(rows[i].person.name + "  ")
            .append($('<input type="checkbox">'));
        var div = $('<div class="checkbox">').append(label);
        div.append(drawStatus(rows[i].evaluation.status));
        var td = $('<td>').append(div);
        tr.append(td);
    }
    if (rows.length % 2 > 0) {
        tr.append($('<td>'));
    }
}

$(document).ready( function() {

    // Delete links.
    $('form.delete').submit(function() {
        return confirm('Delete this ' + $(this).attr('text') + '? You cannot undo this action.');
    });

    $('#details a').click(function (e) {
      e.preventDefault();
      $(this).tab('show');
    });

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
            { "data": "evaluation.status" },
            { "data": "person" },
            { "data": "evaluation.impression" },
            { "data": "evaluation.creation" },
            { "data": "evaluation.handled" },
            { "data": "evaluation.certificate" },
            { "data": "actions" }
        ],
        "columnDefs": [{
                "render": function(data, type, row) { return drawStatus(data); },
                "targets": 0
            }, {
                "render": function(data, type, row) {
                    return '<a href="' + data.url + '">' + data.name + '</a>';
                },
                "targets": 1
            }, {
                "render": function(data, type, row) { return drawImpression(data); },
                "targets": 2
            }, {
                "render": function(data, type, row) { return drawCertificate(data); },
                "targets": 5
            }, {
               "render": function(data, type, row) { return renderDropdown(data); },
               "targets": 6,
               "bSortable": false
            }
        ]
    });

    $("div.toolbar").html($('#filter-containter').html());
    $('#filter-containter').empty();
    $('#participants').on('draw.dt', function() {
        calculateAverageImpression(participantTable);
        drawRequestEvaluationTable(participantTable);
    });
    $('#exportLink').on('click', function() {
        buildExportLink(true)
    });
});

