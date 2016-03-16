'use strict';

import Validation from "./../../common/_form-validation";

/**
 * Form for sending credit
 */
export default class FormCredit {

  /**
   * @param {String} selector
   */
  constructor(selector) {
    this.$root = $(selector);
    this.locals = this._getDom();

    this.availabelBonus = this.locals.$count.text();
    this._assignEvents();
  }

  _getDom() {
    const $root = this.$root;

    return {
      $count: $root.find('[data-credict-count]'),
      $value: $root.find('[data-credit-value]'),
      $to: $root.find('[data-credit-to]'),
      $message: $root.find('[data-credit-message]'),
      $error: $root.find('[data-credit-error]')
    };
  }

  _assignEvents() {
    this.$root
      .on('input', '[data-credit-value], [data-credit-to]', ()=> this._removeErrors())
      .on('submit', this._onSubmitForm.bind(this));
  }

  _onSubmitForm(e) {
    e.preventDefault();

    if (!this._isFormValid()) return false;
    this.sendRequest();
  }
}
(function ($, App) {


  FormCredit.prototype.isFormValid = function () {
    var self = this,
      valid = true,
      errorText = '';

    if (!self.locals.$value.val()) {
      valid = false;
      errorText += 'Give value is empty. ';
      self.setError(self.locals.$value);
    }

    if (+self.locals.$value.val() > (+self.locals.$count.text())) {
      valid = false;
      errorText += 'You cann’t give more than ' + self.locals.$count.text() + ' credits. ';
      self.setError(self.locals.$value);
    }

    if (!self.locals.$to.val()) {
      valid = false;
      errorText += 'Email is empty. ';
      self.setError(self.locals.$to);
    }



    if (error) {
      self.locals.$error.text(errorText);
    }

    return valid;
  };




  FormCredit.prototype.sendRequest = function () {
    var self = this,
      locals = self.locals;

    $.post(
      self.$root.attr('action'),
      {
        value: locals.$value.val(),
        to: locals.$to.val(),
        message: locals.$message.val(),
      }, function () {
        self.$root.clearForm();
        self.$root.addClass('peer-credit_state_send');

        setTimeout(function () {
          self.$root.removeClass('peer-credit_state_send');
        }, 3000)
      }
    );
  };

  App.widgets.FormCredit = FormCredit;

})(jQuery, App);