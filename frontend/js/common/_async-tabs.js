'use strict';

export default class Widget {

    constructor(selector) {
        this.$root = $(selector);
        this.loadedTabs = [];
        this.locals = this._getDom();

        this._assignEvents();

        let $firstLink = this.locals.$links.first();
        this.showTabByLink($firstLink);
    }

    _getDom() {
        return {
            $links: this.$root.find('[data-tab-link]')
        }
    }

    _assignEvents() {
        this.$root.on('click', '[data-tab-link]', this._onClickLink.bind(this));
        
        App.events.sub('hmt.asynctabs.refresh', this._onEventRefresh.bind(this));
    }

    _onClickLink(e) {
        e.preventDefault();
        let $link = $(e.currentTarget);

        if ($link.hasClass('state_active')) return;
        this.showTabByLink($link);
    }

    _onEventRefresh(){
        const $currentLink = this.locals.$links.filter('.state_active').first();
        const indexCurrentTab = this.loadedTabs.indexOf($currentLink.attr('href'));

        this.loadedTabs.splice(indexCurrentTab, 1);
        this.showTabByLink($currentLink);
    }

    /**
     * 
     * @param {jQuery} $link - clicked link
     * @private
     */
    showTabByLink($link){
        const url = $link.attr('data-href');
        const target = $link.attr('href');
        const self = this;

        self._loadContent(url, target)
            .done(  ()=>{
                $link.addClass('state_active').siblings().removeClass('state_active');
                $link.tab('show');

                App.events.pub('hmt.asynctab.shown');
            })
    }

    /**
     *  Load content and insert into target div
     * @param {String} url      - url of loaded content
     * @param {jQuery} target   - div where we should insert conten
     */
    _loadContent(url, target){
        const self = this;
        const isShouldLoad = ($.inArray(target, self.loadedTabs) < 0 && url);
        let defer = $.Deferred();

        if (isShouldLoad) {
            $.get(url, (data) => {
                self.loadedTabs.push(target);
                $(target).html(data);

                defer.resolve();
            });
        } else {
            defer.resolve();
        }
        
        return defer.promise();
    }    

    // static
    static plugin(selector) {
        const $elems = $(selector);
        if (!$elems.length) return;

        return $elems.each(function (index, el) {
            let $element = $(el);
            let data     = $element.data('widget');

            if (!data) {
                data = new Widget(el);
                $element.data('widget', data);
            }
        })
    }
}


