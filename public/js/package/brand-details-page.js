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
	
	var _setCredits = __webpack_require__(2);
	
	var _setCredits2 = _interopRequireDefault(_setCredits);
	
	var _setApi = __webpack_require__(24);
	
	var _setApi2 = _interopRequireDefault(_setApi);
	
	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }
	
	$(function () {
	    App.events.sub('hmt.tab.shown', function () {
	        // Set credits
	        _setCredits2.default.plugin('.js-set-credits');
	
	        //tab api
	        $('[data-toggle="tooltip"]').tooltip();
	        _setApi2.default.plugin('.js-set-api');
	    });
	});

/***/ },
/* 2 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';
	
	Object.defineProperty(exports, "__esModule", {
	    value: true
	});
	
	var _classCallCheck2 = __webpack_require__(3);
	
	var _classCallCheck3 = _interopRequireDefault(_classCallCheck2);
	
	var _createClass2 = __webpack_require__(4);
	
	var _createClass3 = _interopRequireDefault(_createClass2);
	
	var _formHelper = __webpack_require__(23);
	
	var _formHelper2 = _interopRequireDefault(_formHelper);
	
	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }
	
	var Widget = function () {
	    /**
	     * Filter history
	     * @param {String} selector
	     */
	
	    function Widget(selector) {
	        (0, _classCallCheck3.default)(this, Widget);
	
	        this.$root = $(selector);
	        this.locals = this._getDom();
	        this.brandId = this.$root.data('brand-id');
	        this.validation = new _formHelper2.default(this.locals.$input);
	
	        this._assignEvents();
	    }
	
	    (0, _createClass3.default)(Widget, [{
	        key: '_getDom',
	        value: function _getDom() {
	            var $root = this.$root;
	
	            return {
	                $activateBtn: $root.find('[data-setcredit-activate]'),
	                $deActivateBtn: $root.find('[data-setcredit-deactivate]'),
	                $form: $root.find('[data-setcredit-form]'),
	                $input: $root.find('[data-setcredit-input]'),
	                $errors: $root.find('[data-setcredit-errors]')
	            };
	        }
	    }, {
	        key: '_assignEvents',
	        value: function _assignEvents() {
	            var self = this;
	
	            this.$root.on('click', '[data-setcredit-activate]', this._onClickActivate.bind(this)).on('click', '[data-setcredit-deactivate]', this._onClickDeActivate.bind(this)).on('submit', '[data-setcredit-form]', this._onClickSaveCredit.bind(this)).on('input', '[data-setcredit-input]', function (e) {
	                self.$root.removeClass('b-setcredit_state_error');
	                self.locals.$errors.text('');
	            });
	        }
	    }, {
	        key: '_onClickActivate',
	        value: function _onClickActivate(e) {
	            var self = this;
	            e.preventDefault();
	
	            self._sendActivate(self.brandId).done(function () {
	                self.$root.addClass('b-setcredit_state_active');
	            });
	        }
	    }, {
	        key: '_onClickDeActivate',
	        value: function _onClickDeActivate(e) {
	            var self = this;
	            e.preventDefault();
	
	            self._sendDeActivate(self.brandId).done(function () {
	                self.$root.removeClass('b-setcredit_state_active');
	            });
	        }
	    }, {
	        key: '_onClickSaveCredit',
	        value: function _onClickSaveCredit(e) {
	            var self = this;
	            e.preventDefault();
	
	            if (!self.isFormValid()) return;
	
	            self._sendFormData().done(function () {
	                success('Credit limit was updated');
	
	                self.$root.addClass('b-setcredit_state_sended');
	                setTimeout(function () {
	                    self.$root.removeClass('b-setcredit_state_sended');
	                }, 4500);
	            }).fail(function (response) {
	                var data = $.parseJSON(response.responseText).data;
	                var errorText = self.validation.getErrorsText(data.errors);
	
	                if (!data.errors) return;
	
	                self.locals.$error.text(errorText);
	                self.$root.addClass('b-setcredit_state_error');
	                self.validation.setErrors(data.errors);
	            });
	        }
	    }, {
	        key: 'isFormValid',
	        value: function isFormValid() {
	            var locals = this.locals;
	            var isValidCredit = locals.$input.val() > 0;
	            var valid = true;
	            var errorText = '';
	
	            if (!isValidCredit) {
	                valid = false;
	                errorText += 'Spending limit has to be above 0. We recommend set in 100.';
	                this.validation._setError(locals.$input);
	            }
	
	            if (!valid) {
	                this.$root.addClass('b-setcredit_state_error');
	                locals.$errors.text(errorText);
	            }
	
	            return valid;
	        }
	
	        // transport
	
	    }, {
	        key: '_sendActivate',
	        value: function _sendActivate(brandId) {
	            var url = jsRoutes.controllers.cm.brand.Credits.activate(brandId).url;
	            return $.post(url, { brandid: brandId });
	        }
	    }, {
	        key: '_sendDeActivate',
	        value: function _sendDeActivate(brandId) {
	            var url = jsRoutes.controllers.cm.brand.Credits.deactivate(brandId).url;
	            return $.post(url, { brandid: brandId });
	        }
	    }, {
	        key: '_sendFormData',
	        value: function _sendFormData() {
	            var locals = this.locals;
	
	            return $.post(locals.$form.attr('action'), {
	                limit: locals.$input.val()
	            });
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

/***/ },
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
/* 24 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';
	
	Object.defineProperty(exports, "__esModule", {
	    value: true
	});
	
	var _classCallCheck2 = __webpack_require__(3);
	
	var _classCallCheck3 = _interopRequireDefault(_classCallCheck2);
	
	var _createClass2 = __webpack_require__(4);
	
	var _createClass3 = _interopRequireDefault(_createClass2);
	
	var _formHelper = __webpack_require__(23);
	
	var _formHelper2 = _interopRequireDefault(_formHelper);
	
	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }
	
	var Widget = function () {
	    /**
	     * Filter history
	     * @param {String} selector
	     */
	
	    function Widget(selector) {
	        (0, _classCallCheck3.default)(this, Widget);
	
	        this.$root = $(selector);
	        this.locals = this._getDom();
	        this.brandId = this.$root.data('brand-id');
	        this.formData = {};
	        this.formHelper = new _formHelper2.default(this.locals.$inputs);
	
	        this._prepareView(this.locals.$inputs);
	        this._saveFormData(this.locals.$inputs);
	
	        this._assignEvents();
	    }
	
	    (0, _createClass3.default)(Widget, [{
	        key: '_getDom',
	        value: function _getDom() {
	            var $root = this.$root;
	
	            return {
	                $form: $root.find('[data-setapi-form]'),
	                $view: $root.find('[data-setapi-view]'),
	                $modal: $root.find('[data-setapi-modal]'),
	                $inputs: $root.find('.b-apiform__input'),
	                $error: $root.find('[data-setapi-errors]')
	            };
	        }
	    }, {
	        key: '_assignEvents',
	        value: function _assignEvents() {
	            this.$root.on('click', '[data-setapi-activate]', this._onClickActivate.bind(this)).on('click', '[data-setapi-promptbtn]', this._onClickShowPrompt.bind(this)).on('click', '[data-setapi-specify]', this._onClickSpecify.bind(this)).on('click', '[data-setapi-deacticate]', this._onClickDeactivate.bind(this)).on('submit', '[data-setapi-form]', this._onClickSubmit.bind(this)).on('click', '[data-setapi-form-save]', this._onClickSubmit.bind(this)).on('click', '[data-setapi-form-cancel]', this._onClickFormCancel.bind(this)).on('click', '[data-setapi-editform]', this._onClickEditForm.bind(this)).on('click', '.b-apiview__link', this._onClickAddUrl.bind(this));
	        }
	    }, {
	        key: '_onClickActivate',
	        value: function _onClickActivate(e) {
	            e.preventDefault();
	            var self = this;
	            var $root = self.$root;
	
	            self._sendActivate(self.brandId).done(function () {
	                if (!$root.hasClass('b-setapi_state_active')) {
	                    $root.addClass('b-setapi_state_active');
	                }
	                success('API was successfully activated');
	            });
	        }
	    }, {
	        key: '_onClickShowPrompt',
	        value: function _onClickShowPrompt(e) {
	            e.preventDefault();
	            this.locals.$modal.modal('show');
	        }
	    }, {
	        key: '_onClickDeactivate',
	        value: function _onClickDeactivate(e) {
	            var self = this;
	            var $root = self.$root;
	
	            e.preventDefault();
	            e.stopPropagation();
	
	            self._sendDeactivate(self.brandId).done(function () {
	                $root.removeClass('b-setapi_state_active');
	                success('API was successfully deactivated');
	            });
	        }
	    }, {
	        key: '_onClickSpecify',
	        value: function _onClickSpecify(e) {
	            e.preventDefault();
	            var $root = this.$root;
	
	            if (!$root.hasClass('b-setapi_state_form')) {
	                $root.addClass('b-setapi_state_form');
	            }
	        }
	    }, {
	        key: '_onClickSubmit',
	        value: function _onClickSubmit(e) {
	            var _this = this;
	
	            e.preventDefault();
	            var self = this;
	            var $inputs = this.locals.$inputs;
	
	            if (!self.isFormValid()) return;
	
	            var formData = this.formHelper.getFormData();
	            self._sendUrlsData(self.brandId, formData).done(function () {
	                self.locals.$error.html('');
	                _this._saveFormData($inputs);
	                _this._prepareView($inputs);
	                _this._showView();
	
	                success('You are successfully update urls');
	            }).fail(function (response) {
	                var data = $.parseJSON(response.responseText).data;
	                var errorText = self.formHelper.getErrorsFull(data.errors);
	
	                if (!data.errors) return;
	
	                self.formHelper.setErrors(data.errors);
	                self.locals.$error.html(errorText);
	            });
	        }
	    }, {
	        key: '_onClickFormCancel',
	        value: function _onClickFormCancel(e) {
	            e.preventDefault();
	
	            this._restoreFormData(this.locals.$inputs);
	            this.formHelper.removeErrors();
	            this.locals.$error.html('');
	
	            this._showView();
	        }
	    }, {
	        key: '_onClickEditForm',
	        value: function _onClickEditForm(e) {
	            e.preventDefault();
	            this._saveFormData(this.locals.$inputs);
	            this._showForm();
	        }
	    }, {
	        key: '_onClickAddUrl',
	        value: function _onClickAddUrl(e) {
	            e.preventDefault();
	            var field = $(e.currentTarget).closest('[data-setapi-field]').data('setapi-field');
	
	            this._showForm($.trim(field));
	        }
	
	        /**
	         * Show form with api urls
	         * @param {String} field - name of the field that you need to hightlight
	         * @private
	         */
	
	    }, {
	        key: '_showForm',
	        value: function _showForm() {
	            var field = arguments.length <= 0 || arguments[0] === undefined ? '' : arguments[0];
	
	            var $root = this.$root;
	            var locals = this.locals;
	
	            if (!$root.hasClass('b-setapi_state_form')) {
	                $root.addClass('b-setapi_state_form').removeClass('b-setapi_state_view');
	            }
	
	            var selector = field ? 'input[name="' + field + '"]' : 'input';
	
	            locals.$form.find(selector).first().trigger('focus');
	        }
	    }, {
	        key: '_showView',
	        value: function _showView() {
	            var $root = this.$root;
	
	            if (!$root.hasClass('b-setapi_state_view')) {
	                $root.addClass('b-setapi_state_view').removeClass('b-setapi_state_form');
	            }
	        }
	
	        /**
	         * Restore form data, if you cancel editing form
	         * @param {jQuery} $inputs - list of given inputs
	         * @private
	         */
	
	    }, {
	        key: '_restoreFormData',
	        value: function _restoreFormData($inputs) {
	            var data = this.formData;
	            $inputs.each(function (index, el) {
	                var $el = $(el);
	                var value = data[$el.attr('name')];
	                $el.val(value);
	            });
	        }
	
	        /**
	         * Save form data, if you start editing form
	         * @param {jQuery} $inputs - list of given inputs
	         * @private
	         */
	
	    }, {
	        key: '_saveFormData',
	        value: function _saveFormData($inputs) {
	            var data = {};
	            $inputs.each(function (index, el) {
	                var $el = $(el);
	                var name = $el.attr('name');
	
	                if (name) {
	                    data[name] = $el.val();
	                }
	            });
	
	            this.formData = data;
	        }
	
	        /**
	         * Prepare view based on given inputs;
	         * @param $inputs
	         * @private
	         */
	
	    }, {
	        key: '_prepareView',
	        value: function _prepareView($inputs) {
	            var $view = this.locals.$view;
	
	            $inputs.each(function (index, el) {
	                var $el = $(el);
	                var name = $el.attr('name');
	                var $viewPlace = $view.find('[data-setapi-field="' + name + '"]');
	
	                if (!$viewPlace.length) return false;
	
	                var valueInput = $el.val();
	                if ($.trim(valueInput)) {
	                    $viewPlace.removeClass('state_url').find('.b-apiview__text').text(valueInput);
	                } else {
	                    $viewPlace.addClass('state_url').find('.b-apiview__text').text('');
	                }
	            });
	        }
	
	        /**
	         * Check, is form valid through form helper;
	         * @returns {boolean}
	         */
	
	    }, {
	        key: 'isFormValid',
	        value: function isFormValid() {
	            var locals = this.locals;
	            var valid = true;
	
	            if (!this.formHelper.isValidInputs()) {
	                valid = false;
	            }
	
	            return valid;
	        }
	
	        // transport
	
	    }, {
	        key: '_sendActivate',
	        value: function _sendActivate(brandId) {
	            var url = jsRoutes.controllers.cm.brand.API.activate(brandId).url;
	            return $.post(url);
	        }
	    }, {
	        key: '_sendDeactivate',
	        value: function _sendDeactivate(brandId) {
	            var url = jsRoutes.controllers.cm.brand.API.deactivate(brandId).url;
	            return $.post(url);
	        }
	    }, {
	        key: '_sendUrlsData',
	        value: function _sendUrlsData(brandId, formData) {
	            var url = jsRoutes.controllers.cm.brand.API.update(brandId).url;
	            return $.post(url, formData);
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
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIndlYnBhY2s6Ly8vd2VicGFjay9ib290c3RyYXAgZjBmMTY2OTBiZDk3YTI1NWNmYjk/N2VmNCIsIndlYnBhY2s6Ly8vLi9mcm9udGVuZC9qcy9icmFuZC9icmFuZC1kZXRhaWxzLXBhZ2UuanMiLCJ3ZWJwYWNrOi8vLy4vZnJvbnRlbmQvanMvYnJhbmQvd2lkZ2V0cy9fc2V0LWNyZWRpdHMuanMiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL2hlbHBlcnMvY2xhc3NDYWxsQ2hlY2suanM/MjFhZiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvaGVscGVycy9jcmVhdGVDbGFzcy5qcz8xZGZlIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9jb3JlLWpzL29iamVjdC9kZWZpbmUtcHJvcGVydHkuanM/NWY3MCIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvZm4vb2JqZWN0L2RlZmluZS1wcm9wZXJ0eS5qcz80ZDMzIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL2VzNi5vYmplY3QuZGVmaW5lLXByb3BlcnR5LmpzPzhiZGUiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2V4cG9ydC5qcz84ZTQwIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19nbG9iYWwuanM/M2M1MiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fY29yZS5qcz9kNjExIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19jdHguanM/NGU1OSIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fYS1mdW5jdGlvbi5qcz8wNjk5Iiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19oaWRlLmpzPzBkMmUiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX29iamVjdC1kcC5qcz81OTg2Iiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19hbi1vYmplY3QuanM/M2FmMiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9faXMtb2JqZWN0LmpzP2NmZGEiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2llOC1kb20tZGVmaW5lLmpzP2IxMDIiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2Rlc2NyaXB0b3JzLmpzP2MwZjUiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2ZhaWxzLmpzP2M2ZGQiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2RvbS1jcmVhdGUuanM/NjEyZiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fdG8tcHJpbWl0aXZlLmpzPzFhNjUiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX3Byb3BlcnR5LWRlc2MuanM/MjU2YiIsIndlYnBhY2s6Ly8vLi9mcm9udGVuZC9qcy9jb21tb24vX2Zvcm0taGVscGVyLmpzIiwid2VicGFjazovLy8uL2Zyb250ZW5kL2pzL2JyYW5kL3dpZGdldHMvX3NldC1hcGkuanMiXSwibmFtZXMi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QUFxQixFQUFyQixDQUF3QixLQUF4QixDQUE4QixHQUE5QixDQUFrQyxRQUFsQyxDQUEyQyxPQUEzQyxFQUFvRCxHQUFwRCxDQURNO0FBRWxCLG9CQUFPLEVBQUUsSUFBRixDQUFPLEdBQVAsQ0FBUCxDQUZrQjs7Ozt5Q0FLTixTQUFRO0FBQ3BCLGlCQUFNLE1BQU0sU0FBUyxXQUFULENBQXFCLEVBQXJCLENBQXdCLEtBQXhCLENBQThCLEdBQTlCLENBQWtDLFVBQWxDLENBQTZDLE9BQTdDLEVBQXNELEdBQXRELENBRFE7QUFFcEIsb0JBQU8sRUFBRSxJQUFGLENBQU8sR0FBUCxDQUFQLENBRm9COzs7O3VDQUtWLFNBQVMsVUFBUztBQUM1QixpQkFBTSxNQUFNLFNBQVMsV0FBVCxDQUFxQixFQUFyQixDQUF3QixLQUF4QixDQUE4QixHQUE5QixDQUFrQyxNQUFsQyxDQUF5QyxPQUF6QyxFQUFrRCxHQUFsRCxDQURnQjtBQUU1QixvQkFBTyxFQUFFLElBQUYsQ0FBTyxHQUFQLEVBQVksUUFBWixDQUFQLENBRjRCOzs7Ozs7O2dDQU1sQixVQUFVO0FBQ3BCLGlCQUFNLFNBQVMsRUFBRSxRQUFGLENBQVQsQ0FEYztBQUVwQixpQkFBSSxDQUFDLE9BQU8sTUFBUCxFQUFlLE9BQXBCOztBQUVBLG9CQUFPLE9BQU8sSUFBUCxDQUFZLFVBQVUsS0FBVixFQUFpQixFQUFqQixFQUFxQjtBQUNwQyxxQkFBSSxXQUFXLEVBQUUsRUFBRixDQUFYLENBRGdDO0FBRXBDLHFCQUFJLE9BQU8sU0FBUyxJQUFULENBQWMsUUFBZCxDQUFQLENBRmdDOztBQUlwQyxxQkFBSSxDQUFDLElBQUQsRUFBTztBQUNQLDRCQUFPLElBQUksTUFBSixDQUFXLEVBQVgsQ0FBUCxDQURPO0FBRVAsOEJBQVMsSUFBVCxDQUFjLFFBQWQsRUFBd0IsSUFBeEIsRUFGTztrQkFBWDtjQUplLENBQW5CLENBSm9COzs7WUF0UVAiLCJmaWxlIjoiYnJhbmQtZGV0YWlscy1wYWdlLmpzIiwic291cmNlc0NvbnRlbnQiOlsiIFx0Ly8gVGhlIG1vZHVsZSBjYWNoZVxuIFx0dmFyIGluc3RhbGxlZE1vZHVsZXMgPSB7fTtcblxuIFx0Ly8gVGhlIHJlcXVpcmUgZnVuY3Rpb25cbiBcdGZ1bmN0aW9uIF9fd2VicGFja19yZXF1aXJlX18obW9kdWxlSWQpIHtcblxuIFx0XHQvLyBDaGVjayBpZiBtb2R1bGUgaXMgaW4gY2FjaGVcbiBcdFx0aWYoaW5zdGFsbGVkTW9kdWxlc1ttb2R1bGVJZF0pXG4gXHRcdFx0cmV0dXJuIGluc3RhbGxlZE1vZHVsZXNbbW9kdWxlSWRdLmV4cG9ydHM7XG5cbiBcdFx0Ly8gQ3JlYXRlIGEgbmV3IG1vZHVsZSAoYW5kIHB1dCBpdCBpbnRvIHRoZSBjYWNoZSlcbiBcdFx0dmFyIG1vZHVsZSA9IGluc3RhbGxlZE1vZHVsZXNbbW9kdWxlSWRdID0ge1xuIFx0XHRcdGV4cG9ydHM6IHt9LFxuIFx0XHRcdGlkOiBtb2R1bGVJZCxcbiBcdFx0XHRsb2FkZWQ6IGZhbHNlXG4gXHRcdH07XG5cbiBcdFx0Ly8gRXhlY3V0ZSB0aGUgbW9kdWxlIGZ1bmN0aW9uXG4gXHRcdG1vZHVsZXNbbW9kdWxlSWRdLmNhbGwobW9kdWxlLmV4cG9ydHMsIG1vZHVsZSwgbW9kdWxlLmV4cG9ydHMsIF9fd2VicGFja19yZXF1aXJlX18pO1xuXG4gXHRcdC8vIEZsYWcgdGhlIG1vZHVsZSBhcyBsb2FkZWRcbiBcdFx0bW9kdWxlLmxvYWRlZCA9IHRydWU7XG5cbiBcdFx0Ly8gUmV0dXJuIHRoZSBleHBvcnRzIG9mIHRoZSBtb2R1bGVcbiBcdFx0cmV0dXJuIG1vZHVsZS5leHBvcnRzO1xuIFx0fVxuXG5cbiBcdC8vIGV4cG9zZSB0aGUgbW9kdWxlcyBvYmplY3QgKF9fd2VicGFja19tb2R1bGVzX18pXG4gXHRfX3dlYnBhY2tfcmVxdWlyZV9fLm0gPSBtb2R1bGVzO1xuXG4gXHQvLyBleHBvc2UgdGhlIG1vZHVsZSBjYWNoZVxuIFx0X193ZWJwYWNrX3JlcXVpcmVfXy5jID0gaW5zdGFsbGVkTW9kdWxlcztcblxuIFx0Ly8gX193ZWJwYWNrX3B1YmxpY19wYXRoX19cbiBcdF9fd2VicGFja19yZXF1aXJlX18ucCA9IFwiXCI7XG5cbiBcdC8vIExvYWQgZW50cnkgbW9kdWxlIGFuZCByZXR1cm4gZXhwb3J0c1xuIFx0cmV0dXJuIF9fd2VicGFja19yZXF1aXJlX18oMCk7XG5cblxuXG4vKiogV0VCUEFDSyBGT09URVIgKipcbiAqKiB3ZWJwYWNrL2Jvb3RzdHJhcCBmMGYxNjY5MGJkOTdhMjU1Y2ZiOVxuICoqLyIsIid1c2Ugc3RyaWN0JztcblxuaW1wb3J0IFNldENyZWRpdHMgZnJvbSBcIi4vd2lkZ2V0cy9fc2V0LWNyZWRpdHNcIjtcbmltcG9ydCBTZXRBcGkgZnJvbSBcIi4vd2lkZ2V0cy9fc2V0LWFwaVwiO1xuXG4kKGZ1bmN0aW9uKCl7XG4gICAgQXBwLmV2ZW50c1xuICAgICAgICAuc3ViKCdobXQudGFiLnNob3duJywgZnVuY3Rpb24oKXtcbiAgICAgICAgICAgIC8vIFNldCBjcmVkaXRzXG4gICAgICAgICAgICBTZXRDcmVkaXRzLnBsdWdpbignLmpzLXNldC1jcmVkaXRzJyk7XG5cbiAgICAgICAgICAgIC8vdGFiIGFwaVxuICAgICAgICAgICAgJCgnW2RhdGEtdG9nZ2xlPVwidG9vbHRpcFwiXScpLnRvb2x0aXAoKTtcbiAgICAgICAgICAgIFNldEFwaS5wbHVnaW4oJy5qcy1zZXQtYXBpJyk7XG5cbiAgICAgICAgfSlcbn0pO1xuXG5cbi8qKiBXRUJQQUNLIEZPT1RFUiAqKlxuICoqIC4vZnJvbnRlbmQvanMvYnJhbmQvYnJhbmQtZGV0YWlscy1wYWdlLmpzXG4gKiovIiwiJ3VzZSBzdHJpY3QnO1xuXG5pbXBvcnQgRm9ybUhlbHBlciBmcm9tIFwiLi8uLi8uLi9jb21tb24vX2Zvcm0taGVscGVyXCI7XG5cblxuZXhwb3J0IGRlZmF1bHQgY2xhc3MgV2lkZ2V0IHtcbiAgICAvKipcbiAgICAgKiBGaWx0ZXIgaGlzdG9yeVxuICAgICAqIEBwYXJhbSB7U3RyaW5nfSBzZWxlY3RvclxuICAgICAqL1xuICAgIGNvbnN0cnVjdG9yKHNlbGVjdG9yKSB7XG4gICAgICAgIHRoaXMuJHJvb3QgPSAkKHNlbGVjdG9yKTtcbiAgICAgICAgdGhpcy5sb2NhbHMgPSB0aGlzLl9nZXREb20oKTtcbiAgICAgICAgdGhpcy5icmFuZElkID0gdGhpcy4kcm9vdC5kYXRhKCdicmFuZC1pZCcpO1xuICAgICAgICB0aGlzLnZhbGlkYXRpb24gPSBuZXcgRm9ybUhlbHBlcih0aGlzLmxvY2Fscy4kaW5wdXQpO1xuXG4gICAgICAgIHRoaXMuX2Fzc2lnbkV2ZW50cygpO1xuICAgIH1cblxuICAgIF9nZXREb20oKSB7XG4gICAgICAgIGNvbnN0ICRyb290ID0gdGhpcy4kcm9vdDtcblxuICAgICAgICByZXR1cm4ge1xuICAgICAgICAgICAgJGFjdGl2YXRlQnRuOiAkcm9vdC5maW5kKCdbZGF0YS1zZXRjcmVkaXQtYWN0aXZhdGVdJyksXG4gICAgICAgICAgICAkZGVBY3RpdmF0ZUJ0bjogJHJvb3QuZmluZCgnW2RhdGEtc2V0Y3JlZGl0LWRlYWN0aXZhdGVdJyksXG4gICAgICAgICAgICAkZm9ybTogJHJvb3QuZmluZCgnW2RhdGEtc2V0Y3JlZGl0LWZvcm1dJyksXG4gICAgICAgICAgICAkaW5wdXQ6ICRyb290LmZpbmQoJ1tkYXRhLXNldGNyZWRpdC1pbnB1dF0nKSxcbiAgICAgICAgICAgICRlcnJvcnM6ICRyb290LmZpbmQoJ1tkYXRhLXNldGNyZWRpdC1lcnJvcnNdJylcbiAgICAgICAgfTtcbiAgICB9XG5cbiAgICBfYXNzaWduRXZlbnRzKCkge1xuICAgICAgICBjb25zdCBzZWxmID0gdGhpcztcblxuICAgICAgICB0aGlzLiRyb290XG4gICAgICAgICAgICAub24oJ2NsaWNrJywgJ1tkYXRhLXNldGNyZWRpdC1hY3RpdmF0ZV0nLCB0aGlzLl9vbkNsaWNrQWN0aXZhdGUuYmluZCh0aGlzKSlcbiAgICAgICAgICAgIC5vbignY2xpY2snLCAnW2RhdGEtc2V0Y3JlZGl0LWRlYWN0aXZhdGVdJywgdGhpcy5fb25DbGlja0RlQWN0aXZhdGUuYmluZCh0aGlzKSlcbiAgICAgICAgICAgIC5vbignc3VibWl0JywgJ1tkYXRhLXNldGNyZWRpdC1mb3JtXScsIHRoaXMuX29uQ2xpY2tTYXZlQ3JlZGl0LmJpbmQodGhpcykpXG4gICAgICAgICAgICAub24oJ2lucHV0JywgJ1tkYXRhLXNldGNyZWRpdC1pbnB1dF0nLCAoZSkgPT4ge1xuICAgICAgICAgICAgICAgIHNlbGYuJHJvb3QucmVtb3ZlQ2xhc3MoJ2Itc2V0Y3JlZGl0X3N0YXRlX2Vycm9yJyk7XG4gICAgICAgICAgICAgICAgc2VsZi5sb2NhbHMuJGVycm9ycy50ZXh0KCcnKVxuICAgICAgICAgICAgfSlcbiAgICB9XG5cbiAgICBfb25DbGlja0FjdGl2YXRlKGUpIHtcbiAgICAgICAgY29uc3Qgc2VsZiA9IHRoaXM7XG4gICAgICAgIGUucHJldmVudERlZmF1bHQoKTtcblxuICAgICAgICBzZWxmLl9zZW5kQWN0aXZhdGUoc2VsZi5icmFuZElkKVxuICAgICAgICAgICAgLmRvbmUoKCk9PiB7XG4gICAgICAgICAgICAgICAgc2VsZi4kcm9vdC5hZGRDbGFzcygnYi1zZXRjcmVkaXRfc3RhdGVfYWN0aXZlJyk7XG4gICAgICAgICAgICB9KVxuICAgIH1cblxuICAgIF9vbkNsaWNrRGVBY3RpdmF0ZShlKSB7XG4gICAgICAgIGNvbnN0IHNlbGYgPSB0aGlzO1xuICAgICAgICBlLnByZXZlbnREZWZhdWx0KCk7XG5cbiAgICAgICAgc2VsZi5fc2VuZERlQWN0aXZhdGUoc2VsZi5icmFuZElkKVxuICAgICAgICAgICAgLmRvbmUoKCk9PiB7XG4gICAgICAgICAgICAgICAgc2VsZi4kcm9vdC5yZW1vdmVDbGFzcygnYi1zZXRjcmVkaXRfc3RhdGVfYWN0aXZlJyk7XG4gICAgICAgICAgICB9KVxuICAgIH1cblxuICAgIF9vbkNsaWNrU2F2ZUNyZWRpdChlKSB7XG4gICAgICAgIGNvbnN0IHNlbGYgPSB0aGlzO1xuICAgICAgICBlLnByZXZlbnREZWZhdWx0KCk7XG5cbiAgICAgICAgaWYgKCFzZWxmLmlzRm9ybVZhbGlkKCkpIHJldHVybjtcblxuICAgICAgICBzZWxmLl9zZW5kRm9ybURhdGEoKVxuICAgICAgICAgICAgLmRvbmUoKCk9PiB7XG4gICAgICAgICAgICAgICAgc3VjY2VzcygnQ3JlZGl0IGxpbWl0IHdhcyB1cGRhdGVkJyk7XG5cbiAgICAgICAgICAgICAgICBzZWxmLiRyb290LmFkZENsYXNzKCdiLXNldGNyZWRpdF9zdGF0ZV9zZW5kZWQnKTtcbiAgICAgICAgICAgICAgICBzZXRUaW1lb3V0KGZ1bmN0aW9uKCl7XG4gICAgICAgICAgICAgICAgICAgIHNlbGYuJHJvb3QucmVtb3ZlQ2xhc3MoJ2Itc2V0Y3JlZGl0X3N0YXRlX3NlbmRlZCcpO1xuICAgICAgICAgICAgICAgIH0sIDQ1MDApO1xuICAgICAgICAgICAgfSlcbiAgICAgICAgICAgIC5mYWlsKChyZXNwb25zZSk9PiB7XG4gICAgICAgICAgICAgICAgY29uc3QgZGF0YSA9ICQucGFyc2VKU09OKHJlc3BvbnNlLnJlc3BvbnNlVGV4dCkuZGF0YTtcbiAgICAgICAgICAgICAgICBjb25zdCBlcnJvclRleHQgPSBzZWxmLnZhbGlkYXRpb24uZ2V0RXJyb3JzVGV4dChkYXRhLmVycm9ycyk7XG5cbiAgICAgICAgICAgICAgICBpZiAoIWRhdGEuZXJyb3JzKSByZXR1cm47XG5cbiAgICAgICAgICAgICAgICBzZWxmLmxvY2Fscy4kZXJyb3IudGV4dChlcnJvclRleHQpO1xuICAgICAgICAgICAgICAgIHNlbGYuJHJvb3QuYWRkQ2xhc3MoJ2Itc2V0Y3JlZGl0X3N0YXRlX2Vycm9yJyk7XG4gICAgICAgICAgICAgICAgc2VsZi52YWxpZGF0aW9uLnNldEVycm9ycyhkYXRhLmVycm9ycyk7XG4gICAgICAgICAgICB9KVxuICAgIH1cblxuICAgIGlzRm9ybVZhbGlkKCkge1xuICAgICAgICBjb25zdCBsb2NhbHMgPSB0aGlzLmxvY2FscztcbiAgICAgICAgY29uc3QgaXNWYWxpZENyZWRpdCA9IChsb2NhbHMuJGlucHV0LnZhbCgpID4gMCk7XG4gICAgICAgIGxldCB2YWxpZCA9IHRydWU7XG4gICAgICAgIGxldCBlcnJvclRleHQgPSAnJztcblxuICAgICAgICBpZiAoIWlzVmFsaWRDcmVkaXQpe1xuICAgICAgICAgICAgdmFsaWQgPSBmYWxzZTtcbiAgICAgICAgICAgIGVycm9yVGV4dCArPSAnU3BlbmRpbmcgbGltaXQgaGFzIHRvIGJlIGFib3ZlIDAuIFdlIHJlY29tbWVuZCBzZXQgaW4gMTAwLic7XG4gICAgICAgICAgICB0aGlzLnZhbGlkYXRpb24uX3NldEVycm9yKGxvY2Fscy4kaW5wdXQpO1xuICAgICAgICB9XG4gICAgICAgIFxuICAgICAgICBpZiAoIXZhbGlkKXtcbiAgICAgICAgICAgIHRoaXMuJHJvb3QuYWRkQ2xhc3MoJ2Itc2V0Y3JlZGl0X3N0YXRlX2Vycm9yJyk7XG4gICAgICAgICAgICBsb2NhbHMuJGVycm9ycy50ZXh0KGVycm9yVGV4dCk7XG4gICAgICAgIH1cbiAgICAgICAgXG4gICAgICAgIHJldHVybiB2YWxpZDtcbiAgICB9XG5cbiAgICAvLyB0cmFuc3BvcnRcbiAgICBfc2VuZEFjdGl2YXRlKGJyYW5kSWQpIHtcbiAgICAgICAgdmFyIHVybCA9IGpzUm91dGVzLmNvbnRyb2xsZXJzLmNtLmJyYW5kLkNyZWRpdHMuYWN0aXZhdGUoYnJhbmRJZCkudXJsO1xuICAgICAgICByZXR1cm4gJC5wb3N0KHVybCwge2JyYW5kaWQ6IGJyYW5kSWR9KTtcbiAgICB9XG5cbiAgICBfc2VuZERlQWN0aXZhdGUoYnJhbmRJZCkge1xuICAgICAgICB2YXIgdXJsID0ganNSb3V0ZXMuY29udHJvbGxlcnMuY20uYnJhbmQuQ3JlZGl0cy5kZWFjdGl2YXRlKGJyYW5kSWQpLnVybDtcbiAgICAgICAgcmV0dXJuICQucG9zdCh1cmwsIHticmFuZGlkOiBicmFuZElkfSk7XG4gICAgfVxuXG4gICAgX3NlbmRGb3JtRGF0YSgpIHtcbiAgICAgICAgY29uc3QgbG9jYWxzID0gdGhpcy5sb2NhbHM7XG5cbiAgICAgICAgcmV0dXJuICQucG9zdChsb2NhbHMuJGZvcm0uYXR0cignYWN0aW9uJyksIHtcbiAgICAgICAgICAgIGxpbWl0OiBsb2NhbHMuJGlucHV0LnZhbCgpXG4gICAgICAgIH0pXG4gICAgfVxuXG4gICAgLy8gc3RhdGljXG4gICAgc3RhdGljIHBsdWdpbihzZWxlY3Rvcikge1xuICAgICAgICBjb25zdCAkZWxlbXMgPSAkKHNlbGVjdG9yKTtcbiAgICAgICAgaWYgKCEkZWxlbXMubGVuZ3RoKSByZXR1cm47XG5cbiAgICAgICAgcmV0dXJuICRlbGVtcy5lYWNoKGZ1bmN0aW9uIChpbmRleCwgZWwpIHtcbiAgICAgICAgICAgIGxldCAkZWxlbWVudCA9ICQoZWwpO1xuICAgICAgICAgICAgbGV0IGRhdGEgPSAkZWxlbWVudC5kYXRhKCd3aWRnZXQnKTtcblxuICAgICAgICAgICAgaWYgKCFkYXRhKSB7XG4gICAgICAgICAgICAgICAgZGF0YSA9IG5ldyBXaWRnZXQoZWwpO1xuICAgICAgICAgICAgICAgICRlbGVtZW50LmRhdGEoJ3dpZGdldCcsIGRhdGEpO1xuICAgICAgICAgICAgfVxuICAgICAgICB9KVxuICAgIH1cbn1cblxuXG5cblxuXG4vKiogV0VCUEFDSyBGT09URVIgKipcbiAqKiAuL2Zyb250ZW5kL2pzL2JyYW5kL3dpZGdldHMvX3NldC1jcmVkaXRzLmpzXG4gKiovIiwiXCJ1c2Ugc3RyaWN0XCI7XG5cbmV4cG9ydHMuX19lc01vZHVsZSA9IHRydWU7XG5cbmV4cG9ydHMuZGVmYXVsdCA9IGZ1bmN0aW9uIChpbnN0YW5jZSwgQ29uc3RydWN0b3IpIHtcbiAgaWYgKCEoaW5zdGFuY2UgaW5zdGFuY2VvZiBDb25zdHJ1Y3RvcikpIHtcbiAgICB0aHJvdyBuZXcgVHlwZUVycm9yKFwiQ2Fubm90IGNhbGwgYSBjbGFzcyBhcyBhIGZ1bmN0aW9uXCIpO1xuICB9XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvaGVscGVycy9jbGFzc0NhbGxDaGVjay5qc1xuICoqIG1vZHVsZSBpZCA9IDNcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsIlwidXNlIHN0cmljdFwiO1xuXG5leHBvcnRzLl9fZXNNb2R1bGUgPSB0cnVlO1xuXG52YXIgX2RlZmluZVByb3BlcnR5ID0gcmVxdWlyZShcImJhYmVsLXJ1bnRpbWUvY29yZS1qcy9vYmplY3QvZGVmaW5lLXByb3BlcnR5XCIpO1xuXG52YXIgX2RlZmluZVByb3BlcnR5MiA9IF9pbnRlcm9wUmVxdWlyZURlZmF1bHQoX2RlZmluZVByb3BlcnR5KTtcblxuZnVuY3Rpb24gX2ludGVyb3BSZXF1aXJlRGVmYXVsdChvYmopIHsgcmV0dXJuIG9iaiAmJiBvYmouX19lc01vZHVsZSA/IG9iaiA6IHsgZGVmYXVsdDogb2JqIH07IH1cblxuZXhwb3J0cy5kZWZhdWx0ID0gZnVuY3Rpb24gKCkge1xuICBmdW5jdGlvbiBkZWZpbmVQcm9wZXJ0aWVzKHRhcmdldCwgcHJvcHMpIHtcbiAgICBmb3IgKHZhciBpID0gMDsgaSA8IHByb3BzLmxlbmd0aDsgaSsrKSB7XG4gICAgICB2YXIgZGVzY3JpcHRvciA9IHByb3BzW2ldO1xuICAgICAgZGVzY3JpcHRvci5lbnVtZXJhYmxlID0gZGVzY3JpcHRvci5lbnVtZXJhYmxlIHx8IGZhbHNlO1xuICAgICAgZGVzY3JpcHRvci5jb25maWd1cmFibGUgPSB0cnVlO1xuICAgICAgaWYgKFwidmFsdWVcIiBpbiBkZXNjcmlwdG9yKSBkZXNjcmlwdG9yLndyaXRhYmxlID0gdHJ1ZTtcbiAgICAgICgwLCBfZGVmaW5lUHJvcGVydHkyLmRlZmF1bHQpKHRhcmdldCwgZGVzY3JpcHRvci5rZXksIGRlc2NyaXB0b3IpO1xuICAgIH1cbiAgfVxuXG4gIHJldHVybiBmdW5jdGlvbiAoQ29uc3RydWN0b3IsIHByb3RvUHJvcHMsIHN0YXRpY1Byb3BzKSB7XG4gICAgaWYgKHByb3RvUHJvcHMpIGRlZmluZVByb3BlcnRpZXMoQ29uc3RydWN0b3IucHJvdG90eXBlLCBwcm90b1Byb3BzKTtcbiAgICBpZiAoc3RhdGljUHJvcHMpIGRlZmluZVByb3BlcnRpZXMoQ29uc3RydWN0b3IsIHN0YXRpY1Byb3BzKTtcbiAgICByZXR1cm4gQ29uc3RydWN0b3I7XG4gIH07XG59KCk7XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9oZWxwZXJzL2NyZWF0ZUNsYXNzLmpzXG4gKiogbW9kdWxlIGlkID0gNFxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwibW9kdWxlLmV4cG9ydHMgPSB7IFwiZGVmYXVsdFwiOiByZXF1aXJlKFwiY29yZS1qcy9saWJyYXJ5L2ZuL29iamVjdC9kZWZpbmUtcHJvcGVydHlcIiksIF9fZXNNb2R1bGU6IHRydWUgfTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL2NvcmUtanMvb2JqZWN0L2RlZmluZS1wcm9wZXJ0eS5qc1xuICoqIG1vZHVsZSBpZCA9IDVcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsInJlcXVpcmUoJy4uLy4uL21vZHVsZXMvZXM2Lm9iamVjdC5kZWZpbmUtcHJvcGVydHknKTtcbnZhciAkT2JqZWN0ID0gcmVxdWlyZSgnLi4vLi4vbW9kdWxlcy9fY29yZScpLk9iamVjdDtcbm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24gZGVmaW5lUHJvcGVydHkoaXQsIGtleSwgZGVzYyl7XG4gIHJldHVybiAkT2JqZWN0LmRlZmluZVByb3BlcnR5KGl0LCBrZXksIGRlc2MpO1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L2ZuL29iamVjdC9kZWZpbmUtcHJvcGVydHkuanNcbiAqKiBtb2R1bGUgaWQgPSA2XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJ2YXIgJGV4cG9ydCA9IHJlcXVpcmUoJy4vX2V4cG9ydCcpO1xyXG4vLyAxOS4xLjIuNCAvIDE1LjIuMy42IE9iamVjdC5kZWZpbmVQcm9wZXJ0eShPLCBQLCBBdHRyaWJ1dGVzKVxyXG4kZXhwb3J0KCRleHBvcnQuUyArICRleHBvcnQuRiAqICFyZXF1aXJlKCcuL19kZXNjcmlwdG9ycycpLCAnT2JqZWN0Jywge2RlZmluZVByb3BlcnR5OiByZXF1aXJlKCcuL19vYmplY3QtZHAnKS5mfSk7XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL2VzNi5vYmplY3QuZGVmaW5lLXByb3BlcnR5LmpzXG4gKiogbW9kdWxlIGlkID0gN1xuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwidmFyIGdsb2JhbCAgICA9IHJlcXVpcmUoJy4vX2dsb2JhbCcpXG4gICwgY29yZSAgICAgID0gcmVxdWlyZSgnLi9fY29yZScpXG4gICwgY3R4ICAgICAgID0gcmVxdWlyZSgnLi9fY3R4JylcbiAgLCBoaWRlICAgICAgPSByZXF1aXJlKCcuL19oaWRlJylcbiAgLCBQUk9UT1RZUEUgPSAncHJvdG90eXBlJztcblxudmFyICRleHBvcnQgPSBmdW5jdGlvbih0eXBlLCBuYW1lLCBzb3VyY2Upe1xuICB2YXIgSVNfRk9SQ0VEID0gdHlwZSAmICRleHBvcnQuRlxuICAgICwgSVNfR0xPQkFMID0gdHlwZSAmICRleHBvcnQuR1xuICAgICwgSVNfU1RBVElDID0gdHlwZSAmICRleHBvcnQuU1xuICAgICwgSVNfUFJPVE8gID0gdHlwZSAmICRleHBvcnQuUFxuICAgICwgSVNfQklORCAgID0gdHlwZSAmICRleHBvcnQuQlxuICAgICwgSVNfV1JBUCAgID0gdHlwZSAmICRleHBvcnQuV1xuICAgICwgZXhwb3J0cyAgID0gSVNfR0xPQkFMID8gY29yZSA6IGNvcmVbbmFtZV0gfHwgKGNvcmVbbmFtZV0gPSB7fSlcbiAgICAsIGV4cFByb3RvICA9IGV4cG9ydHNbUFJPVE9UWVBFXVxuICAgICwgdGFyZ2V0ICAgID0gSVNfR0xPQkFMID8gZ2xvYmFsIDogSVNfU1RBVElDID8gZ2xvYmFsW25hbWVdIDogKGdsb2JhbFtuYW1lXSB8fCB7fSlbUFJPVE9UWVBFXVxuICAgICwga2V5LCBvd24sIG91dDtcbiAgaWYoSVNfR0xPQkFMKXNvdXJjZSA9IG5hbWU7XG4gIGZvcihrZXkgaW4gc291cmNlKXtcbiAgICAvLyBjb250YWlucyBpbiBuYXRpdmVcbiAgICBvd24gPSAhSVNfRk9SQ0VEICYmIHRhcmdldCAmJiB0YXJnZXRba2V5XSAhPT0gdW5kZWZpbmVkO1xuICAgIGlmKG93biAmJiBrZXkgaW4gZXhwb3J0cyljb250aW51ZTtcbiAgICAvLyBleHBvcnQgbmF0aXZlIG9yIHBhc3NlZFxuICAgIG91dCA9IG93biA/IHRhcmdldFtrZXldIDogc291cmNlW2tleV07XG4gICAgLy8gcHJldmVudCBnbG9iYWwgcG9sbHV0aW9uIGZvciBuYW1lc3BhY2VzXG4gICAgZXhwb3J0c1trZXldID0gSVNfR0xPQkFMICYmIHR5cGVvZiB0YXJnZXRba2V5XSAhPSAnZnVuY3Rpb24nID8gc291cmNlW2tleV1cbiAgICAvLyBiaW5kIHRpbWVycyB0byBnbG9iYWwgZm9yIGNhbGwgZnJvbSBleHBvcnQgY29udGV4dFxuICAgIDogSVNfQklORCAmJiBvd24gPyBjdHgob3V0LCBnbG9iYWwpXG4gICAgLy8gd3JhcCBnbG9iYWwgY29uc3RydWN0b3JzIGZvciBwcmV2ZW50IGNoYW5nZSB0aGVtIGluIGxpYnJhcnlcbiAgICA6IElTX1dSQVAgJiYgdGFyZ2V0W2tleV0gPT0gb3V0ID8gKGZ1bmN0aW9uKEMpe1xuICAgICAgdmFyIEYgPSBmdW5jdGlvbihhLCBiLCBjKXtcbiAgICAgICAgaWYodGhpcyBpbnN0YW5jZW9mIEMpe1xuICAgICAgICAgIHN3aXRjaChhcmd1bWVudHMubGVuZ3RoKXtcbiAgICAgICAgICAgIGNhc2UgMDogcmV0dXJuIG5ldyBDO1xuICAgICAgICAgICAgY2FzZSAxOiByZXR1cm4gbmV3IEMoYSk7XG4gICAgICAgICAgICBjYXNlIDI6IHJldHVybiBuZXcgQyhhLCBiKTtcbiAgICAgICAgICB9IHJldHVybiBuZXcgQyhhLCBiLCBjKTtcbiAgICAgICAgfSByZXR1cm4gQy5hcHBseSh0aGlzLCBhcmd1bWVudHMpO1xuICAgICAgfTtcbiAgICAgIEZbUFJPVE9UWVBFXSA9IENbUFJPVE9UWVBFXTtcbiAgICAgIHJldHVybiBGO1xuICAgIC8vIG1ha2Ugc3RhdGljIHZlcnNpb25zIGZvciBwcm90b3R5cGUgbWV0aG9kc1xuICAgIH0pKG91dCkgOiBJU19QUk9UTyAmJiB0eXBlb2Ygb3V0ID09ICdmdW5jdGlvbicgPyBjdHgoRnVuY3Rpb24uY2FsbCwgb3V0KSA6IG91dDtcbiAgICAvLyBleHBvcnQgcHJvdG8gbWV0aG9kcyB0byBjb3JlLiVDT05TVFJVQ1RPUiUubWV0aG9kcy4lTkFNRSVcbiAgICBpZihJU19QUk9UTyl7XG4gICAgICAoZXhwb3J0cy52aXJ0dWFsIHx8IChleHBvcnRzLnZpcnR1YWwgPSB7fSkpW2tleV0gPSBvdXQ7XG4gICAgICAvLyBleHBvcnQgcHJvdG8gbWV0aG9kcyB0byBjb3JlLiVDT05TVFJVQ1RPUiUucHJvdG90eXBlLiVOQU1FJVxuICAgICAgaWYodHlwZSAmICRleHBvcnQuUiAmJiBleHBQcm90byAmJiAhZXhwUHJvdG9ba2V5XSloaWRlKGV4cFByb3RvLCBrZXksIG91dCk7XG4gICAgfVxuICB9XG59O1xuLy8gdHlwZSBiaXRtYXBcbiRleHBvcnQuRiA9IDE7ICAgLy8gZm9yY2VkXG4kZXhwb3J0LkcgPSAyOyAgIC8vIGdsb2JhbFxuJGV4cG9ydC5TID0gNDsgICAvLyBzdGF0aWNcbiRleHBvcnQuUCA9IDg7ICAgLy8gcHJvdG9cbiRleHBvcnQuQiA9IDE2OyAgLy8gYmluZFxuJGV4cG9ydC5XID0gMzI7ICAvLyB3cmFwXG4kZXhwb3J0LlUgPSA2NDsgIC8vIHNhZmVcbiRleHBvcnQuUiA9IDEyODsgLy8gcmVhbCBwcm90byBtZXRob2QgZm9yIGBsaWJyYXJ5YCBcbm1vZHVsZS5leHBvcnRzID0gJGV4cG9ydDtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2V4cG9ydC5qc1xuICoqIG1vZHVsZSBpZCA9IDhcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsIi8vIGh0dHBzOi8vZ2l0aHViLmNvbS96bG9pcm9jay9jb3JlLWpzL2lzc3Vlcy84NiNpc3N1ZWNvbW1lbnQtMTE1NzU5MDI4XG52YXIgZ2xvYmFsID0gbW9kdWxlLmV4cG9ydHMgPSB0eXBlb2Ygd2luZG93ICE9ICd1bmRlZmluZWQnICYmIHdpbmRvdy5NYXRoID09IE1hdGhcbiAgPyB3aW5kb3cgOiB0eXBlb2Ygc2VsZiAhPSAndW5kZWZpbmVkJyAmJiBzZWxmLk1hdGggPT0gTWF0aCA/IHNlbGYgOiBGdW5jdGlvbigncmV0dXJuIHRoaXMnKSgpO1xuaWYodHlwZW9mIF9fZyA9PSAnbnVtYmVyJylfX2cgPSBnbG9iYWw7IC8vIGVzbGludC1kaXNhYmxlLWxpbmUgbm8tdW5kZWZcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2dsb2JhbC5qc1xuICoqIG1vZHVsZSBpZCA9IDlcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsInZhciBjb3JlID0gbW9kdWxlLmV4cG9ydHMgPSB7dmVyc2lvbjogJzIuMi4wJ307XG5pZih0eXBlb2YgX19lID09ICdudW1iZXInKV9fZSA9IGNvcmU7IC8vIGVzbGludC1kaXNhYmxlLWxpbmUgbm8tdW5kZWZcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2NvcmUuanNcbiAqKiBtb2R1bGUgaWQgPSAxMFxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwiLy8gb3B0aW9uYWwgLyBzaW1wbGUgY29udGV4dCBiaW5kaW5nXG52YXIgYUZ1bmN0aW9uID0gcmVxdWlyZSgnLi9fYS1mdW5jdGlvbicpO1xubW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihmbiwgdGhhdCwgbGVuZ3RoKXtcbiAgYUZ1bmN0aW9uKGZuKTtcbiAgaWYodGhhdCA9PT0gdW5kZWZpbmVkKXJldHVybiBmbjtcbiAgc3dpdGNoKGxlbmd0aCl7XG4gICAgY2FzZSAxOiByZXR1cm4gZnVuY3Rpb24oYSl7XG4gICAgICByZXR1cm4gZm4uY2FsbCh0aGF0LCBhKTtcbiAgICB9O1xuICAgIGNhc2UgMjogcmV0dXJuIGZ1bmN0aW9uKGEsIGIpe1xuICAgICAgcmV0dXJuIGZuLmNhbGwodGhhdCwgYSwgYik7XG4gICAgfTtcbiAgICBjYXNlIDM6IHJldHVybiBmdW5jdGlvbihhLCBiLCBjKXtcbiAgICAgIHJldHVybiBmbi5jYWxsKHRoYXQsIGEsIGIsIGMpO1xuICAgIH07XG4gIH1cbiAgcmV0dXJuIGZ1bmN0aW9uKC8qIC4uLmFyZ3MgKi8pe1xuICAgIHJldHVybiBmbi5hcHBseSh0aGF0LCBhcmd1bWVudHMpO1xuICB9O1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2N0eC5qc1xuICoqIG1vZHVsZSBpZCA9IDExXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJtb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGl0KXtcbiAgaWYodHlwZW9mIGl0ICE9ICdmdW5jdGlvbicpdGhyb3cgVHlwZUVycm9yKGl0ICsgJyBpcyBub3QgYSBmdW5jdGlvbiEnKTtcbiAgcmV0dXJuIGl0O1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2EtZnVuY3Rpb24uanNcbiAqKiBtb2R1bGUgaWQgPSAxMlxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwidmFyIGRQICAgICAgICAgPSByZXF1aXJlKCcuL19vYmplY3QtZHAnKVxuICAsIGNyZWF0ZURlc2MgPSByZXF1aXJlKCcuL19wcm9wZXJ0eS1kZXNjJyk7XG5tb2R1bGUuZXhwb3J0cyA9IHJlcXVpcmUoJy4vX2Rlc2NyaXB0b3JzJykgPyBmdW5jdGlvbihvYmplY3QsIGtleSwgdmFsdWUpe1xuICByZXR1cm4gZFAuZihvYmplY3QsIGtleSwgY3JlYXRlRGVzYygxLCB2YWx1ZSkpO1xufSA6IGZ1bmN0aW9uKG9iamVjdCwga2V5LCB2YWx1ZSl7XG4gIG9iamVjdFtrZXldID0gdmFsdWU7XG4gIHJldHVybiBvYmplY3Q7XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9faGlkZS5qc1xuICoqIG1vZHVsZSBpZCA9IDEzXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJ2YXIgYW5PYmplY3QgICAgICAgPSByZXF1aXJlKCcuL19hbi1vYmplY3QnKVxuICAsIElFOF9ET01fREVGSU5FID0gcmVxdWlyZSgnLi9faWU4LWRvbS1kZWZpbmUnKVxuICAsIHRvUHJpbWl0aXZlICAgID0gcmVxdWlyZSgnLi9fdG8tcHJpbWl0aXZlJylcbiAgLCBkUCAgICAgICAgICAgICA9IE9iamVjdC5kZWZpbmVQcm9wZXJ0eTtcblxuZXhwb3J0cy5mID0gcmVxdWlyZSgnLi9fZGVzY3JpcHRvcnMnKSA/IE9iamVjdC5kZWZpbmVQcm9wZXJ0eSA6IGZ1bmN0aW9uIGRlZmluZVByb3BlcnR5KE8sIFAsIEF0dHJpYnV0ZXMpe1xuICBhbk9iamVjdChPKTtcbiAgUCA9IHRvUHJpbWl0aXZlKFAsIHRydWUpO1xuICBhbk9iamVjdChBdHRyaWJ1dGVzKTtcbiAgaWYoSUU4X0RPTV9ERUZJTkUpdHJ5IHtcbiAgICByZXR1cm4gZFAoTywgUCwgQXR0cmlidXRlcyk7XG4gIH0gY2F0Y2goZSl7IC8qIGVtcHR5ICovIH1cbiAgaWYoJ2dldCcgaW4gQXR0cmlidXRlcyB8fCAnc2V0JyBpbiBBdHRyaWJ1dGVzKXRocm93IFR5cGVFcnJvcignQWNjZXNzb3JzIG5vdCBzdXBwb3J0ZWQhJyk7XG4gIGlmKCd2YWx1ZScgaW4gQXR0cmlidXRlcylPW1BdID0gQXR0cmlidXRlcy52YWx1ZTtcbiAgcmV0dXJuIE87XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fb2JqZWN0LWRwLmpzXG4gKiogbW9kdWxlIGlkID0gMTRcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsInZhciBpc09iamVjdCA9IHJlcXVpcmUoJy4vX2lzLW9iamVjdCcpO1xubW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihpdCl7XG4gIGlmKCFpc09iamVjdChpdCkpdGhyb3cgVHlwZUVycm9yKGl0ICsgJyBpcyBub3QgYW4gb2JqZWN0IScpO1xuICByZXR1cm4gaXQ7XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fYW4tb2JqZWN0LmpzXG4gKiogbW9kdWxlIGlkID0gMTVcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsIm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oaXQpe1xuICByZXR1cm4gdHlwZW9mIGl0ID09PSAnb2JqZWN0JyA/IGl0ICE9PSBudWxsIDogdHlwZW9mIGl0ID09PSAnZnVuY3Rpb24nO1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2lzLW9iamVjdC5qc1xuICoqIG1vZHVsZSBpZCA9IDE2XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJtb2R1bGUuZXhwb3J0cyA9ICFyZXF1aXJlKCcuL19kZXNjcmlwdG9ycycpICYmICFyZXF1aXJlKCcuL19mYWlscycpKGZ1bmN0aW9uKCl7XHJcbiAgcmV0dXJuIE9iamVjdC5kZWZpbmVQcm9wZXJ0eShyZXF1aXJlKCcuL19kb20tY3JlYXRlJykoJ2RpdicpLCAnYScsIHtnZXQ6IGZ1bmN0aW9uKCl7IHJldHVybiA3OyB9fSkuYSAhPSA3O1xyXG59KTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2llOC1kb20tZGVmaW5lLmpzXG4gKiogbW9kdWxlIGlkID0gMTdcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsIi8vIFRoYW5rJ3MgSUU4IGZvciBoaXMgZnVubnkgZGVmaW5lUHJvcGVydHlcbm1vZHVsZS5leHBvcnRzID0gIXJlcXVpcmUoJy4vX2ZhaWxzJykoZnVuY3Rpb24oKXtcbiAgcmV0dXJuIE9iamVjdC5kZWZpbmVQcm9wZXJ0eSh7fSwgJ2EnLCB7Z2V0OiBmdW5jdGlvbigpeyByZXR1cm4gNzsgfX0pLmEgIT0gNztcbn0pO1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZGVzY3JpcHRvcnMuanNcbiAqKiBtb2R1bGUgaWQgPSAxOFxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwibW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihleGVjKXtcbiAgdHJ5IHtcbiAgICByZXR1cm4gISFleGVjKCk7XG4gIH0gY2F0Y2goZSl7XG4gICAgcmV0dXJuIHRydWU7XG4gIH1cbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19mYWlscy5qc1xuICoqIG1vZHVsZSBpZCA9IDE5XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJ2YXIgaXNPYmplY3QgPSByZXF1aXJlKCcuL19pcy1vYmplY3QnKVxuICAsIGRvY3VtZW50ID0gcmVxdWlyZSgnLi9fZ2xvYmFsJykuZG9jdW1lbnRcbiAgLy8gaW4gb2xkIElFIHR5cGVvZiBkb2N1bWVudC5jcmVhdGVFbGVtZW50IGlzICdvYmplY3QnXG4gICwgaXMgPSBpc09iamVjdChkb2N1bWVudCkgJiYgaXNPYmplY3QoZG9jdW1lbnQuY3JlYXRlRWxlbWVudCk7XG5tb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGl0KXtcbiAgcmV0dXJuIGlzID8gZG9jdW1lbnQuY3JlYXRlRWxlbWVudChpdCkgOiB7fTtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19kb20tY3JlYXRlLmpzXG4gKiogbW9kdWxlIGlkID0gMjBcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsIi8vIDcuMS4xIFRvUHJpbWl0aXZlKGlucHV0IFssIFByZWZlcnJlZFR5cGVdKVxudmFyIGlzT2JqZWN0ID0gcmVxdWlyZSgnLi9faXMtb2JqZWN0Jyk7XG4vLyBpbnN0ZWFkIG9mIHRoZSBFUzYgc3BlYyB2ZXJzaW9uLCB3ZSBkaWRuJ3QgaW1wbGVtZW50IEBAdG9QcmltaXRpdmUgY2FzZVxuLy8gYW5kIHRoZSBzZWNvbmQgYXJndW1lbnQgLSBmbGFnIC0gcHJlZmVycmVkIHR5cGUgaXMgYSBzdHJpbmdcbm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oaXQsIFMpe1xuICBpZighaXNPYmplY3QoaXQpKXJldHVybiBpdDtcbiAgdmFyIGZuLCB2YWw7XG4gIGlmKFMgJiYgdHlwZW9mIChmbiA9IGl0LnRvU3RyaW5nKSA9PSAnZnVuY3Rpb24nICYmICFpc09iamVjdCh2YWwgPSBmbi5jYWxsKGl0KSkpcmV0dXJuIHZhbDtcbiAgaWYodHlwZW9mIChmbiA9IGl0LnZhbHVlT2YpID09ICdmdW5jdGlvbicgJiYgIWlzT2JqZWN0KHZhbCA9IGZuLmNhbGwoaXQpKSlyZXR1cm4gdmFsO1xuICBpZighUyAmJiB0eXBlb2YgKGZuID0gaXQudG9TdHJpbmcpID09ICdmdW5jdGlvbicgJiYgIWlzT2JqZWN0KHZhbCA9IGZuLmNhbGwoaXQpKSlyZXR1cm4gdmFsO1xuICB0aHJvdyBUeXBlRXJyb3IoXCJDYW4ndCBjb252ZXJ0IG9iamVjdCB0byBwcmltaXRpdmUgdmFsdWVcIik7XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fdG8tcHJpbWl0aXZlLmpzXG4gKiogbW9kdWxlIGlkID0gMjFcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsIm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oYml0bWFwLCB2YWx1ZSl7XG4gIHJldHVybiB7XG4gICAgZW51bWVyYWJsZSAgOiAhKGJpdG1hcCAmIDEpLFxuICAgIGNvbmZpZ3VyYWJsZTogIShiaXRtYXAgJiAyKSxcbiAgICB3cml0YWJsZSAgICA6ICEoYml0bWFwICYgNCksXG4gICAgdmFsdWUgICAgICAgOiB2YWx1ZVxuICB9O1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX3Byb3BlcnR5LWRlc2MuanNcbiAqKiBtb2R1bGUgaWQgPSAyMlxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwiJ3VzZSBzdHJpY3QnO1xuXG4vKipcbiAqIEVycm9yc1xuICogQHR5cGVkZWYge09iamVjdH0gTGlzdEVycm9yc1xuICogQHByb3BlcnR5IHtTdHJpbmd9IG5hbWUgLSBuYW1lIG9mIGZpZWxkXG4gKiBAcHJvcGVydHkge1N0cmluZ30gZXJyb3IgLSBlcnJvciBkZXNjcmlwdGlvblxuICovXG5cbmV4cG9ydCBkZWZhdWx0IGNsYXNzIEZvcm1IZWxwZXIge1xuICAgIC8qKlxuICAgICAqIFZhbGlkYXRlIGZvcm0gdGhyb3VnaCBpbnB1dHNcbiAgICAgKiBAcGFyYW0ge2pRdWVyeX0gJGNvbnRyb2xzXG4gICAgICovXG4gICAgY29uc3RydWN0b3IoJGNvbnRyb2xzKSB7XG4gICAgICAgIHRoaXMuJGNvbnRyb2xzID0gJGNvbnRyb2xzO1xuICAgICAgICB0aGlzLmFyckVycm9ycyA9IFtdO1xuICAgICAgICB0aGlzLl9hc3NpZ25FdmVudHMoKTtcbiAgICB9XG5cbiAgICBfYXNzaWduRXZlbnRzKCkge1xuICAgICAgICB0aGlzLiRjb250cm9scy5vbignaW5wdXQgY2hhbmdlJywgKGUpID0+IHtcbiAgICAgICAgICAgIGNvbnN0ICRjb250cm9sID0gJChlLmN1cnJlbnRUYXJnZXQpO1xuXG4gICAgICAgICAgICB0aGlzLl92YWxpZGF0ZUltbWVkaWF0ZSgkY29udHJvbCk7XG4gICAgICAgICAgICB0aGlzLl9yZW1vdmVFcnJvcigkY29udHJvbCk7XG4gICAgICAgIH0pO1xuICAgIH1cblxuICAgIF92YWxpZGF0ZUltbWVkaWF0ZSgkY29udHJvbCl7XG4gICAgICAgIGlmICgkY29udHJvbC5oYXNDbGFzcygndHlwZS1udW1lcmljJykpIHtcbiAgICAgICAgICAgICRjb250cm9sLnZhbCgkY29udHJvbC52YWwoKS5yZXBsYWNlKC9bXlxcZF0rL2csICcnKSk7XG4gICAgICAgIH1cblxuICAgICAgICBpZiAoJGNvbnRyb2wuaGFzQ2xhc3MoJ3R5cGUtbm9zcGFjZScpKSB7XG4gICAgICAgICAgICAkY29udHJvbC52YWwoJGNvbnRyb2wudmFsKCkucmVwbGFjZSgvXFxzL2csICcnKSk7XG4gICAgICAgIH1cbiAgICB9XG5cbiAgICBpc1ZhbGlkSW5wdXRzKCkge1xuICAgICAgICBjb25zdCAkY29udHJvbHMgPSB0aGlzLiRjb250cm9scztcbiAgICAgICAgbGV0IGVycm9yID0gMDtcblxuICAgICAgICAkY29udHJvbHMuZWFjaCgoaW5kZXgsIGNvbnRyb2wpID0+IHtcbiAgICAgICAgICAgIGNvbnN0ICRjb250cm9sID0gJChjb250cm9sKTtcblxuICAgICAgICAgICAgaWYgKCF0aGlzLl9pc1ZhbGlkSW5wdXQoJGNvbnRyb2wpKSB7XG4gICAgICAgICAgICAgICAgZXJyb3IgKz0gMTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgfSk7XG4gICAgICAgIHJldHVybiBCb29sZWFuKCFlcnJvcik7XG4gICAgfVxuXG4gICAgLyoqXG4gICAgICogQ2hlY2sgZ2l2ZW4gY29udHJvbCwgaXMgaXQgdmFsaWQ/XG4gICAgICogQHBhcmFtIHtqUXVlcnl9ICRjb250cm9sXG4gICAgICogQHJldHVybnMge2Jvb2xlYW59IC0gSXMgdmFsaWQgY29udHJvbD9cbiAgICAgKi9cbiAgICBfaXNWYWxpZElucHV0KCRjb250cm9sKSB7XG4gICAgICAgIGNvbnN0IHZhbHVlID0gJC50cmltKCRjb250cm9sLnZhbCgpKTtcblxuICAgICAgICBpZiAoIXZhbHVlICYmICEkY29udHJvbC5oYXNDbGFzcygndHlwZS1vcHRpb25hbCcpKSB7XG4gICAgICAgICAgICB0aGlzLl9zZXRFcnJvcigkY29udHJvbCwgJ0VtcHR5Jyk7XG4gICAgICAgICAgICByZXR1cm4gZmFsc2U7XG4gICAgICAgIH1cblxuICAgICAgICBpZiAoKCRjb250cm9sLmhhc0NsYXNzKCd0eXBlLWVtYWlsJykpICYmICF0aGlzLl9pc1ZhbGlkRW1haWwodmFsdWUpKSB7XG4gICAgICAgICAgICB0aGlzLl9zZXRFcnJvcigkY29udHJvbCwgJ0VtYWlsIGlzIG5vdCB2YWxpZCcpO1xuICAgICAgICAgICAgcmV0dXJuIGZhbHNlO1xuICAgICAgICB9XG5cbiAgICAgICAgcmV0dXJuIHRydWU7XG4gICAgfVxuXG4gICAgLyoqXG4gICAgICogSXMgRW1haWwgdmFsaWQ/XG4gICAgICogQHBhcmFtIHtzdHJpbmd9IGVtYWlsXG4gICAgICogQHJldHVybnMge2Jvb2xlYW59XG4gICAgICovXG4gICAgX2lzVmFsaWRFbWFpbChlbWFpbCkge1xuICAgICAgICB2YXIgcmUgPSAvXigoW148PigpXFxbXFxdXFxcXC4sOzpcXHNAXCJdKyhcXC5bXjw+KClcXFtcXF1cXFxcLiw7Olxcc0BcIl0rKSopfChcIi4rXCIpKUAoKFxcW1swLTldezEsM31cXC5bMC05XXsxLDN9XFwuWzAtOV17MSwzfVxcLlswLTldezEsM31dKXwoKFthLXpBLVpcXC0wLTldK1xcLikrW2EtekEtWl17Mix9KSkkLztcbiAgICAgICAgcmV0dXJuIHJlLnRlc3QoZW1haWwpO1xuICAgIH1cblxuICAgIC8qKlxuICAgICAqIFNldCBlcnJvciBmb3IgY29udHJvbFxuICAgICAqIEBwYXJhbSB7alF1ZXJ5fSAkY29udHJvbFxuICAgICAqIEBwYXJhbSB7U3RyaW5nfSBlcnJvclRleHRcbiAgICAgKiBAcGFyYW0ge0Jvb2xlYW59IGluc2VydEVycm9yXG4gICAgICovXG4gICAgX3NldEVycm9yKCRjb250cm9sLCBlcnJvclRleHQsIGluc2VydEVycm9yID0gdHJ1ZSkge1xuICAgICAgICBjb25zdCAkcGFyZW50ID0gJGNvbnRyb2wucGFyZW50KCk7XG4gICAgICAgIGNvbnN0ICRlcnJvciA9ICRwYXJlbnQuZmluZCgnLmItZXJyb3InKTtcblxuICAgICAgICBpZiAoJGVycm9yLmxlbmd0aCkgcmV0dXJuO1xuXG4gICAgICAgICRwYXJlbnQuYWRkQ2xhc3MoJ2ItZXJyb3Jfc2hvdycpO1xuICAgICAgICBcbiAgICAgICAgaW5zZXJ0RXJyb3IgJiYgJCgnPGRpdiBjbGFzcz1cImItZXJyb3JcIiAvPicpXG4gICAgICAgICAgICAudGV4dChlcnJvclRleHQpXG4gICAgICAgICAgICAuYXBwZW5kVG8oJHBhcmVudCk7XG5cbiAgICAgICAgdGhpcy5hcnJFcnJvcnMucHVzaCh7XG4gICAgICAgICAgICBuYW1lOiAkY29udHJvbC5hdHRyKCduYW1lJyksXG4gICAgICAgICAgICBlcnJvcjogZXJyb3JUZXh0XG4gICAgICAgIH0pXG4gICAgfVxuXG4gICAgLyoqXG4gICAgICogUmVtb3ZlIGVycm9yIGZvciBjb250cm9sXG4gICAgICogQHBhcmFtIHtqUXVlcnl9ICRjb250cm9sXG4gICAgICovXG4gICAgX3JlbW92ZUVycm9yKCRjb250cm9sKSB7XG4gICAgICAgIGNvbnN0ICRwYXJlbnQgPSAkY29udHJvbC5wYXJlbnQoKTtcblxuICAgICAgICAkcGFyZW50XG4gICAgICAgICAgICAucmVtb3ZlQ2xhc3MoJ2ItZXJyb3Jfc2hvdycpXG4gICAgICAgICAgICAuZmluZCgnLmItZXJyb3InKS5yZW1vdmUoKTtcblxuICAgICAgICB0aGlzLmFyckVycm9ycyA9IHRoaXMuYXJyRXJyb3JzLmZpbHRlcihmdW5jdGlvbiAoaXRlbSkge1xuICAgICAgICAgICAgcmV0dXJuIGl0ZW0ubmFtZSAhPT0gJGNvbnRyb2wuYXR0cignbmFtZScpXG4gICAgICAgIH0pXG4gICAgfVxuXG4gICAgLyoqXG4gICAgICogU2V0IGVycm9yc1xuICAgICAqIEBwYXJhbSB7QXJyYXl9IGVycm9ycyAtIFt7bmFtZTogXCJlbWFpbFwiLCBlcnJvcjogXCJlbXB0eVwifSwge25hbWU6IFwicGFzc3dvcmRcIiwgZXJyb3I6IFwiZW1wdHlcIn1dXG4gICAgICogQHBhcmFtIHtCb29sZWFufSBpbnNlcnRFcnJvciAtIGluc2VydCBlcnJvciBkZXNjcmlwdGlvbiB0byB0aGUgRG9tIFxuICAgICAqL1xuICAgIHNldEVycm9ycyhlcnJvcnMsIGluc2VydEVycm9yID0gdHJ1ZSkge1xuICAgICAgICBlcnJvcnMuZm9yRWFjaCgoaXRlbSkgPT4ge1xuICAgICAgICAgICAgY29uc3QgJGN1cnJlbnRDb250cm9sID0gdGhpcy4kY29udHJvbHMuZmlsdGVyKCdbbmFtZT1cIicgKyBpdGVtLm5hbWUgKyAnXCJdJykuZmlyc3QoKTtcblxuICAgICAgICAgICAgaWYgKCRjdXJyZW50Q29udHJvbC5sZW5ndGgpIHRoaXMuX3NldEVycm9yKCRjdXJyZW50Q29udHJvbCwgaXRlbS5lcnJvciwgaW5zZXJ0RXJyb3IpXG4gICAgICAgIH0pXG4gICAgfVxuXG4gICAgLyoqXG4gICAgICogR2V0IHRleHQgdmVyc2lvbiBvZiBlcnJvcnMgaW4gb25lIGxpbmUuXG4gICAgICogQHBhcmFtIHtMaXN0RXJyb3JzfSBlcnJvcnNcbiAgICAgKiBAcmV0dXJucyB7c3RyaW5nfVxuICAgICAqL1xuICAgIGdldEVycm9yc1RleHQoZXJyb3JzKSB7XG4gICAgICAgIGNvbnN0IGFyckVycm9ycyA9IGVycm9ycyB8fCB0aGlzLmFyckVycm9ycztcbiAgICAgICAgbGV0IGVycm9yVHh0ID0gJyc7XG5cbiAgICAgICAgYXJyRXJyb3JzLmZvckVhY2goKGl0ZW0pID0+IHtcbiAgICAgICAgICAgIGNvbnN0IG5hbWUgPSBpdGVtLm5hbWVbMF0udG9VcHBlckNhc2UoKSArIGl0ZW0ubmFtZS5zdWJzdHIoMSk7XG5cbiAgICAgICAgICAgIGVycm9yVHh0ICs9IGAke25hbWV9OiAke2l0ZW0uZXJyb3J9LiBgO1xuICAgICAgICB9KTtcblxuICAgICAgICByZXR1cm4gZXJyb3JUeHQ7XG4gICAgfVxuXG4gICAgLyoqXG4gICAgICogR2V0IGxpc3Qgb2YgZXJyb3JzIHdpdGggZnVsbCB0aXRsZSAoZnJvbSBjb250cm9sIHRpdGxlIGF0dHJpYnV0ZSlcbiAgICAgKiBAcGFyYW0ge0xpc3RFcnJvcnN9IGVycm9ycyAtIGxpc3Qgb2YgZXJyb3JzXG4gICAgICogQHJldHVybnMge3N0cmluZ31cbiAgICAgKi9cbiAgICBnZXRFcnJvcnNGdWxsKGVycm9ycykge1xuICAgICAgICBjb25zdCBzZWxmID0gdGhpcztcbiAgICAgICAgY29uc3QgYXJyRXJyb3JzID0gZXJyb3JzIHx8IHRoaXMuYXJyRXJyb3JzO1xuICAgICAgICBsZXQgZXJyb3JUeHQgPSAnJztcblxuICAgICAgICBhcnJFcnJvcnMuZm9yRWFjaCgoaXRlbSkgPT4ge1xuICAgICAgICAgICAgY29uc3QgJGNvbnRyb2wgPSBzZWxmLiRjb250cm9scy5maWx0ZXIoYFtuYW1lPVwiJHtpdGVtLm5hbWV9XCJdYCkuZmlyc3QoKTtcbiAgICAgICAgICAgIGNvbnN0IG5hbWUgPSAkY29udHJvbC5sZW5ndGg/ICRjb250cm9sLmF0dHIoJ3RpdGxlJyk6IGl0ZW0ubmFtZTtcblxuICAgICAgICAgICAgZXJyb3JUeHQgKz0gYDxiPiR7bmFtZX08L2I+OiAke2l0ZW0uZXJyb3J9LiAgPGJyPjxicj5gO1xuICAgICAgICB9KTtcblxuICAgICAgICByZXR1cm4gZXJyb3JUeHQ7XG4gICAgfVxuXG4gICAgZ2V0Rm9ybURhdGEoKXtcbiAgICAgICAgbGV0IGFqYXhEYXRhID0ge307XG5cbiAgICAgICAgdGhpcy4kY29udHJvbHMubWFwKChpbmRleCwgZWwpID0+IHtcbiAgICAgICAgICAgIGNvbnN0ICRlbCA9ICQoZWwpO1xuICAgICAgICAgICAgY29uc3QgbmFtZSA9ICRlbC5hdHRyKCduYW1lJyk7XG5cbiAgICAgICAgICAgIGlmICghbmFtZSkgcmV0dXJuO1xuXG4gICAgICAgICAgICBpZiAoJGVsLmlzKCc6Y2hlY2tib3gnKSl7XG4gICAgICAgICAgICAgICAgYWpheERhdGFbbmFtZV0gPSAkZWwucHJvcCgnY2hlY2tlZCcpXG4gICAgICAgICAgICB9IGVsc2Uge1xuICAgICAgICAgICAgICAgIGFqYXhEYXRhW25hbWVdID0gJGVsLnZhbCgpXG4gICAgICAgICAgICB9XG4gICAgICAgIH0pO1xuXG4gICAgICAgIHJldHVybiBhamF4RGF0YTtcbiAgICB9XG5cbiAgICAvKipcbiAgICAgKiBSZW1vdmUgYWxsIGVycm9yc1xuICAgICAqL1xuICAgIHJlbW92ZUVycm9ycygpIHtcbiAgICAgICAgdGhpcy4kY29udHJvbHMuZWFjaCgoaW5kZXgsIGVsKSA9PiB7XG4gICAgICAgICAgICBjb25zdCAkZWwgPSAkKGVsKTtcbiAgICAgICAgICAgIHRoaXMuX3JlbW92ZUVycm9yKCRlbClcbiAgICAgICAgfSlcbiAgICB9XG5cbiAgICBjbGVhckZvcm0oKSB7XG4gICAgICAgIHRoaXMuJGNvbnRyb2xzLmVhY2goKGluZGV4LCBlbCkgPT4ge1xuICAgICAgICAgICAgY29uc3QgJGVsID0gJChlbCk7XG4gICAgICAgICAgICBpZiAoISRlbC5hdHRyKFwiZGlzYWJsZWRcIikpICAkZWwudmFsKCcnKTtcbiAgICAgICAgfSlcbiAgICB9XG59XG5cblxuXG4vKiogV0VCUEFDSyBGT09URVIgKipcbiAqKiAuL2Zyb250ZW5kL2pzL2NvbW1vbi9fZm9ybS1oZWxwZXIuanNcbiAqKi8iLCIndXNlIHN0cmljdCc7XG5cbmltcG9ydCBGb3JtSGVscGVyIGZyb20gXCIuLy4uLy4uL2NvbW1vbi9fZm9ybS1oZWxwZXJcIjtcblxuXG5leHBvcnQgZGVmYXVsdCBjbGFzcyBXaWRnZXQge1xuICAgIC8qKlxuICAgICAqIEZpbHRlciBoaXN0b3J5XG4gICAgICogQHBhcmFtIHtTdHJpbmd9IHNlbGVjdG9yXG4gICAgICovXG4gICAgY29uc3RydWN0b3Ioc2VsZWN0b3IpIHtcbiAgICAgICAgdGhpcy4kcm9vdCA9ICQoc2VsZWN0b3IpO1xuICAgICAgICB0aGlzLmxvY2FscyA9IHRoaXMuX2dldERvbSgpO1xuICAgICAgICB0aGlzLmJyYW5kSWQgPSB0aGlzLiRyb290LmRhdGEoJ2JyYW5kLWlkJyk7XG4gICAgICAgIHRoaXMuZm9ybURhdGEgPSB7fTtcbiAgICAgICAgdGhpcy5mb3JtSGVscGVyID0gbmV3IEZvcm1IZWxwZXIodGhpcy5sb2NhbHMuJGlucHV0cyk7XG5cbiAgICAgICAgdGhpcy5fcHJlcGFyZVZpZXcodGhpcy5sb2NhbHMuJGlucHV0cyk7XG4gICAgICAgIHRoaXMuX3NhdmVGb3JtRGF0YSh0aGlzLmxvY2Fscy4kaW5wdXRzKTtcblxuICAgICAgICB0aGlzLl9hc3NpZ25FdmVudHMoKTtcbiAgICB9XG5cbiAgICBfZ2V0RG9tKCkge1xuICAgICAgICBjb25zdCAkcm9vdCA9IHRoaXMuJHJvb3Q7XG5cbiAgICAgICAgcmV0dXJuIHtcbiAgICAgICAgICAgICRmb3JtOiAkcm9vdC5maW5kKCdbZGF0YS1zZXRhcGktZm9ybV0nKSxcbiAgICAgICAgICAgICR2aWV3OiAkcm9vdC5maW5kKCdbZGF0YS1zZXRhcGktdmlld10nKSxcbiAgICAgICAgICAgICRtb2RhbDogJHJvb3QuZmluZCgnW2RhdGEtc2V0YXBpLW1vZGFsXScpLFxuICAgICAgICAgICAgJGlucHV0czogJHJvb3QuZmluZCgnLmItYXBpZm9ybV9faW5wdXQnKSxcbiAgICAgICAgICAgICRlcnJvcjogJHJvb3QuZmluZCgnW2RhdGEtc2V0YXBpLWVycm9yc10nKVxuICAgICAgICB9O1xuICAgIH1cblxuICAgIF9hc3NpZ25FdmVudHMoKSB7XG4gICAgICAgIHRoaXMuJHJvb3RcbiAgICAgICAgICAgIC5vbignY2xpY2snLCAnW2RhdGEtc2V0YXBpLWFjdGl2YXRlXScsIHRoaXMuX29uQ2xpY2tBY3RpdmF0ZS5iaW5kKHRoaXMpKVxuICAgICAgICAgICAgLm9uKCdjbGljaycsICdbZGF0YS1zZXRhcGktcHJvbXB0YnRuXScsIHRoaXMuX29uQ2xpY2tTaG93UHJvbXB0LmJpbmQodGhpcykpXG4gICAgICAgICAgICAub24oJ2NsaWNrJywgJ1tkYXRhLXNldGFwaS1zcGVjaWZ5XScsIHRoaXMuX29uQ2xpY2tTcGVjaWZ5LmJpbmQodGhpcykpXG4gICAgICAgICAgICAub24oJ2NsaWNrJywgJ1tkYXRhLXNldGFwaS1kZWFjdGljYXRlXScsIHRoaXMuX29uQ2xpY2tEZWFjdGl2YXRlLmJpbmQodGhpcykpXG5cbiAgICAgICAgICAgIC5vbignc3VibWl0JywgJ1tkYXRhLXNldGFwaS1mb3JtXScsIHRoaXMuX29uQ2xpY2tTdWJtaXQuYmluZCh0aGlzKSlcbiAgICAgICAgICAgIC5vbignY2xpY2snLCAnW2RhdGEtc2V0YXBpLWZvcm0tc2F2ZV0nLCB0aGlzLl9vbkNsaWNrU3VibWl0LmJpbmQodGhpcykpXG4gICAgICAgICAgICAub24oJ2NsaWNrJywgJ1tkYXRhLXNldGFwaS1mb3JtLWNhbmNlbF0nLCB0aGlzLl9vbkNsaWNrRm9ybUNhbmNlbC5iaW5kKHRoaXMpKVxuXG4gICAgICAgICAgICAub24oJ2NsaWNrJywgJ1tkYXRhLXNldGFwaS1lZGl0Zm9ybV0nLCB0aGlzLl9vbkNsaWNrRWRpdEZvcm0uYmluZCh0aGlzKSlcbiAgICAgICAgICAgIC5vbignY2xpY2snLCAnLmItYXBpdmlld19fbGluaycsIHRoaXMuX29uQ2xpY2tBZGRVcmwuYmluZCh0aGlzKSk7XG4gICAgfVxuXG4gICAgX29uQ2xpY2tBY3RpdmF0ZShlKXtcbiAgICAgICAgZS5wcmV2ZW50RGVmYXVsdCgpO1xuICAgICAgICBjb25zdCBzZWxmID0gdGhpcztcbiAgICAgICAgY29uc3QgJHJvb3QgPSBzZWxmLiRyb290O1xuICAgICAgICBcbiAgICAgICAgc2VsZi5fc2VuZEFjdGl2YXRlKHNlbGYuYnJhbmRJZClcbiAgICAgICAgICAgIC5kb25lKCgpPT57XG4gICAgICAgICAgICAgICAgaWYgKCEkcm9vdC5oYXNDbGFzcygnYi1zZXRhcGlfc3RhdGVfYWN0aXZlJykpe1xuICAgICAgICAgICAgICAgICAgICAkcm9vdC5hZGRDbGFzcygnYi1zZXRhcGlfc3RhdGVfYWN0aXZlJyk7XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIHN1Y2Nlc3MoJ0FQSSB3YXMgc3VjY2Vzc2Z1bGx5IGFjdGl2YXRlZCcpXG4gICAgICAgICAgICB9KVxuICAgIH1cblxuICAgIF9vbkNsaWNrU2hvd1Byb21wdChlKXtcbiAgICAgICAgZS5wcmV2ZW50RGVmYXVsdCgpO1xuICAgICAgICB0aGlzLmxvY2Fscy4kbW9kYWwubW9kYWwoJ3Nob3cnKTtcbiAgICB9XG5cbiAgICBfb25DbGlja0RlYWN0aXZhdGUoZSl7XG4gICAgICAgIGNvbnN0IHNlbGYgPSB0aGlzO1xuICAgICAgICBjb25zdCAkcm9vdCA9IHNlbGYuJHJvb3Q7XG4gICAgICAgIFxuICAgICAgICBlLnByZXZlbnREZWZhdWx0KCk7XG4gICAgICAgIGUuc3RvcFByb3BhZ2F0aW9uKCk7XG5cbiAgICAgICAgc2VsZi5fc2VuZERlYWN0aXZhdGUoc2VsZi5icmFuZElkKVxuICAgICAgICAgICAgLmRvbmUoKCk9PntcbiAgICAgICAgICAgICAgICAkcm9vdC5yZW1vdmVDbGFzcygnYi1zZXRhcGlfc3RhdGVfYWN0aXZlJyk7XG4gICAgICAgICAgICAgICAgc3VjY2VzcygnQVBJIHdhcyBzdWNjZXNzZnVsbHkgZGVhY3RpdmF0ZWQnKVxuICAgICAgICAgICAgfSlcbiAgICB9XG5cbiAgICBfb25DbGlja1NwZWNpZnkoZSl7XG4gICAgICAgIGUucHJldmVudERlZmF1bHQoKTtcbiAgICAgICAgY29uc3QgJHJvb3QgPSB0aGlzLiRyb290O1xuXG4gICAgICAgIGlmICghJHJvb3QuaGFzQ2xhc3MoJ2Itc2V0YXBpX3N0YXRlX2Zvcm0nKSl7XG4gICAgICAgICAgICAkcm9vdC5hZGRDbGFzcygnYi1zZXRhcGlfc3RhdGVfZm9ybScpO1xuICAgICAgICB9XG4gICAgfVxuXG4gICAgX29uQ2xpY2tTdWJtaXQoZSl7XG4gICAgICAgIGUucHJldmVudERlZmF1bHQoKTtcbiAgICAgICAgY29uc3Qgc2VsZiA9IHRoaXM7XG4gICAgICAgIGNvbnN0ICRpbnB1dHMgPSB0aGlzLmxvY2Fscy4kaW5wdXRzO1xuXG4gICAgICAgIGlmICghc2VsZi5pc0Zvcm1WYWxpZCgpKSByZXR1cm47XG4gICAgICAgIFxuICAgICAgICBjb25zdCBmb3JtRGF0YSA9IHRoaXMuZm9ybUhlbHBlci5nZXRGb3JtRGF0YSgpO1xuICAgICAgICBzZWxmLl9zZW5kVXJsc0RhdGEoc2VsZi5icmFuZElkLCBmb3JtRGF0YSlcbiAgICAgICAgICAgIC5kb25lKCgpPT57XG4gICAgICAgICAgICAgICAgc2VsZi5sb2NhbHMuJGVycm9yLmh0bWwoJycpO1xuICAgICAgICAgICAgICAgIHRoaXMuX3NhdmVGb3JtRGF0YSgkaW5wdXRzKTtcbiAgICAgICAgICAgICAgICB0aGlzLl9wcmVwYXJlVmlldygkaW5wdXRzKTtcbiAgICAgICAgICAgICAgICB0aGlzLl9zaG93VmlldygpO1xuXG4gICAgICAgICAgICAgICAgc3VjY2VzcygnWW91IGFyZSBzdWNjZXNzZnVsbHkgdXBkYXRlIHVybHMnKVxuICAgICAgICAgICAgfSlcbiAgICAgICAgICAgIC5mYWlsKChyZXNwb25zZSk9PntcbiAgICAgICAgICAgICAgICBjb25zdCBkYXRhID0gJC5wYXJzZUpTT04ocmVzcG9uc2UucmVzcG9uc2VUZXh0KS5kYXRhO1xuICAgICAgICAgICAgICAgIGNvbnN0IGVycm9yVGV4dCA9IHNlbGYuZm9ybUhlbHBlci5nZXRFcnJvcnNGdWxsKGRhdGEuZXJyb3JzKTtcblxuICAgICAgICAgICAgICAgIGlmICghZGF0YS5lcnJvcnMpIHJldHVybjtcblxuICAgICAgICAgICAgICAgIHNlbGYuZm9ybUhlbHBlci5zZXRFcnJvcnMoZGF0YS5lcnJvcnMpO1xuICAgICAgICAgICAgICAgIHNlbGYubG9jYWxzLiRlcnJvci5odG1sKGVycm9yVGV4dCk7XG4gICAgICAgICAgICB9KVxuICAgIH1cblxuICAgIF9vbkNsaWNrRm9ybUNhbmNlbChlKXtcbiAgICAgICAgZS5wcmV2ZW50RGVmYXVsdCgpO1xuXG4gICAgICAgIHRoaXMuX3Jlc3RvcmVGb3JtRGF0YSh0aGlzLmxvY2Fscy4kaW5wdXRzKTtcbiAgICAgICAgdGhpcy5mb3JtSGVscGVyLnJlbW92ZUVycm9ycygpO1xuICAgICAgICB0aGlzLmxvY2Fscy4kZXJyb3IuaHRtbCgnJyk7XG5cbiAgICAgICAgdGhpcy5fc2hvd1ZpZXcoKTtcbiAgICB9XG5cbiAgICBfb25DbGlja0VkaXRGb3JtKGUpe1xuICAgICAgICBlLnByZXZlbnREZWZhdWx0KCk7XG4gICAgICAgIHRoaXMuX3NhdmVGb3JtRGF0YSh0aGlzLmxvY2Fscy4kaW5wdXRzKTtcbiAgICAgICAgdGhpcy5fc2hvd0Zvcm0oKTtcbiAgICB9XG5cbiAgICBfb25DbGlja0FkZFVybChlKXtcbiAgICAgICAgZS5wcmV2ZW50RGVmYXVsdCgpO1xuICAgICAgICBjb25zdCBmaWVsZCA9ICQoZS5jdXJyZW50VGFyZ2V0KS5jbG9zZXN0KCdbZGF0YS1zZXRhcGktZmllbGRdJykuZGF0YSgnc2V0YXBpLWZpZWxkJyk7XG5cbiAgICAgICAgdGhpcy5fc2hvd0Zvcm0oJC50cmltKGZpZWxkKSk7XG4gICAgfVxuXG4gICAgLyoqXG4gICAgICogU2hvdyBmb3JtIHdpdGggYXBpIHVybHNcbiAgICAgKiBAcGFyYW0ge1N0cmluZ30gZmllbGQgLSBuYW1lIG9mIHRoZSBmaWVsZCB0aGF0IHlvdSBuZWVkIHRvIGhpZ2h0bGlnaHRcbiAgICAgKiBAcHJpdmF0ZVxuICAgICAqL1xuICAgIF9zaG93Rm9ybShmaWVsZCA9ICcnKSB7XG4gICAgICAgIGNvbnN0ICRyb290ID0gdGhpcy4kcm9vdDtcbiAgICAgICAgY29uc3QgbG9jYWxzID0gdGhpcy5sb2NhbHM7XG5cbiAgICAgICAgaWYgKCEkcm9vdC5oYXNDbGFzcygnYi1zZXRhcGlfc3RhdGVfZm9ybScpKSB7XG4gICAgICAgICAgICAkcm9vdC5hZGRDbGFzcygnYi1zZXRhcGlfc3RhdGVfZm9ybScpXG4gICAgICAgICAgICAgICAgLnJlbW92ZUNsYXNzKCdiLXNldGFwaV9zdGF0ZV92aWV3Jyk7XG4gICAgICAgIH1cblxuICAgICAgICBjb25zdCBzZWxlY3RvciA9IGZpZWxkPyBgaW5wdXRbbmFtZT1cIiR7ZmllbGR9XCJdYDogJ2lucHV0JztcblxuICAgICAgICBsb2NhbHMuJGZvcm1cbiAgICAgICAgICAgIC5maW5kKHNlbGVjdG9yKS5maXJzdCgpXG4gICAgICAgICAgICAudHJpZ2dlcignZm9jdXMnKTtcbiAgICB9XG5cbiAgICBfc2hvd1ZpZXcoKXtcbiAgICAgICAgY29uc3QgJHJvb3QgPSB0aGlzLiRyb290O1xuXG4gICAgICAgIGlmICghJHJvb3QuaGFzQ2xhc3MoJ2Itc2V0YXBpX3N0YXRlX3ZpZXcnKSkge1xuICAgICAgICAgICAgJHJvb3QuYWRkQ2xhc3MoJ2Itc2V0YXBpX3N0YXRlX3ZpZXcnKVxuICAgICAgICAgICAgICAgIC5yZW1vdmVDbGFzcygnYi1zZXRhcGlfc3RhdGVfZm9ybScpO1xuICAgICAgICB9XG4gICAgfVxuXG4gICAgLyoqXG4gICAgICogUmVzdG9yZSBmb3JtIGRhdGEsIGlmIHlvdSBjYW5jZWwgZWRpdGluZyBmb3JtXG4gICAgICogQHBhcmFtIHtqUXVlcnl9ICRpbnB1dHMgLSBsaXN0IG9mIGdpdmVuIGlucHV0c1xuICAgICAqIEBwcml2YXRlXG4gICAgICovXG4gICAgX3Jlc3RvcmVGb3JtRGF0YSgkaW5wdXRzKXtcbiAgICAgICAgbGV0IGRhdGEgPSB0aGlzLmZvcm1EYXRhO1xuICAgICAgICAkaW5wdXRzLmVhY2goIChpbmRleCwgZWwpID0+e1xuICAgICAgICAgICAgY29uc3QgJGVsID0gJChlbCk7XG4gICAgICAgICAgICBjb25zdCB2YWx1ZSA9IGRhdGFbJGVsLmF0dHIoJ25hbWUnKV07XG4gICAgICAgICAgICAkZWwudmFsKHZhbHVlKTtcbiAgICAgICAgfSkgICAgXG4gICAgfVxuXG4gICAgLyoqXG4gICAgICogU2F2ZSBmb3JtIGRhdGEsIGlmIHlvdSBzdGFydCBlZGl0aW5nIGZvcm1cbiAgICAgKiBAcGFyYW0ge2pRdWVyeX0gJGlucHV0cyAtIGxpc3Qgb2YgZ2l2ZW4gaW5wdXRzXG4gICAgICogQHByaXZhdGVcbiAgICAgKi9cbiAgICBfc2F2ZUZvcm1EYXRhKCRpbnB1dHMpe1xuICAgICAgICBsZXQgZGF0YSA9IHt9O1xuICAgICAgICAkaW5wdXRzLmVhY2goIChpbmRleCwgZWwpID0+e1xuICAgICAgICAgICAgY29uc3QgJGVsID0gJChlbCk7XG4gICAgICAgICAgICBjb25zdCBuYW1lID0gJGVsLmF0dHIoJ25hbWUnKTtcbiAgICAgICAgICAgIFxuICAgICAgICAgICAgaWYgKG5hbWUpe1xuICAgICAgICAgICAgICAgIGRhdGFbbmFtZV0gPSAkZWwudmFsKCk7XG4gICAgICAgICAgICB9ICAgICAgICAgICAgXG4gICAgICAgIH0pO1xuXG4gICAgICAgIHRoaXMuZm9ybURhdGEgPSBkYXRhO1xuICAgIH1cblxuICAgIC8qKlxuICAgICAqIFByZXBhcmUgdmlldyBiYXNlZCBvbiBnaXZlbiBpbnB1dHM7XG4gICAgICogQHBhcmFtICRpbnB1dHNcbiAgICAgKiBAcHJpdmF0ZVxuICAgICAqL1xuICAgIF9wcmVwYXJlVmlldygkaW5wdXRzKXtcbiAgICAgICAgY29uc3QgJHZpZXcgPSB0aGlzLmxvY2Fscy4kdmlldztcbiAgICAgICAgXG4gICAgICAgICRpbnB1dHMuZWFjaCgoaW5kZXgsIGVsKSA9PiB7XG4gICAgICAgICAgICBjb25zdCAkZWwgPSAkKGVsKTtcbiAgICAgICAgICAgIGNvbnN0IG5hbWUgPSAkZWwuYXR0cignbmFtZScpO1xuICAgICAgICAgICAgY29uc3QgJHZpZXdQbGFjZSA9ICR2aWV3LmZpbmQoYFtkYXRhLXNldGFwaS1maWVsZD1cIiR7bmFtZX1cIl1gKTtcbiAgICAgICAgICAgIFxuICAgICAgICAgICAgaWYgKCEkdmlld1BsYWNlLmxlbmd0aCkgcmV0dXJuIGZhbHNlO1xuICAgICAgICAgICAgXG4gICAgICAgICAgICBjb25zdCB2YWx1ZUlucHV0ID0gJGVsLnZhbCgpO1xuICAgICAgICAgICAgaWYgKCQudHJpbSh2YWx1ZUlucHV0KSl7XG4gICAgICAgICAgICAgICAgJHZpZXdQbGFjZS5yZW1vdmVDbGFzcygnc3RhdGVfdXJsJylcbiAgICAgICAgICAgICAgICAgICAgLmZpbmQoJy5iLWFwaXZpZXdfX3RleHQnKVxuICAgICAgICAgICAgICAgICAgICAudGV4dCh2YWx1ZUlucHV0KVxuICAgICAgICAgICAgfSBlbHNlIHtcbiAgICAgICAgICAgICAgICAkdmlld1BsYWNlLmFkZENsYXNzKCdzdGF0ZV91cmwnKVxuICAgICAgICAgICAgICAgICAgICAuZmluZCgnLmItYXBpdmlld19fdGV4dCcpXG4gICAgICAgICAgICAgICAgICAgIC50ZXh0KCcnKVxuICAgICAgICAgICAgfVxuICAgICAgICB9KVxuICAgIH1cblxuICAgIC8qKlxuICAgICAqIENoZWNrLCBpcyBmb3JtIHZhbGlkIHRocm91Z2ggZm9ybSBoZWxwZXI7XG4gICAgICogQHJldHVybnMge2Jvb2xlYW59XG4gICAgICovXG4gICAgaXNGb3JtVmFsaWQoKXtcbiAgICAgICAgY29uc3QgbG9jYWxzID0gdGhpcy5sb2NhbHM7XG4gICAgICAgIGxldCB2YWxpZCA9IHRydWU7XG5cbiAgICAgICAgaWYgKCF0aGlzLmZvcm1IZWxwZXIuaXNWYWxpZElucHV0cygpKXtcbiAgICAgICAgICAgIHZhbGlkID0gZmFsc2U7XG4gICAgICAgIH1cblxuICAgICAgICByZXR1cm4gdmFsaWQ7XG4gICAgfVxuICAgIFxuICAgIFxuICAgIC8vIHRyYW5zcG9ydFxuICAgIF9zZW5kQWN0aXZhdGUoYnJhbmRJZCl7XG4gICAgICAgIGNvbnN0IHVybCA9IGpzUm91dGVzLmNvbnRyb2xsZXJzLmNtLmJyYW5kLkFQSS5hY3RpdmF0ZShicmFuZElkKS51cmw7XG4gICAgICAgIHJldHVybiAkLnBvc3QodXJsKTtcbiAgICB9XG5cbiAgICBfc2VuZERlYWN0aXZhdGUoYnJhbmRJZCl7XG4gICAgICAgIGNvbnN0IHVybCA9IGpzUm91dGVzLmNvbnRyb2xsZXJzLmNtLmJyYW5kLkFQSS5kZWFjdGl2YXRlKGJyYW5kSWQpLnVybDtcbiAgICAgICAgcmV0dXJuICQucG9zdCh1cmwpO1xuICAgIH1cblxuICAgIF9zZW5kVXJsc0RhdGEoYnJhbmRJZCwgZm9ybURhdGEpe1xuICAgICAgICBjb25zdCB1cmwgPSBqc1JvdXRlcy5jb250cm9sbGVycy5jbS5icmFuZC5BUEkudXBkYXRlKGJyYW5kSWQpLnVybDtcbiAgICAgICAgcmV0dXJuICQucG9zdCh1cmwsIGZvcm1EYXRhKTtcbiAgICB9XG5cbiAgICAvLyBzdGF0aWNcbiAgICBzdGF0aWMgcGx1Z2luKHNlbGVjdG9yKSB7XG4gICAgICAgIGNvbnN0ICRlbGVtcyA9ICQoc2VsZWN0b3IpO1xuICAgICAgICBpZiAoISRlbGVtcy5sZW5ndGgpIHJldHVybjtcblxuICAgICAgICByZXR1cm4gJGVsZW1zLmVhY2goZnVuY3Rpb24gKGluZGV4LCBlbCkge1xuICAgICAgICAgICAgbGV0ICRlbGVtZW50ID0gJChlbCk7XG4gICAgICAgICAgICBsZXQgZGF0YSA9ICRlbGVtZW50LmRhdGEoJ3dpZGdldCcpO1xuXG4gICAgICAgICAgICBpZiAoIWRhdGEpIHtcbiAgICAgICAgICAgICAgICBkYXRhID0gbmV3IFdpZGdldChlbCk7XG4gICAgICAgICAgICAgICAgJGVsZW1lbnQuZGF0YSgnd2lkZ2V0JywgZGF0YSk7XG4gICAgICAgICAgICB9XG4gICAgICAgIH0pXG4gICAgfVxufVxuXG5cblxuXG5cbi8qKiBXRUJQQUNLIEZPT1RFUiAqKlxuICoqIC4vZnJvbnRlbmQvanMvYnJhbmQvd2lkZ2V0cy9fc2V0LWFwaS5qc1xuICoqLyJdLCJzb3VyY2VSb290IjoiIn0=