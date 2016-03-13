

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
            url: $("form").attr("action"),
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
        }).complete(function() {
            $("body").css("cursor", "default");
        });
    }
};

/**
 * Returns true if card details are valid
 * @returns {boolean}
 */
var validateDetails = function() {
    // var flag = true;
    // var validateNumber = $.payment.validateCardNumber($('.cc-number').val());
    // var validateExpiry = $.payment.validateCardExpiry($('.cc-exp-month').val(),
    //     $('.cc-exp-year').val());
    // var validateCVC = $.payment.validateCardCVC($('.cc-cvc').val());
    // if (validateNumber) {
    //     $('.cc-number').parent().removeClass('has-error');
    // } else {
    //     $('.cc-number').parent().addClass('has-error');
    //     flag = false;
    // }
    // if (validateExpiry) {
    //     $('.cc-exp-year').parent().removeClass('has-error');
    // } else {
    //     $('.cc-exp-year').parent().addClass('has-error');
    //     flag = false;
    // }
    // if (validateCVC) {
    //     $('.cc-cvc').parent().removeClass('has-error');
    // } else {
    //     $('.cc-cvc').parent().addClass('has-error');
    //     flag = false;
    // }
    // var name = $('.cc-name');
    // if (name.val().length < 1) {
    //     name.parent().addClass('has-error');
    //     flag = false;
    // } else {
    //     name.parent().removeClass('has-error');
    // }
    // return flag;
};




$(document).ready(function($) {
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

        return false;
    });
    // $('input.cc-name').bind('change paste keyup', function() {
    //     this.value = this.value.toUpperCase();
    // });
    // $('input.cc-number').payment('formatCardNumber');
    // $('input.cc-cvc').payment('formatCardCVC');
});

function PaymentForm(selector){
    this.$root = $(selector);
    this.locals = this._getDom();

    this._init();
    this._assignEvents();
}

PaymentForm.prototype._getFom = function(){
    var $root = this.$root;
    
    return {
        $inputNumber: $root.find('.cc-number'),
        $inputName: $root.find('.cc-number'),
        $inputMonth: $root.find('.cc-exp-month'),
        $inputYear: $root.find('.cc-exp-year'),
        $inputCVC: $root.find('.cc-cvc')
    }
};

PaymentForm.prototype._init = function(){
    if (!$.fn.payment){
        console.log('There is no payment plugin on this page');
        return;
    }

    this.locals.$inputNumber.payment('formatCardNumber');
    this.locals.$inputCVC.payment('formatCardCVC');

    this.loadStripeScript()
};

PaymentForm.prototype._assignEvents = function(){
    var self = this;
    
    self.$root  
        .on('keyup', 'input', function(){
            self._removeError($(this));
            $(this).parent().removeClass('has-error');
        })
        .on('change paste keyup', '[data-card-name]', function(e){
            var $this = $(this);
            $this.val($this.val().toUpperCase())
        })
};

PaymentForm.prototype.isValidForm = function(){
    var valid = true,
        locals = this.locals,
        isValidNumber = $.payment.validateCardNumber(locals.$inputNumber.val()),
        isValidExpiry = $.payment.validateCardExpiry(locals.$inputMonth.val(), locals.$inputYear.val()),
        isValidCVC = $.payment.validateCardCVC(locals.$inputCVC.val()),
        isValidName = $.trim(locals.$inputName.val().length);

    if (!isValidNumber){
        self._setError(locals.$inputNumber);
        valid = false;
    }

    if (!isValidExpiry){
        self._setError(locals.$inputMonth);
        self._setError(locals.$inputYear);
        valid = false;
    }

    if (!isValidCVC){
        self._setError(locals.$inputCVC);
        valid = false;
    }

    if (!isValidName){
        self._setError(locals.$inputName);
        valid = false;
    }

    return valid;
};

PaymentForm.prototype._setError = function($el){
    $el.parent().addClass('has-error');
};

PaymentForm.prototype._removeError = function($el){
    $el.parent().removeClass('has-error');
};