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

	module.exports = __webpack_require__(54);


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
/* 23 */,
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
/* 49 */,
/* 50 */,
/* 51 */,
/* 52 */,
/* 53 */,
/* 54 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';
	
	var _cardForm = __webpack_require__(55);
	
	var _cardForm2 = _interopRequireDefault(_cardForm);
	
	var _feeForm = __webpack_require__(56);
	
	var _feeForm2 = _interopRequireDefault(_feeForm);
	
	var _supportersTable = __webpack_require__(57);
	
	var _supportersTable2 = _interopRequireDefault(_supportersTable);
	
	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }
	
	$(function () {
	    _feeForm2.default.plugin('.js-fee-form');
	    _cardForm2.default.plugin('.js-card-form');
	    _supportersTable2.default.plugin('.js-support-table');
	});

/***/ },
/* 55 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';
	
	/**
	 * Form for updating card information
	 * @param selector
	 * @constructor
	 */
	
	Object.defineProperty(exports, "__esModule", {
	    value: true
	});
	
	var _classCallCheck2 = __webpack_require__(3);
	
	var _classCallCheck3 = _interopRequireDefault(_classCallCheck2);
	
	var _createClass2 = __webpack_require__(4);
	
	var _createClass3 = _interopRequireDefault(_createClass2);
	
	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }
	
	var Widget = function () {
	    function Widget(selector) {
	        (0, _classCallCheck3.default)(this, Widget);
	
	        var self = this;
	
	        self.$root = $(selector);
	        self.locals = self._getDom();
	        self.apikey = self.$root.data('apikey');
	
	        self._getStripeScript().done(function () {
	            Stripe.setPublishableKey(self.apikey);
	            self._init();
	            self._assignEvents();
	        });
	    }
	
	    (0, _createClass3.default)(Widget, [{
	        key: '_getStripeScript',
	        value: function _getStripeScript() {
	            return $.ajax({
	                url: 'https://js.stripe.com/v2/',
	                dataType: "script"
	            });
	        }
	    }, {
	        key: '_getDom',
	        value: function _getDom() {
	            var $root = this.$root;
	
	            return {
	                $inputNumber: $root.find('[data-card-number]'),
	                $inputName: $root.find('[data-card-name]'),
	                $inputMonth: $root.find('[data-card-month]'),
	                $inputYear: $root.find('[data-card-year]'),
	                $inputCVC: $root.find('[data-card-cvc]'),
	                $submit: $root.find('[data-card-submit]')
	            };
	        }
	    }, {
	        key: '_init',
	        value: function _init() {
	            if (!$.fn.payment) {
	                console.log('There is no payment plugin on this page');
	                return;
	            }
	
	            this.locals.$inputNumber.payment('formatCardNumber');
	            this.locals.$inputCVC.payment('formatCardCVC');
	        }
	    }, {
	        key: '_assignEvents',
	        value: function _assignEvents() {
	            var self = this;
	
	            self.$root.on('keyup', 'input', function () {
	                self._removeError($(this));
	            }).on('change paste keyup', '[data-card-name]', function (e) {
	                var $this = $(this);
	                $this.val($this.val().toUpperCase());
	            }).on('submit', self._onSubmitHandler.bind(self));
	        }
	    }, {
	        key: '_setError',
	        value: function _setError($el) {
	            $el.parent().addClass('has-error');
	        }
	    }, {
	        key: '_removeError',
	        value: function _removeError($el) {
	            $el.parent().removeClass('has-error');
	        }
	    }, {
	        key: '_disabledForm',
	        value: function _disabledForm() {
	            this.locals.$submit.prop('disabled', true);
	            $("body").css("cursor", "progress");
	        }
	    }, {
	        key: '_enabledForm',
	        value: function _enabledForm() {
	            this.locals.$submit.prop('disabled', false);
	            $("body").css("cursor", "default");
	        }
	    }, {
	        key: '_addTokenInput',
	        value: function _addTokenInput(token) {
	            var $root = this.$root,
	                template = '<input type="hidden" value="' + token + '" name="token" />';
	
	            $root.find('input[name="token"]').remove();
	            $root.append(template);
	        }
	    }, {
	        key: '_onSubmitHandler',
	        value: function _onSubmitHandler(e) {
	            var self = this;
	            e.preventDefault();
	
	            if (!this.isValidForm()) return;
	            this._disabledForm();
	
	            Stripe.card.createToken(self.$root, self._stripeHandler.bind(self));
	        }
	    }, {
	        key: '_stripeHandler',
	        value: function _stripeHandler(status, response) {
	            var self = this;
	            var data = undefined,
	                errorMsg = undefined;
	
	            if (response.error) {
	                self._enabledForm();
	            } else {
	                self._addTokenInput(response.id);
	
	                data = this.$root.serialize();
	                self._sendFormData(data).done(function (data) {
	                    if (data.hasOwnProperty("redirect")) {
	                        window.location = data.redirect;
	                    } else if (data.hasOwnProperty("message")) {
	                        success(data.message);
	                    } else {
	                        var msg = "Internal error #2001. Your card has been charged. ";
	                        msg += "Do not make payment again. Please proceed to your profile directly.";
	                        error(msg);
	                    }
	                }).fail(function (jqXHR, status) {
	                    if (status == "error") {
	                        errorMsg = JSON.parse(jqXHR.responseText);
	                        error(errorMsg.message);
	                    } else {
	                        error("Internal error #2000. Your card has not been charged. Please try again.");
	                    }
	                }).complete(function () {
	                    self._enabledForm();
	                });
	            }
	        }
	    }, {
	        key: '_sendFormData',
	        value: function _sendFormData(dataForm) {
	            return $.ajax({
	                type: "POST",
	                url: this.$root.attr("action"),
	                data: dataForm,
	                dataType: "json"
	            });
	        }
	    }, {
	        key: 'isValidForm',
	        value: function isValidForm() {
	            var self = this,
	                valid = true,
	                locals = this.locals,
	                isValidNumber = $.payment.validateCardNumber(locals.$inputNumber.val()),
	                isValidExpiry = $.payment.validateCardExpiry(locals.$inputMonth.val(), locals.$inputYear.val()),
	                isValidCVC = $.payment.validateCardCVC(locals.$inputCVC.val()),
	                isValidName = +locals.$inputName.val().length;
	
	            if (!isValidNumber) {
	                self._setError(locals.$inputNumber);
	                valid = false;
	            }
	
	            if (!isValidExpiry) {
	                self._setError(locals.$inputMonth);
	                self._setError(locals.$inputYear);
	                valid = false;
	            }
	
	            if (!isValidCVC) {
	                self._setError(locals.$inputCVC);
	                valid = false;
	            }
	
	            if (!isValidName) {
	                self._setError(locals.$inputName);
	                valid = false;
	            }
	
	            return valid;
	        }
	
	        // static
	
	    }], [{
	        key: 'plugin',
	        value: function plugin(selector, options) {
	            var $elems = $(selector);
	            if (!$elems.length) return;
	
	            return $elems.each(function (index, el) {
	                var $element = $(el);
	                var data = $element.data('widget.scrollto');
	
	                if (!data) {
	                    data = new Widget(el, options);
	                    $element.data('widget', data);
	                }
	            });
	        }
	    }]);
	    return Widget;
	}();

	exports.default = Widget;

/***/ },
/* 56 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';
	
	/**
	 * Form for updating contribution level
	 * @param selector
	 * @constructor
	 */
	
	Object.defineProperty(exports, "__esModule", {
	    value: true
	});
	
	var _classCallCheck2 = __webpack_require__(3);
	
	var _classCallCheck3 = _interopRequireDefault(_classCallCheck2);
	
	var _createClass2 = __webpack_require__(4);
	
	var _createClass3 = _interopRequireDefault(_createClass2);
	
	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }
	
	var Widget = function () {
	    function Widget(selector) {
	        (0, _classCallCheck3.default)(this, Widget);
	
	        this.$root = $(selector);
	        this.locals = this._getDom();
	
	        this._assignEvents();
	        this._updateAmount(this.locals.$inputFee);
	    }
	
	    (0, _createClass3.default)(Widget, [{
	        key: '_getDom',
	        value: function _getDom() {
	            var $root = this.$root;
	
	            return {
	                $inputFee: $root.find('.b-feestrip__input').first(),
	                $taxPlace: $root.find('[data-fee-tax]'),
	                $amountPlace: $root.find('[data-fee-amount]'),
	                $payPlace: $root.find('[data-fee-pay]')
	            };
	        }
	    }, {
	        key: '_assignEvents',
	        value: function _assignEvents() {
	            var self = this;
	
	            self.$root.on('change paste keyup', '.b-feestrip__input', function (e) {
	                var $this = $(this);
	
	                self._removeError($this);
	                self._updateAmount($this);
	            }).on('submit', self._onSubmitForm.bind(self));
	        }
	    }, {
	        key: '_onSubmitForm',
	        value: function _onSubmitForm(e) {
	            var self = this;
	            e.preventDefault();
	
	            if (!this.isValidForm()) return;
	
	            var feeAmount = this.locals.$inputFee.val();
	
	            this._sendFeeData(feeAmount).done(function (data) {
	                if (data.hasOwnProperty("message")) {
	                    success(data.message);
	                }
	            }).fail(function (data) {
	                var errorMsg = JSON.parse(jqXHR.responseText);
	                error(errorMsg.message);
	            });
	        }
	    }, {
	        key: '_setError',
	        value: function _setError($el) {
	            var $parent = $el.parent();
	
	            if (!$parent.hasClass('has-error')) {
	                $parent.addClass('has-error');
	            }
	        }
	    }, {
	        key: '_removeError',
	        value: function _removeError($el) {
	            $el.parent().removeClass('has-error');
	        }
	
	        /**
	         * Update tax and pay price on the from, based on input value $el
	         * @param {jQuery} $el - $(input)
	         * @private
	         */
	
	    }, {
	        key: '_updateAmount',
	        value: function _updateAmount($el) {
	            var locals = this.locals,
	                amount = $el.val() < 1 ? 0.00 : parseInt($el.val()),
	                taxPercent = parseFloat($el.data('tax')),
	                tax = undefined,
	                amountWithTax = undefined;
	
	            tax = amount * taxPercent / 100;
	            amountWithTax = amount + tax;
	
	            locals.$amountPlace.text(amount);
	            locals.$taxPlace.text(tax);
	            locals.$payPlace.text(amountWithTax);
	        }
	
	        /**
	         * Check, is Form valid?
	         * @returns {Boolean}
	         */
	
	    }, {
	        key: 'isValidForm',
	        value: function isValidForm() {
	            var valid = true,
	                $inputFee = this.locals.$inputFee,
	                isValidAmount = $inputFee.val().length > 0 && !isNaN($inputFee.val());
	
	            if (!isValidAmount) {
	                valid = false;
	                this._setError($inputFee);
	            }
	
	            return valid;
	        }
	
	        //transport
	        /**
	         * @param {Number} feeAmount
	         * @returns {$.Deffered} - promise
	         * @private
	         */
	
	    }, {
	        key: '_sendFeeData',
	        value: function _sendFeeData(feeAmount) {
	            return $.ajax({
	                type: "POST",
	                url: this.$root.attr("action"),
	                data: { fee: feeAmount },
	                dataType: "json"
	            });
	        }
	
	        // static
	
	    }], [{
	        key: 'plugin',
	        value: function plugin(selector, options) {
	            var $elems = $(selector);
	            if (!$elems.length) return;
	
	            return $elems.each(function (index, el) {
	                var $element = $(el);
	                var data = $element.data('widget.scrollto');
	
	                if (!data) {
	                    data = new Widget(el, options);
	                    $element.data('widget', data);
	                }
	            });
	        }
	    }]);
	    return Widget;
	}();

	exports.default = Widget;

/***/ },
/* 57 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';
	
	/**
	 * @param selector
	 * @constructor
	 */
	
	Object.defineProperty(exports, "__esModule", {
	    value: true
	});
	
	var _classCallCheck2 = __webpack_require__(3);
	
	var _classCallCheck3 = _interopRequireDefault(_classCallCheck2);
	
	var _createClass2 = __webpack_require__(4);
	
	var _createClass3 = _interopRequireDefault(_createClass2);
	
	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }
	
	var Widget = function () {
	    function Widget(selector) {
	        (0, _classCallCheck3.default)(this, Widget);
	
	        this.$root = $(selector);
	        this._assignEvents();
	    }
	
	    (0, _createClass3.default)(Widget, [{
	        key: '_assignEvents',
	        value: function _assignEvents() {
	            var self = this;
	
	            self.$root.on('click', '.dlg-hmfees__link ', function (e) {
	                var $this = $(this);
	                e.preventDefault();
	
	                if ($this.hasClass('state_active')) return;
	
	                self._switchTab($this);
	            });
	        }
	    }, {
	        key: '_switchTab',
	        value: function _switchTab($link) {
	            var $target = this._getTarget($link);
	
	            if (!$target.length) return;
	
	            $target.show().siblings('.table').hide();
	            $link.addClass('state_active').siblings('.dlg-hmfees__link').removeClass('state_active');
	        }
	    }, {
	        key: '_getTarget',
	        value: function _getTarget($el) {
	            return this.$root.find($el.attr('href'));
	        }
	
	        // static
	
	    }], [{
	        key: 'plugin',
	        value: function plugin(selector, options) {
	            var $elems = $(selector);
	            if (!$elems.length) return;
	
	            return $elems.each(function (index, el) {
	                var $element = $(el);
	                var data = $element.data('widget.scrollto');
	
	                if (!data) {
	                    data = new Widget(el, options);
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
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIndlYnBhY2s6Ly8vd2VicGFjay9ib290c3RyYXAgZjBmMTY2OTBiZDk3YTI1NWNmYjk/N2VmNCoqKioqKioqIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9oZWxwZXJzL2NsYXNzQ2FsbENoZWNrLmpzPzIxYWYqKioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvaGVscGVycy9jcmVhdGVDbGFzcy5qcz8xZGZlKioqKioqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL2NvcmUtanMvb2JqZWN0L2RlZmluZS1wcm9wZXJ0eS5qcz81ZjcwKioqKioqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L2ZuL29iamVjdC9kZWZpbmUtcHJvcGVydHkuanM/NGQzMyoqKioqKioqIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL2VzNi5vYmplY3QuZGVmaW5lLXByb3BlcnR5LmpzPzhiZGUqKioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZXhwb3J0LmpzPzhlNDAqKioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZ2xvYmFsLmpzPzNjNTIqKioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fY29yZS5qcz9kNjExKioqKioqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2N0eC5qcz80ZTU5KioqKioqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2EtZnVuY3Rpb24uanM/MDY5OSoqKioqKioqIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19oaWRlLmpzPzBkMmUqKioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fb2JqZWN0LWRwLmpzPzU5ODYqKioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fYW4tb2JqZWN0LmpzPzNhZjIqKioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9faXMtb2JqZWN0LmpzP2NmZGEqKioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9faWU4LWRvbS1kZWZpbmUuanM/YjEwMioqKioqKioqIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19kZXNjcmlwdG9ycy5qcz9jMGY1KioqKioqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2ZhaWxzLmpzP2M2ZGQqKioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZG9tLWNyZWF0ZS5qcz82MTJmKioqKioqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX3RvLXByaW1pdGl2ZS5qcz8xYTY1KioqKioqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX3Byb3BlcnR5LWRlc2MuanM/MjU2YioqKioqKioqIiwid2VicGFjazovLy8uL2Zyb250ZW5kL2pzL3BheW1lbnQvbWVtYmVycy1wYXltZW50LXBhZ2UuanMiLCJ3ZWJwYWNrOi8vLy4vZnJvbnRlbmQvanMvcGF5bWVudC9wYXltZW50LXdpZGdldHMvX2NhcmQtZm9ybS5qcyIsIndlYnBhY2s6Ly8vLi9mcm9udGVuZC9qcy9wYXltZW50L3BheW1lbnQtd2lkZ2V0cy9fZmVlLWZvcm0uanMiLCJ3ZWJwYWNrOi8vLy4vZnJvbnRlbmQvanMvcGF5bWVudC9wYXltZW50LXdpZGdldHMvX3N1cHBvcnRlcnMtdGFibGUuanMiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IjtBQUFBO0FBQ0E7O0FBRUE7QUFDQTs7QUFFQTtBQUNBO0FBQ0E7O0FBRUE7QUFDQTtBQUNBLHVCQUFlO0FBQ2Y7QUFDQTtBQUNBOztBQUVBO0FBQ0E7O0FBRUE7QUFDQTs7QUFFQTtBQUNBO0FBQ0E7OztBQUdBO0FBQ0E7O0FBRUE7QUFDQTs7QUFFQTtBQUNBOztBQUVBO0FBQ0E7Ozs7Ozs7Ozs7Ozs7Ozs7QUN0Q0E7O0FBRUE7O0FBRUE7QUFDQTtBQUNBO0FBQ0E7QUFDQSxHOzs7Ozs7QUNSQTs7QUFFQTs7QUFFQTs7QUFFQTs7QUFFQSx1Q0FBc0MsdUNBQXVDLGdCQUFnQjs7QUFFN0Y7QUFDQTtBQUNBLG9CQUFtQixrQkFBa0I7QUFDckM7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7O0FBRUE7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBLEVBQUMsRzs7Ozs7O0FDMUJELG1CQUFrQix1RDs7Ozs7O0FDQWxCO0FBQ0E7QUFDQTtBQUNBO0FBQ0EsRzs7Ozs7O0FDSkE7QUFDQTtBQUNBLHNFQUF1RSwwQ0FBMEMsRTs7Ozs7O0FDRmpIO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7O0FBRUE7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQSxvRUFBbUU7QUFDbkU7QUFDQSxzRkFBcUY7QUFDckY7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBLFlBQVc7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RUFBRSxFQUFGLENBQVgsQ0FEZ0M7QUFFcEMscUJBQUksT0FBTyxTQUFTLElBQVQsQ0FBYyxpQkFBZCxDQUFQLENBRmdDOztBQUlwQyxxQkFBSSxDQUFDLElBQUQsRUFBTztBQUNQLDRCQUFPLElBQUksTUFBSixDQUFXLEVBQVgsRUFBZSxPQUFmLENBQVAsQ0FETztBQUVQLDhCQUFTLElBQVQsQ0FBYyxRQUFkLEVBQXdCLElBQXhCLEVBRk87a0JBQVg7Y0FKZSxDQUFuQixDQUo2Qjs7O1lBbkNoQiIsImZpbGUiOiJtZW1iZXJzLXBheW1lbnQtcGFnZS5qcyIsInNvdXJjZXNDb250ZW50IjpbIiBcdC8vIFRoZSBtb2R1bGUgY2FjaGVcbiBcdHZhciBpbnN0YWxsZWRNb2R1bGVzID0ge307XG5cbiBcdC8vIFRoZSByZXF1aXJlIGZ1bmN0aW9uXG4gXHRmdW5jdGlvbiBfX3dlYnBhY2tfcmVxdWlyZV9fKG1vZHVsZUlkKSB7XG5cbiBcdFx0Ly8gQ2hlY2sgaWYgbW9kdWxlIGlzIGluIGNhY2hlXG4gXHRcdGlmKGluc3RhbGxlZE1vZHVsZXNbbW9kdWxlSWRdKVxuIFx0XHRcdHJldHVybiBpbnN0YWxsZWRNb2R1bGVzW21vZHVsZUlkXS5leHBvcnRzO1xuXG4gXHRcdC8vIENyZWF0ZSBhIG5ldyBtb2R1bGUgKGFuZCBwdXQgaXQgaW50byB0aGUgY2FjaGUpXG4gXHRcdHZhciBtb2R1bGUgPSBpbnN0YWxsZWRNb2R1bGVzW21vZHVsZUlkXSA9IHtcbiBcdFx0XHRleHBvcnRzOiB7fSxcbiBcdFx0XHRpZDogbW9kdWxlSWQsXG4gXHRcdFx0bG9hZGVkOiBmYWxzZVxuIFx0XHR9O1xuXG4gXHRcdC8vIEV4ZWN1dGUgdGhlIG1vZHVsZSBmdW5jdGlvblxuIFx0XHRtb2R1bGVzW21vZHVsZUlkXS5jYWxsKG1vZHVsZS5leHBvcnRzLCBtb2R1bGUsIG1vZHVsZS5leHBvcnRzLCBfX3dlYnBhY2tfcmVxdWlyZV9fKTtcblxuIFx0XHQvLyBGbGFnIHRoZSBtb2R1bGUgYXMgbG9hZGVkXG4gXHRcdG1vZHVsZS5sb2FkZWQgPSB0cnVlO1xuXG4gXHRcdC8vIFJldHVybiB0aGUgZXhwb3J0cyBvZiB0aGUgbW9kdWxlXG4gXHRcdHJldHVybiBtb2R1bGUuZXhwb3J0cztcbiBcdH1cblxuXG4gXHQvLyBleHBvc2UgdGhlIG1vZHVsZXMgb2JqZWN0IChfX3dlYnBhY2tfbW9kdWxlc19fKVxuIFx0X193ZWJwYWNrX3JlcXVpcmVfXy5tID0gbW9kdWxlcztcblxuIFx0Ly8gZXhwb3NlIHRoZSBtb2R1bGUgY2FjaGVcbiBcdF9fd2VicGFja19yZXF1aXJlX18uYyA9IGluc3RhbGxlZE1vZHVsZXM7XG5cbiBcdC8vIF9fd2VicGFja19wdWJsaWNfcGF0aF9fXG4gXHRfX3dlYnBhY2tfcmVxdWlyZV9fLnAgPSBcIlwiO1xuXG4gXHQvLyBMb2FkIGVudHJ5IG1vZHVsZSBhbmQgcmV0dXJuIGV4cG9ydHNcbiBcdHJldHVybiBfX3dlYnBhY2tfcmVxdWlyZV9fKDApO1xuXG5cblxuLyoqIFdFQlBBQ0sgRk9PVEVSICoqXG4gKiogd2VicGFjay9ib290c3RyYXAgZjBmMTY2OTBiZDk3YTI1NWNmYjlcbiAqKi8iLCJcInVzZSBzdHJpY3RcIjtcblxuZXhwb3J0cy5fX2VzTW9kdWxlID0gdHJ1ZTtcblxuZXhwb3J0cy5kZWZhdWx0ID0gZnVuY3Rpb24gKGluc3RhbmNlLCBDb25zdHJ1Y3Rvcikge1xuICBpZiAoIShpbnN0YW5jZSBpbnN0YW5jZW9mIENvbnN0cnVjdG9yKSkge1xuICAgIHRocm93IG5ldyBUeXBlRXJyb3IoXCJDYW5ub3QgY2FsbCBhIGNsYXNzIGFzIGEgZnVuY3Rpb25cIik7XG4gIH1cbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9oZWxwZXJzL2NsYXNzQ2FsbENoZWNrLmpzXG4gKiogbW9kdWxlIGlkID0gM1xuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwiXCJ1c2Ugc3RyaWN0XCI7XG5cbmV4cG9ydHMuX19lc01vZHVsZSA9IHRydWU7XG5cbnZhciBfZGVmaW5lUHJvcGVydHkgPSByZXF1aXJlKFwiYmFiZWwtcnVudGltZS9jb3JlLWpzL29iamVjdC9kZWZpbmUtcHJvcGVydHlcIik7XG5cbnZhciBfZGVmaW5lUHJvcGVydHkyID0gX2ludGVyb3BSZXF1aXJlRGVmYXVsdChfZGVmaW5lUHJvcGVydHkpO1xuXG5mdW5jdGlvbiBfaW50ZXJvcFJlcXVpcmVEZWZhdWx0KG9iaikgeyByZXR1cm4gb2JqICYmIG9iai5fX2VzTW9kdWxlID8gb2JqIDogeyBkZWZhdWx0OiBvYmogfTsgfVxuXG5leHBvcnRzLmRlZmF1bHQgPSBmdW5jdGlvbiAoKSB7XG4gIGZ1bmN0aW9uIGRlZmluZVByb3BlcnRpZXModGFyZ2V0LCBwcm9wcykge1xuICAgIGZvciAodmFyIGkgPSAwOyBpIDwgcHJvcHMubGVuZ3RoOyBpKyspIHtcbiAgICAgIHZhciBkZXNjcmlwdG9yID0gcHJvcHNbaV07XG4gICAgICBkZXNjcmlwdG9yLmVudW1lcmFibGUgPSBkZXNjcmlwdG9yLmVudW1lcmFibGUgfHwgZmFsc2U7XG4gICAgICBkZXNjcmlwdG9yLmNvbmZpZ3VyYWJsZSA9IHRydWU7XG4gICAgICBpZiAoXCJ2YWx1ZVwiIGluIGRlc2NyaXB0b3IpIGRlc2NyaXB0b3Iud3JpdGFibGUgPSB0cnVlO1xuICAgICAgKDAsIF9kZWZpbmVQcm9wZXJ0eTIuZGVmYXVsdCkodGFyZ2V0LCBkZXNjcmlwdG9yLmtleSwgZGVzY3JpcHRvcik7XG4gICAgfVxuICB9XG5cbiAgcmV0dXJuIGZ1bmN0aW9uIChDb25zdHJ1Y3RvciwgcHJvdG9Qcm9wcywgc3RhdGljUHJvcHMpIHtcbiAgICBpZiAocHJvdG9Qcm9wcykgZGVmaW5lUHJvcGVydGllcyhDb25zdHJ1Y3Rvci5wcm90b3R5cGUsIHByb3RvUHJvcHMpO1xuICAgIGlmIChzdGF0aWNQcm9wcykgZGVmaW5lUHJvcGVydGllcyhDb25zdHJ1Y3Rvciwgc3RhdGljUHJvcHMpO1xuICAgIHJldHVybiBDb25zdHJ1Y3RvcjtcbiAgfTtcbn0oKTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL2hlbHBlcnMvY3JlYXRlQ2xhc3MuanNcbiAqKiBtb2R1bGUgaWQgPSA0XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJtb2R1bGUuZXhwb3J0cyA9IHsgXCJkZWZhdWx0XCI6IHJlcXVpcmUoXCJjb3JlLWpzL2xpYnJhcnkvZm4vb2JqZWN0L2RlZmluZS1wcm9wZXJ0eVwiKSwgX19lc01vZHVsZTogdHJ1ZSB9O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvY29yZS1qcy9vYmplY3QvZGVmaW5lLXByb3BlcnR5LmpzXG4gKiogbW9kdWxlIGlkID0gNVxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwicmVxdWlyZSgnLi4vLi4vbW9kdWxlcy9lczYub2JqZWN0LmRlZmluZS1wcm9wZXJ0eScpO1xudmFyICRPYmplY3QgPSByZXF1aXJlKCcuLi8uLi9tb2R1bGVzL19jb3JlJykuT2JqZWN0O1xubW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbiBkZWZpbmVQcm9wZXJ0eShpdCwga2V5LCBkZXNjKXtcbiAgcmV0dXJuICRPYmplY3QuZGVmaW5lUHJvcGVydHkoaXQsIGtleSwgZGVzYyk7XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvZm4vb2JqZWN0L2RlZmluZS1wcm9wZXJ0eS5qc1xuICoqIG1vZHVsZSBpZCA9IDZcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsInZhciAkZXhwb3J0ID0gcmVxdWlyZSgnLi9fZXhwb3J0Jyk7XHJcbi8vIDE5LjEuMi40IC8gMTUuMi4zLjYgT2JqZWN0LmRlZmluZVByb3BlcnR5KE8sIFAsIEF0dHJpYnV0ZXMpXHJcbiRleHBvcnQoJGV4cG9ydC5TICsgJGV4cG9ydC5GICogIXJlcXVpcmUoJy4vX2Rlc2NyaXB0b3JzJyksICdPYmplY3QnLCB7ZGVmaW5lUHJvcGVydHk6IHJlcXVpcmUoJy4vX29iamVjdC1kcCcpLmZ9KTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvZXM2Lm9iamVjdC5kZWZpbmUtcHJvcGVydHkuanNcbiAqKiBtb2R1bGUgaWQgPSA3XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJ2YXIgZ2xvYmFsICAgID0gcmVxdWlyZSgnLi9fZ2xvYmFsJylcbiAgLCBjb3JlICAgICAgPSByZXF1aXJlKCcuL19jb3JlJylcbiAgLCBjdHggICAgICAgPSByZXF1aXJlKCcuL19jdHgnKVxuICAsIGhpZGUgICAgICA9IHJlcXVpcmUoJy4vX2hpZGUnKVxuICAsIFBST1RPVFlQRSA9ICdwcm90b3R5cGUnO1xuXG52YXIgJGV4cG9ydCA9IGZ1bmN0aW9uKHR5cGUsIG5hbWUsIHNvdXJjZSl7XG4gIHZhciBJU19GT1JDRUQgPSB0eXBlICYgJGV4cG9ydC5GXG4gICAgLCBJU19HTE9CQUwgPSB0eXBlICYgJGV4cG9ydC5HXG4gICAgLCBJU19TVEFUSUMgPSB0eXBlICYgJGV4cG9ydC5TXG4gICAgLCBJU19QUk9UTyAgPSB0eXBlICYgJGV4cG9ydC5QXG4gICAgLCBJU19CSU5EICAgPSB0eXBlICYgJGV4cG9ydC5CXG4gICAgLCBJU19XUkFQICAgPSB0eXBlICYgJGV4cG9ydC5XXG4gICAgLCBleHBvcnRzICAgPSBJU19HTE9CQUwgPyBjb3JlIDogY29yZVtuYW1lXSB8fCAoY29yZVtuYW1lXSA9IHt9KVxuICAgICwgZXhwUHJvdG8gID0gZXhwb3J0c1tQUk9UT1RZUEVdXG4gICAgLCB0YXJnZXQgICAgPSBJU19HTE9CQUwgPyBnbG9iYWwgOiBJU19TVEFUSUMgPyBnbG9iYWxbbmFtZV0gOiAoZ2xvYmFsW25hbWVdIHx8IHt9KVtQUk9UT1RZUEVdXG4gICAgLCBrZXksIG93biwgb3V0O1xuICBpZihJU19HTE9CQUwpc291cmNlID0gbmFtZTtcbiAgZm9yKGtleSBpbiBzb3VyY2Upe1xuICAgIC8vIGNvbnRhaW5zIGluIG5hdGl2ZVxuICAgIG93biA9ICFJU19GT1JDRUQgJiYgdGFyZ2V0ICYmIHRhcmdldFtrZXldICE9PSB1bmRlZmluZWQ7XG4gICAgaWYob3duICYmIGtleSBpbiBleHBvcnRzKWNvbnRpbnVlO1xuICAgIC8vIGV4cG9ydCBuYXRpdmUgb3IgcGFzc2VkXG4gICAgb3V0ID0gb3duID8gdGFyZ2V0W2tleV0gOiBzb3VyY2Vba2V5XTtcbiAgICAvLyBwcmV2ZW50IGdsb2JhbCBwb2xsdXRpb24gZm9yIG5hbWVzcGFjZXNcbiAgICBleHBvcnRzW2tleV0gPSBJU19HTE9CQUwgJiYgdHlwZW9mIHRhcmdldFtrZXldICE9ICdmdW5jdGlvbicgPyBzb3VyY2Vba2V5XVxuICAgIC8vIGJpbmQgdGltZXJzIHRvIGdsb2JhbCBmb3IgY2FsbCBmcm9tIGV4cG9ydCBjb250ZXh0XG4gICAgOiBJU19CSU5EICYmIG93biA/IGN0eChvdXQsIGdsb2JhbClcbiAgICAvLyB3cmFwIGdsb2JhbCBjb25zdHJ1Y3RvcnMgZm9yIHByZXZlbnQgY2hhbmdlIHRoZW0gaW4gbGlicmFyeVxuICAgIDogSVNfV1JBUCAmJiB0YXJnZXRba2V5XSA9PSBvdXQgPyAoZnVuY3Rpb24oQyl7XG4gICAgICB2YXIgRiA9IGZ1bmN0aW9uKGEsIGIsIGMpe1xuICAgICAgICBpZih0aGlzIGluc3RhbmNlb2YgQyl7XG4gICAgICAgICAgc3dpdGNoKGFyZ3VtZW50cy5sZW5ndGgpe1xuICAgICAgICAgICAgY2FzZSAwOiByZXR1cm4gbmV3IEM7XG4gICAgICAgICAgICBjYXNlIDE6IHJldHVybiBuZXcgQyhhKTtcbiAgICAgICAgICAgIGNhc2UgMjogcmV0dXJuIG5ldyBDKGEsIGIpO1xuICAgICAgICAgIH0gcmV0dXJuIG5ldyBDKGEsIGIsIGMpO1xuICAgICAgICB9IHJldHVybiBDLmFwcGx5KHRoaXMsIGFyZ3VtZW50cyk7XG4gICAgICB9O1xuICAgICAgRltQUk9UT1RZUEVdID0gQ1tQUk9UT1RZUEVdO1xuICAgICAgcmV0dXJuIEY7XG4gICAgLy8gbWFrZSBzdGF0aWMgdmVyc2lvbnMgZm9yIHByb3RvdHlwZSBtZXRob2RzXG4gICAgfSkob3V0KSA6IElTX1BST1RPICYmIHR5cGVvZiBvdXQgPT0gJ2Z1bmN0aW9uJyA/IGN0eChGdW5jdGlvbi5jYWxsLCBvdXQpIDogb3V0O1xuICAgIC8vIGV4cG9ydCBwcm90byBtZXRob2RzIHRvIGNvcmUuJUNPTlNUUlVDVE9SJS5tZXRob2RzLiVOQU1FJVxuICAgIGlmKElTX1BST1RPKXtcbiAgICAgIChleHBvcnRzLnZpcnR1YWwgfHwgKGV4cG9ydHMudmlydHVhbCA9IHt9KSlba2V5XSA9IG91dDtcbiAgICAgIC8vIGV4cG9ydCBwcm90byBtZXRob2RzIHRvIGNvcmUuJUNPTlNUUlVDVE9SJS5wcm90b3R5cGUuJU5BTUUlXG4gICAgICBpZih0eXBlICYgJGV4cG9ydC5SICYmIGV4cFByb3RvICYmICFleHBQcm90b1trZXldKWhpZGUoZXhwUHJvdG8sIGtleSwgb3V0KTtcbiAgICB9XG4gIH1cbn07XG4vLyB0eXBlIGJpdG1hcFxuJGV4cG9ydC5GID0gMTsgICAvLyBmb3JjZWRcbiRleHBvcnQuRyA9IDI7ICAgLy8gZ2xvYmFsXG4kZXhwb3J0LlMgPSA0OyAgIC8vIHN0YXRpY1xuJGV4cG9ydC5QID0gODsgICAvLyBwcm90b1xuJGV4cG9ydC5CID0gMTY7ICAvLyBiaW5kXG4kZXhwb3J0LlcgPSAzMjsgIC8vIHdyYXBcbiRleHBvcnQuVSA9IDY0OyAgLy8gc2FmZVxuJGV4cG9ydC5SID0gMTI4OyAvLyByZWFsIHByb3RvIG1ldGhvZCBmb3IgYGxpYnJhcnlgIFxubW9kdWxlLmV4cG9ydHMgPSAkZXhwb3J0O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZXhwb3J0LmpzXG4gKiogbW9kdWxlIGlkID0gOFxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwiLy8gaHR0cHM6Ly9naXRodWIuY29tL3psb2lyb2NrL2NvcmUtanMvaXNzdWVzLzg2I2lzc3VlY29tbWVudC0xMTU3NTkwMjhcbnZhciBnbG9iYWwgPSBtb2R1bGUuZXhwb3J0cyA9IHR5cGVvZiB3aW5kb3cgIT0gJ3VuZGVmaW5lZCcgJiYgd2luZG93Lk1hdGggPT0gTWF0aFxuICA/IHdpbmRvdyA6IHR5cGVvZiBzZWxmICE9ICd1bmRlZmluZWQnICYmIHNlbGYuTWF0aCA9PSBNYXRoID8gc2VsZiA6IEZ1bmN0aW9uKCdyZXR1cm4gdGhpcycpKCk7XG5pZih0eXBlb2YgX19nID09ICdudW1iZXInKV9fZyA9IGdsb2JhbDsgLy8gZXNsaW50LWRpc2FibGUtbGluZSBuby11bmRlZlxuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZ2xvYmFsLmpzXG4gKiogbW9kdWxlIGlkID0gOVxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwidmFyIGNvcmUgPSBtb2R1bGUuZXhwb3J0cyA9IHt2ZXJzaW9uOiAnMi4yLjAnfTtcbmlmKHR5cGVvZiBfX2UgPT0gJ251bWJlcicpX19lID0gY29yZTsgLy8gZXNsaW50LWRpc2FibGUtbGluZSBuby11bmRlZlxuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fY29yZS5qc1xuICoqIG1vZHVsZSBpZCA9IDEwXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCIvLyBvcHRpb25hbCAvIHNpbXBsZSBjb250ZXh0IGJpbmRpbmdcbnZhciBhRnVuY3Rpb24gPSByZXF1aXJlKCcuL19hLWZ1bmN0aW9uJyk7XG5tb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGZuLCB0aGF0LCBsZW5ndGgpe1xuICBhRnVuY3Rpb24oZm4pO1xuICBpZih0aGF0ID09PSB1bmRlZmluZWQpcmV0dXJuIGZuO1xuICBzd2l0Y2gobGVuZ3RoKXtcbiAgICBjYXNlIDE6IHJldHVybiBmdW5jdGlvbihhKXtcbiAgICAgIHJldHVybiBmbi5jYWxsKHRoYXQsIGEpO1xuICAgIH07XG4gICAgY2FzZSAyOiByZXR1cm4gZnVuY3Rpb24oYSwgYil7XG4gICAgICByZXR1cm4gZm4uY2FsbCh0aGF0LCBhLCBiKTtcbiAgICB9O1xuICAgIGNhc2UgMzogcmV0dXJuIGZ1bmN0aW9uKGEsIGIsIGMpe1xuICAgICAgcmV0dXJuIGZuLmNhbGwodGhhdCwgYSwgYiwgYyk7XG4gICAgfTtcbiAgfVxuICByZXR1cm4gZnVuY3Rpb24oLyogLi4uYXJncyAqLyl7XG4gICAgcmV0dXJuIGZuLmFwcGx5KHRoYXQsIGFyZ3VtZW50cyk7XG4gIH07XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fY3R4LmpzXG4gKiogbW9kdWxlIGlkID0gMTFcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsIm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oaXQpe1xuICBpZih0eXBlb2YgaXQgIT0gJ2Z1bmN0aW9uJyl0aHJvdyBUeXBlRXJyb3IoaXQgKyAnIGlzIG5vdCBhIGZ1bmN0aW9uIScpO1xuICByZXR1cm4gaXQ7XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fYS1mdW5jdGlvbi5qc1xuICoqIG1vZHVsZSBpZCA9IDEyXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJ2YXIgZFAgICAgICAgICA9IHJlcXVpcmUoJy4vX29iamVjdC1kcCcpXG4gICwgY3JlYXRlRGVzYyA9IHJlcXVpcmUoJy4vX3Byb3BlcnR5LWRlc2MnKTtcbm1vZHVsZS5leHBvcnRzID0gcmVxdWlyZSgnLi9fZGVzY3JpcHRvcnMnKSA/IGZ1bmN0aW9uKG9iamVjdCwga2V5LCB2YWx1ZSl7XG4gIHJldHVybiBkUC5mKG9iamVjdCwga2V5LCBjcmVhdGVEZXNjKDEsIHZhbHVlKSk7XG59IDogZnVuY3Rpb24ob2JqZWN0LCBrZXksIHZhbHVlKXtcbiAgb2JqZWN0W2tleV0gPSB2YWx1ZTtcbiAgcmV0dXJuIG9iamVjdDtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19oaWRlLmpzXG4gKiogbW9kdWxlIGlkID0gMTNcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsInZhciBhbk9iamVjdCAgICAgICA9IHJlcXVpcmUoJy4vX2FuLW9iamVjdCcpXG4gICwgSUU4X0RPTV9ERUZJTkUgPSByZXF1aXJlKCcuL19pZTgtZG9tLWRlZmluZScpXG4gICwgdG9QcmltaXRpdmUgICAgPSByZXF1aXJlKCcuL190by1wcmltaXRpdmUnKVxuICAsIGRQICAgICAgICAgICAgID0gT2JqZWN0LmRlZmluZVByb3BlcnR5O1xuXG5leHBvcnRzLmYgPSByZXF1aXJlKCcuL19kZXNjcmlwdG9ycycpID8gT2JqZWN0LmRlZmluZVByb3BlcnR5IDogZnVuY3Rpb24gZGVmaW5lUHJvcGVydHkoTywgUCwgQXR0cmlidXRlcyl7XG4gIGFuT2JqZWN0KE8pO1xuICBQID0gdG9QcmltaXRpdmUoUCwgdHJ1ZSk7XG4gIGFuT2JqZWN0KEF0dHJpYnV0ZXMpO1xuICBpZihJRThfRE9NX0RFRklORSl0cnkge1xuICAgIHJldHVybiBkUChPLCBQLCBBdHRyaWJ1dGVzKTtcbiAgfSBjYXRjaChlKXsgLyogZW1wdHkgKi8gfVxuICBpZignZ2V0JyBpbiBBdHRyaWJ1dGVzIHx8ICdzZXQnIGluIEF0dHJpYnV0ZXMpdGhyb3cgVHlwZUVycm9yKCdBY2Nlc3NvcnMgbm90IHN1cHBvcnRlZCEnKTtcbiAgaWYoJ3ZhbHVlJyBpbiBBdHRyaWJ1dGVzKU9bUF0gPSBBdHRyaWJ1dGVzLnZhbHVlO1xuICByZXR1cm4gTztcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19vYmplY3QtZHAuanNcbiAqKiBtb2R1bGUgaWQgPSAxNFxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwidmFyIGlzT2JqZWN0ID0gcmVxdWlyZSgnLi9faXMtb2JqZWN0Jyk7XG5tb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGl0KXtcbiAgaWYoIWlzT2JqZWN0KGl0KSl0aHJvdyBUeXBlRXJyb3IoaXQgKyAnIGlzIG5vdCBhbiBvYmplY3QhJyk7XG4gIHJldHVybiBpdDtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19hbi1vYmplY3QuanNcbiAqKiBtb2R1bGUgaWQgPSAxNVxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwibW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihpdCl7XG4gIHJldHVybiB0eXBlb2YgaXQgPT09ICdvYmplY3QnID8gaXQgIT09IG51bGwgOiB0eXBlb2YgaXQgPT09ICdmdW5jdGlvbic7XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9faXMtb2JqZWN0LmpzXG4gKiogbW9kdWxlIGlkID0gMTZcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsIm1vZHVsZS5leHBvcnRzID0gIXJlcXVpcmUoJy4vX2Rlc2NyaXB0b3JzJykgJiYgIXJlcXVpcmUoJy4vX2ZhaWxzJykoZnVuY3Rpb24oKXtcclxuICByZXR1cm4gT2JqZWN0LmRlZmluZVByb3BlcnR5KHJlcXVpcmUoJy4vX2RvbS1jcmVhdGUnKSgnZGl2JyksICdhJywge2dldDogZnVuY3Rpb24oKXsgcmV0dXJuIDc7IH19KS5hICE9IDc7XHJcbn0pO1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9faWU4LWRvbS1kZWZpbmUuanNcbiAqKiBtb2R1bGUgaWQgPSAxN1xuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwiLy8gVGhhbmsncyBJRTggZm9yIGhpcyBmdW5ueSBkZWZpbmVQcm9wZXJ0eVxubW9kdWxlLmV4cG9ydHMgPSAhcmVxdWlyZSgnLi9fZmFpbHMnKShmdW5jdGlvbigpe1xuICByZXR1cm4gT2JqZWN0LmRlZmluZVByb3BlcnR5KHt9LCAnYScsIHtnZXQ6IGZ1bmN0aW9uKCl7IHJldHVybiA3OyB9fSkuYSAhPSA3O1xufSk7XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19kZXNjcmlwdG9ycy5qc1xuICoqIG1vZHVsZSBpZCA9IDE4XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJtb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGV4ZWMpe1xuICB0cnkge1xuICAgIHJldHVybiAhIWV4ZWMoKTtcbiAgfSBjYXRjaChlKXtcbiAgICByZXR1cm4gdHJ1ZTtcbiAgfVxufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2ZhaWxzLmpzXG4gKiogbW9kdWxlIGlkID0gMTlcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsInZhciBpc09iamVjdCA9IHJlcXVpcmUoJy4vX2lzLW9iamVjdCcpXG4gICwgZG9jdW1lbnQgPSByZXF1aXJlKCcuL19nbG9iYWwnKS5kb2N1bWVudFxuICAvLyBpbiBvbGQgSUUgdHlwZW9mIGRvY3VtZW50LmNyZWF0ZUVsZW1lbnQgaXMgJ29iamVjdCdcbiAgLCBpcyA9IGlzT2JqZWN0KGRvY3VtZW50KSAmJiBpc09iamVjdChkb2N1bWVudC5jcmVhdGVFbGVtZW50KTtcbm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oaXQpe1xuICByZXR1cm4gaXMgPyBkb2N1bWVudC5jcmVhdGVFbGVtZW50KGl0KSA6IHt9O1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2RvbS1jcmVhdGUuanNcbiAqKiBtb2R1bGUgaWQgPSAyMFxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwiLy8gNy4xLjEgVG9QcmltaXRpdmUoaW5wdXQgWywgUHJlZmVycmVkVHlwZV0pXG52YXIgaXNPYmplY3QgPSByZXF1aXJlKCcuL19pcy1vYmplY3QnKTtcbi8vIGluc3RlYWQgb2YgdGhlIEVTNiBzcGVjIHZlcnNpb24sIHdlIGRpZG4ndCBpbXBsZW1lbnQgQEB0b1ByaW1pdGl2ZSBjYXNlXG4vLyBhbmQgdGhlIHNlY29uZCBhcmd1bWVudCAtIGZsYWcgLSBwcmVmZXJyZWQgdHlwZSBpcyBhIHN0cmluZ1xubW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihpdCwgUyl7XG4gIGlmKCFpc09iamVjdChpdCkpcmV0dXJuIGl0O1xuICB2YXIgZm4sIHZhbDtcbiAgaWYoUyAmJiB0eXBlb2YgKGZuID0gaXQudG9TdHJpbmcpID09ICdmdW5jdGlvbicgJiYgIWlzT2JqZWN0KHZhbCA9IGZuLmNhbGwoaXQpKSlyZXR1cm4gdmFsO1xuICBpZih0eXBlb2YgKGZuID0gaXQudmFsdWVPZikgPT0gJ2Z1bmN0aW9uJyAmJiAhaXNPYmplY3QodmFsID0gZm4uY2FsbChpdCkpKXJldHVybiB2YWw7XG4gIGlmKCFTICYmIHR5cGVvZiAoZm4gPSBpdC50b1N0cmluZykgPT0gJ2Z1bmN0aW9uJyAmJiAhaXNPYmplY3QodmFsID0gZm4uY2FsbChpdCkpKXJldHVybiB2YWw7XG4gIHRocm93IFR5cGVFcnJvcihcIkNhbid0IGNvbnZlcnQgb2JqZWN0IHRvIHByaW1pdGl2ZSB2YWx1ZVwiKTtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL190by1wcmltaXRpdmUuanNcbiAqKiBtb2R1bGUgaWQgPSAyMVxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwibW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihiaXRtYXAsIHZhbHVlKXtcbiAgcmV0dXJuIHtcbiAgICBlbnVtZXJhYmxlICA6ICEoYml0bWFwICYgMSksXG4gICAgY29uZmlndXJhYmxlOiAhKGJpdG1hcCAmIDIpLFxuICAgIHdyaXRhYmxlICAgIDogIShiaXRtYXAgJiA0KSxcbiAgICB2YWx1ZSAgICAgICA6IHZhbHVlXG4gIH07XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fcHJvcGVydHktZGVzYy5qc1xuICoqIG1vZHVsZSBpZCA9IDIyXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCIndXNlIHN0cmljdCc7XG5cbmltcG9ydCBDYXJkRm9ybSBmcm9tICcuL3BheW1lbnQtd2lkZ2V0cy9fY2FyZC1mb3JtJztcbmltcG9ydCBGZWVGb3JtIGZyb20gJy4vcGF5bWVudC13aWRnZXRzL19mZWUtZm9ybSc7XG5pbXBvcnQgU3VwcG9ydFRhYmxlIGZyb20gJy4vcGF5bWVudC13aWRnZXRzL19zdXBwb3J0ZXJzLXRhYmxlJztcblxuXG4kKGZ1bmN0aW9uKCl7XG4gICAgRmVlRm9ybS5wbHVnaW4oJy5qcy1mZWUtZm9ybScpO1xuICAgIENhcmRGb3JtLnBsdWdpbignLmpzLWNhcmQtZm9ybScpO1xuICAgIFN1cHBvcnRUYWJsZS5wbHVnaW4oJy5qcy1zdXBwb3J0LXRhYmxlJylcbn0pXG5cblxuLyoqIFdFQlBBQ0sgRk9PVEVSICoqXG4gKiogLi9mcm9udGVuZC9qcy9wYXltZW50L21lbWJlcnMtcGF5bWVudC1wYWdlLmpzXG4gKiovIiwiJ3VzZSBzdHJpY3QnO1xuXG4vKipcbiAqIEZvcm0gZm9yIHVwZGF0aW5nIGNhcmQgaW5mb3JtYXRpb25cbiAqIEBwYXJhbSBzZWxlY3RvclxuICogQGNvbnN0cnVjdG9yXG4gKi9cbmV4cG9ydCBkZWZhdWx0IGNsYXNzIFdpZGdldCB7XG4gICAgY29uc3RydWN0b3Ioc2VsZWN0b3IpIHtcbiAgICAgICAgY29uc3Qgc2VsZiA9IHRoaXM7XG5cbiAgICAgICAgc2VsZi4kcm9vdCA9ICQoc2VsZWN0b3IpO1xuICAgICAgICBzZWxmLmxvY2FscyA9IHNlbGYuX2dldERvbSgpO1xuICAgICAgICBzZWxmLmFwaWtleSA9IHNlbGYuJHJvb3QuZGF0YSgnYXBpa2V5Jyk7XG5cbiAgICAgICAgc2VsZi5fZ2V0U3RyaXBlU2NyaXB0KClcbiAgICAgICAgICAgIC5kb25lKGZ1bmN0aW9uKCl7XG4gICAgICAgICAgICAgICAgU3RyaXBlLnNldFB1Ymxpc2hhYmxlS2V5KHNlbGYuYXBpa2V5KTtcbiAgICAgICAgICAgICAgICBzZWxmLl9pbml0KCk7XG4gICAgICAgICAgICAgICAgc2VsZi5fYXNzaWduRXZlbnRzKCk7XG4gICAgICAgICAgICB9KVxuICAgIH1cblxuICAgIF9nZXRTdHJpcGVTY3JpcHQoKXtcbiAgICAgICAgcmV0dXJuICQuYWpheCh7XG4gICAgICAgICAgICB1cmw6ICdodHRwczovL2pzLnN0cmlwZS5jb20vdjIvJyxcbiAgICAgICAgICAgIGRhdGFUeXBlOiBcInNjcmlwdFwiXG4gICAgICAgIH0pO1xuICAgIH07XG5cbiAgICBfZ2V0RG9tKCl7XG4gICAgICAgIGNvbnN0ICRyb290ID0gdGhpcy4kcm9vdDtcblxuICAgICAgICByZXR1cm4ge1xuICAgICAgICAgICAgJGlucHV0TnVtYmVyOiAkcm9vdC5maW5kKCdbZGF0YS1jYXJkLW51bWJlcl0nKSxcbiAgICAgICAgICAgICRpbnB1dE5hbWU6ICRyb290LmZpbmQoJ1tkYXRhLWNhcmQtbmFtZV0nKSxcbiAgICAgICAgICAgICRpbnB1dE1vbnRoOiAkcm9vdC5maW5kKCdbZGF0YS1jYXJkLW1vbnRoXScpLFxuICAgICAgICAgICAgJGlucHV0WWVhcjogJHJvb3QuZmluZCgnW2RhdGEtY2FyZC15ZWFyXScpLFxuICAgICAgICAgICAgJGlucHV0Q1ZDOiAkcm9vdC5maW5kKCdbZGF0YS1jYXJkLWN2Y10nKSxcbiAgICAgICAgICAgICRzdWJtaXQ6ICRyb290LmZpbmQoJ1tkYXRhLWNhcmQtc3VibWl0XScpXG4gICAgICAgIH1cbiAgICB9O1xuXG4gICAgX2luaXQoKXtcbiAgICAgICAgaWYgKCEkLmZuLnBheW1lbnQpe1xuICAgICAgICAgICAgY29uc29sZS5sb2coJ1RoZXJlIGlzIG5vIHBheW1lbnQgcGx1Z2luIG9uIHRoaXMgcGFnZScpO1xuICAgICAgICAgICAgcmV0dXJuO1xuICAgICAgICB9XG5cbiAgICAgICAgdGhpcy5sb2NhbHMuJGlucHV0TnVtYmVyLnBheW1lbnQoJ2Zvcm1hdENhcmROdW1iZXInKTtcbiAgICAgICAgdGhpcy5sb2NhbHMuJGlucHV0Q1ZDLnBheW1lbnQoJ2Zvcm1hdENhcmRDVkMnKTtcblxuICAgIH07XG5cbiAgICBfYXNzaWduRXZlbnRzKCl7XG4gICAgICAgIGNvbnN0IHNlbGYgPSB0aGlzO1xuXG4gICAgICAgIHNlbGYuJHJvb3RcbiAgICAgICAgICAgIC5vbigna2V5dXAnLCAnaW5wdXQnLCBmdW5jdGlvbigpe1xuICAgICAgICAgICAgICAgIHNlbGYuX3JlbW92ZUVycm9yKCQodGhpcykpO1xuICAgICAgICAgICAgfSlcbiAgICAgICAgICAgIC5vbignY2hhbmdlIHBhc3RlIGtleXVwJywgJ1tkYXRhLWNhcmQtbmFtZV0nLCBmdW5jdGlvbihlKXtcbiAgICAgICAgICAgICAgICB2YXIgJHRoaXMgPSAkKHRoaXMpO1xuICAgICAgICAgICAgICAgICR0aGlzLnZhbCgkdGhpcy52YWwoKS50b1VwcGVyQ2FzZSgpKVxuICAgICAgICAgICAgfSlcbiAgICAgICAgICAgIC5vbignc3VibWl0Jywgc2VsZi5fb25TdWJtaXRIYW5kbGVyLmJpbmQoc2VsZikpO1xuICAgIH07XG5cbiAgICBfc2V0RXJyb3IoJGVsKXtcbiAgICAgICAgJGVsLnBhcmVudCgpLmFkZENsYXNzKCdoYXMtZXJyb3InKTtcbiAgICB9O1xuXG4gICAgX3JlbW92ZUVycm9yKCRlbCl7XG4gICAgICAgICRlbC5wYXJlbnQoKS5yZW1vdmVDbGFzcygnaGFzLWVycm9yJyk7XG4gICAgfTtcblxuICAgIF9kaXNhYmxlZEZvcm0oKXtcbiAgICAgICAgdGhpcy5sb2NhbHMuJHN1Ym1pdC5wcm9wKCdkaXNhYmxlZCcsIHRydWUpO1xuICAgICAgICAkKFwiYm9keVwiKS5jc3MoXCJjdXJzb3JcIiwgXCJwcm9ncmVzc1wiKTtcbiAgICB9O1xuXG4gICAgX2VuYWJsZWRGb3JtKCl7XG4gICAgICAgIHRoaXMubG9jYWxzLiRzdWJtaXQucHJvcCgnZGlzYWJsZWQnLCBmYWxzZSk7XG4gICAgICAgICQoXCJib2R5XCIpLmNzcyhcImN1cnNvclwiLCBcImRlZmF1bHRcIik7XG4gICAgfTtcblxuICAgIF9hZGRUb2tlbklucHV0KHRva2VuKXtcbiAgICAgICAgbGV0ICRyb290ID0gdGhpcy4kcm9vdCxcbiAgICAgICAgICAgIHRlbXBsYXRlID0gJzxpbnB1dCB0eXBlPVwiaGlkZGVuXCIgdmFsdWU9XCInICsgIHRva2VuICsnXCIgbmFtZT1cInRva2VuXCIgLz4nO1xuXG4gICAgICAgICRyb290LmZpbmQoJ2lucHV0W25hbWU9XCJ0b2tlblwiXScpLnJlbW92ZSgpXG4gICAgICAgICRyb290LmFwcGVuZCh0ZW1wbGF0ZSk7XG4gICAgfTtcblxuICAgIF9vblN1Ym1pdEhhbmRsZXIoZSl7XG4gICAgICAgIHZhciBzZWxmID0gdGhpcztcbiAgICAgICAgZS5wcmV2ZW50RGVmYXVsdCgpO1xuXG4gICAgICAgIGlmICghdGhpcy5pc1ZhbGlkRm9ybSgpKSByZXR1cm47XG4gICAgICAgIHRoaXMuX2Rpc2FibGVkRm9ybSgpO1xuXG4gICAgICAgIFN0cmlwZS5jYXJkLmNyZWF0ZVRva2VuKHNlbGYuJHJvb3QsIHNlbGYuX3N0cmlwZUhhbmRsZXIuYmluZChzZWxmKSk7XG4gICAgfTtcblxuICAgIF9zdHJpcGVIYW5kbGVyKHN0YXR1cywgcmVzcG9uc2UpIHtcbiAgICAgICAgY29uc3Qgc2VsZiA9IHRoaXM7XG4gICAgICAgIGxldCBkYXRhLCBlcnJvck1zZztcblxuICAgICAgICBpZiAocmVzcG9uc2UuZXJyb3IpIHtcbiAgICAgICAgICAgIHNlbGYuX2VuYWJsZWRGb3JtKCk7XG4gICAgICAgIH0gZWxzZSB7XG4gICAgICAgICAgICBzZWxmLl9hZGRUb2tlbklucHV0KHJlc3BvbnNlLmlkKTtcblxuICAgICAgICAgICAgZGF0YSA9IHRoaXMuJHJvb3Quc2VyaWFsaXplKCk7XG4gICAgICAgICAgICBzZWxmLl9zZW5kRm9ybURhdGEoZGF0YSlcbiAgICAgICAgICAgICAgICAuZG9uZShmdW5jdGlvbiAoZGF0YSkge1xuICAgICAgICAgICAgICAgICAgICBpZiAoZGF0YS5oYXNPd25Qcm9wZXJ0eShcInJlZGlyZWN0XCIpKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICB3aW5kb3cubG9jYXRpb24gPSBkYXRhLnJlZGlyZWN0O1xuICAgICAgICAgICAgICAgICAgICB9IGVsc2UgaWYgKGRhdGEuaGFzT3duUHJvcGVydHkoXCJtZXNzYWdlXCIpKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICBzdWNjZXNzKGRhdGEubWVzc2FnZSk7XG4gICAgICAgICAgICAgICAgICAgIH0gZWxzZSB7XG4gICAgICAgICAgICAgICAgICAgICAgICB2YXIgbXNnID0gXCJJbnRlcm5hbCBlcnJvciAjMjAwMS4gWW91ciBjYXJkIGhhcyBiZWVuIGNoYXJnZWQuIFwiO1xuICAgICAgICAgICAgICAgICAgICAgICAgbXNnICs9IFwiRG8gbm90IG1ha2UgcGF5bWVudCBhZ2Fpbi4gUGxlYXNlIHByb2NlZWQgdG8geW91ciBwcm9maWxlIGRpcmVjdGx5LlwiO1xuICAgICAgICAgICAgICAgICAgICAgICAgZXJyb3IobXNnKTtcbiAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIH0pXG4gICAgICAgICAgICAgICAgLmZhaWwoZnVuY3Rpb24gKGpxWEhSLCBzdGF0dXMpIHtcbiAgICAgICAgICAgICAgICAgICAgaWYgKHN0YXR1cyA9PSBcImVycm9yXCIpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgIGVycm9yTXNnID0gSlNPTi5wYXJzZShqcVhIUi5yZXNwb25zZVRleHQpO1xuICAgICAgICAgICAgICAgICAgICAgICAgZXJyb3IoZXJyb3JNc2cubWVzc2FnZSk7XG4gICAgICAgICAgICAgICAgICAgIH0gZWxzZSB7XG4gICAgICAgICAgICAgICAgICAgICAgICBlcnJvcihcIkludGVybmFsIGVycm9yICMyMDAwLiBZb3VyIGNhcmQgaGFzIG5vdCBiZWVuIGNoYXJnZWQuIFBsZWFzZSB0cnkgYWdhaW4uXCIpO1xuICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgfSlcbiAgICAgICAgICAgICAgICAuY29tcGxldGUoZnVuY3Rpb24gKCkge1xuICAgICAgICAgICAgICAgICAgICBzZWxmLl9lbmFibGVkRm9ybSgpO1xuICAgICAgICAgICAgICAgIH0pO1xuICAgICAgICB9XG4gICAgfTtcblxuICAgIF9zZW5kRm9ybURhdGEoZGF0YUZvcm0pe1xuICAgICAgICByZXR1cm4gJC5hamF4KHtcbiAgICAgICAgICAgIHR5cGU6IFwiUE9TVFwiLFxuICAgICAgICAgICAgdXJsOiB0aGlzLiRyb290LmF0dHIoXCJhY3Rpb25cIiksXG4gICAgICAgICAgICBkYXRhOiBkYXRhRm9ybSxcbiAgICAgICAgICAgIGRhdGFUeXBlOiBcImpzb25cIlxuICAgICAgICB9KVxuICAgIH07XG5cbiAgICBpc1ZhbGlkRm9ybSgpe1xuICAgICAgICBsZXQgc2VsZiA9IHRoaXMsXG4gICAgICAgICAgICB2YWxpZCA9IHRydWUsXG4gICAgICAgICAgICBsb2NhbHMgPSB0aGlzLmxvY2FscyxcbiAgICAgICAgICAgIGlzVmFsaWROdW1iZXIgPSAkLnBheW1lbnQudmFsaWRhdGVDYXJkTnVtYmVyKGxvY2Fscy4kaW5wdXROdW1iZXIudmFsKCkpLFxuICAgICAgICAgICAgaXNWYWxpZEV4cGlyeSA9ICQucGF5bWVudC52YWxpZGF0ZUNhcmRFeHBpcnkobG9jYWxzLiRpbnB1dE1vbnRoLnZhbCgpLCBsb2NhbHMuJGlucHV0WWVhci52YWwoKSksXG4gICAgICAgICAgICBpc1ZhbGlkQ1ZDID0gJC5wYXltZW50LnZhbGlkYXRlQ2FyZENWQyhsb2NhbHMuJGlucHV0Q1ZDLnZhbCgpKSxcbiAgICAgICAgICAgIGlzVmFsaWROYW1lID0gK2xvY2Fscy4kaW5wdXROYW1lLnZhbCgpLmxlbmd0aDtcblxuICAgICAgICBpZiAoIWlzVmFsaWROdW1iZXIpe1xuICAgICAgICAgICAgc2VsZi5fc2V0RXJyb3IobG9jYWxzLiRpbnB1dE51bWJlcik7XG4gICAgICAgICAgICB2YWxpZCA9IGZhbHNlO1xuICAgICAgICB9XG5cbiAgICAgICAgaWYgKCFpc1ZhbGlkRXhwaXJ5KXtcbiAgICAgICAgICAgIHNlbGYuX3NldEVycm9yKGxvY2Fscy4kaW5wdXRNb250aCk7XG4gICAgICAgICAgICBzZWxmLl9zZXRFcnJvcihsb2NhbHMuJGlucHV0WWVhcik7XG4gICAgICAgICAgICB2YWxpZCA9IGZhbHNlO1xuICAgICAgICB9XG5cbiAgICAgICAgaWYgKCFpc1ZhbGlkQ1ZDKXtcbiAgICAgICAgICAgIHNlbGYuX3NldEVycm9yKGxvY2Fscy4kaW5wdXRDVkMpO1xuICAgICAgICAgICAgdmFsaWQgPSBmYWxzZTtcbiAgICAgICAgfVxuXG4gICAgICAgIGlmICghaXNWYWxpZE5hbWUpe1xuICAgICAgICAgICAgc2VsZi5fc2V0RXJyb3IobG9jYWxzLiRpbnB1dE5hbWUpO1xuICAgICAgICAgICAgdmFsaWQgPSBmYWxzZTtcbiAgICAgICAgfVxuXG4gICAgICAgIHJldHVybiB2YWxpZDtcbiAgICB9XG5cbiAgICAvLyBzdGF0aWNcbiAgICBzdGF0aWMgcGx1Z2luKHNlbGVjdG9yLCBvcHRpb25zKSB7XG4gICAgICAgIGNvbnN0ICRlbGVtcyA9ICQoc2VsZWN0b3IpO1xuICAgICAgICBpZiAoISRlbGVtcy5sZW5ndGgpIHJldHVybjtcblxuICAgICAgICByZXR1cm4gJGVsZW1zLmVhY2goZnVuY3Rpb24gKGluZGV4LCBlbCkge1xuICAgICAgICAgICAgbGV0ICRlbGVtZW50ID0gJChlbCk7XG4gICAgICAgICAgICBsZXQgZGF0YSA9ICRlbGVtZW50LmRhdGEoJ3dpZGdldC5zY3JvbGx0bycpO1xuXG4gICAgICAgICAgICBpZiAoIWRhdGEpIHtcbiAgICAgICAgICAgICAgICBkYXRhID0gbmV3IFdpZGdldChlbCwgb3B0aW9ucyk7XG4gICAgICAgICAgICAgICAgJGVsZW1lbnQuZGF0YSgnd2lkZ2V0JywgZGF0YSk7XG4gICAgICAgICAgICB9XG4gICAgICAgIH0pXG4gICAgfVxufVxuXG5cblxuLyoqIFdFQlBBQ0sgRk9PVEVSICoqXG4gKiogLi9mcm9udGVuZC9qcy9wYXltZW50L3BheW1lbnQtd2lkZ2V0cy9fY2FyZC1mb3JtLmpzXG4gKiovIiwiJ3VzZSBzdHJpY3QnO1xuXG4vKipcbiAqIEZvcm0gZm9yIHVwZGF0aW5nIGNvbnRyaWJ1dGlvbiBsZXZlbFxuICogQHBhcmFtIHNlbGVjdG9yXG4gKiBAY29uc3RydWN0b3JcbiAqL1xuXG5leHBvcnQgZGVmYXVsdCBjbGFzcyBXaWRnZXQge1xuICAgIGNvbnN0cnVjdG9yKHNlbGVjdG9yKSB7XG4gICAgICAgIHRoaXMuJHJvb3QgPSAkKHNlbGVjdG9yKTtcbiAgICAgICAgdGhpcy5sb2NhbHMgPSB0aGlzLl9nZXREb20oKTtcblxuICAgICAgICB0aGlzLl9hc3NpZ25FdmVudHMoKTtcbiAgICAgICAgdGhpcy5fdXBkYXRlQW1vdW50KHRoaXMubG9jYWxzLiRpbnB1dEZlZSk7XG4gICAgfVxuXG4gICAgX2dldERvbSgpIHtcbiAgICAgICAgY29uc3QgJHJvb3QgPSB0aGlzLiRyb290O1xuXG4gICAgICAgIHJldHVybiB7XG4gICAgICAgICAgICAkaW5wdXRGZWU6ICRyb290LmZpbmQoJy5iLWZlZXN0cmlwX19pbnB1dCcpLmZpcnN0KCksXG4gICAgICAgICAgICAkdGF4UGxhY2U6ICRyb290LmZpbmQoJ1tkYXRhLWZlZS10YXhdJyksXG4gICAgICAgICAgICAkYW1vdW50UGxhY2U6ICRyb290LmZpbmQoJ1tkYXRhLWZlZS1hbW91bnRdJyksXG4gICAgICAgICAgICAkcGF5UGxhY2U6ICRyb290LmZpbmQoJ1tkYXRhLWZlZS1wYXldJylcbiAgICAgICAgfVxuICAgIH1cblxuICAgIF9hc3NpZ25FdmVudHMoKSB7XG4gICAgICAgIGNvbnN0IHNlbGYgPSB0aGlzO1xuXG4gICAgICAgIHNlbGYuJHJvb3RcbiAgICAgICAgICAgIC5vbignY2hhbmdlIHBhc3RlIGtleXVwJywgJy5iLWZlZXN0cmlwX19pbnB1dCcsIGZ1bmN0aW9uIChlKSB7XG4gICAgICAgICAgICAgICAgbGV0ICR0aGlzID0gJCh0aGlzKTtcblxuICAgICAgICAgICAgICAgIHNlbGYuX3JlbW92ZUVycm9yKCR0aGlzKTtcbiAgICAgICAgICAgICAgICBzZWxmLl91cGRhdGVBbW91bnQoJHRoaXMpO1xuICAgICAgICAgICAgfSlcbiAgICAgICAgICAgIC5vbignc3VibWl0Jywgc2VsZi5fb25TdWJtaXRGb3JtLmJpbmQoc2VsZikpO1xuICAgIH1cblxuICAgIF9vblN1Ym1pdEZvcm0oZSkge1xuICAgICAgICBjb25zdCBzZWxmID0gdGhpcztcbiAgICAgICAgZS5wcmV2ZW50RGVmYXVsdCgpO1xuXG4gICAgICAgIGlmICghdGhpcy5pc1ZhbGlkRm9ybSgpKSByZXR1cm47XG5cbiAgICAgICAgbGV0IGZlZUFtb3VudCA9IHRoaXMubG9jYWxzLiRpbnB1dEZlZS52YWwoKTtcblxuICAgICAgICB0aGlzLl9zZW5kRmVlRGF0YShmZWVBbW91bnQpXG4gICAgICAgICAgICAuZG9uZShmdW5jdGlvbiAoZGF0YSkge1xuICAgICAgICAgICAgICAgIGlmIChkYXRhLmhhc093blByb3BlcnR5KFwibWVzc2FnZVwiKSkge1xuICAgICAgICAgICAgICAgICAgICBzdWNjZXNzKGRhdGEubWVzc2FnZSk7XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgfSlcbiAgICAgICAgICAgIC5mYWlsKGZ1bmN0aW9uIChkYXRhKSB7XG4gICAgICAgICAgICAgICAgY29uc3QgZXJyb3JNc2cgPSBKU09OLnBhcnNlKGpxWEhSLnJlc3BvbnNlVGV4dCk7XG4gICAgICAgICAgICAgICAgZXJyb3IoZXJyb3JNc2cubWVzc2FnZSk7XG4gICAgICAgICAgICB9KVxuICAgIH1cblxuICAgIF9zZXRFcnJvcigkZWwpIHtcbiAgICAgICAgY29uc3QgJHBhcmVudCA9ICRlbC5wYXJlbnQoKTtcblxuICAgICAgICBpZiAoISRwYXJlbnQuaGFzQ2xhc3MoJ2hhcy1lcnJvcicpKSB7XG4gICAgICAgICAgICAkcGFyZW50LmFkZENsYXNzKCdoYXMtZXJyb3InKTtcbiAgICAgICAgfVxuICAgIH1cblxuICAgIF9yZW1vdmVFcnJvcigkZWwpIHtcbiAgICAgICAgJGVsLnBhcmVudCgpLnJlbW92ZUNsYXNzKCdoYXMtZXJyb3InKTtcbiAgICB9XG5cbiAgICAvKipcbiAgICAgKiBVcGRhdGUgdGF4IGFuZCBwYXkgcHJpY2Ugb24gdGhlIGZyb20sIGJhc2VkIG9uIGlucHV0IHZhbHVlICRlbFxuICAgICAqIEBwYXJhbSB7alF1ZXJ5fSAkZWwgLSAkKGlucHV0KVxuICAgICAqIEBwcml2YXRlXG4gICAgICovXG4gICAgX3VwZGF0ZUFtb3VudCgkZWwpIHtcbiAgICAgICAgbGV0IGxvY2FscyA9IHRoaXMubG9jYWxzLFxuICAgICAgICAgICAgYW1vdW50ID0gJGVsLnZhbCgpIDwgMSA/IDAuMDAgOiBwYXJzZUludCgkZWwudmFsKCkpLFxuICAgICAgICAgICAgdGF4UGVyY2VudCA9IHBhcnNlRmxvYXQoJGVsLmRhdGEoJ3RheCcpKSxcbiAgICAgICAgICAgIHRheCwgYW1vdW50V2l0aFRheDtcblxuICAgICAgICB0YXggPSAoYW1vdW50ICogdGF4UGVyY2VudCkgLyAxMDA7XG4gICAgICAgIGFtb3VudFdpdGhUYXggPSBhbW91bnQgKyB0YXg7XG5cbiAgICAgICAgbG9jYWxzLiRhbW91bnRQbGFjZS50ZXh0KGFtb3VudCk7XG4gICAgICAgIGxvY2Fscy4kdGF4UGxhY2UudGV4dCh0YXgpO1xuICAgICAgICBsb2NhbHMuJHBheVBsYWNlLnRleHQoYW1vdW50V2l0aFRheCk7XG4gICAgfVxuXG4gICAgLyoqXG4gICAgICogQ2hlY2ssIGlzIEZvcm0gdmFsaWQ/XG4gICAgICogQHJldHVybnMge0Jvb2xlYW59XG4gICAgICovXG4gICAgaXNWYWxpZEZvcm0oKSB7XG4gICAgICAgIGxldCB2YWxpZCA9IHRydWUsXG4gICAgICAgICAgICAkaW5wdXRGZWUgPSB0aGlzLmxvY2Fscy4kaW5wdXRGZWUsXG4gICAgICAgICAgICBpc1ZhbGlkQW1vdW50ID0gKCRpbnB1dEZlZS52YWwoKS5sZW5ndGggPiAwKSAmJiAoIWlzTmFOKCRpbnB1dEZlZS52YWwoKSkpO1xuXG4gICAgICAgIGlmICghaXNWYWxpZEFtb3VudCkge1xuICAgICAgICAgICAgdmFsaWQgPSBmYWxzZTtcbiAgICAgICAgICAgIHRoaXMuX3NldEVycm9yKCRpbnB1dEZlZSk7XG4gICAgICAgIH1cblxuICAgICAgICByZXR1cm4gdmFsaWQ7XG4gICAgfVxuXG4gICAgLy90cmFuc3BvcnRcbiAgICAvKipcbiAgICAgKiBAcGFyYW0ge051bWJlcn0gZmVlQW1vdW50XG4gICAgICogQHJldHVybnMgeyQuRGVmZmVyZWR9IC0gcHJvbWlzZVxuICAgICAqIEBwcml2YXRlXG4gICAgICovXG4gICAgX3NlbmRGZWVEYXRhKGZlZUFtb3VudCkge1xuICAgICAgICByZXR1cm4gJC5hamF4KHtcbiAgICAgICAgICAgIHR5cGU6IFwiUE9TVFwiLFxuICAgICAgICAgICAgdXJsOiB0aGlzLiRyb290LmF0dHIoXCJhY3Rpb25cIiksXG4gICAgICAgICAgICBkYXRhOiB7ZmVlOiBmZWVBbW91bnR9LFxuICAgICAgICAgICAgZGF0YVR5cGU6IFwianNvblwiXG4gICAgICAgIH0pXG4gICAgfVxuXG4gICAgLy8gc3RhdGljXG4gICAgc3RhdGljIHBsdWdpbihzZWxlY3Rvciwgb3B0aW9ucykge1xuICAgICAgICBjb25zdCAkZWxlbXMgPSAkKHNlbGVjdG9yKTtcbiAgICAgICAgaWYgKCEkZWxlbXMubGVuZ3RoKSByZXR1cm47XG5cbiAgICAgICAgcmV0dXJuICRlbGVtcy5lYWNoKGZ1bmN0aW9uIChpbmRleCwgZWwpIHtcbiAgICAgICAgICAgIGxldCAkZWxlbWVudCA9ICQoZWwpO1xuICAgICAgICAgICAgbGV0IGRhdGEgPSAkZWxlbWVudC5kYXRhKCd3aWRnZXQuc2Nyb2xsdG8nKTtcblxuICAgICAgICAgICAgaWYgKCFkYXRhKSB7XG4gICAgICAgICAgICAgICAgZGF0YSA9IG5ldyBXaWRnZXQoZWwsIG9wdGlvbnMpO1xuICAgICAgICAgICAgICAgICRlbGVtZW50LmRhdGEoJ3dpZGdldCcsIGRhdGEpO1xuICAgICAgICAgICAgfVxuICAgICAgICB9KVxuICAgIH1cbn1cblxuXG5cblxuXG5cblxuXG5cbi8qKiBXRUJQQUNLIEZPT1RFUiAqKlxuICoqIC4vZnJvbnRlbmQvanMvcGF5bWVudC9wYXltZW50LXdpZGdldHMvX2ZlZS1mb3JtLmpzXG4gKiovIiwiJ3VzZSBzdHJpY3QnO1xuXG4vKipcbiAqIEBwYXJhbSBzZWxlY3RvclxuICogQGNvbnN0cnVjdG9yXG4gKi9cbmV4cG9ydCBkZWZhdWx0IGNsYXNzIFdpZGdldCB7XG4gICAgY29uc3RydWN0b3Ioc2VsZWN0b3IpIHtcbiAgICAgICAgdGhpcy4kcm9vdCA9ICQoc2VsZWN0b3IpO1xuICAgICAgICB0aGlzLl9hc3NpZ25FdmVudHMoKTtcbiAgICB9XG5cbiAgICBfYXNzaWduRXZlbnRzKCkge1xuICAgICAgICBjb25zdCBzZWxmID0gdGhpcztcblxuICAgICAgICBzZWxmLiRyb290Lm9uKCdjbGljaycsICcuZGxnLWhtZmVlc19fbGluayAnLCBmdW5jdGlvbihlKXtcbiAgICAgICAgICAgIHZhciAkdGhpcyA9ICQodGhpcyk7XG4gICAgICAgICAgICBlLnByZXZlbnREZWZhdWx0KCk7XG5cbiAgICAgICAgICAgIGlmICgkdGhpcy5oYXNDbGFzcygnc3RhdGVfYWN0aXZlJykpIHJldHVybjtcblxuICAgICAgICAgICAgc2VsZi5fc3dpdGNoVGFiKCR0aGlzKTtcbiAgICAgICAgfSk7XG4gICAgfTtcblxuICAgIF9zd2l0Y2hUYWIoJGxpbmspe1xuICAgICAgICBjb25zdCAkdGFyZ2V0ID0gdGhpcy5fZ2V0VGFyZ2V0KCRsaW5rKTtcblxuICAgICAgICBpZiAoISR0YXJnZXQubGVuZ3RoKSByZXR1cm47XG5cbiAgICAgICAgJHRhcmdldC5zaG93KClcbiAgICAgICAgICAgIC5zaWJsaW5ncygnLnRhYmxlJykuaGlkZSgpO1xuICAgICAgICAkbGluay5hZGRDbGFzcygnc3RhdGVfYWN0aXZlJylcbiAgICAgICAgICAgIC5zaWJsaW5ncygnLmRsZy1obWZlZXNfX2xpbmsnKS5yZW1vdmVDbGFzcygnc3RhdGVfYWN0aXZlJyk7XG4gICAgfVxuXG4gICAgX2dldFRhcmdldCgkZWwpe1xuICAgICAgICByZXR1cm4gdGhpcy4kcm9vdC5maW5kKCRlbC5hdHRyKCdocmVmJykpO1xuICAgIH1cblxuICAgIC8vIHN0YXRpY1xuICAgIHN0YXRpYyBwbHVnaW4oc2VsZWN0b3IsIG9wdGlvbnMpIHtcbiAgICAgICAgY29uc3QgJGVsZW1zID0gJChzZWxlY3Rvcik7XG4gICAgICAgIGlmICghJGVsZW1zLmxlbmd0aCkgcmV0dXJuO1xuXG4gICAgICAgIHJldHVybiAkZWxlbXMuZWFjaChmdW5jdGlvbiAoaW5kZXgsIGVsKSB7XG4gICAgICAgICAgICBsZXQgJGVsZW1lbnQgPSAkKGVsKTtcbiAgICAgICAgICAgIGxldCBkYXRhID0gJGVsZW1lbnQuZGF0YSgnd2lkZ2V0LnNjcm9sbHRvJyk7XG5cbiAgICAgICAgICAgIGlmICghZGF0YSkge1xuICAgICAgICAgICAgICAgIGRhdGEgPSBuZXcgV2lkZ2V0KGVsLCBvcHRpb25zKTtcbiAgICAgICAgICAgICAgICAkZWxlbWVudC5kYXRhKCd3aWRnZXQnLCBkYXRhKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgfSlcbiAgICB9XG59XG5cblxuXG4vKiogV0VCUEFDSyBGT09URVIgKipcbiAqKiAuL2Zyb250ZW5kL2pzL3BheW1lbnQvcGF5bWVudC13aWRnZXRzL19zdXBwb3J0ZXJzLXRhYmxlLmpzXG4gKiovIl0sInNvdXJjZVJvb3QiOiIifQ==