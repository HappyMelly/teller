

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