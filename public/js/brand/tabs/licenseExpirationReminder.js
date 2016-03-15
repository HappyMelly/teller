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
function isEmailEmpty() {
    return $('#reminderContent').val().trim().length == 0;
}

function activateForm() {
    $('#reminderButton').removeAttr('disabled');
    $('.notice').hide();
}

function deactivateForm() {
    $('#reminderButton').attr('disabled', 'disabled');
    $('.notice').show();
}

function turnReminderOff(url) {
    $.post(url, {}, function(data) {
        success(data.message)
    }, "json").fail(function(jqXHR, status, errorCode) {
        if (status == "error") {
            var response = JSON.parse(jqXHR.responseText);
            error(response.message);
        } else {
            var msg = "Internal error. Please try again or contact the support team.";
            error(msg);
        }
    });
}

function turnReminderOn(url) {
    $.post(url, {content: $('#reminderContent').val()}, function(data) {
        success(data.message)
    }, "json").fail(function(jqXHR, status, errorCode) {
        if (status == "error") {
            var response = JSON.parse(jqXHR.responseText);
            error(response.message);
        } else {
            var msg = "Internal error. Please try again or contact the support team.";
            error(msg);
        }
    });
}

function updateForm(obj) {
    if (obj.value == 'off') {
        turnReminderOff($(obj).data('href'));
        $('.active-reminder').hide();
    } else {
        if (!isEmailEmpty()) {
            turnReminderOn($(obj).data('href'));
        }
        $('.active-reminder').show();
    }
}

function initializeForm(value) {
    if (value == 'off') {
        $('.active-reminder').hide();
    } else {
        $('.active-reminder').show();
    }
    if (isEmailEmpty()) {
        deactivateForm();
    } else {
        activateForm();
    }
}

$(document).ready(function() {
    $('input[type=radio][name=switcher]').change(function() {
        updateForm(this);
    });
    initializeForm($('input[type=radio][name=switcher]:checked').val());
    $('#reminderContent').on('keyup', function(e) {
        if (isEmailEmpty()) {
            deactivateForm();
        } else {
            activateForm();
        }
    });
    $('#reminderButton').on('click', function(e) {
        turnReminderOn($(this).data('href'));
    });
});