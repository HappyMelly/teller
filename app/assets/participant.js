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


$.extend( $.fn.dataTableExt.oStdClasses, {
    "sWrapper": "dataTables_wrapper form-inline"
} );

/**
 * Render a dropdown with actions for a participant/event/evaluation
 *
 * @param data {object}
 * @param brand {string}
 * @returns {string}
 */
function renderDropdown(data, brand) {
    var emptyDropdown = true;
    var html = '<div class="dropdown">';
    html += '<a class="dropdown-toggle" data-toggle="dropdown" href="#"><i class="glyphicon glyphicon-tasks"></i></a>';
    html += '<ul class="dropdown-menu pull-right" aria-labelledby="dLabel">';
    if ('certificate' in data && data.certificate) {
        if ('generate' in data.certificate && data.certificate.generate) {
            emptyDropdown = false;
            html += '<li><a tabindex="-1" href="' + data.certificate.generate;
            html += '" title="Generate Certificate"><i class="glyphicon glyphicon-file"></i> Generate Certificate</a></li>';
        }
    }
    if ('evaluation' in data && data.evaluation) {
        var evaluation = data.evaluation;
        if (!emptyDropdown) {
            html += '<li class="divider"></li>';
        }
        if ('add' in evaluation && evaluation.add) {
            emptyDropdown = false;
            html += '<li><a tabindex="-1" href="' + evaluation.add;
            html += '" title="Add Evaluation"><i class="glyphicon glyphicon-plus"></i> Add Evaluation</a></li>';
        }
        if ('approve' in evaluation && evaluation.approve) {
            emptyDropdown = false;
            html += '<li><a class="approve" tabindex="-1" href="#approve" data-href="' + evaluation.approve;
            html += '" data-toggle="modal" title="Approve Evaluation"><i class="glyphicon glyphicon-thumbs-up"></i> Approve Evaluation</a></li>';
        }
        if ('reject' in evaluation && evaluation.reject) {
            emptyDropdown = false;
            html += '<li><a class="reject" tabindex="-1" href="#reject" data-href="' + evaluation.reject;
            html += '" data-toggle="modal" title="Reject Evaluation"><i class="glyphicon glyphicon-thumbs-down"></i> Reject Evaluation</a></li>';
        }
        if ('move' in evaluation && evaluation.move) {
            emptyDropdown = false;
            html += '<li><a class="move" tabindex="-1" href="#move" data-href="' + evaluation.move;
            html += '" data-brand="' + brand + '" data-toggle="modal" title="Move Evaluation">';
            html += '<i class="glyphicon glyphicon-random"></i> Move Evaluation</a></li>';
        }
        if ('view' in evaluation && evaluation.view) {
            emptyDropdown = false;
            html += '<li><a tabindex="-1" href="' + evaluation.view;
            html += '" title="View Evaluation"><i class="glyphicon glyphicon-eye-open"></i> View Evaluation</a></li>';
        }
        if ('edit' in evaluation && evaluation.edit) {
            emptyDropdown = false;
            html += '<li><a tabindex="-1" href="' + evaluation.edit;
            html += '" title="Edit Evaluation"><i class="glyphicon glyphicon-pencil"></i> Edit Evaluation</a></li>';
        }
        if ('remove' in evaluation && evaluation.remove) {
            emptyDropdown = false;
            html += '<li><a tabindex="-1" href="' + evaluation.remove;
            html += '" title="Delete Evaluation" onclick="';
            html += "return confirm('Delete this evaluation? You cannot undo this action.')\">";
            html += '<i class="glyphicon glyphicon-trash"></i> Delete Evaluation</a></li>';
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
            html += '" title="View Person"><i class="glyphicon glyphicon-eye-open"></i> View Person</a></li>';
        }
        if ('edit' in participant && participant.edit) {
            emptyDropdown = false;
            html += '<li><a tabindex="-1" href="' + participant.edit;
            html += '" title="Edit Person"><i class="glyphicon glyphicon-pencil"></i> Edit Person</a></li>';
        }
        if ('remove' in participant && participant.remove) {
            emptyDropdown = false;
            html += '<li><a tabindex="-1" href="' + participant.remove;
            html += '" title="Delete Person">';
            html += '<i class="glyphicon glyphicon-trash"></i> Delete Person</a></li>';
        }
        if ('removeParticipation' in participant && participant.removeParticipation) {
            emptyDropdown = false;
            html += '<li class="divider"></li>';
            html += '<li><a tabindex="-1" href="' + participant.removeParticipation;
            html += '" title="Remove Participation" onclick="';
            html += "return confirm('Remove this participation? The evaluation (if exists) will also be deleted. You cannot undo this action.')\">";
            html += '<i class="glyphicon glyphicon-trash"></i> Remove Participation</a></li>';
        }
    }
    html += '</ul></div>';
    if (emptyDropdown) {
        return '';
    }
    return html;
}

/**
 * This function creates a new export link when a user clicks 'Export to XLSX'.
 *  It collects data from all table filters
 */
function buildExportLink(detailsPage) {
    var brandCode = '';
    var eventId = 0;
    var status = -1;
    var facilitatedByMe = false;
    if (detailsPage) {
        brandCode = $('#brandCode').val();
        eventId = $('#eventId').val();
    } else {
        brandCode = $('#brands').find(':selected').val();
        eventId = $('#events').find(':selected').val();
        if (!eventId) {
            eventId = 0;
        }
        status = $('#status').find(':selected').val();
        if (status == 'all') {
            status = -1;
        }
        facilitatedByMe = $('#facilitatedByMe').is(':checked');
    }
    var suffix = brandCode + '/event/' + eventId + '/status/' + status + '/byMe/' + facilitatedByMe;
    $("#exportLink").attr("href", "/evaluations/export/" + suffix);
}

/**
 * Calculate and draw an average impression for a set of evaluations
 */
function calculateAverageImpression(table) {
    var rows = table._('tr', {"filter":"applied"});
    var impression = 0;
    var counter = 0;
    for (var i = 0; i < rows.length; i++) {
        if (rows[i].evaluation.impression) {
            counter++;
            impression += rows[i].evaluation.impression.value;
        }
    }
    if (counter) {
        impression = Math.round((impression/counter) * 100)/100;
    }
    var badge = '';
    if (impression < 5) {
        badge = '<span class="badge alert-danger">' + impression + '</span>';
    } else if (impression < 8) {
        badge = '<span class="badge alert-warning">' + impression + '</span>';
    } else {
        badge = '<span class="badge alert-success">' + impression + '</span>';
    }
    $("#impression").html("General impr " + badge);
}

function drawStatus(data) {
    var style = [
        { badge: '', icon: 'glyphicon-hand-right' },
        { badge: 'alert-success', icon: 'glyphicon-thumbs-up' },
        { badge: 'alert-warning', icon: 'glyphicon-thumbs-down' }
    ];
    if (data) {
        var html = '<span class="badge ' + style[data.value].badge + '"';
        html += ' value="' + data.value + '" ';
        html += 'title="Status: ' + data.label + '">';
        html += '<i class="glyphicon-white glyphicon ' + style[data.value].icon + '"></i></span>';
        return html;
    }
    return '';
}

function drawCertificate(data) {
    if (data && data.url) {
        return '<a href="' + data.url + '" target="_blank">' + data.id + '</a>';
    }
    return '';
}

function drawImpression(data) {
    if (data) {
        return '<strong>' + data.caption + '</strong>';
    }
    return '';
}


$(document).ready( function() {
    $("#participants").on('click', '.approve', function(){
        $("#approveLink").attr('href', $(this).data('href'));
    });
    $("#participants").on('click', '.reject', function(){
        $("#rejectLink").attr('href', $(this).data('href'));
    });
    $("#participants").on('click', '.move', function(){
        var href = $(this).data('href');
        getPastEvents($(this).data('brand'));
        $("#moveButton").on('click', function(e) {
            e.preventDefault();
            $.post(href, { eventId: $("#eventIdMoveForm").find(':selected').val() }, function() {
                $('#move').modal('hide');
                $('#participants').DataTable().ajax.reload();
            });
        });
    });
});