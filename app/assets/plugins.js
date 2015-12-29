

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

    var PreviewMarkdown = function (el) {
        this.el = el;
        this.$el = $(el);
        this.customClass = this.$el.attr('markdownpreview');

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

        $(window).resize(function(){
            var $popover = self.$popover;

            $popover && $popover.css({
                left: self.$el.offset().left + self.$el.outerWidth()
            })
        })
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
            'class': 'popover-bl',
            html: content,
            css: {
                top: $el.offset().top,
                left: $el.offset().left + $el.outerWidth()
            }
        });

        $popover
            .appendTo('body')
            .addClass(self.customClass);

        self.$popover = $popover;
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
                data  = $this.data('hmt.PreviewMarkdown');

            if (!data) $this.data('hmt.PreviewMarkdown', (data = new PreviewMarkdown(this)))
            if (typeof option == 'string') data[option].call($this)
        })
    }

    $.fn.previewMarkdown = Plugin

})(jQuery, marked);