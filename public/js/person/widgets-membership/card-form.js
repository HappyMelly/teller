(function ($, App) {
    'use strict';

    /**
     * Form for updating card information
     * @param selector
     * @constructor
     */
    function CardForm(selector) {
        this.$root = $(selector);
        this.locals = this._getDom();

        this._assignEvents();
    }

    CardForm.prototype._getDom = function () {
        var $root = this.$root;

        return {
            
        }
    };

    CardForm.prototype._assignEvents = function () {
        var self = this;

       
    };


    App.widgets.CardForm = CardForm;
})(jQuery, App);