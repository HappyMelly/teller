

/**
 *  filter list
 */
(function ($, App) {
    'use strict';

    function FormCredit(selector, options){
        var self = this;

        self.$root = $(selector);
        self.options = $.extend({}, options, self.$root.data());

        self.locals = {
            $count: self.$root.find('[data-credict-count]'),
            $value: self.$root.find('[data-credit-value]'),
            $to: self.$root.find('[data-credit-to]'),
            $message: self.$root.find('[data-credit-message]'),
            $error: self.$root.find('[data-credit-error]')
        };

        self.availabelBonus = self.locals.$count.text();

        self.assignEvents();
    }

    FormCredit.prototype.assignEvents = function(){
        var self = this;

        self.$root
            .on('submit', function (e) {
                e.preventDefault();

                if (!self.isFormValid()) return false;
                self.sendRequest();
            })
            .on('input', '[data-credit-value], [data-credit-to]', function(e){
                self.removeErrors();
            })
    };

    FormCredit.prototype.isFormValid = function(){
        var self = this,
            error = null,
            errorText = '';

        if (!self.locals.$value.val()){
            error = error || true;
            errorText += 'Give value is empty. ';
            self.setError(self.locals.$value);
        }

        if (+self.locals.$value.val() > (+self.locals.$count.text())){
            error = error || true;
            errorText += 'You cann’t give more than ' + self.locals.$count.text() + ' credits. ';
            self.setError(self.locals.$value);
        }

        if (!self.locals.$to.val()){
            error = error || true;
            errorText += 'Email is empty. ';
            self.setError(self.locals.$to);
        }

        if (!self.isEmailValid(self.locals.$to.val())){
            error = error || true;
            errorText += 'Email format is incorrect. ';
            self.setError(self.locals.$to);
        }

        if (error){
            self.locals.$error.text(errorText);
            return false;
        }

        return true;
    };

    FormCredit.prototype.isEmailValid = function (email) {
        var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        return re.test(email);
    };

    FormCredit.prototype.setError = function ($input) {
        var $container = $input.closest('[data-credit-item]');

        if (!$container.hasClass('state_error')){
            $container.addClass('state_error')
        }
    };

    FormCredit.prototype.removeErrors = function(){
        this.$root.removeClass('peer-credit_show_error');
        this.locals.$error.text('');
        this.$root.find('[data-credit-item]').removeClass('state_error');
    };

    FormCredit.prototype.clearForm = function(){
        var locals = this.locals,
            currentBonus = +locals.$count.text() - locals.$value.val();

        locals.$count.text(currentBonus);
        locals.$value.val('');
        locals.$to.val('');
        locals.$message.val('');
    };

    FormCredit.prototype.sendRequest = function(){
        var self = this,
            locals = self.locals;

        $.post(
            self.$root.attr('action'),
            {
                value: locals.$value.val(),
                to: locals.$to.val(),
                message: locals.$message.val(),
            }, function(){
                self.$root.clearForm();
                self.$root.addClass('peer-credit_state_send');

                setTimeout(function(){
                    self.$root.removeClass('peer-credit_state_send');
                }, 3000)
            }
        );
    };

    App.widgets.FormCredit = FormCredit;

})(jQuery, App);