/**
 *  Header notification
 */
(function ($, App) {
    'use strict';

    function RequestEvaluationDlg(selector, options){
        var self = this;

        self.$root = $(selector);
        self.options = $.extend({}, options, self.$root.data());
        self.locals = {
            $listParticipants: self.$root.find('[data-requesteval-list]'),
            $textarea: self.$root.find('[data-requesteval-textarea]'),
            $filterLinks: self.$root.find('[data-requesteval-filter]'),
            $submit: self.$root.find('[data-requesteval-submit]')
        };
        self.template = _.template( $('#request-dlg-template').html());

        self.checkFormValidation();
        self.assignEvents();
    }

    RequestEvaluationDlg.prototype.assignEvents = function(){
        var self = this;

        self.$root
            .on('change', '[data-requesteval-list] input', function () {
                self.checkFormValidation();
            })
            .on('input propertychange', '[data-requesteval-textarea]', function() {
                self.checkFormValidation();
            })
            .on('click', '[data-requesteval-filter]', function(e){
                var $this = $(this);

                self.filterParticipants($this);
                e.preventDefault();
            })
            .on('hmt.requestDlg.render', function(e, eventData){
                self.renderCheckboxes(eventData.table);
            })
    };

    RequestEvaluationDlg.prototype.checkFormValidation = function(){
        var self = this,
            locals = self.locals,
            error = 0,
            $participants = self.$root.find('[data-requesteval-list] input');

        if (!$participants.filter(':checked').length){
            error += 1;
        }

        if (!/https?:/i.test(locals.$textarea.val())) {
            error += 1;
        }

        if (error){
            locals.$submit.attr('disabled', 'disabled');
        } else {
            locals.$submit.removeAttr('disabled');
        }
    };

    RequestEvaluationDlg.prototype.renderCheckboxes = function(table){
        var self = this,
            rowsInfo = table._('tr', {}),
            i, n;

        for (i = 0, n = rowsInfo.length; i < n; i++){
            var data = {
                index: i,
                value: rowsInfo[i].person.id,
                name: rowsInfo[i].person.name,
                status: $.isPlainObject(rowsInfo[i].evaluation.status)
            };
            var label = self.template(data);

            self.locals.$listParticipants.append(label);
        }
    };

    RequestEvaluationDlg.prototype.filterParticipants = function($link){
        var $participants = this.$root.find('[data-requesteval-list] input'),
            filterText = $link.data('requesteval-filter'),
            $filterLinks = this.locals.$filterLinks;

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

        this.checkFormValidation();
    };

    App.widgets.RequestEvaluationDlg = RequestEvaluationDlg;

})(jQuery, App);