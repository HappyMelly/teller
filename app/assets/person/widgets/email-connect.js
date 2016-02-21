/**
 *  Email connect
 */
(function ($, App) {
    'use strict';

    function PersonEmailConnect(selector, options){
        var self = this;

        self.$root = $(selector);
        self.options = $.extend({}, options, self.$root.data());
        self.locals = {
            $currentEmail: self.$root.find('[data-email-current]'),
            $createEmailForm: self.$root.find('[data-create-email]'),
            $changePassForm: self.$root.find('[data-change-pass]'),
            $changeEmailForm: self.$root.find('[data-change-email]')
        };

        self.assignEvents();
    }

    PersonEmailConnect.prototype.assignEvents = function(){
        var self = this;

        self.locals.$createEmailForm.on('submit', '', function (e) {
            var $this = $(this);
            e.preventDefault();

            if (!self.isFormValid($this)) return;
            self.createEmail($this);
        });

        self.locals.$changePassForm.on('submit', '', function (e) {
            var $this = $(this);
            e.preventDefault();

            if (!self.isFormValid($this)) return;
            self.changePassword($this);
        });

        self.locals.$changeEmailForm.on('submit', '', function (e) {
            var $this = $(this);
            e.preventDefault();

            if (!self.isFormValid($this)) return;
            self.changeEmail($this);
        });

        self.$root.on('input', '.modal input', function(e){
                var $this = $(this);
                self.removeError($this);
            })
    };

    PersonEmailConnect.prototype.isFormValid = function($form){
        var self = this,
            $inputs = $form.find('input'),
            error = 0;

        $inputs.each(function(index, el){
            var $el = $(el),
                value = $.trim($el.val());

            if ($el.hasClass('type-email') && !self.isValidEmail(value)){
                self.setError($el, 'Email is not valid');
                error += 1;
            }

            if ($el.hasClass('is-the-same') && !self.isSamePass($form, $el)){
                self.setError($el, 'Password should be the same');
                error += 1;
            }

            if (!value) {
                self.setError($el, 'Empty');
                error += 1;
            }
        });

        return !error;
    };

    PersonEmailConnect.prototype.createEmail = function($form){
        var self = this,
            $modal = $form.closest('.modal'),
            $newEmail = $modal.find('input[name="email"]').val();

        $.post('/send/createEmail', {
            email: $form.find('input[name="email"]').val(),
            password: $form.find('input[name="password"]').val()
        }, function(){
            $modal.modal('hide');
            self.$root.addClass('show_connected');
            self.locals.$currentEmail.text($newEmail);
        }).fail(function(){

        })
    };

    PersonEmailConnect.prototype.changePassword = function($form){
        var self = this,
            $modal = $form.closest('.modal');

        $.post('/send/createPassword', {
            oldpassword: $form.find('input[name="old-password"]').val(),
            password: $form.find('input[name="password"]').val()
        }, function(){
            $modal.modal('hide');
            self.clearForm($form);
        }).fail(function(){

        })
    };

    PersonEmailConnect.prototype.changeEmail = function($form){
        var self = this,
            $modal = $form.closest('.modal'),
            $newEmail = $modal.find('input[name="new-email"]').val();

        $.post('/send/changeEmail', {
            oldemail: $form.find('input[name="old-email"]').val(),
            email: $form.find('input[name="new-email"]').val(),
            password: $form.find('input[name="hmt-password"]').val()
        }, function(){
            $modal.modal('hide');
            self.locals.$currentEmail.text($newEmail);
            self.clearForm($form);
        }).fail(function(){

        })
    };

    PersonEmailConnect.prototype.isValidEmail = function(email) {
        var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        return re.test(email);
    };

    PersonEmailConnect.prototype.isSamePass = function($form, $el) {
        var $originalPass = $form.find('input[name="password"]');

        return $originalPass.val() == $el.val();
    };

    PersonEmailConnect.prototype.removeError = function($el){
        var $parent = $el.closest('.email-dlg__i');

        $parent.removeClass('state-error');
    };

    PersonEmailConnect.prototype.setError = function($el, text){
        var $parent = $el.closest('.email-dlg__i'),
            $error = $parent.find('.item-error');

        $parent.addClass('state-error');

        if (!$error.length){
            $error = $('<div />', {
                "class": "item-error"
            }).appendTo($parent);
        }

        $error.text(text);
    };

    PersonEmailConnect.prototype.clearForm = function($form){
        var $inputs = $form.find('input');

        $inputs.each(function(index, el){
            var $el = $(el);
            if (!$el.attr("disabled"))  $el.val('');
        })
    };

    App.widgets.PersonEmailConnect = PersonEmailConnect;

})(jQuery, App);