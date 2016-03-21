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
	
	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }
	
	$(function () {
	    App.events.sub('hmt.tab.shown', function () {
	        _setCredits2.default.plugin('.js-set-credits');
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
	                return self.locals.$errors.text('');
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
	                self.validation.clearForm();
	                success('Credit limit was updated');
	            }).fail(function (response) {
	                var data = $.parseJSON(response.responseText).data;
	                var errorText = self.validation.getErrorsText(data.errors);
	
	                if (!data.errors) return;
	
	                self.locals.$error.text(errorText);
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
	                locals.$errors.text(errorText);
	            }
	
	            return valid;
	        }
	
	        // transport
	
	    }, {
	        key: '_sendActivate',
	        value: function _sendActivate(brandId) {
	            var url = jsRoutes.controllers.brand.Credits.activate(brandId).url;
	            return $.post(url, { brandid: brandId });
	        }
	    }, {
	        key: '_sendDeActivate',
	        value: function _sendDeActivate(brandId) {
	            var url = jsRoutes.controllers.brand.Credits.deactivate(brandId).url;
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
	     * @param {jQuery} $inputs
	     */
	
	    function FormHelper($inputs) {
	        (0, _classCallCheck3.default)(this, FormHelper);
	
	        this.$inputs = $inputs;
	        this.arrErrors = [];
	        this._assignEvents();
	    }
	
	    (0, _createClass3.default)(FormHelper, [{
	        key: '_assignEvents',
	        value: function _assignEvents() {
	            var _this = this;
	
	            this.$inputs.on('input', function (e) {
	                var $input = $(e.currentTarget);
	
	                _this._validateImmediate($input);
	                _this._removeError($input);
	            });
	        }
	    }, {
	        key: '_validateImmediate',
	        value: function _validateImmediate($input) {
	            if ($input.hasClass('type-numeric')) {
	                $input.val($input.val().replace(/[^\d]+/g, ''));
	            }
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
	
	            this.arrErrors.push({
	                name: $input.attr('name'),
	                error: errorText
	            });
	        }
	
	        /**
	         * Remove error for input
	         * @param {jQuery} $input
	         */
	
	    }, {
	        key: '_removeError',
	        value: function _removeError($input) {
	            var $parent = $input.parent();
	
	            $parent.removeClass('b-error_show').find('.b-error').remove();
	
	            this.arrErrors = this.arrErrors.filter(function (item) {
	                return item.name !== $input.attr('name');
	            });
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
	         * Get txt version of all errors
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
	    return FormHelper;
	}();

	exports.default = FormHelper;

/***/ }
/******/ ]);
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIndlYnBhY2s6Ly8vd2VicGFjay9ib290c3RyYXAgYWIwNGQ0YjYwZTc2Y2I4OWQ0NTYiLCJ3ZWJwYWNrOi8vLy4vZnJvbnRlbmQvanMvYnJhbmQvYnJhbmQtZGV0YWlscy1wYWdlLmpzIiwid2VicGFjazovLy8uL2Zyb250ZW5kL2pzL2JyYW5kL3dpZGdldHMvX3NldC1jcmVkaXRzLmpzIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9oZWxwZXJzL2NsYXNzQ2FsbENoZWNrLmpzIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9oZWxwZXJzL2NyZWF0ZUNsYXNzLmpzIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9jb3JlLWpzL29iamVjdC9kZWZpbmUtcHJvcGVydHkuanMiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L2ZuL29iamVjdC9kZWZpbmUtcHJvcGVydHkuanMiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvZXM2Lm9iamVjdC5kZWZpbmUtcHJvcGVydHkuanMiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2V4cG9ydC5qcyIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZ2xvYmFsLmpzIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19jb3JlLmpzIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19jdHguanMiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2EtZnVuY3Rpb24uanMiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2hpZGUuanMiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX29iamVjdC1kcC5qcyIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fYW4tb2JqZWN0LmpzIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19pcy1vYmplY3QuanMiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2llOC1kb20tZGVmaW5lLmpzIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19kZXNjcmlwdG9ycy5qcyIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZmFpbHMuanMiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2RvbS1jcmVhdGUuanMiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX3RvLXByaW1pdGl2ZS5qcyIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fcHJvcGVydHktZGVzYy5qcyIsIndlYnBhY2s6Ly8vLi9mcm9udGVuZC9qcy9jb21tb24vX2Zvcm0taGVscGVyLmpzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiI7QUFBQTtBQUNBOztBQUVBO0FBQ0E7O0FBRUE7QUFDQTtBQUNBOztBQUVBO0FBQ0E7QUFDQSx1QkFBZTtBQUNmO0FBQ0E7QUFDQTs7QUFFQTtBQUNBOztBQUVBO0FBQ0E7O0FBRUE7QUFDQTtBQUNBOzs7QUFHQTtBQUNBOztBQUVBO0FBQ0E7O0FBRUE7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S0FBRCxFQUFRLEVBQVIsRUFBZTtBQUM3QixxQkFBTSxNQUFNLEVBQUUsRUFBRixDQUFOLENBRHVCO0FBRTdCLHdCQUFLLFlBQUwsQ0FBa0IsR0FBbEIsRUFGNkI7Y0FBZixDQUFsQixDQURXOzs7O3FDQU9IO0FBQ1Isa0JBQUssT0FBTCxDQUFhLElBQWIsQ0FBa0IsVUFBQyxLQUFELEVBQVEsRUFBUixFQUFlO0FBQzdCLHFCQUFNLE1BQU0sRUFBRSxFQUFGLENBQU4sQ0FEdUI7QUFFN0IscUJBQUksQ0FBQyxJQUFJLElBQUosQ0FBUyxVQUFULENBQUQsRUFBd0IsSUFBSSxHQUFKLENBQVEsRUFBUixFQUE1QjtjQUZjLENBQWxCLENBRFE7OztZQWpKSyIsImZpbGUiOiJicmFuZC1kZXRhaWxzLXBhZ2UuanMiLCJzb3VyY2VzQ29udGVudCI6WyIgXHQvLyBUaGUgbW9kdWxlIGNhY2hlXG4gXHR2YXIgaW5zdGFsbGVkTW9kdWxlcyA9IHt9O1xuXG4gXHQvLyBUaGUgcmVxdWlyZSBmdW5jdGlvblxuIFx0ZnVuY3Rpb24gX193ZWJwYWNrX3JlcXVpcmVfXyhtb2R1bGVJZCkge1xuXG4gXHRcdC8vIENoZWNrIGlmIG1vZHVsZSBpcyBpbiBjYWNoZVxuIFx0XHRpZihpbnN0YWxsZWRNb2R1bGVzW21vZHVsZUlkXSlcbiBcdFx0XHRyZXR1cm4gaW5zdGFsbGVkTW9kdWxlc1ttb2R1bGVJZF0uZXhwb3J0cztcblxuIFx0XHQvLyBDcmVhdGUgYSBuZXcgbW9kdWxlIChhbmQgcHV0IGl0IGludG8gdGhlIGNhY2hlKVxuIFx0XHR2YXIgbW9kdWxlID0gaW5zdGFsbGVkTW9kdWxlc1ttb2R1bGVJZF0gPSB7XG4gXHRcdFx0ZXhwb3J0czoge30sXG4gXHRcdFx0aWQ6IG1vZHVsZUlkLFxuIFx0XHRcdGxvYWRlZDogZmFsc2VcbiBcdFx0fTtcblxuIFx0XHQvLyBFeGVjdXRlIHRoZSBtb2R1bGUgZnVuY3Rpb25cbiBcdFx0bW9kdWxlc1ttb2R1bGVJZF0uY2FsbChtb2R1bGUuZXhwb3J0cywgbW9kdWxlLCBtb2R1bGUuZXhwb3J0cywgX193ZWJwYWNrX3JlcXVpcmVfXyk7XG5cbiBcdFx0Ly8gRmxhZyB0aGUgbW9kdWxlIGFzIGxvYWRlZFxuIFx0XHRtb2R1bGUubG9hZGVkID0gdHJ1ZTtcblxuIFx0XHQvLyBSZXR1cm4gdGhlIGV4cG9ydHMgb2YgdGhlIG1vZHVsZVxuIFx0XHRyZXR1cm4gbW9kdWxlLmV4cG9ydHM7XG4gXHR9XG5cblxuIFx0Ly8gZXhwb3NlIHRoZSBtb2R1bGVzIG9iamVjdCAoX193ZWJwYWNrX21vZHVsZXNfXylcbiBcdF9fd2VicGFja19yZXF1aXJlX18ubSA9IG1vZHVsZXM7XG5cbiBcdC8vIGV4cG9zZSB0aGUgbW9kdWxlIGNhY2hlXG4gXHRfX3dlYnBhY2tfcmVxdWlyZV9fLmMgPSBpbnN0YWxsZWRNb2R1bGVzO1xuXG4gXHQvLyBfX3dlYnBhY2tfcHVibGljX3BhdGhfX1xuIFx0X193ZWJwYWNrX3JlcXVpcmVfXy5wID0gXCJcIjtcblxuIFx0Ly8gTG9hZCBlbnRyeSBtb2R1bGUgYW5kIHJldHVybiBleHBvcnRzXG4gXHRyZXR1cm4gX193ZWJwYWNrX3JlcXVpcmVfXygwKTtcblxuXG5cbi8qKiBXRUJQQUNLIEZPT1RFUiAqKlxuICoqIHdlYnBhY2svYm9vdHN0cmFwIGFiMDRkNGI2MGU3NmNiODlkNDU2XG4gKiovIiwiJ3VzZSBzdHJpY3QnO1xuXG5pbXBvcnQgU2V0Q3JlZGl0cyBmcm9tIFwiLi93aWRnZXRzL19zZXQtY3JlZGl0c1wiO1xuXG4kKGZ1bmN0aW9uKCl7XG4gICAgQXBwLmV2ZW50c1xuICAgICAgICAuc3ViKCdobXQudGFiLnNob3duJywgZnVuY3Rpb24oKXtcbiAgICAgICAgICAgIFNldENyZWRpdHMucGx1Z2luKCcuanMtc2V0LWNyZWRpdHMnKTtcbiAgICAgICAgfSlcbn0pO1xuXG5cbi8qKiBXRUJQQUNLIEZPT1RFUiAqKlxuICoqIC4vZnJvbnRlbmQvanMvYnJhbmQvYnJhbmQtZGV0YWlscy1wYWdlLmpzXG4gKiovIiwiJ3VzZSBzdHJpY3QnO1xuXG5pbXBvcnQgRm9ybUhlbHBlciBmcm9tIFwiLi8uLi8uLi9jb21tb24vX2Zvcm0taGVscGVyXCI7XG5cblxuZXhwb3J0IGRlZmF1bHQgY2xhc3MgV2lkZ2V0IHtcbiAgICAvKipcbiAgICAgKiBGaWx0ZXIgaGlzdG9yeVxuICAgICAqIEBwYXJhbSB7U3RyaW5nfSBzZWxlY3RvclxuICAgICAqL1xuICAgIGNvbnN0cnVjdG9yKHNlbGVjdG9yKSB7XG4gICAgICAgIHRoaXMuJHJvb3QgPSAkKHNlbGVjdG9yKTtcbiAgICAgICAgdGhpcy5sb2NhbHMgPSB0aGlzLl9nZXREb20oKTtcbiAgICAgICAgdGhpcy5icmFuZElkID0gdGhpcy4kcm9vdC5kYXRhKCdicmFuZC1pZCcpO1xuICAgICAgICB0aGlzLnZhbGlkYXRpb24gPSBuZXcgRm9ybUhlbHBlcih0aGlzLmxvY2Fscy4kaW5wdXQpO1xuXG4gICAgICAgIHRoaXMuX2Fzc2lnbkV2ZW50cygpO1xuICAgIH1cblxuICAgIF9nZXREb20oKSB7XG4gICAgICAgIGNvbnN0ICRyb290ID0gdGhpcy4kcm9vdDtcblxuICAgICAgICByZXR1cm4ge1xuICAgICAgICAgICAgJGFjdGl2YXRlQnRuOiAkcm9vdC5maW5kKCdbZGF0YS1zZXRjcmVkaXQtYWN0aXZhdGVdJyksXG4gICAgICAgICAgICAkZGVBY3RpdmF0ZUJ0bjogJHJvb3QuZmluZCgnW2RhdGEtc2V0Y3JlZGl0LWRlYWN0aXZhdGVdJyksXG4gICAgICAgICAgICAkZm9ybTogJHJvb3QuZmluZCgnW2RhdGEtc2V0Y3JlZGl0LWZvcm1dJyksXG4gICAgICAgICAgICAkaW5wdXQ6ICRyb290LmZpbmQoJ1tkYXRhLXNldGNyZWRpdC1pbnB1dF0nKSxcbiAgICAgICAgICAgICRlcnJvcnM6ICRyb290LmZpbmQoJ1tkYXRhLXNldGNyZWRpdC1lcnJvcnNdJylcbiAgICAgICAgfTtcbiAgICB9XG5cbiAgICBfYXNzaWduRXZlbnRzKCkge1xuICAgICAgICBjb25zdCBzZWxmID0gdGhpcztcblxuICAgICAgICB0aGlzLiRyb290XG4gICAgICAgICAgICAub24oJ2NsaWNrJywgJ1tkYXRhLXNldGNyZWRpdC1hY3RpdmF0ZV0nLCB0aGlzLl9vbkNsaWNrQWN0aXZhdGUuYmluZCh0aGlzKSlcbiAgICAgICAgICAgIC5vbignY2xpY2snLCAnW2RhdGEtc2V0Y3JlZGl0LWRlYWN0aXZhdGVdJywgdGhpcy5fb25DbGlja0RlQWN0aXZhdGUuYmluZCh0aGlzKSlcbiAgICAgICAgICAgIC5vbignc3VibWl0JywgJ1tkYXRhLXNldGNyZWRpdC1mb3JtXScsIHRoaXMuX29uQ2xpY2tTYXZlQ3JlZGl0LmJpbmQodGhpcykpXG4gICAgICAgICAgICAub24oJ2lucHV0JywgJ1tkYXRhLXNldGNyZWRpdC1pbnB1dF0nLCAoZSkgPT4gc2VsZi5sb2NhbHMuJGVycm9ycy50ZXh0KCcnKSlcbiAgICB9XG5cbiAgICBfb25DbGlja0FjdGl2YXRlKGUpIHtcbiAgICAgICAgY29uc3Qgc2VsZiA9IHRoaXM7XG4gICAgICAgIGUucHJldmVudERlZmF1bHQoKTtcblxuICAgICAgICBzZWxmLl9zZW5kQWN0aXZhdGUoc2VsZi5icmFuZElkKVxuICAgICAgICAgICAgLmRvbmUoKCk9PiB7XG4gICAgICAgICAgICAgICAgc2VsZi4kcm9vdC5hZGRDbGFzcygnYi1zZXRjcmVkaXRfc3RhdGVfYWN0aXZlJyk7XG4gICAgICAgICAgICB9KVxuICAgIH1cblxuICAgIF9vbkNsaWNrRGVBY3RpdmF0ZShlKSB7XG4gICAgICAgIGNvbnN0IHNlbGYgPSB0aGlzO1xuICAgICAgICBlLnByZXZlbnREZWZhdWx0KCk7XG5cbiAgICAgICAgc2VsZi5fc2VuZERlQWN0aXZhdGUoc2VsZi5icmFuZElkKVxuICAgICAgICAgICAgLmRvbmUoKCk9PiB7XG4gICAgICAgICAgICAgICAgc2VsZi4kcm9vdC5yZW1vdmVDbGFzcygnYi1zZXRjcmVkaXRfc3RhdGVfYWN0aXZlJyk7XG4gICAgICAgICAgICB9KVxuICAgIH1cblxuICAgIF9vbkNsaWNrU2F2ZUNyZWRpdChlKSB7XG4gICAgICAgIGNvbnN0IHNlbGYgPSB0aGlzO1xuICAgICAgICBlLnByZXZlbnREZWZhdWx0KCk7XG5cbiAgICAgICAgaWYgKCFzZWxmLmlzRm9ybVZhbGlkKCkpIHJldHVybjtcblxuICAgICAgICBzZWxmLl9zZW5kRm9ybURhdGEoKVxuICAgICAgICAgICAgLmRvbmUoKCk9PiB7XG4gICAgICAgICAgICAgICAgc2VsZi52YWxpZGF0aW9uLmNsZWFyRm9ybSgpO1xuICAgICAgICAgICAgICAgIHN1Y2Nlc3MoJ0NyZWRpdCBsaW1pdCB3YXMgdXBkYXRlZCcpO1xuICAgICAgICAgICAgfSlcbiAgICAgICAgICAgIC5mYWlsKChyZXNwb25zZSk9PiB7XG4gICAgICAgICAgICAgICAgY29uc3QgZGF0YSA9ICQucGFyc2VKU09OKHJlc3BvbnNlLnJlc3BvbnNlVGV4dCkuZGF0YTtcbiAgICAgICAgICAgICAgICBjb25zdCBlcnJvclRleHQgPSBzZWxmLnZhbGlkYXRpb24uZ2V0RXJyb3JzVGV4dChkYXRhLmVycm9ycyk7XG5cbiAgICAgICAgICAgICAgICBpZiAoIWRhdGEuZXJyb3JzKSByZXR1cm47XG5cbiAgICAgICAgICAgICAgICBzZWxmLmxvY2Fscy4kZXJyb3IudGV4dChlcnJvclRleHQpO1xuICAgICAgICAgICAgICAgIHNlbGYudmFsaWRhdGlvbi5zZXRFcnJvcnMoZGF0YS5lcnJvcnMpO1xuICAgICAgICAgICAgfSlcbiAgICB9XG5cbiAgICBpc0Zvcm1WYWxpZCgpIHtcbiAgICAgICAgY29uc3QgbG9jYWxzID0gdGhpcy5sb2NhbHM7XG4gICAgICAgIGNvbnN0IGlzVmFsaWRDcmVkaXQgPSAobG9jYWxzLiRpbnB1dC52YWwoKSA+IDApO1xuICAgICAgICBsZXQgdmFsaWQgPSB0cnVlO1xuICAgICAgICBsZXQgZXJyb3JUZXh0ID0gJyc7XG5cbiAgICAgICAgaWYgKCFpc1ZhbGlkQ3JlZGl0KXtcbiAgICAgICAgICAgIHZhbGlkID0gZmFsc2U7XG4gICAgICAgICAgICBlcnJvclRleHQgKz0gJ1NwZW5kaW5nIGxpbWl0IGhhcyB0byBiZSBhYm92ZSAwLiBXZSByZWNvbW1lbmQgc2V0IGluIDEwMC4nO1xuICAgICAgICAgICAgdGhpcy52YWxpZGF0aW9uLl9zZXRFcnJvcihsb2NhbHMuJGlucHV0KTtcbiAgICAgICAgfVxuICAgICAgICBcbiAgICAgICAgaWYgKCF2YWxpZCl7XG4gICAgICAgICAgICBsb2NhbHMuJGVycm9ycy50ZXh0KGVycm9yVGV4dCk7XG4gICAgICAgIH1cbiAgICAgICAgXG4gICAgICAgIHJldHVybiB2YWxpZDtcbiAgICB9XG5cbiAgICAvLyB0cmFuc3BvcnRcbiAgICBfc2VuZEFjdGl2YXRlKGJyYW5kSWQpIHtcbiAgICAgICAgdmFyIHVybCA9IGpzUm91dGVzLmNvbnRyb2xsZXJzLmJyYW5kLkNyZWRpdHMuYWN0aXZhdGUoYnJhbmRJZCkudXJsO1xuICAgICAgICByZXR1cm4gJC5wb3N0KHVybCwge2JyYW5kaWQ6IGJyYW5kSWR9KTtcbiAgICB9XG5cbiAgICBfc2VuZERlQWN0aXZhdGUoYnJhbmRJZCkge1xuICAgICAgICB2YXIgdXJsID0ganNSb3V0ZXMuY29udHJvbGxlcnMuYnJhbmQuQ3JlZGl0cy5kZWFjdGl2YXRlKGJyYW5kSWQpLnVybDtcbiAgICAgICAgcmV0dXJuICQucG9zdCh1cmwsIHticmFuZGlkOiBicmFuZElkfSk7XG4gICAgfVxuXG4gICAgX3NlbmRGb3JtRGF0YSgpIHtcbiAgICAgICAgY29uc3QgbG9jYWxzID0gdGhpcy5sb2NhbHM7XG5cbiAgICAgICAgcmV0dXJuICQucG9zdChsb2NhbHMuJGZvcm0uYXR0cignYWN0aW9uJyksIHtcbiAgICAgICAgICAgIGxpbWl0OiBsb2NhbHMuJGlucHV0LnZhbCgpXG4gICAgICAgIH0pXG4gICAgfVxuXG4gICAgLy8gc3RhdGljXG4gICAgc3RhdGljIHBsdWdpbihzZWxlY3Rvcikge1xuICAgICAgICBjb25zdCAkZWxlbXMgPSAkKHNlbGVjdG9yKTtcbiAgICAgICAgaWYgKCEkZWxlbXMubGVuZ3RoKSByZXR1cm47XG5cbiAgICAgICAgcmV0dXJuICRlbGVtcy5lYWNoKGZ1bmN0aW9uIChpbmRleCwgZWwpIHtcbiAgICAgICAgICAgIGxldCAkZWxlbWVudCA9ICQoZWwpO1xuICAgICAgICAgICAgbGV0IGRhdGEgPSAkZWxlbWVudC5kYXRhKCd3aWRnZXQnKTtcblxuICAgICAgICAgICAgaWYgKCFkYXRhKSB7XG4gICAgICAgICAgICAgICAgZGF0YSA9IG5ldyBXaWRnZXQoZWwpO1xuICAgICAgICAgICAgICAgICRlbGVtZW50LmRhdGEoJ3dpZGdldCcsIGRhdGEpO1xuICAgICAgICAgICAgfVxuICAgICAgICB9KVxuICAgIH1cbn1cblxuXG5cblxuXG4vKiogV0VCUEFDSyBGT09URVIgKipcbiAqKiAuL2Zyb250ZW5kL2pzL2JyYW5kL3dpZGdldHMvX3NldC1jcmVkaXRzLmpzXG4gKiovIiwiXCJ1c2Ugc3RyaWN0XCI7XG5cbmV4cG9ydHMuX19lc01vZHVsZSA9IHRydWU7XG5cbmV4cG9ydHMuZGVmYXVsdCA9IGZ1bmN0aW9uIChpbnN0YW5jZSwgQ29uc3RydWN0b3IpIHtcbiAgaWYgKCEoaW5zdGFuY2UgaW5zdGFuY2VvZiBDb25zdHJ1Y3RvcikpIHtcbiAgICB0aHJvdyBuZXcgVHlwZUVycm9yKFwiQ2Fubm90IGNhbGwgYSBjbGFzcyBhcyBhIGZ1bmN0aW9uXCIpO1xuICB9XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvaGVscGVycy9jbGFzc0NhbGxDaGVjay5qc1xuICoqIG1vZHVsZSBpZCA9IDNcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxXG4gKiovIiwiXCJ1c2Ugc3RyaWN0XCI7XG5cbmV4cG9ydHMuX19lc01vZHVsZSA9IHRydWU7XG5cbnZhciBfZGVmaW5lUHJvcGVydHkgPSByZXF1aXJlKFwiYmFiZWwtcnVudGltZS9jb3JlLWpzL29iamVjdC9kZWZpbmUtcHJvcGVydHlcIik7XG5cbnZhciBfZGVmaW5lUHJvcGVydHkyID0gX2ludGVyb3BSZXF1aXJlRGVmYXVsdChfZGVmaW5lUHJvcGVydHkpO1xuXG5mdW5jdGlvbiBfaW50ZXJvcFJlcXVpcmVEZWZhdWx0KG9iaikgeyByZXR1cm4gb2JqICYmIG9iai5fX2VzTW9kdWxlID8gb2JqIDogeyBkZWZhdWx0OiBvYmogfTsgfVxuXG5leHBvcnRzLmRlZmF1bHQgPSBmdW5jdGlvbiAoKSB7XG4gIGZ1bmN0aW9uIGRlZmluZVByb3BlcnRpZXModGFyZ2V0LCBwcm9wcykge1xuICAgIGZvciAodmFyIGkgPSAwOyBpIDwgcHJvcHMubGVuZ3RoOyBpKyspIHtcbiAgICAgIHZhciBkZXNjcmlwdG9yID0gcHJvcHNbaV07XG4gICAgICBkZXNjcmlwdG9yLmVudW1lcmFibGUgPSBkZXNjcmlwdG9yLmVudW1lcmFibGUgfHwgZmFsc2U7XG4gICAgICBkZXNjcmlwdG9yLmNvbmZpZ3VyYWJsZSA9IHRydWU7XG4gICAgICBpZiAoXCJ2YWx1ZVwiIGluIGRlc2NyaXB0b3IpIGRlc2NyaXB0b3Iud3JpdGFibGUgPSB0cnVlO1xuICAgICAgKDAsIF9kZWZpbmVQcm9wZXJ0eTIuZGVmYXVsdCkodGFyZ2V0LCBkZXNjcmlwdG9yLmtleSwgZGVzY3JpcHRvcik7XG4gICAgfVxuICB9XG5cbiAgcmV0dXJuIGZ1bmN0aW9uIChDb25zdHJ1Y3RvciwgcHJvdG9Qcm9wcywgc3RhdGljUHJvcHMpIHtcbiAgICBpZiAocHJvdG9Qcm9wcykgZGVmaW5lUHJvcGVydGllcyhDb25zdHJ1Y3Rvci5wcm90b3R5cGUsIHByb3RvUHJvcHMpO1xuICAgIGlmIChzdGF0aWNQcm9wcykgZGVmaW5lUHJvcGVydGllcyhDb25zdHJ1Y3Rvciwgc3RhdGljUHJvcHMpO1xuICAgIHJldHVybiBDb25zdHJ1Y3RvcjtcbiAgfTtcbn0oKTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL2hlbHBlcnMvY3JlYXRlQ2xhc3MuanNcbiAqKiBtb2R1bGUgaWQgPSA0XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMVxuICoqLyIsIm1vZHVsZS5leHBvcnRzID0geyBcImRlZmF1bHRcIjogcmVxdWlyZShcImNvcmUtanMvbGlicmFyeS9mbi9vYmplY3QvZGVmaW5lLXByb3BlcnR5XCIpLCBfX2VzTW9kdWxlOiB0cnVlIH07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9jb3JlLWpzL29iamVjdC9kZWZpbmUtcHJvcGVydHkuanNcbiAqKiBtb2R1bGUgaWQgPSA1XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMVxuICoqLyIsInJlcXVpcmUoJy4uLy4uL21vZHVsZXMvZXM2Lm9iamVjdC5kZWZpbmUtcHJvcGVydHknKTtcbnZhciAkT2JqZWN0ID0gcmVxdWlyZSgnLi4vLi4vbW9kdWxlcy9fY29yZScpLk9iamVjdDtcbm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24gZGVmaW5lUHJvcGVydHkoaXQsIGtleSwgZGVzYyl7XG4gIHJldHVybiAkT2JqZWN0LmRlZmluZVByb3BlcnR5KGl0LCBrZXksIGRlc2MpO1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L2ZuL29iamVjdC9kZWZpbmUtcHJvcGVydHkuanNcbiAqKiBtb2R1bGUgaWQgPSA2XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMVxuICoqLyIsInZhciAkZXhwb3J0ID0gcmVxdWlyZSgnLi9fZXhwb3J0Jyk7XHJcbi8vIDE5LjEuMi40IC8gMTUuMi4zLjYgT2JqZWN0LmRlZmluZVByb3BlcnR5KE8sIFAsIEF0dHJpYnV0ZXMpXHJcbiRleHBvcnQoJGV4cG9ydC5TICsgJGV4cG9ydC5GICogIXJlcXVpcmUoJy4vX2Rlc2NyaXB0b3JzJyksICdPYmplY3QnLCB7ZGVmaW5lUHJvcGVydHk6IHJlcXVpcmUoJy4vX29iamVjdC1kcCcpLmZ9KTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvZXM2Lm9iamVjdC5kZWZpbmUtcHJvcGVydHkuanNcbiAqKiBtb2R1bGUgaWQgPSA3XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMVxuICoqLyIsInZhciBnbG9iYWwgICAgPSByZXF1aXJlKCcuL19nbG9iYWwnKVxuICAsIGNvcmUgICAgICA9IHJlcXVpcmUoJy4vX2NvcmUnKVxuICAsIGN0eCAgICAgICA9IHJlcXVpcmUoJy4vX2N0eCcpXG4gICwgaGlkZSAgICAgID0gcmVxdWlyZSgnLi9faGlkZScpXG4gICwgUFJPVE9UWVBFID0gJ3Byb3RvdHlwZSc7XG5cbnZhciAkZXhwb3J0ID0gZnVuY3Rpb24odHlwZSwgbmFtZSwgc291cmNlKXtcbiAgdmFyIElTX0ZPUkNFRCA9IHR5cGUgJiAkZXhwb3J0LkZcbiAgICAsIElTX0dMT0JBTCA9IHR5cGUgJiAkZXhwb3J0LkdcbiAgICAsIElTX1NUQVRJQyA9IHR5cGUgJiAkZXhwb3J0LlNcbiAgICAsIElTX1BST1RPICA9IHR5cGUgJiAkZXhwb3J0LlBcbiAgICAsIElTX0JJTkQgICA9IHR5cGUgJiAkZXhwb3J0LkJcbiAgICAsIElTX1dSQVAgICA9IHR5cGUgJiAkZXhwb3J0LldcbiAgICAsIGV4cG9ydHMgICA9IElTX0dMT0JBTCA/IGNvcmUgOiBjb3JlW25hbWVdIHx8IChjb3JlW25hbWVdID0ge30pXG4gICAgLCBleHBQcm90byAgPSBleHBvcnRzW1BST1RPVFlQRV1cbiAgICAsIHRhcmdldCAgICA9IElTX0dMT0JBTCA/IGdsb2JhbCA6IElTX1NUQVRJQyA/IGdsb2JhbFtuYW1lXSA6IChnbG9iYWxbbmFtZV0gfHwge30pW1BST1RPVFlQRV1cbiAgICAsIGtleSwgb3duLCBvdXQ7XG4gIGlmKElTX0dMT0JBTClzb3VyY2UgPSBuYW1lO1xuICBmb3Ioa2V5IGluIHNvdXJjZSl7XG4gICAgLy8gY29udGFpbnMgaW4gbmF0aXZlXG4gICAgb3duID0gIUlTX0ZPUkNFRCAmJiB0YXJnZXQgJiYgdGFyZ2V0W2tleV0gIT09IHVuZGVmaW5lZDtcbiAgICBpZihvd24gJiYga2V5IGluIGV4cG9ydHMpY29udGludWU7XG4gICAgLy8gZXhwb3J0IG5hdGl2ZSBvciBwYXNzZWRcbiAgICBvdXQgPSBvd24gPyB0YXJnZXRba2V5XSA6IHNvdXJjZVtrZXldO1xuICAgIC8vIHByZXZlbnQgZ2xvYmFsIHBvbGx1dGlvbiBmb3IgbmFtZXNwYWNlc1xuICAgIGV4cG9ydHNba2V5XSA9IElTX0dMT0JBTCAmJiB0eXBlb2YgdGFyZ2V0W2tleV0gIT0gJ2Z1bmN0aW9uJyA/IHNvdXJjZVtrZXldXG4gICAgLy8gYmluZCB0aW1lcnMgdG8gZ2xvYmFsIGZvciBjYWxsIGZyb20gZXhwb3J0IGNvbnRleHRcbiAgICA6IElTX0JJTkQgJiYgb3duID8gY3R4KG91dCwgZ2xvYmFsKVxuICAgIC8vIHdyYXAgZ2xvYmFsIGNvbnN0cnVjdG9ycyBmb3IgcHJldmVudCBjaGFuZ2UgdGhlbSBpbiBsaWJyYXJ5XG4gICAgOiBJU19XUkFQICYmIHRhcmdldFtrZXldID09IG91dCA/IChmdW5jdGlvbihDKXtcbiAgICAgIHZhciBGID0gZnVuY3Rpb24oYSwgYiwgYyl7XG4gICAgICAgIGlmKHRoaXMgaW5zdGFuY2VvZiBDKXtcbiAgICAgICAgICBzd2l0Y2goYXJndW1lbnRzLmxlbmd0aCl7XG4gICAgICAgICAgICBjYXNlIDA6IHJldHVybiBuZXcgQztcbiAgICAgICAgICAgIGNhc2UgMTogcmV0dXJuIG5ldyBDKGEpO1xuICAgICAgICAgICAgY2FzZSAyOiByZXR1cm4gbmV3IEMoYSwgYik7XG4gICAgICAgICAgfSByZXR1cm4gbmV3IEMoYSwgYiwgYyk7XG4gICAgICAgIH0gcmV0dXJuIEMuYXBwbHkodGhpcywgYXJndW1lbnRzKTtcbiAgICAgIH07XG4gICAgICBGW1BST1RPVFlQRV0gPSBDW1BST1RPVFlQRV07XG4gICAgICByZXR1cm4gRjtcbiAgICAvLyBtYWtlIHN0YXRpYyB2ZXJzaW9ucyBmb3IgcHJvdG90eXBlIG1ldGhvZHNcbiAgICB9KShvdXQpIDogSVNfUFJPVE8gJiYgdHlwZW9mIG91dCA9PSAnZnVuY3Rpb24nID8gY3R4KEZ1bmN0aW9uLmNhbGwsIG91dCkgOiBvdXQ7XG4gICAgLy8gZXhwb3J0IHByb3RvIG1ldGhvZHMgdG8gY29yZS4lQ09OU1RSVUNUT1IlLm1ldGhvZHMuJU5BTUUlXG4gICAgaWYoSVNfUFJPVE8pe1xuICAgICAgKGV4cG9ydHMudmlydHVhbCB8fCAoZXhwb3J0cy52aXJ0dWFsID0ge30pKVtrZXldID0gb3V0O1xuICAgICAgLy8gZXhwb3J0IHByb3RvIG1ldGhvZHMgdG8gY29yZS4lQ09OU1RSVUNUT1IlLnByb3RvdHlwZS4lTkFNRSVcbiAgICAgIGlmKHR5cGUgJiAkZXhwb3J0LlIgJiYgZXhwUHJvdG8gJiYgIWV4cFByb3RvW2tleV0paGlkZShleHBQcm90bywga2V5LCBvdXQpO1xuICAgIH1cbiAgfVxufTtcbi8vIHR5cGUgYml0bWFwXG4kZXhwb3J0LkYgPSAxOyAgIC8vIGZvcmNlZFxuJGV4cG9ydC5HID0gMjsgICAvLyBnbG9iYWxcbiRleHBvcnQuUyA9IDQ7ICAgLy8gc3RhdGljXG4kZXhwb3J0LlAgPSA4OyAgIC8vIHByb3RvXG4kZXhwb3J0LkIgPSAxNjsgIC8vIGJpbmRcbiRleHBvcnQuVyA9IDMyOyAgLy8gd3JhcFxuJGV4cG9ydC5VID0gNjQ7ICAvLyBzYWZlXG4kZXhwb3J0LlIgPSAxMjg7IC8vIHJlYWwgcHJvdG8gbWV0aG9kIGZvciBgbGlicmFyeWAgXG5tb2R1bGUuZXhwb3J0cyA9ICRleHBvcnQ7XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19leHBvcnQuanNcbiAqKiBtb2R1bGUgaWQgPSA4XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMVxuICoqLyIsIi8vIGh0dHBzOi8vZ2l0aHViLmNvbS96bG9pcm9jay9jb3JlLWpzL2lzc3Vlcy84NiNpc3N1ZWNvbW1lbnQtMTE1NzU5MDI4XG52YXIgZ2xvYmFsID0gbW9kdWxlLmV4cG9ydHMgPSB0eXBlb2Ygd2luZG93ICE9ICd1bmRlZmluZWQnICYmIHdpbmRvdy5NYXRoID09IE1hdGhcbiAgPyB3aW5kb3cgOiB0eXBlb2Ygc2VsZiAhPSAndW5kZWZpbmVkJyAmJiBzZWxmLk1hdGggPT0gTWF0aCA/IHNlbGYgOiBGdW5jdGlvbigncmV0dXJuIHRoaXMnKSgpO1xuaWYodHlwZW9mIF9fZyA9PSAnbnVtYmVyJylfX2cgPSBnbG9iYWw7IC8vIGVzbGludC1kaXNhYmxlLWxpbmUgbm8tdW5kZWZcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2dsb2JhbC5qc1xuICoqIG1vZHVsZSBpZCA9IDlcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxXG4gKiovIiwidmFyIGNvcmUgPSBtb2R1bGUuZXhwb3J0cyA9IHt2ZXJzaW9uOiAnMi4yLjAnfTtcbmlmKHR5cGVvZiBfX2UgPT0gJ251bWJlcicpX19lID0gY29yZTsgLy8gZXNsaW50LWRpc2FibGUtbGluZSBuby11bmRlZlxuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fY29yZS5qc1xuICoqIG1vZHVsZSBpZCA9IDEwXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMVxuICoqLyIsIi8vIG9wdGlvbmFsIC8gc2ltcGxlIGNvbnRleHQgYmluZGluZ1xudmFyIGFGdW5jdGlvbiA9IHJlcXVpcmUoJy4vX2EtZnVuY3Rpb24nKTtcbm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oZm4sIHRoYXQsIGxlbmd0aCl7XG4gIGFGdW5jdGlvbihmbik7XG4gIGlmKHRoYXQgPT09IHVuZGVmaW5lZClyZXR1cm4gZm47XG4gIHN3aXRjaChsZW5ndGgpe1xuICAgIGNhc2UgMTogcmV0dXJuIGZ1bmN0aW9uKGEpe1xuICAgICAgcmV0dXJuIGZuLmNhbGwodGhhdCwgYSk7XG4gICAgfTtcbiAgICBjYXNlIDI6IHJldHVybiBmdW5jdGlvbihhLCBiKXtcbiAgICAgIHJldHVybiBmbi5jYWxsKHRoYXQsIGEsIGIpO1xuICAgIH07XG4gICAgY2FzZSAzOiByZXR1cm4gZnVuY3Rpb24oYSwgYiwgYyl7XG4gICAgICByZXR1cm4gZm4uY2FsbCh0aGF0LCBhLCBiLCBjKTtcbiAgICB9O1xuICB9XG4gIHJldHVybiBmdW5jdGlvbigvKiAuLi5hcmdzICovKXtcbiAgICByZXR1cm4gZm4uYXBwbHkodGhhdCwgYXJndW1lbnRzKTtcbiAgfTtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19jdHguanNcbiAqKiBtb2R1bGUgaWQgPSAxMVxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDFcbiAqKi8iLCJtb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGl0KXtcbiAgaWYodHlwZW9mIGl0ICE9ICdmdW5jdGlvbicpdGhyb3cgVHlwZUVycm9yKGl0ICsgJyBpcyBub3QgYSBmdW5jdGlvbiEnKTtcbiAgcmV0dXJuIGl0O1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2EtZnVuY3Rpb24uanNcbiAqKiBtb2R1bGUgaWQgPSAxMlxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDFcbiAqKi8iLCJ2YXIgZFAgICAgICAgICA9IHJlcXVpcmUoJy4vX29iamVjdC1kcCcpXG4gICwgY3JlYXRlRGVzYyA9IHJlcXVpcmUoJy4vX3Byb3BlcnR5LWRlc2MnKTtcbm1vZHVsZS5leHBvcnRzID0gcmVxdWlyZSgnLi9fZGVzY3JpcHRvcnMnKSA/IGZ1bmN0aW9uKG9iamVjdCwga2V5LCB2YWx1ZSl7XG4gIHJldHVybiBkUC5mKG9iamVjdCwga2V5LCBjcmVhdGVEZXNjKDEsIHZhbHVlKSk7XG59IDogZnVuY3Rpb24ob2JqZWN0LCBrZXksIHZhbHVlKXtcbiAgb2JqZWN0W2tleV0gPSB2YWx1ZTtcbiAgcmV0dXJuIG9iamVjdDtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19oaWRlLmpzXG4gKiogbW9kdWxlIGlkID0gMTNcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxXG4gKiovIiwidmFyIGFuT2JqZWN0ICAgICAgID0gcmVxdWlyZSgnLi9fYW4tb2JqZWN0JylcbiAgLCBJRThfRE9NX0RFRklORSA9IHJlcXVpcmUoJy4vX2llOC1kb20tZGVmaW5lJylcbiAgLCB0b1ByaW1pdGl2ZSAgICA9IHJlcXVpcmUoJy4vX3RvLXByaW1pdGl2ZScpXG4gICwgZFAgICAgICAgICAgICAgPSBPYmplY3QuZGVmaW5lUHJvcGVydHk7XG5cbmV4cG9ydHMuZiA9IHJlcXVpcmUoJy4vX2Rlc2NyaXB0b3JzJykgPyBPYmplY3QuZGVmaW5lUHJvcGVydHkgOiBmdW5jdGlvbiBkZWZpbmVQcm9wZXJ0eShPLCBQLCBBdHRyaWJ1dGVzKXtcbiAgYW5PYmplY3QoTyk7XG4gIFAgPSB0b1ByaW1pdGl2ZShQLCB0cnVlKTtcbiAgYW5PYmplY3QoQXR0cmlidXRlcyk7XG4gIGlmKElFOF9ET01fREVGSU5FKXRyeSB7XG4gICAgcmV0dXJuIGRQKE8sIFAsIEF0dHJpYnV0ZXMpO1xuICB9IGNhdGNoKGUpeyAvKiBlbXB0eSAqLyB9XG4gIGlmKCdnZXQnIGluIEF0dHJpYnV0ZXMgfHwgJ3NldCcgaW4gQXR0cmlidXRlcyl0aHJvdyBUeXBlRXJyb3IoJ0FjY2Vzc29ycyBub3Qgc3VwcG9ydGVkIScpO1xuICBpZigndmFsdWUnIGluIEF0dHJpYnV0ZXMpT1tQXSA9IEF0dHJpYnV0ZXMudmFsdWU7XG4gIHJldHVybiBPO1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX29iamVjdC1kcC5qc1xuICoqIG1vZHVsZSBpZCA9IDE0XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMVxuICoqLyIsInZhciBpc09iamVjdCA9IHJlcXVpcmUoJy4vX2lzLW9iamVjdCcpO1xubW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihpdCl7XG4gIGlmKCFpc09iamVjdChpdCkpdGhyb3cgVHlwZUVycm9yKGl0ICsgJyBpcyBub3QgYW4gb2JqZWN0IScpO1xuICByZXR1cm4gaXQ7XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fYW4tb2JqZWN0LmpzXG4gKiogbW9kdWxlIGlkID0gMTVcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxXG4gKiovIiwibW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihpdCl7XG4gIHJldHVybiB0eXBlb2YgaXQgPT09ICdvYmplY3QnID8gaXQgIT09IG51bGwgOiB0eXBlb2YgaXQgPT09ICdmdW5jdGlvbic7XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9faXMtb2JqZWN0LmpzXG4gKiogbW9kdWxlIGlkID0gMTZcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxXG4gKiovIiwibW9kdWxlLmV4cG9ydHMgPSAhcmVxdWlyZSgnLi9fZGVzY3JpcHRvcnMnKSAmJiAhcmVxdWlyZSgnLi9fZmFpbHMnKShmdW5jdGlvbigpe1xyXG4gIHJldHVybiBPYmplY3QuZGVmaW5lUHJvcGVydHkocmVxdWlyZSgnLi9fZG9tLWNyZWF0ZScpKCdkaXYnKSwgJ2EnLCB7Z2V0OiBmdW5jdGlvbigpeyByZXR1cm4gNzsgfX0pLmEgIT0gNztcclxufSk7XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19pZTgtZG9tLWRlZmluZS5qc1xuICoqIG1vZHVsZSBpZCA9IDE3XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMVxuICoqLyIsIi8vIFRoYW5rJ3MgSUU4IGZvciBoaXMgZnVubnkgZGVmaW5lUHJvcGVydHlcbm1vZHVsZS5leHBvcnRzID0gIXJlcXVpcmUoJy4vX2ZhaWxzJykoZnVuY3Rpb24oKXtcbiAgcmV0dXJuIE9iamVjdC5kZWZpbmVQcm9wZXJ0eSh7fSwgJ2EnLCB7Z2V0OiBmdW5jdGlvbigpeyByZXR1cm4gNzsgfX0pLmEgIT0gNztcbn0pO1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZGVzY3JpcHRvcnMuanNcbiAqKiBtb2R1bGUgaWQgPSAxOFxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDFcbiAqKi8iLCJtb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGV4ZWMpe1xuICB0cnkge1xuICAgIHJldHVybiAhIWV4ZWMoKTtcbiAgfSBjYXRjaChlKXtcbiAgICByZXR1cm4gdHJ1ZTtcbiAgfVxufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2ZhaWxzLmpzXG4gKiogbW9kdWxlIGlkID0gMTlcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxXG4gKiovIiwidmFyIGlzT2JqZWN0ID0gcmVxdWlyZSgnLi9faXMtb2JqZWN0JylcbiAgLCBkb2N1bWVudCA9IHJlcXVpcmUoJy4vX2dsb2JhbCcpLmRvY3VtZW50XG4gIC8vIGluIG9sZCBJRSB0eXBlb2YgZG9jdW1lbnQuY3JlYXRlRWxlbWVudCBpcyAnb2JqZWN0J1xuICAsIGlzID0gaXNPYmplY3QoZG9jdW1lbnQpICYmIGlzT2JqZWN0KGRvY3VtZW50LmNyZWF0ZUVsZW1lbnQpO1xubW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihpdCl7XG4gIHJldHVybiBpcyA/IGRvY3VtZW50LmNyZWF0ZUVsZW1lbnQoaXQpIDoge307XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZG9tLWNyZWF0ZS5qc1xuICoqIG1vZHVsZSBpZCA9IDIwXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMVxuICoqLyIsIi8vIDcuMS4xIFRvUHJpbWl0aXZlKGlucHV0IFssIFByZWZlcnJlZFR5cGVdKVxudmFyIGlzT2JqZWN0ID0gcmVxdWlyZSgnLi9faXMtb2JqZWN0Jyk7XG4vLyBpbnN0ZWFkIG9mIHRoZSBFUzYgc3BlYyB2ZXJzaW9uLCB3ZSBkaWRuJ3QgaW1wbGVtZW50IEBAdG9QcmltaXRpdmUgY2FzZVxuLy8gYW5kIHRoZSBzZWNvbmQgYXJndW1lbnQgLSBmbGFnIC0gcHJlZmVycmVkIHR5cGUgaXMgYSBzdHJpbmdcbm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oaXQsIFMpe1xuICBpZighaXNPYmplY3QoaXQpKXJldHVybiBpdDtcbiAgdmFyIGZuLCB2YWw7XG4gIGlmKFMgJiYgdHlwZW9mIChmbiA9IGl0LnRvU3RyaW5nKSA9PSAnZnVuY3Rpb24nICYmICFpc09iamVjdCh2YWwgPSBmbi5jYWxsKGl0KSkpcmV0dXJuIHZhbDtcbiAgaWYodHlwZW9mIChmbiA9IGl0LnZhbHVlT2YpID09ICdmdW5jdGlvbicgJiYgIWlzT2JqZWN0KHZhbCA9IGZuLmNhbGwoaXQpKSlyZXR1cm4gdmFsO1xuICBpZighUyAmJiB0eXBlb2YgKGZuID0gaXQudG9TdHJpbmcpID09ICdmdW5jdGlvbicgJiYgIWlzT2JqZWN0KHZhbCA9IGZuLmNhbGwoaXQpKSlyZXR1cm4gdmFsO1xuICB0aHJvdyBUeXBlRXJyb3IoXCJDYW4ndCBjb252ZXJ0IG9iamVjdCB0byBwcmltaXRpdmUgdmFsdWVcIik7XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fdG8tcHJpbWl0aXZlLmpzXG4gKiogbW9kdWxlIGlkID0gMjFcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxXG4gKiovIiwibW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihiaXRtYXAsIHZhbHVlKXtcbiAgcmV0dXJuIHtcbiAgICBlbnVtZXJhYmxlICA6ICEoYml0bWFwICYgMSksXG4gICAgY29uZmlndXJhYmxlOiAhKGJpdG1hcCAmIDIpLFxuICAgIHdyaXRhYmxlICAgIDogIShiaXRtYXAgJiA0KSxcbiAgICB2YWx1ZSAgICAgICA6IHZhbHVlXG4gIH07XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fcHJvcGVydHktZGVzYy5qc1xuICoqIG1vZHVsZSBpZCA9IDIyXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMVxuICoqLyIsIid1c2Ugc3RyaWN0JztcblxuZXhwb3J0IGRlZmF1bHQgY2xhc3MgRm9ybUhlbHBlciB7XG4gICAgLyoqXG4gICAgICogVmFsaWRhdGUgZm9ybSB0aHJvdWdoIGlucHV0c1xuICAgICAqIEBwYXJhbSB7alF1ZXJ5fSAkaW5wdXRzXG4gICAgICovXG4gICAgY29uc3RydWN0b3IoJGlucHV0cykge1xuICAgICAgICB0aGlzLiRpbnB1dHMgPSAkaW5wdXRzO1xuICAgICAgICB0aGlzLmFyckVycm9ycyA9IFtdO1xuICAgICAgICB0aGlzLl9hc3NpZ25FdmVudHMoKTtcbiAgICB9XG5cbiAgICBfYXNzaWduRXZlbnRzKCkge1xuICAgICAgICB0aGlzLiRpbnB1dHMub24oJ2lucHV0JywgKGUpID0+IHtcbiAgICAgICAgICAgIGNvbnN0ICRpbnB1dCA9ICQoZS5jdXJyZW50VGFyZ2V0KTtcblxuICAgICAgICAgICAgdGhpcy5fdmFsaWRhdGVJbW1lZGlhdGUoJGlucHV0KTtcbiAgICAgICAgICAgIHRoaXMuX3JlbW92ZUVycm9yKCRpbnB1dCk7XG4gICAgICAgIH0pO1xuICAgIH1cblxuICAgIF92YWxpZGF0ZUltbWVkaWF0ZSgkaW5wdXQpe1xuICAgICAgICBpZiAoJGlucHV0Lmhhc0NsYXNzKCd0eXBlLW51bWVyaWMnKSkge1xuICAgICAgICAgICAgJGlucHV0LnZhbCgkaW5wdXQudmFsKCkucmVwbGFjZSgvW15cXGRdKy9nLCAnJykpO1xuICAgICAgICB9XG4gICAgfVxuXG4gICAgaXNWYWxpZElucHV0cygpIHtcbiAgICAgICAgY29uc3QgJGlucHV0cyA9IHRoaXMuJGlucHV0cztcbiAgICAgICAgbGV0IGVycm9yID0gMDtcblxuICAgICAgICAkaW5wdXRzLmVhY2goKGluZGV4LCBpbnB1dCkgPT4ge1xuICAgICAgICAgICAgY29uc3QgJGlucHV0ID0gJChpbnB1dCk7XG5cbiAgICAgICAgICAgIGlmICghdGhpcy5faXNWYWxpZElucHV0KCRpbnB1dCkpIGVycm9yICs9IDE7XG4gICAgICAgIH0pO1xuICAgICAgICByZXR1cm4gQm9vbGVhbighZXJyb3IpO1xuICAgIH1cblxuICAgIC8qKlxuICAgICAqIENoZWNrIGdpdmVuIGlucHV0LCBpcyBpdCB2YWxpZD9cbiAgICAgKiBAcGFyYW0ge2pRdWVyeX0gJGlucHV0XG4gICAgICogQHJldHVybnMge2Jvb2xlYW59IC0gSXMgdmFsaWQgaW5wdXQ/XG4gICAgICovXG4gICAgX2lzVmFsaWRJbnB1dCgkaW5wdXQpIHtcbiAgICAgICAgY29uc3QgdmFsdWUgPSAkLnRyaW0oJGlucHV0LnZhbCgpKTtcblxuICAgICAgICBpZiAoIXZhbHVlKSB7XG4gICAgICAgICAgICB0aGlzLl9zZXRFcnJvcigkaW5wdXQsICdFbXB0eScpO1xuICAgICAgICAgICAgcmV0dXJuIGZhbHNlO1xuICAgICAgICB9XG5cbiAgICAgICAgaWYgKCgkaW5wdXQuaGFzQ2xhc3MoJ3R5cGUtZW1haWwnKSkgJiYgIXRoaXMuX2lzVmFsaWRFbWFpbCh2YWx1ZSkpIHtcbiAgICAgICAgICAgIHRoaXMuX3NldEVycm9yKCRpbnB1dCwgJ0VtYWlsIGlzIG5vdCB2YWxpZCcpO1xuICAgICAgICAgICAgcmV0dXJuIGZhbHNlO1xuICAgICAgICB9XG5cbiAgICAgICAgcmV0dXJuIHRydWU7XG4gICAgfVxuXG4gICAgLyoqXG4gICAgICogSXMgRW1haWwgdmFsaWQ/XG4gICAgICogQHBhcmFtIHtzdHJpbmd9IGVtYWlsXG4gICAgICogQHJldHVybnMge2Jvb2xlYW59XG4gICAgICovXG4gICAgX2lzVmFsaWRFbWFpbChlbWFpbCkge1xuICAgICAgICB2YXIgcmUgPSAvXigoW148PigpXFxbXFxdXFxcXC4sOzpcXHNAXCJdKyhcXC5bXjw+KClcXFtcXF1cXFxcLiw7Olxcc0BcIl0rKSopfChcIi4rXCIpKUAoKFxcW1swLTldezEsM31cXC5bMC05XXsxLDN9XFwuWzAtOV17MSwzfVxcLlswLTldezEsM31dKXwoKFthLXpBLVpcXC0wLTldK1xcLikrW2EtekEtWl17Mix9KSkkLztcbiAgICAgICAgcmV0dXJuIHJlLnRlc3QoZW1haWwpO1xuICAgIH1cblxuICAgIC8qKlxuICAgICAqIFNldCBlcnJvciBmb3IgaW5wdXRcbiAgICAgKiBAcGFyYW0ge2pRdWVyeX0gJGlucHV0XG4gICAgICogQHBhcmFtIHtzdHJpbmd9IGVycm9yVGV4dFxuICAgICAqL1xuICAgIF9zZXRFcnJvcigkaW5wdXQsIGVycm9yVGV4dCkge1xuICAgICAgICBjb25zdCAkcGFyZW50ID0gJGlucHV0LnBhcmVudCgpO1xuICAgICAgICBjb25zdCAkZXJyb3IgPSAkcGFyZW50LmZpbmQoJy5iLWVycm9yJyk7XG5cbiAgICAgICAgaWYgKCRlcnJvci5sZW5ndGgpIHJldHVybjtcblxuICAgICAgICAkcGFyZW50LmFkZENsYXNzKCdiLWVycm9yX3Nob3cnKTtcbiAgICAgICAgJCgnPGRpdiBjbGFzcz1cImItZXJyb3JcIiAvPicpXG4gICAgICAgICAgICAudGV4dChlcnJvclRleHQpXG4gICAgICAgICAgICAucHJlcGVuZFRvKCRwYXJlbnQpO1xuXG4gICAgICAgIHRoaXMuYXJyRXJyb3JzLnB1c2goe1xuICAgICAgICAgICAgbmFtZTogJGlucHV0LmF0dHIoJ25hbWUnKSxcbiAgICAgICAgICAgIGVycm9yOiBlcnJvclRleHRcbiAgICAgICAgfSlcbiAgICB9XG5cbiAgICAvKipcbiAgICAgKiBSZW1vdmUgZXJyb3IgZm9yIGlucHV0XG4gICAgICogQHBhcmFtIHtqUXVlcnl9ICRpbnB1dFxuICAgICAqL1xuICAgIF9yZW1vdmVFcnJvcigkaW5wdXQpIHtcbiAgICAgICAgY29uc3QgJHBhcmVudCA9ICRpbnB1dC5wYXJlbnQoKTtcblxuICAgICAgICAkcGFyZW50XG4gICAgICAgICAgICAucmVtb3ZlQ2xhc3MoJ2ItZXJyb3Jfc2hvdycpXG4gICAgICAgICAgICAuZmluZCgnLmItZXJyb3InKS5yZW1vdmUoKTtcblxuICAgICAgICB0aGlzLmFyckVycm9ycyA9IHRoaXMuYXJyRXJyb3JzLmZpbHRlcihmdW5jdGlvbiAoaXRlbSkge1xuICAgICAgICAgICAgcmV0dXJuIGl0ZW0ubmFtZSAhPT0gJGlucHV0LmF0dHIoJ25hbWUnKVxuICAgICAgICB9KVxuICAgIH1cblxuICAgIC8qKlxuICAgICAqIFNldCBlcnJvcnNcbiAgICAgKiBAcGFyYW0ge0FycmF5fSBlcnJvcnMgLSBbe25hbWU6IFwiZW1haWxcIiwgZXJyb3I6IFwiZW1wdHlcIn0sIHtuYW1lOiBcInBhc3N3b3JkXCIsIGVycm9yOiBcImVtcHR5XCJ9XVxuICAgICAqL1xuICAgIHNldEVycm9ycyhlcnJvcnMpIHtcbiAgICAgICAgZXJyb3JzLmZvckVhY2goKGl0ZW0pID0+IHtcbiAgICAgICAgICAgIGNvbnN0ICRjdXJyZW50SW5wdXQgPSB0aGlzLiRpbnB1dHMuZmlsdGVyKCdbbmFtZT1cIicgKyBpdGVtLm5hbWUgKyAnXCJdJykuZmlyc3QoKTtcblxuICAgICAgICAgICAgaWYgKCRjdXJyZW50SW5wdXQubGVuZ3RoKSB0aGlzLl9zZXRFcnJvcigkY3VycmVudElucHV0LCBpdGVtLmVycm9yKVxuICAgICAgICB9KVxuICAgIH1cblxuICAgIC8qKlxuICAgICAqIEdldCB0eHQgdmVyc2lvbiBvZiBhbGwgZXJyb3JzXG4gICAgICovXG4gICAgZ2V0RXJyb3JzVGV4dChlcnJvcnMpIHtcbiAgICAgICAgY29uc3QgYXJyRXJyb3JzID0gZXJyb3JzIHx8IHRoaXMuYXJyRXJyb3JzO1xuICAgICAgICBsZXQgZXJyb3JUeHQgPSAnJztcblxuICAgICAgICBhcnJFcnJvcnMuZm9yRWFjaCgoaXRlbSkgPT4ge1xuICAgICAgICAgICAgY29uc3QgbmFtZSA9IGl0ZW0ubmFtZVswXS50b1VwcGVyQ2FzZSgpICsgaXRlbS5uYW1lLnN1YnN0cigxKTtcblxuICAgICAgICAgICAgZXJyb3JUeHQgKz0gYCR7bmFtZX06ICR7aXRlbS5lcnJvcn0uIGA7XG4gICAgICAgIH0pO1xuXG4gICAgICAgIHJldHVybiBlcnJvclR4dDtcbiAgICB9XG5cbiAgICAvKipcbiAgICAgKiBSZW1vdmUgYWxsIGVycm9yc1xuICAgICAqL1xuICAgIHJlbW92ZUVycm9ycygpIHtcbiAgICAgICAgdGhpcy4kaW5wdXRzLmVhY2goKGluZGV4LCBlbCkgPT4ge1xuICAgICAgICAgICAgY29uc3QgJGVsID0gJChlbCk7XG4gICAgICAgICAgICB0aGlzLl9yZW1vdmVFcnJvcigkZWwpXG4gICAgICAgIH0pXG4gICAgfVxuXG4gICAgY2xlYXJGb3JtKCkge1xuICAgICAgICB0aGlzLiRpbnB1dHMuZWFjaCgoaW5kZXgsIGVsKSA9PiB7XG4gICAgICAgICAgICBjb25zdCAkZWwgPSAkKGVsKTtcbiAgICAgICAgICAgIGlmICghJGVsLmF0dHIoXCJkaXNhYmxlZFwiKSkgICRlbC52YWwoJycpO1xuICAgICAgICB9KVxuICAgIH1cbn1cblxuXG5cbi8qKiBXRUJQQUNLIEZPT1RFUiAqKlxuICoqIC4vZnJvbnRlbmQvanMvY29tbW9uL19mb3JtLWhlbHBlci5qc1xuICoqLyJdLCJzb3VyY2VSb290IjoiIn0=