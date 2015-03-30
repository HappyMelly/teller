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
 * Show notification message
 * @param message {string}
 * @param type {string} 'success/danger'
 */
function notify(message, type) {
    $('#notification').html("");
    $('#notification').append(
        $('<div class="alert alert-' + type + '">')
            .text(message)
            .append('<button type="button" class="close" data-dismiss="alert">&times;</button>')
    );
}

$(document).ready( function() {

    // Delete links.
    $('.delete').click(function() {
        return confirm('Delete this ' + $(this).attr('text') + '? You cannot undo this action.');
    });

    // Datatables
    $.extend( $.fn.dataTableExt.oStdClasses, {
        "sWrapper": "dataTables_wrapper form-inline"
    } );
    $('.datatables').each(function() {
        $(this).dataTable( {
            "sPaginationType": "bootstrap",
            "sDom": "<'row'<'span4'l><'span4'f>r>t<'row'<'span4'i><'span4'p>>",
            "order": [[ 0, "asc" ]],
            "iDisplayLength": 100,
            "asStripeClasses":[],
            "aaSorting": [],
            "bFilter": false,
            "bInfo": false,
            "bLengthChange": false,
            "bPaginate": false
        });
    });

    $('#activities a').click(function (e) {
      e.preventDefault();
      $(this).tab('show');
    });

    var hash = window.location.hash.substring(1);
    if (!hash) {
        hash = 'products';
    }
    $('#activities a[href="#' + hash + '"]').tab('show');

    $('#eventTypes').editableTableWidget();
    $('#eventTypes td').on('validate', function(evt, newValue) {
        if (newValue == "") {
            return false; // mark cell as invalid
        }
    }).on('change', function(evt, newValue) {
        $('#notification').html("");
        var data = [];
        var id = $(this).parent().data('id');
        data[0] = { name: 'brandId', value: $(this).parent().data('brandid') };
        var i = 1;
        $(this).parent().children('td').each(function() {
            data[i] = { name: $(this).data('name'), value: $(this).text() };
            i += 1;
        });
        var that = this;
        $("body").css("cursor", "progress");
        $.ajax({
            type: "POST",
            url: jsRoutes.controllers.EventTypes.update(id).url,
            data: data
        }).done(function(data) {
            notify("You successfully updated the event type", 'success');
            $(that).text(newValue);
        }).fail(function(jqXHR, status, error) {
            if (status == "error") {
                var error = JSON.parse(jqXHR.responseText);
                notify(error.message, 'danger');
            } else {
                var msg = "Unexpected error. Please contact the support team.";
                notify(msg, 'danger');
            }
        }).complete(function() {
            $("body").css("cursor", "default");
        });
        return false;
    });
});

