'use strict';

export default class Widget {
    /**
     * Filter history
     * @param {String} selector
     */
    constructor(selector) {
        this.$root = $(selector);
        this._assignEvents();
    }

    _assignEvents() {
        this.$root.on('click', this._onClickLink.bind(this));
    }

    _onClickLink(e) {
        const $link = $(e.currentTarget);
        const target = $link.data('target');

        if (!this._isTargetValid(target)) return false;

        this.scrollToTarget('#' + target);
        e.preventDefault();
    }

    _isTargetValid(target){
        let valid = true;

        if (!target) {
            console.log('There is no data-target attribute with id-name for this link');
            valid = false;
        }

        if (!$('#' + target).length) {
            console.log('There is no element with such id name');
            valid = false;
        }

        return valid;
    }

    /**
     * Scroll to the element with "target" id
     * @param {String} target - id selector of element
     * @returns {boolean}
     */
    scrollToTarget(target) {
        const $target = $(target);

        if (!$target.length) return false;

        $('html, body').animate({
            scrollTop: $target.offset().top
        }, 400);
    }

    // static
    static plugin(selector) {
        const $elems = $(selector);
        if (!$elems.length) return;

        return $elems.each(function (index, el) {
            let $element = $(el);
            let data     = $element.data('widget.scrollto');

            if (!data) {
                data = new Widget(el);
                $element.data('widget', data);
            }
        })
    }
}
