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
function activateLoginForm() {
    var obj = $('#passwordReset');
    $(obj).on('click', function(e) {
        e.preventDefault();
        $('#password').removeAttr('disabled').attr('type', 'password').val('');
        $('[type=submit]').removeAttr('disabled');
        $(obj).text('Reset password');
        $(obj).off('click');
        activateResetForm();
    });
}

function activateResetForm() {
    var obj = $('#passwordReset');
    $(obj).on('click', function(e) {
        e.preventDefault();
        $.post(
            $(obj).attr('href'),
            { 'email': $('#email').val() },
            function(data) {
                $('#password').attr('disabled', 'disabled').attr('type', 'text').val('Check your inbox');
                $(obj).text('Enter');
                $('[type=submit]').attr('disabled', 'disabled');
                $(obj).off('click');
                activateLoginForm();
                success(data.message)
            },
            "json"
        );
        return true;
    });
}

function showHideRemindMeLink() {
    var $remindMe = $('.remind-me');
    var $email = $('#email');
    var $password = $('#password');

    function showHide() {
        if ($email.val().length > 0 && $password.val().length == 0) {
            $remindMe.show();
        } else {
            $remindMe.hide();
        }
    }

    $email.on('keyup', showHide );
    $password.on('keyup', showHide );
}

$(document).ready( function() {
    $('.remind-me').hide();
    showHideRemindMeLink();
    activateResetForm();
});