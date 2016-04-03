'use strict';

export default class Widget {
    constructor(selector){
        this.$root = $(selector);
        this.locals = this._getDom();
        this.template = _.template( $('#request-dlg-template').html());

        this._checkFormValidation();
        this._assignEvent();
    }

    _getDom(){
        const $root = this.$root;

        return {
            $listParticipants: $root.find('[data-requesteval-list]'),
            $textarea: $root.find('[data-requesteval-textarea]'),
            $filterLinks: $root.find('[data-requesteval-filter]'),
            $submit: $root.find('[data-requesteval-submit]')
        }
    }

    _assignEvent(){
        this.$root
            .on('change', '[data-requesteval-list] input', this._checkFormValidation.bind(this))
            .on('input propertychange', '[data-requesteval-textarea]', this._checkFormValidation.bind(this))
            .on('click', '[data-requesteval-filter]', (e) =>{
                e.preventDefault();

                const $link = $(e.currentTarget);
                this._filterParticipants($link);
            });

        App.events.sub('hmt.requestDlg.render', this._renderCheckboxes.bind(this));
    }

    _checkFormValidation(){
        const locals = this.locals;
        const $participants = this.$root.find('[data-requesteval-list] input');
        let valid = true;

        if (!$participants.filter(':checked').length){
            valid = false;
        }

        if (!/https?:/i.test(locals.$textarea.val())) {
            valid = false;
        }

        if (!valid){
            locals.$submit.attr('disabled', 'disabled');
        } else {
            locals.$submit.removeAttr('disabled');
        }
    }

    /**
     * Render list of checkboxes
     * @param {jQuery} participant - participant table
     * @private
     */
    _renderCheckboxes(participant) {
        let rowsInfo = participant.table._('tr', {});
        let i, n;

        for (i = 0, n = rowsInfo.length; i < n; i++) {
            const data = {
                index: i,
                value: rowsInfo[i].person.id,
                name: rowsInfo[i].person.name,
                status: $.isPlainObject(rowsInfo[i].evaluation.status)
            };
            const label = this.template(data);

            this.locals.$listParticipants.append(label);
        }
    }

    /**
     *
     * @param {jQuery} $link
     * @private
     */
    _filterParticipants($link) {
        const $participants = this.$root.find('[data-requesteval-list] input');
        const filterText = $link.data('requesteval-filter');
        const $filterLinks = this.locals.$filterLinks;

        if ($link.hasClass('state_selected')) return;

        switch (filterText){
            case 'all': {
                $participants.prop('checked', true);
                break;
            }
            case 'with': {
                $participants.prop('checked', false)
                    .filter('.have-evaluation').prop('checked', true);
                break;
            }
            case 'without': {
                $participants
                    .prop('checked', true)
                    .filter('.have-evaluation').prop('checked', false);
                break;
            }
            default: {
                $participants.prop('checked', false);
                break;
            }
        }

        $filterLinks.removeClass('state_selected');
        $link.addClass('state_selected');

        this._checkFormValidation();
    }

    // static
    static plugin(selector) {
        const $elems = $(selector);
        if (!$elems.length) return;

        return $elems.each(function (index, el) {
            let $element = $(el);
            let data     = $element.data('hmt.events.upcoming');

            if (!data) {
                data = new Widget(el);
                $element.data('widget', data);
            }
        })
    }
}

