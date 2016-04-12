'use strict';


export default class InputChecking {
    /**
     * Validate form through inputs
     */
    constructor(data) {
        this.$root = data.$root;
        this.url = data.url;
        this.locals = this._getDom();
        this.valid = true;

        this._checkValue();
        this._assignEvents();
    }

    _getDom() {
        const $root = this.$root;

        return {
            $content: $root.find('[data-inputcheck-block]'),
            $input: $root.find('[data-inputcheck-input]'),
            $error: $root.find('[data-inputcheck-error]'),
            $text: $root.find('[data-inputcheck-text]')
        };
    }

    _assignEvents() {
        this.locals.$input
            .on('blur', this._checkValue.bind(this))
            .on('focus', this._hideCheckingError.bind(this))
    }

    _checkValue() {
        const self = this;
        const locals = self.locals;
        const valueInput = locals.$input.val();

        if (!locals.$input.val()){
            self._showCheckingError('Empty vat value');
            return;
        }

        this.$root
            .removeClass('b-inputcheck_state_complete b-inputcheck_state_error')
            .addClass('b-inputcheck_state_checking');

        self._sendCheckVat(valueInput)
            .done(function(response){
                const vatText = $.parseJSON(response).message;
                self._completeChecking(vatText);
            })
            .fail(function(response){
                const error = $.parseJSON(response.responseText).message;
                self._showCheckingError(error);
            })
    }

    /**
     * Show error when input value is invalid
     * @param {String} error
     * @private
     */
    _showCheckingError(error){
        const locals = this.locals;
        this.valid = false;

        this.$root
            .removeClass('b-inputcheck_state_checking')
            .addClass('b-inputcheck_state_error');

        this.$root.trigger('change.inpuchecking');
        locals.$error.text(error);
    }

    /**
     * Show success text after checking
     * @param {String} vatText
     * @private
     */
    _completeChecking(vatText){
        const locals = this.locals;
        this.valid = true;

        this.$root
            .removeClass('b-inputcheck_state_checking')
            .addClass('b-inputcheck_state_complete');
        
        this.$root.trigger('change.inpuchecking');
        locals.$text.text(vatText);
    }

    _hideCheckingError(){
        this.valid = false;
        this.$root.removeClass('b-inputcheck_state_error');
    }

    isValid() {
        return this.valid;
    }

    //transport
    _sendCheckVat(value){
        return $.get(this.url(value).url)
    }
}
