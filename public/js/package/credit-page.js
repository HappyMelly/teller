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
	
	var _sendCredits = __webpack_require__(23);
	
	var _sendCredits2 = _interopRequireDefault(_sendCredits);
	
	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }
	
	new _filterHistory2.default('.js-credit-history');
	new _sendCredits2.default('.js-form-credit');

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
	
	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }
	
	var FilterHistory = function () {
	    /**
	     * Filter history
	     * @param {String} selector
	     */
	
	    function FilterHistory(selector) {
	        (0, _classCallCheck3.default)(this, FilterHistory);
	
	        this.$root = $(selector);
	        this.locals = this._getDom();
	
	        this._assignEvents();
	    }
	
	    (0, _createClass3.default)(FilterHistory, [{
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
	
	var _formHelper = __webpack_require__(24);
	
	var _formHelper2 = _interopRequireDefault(_formHelper);
	
	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }
	
	/**
	 * Form for sending credit
	 */
	
	var FormCredit = function () {
	    function FormCredit(selector) {
	        (0, _classCallCheck3.default)(this, FormCredit);
	
	        this.$root = $(selector);
	        this.locals = this._getDom();
	        this.validation = new _formHelper2.default(this.$root.find('input'));
	
	        this._assignEvents();
	    }
	
	    (0, _createClass3.default)(FormCredit, [{
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
	
	            this.$root.on('input', 'input', function (e) {
	                return _this.locals.$error.text('');
	            }).on('submit', this._onSubmitForm.bind(this));
	        }
	    }, {
	        key: '_onSubmitForm',
	        value: function _onSubmitForm(e) {
	            var _this2 = this;
	
	            e.preventDefault();
	
	            if (!this._isFormValid()) return false;
	
	            this._sendRequest().done(function () {
	                _this2.validation.clearForm();
	
	                _this2.$root.addClass('b-credits_state_send');
	                setTimeout(function () {
	                    _this2.$root.removeClass('b-credits_state_send');
	                }, 3000);
	            }).fail(function (response) {
	                var data = $.parseJSON(response.responseText).data;
	                var errorText = _this2.validation.getErrorsText(data.errors);
	
	                if (!data.errors) return;
	
	                _this2.locals.$error.text(errorText);
	                _this2.validation.setErrors(data.errors);
	            });
	        }
	    }, {
	        key: '_isFormValid',
	        value: function _isFormValid() {
	            var locals = this.locals;
	            var isEnoughCredits = Number(locals.$value.val()) <= Number(locals.$count.text());
	            var valid = true;
	            var errorText = '';
	
	            if (!this.validation.isValidInputs()) {
	                valid = false;
	                errorText += this.validation.getErrorsText();
	            }
	
	            if (!isEnoughCredits) {
	                valid = false;
	                errorText += 'You cann’t give more than ' + locals.$count.text() + ' credits. ';
	            }
	
	            if (!valid) {
	                locals.$error.text(errorText);
	            }
	
	            return valid;
	        }
	    }, {
	        key: '_sendRequest',
	        value: function _sendRequest() {
	            return $.post(this.$root.attr('action'), {
	                give: this.locals.$value.val(),
	                to: this.locals.$to.val(),
	                message: this.locals.$message.val()
	            });
	        }
	    }]);
	    return FormCredit;
	}();

	exports.default = FormCredit;

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
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIndlYnBhY2s6Ly8vd2VicGFjay9ib290c3RyYXAgNzM2ZWI1M2NlYmQyNDcwY2M4Y2EiLCJ3ZWJwYWNrOi8vLy4vZnJvbnRlbmQvanMvcGVyc29uL2NyZWRpdC1wYWdlLmpzIiwid2VicGFjazovLy8uL2Zyb250ZW5kL2pzL3BlcnNvbi93aWRnZXRzLWNyZWRpdC9fZmlsdGVyLWhpc3RvcnkuanMiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL2hlbHBlcnMvY2xhc3NDYWxsQ2hlY2suanMiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL2hlbHBlcnMvY3JlYXRlQ2xhc3MuanMiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL2NvcmUtanMvb2JqZWN0L2RlZmluZS1wcm9wZXJ0eS5qcyIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvZm4vb2JqZWN0L2RlZmluZS1wcm9wZXJ0eS5qcyIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9lczYub2JqZWN0LmRlZmluZS1wcm9wZXJ0eS5qcyIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZXhwb3J0LmpzIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19nbG9iYWwuanMiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2NvcmUuanMiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2N0eC5qcyIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fYS1mdW5jdGlvbi5qcyIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9faGlkZS5qcyIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fb2JqZWN0LWRwLmpzIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19hbi1vYmplY3QuanMiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2lzLW9iamVjdC5qcyIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9faWU4LWRvbS1kZWZpbmUuanMiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2Rlc2NyaXB0b3JzLmpzIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19mYWlscy5qcyIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZG9tLWNyZWF0ZS5qcyIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fdG8tcHJpbWl0aXZlLmpzIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19wcm9wZXJ0eS1kZXNjLmpzIiwid2VicGFjazovLy8uL2Zyb250ZW5kL2pzL3BlcnNvbi93aWRnZXRzLWNyZWRpdC9fc2VuZC1jcmVkaXRzLmpzIiwid2VicGFjazovLy8uL2Zyb250ZW5kL2pzL2NvbW1vbi9fZm9ybS1oZWxwZXIuanMiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IjtBQUFBO0FBQ0E7O0FBRUE7QUFDQTs7QUFFQTtBQUNBO0FBQ0E7O0FBRUE7QUFDQTtBQUNBLHVCQUFlO0FBQ2Y7QUFDQTtB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QixHQUFsQixFQUY2QjtjQUFmLENBQWxCLENBRFc7Ozs7cUNBT0g7QUFDUixrQkFBSyxPQUFMLENBQWEsSUFBYixDQUFrQixVQUFDLEtBQUQsRUFBUSxFQUFSLEVBQWU7QUFDN0IscUJBQU0sTUFBTSxFQUFFLEVBQUYsQ0FBTixDQUR1QjtBQUU3QixxQkFBSSxDQUFDLElBQUksSUFBSixDQUFTLFVBQVQsQ0FBRCxFQUF3QixJQUFJLEdBQUosQ0FBUSxFQUFSLEVBQTVCO2NBRmMsQ0FBbEIsQ0FEUTs7O1lBcklLIiwiZmlsZSI6ImNyZWRpdC1wYWdlLmpzIiwic291cmNlc0NvbnRlbnQiOlsiIFx0Ly8gVGhlIG1vZHVsZSBjYWNoZVxuIFx0dmFyIGluc3RhbGxlZE1vZHVsZXMgPSB7fTtcblxuIFx0Ly8gVGhlIHJlcXVpcmUgZnVuY3Rpb25cbiBcdGZ1bmN0aW9uIF9fd2VicGFja19yZXF1aXJlX18obW9kdWxlSWQpIHtcblxuIFx0XHQvLyBDaGVjayBpZiBtb2R1bGUgaXMgaW4gY2FjaGVcbiBcdFx0aWYoaW5zdGFsbGVkTW9kdWxlc1ttb2R1bGVJZF0pXG4gXHRcdFx0cmV0dXJuIGluc3RhbGxlZE1vZHVsZXNbbW9kdWxlSWRdLmV4cG9ydHM7XG5cbiBcdFx0Ly8gQ3JlYXRlIGEgbmV3IG1vZHVsZSAoYW5kIHB1dCBpdCBpbnRvIHRoZSBjYWNoZSlcbiBcdFx0dmFyIG1vZHVsZSA9IGluc3RhbGxlZE1vZHVsZXNbbW9kdWxlSWRdID0ge1xuIFx0XHRcdGV4cG9ydHM6IHt9LFxuIFx0XHRcdGlkOiBtb2R1bGVJZCxcbiBcdFx0XHRsb2FkZWQ6IGZhbHNlXG4gXHRcdH07XG5cbiBcdFx0Ly8gRXhlY3V0ZSB0aGUgbW9kdWxlIGZ1bmN0aW9uXG4gXHRcdG1vZHVsZXNbbW9kdWxlSWRdLmNhbGwobW9kdWxlLmV4cG9ydHMsIG1vZHVsZSwgbW9kdWxlLmV4cG9ydHMsIF9fd2VicGFja19yZXF1aXJlX18pO1xuXG4gXHRcdC8vIEZsYWcgdGhlIG1vZHVsZSBhcyBsb2FkZWRcbiBcdFx0bW9kdWxlLmxvYWRlZCA9IHRydWU7XG5cbiBcdFx0Ly8gUmV0dXJuIHRoZSBleHBvcnRzIG9mIHRoZSBtb2R1bGVcbiBcdFx0cmV0dXJuIG1vZHVsZS5leHBvcnRzO1xuIFx0fVxuXG5cbiBcdC8vIGV4cG9zZSB0aGUgbW9kdWxlcyBvYmplY3QgKF9fd2VicGFja19tb2R1bGVzX18pXG4gXHRfX3dlYnBhY2tfcmVxdWlyZV9fLm0gPSBtb2R1bGVzO1xuXG4gXHQvLyBleHBvc2UgdGhlIG1vZHVsZSBjYWNoZVxuIFx0X193ZWJwYWNrX3JlcXVpcmVfXy5jID0gaW5zdGFsbGVkTW9kdWxlcztcblxuIFx0Ly8gX193ZWJwYWNrX3B1YmxpY19wYXRoX19cbiBcdF9fd2VicGFja19yZXF1aXJlX18ucCA9IFwiXCI7XG5cbiBcdC8vIExvYWQgZW50cnkgbW9kdWxlIGFuZCByZXR1cm4gZXhwb3J0c1xuIFx0cmV0dXJuIF9fd2VicGFja19yZXF1aXJlX18oMCk7XG5cblxuXG4vKiogV0VCUEFDSyBGT09URVIgKipcbiAqKiB3ZWJwYWNrL2Jvb3RzdHJhcCA3MzZlYjUzY2ViZDI0NzBjYzhjYVxuICoqLyIsIlxuaW1wb3J0IEZpbHRlckhpc3RvcnkgZnJvbSAnLi93aWRnZXRzLWNyZWRpdC9fZmlsdGVyLWhpc3RvcnknO1xuaW1wb3J0IENyZWRpdEZvcm0gZnJvbSAnLi93aWRnZXRzLWNyZWRpdC9fc2VuZC1jcmVkaXRzJztcblxuXG5uZXcgRmlsdGVySGlzdG9yeSgnLmpzLWNyZWRpdC1oaXN0b3J5Jyk7XG5uZXcgQ3JlZGl0Rm9ybSgnLmpzLWZvcm0tY3JlZGl0Jyk7XG5cblxuXG5cbi8qKiBXRUJQQUNLIEZPT1RFUiAqKlxuICoqIC4vZnJvbnRlbmQvanMvcGVyc29uL2NyZWRpdC1wYWdlLmpzXG4gKiovIiwiJ3VzZSBzdHJpY3QnO1xuXG5leHBvcnQgZGVmYXVsdCBjbGFzcyBGaWx0ZXJIaXN0b3J5IHtcbiAgICAvKipcbiAgICAgKiBGaWx0ZXIgaGlzdG9yeVxuICAgICAqIEBwYXJhbSB7U3RyaW5nfSBzZWxlY3RvclxuICAgICAqL1xuICAgIGNvbnN0cnVjdG9yKHNlbGVjdG9yKSB7XG4gICAgICAgIHRoaXMuJHJvb3QgPSAkKHNlbGVjdG9yKTtcbiAgICAgICAgdGhpcy5sb2NhbHMgPSB0aGlzLl9nZXREb20oKTtcblxuICAgICAgICB0aGlzLl9hc3NpZ25FdmVudHMoKTtcbiAgICB9XG5cbiAgICBfZ2V0RG9tKCkge1xuICAgICAgICBjb25zdCAkcm9vdCA9IHRoaXMuJHJvb3Q7XG5cbiAgICAgICAgcmV0dXJuIHtcbiAgICAgICAgICAgICRsaXN0OiAkcm9vdC5maW5kKCdbZGF0YS1maWx0ZXItbGlzdF0nKSxcbiAgICAgICAgICAgICRpdGVtczogJHJvb3QuZmluZCgnW2RhdGEtZmlsdGVyLXRleHRdJyksXG4gICAgICAgIH07XG4gICAgfVxuXG4gICAgX2Fzc2lnbkV2ZW50cygpIHtcbiAgICAgICAgdGhpcy4kcm9vdC5vbignY2xpY2snLCAnW2RhdGEtZmlsdGVyLWxpbmtdJywgdGhpcy5fb25DbGlja0ZpbHRlci5iaW5kKHRoaXMpKTtcbiAgICB9XG5cbiAgICBfb25DbGlja0ZpbHRlcihlKSB7XG4gICAgICAgIGNvbnN0ICRsaW5rID0gJChlLnRhcmdldCk7XG4gICAgICAgIGNvbnN0IGZpbHRlclRleHQgPSAkbGluay5kYXRhKCdmaWx0ZXItbGluaycpO1xuXG4gICAgICAgIGUucHJldmVudERlZmF1bHQoKTtcblxuICAgICAgICBpZiAoJGxpbmsuaGFzQ2xhc3MoJ3N0YXRlX3NlbGVjdGVkJykpIHJldHVybjtcblxuICAgICAgICB0aGlzLnNldEFjdGl2ZUxpbmsoJGxpbmspO1xuICAgICAgICB0aGlzLmZpbHRlckxpc3QoZmlsdGVyVGV4dCk7XG4gICAgfTtcblxuICAgIC8qKlxuICAgICAqIEZpbHRlciBsaXN0IHRocm91Z2ggdGV4dFxuICAgICAqIEBwYXJhbSB7U3RyaW5nfSBmaWx0ZXJUZXh0XG4gICAgICovXG4gICAgZmlsdGVyTGlzdChmaWx0ZXJUZXh0KSB7XG4gICAgICAgIGNvbnN0ICRpdGVtcyA9IHRoaXMubG9jYWxzLiRpdGVtcztcblxuICAgICAgICBpZiAoZmlsdGVyVGV4dCA9PSAnYWxsJykge1xuICAgICAgICAgICAgJGl0ZW1zLnJlbW92ZUNsYXNzKCdzdGF0ZV9oaWRkZW4nKTtcbiAgICAgICAgICAgIHJldHVybjtcbiAgICAgICAgfVxuXG4gICAgICAgICRpdGVtcy5lYWNoKChpbmRleCwgZWwpID0+IHtcbiAgICAgICAgICAgIGNvbnN0ICRlbCA9ICQoZWwpO1xuICAgICAgICAgICAgY29uc3QgaXNIaWRkZW4gPSAkZWwuZGF0YSgnZmlsdGVyLXRleHQnKS5pbmRleE9mKGZpbHRlclRleHQpID09PSAtMTtcblxuICAgICAgICAgICAgJGVsLnRvZ2dsZUNsYXNzKCdzdGF0ZV9oaWRkZW4nLCBpc0hpZGRlbik7XG4gICAgICAgIH0pO1xuICAgIH07XG5cbiAgICAvKipcbiAgICAgKiBTZXQgbGluayB0byBhY3RpdmUgYW5kIGRlYWN0aXZhdGUgb3RoZXJcbiAgICAgKiBAcGFyYW0ge2pRdWVyeX0gJGVsXG4gICAgICovXG4gICAgc2V0QWN0aXZlTGluaygkZWwpIHtcbiAgICAgICAgJGVsLmFkZENsYXNzKCdzdGF0ZV9zZWxlY3RlZCcpXG4gICAgICAgICAgICAuc2libGluZ3MoKS5yZW1vdmVDbGFzcygnc3RhdGVfc2VsZWN0ZWQnKTtcbiAgICB9O1xufVxuXG5cblxuXG4vKiogV0VCUEFDSyBGT09URVIgKipcbiAqKiAuL2Zyb250ZW5kL2pzL3BlcnNvbi93aWRnZXRzLWNyZWRpdC9fZmlsdGVyLWhpc3RvcnkuanNcbiAqKi8iLCJcInVzZSBzdHJpY3RcIjtcblxuZXhwb3J0cy5fX2VzTW9kdWxlID0gdHJ1ZTtcblxuZXhwb3J0cy5kZWZhdWx0ID0gZnVuY3Rpb24gKGluc3RhbmNlLCBDb25zdHJ1Y3Rvcikge1xuICBpZiAoIShpbnN0YW5jZSBpbnN0YW5jZW9mIENvbnN0cnVjdG9yKSkge1xuICAgIHRocm93IG5ldyBUeXBlRXJyb3IoXCJDYW5ub3QgY2FsbCBhIGNsYXNzIGFzIGEgZnVuY3Rpb25cIik7XG4gIH1cbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9oZWxwZXJzL2NsYXNzQ2FsbENoZWNrLmpzXG4gKiogbW9kdWxlIGlkID0gM1xuICoqIG1vZHVsZSBjaHVua3MgPSAwXG4gKiovIiwiXCJ1c2Ugc3RyaWN0XCI7XG5cbmV4cG9ydHMuX19lc01vZHVsZSA9IHRydWU7XG5cbnZhciBfZGVmaW5lUHJvcGVydHkgPSByZXF1aXJlKFwiYmFiZWwtcnVudGltZS9jb3JlLWpzL29iamVjdC9kZWZpbmUtcHJvcGVydHlcIik7XG5cbnZhciBfZGVmaW5lUHJvcGVydHkyID0gX2ludGVyb3BSZXF1aXJlRGVmYXVsdChfZGVmaW5lUHJvcGVydHkpO1xuXG5mdW5jdGlvbiBfaW50ZXJvcFJlcXVpcmVEZWZhdWx0KG9iaikgeyByZXR1cm4gb2JqICYmIG9iai5fX2VzTW9kdWxlID8gb2JqIDogeyBkZWZhdWx0OiBvYmogfTsgfVxuXG5leHBvcnRzLmRlZmF1bHQgPSBmdW5jdGlvbiAoKSB7XG4gIGZ1bmN0aW9uIGRlZmluZVByb3BlcnRpZXModGFyZ2V0LCBwcm9wcykge1xuICAgIGZvciAodmFyIGkgPSAwOyBpIDwgcHJvcHMubGVuZ3RoOyBpKyspIHtcbiAgICAgIHZhciBkZXNjcmlwdG9yID0gcHJvcHNbaV07XG4gICAgICBkZXNjcmlwdG9yLmVudW1lcmFibGUgPSBkZXNjcmlwdG9yLmVudW1lcmFibGUgfHwgZmFsc2U7XG4gICAgICBkZXNjcmlwdG9yLmNvbmZpZ3VyYWJsZSA9IHRydWU7XG4gICAgICBpZiAoXCJ2YWx1ZVwiIGluIGRlc2NyaXB0b3IpIGRlc2NyaXB0b3Iud3JpdGFibGUgPSB0cnVlO1xuICAgICAgKDAsIF9kZWZpbmVQcm9wZXJ0eTIuZGVmYXVsdCkodGFyZ2V0LCBkZXNjcmlwdG9yLmtleSwgZGVzY3JpcHRvcik7XG4gICAgfVxuICB9XG5cbiAgcmV0dXJuIGZ1bmN0aW9uIChDb25zdHJ1Y3RvciwgcHJvdG9Qcm9wcywgc3RhdGljUHJvcHMpIHtcbiAgICBpZiAocHJvdG9Qcm9wcykgZGVmaW5lUHJvcGVydGllcyhDb25zdHJ1Y3Rvci5wcm90b3R5cGUsIHByb3RvUHJvcHMpO1xuICAgIGlmIChzdGF0aWNQcm9wcykgZGVmaW5lUHJvcGVydGllcyhDb25zdHJ1Y3Rvciwgc3RhdGljUHJvcHMpO1xuICAgIHJldHVybiBDb25zdHJ1Y3RvcjtcbiAgfTtcbn0oKTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL2hlbHBlcnMvY3JlYXRlQ2xhc3MuanNcbiAqKiBtb2R1bGUgaWQgPSA0XG4gKiogbW9kdWxlIGNodW5rcyA9IDBcbiAqKi8iLCJtb2R1bGUuZXhwb3J0cyA9IHsgXCJkZWZhdWx0XCI6IHJlcXVpcmUoXCJjb3JlLWpzL2xpYnJhcnkvZm4vb2JqZWN0L2RlZmluZS1wcm9wZXJ0eVwiKSwgX19lc01vZHVsZTogdHJ1ZSB9O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvY29yZS1qcy9vYmplY3QvZGVmaW5lLXByb3BlcnR5LmpzXG4gKiogbW9kdWxlIGlkID0gNVxuICoqIG1vZHVsZSBjaHVua3MgPSAwXG4gKiovIiwicmVxdWlyZSgnLi4vLi4vbW9kdWxlcy9lczYub2JqZWN0LmRlZmluZS1wcm9wZXJ0eScpO1xudmFyICRPYmplY3QgPSByZXF1aXJlKCcuLi8uLi9tb2R1bGVzL19jb3JlJykuT2JqZWN0O1xubW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbiBkZWZpbmVQcm9wZXJ0eShpdCwga2V5LCBkZXNjKXtcbiAgcmV0dXJuICRPYmplY3QuZGVmaW5lUHJvcGVydHkoaXQsIGtleSwgZGVzYyk7XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvZm4vb2JqZWN0L2RlZmluZS1wcm9wZXJ0eS5qc1xuICoqIG1vZHVsZSBpZCA9IDZcbiAqKiBtb2R1bGUgY2h1bmtzID0gMFxuICoqLyIsInZhciAkZXhwb3J0ID0gcmVxdWlyZSgnLi9fZXhwb3J0Jyk7XHJcbi8vIDE5LjEuMi40IC8gMTUuMi4zLjYgT2JqZWN0LmRlZmluZVByb3BlcnR5KE8sIFAsIEF0dHJpYnV0ZXMpXHJcbiRleHBvcnQoJGV4cG9ydC5TICsgJGV4cG9ydC5GICogIXJlcXVpcmUoJy4vX2Rlc2NyaXB0b3JzJyksICdPYmplY3QnLCB7ZGVmaW5lUHJvcGVydHk6IHJlcXVpcmUoJy4vX29iamVjdC1kcCcpLmZ9KTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvZXM2Lm9iamVjdC5kZWZpbmUtcHJvcGVydHkuanNcbiAqKiBtb2R1bGUgaWQgPSA3XG4gKiogbW9kdWxlIGNodW5rcyA9IDBcbiAqKi8iLCJ2YXIgZ2xvYmFsICAgID0gcmVxdWlyZSgnLi9fZ2xvYmFsJylcbiAgLCBjb3JlICAgICAgPSByZXF1aXJlKCcuL19jb3JlJylcbiAgLCBjdHggICAgICAgPSByZXF1aXJlKCcuL19jdHgnKVxuICAsIGhpZGUgICAgICA9IHJlcXVpcmUoJy4vX2hpZGUnKVxuICAsIFBST1RPVFlQRSA9ICdwcm90b3R5cGUnO1xuXG52YXIgJGV4cG9ydCA9IGZ1bmN0aW9uKHR5cGUsIG5hbWUsIHNvdXJjZSl7XG4gIHZhciBJU19GT1JDRUQgPSB0eXBlICYgJGV4cG9ydC5GXG4gICAgLCBJU19HTE9CQUwgPSB0eXBlICYgJGV4cG9ydC5HXG4gICAgLCBJU19TVEFUSUMgPSB0eXBlICYgJGV4cG9ydC5TXG4gICAgLCBJU19QUk9UTyAgPSB0eXBlICYgJGV4cG9ydC5QXG4gICAgLCBJU19CSU5EICAgPSB0eXBlICYgJGV4cG9ydC5CXG4gICAgLCBJU19XUkFQICAgPSB0eXBlICYgJGV4cG9ydC5XXG4gICAgLCBleHBvcnRzICAgPSBJU19HTE9CQUwgPyBjb3JlIDogY29yZVtuYW1lXSB8fCAoY29yZVtuYW1lXSA9IHt9KVxuICAgICwgZXhwUHJvdG8gID0gZXhwb3J0c1tQUk9UT1RZUEVdXG4gICAgLCB0YXJnZXQgICAgPSBJU19HTE9CQUwgPyBnbG9iYWwgOiBJU19TVEFUSUMgPyBnbG9iYWxbbmFtZV0gOiAoZ2xvYmFsW25hbWVdIHx8IHt9KVtQUk9UT1RZUEVdXG4gICAgLCBrZXksIG93biwgb3V0O1xuICBpZihJU19HTE9CQUwpc291cmNlID0gbmFtZTtcbiAgZm9yKGtleSBpbiBzb3VyY2Upe1xuICAgIC8vIGNvbnRhaW5zIGluIG5hdGl2ZVxuICAgIG93biA9ICFJU19GT1JDRUQgJiYgdGFyZ2V0ICYmIHRhcmdldFtrZXldICE9PSB1bmRlZmluZWQ7XG4gICAgaWYob3duICYmIGtleSBpbiBleHBvcnRzKWNvbnRpbnVlO1xuICAgIC8vIGV4cG9ydCBuYXRpdmUgb3IgcGFzc2VkXG4gICAgb3V0ID0gb3duID8gdGFyZ2V0W2tleV0gOiBzb3VyY2Vba2V5XTtcbiAgICAvLyBwcmV2ZW50IGdsb2JhbCBwb2xsdXRpb24gZm9yIG5hbWVzcGFjZXNcbiAgICBleHBvcnRzW2tleV0gPSBJU19HTE9CQUwgJiYgdHlwZW9mIHRhcmdldFtrZXldICE9ICdmdW5jdGlvbicgPyBzb3VyY2Vba2V5XVxuICAgIC8vIGJpbmQgdGltZXJzIHRvIGdsb2JhbCBmb3IgY2FsbCBmcm9tIGV4cG9ydCBjb250ZXh0XG4gICAgOiBJU19CSU5EICYmIG93biA/IGN0eChvdXQsIGdsb2JhbClcbiAgICAvLyB3cmFwIGdsb2JhbCBjb25zdHJ1Y3RvcnMgZm9yIHByZXZlbnQgY2hhbmdlIHRoZW0gaW4gbGlicmFyeVxuICAgIDogSVNfV1JBUCAmJiB0YXJnZXRba2V5XSA9PSBvdXQgPyAoZnVuY3Rpb24oQyl7XG4gICAgICB2YXIgRiA9IGZ1bmN0aW9uKGEsIGIsIGMpe1xuICAgICAgICBpZih0aGlzIGluc3RhbmNlb2YgQyl7XG4gICAgICAgICAgc3dpdGNoKGFyZ3VtZW50cy5sZW5ndGgpe1xuICAgICAgICAgICAgY2FzZSAwOiByZXR1cm4gbmV3IEM7XG4gICAgICAgICAgICBjYXNlIDE6IHJldHVybiBuZXcgQyhhKTtcbiAgICAgICAgICAgIGNhc2UgMjogcmV0dXJuIG5ldyBDKGEsIGIpO1xuICAgICAgICAgIH0gcmV0dXJuIG5ldyBDKGEsIGIsIGMpO1xuICAgICAgICB9IHJldHVybiBDLmFwcGx5KHRoaXMsIGFyZ3VtZW50cyk7XG4gICAgICB9O1xuICAgICAgRltQUk9UT1RZUEVdID0gQ1tQUk9UT1RZUEVdO1xuICAgICAgcmV0dXJuIEY7XG4gICAgLy8gbWFrZSBzdGF0aWMgdmVyc2lvbnMgZm9yIHByb3RvdHlwZSBtZXRob2RzXG4gICAgfSkob3V0KSA6IElTX1BST1RPICYmIHR5cGVvZiBvdXQgPT0gJ2Z1bmN0aW9uJyA/IGN0eChGdW5jdGlvbi5jYWxsLCBvdXQpIDogb3V0O1xuICAgIC8vIGV4cG9ydCBwcm90byBtZXRob2RzIHRvIGNvcmUuJUNPTlNUUlVDVE9SJS5tZXRob2RzLiVOQU1FJVxuICAgIGlmKElTX1BST1RPKXtcbiAgICAgIChleHBvcnRzLnZpcnR1YWwgfHwgKGV4cG9ydHMudmlydHVhbCA9IHt9KSlba2V5XSA9IG91dDtcbiAgICAgIC8vIGV4cG9ydCBwcm90byBtZXRob2RzIHRvIGNvcmUuJUNPTlNUUlVDVE9SJS5wcm90b3R5cGUuJU5BTUUlXG4gICAgICBpZih0eXBlICYgJGV4cG9ydC5SICYmIGV4cFByb3RvICYmICFleHBQcm90b1trZXldKWhpZGUoZXhwUHJvdG8sIGtleSwgb3V0KTtcbiAgICB9XG4gIH1cbn07XG4vLyB0eXBlIGJpdG1hcFxuJGV4cG9ydC5GID0gMTsgICAvLyBmb3JjZWRcbiRleHBvcnQuRyA9IDI7ICAgLy8gZ2xvYmFsXG4kZXhwb3J0LlMgPSA0OyAgIC8vIHN0YXRpY1xuJGV4cG9ydC5QID0gODsgICAvLyBwcm90b1xuJGV4cG9ydC5CID0gMTY7ICAvLyBiaW5kXG4kZXhwb3J0LlcgPSAzMjsgIC8vIHdyYXBcbiRleHBvcnQuVSA9IDY0OyAgLy8gc2FmZVxuJGV4cG9ydC5SID0gMTI4OyAvLyByZWFsIHByb3RvIG1ldGhvZCBmb3IgYGxpYnJhcnlgIFxubW9kdWxlLmV4cG9ydHMgPSAkZXhwb3J0O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZXhwb3J0LmpzXG4gKiogbW9kdWxlIGlkID0gOFxuICoqIG1vZHVsZSBjaHVua3MgPSAwXG4gKiovIiwiLy8gaHR0cHM6Ly9naXRodWIuY29tL3psb2lyb2NrL2NvcmUtanMvaXNzdWVzLzg2I2lzc3VlY29tbWVudC0xMTU3NTkwMjhcbnZhciBnbG9iYWwgPSBtb2R1bGUuZXhwb3J0cyA9IHR5cGVvZiB3aW5kb3cgIT0gJ3VuZGVmaW5lZCcgJiYgd2luZG93Lk1hdGggPT0gTWF0aFxuICA/IHdpbmRvdyA6IHR5cGVvZiBzZWxmICE9ICd1bmRlZmluZWQnICYmIHNlbGYuTWF0aCA9PSBNYXRoID8gc2VsZiA6IEZ1bmN0aW9uKCdyZXR1cm4gdGhpcycpKCk7XG5pZih0eXBlb2YgX19nID09ICdudW1iZXInKV9fZyA9IGdsb2JhbDsgLy8gZXNsaW50LWRpc2FibGUtbGluZSBuby11bmRlZlxuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZ2xvYmFsLmpzXG4gKiogbW9kdWxlIGlkID0gOVxuICoqIG1vZHVsZSBjaHVua3MgPSAwXG4gKiovIiwidmFyIGNvcmUgPSBtb2R1bGUuZXhwb3J0cyA9IHt2ZXJzaW9uOiAnMi4yLjAnfTtcbmlmKHR5cGVvZiBfX2UgPT0gJ251bWJlcicpX19lID0gY29yZTsgLy8gZXNsaW50LWRpc2FibGUtbGluZSBuby11bmRlZlxuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fY29yZS5qc1xuICoqIG1vZHVsZSBpZCA9IDEwXG4gKiogbW9kdWxlIGNodW5rcyA9IDBcbiAqKi8iLCIvLyBvcHRpb25hbCAvIHNpbXBsZSBjb250ZXh0IGJpbmRpbmdcbnZhciBhRnVuY3Rpb24gPSByZXF1aXJlKCcuL19hLWZ1bmN0aW9uJyk7XG5tb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGZuLCB0aGF0LCBsZW5ndGgpe1xuICBhRnVuY3Rpb24oZm4pO1xuICBpZih0aGF0ID09PSB1bmRlZmluZWQpcmV0dXJuIGZuO1xuICBzd2l0Y2gobGVuZ3RoKXtcbiAgICBjYXNlIDE6IHJldHVybiBmdW5jdGlvbihhKXtcbiAgICAgIHJldHVybiBmbi5jYWxsKHRoYXQsIGEpO1xuICAgIH07XG4gICAgY2FzZSAyOiByZXR1cm4gZnVuY3Rpb24oYSwgYil7XG4gICAgICByZXR1cm4gZm4uY2FsbCh0aGF0LCBhLCBiKTtcbiAgICB9O1xuICAgIGNhc2UgMzogcmV0dXJuIGZ1bmN0aW9uKGEsIGIsIGMpe1xuICAgICAgcmV0dXJuIGZuLmNhbGwodGhhdCwgYSwgYiwgYyk7XG4gICAgfTtcbiAgfVxuICByZXR1cm4gZnVuY3Rpb24oLyogLi4uYXJncyAqLyl7XG4gICAgcmV0dXJuIGZuLmFwcGx5KHRoYXQsIGFyZ3VtZW50cyk7XG4gIH07XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fY3R4LmpzXG4gKiogbW9kdWxlIGlkID0gMTFcbiAqKiBtb2R1bGUgY2h1bmtzID0gMFxuICoqLyIsIm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oaXQpe1xuICBpZih0eXBlb2YgaXQgIT0gJ2Z1bmN0aW9uJyl0aHJvdyBUeXBlRXJyb3IoaXQgKyAnIGlzIG5vdCBhIGZ1bmN0aW9uIScpO1xuICByZXR1cm4gaXQ7XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fYS1mdW5jdGlvbi5qc1xuICoqIG1vZHVsZSBpZCA9IDEyXG4gKiogbW9kdWxlIGNodW5rcyA9IDBcbiAqKi8iLCJ2YXIgZFAgICAgICAgICA9IHJlcXVpcmUoJy4vX29iamVjdC1kcCcpXG4gICwgY3JlYXRlRGVzYyA9IHJlcXVpcmUoJy4vX3Byb3BlcnR5LWRlc2MnKTtcbm1vZHVsZS5leHBvcnRzID0gcmVxdWlyZSgnLi9fZGVzY3JpcHRvcnMnKSA/IGZ1bmN0aW9uKG9iamVjdCwga2V5LCB2YWx1ZSl7XG4gIHJldHVybiBkUC5mKG9iamVjdCwga2V5LCBjcmVhdGVEZXNjKDEsIHZhbHVlKSk7XG59IDogZnVuY3Rpb24ob2JqZWN0LCBrZXksIHZhbHVlKXtcbiAgb2JqZWN0W2tleV0gPSB2YWx1ZTtcbiAgcmV0dXJuIG9iamVjdDtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19oaWRlLmpzXG4gKiogbW9kdWxlIGlkID0gMTNcbiAqKiBtb2R1bGUgY2h1bmtzID0gMFxuICoqLyIsInZhciBhbk9iamVjdCAgICAgICA9IHJlcXVpcmUoJy4vX2FuLW9iamVjdCcpXG4gICwgSUU4X0RPTV9ERUZJTkUgPSByZXF1aXJlKCcuL19pZTgtZG9tLWRlZmluZScpXG4gICwgdG9QcmltaXRpdmUgICAgPSByZXF1aXJlKCcuL190by1wcmltaXRpdmUnKVxuICAsIGRQICAgICAgICAgICAgID0gT2JqZWN0LmRlZmluZVByb3BlcnR5O1xuXG5leHBvcnRzLmYgPSByZXF1aXJlKCcuL19kZXNjcmlwdG9ycycpID8gT2JqZWN0LmRlZmluZVByb3BlcnR5IDogZnVuY3Rpb24gZGVmaW5lUHJvcGVydHkoTywgUCwgQXR0cmlidXRlcyl7XG4gIGFuT2JqZWN0KE8pO1xuICBQID0gdG9QcmltaXRpdmUoUCwgdHJ1ZSk7XG4gIGFuT2JqZWN0KEF0dHJpYnV0ZXMpO1xuICBpZihJRThfRE9NX0RFRklORSl0cnkge1xuICAgIHJldHVybiBkUChPLCBQLCBBdHRyaWJ1dGVzKTtcbiAgfSBjYXRjaChlKXsgLyogZW1wdHkgKi8gfVxuICBpZignZ2V0JyBpbiBBdHRyaWJ1dGVzIHx8ICdzZXQnIGluIEF0dHJpYnV0ZXMpdGhyb3cgVHlwZUVycm9yKCdBY2Nlc3NvcnMgbm90IHN1cHBvcnRlZCEnKTtcbiAgaWYoJ3ZhbHVlJyBpbiBBdHRyaWJ1dGVzKU9bUF0gPSBBdHRyaWJ1dGVzLnZhbHVlO1xuICByZXR1cm4gTztcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19vYmplY3QtZHAuanNcbiAqKiBtb2R1bGUgaWQgPSAxNFxuICoqIG1vZHVsZSBjaHVua3MgPSAwXG4gKiovIiwidmFyIGlzT2JqZWN0ID0gcmVxdWlyZSgnLi9faXMtb2JqZWN0Jyk7XG5tb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGl0KXtcbiAgaWYoIWlzT2JqZWN0KGl0KSl0aHJvdyBUeXBlRXJyb3IoaXQgKyAnIGlzIG5vdCBhbiBvYmplY3QhJyk7XG4gIHJldHVybiBpdDtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19hbi1vYmplY3QuanNcbiAqKiBtb2R1bGUgaWQgPSAxNVxuICoqIG1vZHVsZSBjaHVua3MgPSAwXG4gKiovIiwibW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihpdCl7XG4gIHJldHVybiB0eXBlb2YgaXQgPT09ICdvYmplY3QnID8gaXQgIT09IG51bGwgOiB0eXBlb2YgaXQgPT09ICdmdW5jdGlvbic7XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9faXMtb2JqZWN0LmpzXG4gKiogbW9kdWxlIGlkID0gMTZcbiAqKiBtb2R1bGUgY2h1bmtzID0gMFxuICoqLyIsIm1vZHVsZS5leHBvcnRzID0gIXJlcXVpcmUoJy4vX2Rlc2NyaXB0b3JzJykgJiYgIXJlcXVpcmUoJy4vX2ZhaWxzJykoZnVuY3Rpb24oKXtcclxuICByZXR1cm4gT2JqZWN0LmRlZmluZVByb3BlcnR5KHJlcXVpcmUoJy4vX2RvbS1jcmVhdGUnKSgnZGl2JyksICdhJywge2dldDogZnVuY3Rpb24oKXsgcmV0dXJuIDc7IH19KS5hICE9IDc7XHJcbn0pO1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9faWU4LWRvbS1kZWZpbmUuanNcbiAqKiBtb2R1bGUgaWQgPSAxN1xuICoqIG1vZHVsZSBjaHVua3MgPSAwXG4gKiovIiwiLy8gVGhhbmsncyBJRTggZm9yIGhpcyBmdW5ueSBkZWZpbmVQcm9wZXJ0eVxubW9kdWxlLmV4cG9ydHMgPSAhcmVxdWlyZSgnLi9fZmFpbHMnKShmdW5jdGlvbigpe1xuICByZXR1cm4gT2JqZWN0LmRlZmluZVByb3BlcnR5KHt9LCAnYScsIHtnZXQ6IGZ1bmN0aW9uKCl7IHJldHVybiA3OyB9fSkuYSAhPSA3O1xufSk7XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19kZXNjcmlwdG9ycy5qc1xuICoqIG1vZHVsZSBpZCA9IDE4XG4gKiogbW9kdWxlIGNodW5rcyA9IDBcbiAqKi8iLCJtb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGV4ZWMpe1xuICB0cnkge1xuICAgIHJldHVybiAhIWV4ZWMoKTtcbiAgfSBjYXRjaChlKXtcbiAgICByZXR1cm4gdHJ1ZTtcbiAgfVxufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2ZhaWxzLmpzXG4gKiogbW9kdWxlIGlkID0gMTlcbiAqKiBtb2R1bGUgY2h1bmtzID0gMFxuICoqLyIsInZhciBpc09iamVjdCA9IHJlcXVpcmUoJy4vX2lzLW9iamVjdCcpXG4gICwgZG9jdW1lbnQgPSByZXF1aXJlKCcuL19nbG9iYWwnKS5kb2N1bWVudFxuICAvLyBpbiBvbGQgSUUgdHlwZW9mIGRvY3VtZW50LmNyZWF0ZUVsZW1lbnQgaXMgJ29iamVjdCdcbiAgLCBpcyA9IGlzT2JqZWN0KGRvY3VtZW50KSAmJiBpc09iamVjdChkb2N1bWVudC5jcmVhdGVFbGVtZW50KTtcbm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oaXQpe1xuICByZXR1cm4gaXMgPyBkb2N1bWVudC5jcmVhdGVFbGVtZW50KGl0KSA6IHt9O1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2RvbS1jcmVhdGUuanNcbiAqKiBtb2R1bGUgaWQgPSAyMFxuICoqIG1vZHVsZSBjaHVua3MgPSAwXG4gKiovIiwiLy8gNy4xLjEgVG9QcmltaXRpdmUoaW5wdXQgWywgUHJlZmVycmVkVHlwZV0pXG52YXIgaXNPYmplY3QgPSByZXF1aXJlKCcuL19pcy1vYmplY3QnKTtcbi8vIGluc3RlYWQgb2YgdGhlIEVTNiBzcGVjIHZlcnNpb24sIHdlIGRpZG4ndCBpbXBsZW1lbnQgQEB0b1ByaW1pdGl2ZSBjYXNlXG4vLyBhbmQgdGhlIHNlY29uZCBhcmd1bWVudCAtIGZsYWcgLSBwcmVmZXJyZWQgdHlwZSBpcyBhIHN0cmluZ1xubW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihpdCwgUyl7XG4gIGlmKCFpc09iamVjdChpdCkpcmV0dXJuIGl0O1xuICB2YXIgZm4sIHZhbDtcbiAgaWYoUyAmJiB0eXBlb2YgKGZuID0gaXQudG9TdHJpbmcpID09ICdmdW5jdGlvbicgJiYgIWlzT2JqZWN0KHZhbCA9IGZuLmNhbGwoaXQpKSlyZXR1cm4gdmFsO1xuICBpZih0eXBlb2YgKGZuID0gaXQudmFsdWVPZikgPT0gJ2Z1bmN0aW9uJyAmJiAhaXNPYmplY3QodmFsID0gZm4uY2FsbChpdCkpKXJldHVybiB2YWw7XG4gIGlmKCFTICYmIHR5cGVvZiAoZm4gPSBpdC50b1N0cmluZykgPT0gJ2Z1bmN0aW9uJyAmJiAhaXNPYmplY3QodmFsID0gZm4uY2FsbChpdCkpKXJldHVybiB2YWw7XG4gIHRocm93IFR5cGVFcnJvcihcIkNhbid0IGNvbnZlcnQgb2JqZWN0IHRvIHByaW1pdGl2ZSB2YWx1ZVwiKTtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL190by1wcmltaXRpdmUuanNcbiAqKiBtb2R1bGUgaWQgPSAyMVxuICoqIG1vZHVsZSBjaHVua3MgPSAwXG4gKiovIiwibW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihiaXRtYXAsIHZhbHVlKXtcbiAgcmV0dXJuIHtcbiAgICBlbnVtZXJhYmxlICA6ICEoYml0bWFwICYgMSksXG4gICAgY29uZmlndXJhYmxlOiAhKGJpdG1hcCAmIDIpLFxuICAgIHdyaXRhYmxlICAgIDogIShiaXRtYXAgJiA0KSxcbiAgICB2YWx1ZSAgICAgICA6IHZhbHVlXG4gIH07XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fcHJvcGVydHktZGVzYy5qc1xuICoqIG1vZHVsZSBpZCA9IDIyXG4gKiogbW9kdWxlIGNodW5rcyA9IDBcbiAqKi8iLCIndXNlIHN0cmljdCc7XG5cbmltcG9ydCBGb3JtSGVscGVyIGZyb20gXCIuLy4uLy4uL2NvbW1vbi9fZm9ybS1oZWxwZXJcIjtcblxuLyoqXG4gKiBGb3JtIGZvciBzZW5kaW5nIGNyZWRpdFxuICovXG5leHBvcnQgZGVmYXVsdCBjbGFzcyBGb3JtQ3JlZGl0IHtcblxuICAgIGNvbnN0cnVjdG9yKHNlbGVjdG9yKSB7XG4gICAgICAgIHRoaXMuJHJvb3QgPSAkKHNlbGVjdG9yKTtcbiAgICAgICAgdGhpcy5sb2NhbHMgPSB0aGlzLl9nZXREb20oKTtcbiAgICAgICAgdGhpcy52YWxpZGF0aW9uID0gbmV3IEZvcm1IZWxwZXIodGhpcy4kcm9vdC5maW5kKCdpbnB1dCcpKTtcblxuICAgICAgICB0aGlzLl9hc3NpZ25FdmVudHMoKTtcbiAgICB9XG5cbiAgICBfZ2V0RG9tKCkge1xuICAgICAgICBjb25zdCAkcm9vdCA9IHRoaXMuJHJvb3Q7XG5cbiAgICAgICAgcmV0dXJuIHtcbiAgICAgICAgICAgICRjb3VudDogJHJvb3QuZmluZCgnW2RhdGEtY3JlZGljdC1jb3VudF0nKSxcbiAgICAgICAgICAgICR2YWx1ZTogJHJvb3QuZmluZCgnW2RhdGEtY3JlZGl0LXZhbHVlXScpLFxuICAgICAgICAgICAgJHRvOiAkcm9vdC5maW5kKCdbZGF0YS1jcmVkaXQtdG9dJyksXG4gICAgICAgICAgICAkbWVzc2FnZTogJHJvb3QuZmluZCgnW2RhdGEtY3JlZGl0LW1lc3NhZ2VdJyksXG4gICAgICAgICAgICAkZXJyb3I6ICRyb290LmZpbmQoJ1tkYXRhLWNyZWRpdC1lcnJvcl0nKVxuICAgICAgICB9O1xuICAgIH1cblxuICAgIF9hc3NpZ25FdmVudHMoKSB7XG4gICAgICAgIHRoaXMuJHJvb3RcbiAgICAgICAgICAgIC5vbignaW5wdXQnLCAnaW5wdXQnLCAoZSkgPT4gdGhpcy5sb2NhbHMuJGVycm9yLnRleHQoJycpKVxuICAgICAgICAgICAgLm9uKCdzdWJtaXQnLCB0aGlzLl9vblN1Ym1pdEZvcm0uYmluZCh0aGlzKSk7XG4gICAgfVxuXG4gICAgX29uU3VibWl0Rm9ybShlKSB7XG4gICAgICAgIGUucHJldmVudERlZmF1bHQoKTtcblxuICAgICAgICBpZiAoIXRoaXMuX2lzRm9ybVZhbGlkKCkpIHJldHVybiBmYWxzZTtcblxuICAgICAgICB0aGlzLl9zZW5kUmVxdWVzdCgpXG4gICAgICAgICAgICAuZG9uZSgoKSA9PiB7XG4gICAgICAgICAgICAgICAgdGhpcy52YWxpZGF0aW9uLmNsZWFyRm9ybSgpO1xuXG4gICAgICAgICAgICAgICAgdGhpcy4kcm9vdC5hZGRDbGFzcygnYi1jcmVkaXRzX3N0YXRlX3NlbmQnKTtcbiAgICAgICAgICAgICAgICBzZXRUaW1lb3V0KCgpPT4ge1xuICAgICAgICAgICAgICAgICAgICB0aGlzLiRyb290LnJlbW92ZUNsYXNzKCdiLWNyZWRpdHNfc3RhdGVfc2VuZCcpO1xuICAgICAgICAgICAgICAgIH0sIDMwMDApXG4gICAgICAgICAgICB9KVxuICAgICAgICAgICAgLmZhaWwoKHJlc3BvbnNlKSA9PiB7XG4gICAgICAgICAgICAgICAgY29uc3QgZGF0YSA9ICQucGFyc2VKU09OKHJlc3BvbnNlLnJlc3BvbnNlVGV4dCkuZGF0YTtcbiAgICAgICAgICAgICAgICBjb25zdCBlcnJvclRleHQgPSB0aGlzLnZhbGlkYXRpb24uZ2V0RXJyb3JzVGV4dChkYXRhLmVycm9ycyk7XG5cbiAgICAgICAgICAgICAgICBpZiAoIWRhdGEuZXJyb3JzKSByZXR1cm47XG4gICAgICAgICAgICAgICAgXG4gICAgICAgICAgICAgICAgdGhpcy5sb2NhbHMuJGVycm9yLnRleHQoZXJyb3JUZXh0KTtcbiAgICAgICAgICAgICAgICB0aGlzLnZhbGlkYXRpb24uc2V0RXJyb3JzKGRhdGEuZXJyb3JzKTtcbiAgICAgICAgICAgIH0pXG4gICAgfVxuXG4gICAgX2lzRm9ybVZhbGlkKCkge1xuICAgICAgICBjb25zdCBsb2NhbHMgPSB0aGlzLmxvY2FscztcbiAgICAgICAgY29uc3QgaXNFbm91Z2hDcmVkaXRzID0gTnVtYmVyKGxvY2Fscy4kdmFsdWUudmFsKCkpIDw9IE51bWJlcihsb2NhbHMuJGNvdW50LnRleHQoKSk7XG4gICAgICAgIGxldCB2YWxpZCA9IHRydWU7XG4gICAgICAgIGxldCBlcnJvclRleHQgPSAnJztcblxuICAgICAgICBpZiAoIXRoaXMudmFsaWRhdGlvbi5pc1ZhbGlkSW5wdXRzKCkpIHtcbiAgICAgICAgICAgIHZhbGlkID0gZmFsc2U7XG4gICAgICAgICAgICBlcnJvclRleHQgKz0gdGhpcy52YWxpZGF0aW9uLmdldEVycm9yc1RleHQoKTtcbiAgICAgICAgfVxuXG4gICAgICAgIGlmICghaXNFbm91Z2hDcmVkaXRzKSB7XG4gICAgICAgICAgICB2YWxpZCA9IGZhbHNlO1xuICAgICAgICAgICAgZXJyb3JUZXh0ICs9ICdZb3UgY2FubuKAmXQgZ2l2ZSBtb3JlIHRoYW4gJyArIGxvY2Fscy4kY291bnQudGV4dCgpICsgJyBjcmVkaXRzLiAnO1xuICAgICAgICB9XG5cbiAgICAgICAgaWYgKCF2YWxpZCkge1xuICAgICAgICAgICAgbG9jYWxzLiRlcnJvci50ZXh0KGVycm9yVGV4dCk7XG4gICAgICAgIH1cblxuICAgICAgICByZXR1cm4gdmFsaWQ7XG4gICAgfVxuXG4gICAgX3NlbmRSZXF1ZXN0KCkge1xuICAgICAgICByZXR1cm4gJC5wb3N0KHRoaXMuJHJvb3QuYXR0cignYWN0aW9uJyksXG4gICAgICAgICAgICB7XG4gICAgICAgICAgICAgICAgZ2l2ZTogdGhpcy5sb2NhbHMuJHZhbHVlLnZhbCgpLFxuICAgICAgICAgICAgICAgIHRvOiB0aGlzLmxvY2Fscy4kdG8udmFsKCksXG4gICAgICAgICAgICAgICAgbWVzc2FnZTogdGhpcy5sb2NhbHMuJG1lc3NhZ2UudmFsKClcbiAgICAgICAgICAgIH1cbiAgICAgICAgKTtcbiAgICB9XG59XG5cblxuXG4vKiogV0VCUEFDSyBGT09URVIgKipcbiAqKiAuL2Zyb250ZW5kL2pzL3BlcnNvbi93aWRnZXRzLWNyZWRpdC9fc2VuZC1jcmVkaXRzLmpzXG4gKiovIiwiJ3VzZSBzdHJpY3QnO1xuXG5leHBvcnQgZGVmYXVsdCBjbGFzcyBGb3JtSGVscGVyIHtcbiAgICAvKipcbiAgICAgKiBWYWxpZGF0ZSBmb3JtIHRocm91Z2ggaW5wdXRzXG4gICAgICogQHBhcmFtIHtqUXVlcnl9ICRpbnB1dHNcbiAgICAgKi9cbiAgICBjb25zdHJ1Y3RvcigkaW5wdXRzKSB7XG4gICAgICAgIHRoaXMuJGlucHV0cyA9ICRpbnB1dHM7XG4gICAgICAgIHRoaXMuYXJyRXJyb3JzID0gW107XG4gICAgICAgIHRoaXMuX2Fzc2lnbkV2ZW50cygpO1xuICAgIH1cblxuICAgIF9hc3NpZ25FdmVudHMoKSB7XG4gICAgICAgIHRoaXMuJGlucHV0cy5vbignaW5wdXQnLCAoZSkgPT4gdGhpcy5fcmVtb3ZlRXJyb3IoJChlLnRhcmdldCkpKTtcbiAgICB9XG5cbiAgICBpc1ZhbGlkSW5wdXRzKCkge1xuICAgICAgICBjb25zdCAkaW5wdXRzID0gdGhpcy4kaW5wdXRzO1xuICAgICAgICBsZXQgZXJyb3IgPSAwO1xuXG4gICAgICAgICRpbnB1dHMuZWFjaCgoaW5kZXgsIGlucHV0KSA9PiB7XG4gICAgICAgICAgICBjb25zdCAkaW5wdXQgPSAkKGlucHV0KTtcblxuICAgICAgICAgICAgaWYgKCF0aGlzLl9pc1ZhbGlkSW5wdXQoJGlucHV0KSkgZXJyb3IgKz0gMTtcbiAgICAgICAgfSk7XG4gICAgICAgIHJldHVybiBCb29sZWFuKCFlcnJvcik7XG4gICAgfVxuXG4gICAgLyoqXG4gICAgICogQ2hlY2sgZ2l2ZW4gaW5wdXQsIGlzIGl0IHZhbGlkP1xuICAgICAqIEBwYXJhbSB7alF1ZXJ5fSAkaW5wdXRcbiAgICAgKiBAcmV0dXJucyB7Ym9vbGVhbn0gLSBJcyB2YWxpZCBpbnB1dD9cbiAgICAgKi9cbiAgICBfaXNWYWxpZElucHV0KCRpbnB1dCkge1xuICAgICAgICBjb25zdCB2YWx1ZSA9ICQudHJpbSgkaW5wdXQudmFsKCkpO1xuXG4gICAgICAgIGlmICghdmFsdWUpIHtcbiAgICAgICAgICAgIHRoaXMuX3NldEVycm9yKCRpbnB1dCwgJ0VtcHR5Jyk7XG4gICAgICAgICAgICByZXR1cm4gZmFsc2U7XG4gICAgICAgIH1cblxuICAgICAgICBpZiAoKCRpbnB1dC5oYXNDbGFzcygndHlwZS1lbWFpbCcpKSAmJiAhdGhpcy5faXNWYWxpZEVtYWlsKHZhbHVlKSkge1xuICAgICAgICAgICAgdGhpcy5fc2V0RXJyb3IoJGlucHV0LCAnRW1haWwgaXMgbm90IHZhbGlkJyk7XG4gICAgICAgICAgICByZXR1cm4gZmFsc2U7XG4gICAgICAgIH1cbiAgICAgICAgcmV0dXJuIHRydWU7XG4gICAgfVxuXG4gICAgLyoqXG4gICAgICogSXMgRW1haWwgdmFsaWQ/XG4gICAgICogQHBhcmFtIHtzdHJpbmd9IGVtYWlsXG4gICAgICogQHJldHVybnMge2Jvb2xlYW59XG4gICAgICovXG4gICAgX2lzVmFsaWRFbWFpbChlbWFpbCkge1xuICAgICAgICB2YXIgcmUgPSAvXigoW148PigpXFxbXFxdXFxcXC4sOzpcXHNAXCJdKyhcXC5bXjw+KClcXFtcXF1cXFxcLiw7Olxcc0BcIl0rKSopfChcIi4rXCIpKUAoKFxcW1swLTldezEsM31cXC5bMC05XXsxLDN9XFwuWzAtOV17MSwzfVxcLlswLTldezEsM31dKXwoKFthLXpBLVpcXC0wLTldK1xcLikrW2EtekEtWl17Mix9KSkkLztcbiAgICAgICAgcmV0dXJuIHJlLnRlc3QoZW1haWwpO1xuICAgIH1cblxuICAgIC8qKlxuICAgICAqIFNldCBlcnJvciBmb3IgaW5wdXRcbiAgICAgKiBAcGFyYW0ge2pRdWVyeX0gJGlucHV0XG4gICAgICogQHBhcmFtIHtzdHJpbmd9IGVycm9yVGV4dFxuICAgICAqL1xuICAgIF9zZXRFcnJvcigkaW5wdXQsIGVycm9yVGV4dCkge1xuICAgICAgICBjb25zdCAkcGFyZW50ID0gJGlucHV0LnBhcmVudCgpO1xuICAgICAgICBjb25zdCAkZXJyb3IgPSAkcGFyZW50LmZpbmQoJy5iLWVycm9yJyk7XG5cbiAgICAgICAgaWYgKCRlcnJvci5sZW5ndGgpIHJldHVybjtcblxuICAgICAgICAkcGFyZW50LmFkZENsYXNzKCdiLWVycm9yX3Nob3cnKTtcbiAgICAgICAgJCgnPGRpdiBjbGFzcz1cImItZXJyb3JcIiAvPicpXG4gICAgICAgICAgICAudGV4dChlcnJvclRleHQpXG4gICAgICAgICAgICAucHJlcGVuZFRvKCRwYXJlbnQpO1xuXG4gICAgICAgIHRoaXMuYXJyRXJyb3JzLnB1c2goe1xuICAgICAgICAgICAgbmFtZTogJGlucHV0LmF0dHIoJ25hbWUnKSxcbiAgICAgICAgICAgIGVycm9yOiBlcnJvclRleHRcbiAgICAgICAgfSlcbiAgICB9XG5cbiAgICAvKipcbiAgICAgKiBSZW1vdmUgZXJyb3IgZm9yIGlucHV0XG4gICAgICogQHBhcmFtIHtqUXVlcnl9ICRpbnB1dFxuICAgICAqL1xuICAgIF9yZW1vdmVFcnJvcigkaW5wdXQpIHtcbiAgICAgICAgY29uc3QgJHBhcmVudCA9ICRpbnB1dC5wYXJlbnQoKTtcblxuICAgICAgICAkcGFyZW50XG4gICAgICAgICAgICAucmVtb3ZlQ2xhc3MoJ2ItZXJyb3Jfc2hvdycpXG4gICAgICAgICAgICAuZmluZCgnLmItZXJyb3InKS5yZW1vdmUoKTtcblxuICAgICAgICB0aGlzLmFyckVycm9ycyA9IHRoaXMuYXJyRXJyb3JzLmZpbHRlcihmdW5jdGlvbiAoaXRlbSkge1xuICAgICAgICAgICAgcmV0dXJuIGl0ZW0ubmFtZSAhPT0gJGlucHV0LmF0dHIoJ25hbWUnKVxuICAgICAgICB9KVxuICAgIH1cblxuICAgIC8qKlxuICAgICAqIFNldCBlcnJvcnNcbiAgICAgKiBAcGFyYW0ge0FycmF5fSBlcnJvcnMgLSBbe25hbWU6IFwiZW1haWxcIiwgZXJyb3I6IFwiZW1wdHlcIn0sIHtuYW1lOiBcInBhc3N3b3JkXCIsIGVycm9yOiBcImVtcHR5XCJ9XVxuICAgICAqL1xuICAgIHNldEVycm9ycyhlcnJvcnMpIHtcbiAgICAgICAgZXJyb3JzLmZvckVhY2goKGl0ZW0pID0+IHtcbiAgICAgICAgICAgIGNvbnN0ICRjdXJyZW50SW5wdXQgPSB0aGlzLiRpbnB1dHMuZmlsdGVyKCdbbmFtZT1cIicgKyBpdGVtLm5hbWUgKyAnXCJdJykuZmlyc3QoKTtcblxuICAgICAgICAgICAgaWYgKCRjdXJyZW50SW5wdXQubGVuZ3RoKSB0aGlzLl9zZXRFcnJvcigkY3VycmVudElucHV0LCBpdGVtLmVycm9yKVxuICAgICAgICB9KVxuICAgIH1cblxuICAgIC8qKlxuICAgICAqIEdldCB0eHQgdmVyc2lvbiBvZiBhbGwgZXJyb3JzXG4gICAgICovXG4gICAgZ2V0RXJyb3JzVGV4dChlcnJvcnMpIHtcbiAgICAgICAgY29uc3QgYXJyRXJyb3JzID0gZXJyb3JzIHx8IHRoaXMuYXJyRXJyb3JzO1xuICAgICAgICBsZXQgZXJyb3JUeHQgPSAnJztcblxuICAgICAgICBhcnJFcnJvcnMuZm9yRWFjaCgoaXRlbSkgPT4ge1xuICAgICAgICAgICAgY29uc3QgbmFtZSA9IGl0ZW0ubmFtZVswXS50b1VwcGVyQ2FzZSgpICsgaXRlbS5uYW1lLnN1YnN0cigxKTtcblxuICAgICAgICAgICAgZXJyb3JUeHQgKz0gYCR7bmFtZX0gdmFsdWUgaXMgJHtpdGVtLmVycm9yLnRvTG93ZXJDYXNlKCl9LiBgO1xuICAgICAgICB9KTtcblxuICAgICAgICByZXR1cm4gZXJyb3JUeHQ7XG4gICAgfVxuXG4gICAgLyoqXG4gICAgICogUmVtb3ZlIGFsbCBlcnJvcnNcbiAgICAgKi9cbiAgICByZW1vdmVFcnJvcnMoKSB7XG4gICAgICAgIHRoaXMuJGlucHV0cy5lYWNoKChpbmRleCwgZWwpID0+IHtcbiAgICAgICAgICAgIGNvbnN0ICRlbCA9ICQoZWwpO1xuICAgICAgICAgICAgdGhpcy5fcmVtb3ZlRXJyb3IoJGVsKVxuICAgICAgICB9KVxuICAgIH1cblxuICAgIGNsZWFyRm9ybSgpIHtcbiAgICAgICAgdGhpcy4kaW5wdXRzLmVhY2goKGluZGV4LCBlbCkgPT4ge1xuICAgICAgICAgICAgY29uc3QgJGVsID0gJChlbCk7XG4gICAgICAgICAgICBpZiAoISRlbC5hdHRyKFwiZGlzYWJsZWRcIikpICAkZWwudmFsKCcnKTtcbiAgICAgICAgfSlcbiAgICB9XG59XG5cblxuXG4vKiogV0VCUEFDSyBGT09URVIgKipcbiAqKiAuL2Zyb250ZW5kL2pzL2NvbW1vbi9fZm9ybS1oZWxwZXIuanNcbiAqKi8iXSwic291cmNlUm9vdCI6IiJ9