'use strict';

export default class FormValidation{
  /**
   * Validate form through inputs
   * @param {jQuery} $inputs
   */
  constructor($inputs){
    this.$inputs = $inputs;
    this._assignEvents();
  }

  _assignEvents() {
    this.$inputs.on('input', (e) => this.removeError($(e.target)));
  }

  isValidInputs() {
    const $inputs = this.$inputs;
    let error = 0;

    $inputs.each( (index, input) =>{
      const $input = $(input);

      if (!this._isValidInput($input)) error += 1;
    });
    return Boolean(!error);
  }

  /**
   * Check given input, is it valid?
   * @param {jQuery} $input
   * @returns {boolean} - Is valid input?
   */
  _isValidInput($input) {
    const value = $.trim($input.val());

    if (!value) {
      this._setError($input, 'Empty');
      return false;
    }

    if (($input.hasClass('type-email')) && !this._isValidEmail(value)) {
      this._setError($input, 'Email is not valid');
      return false;
    }
    return true;
  }

  /**
   * Is Email valid?
   * @param {string} email
   * @returns {boolean}
   */
  _isValidEmail (email) {
    var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(email);
  }

  /**
   * Set error for input
   * @param {jQuery} $input
   * @param {string} errorText
   */
  _setError($input, errorText) {
    const $parent = $input.parent();
    const $error = $parent.find('.b-error');

    if ($error.length) return;

    $parent.addClass('b-error_show');
    $('<div class="b-error" />')
      .text(errorText)
      .prependTo($parent);
  }

  /**
   * Remove error for input
   * @param {jQuery} $input
   */
  _removeError($input) {
    const $parent = $input.parent();
    const $error = $parent.find('b-error');

    $parent
      .removeClass('b-error_show')
      .find('.b-error').remove();
  }

  /**
   * Set errors
   * @param {Array} errors - [{name: "email", error: "empty"}, {name: "password", error: "empty"}]
   */
  setErrors(errors) {
    errors.forEach( (item) => {
      const $currentInput = this.$inputs.filter('[name="' + item.name + '"]').first();

      if ($currentInput.length) this._setError($currentInput, item.error)
    })
  }

  /**
   * Remove all errors
   */
  removeErrors() {
    this.$inputs.each( (index, el) => {
      const $el = $(el);
      this._removeError($el)
    })
  }

  clearForm() {
    this.$inputs.each( (index, el) => {
      const $el = $(el);
      if (!$el.attr("disabled"))  $el.val('');
    })
  }
}
