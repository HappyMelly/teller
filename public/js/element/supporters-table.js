(function ($, App) {
    'use strict';

    /**
     * @param selector
     * @constructor
     */
    function SupportersTable(selector) {
        this.$root = $(selector);
        this._assignEvents();
    }

    SupportersTable.prototype._assignEvents = function () {
        var self = this;

        self.$root.on('click', '.dlg-hmfees__link ', function(e){
            var $this = $(this);
            e.preventDefault();

            if ($this.hasClass('state_active')) return;

            self._switchTab($this);
        });
    };

    SupportersTable.prototype._switchTab = function($link){
        var $target = this._getTarget($link);

        if (!$target.length) return;
        
        $target.show()
            .siblings('.table').hide();        
        $link.addClass('state_active')
            .siblings('.dlg-hmfees__link').removeClass('state_active');
    };

    SupportersTable.prototype._getTarget = function($el){
        return this.$root.find($el.attr('href'));
    };

    App.widgets.SupportersTable = SupportersTable;
})(jQuery, App);