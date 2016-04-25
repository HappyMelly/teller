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

	module.exports = __webpack_require__(58);


/***/ },
/* 1 */,
/* 2 */,
/* 3 */
/***/ function(module, exports) {

	"use strict";
	
	exports.__esModule = true;
	
	exports.default = function (instance, Constructor) {
	  if (!(instance instanceof Constructor)) {
	    throw new TypeError("Cannot call a class as a function");
	  }
	};

/***/ },
/* 4 */
/***/ function(module, exports, __webpack_require__) {

	"use strict";
	
	exports.__esModule = true;
	
	var _defineProperty = __webpack_require__(5);
	
	var _defineProperty2 = _interopRequireDefault(_defineProperty);
	
	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }
	
	exports.default = function () {
	  function defineProperties(target, props) {
	    for (var i = 0; i < props.length; i++) {
	      var descriptor = props[i];
	      descriptor.enumerable = descriptor.enumerable || false;
	      descriptor.configurable = true;
	      if ("value" in descriptor) descriptor.writable = true;
	      (0, _defineProperty2.default)(target, descriptor.key, descriptor);
	    }
	  }
	
	  return function (Constructor, protoProps, staticProps) {
	    if (protoProps) defineProperties(Constructor.prototype, protoProps);
	    if (staticProps) defineProperties(Constructor, staticProps);
	    return Constructor;
	  };
	}();

/***/ },
/* 5 */
/***/ function(module, exports, __webpack_require__) {

	module.exports = { "default": __webpack_require__(6), __esModule: true };

/***/ },
/* 6 */
/***/ function(module, exports, __webpack_require__) {

	__webpack_require__(7);
	var $Object = __webpack_require__(10).Object;
	module.exports = function defineProperty(it, key, desc){
	  return $Object.defineProperty(it, key, desc);
	};

/***/ },
/* 7 */
/***/ function(module, exports, __webpack_require__) {

	var $export = __webpack_require__(8);
	// 19.1.2.4 / 15.2.3.6 Object.defineProperty(O, P, Attributes)
	$export($export.S + $export.F * !__webpack_require__(18), 'Object', {defineProperty: __webpack_require__(14).f});

/***/ },
/* 8 */
/***/ function(module, exports, __webpack_require__) {

	var global    = __webpack_require__(9)
	  , core      = __webpack_require__(10)
	  , ctx       = __webpack_require__(11)
	  , hide      = __webpack_require__(13)
	  , PROTOTYPE = 'prototype';
	
	var $export = function(type, name, source){
	  var IS_FORCED = type & $export.F
	    , IS_GLOBAL = type & $export.G
	    , IS_STATIC = type & $export.S
	    , IS_PROTO  = type & $export.P
	    , IS_BIND   = type & $export.B
	    , IS_WRAP   = type & $export.W
	    , exports   = IS_GLOBAL ? core : core[name] || (core[name] = {})
	    , expProto  = exports[PROTOTYPE]
	    , target    = IS_GLOBAL ? global : IS_STATIC ? global[name] : (global[name] || {})[PROTOTYPE]
	    , key, own, out;
	  if(IS_GLOBAL)source = name;
	  for(key in source){
	    // contains in native
	    own = !IS_FORCED && target && target[key] !== undefined;
	    if(own && key in exports)continue;
	    // export native or passed
	    out = own ? target[key] : source[key];
	    // prevent global pollution for namespaces
	    exports[key] = IS_GLOBAL && typeof target[key] != 'function' ? source[key]
	    // bind timers to global for call from export context
	    : IS_BIND && own ? ctx(out, global)
	    // wrap global constructors for prevent change them in library
	    : IS_WRAP && target[key] == out ? (function(C){
	      var F = function(a, b, c){
	        if(this instanceof C){
	          switch(arguments.length){
	            case 0: return new C;
	            case 1: return new C(a);
	            case 2: return new C(a, b);
	          } return new C(a, b, c);
	        } return C.apply(this, arguments);
	      };
	      F[PROTOTYPE] = C[PROTOTYPE];
	      return F;
	    // make static versions for prototype methods
	    })(out) : IS_PROTO && typeof out == 'function' ? ctx(Function.call, out) : out;
	    // export proto methods to core.%CONSTRUCTOR%.methods.%NAME%
	    if(IS_PROTO){
	      (exports.virtual || (exports.virtual = {}))[key] = out;
	      // export proto methods to core.%CONSTRUCTOR%.prototype.%NAME%
	      if(type & $export.R && expProto && !expProto[key])hide(expProto, key, out);
	    }
	  }
	};
	// type bitmap
	$export.F = 1;   // forced
	$export.G = 2;   // global
	$export.S = 4;   // static
	$export.P = 8;   // proto
	$export.B = 16;  // bind
	$export.W = 32;  // wrap
	$export.U = 64;  // safe
	$export.R = 128; // real proto method for `library` 
	module.exports = $export;

/***/ },
/* 9 */
/***/ function(module, exports) {

	// https://github.com/zloirock/core-js/issues/86#issuecomment-115759028
	var global = module.exports = typeof window != 'undefined' && window.Math == Math
	  ? window : typeof self != 'undefined' && self.Math == Math ? self : Function('return this')();
	if(typeof __g == 'number')__g = global; // eslint-disable-line no-undef

/***/ },
/* 10 */
/***/ function(module, exports) {

	var core = module.exports = {version: '2.2.0'};
	if(typeof __e == 'number')__e = core; // eslint-disable-line no-undef

/***/ },
/* 11 */
/***/ function(module, exports, __webpack_require__) {

	// optional / simple context binding
	var aFunction = __webpack_require__(12);
	module.exports = function(fn, that, length){
	  aFunction(fn);
	  if(that === undefined)return fn;
	  switch(length){
	    case 1: return function(a){
	      return fn.call(that, a);
	    };
	    case 2: return function(a, b){
	      return fn.call(that, a, b);
	    };
	    case 3: return function(a, b, c){
	      return fn.call(that, a, b, c);
	    };
	  }
	  return function(/* ...args */){
	    return fn.apply(that, arguments);
	  };
	};

/***/ },
/* 12 */
/***/ function(module, exports) {

	module.exports = function(it){
	  if(typeof it != 'function')throw TypeError(it + ' is not a function!');
	  return it;
	};

/***/ },
/* 13 */
/***/ function(module, exports, __webpack_require__) {

	var dP         = __webpack_require__(14)
	  , createDesc = __webpack_require__(22);
	module.exports = __webpack_require__(18) ? function(object, key, value){
	  return dP.f(object, key, createDesc(1, value));
	} : function(object, key, value){
	  object[key] = value;
	  return object;
	};

/***/ },
/* 14 */
/***/ function(module, exports, __webpack_require__) {

	var anObject       = __webpack_require__(15)
	  , IE8_DOM_DEFINE = __webpack_require__(17)
	  , toPrimitive    = __webpack_require__(21)
	  , dP             = Object.defineProperty;
	
	exports.f = __webpack_require__(18) ? Object.defineProperty : function defineProperty(O, P, Attributes){
	  anObject(O);
	  P = toPrimitive(P, true);
	  anObject(Attributes);
	  if(IE8_DOM_DEFINE)try {
	    return dP(O, P, Attributes);
	  } catch(e){ /* empty */ }
	  if('get' in Attributes || 'set' in Attributes)throw TypeError('Accessors not supported!');
	  if('value' in Attributes)O[P] = Attributes.value;
	  return O;
	};

/***/ },
/* 15 */
/***/ function(module, exports, __webpack_require__) {

	var isObject = __webpack_require__(16);
	module.exports = function(it){
	  if(!isObject(it))throw TypeError(it + ' is not an object!');
	  return it;
	};

/***/ },
/* 16 */
/***/ function(module, exports) {

	module.exports = function(it){
	  return typeof it === 'object' ? it !== null : typeof it === 'function';
	};

/***/ },
/* 17 */
/***/ function(module, exports, __webpack_require__) {

	module.exports = !__webpack_require__(18) && !__webpack_require__(19)(function(){
	  return Object.defineProperty(__webpack_require__(20)('div'), 'a', {get: function(){ return 7; }}).a != 7;
	});

/***/ },
/* 18 */
/***/ function(module, exports, __webpack_require__) {

	// Thank's IE8 for his funny defineProperty
	module.exports = !__webpack_require__(19)(function(){
	  return Object.defineProperty({}, 'a', {get: function(){ return 7; }}).a != 7;
	});

/***/ },
/* 19 */
/***/ function(module, exports) {

	module.exports = function(exec){
	  try {
	    return !!exec();
	  } catch(e){
	    return true;
	  }
	};

/***/ },
/* 20 */
/***/ function(module, exports, __webpack_require__) {

	var isObject = __webpack_require__(16)
	  , document = __webpack_require__(9).document
	  // in old IE typeof document.createElement is 'object'
	  , is = isObject(document) && isObject(document.createElement);
	module.exports = function(it){
	  return is ? document.createElement(it) : {};
	};

/***/ },
/* 21 */
/***/ function(module, exports, __webpack_require__) {

	// 7.1.1 ToPrimitive(input [, PreferredType])
	var isObject = __webpack_require__(16);
	// instead of the ES6 spec version, we didn't implement @@toPrimitive case
	// and the second argument - flag - preferred type is a string
	module.exports = function(it, S){
	  if(!isObject(it))return it;
	  var fn, val;
	  if(S && typeof (fn = it.toString) == 'function' && !isObject(val = fn.call(it)))return val;
	  if(typeof (fn = it.valueOf) == 'function' && !isObject(val = fn.call(it)))return val;
	  if(!S && typeof (fn = it.toString) == 'function' && !isObject(val = fn.call(it)))return val;
	  throw TypeError("Can't convert object to primitive value");
	};

/***/ },
/* 22 */
/***/ function(module, exports) {

	module.exports = function(bitmap, value){
	  return {
	    enumerable  : !(bitmap & 1),
	    configurable: !(bitmap & 2),
	    writable    : !(bitmap & 4),
	    value       : value
	  };
	};

/***/ },
/* 23 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';
	
	/**
	 * Errors
	 * @typedef {Object} ListErrors
	 * @property {String} name - name of field
	 * @property {String} error - error description
	 */
	
	Object.defineProperty(exports, "__esModule", {
	    value: true
	});
	
	var _classCallCheck2 = __webpack_require__(3);
	
	var _classCallCheck3 = _interopRequireDefault(_classCallCheck2);
	
	var _createClass2 = __webpack_require__(4);
	
	var _createClass3 = _interopRequireDefault(_createClass2);
	
	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }
	
	var FormHelper = function () {
	    /**
	     * Validate form through inputs
	     * @param {jQuery} $controls
	     */
	
	    function FormHelper($controls) {
	        (0, _classCallCheck3.default)(this, FormHelper);
	
	        this.$controls = $controls;
	        this.arrErrors = [];
	        this._assignEvents();
	    }
	
	    (0, _createClass3.default)(FormHelper, [{
	        key: '_assignEvents',
	        value: function _assignEvents() {
	            var _this = this;
	
	            this.$controls.on('input change', function (e) {
	                var $control = $(e.currentTarget);
	
	                _this._validateImmediate($control);
	                _this._removeError($control);
	            });
	        }
	    }, {
	        key: '_validateImmediate',
	        value: function _validateImmediate($control) {
	            if ($control.hasClass('type-numeric')) {
	                $control.val($control.val().replace(/[^\d]+/g, ''));
	            }
	
	            if ($control.hasClass('type-nospace')) {
	                $control.val($control.val().replace(/\s/g, ''));
	            }
	        }
	    }, {
	        key: 'isValidInputs',
	        value: function isValidInputs() {
	            var _this2 = this;
	
	            var $controls = this.$controls;
	            var error = 0;
	
	            $controls.each(function (index, control) {
	                var $control = $(control);
	
	                if (!_this2._isValidInput($control)) {
	                    error += 1;
	                }
	            });
	            return Boolean(!error);
	        }
	
	        /**
	         * Check given control, is it valid?
	         * @param {jQuery} $control
	         * @returns {boolean} - Is valid control?
	         */
	
	    }, {
	        key: '_isValidInput',
	        value: function _isValidInput($control) {
	            var value = $.trim($control.val());
	
	            if (!value && !$control.hasClass('type-optional')) {
	                this._setError($control, 'Empty');
	                return false;
	            }
	
	            if ($control.hasClass('type-email') && !this._isValidEmail(value)) {
	                this._setError($control, 'Email is not valid');
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
	         * Set error for control
	         * @param {jQuery} $control
	         * @param {String} errorText
	         * @param {Boolean} insertError
	         */
	
	    }, {
	        key: '_setError',
	        value: function _setError($control, errorText) {
	            var insertError = arguments.length <= 2 || arguments[2] === undefined ? true : arguments[2];
	
	            var $parent = $control.parent();
	            var $error = $parent.find('.b-error');
	
	            if ($error.length) return;
	
	            $parent.addClass('b-error_show');
	
	            insertError && $('<div class="b-error" />').text(errorText).appendTo($parent);
	
	            this.arrErrors.push({
	                name: $control.attr('name'),
	                error: errorText
	            });
	        }
	
	        /**
	         * Remove error for control
	         * @param {jQuery} $control
	         */
	
	    }, {
	        key: '_removeError',
	        value: function _removeError($control) {
	            var $parent = $control.parent();
	
	            $parent.removeClass('b-error_show').find('.b-error').remove();
	
	            this.arrErrors = this.arrErrors.filter(function (item) {
	                return item.name !== $control.attr('name');
	            });
	        }
	
	        /**
	         * Set errors
	         * @param {Array} errors - [{name: "email", error: "empty"}, {name: "password", error: "empty"}]
	         * @param {Boolean} insertError - insert error description to the Dom 
	         */
	
	    }, {
	        key: 'setErrors',
	        value: function setErrors(errors) {
	            var _this3 = this;
	
	            var insertError = arguments.length <= 1 || arguments[1] === undefined ? true : arguments[1];
	
	            errors.forEach(function (item) {
	                var $currentControl = _this3.$controls.filter('[name="' + item.name + '"]').first();
	
	                if ($currentControl.length) _this3._setError($currentControl, item.error, insertError);
	            });
	        }
	
	        /**
	         * Get text version of errors in one line.
	         * @param {ListErrors} errors
	         * @returns {string}
	         */
	
	    }, {
	        key: 'getErrorsText',
	        value: function getErrorsText(errors) {
	            var arrErrors = errors || this.arrErrors;
	            var errorTxt = '';
	
	            arrErrors.forEach(function (item) {
	                var name = item.name[0].toUpperCase() + item.name.substr(1);
	
	                errorTxt += name + ': ' + item.error + '. ';
	            });
	
	            return errorTxt;
	        }
	
	        /**
	         * Get list of errors with full title (from control title attribute)
	         * @param {ListErrors} errors - list of errors
	         * @returns {string}
	         */
	
	    }, {
	        key: 'getErrorsFull',
	        value: function getErrorsFull(errors) {
	            var self = this;
	            var arrErrors = errors || this.arrErrors;
	            var errorTxt = '';
	
	            arrErrors.forEach(function (item) {
	                var $control = self.$controls.filter('[name="' + item.name + '"]').first();
	                var name = $control.length ? $control.attr('title') : item.name;
	
	                errorTxt += '<b>' + name + '</b>: ' + item.error + '.  <br><br>';
	            });
	
	            return errorTxt;
	        }
	    }, {
	        key: 'getFormData',
	        value: function getFormData() {
	            var ajaxData = {};
	
	            this.$controls.map(function (index, el) {
	                var $el = $(el);
	                var name = $el.attr('name');
	
	                if (!name) return;
	
	                if ($el.is(':checkbox')) {
	                    ajaxData[name] = $el.prop('checked');
	                } else {
	                    ajaxData[name] = $el.val();
	                }
	            });
	
	            return ajaxData;
	        }
	
	        /**
	         * Remove all errors
	         */
	
	    }, {
	        key: 'removeErrors',
	        value: function removeErrors() {
	            var _this4 = this;
	
	            this.$controls.each(function (index, el) {
	                var $el = $(el);
	                _this4._removeError($el);
	            });
	        }
	    }, {
	        key: 'clearForm',
	        value: function clearForm() {
	            this.$controls.each(function (index, el) {
	                var $el = $(el);
	                if (!$el.attr("disabled")) $el.val('');
	            });
	        }
	    }]);
	    return FormHelper;
	}();

	exports.default = FormHelper;

/***/ },
/* 24 */,
/* 25 */,
/* 26 */,
/* 27 */,
/* 28 */,
/* 29 */,
/* 30 */,
/* 31 */,
/* 32 */,
/* 33 */,
/* 34 */,
/* 35 */,
/* 36 */,
/* 37 */,
/* 38 */,
/* 39 */,
/* 40 */,
/* 41 */,
/* 42 */,
/* 43 */,
/* 44 */,
/* 45 */,
/* 46 */,
/* 47 */,
/* 48 */,
/* 49 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';
	
	Object.defineProperty(exports, "__esModule", {
	    value: true
	});
	
	var _classCallCheck2 = __webpack_require__(3);
	
	var _classCallCheck3 = _interopRequireDefault(_classCallCheck2);
	
	var _createClass2 = __webpack_require__(4);
	
	var _createClass3 = _interopRequireDefault(_createClass2);
	
	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }
	
	var InputChecking = function () {
	    /**
	     * Validate input through backend url
	     */
	
	    function InputChecking(data) {
	        (0, _classCallCheck3.default)(this, InputChecking);
	
	        this.$root = data.$root;
	        this.url = data.url;
	        this.locals = this._getDom();
	
	        this._checkValue();
	        this._assignEvents();
	    }
	
	    (0, _createClass3.default)(InputChecking, [{
	        key: '_getDom',
	        value: function _getDom() {
	            var $root = this.$root;
	
	            return {
	                $content: $root.find('[data-inputcheck-block]'),
	                $input: $root.find('input'),
	                $error: $root.find('[data-inputcheck-error]'),
	                $successText: $root.find('[data-inputcheck-text]')
	            };
	        }
	    }, {
	        key: '_assignEvents',
	        value: function _assignEvents() {
	            this.locals.$input.on('blur', this._checkValue.bind(this)).on('focus', this._hideCheckingError.bind(this));
	        }
	    }, {
	        key: '_checkValue',
	        value: function _checkValue() {
	            var self = this;
	            var locals = self.locals;
	            var valueInput = locals.$input.val();
	
	            if (!locals.$input.val()) {
	                return;
	            }
	
	            this.$root.removeClass('b-inputcheck_state_complete b-inputcheck_state_error').addClass('b-inputcheck_state_checking');
	
	            self._sendCheck(valueInput).done(function (response) {
	                var successText = $.parseJSON(response).message;
	                self._completeChecking(successText);
	            }).fail(function (response) {
	                var error = $.parseJSON(response.responseText).message;
	                self._showCheckingError(error);
	            });
	        }
	
	        /**
	         * Show error when input value is invalid
	         * @param {String} error
	         * @private
	         */
	
	    }, {
	        key: '_showCheckingError',
	        value: function _showCheckingError(error) {
	            var locals = this.locals;
	            this.valid = false;
	
	            this.$root.removeClass('b-inputcheck_state_checking').addClass('b-inputcheck_state_error');
	
	            this.$root.trigger('input_checking.change');
	            locals.$error.text(error);
	        }
	
	        /**
	         * Show success text after checking
	         * @param {String} success
	         * @private
	         */
	
	    }, {
	        key: '_completeChecking',
	        value: function _completeChecking(success) {
	            var locals = this.locals;
	            this.valid = true;
	
	            this.$root.removeClass('b-inputcheck_state_checking').addClass('b-inputcheck_state_complete');
	
	            this.$root.trigger('input_checking.change');
	            locals.$successText.text(success);
	        }
	    }, {
	        key: '_hideCheckingError',
	        value: function _hideCheckingError() {
	            this.valid = false;
	            this.$root.removeClass('b-inputcheck_state_error');
	        }
	    }, {
	        key: 'isValid',
	        value: function isValid() {
	            return this.valid;
	        }
	
	        //transport
	
	    }, {
	        key: '_sendCheck',
	        value: function _sendCheck(value) {
	            return $.get(this.url(value).url);
	        }
	    }]);
	    return InputChecking;
	}();

	exports.default = InputChecking;

/***/ },
/* 50 */,
/* 51 */,
/* 52 */,
/* 53 */,
/* 54 */,
/* 55 */,
/* 56 */,
/* 57 */,
/* 58 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';
	
	var _organizationForm = __webpack_require__(59);
	
	var _organizationForm2 = _interopRequireDefault(_organizationForm);
	
	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }
	
	$(function () {
	    _organizationForm2.default.plugin('.js-org-form');
	});

/***/ },
/* 59 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';
	
	Object.defineProperty(exports, "__esModule", {
	    value: true
	});
	
	var _classCallCheck2 = __webpack_require__(3);
	
	var _classCallCheck3 = _interopRequireDefault(_classCallCheck2);
	
	var _createClass2 = __webpack_require__(4);
	
	var _createClass3 = _interopRequireDefault(_createClass2);
	
	var _inputChecking = __webpack_require__(49);
	
	var _inputChecking2 = _interopRequireDefault(_inputChecking);
	
	var _formHelper = __webpack_require__(23);
	
	var _formHelper2 = _interopRequireDefault(_formHelper);
	
	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }
	
	var Widget = function () {
	    /**
	     * @param {String} selector
	     */
	
	    function Widget(selector) {
	        (0, _classCallCheck3.default)(this, Widget);
	
	        this.$root = $(selector);
	        this.locals = this._getDom();
	        this._correctOldVatInput();
	
	        this.inputVAT = new _inputChecking2.default({
	            $root: this.$root.find('.b-inputcheck'),
	            url: jsRoutes.controllers.Utilities.validateVAT
	        });
	        this.formHelper = new _formHelper2.default(this.locals.$vatInput);
	
	        this._assignEvents();
	    }
	
	    (0, _createClass3.default)(Widget, [{
	        key: '_correctOldVatInput',
	        value: function _correctOldVatInput() {
	            var $vatInput = this.locals.$vatInput;
	
	            $vatInput.addClass('type-nospace').val($vatInput.val().replace(/\s/g, ''));
	        }
	    }, {
	        key: '_getDom',
	        value: function _getDom() {
	            var $root = this.$root;
	
	            return {
	                $cancel: $root.find('[data-form-cancel]'),
	                $submit: $root.find('[data-form-submit]'),
	                $vatInput: $root.find('.b-inputcheck input').first()
	            };
	        }
	    }, {
	        key: '_assignEvents',
	        value: function _assignEvents() {
	            this.$root.on('input_checking.change', this._onBlurVatInput.bind(this));
	        }
	    }, {
	        key: '_onBlurVatInput',
	        value: function _onBlurVatInput() {
	            if (this.inputVAT.isValid()) {
	                this._enabledForm();
	            } else {
	                this._disabledForm();
	            }
	        }
	    }, {
	        key: '_disabledForm',
	        value: function _disabledForm() {
	            this.locals.$submit.prop('disabled', true);
	        }
	    }, {
	        key: '_enabledForm',
	        value: function _enabledForm() {
	            this.locals.$submit.prop('disabled', false);
	        }
	
	        // static
	
	    }], [{
	        key: 'plugin',
	        value: function plugin(selector) {
	            var $elems = $(selector);
	            if (!$elems.length) return;
	
	            return $elems.each(function (index, el) {
	                var $element = $(el);
	                var data = $element.data('widget');
	
	                if (!data) {
	                    data = new Widget(el);
	                    $element.data('widget', data);
	                }
	            });
	        }
	    }]);
	    return Widget;
	}();

	exports.default = Widget;

/***/ }
/******/ ]);
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIndlYnBhY2s6Ly8vd2VicGFjay9ib290c3RyYXAgZjBmMTY2OTBiZDk3YTI1NWNmYjk/N2VmNCoqKioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvaGVscGVycy9jbGFzc0NhbGxDaGVjay5qcz8yMWFmKioqKioqKioqIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9oZWxwZXJzL2NyZWF0ZUNsYXNzLmpzPzFkZmUqKioqKioqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL2NvcmUtanMvb2JqZWN0L2RlZmluZS1wcm9wZXJ0eS5qcz81ZjcwKioqKioqKioqIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9mbi9vYmplY3QvZGVmaW5lLXByb3BlcnR5LmpzPzRkMzMqKioqKioqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvZXM2Lm9iamVjdC5kZWZpbmUtcHJvcGVydHkuanM/OGJkZSoqKioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZXhwb3J0LmpzPzhlNDAqKioqKioqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2dsb2JhbC5qcz8zYzUyKioqKioqKioqIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19jb3JlLmpzP2Q2MTEqKioqKioqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2N0eC5qcz80ZTU5KioqKioqKioqIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19hLWZ1bmN0aW9uLmpzPzA2OTkqKioqKioqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2hpZGUuanM/MGQyZSoqKioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fb2JqZWN0LWRwLmpzPzU5ODYqKioqKioqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2FuLW9iamVjdC5qcz8zYWYyKioqKioqKioqIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19pcy1vYmplY3QuanM/Y2ZkYSoqKioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9faWU4LWRvbS1kZWZpbmUuanM/YjEwMioqKioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZGVzY3JpcHRvcnMuanM/YzBmNSoqKioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZmFpbHMuanM/YzZkZCoqKioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZG9tLWNyZWF0ZS5qcz82MTJmKioqKioqKioqIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL190by1wcmltaXRpdmUuanM/MWE2NSoqKioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fcHJvcGVydHktZGVzYy5qcz8yNTZiKioqKioqKioqIiwid2VicGFjazovLy8uL2Zyb250ZW5kL2pzL2NvbW1vbi9fZm9ybS1oZWxwZXIuanM/ODYzNioqKioiLCJ3ZWJwYWNrOi8vLy4vZnJvbnRlbmQvanMvY29tbW9uL19pbnB1dC1jaGVja2luZy5qcz82MWMzIiwid2VicGFjazovLy8uL2Zyb250ZW5kL2pzL29yZ2FuaXphdGlvbi9vcmdhbml6YXRpb24tZm9ybS5qcyIsIndlYnBhY2s6Ly8vLi9mcm9udGVuZC9qcy9vcmdhbml6YXRpb24vZm9ybS13aWRnZXRzL19vcmdhbml6YXRpb24tZm9ybS5qcyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiO0FBQUE7QUFDQTs7QUFFQTtBQUNBOztBQUVBO0FBQ0E7QUFDQTs7QUFFQTtBQUNBO0FBQ0EsdUJBQWU7QUFDZjtBQUNBO0FBQ0E7O0FBRUE7QUFDQTs7QUFFQTtBQUNBOztBQUVBO0FBQ0E7QUFDQTs7O0FBR0E7QUFDQTs7QUFFQTtBQUNBOztBQUVBO0FBQ0E7O0FBRUE7QUFDQTs7Ozs7Ozs7Ozs7Ozs7OztBQ3RDQTs7QUFFQTs7QUFFQTtBQUNBO0FBQ0E7QUFDQTtBQUNBLEc7Ozs7OztBQ1JBOztBQUVBOztBQUVBOztBQUVBOztBQUVBLHVDQUFzQyx1Q0FBdUMsZ0JBQWdCOztBQUU3RjtBQUNBO0FBQ0Esb0JBQW1CLGtCQUFrQjtBQUNyQztBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTs7QUFFQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0EsRUFBQyxHOzs7Ozs7QUMxQkQsbUJBQWtCLHVEOzs7Ozs7QUNBbEI7QUFDQTtBQUNBO0FBQ0E7QUFDQSxHOzs7Ozs7QUNKQTtBQUNBO0FBQ0Esc0VBQXVFLDBDQUEwQyxFOzs7Ozs7QUNGakg7QUFDQTtBQUNBO0FBQ0E7QUFDQTs7QUFFQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBLG9FQUFtRTtBQUNuRTtBQUNBLHNGQUFxRjtBQUNyRjtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtB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QyxxQkFBSSxPQUFPLFNBQVMsSUFBVCxDQUFjLFFBQWQsQ0FBUCxDQUZnQzs7QUFJcEMscUJBQUksQ0FBQyxJQUFELEVBQU87QUFDUCw0QkFBTyxJQUFJLE1BQUosQ0FBVyxFQUFYLENBQVAsQ0FETztBQUVQLDhCQUFTLElBQVQsQ0FBYyxRQUFkLEVBQXdCLElBQXhCLEVBRk87a0JBQVg7Y0FKZSxDQUFuQixDQUpvQjs7O1lBMURQIiwiZmlsZSI6Im9yZ2FuaXphdGlvbi1mb3JtLmpzIiwic291cmNlc0NvbnRlbnQiOlsiIFx0Ly8gVGhlIG1vZHVsZSBjYWNoZVxuIFx0dmFyIGluc3RhbGxlZE1vZHVsZXMgPSB7fTtcblxuIFx0Ly8gVGhlIHJlcXVpcmUgZnVuY3Rpb25cbiBcdGZ1bmN0aW9uIF9fd2VicGFja19yZXF1aXJlX18obW9kdWxlSWQpIHtcblxuIFx0XHQvLyBDaGVjayBpZiBtb2R1bGUgaXMgaW4gY2FjaGVcbiBcdFx0aWYoaW5zdGFsbGVkTW9kdWxlc1ttb2R1bGVJZF0pXG4gXHRcdFx0cmV0dXJuIGluc3RhbGxlZE1vZHVsZXNbbW9kdWxlSWRdLmV4cG9ydHM7XG5cbiBcdFx0Ly8gQ3JlYXRlIGEgbmV3IG1vZHVsZSAoYW5kIHB1dCBpdCBpbnRvIHRoZSBjYWNoZSlcbiBcdFx0dmFyIG1vZHVsZSA9IGluc3RhbGxlZE1vZHVsZXNbbW9kdWxlSWRdID0ge1xuIFx0XHRcdGV4cG9ydHM6IHt9LFxuIFx0XHRcdGlkOiBtb2R1bGVJZCxcbiBcdFx0XHRsb2FkZWQ6IGZhbHNlXG4gXHRcdH07XG5cbiBcdFx0Ly8gRXhlY3V0ZSB0aGUgbW9kdWxlIGZ1bmN0aW9uXG4gXHRcdG1vZHVsZXNbbW9kdWxlSWRdLmNhbGwobW9kdWxlLmV4cG9ydHMsIG1vZHVsZSwgbW9kdWxlLmV4cG9ydHMsIF9fd2VicGFja19yZXF1aXJlX18pO1xuXG4gXHRcdC8vIEZsYWcgdGhlIG1vZHVsZSBhcyBsb2FkZWRcbiBcdFx0bW9kdWxlLmxvYWRlZCA9IHRydWU7XG5cbiBcdFx0Ly8gUmV0dXJuIHRoZSBleHBvcnRzIG9mIHRoZSBtb2R1bGVcbiBcdFx0cmV0dXJuIG1vZHVsZS5leHBvcnRzO1xuIFx0fVxuXG5cbiBcdC8vIGV4cG9zZSB0aGUgbW9kdWxlcyBvYmplY3QgKF9fd2VicGFja19tb2R1bGVzX18pXG4gXHRfX3dlYnBhY2tfcmVxdWlyZV9fLm0gPSBtb2R1bGVzO1xuXG4gXHQvLyBleHBvc2UgdGhlIG1vZHVsZSBjYWNoZVxuIFx0X193ZWJwYWNrX3JlcXVpcmVfXy5jID0gaW5zdGFsbGVkTW9kdWxlcztcblxuIFx0Ly8gX193ZWJwYWNrX3B1YmxpY19wYXRoX19cbiBcdF9fd2VicGFja19yZXF1aXJlX18ucCA9IFwiXCI7XG5cbiBcdC8vIExvYWQgZW50cnkgbW9kdWxlIGFuZCByZXR1cm4gZXhwb3J0c1xuIFx0cmV0dXJuIF9fd2VicGFja19yZXF1aXJlX18oMCk7XG5cblxuXG4vKiogV0VCUEFDSyBGT09URVIgKipcbiAqKiB3ZWJwYWNrL2Jvb3RzdHJhcCBmMGYxNjY5MGJkOTdhMjU1Y2ZiOVxuICoqLyIsIlwidXNlIHN0cmljdFwiO1xuXG5leHBvcnRzLl9fZXNNb2R1bGUgPSB0cnVlO1xuXG5leHBvcnRzLmRlZmF1bHQgPSBmdW5jdGlvbiAoaW5zdGFuY2UsIENvbnN0cnVjdG9yKSB7XG4gIGlmICghKGluc3RhbmNlIGluc3RhbmNlb2YgQ29uc3RydWN0b3IpKSB7XG4gICAgdGhyb3cgbmV3IFR5cGVFcnJvcihcIkNhbm5vdCBjYWxsIGEgY2xhc3MgYXMgYSBmdW5jdGlvblwiKTtcbiAgfVxufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL2hlbHBlcnMvY2xhc3NDYWxsQ2hlY2suanNcbiAqKiBtb2R1bGUgaWQgPSAzXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJcInVzZSBzdHJpY3RcIjtcblxuZXhwb3J0cy5fX2VzTW9kdWxlID0gdHJ1ZTtcblxudmFyIF9kZWZpbmVQcm9wZXJ0eSA9IHJlcXVpcmUoXCJiYWJlbC1ydW50aW1lL2NvcmUtanMvb2JqZWN0L2RlZmluZS1wcm9wZXJ0eVwiKTtcblxudmFyIF9kZWZpbmVQcm9wZXJ0eTIgPSBfaW50ZXJvcFJlcXVpcmVEZWZhdWx0KF9kZWZpbmVQcm9wZXJ0eSk7XG5cbmZ1bmN0aW9uIF9pbnRlcm9wUmVxdWlyZURlZmF1bHQob2JqKSB7IHJldHVybiBvYmogJiYgb2JqLl9fZXNNb2R1bGUgPyBvYmogOiB7IGRlZmF1bHQ6IG9iaiB9OyB9XG5cbmV4cG9ydHMuZGVmYXVsdCA9IGZ1bmN0aW9uICgpIHtcbiAgZnVuY3Rpb24gZGVmaW5lUHJvcGVydGllcyh0YXJnZXQsIHByb3BzKSB7XG4gICAgZm9yICh2YXIgaSA9IDA7IGkgPCBwcm9wcy5sZW5ndGg7IGkrKykge1xuICAgICAgdmFyIGRlc2NyaXB0b3IgPSBwcm9wc1tpXTtcbiAgICAgIGRlc2NyaXB0b3IuZW51bWVyYWJsZSA9IGRlc2NyaXB0b3IuZW51bWVyYWJsZSB8fCBmYWxzZTtcbiAgICAgIGRlc2NyaXB0b3IuY29uZmlndXJhYmxlID0gdHJ1ZTtcbiAgICAgIGlmIChcInZhbHVlXCIgaW4gZGVzY3JpcHRvcikgZGVzY3JpcHRvci53cml0YWJsZSA9IHRydWU7XG4gICAgICAoMCwgX2RlZmluZVByb3BlcnR5Mi5kZWZhdWx0KSh0YXJnZXQsIGRlc2NyaXB0b3Iua2V5LCBkZXNjcmlwdG9yKTtcbiAgICB9XG4gIH1cblxuICByZXR1cm4gZnVuY3Rpb24gKENvbnN0cnVjdG9yLCBwcm90b1Byb3BzLCBzdGF0aWNQcm9wcykge1xuICAgIGlmIChwcm90b1Byb3BzKSBkZWZpbmVQcm9wZXJ0aWVzKENvbnN0cnVjdG9yLnByb3RvdHlwZSwgcHJvdG9Qcm9wcyk7XG4gICAgaWYgKHN0YXRpY1Byb3BzKSBkZWZpbmVQcm9wZXJ0aWVzKENvbnN0cnVjdG9yLCBzdGF0aWNQcm9wcyk7XG4gICAgcmV0dXJuIENvbnN0cnVjdG9yO1xuICB9O1xufSgpO1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvaGVscGVycy9jcmVhdGVDbGFzcy5qc1xuICoqIG1vZHVsZSBpZCA9IDRcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsIm1vZHVsZS5leHBvcnRzID0geyBcImRlZmF1bHRcIjogcmVxdWlyZShcImNvcmUtanMvbGlicmFyeS9mbi9vYmplY3QvZGVmaW5lLXByb3BlcnR5XCIpLCBfX2VzTW9kdWxlOiB0cnVlIH07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9jb3JlLWpzL29iamVjdC9kZWZpbmUtcHJvcGVydHkuanNcbiAqKiBtb2R1bGUgaWQgPSA1XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJyZXF1aXJlKCcuLi8uLi9tb2R1bGVzL2VzNi5vYmplY3QuZGVmaW5lLXByb3BlcnR5Jyk7XG52YXIgJE9iamVjdCA9IHJlcXVpcmUoJy4uLy4uL21vZHVsZXMvX2NvcmUnKS5PYmplY3Q7XG5tb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uIGRlZmluZVByb3BlcnR5KGl0LCBrZXksIGRlc2Mpe1xuICByZXR1cm4gJE9iamVjdC5kZWZpbmVQcm9wZXJ0eShpdCwga2V5LCBkZXNjKTtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9mbi9vYmplY3QvZGVmaW5lLXByb3BlcnR5LmpzXG4gKiogbW9kdWxlIGlkID0gNlxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwidmFyICRleHBvcnQgPSByZXF1aXJlKCcuL19leHBvcnQnKTtcclxuLy8gMTkuMS4yLjQgLyAxNS4yLjMuNiBPYmplY3QuZGVmaW5lUHJvcGVydHkoTywgUCwgQXR0cmlidXRlcylcclxuJGV4cG9ydCgkZXhwb3J0LlMgKyAkZXhwb3J0LkYgKiAhcmVxdWlyZSgnLi9fZGVzY3JpcHRvcnMnKSwgJ09iamVjdCcsIHtkZWZpbmVQcm9wZXJ0eTogcmVxdWlyZSgnLi9fb2JqZWN0LWRwJykuZn0pO1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9lczYub2JqZWN0LmRlZmluZS1wcm9wZXJ0eS5qc1xuICoqIG1vZHVsZSBpZCA9IDdcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsInZhciBnbG9iYWwgICAgPSByZXF1aXJlKCcuL19nbG9iYWwnKVxuICAsIGNvcmUgICAgICA9IHJlcXVpcmUoJy4vX2NvcmUnKVxuICAsIGN0eCAgICAgICA9IHJlcXVpcmUoJy4vX2N0eCcpXG4gICwgaGlkZSAgICAgID0gcmVxdWlyZSgnLi9faGlkZScpXG4gICwgUFJPVE9UWVBFID0gJ3Byb3RvdHlwZSc7XG5cbnZhciAkZXhwb3J0ID0gZnVuY3Rpb24odHlwZSwgbmFtZSwgc291cmNlKXtcbiAgdmFyIElTX0ZPUkNFRCA9IHR5cGUgJiAkZXhwb3J0LkZcbiAgICAsIElTX0dMT0JBTCA9IHR5cGUgJiAkZXhwb3J0LkdcbiAgICAsIElTX1NUQVRJQyA9IHR5cGUgJiAkZXhwb3J0LlNcbiAgICAsIElTX1BST1RPICA9IHR5cGUgJiAkZXhwb3J0LlBcbiAgICAsIElTX0JJTkQgICA9IHR5cGUgJiAkZXhwb3J0LkJcbiAgICAsIElTX1dSQVAgICA9IHR5cGUgJiAkZXhwb3J0LldcbiAgICAsIGV4cG9ydHMgICA9IElTX0dMT0JBTCA/IGNvcmUgOiBjb3JlW25hbWVdIHx8IChjb3JlW25hbWVdID0ge30pXG4gICAgLCBleHBQcm90byAgPSBleHBvcnRzW1BST1RPVFlQRV1cbiAgICAsIHRhcmdldCAgICA9IElTX0dMT0JBTCA/IGdsb2JhbCA6IElTX1NUQVRJQyA/IGdsb2JhbFtuYW1lXSA6IChnbG9iYWxbbmFtZV0gfHwge30pW1BST1RPVFlQRV1cbiAgICAsIGtleSwgb3duLCBvdXQ7XG4gIGlmKElTX0dMT0JBTClzb3VyY2UgPSBuYW1lO1xuICBmb3Ioa2V5IGluIHNvdXJjZSl7XG4gICAgLy8gY29udGFpbnMgaW4gbmF0aXZlXG4gICAgb3duID0gIUlTX0ZPUkNFRCAmJiB0YXJnZXQgJiYgdGFyZ2V0W2tleV0gIT09IHVuZGVmaW5lZDtcbiAgICBpZihvd24gJiYga2V5IGluIGV4cG9ydHMpY29udGludWU7XG4gICAgLy8gZXhwb3J0IG5hdGl2ZSBvciBwYXNzZWRcbiAgICBvdXQgPSBvd24gPyB0YXJnZXRba2V5XSA6IHNvdXJjZVtrZXldO1xuICAgIC8vIHByZXZlbnQgZ2xvYmFsIHBvbGx1dGlvbiBmb3IgbmFtZXNwYWNlc1xuICAgIGV4cG9ydHNba2V5XSA9IElTX0dMT0JBTCAmJiB0eXBlb2YgdGFyZ2V0W2tleV0gIT0gJ2Z1bmN0aW9uJyA/IHNvdXJjZVtrZXldXG4gICAgLy8gYmluZCB0aW1lcnMgdG8gZ2xvYmFsIGZvciBjYWxsIGZyb20gZXhwb3J0IGNvbnRleHRcbiAgICA6IElTX0JJTkQgJiYgb3duID8gY3R4KG91dCwgZ2xvYmFsKVxuICAgIC8vIHdyYXAgZ2xvYmFsIGNvbnN0cnVjdG9ycyBmb3IgcHJldmVudCBjaGFuZ2UgdGhlbSBpbiBsaWJyYXJ5XG4gICAgOiBJU19XUkFQICYmIHRhcmdldFtrZXldID09IG91dCA/IChmdW5jdGlvbihDKXtcbiAgICAgIHZhciBGID0gZnVuY3Rpb24oYSwgYiwgYyl7XG4gICAgICAgIGlmKHRoaXMgaW5zdGFuY2VvZiBDKXtcbiAgICAgICAgICBzd2l0Y2goYXJndW1lbnRzLmxlbmd0aCl7XG4gICAgICAgICAgICBjYXNlIDA6IHJldHVybiBuZXcgQztcbiAgICAgICAgICAgIGNhc2UgMTogcmV0dXJuIG5ldyBDKGEpO1xuICAgICAgICAgICAgY2FzZSAyOiByZXR1cm4gbmV3IEMoYSwgYik7XG4gICAgICAgICAgfSByZXR1cm4gbmV3IEMoYSwgYiwgYyk7XG4gICAgICAgIH0gcmV0dXJuIEMuYXBwbHkodGhpcywgYXJndW1lbnRzKTtcbiAgICAgIH07XG4gICAgICBGW1BST1RPVFlQRV0gPSBDW1BST1RPVFlQRV07XG4gICAgICByZXR1cm4gRjtcbiAgICAvLyBtYWtlIHN0YXRpYyB2ZXJzaW9ucyBmb3IgcHJvdG90eXBlIG1ldGhvZHNcbiAgICB9KShvdXQpIDogSVNfUFJPVE8gJiYgdHlwZW9mIG91dCA9PSAnZnVuY3Rpb24nID8gY3R4KEZ1bmN0aW9uLmNhbGwsIG91dCkgOiBvdXQ7XG4gICAgLy8gZXhwb3J0IHByb3RvIG1ldGhvZHMgdG8gY29yZS4lQ09OU1RSVUNUT1IlLm1ldGhvZHMuJU5BTUUlXG4gICAgaWYoSVNfUFJPVE8pe1xuICAgICAgKGV4cG9ydHMudmlydHVhbCB8fCAoZXhwb3J0cy52aXJ0dWFsID0ge30pKVtrZXldID0gb3V0O1xuICAgICAgLy8gZXhwb3J0IHByb3RvIG1ldGhvZHMgdG8gY29yZS4lQ09OU1RSVUNUT1IlLnByb3RvdHlwZS4lTkFNRSVcbiAgICAgIGlmKHR5cGUgJiAkZXhwb3J0LlIgJiYgZXhwUHJvdG8gJiYgIWV4cFByb3RvW2tleV0paGlkZShleHBQcm90bywga2V5LCBvdXQpO1xuICAgIH1cbiAgfVxufTtcbi8vIHR5cGUgYml0bWFwXG4kZXhwb3J0LkYgPSAxOyAgIC8vIGZvcmNlZFxuJGV4cG9ydC5HID0gMjsgICAvLyBnbG9iYWxcbiRleHBvcnQuUyA9IDQ7ICAgLy8gc3RhdGljXG4kZXhwb3J0LlAgPSA4OyAgIC8vIHByb3RvXG4kZXhwb3J0LkIgPSAxNjsgIC8vIGJpbmRcbiRleHBvcnQuVyA9IDMyOyAgLy8gd3JhcFxuJGV4cG9ydC5VID0gNjQ7ICAvLyBzYWZlXG4kZXhwb3J0LlIgPSAxMjg7IC8vIHJlYWwgcHJvdG8gbWV0aG9kIGZvciBgbGlicmFyeWAgXG5tb2R1bGUuZXhwb3J0cyA9ICRleHBvcnQ7XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19leHBvcnQuanNcbiAqKiBtb2R1bGUgaWQgPSA4XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCIvLyBodHRwczovL2dpdGh1Yi5jb20vemxvaXJvY2svY29yZS1qcy9pc3N1ZXMvODYjaXNzdWVjb21tZW50LTExNTc1OTAyOFxudmFyIGdsb2JhbCA9IG1vZHVsZS5leHBvcnRzID0gdHlwZW9mIHdpbmRvdyAhPSAndW5kZWZpbmVkJyAmJiB3aW5kb3cuTWF0aCA9PSBNYXRoXG4gID8gd2luZG93IDogdHlwZW9mIHNlbGYgIT0gJ3VuZGVmaW5lZCcgJiYgc2VsZi5NYXRoID09IE1hdGggPyBzZWxmIDogRnVuY3Rpb24oJ3JldHVybiB0aGlzJykoKTtcbmlmKHR5cGVvZiBfX2cgPT0gJ251bWJlcicpX19nID0gZ2xvYmFsOyAvLyBlc2xpbnQtZGlzYWJsZS1saW5lIG5vLXVuZGVmXG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19nbG9iYWwuanNcbiAqKiBtb2R1bGUgaWQgPSA5XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJ2YXIgY29yZSA9IG1vZHVsZS5leHBvcnRzID0ge3ZlcnNpb246ICcyLjIuMCd9O1xuaWYodHlwZW9mIF9fZSA9PSAnbnVtYmVyJylfX2UgPSBjb3JlOyAvLyBlc2xpbnQtZGlzYWJsZS1saW5lIG5vLXVuZGVmXG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19jb3JlLmpzXG4gKiogbW9kdWxlIGlkID0gMTBcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsIi8vIG9wdGlvbmFsIC8gc2ltcGxlIGNvbnRleHQgYmluZGluZ1xudmFyIGFGdW5jdGlvbiA9IHJlcXVpcmUoJy4vX2EtZnVuY3Rpb24nKTtcbm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oZm4sIHRoYXQsIGxlbmd0aCl7XG4gIGFGdW5jdGlvbihmbik7XG4gIGlmKHRoYXQgPT09IHVuZGVmaW5lZClyZXR1cm4gZm47XG4gIHN3aXRjaChsZW5ndGgpe1xuICAgIGNhc2UgMTogcmV0dXJuIGZ1bmN0aW9uKGEpe1xuICAgICAgcmV0dXJuIGZuLmNhbGwodGhhdCwgYSk7XG4gICAgfTtcbiAgICBjYXNlIDI6IHJldHVybiBmdW5jdGlvbihhLCBiKXtcbiAgICAgIHJldHVybiBmbi5jYWxsKHRoYXQsIGEsIGIpO1xuICAgIH07XG4gICAgY2FzZSAzOiByZXR1cm4gZnVuY3Rpb24oYSwgYiwgYyl7XG4gICAgICByZXR1cm4gZm4uY2FsbCh0aGF0LCBhLCBiLCBjKTtcbiAgICB9O1xuICB9XG4gIHJldHVybiBmdW5jdGlvbigvKiAuLi5hcmdzICovKXtcbiAgICByZXR1cm4gZm4uYXBwbHkodGhhdCwgYXJndW1lbnRzKTtcbiAgfTtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19jdHguanNcbiAqKiBtb2R1bGUgaWQgPSAxMVxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwibW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihpdCl7XG4gIGlmKHR5cGVvZiBpdCAhPSAnZnVuY3Rpb24nKXRocm93IFR5cGVFcnJvcihpdCArICcgaXMgbm90IGEgZnVuY3Rpb24hJyk7XG4gIHJldHVybiBpdDtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19hLWZ1bmN0aW9uLmpzXG4gKiogbW9kdWxlIGlkID0gMTJcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsInZhciBkUCAgICAgICAgID0gcmVxdWlyZSgnLi9fb2JqZWN0LWRwJylcbiAgLCBjcmVhdGVEZXNjID0gcmVxdWlyZSgnLi9fcHJvcGVydHktZGVzYycpO1xubW9kdWxlLmV4cG9ydHMgPSByZXF1aXJlKCcuL19kZXNjcmlwdG9ycycpID8gZnVuY3Rpb24ob2JqZWN0LCBrZXksIHZhbHVlKXtcbiAgcmV0dXJuIGRQLmYob2JqZWN0LCBrZXksIGNyZWF0ZURlc2MoMSwgdmFsdWUpKTtcbn0gOiBmdW5jdGlvbihvYmplY3QsIGtleSwgdmFsdWUpe1xuICBvYmplY3Rba2V5XSA9IHZhbHVlO1xuICByZXR1cm4gb2JqZWN0O1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2hpZGUuanNcbiAqKiBtb2R1bGUgaWQgPSAxM1xuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwidmFyIGFuT2JqZWN0ICAgICAgID0gcmVxdWlyZSgnLi9fYW4tb2JqZWN0JylcbiAgLCBJRThfRE9NX0RFRklORSA9IHJlcXVpcmUoJy4vX2llOC1kb20tZGVmaW5lJylcbiAgLCB0b1ByaW1pdGl2ZSAgICA9IHJlcXVpcmUoJy4vX3RvLXByaW1pdGl2ZScpXG4gICwgZFAgICAgICAgICAgICAgPSBPYmplY3QuZGVmaW5lUHJvcGVydHk7XG5cbmV4cG9ydHMuZiA9IHJlcXVpcmUoJy4vX2Rlc2NyaXB0b3JzJykgPyBPYmplY3QuZGVmaW5lUHJvcGVydHkgOiBmdW5jdGlvbiBkZWZpbmVQcm9wZXJ0eShPLCBQLCBBdHRyaWJ1dGVzKXtcbiAgYW5PYmplY3QoTyk7XG4gIFAgPSB0b1ByaW1pdGl2ZShQLCB0cnVlKTtcbiAgYW5PYmplY3QoQXR0cmlidXRlcyk7XG4gIGlmKElFOF9ET01fREVGSU5FKXRyeSB7XG4gICAgcmV0dXJuIGRQKE8sIFAsIEF0dHJpYnV0ZXMpO1xuICB9IGNhdGNoKGUpeyAvKiBlbXB0eSAqLyB9XG4gIGlmKCdnZXQnIGluIEF0dHJpYnV0ZXMgfHwgJ3NldCcgaW4gQXR0cmlidXRlcyl0aHJvdyBUeXBlRXJyb3IoJ0FjY2Vzc29ycyBub3Qgc3VwcG9ydGVkIScpO1xuICBpZigndmFsdWUnIGluIEF0dHJpYnV0ZXMpT1tQXSA9IEF0dHJpYnV0ZXMudmFsdWU7XG4gIHJldHVybiBPO1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX29iamVjdC1kcC5qc1xuICoqIG1vZHVsZSBpZCA9IDE0XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJ2YXIgaXNPYmplY3QgPSByZXF1aXJlKCcuL19pcy1vYmplY3QnKTtcbm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oaXQpe1xuICBpZighaXNPYmplY3QoaXQpKXRocm93IFR5cGVFcnJvcihpdCArICcgaXMgbm90IGFuIG9iamVjdCEnKTtcbiAgcmV0dXJuIGl0O1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2FuLW9iamVjdC5qc1xuICoqIG1vZHVsZSBpZCA9IDE1XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJtb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGl0KXtcbiAgcmV0dXJuIHR5cGVvZiBpdCA9PT0gJ29iamVjdCcgPyBpdCAhPT0gbnVsbCA6IHR5cGVvZiBpdCA9PT0gJ2Z1bmN0aW9uJztcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19pcy1vYmplY3QuanNcbiAqKiBtb2R1bGUgaWQgPSAxNlxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwibW9kdWxlLmV4cG9ydHMgPSAhcmVxdWlyZSgnLi9fZGVzY3JpcHRvcnMnKSAmJiAhcmVxdWlyZSgnLi9fZmFpbHMnKShmdW5jdGlvbigpe1xyXG4gIHJldHVybiBPYmplY3QuZGVmaW5lUHJvcGVydHkocmVxdWlyZSgnLi9fZG9tLWNyZWF0ZScpKCdkaXYnKSwgJ2EnLCB7Z2V0OiBmdW5jdGlvbigpeyByZXR1cm4gNzsgfX0pLmEgIT0gNztcclxufSk7XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19pZTgtZG9tLWRlZmluZS5qc1xuICoqIG1vZHVsZSBpZCA9IDE3XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCIvLyBUaGFuaydzIElFOCBmb3IgaGlzIGZ1bm55IGRlZmluZVByb3BlcnR5XG5tb2R1bGUuZXhwb3J0cyA9ICFyZXF1aXJlKCcuL19mYWlscycpKGZ1bmN0aW9uKCl7XG4gIHJldHVybiBPYmplY3QuZGVmaW5lUHJvcGVydHkoe30sICdhJywge2dldDogZnVuY3Rpb24oKXsgcmV0dXJuIDc7IH19KS5hICE9IDc7XG59KTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2Rlc2NyaXB0b3JzLmpzXG4gKiogbW9kdWxlIGlkID0gMThcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsIm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oZXhlYyl7XG4gIHRyeSB7XG4gICAgcmV0dXJuICEhZXhlYygpO1xuICB9IGNhdGNoKGUpe1xuICAgIHJldHVybiB0cnVlO1xuICB9XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZmFpbHMuanNcbiAqKiBtb2R1bGUgaWQgPSAxOVxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwidmFyIGlzT2JqZWN0ID0gcmVxdWlyZSgnLi9faXMtb2JqZWN0JylcbiAgLCBkb2N1bWVudCA9IHJlcXVpcmUoJy4vX2dsb2JhbCcpLmRvY3VtZW50XG4gIC8vIGluIG9sZCBJRSB0eXBlb2YgZG9jdW1lbnQuY3JlYXRlRWxlbWVudCBpcyAnb2JqZWN0J1xuICAsIGlzID0gaXNPYmplY3QoZG9jdW1lbnQpICYmIGlzT2JqZWN0KGRvY3VtZW50LmNyZWF0ZUVsZW1lbnQpO1xubW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihpdCl7XG4gIHJldHVybiBpcyA/IGRvY3VtZW50LmNyZWF0ZUVsZW1lbnQoaXQpIDoge307XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZG9tLWNyZWF0ZS5qc1xuICoqIG1vZHVsZSBpZCA9IDIwXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCIvLyA3LjEuMSBUb1ByaW1pdGl2ZShpbnB1dCBbLCBQcmVmZXJyZWRUeXBlXSlcbnZhciBpc09iamVjdCA9IHJlcXVpcmUoJy4vX2lzLW9iamVjdCcpO1xuLy8gaW5zdGVhZCBvZiB0aGUgRVM2IHNwZWMgdmVyc2lvbiwgd2UgZGlkbid0IGltcGxlbWVudCBAQHRvUHJpbWl0aXZlIGNhc2Vcbi8vIGFuZCB0aGUgc2Vjb25kIGFyZ3VtZW50IC0gZmxhZyAtIHByZWZlcnJlZCB0eXBlIGlzIGEgc3RyaW5nXG5tb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGl0LCBTKXtcbiAgaWYoIWlzT2JqZWN0KGl0KSlyZXR1cm4gaXQ7XG4gIHZhciBmbiwgdmFsO1xuICBpZihTICYmIHR5cGVvZiAoZm4gPSBpdC50b1N0cmluZykgPT0gJ2Z1bmN0aW9uJyAmJiAhaXNPYmplY3QodmFsID0gZm4uY2FsbChpdCkpKXJldHVybiB2YWw7XG4gIGlmKHR5cGVvZiAoZm4gPSBpdC52YWx1ZU9mKSA9PSAnZnVuY3Rpb24nICYmICFpc09iamVjdCh2YWwgPSBmbi5jYWxsKGl0KSkpcmV0dXJuIHZhbDtcbiAgaWYoIVMgJiYgdHlwZW9mIChmbiA9IGl0LnRvU3RyaW5nKSA9PSAnZnVuY3Rpb24nICYmICFpc09iamVjdCh2YWwgPSBmbi5jYWxsKGl0KSkpcmV0dXJuIHZhbDtcbiAgdGhyb3cgVHlwZUVycm9yKFwiQ2FuJ3QgY29udmVydCBvYmplY3QgdG8gcHJpbWl0aXZlIHZhbHVlXCIpO1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX3RvLXByaW1pdGl2ZS5qc1xuICoqIG1vZHVsZSBpZCA9IDIxXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJtb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGJpdG1hcCwgdmFsdWUpe1xuICByZXR1cm4ge1xuICAgIGVudW1lcmFibGUgIDogIShiaXRtYXAgJiAxKSxcbiAgICBjb25maWd1cmFibGU6ICEoYml0bWFwICYgMiksXG4gICAgd3JpdGFibGUgICAgOiAhKGJpdG1hcCAmIDQpLFxuICAgIHZhbHVlICAgICAgIDogdmFsdWVcbiAgfTtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19wcm9wZXJ0eS1kZXNjLmpzXG4gKiogbW9kdWxlIGlkID0gMjJcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsIid1c2Ugc3RyaWN0JztcblxuLyoqXG4gKiBFcnJvcnNcbiAqIEB0eXBlZGVmIHtPYmplY3R9IExpc3RFcnJvcnNcbiAqIEBwcm9wZXJ0eSB7U3RyaW5nfSBuYW1lIC0gbmFtZSBvZiBmaWVsZFxuICogQHByb3BlcnR5IHtTdHJpbmd9IGVycm9yIC0gZXJyb3IgZGVzY3JpcHRpb25cbiAqL1xuXG5leHBvcnQgZGVmYXVsdCBjbGFzcyBGb3JtSGVscGVyIHtcbiAgICAvKipcbiAgICAgKiBWYWxpZGF0ZSBmb3JtIHRocm91Z2ggaW5wdXRzXG4gICAgICogQHBhcmFtIHtqUXVlcnl9ICRjb250cm9sc1xuICAgICAqL1xuICAgIGNvbnN0cnVjdG9yKCRjb250cm9scykge1xuICAgICAgICB0aGlzLiRjb250cm9scyA9ICRjb250cm9scztcbiAgICAgICAgdGhpcy5hcnJFcnJvcnMgPSBbXTtcbiAgICAgICAgdGhpcy5fYXNzaWduRXZlbnRzKCk7XG4gICAgfVxuXG4gICAgX2Fzc2lnbkV2ZW50cygpIHtcbiAgICAgICAgdGhpcy4kY29udHJvbHMub24oJ2lucHV0IGNoYW5nZScsIChlKSA9PiB7XG4gICAgICAgICAgICBjb25zdCAkY29udHJvbCA9ICQoZS5jdXJyZW50VGFyZ2V0KTtcblxuICAgICAgICAgICAgdGhpcy5fdmFsaWRhdGVJbW1lZGlhdGUoJGNvbnRyb2wpO1xuICAgICAgICAgICAgdGhpcy5fcmVtb3ZlRXJyb3IoJGNvbnRyb2wpO1xuICAgICAgICB9KTtcbiAgICB9XG5cbiAgICBfdmFsaWRhdGVJbW1lZGlhdGUoJGNvbnRyb2wpe1xuICAgICAgICBpZiAoJGNvbnRyb2wuaGFzQ2xhc3MoJ3R5cGUtbnVtZXJpYycpKSB7XG4gICAgICAgICAgICAkY29udHJvbC52YWwoJGNvbnRyb2wudmFsKCkucmVwbGFjZSgvW15cXGRdKy9nLCAnJykpO1xuICAgICAgICB9XG5cbiAgICAgICAgaWYgKCRjb250cm9sLmhhc0NsYXNzKCd0eXBlLW5vc3BhY2UnKSkge1xuICAgICAgICAgICAgJGNvbnRyb2wudmFsKCRjb250cm9sLnZhbCgpLnJlcGxhY2UoL1xccy9nLCAnJykpO1xuICAgICAgICB9XG4gICAgfVxuXG4gICAgaXNWYWxpZElucHV0cygpIHtcbiAgICAgICAgY29uc3QgJGNvbnRyb2xzID0gdGhpcy4kY29udHJvbHM7XG4gICAgICAgIGxldCBlcnJvciA9IDA7XG5cbiAgICAgICAgJGNvbnRyb2xzLmVhY2goKGluZGV4LCBjb250cm9sKSA9PiB7XG4gICAgICAgICAgICBjb25zdCAkY29udHJvbCA9ICQoY29udHJvbCk7XG5cbiAgICAgICAgICAgIGlmICghdGhpcy5faXNWYWxpZElucHV0KCRjb250cm9sKSkge1xuICAgICAgICAgICAgICAgIGVycm9yICs9IDE7XG4gICAgICAgICAgICB9XG4gICAgICAgIH0pO1xuICAgICAgICByZXR1cm4gQm9vbGVhbighZXJyb3IpO1xuICAgIH1cblxuICAgIC8qKlxuICAgICAqIENoZWNrIGdpdmVuIGNvbnRyb2wsIGlzIGl0IHZhbGlkP1xuICAgICAqIEBwYXJhbSB7alF1ZXJ5fSAkY29udHJvbFxuICAgICAqIEByZXR1cm5zIHtib29sZWFufSAtIElzIHZhbGlkIGNvbnRyb2w/XG4gICAgICovXG4gICAgX2lzVmFsaWRJbnB1dCgkY29udHJvbCkge1xuICAgICAgICBjb25zdCB2YWx1ZSA9ICQudHJpbSgkY29udHJvbC52YWwoKSk7XG5cbiAgICAgICAgaWYgKCF2YWx1ZSAmJiAhJGNvbnRyb2wuaGFzQ2xhc3MoJ3R5cGUtb3B0aW9uYWwnKSkge1xuICAgICAgICAgICAgdGhpcy5fc2V0RXJyb3IoJGNvbnRyb2wsICdFbXB0eScpO1xuICAgICAgICAgICAgcmV0dXJuIGZhbHNlO1xuICAgICAgICB9XG5cbiAgICAgICAgaWYgKCgkY29udHJvbC5oYXNDbGFzcygndHlwZS1lbWFpbCcpKSAmJiAhdGhpcy5faXNWYWxpZEVtYWlsKHZhbHVlKSkge1xuICAgICAgICAgICAgdGhpcy5fc2V0RXJyb3IoJGNvbnRyb2wsICdFbWFpbCBpcyBub3QgdmFsaWQnKTtcbiAgICAgICAgICAgIHJldHVybiBmYWxzZTtcbiAgICAgICAgfVxuXG4gICAgICAgIHJldHVybiB0cnVlO1xuICAgIH1cblxuICAgIC8qKlxuICAgICAqIElzIEVtYWlsIHZhbGlkP1xuICAgICAqIEBwYXJhbSB7c3RyaW5nfSBlbWFpbFxuICAgICAqIEByZXR1cm5zIHtib29sZWFufVxuICAgICAqL1xuICAgIF9pc1ZhbGlkRW1haWwoZW1haWwpIHtcbiAgICAgICAgdmFyIHJlID0gL14oKFtePD4oKVxcW1xcXVxcXFwuLDs6XFxzQFwiXSsoXFwuW148PigpXFxbXFxdXFxcXC4sOzpcXHNAXCJdKykqKXwoXCIuK1wiKSlAKChcXFtbMC05XXsxLDN9XFwuWzAtOV17MSwzfVxcLlswLTldezEsM31cXC5bMC05XXsxLDN9XSl8KChbYS16QS1aXFwtMC05XStcXC4pK1thLXpBLVpdezIsfSkpJC87XG4gICAgICAgIHJldHVybiByZS50ZXN0KGVtYWlsKTtcbiAgICB9XG5cbiAgICAvKipcbiAgICAgKiBTZXQgZXJyb3IgZm9yIGNvbnRyb2xcbiAgICAgKiBAcGFyYW0ge2pRdWVyeX0gJGNvbnRyb2xcbiAgICAgKiBAcGFyYW0ge1N0cmluZ30gZXJyb3JUZXh0XG4gICAgICogQHBhcmFtIHtCb29sZWFufSBpbnNlcnRFcnJvclxuICAgICAqL1xuICAgIF9zZXRFcnJvcigkY29udHJvbCwgZXJyb3JUZXh0LCBpbnNlcnRFcnJvciA9IHRydWUpIHtcbiAgICAgICAgY29uc3QgJHBhcmVudCA9ICRjb250cm9sLnBhcmVudCgpO1xuICAgICAgICBjb25zdCAkZXJyb3IgPSAkcGFyZW50LmZpbmQoJy5iLWVycm9yJyk7XG5cbiAgICAgICAgaWYgKCRlcnJvci5sZW5ndGgpIHJldHVybjtcblxuICAgICAgICAkcGFyZW50LmFkZENsYXNzKCdiLWVycm9yX3Nob3cnKTtcbiAgICAgICAgXG4gICAgICAgIGluc2VydEVycm9yICYmICQoJzxkaXYgY2xhc3M9XCJiLWVycm9yXCIgLz4nKVxuICAgICAgICAgICAgLnRleHQoZXJyb3JUZXh0KVxuICAgICAgICAgICAgLmFwcGVuZFRvKCRwYXJlbnQpO1xuXG4gICAgICAgIHRoaXMuYXJyRXJyb3JzLnB1c2goe1xuICAgICAgICAgICAgbmFtZTogJGNvbnRyb2wuYXR0cignbmFtZScpLFxuICAgICAgICAgICAgZXJyb3I6IGVycm9yVGV4dFxuICAgICAgICB9KVxuICAgIH1cblxuICAgIC8qKlxuICAgICAqIFJlbW92ZSBlcnJvciBmb3IgY29udHJvbFxuICAgICAqIEBwYXJhbSB7alF1ZXJ5fSAkY29udHJvbFxuICAgICAqL1xuICAgIF9yZW1vdmVFcnJvcigkY29udHJvbCkge1xuICAgICAgICBjb25zdCAkcGFyZW50ID0gJGNvbnRyb2wucGFyZW50KCk7XG5cbiAgICAgICAgJHBhcmVudFxuICAgICAgICAgICAgLnJlbW92ZUNsYXNzKCdiLWVycm9yX3Nob3cnKVxuICAgICAgICAgICAgLmZpbmQoJy5iLWVycm9yJykucmVtb3ZlKCk7XG5cbiAgICAgICAgdGhpcy5hcnJFcnJvcnMgPSB0aGlzLmFyckVycm9ycy5maWx0ZXIoZnVuY3Rpb24gKGl0ZW0pIHtcbiAgICAgICAgICAgIHJldHVybiBpdGVtLm5hbWUgIT09ICRjb250cm9sLmF0dHIoJ25hbWUnKVxuICAgICAgICB9KVxuICAgIH1cblxuICAgIC8qKlxuICAgICAqIFNldCBlcnJvcnNcbiAgICAgKiBAcGFyYW0ge0FycmF5fSBlcnJvcnMgLSBbe25hbWU6IFwiZW1haWxcIiwgZXJyb3I6IFwiZW1wdHlcIn0sIHtuYW1lOiBcInBhc3N3b3JkXCIsIGVycm9yOiBcImVtcHR5XCJ9XVxuICAgICAqIEBwYXJhbSB7Qm9vbGVhbn0gaW5zZXJ0RXJyb3IgLSBpbnNlcnQgZXJyb3IgZGVzY3JpcHRpb24gdG8gdGhlIERvbSBcbiAgICAgKi9cbiAgICBzZXRFcnJvcnMoZXJyb3JzLCBpbnNlcnRFcnJvciA9IHRydWUpIHtcbiAgICAgICAgZXJyb3JzLmZvckVhY2goKGl0ZW0pID0+IHtcbiAgICAgICAgICAgIGNvbnN0ICRjdXJyZW50Q29udHJvbCA9IHRoaXMuJGNvbnRyb2xzLmZpbHRlcignW25hbWU9XCInICsgaXRlbS5uYW1lICsgJ1wiXScpLmZpcnN0KCk7XG5cbiAgICAgICAgICAgIGlmICgkY3VycmVudENvbnRyb2wubGVuZ3RoKSB0aGlzLl9zZXRFcnJvcigkY3VycmVudENvbnRyb2wsIGl0ZW0uZXJyb3IsIGluc2VydEVycm9yKVxuICAgICAgICB9KVxuICAgIH1cblxuICAgIC8qKlxuICAgICAqIEdldCB0ZXh0IHZlcnNpb24gb2YgZXJyb3JzIGluIG9uZSBsaW5lLlxuICAgICAqIEBwYXJhbSB7TGlzdEVycm9yc30gZXJyb3JzXG4gICAgICogQHJldHVybnMge3N0cmluZ31cbiAgICAgKi9cbiAgICBnZXRFcnJvcnNUZXh0KGVycm9ycykge1xuICAgICAgICBjb25zdCBhcnJFcnJvcnMgPSBlcnJvcnMgfHwgdGhpcy5hcnJFcnJvcnM7XG4gICAgICAgIGxldCBlcnJvclR4dCA9ICcnO1xuXG4gICAgICAgIGFyckVycm9ycy5mb3JFYWNoKChpdGVtKSA9PiB7XG4gICAgICAgICAgICBjb25zdCBuYW1lID0gaXRlbS5uYW1lWzBdLnRvVXBwZXJDYXNlKCkgKyBpdGVtLm5hbWUuc3Vic3RyKDEpO1xuXG4gICAgICAgICAgICBlcnJvclR4dCArPSBgJHtuYW1lfTogJHtpdGVtLmVycm9yfS4gYDtcbiAgICAgICAgfSk7XG5cbiAgICAgICAgcmV0dXJuIGVycm9yVHh0O1xuICAgIH1cblxuICAgIC8qKlxuICAgICAqIEdldCBsaXN0IG9mIGVycm9ycyB3aXRoIGZ1bGwgdGl0bGUgKGZyb20gY29udHJvbCB0aXRsZSBhdHRyaWJ1dGUpXG4gICAgICogQHBhcmFtIHtMaXN0RXJyb3JzfSBlcnJvcnMgLSBsaXN0IG9mIGVycm9yc1xuICAgICAqIEByZXR1cm5zIHtzdHJpbmd9XG4gICAgICovXG4gICAgZ2V0RXJyb3JzRnVsbChlcnJvcnMpIHtcbiAgICAgICAgY29uc3Qgc2VsZiA9IHRoaXM7XG4gICAgICAgIGNvbnN0IGFyckVycm9ycyA9IGVycm9ycyB8fCB0aGlzLmFyckVycm9ycztcbiAgICAgICAgbGV0IGVycm9yVHh0ID0gJyc7XG5cbiAgICAgICAgYXJyRXJyb3JzLmZvckVhY2goKGl0ZW0pID0+IHtcbiAgICAgICAgICAgIGNvbnN0ICRjb250cm9sID0gc2VsZi4kY29udHJvbHMuZmlsdGVyKGBbbmFtZT1cIiR7aXRlbS5uYW1lfVwiXWApLmZpcnN0KCk7XG4gICAgICAgICAgICBjb25zdCBuYW1lID0gJGNvbnRyb2wubGVuZ3RoPyAkY29udHJvbC5hdHRyKCd0aXRsZScpOiBpdGVtLm5hbWU7XG5cbiAgICAgICAgICAgIGVycm9yVHh0ICs9IGA8Yj4ke25hbWV9PC9iPjogJHtpdGVtLmVycm9yfS4gIDxicj48YnI+YDtcbiAgICAgICAgfSk7XG5cbiAgICAgICAgcmV0dXJuIGVycm9yVHh0O1xuICAgIH1cblxuICAgIGdldEZvcm1EYXRhKCl7XG4gICAgICAgIGxldCBhamF4RGF0YSA9IHt9O1xuXG4gICAgICAgIHRoaXMuJGNvbnRyb2xzLm1hcCgoaW5kZXgsIGVsKSA9PiB7XG4gICAgICAgICAgICBjb25zdCAkZWwgPSAkKGVsKTtcbiAgICAgICAgICAgIGNvbnN0IG5hbWUgPSAkZWwuYXR0cignbmFtZScpO1xuXG4gICAgICAgICAgICBpZiAoIW5hbWUpIHJldHVybjtcblxuICAgICAgICAgICAgaWYgKCRlbC5pcygnOmNoZWNrYm94Jykpe1xuICAgICAgICAgICAgICAgIGFqYXhEYXRhW25hbWVdID0gJGVsLnByb3AoJ2NoZWNrZWQnKVxuICAgICAgICAgICAgfSBlbHNlIHtcbiAgICAgICAgICAgICAgICBhamF4RGF0YVtuYW1lXSA9ICRlbC52YWwoKVxuICAgICAgICAgICAgfVxuICAgICAgICB9KTtcblxuICAgICAgICByZXR1cm4gYWpheERhdGE7XG4gICAgfVxuXG4gICAgLyoqXG4gICAgICogUmVtb3ZlIGFsbCBlcnJvcnNcbiAgICAgKi9cbiAgICByZW1vdmVFcnJvcnMoKSB7XG4gICAgICAgIHRoaXMuJGNvbnRyb2xzLmVhY2goKGluZGV4LCBlbCkgPT4ge1xuICAgICAgICAgICAgY29uc3QgJGVsID0gJChlbCk7XG4gICAgICAgICAgICB0aGlzLl9yZW1vdmVFcnJvcigkZWwpXG4gICAgICAgIH0pXG4gICAgfVxuXG4gICAgY2xlYXJGb3JtKCkge1xuICAgICAgICB0aGlzLiRjb250cm9scy5lYWNoKChpbmRleCwgZWwpID0+IHtcbiAgICAgICAgICAgIGNvbnN0ICRlbCA9ICQoZWwpO1xuICAgICAgICAgICAgaWYgKCEkZWwuYXR0cihcImRpc2FibGVkXCIpKSAgJGVsLnZhbCgnJyk7XG4gICAgICAgIH0pXG4gICAgfVxufVxuXG5cblxuLyoqIFdFQlBBQ0sgRk9PVEVSICoqXG4gKiogLi9mcm9udGVuZC9qcy9jb21tb24vX2Zvcm0taGVscGVyLmpzXG4gKiovIiwiJ3VzZSBzdHJpY3QnO1xuXG5cbmV4cG9ydCBkZWZhdWx0IGNsYXNzIElucHV0Q2hlY2tpbmcge1xuICAgIC8qKlxuICAgICAqIFZhbGlkYXRlIGlucHV0IHRocm91Z2ggYmFja2VuZCB1cmxcbiAgICAgKi9cbiAgICBjb25zdHJ1Y3RvcihkYXRhKSB7XG4gICAgICAgIHRoaXMuJHJvb3QgPSBkYXRhLiRyb290O1xuICAgICAgICB0aGlzLnVybCA9IGRhdGEudXJsO1xuICAgICAgICB0aGlzLmxvY2FscyA9IHRoaXMuX2dldERvbSgpO1xuXG4gICAgICAgIHRoaXMuX2NoZWNrVmFsdWUoKTtcbiAgICAgICAgdGhpcy5fYXNzaWduRXZlbnRzKCk7XG4gICAgfVxuXG4gICAgX2dldERvbSgpIHtcbiAgICAgICAgY29uc3QgJHJvb3QgPSB0aGlzLiRyb290O1xuXG4gICAgICAgIHJldHVybiB7XG4gICAgICAgICAgICAkY29udGVudDogJHJvb3QuZmluZCgnW2RhdGEtaW5wdXRjaGVjay1ibG9ja10nKSxcbiAgICAgICAgICAgICRpbnB1dDogJHJvb3QuZmluZCgnaW5wdXQnKSxcbiAgICAgICAgICAgICRlcnJvcjogJHJvb3QuZmluZCgnW2RhdGEtaW5wdXRjaGVjay1lcnJvcl0nKSxcbiAgICAgICAgICAgICRzdWNjZXNzVGV4dDogJHJvb3QuZmluZCgnW2RhdGEtaW5wdXRjaGVjay10ZXh0XScpXG4gICAgICAgIH07XG4gICAgfVxuXG4gICAgX2Fzc2lnbkV2ZW50cygpIHtcbiAgICAgICAgdGhpcy5sb2NhbHMuJGlucHV0XG4gICAgICAgICAgICAub24oJ2JsdXInLCB0aGlzLl9jaGVja1ZhbHVlLmJpbmQodGhpcykpXG4gICAgICAgICAgICAub24oJ2ZvY3VzJywgdGhpcy5faGlkZUNoZWNraW5nRXJyb3IuYmluZCh0aGlzKSlcbiAgICB9XG5cbiAgICBfY2hlY2tWYWx1ZSgpIHtcbiAgICAgICAgY29uc3Qgc2VsZiA9IHRoaXM7XG4gICAgICAgIGNvbnN0IGxvY2FscyA9IHNlbGYubG9jYWxzO1xuICAgICAgICBjb25zdCB2YWx1ZUlucHV0ID0gbG9jYWxzLiRpbnB1dC52YWwoKTtcblxuICAgICAgICBpZiAoIWxvY2Fscy4kaW5wdXQudmFsKCkpe1xuICAgICAgICAgICAgcmV0dXJuO1xuICAgICAgICB9XG5cbiAgICAgICAgdGhpcy4kcm9vdFxuICAgICAgICAgICAgLnJlbW92ZUNsYXNzKCdiLWlucHV0Y2hlY2tfc3RhdGVfY29tcGxldGUgYi1pbnB1dGNoZWNrX3N0YXRlX2Vycm9yJylcbiAgICAgICAgICAgIC5hZGRDbGFzcygnYi1pbnB1dGNoZWNrX3N0YXRlX2NoZWNraW5nJyk7XG5cbiAgICAgICAgc2VsZi5fc2VuZENoZWNrKHZhbHVlSW5wdXQpXG4gICAgICAgICAgICAuZG9uZShmdW5jdGlvbihyZXNwb25zZSl7XG4gICAgICAgICAgICAgICAgY29uc3Qgc3VjY2Vzc1RleHQgPSAkLnBhcnNlSlNPTihyZXNwb25zZSkubWVzc2FnZTtcbiAgICAgICAgICAgICAgICBzZWxmLl9jb21wbGV0ZUNoZWNraW5nKHN1Y2Nlc3NUZXh0KTtcbiAgICAgICAgICAgIH0pXG4gICAgICAgICAgICAuZmFpbChmdW5jdGlvbihyZXNwb25zZSl7XG4gICAgICAgICAgICAgICAgY29uc3QgZXJyb3IgPSAkLnBhcnNlSlNPTihyZXNwb25zZS5yZXNwb25zZVRleHQpLm1lc3NhZ2U7XG4gICAgICAgICAgICAgICAgc2VsZi5fc2hvd0NoZWNraW5nRXJyb3IoZXJyb3IpO1xuICAgICAgICAgICAgfSlcbiAgICB9XG5cbiAgICAvKipcbiAgICAgKiBTaG93IGVycm9yIHdoZW4gaW5wdXQgdmFsdWUgaXMgaW52YWxpZFxuICAgICAqIEBwYXJhbSB7U3RyaW5nfSBlcnJvclxuICAgICAqIEBwcml2YXRlXG4gICAgICovXG4gICAgX3Nob3dDaGVja2luZ0Vycm9yKGVycm9yKXtcbiAgICAgICAgY29uc3QgbG9jYWxzID0gdGhpcy5sb2NhbHM7XG4gICAgICAgIHRoaXMudmFsaWQgPSBmYWxzZTtcblxuICAgICAgICB0aGlzLiRyb290XG4gICAgICAgICAgICAucmVtb3ZlQ2xhc3MoJ2ItaW5wdXRjaGVja19zdGF0ZV9jaGVja2luZycpXG4gICAgICAgICAgICAuYWRkQ2xhc3MoJ2ItaW5wdXRjaGVja19zdGF0ZV9lcnJvcicpO1xuXG4gICAgICAgIHRoaXMuJHJvb3QudHJpZ2dlcignaW5wdXRfY2hlY2tpbmcuY2hhbmdlJyk7XG4gICAgICAgIGxvY2Fscy4kZXJyb3IudGV4dChlcnJvcik7XG4gICAgfVxuXG4gICAgLyoqXG4gICAgICogU2hvdyBzdWNjZXNzIHRleHQgYWZ0ZXIgY2hlY2tpbmdcbiAgICAgKiBAcGFyYW0ge1N0cmluZ30gc3VjY2Vzc1xuICAgICAqIEBwcml2YXRlXG4gICAgICovXG4gICAgX2NvbXBsZXRlQ2hlY2tpbmcoc3VjY2Vzcyl7XG4gICAgICAgIGNvbnN0IGxvY2FscyA9IHRoaXMubG9jYWxzO1xuICAgICAgICB0aGlzLnZhbGlkID0gdHJ1ZTtcblxuICAgICAgICB0aGlzLiRyb290XG4gICAgICAgICAgICAucmVtb3ZlQ2xhc3MoJ2ItaW5wdXRjaGVja19zdGF0ZV9jaGVja2luZycpXG4gICAgICAgICAgICAuYWRkQ2xhc3MoJ2ItaW5wdXRjaGVja19zdGF0ZV9jb21wbGV0ZScpO1xuICAgICAgICBcbiAgICAgICAgdGhpcy4kcm9vdC50cmlnZ2VyKCdpbnB1dF9jaGVja2luZy5jaGFuZ2UnKTtcbiAgICAgICAgbG9jYWxzLiRzdWNjZXNzVGV4dC50ZXh0KHN1Y2Nlc3MpO1xuICAgIH1cblxuICAgIF9oaWRlQ2hlY2tpbmdFcnJvcigpe1xuICAgICAgICB0aGlzLnZhbGlkID0gZmFsc2U7XG4gICAgICAgIHRoaXMuJHJvb3QucmVtb3ZlQ2xhc3MoJ2ItaW5wdXRjaGVja19zdGF0ZV9lcnJvcicpO1xuICAgIH1cblxuICAgIGlzVmFsaWQoKSB7XG4gICAgICAgIHJldHVybiB0aGlzLnZhbGlkO1xuICAgIH1cblxuICAgIC8vdHJhbnNwb3J0XG4gICAgX3NlbmRDaGVjayh2YWx1ZSl7XG4gICAgICAgIHJldHVybiAkLmdldCh0aGlzLnVybCh2YWx1ZSkudXJsKVxuICAgIH1cbn1cblxuXG5cbi8qKiBXRUJQQUNLIEZPT1RFUiAqKlxuICoqIC4vZnJvbnRlbmQvanMvY29tbW9uL19pbnB1dC1jaGVja2luZy5qc1xuICoqLyIsIid1c2Ugc3RyaWN0JztcblxuaW1wb3J0IE9yZ0Zvcm0gZnJvbSBcIi4vZm9ybS13aWRnZXRzL19vcmdhbml6YXRpb24tZm9ybVwiO1xuXG4kKGZ1bmN0aW9uKCl7XG4gICAgT3JnRm9ybS5wbHVnaW4oJy5qcy1vcmctZm9ybScpO1xufSk7XG5cblxuLyoqIFdFQlBBQ0sgRk9PVEVSICoqXG4gKiogLi9mcm9udGVuZC9qcy9vcmdhbml6YXRpb24vb3JnYW5pemF0aW9uLWZvcm0uanNcbiAqKi8iLCIndXNlIHN0cmljdCc7XG5cbmltcG9ydCBJbnB1dENoZWNraW5nIGZyb20gJy4vLi4vLi4vY29tbW9uL19pbnB1dC1jaGVja2luZyc7XG5pbXBvcnQgRm9ybUhlbHBlciBmcm9tICcuLy4uLy4uL2NvbW1vbi9fZm9ybS1oZWxwZXInO1xuXG5leHBvcnQgZGVmYXVsdCBjbGFzcyBXaWRnZXQge1xuICAgIC8qKlxuICAgICAqIEBwYXJhbSB7U3RyaW5nfSBzZWxlY3RvclxuICAgICAqL1xuICAgIGNvbnN0cnVjdG9yKHNlbGVjdG9yKSB7XG4gICAgICAgIHRoaXMuJHJvb3QgPSAkKHNlbGVjdG9yKTtcbiAgICAgICAgdGhpcy5sb2NhbHMgPSB0aGlzLl9nZXREb20oKTtcbiAgICAgICAgdGhpcy5fY29ycmVjdE9sZFZhdElucHV0KCk7XG5cbiAgICAgICAgdGhpcy5pbnB1dFZBVCA9IG5ldyBJbnB1dENoZWNraW5nKHtcbiAgICAgICAgICAgICRyb290OiB0aGlzLiRyb290LmZpbmQoJy5iLWlucHV0Y2hlY2snKSxcbiAgICAgICAgICAgIHVybDoganNSb3V0ZXMuY29udHJvbGxlcnMuVXRpbGl0aWVzLnZhbGlkYXRlVkFUXG4gICAgICAgIH0pO1xuICAgICAgICB0aGlzLmZvcm1IZWxwZXIgPSBuZXcgRm9ybUhlbHBlcih0aGlzLmxvY2Fscy4kdmF0SW5wdXQpO1xuXG4gICAgICAgIHRoaXMuX2Fzc2lnbkV2ZW50cygpO1xuICAgIH1cblxuICAgIF9jb3JyZWN0T2xkVmF0SW5wdXQoKXtcbiAgICAgICAgY29uc3QgJHZhdElucHV0ID0gdGhpcy5sb2NhbHMuJHZhdElucHV0O1xuXG4gICAgICAgICR2YXRJbnB1dFxuICAgICAgICAgICAgLmFkZENsYXNzKCd0eXBlLW5vc3BhY2UnKVxuICAgICAgICAgICAgLnZhbCgkdmF0SW5wdXQudmFsKCkucmVwbGFjZSgvXFxzL2csICcnKSk7XG4gICAgfVxuXG4gICAgX2dldERvbSgpIHtcbiAgICAgICAgY29uc3QgJHJvb3QgPSB0aGlzLiRyb290O1xuXG4gICAgICAgIHJldHVybiB7XG4gICAgICAgICAgICAkY2FuY2VsOiAkcm9vdC5maW5kKCdbZGF0YS1mb3JtLWNhbmNlbF0nKSxcbiAgICAgICAgICAgICRzdWJtaXQ6ICRyb290LmZpbmQoJ1tkYXRhLWZvcm0tc3VibWl0XScpLFxuICAgICAgICAgICAgJHZhdElucHV0OiAkcm9vdC5maW5kKCcuYi1pbnB1dGNoZWNrIGlucHV0JykuZmlyc3QoKVxuICAgICAgICB9O1xuICAgIH1cblxuICAgIF9hc3NpZ25FdmVudHMoKSB7XG4gICAgICAgIHRoaXMuJHJvb3RcbiAgICAgICAgICAgIC5vbignaW5wdXRfY2hlY2tpbmcuY2hhbmdlJywgdGhpcy5fb25CbHVyVmF0SW5wdXQuYmluZCh0aGlzKSk7XG4gICAgfVxuXG4gICAgX29uQmx1clZhdElucHV0KCl7XG4gICAgICAgIGlmICh0aGlzLmlucHV0VkFULmlzVmFsaWQoKSl7XG4gICAgICAgICAgICB0aGlzLl9lbmFibGVkRm9ybSgpO1xuICAgICAgICB9IGVsc2Uge1xuICAgICAgICAgICAgdGhpcy5fZGlzYWJsZWRGb3JtKCk7XG4gICAgICAgIH1cbiAgICB9XG5cbiAgICBfZGlzYWJsZWRGb3JtKCl7XG4gICAgICAgIHRoaXMubG9jYWxzLiRzdWJtaXQucHJvcCgnZGlzYWJsZWQnLCB0cnVlKTtcbiAgICB9XG5cbiAgICBfZW5hYmxlZEZvcm0oKXtcbiAgICAgICAgdGhpcy5sb2NhbHMuJHN1Ym1pdC5wcm9wKCdkaXNhYmxlZCcsIGZhbHNlKTtcbiAgICB9XG5cbiAgICAvLyBzdGF0aWNcbiAgICBzdGF0aWMgcGx1Z2luKHNlbGVjdG9yKSB7XG4gICAgICAgIGNvbnN0ICRlbGVtcyA9ICQoc2VsZWN0b3IpO1xuICAgICAgICBpZiAoISRlbGVtcy5sZW5ndGgpIHJldHVybjtcblxuICAgICAgICByZXR1cm4gJGVsZW1zLmVhY2goZnVuY3Rpb24gKGluZGV4LCBlbCkge1xuICAgICAgICAgICAgbGV0ICRlbGVtZW50ID0gJChlbCk7XG4gICAgICAgICAgICBsZXQgZGF0YSA9ICRlbGVtZW50LmRhdGEoJ3dpZGdldCcpO1xuXG4gICAgICAgICAgICBpZiAoIWRhdGEpIHtcbiAgICAgICAgICAgICAgICBkYXRhID0gbmV3IFdpZGdldChlbCk7XG4gICAgICAgICAgICAgICAgJGVsZW1lbnQuZGF0YSgnd2lkZ2V0JywgZGF0YSk7XG4gICAgICAgICAgICB9XG4gICAgICAgIH0pXG4gICAgfVxufVxuXG5cblxuXG5cbi8qKiBXRUJQQUNLIEZPT1RFUiAqKlxuICoqIC4vZnJvbnRlbmQvanMvb3JnYW5pemF0aW9uL2Zvcm0td2lkZ2V0cy9fb3JnYW5pemF0aW9uLWZvcm0uanNcbiAqKi8iXSwic291cmNlUm9vdCI6IiJ9