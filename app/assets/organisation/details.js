/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2016, Happy Melly http://www.happymelly.com
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
 * If you have questions concerning this license or the applicable additional
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */


function initializeActions() {
    $('#experimentList').on('click', 'button.remove', function(e) {
        var experimentId = $(this).data('id');
        $.ajax({
            type: "DELETE",
            url: $(this).data('href'),
            dataType: "json"
        }).done(function(data) {
            $('div[data-id="' + experimentId + '"]').remove();
        }).fail(function(jqXHR, status, error) {
            //empty
        });
        return false;
    });
    $('#experimentList').on('click', 'button.deletePicture', function(e) {
        var experimentId = $(this).data('id');
        var that = this;
        $.ajax({
            type: "DELETE",
            url: $(this).data('href'),
            dataType: "json"
        }).done(function(data) {
            $('div[data-id="' + experimentId + '"]').find('.picture').remove();
            $(that).remove();
        }).fail(function(jqXHR, status, error) {
            //empty
        });
        return false;
    });
}


$(document).ready( function() {

    // Delete links.
    $('form.delete').submit(function() {
        return confirm('Delete this ' + $(this).attr('text') + '? You cannot undo this action.');
    });

    $('.datatables').each(function() {
        $(this).dataTable( {
            "sPaginationType": "bootstrap",
            "sDom": "<'row'<'span4'l><'span4'f>r>t<'row'<'span4'i><'span4'p>>",
            "order": [[ 0, "asc" ]],
            "bFilter": false,
            "bInfo": false,
            "bLengthChange": false,
            "bPaginate": false
        });
    });
    $('.payments').dataTable( {
        "sPaginationType": "bootstrap",
        "order": [[ 2, "desc" ]],
        "columnDefs": [
            { "orderable": false, "targets": 0 },
            { "orderable": false, "targets": 1 }
        ],
        "bFilter": false,
        "bInfo": false,
        "bLengthChange": false,
        "bPaginate": false
    });

    $('[data-toggle="tooltip"]').tooltip();

    function getOrganozationId(){
        return $('#org').text();
    }

    new App.widgets.UploadPhotoWidget({
        selector: '.js-organization-photo',
        urlDelete: jsRoutes.controllers.core.Organisations.deleteLogo(getOrganozationId()).url
    })

    new App.widgets.Sidemenu('.js-organization-menu', {
        hashDefault: 'details',
        afterShowTab: initializeActions
    });
});

