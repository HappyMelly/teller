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

(function ($, markedPlugin) {
    'use strict';

    var PreviewMarkdown = function (el, options) {
        var defaults = {
           markdownposition: "fixed"
        };

        this.el = el;
        this.$el = $(el);

        this.options = $.extend({}, defaults, options);

        if (!this.isValidDevepdencies()) return;

        markedPlugin.setOptions({
            renderer: new markedPlugin.Renderer(),
            gfm: true,
            tables: true,
            breaks: false,
            pedantic: false,
            sanitize: true,
            smartLists: true,
            smartypants: false
        });

        this.assignEvents();
    };

    PreviewMarkdown.prototype.isValidDevepdencies = function(){
        var valid = true;

        if (!markedPlugin) {
            valid = false;
            console.log('There is no MarkDown plugin : https://github.com/chjj/marked');
        }

        return valid;
    };

    PreviewMarkdown.prototype.assignEvents = function (e) {
        var self = this;

        self.$el
            .on('focus', function (e) {
                var $this = $(this),
                    compiledContent = self.compileMarkdown($this.val());

                self.setContent(compiledContent);
                self.showPopover();
            })
            .on('keydown', function (e) {
                var $this = $(this),
                    compiledContent = self.compileMarkdown($this.val());

                self.setContent(compiledContent);
            })
            .on('blur', function (e) {
                var $this = $(this);

                self.hidePopover();
            });

        $(window)
            .resize(function () {
                self.$popover && self.$popover.css(self.getPosition());
            });
    };

    PreviewMarkdown.prototype.compileMarkdown = function(content){
        return markedPlugin(content);
    };

    PreviewMarkdown.prototype.setContent = function (content) {
        var self = this,
            $el = self.$el;

        if (self.$popover){
            self.$popover.html(content);
            return;
        }

        var $popover = $('<div />', {
            'class': 'popover-bl ' + self.options.markdownclass,
            html: content,
            css:  self.getPosition()
        });

        if (self.options.markdownposition == "fixed"){
            $popover.appendTo('body');
        } else {
            self.$el.after($popover)
        }

        self.$popover = $popover;
    };

    PreviewMarkdown.prototype.getPosition = function(){
        var positionFixed = {
            top: this.$el.offset().top,
            left: this.$el.offset().left + this.$el.outerWidth()
        };

        return this.options.markdownposition == "fixed"? positionFixed: {};
    };

    PreviewMarkdown.prototype.showPopover = function () {
        var self = this,
            $el = self.$el;

        if (self.isVisible) return;

        self.isVisible = true;
        self.$popover.addClass('popover-bl_show');
    };

    PreviewMarkdown.prototype.hidePopover = function () {
        var self = this;

        if (!self.isVisible) return;

        self.isVisible = false;
        self.$popover.removeClass('popover-bl_show');
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

})(jQuery, marked);