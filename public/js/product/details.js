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
 * Changes UI on product activation/deactivation
 *
 * @param active {boolean} if true, product is active
 */
function switchState(active) {
    if (active) {
        $('#activate').removeClass('btn-success').addClass('btn-warning');
        $('#activate').html('<i class="glyphicon-off glyphicon glyphicon-white"></i> Deactivate');
        $('#deactivatedStatus').hide();
    } else {
        $('#activate').addClass('btn-success').removeClass('btn-warning');
        $('#activate').html('<i class="glyphicon-off glyphicon glyphicon-white"></i> Activate');
        $('#deactivatedStatus').show();
    }
}

$(document).ready( function() {

    // Delete links.
    $('form.delete').submit(function() {
        return confirm('Delete this ' + $(this).attr('text') + '? You cannot undo this action.');
    });

    // Datatables
    $('.datatables').each(function() {
        $(this).dataTable( {
            "sPaginationType": "bootstrap",
            "sDom": "<'row'<'span4'l><'span4'f>r>t<'row'<'span4'i><'span4'p>>",
            "iDisplayLength": 100,
            "asStripeClasses":[],
            "aaSorting": [],
            "bFilter": false,
            "bInfo": false,
            "bLengthChange": false,
            "bPaginate": false
        });
    });
    $('[data-toggle="tooltip"]').tooltip();

    // Select functionality for product details page
    $("#contributor > select").change(function(){
        $("#contributor > select option:selected").each(function() {
            $("#contributor > input[name=isPerson]").attr("value", $(this).attr("isPerson"));
        })
    });
    if ($('#activate').hasClass('btn-warning')) {
        $('#deactivatedStatus').hide();
    }
    $('#activate').on('click', function(e) {
        e.preventDefault();
        var url = jsRoutes.controllers.hm.Products.activation($(this).data('id')).url;
        var active = $(this).hasClass('btn-success');
        $.post(url, {active: active}, function(data, textStatus, xhr) {
            switchState(active);
        });
    });

});

