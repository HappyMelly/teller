'use strict';

import FormHelper from "./../../common/_form-helper";

export default class Widget {
    constructor(selector, options) {
        this.$root = $(selector);
        this.options = $.extend({}, options, this.$root.data());

        this._assignEvents();
    }

    _assignEvents(){
        this.$root.on('click', '[data-social-connect]', this._onClickConnect.bind(this));
    }
    
    _onClickConnect(e){
        e.preventDefault();
        const $root = $(e.currentTarget).closest('.b-connect-i');

        this.toggleConnect($root);
    }

    _setConnect($el) {
        window.location = $el.data('url');
    }

    _unSetConnect($el){
        const socialType = $el.data('social');
        const url = jsRoutes.controllers.core.UserAccounts.disconnect(socialType).url;

        $.post(url, {}, function(data) {
            $el.removeClass('state-complete');
            success(data.message);
        }, "json").fail(function(jqXHR, textStatus, errorThrown) {
            var response = JSON.parse(jqXHR.responseText);
            error(response.message);
        });
    }

    toggleConnect($el){
        if ($el.hasClass('state-complete')){
            this._unSetConnect($el);
        } else {
            this._setConnect($el);
        }
    }

    // static
    static plugin(selector, options) {
        const $elems = $(selector);
        if (!$elems.length) return;

        return $elems.each(function (index, el) {
            let $element = $(el);
            let data     = $element.data('widget.scrollto');

            if (!data) {
                data = new Widget(el, options);
                $element.data('widget', data);
            }
        })
    }
}
