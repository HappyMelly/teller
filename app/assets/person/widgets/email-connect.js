
/**
 *  Dialog
 */
(function ($, App) {
    'use strict';

    function EmailConnectDlg(selector, options){
        var self = this;

        self.$root = $(selector);
        self.locals = {
            $form: self.$root.find('form'),
            $inputs: self.$root.find('form input')
        };
        self.options = $.extend({}, options, self.$root.data());
        self.validation = new App.widgets.FormValidation(self.locals.$inputs);

        self.assignEvents();
    }

    EmailConnectDlg.prototype.assignEvents = function(){
        var self = this;

        self.locals.$form.on('submit', function (e) {
            var $inputs = self.locals.$inputs;
            e.preventDefault();

            if (!self.validation.isValidInputs()) return;
            self.submitForm();
        });

        self.$root.on('hide.bs.modal', function(e){
            self.validation.clearForm();
            self.validation.removeErrors();
        })
    };

    EmailConnectDlg.prototype.submitForm = function(){
        var self = this,
            ajaxData = {};

        self.locals.$inputs.each(function(index, el){
            var $el = $(el),
                name = $el.attr('name');

            name && (ajaxData[name] = $el.val())
        });

        $.post(self.options.url, ajaxData, function () {
                self.$root.modal('hide');
                self.validation.clearForm();

                self.options.success && self.options.success();
            })
            .fail(function (response) {
                var data = $.parseJSON(response.responseText).data;

                if (!data.errors) return;
                self.validation.setErrors(data.errors);

                self.options.fail && self.options.fail();
            })
    };

    App.widgets.EmailConnectDlg = EmailConnectDlg;

})(jQuery, App);


