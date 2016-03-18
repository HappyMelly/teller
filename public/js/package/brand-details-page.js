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
	
	var _setCredist = __webpack_require__(2);
	
	var _setCredist2 = _interopRequireDefault(_setCredist);
	
	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }
	
	$(function () {
	    $('.js-brand-detail').on('hmt.tab.shown', function () {
	        _setCredist2.default.plugin('.js-set-credits');
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
	            this.$root.on('click', '[data-setcredit-activate]', this._onClickActivate.bind(this)).on('click', '[data-setcredit-deactivate]', this._onClickDeActivate.bind(this)).on('submit', '[data-setcredit-form]', this._onClickSaveCredit.bind(this));
	        }
	    }, {
	        key: '_onClickActivate',
	        value: function _onClickActivate(e) {
	            var _this = this;
	
	            e.preventDefault();
	
	            this._sendActivate(this.brandId).done(function () {
	                _this.$root.addClass('b-setcredit_state_active');
	            });
	        }
	    }, {
	        key: '_onClickDeActivate',
	        value: function _onClickDeActivate(e) {
	            var _this2 = this;
	
	            e.preventDefault();
	
	            this._sendDeActivate(this.brandId).done(function () {
	                _this2.$root.removeClass('b-setcredit_state_active');
	            });
	        }
	    }, {
	        key: '_onClickSaveCredit',
	        value: function _onClickSaveCredit(e) {
	            var _this3 = this;
	
	            e.preventDefault();
	
	            if (!this.isFormValid()) return;
	
	            this._sendFormData().done(function () {
	                _this3.validation.clearForm();
	                success('Set credits was successfull');
	            }).fail(function (response) {
	                var data = $.parseJSON(response.responseText).data;
	                var errorText = _this3.validation.getErrorsText(data.errors);
	
	                if (!data.errors) return;
	
	                _this3.locals.$error.text(errorText);
	                _this3.validation.setErrors(data.errors);
	            });
	        }
	    }, {
	        key: 'isFormValid',
	        value: function isFormValid() {
	            var locals = this.locals;
	            var isValidCredit = locals.$input.val().length > 0;
	            var valid = true;
	            var errorText = '';
	
	            if (isValidCredit) {
	                valid = false;
	                errorText += 'Spending limit has to be above 0. We recomend set in 100.';
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
	            var url = '/activate/' + brandId;
	            return $.post(url, { brandid: brandId });
	        }
	    }, {
	        key: '_sendDeActivate',
	        value: function _sendDeActivate(brandId) {
	            var url = '/deactivate/' + brandId;
	            return $.post(url, { brandid: brandId });
	        }
	    }, {
	        key: '_sendFormData',
	        value: function _sendFormData() {
	            var locals = this.locals;
	
	            return $.post(locals.$form.attr('action'), {
	                credits: locals.$input.val()
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
	                return _this._removeError($(e.target));
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
	
	                errorTxt += name + ' value is ' + item.error.toLowerCase() + '. ';
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
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIndlYnBhY2s6Ly8vd2VicGFjay9ib290c3RyYXAgNDIzMjY0ZjhmOTFkOTM0YzFhMzEiLCJ3ZWJwYWNrOi8vLy4vZnJvbnRlbmQvanMvYnJhbmQvYnJhbmQtZGV0YWlscy1wYWdlLmpzIiwid2VicGFjazovLy8uL2Zyb250ZW5kL2pzL2JyYW5kL3dpZGdldHMvX3NldC1jcmVkaXN0LmpzIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9oZWxwZXJzL2NsYXNzQ2FsbENoZWNrLmpzIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9oZWxwZXJzL2NyZWF0ZUNsYXNzLmpzIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9jb3JlLWpzL29iamVjdC9kZWZpbmUtcHJvcGVydHkuanMiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L2ZuL29iamVjdC9kZWZpbmUtcHJvcGVydHkuanMiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvZXM2Lm9iamVjdC5kZWZpbmUtcHJvcGVydHkuanMiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2V4cG9ydC5qcyIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZ2xvYmFsLmpzIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19jb3JlLmpzIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19jdHguanMiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2EtZnVuY3Rpb24uanMiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2hpZGUuanMiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX29iamVjdC1kcC5qcyIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fYW4tb2JqZWN0LmpzIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19pcy1vYmplY3QuanMiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2llOC1kb20tZGVmaW5lLmpzIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19kZXNjcmlwdG9ycy5qcyIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZmFpbHMuanMiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2RvbS1jcmVhdGUuanMiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX3RvLXByaW1pdGl2ZS5qcyIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fcHJvcGVydHktZGVzYy5qcyIsIndlYnBhY2s6Ly8vLi9mcm9udGVuZC9qcy9jb21tb24vX2Zvcm0taGVscGVyLmpzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiI7QUFBQTtBQUNBOztBQUVBO0FBQ0E7O0FBRUE7QUFDQTtBQUNBOztBQUVBO0FBQ0E7QUFDQSx1QkFBZTtBQUNmO0FBQ0E7QUFDQTs7QUFFQTtBQUNBOztBQUVBO0FBQ0E7O0FBRUE7QUFDQTtBQUNBOzs7QUFHQTtBQUNBOztBQUVBO0FBQ0E7O0FBRUE7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ZixDQURjOzs7Ozs7Ozs7dUNBV0osUUFBUTtBQUNsQixpQkFBTSxZQUFZLFVBQVUsS0FBSyxTQUFMLENBRFY7QUFFbEIsaUJBQUksV0FBVyxFQUFYLENBRmM7O0FBSWxCLHVCQUFVLE9BQVYsQ0FBa0IsVUFBQyxJQUFELEVBQVU7QUFDeEIscUJBQU0sT0FBTyxLQUFLLElBQUwsQ0FBVSxDQUFWLEVBQWEsV0FBYixLQUE2QixLQUFLLElBQUwsQ0FBVSxNQUFWLENBQWlCLENBQWpCLENBQTdCLENBRFc7O0FBR3hCLDZCQUFlLHNCQUFpQixLQUFLLEtBQUwsQ0FBVyxXQUFYLFNBQWhDLENBSHdCO2NBQVYsQ0FBbEIsQ0FKa0I7O0FBVWxCLG9CQUFPLFFBQVAsQ0FWa0I7Ozs7Ozs7Ozt3Q0FnQlA7OztBQUNYLGtCQUFLLE9BQUwsQ0FBYSxJQUFiLENBQWtCLFVBQUMsS0FBRCxFQUFRLEVBQVIsRUFBZTtBQUM3QixxQkFBTSxNQUFNLEVBQUUsRUFBRixDQUFOLENBRHVCO0FBRTdCLHdCQUFLLFlBQUwsQ0FBa0IsR0FBbEIsRUFGNkI7Y0FBZixDQUFsQixDQURXOzs7O3FDQU9IO0FBQ1Isa0JBQUssT0FBTCxDQUFhLElBQWIsQ0FBa0IsVUFBQyxLQUFELEVBQVEsRUFBUixFQUFlO0FBQzdCLHFCQUFNLE1BQU0sRUFBRSxFQUFGLENBQU4sQ0FEdUI7QUFFN0IscUJBQUksQ0FBQyxJQUFJLElBQUosQ0FBUyxVQUFULENBQUQsRUFBd0IsSUFBSSxHQUFKLENBQVEsRUFBUixFQUE1QjtjQUZjLENBQWxCLENBRFE7OztZQXJJSyIsImZpbGUiOiJicmFuZC1kZXRhaWxzLXBhZ2UuanMiLCJzb3VyY2VzQ29udGVudCI6WyIgXHQvLyBUaGUgbW9kdWxlIGNhY2hlXG4gXHR2YXIgaW5zdGFsbGVkTW9kdWxlcyA9IHt9O1xuXG4gXHQvLyBUaGUgcmVxdWlyZSBmdW5jdGlvblxuIFx0ZnVuY3Rpb24gX193ZWJwYWNrX3JlcXVpcmVfXyhtb2R1bGVJZCkge1xuXG4gXHRcdC8vIENoZWNrIGlmIG1vZHVsZSBpcyBpbiBjYWNoZVxuIFx0XHRpZihpbnN0YWxsZWRNb2R1bGVzW21vZHVsZUlkXSlcbiBcdFx0XHRyZXR1cm4gaW5zdGFsbGVkTW9kdWxlc1ttb2R1bGVJZF0uZXhwb3J0cztcblxuIFx0XHQvLyBDcmVhdGUgYSBuZXcgbW9kdWxlIChhbmQgcHV0IGl0IGludG8gdGhlIGNhY2hlKVxuIFx0XHR2YXIgbW9kdWxlID0gaW5zdGFsbGVkTW9kdWxlc1ttb2R1bGVJZF0gPSB7XG4gXHRcdFx0ZXhwb3J0czoge30sXG4gXHRcdFx0aWQ6IG1vZHVsZUlkLFxuIFx0XHRcdGxvYWRlZDogZmFsc2VcbiBcdFx0fTtcblxuIFx0XHQvLyBFeGVjdXRlIHRoZSBtb2R1bGUgZnVuY3Rpb25cbiBcdFx0bW9kdWxlc1ttb2R1bGVJZF0uY2FsbChtb2R1bGUuZXhwb3J0cywgbW9kdWxlLCBtb2R1bGUuZXhwb3J0cywgX193ZWJwYWNrX3JlcXVpcmVfXyk7XG5cbiBcdFx0Ly8gRmxhZyB0aGUgbW9kdWxlIGFzIGxvYWRlZFxuIFx0XHRtb2R1bGUubG9hZGVkID0gdHJ1ZTtcblxuIFx0XHQvLyBSZXR1cm4gdGhlIGV4cG9ydHMgb2YgdGhlIG1vZHVsZVxuIFx0XHRyZXR1cm4gbW9kdWxlLmV4cG9ydHM7XG4gXHR9XG5cblxuIFx0Ly8gZXhwb3NlIHRoZSBtb2R1bGVzIG9iamVjdCAoX193ZWJwYWNrX21vZHVsZXNfXylcbiBcdF9fd2VicGFja19yZXF1aXJlX18ubSA9IG1vZHVsZXM7XG5cbiBcdC8vIGV4cG9zZSB0aGUgbW9kdWxlIGNhY2hlXG4gXHRfX3dlYnBhY2tfcmVxdWlyZV9fLmMgPSBpbnN0YWxsZWRNb2R1bGVzO1xuXG4gXHQvLyBfX3dlYnBhY2tfcHVibGljX3BhdGhfX1xuIFx0X193ZWJwYWNrX3JlcXVpcmVfXy5wID0gXCJcIjtcblxuIFx0Ly8gTG9hZCBlbnRyeSBtb2R1bGUgYW5kIHJldHVybiBleHBvcnRzXG4gXHRyZXR1cm4gX193ZWJwYWNrX3JlcXVpcmVfXygwKTtcblxuXG5cbi8qKiBXRUJQQUNLIEZPT1RFUiAqKlxuICoqIHdlYnBhY2svYm9vdHN0cmFwIDQyMzI2NGY4ZjkxZDkzNGMxYTMxXG4gKiovIiwiJ3VzZSBzdHJpY3QnO1xuXG5pbXBvcnQgU2V0Q3JlZGl0cyBmcm9tIFwiLi93aWRnZXRzL19zZXQtY3JlZGlzdFwiO1xuXG4kKGZ1bmN0aW9uKCl7XG4gICAgJCgnLmpzLWJyYW5kLWRldGFpbCcpXG4gICAgICAgIC5vbignaG10LnRhYi5zaG93bicsIGZ1bmN0aW9uKCl7XG4gICAgICAgICAgICBTZXRDcmVkaXRzLnBsdWdpbignLmpzLXNldC1jcmVkaXRzJyk7XG4gICAgICAgIH0pXG59KTtcblxuXG4vKiogV0VCUEFDSyBGT09URVIgKipcbiAqKiAuL2Zyb250ZW5kL2pzL2JyYW5kL2JyYW5kLWRldGFpbHMtcGFnZS5qc1xuICoqLyIsIid1c2Ugc3RyaWN0JztcblxuaW1wb3J0IEZvcm1IZWxwZXIgZnJvbSBcIi4vLi4vLi4vY29tbW9uL19mb3JtLWhlbHBlclwiO1xuXG5cbmV4cG9ydCBkZWZhdWx0IGNsYXNzIFdpZGdldCB7XG4gICAgLyoqXG4gICAgICogRmlsdGVyIGhpc3RvcnlcbiAgICAgKiBAcGFyYW0ge1N0cmluZ30gc2VsZWN0b3JcbiAgICAgKi9cbiAgICBjb25zdHJ1Y3RvcihzZWxlY3Rvcikge1xuICAgICAgICB0aGlzLiRyb290ID0gJChzZWxlY3Rvcik7XG4gICAgICAgIHRoaXMubG9jYWxzID0gdGhpcy5fZ2V0RG9tKCk7XG4gICAgICAgIHRoaXMuYnJhbmRJZCA9IHRoaXMuJHJvb3QuZGF0YSgnYnJhbmQtaWQnKTtcbiAgICAgICAgdGhpcy52YWxpZGF0aW9uID0gbmV3IEZvcm1IZWxwZXIodGhpcy5sb2NhbHMuJGlucHV0KVxuXG4gICAgICAgIHRoaXMuX2Fzc2lnbkV2ZW50cygpO1xuICAgIH1cblxuICAgIF9nZXREb20oKSB7XG4gICAgICAgIGNvbnN0ICRyb290ID0gdGhpcy4kcm9vdDtcblxuICAgICAgICByZXR1cm4ge1xuICAgICAgICAgICAgJGFjdGl2YXRlQnRuOiAkcm9vdC5maW5kKCdbZGF0YS1zZXRjcmVkaXQtYWN0aXZhdGVdJyksXG4gICAgICAgICAgICAkZGVBY3RpdmF0ZUJ0bjogJHJvb3QuZmluZCgnW2RhdGEtc2V0Y3JlZGl0LWRlYWN0aXZhdGVdJyksXG4gICAgICAgICAgICAkZm9ybTogJHJvb3QuZmluZCgnW2RhdGEtc2V0Y3JlZGl0LWZvcm1dJyksXG4gICAgICAgICAgICAkaW5wdXQ6ICRyb290LmZpbmQoJ1tkYXRhLXNldGNyZWRpdC1pbnB1dF0nKSxcbiAgICAgICAgICAgICRlcnJvcnM6ICRyb290LmZpbmQoJ1tkYXRhLXNldGNyZWRpdC1lcnJvcnNdJylcbiAgICAgICAgfTtcbiAgICB9XG5cbiAgICBfYXNzaWduRXZlbnRzKCkge1xuICAgICAgICB0aGlzLiRyb290XG4gICAgICAgICAgICAub24oJ2NsaWNrJywgJ1tkYXRhLXNldGNyZWRpdC1hY3RpdmF0ZV0nLCB0aGlzLl9vbkNsaWNrQWN0aXZhdGUuYmluZCh0aGlzKSlcbiAgICAgICAgICAgIC5vbignY2xpY2snLCAnW2RhdGEtc2V0Y3JlZGl0LWRlYWN0aXZhdGVdJywgdGhpcy5fb25DbGlja0RlQWN0aXZhdGUuYmluZCh0aGlzKSlcbiAgICAgICAgICAgIC5vbignc3VibWl0JywgJ1tkYXRhLXNldGNyZWRpdC1mb3JtXScsIHRoaXMuX29uQ2xpY2tTYXZlQ3JlZGl0LmJpbmQodGhpcykpXG4gICAgfVxuXG4gICAgX29uQ2xpY2tBY3RpdmF0ZShlKSB7XG4gICAgICAgIGUucHJldmVudERlZmF1bHQoKTtcblxuICAgICAgICB0aGlzLl9zZW5kQWN0aXZhdGUodGhpcy5icmFuZElkKVxuICAgICAgICAgICAgLmRvbmUoKCk9PiB7XG4gICAgICAgICAgICAgICAgdGhpcy4kcm9vdC5hZGRDbGFzcygnYi1zZXRjcmVkaXRfc3RhdGVfYWN0aXZlJyk7XG4gICAgICAgICAgICB9KVxuICAgIH1cblxuICAgIF9vbkNsaWNrRGVBY3RpdmF0ZShlKSB7XG4gICAgICAgIGUucHJldmVudERlZmF1bHQoKTtcblxuICAgICAgICB0aGlzLl9zZW5kRGVBY3RpdmF0ZSh0aGlzLmJyYW5kSWQpXG4gICAgICAgICAgICAuZG9uZSgoKT0+IHtcbiAgICAgICAgICAgICAgICB0aGlzLiRyb290LnJlbW92ZUNsYXNzKCdiLXNldGNyZWRpdF9zdGF0ZV9hY3RpdmUnKTtcbiAgICAgICAgICAgIH0pXG4gICAgfVxuXG4gICAgX29uQ2xpY2tTYXZlQ3JlZGl0KGUpIHtcbiAgICAgICAgZS5wcmV2ZW50RGVmYXVsdCgpO1xuXG4gICAgICAgIGlmICghdGhpcy5pc0Zvcm1WYWxpZCgpKSByZXR1cm47XG5cbiAgICAgICAgdGhpcy5fc2VuZEZvcm1EYXRhKClcbiAgICAgICAgICAgIC5kb25lKCgpPT4ge1xuICAgICAgICAgICAgICAgIHRoaXMudmFsaWRhdGlvbi5jbGVhckZvcm0oKTtcbiAgICAgICAgICAgICAgICBzdWNjZXNzKCdTZXQgY3JlZGl0cyB3YXMgc3VjY2Vzc2Z1bGwnKTtcbiAgICAgICAgICAgIH0pXG4gICAgICAgICAgICAuZmFpbCgocmVzcG9uc2UpPT4ge1xuICAgICAgICAgICAgICAgIGNvbnN0IGRhdGEgPSAkLnBhcnNlSlNPTihyZXNwb25zZS5yZXNwb25zZVRleHQpLmRhdGE7XG4gICAgICAgICAgICAgICAgY29uc3QgZXJyb3JUZXh0ID0gdGhpcy52YWxpZGF0aW9uLmdldEVycm9yc1RleHQoZGF0YS5lcnJvcnMpO1xuXG4gICAgICAgICAgICAgICAgaWYgKCFkYXRhLmVycm9ycykgcmV0dXJuO1xuXG4gICAgICAgICAgICAgICAgdGhpcy5sb2NhbHMuJGVycm9yLnRleHQoZXJyb3JUZXh0KTtcbiAgICAgICAgICAgICAgICB0aGlzLnZhbGlkYXRpb24uc2V0RXJyb3JzKGRhdGEuZXJyb3JzKTtcbiAgICAgICAgICAgIH0pXG4gICAgfVxuXG4gICAgaXNGb3JtVmFsaWQoKSB7XG4gICAgICAgIGNvbnN0IGxvY2FscyA9IHRoaXMubG9jYWxzO1xuICAgICAgICBjb25zdCBpc1ZhbGlkQ3JlZGl0ID0gIChsb2NhbHMuJGlucHV0LnZhbCgpLmxlbmd0aCA+IDApO1xuICAgICAgICBsZXQgdmFsaWQgPSB0cnVlO1xuICAgICAgICBsZXQgZXJyb3JUZXh0ID0gJyc7XG5cbiAgICAgICAgaWYgKGlzVmFsaWRDcmVkaXQpe1xuICAgICAgICAgICAgdmFsaWQgPSBmYWxzZTtcbiAgICAgICAgICAgIGVycm9yVGV4dCArPSAnU3BlbmRpbmcgbGltaXQgaGFzIHRvIGJlIGFib3ZlIDAuIFdlIHJlY29tZW5kIHNldCBpbiAxMDAuJztcbiAgICAgICAgICAgIHRoaXMudmFsaWRhdGlvbi5fc2V0RXJyb3IobG9jYWxzLiRpbnB1dCk7XG4gICAgICAgIH1cbiAgICAgICAgXG4gICAgICAgIGlmICghdmFsaWQpe1xuICAgICAgICAgICAgbG9jYWxzLiRlcnJvcnMudGV4dChlcnJvclRleHQpO1xuICAgICAgICB9XG4gICAgICAgIFxuICAgICAgICByZXR1cm4gdmFsaWQ7XG4gICAgfVxuXG4gICAgLy8gdHJhbnNwb3J0XG4gICAgX3NlbmRBY3RpdmF0ZShicmFuZElkKSB7XG4gICAgICAgIHZhciB1cmwgPSAnL2FjdGl2YXRlLycgKyBicmFuZElkO1xuICAgICAgICByZXR1cm4gJC5wb3N0KHVybCwge2JyYW5kaWQ6IGJyYW5kSWR9KTtcbiAgICB9XG5cbiAgICBfc2VuZERlQWN0aXZhdGUoYnJhbmRJZCkge1xuICAgICAgICB2YXIgdXJsID0gJy9kZWFjdGl2YXRlLycgKyBicmFuZElkO1xuICAgICAgICByZXR1cm4gJC5wb3N0KHVybCwge2JyYW5kaWQ6IGJyYW5kSWR9KTtcbiAgICB9XG5cbiAgICBfc2VuZEZvcm1EYXRhKCkge1xuICAgICAgICBjb25zdCBsb2NhbHMgPSB0aGlzLmxvY2FscztcblxuICAgICAgICByZXR1cm4gJC5wb3N0KGxvY2Fscy4kZm9ybS5hdHRyKCdhY3Rpb24nKSwge1xuICAgICAgICAgICAgY3JlZGl0czogbG9jYWxzLiRpbnB1dC52YWwoKVxuICAgICAgICB9KVxuICAgIH1cblxuICAgIC8vIHN0YXRpY1xuICAgIHN0YXRpYyBwbHVnaW4oc2VsZWN0b3IpIHtcbiAgICAgICAgY29uc3QgJGVsZW1zID0gJChzZWxlY3Rvcik7XG4gICAgICAgIGlmICghJGVsZW1zLmxlbmd0aCkgcmV0dXJuO1xuXG4gICAgICAgIHJldHVybiAkZWxlbXMuZWFjaChmdW5jdGlvbiAoaW5kZXgsIGVsKSB7XG4gICAgICAgICAgICBsZXQgJGVsZW1lbnQgPSAkKGVsKTtcbiAgICAgICAgICAgIGxldCBkYXRhID0gJGVsZW1lbnQuZGF0YSgnd2lkZ2V0Jyk7XG5cbiAgICAgICAgICAgIGlmICghZGF0YSkge1xuICAgICAgICAgICAgICAgIGRhdGEgPSBuZXcgV2lkZ2V0KGVsKTtcbiAgICAgICAgICAgICAgICAkZWxlbWVudC5kYXRhKCd3aWRnZXQnLCBkYXRhKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgfSlcbiAgICB9XG59XG5cblxuXG5cblxuLyoqIFdFQlBBQ0sgRk9PVEVSICoqXG4gKiogLi9mcm9udGVuZC9qcy9icmFuZC93aWRnZXRzL19zZXQtY3JlZGlzdC5qc1xuICoqLyIsIlwidXNlIHN0cmljdFwiO1xuXG5leHBvcnRzLl9fZXNNb2R1bGUgPSB0cnVlO1xuXG5leHBvcnRzLmRlZmF1bHQgPSBmdW5jdGlvbiAoaW5zdGFuY2UsIENvbnN0cnVjdG9yKSB7XG4gIGlmICghKGluc3RhbmNlIGluc3RhbmNlb2YgQ29uc3RydWN0b3IpKSB7XG4gICAgdGhyb3cgbmV3IFR5cGVFcnJvcihcIkNhbm5vdCBjYWxsIGEgY2xhc3MgYXMgYSBmdW5jdGlvblwiKTtcbiAgfVxufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL2hlbHBlcnMvY2xhc3NDYWxsQ2hlY2suanNcbiAqKiBtb2R1bGUgaWQgPSAzXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMVxuICoqLyIsIlwidXNlIHN0cmljdFwiO1xuXG5leHBvcnRzLl9fZXNNb2R1bGUgPSB0cnVlO1xuXG52YXIgX2RlZmluZVByb3BlcnR5ID0gcmVxdWlyZShcImJhYmVsLXJ1bnRpbWUvY29yZS1qcy9vYmplY3QvZGVmaW5lLXByb3BlcnR5XCIpO1xuXG52YXIgX2RlZmluZVByb3BlcnR5MiA9IF9pbnRlcm9wUmVxdWlyZURlZmF1bHQoX2RlZmluZVByb3BlcnR5KTtcblxuZnVuY3Rpb24gX2ludGVyb3BSZXF1aXJlRGVmYXVsdChvYmopIHsgcmV0dXJuIG9iaiAmJiBvYmouX19lc01vZHVsZSA/IG9iaiA6IHsgZGVmYXVsdDogb2JqIH07IH1cblxuZXhwb3J0cy5kZWZhdWx0ID0gZnVuY3Rpb24gKCkge1xuICBmdW5jdGlvbiBkZWZpbmVQcm9wZXJ0aWVzKHRhcmdldCwgcHJvcHMpIHtcbiAgICBmb3IgKHZhciBpID0gMDsgaSA8IHByb3BzLmxlbmd0aDsgaSsrKSB7XG4gICAgICB2YXIgZGVzY3JpcHRvciA9IHByb3BzW2ldO1xuICAgICAgZGVzY3JpcHRvci5lbnVtZXJhYmxlID0gZGVzY3JpcHRvci5lbnVtZXJhYmxlIHx8IGZhbHNlO1xuICAgICAgZGVzY3JpcHRvci5jb25maWd1cmFibGUgPSB0cnVlO1xuICAgICAgaWYgKFwidmFsdWVcIiBpbiBkZXNjcmlwdG9yKSBkZXNjcmlwdG9yLndyaXRhYmxlID0gdHJ1ZTtcbiAgICAgICgwLCBfZGVmaW5lUHJvcGVydHkyLmRlZmF1bHQpKHRhcmdldCwgZGVzY3JpcHRvci5rZXksIGRlc2NyaXB0b3IpO1xuICAgIH1cbiAgfVxuXG4gIHJldHVybiBmdW5jdGlvbiAoQ29uc3RydWN0b3IsIHByb3RvUHJvcHMsIHN0YXRpY1Byb3BzKSB7XG4gICAgaWYgKHByb3RvUHJvcHMpIGRlZmluZVByb3BlcnRpZXMoQ29uc3RydWN0b3IucHJvdG90eXBlLCBwcm90b1Byb3BzKTtcbiAgICBpZiAoc3RhdGljUHJvcHMpIGRlZmluZVByb3BlcnRpZXMoQ29uc3RydWN0b3IsIHN0YXRpY1Byb3BzKTtcbiAgICByZXR1cm4gQ29uc3RydWN0b3I7XG4gIH07XG59KCk7XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9oZWxwZXJzL2NyZWF0ZUNsYXNzLmpzXG4gKiogbW9kdWxlIGlkID0gNFxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDFcbiAqKi8iLCJtb2R1bGUuZXhwb3J0cyA9IHsgXCJkZWZhdWx0XCI6IHJlcXVpcmUoXCJjb3JlLWpzL2xpYnJhcnkvZm4vb2JqZWN0L2RlZmluZS1wcm9wZXJ0eVwiKSwgX19lc01vZHVsZTogdHJ1ZSB9O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvY29yZS1qcy9vYmplY3QvZGVmaW5lLXByb3BlcnR5LmpzXG4gKiogbW9kdWxlIGlkID0gNVxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDFcbiAqKi8iLCJyZXF1aXJlKCcuLi8uLi9tb2R1bGVzL2VzNi5vYmplY3QuZGVmaW5lLXByb3BlcnR5Jyk7XG52YXIgJE9iamVjdCA9IHJlcXVpcmUoJy4uLy4uL21vZHVsZXMvX2NvcmUnKS5PYmplY3Q7XG5tb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uIGRlZmluZVByb3BlcnR5KGl0LCBrZXksIGRlc2Mpe1xuICByZXR1cm4gJE9iamVjdC5kZWZpbmVQcm9wZXJ0eShpdCwga2V5LCBkZXNjKTtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9mbi9vYmplY3QvZGVmaW5lLXByb3BlcnR5LmpzXG4gKiogbW9kdWxlIGlkID0gNlxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDFcbiAqKi8iLCJ2YXIgJGV4cG9ydCA9IHJlcXVpcmUoJy4vX2V4cG9ydCcpO1xyXG4vLyAxOS4xLjIuNCAvIDE1LjIuMy42IE9iamVjdC5kZWZpbmVQcm9wZXJ0eShPLCBQLCBBdHRyaWJ1dGVzKVxyXG4kZXhwb3J0KCRleHBvcnQuUyArICRleHBvcnQuRiAqICFyZXF1aXJlKCcuL19kZXNjcmlwdG9ycycpLCAnT2JqZWN0Jywge2RlZmluZVByb3BlcnR5OiByZXF1aXJlKCcuL19vYmplY3QtZHAnKS5mfSk7XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL2VzNi5vYmplY3QuZGVmaW5lLXByb3BlcnR5LmpzXG4gKiogbW9kdWxlIGlkID0gN1xuICoqIG1vZHVsZSBjaHVua3MgPSAwIDFcbiAqKi8iLCJ2YXIgZ2xvYmFsICAgID0gcmVxdWlyZSgnLi9fZ2xvYmFsJylcbiAgLCBjb3JlICAgICAgPSByZXF1aXJlKCcuL19jb3JlJylcbiAgLCBjdHggICAgICAgPSByZXF1aXJlKCcuL19jdHgnKVxuICAsIGhpZGUgICAgICA9IHJlcXVpcmUoJy4vX2hpZGUnKVxuICAsIFBST1RPVFlQRSA9ICdwcm90b3R5cGUnO1xuXG52YXIgJGV4cG9ydCA9IGZ1bmN0aW9uKHR5cGUsIG5hbWUsIHNvdXJjZSl7XG4gIHZhciBJU19GT1JDRUQgPSB0eXBlICYgJGV4cG9ydC5GXG4gICAgLCBJU19HTE9CQUwgPSB0eXBlICYgJGV4cG9ydC5HXG4gICAgLCBJU19TVEFUSUMgPSB0eXBlICYgJGV4cG9ydC5TXG4gICAgLCBJU19QUk9UTyAgPSB0eXBlICYgJGV4cG9ydC5QXG4gICAgLCBJU19CSU5EICAgPSB0eXBlICYgJGV4cG9ydC5CXG4gICAgLCBJU19XUkFQICAgPSB0eXBlICYgJGV4cG9ydC5XXG4gICAgLCBleHBvcnRzICAgPSBJU19HTE9CQUwgPyBjb3JlIDogY29yZVtuYW1lXSB8fCAoY29yZVtuYW1lXSA9IHt9KVxuICAgICwgZXhwUHJvdG8gID0gZXhwb3J0c1tQUk9UT1RZUEVdXG4gICAgLCB0YXJnZXQgICAgPSBJU19HTE9CQUwgPyBnbG9iYWwgOiBJU19TVEFUSUMgPyBnbG9iYWxbbmFtZV0gOiAoZ2xvYmFsW25hbWVdIHx8IHt9KVtQUk9UT1RZUEVdXG4gICAgLCBrZXksIG93biwgb3V0O1xuICBpZihJU19HTE9CQUwpc291cmNlID0gbmFtZTtcbiAgZm9yKGtleSBpbiBzb3VyY2Upe1xuICAgIC8vIGNvbnRhaW5zIGluIG5hdGl2ZVxuICAgIG93biA9ICFJU19GT1JDRUQgJiYgdGFyZ2V0ICYmIHRhcmdldFtrZXldICE9PSB1bmRlZmluZWQ7XG4gICAgaWYob3duICYmIGtleSBpbiBleHBvcnRzKWNvbnRpbnVlO1xuICAgIC8vIGV4cG9ydCBuYXRpdmUgb3IgcGFzc2VkXG4gICAgb3V0ID0gb3duID8gdGFyZ2V0W2tleV0gOiBzb3VyY2Vba2V5XTtcbiAgICAvLyBwcmV2ZW50IGdsb2JhbCBwb2xsdXRpb24gZm9yIG5hbWVzcGFjZXNcbiAgICBleHBvcnRzW2tleV0gPSBJU19HTE9CQUwgJiYgdHlwZW9mIHRhcmdldFtrZXldICE9ICdmdW5jdGlvbicgPyBzb3VyY2Vba2V5XVxuICAgIC8vIGJpbmQgdGltZXJzIHRvIGdsb2JhbCBmb3IgY2FsbCBmcm9tIGV4cG9ydCBjb250ZXh0XG4gICAgOiBJU19CSU5EICYmIG93biA/IGN0eChvdXQsIGdsb2JhbClcbiAgICAvLyB3cmFwIGdsb2JhbCBjb25zdHJ1Y3RvcnMgZm9yIHByZXZlbnQgY2hhbmdlIHRoZW0gaW4gbGlicmFyeVxuICAgIDogSVNfV1JBUCAmJiB0YXJnZXRba2V5XSA9PSBvdXQgPyAoZnVuY3Rpb24oQyl7XG4gICAgICB2YXIgRiA9IGZ1bmN0aW9uKGEsIGIsIGMpe1xuICAgICAgICBpZih0aGlzIGluc3RhbmNlb2YgQyl7XG4gICAgICAgICAgc3dpdGNoKGFyZ3VtZW50cy5sZW5ndGgpe1xuICAgICAgICAgICAgY2FzZSAwOiByZXR1cm4gbmV3IEM7XG4gICAgICAgICAgICBjYXNlIDE6IHJldHVybiBuZXcgQyhhKTtcbiAgICAgICAgICAgIGNhc2UgMjogcmV0dXJuIG5ldyBDKGEsIGIpO1xuICAgICAgICAgIH0gcmV0dXJuIG5ldyBDKGEsIGIsIGMpO1xuICAgICAgICB9IHJldHVybiBDLmFwcGx5KHRoaXMsIGFyZ3VtZW50cyk7XG4gICAgICB9O1xuICAgICAgRltQUk9UT1RZUEVdID0gQ1tQUk9UT1RZUEVdO1xuICAgICAgcmV0dXJuIEY7XG4gICAgLy8gbWFrZSBzdGF0aWMgdmVyc2lvbnMgZm9yIHByb3RvdHlwZSBtZXRob2RzXG4gICAgfSkob3V0KSA6IElTX1BST1RPICYmIHR5cGVvZiBvdXQgPT0gJ2Z1bmN0aW9uJyA/IGN0eChGdW5jdGlvbi5jYWxsLCBvdXQpIDogb3V0O1xuICAgIC8vIGV4cG9ydCBwcm90byBtZXRob2RzIHRvIGNvcmUuJUNPTlNUUlVDVE9SJS5tZXRob2RzLiVOQU1FJVxuICAgIGlmKElTX1BST1RPKXtcbiAgICAgIChleHBvcnRzLnZpcnR1YWwgfHwgKGV4cG9ydHMudmlydHVhbCA9IHt9KSlba2V5XSA9IG91dDtcbiAgICAgIC8vIGV4cG9ydCBwcm90byBtZXRob2RzIHRvIGNvcmUuJUNPTlNUUlVDVE9SJS5wcm90b3R5cGUuJU5BTUUlXG4gICAgICBpZih0eXBlICYgJGV4cG9ydC5SICYmIGV4cFByb3RvICYmICFleHBQcm90b1trZXldKWhpZGUoZXhwUHJvdG8sIGtleSwgb3V0KTtcbiAgICB9XG4gIH1cbn07XG4vLyB0eXBlIGJpdG1hcFxuJGV4cG9ydC5GID0gMTsgICAvLyBmb3JjZWRcbiRleHBvcnQuRyA9IDI7ICAgLy8gZ2xvYmFsXG4kZXhwb3J0LlMgPSA0OyAgIC8vIHN0YXRpY1xuJGV4cG9ydC5QID0gODsgICAvLyBwcm90b1xuJGV4cG9ydC5CID0gMTY7ICAvLyBiaW5kXG4kZXhwb3J0LlcgPSAzMjsgIC8vIHdyYXBcbiRleHBvcnQuVSA9IDY0OyAgLy8gc2FmZVxuJGV4cG9ydC5SID0gMTI4OyAvLyByZWFsIHByb3RvIG1ldGhvZCBmb3IgYGxpYnJhcnlgIFxubW9kdWxlLmV4cG9ydHMgPSAkZXhwb3J0O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZXhwb3J0LmpzXG4gKiogbW9kdWxlIGlkID0gOFxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDFcbiAqKi8iLCIvLyBodHRwczovL2dpdGh1Yi5jb20vemxvaXJvY2svY29yZS1qcy9pc3N1ZXMvODYjaXNzdWVjb21tZW50LTExNTc1OTAyOFxudmFyIGdsb2JhbCA9IG1vZHVsZS5leHBvcnRzID0gdHlwZW9mIHdpbmRvdyAhPSAndW5kZWZpbmVkJyAmJiB3aW5kb3cuTWF0aCA9PSBNYXRoXG4gID8gd2luZG93IDogdHlwZW9mIHNlbGYgIT0gJ3VuZGVmaW5lZCcgJiYgc2VsZi5NYXRoID09IE1hdGggPyBzZWxmIDogRnVuY3Rpb24oJ3JldHVybiB0aGlzJykoKTtcbmlmKHR5cGVvZiBfX2cgPT0gJ251bWJlcicpX19nID0gZ2xvYmFsOyAvLyBlc2xpbnQtZGlzYWJsZS1saW5lIG5vLXVuZGVmXG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19nbG9iYWwuanNcbiAqKiBtb2R1bGUgaWQgPSA5XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMVxuICoqLyIsInZhciBjb3JlID0gbW9kdWxlLmV4cG9ydHMgPSB7dmVyc2lvbjogJzIuMi4wJ307XG5pZih0eXBlb2YgX19lID09ICdudW1iZXInKV9fZSA9IGNvcmU7IC8vIGVzbGludC1kaXNhYmxlLWxpbmUgbm8tdW5kZWZcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2NvcmUuanNcbiAqKiBtb2R1bGUgaWQgPSAxMFxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDFcbiAqKi8iLCIvLyBvcHRpb25hbCAvIHNpbXBsZSBjb250ZXh0IGJpbmRpbmdcbnZhciBhRnVuY3Rpb24gPSByZXF1aXJlKCcuL19hLWZ1bmN0aW9uJyk7XG5tb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGZuLCB0aGF0LCBsZW5ndGgpe1xuICBhRnVuY3Rpb24oZm4pO1xuICBpZih0aGF0ID09PSB1bmRlZmluZWQpcmV0dXJuIGZuO1xuICBzd2l0Y2gobGVuZ3RoKXtcbiAgICBjYXNlIDE6IHJldHVybiBmdW5jdGlvbihhKXtcbiAgICAgIHJldHVybiBmbi5jYWxsKHRoYXQsIGEpO1xuICAgIH07XG4gICAgY2FzZSAyOiByZXR1cm4gZnVuY3Rpb24oYSwgYil7XG4gICAgICByZXR1cm4gZm4uY2FsbCh0aGF0LCBhLCBiKTtcbiAgICB9O1xuICAgIGNhc2UgMzogcmV0dXJuIGZ1bmN0aW9uKGEsIGIsIGMpe1xuICAgICAgcmV0dXJuIGZuLmNhbGwodGhhdCwgYSwgYiwgYyk7XG4gICAgfTtcbiAgfVxuICByZXR1cm4gZnVuY3Rpb24oLyogLi4uYXJncyAqLyl7XG4gICAgcmV0dXJuIGZuLmFwcGx5KHRoYXQsIGFyZ3VtZW50cyk7XG4gIH07XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fY3R4LmpzXG4gKiogbW9kdWxlIGlkID0gMTFcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxXG4gKiovIiwibW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihpdCl7XG4gIGlmKHR5cGVvZiBpdCAhPSAnZnVuY3Rpb24nKXRocm93IFR5cGVFcnJvcihpdCArICcgaXMgbm90IGEgZnVuY3Rpb24hJyk7XG4gIHJldHVybiBpdDtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19hLWZ1bmN0aW9uLmpzXG4gKiogbW9kdWxlIGlkID0gMTJcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxXG4gKiovIiwidmFyIGRQICAgICAgICAgPSByZXF1aXJlKCcuL19vYmplY3QtZHAnKVxuICAsIGNyZWF0ZURlc2MgPSByZXF1aXJlKCcuL19wcm9wZXJ0eS1kZXNjJyk7XG5tb2R1bGUuZXhwb3J0cyA9IHJlcXVpcmUoJy4vX2Rlc2NyaXB0b3JzJykgPyBmdW5jdGlvbihvYmplY3QsIGtleSwgdmFsdWUpe1xuICByZXR1cm4gZFAuZihvYmplY3QsIGtleSwgY3JlYXRlRGVzYygxLCB2YWx1ZSkpO1xufSA6IGZ1bmN0aW9uKG9iamVjdCwga2V5LCB2YWx1ZSl7XG4gIG9iamVjdFtrZXldID0gdmFsdWU7XG4gIHJldHVybiBvYmplY3Q7XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9faGlkZS5qc1xuICoqIG1vZHVsZSBpZCA9IDEzXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMVxuICoqLyIsInZhciBhbk9iamVjdCAgICAgICA9IHJlcXVpcmUoJy4vX2FuLW9iamVjdCcpXG4gICwgSUU4X0RPTV9ERUZJTkUgPSByZXF1aXJlKCcuL19pZTgtZG9tLWRlZmluZScpXG4gICwgdG9QcmltaXRpdmUgICAgPSByZXF1aXJlKCcuL190by1wcmltaXRpdmUnKVxuICAsIGRQICAgICAgICAgICAgID0gT2JqZWN0LmRlZmluZVByb3BlcnR5O1xuXG5leHBvcnRzLmYgPSByZXF1aXJlKCcuL19kZXNjcmlwdG9ycycpID8gT2JqZWN0LmRlZmluZVByb3BlcnR5IDogZnVuY3Rpb24gZGVmaW5lUHJvcGVydHkoTywgUCwgQXR0cmlidXRlcyl7XG4gIGFuT2JqZWN0KE8pO1xuICBQID0gdG9QcmltaXRpdmUoUCwgdHJ1ZSk7XG4gIGFuT2JqZWN0KEF0dHJpYnV0ZXMpO1xuICBpZihJRThfRE9NX0RFRklORSl0cnkge1xuICAgIHJldHVybiBkUChPLCBQLCBBdHRyaWJ1dGVzKTtcbiAgfSBjYXRjaChlKXsgLyogZW1wdHkgKi8gfVxuICBpZignZ2V0JyBpbiBBdHRyaWJ1dGVzIHx8ICdzZXQnIGluIEF0dHJpYnV0ZXMpdGhyb3cgVHlwZUVycm9yKCdBY2Nlc3NvcnMgbm90IHN1cHBvcnRlZCEnKTtcbiAgaWYoJ3ZhbHVlJyBpbiBBdHRyaWJ1dGVzKU9bUF0gPSBBdHRyaWJ1dGVzLnZhbHVlO1xuICByZXR1cm4gTztcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19vYmplY3QtZHAuanNcbiAqKiBtb2R1bGUgaWQgPSAxNFxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDFcbiAqKi8iLCJ2YXIgaXNPYmplY3QgPSByZXF1aXJlKCcuL19pcy1vYmplY3QnKTtcbm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oaXQpe1xuICBpZighaXNPYmplY3QoaXQpKXRocm93IFR5cGVFcnJvcihpdCArICcgaXMgbm90IGFuIG9iamVjdCEnKTtcbiAgcmV0dXJuIGl0O1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2FuLW9iamVjdC5qc1xuICoqIG1vZHVsZSBpZCA9IDE1XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMVxuICoqLyIsIm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oaXQpe1xuICByZXR1cm4gdHlwZW9mIGl0ID09PSAnb2JqZWN0JyA/IGl0ICE9PSBudWxsIDogdHlwZW9mIGl0ID09PSAnZnVuY3Rpb24nO1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2lzLW9iamVjdC5qc1xuICoqIG1vZHVsZSBpZCA9IDE2XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMVxuICoqLyIsIm1vZHVsZS5leHBvcnRzID0gIXJlcXVpcmUoJy4vX2Rlc2NyaXB0b3JzJykgJiYgIXJlcXVpcmUoJy4vX2ZhaWxzJykoZnVuY3Rpb24oKXtcclxuICByZXR1cm4gT2JqZWN0LmRlZmluZVByb3BlcnR5KHJlcXVpcmUoJy4vX2RvbS1jcmVhdGUnKSgnZGl2JyksICdhJywge2dldDogZnVuY3Rpb24oKXsgcmV0dXJuIDc7IH19KS5hICE9IDc7XHJcbn0pO1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9faWU4LWRvbS1kZWZpbmUuanNcbiAqKiBtb2R1bGUgaWQgPSAxN1xuICoqIG1vZHVsZSBjaHVua3MgPSAwIDFcbiAqKi8iLCIvLyBUaGFuaydzIElFOCBmb3IgaGlzIGZ1bm55IGRlZmluZVByb3BlcnR5XG5tb2R1bGUuZXhwb3J0cyA9ICFyZXF1aXJlKCcuL19mYWlscycpKGZ1bmN0aW9uKCl7XG4gIHJldHVybiBPYmplY3QuZGVmaW5lUHJvcGVydHkoe30sICdhJywge2dldDogZnVuY3Rpb24oKXsgcmV0dXJuIDc7IH19KS5hICE9IDc7XG59KTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2Rlc2NyaXB0b3JzLmpzXG4gKiogbW9kdWxlIGlkID0gMThcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxXG4gKiovIiwibW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihleGVjKXtcbiAgdHJ5IHtcbiAgICByZXR1cm4gISFleGVjKCk7XG4gIH0gY2F0Y2goZSl7XG4gICAgcmV0dXJuIHRydWU7XG4gIH1cbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19mYWlscy5qc1xuICoqIG1vZHVsZSBpZCA9IDE5XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMVxuICoqLyIsInZhciBpc09iamVjdCA9IHJlcXVpcmUoJy4vX2lzLW9iamVjdCcpXG4gICwgZG9jdW1lbnQgPSByZXF1aXJlKCcuL19nbG9iYWwnKS5kb2N1bWVudFxuICAvLyBpbiBvbGQgSUUgdHlwZW9mIGRvY3VtZW50LmNyZWF0ZUVsZW1lbnQgaXMgJ29iamVjdCdcbiAgLCBpcyA9IGlzT2JqZWN0KGRvY3VtZW50KSAmJiBpc09iamVjdChkb2N1bWVudC5jcmVhdGVFbGVtZW50KTtcbm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oaXQpe1xuICByZXR1cm4gaXMgPyBkb2N1bWVudC5jcmVhdGVFbGVtZW50KGl0KSA6IHt9O1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2RvbS1jcmVhdGUuanNcbiAqKiBtb2R1bGUgaWQgPSAyMFxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDFcbiAqKi8iLCIvLyA3LjEuMSBUb1ByaW1pdGl2ZShpbnB1dCBbLCBQcmVmZXJyZWRUeXBlXSlcbnZhciBpc09iamVjdCA9IHJlcXVpcmUoJy4vX2lzLW9iamVjdCcpO1xuLy8gaW5zdGVhZCBvZiB0aGUgRVM2IHNwZWMgdmVyc2lvbiwgd2UgZGlkbid0IGltcGxlbWVudCBAQHRvUHJpbWl0aXZlIGNhc2Vcbi8vIGFuZCB0aGUgc2Vjb25kIGFyZ3VtZW50IC0gZmxhZyAtIHByZWZlcnJlZCB0eXBlIGlzIGEgc3RyaW5nXG5tb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGl0LCBTKXtcbiAgaWYoIWlzT2JqZWN0KGl0KSlyZXR1cm4gaXQ7XG4gIHZhciBmbiwgdmFsO1xuICBpZihTICYmIHR5cGVvZiAoZm4gPSBpdC50b1N0cmluZykgPT0gJ2Z1bmN0aW9uJyAmJiAhaXNPYmplY3QodmFsID0gZm4uY2FsbChpdCkpKXJldHVybiB2YWw7XG4gIGlmKHR5cGVvZiAoZm4gPSBpdC52YWx1ZU9mKSA9PSAnZnVuY3Rpb24nICYmICFpc09iamVjdCh2YWwgPSBmbi5jYWxsKGl0KSkpcmV0dXJuIHZhbDtcbiAgaWYoIVMgJiYgdHlwZW9mIChmbiA9IGl0LnRvU3RyaW5nKSA9PSAnZnVuY3Rpb24nICYmICFpc09iamVjdCh2YWwgPSBmbi5jYWxsKGl0KSkpcmV0dXJuIHZhbDtcbiAgdGhyb3cgVHlwZUVycm9yKFwiQ2FuJ3QgY29udmVydCBvYmplY3QgdG8gcHJpbWl0aXZlIHZhbHVlXCIpO1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX3RvLXByaW1pdGl2ZS5qc1xuICoqIG1vZHVsZSBpZCA9IDIxXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMVxuICoqLyIsIm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oYml0bWFwLCB2YWx1ZSl7XG4gIHJldHVybiB7XG4gICAgZW51bWVyYWJsZSAgOiAhKGJpdG1hcCAmIDEpLFxuICAgIGNvbmZpZ3VyYWJsZTogIShiaXRtYXAgJiAyKSxcbiAgICB3cml0YWJsZSAgICA6ICEoYml0bWFwICYgNCksXG4gICAgdmFsdWUgICAgICAgOiB2YWx1ZVxuICB9O1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX3Byb3BlcnR5LWRlc2MuanNcbiAqKiBtb2R1bGUgaWQgPSAyMlxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDFcbiAqKi8iLCIndXNlIHN0cmljdCc7XG5cbmV4cG9ydCBkZWZhdWx0IGNsYXNzIEZvcm1IZWxwZXIge1xuICAgIC8qKlxuICAgICAqIFZhbGlkYXRlIGZvcm0gdGhyb3VnaCBpbnB1dHNcbiAgICAgKiBAcGFyYW0ge2pRdWVyeX0gJGlucHV0c1xuICAgICAqL1xuICAgIGNvbnN0cnVjdG9yKCRpbnB1dHMpIHtcbiAgICAgICAgdGhpcy4kaW5wdXRzID0gJGlucHV0cztcbiAgICAgICAgdGhpcy5hcnJFcnJvcnMgPSBbXTtcbiAgICAgICAgdGhpcy5fYXNzaWduRXZlbnRzKCk7XG4gICAgfVxuXG4gICAgX2Fzc2lnbkV2ZW50cygpIHtcbiAgICAgICAgdGhpcy4kaW5wdXRzLm9uKCdpbnB1dCcsIChlKSA9PiB0aGlzLl9yZW1vdmVFcnJvcigkKGUudGFyZ2V0KSkpO1xuICAgIH1cblxuICAgIGlzVmFsaWRJbnB1dHMoKSB7XG4gICAgICAgIGNvbnN0ICRpbnB1dHMgPSB0aGlzLiRpbnB1dHM7XG4gICAgICAgIGxldCBlcnJvciA9IDA7XG5cbiAgICAgICAgJGlucHV0cy5lYWNoKChpbmRleCwgaW5wdXQpID0+IHtcbiAgICAgICAgICAgIGNvbnN0ICRpbnB1dCA9ICQoaW5wdXQpO1xuXG4gICAgICAgICAgICBpZiAoIXRoaXMuX2lzVmFsaWRJbnB1dCgkaW5wdXQpKSBlcnJvciArPSAxO1xuICAgICAgICB9KTtcbiAgICAgICAgcmV0dXJuIEJvb2xlYW4oIWVycm9yKTtcbiAgICB9XG5cbiAgICAvKipcbiAgICAgKiBDaGVjayBnaXZlbiBpbnB1dCwgaXMgaXQgdmFsaWQ/XG4gICAgICogQHBhcmFtIHtqUXVlcnl9ICRpbnB1dFxuICAgICAqIEByZXR1cm5zIHtib29sZWFufSAtIElzIHZhbGlkIGlucHV0P1xuICAgICAqL1xuICAgIF9pc1ZhbGlkSW5wdXQoJGlucHV0KSB7XG4gICAgICAgIGNvbnN0IHZhbHVlID0gJC50cmltKCRpbnB1dC52YWwoKSk7XG5cbiAgICAgICAgaWYgKCF2YWx1ZSkge1xuICAgICAgICAgICAgdGhpcy5fc2V0RXJyb3IoJGlucHV0LCAnRW1wdHknKTtcbiAgICAgICAgICAgIHJldHVybiBmYWxzZTtcbiAgICAgICAgfVxuXG4gICAgICAgIGlmICgoJGlucHV0Lmhhc0NsYXNzKCd0eXBlLWVtYWlsJykpICYmICF0aGlzLl9pc1ZhbGlkRW1haWwodmFsdWUpKSB7XG4gICAgICAgICAgICB0aGlzLl9zZXRFcnJvcigkaW5wdXQsICdFbWFpbCBpcyBub3QgdmFsaWQnKTtcbiAgICAgICAgICAgIHJldHVybiBmYWxzZTtcbiAgICAgICAgfVxuICAgICAgICByZXR1cm4gdHJ1ZTtcbiAgICB9XG5cbiAgICAvKipcbiAgICAgKiBJcyBFbWFpbCB2YWxpZD9cbiAgICAgKiBAcGFyYW0ge3N0cmluZ30gZW1haWxcbiAgICAgKiBAcmV0dXJucyB7Ym9vbGVhbn1cbiAgICAgKi9cbiAgICBfaXNWYWxpZEVtYWlsKGVtYWlsKSB7XG4gICAgICAgIHZhciByZSA9IC9eKChbXjw+KClcXFtcXF1cXFxcLiw7Olxcc0BcIl0rKFxcLltePD4oKVxcW1xcXVxcXFwuLDs6XFxzQFwiXSspKil8KFwiLitcIikpQCgoXFxbWzAtOV17MSwzfVxcLlswLTldezEsM31cXC5bMC05XXsxLDN9XFwuWzAtOV17MSwzfV0pfCgoW2EtekEtWlxcLTAtOV0rXFwuKStbYS16QS1aXXsyLH0pKSQvO1xuICAgICAgICByZXR1cm4gcmUudGVzdChlbWFpbCk7XG4gICAgfVxuXG4gICAgLyoqXG4gICAgICogU2V0IGVycm9yIGZvciBpbnB1dFxuICAgICAqIEBwYXJhbSB7alF1ZXJ5fSAkaW5wdXRcbiAgICAgKiBAcGFyYW0ge3N0cmluZ30gZXJyb3JUZXh0XG4gICAgICovXG4gICAgX3NldEVycm9yKCRpbnB1dCwgZXJyb3JUZXh0KSB7XG4gICAgICAgIGNvbnN0ICRwYXJlbnQgPSAkaW5wdXQucGFyZW50KCk7XG4gICAgICAgIGNvbnN0ICRlcnJvciA9ICRwYXJlbnQuZmluZCgnLmItZXJyb3InKTtcblxuICAgICAgICBpZiAoJGVycm9yLmxlbmd0aCkgcmV0dXJuO1xuXG4gICAgICAgICRwYXJlbnQuYWRkQ2xhc3MoJ2ItZXJyb3Jfc2hvdycpO1xuICAgICAgICAkKCc8ZGl2IGNsYXNzPVwiYi1lcnJvclwiIC8+JylcbiAgICAgICAgICAgIC50ZXh0KGVycm9yVGV4dClcbiAgICAgICAgICAgIC5wcmVwZW5kVG8oJHBhcmVudCk7XG5cbiAgICAgICAgdGhpcy5hcnJFcnJvcnMucHVzaCh7XG4gICAgICAgICAgICBuYW1lOiAkaW5wdXQuYXR0cignbmFtZScpLFxuICAgICAgICAgICAgZXJyb3I6IGVycm9yVGV4dFxuICAgICAgICB9KVxuICAgIH1cblxuICAgIC8qKlxuICAgICAqIFJlbW92ZSBlcnJvciBmb3IgaW5wdXRcbiAgICAgKiBAcGFyYW0ge2pRdWVyeX0gJGlucHV0XG4gICAgICovXG4gICAgX3JlbW92ZUVycm9yKCRpbnB1dCkge1xuICAgICAgICBjb25zdCAkcGFyZW50ID0gJGlucHV0LnBhcmVudCgpO1xuXG4gICAgICAgICRwYXJlbnRcbiAgICAgICAgICAgIC5yZW1vdmVDbGFzcygnYi1lcnJvcl9zaG93JylcbiAgICAgICAgICAgIC5maW5kKCcuYi1lcnJvcicpLnJlbW92ZSgpO1xuXG4gICAgICAgIHRoaXMuYXJyRXJyb3JzID0gdGhpcy5hcnJFcnJvcnMuZmlsdGVyKGZ1bmN0aW9uIChpdGVtKSB7XG4gICAgICAgICAgICByZXR1cm4gaXRlbS5uYW1lICE9PSAkaW5wdXQuYXR0cignbmFtZScpXG4gICAgICAgIH0pXG4gICAgfVxuXG4gICAgLyoqXG4gICAgICogU2V0IGVycm9yc1xuICAgICAqIEBwYXJhbSB7QXJyYXl9IGVycm9ycyAtIFt7bmFtZTogXCJlbWFpbFwiLCBlcnJvcjogXCJlbXB0eVwifSwge25hbWU6IFwicGFzc3dvcmRcIiwgZXJyb3I6IFwiZW1wdHlcIn1dXG4gICAgICovXG4gICAgc2V0RXJyb3JzKGVycm9ycykge1xuICAgICAgICBlcnJvcnMuZm9yRWFjaCgoaXRlbSkgPT4ge1xuICAgICAgICAgICAgY29uc3QgJGN1cnJlbnRJbnB1dCA9IHRoaXMuJGlucHV0cy5maWx0ZXIoJ1tuYW1lPVwiJyArIGl0ZW0ubmFtZSArICdcIl0nKS5maXJzdCgpO1xuXG4gICAgICAgICAgICBpZiAoJGN1cnJlbnRJbnB1dC5sZW5ndGgpIHRoaXMuX3NldEVycm9yKCRjdXJyZW50SW5wdXQsIGl0ZW0uZXJyb3IpXG4gICAgICAgIH0pXG4gICAgfVxuXG4gICAgLyoqXG4gICAgICogR2V0IHR4dCB2ZXJzaW9uIG9mIGFsbCBlcnJvcnNcbiAgICAgKi9cbiAgICBnZXRFcnJvcnNUZXh0KGVycm9ycykge1xuICAgICAgICBjb25zdCBhcnJFcnJvcnMgPSBlcnJvcnMgfHwgdGhpcy5hcnJFcnJvcnM7XG4gICAgICAgIGxldCBlcnJvclR4dCA9ICcnO1xuXG4gICAgICAgIGFyckVycm9ycy5mb3JFYWNoKChpdGVtKSA9PiB7XG4gICAgICAgICAgICBjb25zdCBuYW1lID0gaXRlbS5uYW1lWzBdLnRvVXBwZXJDYXNlKCkgKyBpdGVtLm5hbWUuc3Vic3RyKDEpO1xuXG4gICAgICAgICAgICBlcnJvclR4dCArPSBgJHtuYW1lfSB2YWx1ZSBpcyAke2l0ZW0uZXJyb3IudG9Mb3dlckNhc2UoKX0uIGA7XG4gICAgICAgIH0pO1xuXG4gICAgICAgIHJldHVybiBlcnJvclR4dDtcbiAgICB9XG5cbiAgICAvKipcbiAgICAgKiBSZW1vdmUgYWxsIGVycm9yc1xuICAgICAqL1xuICAgIHJlbW92ZUVycm9ycygpIHtcbiAgICAgICAgdGhpcy4kaW5wdXRzLmVhY2goKGluZGV4LCBlbCkgPT4ge1xuICAgICAgICAgICAgY29uc3QgJGVsID0gJChlbCk7XG4gICAgICAgICAgICB0aGlzLl9yZW1vdmVFcnJvcigkZWwpXG4gICAgICAgIH0pXG4gICAgfVxuXG4gICAgY2xlYXJGb3JtKCkge1xuICAgICAgICB0aGlzLiRpbnB1dHMuZWFjaCgoaW5kZXgsIGVsKSA9PiB7XG4gICAgICAgICAgICBjb25zdCAkZWwgPSAkKGVsKTtcbiAgICAgICAgICAgIGlmICghJGVsLmF0dHIoXCJkaXNhYmxlZFwiKSkgICRlbC52YWwoJycpO1xuICAgICAgICB9KVxuICAgIH1cbn1cblxuXG5cbi8qKiBXRUJQQUNLIEZPT1RFUiAqKlxuICoqIC4vZnJvbnRlbmQvanMvY29tbW9uL19mb3JtLWhlbHBlci5qc1xuICoqLyJdLCJzb3VyY2VSb290IjoiIn0=