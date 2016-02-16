
/**
 *  filter list
 */
(function ($, App) {
    'use strict';

    function FilterList(selector, options){
        var self = this;

        self.$root = $(selector);
        self.options = $.extend({}, options, self.$root.data());

        self.locals = {
            $list: self.$root.find('[data-filter-list]'),
            $items: self.$root.find('[data-filter-text]')
        };

        self.assignEvents();
    }

    FilterList.prototype.assignEvents = function(){
        var self = this;

        self.$root
            .on('click', '[data-filter-link]', function (e) {
                var $this = $(this);

                if ($this.hasClass('state_selected')) return;

                self.setActiveLink($this);
                self.filterList($this.data('filter-link'));
                e.preventDefault();
            })
    };

    FilterList.prototype.filterList = function(filterText){
        var self = this,
            locals = self.locals;

        if (filterText == 'all'){
            locals.$items.each(function(index, el){
                $(el).removeClass('state_hidden');
            });
            return;
        }

        locals.$items.each(function(index, el){
            var $el = $(el),
                isHiddenItem = $el.data('filter-text').indexOf(filterText) === -1;

            $el.toggleClass('state_hidden', isHiddenItem);
        })
    };

    FilterList.prototype.setActiveLink = function($el){
        $el.addClass('state_selected')
            .siblings().removeClass('state_selected');
    };

    App.widgets.FilterList = FilterList;

})(jQuery, App);