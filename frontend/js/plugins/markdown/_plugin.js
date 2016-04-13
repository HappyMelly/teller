'use strict';

import conf from './_set';
import plugin from './_jquery.markitup';

export default class Widget {

    constructor(selector) {
        this.$root = $(selector);
        this.url = jsRoutes.controllers.Utilities.markdown().url;

        if (!$.fn.markItUp){
            console.log('invalid dependency');
            return;
        }
        this._init();

        this.locals = this._getDom();
        this._assignEvents();
    }
    
    _init(){
        const linksTemplate = '<div class="markitup__link type-write state_active">Write</div><div class="markitup__link type-preview">Preview</div> <div class="markitup__preview"></div>';

        this.$root
            .wrap('<div class="markitup__con"></div>')
            .after(linksTemplate);

        this.$root.markItUp(conf);        
    }

    _getDom(){
        const $container = this.$root.closest('.markitup__con');

        return {
            $container: $container,
            $textarea: $container.find('textarea'),
            $preview: $container.find('.markitup__preview')
        }
    }

    _assignEvents() {
        this.locals.$container
            .on('click', '.markitup__link', this._onClickToggle.bind(this));
    }

    _onClickToggle(e){
        const $link = $(e.currentTarget);
        e.preventDefault();

        if ($link.hasClass('state_active')) return;

        const isShowPreview = $link.hasClass('type-preview');
        this._togglePreview(isShowPreview);

        $link.addClass('state_active')
            .siblings()
            .removeClass('state_active');
    }

    _togglePreview(isShowPreview){
        const locals = this.locals;
        const nameClass = 'markitup_state_preview';

        if (!isShowPreview){
            locals.$container.removeClass(nameClass);
            return;
        }

        this._compileContent(locals.$textarea.val())
            .done((data) => {
                locals.$preview.html(data);

                if (!locals.$container.hasClass(nameClass)){
                    locals.$container.addClass(nameClass)
                }
            })
    }

    //transport
    _compileContent(content) {
         return $.post(this.url, {
            data: content
        });
    }

    // static
    static plugin(selector) {
        const $elems = $(selector);
        if (!$elems.length) return;

        return $elems.each(function (index, el) {
            let $element = $(el);
            let data = $element.data('widget.preview.markdown');

            if (!data) {
                data = new Widget(el);
                $element.data('widget', data);
            }
        })
    }
}

