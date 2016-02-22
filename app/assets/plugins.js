/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2014, Happy Melly http://www.happymelly.com
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

// Scrolling to element on the page with id = "id-name"
//
// <a href="#" class="selector" data-target="#id-name"></a>
//
//  $('.selector').scrollToEl();
// ======================

(function ($) {
    'use strict';

    var ScrollToEl   = function (el) {
        this.el = el;
        this.$el = $(el);
        this.assignEvents();
    };

    ScrollToEl.prototype.assignEvents = function (e) {
        var self = this;

        self.$el.on('click', function (e) {
            var $this = $(this),
                target = $this.data('target');

            if (!self.isTargetValid(target)){
                return false;
            }

            self.scrollToTarget('#' + target);
            e.preventDefault();
        })
    };

    ScrollToEl.prototype.isTargetValid = function(target){
        var valid = true;

        if (!target) {
            console.log('There is no data-target attribute with id-name for this link');
            valid = false;
        }

        if (!$('#' + target).length) {
            console.log('There is no element with such id name');
            valid = false;
        }

        return valid;
    };

    ScrollToEl.prototype.scrollToTarget = function (target) {
        var $target = $(target);

        if (!$target.length) return false;

        $('html, body').animate({
            scrollTop: $target.offset().top
        }, 400);
    };

    function Plugin(option) {
        return this.each(function () {
            var $this = $(this),
                data  = $this.data('hmt.ScrollToEl');

            if (!data) $this.data('hmt.ScrollToEl', (data = new ScrollToEl(this)))
            if (typeof option == 'string') data[option].call($this)
        })
    }

    $.fn.scrollToEl = Plugin

})(jQuery);




// Preview markdown plugin
//
// <textarea class="selector"></textarea>
//
//  $('.selector').previewMarkdown();
// ======================

(function ($) {
    'use strict';

    var PreviewMarkdown = function (el, options) {
        var defaultOptions = {
            url: '/get/compileContent',
            interval: 3000,
            markdownposition: "body",
            template: "<div class='popover-bl'><i class='fa fa-spinner fa-spin'></i><div class='popover-bl__content' data-content></div></div>"
        };

        this.$el = $(el);
        this.options = $.extend({}, defaultOptions, options);

        this.resetState();
        this.createPopover();
        this.assignEvents();
    };

    PreviewMarkdown.prototype.resetState = function(){
        this.sending = null;
        this.sending = null;
        this.isNeedUpdating = null;
        this.waitingTimer = null
    };

    PreviewMarkdown.prototype.createPopover = function(){
        if (this.$popover) return;

        this.$popover = $(this.options.template)
            .css(this.getPosition());

        if (this.options.markdownclass){
            this.$popover.addClass(this.options.markdownclass);
        }

        if (this.options.markdownposition == "body"){
            this.$popover.appendTo('body');
        } else {
            this.$popover.insertAfter(this.$el);
        }
    };

    PreviewMarkdown.prototype.getPosition = function(){
        var $el = this.$el,
            offsetBody = {
                top: $el.offset().top,
                left: $el.offset().left + $el.outerWidth()
            };

        return this.options.markdownposition == "body"? offsetBody: {};
    };

    PreviewMarkdown.prototype.assignEvents = function(){
        var self = this;

        self.$el
            .on('focus', function(){
                self.toggle();
                self.compileContent();
            })
            .on('keyup', function(e){
                if (!self.isKeyTrigger(e.which)) return true;

                self.pausing && (self.isNeedUpdating = true);
                self.compileContent();
            })
            .on('blur', function(){
                self.resetState();
                self.toggle();
            });

        $(window).resize(function () {
            self.$popover && self.$popover.css(self.getPosition());
        });
    };

    PreviewMarkdown.prototype.compileContent = function(content){
        var self = this;
        content = content || self.$el.val();

        if (self.sending || self.pausing) return;

        self.pausing = true;
        self.setSending();
        self.setWaiting();

        $.post(self.options.url, {
            data: content
        }, function(data){
            self.setSended();
            self.$popover.find('[data-content]').html(data);
        });
    };

    PreviewMarkdown.prototype.setSending = function(){
        this.sending = true;
        this.$popover.addClass('popover-bl_loading');
    };

    PreviewMarkdown.prototype.setSended = function(){
        this.sending = false;
        this.$popover.removeClass('popover-bl_loading');
    };

    PreviewMarkdown.prototype.setWaiting = function(){
        var self = this;

        self.waitingTimer = setTimeout(
            function () {
                self.pausing = false;
                if (self.isNeedUpdating) {
                    self.isNeedUpdating = false;
                    self.compileContent();
                }
            }, self.options.interval);
    };

    PreviewMarkdown.prototype.show = function(){
        if (this.isVisible) return;

        this.isVisible = true;
        this.$popover.addClass('popover-bl_show');
    };

    PreviewMarkdown.prototype.hide = function(){
        if (!this.isVisible) return;

        this.isVisible = false;
        this.$popover.removeClass('popover-bl_show');
    };

    PreviewMarkdown.prototype.toggle = function(){
        this[this.isVisible? 'hide': 'show']();
    };

    PreviewMarkdown.prototype.isKeyTrigger = function(code){
        return (code >= 45 && code <= 90) || (code >= 186) && (code <= 222) || (code == 13) || (code == 27) || (code == 32) || (code == 8);
    };

    function Plugin(option) {
        return this.each(function () {
            var $this = $(this),
                data  = $this.data('hmt.PreviewMarkdown'),
                options = $.extend({}, option, $this.data());

            if (!data) $this.data('hmt.PreviewMarkdown', (data = new PreviewMarkdown(this, options)))
            if (typeof option == 'string') data[option].call($this)
        })
    }

    $.fn.previewMarkdown = Plugin

})(jQuery);