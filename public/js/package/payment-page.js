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

	module.exports = __webpack_require__(60);


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
/* 54 */,
/* 55 */,
/* 56 */,
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

/***/ },
/* 58 */,
/* 59 */,
/* 60 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';
	
	var _paymentForm = __webpack_require__(61);
	
	var _paymentForm2 = _interopRequireDefault(_paymentForm);
	
	var _supportersTable = __webpack_require__(57);
	
	var _supportersTable2 = _interopRequireDefault(_supportersTable);
	
	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }
	
	$(function () {
	    _paymentForm2.default.plugin('.js-payment-form');
	    _supportersTable2.default.plugin('.js-support-table');
	});

/***/ },
/* 61 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';
	
	/**
	 * Form for payment with fee
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
	
	            self._updateAmount(self.locals.$inputFee);
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
	                $submit: $root.find('[data-payment-submit]'),
	                $inputFee: $root.find('[data-payment-fee]'),
	                $taxPlace: $root.find('[data-payment-tax]'),
	                $amountPlace: $root.find('[data-payment-amount]'),
	                $payPlace: $root.find('[data-payment-price]')
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
	
	            self.$root.on('change paste keyup', '[data-payment-fee]', function (e) {
	                var $this = $(this);
	
	                self._removeError($this);
	                self._updateAmount($this);
	            }).on('keyup', 'input', function () {
	                self._removeError($(this));
	            }).on('change paste keyup', '[data-card-name]', function (e) {
	                var $this = $(this);
	                $this.val($this.val().toUpperCase());
	            }).on('submit', self._onSubmitHandler.bind(self));
	        }
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
	            var self = this,
	                data = undefined,
	                errorMsg = undefined;
	
	            if (response.error) {
	                self._enabledForm();
	            } else {
	
	                self._addTokenInput(response.id);
	
	                data = this.$root.serialize();
	                self._sendFormData(data).done(function (data) {
	                    if (data.hasOwnProperty("redirect")) {
	                        window.location = data.redirect;
	                    } else {
	                        var msg = "Internal error #2001. Your card has been charged. ";
	                        msg += "Do not make payment again. Please proceed to your profile directly.";
	                        error(msg, 4500);
	                    }
	                }).fail(function (jqXHR, status) {
	                    if (status == "error") {
	                        errorMsg = JSON.parse(jqXHR.responseText);
	                        error(errorMsg.message, 4500);
	                    } else {
	                        error("Internal error #2000. Your card has not been charged. Please try again.", 4500);
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
	                data: dataForm
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
	                isValidName = +locals.$inputName.val().length,
	                isValidAmount = locals.$inputFee.val().length > 0 && !isNaN(locals.$inputFee.val());
	
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
	
	            if (!isValidAmount) {
	                valid = false;
	                this._setError(locals.$inputFee);
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

/***/ }
/******/ ]);
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIndlYnBhY2s6Ly8vd2VicGFjay9ib290c3RyYXAgZjBmMTY2OTBiZDk3YTI1NWNmYjk/N2VmNCoqKioqKioqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL2hlbHBlcnMvY2xhc3NDYWxsQ2hlY2suanM/MjFhZioqKioqKioqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL2hlbHBlcnMvY3JlYXRlQ2xhc3MuanM/MWRmZSoqKioqKioqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL2NvcmUtanMvb2JqZWN0L2RlZmluZS1wcm9wZXJ0eS5qcz81ZjcwKioqKioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvZm4vb2JqZWN0L2RlZmluZS1wcm9wZXJ0eS5qcz80ZDMzKioqKioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9lczYub2JqZWN0LmRlZmluZS1wcm9wZXJ0eS5qcz84YmRlKioqKioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZXhwb3J0LmpzPzhlNDAqKioqKioqKioqIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19nbG9iYWwuanM/M2M1MioqKioqKioqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2NvcmUuanM/ZDYxMSoqKioqKioqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2N0eC5qcz80ZTU5KioqKioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fYS1mdW5jdGlvbi5qcz8wNjk5KioqKioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9faGlkZS5qcz8wZDJlKioqKioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fb2JqZWN0LWRwLmpzPzU5ODYqKioqKioqKioqIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19hbi1vYmplY3QuanM/M2FmMioqKioqKioqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2lzLW9iamVjdC5qcz9jZmRhKioqKioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9faWU4LWRvbS1kZWZpbmUuanM/YjEwMioqKioqKioqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2Rlc2NyaXB0b3JzLmpzP2MwZjUqKioqKioqKioqIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19mYWlscy5qcz9jNmRkKioqKioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZG9tLWNyZWF0ZS5qcz82MTJmKioqKioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fdG8tcHJpbWl0aXZlLmpzPzFhNjUqKioqKioqKioqIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19wcm9wZXJ0eS1kZXNjLmpzPzI1NmIqKioqKioqKioqIiwid2VicGFjazovLy8uL2Zyb250ZW5kL2pzL3BheW1lbnQvcGF5bWVudC13aWRnZXRzL19zdXBwb3J0ZXJzLXRhYmxlLmpzPzFmZGIiLCJ3ZWJwYWNrOi8vLy4vZnJvbnRlbmQvanMvcGF5bWVudC9wYXltZW50LXBhZ2UuanMiLCJ3ZWJwYWNrOi8vLy4vZnJvbnRlbmQvanMvcGF5bWVudC9wYXltZW50LXdpZGdldHMvX3BheW1lbnQtZm9ybS5qcyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiO0FBQUE7QUFDQTs7QUFFQTtBQUNBOztBQUVBO0FBQ0E7QUFDQTs7QUFFQTtBQUNBO0FBQ0EsdUJBQWU7QUFDZjtBQUNBO0FBQ0E7O0FBRUE7QUFDQTs7QUFFQTtBQUNBOztBQUVBO0FBQ0E7QUFDQTs7O0FBR0E7QUFDQTs7QUFFQTtBQUNBOztBQUVBO0FBQ0E7O0FBRUE7QUFDQTs7Ozs7Ozs7Ozs7Ozs7OztBQ3RDQTs7QUFFQTs7QUFFQTtBQUNBO0FBQ0E7QUFDQTtBQUNBLEc7Ozs7OztBQ1JBOztBQUVBOztBQUVBOztBQUVBOztBQUVBLHVDQUFzQyx1Q0FBdUMsZ0JBQWdCOztBQUU3RjtBQUNBO0FBQ0Esb0JBQW1CLGtCQUFrQjtBQUNyQztBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTs7QUFFQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0EsRUFBQyxHOzs7Ozs7QUMxQkQsbUJBQWtCLHVEOzs7Ozs7QUNBbEI7QUFDQTtBQUNBO0FBQ0E7QUFDQSxHOzs7Ozs7QUNKQTtBQUNBO0FBQ0Esc0VBQXVFLDBDQUEwQyxFOzs7Ozs7QUNGakg7QUFDQTtBQUNBO0FBQ0E7QUFDQTs7QUFFQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBLG9FQUFtRTtBQUNuRTtBQUNBLHNGQUFxRjtBQUNyRjtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0EsWUFBVztBQUNYLFVBQVM7QUFDVDtBQUNBO0FB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RSxFQUFGLENBQVgsQ0FEZ0M7QUFFcEMscUJBQUksT0FBTyxTQUFTLElBQVQsQ0FBYyxpQkFBZCxDQUFQLENBRmdDOztBQUlwQyxxQkFBSSxDQUFDLElBQUQsRUFBTztBQUNQLDRCQUFPLElBQUksTUFBSixDQUFXLEVBQVgsRUFBZSxPQUFmLENBQVAsQ0FETztBQUVQLDhCQUFTLElBQVQsQ0FBYyxRQUFkLEVBQXdCLElBQXhCLEVBRk87a0JBQVg7Y0FKZSxDQUFuQixDQUo2Qjs7O1lBN01oQiIsImZpbGUiOiJwYXltZW50LXBhZ2UuanMiLCJzb3VyY2VzQ29udGVudCI6WyIgXHQvLyBUaGUgbW9kdWxlIGNhY2hlXG4gXHR2YXIgaW5zdGFsbGVkTW9kdWxlcyA9IHt9O1xuXG4gXHQvLyBUaGUgcmVxdWlyZSBmdW5jdGlvblxuIFx0ZnVuY3Rpb24gX193ZWJwYWNrX3JlcXVpcmVfXyhtb2R1bGVJZCkge1xuXG4gXHRcdC8vIENoZWNrIGlmIG1vZHVsZSBpcyBpbiBjYWNoZVxuIFx0XHRpZihpbnN0YWxsZWRNb2R1bGVzW21vZHVsZUlkXSlcbiBcdFx0XHRyZXR1cm4gaW5zdGFsbGVkTW9kdWxlc1ttb2R1bGVJZF0uZXhwb3J0cztcblxuIFx0XHQvLyBDcmVhdGUgYSBuZXcgbW9kdWxlIChhbmQgcHV0IGl0IGludG8gdGhlIGNhY2hlKVxuIFx0XHR2YXIgbW9kdWxlID0gaW5zdGFsbGVkTW9kdWxlc1ttb2R1bGVJZF0gPSB7XG4gXHRcdFx0ZXhwb3J0czoge30sXG4gXHRcdFx0aWQ6IG1vZHVsZUlkLFxuIFx0XHRcdGxvYWRlZDogZmFsc2VcbiBcdFx0fTtcblxuIFx0XHQvLyBFeGVjdXRlIHRoZSBtb2R1bGUgZnVuY3Rpb25cbiBcdFx0bW9kdWxlc1ttb2R1bGVJZF0uY2FsbChtb2R1bGUuZXhwb3J0cywgbW9kdWxlLCBtb2R1bGUuZXhwb3J0cywgX193ZWJwYWNrX3JlcXVpcmVfXyk7XG5cbiBcdFx0Ly8gRmxhZyB0aGUgbW9kdWxlIGFzIGxvYWRlZFxuIFx0XHRtb2R1bGUubG9hZGVkID0gdHJ1ZTtcblxuIFx0XHQvLyBSZXR1cm4gdGhlIGV4cG9ydHMgb2YgdGhlIG1vZHVsZVxuIFx0XHRyZXR1cm4gbW9kdWxlLmV4cG9ydHM7XG4gXHR9XG5cblxuIFx0Ly8gZXhwb3NlIHRoZSBtb2R1bGVzIG9iamVjdCAoX193ZWJwYWNrX21vZHVsZXNfXylcbiBcdF9fd2VicGFja19yZXF1aXJlX18ubSA9IG1vZHVsZXM7XG5cbiBcdC8vIGV4cG9zZSB0aGUgbW9kdWxlIGNhY2hlXG4gXHRfX3dlYnBhY2tfcmVxdWlyZV9fLmMgPSBpbnN0YWxsZWRNb2R1bGVzO1xuXG4gXHQvLyBfX3dlYnBhY2tfcHVibGljX3BhdGhfX1xuIFx0X193ZWJwYWNrX3JlcXVpcmVfXy5wID0gXCJcIjtcblxuIFx0Ly8gTG9hZCBlbnRyeSBtb2R1bGUgYW5kIHJldHVybiBleHBvcnRzXG4gXHRyZXR1cm4gX193ZWJwYWNrX3JlcXVpcmVfXygwKTtcblxuXG5cbi8qKiBXRUJQQUNLIEZPT1RFUiAqKlxuICoqIHdlYnBhY2svYm9vdHN0cmFwIGYwZjE2NjkwYmQ5N2EyNTVjZmI5XG4gKiovIiwiXCJ1c2Ugc3RyaWN0XCI7XG5cbmV4cG9ydHMuX19lc01vZHVsZSA9IHRydWU7XG5cbmV4cG9ydHMuZGVmYXVsdCA9IGZ1bmN0aW9uIChpbnN0YW5jZSwgQ29uc3RydWN0b3IpIHtcbiAgaWYgKCEoaW5zdGFuY2UgaW5zdGFuY2VvZiBDb25zdHJ1Y3RvcikpIHtcbiAgICB0aHJvdyBuZXcgVHlwZUVycm9yKFwiQ2Fubm90IGNhbGwgYSBjbGFzcyBhcyBhIGZ1bmN0aW9uXCIpO1xuICB9XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvaGVscGVycy9jbGFzc0NhbGxDaGVjay5qc1xuICoqIG1vZHVsZSBpZCA9IDNcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsIlwidXNlIHN0cmljdFwiO1xuXG5leHBvcnRzLl9fZXNNb2R1bGUgPSB0cnVlO1xuXG52YXIgX2RlZmluZVByb3BlcnR5ID0gcmVxdWlyZShcImJhYmVsLXJ1bnRpbWUvY29yZS1qcy9vYmplY3QvZGVmaW5lLXByb3BlcnR5XCIpO1xuXG52YXIgX2RlZmluZVByb3BlcnR5MiA9IF9pbnRlcm9wUmVxdWlyZURlZmF1bHQoX2RlZmluZVByb3BlcnR5KTtcblxuZnVuY3Rpb24gX2ludGVyb3BSZXF1aXJlRGVmYXVsdChvYmopIHsgcmV0dXJuIG9iaiAmJiBvYmouX19lc01vZHVsZSA/IG9iaiA6IHsgZGVmYXVsdDogb2JqIH07IH1cblxuZXhwb3J0cy5kZWZhdWx0ID0gZnVuY3Rpb24gKCkge1xuICBmdW5jdGlvbiBkZWZpbmVQcm9wZXJ0aWVzKHRhcmdldCwgcHJvcHMpIHtcbiAgICBmb3IgKHZhciBpID0gMDsgaSA8IHByb3BzLmxlbmd0aDsgaSsrKSB7XG4gICAgICB2YXIgZGVzY3JpcHRvciA9IHByb3BzW2ldO1xuICAgICAgZGVzY3JpcHRvci5lbnVtZXJhYmxlID0gZGVzY3JpcHRvci5lbnVtZXJhYmxlIHx8IGZhbHNlO1xuICAgICAgZGVzY3JpcHRvci5jb25maWd1cmFibGUgPSB0cnVlO1xuICAgICAgaWYgKFwidmFsdWVcIiBpbiBkZXNjcmlwdG9yKSBkZXNjcmlwdG9yLndyaXRhYmxlID0gdHJ1ZTtcbiAgICAgICgwLCBfZGVmaW5lUHJvcGVydHkyLmRlZmF1bHQpKHRhcmdldCwgZGVzY3JpcHRvci5rZXksIGRlc2NyaXB0b3IpO1xuICAgIH1cbiAgfVxuXG4gIHJldHVybiBmdW5jdGlvbiAoQ29uc3RydWN0b3IsIHByb3RvUHJvcHMsIHN0YXRpY1Byb3BzKSB7XG4gICAgaWYgKHByb3RvUHJvcHMpIGRlZmluZVByb3BlcnRpZXMoQ29uc3RydWN0b3IucHJvdG90eXBlLCBwcm90b1Byb3BzKTtcbiAgICBpZiAoc3RhdGljUHJvcHMpIGRlZmluZVByb3BlcnRpZXMoQ29uc3RydWN0b3IsIHN0YXRpY1Byb3BzKTtcbiAgICByZXR1cm4gQ29uc3RydWN0b3I7XG4gIH07XG59KCk7XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9oZWxwZXJzL2NyZWF0ZUNsYXNzLmpzXG4gKiogbW9kdWxlIGlkID0gNFxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwibW9kdWxlLmV4cG9ydHMgPSB7IFwiZGVmYXVsdFwiOiByZXF1aXJlKFwiY29yZS1qcy9saWJyYXJ5L2ZuL29iamVjdC9kZWZpbmUtcHJvcGVydHlcIiksIF9fZXNNb2R1bGU6IHRydWUgfTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL2NvcmUtanMvb2JqZWN0L2RlZmluZS1wcm9wZXJ0eS5qc1xuICoqIG1vZHVsZSBpZCA9IDVcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsInJlcXVpcmUoJy4uLy4uL21vZHVsZXMvZXM2Lm9iamVjdC5kZWZpbmUtcHJvcGVydHknKTtcbnZhciAkT2JqZWN0ID0gcmVxdWlyZSgnLi4vLi4vbW9kdWxlcy9fY29yZScpLk9iamVjdDtcbm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24gZGVmaW5lUHJvcGVydHkoaXQsIGtleSwgZGVzYyl7XG4gIHJldHVybiAkT2JqZWN0LmRlZmluZVByb3BlcnR5KGl0LCBrZXksIGRlc2MpO1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L2ZuL29iamVjdC9kZWZpbmUtcHJvcGVydHkuanNcbiAqKiBtb2R1bGUgaWQgPSA2XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJ2YXIgJGV4cG9ydCA9IHJlcXVpcmUoJy4vX2V4cG9ydCcpO1xyXG4vLyAxOS4xLjIuNCAvIDE1LjIuMy42IE9iamVjdC5kZWZpbmVQcm9wZXJ0eShPLCBQLCBBdHRyaWJ1dGVzKVxyXG4kZXhwb3J0KCRleHBvcnQuUyArICRleHBvcnQuRiAqICFyZXF1aXJlKCcuL19kZXNjcmlwdG9ycycpLCAnT2JqZWN0Jywge2RlZmluZVByb3BlcnR5OiByZXF1aXJlKCcuL19vYmplY3QtZHAnKS5mfSk7XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL2VzNi5vYmplY3QuZGVmaW5lLXByb3BlcnR5LmpzXG4gKiogbW9kdWxlIGlkID0gN1xuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwidmFyIGdsb2JhbCAgICA9IHJlcXVpcmUoJy4vX2dsb2JhbCcpXG4gICwgY29yZSAgICAgID0gcmVxdWlyZSgnLi9fY29yZScpXG4gICwgY3R4ICAgICAgID0gcmVxdWlyZSgnLi9fY3R4JylcbiAgLCBoaWRlICAgICAgPSByZXF1aXJlKCcuL19oaWRlJylcbiAgLCBQUk9UT1RZUEUgPSAncHJvdG90eXBlJztcblxudmFyICRleHBvcnQgPSBmdW5jdGlvbih0eXBlLCBuYW1lLCBzb3VyY2Upe1xuICB2YXIgSVNfRk9SQ0VEID0gdHlwZSAmICRleHBvcnQuRlxuICAgICwgSVNfR0xPQkFMID0gdHlwZSAmICRleHBvcnQuR1xuICAgICwgSVNfU1RBVElDID0gdHlwZSAmICRleHBvcnQuU1xuICAgICwgSVNfUFJPVE8gID0gdHlwZSAmICRleHBvcnQuUFxuICAgICwgSVNfQklORCAgID0gdHlwZSAmICRleHBvcnQuQlxuICAgICwgSVNfV1JBUCAgID0gdHlwZSAmICRleHBvcnQuV1xuICAgICwgZXhwb3J0cyAgID0gSVNfR0xPQkFMID8gY29yZSA6IGNvcmVbbmFtZV0gfHwgKGNvcmVbbmFtZV0gPSB7fSlcbiAgICAsIGV4cFByb3RvICA9IGV4cG9ydHNbUFJPVE9UWVBFXVxuICAgICwgdGFyZ2V0ICAgID0gSVNfR0xPQkFMID8gZ2xvYmFsIDogSVNfU1RBVElDID8gZ2xvYmFsW25hbWVdIDogKGdsb2JhbFtuYW1lXSB8fCB7fSlbUFJPVE9UWVBFXVxuICAgICwga2V5LCBvd24sIG91dDtcbiAgaWYoSVNfR0xPQkFMKXNvdXJjZSA9IG5hbWU7XG4gIGZvcihrZXkgaW4gc291cmNlKXtcbiAgICAvLyBjb250YWlucyBpbiBuYXRpdmVcbiAgICBvd24gPSAhSVNfRk9SQ0VEICYmIHRhcmdldCAmJiB0YXJnZXRba2V5XSAhPT0gdW5kZWZpbmVkO1xuICAgIGlmKG93biAmJiBrZXkgaW4gZXhwb3J0cyljb250aW51ZTtcbiAgICAvLyBleHBvcnQgbmF0aXZlIG9yIHBhc3NlZFxuICAgIG91dCA9IG93biA/IHRhcmdldFtrZXldIDogc291cmNlW2tleV07XG4gICAgLy8gcHJldmVudCBnbG9iYWwgcG9sbHV0aW9uIGZvciBuYW1lc3BhY2VzXG4gICAgZXhwb3J0c1trZXldID0gSVNfR0xPQkFMICYmIHR5cGVvZiB0YXJnZXRba2V5XSAhPSAnZnVuY3Rpb24nID8gc291cmNlW2tleV1cbiAgICAvLyBiaW5kIHRpbWVycyB0byBnbG9iYWwgZm9yIGNhbGwgZnJvbSBleHBvcnQgY29udGV4dFxuICAgIDogSVNfQklORCAmJiBvd24gPyBjdHgob3V0LCBnbG9iYWwpXG4gICAgLy8gd3JhcCBnbG9iYWwgY29uc3RydWN0b3JzIGZvciBwcmV2ZW50IGNoYW5nZSB0aGVtIGluIGxpYnJhcnlcbiAgICA6IElTX1dSQVAgJiYgdGFyZ2V0W2tleV0gPT0gb3V0ID8gKGZ1bmN0aW9uKEMpe1xuICAgICAgdmFyIEYgPSBmdW5jdGlvbihhLCBiLCBjKXtcbiAgICAgICAgaWYodGhpcyBpbnN0YW5jZW9mIEMpe1xuICAgICAgICAgIHN3aXRjaChhcmd1bWVudHMubGVuZ3RoKXtcbiAgICAgICAgICAgIGNhc2UgMDogcmV0dXJuIG5ldyBDO1xuICAgICAgICAgICAgY2FzZSAxOiByZXR1cm4gbmV3IEMoYSk7XG4gICAgICAgICAgICBjYXNlIDI6IHJldHVybiBuZXcgQyhhLCBiKTtcbiAgICAgICAgICB9IHJldHVybiBuZXcgQyhhLCBiLCBjKTtcbiAgICAgICAgfSByZXR1cm4gQy5hcHBseSh0aGlzLCBhcmd1bWVudHMpO1xuICAgICAgfTtcbiAgICAgIEZbUFJPVE9UWVBFXSA9IENbUFJPVE9UWVBFXTtcbiAgICAgIHJldHVybiBGO1xuICAgIC8vIG1ha2Ugc3RhdGljIHZlcnNpb25zIGZvciBwcm90b3R5cGUgbWV0aG9kc1xuICAgIH0pKG91dCkgOiBJU19QUk9UTyAmJiB0eXBlb2Ygb3V0ID09ICdmdW5jdGlvbicgPyBjdHgoRnVuY3Rpb24uY2FsbCwgb3V0KSA6IG91dDtcbiAgICAvLyBleHBvcnQgcHJvdG8gbWV0aG9kcyB0byBjb3JlLiVDT05TVFJVQ1RPUiUubWV0aG9kcy4lTkFNRSVcbiAgICBpZihJU19QUk9UTyl7XG4gICAgICAoZXhwb3J0cy52aXJ0dWFsIHx8IChleHBvcnRzLnZpcnR1YWwgPSB7fSkpW2tleV0gPSBvdXQ7XG4gICAgICAvLyBleHBvcnQgcHJvdG8gbWV0aG9kcyB0byBjb3JlLiVDT05TVFJVQ1RPUiUucHJvdG90eXBlLiVOQU1FJVxuICAgICAgaWYodHlwZSAmICRleHBvcnQuUiAmJiBleHBQcm90byAmJiAhZXhwUHJvdG9ba2V5XSloaWRlKGV4cFByb3RvLCBrZXksIG91dCk7XG4gICAgfVxuICB9XG59O1xuLy8gdHlwZSBiaXRtYXBcbiRleHBvcnQuRiA9IDE7ICAgLy8gZm9yY2VkXG4kZXhwb3J0LkcgPSAyOyAgIC8vIGdsb2JhbFxuJGV4cG9ydC5TID0gNDsgICAvLyBzdGF0aWNcbiRleHBvcnQuUCA9IDg7ICAgLy8gcHJvdG9cbiRleHBvcnQuQiA9IDE2OyAgLy8gYmluZFxuJGV4cG9ydC5XID0gMzI7ICAvLyB3cmFwXG4kZXhwb3J0LlUgPSA2NDsgIC8vIHNhZmVcbiRleHBvcnQuUiA9IDEyODsgLy8gcmVhbCBwcm90byBtZXRob2QgZm9yIGBsaWJyYXJ5YCBcbm1vZHVsZS5leHBvcnRzID0gJGV4cG9ydDtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2V4cG9ydC5qc1xuICoqIG1vZHVsZSBpZCA9IDhcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsIi8vIGh0dHBzOi8vZ2l0aHViLmNvbS96bG9pcm9jay9jb3JlLWpzL2lzc3Vlcy84NiNpc3N1ZWNvbW1lbnQtMTE1NzU5MDI4XG52YXIgZ2xvYmFsID0gbW9kdWxlLmV4cG9ydHMgPSB0eXBlb2Ygd2luZG93ICE9ICd1bmRlZmluZWQnICYmIHdpbmRvdy5NYXRoID09IE1hdGhcbiAgPyB3aW5kb3cgOiB0eXBlb2Ygc2VsZiAhPSAndW5kZWZpbmVkJyAmJiBzZWxmLk1hdGggPT0gTWF0aCA/IHNlbGYgOiBGdW5jdGlvbigncmV0dXJuIHRoaXMnKSgpO1xuaWYodHlwZW9mIF9fZyA9PSAnbnVtYmVyJylfX2cgPSBnbG9iYWw7IC8vIGVzbGludC1kaXNhYmxlLWxpbmUgbm8tdW5kZWZcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2dsb2JhbC5qc1xuICoqIG1vZHVsZSBpZCA9IDlcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsInZhciBjb3JlID0gbW9kdWxlLmV4cG9ydHMgPSB7dmVyc2lvbjogJzIuMi4wJ307XG5pZih0eXBlb2YgX19lID09ICdudW1iZXInKV9fZSA9IGNvcmU7IC8vIGVzbGludC1kaXNhYmxlLWxpbmUgbm8tdW5kZWZcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2NvcmUuanNcbiAqKiBtb2R1bGUgaWQgPSAxMFxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwiLy8gb3B0aW9uYWwgLyBzaW1wbGUgY29udGV4dCBiaW5kaW5nXG52YXIgYUZ1bmN0aW9uID0gcmVxdWlyZSgnLi9fYS1mdW5jdGlvbicpO1xubW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihmbiwgdGhhdCwgbGVuZ3RoKXtcbiAgYUZ1bmN0aW9uKGZuKTtcbiAgaWYodGhhdCA9PT0gdW5kZWZpbmVkKXJldHVybiBmbjtcbiAgc3dpdGNoKGxlbmd0aCl7XG4gICAgY2FzZSAxOiByZXR1cm4gZnVuY3Rpb24oYSl7XG4gICAgICByZXR1cm4gZm4uY2FsbCh0aGF0LCBhKTtcbiAgICB9O1xuICAgIGNhc2UgMjogcmV0dXJuIGZ1bmN0aW9uKGEsIGIpe1xuICAgICAgcmV0dXJuIGZuLmNhbGwodGhhdCwgYSwgYik7XG4gICAgfTtcbiAgICBjYXNlIDM6IHJldHVybiBmdW5jdGlvbihhLCBiLCBjKXtcbiAgICAgIHJldHVybiBmbi5jYWxsKHRoYXQsIGEsIGIsIGMpO1xuICAgIH07XG4gIH1cbiAgcmV0dXJuIGZ1bmN0aW9uKC8qIC4uLmFyZ3MgKi8pe1xuICAgIHJldHVybiBmbi5hcHBseSh0aGF0LCBhcmd1bWVudHMpO1xuICB9O1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2N0eC5qc1xuICoqIG1vZHVsZSBpZCA9IDExXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJtb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGl0KXtcbiAgaWYodHlwZW9mIGl0ICE9ICdmdW5jdGlvbicpdGhyb3cgVHlwZUVycm9yKGl0ICsgJyBpcyBub3QgYSBmdW5jdGlvbiEnKTtcbiAgcmV0dXJuIGl0O1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2EtZnVuY3Rpb24uanNcbiAqKiBtb2R1bGUgaWQgPSAxMlxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwidmFyIGRQICAgICAgICAgPSByZXF1aXJlKCcuL19vYmplY3QtZHAnKVxuICAsIGNyZWF0ZURlc2MgPSByZXF1aXJlKCcuL19wcm9wZXJ0eS1kZXNjJyk7XG5tb2R1bGUuZXhwb3J0cyA9IHJlcXVpcmUoJy4vX2Rlc2NyaXB0b3JzJykgPyBmdW5jdGlvbihvYmplY3QsIGtleSwgdmFsdWUpe1xuICByZXR1cm4gZFAuZihvYmplY3QsIGtleSwgY3JlYXRlRGVzYygxLCB2YWx1ZSkpO1xufSA6IGZ1bmN0aW9uKG9iamVjdCwga2V5LCB2YWx1ZSl7XG4gIG9iamVjdFtrZXldID0gdmFsdWU7XG4gIHJldHVybiBvYmplY3Q7XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9faGlkZS5qc1xuICoqIG1vZHVsZSBpZCA9IDEzXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJ2YXIgYW5PYmplY3QgICAgICAgPSByZXF1aXJlKCcuL19hbi1vYmplY3QnKVxuICAsIElFOF9ET01fREVGSU5FID0gcmVxdWlyZSgnLi9faWU4LWRvbS1kZWZpbmUnKVxuICAsIHRvUHJpbWl0aXZlICAgID0gcmVxdWlyZSgnLi9fdG8tcHJpbWl0aXZlJylcbiAgLCBkUCAgICAgICAgICAgICA9IE9iamVjdC5kZWZpbmVQcm9wZXJ0eTtcblxuZXhwb3J0cy5mID0gcmVxdWlyZSgnLi9fZGVzY3JpcHRvcnMnKSA/IE9iamVjdC5kZWZpbmVQcm9wZXJ0eSA6IGZ1bmN0aW9uIGRlZmluZVByb3BlcnR5KE8sIFAsIEF0dHJpYnV0ZXMpe1xuICBhbk9iamVjdChPKTtcbiAgUCA9IHRvUHJpbWl0aXZlKFAsIHRydWUpO1xuICBhbk9iamVjdChBdHRyaWJ1dGVzKTtcbiAgaWYoSUU4X0RPTV9ERUZJTkUpdHJ5IHtcbiAgICByZXR1cm4gZFAoTywgUCwgQXR0cmlidXRlcyk7XG4gIH0gY2F0Y2goZSl7IC8qIGVtcHR5ICovIH1cbiAgaWYoJ2dldCcgaW4gQXR0cmlidXRlcyB8fCAnc2V0JyBpbiBBdHRyaWJ1dGVzKXRocm93IFR5cGVFcnJvcignQWNjZXNzb3JzIG5vdCBzdXBwb3J0ZWQhJyk7XG4gIGlmKCd2YWx1ZScgaW4gQXR0cmlidXRlcylPW1BdID0gQXR0cmlidXRlcy52YWx1ZTtcbiAgcmV0dXJuIE87XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fb2JqZWN0LWRwLmpzXG4gKiogbW9kdWxlIGlkID0gMTRcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsInZhciBpc09iamVjdCA9IHJlcXVpcmUoJy4vX2lzLW9iamVjdCcpO1xubW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihpdCl7XG4gIGlmKCFpc09iamVjdChpdCkpdGhyb3cgVHlwZUVycm9yKGl0ICsgJyBpcyBub3QgYW4gb2JqZWN0IScpO1xuICByZXR1cm4gaXQ7XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fYW4tb2JqZWN0LmpzXG4gKiogbW9kdWxlIGlkID0gMTVcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsIm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oaXQpe1xuICByZXR1cm4gdHlwZW9mIGl0ID09PSAnb2JqZWN0JyA/IGl0ICE9PSBudWxsIDogdHlwZW9mIGl0ID09PSAnZnVuY3Rpb24nO1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2lzLW9iamVjdC5qc1xuICoqIG1vZHVsZSBpZCA9IDE2XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJtb2R1bGUuZXhwb3J0cyA9ICFyZXF1aXJlKCcuL19kZXNjcmlwdG9ycycpICYmICFyZXF1aXJlKCcuL19mYWlscycpKGZ1bmN0aW9uKCl7XHJcbiAgcmV0dXJuIE9iamVjdC5kZWZpbmVQcm9wZXJ0eShyZXF1aXJlKCcuL19kb20tY3JlYXRlJykoJ2RpdicpLCAnYScsIHtnZXQ6IGZ1bmN0aW9uKCl7IHJldHVybiA3OyB9fSkuYSAhPSA3O1xyXG59KTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2llOC1kb20tZGVmaW5lLmpzXG4gKiogbW9kdWxlIGlkID0gMTdcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsIi8vIFRoYW5rJ3MgSUU4IGZvciBoaXMgZnVubnkgZGVmaW5lUHJvcGVydHlcbm1vZHVsZS5leHBvcnRzID0gIXJlcXVpcmUoJy4vX2ZhaWxzJykoZnVuY3Rpb24oKXtcbiAgcmV0dXJuIE9iamVjdC5kZWZpbmVQcm9wZXJ0eSh7fSwgJ2EnLCB7Z2V0OiBmdW5jdGlvbigpeyByZXR1cm4gNzsgfX0pLmEgIT0gNztcbn0pO1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZGVzY3JpcHRvcnMuanNcbiAqKiBtb2R1bGUgaWQgPSAxOFxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwibW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihleGVjKXtcbiAgdHJ5IHtcbiAgICByZXR1cm4gISFleGVjKCk7XG4gIH0gY2F0Y2goZSl7XG4gICAgcmV0dXJuIHRydWU7XG4gIH1cbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19mYWlscy5qc1xuICoqIG1vZHVsZSBpZCA9IDE5XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJ2YXIgaXNPYmplY3QgPSByZXF1aXJlKCcuL19pcy1vYmplY3QnKVxuICAsIGRvY3VtZW50ID0gcmVxdWlyZSgnLi9fZ2xvYmFsJykuZG9jdW1lbnRcbiAgLy8gaW4gb2xkIElFIHR5cGVvZiBkb2N1bWVudC5jcmVhdGVFbGVtZW50IGlzICdvYmplY3QnXG4gICwgaXMgPSBpc09iamVjdChkb2N1bWVudCkgJiYgaXNPYmplY3QoZG9jdW1lbnQuY3JlYXRlRWxlbWVudCk7XG5tb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGl0KXtcbiAgcmV0dXJuIGlzID8gZG9jdW1lbnQuY3JlYXRlRWxlbWVudChpdCkgOiB7fTtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19kb20tY3JlYXRlLmpzXG4gKiogbW9kdWxlIGlkID0gMjBcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsIi8vIDcuMS4xIFRvUHJpbWl0aXZlKGlucHV0IFssIFByZWZlcnJlZFR5cGVdKVxudmFyIGlzT2JqZWN0ID0gcmVxdWlyZSgnLi9faXMtb2JqZWN0Jyk7XG4vLyBpbnN0ZWFkIG9mIHRoZSBFUzYgc3BlYyB2ZXJzaW9uLCB3ZSBkaWRuJ3QgaW1wbGVtZW50IEBAdG9QcmltaXRpdmUgY2FzZVxuLy8gYW5kIHRoZSBzZWNvbmQgYXJndW1lbnQgLSBmbGFnIC0gcHJlZmVycmVkIHR5cGUgaXMgYSBzdHJpbmdcbm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oaXQsIFMpe1xuICBpZighaXNPYmplY3QoaXQpKXJldHVybiBpdDtcbiAgdmFyIGZuLCB2YWw7XG4gIGlmKFMgJiYgdHlwZW9mIChmbiA9IGl0LnRvU3RyaW5nKSA9PSAnZnVuY3Rpb24nICYmICFpc09iamVjdCh2YWwgPSBmbi5jYWxsKGl0KSkpcmV0dXJuIHZhbDtcbiAgaWYodHlwZW9mIChmbiA9IGl0LnZhbHVlT2YpID09ICdmdW5jdGlvbicgJiYgIWlzT2JqZWN0KHZhbCA9IGZuLmNhbGwoaXQpKSlyZXR1cm4gdmFsO1xuICBpZighUyAmJiB0eXBlb2YgKGZuID0gaXQudG9TdHJpbmcpID09ICdmdW5jdGlvbicgJiYgIWlzT2JqZWN0KHZhbCA9IGZuLmNhbGwoaXQpKSlyZXR1cm4gdmFsO1xuICB0aHJvdyBUeXBlRXJyb3IoXCJDYW4ndCBjb252ZXJ0IG9iamVjdCB0byBwcmltaXRpdmUgdmFsdWVcIik7XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fdG8tcHJpbWl0aXZlLmpzXG4gKiogbW9kdWxlIGlkID0gMjFcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsIm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oYml0bWFwLCB2YWx1ZSl7XG4gIHJldHVybiB7XG4gICAgZW51bWVyYWJsZSAgOiAhKGJpdG1hcCAmIDEpLFxuICAgIGNvbmZpZ3VyYWJsZTogIShiaXRtYXAgJiAyKSxcbiAgICB3cml0YWJsZSAgICA6ICEoYml0bWFwICYgNCksXG4gICAgdmFsdWUgICAgICAgOiB2YWx1ZVxuICB9O1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX3Byb3BlcnR5LWRlc2MuanNcbiAqKiBtb2R1bGUgaWQgPSAyMlxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwiJ3VzZSBzdHJpY3QnO1xuXG4vKipcbiAqIEBwYXJhbSBzZWxlY3RvclxuICogQGNvbnN0cnVjdG9yXG4gKi9cbmV4cG9ydCBkZWZhdWx0IGNsYXNzIFdpZGdldCB7XG4gICAgY29uc3RydWN0b3Ioc2VsZWN0b3IpIHtcbiAgICAgICAgdGhpcy4kcm9vdCA9ICQoc2VsZWN0b3IpO1xuICAgICAgICB0aGlzLl9hc3NpZ25FdmVudHMoKTtcbiAgICB9XG5cbiAgICBfYXNzaWduRXZlbnRzKCkge1xuICAgICAgICBjb25zdCBzZWxmID0gdGhpcztcblxuICAgICAgICBzZWxmLiRyb290Lm9uKCdjbGljaycsICcuZGxnLWhtZmVlc19fbGluayAnLCBmdW5jdGlvbihlKXtcbiAgICAgICAgICAgIHZhciAkdGhpcyA9ICQodGhpcyk7XG4gICAgICAgICAgICBlLnByZXZlbnREZWZhdWx0KCk7XG5cbiAgICAgICAgICAgIGlmICgkdGhpcy5oYXNDbGFzcygnc3RhdGVfYWN0aXZlJykpIHJldHVybjtcblxuICAgICAgICAgICAgc2VsZi5fc3dpdGNoVGFiKCR0aGlzKTtcbiAgICAgICAgfSk7XG4gICAgfTtcblxuICAgIF9zd2l0Y2hUYWIoJGxpbmspe1xuICAgICAgICBjb25zdCAkdGFyZ2V0ID0gdGhpcy5fZ2V0VGFyZ2V0KCRsaW5rKTtcblxuICAgICAgICBpZiAoISR0YXJnZXQubGVuZ3RoKSByZXR1cm47XG5cbiAgICAgICAgJHRhcmdldC5zaG93KClcbiAgICAgICAgICAgIC5zaWJsaW5ncygnLnRhYmxlJykuaGlkZSgpO1xuICAgICAgICAkbGluay5hZGRDbGFzcygnc3RhdGVfYWN0aXZlJylcbiAgICAgICAgICAgIC5zaWJsaW5ncygnLmRsZy1obWZlZXNfX2xpbmsnKS5yZW1vdmVDbGFzcygnc3RhdGVfYWN0aXZlJyk7XG4gICAgfVxuXG4gICAgX2dldFRhcmdldCgkZWwpe1xuICAgICAgICByZXR1cm4gdGhpcy4kcm9vdC5maW5kKCRlbC5hdHRyKCdocmVmJykpO1xuICAgIH1cblxuICAgIC8vIHN0YXRpY1xuICAgIHN0YXRpYyBwbHVnaW4oc2VsZWN0b3IsIG9wdGlvbnMpIHtcbiAgICAgICAgY29uc3QgJGVsZW1zID0gJChzZWxlY3Rvcik7XG4gICAgICAgIGlmICghJGVsZW1zLmxlbmd0aCkgcmV0dXJuO1xuXG4gICAgICAgIHJldHVybiAkZWxlbXMuZWFjaChmdW5jdGlvbiAoaW5kZXgsIGVsKSB7XG4gICAgICAgICAgICBsZXQgJGVsZW1lbnQgPSAkKGVsKTtcbiAgICAgICAgICAgIGxldCBkYXRhID0gJGVsZW1lbnQuZGF0YSgnd2lkZ2V0LnNjcm9sbHRvJyk7XG5cbiAgICAgICAgICAgIGlmICghZGF0YSkge1xuICAgICAgICAgICAgICAgIGRhdGEgPSBuZXcgV2lkZ2V0KGVsLCBvcHRpb25zKTtcbiAgICAgICAgICAgICAgICAkZWxlbWVudC5kYXRhKCd3aWRnZXQnLCBkYXRhKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgfSlcbiAgICB9XG59XG5cblxuXG4vKiogV0VCUEFDSyBGT09URVIgKipcbiAqKiAuL2Zyb250ZW5kL2pzL3BheW1lbnQvcGF5bWVudC13aWRnZXRzL19zdXBwb3J0ZXJzLXRhYmxlLmpzXG4gKiovIiwiJ3VzZSBzdHJpY3QnO1xuXG5pbXBvcnQgUGF5bWVudEZvcm0gZnJvbSAnLi9wYXltZW50LXdpZGdldHMvX3BheW1lbnQtZm9ybSc7XG5pbXBvcnQgU3VwcG9ydFRhYmxlIGZyb20gJy4vcGF5bWVudC13aWRnZXRzL19zdXBwb3J0ZXJzLXRhYmxlJztcblxuXG4kKGZ1bmN0aW9uKCl7XG4gICAgUGF5bWVudEZvcm0ucGx1Z2luKCcuanMtcGF5bWVudC1mb3JtJyk7XG4gICAgU3VwcG9ydFRhYmxlLnBsdWdpbignLmpzLXN1cHBvcnQtdGFibGUnKVxufSk7XG5cblxuXG4vKiogV0VCUEFDSyBGT09URVIgKipcbiAqKiAuL2Zyb250ZW5kL2pzL3BheW1lbnQvcGF5bWVudC1wYWdlLmpzXG4gKiovIiwiJ3VzZSBzdHJpY3QnO1xuXG4vKipcbiAqIEZvcm0gZm9yIHBheW1lbnQgd2l0aCBmZWVcbiAqIEBwYXJhbSBzZWxlY3RvclxuICogQGNvbnN0cnVjdG9yXG4gKi9cbmV4cG9ydCBkZWZhdWx0IGNsYXNzIFdpZGdldCB7XG4gICAgY29uc3RydWN0b3Ioc2VsZWN0b3IpIHtcbiAgICAgICAgY29uc3Qgc2VsZiA9IHRoaXM7XG5cbiAgICAgICAgc2VsZi4kcm9vdCA9ICQoc2VsZWN0b3IpO1xuICAgICAgICBzZWxmLmxvY2FscyA9IHNlbGYuX2dldERvbSgpO1xuICAgICAgICBzZWxmLmFwaWtleSA9IHNlbGYuJHJvb3QuZGF0YSgnYXBpa2V5Jyk7XG5cbiAgICAgICAgc2VsZi5fZ2V0U3RyaXBlU2NyaXB0KClcbiAgICAgICAgICAgIC5kb25lKGZ1bmN0aW9uICgpIHtcbiAgICAgICAgICAgICAgICBTdHJpcGUuc2V0UHVibGlzaGFibGVLZXkoc2VsZi5hcGlrZXkpO1xuICAgICAgICAgICAgICAgIHNlbGYuX2luaXQoKTtcblxuICAgICAgICAgICAgICAgIHNlbGYuX3VwZGF0ZUFtb3VudChzZWxmLmxvY2Fscy4kaW5wdXRGZWUpO1xuICAgICAgICAgICAgICAgIHNlbGYuX2Fzc2lnbkV2ZW50cygpO1xuICAgICAgICAgICAgfSlcbiAgICB9XG5cbiAgICBfZ2V0U3RyaXBlU2NyaXB0KCkge1xuICAgICAgICByZXR1cm4gJC5hamF4KHtcbiAgICAgICAgICAgIHVybDogJ2h0dHBzOi8vanMuc3RyaXBlLmNvbS92Mi8nLFxuICAgICAgICAgICAgZGF0YVR5cGU6IFwic2NyaXB0XCJcbiAgICAgICAgfSk7XG4gICAgfVxuXG4gICAgX2dldERvbSgpIHtcbiAgICAgICAgY29uc3QgJHJvb3QgPSB0aGlzLiRyb290O1xuXG4gICAgICAgIHJldHVybiB7XG4gICAgICAgICAgICAkaW5wdXROdW1iZXI6ICRyb290LmZpbmQoJ1tkYXRhLWNhcmQtbnVtYmVyXScpLFxuICAgICAgICAgICAgJGlucHV0TmFtZTogJHJvb3QuZmluZCgnW2RhdGEtY2FyZC1uYW1lXScpLFxuICAgICAgICAgICAgJGlucHV0TW9udGg6ICRyb290LmZpbmQoJ1tkYXRhLWNhcmQtbW9udGhdJyksXG4gICAgICAgICAgICAkaW5wdXRZZWFyOiAkcm9vdC5maW5kKCdbZGF0YS1jYXJkLXllYXJdJyksXG4gICAgICAgICAgICAkaW5wdXRDVkM6ICRyb290LmZpbmQoJ1tkYXRhLWNhcmQtY3ZjXScpLFxuICAgICAgICAgICAgJHN1Ym1pdDogJHJvb3QuZmluZCgnW2RhdGEtcGF5bWVudC1zdWJtaXRdJyksXG4gICAgICAgICAgICAkaW5wdXRGZWU6ICRyb290LmZpbmQoJ1tkYXRhLXBheW1lbnQtZmVlXScpLFxuICAgICAgICAgICAgJHRheFBsYWNlOiAkcm9vdC5maW5kKCdbZGF0YS1wYXltZW50LXRheF0nKSxcbiAgICAgICAgICAgICRhbW91bnRQbGFjZTogJHJvb3QuZmluZCgnW2RhdGEtcGF5bWVudC1hbW91bnRdJyksXG4gICAgICAgICAgICAkcGF5UGxhY2U6ICRyb290LmZpbmQoJ1tkYXRhLXBheW1lbnQtcHJpY2VdJylcbiAgICAgICAgfVxuICAgIH1cblxuICAgIF9pbml0KCkge1xuICAgICAgICBpZiAoISQuZm4ucGF5bWVudCkge1xuICAgICAgICAgICAgY29uc29sZS5sb2coJ1RoZXJlIGlzIG5vIHBheW1lbnQgcGx1Z2luIG9uIHRoaXMgcGFnZScpO1xuICAgICAgICAgICAgcmV0dXJuO1xuICAgICAgICB9XG5cbiAgICAgICAgdGhpcy5sb2NhbHMuJGlucHV0TnVtYmVyLnBheW1lbnQoJ2Zvcm1hdENhcmROdW1iZXInKTtcbiAgICAgICAgdGhpcy5sb2NhbHMuJGlucHV0Q1ZDLnBheW1lbnQoJ2Zvcm1hdENhcmRDVkMnKTtcbiAgICB9XG5cbiAgICBfYXNzaWduRXZlbnRzKCkge1xuICAgICAgICBjb25zdCBzZWxmID0gdGhpcztcblxuICAgICAgICBzZWxmLiRyb290XG4gICAgICAgICAgICAub24oJ2NoYW5nZSBwYXN0ZSBrZXl1cCcsICdbZGF0YS1wYXltZW50LWZlZV0nLCBmdW5jdGlvbiAoZSkge1xuICAgICAgICAgICAgICAgIHZhciAkdGhpcyA9ICQodGhpcyk7XG5cbiAgICAgICAgICAgICAgICBzZWxmLl9yZW1vdmVFcnJvcigkdGhpcyk7XG4gICAgICAgICAgICAgICAgc2VsZi5fdXBkYXRlQW1vdW50KCR0aGlzKTtcbiAgICAgICAgICAgIH0pXG4gICAgICAgICAgICAub24oJ2tleXVwJywgJ2lucHV0JywgZnVuY3Rpb24gKCkge1xuICAgICAgICAgICAgICAgIHNlbGYuX3JlbW92ZUVycm9yKCQodGhpcykpO1xuICAgICAgICAgICAgfSlcbiAgICAgICAgICAgIC5vbignY2hhbmdlIHBhc3RlIGtleXVwJywgJ1tkYXRhLWNhcmQtbmFtZV0nLCBmdW5jdGlvbiAoZSkge1xuICAgICAgICAgICAgICAgIHZhciAkdGhpcyA9ICQodGhpcyk7XG4gICAgICAgICAgICAgICAgJHRoaXMudmFsKCR0aGlzLnZhbCgpLnRvVXBwZXJDYXNlKCkpXG4gICAgICAgICAgICB9KVxuICAgICAgICAgICAgLm9uKCdzdWJtaXQnLCBzZWxmLl9vblN1Ym1pdEhhbmRsZXIuYmluZChzZWxmKSk7XG4gICAgfVxuXG4gICAgX3VwZGF0ZUFtb3VudCgkZWwpIHtcbiAgICAgICAgbGV0IGxvY2FscyA9IHRoaXMubG9jYWxzLFxuICAgICAgICAgICAgYW1vdW50ID0gJGVsLnZhbCgpIDwgMSA/IDAuMDAgOiBwYXJzZUludCgkZWwudmFsKCkpLFxuICAgICAgICAgICAgdGF4UGVyY2VudCA9IHBhcnNlRmxvYXQoJGVsLmRhdGEoJ3RheCcpKSxcbiAgICAgICAgICAgIHRheCwgYW1vdW50V2l0aFRheDtcblxuICAgICAgICB0YXggPSAoYW1vdW50ICogdGF4UGVyY2VudCkgLyAxMDA7XG4gICAgICAgIGFtb3VudFdpdGhUYXggPSBhbW91bnQgKyB0YXg7XG5cbiAgICAgICAgbG9jYWxzLiRhbW91bnRQbGFjZS50ZXh0KGFtb3VudCk7XG4gICAgICAgIGxvY2Fscy4kdGF4UGxhY2UudGV4dCh0YXgpO1xuICAgICAgICBsb2NhbHMuJHBheVBsYWNlLnRleHQoYW1vdW50V2l0aFRheCk7XG4gICAgfVxuXG4gICAgX3NldEVycm9yKCRlbCkge1xuICAgICAgICAkZWwucGFyZW50KCkuYWRkQ2xhc3MoJ2hhcy1lcnJvcicpO1xuICAgIH1cblxuICAgIF9yZW1vdmVFcnJvcigkZWwpIHtcbiAgICAgICAgJGVsLnBhcmVudCgpLnJlbW92ZUNsYXNzKCdoYXMtZXJyb3InKTtcbiAgICB9XG5cbiAgICBfZGlzYWJsZWRGb3JtKCkge1xuICAgICAgICB0aGlzLmxvY2Fscy4kc3VibWl0LnByb3AoJ2Rpc2FibGVkJywgdHJ1ZSk7XG4gICAgICAgICQoXCJib2R5XCIpLmNzcyhcImN1cnNvclwiLCBcInByb2dyZXNzXCIpO1xuICAgIH1cblxuICAgIF9lbmFibGVkRm9ybSgpIHtcbiAgICAgICAgdGhpcy5sb2NhbHMuJHN1Ym1pdC5wcm9wKCdkaXNhYmxlZCcsIGZhbHNlKTtcbiAgICAgICAgJChcImJvZHlcIikuY3NzKFwiY3Vyc29yXCIsIFwiZGVmYXVsdFwiKTtcbiAgICB9XG5cbiAgICBfYWRkVG9rZW5JbnB1dCh0b2tlbikge1xuICAgICAgICBsZXQgJHJvb3QgPSB0aGlzLiRyb290LFxuICAgICAgICAgICAgdGVtcGxhdGUgPSAnPGlucHV0IHR5cGU9XCJoaWRkZW5cIiB2YWx1ZT1cIicgKyB0b2tlbiArICdcIiBuYW1lPVwidG9rZW5cIiAvPic7XG5cbiAgICAgICAgJHJvb3QuZmluZCgnaW5wdXRbbmFtZT1cInRva2VuXCJdJykucmVtb3ZlKClcbiAgICAgICAgJHJvb3QuYXBwZW5kKHRlbXBsYXRlKTtcbiAgICB9O1xuXG4gICAgX29uU3VibWl0SGFuZGxlcihlKSB7XG4gICAgICAgIGNvbnN0IHNlbGYgPSB0aGlzO1xuICAgICAgICBlLnByZXZlbnREZWZhdWx0KCk7XG5cbiAgICAgICAgaWYgKCF0aGlzLmlzVmFsaWRGb3JtKCkpIHJldHVybjtcbiAgICAgICAgdGhpcy5fZGlzYWJsZWRGb3JtKCk7XG5cbiAgICAgICAgU3RyaXBlLmNhcmQuY3JlYXRlVG9rZW4oc2VsZi4kcm9vdCwgc2VsZi5fc3RyaXBlSGFuZGxlci5iaW5kKHNlbGYpKTtcbiAgICB9O1xuXG4gICAgX3N0cmlwZUhhbmRsZXIoc3RhdHVzLCByZXNwb25zZSkge1xuICAgICAgICBsZXQgc2VsZiA9IHRoaXMsXG4gICAgICAgICAgICBkYXRhLCBlcnJvck1zZztcblxuICAgICAgICBpZiAocmVzcG9uc2UuZXJyb3IpIHtcbiAgICAgICAgICAgIHNlbGYuX2VuYWJsZWRGb3JtKCk7XG4gICAgICAgIH0gZWxzZSB7XG5cbiAgICAgICAgICAgIHNlbGYuX2FkZFRva2VuSW5wdXQocmVzcG9uc2UuaWQpO1xuXG4gICAgICAgICAgICBkYXRhID0gdGhpcy4kcm9vdC5zZXJpYWxpemUoKTtcbiAgICAgICAgICAgIHNlbGYuX3NlbmRGb3JtRGF0YShkYXRhKVxuICAgICAgICAgICAgICAgIC5kb25lKGZ1bmN0aW9uIChkYXRhKSB7XG4gICAgICAgICAgICAgICAgICAgIGlmIChkYXRhLmhhc093blByb3BlcnR5KFwicmVkaXJlY3RcIikpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgIHdpbmRvdy5sb2NhdGlvbiA9IGRhdGEucmVkaXJlY3Q7XG4gICAgICAgICAgICAgICAgICAgIH0gZWxzZSB7XG4gICAgICAgICAgICAgICAgICAgICAgICB2YXIgbXNnID0gXCJJbnRlcm5hbCBlcnJvciAjMjAwMS4gWW91ciBjYXJkIGhhcyBiZWVuIGNoYXJnZWQuIFwiO1xuICAgICAgICAgICAgICAgICAgICAgICAgbXNnICs9IFwiRG8gbm90IG1ha2UgcGF5bWVudCBhZ2Fpbi4gUGxlYXNlIHByb2NlZWQgdG8geW91ciBwcm9maWxlIGRpcmVjdGx5LlwiO1xuICAgICAgICAgICAgICAgICAgICAgICAgZXJyb3IobXNnLCA0NTAwKTtcbiAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIH0pXG4gICAgICAgICAgICAgICAgLmZhaWwoZnVuY3Rpb24gKGpxWEhSLCBzdGF0dXMpIHtcbiAgICAgICAgICAgICAgICAgICAgaWYgKHN0YXR1cyA9PSBcImVycm9yXCIpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgIGVycm9yTXNnID0gSlNPTi5wYXJzZShqcVhIUi5yZXNwb25zZVRleHQpO1xuICAgICAgICAgICAgICAgICAgICAgICAgZXJyb3IoZXJyb3JNc2cubWVzc2FnZSwgNDUwMCk7XG4gICAgICAgICAgICAgICAgICAgIH0gZWxzZSB7XG4gICAgICAgICAgICAgICAgICAgICAgICBlcnJvcihcIkludGVybmFsIGVycm9yICMyMDAwLiBZb3VyIGNhcmQgaGFzIG5vdCBiZWVuIGNoYXJnZWQuIFBsZWFzZSB0cnkgYWdhaW4uXCIsIDQ1MDApO1xuICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgfSlcbiAgICAgICAgICAgICAgICAuY29tcGxldGUoZnVuY3Rpb24gKCkge1xuICAgICAgICAgICAgICAgICAgICBzZWxmLl9lbmFibGVkRm9ybSgpO1xuICAgICAgICAgICAgICAgIH0pO1xuICAgICAgICB9XG4gICAgfTtcblxuICAgIF9zZW5kRm9ybURhdGEoZGF0YUZvcm0pIHtcbiAgICAgICAgcmV0dXJuICQuYWpheCh7XG4gICAgICAgICAgICB0eXBlOiBcIlBPU1RcIixcbiAgICAgICAgICAgIHVybDogdGhpcy4kcm9vdC5hdHRyKFwiYWN0aW9uXCIpLFxuICAgICAgICAgICAgZGF0YTogZGF0YUZvcm1cbiAgICAgICAgfSlcbiAgICB9O1xuXG4gICAgaXNWYWxpZEZvcm0oKSB7XG4gICAgICAgIGxldCBzZWxmID0gdGhpcyxcbiAgICAgICAgICAgIHZhbGlkID0gdHJ1ZSxcbiAgICAgICAgICAgIGxvY2FscyA9IHRoaXMubG9jYWxzLFxuICAgICAgICAgICAgaXNWYWxpZE51bWJlciA9ICQucGF5bWVudC52YWxpZGF0ZUNhcmROdW1iZXIobG9jYWxzLiRpbnB1dE51bWJlci52YWwoKSksXG4gICAgICAgICAgICBpc1ZhbGlkRXhwaXJ5ID0gJC5wYXltZW50LnZhbGlkYXRlQ2FyZEV4cGlyeShsb2NhbHMuJGlucHV0TW9udGgudmFsKCksIGxvY2Fscy4kaW5wdXRZZWFyLnZhbCgpKSxcbiAgICAgICAgICAgIGlzVmFsaWRDVkMgPSAkLnBheW1lbnQudmFsaWRhdGVDYXJkQ1ZDKGxvY2Fscy4kaW5wdXRDVkMudmFsKCkpLFxuICAgICAgICAgICAgaXNWYWxpZE5hbWUgPSArbG9jYWxzLiRpbnB1dE5hbWUudmFsKCkubGVuZ3RoLFxuICAgICAgICAgICAgaXNWYWxpZEFtb3VudCA9IChsb2NhbHMuJGlucHV0RmVlLnZhbCgpLmxlbmd0aCA+IDApICYmICghaXNOYU4obG9jYWxzLiRpbnB1dEZlZS52YWwoKSkpO1xuXG4gICAgICAgIGlmICghaXNWYWxpZE51bWJlcikge1xuICAgICAgICAgICAgc2VsZi5fc2V0RXJyb3IobG9jYWxzLiRpbnB1dE51bWJlcik7XG4gICAgICAgICAgICB2YWxpZCA9IGZhbHNlO1xuICAgICAgICB9XG5cbiAgICAgICAgaWYgKCFpc1ZhbGlkRXhwaXJ5KSB7XG4gICAgICAgICAgICBzZWxmLl9zZXRFcnJvcihsb2NhbHMuJGlucHV0TW9udGgpO1xuICAgICAgICAgICAgc2VsZi5fc2V0RXJyb3IobG9jYWxzLiRpbnB1dFllYXIpO1xuICAgICAgICAgICAgdmFsaWQgPSBmYWxzZTtcbiAgICAgICAgfVxuXG4gICAgICAgIGlmICghaXNWYWxpZENWQykge1xuICAgICAgICAgICAgc2VsZi5fc2V0RXJyb3IobG9jYWxzLiRpbnB1dENWQyk7XG4gICAgICAgICAgICB2YWxpZCA9IGZhbHNlO1xuICAgICAgICB9XG5cbiAgICAgICAgaWYgKCFpc1ZhbGlkTmFtZSkge1xuICAgICAgICAgICAgc2VsZi5fc2V0RXJyb3IobG9jYWxzLiRpbnB1dE5hbWUpO1xuICAgICAgICAgICAgdmFsaWQgPSBmYWxzZTtcbiAgICAgICAgfVxuXG4gICAgICAgIGlmICghaXNWYWxpZEFtb3VudCkge1xuICAgICAgICAgICAgdmFsaWQgPSBmYWxzZTtcbiAgICAgICAgICAgIHRoaXMuX3NldEVycm9yKGxvY2Fscy4kaW5wdXRGZWUpO1xuICAgICAgICB9XG5cbiAgICAgICAgcmV0dXJuIHZhbGlkO1xuICAgIH1cblxuICAgIC8vIHN0YXRpY1xuICAgIHN0YXRpYyBwbHVnaW4oc2VsZWN0b3IsIG9wdGlvbnMpIHtcbiAgICAgICAgY29uc3QgJGVsZW1zID0gJChzZWxlY3Rvcik7XG4gICAgICAgIGlmICghJGVsZW1zLmxlbmd0aCkgcmV0dXJuO1xuXG4gICAgICAgIHJldHVybiAkZWxlbXMuZWFjaChmdW5jdGlvbiAoaW5kZXgsIGVsKSB7XG4gICAgICAgICAgICBsZXQgJGVsZW1lbnQgPSAkKGVsKTtcbiAgICAgICAgICAgIGxldCBkYXRhID0gJGVsZW1lbnQuZGF0YSgnd2lkZ2V0LnNjcm9sbHRvJyk7XG5cbiAgICAgICAgICAgIGlmICghZGF0YSkge1xuICAgICAgICAgICAgICAgIGRhdGEgPSBuZXcgV2lkZ2V0KGVsLCBvcHRpb25zKTtcbiAgICAgICAgICAgICAgICAkZWxlbWVudC5kYXRhKCd3aWRnZXQnLCBkYXRhKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgfSlcbiAgICB9XG59XG5cblxuXG5cbi8qKiBXRUJQQUNLIEZPT1RFUiAqKlxuICoqIC4vZnJvbnRlbmQvanMvcGF5bWVudC9wYXltZW50LXdpZGdldHMvX3BheW1lbnQtZm9ybS5qc1xuICoqLyJdLCJzb3VyY2VSb290IjoiIn0=