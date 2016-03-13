(function ($, App) {
    'use strict';

    /**
     * @param selector
     * @constructor
     */
    function SupportersTable(selector) {
        this.$root = $(selector);
        this.locals = this._getDom();

        this._assignEvents();
    }

    SupportersTable.prototype._getDom = function () {
        var $root = this.$root;

        return {

        }
    };

    SupportersTable.prototype._assignEvents = function () {
        var self = this;


    };


    App.widgets.SupportersTable = SupportersTable;
})(jQuery, App);