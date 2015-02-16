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

/**
 * If requests was successful, submits data to a server
 */
var stripeResponseHandler = function(status, response) {
    var $form = $('#payment-form');
    if (response.error) {
        // Show the errors on the form
        $form.find('.payment-errors').text(response.error.message);
        $form.find('button').prop('disabled', false);
    } else {
        // token contains id, last4, and card type
        var token = response.id;
        // Insert the token into the form so it gets submitted to the server
        $form.append($('<input type="hidden" name="token" />').val(token));
        // and re-submit
        $form.get(0).submit();
    }
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
    var field = $('#fee_amount');
    if (field.val().length < 1 || isNaN(field.val())) {
        field.parent().addClass('has-error');
        return false;
    } else {
        field.parent().removeClass('has-error');
        return true;
    }
};

jQuery(function($) {
    $('#payment-form').submit(function(e) {
        var $form = $(this);
        var details = validateDetails();
        var amount = validateAmount();
        if (details && amount) {
            // Disable the submit button to prevent repeated clicks
            $form.find('button').prop('disabled', true);
            Stripe.card.createToken($form, stripeResponseHandler);
        }
        // Prevent the form from submitting with the default action
        return false;
    });
    $('input.cc-number').payment('formatCardNumber');
    $('input.cc-cvc').payment('formatCardCVC');
});
