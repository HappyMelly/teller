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
 * If you have questions concerning this license or the applicable additional
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */

function showError(message) {
    var obj = $('.alert');
    obj.show();
    obj.text("");
    obj.text(message);
}

/**
 * If requests was successful, submits data to a server
 */
var stripeResponseHandler = function(status, response) {
    var $form = $('#payment-form');
    if (response.error) {
        // Show the errors on the form
        if (response.error.param == "exp_year") {
            $('.cc-exp-year').parent().addClass('has-error');
        } else {
            $('.cc-exp-year').parent().removeClass('has-error');
        }
        $form.find('button').prop('disabled', false);
    } else {
        // token contains id, last4, and card type
        var token = response.id;
        // Insert the token into the form so it gets submitted to the server
        $('input[name="token"]').remove();
        $form.append($('<input type="hidden" name="token" />').val(token));
        $.ajax({
            type: "POST",
            url: "/membership/payment",
            data: $form.serialize()
        }).done(function(data) {
            if (data.hasOwnProperty("redirect")) {
                window.location = data.redirect;
            } else {
                var msg = "Internal error #2001. Your card has been charged. ";
                msg += "Do not make payment again. Please proceed to your profile directly.";
                showError(msg);
            }
        }).fail(function(jqXHR, status, error) {
            if (status == "error") {
                var error = JSON.parse(jqXHR.responseText);
                showError(error.message);
            } else {
                var msg = "Internal error #2000. Your card has not been charged. Please try again.";
                showError(msg);
            }
            $form.find('button').prop('disabled', false);
        });
    }
    $("body").css("cursor", "default");
};

/**
 * Returns true if card details are valid
 * @returns {boolean}
 */
var validateDetails = function() {
    var flag = true;
    var validateNumber = $.payment.validateCardNumber($('.cc-number').val());
    var validateExpiry = $.payment.validateCardExpiry($('.cc-exp-month').val(),
        $('.cc-exp-year').val());
    var validateCVC = $.payment.validateCardCVC($('.cc-cvc').val());
    if (validateNumber) {
        $('.cc-number').parent().removeClass('has-error');
    } else {
        $('.cc-number').parent().addClass('has-error');
        flag = false;
    }
    if (validateExpiry) {
        $('.cc-exp-year').parent().removeClass('has-error');
    } else {
        $('.cc-exp-year').parent().addClass('has-error');
        flag = false;
    }
    if (validateCVC) {
        $('.cc-cvc').parent().removeClass('has-error');
    } else {
        $('.cc-cvc').parent().addClass('has-error');
        flag = false;
    }
    var name = $('.cc-name');
    if (name.val().length < 1) {
        name.parent().addClass('has-error');
        flag = false;
    } else {
        name.parent().removeClass('has-error');
    }
    return flag;
};

/**
 * Returns true if entered amount is valid
 * @returns {boolean}
 */
var validateAmount = function() {
    var field = $('#fee');
    if (field.val().length < 1 || isNaN(field.val())) {
        field.parent().addClass('has-error');
        return false;
    } else {
        field.parent().removeClass('has-error');
        return true;
    }
};

/**
 * Updates charged amount field
 * @param objectId Fee field
 */
var updateAmount = function(objectId) {
    var amount = $(objectId).val();
    if (amount.length < 1) {
        amount = 0.00;
    } else {
        amount = parseInt(amount);
    }
    var taxPercent = parseFloat($('#fee').data('tax'));
    var tax = (amount * taxPercent) / 100;
    var amountWithTax = amount + tax;
    $('div.amount > span').text(amountWithTax);
    $('#amount > span').text(tax);
};

jQuery(function($) {
    $('.alert').hide();
    $('#payment-form').submit(function(e) {
        var $form = $(this);
        var details = validateDetails();
        var amount = validateAmount();
        $('.alert').hide();
        if (details && amount) {
            // Disable the submit button to prevent repeated clicks
            $form.find('button').prop('disabled', true);
            $("body").css("cursor", "progress");
            Stripe.card.createToken($form, stripeResponseHandler);
        }
        // Prevent the form from submitting with the default action
        return false;
    });
    $('#fee').bind('change paste keyup', function() {
        updateAmount('#fee');
    });
    $('input.cc-name').bind('change paste keyup', function() {
        this.value = this.value.toUpperCase();
    });
    $('input.cc-number').payment('formatCardNumber');
    $('input.cc-cvc').payment('formatCardCVC');
    updateAmount('#fee');
});
