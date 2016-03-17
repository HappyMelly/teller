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
	
	var _sideMenu = __webpack_require__(2);
	
	var _sideMenu2 = _interopRequireDefault(_sideMenu);
	
	var _filterHistory = __webpack_require__(23);
	
	var _filterHistory2 = _interopRequireDefault(_filterHistory);
	
	var _sendCredits = __webpack_require__(24);
	
	var _sendCredits2 = _interopRequireDefault(_sendCredits);
	
	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }
	
	_sideMenu2.default.interface('.js-sidemenu-tabs');
	_filterHistory2.default.interface('.js-credit-history');
	_sendCredits2.default.interface('.js-form-credit');
	
	$('.js-sidemenu-tabs').on('hmt.menuLoadTab', function () {
	    console.log('tab is loaded');
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
	
	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }
	
	var Widget = function () {
	    function Widget(selector, options) {
	        (0, _classCallCheck3.default)(this, Widget);
	
	        var hashSelector = undefined;
	        var defaultOptions = {
	            hashDefault: ''
	        };
	
	        this.$root = $(selector);
	        this.options = $.extend({}, defaultOptions, options);
	        this.loadedTabs = [];
	
	        var hash = window.location.hash.substring(1) || this.options.hashDefault;
	
	        if (hash) {
	            hashSelector = '[href="#' + hash + '"][data-menuside]';
	            this._showTabByLink($(hashSelector));
	        }
	        this._assignEvents();
	    }
	
	    (0, _createClass3.default)(Widget, [{
	        key: '_assignEvents',
	        value: function _assignEvents() {
	            var _this = this;
	
	            this.$root.on('click', '[data-menuside]', function (e) {
	                var $link = $(e.target);
	
	                _this.showTabByLink($link);
	                e.preventDefault();
	            });
	        }
	
	        /**
	         * 
	         * @param {jQuery} $link - clicked link
	         * @private
	         */
	
	    }, {
	        key: 'showTabByLink',
	        value: function showTabByLink($link) {
	            var _this2 = this;
	
	            var url = $link.attr('data-href');
	            var target = $link.attr('href');
	
	            if ($link.hasClass('active')) return;
	
	            this._loadContentForTab(url, target, function () {
	                _this2.$root.find('[data-menuside]').removeClass('active');
	                $link.addClass('active').tab('show');
	
	                _this2.$root.trigger('hmt.menuLoadTab');
	            });
	        }
	
	        /**
	         * 
	         * @param {String} url              - url of loaded content
	         * @param {jQuery} targetSelector   - div where we should insert content
	         * @param {Function} cb             - callback function
	         */
	
	    }, {
	        key: '_loadContentForTab',
	        value: function _loadContentForTab(url, targetSelector, cb) {
	            var self = this;
	
	            if ($.inArray(targetSelector, self.loadedTabs) < 0 && url) {
	                $.get(url, function (data) {
	                    $(targetSelector).html(data);
	                    self.loadedTabs.push(targetSelector);
	
	                    cb && cb();
	                });
	            } else {
	                cb && cb();
	            }
	        }
	
	        // static
	
	    }], [{
	        key: 'interface',
	        value: function _interface(selector, options) {
	            var $elems = $(selector);
	            if (!$elems.length) return;
	
	            return $elems.each(function (index, el) {
	                var $element = $(el);
	                var data = $element.data('widget');
	
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
	
	var Widget = function () {
	    /**
	     * Filter history
	     * @param {String} selector
	     */
	
	    function Widget(selector) {
	        (0, _classCallCheck3.default)(this, Widget);
	
	        this.$root = $(selector);
	        this.locals = this._getDom();
	
	        this._assignEvents();
	    }
	
	    (0, _createClass3.default)(Widget, [{
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
	    }], [{
	        key: 'interface',
	
	
	        // static
	        value: function _interface(selector) {
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
	
	var _formHelper = __webpack_require__(25);
	
	var _formHelper2 = _interopRequireDefault(_formHelper);
	
	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }
	
	/**
	 * Form for sending credit
	 */
	
	var Widget = function () {
	    function Widget(selector) {
	        (0, _classCallCheck3.default)(this, Widget);
	
	        this.$root = $(selector);
	        this.locals = this._getDom();
	        this.validation = new _formHelper2.default(this.$root.find('input'));
	
	        this._assignEvents();
	    }
	
	    (0, _createClass3.default)(Widget, [{
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
	
	        // static
	
	    }], [{
	        key: 'interface',
	        value: function _interface(selector) {
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
/* 25 */
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
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIndlYnBhY2s6Ly8vd2VicGFjay9ib290c3RyYXAgODEyMTRmZTQxOGMwNjEyNDcxYjYiLCJ3ZWJwYWNrOi8vLy4vZnJvbnRlbmQvanMvcGVyc29uL2NyZWRpdC1wYWdlLmpzIiwid2VicGFjazovLy8uL2Zyb250ZW5kL2pzL2NvbW1vbi9fc2lkZS1tZW51LmpzIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9oZWxwZXJzL2NsYXNzQ2FsbENoZWNrLmpzIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9oZWxwZXJzL2NyZWF0ZUNsYXNzLmpzIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9jb3JlLWpzL29iamVjdC9kZWZpbmUtcHJvcGVydHkuanMiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L2ZuL29iamVjdC9kZWZpbmUtcHJvcGVydHkuanMiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvZXM2Lm9iamVjdC5kZWZpbmUtcHJvcGVydHkuanMiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2V4cG9ydC5qcyIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZ2xvYmFsLmpzIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19jb3JlLmpzIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19jdHguanMiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2EtZnVuY3Rpb24uanMiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2hpZGUuanMiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX29iamVjdC1kcC5qcyIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fYW4tb2JqZWN0LmpzIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19pcy1vYmplY3QuanMiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2llOC1kb20tZGVmaW5lLmpzIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19kZXNjcmlwdG9ycy5qcyIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZmFpbHMuanMiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2RvbS1jcmVhdGUuanMiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX3RvLXByaW1pdGl2ZS5qcyIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fcHJvcGVydHktZGVzYy5qcyIsIndlYnBhY2s6Ly8vLi9mcm9udGVuZC9qcy9wZXJzb24vd2lkZ2V0cy1jcmVkaXQvX2ZpbHRlci1oaXN0b3J5LmpzIiwid2VicGFjazovLy8uL2Zyb250ZW5kL2pzL3BlcnNvbi93aWRnZXRzLWNyZWRpdC9fc2VuZC1jcmVkaXRzLmpzIiwid2VicGFjazovLy8uL2Zyb250ZW5kL2pzL2NvbW1vbi9fZm9ybS1oZWxwZXIuanMiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IjtBQUFBO0FBQ0E7O0FBRUE7QUFDQTs7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OztBQVVsQixvQkFBTyxRQUFQLENBVmtCOzs7Ozs7Ozs7d0NBZ0JQOzs7QUFDWCxrQkFBSyxPQUFMLENBQWEsSUFBYixDQUFrQixVQUFDLEtBQUQsRUFBUSxFQUFSLEVBQWU7QUFDN0IscUJBQU0sTUFBTSxFQUFFLEVBQUYsQ0FBTixDQUR1QjtBQUU3Qix3QkFBSyxZQUFMLENBQWtCLEdBQWxCLEVBRjZCO2NBQWYsQ0FBbEIsQ0FEVzs7OztxQ0FPSDtBQUNSLGtCQUFLLE9BQUwsQ0FBYSxJQUFiLENBQWtCLFVBQUMsS0FBRCxFQUFRLEVBQVIsRUFBZTtBQUM3QixxQkFBTSxNQUFNLEVBQUUsRUFBRixDQUFOLENBRHVCO0FBRTdCLHFCQUFJLENBQUMsSUFBSSxJQUFKLENBQVMsVUFBVCxDQUFELEVBQXdCLElBQUksR0FBSixDQUFRLEVBQVIsRUFBNUI7Y0FGYyxDQUFsQixDQURROzs7WUFySUsiLCJmaWxlIjoiY3JlZGl0LXBhZ2UuanMiLCJzb3VyY2VzQ29udGVudCI6WyIgXHQvLyBUaGUgbW9kdWxlIGNhY2hlXG4gXHR2YXIgaW5zdGFsbGVkTW9kdWxlcyA9IHt9O1xuXG4gXHQvLyBUaGUgcmVxdWlyZSBmdW5jdGlvblxuIFx0ZnVuY3Rpb24gX193ZWJwYWNrX3JlcXVpcmVfXyhtb2R1bGVJZCkge1xuXG4gXHRcdC8vIENoZWNrIGlmIG1vZHVsZSBpcyBpbiBjYWNoZVxuIFx0XHRpZihpbnN0YWxsZWRNb2R1bGVzW21vZHVsZUlkXSlcbiBcdFx0XHRyZXR1cm4gaW5zdGFsbGVkTW9kdWxlc1ttb2R1bGVJZF0uZXhwb3J0cztcblxuIFx0XHQvLyBDcmVhdGUgYSBuZXcgbW9kdWxlIChhbmQgcHV0IGl0IGludG8gdGhlIGNhY2hlKVxuIFx0XHR2YXIgbW9kdWxlID0gaW5zdGFsbGVkTW9kdWxlc1ttb2R1bGVJZF0gPSB7XG4gXHRcdFx0ZXhwb3J0czoge30sXG4gXHRcdFx0aWQ6IG1vZHVsZUlkLFxuIFx0XHRcdGxvYWRlZDogZmFsc2VcbiBcdFx0fTtcblxuIFx0XHQvLyBFeGVjdXRlIHRoZSBtb2R1bGUgZnVuY3Rpb25cbiBcdFx0bW9kdWxlc1ttb2R1bGVJZF0uY2FsbChtb2R1bGUuZXhwb3J0cywgbW9kdWxlLCBtb2R1bGUuZXhwb3J0cywgX193ZWJwYWNrX3JlcXVpcmVfXyk7XG5cbiBcdFx0Ly8gRmxhZyB0aGUgbW9kdWxlIGFzIGxvYWRlZFxuIFx0XHRtb2R1bGUubG9hZGVkID0gdHJ1ZTtcblxuIFx0XHQvLyBSZXR1cm4gdGhlIGV4cG9ydHMgb2YgdGhlIG1vZHVsZVxuIFx0XHRyZXR1cm4gbW9kdWxlLmV4cG9ydHM7XG4gXHR9XG5cblxuIFx0Ly8gZXhwb3NlIHRoZSBtb2R1bGVzIG9iamVjdCAoX193ZWJwYWNrX21vZHVsZXNfXylcbiBcdF9fd2VicGFja19yZXF1aXJlX18ubSA9IG1vZHVsZXM7XG5cbiBcdC8vIGV4cG9zZSB0aGUgbW9kdWxlIGNhY2hlXG4gXHRfX3dlYnBhY2tfcmVxdWlyZV9fLmMgPSBpbnN0YWxsZWRNb2R1bGVzO1xuXG4gXHQvLyBfX3dlYnBhY2tfcHVibGljX3BhdGhfX1xuIFx0X193ZWJwYWNrX3JlcXVpcmVfXy5wID0gXCJcIjtcblxuIFx0Ly8gTG9hZCBlbnRyeSBtb2R1bGUgYW5kIHJldHVybiBleHBvcnRzXG4gXHRyZXR1cm4gX193ZWJwYWNrX3JlcXVpcmVfXygwKTtcblxuXG5cbi8qKiBXRUJQQUNLIEZPT1RFUiAqKlxuICoqIHdlYnBhY2svYm9vdHN0cmFwIDgxMjE0ZmU0MThjMDYxMjQ3MWI2XG4gKiovIiwiXG5pbXBvcnQgU2lkZU1lbnUgZnJvbSAnLi8uLi9jb21tb24vX3NpZGUtbWVudSc7XG5pbXBvcnQgRmlsdGVySGlzdG9yeSBmcm9tICcuL3dpZGdldHMtY3JlZGl0L19maWx0ZXItaGlzdG9yeSc7XG5pbXBvcnQgQ3JlZGl0Rm9ybSBmcm9tICcuL3dpZGdldHMtY3JlZGl0L19zZW5kLWNyZWRpdHMnO1xuXG5cblNpZGVNZW51LmludGVyZmFjZSgnLmpzLXNpZGVtZW51LXRhYnMnKTtcbkZpbHRlckhpc3RvcnkuaW50ZXJmYWNlKCcuanMtY3JlZGl0LWhpc3RvcnknKTtcbkNyZWRpdEZvcm0uaW50ZXJmYWNlKCcuanMtZm9ybS1jcmVkaXQnKTtcblxuJCgnLmpzLXNpZGVtZW51LXRhYnMnKS5vbignaG10Lm1lbnVMb2FkVGFiJywgKCk9PntcbiAgICBjb25zb2xlLmxvZygndGFiIGlzIGxvYWRlZCcpO1xufSk7XG5cblxuXG5cblxuXG4vKiogV0VCUEFDSyBGT09URVIgKipcbiAqKiAuL2Zyb250ZW5kL2pzL3BlcnNvbi9jcmVkaXQtcGFnZS5qc1xuICoqLyIsIid1c2Ugc3RyaWN0JztcblxuZXhwb3J0IGRlZmF1bHQgY2xhc3MgV2lkZ2V0IHtcblxuICAgIGNvbnN0cnVjdG9yKHNlbGVjdG9yLCBvcHRpb25zKSB7XG4gICAgICAgIGxldCBoYXNoU2VsZWN0b3I7XG4gICAgICAgIGxldCBkZWZhdWx0T3B0aW9ucyA9IHtcbiAgICAgICAgICAgICAgICBoYXNoRGVmYXVsdDogJydcbiAgICAgICAgICAgIH07XG4gICAgICAgIFxuICAgICAgICB0aGlzLiRyb290ID0gJChzZWxlY3Rvcik7XG4gICAgICAgIHRoaXMub3B0aW9ucyA9ICQuZXh0ZW5kKHt9LCBkZWZhdWx0T3B0aW9ucywgb3B0aW9ucyk7XG4gICAgICAgIHRoaXMubG9hZGVkVGFicyA9IFtdO1xuXG4gICAgICAgIGxldCBoYXNoID0gd2luZG93LmxvY2F0aW9uLmhhc2guc3Vic3RyaW5nKDEpIHx8IHRoaXMub3B0aW9ucy5oYXNoRGVmYXVsdDtcblxuICAgICAgICBpZiAoaGFzaCl7XG4gICAgICAgICAgICBoYXNoU2VsZWN0b3IgPSAnW2hyZWY9XCIjJysgaGFzaCArJ1wiXVtkYXRhLW1lbnVzaWRlXSc7XG4gICAgICAgICAgICB0aGlzLl9zaG93VGFiQnlMaW5rKCQoaGFzaFNlbGVjdG9yKSk7XG4gICAgICAgIH1cbiAgICAgICAgdGhpcy5fYXNzaWduRXZlbnRzKCk7XG4gICAgfVxuXG4gICAgX2Fzc2lnbkV2ZW50cygpIHtcbiAgICAgICAgdGhpcy4kcm9vdC5vbignY2xpY2snLCAnW2RhdGEtbWVudXNpZGVdJywgKGUpID0+IHtcbiAgICAgICAgICAgIHZhciAkbGluayA9ICQoZS50YXJnZXQpO1xuXG4gICAgICAgICAgICB0aGlzLnNob3dUYWJCeUxpbmsoJGxpbmspO1xuICAgICAgICAgICAgZS5wcmV2ZW50RGVmYXVsdCgpO1xuICAgICAgICB9KVxuICAgIH1cblxuICAgIC8qKlxuICAgICAqIFxuICAgICAqIEBwYXJhbSB7alF1ZXJ5fSAkbGluayAtIGNsaWNrZWQgbGlua1xuICAgICAqIEBwcml2YXRlXG4gICAgICovXG4gICAgc2hvd1RhYkJ5TGluaygkbGluayl7XG4gICAgICAgIGNvbnN0IHVybCA9ICRsaW5rLmF0dHIoJ2RhdGEtaHJlZicpO1xuICAgICAgICBjb25zdCB0YXJnZXQgPSAkbGluay5hdHRyKCdocmVmJyk7XG5cbiAgICAgICAgaWYgKCRsaW5rLmhhc0NsYXNzKCdhY3RpdmUnKSkgcmV0dXJuO1xuXG4gICAgICAgIHRoaXMuX2xvYWRDb250ZW50Rm9yVGFiKHVybCwgdGFyZ2V0LCAoKSA9PiB7XG4gICAgICAgICAgICB0aGlzLiRyb290LmZpbmQoJ1tkYXRhLW1lbnVzaWRlXScpLnJlbW92ZUNsYXNzKCdhY3RpdmUnKTtcbiAgICAgICAgICAgICRsaW5rLmFkZENsYXNzKCdhY3RpdmUnKS50YWIoJ3Nob3cnKTtcblxuICAgICAgICAgICAgdGhpcy4kcm9vdC50cmlnZ2VyKCdobXQubWVudUxvYWRUYWInKTtcbiAgICAgICAgfSlcbiAgICB9XG5cbiAgICAvKipcbiAgICAgKiBcbiAgICAgKiBAcGFyYW0ge1N0cmluZ30gdXJsICAgICAgICAgICAgICAtIHVybCBvZiBsb2FkZWQgY29udGVudFxuICAgICAqIEBwYXJhbSB7alF1ZXJ5fSB0YXJnZXRTZWxlY3RvciAgIC0gZGl2IHdoZXJlIHdlIHNob3VsZCBpbnNlcnQgY29udGVudFxuICAgICAqIEBwYXJhbSB7RnVuY3Rpb259IGNiICAgICAgICAgICAgIC0gY2FsbGJhY2sgZnVuY3Rpb25cbiAgICAgKi9cbiAgICBfbG9hZENvbnRlbnRGb3JUYWIodXJsLCB0YXJnZXRTZWxlY3RvciwgY2Ipe1xuICAgICAgICBjb25zdCBzZWxmID0gdGhpcztcblxuICAgICAgICBpZiAoJC5pbkFycmF5KHRhcmdldFNlbGVjdG9yLCBzZWxmLmxvYWRlZFRhYnMpIDwgMCAmJiB1cmwpIHtcbiAgICAgICAgICAgICQuZ2V0KHVybCwgKGRhdGEpID0+IHtcbiAgICAgICAgICAgICAgICAkKHRhcmdldFNlbGVjdG9yKS5odG1sKGRhdGEpO1xuICAgICAgICAgICAgICAgIHNlbGYubG9hZGVkVGFicy5wdXNoKHRhcmdldFNlbGVjdG9yKTtcblxuICAgICAgICAgICAgICAgIGNiICYmIGNiKCk7XG4gICAgICAgICAgICB9KTtcbiAgICAgICAgfSBlbHNlIHtcbiAgICAgICAgICAgIGNiICYmIGNiKCk7XG4gICAgICAgIH1cbiAgICB9ICAgIFxuXG4gICAgLy8gc3RhdGljXG4gICAgc3RhdGljIGludGVyZmFjZShzZWxlY3Rvciwgb3B0aW9ucykge1xuICAgICAgICBjb25zdCAkZWxlbXMgPSAkKHNlbGVjdG9yKTtcbiAgICAgICAgaWYgKCEkZWxlbXMubGVuZ3RoKSByZXR1cm47XG5cbiAgICAgICAgcmV0dXJuICRlbGVtcy5lYWNoKGZ1bmN0aW9uIChpbmRleCwgZWwpIHtcbiAgICAgICAgICAgIGxldCAkZWxlbWVudCA9ICQoZWwpO1xuICAgICAgICAgICAgbGV0IGRhdGEgICAgID0gJGVsZW1lbnQuZGF0YSgnd2lkZ2V0Jyk7XG5cbiAgICAgICAgICAgIGlmICghZGF0YSkge1xuICAgICAgICAgICAgICAgIGRhdGEgPSBuZXcgV2lkZ2V0KGVsLCBvcHRpb25zKTtcbiAgICAgICAgICAgICAgICAkZWxlbWVudC5kYXRhKCd3aWRnZXQnLCBkYXRhKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgfSlcbiAgICB9XG59XG5cblxuXG5cblxuLyoqIFdFQlBBQ0sgRk9PVEVSICoqXG4gKiogLi9mcm9udGVuZC9qcy9jb21tb24vX3NpZGUtbWVudS5qc1xuICoqLyIsIlwidXNlIHN0cmljdFwiO1xuXG5leHBvcnRzLl9fZXNNb2R1bGUgPSB0cnVlO1xuXG5leHBvcnRzLmRlZmF1bHQgPSBmdW5jdGlvbiAoaW5zdGFuY2UsIENvbnN0cnVjdG9yKSB7XG4gIGlmICghKGluc3RhbmNlIGluc3RhbmNlb2YgQ29uc3RydWN0b3IpKSB7XG4gICAgdGhyb3cgbmV3IFR5cGVFcnJvcihcIkNhbm5vdCBjYWxsIGEgY2xhc3MgYXMgYSBmdW5jdGlvblwiKTtcbiAgfVxufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL2hlbHBlcnMvY2xhc3NDYWxsQ2hlY2suanNcbiAqKiBtb2R1bGUgaWQgPSAzXG4gKiogbW9kdWxlIGNodW5rcyA9IDBcbiAqKi8iLCJcInVzZSBzdHJpY3RcIjtcblxuZXhwb3J0cy5fX2VzTW9kdWxlID0gdHJ1ZTtcblxudmFyIF9kZWZpbmVQcm9wZXJ0eSA9IHJlcXVpcmUoXCJiYWJlbC1ydW50aW1lL2NvcmUtanMvb2JqZWN0L2RlZmluZS1wcm9wZXJ0eVwiKTtcblxudmFyIF9kZWZpbmVQcm9wZXJ0eTIgPSBfaW50ZXJvcFJlcXVpcmVEZWZhdWx0KF9kZWZpbmVQcm9wZXJ0eSk7XG5cbmZ1bmN0aW9uIF9pbnRlcm9wUmVxdWlyZURlZmF1bHQob2JqKSB7IHJldHVybiBvYmogJiYgb2JqLl9fZXNNb2R1bGUgPyBvYmogOiB7IGRlZmF1bHQ6IG9iaiB9OyB9XG5cbmV4cG9ydHMuZGVmYXVsdCA9IGZ1bmN0aW9uICgpIHtcbiAgZnVuY3Rpb24gZGVmaW5lUHJvcGVydGllcyh0YXJnZXQsIHByb3BzKSB7XG4gICAgZm9yICh2YXIgaSA9IDA7IGkgPCBwcm9wcy5sZW5ndGg7IGkrKykge1xuICAgICAgdmFyIGRlc2NyaXB0b3IgPSBwcm9wc1tpXTtcbiAgICAgIGRlc2NyaXB0b3IuZW51bWVyYWJsZSA9IGRlc2NyaXB0b3IuZW51bWVyYWJsZSB8fCBmYWxzZTtcbiAgICAgIGRlc2NyaXB0b3IuY29uZmlndXJhYmxlID0gdHJ1ZTtcbiAgICAgIGlmIChcInZhbHVlXCIgaW4gZGVzY3JpcHRvcikgZGVzY3JpcHRvci53cml0YWJsZSA9IHRydWU7XG4gICAgICAoMCwgX2RlZmluZVByb3BlcnR5Mi5kZWZhdWx0KSh0YXJnZXQsIGRlc2NyaXB0b3Iua2V5LCBkZXNjcmlwdG9yKTtcbiAgICB9XG4gIH1cblxuICByZXR1cm4gZnVuY3Rpb24gKENvbnN0cnVjdG9yLCBwcm90b1Byb3BzLCBzdGF0aWNQcm9wcykge1xuICAgIGlmIChwcm90b1Byb3BzKSBkZWZpbmVQcm9wZXJ0aWVzKENvbnN0cnVjdG9yLnByb3RvdHlwZSwgcHJvdG9Qcm9wcyk7XG4gICAgaWYgKHN0YXRpY1Byb3BzKSBkZWZpbmVQcm9wZXJ0aWVzKENvbnN0cnVjdG9yLCBzdGF0aWNQcm9wcyk7XG4gICAgcmV0dXJuIENvbnN0cnVjdG9yO1xuICB9O1xufSgpO1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvaGVscGVycy9jcmVhdGVDbGFzcy5qc1xuICoqIG1vZHVsZSBpZCA9IDRcbiAqKiBtb2R1bGUgY2h1bmtzID0gMFxuICoqLyIsIm1vZHVsZS5leHBvcnRzID0geyBcImRlZmF1bHRcIjogcmVxdWlyZShcImNvcmUtanMvbGlicmFyeS9mbi9vYmplY3QvZGVmaW5lLXByb3BlcnR5XCIpLCBfX2VzTW9kdWxlOiB0cnVlIH07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9jb3JlLWpzL29iamVjdC9kZWZpbmUtcHJvcGVydHkuanNcbiAqKiBtb2R1bGUgaWQgPSA1XG4gKiogbW9kdWxlIGNodW5rcyA9IDBcbiAqKi8iLCJyZXF1aXJlKCcuLi8uLi9tb2R1bGVzL2VzNi5vYmplY3QuZGVmaW5lLXByb3BlcnR5Jyk7XG52YXIgJE9iamVjdCA9IHJlcXVpcmUoJy4uLy4uL21vZHVsZXMvX2NvcmUnKS5PYmplY3Q7XG5tb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uIGRlZmluZVByb3BlcnR5KGl0LCBrZXksIGRlc2Mpe1xuICByZXR1cm4gJE9iamVjdC5kZWZpbmVQcm9wZXJ0eShpdCwga2V5LCBkZXNjKTtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9mbi9vYmplY3QvZGVmaW5lLXByb3BlcnR5LmpzXG4gKiogbW9kdWxlIGlkID0gNlxuICoqIG1vZHVsZSBjaHVua3MgPSAwXG4gKiovIiwidmFyICRleHBvcnQgPSByZXF1aXJlKCcuL19leHBvcnQnKTtcclxuLy8gMTkuMS4yLjQgLyAxNS4yLjMuNiBPYmplY3QuZGVmaW5lUHJvcGVydHkoTywgUCwgQXR0cmlidXRlcylcclxuJGV4cG9ydCgkZXhwb3J0LlMgKyAkZXhwb3J0LkYgKiAhcmVxdWlyZSgnLi9fZGVzY3JpcHRvcnMnKSwgJ09iamVjdCcsIHtkZWZpbmVQcm9wZXJ0eTogcmVxdWlyZSgnLi9fb2JqZWN0LWRwJykuZn0pO1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9lczYub2JqZWN0LmRlZmluZS1wcm9wZXJ0eS5qc1xuICoqIG1vZHVsZSBpZCA9IDdcbiAqKiBtb2R1bGUgY2h1bmtzID0gMFxuICoqLyIsInZhciBnbG9iYWwgICAgPSByZXF1aXJlKCcuL19nbG9iYWwnKVxuICAsIGNvcmUgICAgICA9IHJlcXVpcmUoJy4vX2NvcmUnKVxuICAsIGN0eCAgICAgICA9IHJlcXVpcmUoJy4vX2N0eCcpXG4gICwgaGlkZSAgICAgID0gcmVxdWlyZSgnLi9faGlkZScpXG4gICwgUFJPVE9UWVBFID0gJ3Byb3RvdHlwZSc7XG5cbnZhciAkZXhwb3J0ID0gZnVuY3Rpb24odHlwZSwgbmFtZSwgc291cmNlKXtcbiAgdmFyIElTX0ZPUkNFRCA9IHR5cGUgJiAkZXhwb3J0LkZcbiAgICAsIElTX0dMT0JBTCA9IHR5cGUgJiAkZXhwb3J0LkdcbiAgICAsIElTX1NUQVRJQyA9IHR5cGUgJiAkZXhwb3J0LlNcbiAgICAsIElTX1BST1RPICA9IHR5cGUgJiAkZXhwb3J0LlBcbiAgICAsIElTX0JJTkQgICA9IHR5cGUgJiAkZXhwb3J0LkJcbiAgICAsIElTX1dSQVAgICA9IHR5cGUgJiAkZXhwb3J0LldcbiAgICAsIGV4cG9ydHMgICA9IElTX0dMT0JBTCA/IGNvcmUgOiBjb3JlW25hbWVdIHx8IChjb3JlW25hbWVdID0ge30pXG4gICAgLCBleHBQcm90byAgPSBleHBvcnRzW1BST1RPVFlQRV1cbiAgICAsIHRhcmdldCAgICA9IElTX0dMT0JBTCA/IGdsb2JhbCA6IElTX1NUQVRJQyA/IGdsb2JhbFtuYW1lXSA6IChnbG9iYWxbbmFtZV0gfHwge30pW1BST1RPVFlQRV1cbiAgICAsIGtleSwgb3duLCBvdXQ7XG4gIGlmKElTX0dMT0JBTClzb3VyY2UgPSBuYW1lO1xuICBmb3Ioa2V5IGluIHNvdXJjZSl7XG4gICAgLy8gY29udGFpbnMgaW4gbmF0aXZlXG4gICAgb3duID0gIUlTX0ZPUkNFRCAmJiB0YXJnZXQgJiYgdGFyZ2V0W2tleV0gIT09IHVuZGVmaW5lZDtcbiAgICBpZihvd24gJiYga2V5IGluIGV4cG9ydHMpY29udGludWU7XG4gICAgLy8gZXhwb3J0IG5hdGl2ZSBvciBwYXNzZWRcbiAgICBvdXQgPSBvd24gPyB0YXJnZXRba2V5XSA6IHNvdXJjZVtrZXldO1xuICAgIC8vIHByZXZlbnQgZ2xvYmFsIHBvbGx1dGlvbiBmb3IgbmFtZXNwYWNlc1xuICAgIGV4cG9ydHNba2V5XSA9IElTX0dMT0JBTCAmJiB0eXBlb2YgdGFyZ2V0W2tleV0gIT0gJ2Z1bmN0aW9uJyA/IHNvdXJjZVtrZXldXG4gICAgLy8gYmluZCB0aW1lcnMgdG8gZ2xvYmFsIGZvciBjYWxsIGZyb20gZXhwb3J0IGNvbnRleHRcbiAgICA6IElTX0JJTkQgJiYgb3duID8gY3R4KG91dCwgZ2xvYmFsKVxuICAgIC8vIHdyYXAgZ2xvYmFsIGNvbnN0cnVjdG9ycyBmb3IgcHJldmVudCBjaGFuZ2UgdGhlbSBpbiBsaWJyYXJ5XG4gICAgOiBJU19XUkFQICYmIHRhcmdldFtrZXldID09IG91dCA/IChmdW5jdGlvbihDKXtcbiAgICAgIHZhciBGID0gZnVuY3Rpb24oYSwgYiwgYyl7XG4gICAgICAgIGlmKHRoaXMgaW5zdGFuY2VvZiBDKXtcbiAgICAgICAgICBzd2l0Y2goYXJndW1lbnRzLmxlbmd0aCl7XG4gICAgICAgICAgICBjYXNlIDA6IHJldHVybiBuZXcgQztcbiAgICAgICAgICAgIGNhc2UgMTogcmV0dXJuIG5ldyBDKGEpO1xuICAgICAgICAgICAgY2FzZSAyOiByZXR1cm4gbmV3IEMoYSwgYik7XG4gICAgICAgICAgfSByZXR1cm4gbmV3IEMoYSwgYiwgYyk7XG4gICAgICAgIH0gcmV0dXJuIEMuYXBwbHkodGhpcywgYXJndW1lbnRzKTtcbiAgICAgIH07XG4gICAgICBGW1BST1RPVFlQRV0gPSBDW1BST1RPVFlQRV07XG4gICAgICByZXR1cm4gRjtcbiAgICAvLyBtYWtlIHN0YXRpYyB2ZXJzaW9ucyBmb3IgcHJvdG90eXBlIG1ldGhvZHNcbiAgICB9KShvdXQpIDogSVNfUFJPVE8gJiYgdHlwZW9mIG91dCA9PSAnZnVuY3Rpb24nID8gY3R4KEZ1bmN0aW9uLmNhbGwsIG91dCkgOiBvdXQ7XG4gICAgLy8gZXhwb3J0IHByb3RvIG1ldGhvZHMgdG8gY29yZS4lQ09OU1RSVUNUT1IlLm1ldGhvZHMuJU5BTUUlXG4gICAgaWYoSVNfUFJPVE8pe1xuICAgICAgKGV4cG9ydHMudmlydHVhbCB8fCAoZXhwb3J0cy52aXJ0dWFsID0ge30pKVtrZXldID0gb3V0O1xuICAgICAgLy8gZXhwb3J0IHByb3RvIG1ldGhvZHMgdG8gY29yZS4lQ09OU1RSVUNUT1IlLnByb3RvdHlwZS4lTkFNRSVcbiAgICAgIGlmKHR5cGUgJiAkZXhwb3J0LlIgJiYgZXhwUHJvdG8gJiYgIWV4cFByb3RvW2tleV0paGlkZShleHBQcm90bywga2V5LCBvdXQpO1xuICAgIH1cbiAgfVxufTtcbi8vIHR5cGUgYml0bWFwXG4kZXhwb3J0LkYgPSAxOyAgIC8vIGZvcmNlZFxuJGV4cG9ydC5HID0gMjsgICAvLyBnbG9iYWxcbiRleHBvcnQuUyA9IDQ7ICAgLy8gc3RhdGljXG4kZXhwb3J0LlAgPSA4OyAgIC8vIHByb3RvXG4kZXhwb3J0LkIgPSAxNjsgIC8vIGJpbmRcbiRleHBvcnQuVyA9IDMyOyAgLy8gd3JhcFxuJGV4cG9ydC5VID0gNjQ7ICAvLyBzYWZlXG4kZXhwb3J0LlIgPSAxMjg7IC8vIHJlYWwgcHJvdG8gbWV0aG9kIGZvciBgbGlicmFyeWAgXG5tb2R1bGUuZXhwb3J0cyA9ICRleHBvcnQ7XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19leHBvcnQuanNcbiAqKiBtb2R1bGUgaWQgPSA4XG4gKiogbW9kdWxlIGNodW5rcyA9IDBcbiAqKi8iLCIvLyBodHRwczovL2dpdGh1Yi5jb20vemxvaXJvY2svY29yZS1qcy9pc3N1ZXMvODYjaXNzdWVjb21tZW50LTExNTc1OTAyOFxudmFyIGdsb2JhbCA9IG1vZHVsZS5leHBvcnRzID0gdHlwZW9mIHdpbmRvdyAhPSAndW5kZWZpbmVkJyAmJiB3aW5kb3cuTWF0aCA9PSBNYXRoXG4gID8gd2luZG93IDogdHlwZW9mIHNlbGYgIT0gJ3VuZGVmaW5lZCcgJiYgc2VsZi5NYXRoID09IE1hdGggPyBzZWxmIDogRnVuY3Rpb24oJ3JldHVybiB0aGlzJykoKTtcbmlmKHR5cGVvZiBfX2cgPT0gJ251bWJlcicpX19nID0gZ2xvYmFsOyAvLyBlc2xpbnQtZGlzYWJsZS1saW5lIG5vLXVuZGVmXG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19nbG9iYWwuanNcbiAqKiBtb2R1bGUgaWQgPSA5XG4gKiogbW9kdWxlIGNodW5rcyA9IDBcbiAqKi8iLCJ2YXIgY29yZSA9IG1vZHVsZS5leHBvcnRzID0ge3ZlcnNpb246ICcyLjIuMCd9O1xuaWYodHlwZW9mIF9fZSA9PSAnbnVtYmVyJylfX2UgPSBjb3JlOyAvLyBlc2xpbnQtZGlzYWJsZS1saW5lIG5vLXVuZGVmXG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19jb3JlLmpzXG4gKiogbW9kdWxlIGlkID0gMTBcbiAqKiBtb2R1bGUgY2h1bmtzID0gMFxuICoqLyIsIi8vIG9wdGlvbmFsIC8gc2ltcGxlIGNvbnRleHQgYmluZGluZ1xudmFyIGFGdW5jdGlvbiA9IHJlcXVpcmUoJy4vX2EtZnVuY3Rpb24nKTtcbm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oZm4sIHRoYXQsIGxlbmd0aCl7XG4gIGFGdW5jdGlvbihmbik7XG4gIGlmKHRoYXQgPT09IHVuZGVmaW5lZClyZXR1cm4gZm47XG4gIHN3aXRjaChsZW5ndGgpe1xuICAgIGNhc2UgMTogcmV0dXJuIGZ1bmN0aW9uKGEpe1xuICAgICAgcmV0dXJuIGZuLmNhbGwodGhhdCwgYSk7XG4gICAgfTtcbiAgICBjYXNlIDI6IHJldHVybiBmdW5jdGlvbihhLCBiKXtcbiAgICAgIHJldHVybiBmbi5jYWxsKHRoYXQsIGEsIGIpO1xuICAgIH07XG4gICAgY2FzZSAzOiByZXR1cm4gZnVuY3Rpb24oYSwgYiwgYyl7XG4gICAgICByZXR1cm4gZm4uY2FsbCh0aGF0LCBhLCBiLCBjKTtcbiAgICB9O1xuICB9XG4gIHJldHVybiBmdW5jdGlvbigvKiAuLi5hcmdzICovKXtcbiAgICByZXR1cm4gZm4uYXBwbHkodGhhdCwgYXJndW1lbnRzKTtcbiAgfTtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19jdHguanNcbiAqKiBtb2R1bGUgaWQgPSAxMVxuICoqIG1vZHVsZSBjaHVua3MgPSAwXG4gKiovIiwibW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihpdCl7XG4gIGlmKHR5cGVvZiBpdCAhPSAnZnVuY3Rpb24nKXRocm93IFR5cGVFcnJvcihpdCArICcgaXMgbm90IGEgZnVuY3Rpb24hJyk7XG4gIHJldHVybiBpdDtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19hLWZ1bmN0aW9uLmpzXG4gKiogbW9kdWxlIGlkID0gMTJcbiAqKiBtb2R1bGUgY2h1bmtzID0gMFxuICoqLyIsInZhciBkUCAgICAgICAgID0gcmVxdWlyZSgnLi9fb2JqZWN0LWRwJylcbiAgLCBjcmVhdGVEZXNjID0gcmVxdWlyZSgnLi9fcHJvcGVydHktZGVzYycpO1xubW9kdWxlLmV4cG9ydHMgPSByZXF1aXJlKCcuL19kZXNjcmlwdG9ycycpID8gZnVuY3Rpb24ob2JqZWN0LCBrZXksIHZhbHVlKXtcbiAgcmV0dXJuIGRQLmYob2JqZWN0LCBrZXksIGNyZWF0ZURlc2MoMSwgdmFsdWUpKTtcbn0gOiBmdW5jdGlvbihvYmplY3QsIGtleSwgdmFsdWUpe1xuICBvYmplY3Rba2V5XSA9IHZhbHVlO1xuICByZXR1cm4gb2JqZWN0O1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2hpZGUuanNcbiAqKiBtb2R1bGUgaWQgPSAxM1xuICoqIG1vZHVsZSBjaHVua3MgPSAwXG4gKiovIiwidmFyIGFuT2JqZWN0ICAgICAgID0gcmVxdWlyZSgnLi9fYW4tb2JqZWN0JylcbiAgLCBJRThfRE9NX0RFRklORSA9IHJlcXVpcmUoJy4vX2llOC1kb20tZGVmaW5lJylcbiAgLCB0b1ByaW1pdGl2ZSAgICA9IHJlcXVpcmUoJy4vX3RvLXByaW1pdGl2ZScpXG4gICwgZFAgICAgICAgICAgICAgPSBPYmplY3QuZGVmaW5lUHJvcGVydHk7XG5cbmV4cG9ydHMuZiA9IHJlcXVpcmUoJy4vX2Rlc2NyaXB0b3JzJykgPyBPYmplY3QuZGVmaW5lUHJvcGVydHkgOiBmdW5jdGlvbiBkZWZpbmVQcm9wZXJ0eShPLCBQLCBBdHRyaWJ1dGVzKXtcbiAgYW5PYmplY3QoTyk7XG4gIFAgPSB0b1ByaW1pdGl2ZShQLCB0cnVlKTtcbiAgYW5PYmplY3QoQXR0cmlidXRlcyk7XG4gIGlmKElFOF9ET01fREVGSU5FKXRyeSB7XG4gICAgcmV0dXJuIGRQKE8sIFAsIEF0dHJpYnV0ZXMpO1xuICB9IGNhdGNoKGUpeyAvKiBlbXB0eSAqLyB9XG4gIGlmKCdnZXQnIGluIEF0dHJpYnV0ZXMgfHwgJ3NldCcgaW4gQXR0cmlidXRlcyl0aHJvdyBUeXBlRXJyb3IoJ0FjY2Vzc29ycyBub3Qgc3VwcG9ydGVkIScpO1xuICBpZigndmFsdWUnIGluIEF0dHJpYnV0ZXMpT1tQXSA9IEF0dHJpYnV0ZXMudmFsdWU7XG4gIHJldHVybiBPO1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX29iamVjdC1kcC5qc1xuICoqIG1vZHVsZSBpZCA9IDE0XG4gKiogbW9kdWxlIGNodW5rcyA9IDBcbiAqKi8iLCJ2YXIgaXNPYmplY3QgPSByZXF1aXJlKCcuL19pcy1vYmplY3QnKTtcbm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oaXQpe1xuICBpZighaXNPYmplY3QoaXQpKXRocm93IFR5cGVFcnJvcihpdCArICcgaXMgbm90IGFuIG9iamVjdCEnKTtcbiAgcmV0dXJuIGl0O1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2FuLW9iamVjdC5qc1xuICoqIG1vZHVsZSBpZCA9IDE1XG4gKiogbW9kdWxlIGNodW5rcyA9IDBcbiAqKi8iLCJtb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGl0KXtcbiAgcmV0dXJuIHR5cGVvZiBpdCA9PT0gJ29iamVjdCcgPyBpdCAhPT0gbnVsbCA6IHR5cGVvZiBpdCA9PT0gJ2Z1bmN0aW9uJztcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19pcy1vYmplY3QuanNcbiAqKiBtb2R1bGUgaWQgPSAxNlxuICoqIG1vZHVsZSBjaHVua3MgPSAwXG4gKiovIiwibW9kdWxlLmV4cG9ydHMgPSAhcmVxdWlyZSgnLi9fZGVzY3JpcHRvcnMnKSAmJiAhcmVxdWlyZSgnLi9fZmFpbHMnKShmdW5jdGlvbigpe1xyXG4gIHJldHVybiBPYmplY3QuZGVmaW5lUHJvcGVydHkocmVxdWlyZSgnLi9fZG9tLWNyZWF0ZScpKCdkaXYnKSwgJ2EnLCB7Z2V0OiBmdW5jdGlvbigpeyByZXR1cm4gNzsgfX0pLmEgIT0gNztcclxufSk7XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19pZTgtZG9tLWRlZmluZS5qc1xuICoqIG1vZHVsZSBpZCA9IDE3XG4gKiogbW9kdWxlIGNodW5rcyA9IDBcbiAqKi8iLCIvLyBUaGFuaydzIElFOCBmb3IgaGlzIGZ1bm55IGRlZmluZVByb3BlcnR5XG5tb2R1bGUuZXhwb3J0cyA9ICFyZXF1aXJlKCcuL19mYWlscycpKGZ1bmN0aW9uKCl7XG4gIHJldHVybiBPYmplY3QuZGVmaW5lUHJvcGVydHkoe30sICdhJywge2dldDogZnVuY3Rpb24oKXsgcmV0dXJuIDc7IH19KS5hICE9IDc7XG59KTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2Rlc2NyaXB0b3JzLmpzXG4gKiogbW9kdWxlIGlkID0gMThcbiAqKiBtb2R1bGUgY2h1bmtzID0gMFxuICoqLyIsIm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oZXhlYyl7XG4gIHRyeSB7XG4gICAgcmV0dXJuICEhZXhlYygpO1xuICB9IGNhdGNoKGUpe1xuICAgIHJldHVybiB0cnVlO1xuICB9XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZmFpbHMuanNcbiAqKiBtb2R1bGUgaWQgPSAxOVxuICoqIG1vZHVsZSBjaHVua3MgPSAwXG4gKiovIiwidmFyIGlzT2JqZWN0ID0gcmVxdWlyZSgnLi9faXMtb2JqZWN0JylcbiAgLCBkb2N1bWVudCA9IHJlcXVpcmUoJy4vX2dsb2JhbCcpLmRvY3VtZW50XG4gIC8vIGluIG9sZCBJRSB0eXBlb2YgZG9jdW1lbnQuY3JlYXRlRWxlbWVudCBpcyAnb2JqZWN0J1xuICAsIGlzID0gaXNPYmplY3QoZG9jdW1lbnQpICYmIGlzT2JqZWN0KGRvY3VtZW50LmNyZWF0ZUVsZW1lbnQpO1xubW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihpdCl7XG4gIHJldHVybiBpcyA/IGRvY3VtZW50LmNyZWF0ZUVsZW1lbnQoaXQpIDoge307XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZG9tLWNyZWF0ZS5qc1xuICoqIG1vZHVsZSBpZCA9IDIwXG4gKiogbW9kdWxlIGNodW5rcyA9IDBcbiAqKi8iLCIvLyA3LjEuMSBUb1ByaW1pdGl2ZShpbnB1dCBbLCBQcmVmZXJyZWRUeXBlXSlcbnZhciBpc09iamVjdCA9IHJlcXVpcmUoJy4vX2lzLW9iamVjdCcpO1xuLy8gaW5zdGVhZCBvZiB0aGUgRVM2IHNwZWMgdmVyc2lvbiwgd2UgZGlkbid0IGltcGxlbWVudCBAQHRvUHJpbWl0aXZlIGNhc2Vcbi8vIGFuZCB0aGUgc2Vjb25kIGFyZ3VtZW50IC0gZmxhZyAtIHByZWZlcnJlZCB0eXBlIGlzIGEgc3RyaW5nXG5tb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGl0LCBTKXtcbiAgaWYoIWlzT2JqZWN0KGl0KSlyZXR1cm4gaXQ7XG4gIHZhciBmbiwgdmFsO1xuICBpZihTICYmIHR5cGVvZiAoZm4gPSBpdC50b1N0cmluZykgPT0gJ2Z1bmN0aW9uJyAmJiAhaXNPYmplY3QodmFsID0gZm4uY2FsbChpdCkpKXJldHVybiB2YWw7XG4gIGlmKHR5cGVvZiAoZm4gPSBpdC52YWx1ZU9mKSA9PSAnZnVuY3Rpb24nICYmICFpc09iamVjdCh2YWwgPSBmbi5jYWxsKGl0KSkpcmV0dXJuIHZhbDtcbiAgaWYoIVMgJiYgdHlwZW9mIChmbiA9IGl0LnRvU3RyaW5nKSA9PSAnZnVuY3Rpb24nICYmICFpc09iamVjdCh2YWwgPSBmbi5jYWxsKGl0KSkpcmV0dXJuIHZhbDtcbiAgdGhyb3cgVHlwZUVycm9yKFwiQ2FuJ3QgY29udmVydCBvYmplY3QgdG8gcHJpbWl0aXZlIHZhbHVlXCIpO1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX3RvLXByaW1pdGl2ZS5qc1xuICoqIG1vZHVsZSBpZCA9IDIxXG4gKiogbW9kdWxlIGNodW5rcyA9IDBcbiAqKi8iLCJtb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGJpdG1hcCwgdmFsdWUpe1xuICByZXR1cm4ge1xuICAgIGVudW1lcmFibGUgIDogIShiaXRtYXAgJiAxKSxcbiAgICBjb25maWd1cmFibGU6ICEoYml0bWFwICYgMiksXG4gICAgd3JpdGFibGUgICAgOiAhKGJpdG1hcCAmIDQpLFxuICAgIHZhbHVlICAgICAgIDogdmFsdWVcbiAgfTtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19wcm9wZXJ0eS1kZXNjLmpzXG4gKiogbW9kdWxlIGlkID0gMjJcbiAqKiBtb2R1bGUgY2h1bmtzID0gMFxuICoqLyIsIid1c2Ugc3RyaWN0JztcblxuXG5leHBvcnQgZGVmYXVsdCBjbGFzcyBXaWRnZXQge1xuICAgIC8qKlxuICAgICAqIEZpbHRlciBoaXN0b3J5XG4gICAgICogQHBhcmFtIHtTdHJpbmd9IHNlbGVjdG9yXG4gICAgICovXG4gICAgY29uc3RydWN0b3Ioc2VsZWN0b3IpIHtcbiAgICAgICAgdGhpcy4kcm9vdCA9ICQoc2VsZWN0b3IpO1xuICAgICAgICB0aGlzLmxvY2FscyA9IHRoaXMuX2dldERvbSgpO1xuXG4gICAgICAgIHRoaXMuX2Fzc2lnbkV2ZW50cygpO1xuICAgIH1cblxuICAgIF9nZXREb20oKSB7XG4gICAgICAgIGNvbnN0ICRyb290ID0gdGhpcy4kcm9vdDtcblxuICAgICAgICByZXR1cm4ge1xuICAgICAgICAgICAgJGxpc3Q6ICRyb290LmZpbmQoJ1tkYXRhLWZpbHRlci1saXN0XScpLFxuICAgICAgICAgICAgJGl0ZW1zOiAkcm9vdC5maW5kKCdbZGF0YS1maWx0ZXItdGV4dF0nKSxcbiAgICAgICAgfTtcbiAgICB9XG5cbiAgICBfYXNzaWduRXZlbnRzKCkge1xuICAgICAgICB0aGlzLiRyb290Lm9uKCdjbGljaycsICdbZGF0YS1maWx0ZXItbGlua10nLCB0aGlzLl9vbkNsaWNrRmlsdGVyLmJpbmQodGhpcykpO1xuICAgIH1cblxuICAgIF9vbkNsaWNrRmlsdGVyKGUpIHtcbiAgICAgICAgY29uc3QgJGxpbmsgPSAkKGUudGFyZ2V0KTtcbiAgICAgICAgY29uc3QgZmlsdGVyVGV4dCA9ICRsaW5rLmRhdGEoJ2ZpbHRlci1saW5rJyk7XG5cbiAgICAgICAgZS5wcmV2ZW50RGVmYXVsdCgpO1xuXG4gICAgICAgIGlmICgkbGluay5oYXNDbGFzcygnc3RhdGVfc2VsZWN0ZWQnKSkgcmV0dXJuO1xuXG4gICAgICAgIHRoaXMuc2V0QWN0aXZlTGluaygkbGluayk7XG4gICAgICAgIHRoaXMuZmlsdGVyTGlzdChmaWx0ZXJUZXh0KTtcbiAgICB9O1xuXG4gICAgLyoqXG4gICAgICogRmlsdGVyIGxpc3QgdGhyb3VnaCB0ZXh0XG4gICAgICogQHBhcmFtIHtTdHJpbmd9IGZpbHRlclRleHRcbiAgICAgKi9cbiAgICBmaWx0ZXJMaXN0KGZpbHRlclRleHQpIHtcbiAgICAgICAgY29uc3QgJGl0ZW1zID0gdGhpcy5sb2NhbHMuJGl0ZW1zO1xuXG4gICAgICAgIGlmIChmaWx0ZXJUZXh0ID09ICdhbGwnKSB7XG4gICAgICAgICAgICAkaXRlbXMucmVtb3ZlQ2xhc3MoJ3N0YXRlX2hpZGRlbicpO1xuICAgICAgICAgICAgcmV0dXJuO1xuICAgICAgICB9XG5cbiAgICAgICAgJGl0ZW1zLmVhY2goKGluZGV4LCBlbCkgPT4ge1xuICAgICAgICAgICAgY29uc3QgJGVsID0gJChlbCk7XG4gICAgICAgICAgICBjb25zdCBpc0hpZGRlbiA9ICRlbC5kYXRhKCdmaWx0ZXItdGV4dCcpLmluZGV4T2YoZmlsdGVyVGV4dCkgPT09IC0xO1xuXG4gICAgICAgICAgICAkZWwudG9nZ2xlQ2xhc3MoJ3N0YXRlX2hpZGRlbicsIGlzSGlkZGVuKTtcbiAgICAgICAgfSk7XG4gICAgfTtcblxuICAgIC8qKlxuICAgICAqIFNldCBsaW5rIHRvIGFjdGl2ZSBhbmQgZGVhY3RpdmF0ZSBvdGhlclxuICAgICAqIEBwYXJhbSB7alF1ZXJ5fSAkZWxcbiAgICAgKi9cbiAgICBzZXRBY3RpdmVMaW5rKCRlbCkge1xuICAgICAgICAkZWwuYWRkQ2xhc3MoJ3N0YXRlX3NlbGVjdGVkJylcbiAgICAgICAgICAgIC5zaWJsaW5ncygpLnJlbW92ZUNsYXNzKCdzdGF0ZV9zZWxlY3RlZCcpO1xuICAgIH07XG4gICAgXG4gICAgLy8gc3RhdGljXG4gICAgc3RhdGljIGludGVyZmFjZShzZWxlY3Rvcikge1xuICAgICAgICBjb25zdCAkZWxlbXMgPSAkKHNlbGVjdG9yKTtcbiAgICAgICAgaWYgKCEkZWxlbXMubGVuZ3RoKSByZXR1cm47XG5cbiAgICAgICAgcmV0dXJuICRlbGVtcy5lYWNoKGZ1bmN0aW9uIChpbmRleCwgZWwpIHtcbiAgICAgICAgICAgIGxldCAkZWxlbWVudCA9ICQoZWwpO1xuICAgICAgICAgICAgbGV0IGRhdGEgICAgID0gJGVsZW1lbnQuZGF0YSgnd2lkZ2V0Jyk7XG5cbiAgICAgICAgICAgIGlmICghZGF0YSkge1xuICAgICAgICAgICAgICAgIGRhdGEgPSBuZXcgV2lkZ2V0KGVsKTtcbiAgICAgICAgICAgICAgICAkZWxlbWVudC5kYXRhKCd3aWRnZXQnLCBkYXRhKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgfSlcbiAgICB9XG59XG5cblxuXG5cblxuLyoqIFdFQlBBQ0sgRk9PVEVSICoqXG4gKiogLi9mcm9udGVuZC9qcy9wZXJzb24vd2lkZ2V0cy1jcmVkaXQvX2ZpbHRlci1oaXN0b3J5LmpzXG4gKiovIiwiJ3VzZSBzdHJpY3QnO1xuXG5pbXBvcnQgRm9ybUhlbHBlciBmcm9tIFwiLi8uLi8uLi9jb21tb24vX2Zvcm0taGVscGVyXCI7XG5cbi8qKlxuICogRm9ybSBmb3Igc2VuZGluZyBjcmVkaXRcbiAqL1xuZXhwb3J0IGRlZmF1bHQgY2xhc3MgV2lkZ2V0e1xuXG4gICAgY29uc3RydWN0b3Ioc2VsZWN0b3IpIHtcbiAgICAgICAgdGhpcy4kcm9vdCA9ICQoc2VsZWN0b3IpO1xuICAgICAgICB0aGlzLmxvY2FscyA9IHRoaXMuX2dldERvbSgpO1xuICAgICAgICB0aGlzLnZhbGlkYXRpb24gPSBuZXcgRm9ybUhlbHBlcih0aGlzLiRyb290LmZpbmQoJ2lucHV0JykpO1xuXG4gICAgICAgIHRoaXMuX2Fzc2lnbkV2ZW50cygpO1xuICAgIH1cblxuICAgIF9nZXREb20oKSB7XG4gICAgICAgIGNvbnN0ICRyb290ID0gdGhpcy4kcm9vdDtcblxuICAgICAgICByZXR1cm4ge1xuICAgICAgICAgICAgJGNvdW50OiAkcm9vdC5maW5kKCdbZGF0YS1jcmVkaWN0LWNvdW50XScpLFxuICAgICAgICAgICAgJHZhbHVlOiAkcm9vdC5maW5kKCdbZGF0YS1jcmVkaXQtdmFsdWVdJyksXG4gICAgICAgICAgICAkdG86ICRyb290LmZpbmQoJ1tkYXRhLWNyZWRpdC10b10nKSxcbiAgICAgICAgICAgICRtZXNzYWdlOiAkcm9vdC5maW5kKCdbZGF0YS1jcmVkaXQtbWVzc2FnZV0nKSxcbiAgICAgICAgICAgICRlcnJvcjogJHJvb3QuZmluZCgnW2RhdGEtY3JlZGl0LWVycm9yXScpXG4gICAgICAgIH07XG4gICAgfVxuXG4gICAgX2Fzc2lnbkV2ZW50cygpIHtcbiAgICAgICAgdGhpcy4kcm9vdFxuICAgICAgICAgICAgLm9uKCdpbnB1dCcsICdpbnB1dCcsIChlKSA9PiB0aGlzLmxvY2Fscy4kZXJyb3IudGV4dCgnJykpXG4gICAgICAgICAgICAub24oJ3N1Ym1pdCcsIHRoaXMuX29uU3VibWl0Rm9ybS5iaW5kKHRoaXMpKTtcbiAgICB9XG5cbiAgICBfb25TdWJtaXRGb3JtKGUpIHtcbiAgICAgICAgZS5wcmV2ZW50RGVmYXVsdCgpO1xuXG4gICAgICAgIGlmICghdGhpcy5faXNGb3JtVmFsaWQoKSkgcmV0dXJuIGZhbHNlO1xuXG4gICAgICAgIHRoaXMuX3NlbmRSZXF1ZXN0KClcbiAgICAgICAgICAgIC5kb25lKCgpID0+IHtcbiAgICAgICAgICAgICAgICB0aGlzLnZhbGlkYXRpb24uY2xlYXJGb3JtKCk7XG5cbiAgICAgICAgICAgICAgICB0aGlzLiRyb290LmFkZENsYXNzKCdiLWNyZWRpdHNfc3RhdGVfc2VuZCcpO1xuICAgICAgICAgICAgICAgIHNldFRpbWVvdXQoKCk9PiB7XG4gICAgICAgICAgICAgICAgICAgIHRoaXMuJHJvb3QucmVtb3ZlQ2xhc3MoJ2ItY3JlZGl0c19zdGF0ZV9zZW5kJyk7XG4gICAgICAgICAgICAgICAgfSwgMzAwMClcbiAgICAgICAgICAgIH0pXG4gICAgICAgICAgICAuZmFpbCgocmVzcG9uc2UpID0+IHtcbiAgICAgICAgICAgICAgICBjb25zdCBkYXRhID0gJC5wYXJzZUpTT04ocmVzcG9uc2UucmVzcG9uc2VUZXh0KS5kYXRhO1xuICAgICAgICAgICAgICAgIGNvbnN0IGVycm9yVGV4dCA9IHRoaXMudmFsaWRhdGlvbi5nZXRFcnJvcnNUZXh0KGRhdGEuZXJyb3JzKTtcblxuICAgICAgICAgICAgICAgIGlmICghZGF0YS5lcnJvcnMpIHJldHVybjtcbiAgICAgICAgICAgICAgICBcbiAgICAgICAgICAgICAgICB0aGlzLmxvY2Fscy4kZXJyb3IudGV4dChlcnJvclRleHQpO1xuICAgICAgICAgICAgICAgIHRoaXMudmFsaWRhdGlvbi5zZXRFcnJvcnMoZGF0YS5lcnJvcnMpO1xuICAgICAgICAgICAgfSlcbiAgICB9XG5cbiAgICBfaXNGb3JtVmFsaWQoKSB7XG4gICAgICAgIGNvbnN0IGxvY2FscyA9IHRoaXMubG9jYWxzO1xuICAgICAgICBjb25zdCBpc0Vub3VnaENyZWRpdHMgPSBOdW1iZXIobG9jYWxzLiR2YWx1ZS52YWwoKSkgPD0gTnVtYmVyKGxvY2Fscy4kY291bnQudGV4dCgpKTtcbiAgICAgICAgbGV0IHZhbGlkID0gdHJ1ZTtcbiAgICAgICAgbGV0IGVycm9yVGV4dCA9ICcnO1xuXG4gICAgICAgIGlmICghdGhpcy52YWxpZGF0aW9uLmlzVmFsaWRJbnB1dHMoKSkge1xuICAgICAgICAgICAgdmFsaWQgPSBmYWxzZTtcbiAgICAgICAgICAgIGVycm9yVGV4dCArPSB0aGlzLnZhbGlkYXRpb24uZ2V0RXJyb3JzVGV4dCgpO1xuICAgICAgICB9XG5cbiAgICAgICAgaWYgKCFpc0Vub3VnaENyZWRpdHMpIHtcbiAgICAgICAgICAgIHZhbGlkID0gZmFsc2U7XG4gICAgICAgICAgICBlcnJvclRleHQgKz0gJ1lvdSBjYW5u4oCZdCBnaXZlIG1vcmUgdGhhbiAnICsgbG9jYWxzLiRjb3VudC50ZXh0KCkgKyAnIGNyZWRpdHMuICc7XG4gICAgICAgIH1cblxuICAgICAgICBpZiAoIXZhbGlkKSB7XG4gICAgICAgICAgICBsb2NhbHMuJGVycm9yLnRleHQoZXJyb3JUZXh0KTtcbiAgICAgICAgfVxuXG4gICAgICAgIHJldHVybiB2YWxpZDtcbiAgICB9XG5cbiAgICBfc2VuZFJlcXVlc3QoKSB7XG4gICAgICAgIHJldHVybiAkLnBvc3QodGhpcy4kcm9vdC5hdHRyKCdhY3Rpb24nKSxcbiAgICAgICAgICAgIHtcbiAgICAgICAgICAgICAgICBnaXZlOiB0aGlzLmxvY2Fscy4kdmFsdWUudmFsKCksXG4gICAgICAgICAgICAgICAgdG86IHRoaXMubG9jYWxzLiR0by52YWwoKSxcbiAgICAgICAgICAgICAgICBtZXNzYWdlOiB0aGlzLmxvY2Fscy4kbWVzc2FnZS52YWwoKVxuICAgICAgICAgICAgfVxuICAgICAgICApO1xuICAgIH1cblxuXG4gICAgLy8gc3RhdGljXG4gICAgc3RhdGljIGludGVyZmFjZShzZWxlY3Rvcikge1xuICAgICAgICBjb25zdCAkZWxlbXMgPSAkKHNlbGVjdG9yKTtcbiAgICAgICAgaWYgKCEkZWxlbXMubGVuZ3RoKSByZXR1cm47XG5cbiAgICAgICAgcmV0dXJuICRlbGVtcy5lYWNoKGZ1bmN0aW9uIChpbmRleCwgZWwpIHtcbiAgICAgICAgICAgIGxldCAkZWxlbWVudCA9ICQoZWwpO1xuICAgICAgICAgICAgbGV0IGRhdGEgICAgID0gJGVsZW1lbnQuZGF0YSgnd2lkZ2V0Jyk7XG5cbiAgICAgICAgICAgIGlmICghZGF0YSkge1xuICAgICAgICAgICAgICAgIGRhdGEgPSBuZXcgV2lkZ2V0KGVsKTtcbiAgICAgICAgICAgICAgICAkZWxlbWVudC5kYXRhKCd3aWRnZXQnLCBkYXRhKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgfSlcbiAgICB9XG59XG5cblxuXG5cbi8qKiBXRUJQQUNLIEZPT1RFUiAqKlxuICoqIC4vZnJvbnRlbmQvanMvcGVyc29uL3dpZGdldHMtY3JlZGl0L19zZW5kLWNyZWRpdHMuanNcbiAqKi8iLCIndXNlIHN0cmljdCc7XG5cbmV4cG9ydCBkZWZhdWx0IGNsYXNzIEZvcm1IZWxwZXIge1xuICAgIC8qKlxuICAgICAqIFZhbGlkYXRlIGZvcm0gdGhyb3VnaCBpbnB1dHNcbiAgICAgKiBAcGFyYW0ge2pRdWVyeX0gJGlucHV0c1xuICAgICAqL1xuICAgIGNvbnN0cnVjdG9yKCRpbnB1dHMpIHtcbiAgICAgICAgdGhpcy4kaW5wdXRzID0gJGlucHV0cztcbiAgICAgICAgdGhpcy5hcnJFcnJvcnMgPSBbXTtcbiAgICAgICAgdGhpcy5fYXNzaWduRXZlbnRzKCk7XG4gICAgfVxuXG4gICAgX2Fzc2lnbkV2ZW50cygpIHtcbiAgICAgICAgdGhpcy4kaW5wdXRzLm9uKCdpbnB1dCcsIChlKSA9PiB0aGlzLl9yZW1vdmVFcnJvcigkKGUudGFyZ2V0KSkpO1xuICAgIH1cblxuICAgIGlzVmFsaWRJbnB1dHMoKSB7XG4gICAgICAgIGNvbnN0ICRpbnB1dHMgPSB0aGlzLiRpbnB1dHM7XG4gICAgICAgIGxldCBlcnJvciA9IDA7XG5cbiAgICAgICAgJGlucHV0cy5lYWNoKChpbmRleCwgaW5wdXQpID0+IHtcbiAgICAgICAgICAgIGNvbnN0ICRpbnB1dCA9ICQoaW5wdXQpO1xuXG4gICAgICAgICAgICBpZiAoIXRoaXMuX2lzVmFsaWRJbnB1dCgkaW5wdXQpKSBlcnJvciArPSAxO1xuICAgICAgICB9KTtcbiAgICAgICAgcmV0dXJuIEJvb2xlYW4oIWVycm9yKTtcbiAgICB9XG5cbiAgICAvKipcbiAgICAgKiBDaGVjayBnaXZlbiBpbnB1dCwgaXMgaXQgdmFsaWQ/XG4gICAgICogQHBhcmFtIHtqUXVlcnl9ICRpbnB1dFxuICAgICAqIEByZXR1cm5zIHtib29sZWFufSAtIElzIHZhbGlkIGlucHV0P1xuICAgICAqL1xuICAgIF9pc1ZhbGlkSW5wdXQoJGlucHV0KSB7XG4gICAgICAgIGNvbnN0IHZhbHVlID0gJC50cmltKCRpbnB1dC52YWwoKSk7XG5cbiAgICAgICAgaWYgKCF2YWx1ZSkge1xuICAgICAgICAgICAgdGhpcy5fc2V0RXJyb3IoJGlucHV0LCAnRW1wdHknKTtcbiAgICAgICAgICAgIHJldHVybiBmYWxzZTtcbiAgICAgICAgfVxuXG4gICAgICAgIGlmICgoJGlucHV0Lmhhc0NsYXNzKCd0eXBlLWVtYWlsJykpICYmICF0aGlzLl9pc1ZhbGlkRW1haWwodmFsdWUpKSB7XG4gICAgICAgICAgICB0aGlzLl9zZXRFcnJvcigkaW5wdXQsICdFbWFpbCBpcyBub3QgdmFsaWQnKTtcbiAgICAgICAgICAgIHJldHVybiBmYWxzZTtcbiAgICAgICAgfVxuICAgICAgICByZXR1cm4gdHJ1ZTtcbiAgICB9XG5cbiAgICAvKipcbiAgICAgKiBJcyBFbWFpbCB2YWxpZD9cbiAgICAgKiBAcGFyYW0ge3N0cmluZ30gZW1haWxcbiAgICAgKiBAcmV0dXJucyB7Ym9vbGVhbn1cbiAgICAgKi9cbiAgICBfaXNWYWxpZEVtYWlsKGVtYWlsKSB7XG4gICAgICAgIHZhciByZSA9IC9eKChbXjw+KClcXFtcXF1cXFxcLiw7Olxcc0BcIl0rKFxcLltePD4oKVxcW1xcXVxcXFwuLDs6XFxzQFwiXSspKil8KFwiLitcIikpQCgoXFxbWzAtOV17MSwzfVxcLlswLTldezEsM31cXC5bMC05XXsxLDN9XFwuWzAtOV17MSwzfV0pfCgoW2EtekEtWlxcLTAtOV0rXFwuKStbYS16QS1aXXsyLH0pKSQvO1xuICAgICAgICByZXR1cm4gcmUudGVzdChlbWFpbCk7XG4gICAgfVxuXG4gICAgLyoqXG4gICAgICogU2V0IGVycm9yIGZvciBpbnB1dFxuICAgICAqIEBwYXJhbSB7alF1ZXJ5fSAkaW5wdXRcbiAgICAgKiBAcGFyYW0ge3N0cmluZ30gZXJyb3JUZXh0XG4gICAgICovXG4gICAgX3NldEVycm9yKCRpbnB1dCwgZXJyb3JUZXh0KSB7XG4gICAgICAgIGNvbnN0ICRwYXJlbnQgPSAkaW5wdXQucGFyZW50KCk7XG4gICAgICAgIGNvbnN0ICRlcnJvciA9ICRwYXJlbnQuZmluZCgnLmItZXJyb3InKTtcblxuICAgICAgICBpZiAoJGVycm9yLmxlbmd0aCkgcmV0dXJuO1xuXG4gICAgICAgICRwYXJlbnQuYWRkQ2xhc3MoJ2ItZXJyb3Jfc2hvdycpO1xuICAgICAgICAkKCc8ZGl2IGNsYXNzPVwiYi1lcnJvclwiIC8+JylcbiAgICAgICAgICAgIC50ZXh0KGVycm9yVGV4dClcbiAgICAgICAgICAgIC5wcmVwZW5kVG8oJHBhcmVudCk7XG5cbiAgICAgICAgdGhpcy5hcnJFcnJvcnMucHVzaCh7XG4gICAgICAgICAgICBuYW1lOiAkaW5wdXQuYXR0cignbmFtZScpLFxuICAgICAgICAgICAgZXJyb3I6IGVycm9yVGV4dFxuICAgICAgICB9KVxuICAgIH1cblxuICAgIC8qKlxuICAgICAqIFJlbW92ZSBlcnJvciBmb3IgaW5wdXRcbiAgICAgKiBAcGFyYW0ge2pRdWVyeX0gJGlucHV0XG4gICAgICovXG4gICAgX3JlbW92ZUVycm9yKCRpbnB1dCkge1xuICAgICAgICBjb25zdCAkcGFyZW50ID0gJGlucHV0LnBhcmVudCgpO1xuXG4gICAgICAgICRwYXJlbnRcbiAgICAgICAgICAgIC5yZW1vdmVDbGFzcygnYi1lcnJvcl9zaG93JylcbiAgICAgICAgICAgIC5maW5kKCcuYi1lcnJvcicpLnJlbW92ZSgpO1xuXG4gICAgICAgIHRoaXMuYXJyRXJyb3JzID0gdGhpcy5hcnJFcnJvcnMuZmlsdGVyKGZ1bmN0aW9uIChpdGVtKSB7XG4gICAgICAgICAgICByZXR1cm4gaXRlbS5uYW1lICE9PSAkaW5wdXQuYXR0cignbmFtZScpXG4gICAgICAgIH0pXG4gICAgfVxuXG4gICAgLyoqXG4gICAgICogU2V0IGVycm9yc1xuICAgICAqIEBwYXJhbSB7QXJyYXl9IGVycm9ycyAtIFt7bmFtZTogXCJlbWFpbFwiLCBlcnJvcjogXCJlbXB0eVwifSwge25hbWU6IFwicGFzc3dvcmRcIiwgZXJyb3I6IFwiZW1wdHlcIn1dXG4gICAgICovXG4gICAgc2V0RXJyb3JzKGVycm9ycykge1xuICAgICAgICBlcnJvcnMuZm9yRWFjaCgoaXRlbSkgPT4ge1xuICAgICAgICAgICAgY29uc3QgJGN1cnJlbnRJbnB1dCA9IHRoaXMuJGlucHV0cy5maWx0ZXIoJ1tuYW1lPVwiJyArIGl0ZW0ubmFtZSArICdcIl0nKS5maXJzdCgpO1xuXG4gICAgICAgICAgICBpZiAoJGN1cnJlbnRJbnB1dC5sZW5ndGgpIHRoaXMuX3NldEVycm9yKCRjdXJyZW50SW5wdXQsIGl0ZW0uZXJyb3IpXG4gICAgICAgIH0pXG4gICAgfVxuXG4gICAgLyoqXG4gICAgICogR2V0IHR4dCB2ZXJzaW9uIG9mIGFsbCBlcnJvcnNcbiAgICAgKi9cbiAgICBnZXRFcnJvcnNUZXh0KGVycm9ycykge1xuICAgICAgICBjb25zdCBhcnJFcnJvcnMgPSBlcnJvcnMgfHwgdGhpcy5hcnJFcnJvcnM7XG4gICAgICAgIGxldCBlcnJvclR4dCA9ICcnO1xuXG4gICAgICAgIGFyckVycm9ycy5mb3JFYWNoKChpdGVtKSA9PiB7XG4gICAgICAgICAgICBjb25zdCBuYW1lID0gaXRlbS5uYW1lWzBdLnRvVXBwZXJDYXNlKCkgKyBpdGVtLm5hbWUuc3Vic3RyKDEpO1xuXG4gICAgICAgICAgICBlcnJvclR4dCArPSBgJHtuYW1lfSB2YWx1ZSBpcyAke2l0ZW0uZXJyb3IudG9Mb3dlckNhc2UoKX0uIGA7XG4gICAgICAgIH0pO1xuXG4gICAgICAgIHJldHVybiBlcnJvclR4dDtcbiAgICB9XG5cbiAgICAvKipcbiAgICAgKiBSZW1vdmUgYWxsIGVycm9yc1xuICAgICAqL1xuICAgIHJlbW92ZUVycm9ycygpIHtcbiAgICAgICAgdGhpcy4kaW5wdXRzLmVhY2goKGluZGV4LCBlbCkgPT4ge1xuICAgICAgICAgICAgY29uc3QgJGVsID0gJChlbCk7XG4gICAgICAgICAgICB0aGlzLl9yZW1vdmVFcnJvcigkZWwpXG4gICAgICAgIH0pXG4gICAgfVxuXG4gICAgY2xlYXJGb3JtKCkge1xuICAgICAgICB0aGlzLiRpbnB1dHMuZWFjaCgoaW5kZXgsIGVsKSA9PiB7XG4gICAgICAgICAgICBjb25zdCAkZWwgPSAkKGVsKTtcbiAgICAgICAgICAgIGlmICghJGVsLmF0dHIoXCJkaXNhYmxlZFwiKSkgICRlbC52YWwoJycpO1xuICAgICAgICB9KVxuICAgIH1cbn1cblxuXG5cbi8qKiBXRUJQQUNLIEZPT1RFUiAqKlxuICoqIC4vZnJvbnRlbmQvanMvY29tbW9uL19mb3JtLWhlbHBlci5qc1xuICoqLyJdLCJzb3VyY2VSb290IjoiIn0=