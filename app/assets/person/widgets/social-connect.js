

/**
 *  Social connect
 */
(function ($, App) {
    'use strict';

    function PersonSocialConnect(selector, options){
        var self = this;

        self.$root = $(selector);
        self.options = $.extend({}, options, self.$root.data());

        self.assignEvents();
    }

    PersonSocialConnect.prototype.assignEvents = function(){
        var self = this;

        self.$root
            .on('click', '[data-social-connect]', function (e) {
                var $this = $(this),
                    $root = $this.closest('.b-connect-i');

               self.toggleConnect($root);
                e.preventDefault();
            })
    };

    PersonSocialConnect.prototype.setConnect = function($el){
        window.location = $el.data('url');
    };

    PersonSocialConnect.prototype.unSetConnect = function($el){
        var socialType = $el.data('social');
        var url = jsRoutes.controllers.UserAccounts.disconnect(socialType).url;

        $.post(url, {}, function(data) {
            $el.removeClass('state-complete');
            success(data.message);
        }, "json").fail(function(jqXHR, textStatus, errorThrown) {
            var response = JSON.parse(jqXHR.responseText);
            error(response.message);
        });
    };

    PersonSocialConnect.prototype.toggleConnect = function($el){
        if ($el.hasClass('state-complete')){
            this.unSetConnect($el);
        } else {
            this.setConnect($el);
        }
    };

    PersonSocialConnect.prototype.createWindow = function(url){
        var windowParams = "menubar=no,location=yes,resizable=yes,scrollbars=yes,status=no,width=500,height=300";

        return window.open(url, "HMT_authorization", windowParams);
    };

    App.widgets.PersonSocialConnect = PersonSocialConnect;

})(jQuery, App);