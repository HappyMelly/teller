

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
        var socialType = $el.data('social');

        $.post('/setSocialConntect', {
            social: socialType
        }, function(){
            $el.addClass('state-complete');
        })
    };

    PersonSocialConnect.prototype.unSetConnect = function($el){
        var socialType = $el.data('social');

        $.post('/unsetSocialConntect', {
            social: socialType
        }, function(){
            $el.removeClass('state-complete');
        })
    };

    PersonSocialConnect.prototype.toggleConnect = function($el){
        if ($el.hasClass('state-complete')){
            this.unSetConnect($el);
        } else {
            this.setConnect($el);
        }
    };

    App.widgets.PersonSocialConnect = PersonSocialConnect;

})(jQuery, App);