'use strict';

export default class Widget {

    constructor(selector, options) {
        let defaultOptions = {
            url: jsRoutes.controllers.Utilities.markdown().url,
            interval: 1000,
            markdownposition: "body",
            template: "<div class='popover-bl'><i class='fa fa-spinner fa-spin'></i><div class='popover-bl__content' data-content></div></div>"
        };

        this.$root = $(selector);
        this.options = $.extend({}, defaultOptions, options);

        this.resetState();
        this.assignEvents();
    }

    resetState(){
        this.sending = null;
        this.sending = null;
        this.isNeedUpdating = null;
        this.waitingTimer = null
    }

    createPopover(){
        this.$popover = $(this.options.template);

        if (this.options.markdownclass){
            this.$popover.addClass(this.options.markdownclass);
        }

        if (this.options.markdownposition == "body"){
            this.$popover.appendTo('body');
        } else {
            this.$popover.insertAfter(this.$root);
        }
    }

    getPosition(){
        const $root = this.$root;
        let offsetBody = {
                top: $root.offset().top,
                left: $root.offset().left + $root.outerWidth()
            };

        return this.options.markdownposition == "body"? offsetBody: {};
    }

    assignEvents (){
        const self = this;

        self.$root
            .on('focus', function(){
                self.toggle();
                self.compileContent();
            })
            .on('keyup', function(e){
                if (!self.isKeyTrigger(e.which)) return true;

                self.pausing && (self.isNeedUpdating = true);
                self.compileContent();
            })
            .on('blur', function(){
                self.resetState();
                self.toggle();
            });

        $(window).resize(function () {
            self.$popover && self.$popover.css(self.getPosition());
        });
    }

    compileContent(content){
        var self = this;
        content = content || self.$root.val();

        if (self.sending || self.pausing) return;

        self.pausing = true;
        self.setSending();
        self.setWaiting();

        $.post(self.options.url, {
            data: content
        }, function(data){
            self.setSended();
            self.$popover.find('[data-content]').html(data);
        });
    }

    setSending(){
        this.sending = true;
        this.$popover.addClass('popover-bl_loading');
    }

    setSended(){
        this.sending = false;
        this.$popover.removeClass('popover-bl_loading');
    }

    setWaiting(){
        var self = this;

        self.waitingTimer = setTimeout(
            function () {
                self.pausing = false;
                if (self.isNeedUpdating) {
                    self.isNeedUpdating = false;
                    self.compileContent();
                }
            }, self.options.interval);
    }

    show(){
        if ( !this.$popover) this.createPopover();
        this.$popover.css(this.getPosition());

        if (this.isVisible) return;

        this.isVisible = true;
        this.$popover.addClass('popover-bl_show');
    }

    hide(){
        if (!this.isVisible) return;

        this.isVisible = false;
        this.$popover.removeClass('popover-bl_show');
    }

    toggle(){
        this[this.isVisible? 'hide': 'show']();
    }

    isKeyTrigger(code){
        return (code >= 45 && code <= 90) || (code >= 186) && (code <= 222) || (code == 13) || (code == 27) || (code == 32) || (code == 8);
    }



    // static
    static plugin(selector, options) {
        const $elems = $(selector);
        if (!$elems.length) return;

        return $elems.each(function (index, el) {
            let $element = $(el);
            let data     = $element.data('widget.preview.markdown');

            if (!data) {
                data = new Widget(el, options);
                $element.data('widget', data);
            }
        })
    }
}

