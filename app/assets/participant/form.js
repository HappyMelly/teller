/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2015, Happy Melly http://www.happymelly.com
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

function showError(message) {
    $('#error').append(
        $('<div class="alert alert-danger">')
            .text(message)
            .append('<button type="button" class="close" data-dismiss="alert">&times;</button>')
    );
}

/**
 * Retrieve a list of event types for the brand
 * @param brandCode     String
 * @param currentEvent  String
 */
function getEvents(brandCode, currentEvent) {
    //TODO it should retrieve archived events only
    $.ajax({
        url: '/brand/' + brandCode + '/events',
        dataType: "json"
    }).done(function(data) {
            var selector = "[name=eventId]";
            var value = parseInt($(selector).attr('value'));
            $(selector)
                .empty()
                .append($("<option></option>").attr("value", 0).text("Choose an event"));
            for(var i = 0; i < data.length; i++) {
                var option = $("<option></option>")
                    .attr("value", data[i].id)
                    .text(data[i].title);
                if (value == data[i].id) {
                    option.attr('selected', 'selected');
                }
                $(selector).append(option);
            }
            if (currentEvent) {
                selector = 'option[value="' + currentEvent + '"]';
                $('[name=eventId]').find(selector).attr('selected', 'selected');
            }
        }).fail(function() {
            showError("Sorry we don't know anything about the brand you try to request");
        });
}

function showNewPersonForm() {
  $('.newPerson').show();
  $('.existingPerson').hide();
}

function showExistingPersonForm() {
  $('.existingPerson').show();
  $('.newPerson').hide();
}

$(document).ready(function() {
    $('#existingPerson').on('change', function() {
        showExistingPersonForm();
    });
    $('#newPerson').on('change', function() {
        showNewPersonForm();
    });
    if ($('#newPerson').prop("checked")) {
        showNewPersonForm();
    } else {
        showExistingPersonForm();
    }

    $("[name=brandId]").change(function() {
        var code = $(this).find(':selected').val();
        $("[name=brandId]").find('[value=' + code + ']').attr('selected', 'selected');
        getEvents(code, "");
    });
    $("[name=eventId]").change(function() {
        var id = $(this).find(':selected').val();
        $("[name=eventId]").find('[value=' + id + ']').attr('selected', 'selected');
    });

    var eventId = $('#currentEvent').attr('value');
    var code = $('#currentBrand').attr('value');
    if (code) {
        $("[name=brandId]").find('[value=' + code + ']').attr('selected', 'selected');
        getEvents(code, eventId);
    }
});
