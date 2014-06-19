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
$.fn.dataTableExt.afnFiltering.push(filterByStatus);
$.fn.dataTableExt.afnFiltering.push(filterByEvent);

$.extend( $.fn.dataTableExt.oStdClasses, {
    "sWrapper": "dataTables_wrapper form-inline"
} );

function renderDropdown(data) {
    var emptyDropdown = true;
    var html = '<div class="dropdown">';
    html += '<a class="dropdown-toggle" data-toggle="dropdown" href="#"><i class=" icon-tasks"></i></a>';
    html += '<ul class="dropdown-menu pull-right" aria-labelledby="dLabel">';
    if ('certificate' in data && data.certificate) {
        if ('generate' in data.certificate && data.certificate.generate) {
            emptyDropdown = false;
            html += '<li><a tabindex="-1" href="' + data.certificate.generate;
            html += '" title="Generate Certificate"><i class="icon-file"></i> Generate Certificate</a></li>';
        }
    }
    if ('evaluation' in data && data.evaluation) {
        var evaluation = data.evaluation;
        if (!emptyDropdown) {
            html += '<li class="divider"></li>';
        }
        if ('approve' in evaluation && evaluation.approve) {
            emptyDropdown = false;
            html += '<li><a class="approve" tabindex="-1" href="#approve" data-href="' + evaluation.approve;
            html += '" data-toggle="modal" title="Approve Evaluation"><i class="icon-thumbs-up"></i> Approve Evaluation</a></li>';
        }
        if ('reject' in evaluation && evaluation.reject) {
            emptyDropdown = false;
            html += '<li><a class="reject" tabindex="-1" href="#reject" data-href="' + evaluation.reject;
            html += '" data-toggle="modal" title="Reject Evaluation"><i class="icon-thumbs-down"></i> Reject Evaluation</a></li>';
        }
        if ('view' in evaluation && evaluation.view) {
            emptyDropdown = false;
            html += '<li><a tabindex="-1" href="' + evaluation.view;
            html += '" title="View Evaluation"><i class="icon-eye-open"></i> View Evaluation</a></li>';
        }
        if ('edit' in evaluation && evaluation.edit) {
            emptyDropdown = false;
            html += '<li><a tabindex="-1" href="' + evaluation.edit;
            html += '" title="Edit Evaluation"><i class="icon-pencil"></i> Edit Evaluation</a></li>';
        }
        if ('remove' in evaluation && evaluation.remove) {
            emptyDropdown = false;
            html += '<li><a tabindex="-1" href="' + evaluation.remove;
            html += '" title="Delete Evaluation" onclick="';
            html += "return confirm('Delete this evaluation? You cannot undo this action.')\">";
            html += '<i class="icon-trash"></i> Delete Evaluation</a></li>';
        }
    }
    if ('participant' in data && data.participant) {
        var participant = data.participant;
        if (!emptyDropdown) {
            html += '<li class="divider"></li>';
        }
        if ('view' in participant && participant.view) {
            emptyDropdown = false;
            html += '<li><a tabindex="-1" href="' + participant.view;
            html += '" title="View Participant"><i class="icon-eye-open"></i> View Person</a></li>';
        }
        if ('edit' in participant && participant.edit) {
            emptyDropdown = false;
            html += '<li><a tabindex="-1" href="' + participant.edit;
            html += '" title="Edit Participant"><i class="icon-pencil"></i> Edit Person</a></li>';
        }
        if ('remove' in participant && participant.remove) {
            emptyDropdown = false;
            html += '<li><a tabindex="-1" href="' + participant.remove;
            html += '" title="Delete Person">';
            html += '<i class="icon-trash"></i> Delete Person</a></li>';
        }
        if ('removeParticipation' in participant && participant.removeParticipation) {
            emptyDropdown = false;
            html += '<li class="divider"></li>';
            html += '<li><a tabindex="-1" href="' + participant.removeParticipation;
            html += '" title="Remove Participation" onclick="';
            html += "return confirm('Remove this participation? The evaluation (if exists) will also be deleted. You cannot undo this action.')\">";
            html += '<i class="icon-trash"></i> Remove Participation</a></li>';
        }
    }
    html += '</ul></div>';
    if (emptyDropdown) {
        return '';
    }
    return html;
}

function loadEventList(events) {
    $('#events').empty().append($("<option></option>").attr("value", "").text("Select an event"));
    for(var i = 0; i < events.length; i++) {
        var event = events[i];
        $('#events').append( $('<option value="'+ event.id +'">' + event.longTitle +'</option>') );
    }
}

$(document).ready( function() {
    var currentBrand = $('#brands').val();
    var brandInSession = $('#participants').attr('brandCode');
    if (brandInSession) {
        currentBrand = brandInSession;
        $("#brands option[value='" + currentBrand + "']").attr('selected', 'selected');
    }
    var events = [];
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
        "order": [[ 6, "desc" ]],
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
            { "data": "event" },
            { "data": "actions" }
        ],
        "columnDefs": [{
                "render": function(data, type, row) {
                    var style = [
                        { badge: '', icon: 'icon-hand-right' },
                        { badge: 'badge-success', icon: 'icon-thumbs-up' },
                        { badge: 'badge-warning', icon: 'icon-thumbs-down' }
                    ];
                    if (data) {
                        var html = '<span class="badge ' + style[data.value].badge + '"';
                        html += ' value="' + data.value + '" ';
                        html += 'title="Status: ' + data.label + '">';
                        html += '<i class="icon-white ' + style[data.value].icon + '"></i></span>';
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
                    var result = $.grep(events, function(e){ return e.url == data.url; });
                    if (result.length == 0) {
                        events.push(data);
                    }
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
                    if (data && data.url) {
                        return '<a href="' + data.url + '" target="_blank">' + data.id + '</a>';
                    }
                    return '';
                },
                "targets": 8
            }, {
                "render": function(data, type, row) {
                    return data.id;
                },
                "visible": false,
                "targets": 9
            },{
                "render": function(data, type, row) {
                    return renderDropdown(data);
                },
                "targets": 10,
                "bSortable": false
            }
        ]
    });
    participantTable
        .api()
        .on('init.dt', function (e, settings, data) {
            loadEventList(events);
        });

    $("div.toolbar").html($('#filter-containter').html());
    $('#filter-containter').empty();
    $('#status').on('change', function() { participantTable.fnDraw(); } );
    $("#events").on('change', function() { participantTable.fnDraw(); } );

    $("#brands").change(function() {
        var brandCode = $(this).find(':selected').val();
        events = [];
        participantTable
            .api()
            .ajax
            .url("participants/brand/" + brandCode)
            .load(function(){
                loadEventList(events);
            });
    });
    $("#participants").on('click', '.approve', function(){
        $("#approveLink").attr('href', $(this).data('href'));
    });
    $("#participants").on('click', '.reject', function(){
        $("#rejectLink").attr('href', $(this).data('href'));
    });
});
