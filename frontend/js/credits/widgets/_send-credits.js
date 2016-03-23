'use strict';

import FormHelper from "./../../common/_form-helper";


/**
 * Suggestion item
 * @typedef {Object} Suggestion
 * @property {String} value - name of persion
 * @property {Number} data.id - id of person
 * @property {String} data.img - url of image
 */

/**
 * Form for sending credit
 */
export default class Widget{

    constructor(selector) {
        this.$root = $(selector);
        this.locals = this._getDom();
        this.validation = new FormHelper(this.$root.find('.b-credits__input'));

        if (!Boolean($.fn.autocomplete)){
            console.log('jQuery autocomplete plugin is not include into page');
            return;
        }
        this._initAutoComplete();
        this._assignEvents();
    }

    _getDom() {
        const $root = this.$root;

        return {
            $count: $root.find('[data-credict-count]'),
            $value: $root.find('[data-credit-value]'),
            $to: $root.find('[data-credit-to]'),
            $toData: $root.find('[data-credit-to-data]'),
            $message: $root.find('[data-credit-message]'),
            $error: $root.find('[data-credit-error]')
        };
    }

    _initAutoComplete() {
        const self = this;
        const locals = this.locals;
        const url = jsRoutes.controllers.Facilitators.search(this.$root.attr('data-brand-id')).url;

        locals.$to.autocomplete({
            serviceUrl: url,
            paramName: 'query',
            minChars: 3,
            preserveInput: true,            
            onSelect: function (suggestion) {
                locals.$to.val(suggestion.name);
                locals.$toData.val(suggestion.data);
                return true;
            },
            formatResult: function (suggestion, currentValue) {
                return suggestion.value;
            },
            transformResult: function(response) {
                const suggestions = $.parseJSON(response).suggestions;

                return {
                    suggestions: suggestions.map(function(item){
                        let template = self._getSuggestTemplate(item);

                        return {
                            value: template,
                            data: item.data.id,
                            name: item.value
                        }
                    })
                };
            }
        });
    }

    /**
     * Render template for suggestion
     * @param {Suggestion} data - suggestion object
     * @private
     */
     _getSuggestTemplate(item){
        return `<div class="b-suggest__img" style="background-image: url(${item.data.img})"></div><div class="b-suggest__name">${item.value}</div>`
    }

    _assignEvents() {
        this.$root
            .on('input', 'input', (e) => this.locals.$error.text(''))
            .on('submit', this._onSubmitForm.bind(this));
    }

    _onSubmitForm(e) {
        e.preventDefault();
        const self = this;

        if (!self._isFormValid()) return false;

        self._sendRequest()
            .done(() => {
                success("You have sent credits successfully!", 4500);
                self._setNewValues();

                self.validation.clearForm();
            })
            .fail((response) => {
                self._setNewValues();

                const data = $.parseJSON(response.responseText).data;
                const errorText = self.validation.getErrorsText(data.errors);

                if (!data.errors) return;

                self.locals.$error.text(errorText);
                self.validation.setErrors(data.errors);
            })
    }

    _isFormValid() {
        const locals = this.locals;
        const creditsLeft = Number(locals.$count.text());
        const isEnoughCredits = Number(locals.$value.val()) <= creditsLeft;
        let valid = true;
        let errorText = '';

        if (!this.validation.isValidInputs()) {
            valid = false;
            errorText += this.validation.getErrorsText();
        }

        if (creditsLeft == 0) {
            valid = false;
            errorText += 'You have no more credits to share. ';
        } else if (!isEnoughCredits) {
            valid = false;
            errorText += 'You cannot give more than ' + locals.$count.text() + ' credits. ';
        }

        if (!valid) {
            locals.$error.text(errorText);
        }

        return valid;
    }

    _sendRequest() {
        return $.post(this.$root.attr('action'),
            {
                amount: this.locals.$value.val(),
                to: this.locals.$toData.val(),
                reason: this.locals.$message.val()
            }
        );
    }

    _setNewValues() {
        const locals = this.locals;
        let lastCredits;

        lastCredits = +parseInt(locals.$count.text()) - (+parseInt(locals.$value.val()));
        locals.$count.text(lastCredits);

        App.events.pub('hmt.asynctabs.refresh');
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

