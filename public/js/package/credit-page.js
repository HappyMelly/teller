/******/ (function(modules) { // webpackBootstrap
/******/ 	// The module cache
/******/ 	var installedModules = {};
/******/
/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {
/******/
/******/ 		// Check if module is in cache
/******/ 		if(installedModules[moduleId])
/******/ 			return installedModules[moduleId].exports;
/******/
/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = installedModules[moduleId] = {
/******/ 			exports: {},
/******/ 			id: moduleId,
/******/ 			loaded: false
/******/ 		};
/******/
/******/ 		// Execute the module function
/******/ 		modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);
/******/
/******/ 		// Flag the module as loaded
/******/ 		module.loaded = true;
/******/
/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}
/******/
/******/
/******/ 	// expose the modules object (__webpack_modules__)
/******/ 	__webpack_require__.m = modules;
/******/
/******/ 	// expose the module cache
/******/ 	__webpack_require__.c = installedModules;
/******/
/******/ 	// __webpack_public_path__
/******/ 	__webpack_require__.p = "";
/******/
/******/ 	// Load entry module and return exports
/******/ 	return __webpack_require__(0);
/******/ })
/************************************************************************/
/******/ ([
/* 0 */
/***/ function(module, exports, __webpack_require__) {

	module.exports = __webpack_require__(1);


/***/ },
/* 1 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';
	
	var _filterHistory = __webpack_require__(2);
	
	var _filterHistory2 = _interopRequireDefault(_filterHistory);
	
	var _sendCredits = __webpack_require__(3);
	
	var _sendCredits2 = _interopRequireDefault(_sendCredits);
	
	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }
	
	new _filterHistory2.default('.js-credit-history');
	new _sendCredits2.default('.js-form-credit');

/***/ },
/* 2 */
/***/ function(module, exports) {

	'use strict';
	
	Object.defineProperty(exports, "__esModule", {
	  value: true
	});
	
	var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();
	
	function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }
	
	var FilterHistory = function () {
	  /**
	   * Filter history
	   * @param {String} selector
	   */
	
	  function FilterHistory(selector) {
	    _classCallCheck(this, FilterHistory);
	
	    this.$root = $(selector);
	    this.locals = this._getDom();
	
	    this._assignEvents();
	  }
	
	  _createClass(FilterHistory, [{
	    key: '_getDom',
	    value: function _getDom() {
	      var $root = this.$root;
	
	      return {
	        $list: $root.find('[data-filter-list]'),
	        $items: $root.find('[data-filter-text]')
	      };
	    }
	  }, {
	    key: '_assignEvents',
	    value: function _assignEvents() {
	      this.$root.on('click', '[data-filter-link]', this._onClickFilter.bind(this));
	    }
	  }, {
	    key: '_onClickFilter',
	    value: function _onClickFilter(e) {
	      var $link = $(e.target);
	      var filterText = $link.data('filter-link');
	
	      e.preventDefault();
	
	      if ($link.hasClass('state_selected')) return;
	
	      this.setActiveLink($link);
	      this.filterList(filterText);
	    }
	  }, {
	    key: 'filterList',
	
	
	    /**
	     * Filter list through text
	     * @param {String} filterText
	     */
	    value: function filterList(filterText) {
	      var $items = this.locals.$items;
	
	      if (filterText == 'all') {
	        $items.removeClass('state_hidden');
	        return;
	      }
	
	      $items.each(function (index, el) {
	        var $el = $(el);
	        var isHidden = $el.data('filter-text').indexOf(filterText) === -1;
	
	        $el.toggleClass('state_hidden', isHidden);
	      });
	    }
	  }, {
	    key: 'setActiveLink',
	
	
	    /**
	     * Set link to active and deactivate other
	     * @param {jQuery} $el
	     */
	    value: function setActiveLink($el) {
	      $el.addClass('state_selected').siblings().removeClass('state_selected');
	    }
	  }]);
	
	  return FilterHistory;
	}();

	exports.default = FilterHistory;

/***/ },
/* 3 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';
	
	Object.defineProperty(exports, "__esModule", {
	  value: true
	});
	
	var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();
	
	var _formValidation = __webpack_require__(4);
	
	var _formValidation2 = _interopRequireDefault(_formValidation);
	
	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }
	
	function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }
	
	/**
	 * Form for sending credit
	 */
	
	var FormCredit = function () {
	
	  /**
	   * @param {String} selector
	   */
	
	  function FormCredit(selector) {
	    _classCallCheck(this, FormCredit);
	
	    this.$root = $(selector);
	    this.locals = this._getDom();
	
	    this.availabelBonus = this.locals.$count.text();
	    this._assignEvents();
	  }
	
	  _createClass(FormCredit, [{
	    key: '_getDom',
	    value: function _getDom() {
	      var $root = this.$root;
	
	      return {
	        $count: $root.find('[data-credict-count]'),
	        $value: $root.find('[data-credit-value]'),
	        $to: $root.find('[data-credit-to]'),
	        $message: $root.find('[data-credit-message]'),
	        $error: $root.find('[data-credit-error]')
	      };
	    }
	  }, {
	    key: '_assignEvents',
	    value: function _assignEvents() {
	      var _this = this;
	
	      this.$root.on('input', '[data-credit-value], [data-credit-to]', function () {
	        return _this._removeErrors();
	      }).on('submit', this._onSubmitForm.bind(this));
	    }
	  }, {
	    key: '_onSubmitForm',
	    value: function _onSubmitForm(e) {
	      e.preventDefault();
	
	      if (!this._isFormValid()) return false;
	      this.sendRequest();
	    }
	  }]);
	
	  return FormCredit;
	}();
	
	exports.default = FormCredit;
	
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
	
	    if (+self.locals.$value.val() > +self.locals.$count.text()) {
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
	
	    $.post(self.$root.attr('action'), {
	      value: locals.$value.val(),
	      to: locals.$to.val(),
	      message: locals.$message.val()
	    }, function () {
	      self.$root.clearForm();
	      self.$root.addClass('peer-credit_state_send');
	
	      setTimeout(function () {
	        self.$root.removeClass('peer-credit_state_send');
	      }, 3000);
	    });
	  };
	
	  App.widgets.FormCredit = FormCredit;
	})(jQuery, App);

/***/ },
/* 4 */
/***/ function(module, exports) {

	'use strict';
	
	Object.defineProperty(exports, "__esModule", {
	  value: true
	});
	
	var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();
	
	function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }
	
	var FormValidation = function () {
	  /**
	   * Validate form through inputs
	   * @param {jQuery} $inputs
	   */
	
	  function FormValidation($inputs) {
	    _classCallCheck(this, FormValidation);
	
	    this.$inputs = $inputs;
	    this._assignEvents();
	  }
	
	  _createClass(FormValidation, [{
	    key: '_assignEvents',
	    value: function _assignEvents() {
	      var _this = this;
	
	      this.$inputs.on('input', function (e) {
	        return _this.removeError($(e.target));
	      });
	    }
	  }, {
	    key: 'isValidInputs',
	    value: function isValidInputs() {
	      var _this2 = this;
	
	      var $inputs = this.$inputs;
	      var error = 0;
	
	      $inputs.each(function (index, input) {
	        var $input = $(input);
	
	        if (!_this2._isValidInput($input)) error += 1;
	      });
	      return Boolean(!error);
	    }
	
	    /**
	     * Check given input, is it valid?
	     * @param {jQuery} $input
	     * @returns {boolean} - Is valid input?
	     */
	
	  }, {
	    key: '_isValidInput',
	    value: function _isValidInput($input) {
	      var value = $.trim($input.val());
	
	      if (!value) {
	        this._setError($input, 'Empty');
	        return false;
	      }
	
	      if ($input.hasClass('type-email') && !this._isValidEmail(value)) {
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
	
	  }, {
	    key: '_isValidEmail',
	    value: function _isValidEmail(email) {
	      var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
	      return re.test(email);
	    }
	
	    /**
	     * Set error for input
	     * @param {jQuery} $input
	     * @param {string} errorText
	     */
	
	  }, {
	    key: '_setError',
	    value: function _setError($input, errorText) {
	      var $parent = $input.parent();
	      var $error = $parent.find('.b-error');
	
	      if ($error.length) return;
	
	      $parent.addClass('b-error_show');
	      $('<div class="b-error" />').text(errorText).prependTo($parent);
	    }
	
	    /**
	     * Remove error for input
	     * @param {jQuery} $input
	     */
	
	  }, {
	    key: '_removeError',
	    value: function _removeError($input) {
	      var $parent = $input.parent();
	      var $error = $parent.find('b-error');
	
	      $parent.removeClass('b-error_show').find('.b-error').remove();
	    }
	
	    /**
	     * Set errors
	     * @param {Array} errors - [{name: "email", error: "empty"}, {name: "password", error: "empty"}]
	     */
	
	  }, {
	    key: 'setErrors',
	    value: function setErrors(errors) {
	      var _this3 = this;
	
	      errors.forEach(function (item) {
	        var $currentInput = _this3.$inputs.filter('[name="' + item.name + '"]').first();
	
	        if ($currentInput.length) _this3._setError($currentInput, item.error);
	      });
	    }
	
	    /**
	     * Remove all errors
	     */
	
	  }, {
	    key: 'removeErrors',
	    value: function removeErrors() {
	      var _this4 = this;
	
	      this.$inputs.each(function (index, el) {
	        var $el = $(el);
	        _this4._removeError($el);
	      });
	    }
	  }, {
	    key: 'clearForm',
	    value: function clearForm() {
	      this.$inputs.each(function (index, el) {
	        var $el = $(el);
	        if (!$el.attr("disabled")) $el.val('');
	      });
	    }
	  }]);
	
	  return FormValidation;
	}();

	exports.default = FormValidation;

/***/ }
/******/ ]);
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIndlYnBhY2s6Ly8vd2VicGFjay9ib290c3RyYXAgNTlmNWJkMzZhZGRhYzMwNzEzYWYiLCJ3ZWJwYWNrOi8vLy4vZnJvbnRlbmQvanMvcGVyc29uL2NyZWRpdC1wYWdlLmpzIiwid2VicGFjazovLy8uL2Zyb250ZW5kL2pzL3BlcnNvbi93aWRnZXRzLWNyZWRpdC9fZmlsdGVyLWhpc3RvcnkuanMiLCJ3ZWJwYWNrOi8vLy4vZnJvbnRlbmQvanMvcGVyc29uL3dpZGdldHMtY3JlZGl0L19zZW5kLWNyZWRpdHMuanMiLCJ3ZWJwYWNrOi8vLy4vZnJvbnRlbmQvanMvY29tbW9uL19mb3JtLXZhbGlkYXRpb24uanMiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IjtBQUFBO0FBQ0E7O0FBRUE7QUFDQTs7QUFFQTtBQUNBO0FBQ0E7O0FBRUE7QUFDQTtBQUNBLHVCQUFlO0FBQ2Y7QUFDQTtBQUNBOztBQUVBO0FBQ0E7O0FBRUE7QUFDQTs7QUFFQTtBQUNBO0FBQ0E7OztBQUdBO0FBQ0E7O0FBRUE7QUFDQTs7QUFFQTtBQUNBOztBQUVBO0FBQ0E7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7O0FDakNBLDZCQUFXLG9CQUFYO0FBQ0EsMkJBQWUsaUJBQWYsRTs7Ozs7O0FDTkE7Ozs7Ozs7Ozs7S0FFcUI7Ozs7OztBQUtuQixZQUxtQixhQUtuQixDQUFZLFFBQVosRUFBc0I7MkJBTEgsZUFLRzs7QUFDcEIsVUFBSyxLQUFMLEdBQWEsRUFBRSxRQUFGLENBQWIsQ0FEb0I7QUFFcEIsVUFBSyxNQUFMLEdBQWMsS0FBSyxPQUFMLEVBQWQsQ0FGb0I7O0FBSXBCLFVBQUssYUFBTCxHQUpvQjtJQUF0Qjs7Z0JBTG1COzsrQkFZVDtBQUNSLFdBQU0sUUFBUSxLQUFLLEtBQUwsQ0FETjs7QUFHUixjQUFPO0FBQ0wsZ0JBQU8sTUFBTSxJQUFOLENBQVcsb0JBQVgsQ0FBUDtBQUNBLGlCQUFRLE1BQU0sSUFBTixDQUFXLG9CQUFYLENBQVI7UUFGRixDQUhROzs7O3FDQVNNO0FBQ2Qs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QixDQUFwQixDQUFrRCxLQUFsRCxFQUFoQixDQURrQjs7QUFHeEIsYUFBSSxjQUFjLE1BQWQsRUFBc0IsT0FBSyxTQUFMLENBQWUsYUFBZixFQUE4QixLQUFLLEtBQUwsQ0FBOUIsQ0FBMUI7UUFIYyxDQUFoQixDQURnQjs7Ozs7Ozs7O29DQVdIOzs7QUFDYixZQUFLLE9BQUwsQ0FBYSxJQUFiLENBQW1CLFVBQUMsS0FBRCxFQUFRLEVBQVIsRUFBZTtBQUNoQyxhQUFNLE1BQU0sRUFBRSxFQUFGLENBQU4sQ0FEMEI7QUFFaEMsZ0JBQUssWUFBTCxDQUFrQixHQUFsQixFQUZnQztRQUFmLENBQW5CLENBRGE7Ozs7aUNBT0g7QUFDVixZQUFLLE9BQUwsQ0FBYSxJQUFiLENBQW1CLFVBQUMsS0FBRCxFQUFRLEVBQVIsRUFBZTtBQUNoQyxhQUFNLE1BQU0sRUFBRSxFQUFGLENBQU4sQ0FEMEI7QUFFaEMsYUFBSSxDQUFDLElBQUksSUFBSixDQUFTLFVBQVQsQ0FBRCxFQUF3QixJQUFJLEdBQUosQ0FBUSxFQUFSLEVBQTVCO1FBRmlCLENBQW5CLENBRFU7Ozs7VUE1R08iLCJmaWxlIjoiY3JlZGl0LXBhZ2UuanMiLCJzb3VyY2VzQ29udGVudCI6WyIgXHQvLyBUaGUgbW9kdWxlIGNhY2hlXG4gXHR2YXIgaW5zdGFsbGVkTW9kdWxlcyA9IHt9O1xuXG4gXHQvLyBUaGUgcmVxdWlyZSBmdW5jdGlvblxuIFx0ZnVuY3Rpb24gX193ZWJwYWNrX3JlcXVpcmVfXyhtb2R1bGVJZCkge1xuXG4gXHRcdC8vIENoZWNrIGlmIG1vZHVsZSBpcyBpbiBjYWNoZVxuIFx0XHRpZihpbnN0YWxsZWRNb2R1bGVzW21vZHVsZUlkXSlcbiBcdFx0XHRyZXR1cm4gaW5zdGFsbGVkTW9kdWxlc1ttb2R1bGVJZF0uZXhwb3J0cztcblxuIFx0XHQvLyBDcmVhdGUgYSBuZXcgbW9kdWxlIChhbmQgcHV0IGl0IGludG8gdGhlIGNhY2hlKVxuIFx0XHR2YXIgbW9kdWxlID0gaW5zdGFsbGVkTW9kdWxlc1ttb2R1bGVJZF0gPSB7XG4gXHRcdFx0ZXhwb3J0czoge30sXG4gXHRcdFx0aWQ6IG1vZHVsZUlkLFxuIFx0XHRcdGxvYWRlZDogZmFsc2VcbiBcdFx0fTtcblxuIFx0XHQvLyBFeGVjdXRlIHRoZSBtb2R1bGUgZnVuY3Rpb25cbiBcdFx0bW9kdWxlc1ttb2R1bGVJZF0uY2FsbChtb2R1bGUuZXhwb3J0cywgbW9kdWxlLCBtb2R1bGUuZXhwb3J0cywgX193ZWJwYWNrX3JlcXVpcmVfXyk7XG5cbiBcdFx0Ly8gRmxhZyB0aGUgbW9kdWxlIGFzIGxvYWRlZFxuIFx0XHRtb2R1bGUubG9hZGVkID0gdHJ1ZTtcblxuIFx0XHQvLyBSZXR1cm4gdGhlIGV4cG9ydHMgb2YgdGhlIG1vZHVsZVxuIFx0XHRyZXR1cm4gbW9kdWxlLmV4cG9ydHM7XG4gXHR9XG5cblxuIFx0Ly8gZXhwb3NlIHRoZSBtb2R1bGVzIG9iamVjdCAoX193ZWJwYWNrX21vZHVsZXNfXylcbiBcdF9fd2VicGFja19yZXF1aXJlX18ubSA9IG1vZHVsZXM7XG5cbiBcdC8vIGV4cG9zZSB0aGUgbW9kdWxlIGNhY2hlXG4gXHRfX3dlYnBhY2tfcmVxdWlyZV9fLmMgPSBpbnN0YWxsZWRNb2R1bGVzO1xuXG4gXHQvLyBfX3dlYnBhY2tfcHVibGljX3BhdGhfX1xuIFx0X193ZWJwYWNrX3JlcXVpcmVfXy5wID0gXCJcIjtcblxuIFx0Ly8gTG9hZCBlbnRyeSBtb2R1bGUgYW5kIHJldHVybiBleHBvcnRzXG4gXHRyZXR1cm4gX193ZWJwYWNrX3JlcXVpcmVfXygwKTtcblxuXG5cbi8qKiBXRUJQQUNLIEZPT1RFUiAqKlxuICoqIHdlYnBhY2svYm9vdHN0cmFwIDU5ZjViZDM2YWRkYWMzMDcxM2FmXG4gKiovIiwiXG5pbXBvcnQgRmlsdGVyIGZyb20gJy4vd2lkZ2V0cy1jcmVkaXQvX2ZpbHRlci1oaXN0b3J5JztcbmltcG9ydCBDcmVkaXRGb3JtIGZyb20gJy4vd2lkZ2V0cy1jcmVkaXQvX3NlbmQtY3JlZGl0cyc7XG5cblxubmV3IEZpbHRlcignLmpzLWNyZWRpdC1oaXN0b3J5Jyk7XG5uZXcgQ3JlZGl0Rm9ybSgnLmpzLWZvcm0tY3JlZGl0Jyk7XG5cblxuXG5cbi8qKiBXRUJQQUNLIEZPT1RFUiAqKlxuICoqIC4vZnJvbnRlbmQvanMvcGVyc29uL2NyZWRpdC1wYWdlLmpzXG4gKiovIiwiJ3VzZSBzdHJpY3QnO1xuXG5leHBvcnQgZGVmYXVsdCBjbGFzcyBGaWx0ZXJIaXN0b3J5IHtcbiAgLyoqXG4gICAqIEZpbHRlciBoaXN0b3J5XG4gICAqIEBwYXJhbSB7U3RyaW5nfSBzZWxlY3RvclxuICAgKi9cbiAgY29uc3RydWN0b3Ioc2VsZWN0b3IpIHtcbiAgICB0aGlzLiRyb290ID0gJChzZWxlY3Rvcik7XG4gICAgdGhpcy5sb2NhbHMgPSB0aGlzLl9nZXREb20oKTtcblxuICAgIHRoaXMuX2Fzc2lnbkV2ZW50cygpO1xuICB9XG5cbiAgX2dldERvbSgpIHtcbiAgICBjb25zdCAkcm9vdCA9IHRoaXMuJHJvb3Q7XG5cbiAgICByZXR1cm4ge1xuICAgICAgJGxpc3Q6ICRyb290LmZpbmQoJ1tkYXRhLWZpbHRlci1saXN0XScpLFxuICAgICAgJGl0ZW1zOiAkcm9vdC5maW5kKCdbZGF0YS1maWx0ZXItdGV4dF0nKSxcbiAgICB9O1xuICB9XG5cbiAgX2Fzc2lnbkV2ZW50cygpIHtcbiAgICB0aGlzLiRyb290Lm9uKCdjbGljaycsICdbZGF0YS1maWx0ZXItbGlua10nLCB0aGlzLl9vbkNsaWNrRmlsdGVyLmJpbmQodGhpcykpO1xuICB9XG5cbiAgX29uQ2xpY2tGaWx0ZXIoZSkge1xuICAgIGNvbnN0ICRsaW5rID0gJChlLnRhcmdldCk7XG4gICAgY29uc3QgZmlsdGVyVGV4dCA9ICRsaW5rLmRhdGEoJ2ZpbHRlci1saW5rJyk7XG5cbiAgICBlLnByZXZlbnREZWZhdWx0KCk7XG5cbiAgICBpZiAoJGxpbmsuaGFzQ2xhc3MoJ3N0YXRlX3NlbGVjdGVkJykpIHJldHVybjtcblxuICAgIHRoaXMuc2V0QWN0aXZlTGluaygkbGluayk7XG4gICAgdGhpcy5maWx0ZXJMaXN0KGZpbHRlclRleHQpO1xuICB9O1xuXG4gIC8qKlxuICAgKiBGaWx0ZXIgbGlzdCB0aHJvdWdoIHRleHRcbiAgICogQHBhcmFtIHtTdHJpbmd9IGZpbHRlclRleHRcbiAgICovXG4gIGZpbHRlckxpc3QoZmlsdGVyVGV4dCkge1xuICAgIGNvbnN0ICRpdGVtcyA9IHRoaXMubG9jYWxzLiRpdGVtcztcblxuICAgIGlmIChmaWx0ZXJUZXh0ID09ICdhbGwnKSB7XG4gICAgICAkaXRlbXMucmVtb3ZlQ2xhc3MoJ3N0YXRlX2hpZGRlbicpO1xuICAgICAgcmV0dXJuO1xuICAgIH1cblxuICAgICRpdGVtcy5lYWNoKChpbmRleCwgZWwpID0+IHtcbiAgICAgIGNvbnN0ICRlbCA9ICQoZWwpO1xuICAgICAgY29uc3QgaXNIaWRkZW4gPSAkZWwuZGF0YSgnZmlsdGVyLXRleHQnKS5pbmRleE9mKGZpbHRlclRleHQpID09PSAtMTtcblxuICAgICAgJGVsLnRvZ2dsZUNsYXNzKCdzdGF0ZV9oaWRkZW4nLCBpc0hpZGRlbik7XG4gICAgfSk7XG4gIH07XG5cbiAgLyoqXG4gICAqIFNldCBsaW5rIHRvIGFjdGl2ZSBhbmQgZGVhY3RpdmF0ZSBvdGhlclxuICAgKiBAcGFyYW0ge2pRdWVyeX0gJGVsXG4gICAqL1xuICBzZXRBY3RpdmVMaW5rKCRlbCkge1xuICAgICRlbC5hZGRDbGFzcygnc3RhdGVfc2VsZWN0ZWQnKVxuICAgICAgLnNpYmxpbmdzKCkucmVtb3ZlQ2xhc3MoJ3N0YXRlX3NlbGVjdGVkJyk7XG4gIH07XG59XG5cblxuXG5cbi8qKiBXRUJQQUNLIEZPT1RFUiAqKlxuICoqIC4vZnJvbnRlbmQvanMvcGVyc29uL3dpZGdldHMtY3JlZGl0L19maWx0ZXItaGlzdG9yeS5qc1xuICoqLyIsIid1c2Ugc3RyaWN0JztcblxuaW1wb3J0IFZhbGlkYXRpb24gZnJvbSBcIi4vLi4vLi4vY29tbW9uL19mb3JtLXZhbGlkYXRpb25cIjtcblxuLyoqXG4gKiBGb3JtIGZvciBzZW5kaW5nIGNyZWRpdFxuICovXG5leHBvcnQgZGVmYXVsdCBjbGFzcyBGb3JtQ3JlZGl0IHtcblxuICAvKipcbiAgICogQHBhcmFtIHtTdHJpbmd9IHNlbGVjdG9yXG4gICAqL1xuICBjb25zdHJ1Y3RvcihzZWxlY3Rvcikge1xuICAgIHRoaXMuJHJvb3QgPSAkKHNlbGVjdG9yKTtcbiAgICB0aGlzLmxvY2FscyA9IHRoaXMuX2dldERvbSgpO1xuXG4gICAgdGhpcy5hdmFpbGFiZWxCb251cyA9IHRoaXMubG9jYWxzLiRjb3VudC50ZXh0KCk7XG4gICAgdGhpcy5fYXNzaWduRXZlbnRzKCk7XG4gIH1cblxuICBfZ2V0RG9tKCkge1xuICAgIGNvbnN0ICRyb290ID0gdGhpcy4kcm9vdDtcblxuICAgIHJldHVybiB7XG4gICAgICAkY291bnQ6ICRyb290LmZpbmQoJ1tkYXRhLWNyZWRpY3QtY291bnRdJyksXG4gICAgICAkdmFsdWU6ICRyb290LmZpbmQoJ1tkYXRhLWNyZWRpdC12YWx1ZV0nKSxcbiAgICAgICR0bzogJHJvb3QuZmluZCgnW2RhdGEtY3JlZGl0LXRvXScpLFxuICAgICAgJG1lc3NhZ2U6ICRyb290LmZpbmQoJ1tkYXRhLWNyZWRpdC1tZXNzYWdlXScpLFxuICAgICAgJGVycm9yOiAkcm9vdC5maW5kKCdbZGF0YS1jcmVkaXQtZXJyb3JdJylcbiAgICB9O1xuICB9XG5cbiAgX2Fzc2lnbkV2ZW50cygpIHtcbiAgICB0aGlzLiRyb290XG4gICAgICAub24oJ2lucHV0JywgJ1tkYXRhLWNyZWRpdC12YWx1ZV0sIFtkYXRhLWNyZWRpdC10b10nLCAoKT0+IHRoaXMuX3JlbW92ZUVycm9ycygpKVxuICAgICAgLm9uKCdzdWJtaXQnLCB0aGlzLl9vblN1Ym1pdEZvcm0uYmluZCh0aGlzKSk7XG4gIH1cblxuICBfb25TdWJtaXRGb3JtKGUpIHtcbiAgICBlLnByZXZlbnREZWZhdWx0KCk7XG5cbiAgICBpZiAoIXRoaXMuX2lzRm9ybVZhbGlkKCkpIHJldHVybiBmYWxzZTtcbiAgICB0aGlzLnNlbmRSZXF1ZXN0KCk7XG4gIH1cbn1cbihmdW5jdGlvbiAoJCwgQXBwKSB7XG5cblxuICBGb3JtQ3JlZGl0LnByb3RvdHlwZS5pc0Zvcm1WYWxpZCA9IGZ1bmN0aW9uICgpIHtcbiAgICB2YXIgc2VsZiA9IHRoaXMsXG4gICAgICB2YWxpZCA9IHRydWUsXG4gICAgICBlcnJvclRleHQgPSAnJztcblxuICAgIGlmICghc2VsZi5sb2NhbHMuJHZhbHVlLnZhbCgpKSB7XG4gICAgICB2YWxpZCA9IGZhbHNlO1xuICAgICAgZXJyb3JUZXh0ICs9ICdHaXZlIHZhbHVlIGlzIGVtcHR5LiAnO1xuICAgICAgc2VsZi5zZXRFcnJvcihzZWxmLmxvY2Fscy4kdmFsdWUpO1xuICAgIH1cblxuICAgIGlmICgrc2VsZi5sb2NhbHMuJHZhbHVlLnZhbCgpID4gKCtzZWxmLmxvY2Fscy4kY291bnQudGV4dCgpKSkge1xuICAgICAgdmFsaWQgPSBmYWxzZTtcbiAgICAgIGVycm9yVGV4dCArPSAnWW91IGNhbm7igJl0IGdpdmUgbW9yZSB0aGFuICcgKyBzZWxmLmxvY2Fscy4kY291bnQudGV4dCgpICsgJyBjcmVkaXRzLiAnO1xuICAgICAgc2VsZi5zZXRFcnJvcihzZWxmLmxvY2Fscy4kdmFsdWUpO1xuICAgIH1cblxuICAgIGlmICghc2VsZi5sb2NhbHMuJHRvLnZhbCgpKSB7XG4gICAgICB2YWxpZCA9IGZhbHNlO1xuICAgICAgZXJyb3JUZXh0ICs9ICdFbWFpbCBpcyBlbXB0eS4gJztcbiAgICAgIHNlbGYuc2V0RXJyb3Ioc2VsZi5sb2NhbHMuJHRvKTtcbiAgICB9XG5cblxuXG4gICAgaWYgKGVycm9yKSB7XG4gICAgICBzZWxmLmxvY2Fscy4kZXJyb3IudGV4dChlcnJvclRleHQpO1xuICAgIH1cblxuICAgIHJldHVybiB2YWxpZDtcbiAgfTtcblxuXG5cblxuICBGb3JtQ3JlZGl0LnByb3RvdHlwZS5zZW5kUmVxdWVzdCA9IGZ1bmN0aW9uICgpIHtcbiAgICB2YXIgc2VsZiA9IHRoaXMsXG4gICAgICBsb2NhbHMgPSBzZWxmLmxvY2FscztcblxuICAgICQucG9zdChcbiAgICAgIHNlbGYuJHJvb3QuYXR0cignYWN0aW9uJyksXG4gICAgICB7XG4gICAgICAgIHZhbHVlOiBsb2NhbHMuJHZhbHVlLnZhbCgpLFxuICAgICAgICB0bzogbG9jYWxzLiR0by52YWwoKSxcbiAgICAgICAgbWVzc2FnZTogbG9jYWxzLiRtZXNzYWdlLnZhbCgpLFxuICAgICAgfSwgZnVuY3Rpb24gKCkge1xuICAgICAgICBzZWxmLiRyb290LmNsZWFyRm9ybSgpO1xuICAgICAgICBzZWxmLiRyb290LmFkZENsYXNzKCdwZWVyLWNyZWRpdF9zdGF0ZV9zZW5kJyk7XG5cbiAgICAgICAgc2V0VGltZW91dChmdW5jdGlvbiAoKSB7XG4gICAgICAgICAgc2VsZi4kcm9vdC5yZW1vdmVDbGFzcygncGVlci1jcmVkaXRfc3RhdGVfc2VuZCcpO1xuICAgICAgICB9LCAzMDAwKVxuICAgICAgfVxuICAgICk7XG4gIH07XG5cbiAgQXBwLndpZGdldHMuRm9ybUNyZWRpdCA9IEZvcm1DcmVkaXQ7XG5cbn0pKGpRdWVyeSwgQXBwKTtcblxuXG4vKiogV0VCUEFDSyBGT09URVIgKipcbiAqKiAuL2Zyb250ZW5kL2pzL3BlcnNvbi93aWRnZXRzLWNyZWRpdC9fc2VuZC1jcmVkaXRzLmpzXG4gKiovIiwiJ3VzZSBzdHJpY3QnO1xuXG5leHBvcnQgZGVmYXVsdCBjbGFzcyBGb3JtVmFsaWRhdGlvbntcbiAgLyoqXG4gICAqIFZhbGlkYXRlIGZvcm0gdGhyb3VnaCBpbnB1dHNcbiAgICogQHBhcmFtIHtqUXVlcnl9ICRpbnB1dHNcbiAgICovXG4gIGNvbnN0cnVjdG9yKCRpbnB1dHMpe1xuICAgIHRoaXMuJGlucHV0cyA9ICRpbnB1dHM7XG4gICAgdGhpcy5fYXNzaWduRXZlbnRzKCk7XG4gIH1cblxuICBfYXNzaWduRXZlbnRzKCkge1xuICAgIHRoaXMuJGlucHV0cy5vbignaW5wdXQnLCAoZSkgPT4gdGhpcy5yZW1vdmVFcnJvcigkKGUudGFyZ2V0KSkpO1xuICB9XG5cbiAgaXNWYWxpZElucHV0cygpIHtcbiAgICBjb25zdCAkaW5wdXRzID0gdGhpcy4kaW5wdXRzO1xuICAgIGxldCBlcnJvciA9IDA7XG5cbiAgICAkaW5wdXRzLmVhY2goIChpbmRleCwgaW5wdXQpID0+e1xuICAgICAgY29uc3QgJGlucHV0ID0gJChpbnB1dCk7XG5cbiAgICAgIGlmICghdGhpcy5faXNWYWxpZElucHV0KCRpbnB1dCkpIGVycm9yICs9IDE7XG4gICAgfSk7XG4gICAgcmV0dXJuIEJvb2xlYW4oIWVycm9yKTtcbiAgfVxuXG4gIC8qKlxuICAgKiBDaGVjayBnaXZlbiBpbnB1dCwgaXMgaXQgdmFsaWQ/XG4gICAqIEBwYXJhbSB7alF1ZXJ5fSAkaW5wdXRcbiAgICogQHJldHVybnMge2Jvb2xlYW59IC0gSXMgdmFsaWQgaW5wdXQ/XG4gICAqL1xuICBfaXNWYWxpZElucHV0KCRpbnB1dCkge1xuICAgIGNvbnN0IHZhbHVlID0gJC50cmltKCRpbnB1dC52YWwoKSk7XG5cbiAgICBpZiAoIXZhbHVlKSB7XG4gICAgICB0aGlzLl9zZXRFcnJvcigkaW5wdXQsICdFbXB0eScpO1xuICAgICAgcmV0dXJuIGZhbHNlO1xuICAgIH1cblxuICAgIGlmICgoJGlucHV0Lmhhc0NsYXNzKCd0eXBlLWVtYWlsJykpICYmICF0aGlzLl9pc1ZhbGlkRW1haWwodmFsdWUpKSB7XG4gICAgICB0aGlzLl9zZXRFcnJvcigkaW5wdXQsICdFbWFpbCBpcyBub3QgdmFsaWQnKTtcbiAgICAgIHJldHVybiBmYWxzZTtcbiAgICB9XG4gICAgcmV0dXJuIHRydWU7XG4gIH1cblxuICAvKipcbiAgICogSXMgRW1haWwgdmFsaWQ/XG4gICAqIEBwYXJhbSB7c3RyaW5nfSBlbWFpbFxuICAgKiBAcmV0dXJucyB7Ym9vbGVhbn1cbiAgICovXG4gIF9pc1ZhbGlkRW1haWwgKGVtYWlsKSB7XG4gICAgdmFyIHJlID0gL14oKFtePD4oKVxcW1xcXVxcXFwuLDs6XFxzQFwiXSsoXFwuW148PigpXFxbXFxdXFxcXC4sOzpcXHNAXCJdKykqKXwoXCIuK1wiKSlAKChcXFtbMC05XXsxLDN9XFwuWzAtOV17MSwzfVxcLlswLTldezEsM31cXC5bMC05XXsxLDN9XSl8KChbYS16QS1aXFwtMC05XStcXC4pK1thLXpBLVpdezIsfSkpJC87XG4gICAgcmV0dXJuIHJlLnRlc3QoZW1haWwpO1xuICB9XG5cbiAgLyoqXG4gICAqIFNldCBlcnJvciBmb3IgaW5wdXRcbiAgICogQHBhcmFtIHtqUXVlcnl9ICRpbnB1dFxuICAgKiBAcGFyYW0ge3N0cmluZ30gZXJyb3JUZXh0XG4gICAqL1xuICBfc2V0RXJyb3IoJGlucHV0LCBlcnJvclRleHQpIHtcbiAgICBjb25zdCAkcGFyZW50ID0gJGlucHV0LnBhcmVudCgpO1xuICAgIGNvbnN0ICRlcnJvciA9ICRwYXJlbnQuZmluZCgnLmItZXJyb3InKTtcblxuICAgIGlmICgkZXJyb3IubGVuZ3RoKSByZXR1cm47XG5cbiAgICAkcGFyZW50LmFkZENsYXNzKCdiLWVycm9yX3Nob3cnKTtcbiAgICAkKCc8ZGl2IGNsYXNzPVwiYi1lcnJvclwiIC8+JylcbiAgICAgIC50ZXh0KGVycm9yVGV4dClcbiAgICAgIC5wcmVwZW5kVG8oJHBhcmVudCk7XG4gIH1cblxuICAvKipcbiAgICogUmVtb3ZlIGVycm9yIGZvciBpbnB1dFxuICAgKiBAcGFyYW0ge2pRdWVyeX0gJGlucHV0XG4gICAqL1xuICBfcmVtb3ZlRXJyb3IoJGlucHV0KSB7XG4gICAgY29uc3QgJHBhcmVudCA9ICRpbnB1dC5wYXJlbnQoKTtcbiAgICBjb25zdCAkZXJyb3IgPSAkcGFyZW50LmZpbmQoJ2ItZXJyb3InKTtcblxuICAgICRwYXJlbnRcbiAgICAgIC5yZW1vdmVDbGFzcygnYi1lcnJvcl9zaG93JylcbiAgICAgIC5maW5kKCcuYi1lcnJvcicpLnJlbW92ZSgpO1xuICB9XG5cbiAgLyoqXG4gICAqIFNldCBlcnJvcnNcbiAgICogQHBhcmFtIHtBcnJheX0gZXJyb3JzIC0gW3tuYW1lOiBcImVtYWlsXCIsIGVycm9yOiBcImVtcHR5XCJ9LCB7bmFtZTogXCJwYXNzd29yZFwiLCBlcnJvcjogXCJlbXB0eVwifV1cbiAgICovXG4gIHNldEVycm9ycyhlcnJvcnMpIHtcbiAgICBlcnJvcnMuZm9yRWFjaCggKGl0ZW0pID0+IHtcbiAgICAgIGNvbnN0ICRjdXJyZW50SW5wdXQgPSB0aGlzLiRpbnB1dHMuZmlsdGVyKCdbbmFtZT1cIicgKyBpdGVtLm5hbWUgKyAnXCJdJykuZmlyc3QoKTtcblxuICAgICAgaWYgKCRjdXJyZW50SW5wdXQubGVuZ3RoKSB0aGlzLl9zZXRFcnJvcigkY3VycmVudElucHV0LCBpdGVtLmVycm9yKVxuICAgIH0pXG4gIH1cblxuICAvKipcbiAgICogUmVtb3ZlIGFsbCBlcnJvcnNcbiAgICovXG4gIHJlbW92ZUVycm9ycygpIHtcbiAgICB0aGlzLiRpbnB1dHMuZWFjaCggKGluZGV4LCBlbCkgPT4ge1xuICAgICAgY29uc3QgJGVsID0gJChlbCk7XG4gICAgICB0aGlzLl9yZW1vdmVFcnJvcigkZWwpXG4gICAgfSlcbiAgfVxuXG4gIGNsZWFyRm9ybSgpIHtcbiAgICB0aGlzLiRpbnB1dHMuZWFjaCggKGluZGV4LCBlbCkgPT4ge1xuICAgICAgY29uc3QgJGVsID0gJChlbCk7XG4gICAgICBpZiAoISRlbC5hdHRyKFwiZGlzYWJsZWRcIikpICAkZWwudmFsKCcnKTtcbiAgICB9KVxuICB9XG59XG5cblxuXG4vKiogV0VCUEFDSyBGT09URVIgKipcbiAqKiAuL2Zyb250ZW5kL2pzL2NvbW1vbi9fZm9ybS12YWxpZGF0aW9uLmpzXG4gKiovIl0sInNvdXJjZVJvb3QiOiIifQ==