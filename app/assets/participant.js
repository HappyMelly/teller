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
        $(object).children('span').removeClass('glyphicon-chevron-down').
            addClass('glyphicon-chevron-up');
        initializeParticipantActionsInDetails();
    });
}

function hideParticipantDetails(object) {
    $(object).parents('tr').first().removeClass('active');
    $(object).removeClass('active');
    $(object).children('span').removeClass('glyphicon-chevron-up').
        addClass('glyphicon-chevron-down');
    $('.participant-details').remove();
}

function toggleParticipantDetails(object) {
    if ($(object).hasClass('active')) {
        hideParticipantDetails(object);
    } else {
        showParticipantDetails(object);
    }
}

function getActiveRow() {
    return $('tr[role="row"].active');
}

/**
 * Sends reject request for the given evaluation and updates UI
 * @param object {object} Reject button
 */
function rejectEvaluation(object) {
    var evaluationId = $(object).data('id');
    var url = jsRoutes.controllers.Evaluations.reject(evaluationId).url;
    $.post(url, {}, function(data) {
        var date = JSON.parse(data).date;
        $(object).attr('disabled', 'disabled').text('Rejected');
        $(object).parent('.buttons-block').children('.approve').first().
            data('id', evaluationId).removeAttr('disabled').text('Approve');
        var statusCell = getActiveRow().find('td.status');
        var icon = statusCell.find('i').removeClass('fa-thumb-tack').
            removeClass('fa-thumbs-up').addClass('fa-thumbs-down').
            attr('value', 2);
        statusCell.html(icon).append(' ' + date);
        getActiveRow().find('td.certificate').html("");
        var caption = "Evaluation is rejected and a notification is sent to the participant";
        noty({text: caption, layout: 'bottom',
            theme: 'relax', timeout: 2000 , type: 'success'});
    });
}

/**
 * Sends approval request for the given evaluation and updates UI
 * @param object {object} Approve button
 */
function approveEvaluation(object) {
    var evaluationId = $(object).data('id');
    var url = jsRoutes.controllers.Evaluations.approve(evaluationId).url;
    $.post(url, {}, function(data) {
        var date = JSON.parse(data).date;
        $(object).attr('disabled', 'disabled').text('Approved');
        $(object).parent('.buttons-block').children('.reject').first().
            data('id', evaluationId).removeAttr('disabled').text('Reject');
        var statusCell = getActiveRow().find('td.status');
        var icon = statusCell.find('i').removeClass('fa-thumb-tack').
            removeClass('fa-thumbs-down').addClass('fa-thumbs-up').
            attr('value', 1);
        statusCell.html(icon).append(' ' + date);
        var caption = "Evaluation is approved and certificate is sent to the participant";
        noty({text: caption, layout: 'bottom',
            theme: 'relax', timeout: 2000 , type: 'success'});
    });
}

/**
 * Delete the given evaluation
 * @param object {object} Delete button
 */
function deleteEvaluation(object) {
    var evaluationId = $(object).data('id');
    var result = confirm("Remove this evaluation? You cannot undo this action.");
    if (result == true) {
        $.ajax({
            type: "POST",
            url: jsRoutes.controllers.Evaluations.delete(evaluationId).url,
            data: {}
        }).done(function(data) {
            $('.evaluation-actions').remove();
            getActiveRow().find('.evaluation-field').html('');
            var caption = "Evaluation was successfully deleted";
            noty({text: caption, layout: 'bottom',
                theme: 'relax', timeout: 2000 , type: 'success'});
        });
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
    $('.approve').on('click', function(e) {
        e.preventDefault();
        approveEvaluation($(this));
        return false;
    });
    $('.reject').on('click', function(e) {
        e.preventDefault();
        rejectEvaluation($(this));
        return false;
    });
    $('.delete-evaluation').on('click', function(e) {
        e.preventDefault();
        deleteEvaluation($(this));
    });
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
    var style = ['fa-thumb-tack', 'fa-thumbs-up', 'fa-thumbs-down', 'fa-hourglass'];
    if (data.status) {
        var html = '<i class="text-muted fa fa-fw ' + style[data.status.value] + '"';
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
    if (data.certificate.show) {
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

$(document).ready( function() {
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