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

function showParticipantDetails(object) {
    var row = $(object).parents('tr').first();
    var url = jsRoutes.controllers.Participants.details($(object).data('event'), $(object).data('person')).url;
    $.get(url, {}, function(data) {
        var tableContainer = $("<tr class='participant-details active'>").append(
            $("<td colspan='10'>").append(data));
        $(row).addClass('active').after(tableContainer);
        $(object).addClass('active');
        initializeParticipantActionsInDetails();
    });
}

function hideParticipantDetails(object) {
    $(object).parents('tr').first().removeClass('active');
    $(object).removeClass('active');
    $('.participant-details').remove();
}

function toggleParticipantDetails(object) {
    if ($(object).hasClass('active')) {
        hideParticipantDetails(object);
    } else {
        showParticipantDetails(object);
    }
}

/**
 * Sends a request to the server to generate certificate and notifies user
 *  about it
 * @param object {object} Target Link
 */
function generateCertificate(object) {
    var eventId = $(object).data('event');
    var personId = $(object).data('person');
    var url = jsRoutes.controllers.Certificates.create(eventId, personId).url;
    $.get(url, {}, function(data) {
        var certificate = JSON.parse(data).certificate;
        var url = jsRoutes.controllers.Certificates.certificate(certificate).url;
        $(object).removeClass('generate-certificate').off('click').
            attr('href', url).attr('target', '_blank').text(certificate);
        var caption = 'Certificate was generated and sent to the participant';
        noty({text: caption, layout: 'bottom',
            theme: 'relax', timeout: 2000 , type: 'success'});
    });
}

/**
 * Removes participant's rows from the table
 */
function removeParticipant() {
    $('#participants').find('tr.active').remove();
}

function initializeParticipantActions() {
    $('.circle-show-more').on('click', function() {
        toggleParticipantDetails($(this));
    });
    $('.generate-certificate').on('click', function(e) {
        e.preventDefault();
        generateCertificate($(this));
        return true;
    });
}

function initializeParticipantActionsInDetails() {
    $('.remove-participation').on('click', function(e) {
        e.preventDefault();
        var object = $(this);
        var result = confirm("Remove this participant? You cannot undo this action.");
        if (result == true) {
            var eventId = $(this).data('event');
            var personId = $(this).data('person');
            var url = jsRoutes.controllers.Participants.delete(eventId, personId).url;
            $.get(url, {}, function() {
                removeParticipant();
                noty({text: 'Participant was successfully removed', layout: 'bottom',
                    theme: 'relax', timeout: 2000 , type: 'success'});
            });
        }
        return false;
    });
}

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
    html = '<div class="circle-show-more"><span class="glyphicon glyphicon-chevron-down"></span></div>';
    return html;
}

/**
 * This function creates a new export link when a user clicks 'Export to XLSX'.
 *  It collects data from all table filters
 */
function buildExportLink(detailsPage) {
    var brandId = '';
    var eventId = 0;
    var status = -1;
    if (detailsPage) {
        brandId = $('#brandId').val();
        eventId = $('#eventId').val();
    } else {
        brandId = $('#brands').find(':selected').val();
        if (brandId == undefined) {
            brandId = $('#activeBrandId').val();
        }
        eventId = $('#events').find(':selected').val();
        if (!eventId) {
            eventId = 0;
        }
        status = $('#status').find(':selected').val();
        if (status == 'all') {
            status = -1;
        }
    }
    var suffix = brandId + '/event/' + eventId + '/status/' + status;
    $("#exportLink").attr("href", "/report/create/" + suffix);
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
            impression += rows[i].evaluation.impression;
        }
    }
    if (counter) {
        impression = Math.round((impression/counter) * 100)/100;
    }
    var badge = '<span class="yellow-rating" title="Rating"><i class="fa fa-star"></i> ' + impression + '</span>';
    $("#impression").html("Impression " + badge);
}

function drawStatus(data) {
    var style = ['fa-thumbs-tack', 'fa-thumbs-up', 'fa-thumbs-down', 'fa-hourglass'];
    if (data.status) {
        var html = '<i class="fa fa-fw ' + style[data.status.value] + '"';
        html += ' value="' + data.status.value + '"></i> ';
        if (data.status.value == 0 || data.status.value == 3) {
            html += data.status.label;
        } else {
            html += data.handled;
        }
        return html;
    }
    return '';
}

function drawCertificate(data) {
    if (data.certificate.generate) {
        if (data.certificate.number == null) {
            var html = '<a class="generate-certificate" href="#"';
            html += 'data-event="' + data.event +'"';
            html += ' data-person="' + data.person + '">Generate</a>';
            return html;
        } else {
            var url = jsRoutes.controllers.Certificates.certificate(data.certificate.number).url;
            return '<a href="' + url + '" target="_blank">' + data.certificate.number + '</a>';
        }
    } else {
        return '';
    }
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