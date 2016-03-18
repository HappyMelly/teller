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
	
	var _asyncTabs = __webpack_require__(2);
	
	var _asyncTabs2 = _interopRequireDefault(_asyncTabs);
	
	var _filterHistory = __webpack_require__(23);
	
	var _filterHistory2 = _interopRequireDefault(_filterHistory);
	
	var _sendCredits = __webpack_require__(24);
	
	var _sendCredits2 = _interopRequireDefault(_sendCredits);
	
	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }
	
	_asyncTabs2.default.plugin('.js-credits-tabs');
	
	$('.js-credit-page').on('hmt.tab.shown', function () {
	    _filterHistory2.default.plugin('.js-credit-history');
	    _sendCredits2.default.plugin('.js-form-credit');
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
	    function Widget(selector) {
	        (0, _classCallCheck3.default)(this, Widget);
	
	        this.$root = $(selector);
	        this.loadedTabs = [];
	        this.locals = this._getDom();
	
	        this._assignEvents();
	
	        var $firstLink = this.locals.$links.first();
	        this.showTabByLink($firstLink);
	    }
	
	    (0, _createClass3.default)(Widget, [{
	        key: '_getDom',
	        value: function _getDom() {
	            return {
	                $links: this.$root.find('[data-tab-link]')
	            };
	        }
	    }, {
	        key: '_assignEvents',
	        value: function _assignEvents() {
	            this.$root.on('click', '[data-tab-link]', this._onClickLink.bind(this));
	        }
	    }, {
	        key: '_onClickLink',
	        value: function _onClickLink(e) {
	            e.preventDefault();
	            var $link = $(e.target);
	
	            if ($link.hasClass('state_active')) return;
	            this.showTabByLink($link);
	        }
	
	        /**
	         * 
	         * @param {jQuery} $link - clicked link
	         * @private
	         */
	
	    }, {
	        key: 'showTabByLink',
	        value: function showTabByLink($link) {
	            var _this = this;
	
	            var url = $link.attr('data-href');
	            var target = $link.attr('href');
	
	            this._loadContent(url, target).done(function () {
	                $link.addClass('state_active').siblings().removeClass('state_active');
	                $link.tab('show');
	
	                _this.$root.trigger('hmt.tab.shown');
	            });
	        }
	
	        /**
	         * 
	         * @param {String} url              - url of loaded content
	         * @param {jQuery} target   - div where we should insert content
	         */
	
	    }, {
	        key: '_loadContent',
	        value: function _loadContent(url, target) {
	            var self = this;
	            var defer = $.Deferred();
	
	            if ($.inArray(target, self.loadedTabs) < 0 && url) {
	                $.get(url, function (data) {
	                    self.loadedTabs.push(target);
	                    $(target).html(data);
	
	                    defer.resolve();
	                });
	            } else {
	                defer.resolve();
	            }
	
	            return defer.promise();
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
	        key: 'plugin',
	
	
	        // static
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
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIndlYnBhY2s6Ly8vd2VicGFjay9ib290c3RyYXAgZTJiZDY2OWU2YmQ0YTRmODFlNjYiLCJ3ZWJwYWNrOi8vLy4vZnJvbnRlbmQvanMvY3JlZGl0cy9jcmVkaXQtcGFnZS5qcyIsIndlYnBhY2s6Ly8vLi9mcm9udGVuZC9qcy9jb21tb24vX2FzeW5jLXRhYnMuanMiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL2hlbHBlcnMvY2xhc3NDYWxsQ2hlY2suanMiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL2hlbHBlcnMvY3JlYXRlQ2xhc3MuanMiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL2NvcmUtanMvb2JqZWN0L2RlZmluZS1wcm9wZXJ0eS5qcyIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvZm4vb2JqZWN0L2RlZmluZS1wcm9wZXJ0eS5qcyIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9lczYub2JqZWN0LmRlZmluZS1wcm9wZXJ0eS5qcyIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZXhwb3J0LmpzIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19nbG9iYWwuanMiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2NvcmUuanMiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2N0eC5qcyIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fYS1mdW5jdGlvbi5qcyIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9faGlkZS5qcyIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fb2JqZWN0LWRwLmpzIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19hbi1vYmplY3QuanMiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2lzLW9iamVjdC5qcyIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9faWU4LWRvbS1kZWZpbmUuanMiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2Rlc2NyaXB0b3JzLmpzIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19mYWlscy5qcyIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZG9tLWNyZWF0ZS5qcyIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fdG8tcHJpbWl0aXZlLmpzIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19wcm9wZXJ0eS1kZXNjLmpzIiwid2VicGFjazovLy8uL2Zyb250ZW5kL2pzL2NyZWRpdHMvd2lkZ2V0cy9fZmlsdGVyLWhpc3RvcnkuanMiLCJ3ZWJwYWNrOi8vLy4vZnJvbnRlbmQvanMvY3JlZGl0cy93aWRnZXRzL19zZW5kLWNyZWRpdHMuanMiLCJ3ZWJwYWNrOi8vLy4vZnJvbnRlbmQvanMvY29tbW9uL19mb3JtLWhlbHBlci5qcyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiO0FBQUE7QUFDQTs7QUFFQTtBQUNBOztBQUVBO0FBQ0E7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QUFLLFlBQUwsQ0FBa0IsR0FBbEIsRUFGNkI7Y0FBZixDQUFsQixDQURXOzs7O3FDQU9IO0FBQ1Isa0JBQUssT0FBTCxDQUFhLElBQWIsQ0FBa0IsVUFBQyxLQUFELEVBQVEsRUFBUixFQUFlO0FBQzdCLHFCQUFNLE1BQU0sRUFBRSxFQUFGLENBQU4sQ0FEdUI7QUFFN0IscUJBQUksQ0FBQyxJQUFJLElBQUosQ0FBUyxVQUFULENBQUQsRUFBd0IsSUFBSSxHQUFKLENBQVEsRUFBUixFQUE1QjtjQUZjLENBQWxCLENBRFE7OztZQXJJSyIsImZpbGUiOiJjcmVkaXQtcGFnZS5qcyIsInNvdXJjZXNDb250ZW50IjpbIiBcdC8vIFRoZSBtb2R1bGUgY2FjaGVcbiBcdHZhciBpbnN0YWxsZWRNb2R1bGVzID0ge307XG5cbiBcdC8vIFRoZSByZXF1aXJlIGZ1bmN0aW9uXG4gXHRmdW5jdGlvbiBfX3dlYnBhY2tfcmVxdWlyZV9fKG1vZHVsZUlkKSB7XG5cbiBcdFx0Ly8gQ2hlY2sgaWYgbW9kdWxlIGlzIGluIGNhY2hlXG4gXHRcdGlmKGluc3RhbGxlZE1vZHVsZXNbbW9kdWxlSWRdKVxuIFx0XHRcdHJldHVybiBpbnN0YWxsZWRNb2R1bGVzW21vZHVsZUlkXS5leHBvcnRzO1xuXG4gXHRcdC8vIENyZWF0ZSBhIG5ldyBtb2R1bGUgKGFuZCBwdXQgaXQgaW50byB0aGUgY2FjaGUpXG4gXHRcdHZhciBtb2R1bGUgPSBpbnN0YWxsZWRNb2R1bGVzW21vZHVsZUlkXSA9IHtcbiBcdFx0XHRleHBvcnRzOiB7fSxcbiBcdFx0XHRpZDogbW9kdWxlSWQsXG4gXHRcdFx0bG9hZGVkOiBmYWxzZVxuIFx0XHR9O1xuXG4gXHRcdC8vIEV4ZWN1dGUgdGhlIG1vZHVsZSBmdW5jdGlvblxuIFx0XHRtb2R1bGVzW21vZHVsZUlkXS5jYWxsKG1vZHVsZS5leHBvcnRzLCBtb2R1bGUsIG1vZHVsZS5leHBvcnRzLCBfX3dlYnBhY2tfcmVxdWlyZV9fKTtcblxuIFx0XHQvLyBGbGFnIHRoZSBtb2R1bGUgYXMgbG9hZGVkXG4gXHRcdG1vZHVsZS5sb2FkZWQgPSB0cnVlO1xuXG4gXHRcdC8vIFJldHVybiB0aGUgZXhwb3J0cyBvZiB0aGUgbW9kdWxlXG4gXHRcdHJldHVybiBtb2R1bGUuZXhwb3J0cztcbiBcdH1cblxuXG4gXHQvLyBleHBvc2UgdGhlIG1vZHVsZXMgb2JqZWN0IChfX3dlYnBhY2tfbW9kdWxlc19fKVxuIFx0X193ZWJwYWNrX3JlcXVpcmVfXy5tID0gbW9kdWxlcztcblxuIFx0Ly8gZXhwb3NlIHRoZSBtb2R1bGUgY2FjaGVcbiBcdF9fd2VicGFja19yZXF1aXJlX18uYyA9IGluc3RhbGxlZE1vZHVsZXM7XG5cbiBcdC8vIF9fd2VicGFja19wdWJsaWNfcGF0aF9fXG4gXHRfX3dlYnBhY2tfcmVxdWlyZV9fLnAgPSBcIlwiO1xuXG4gXHQvLyBMb2FkIGVudHJ5IG1vZHVsZSBhbmQgcmV0dXJuIGV4cG9ydHNcbiBcdHJldHVybiBfX3dlYnBhY2tfcmVxdWlyZV9fKDApO1xuXG5cblxuLyoqIFdFQlBBQ0sgRk9PVEVSICoqXG4gKiogd2VicGFjay9ib290c3RyYXAgZTJiZDY2OWU2YmQ0YTRmODFlNjZcbiAqKi8iLCJcbmltcG9ydCBBc3luY1RhYiBmcm9tICcuLy4uL2NvbW1vbi9fYXN5bmMtdGFicyc7XG5pbXBvcnQgRmlsdGVySGlzdG9yeSBmcm9tICcuL3dpZGdldHMvX2ZpbHRlci1oaXN0b3J5JztcbmltcG9ydCBDcmVkaXRGb3JtIGZyb20gJy4vd2lkZ2V0cy9fc2VuZC1jcmVkaXRzJztcblxuXG5Bc3luY1RhYi5wbHVnaW4oJy5qcy1jcmVkaXRzLXRhYnMnKTtcblxuJCgnLmpzLWNyZWRpdC1wYWdlJylcbiAgICAub24oJ2htdC50YWIuc2hvd24nLCAoKT0+IHtcbiAgICAgICAgRmlsdGVySGlzdG9yeS5wbHVnaW4oJy5qcy1jcmVkaXQtaGlzdG9yeScpO1xuICAgICAgICBDcmVkaXRGb3JtLnBsdWdpbignLmpzLWZvcm0tY3JlZGl0Jyk7XG4gICAgfSk7XG5cblxuXG5cblxuXG4vKiogV0VCUEFDSyBGT09URVIgKipcbiAqKiAuL2Zyb250ZW5kL2pzL2NyZWRpdHMvY3JlZGl0LXBhZ2UuanNcbiAqKi8iLCIndXNlIHN0cmljdCc7XG5cbmV4cG9ydCBkZWZhdWx0IGNsYXNzIFdpZGdldCB7XG5cbiAgICBjb25zdHJ1Y3RvcihzZWxlY3Rvcikge1xuICAgICAgICB0aGlzLiRyb290ID0gJChzZWxlY3Rvcik7XG4gICAgICAgIHRoaXMubG9hZGVkVGFicyA9IFtdO1xuICAgICAgICB0aGlzLmxvY2FscyA9IHRoaXMuX2dldERvbSgpO1xuXG4gICAgICAgIHRoaXMuX2Fzc2lnbkV2ZW50cygpO1xuXG4gICAgICAgIGxldCAkZmlyc3RMaW5rID0gdGhpcy5sb2NhbHMuJGxpbmtzLmZpcnN0KCk7XG4gICAgICAgIHRoaXMuc2hvd1RhYkJ5TGluaygkZmlyc3RMaW5rKTtcbiAgICB9XG5cbiAgICBfZ2V0RG9tKCkge1xuICAgICAgICByZXR1cm4ge1xuICAgICAgICAgICAgJGxpbmtzOiB0aGlzLiRyb290LmZpbmQoJ1tkYXRhLXRhYi1saW5rXScpXG4gICAgICAgIH1cbiAgICB9XG5cbiAgICBfYXNzaWduRXZlbnRzKCkge1xuICAgICAgICB0aGlzLiRyb290Lm9uKCdjbGljaycsICdbZGF0YS10YWItbGlua10nLCB0aGlzLl9vbkNsaWNrTGluay5iaW5kKHRoaXMpKTtcbiAgICB9XG5cbiAgICBfb25DbGlja0xpbmsoZSkge1xuICAgICAgICBlLnByZXZlbnREZWZhdWx0KCk7XG4gICAgICAgIGxldCAkbGluayA9ICQoZS50YXJnZXQpO1xuXG4gICAgICAgIGlmICgkbGluay5oYXNDbGFzcygnc3RhdGVfYWN0aXZlJykpIHJldHVybjtcbiAgICAgICAgdGhpcy5zaG93VGFiQnlMaW5rKCRsaW5rKTtcbiAgICB9XG5cbiAgICAvKipcbiAgICAgKiBcbiAgICAgKiBAcGFyYW0ge2pRdWVyeX0gJGxpbmsgLSBjbGlja2VkIGxpbmtcbiAgICAgKiBAcHJpdmF0ZVxuICAgICAqL1xuICAgIHNob3dUYWJCeUxpbmsoJGxpbmspe1xuICAgICAgICBjb25zdCB1cmwgPSAkbGluay5hdHRyKCdkYXRhLWhyZWYnKTtcbiAgICAgICAgY29uc3QgdGFyZ2V0ID0gJGxpbmsuYXR0cignaHJlZicpO1xuXG4gICAgICAgIHRoaXMuX2xvYWRDb250ZW50KHVybCwgdGFyZ2V0KVxuICAgICAgICAgICAgLmRvbmUoICAoKT0+e1xuICAgICAgICAgICAgICAgICRsaW5rLmFkZENsYXNzKCdzdGF0ZV9hY3RpdmUnKS5zaWJsaW5ncygpLnJlbW92ZUNsYXNzKCdzdGF0ZV9hY3RpdmUnKTtcbiAgICAgICAgICAgICAgICAkbGluay50YWIoJ3Nob3cnKTtcblxuICAgICAgICAgICAgICAgIHRoaXMuJHJvb3QudHJpZ2dlcignaG10LnRhYi5zaG93bicpO1xuICAgICAgICAgICAgfSlcbiAgICB9XG5cbiAgICAvKipcbiAgICAgKiBcbiAgICAgKiBAcGFyYW0ge1N0cmluZ30gdXJsICAgICAgICAgICAgICAtIHVybCBvZiBsb2FkZWQgY29udGVudFxuICAgICAqIEBwYXJhbSB7alF1ZXJ5fSB0YXJnZXQgICAtIGRpdiB3aGVyZSB3ZSBzaG91bGQgaW5zZXJ0IGNvbnRlbnRcbiAgICAgKi9cbiAgICBfbG9hZENvbnRlbnQodXJsLCB0YXJnZXQpe1xuICAgICAgICBjb25zdCBzZWxmID0gdGhpcztcbiAgICAgICAgbGV0IGRlZmVyID0gJC5EZWZlcnJlZCgpO1xuXG4gICAgICAgIGlmICgkLmluQXJyYXkodGFyZ2V0LCBzZWxmLmxvYWRlZFRhYnMpIDwgMCAmJiB1cmwpIHtcbiAgICAgICAgICAgICQuZ2V0KHVybCwgKGRhdGEpID0+IHtcbiAgICAgICAgICAgICAgICBzZWxmLmxvYWRlZFRhYnMucHVzaCh0YXJnZXQpO1xuICAgICAgICAgICAgICAgICQodGFyZ2V0KS5odG1sKGRhdGEpO1xuXG4gICAgICAgICAgICAgICAgZGVmZXIucmVzb2x2ZSgpO1xuICAgICAgICAgICAgfSk7XG4gICAgICAgIH0gZWxzZSB7XG4gICAgICAgICAgICBkZWZlci5yZXNvbHZlKCk7XG4gICAgICAgIH1cbiAgICAgICAgXG4gICAgICAgIHJldHVybiBkZWZlci5wcm9taXNlKCk7XG4gICAgfSAgICBcblxuICAgIC8vIHN0YXRpY1xuICAgIHN0YXRpYyBwbHVnaW4oc2VsZWN0b3IpIHtcbiAgICAgICAgY29uc3QgJGVsZW1zID0gJChzZWxlY3Rvcik7XG4gICAgICAgIGlmICghJGVsZW1zLmxlbmd0aCkgcmV0dXJuO1xuXG4gICAgICAgIHJldHVybiAkZWxlbXMuZWFjaChmdW5jdGlvbiAoaW5kZXgsIGVsKSB7XG4gICAgICAgICAgICBsZXQgJGVsZW1lbnQgPSAkKGVsKTtcbiAgICAgICAgICAgIGxldCBkYXRhICAgICA9ICRlbGVtZW50LmRhdGEoJ3dpZGdldCcpO1xuXG4gICAgICAgICAgICBpZiAoIWRhdGEpIHtcbiAgICAgICAgICAgICAgICBkYXRhID0gbmV3IFdpZGdldChlbCk7XG4gICAgICAgICAgICAgICAgJGVsZW1lbnQuZGF0YSgnd2lkZ2V0JywgZGF0YSk7XG4gICAgICAgICAgICB9XG4gICAgICAgIH0pXG4gICAgfVxufVxuXG5cblxuXG5cbi8qKiBXRUJQQUNLIEZPT1RFUiAqKlxuICoqIC4vZnJvbnRlbmQvanMvY29tbW9uL19hc3luYy10YWJzLmpzXG4gKiovIiwiXCJ1c2Ugc3RyaWN0XCI7XG5cbmV4cG9ydHMuX19lc01vZHVsZSA9IHRydWU7XG5cbmV4cG9ydHMuZGVmYXVsdCA9IGZ1bmN0aW9uIChpbnN0YW5jZSwgQ29uc3RydWN0b3IpIHtcbiAgaWYgKCEoaW5zdGFuY2UgaW5zdGFuY2VvZiBDb25zdHJ1Y3RvcikpIHtcbiAgICB0aHJvdyBuZXcgVHlwZUVycm9yKFwiQ2Fubm90IGNhbGwgYSBjbGFzcyBhcyBhIGZ1bmN0aW9uXCIpO1xuICB9XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvaGVscGVycy9jbGFzc0NhbGxDaGVjay5qc1xuICoqIG1vZHVsZSBpZCA9IDNcbiAqKiBtb2R1bGUgY2h1bmtzID0gMFxuICoqLyIsIlwidXNlIHN0cmljdFwiO1xuXG5leHBvcnRzLl9fZXNNb2R1bGUgPSB0cnVlO1xuXG52YXIgX2RlZmluZVByb3BlcnR5ID0gcmVxdWlyZShcImJhYmVsLXJ1bnRpbWUvY29yZS1qcy9vYmplY3QvZGVmaW5lLXByb3BlcnR5XCIpO1xuXG52YXIgX2RlZmluZVByb3BlcnR5MiA9IF9pbnRlcm9wUmVxdWlyZURlZmF1bHQoX2RlZmluZVByb3BlcnR5KTtcblxuZnVuY3Rpb24gX2ludGVyb3BSZXF1aXJlRGVmYXVsdChvYmopIHsgcmV0dXJuIG9iaiAmJiBvYmouX19lc01vZHVsZSA/IG9iaiA6IHsgZGVmYXVsdDogb2JqIH07IH1cblxuZXhwb3J0cy5kZWZhdWx0ID0gZnVuY3Rpb24gKCkge1xuICBmdW5jdGlvbiBkZWZpbmVQcm9wZXJ0aWVzKHRhcmdldCwgcHJvcHMpIHtcbiAgICBmb3IgKHZhciBpID0gMDsgaSA8IHByb3BzLmxlbmd0aDsgaSsrKSB7XG4gICAgICB2YXIgZGVzY3JpcHRvciA9IHByb3BzW2ldO1xuICAgICAgZGVzY3JpcHRvci5lbnVtZXJhYmxlID0gZGVzY3JpcHRvci5lbnVtZXJhYmxlIHx8IGZhbHNlO1xuICAgICAgZGVzY3JpcHRvci5jb25maWd1cmFibGUgPSB0cnVlO1xuICAgICAgaWYgKFwidmFsdWVcIiBpbiBkZXNjcmlwdG9yKSBkZXNjcmlwdG9yLndyaXRhYmxlID0gdHJ1ZTtcbiAgICAgICgwLCBfZGVmaW5lUHJvcGVydHkyLmRlZmF1bHQpKHRhcmdldCwgZGVzY3JpcHRvci5rZXksIGRlc2NyaXB0b3IpO1xuICAgIH1cbiAgfVxuXG4gIHJldHVybiBmdW5jdGlvbiAoQ29uc3RydWN0b3IsIHByb3RvUHJvcHMsIHN0YXRpY1Byb3BzKSB7XG4gICAgaWYgKHByb3RvUHJvcHMpIGRlZmluZVByb3BlcnRpZXMoQ29uc3RydWN0b3IucHJvdG90eXBlLCBwcm90b1Byb3BzKTtcbiAgICBpZiAoc3RhdGljUHJvcHMpIGRlZmluZVByb3BlcnRpZXMoQ29uc3RydWN0b3IsIHN0YXRpY1Byb3BzKTtcbiAgICByZXR1cm4gQ29uc3RydWN0b3I7XG4gIH07XG59KCk7XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9oZWxwZXJzL2NyZWF0ZUNsYXNzLmpzXG4gKiogbW9kdWxlIGlkID0gNFxuICoqIG1vZHVsZSBjaHVua3MgPSAwXG4gKiovIiwibW9kdWxlLmV4cG9ydHMgPSB7IFwiZGVmYXVsdFwiOiByZXF1aXJlKFwiY29yZS1qcy9saWJyYXJ5L2ZuL29iamVjdC9kZWZpbmUtcHJvcGVydHlcIiksIF9fZXNNb2R1bGU6IHRydWUgfTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL2NvcmUtanMvb2JqZWN0L2RlZmluZS1wcm9wZXJ0eS5qc1xuICoqIG1vZHVsZSBpZCA9IDVcbiAqKiBtb2R1bGUgY2h1bmtzID0gMFxuICoqLyIsInJlcXVpcmUoJy4uLy4uL21vZHVsZXMvZXM2Lm9iamVjdC5kZWZpbmUtcHJvcGVydHknKTtcbnZhciAkT2JqZWN0ID0gcmVxdWlyZSgnLi4vLi4vbW9kdWxlcy9fY29yZScpLk9iamVjdDtcbm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24gZGVmaW5lUHJvcGVydHkoaXQsIGtleSwgZGVzYyl7XG4gIHJldHVybiAkT2JqZWN0LmRlZmluZVByb3BlcnR5KGl0LCBrZXksIGRlc2MpO1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L2ZuL29iamVjdC9kZWZpbmUtcHJvcGVydHkuanNcbiAqKiBtb2R1bGUgaWQgPSA2XG4gKiogbW9kdWxlIGNodW5rcyA9IDBcbiAqKi8iLCJ2YXIgJGV4cG9ydCA9IHJlcXVpcmUoJy4vX2V4cG9ydCcpO1xyXG4vLyAxOS4xLjIuNCAvIDE1LjIuMy42IE9iamVjdC5kZWZpbmVQcm9wZXJ0eShPLCBQLCBBdHRyaWJ1dGVzKVxyXG4kZXhwb3J0KCRleHBvcnQuUyArICRleHBvcnQuRiAqICFyZXF1aXJlKCcuL19kZXNjcmlwdG9ycycpLCAnT2JqZWN0Jywge2RlZmluZVByb3BlcnR5OiByZXF1aXJlKCcuL19vYmplY3QtZHAnKS5mfSk7XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL2VzNi5vYmplY3QuZGVmaW5lLXByb3BlcnR5LmpzXG4gKiogbW9kdWxlIGlkID0gN1xuICoqIG1vZHVsZSBjaHVua3MgPSAwXG4gKiovIiwidmFyIGdsb2JhbCAgICA9IHJlcXVpcmUoJy4vX2dsb2JhbCcpXG4gICwgY29yZSAgICAgID0gcmVxdWlyZSgnLi9fY29yZScpXG4gICwgY3R4ICAgICAgID0gcmVxdWlyZSgnLi9fY3R4JylcbiAgLCBoaWRlICAgICAgPSByZXF1aXJlKCcuL19oaWRlJylcbiAgLCBQUk9UT1RZUEUgPSAncHJvdG90eXBlJztcblxudmFyICRleHBvcnQgPSBmdW5jdGlvbih0eXBlLCBuYW1lLCBzb3VyY2Upe1xuICB2YXIgSVNfRk9SQ0VEID0gdHlwZSAmICRleHBvcnQuRlxuICAgICwgSVNfR0xPQkFMID0gdHlwZSAmICRleHBvcnQuR1xuICAgICwgSVNfU1RBVElDID0gdHlwZSAmICRleHBvcnQuU1xuICAgICwgSVNfUFJPVE8gID0gdHlwZSAmICRleHBvcnQuUFxuICAgICwgSVNfQklORCAgID0gdHlwZSAmICRleHBvcnQuQlxuICAgICwgSVNfV1JBUCAgID0gdHlwZSAmICRleHBvcnQuV1xuICAgICwgZXhwb3J0cyAgID0gSVNfR0xPQkFMID8gY29yZSA6IGNvcmVbbmFtZV0gfHwgKGNvcmVbbmFtZV0gPSB7fSlcbiAgICAsIGV4cFByb3RvICA9IGV4cG9ydHNbUFJPVE9UWVBFXVxuICAgICwgdGFyZ2V0ICAgID0gSVNfR0xPQkFMID8gZ2xvYmFsIDogSVNfU1RBVElDID8gZ2xvYmFsW25hbWVdIDogKGdsb2JhbFtuYW1lXSB8fCB7fSlbUFJPVE9UWVBFXVxuICAgICwga2V5LCBvd24sIG91dDtcbiAgaWYoSVNfR0xPQkFMKXNvdXJjZSA9IG5hbWU7XG4gIGZvcihrZXkgaW4gc291cmNlKXtcbiAgICAvLyBjb250YWlucyBpbiBuYXRpdmVcbiAgICBvd24gPSAhSVNfRk9SQ0VEICYmIHRhcmdldCAmJiB0YXJnZXRba2V5XSAhPT0gdW5kZWZpbmVkO1xuICAgIGlmKG93biAmJiBrZXkgaW4gZXhwb3J0cyljb250aW51ZTtcbiAgICAvLyBleHBvcnQgbmF0aXZlIG9yIHBhc3NlZFxuICAgIG91dCA9IG93biA/IHRhcmdldFtrZXldIDogc291cmNlW2tleV07XG4gICAgLy8gcHJldmVudCBnbG9iYWwgcG9sbHV0aW9uIGZvciBuYW1lc3BhY2VzXG4gICAgZXhwb3J0c1trZXldID0gSVNfR0xPQkFMICYmIHR5cGVvZiB0YXJnZXRba2V5XSAhPSAnZnVuY3Rpb24nID8gc291cmNlW2tleV1cbiAgICAvLyBiaW5kIHRpbWVycyB0byBnbG9iYWwgZm9yIGNhbGwgZnJvbSBleHBvcnQgY29udGV4dFxuICAgIDogSVNfQklORCAmJiBvd24gPyBjdHgob3V0LCBnbG9iYWwpXG4gICAgLy8gd3JhcCBnbG9iYWwgY29uc3RydWN0b3JzIGZvciBwcmV2ZW50IGNoYW5nZSB0aGVtIGluIGxpYnJhcnlcbiAgICA6IElTX1dSQVAgJiYgdGFyZ2V0W2tleV0gPT0gb3V0ID8gKGZ1bmN0aW9uKEMpe1xuICAgICAgdmFyIEYgPSBmdW5jdGlvbihhLCBiLCBjKXtcbiAgICAgICAgaWYodGhpcyBpbnN0YW5jZW9mIEMpe1xuICAgICAgICAgIHN3aXRjaChhcmd1bWVudHMubGVuZ3RoKXtcbiAgICAgICAgICAgIGNhc2UgMDogcmV0dXJuIG5ldyBDO1xuICAgICAgICAgICAgY2FzZSAxOiByZXR1cm4gbmV3IEMoYSk7XG4gICAgICAgICAgICBjYXNlIDI6IHJldHVybiBuZXcgQyhhLCBiKTtcbiAgICAgICAgICB9IHJldHVybiBuZXcgQyhhLCBiLCBjKTtcbiAgICAgICAgfSByZXR1cm4gQy5hcHBseSh0aGlzLCBhcmd1bWVudHMpO1xuICAgICAgfTtcbiAgICAgIEZbUFJPVE9UWVBFXSA9IENbUFJPVE9UWVBFXTtcbiAgICAgIHJldHVybiBGO1xuICAgIC8vIG1ha2Ugc3RhdGljIHZlcnNpb25zIGZvciBwcm90b3R5cGUgbWV0aG9kc1xuICAgIH0pKG91dCkgOiBJU19QUk9UTyAmJiB0eXBlb2Ygb3V0ID09ICdmdW5jdGlvbicgPyBjdHgoRnVuY3Rpb24uY2FsbCwgb3V0KSA6IG91dDtcbiAgICAvLyBleHBvcnQgcHJvdG8gbWV0aG9kcyB0byBjb3JlLiVDT05TVFJVQ1RPUiUubWV0aG9kcy4lTkFNRSVcbiAgICBpZihJU19QUk9UTyl7XG4gICAgICAoZXhwb3J0cy52aXJ0dWFsIHx8IChleHBvcnRzLnZpcnR1YWwgPSB7fSkpW2tleV0gPSBvdXQ7XG4gICAgICAvLyBleHBvcnQgcHJvdG8gbWV0aG9kcyB0byBjb3JlLiVDT05TVFJVQ1RPUiUucHJvdG90eXBlLiVOQU1FJVxuICAgICAgaWYodHlwZSAmICRleHBvcnQuUiAmJiBleHBQcm90byAmJiAhZXhwUHJvdG9ba2V5XSloaWRlKGV4cFByb3RvLCBrZXksIG91dCk7XG4gICAgfVxuICB9XG59O1xuLy8gdHlwZSBiaXRtYXBcbiRleHBvcnQuRiA9IDE7ICAgLy8gZm9yY2VkXG4kZXhwb3J0LkcgPSAyOyAgIC8vIGdsb2JhbFxuJGV4cG9ydC5TID0gNDsgICAvLyBzdGF0aWNcbiRleHBvcnQuUCA9IDg7ICAgLy8gcHJvdG9cbiRleHBvcnQuQiA9IDE2OyAgLy8gYmluZFxuJGV4cG9ydC5XID0gMzI7ICAvLyB3cmFwXG4kZXhwb3J0LlUgPSA2NDsgIC8vIHNhZmVcbiRleHBvcnQuUiA9IDEyODsgLy8gcmVhbCBwcm90byBtZXRob2QgZm9yIGBsaWJyYXJ5YCBcbm1vZHVsZS5leHBvcnRzID0gJGV4cG9ydDtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2V4cG9ydC5qc1xuICoqIG1vZHVsZSBpZCA9IDhcbiAqKiBtb2R1bGUgY2h1bmtzID0gMFxuICoqLyIsIi8vIGh0dHBzOi8vZ2l0aHViLmNvbS96bG9pcm9jay9jb3JlLWpzL2lzc3Vlcy84NiNpc3N1ZWNvbW1lbnQtMTE1NzU5MDI4XG52YXIgZ2xvYmFsID0gbW9kdWxlLmV4cG9ydHMgPSB0eXBlb2Ygd2luZG93ICE9ICd1bmRlZmluZWQnICYmIHdpbmRvdy5NYXRoID09IE1hdGhcbiAgPyB3aW5kb3cgOiB0eXBlb2Ygc2VsZiAhPSAndW5kZWZpbmVkJyAmJiBzZWxmLk1hdGggPT0gTWF0aCA/IHNlbGYgOiBGdW5jdGlvbigncmV0dXJuIHRoaXMnKSgpO1xuaWYodHlwZW9mIF9fZyA9PSAnbnVtYmVyJylfX2cgPSBnbG9iYWw7IC8vIGVzbGludC1kaXNhYmxlLWxpbmUgbm8tdW5kZWZcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2dsb2JhbC5qc1xuICoqIG1vZHVsZSBpZCA9IDlcbiAqKiBtb2R1bGUgY2h1bmtzID0gMFxuICoqLyIsInZhciBjb3JlID0gbW9kdWxlLmV4cG9ydHMgPSB7dmVyc2lvbjogJzIuMi4wJ307XG5pZih0eXBlb2YgX19lID09ICdudW1iZXInKV9fZSA9IGNvcmU7IC8vIGVzbGludC1kaXNhYmxlLWxpbmUgbm8tdW5kZWZcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2NvcmUuanNcbiAqKiBtb2R1bGUgaWQgPSAxMFxuICoqIG1vZHVsZSBjaHVua3MgPSAwXG4gKiovIiwiLy8gb3B0aW9uYWwgLyBzaW1wbGUgY29udGV4dCBiaW5kaW5nXG52YXIgYUZ1bmN0aW9uID0gcmVxdWlyZSgnLi9fYS1mdW5jdGlvbicpO1xubW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihmbiwgdGhhdCwgbGVuZ3RoKXtcbiAgYUZ1bmN0aW9uKGZuKTtcbiAgaWYodGhhdCA9PT0gdW5kZWZpbmVkKXJldHVybiBmbjtcbiAgc3dpdGNoKGxlbmd0aCl7XG4gICAgY2FzZSAxOiByZXR1cm4gZnVuY3Rpb24oYSl7XG4gICAgICByZXR1cm4gZm4uY2FsbCh0aGF0LCBhKTtcbiAgICB9O1xuICAgIGNhc2UgMjogcmV0dXJuIGZ1bmN0aW9uKGEsIGIpe1xuICAgICAgcmV0dXJuIGZuLmNhbGwodGhhdCwgYSwgYik7XG4gICAgfTtcbiAgICBjYXNlIDM6IHJldHVybiBmdW5jdGlvbihhLCBiLCBjKXtcbiAgICAgIHJldHVybiBmbi5jYWxsKHRoYXQsIGEsIGIsIGMpO1xuICAgIH07XG4gIH1cbiAgcmV0dXJuIGZ1bmN0aW9uKC8qIC4uLmFyZ3MgKi8pe1xuICAgIHJldHVybiBmbi5hcHBseSh0aGF0LCBhcmd1bWVudHMpO1xuICB9O1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2N0eC5qc1xuICoqIG1vZHVsZSBpZCA9IDExXG4gKiogbW9kdWxlIGNodW5rcyA9IDBcbiAqKi8iLCJtb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGl0KXtcbiAgaWYodHlwZW9mIGl0ICE9ICdmdW5jdGlvbicpdGhyb3cgVHlwZUVycm9yKGl0ICsgJyBpcyBub3QgYSBmdW5jdGlvbiEnKTtcbiAgcmV0dXJuIGl0O1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2EtZnVuY3Rpb24uanNcbiAqKiBtb2R1bGUgaWQgPSAxMlxuICoqIG1vZHVsZSBjaHVua3MgPSAwXG4gKiovIiwidmFyIGRQICAgICAgICAgPSByZXF1aXJlKCcuL19vYmplY3QtZHAnKVxuICAsIGNyZWF0ZURlc2MgPSByZXF1aXJlKCcuL19wcm9wZXJ0eS1kZXNjJyk7XG5tb2R1bGUuZXhwb3J0cyA9IHJlcXVpcmUoJy4vX2Rlc2NyaXB0b3JzJykgPyBmdW5jdGlvbihvYmplY3QsIGtleSwgdmFsdWUpe1xuICByZXR1cm4gZFAuZihvYmplY3QsIGtleSwgY3JlYXRlRGVzYygxLCB2YWx1ZSkpO1xufSA6IGZ1bmN0aW9uKG9iamVjdCwga2V5LCB2YWx1ZSl7XG4gIG9iamVjdFtrZXldID0gdmFsdWU7XG4gIHJldHVybiBvYmplY3Q7XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9faGlkZS5qc1xuICoqIG1vZHVsZSBpZCA9IDEzXG4gKiogbW9kdWxlIGNodW5rcyA9IDBcbiAqKi8iLCJ2YXIgYW5PYmplY3QgICAgICAgPSByZXF1aXJlKCcuL19hbi1vYmplY3QnKVxuICAsIElFOF9ET01fREVGSU5FID0gcmVxdWlyZSgnLi9faWU4LWRvbS1kZWZpbmUnKVxuICAsIHRvUHJpbWl0aXZlICAgID0gcmVxdWlyZSgnLi9fdG8tcHJpbWl0aXZlJylcbiAgLCBkUCAgICAgICAgICAgICA9IE9iamVjdC5kZWZpbmVQcm9wZXJ0eTtcblxuZXhwb3J0cy5mID0gcmVxdWlyZSgnLi9fZGVzY3JpcHRvcnMnKSA/IE9iamVjdC5kZWZpbmVQcm9wZXJ0eSA6IGZ1bmN0aW9uIGRlZmluZVByb3BlcnR5KE8sIFAsIEF0dHJpYnV0ZXMpe1xuICBhbk9iamVjdChPKTtcbiAgUCA9IHRvUHJpbWl0aXZlKFAsIHRydWUpO1xuICBhbk9iamVjdChBdHRyaWJ1dGVzKTtcbiAgaWYoSUU4X0RPTV9ERUZJTkUpdHJ5IHtcbiAgICByZXR1cm4gZFAoTywgUCwgQXR0cmlidXRlcyk7XG4gIH0gY2F0Y2goZSl7IC8qIGVtcHR5ICovIH1cbiAgaWYoJ2dldCcgaW4gQXR0cmlidXRlcyB8fCAnc2V0JyBpbiBBdHRyaWJ1dGVzKXRocm93IFR5cGVFcnJvcignQWNjZXNzb3JzIG5vdCBzdXBwb3J0ZWQhJyk7XG4gIGlmKCd2YWx1ZScgaW4gQXR0cmlidXRlcylPW1BdID0gQXR0cmlidXRlcy52YWx1ZTtcbiAgcmV0dXJuIE87XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fb2JqZWN0LWRwLmpzXG4gKiogbW9kdWxlIGlkID0gMTRcbiAqKiBtb2R1bGUgY2h1bmtzID0gMFxuICoqLyIsInZhciBpc09iamVjdCA9IHJlcXVpcmUoJy4vX2lzLW9iamVjdCcpO1xubW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihpdCl7XG4gIGlmKCFpc09iamVjdChpdCkpdGhyb3cgVHlwZUVycm9yKGl0ICsgJyBpcyBub3QgYW4gb2JqZWN0IScpO1xuICByZXR1cm4gaXQ7XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fYW4tb2JqZWN0LmpzXG4gKiogbW9kdWxlIGlkID0gMTVcbiAqKiBtb2R1bGUgY2h1bmtzID0gMFxuICoqLyIsIm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oaXQpe1xuICByZXR1cm4gdHlwZW9mIGl0ID09PSAnb2JqZWN0JyA/IGl0ICE9PSBudWxsIDogdHlwZW9mIGl0ID09PSAnZnVuY3Rpb24nO1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2lzLW9iamVjdC5qc1xuICoqIG1vZHVsZSBpZCA9IDE2XG4gKiogbW9kdWxlIGNodW5rcyA9IDBcbiAqKi8iLCJtb2R1bGUuZXhwb3J0cyA9ICFyZXF1aXJlKCcuL19kZXNjcmlwdG9ycycpICYmICFyZXF1aXJlKCcuL19mYWlscycpKGZ1bmN0aW9uKCl7XHJcbiAgcmV0dXJuIE9iamVjdC5kZWZpbmVQcm9wZXJ0eShyZXF1aXJlKCcuL19kb20tY3JlYXRlJykoJ2RpdicpLCAnYScsIHtnZXQ6IGZ1bmN0aW9uKCl7IHJldHVybiA3OyB9fSkuYSAhPSA3O1xyXG59KTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2llOC1kb20tZGVmaW5lLmpzXG4gKiogbW9kdWxlIGlkID0gMTdcbiAqKiBtb2R1bGUgY2h1bmtzID0gMFxuICoqLyIsIi8vIFRoYW5rJ3MgSUU4IGZvciBoaXMgZnVubnkgZGVmaW5lUHJvcGVydHlcbm1vZHVsZS5leHBvcnRzID0gIXJlcXVpcmUoJy4vX2ZhaWxzJykoZnVuY3Rpb24oKXtcbiAgcmV0dXJuIE9iamVjdC5kZWZpbmVQcm9wZXJ0eSh7fSwgJ2EnLCB7Z2V0OiBmdW5jdGlvbigpeyByZXR1cm4gNzsgfX0pLmEgIT0gNztcbn0pO1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZGVzY3JpcHRvcnMuanNcbiAqKiBtb2R1bGUgaWQgPSAxOFxuICoqIG1vZHVsZSBjaHVua3MgPSAwXG4gKiovIiwibW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihleGVjKXtcbiAgdHJ5IHtcbiAgICByZXR1cm4gISFleGVjKCk7XG4gIH0gY2F0Y2goZSl7XG4gICAgcmV0dXJuIHRydWU7XG4gIH1cbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19mYWlscy5qc1xuICoqIG1vZHVsZSBpZCA9IDE5XG4gKiogbW9kdWxlIGNodW5rcyA9IDBcbiAqKi8iLCJ2YXIgaXNPYmplY3QgPSByZXF1aXJlKCcuL19pcy1vYmplY3QnKVxuICAsIGRvY3VtZW50ID0gcmVxdWlyZSgnLi9fZ2xvYmFsJykuZG9jdW1lbnRcbiAgLy8gaW4gb2xkIElFIHR5cGVvZiBkb2N1bWVudC5jcmVhdGVFbGVtZW50IGlzICdvYmplY3QnXG4gICwgaXMgPSBpc09iamVjdChkb2N1bWVudCkgJiYgaXNPYmplY3QoZG9jdW1lbnQuY3JlYXRlRWxlbWVudCk7XG5tb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGl0KXtcbiAgcmV0dXJuIGlzID8gZG9jdW1lbnQuY3JlYXRlRWxlbWVudChpdCkgOiB7fTtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19kb20tY3JlYXRlLmpzXG4gKiogbW9kdWxlIGlkID0gMjBcbiAqKiBtb2R1bGUgY2h1bmtzID0gMFxuICoqLyIsIi8vIDcuMS4xIFRvUHJpbWl0aXZlKGlucHV0IFssIFByZWZlcnJlZFR5cGVdKVxudmFyIGlzT2JqZWN0ID0gcmVxdWlyZSgnLi9faXMtb2JqZWN0Jyk7XG4vLyBpbnN0ZWFkIG9mIHRoZSBFUzYgc3BlYyB2ZXJzaW9uLCB3ZSBkaWRuJ3QgaW1wbGVtZW50IEBAdG9QcmltaXRpdmUgY2FzZVxuLy8gYW5kIHRoZSBzZWNvbmQgYXJndW1lbnQgLSBmbGFnIC0gcHJlZmVycmVkIHR5cGUgaXMgYSBzdHJpbmdcbm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oaXQsIFMpe1xuICBpZighaXNPYmplY3QoaXQpKXJldHVybiBpdDtcbiAgdmFyIGZuLCB2YWw7XG4gIGlmKFMgJiYgdHlwZW9mIChmbiA9IGl0LnRvU3RyaW5nKSA9PSAnZnVuY3Rpb24nICYmICFpc09iamVjdCh2YWwgPSBmbi5jYWxsKGl0KSkpcmV0dXJuIHZhbDtcbiAgaWYodHlwZW9mIChmbiA9IGl0LnZhbHVlT2YpID09ICdmdW5jdGlvbicgJiYgIWlzT2JqZWN0KHZhbCA9IGZuLmNhbGwoaXQpKSlyZXR1cm4gdmFsO1xuICBpZighUyAmJiB0eXBlb2YgKGZuID0gaXQudG9TdHJpbmcpID09ICdmdW5jdGlvbicgJiYgIWlzT2JqZWN0KHZhbCA9IGZuLmNhbGwoaXQpKSlyZXR1cm4gdmFsO1xuICB0aHJvdyBUeXBlRXJyb3IoXCJDYW4ndCBjb252ZXJ0IG9iamVjdCB0byBwcmltaXRpdmUgdmFsdWVcIik7XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fdG8tcHJpbWl0aXZlLmpzXG4gKiogbW9kdWxlIGlkID0gMjFcbiAqKiBtb2R1bGUgY2h1bmtzID0gMFxuICoqLyIsIm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oYml0bWFwLCB2YWx1ZSl7XG4gIHJldHVybiB7XG4gICAgZW51bWVyYWJsZSAgOiAhKGJpdG1hcCAmIDEpLFxuICAgIGNvbmZpZ3VyYWJsZTogIShiaXRtYXAgJiAyKSxcbiAgICB3cml0YWJsZSAgICA6ICEoYml0bWFwICYgNCksXG4gICAgdmFsdWUgICAgICAgOiB2YWx1ZVxuICB9O1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX3Byb3BlcnR5LWRlc2MuanNcbiAqKiBtb2R1bGUgaWQgPSAyMlxuICoqIG1vZHVsZSBjaHVua3MgPSAwXG4gKiovIiwiJ3VzZSBzdHJpY3QnO1xuXG5cbmV4cG9ydCBkZWZhdWx0IGNsYXNzIFdpZGdldCB7XG4gICAgLyoqXG4gICAgICogRmlsdGVyIGhpc3RvcnlcbiAgICAgKiBAcGFyYW0ge1N0cmluZ30gc2VsZWN0b3JcbiAgICAgKi9cbiAgICBjb25zdHJ1Y3RvcihzZWxlY3Rvcikge1xuICAgICAgICB0aGlzLiRyb290ID0gJChzZWxlY3Rvcik7XG4gICAgICAgIHRoaXMubG9jYWxzID0gdGhpcy5fZ2V0RG9tKCk7XG5cbiAgICAgICAgdGhpcy5fYXNzaWduRXZlbnRzKCk7XG4gICAgfVxuXG4gICAgX2dldERvbSgpIHtcbiAgICAgICAgY29uc3QgJHJvb3QgPSB0aGlzLiRyb290O1xuXG4gICAgICAgIHJldHVybiB7XG4gICAgICAgICAgICAkbGlzdDogJHJvb3QuZmluZCgnW2RhdGEtZmlsdGVyLWxpc3RdJyksXG4gICAgICAgICAgICAkaXRlbXM6ICRyb290LmZpbmQoJ1tkYXRhLWZpbHRlci10ZXh0XScpLFxuICAgICAgICB9O1xuICAgIH1cblxuICAgIF9hc3NpZ25FdmVudHMoKSB7XG4gICAgICAgIHRoaXMuJHJvb3Qub24oJ2NsaWNrJywgJ1tkYXRhLWZpbHRlci1saW5rXScsIHRoaXMuX29uQ2xpY2tGaWx0ZXIuYmluZCh0aGlzKSk7XG4gICAgfVxuXG4gICAgX29uQ2xpY2tGaWx0ZXIoZSkge1xuICAgICAgICBjb25zdCAkbGluayA9ICQoZS50YXJnZXQpO1xuICAgICAgICBjb25zdCBmaWx0ZXJUZXh0ID0gJGxpbmsuZGF0YSgnZmlsdGVyLWxpbmsnKTtcblxuICAgICAgICBlLnByZXZlbnREZWZhdWx0KCk7XG5cbiAgICAgICAgaWYgKCRsaW5rLmhhc0NsYXNzKCdzdGF0ZV9zZWxlY3RlZCcpKSByZXR1cm47XG5cbiAgICAgICAgdGhpcy5zZXRBY3RpdmVMaW5rKCRsaW5rKTtcbiAgICAgICAgdGhpcy5maWx0ZXJMaXN0KGZpbHRlclRleHQpO1xuICAgIH07XG5cbiAgICAvKipcbiAgICAgKiBGaWx0ZXIgbGlzdCB0aHJvdWdoIHRleHRcbiAgICAgKiBAcGFyYW0ge1N0cmluZ30gZmlsdGVyVGV4dFxuICAgICAqL1xuICAgIGZpbHRlckxpc3QoZmlsdGVyVGV4dCkge1xuICAgICAgICBjb25zdCAkaXRlbXMgPSB0aGlzLmxvY2Fscy4kaXRlbXM7XG5cbiAgICAgICAgaWYgKGZpbHRlclRleHQgPT0gJ2FsbCcpIHtcbiAgICAgICAgICAgICRpdGVtcy5yZW1vdmVDbGFzcygnc3RhdGVfaGlkZGVuJyk7XG4gICAgICAgICAgICByZXR1cm47XG4gICAgICAgIH1cblxuICAgICAgICAkaXRlbXMuZWFjaCgoaW5kZXgsIGVsKSA9PiB7XG4gICAgICAgICAgICBjb25zdCAkZWwgPSAkKGVsKTtcbiAgICAgICAgICAgIGNvbnN0IGlzSGlkZGVuID0gJGVsLmRhdGEoJ2ZpbHRlci10ZXh0JykuaW5kZXhPZihmaWx0ZXJUZXh0KSA9PT0gLTE7XG5cbiAgICAgICAgICAgICRlbC50b2dnbGVDbGFzcygnc3RhdGVfaGlkZGVuJywgaXNIaWRkZW4pO1xuICAgICAgICB9KTtcbiAgICB9O1xuXG4gICAgLyoqXG4gICAgICogU2V0IGxpbmsgdG8gYWN0aXZlIGFuZCBkZWFjdGl2YXRlIG90aGVyXG4gICAgICogQHBhcmFtIHtqUXVlcnl9ICRlbFxuICAgICAqL1xuICAgIHNldEFjdGl2ZUxpbmsoJGVsKSB7XG4gICAgICAgICRlbC5hZGRDbGFzcygnc3RhdGVfc2VsZWN0ZWQnKVxuICAgICAgICAgICAgLnNpYmxpbmdzKCkucmVtb3ZlQ2xhc3MoJ3N0YXRlX3NlbGVjdGVkJyk7XG4gICAgfTtcbiAgICBcbiAgICAvLyBzdGF0aWNcbiAgICBzdGF0aWMgcGx1Z2luKHNlbGVjdG9yKSB7XG4gICAgICAgIGNvbnN0ICRlbGVtcyA9ICQoc2VsZWN0b3IpO1xuICAgICAgICBpZiAoISRlbGVtcy5sZW5ndGgpIHJldHVybjtcblxuICAgICAgICByZXR1cm4gJGVsZW1zLmVhY2goZnVuY3Rpb24gKGluZGV4LCBlbCkge1xuICAgICAgICAgICAgbGV0ICRlbGVtZW50ID0gJChlbCk7XG4gICAgICAgICAgICBsZXQgZGF0YSAgICAgPSAkZWxlbWVudC5kYXRhKCd3aWRnZXQnKTtcblxuICAgICAgICAgICAgaWYgKCFkYXRhKSB7XG4gICAgICAgICAgICAgICAgZGF0YSA9IG5ldyBXaWRnZXQoZWwpO1xuICAgICAgICAgICAgICAgICRlbGVtZW50LmRhdGEoJ3dpZGdldCcsIGRhdGEpO1xuICAgICAgICAgICAgfVxuICAgICAgICB9KVxuICAgIH1cbn1cblxuXG5cblxuXG4vKiogV0VCUEFDSyBGT09URVIgKipcbiAqKiAuL2Zyb250ZW5kL2pzL2NyZWRpdHMvd2lkZ2V0cy9fZmlsdGVyLWhpc3RvcnkuanNcbiAqKi8iLCIndXNlIHN0cmljdCc7XG5cbmltcG9ydCBGb3JtSGVscGVyIGZyb20gXCIuLy4uLy4uL2NvbW1vbi9fZm9ybS1oZWxwZXJcIjtcblxuLyoqXG4gKiBGb3JtIGZvciBzZW5kaW5nIGNyZWRpdFxuICovXG5leHBvcnQgZGVmYXVsdCBjbGFzcyBXaWRnZXR7XG5cbiAgICBjb25zdHJ1Y3RvcihzZWxlY3Rvcikge1xuICAgICAgICB0aGlzLiRyb290ID0gJChzZWxlY3Rvcik7XG4gICAgICAgIHRoaXMubG9jYWxzID0gdGhpcy5fZ2V0RG9tKCk7XG4gICAgICAgIHRoaXMudmFsaWRhdGlvbiA9IG5ldyBGb3JtSGVscGVyKHRoaXMuJHJvb3QuZmluZCgnaW5wdXQnKSk7XG5cbiAgICAgICAgdGhpcy5fYXNzaWduRXZlbnRzKCk7XG4gICAgfVxuXG4gICAgX2dldERvbSgpIHtcbiAgICAgICAgY29uc3QgJHJvb3QgPSB0aGlzLiRyb290O1xuXG4gICAgICAgIHJldHVybiB7XG4gICAgICAgICAgICAkY291bnQ6ICRyb290LmZpbmQoJ1tkYXRhLWNyZWRpY3QtY291bnRdJyksXG4gICAgICAgICAgICAkdmFsdWU6ICRyb290LmZpbmQoJ1tkYXRhLWNyZWRpdC12YWx1ZV0nKSxcbiAgICAgICAgICAgICR0bzogJHJvb3QuZmluZCgnW2RhdGEtY3JlZGl0LXRvXScpLFxuICAgICAgICAgICAgJG1lc3NhZ2U6ICRyb290LmZpbmQoJ1tkYXRhLWNyZWRpdC1tZXNzYWdlXScpLFxuICAgICAgICAgICAgJGVycm9yOiAkcm9vdC5maW5kKCdbZGF0YS1jcmVkaXQtZXJyb3JdJylcbiAgICAgICAgfTtcbiAgICB9XG5cbiAgICBfYXNzaWduRXZlbnRzKCkge1xuICAgICAgICB0aGlzLiRyb290XG4gICAgICAgICAgICAub24oJ2lucHV0JywgJ2lucHV0JywgKGUpID0+IHRoaXMubG9jYWxzLiRlcnJvci50ZXh0KCcnKSlcbiAgICAgICAgICAgIC5vbignc3VibWl0JywgdGhpcy5fb25TdWJtaXRGb3JtLmJpbmQodGhpcykpO1xuICAgIH1cblxuICAgIF9vblN1Ym1pdEZvcm0oZSkge1xuICAgICAgICBlLnByZXZlbnREZWZhdWx0KCk7XG5cbiAgICAgICAgaWYgKCF0aGlzLl9pc0Zvcm1WYWxpZCgpKSByZXR1cm4gZmFsc2U7XG5cbiAgICAgICAgdGhpcy5fc2VuZFJlcXVlc3QoKVxuICAgICAgICAgICAgLmRvbmUoKCkgPT4ge1xuICAgICAgICAgICAgICAgIHRoaXMudmFsaWRhdGlvbi5jbGVhckZvcm0oKTtcblxuICAgICAgICAgICAgICAgIHRoaXMuJHJvb3QuYWRkQ2xhc3MoJ2ItY3JlZGl0c19zdGF0ZV9zZW5kJyk7XG4gICAgICAgICAgICAgICAgc2V0VGltZW91dCgoKT0+IHtcbiAgICAgICAgICAgICAgICAgICAgdGhpcy4kcm9vdC5yZW1vdmVDbGFzcygnYi1jcmVkaXRzX3N0YXRlX3NlbmQnKTtcbiAgICAgICAgICAgICAgICB9LCAzMDAwKVxuICAgICAgICAgICAgfSlcbiAgICAgICAgICAgIC5mYWlsKChyZXNwb25zZSkgPT4ge1xuICAgICAgICAgICAgICAgIGNvbnN0IGRhdGEgPSAkLnBhcnNlSlNPTihyZXNwb25zZS5yZXNwb25zZVRleHQpLmRhdGE7XG4gICAgICAgICAgICAgICAgY29uc3QgZXJyb3JUZXh0ID0gdGhpcy52YWxpZGF0aW9uLmdldEVycm9yc1RleHQoZGF0YS5lcnJvcnMpO1xuXG4gICAgICAgICAgICAgICAgaWYgKCFkYXRhLmVycm9ycykgcmV0dXJuO1xuICAgICAgICAgICAgICAgIFxuICAgICAgICAgICAgICAgIHRoaXMubG9jYWxzLiRlcnJvci50ZXh0KGVycm9yVGV4dCk7XG4gICAgICAgICAgICAgICAgdGhpcy52YWxpZGF0aW9uLnNldEVycm9ycyhkYXRhLmVycm9ycyk7XG4gICAgICAgICAgICB9KVxuICAgIH1cblxuICAgIF9pc0Zvcm1WYWxpZCgpIHtcbiAgICAgICAgY29uc3QgbG9jYWxzID0gdGhpcy5sb2NhbHM7XG4gICAgICAgIGNvbnN0IGlzRW5vdWdoQ3JlZGl0cyA9IE51bWJlcihsb2NhbHMuJHZhbHVlLnZhbCgpKSA8PSBOdW1iZXIobG9jYWxzLiRjb3VudC50ZXh0KCkpO1xuICAgICAgICBsZXQgdmFsaWQgPSB0cnVlO1xuICAgICAgICBsZXQgZXJyb3JUZXh0ID0gJyc7XG5cbiAgICAgICAgaWYgKCF0aGlzLnZhbGlkYXRpb24uaXNWYWxpZElucHV0cygpKSB7XG4gICAgICAgICAgICB2YWxpZCA9IGZhbHNlO1xuICAgICAgICAgICAgZXJyb3JUZXh0ICs9IHRoaXMudmFsaWRhdGlvbi5nZXRFcnJvcnNUZXh0KCk7XG4gICAgICAgIH1cblxuICAgICAgICBpZiAoIWlzRW5vdWdoQ3JlZGl0cykge1xuICAgICAgICAgICAgdmFsaWQgPSBmYWxzZTtcbiAgICAgICAgICAgIGVycm9yVGV4dCArPSAnWW91IGNhbm7igJl0IGdpdmUgbW9yZSB0aGFuICcgKyBsb2NhbHMuJGNvdW50LnRleHQoKSArICcgY3JlZGl0cy4gJztcbiAgICAgICAgfVxuXG4gICAgICAgIGlmICghdmFsaWQpIHtcbiAgICAgICAgICAgIGxvY2Fscy4kZXJyb3IudGV4dChlcnJvclRleHQpO1xuICAgICAgICB9XG5cbiAgICAgICAgcmV0dXJuIHZhbGlkO1xuICAgIH1cblxuICAgIF9zZW5kUmVxdWVzdCgpIHtcbiAgICAgICAgcmV0dXJuICQucG9zdCh0aGlzLiRyb290LmF0dHIoJ2FjdGlvbicpLFxuICAgICAgICAgICAge1xuICAgICAgICAgICAgICAgIGdpdmU6IHRoaXMubG9jYWxzLiR2YWx1ZS52YWwoKSxcbiAgICAgICAgICAgICAgICB0bzogdGhpcy5sb2NhbHMuJHRvLnZhbCgpLFxuICAgICAgICAgICAgICAgIG1lc3NhZ2U6IHRoaXMubG9jYWxzLiRtZXNzYWdlLnZhbCgpXG4gICAgICAgICAgICB9XG4gICAgICAgICk7XG4gICAgfVxuXG5cbiAgICAvLyBzdGF0aWNcbiAgICBzdGF0aWMgcGx1Z2luKHNlbGVjdG9yKSB7XG4gICAgICAgIGNvbnN0ICRlbGVtcyA9ICQoc2VsZWN0b3IpO1xuICAgICAgICBpZiAoISRlbGVtcy5sZW5ndGgpIHJldHVybjtcblxuICAgICAgICByZXR1cm4gJGVsZW1zLmVhY2goZnVuY3Rpb24gKGluZGV4LCBlbCkge1xuICAgICAgICAgICAgbGV0ICRlbGVtZW50ID0gJChlbCk7XG4gICAgICAgICAgICBsZXQgZGF0YSAgICAgPSAkZWxlbWVudC5kYXRhKCd3aWRnZXQnKTtcblxuICAgICAgICAgICAgaWYgKCFkYXRhKSB7XG4gICAgICAgICAgICAgICAgZGF0YSA9IG5ldyBXaWRnZXQoZWwpO1xuICAgICAgICAgICAgICAgICRlbGVtZW50LmRhdGEoJ3dpZGdldCcsIGRhdGEpO1xuICAgICAgICAgICAgfVxuICAgICAgICB9KVxuICAgIH1cbn1cblxuXG5cblxuLyoqIFdFQlBBQ0sgRk9PVEVSICoqXG4gKiogLi9mcm9udGVuZC9qcy9jcmVkaXRzL3dpZGdldHMvX3NlbmQtY3JlZGl0cy5qc1xuICoqLyIsIid1c2Ugc3RyaWN0JztcblxuZXhwb3J0IGRlZmF1bHQgY2xhc3MgRm9ybUhlbHBlciB7XG4gICAgLyoqXG4gICAgICogVmFsaWRhdGUgZm9ybSB0aHJvdWdoIGlucHV0c1xuICAgICAqIEBwYXJhbSB7alF1ZXJ5fSAkaW5wdXRzXG4gICAgICovXG4gICAgY29uc3RydWN0b3IoJGlucHV0cykge1xuICAgICAgICB0aGlzLiRpbnB1dHMgPSAkaW5wdXRzO1xuICAgICAgICB0aGlzLmFyckVycm9ycyA9IFtdO1xuICAgICAgICB0aGlzLl9hc3NpZ25FdmVudHMoKTtcbiAgICB9XG5cbiAgICBfYXNzaWduRXZlbnRzKCkge1xuICAgICAgICB0aGlzLiRpbnB1dHMub24oJ2lucHV0JywgKGUpID0+IHRoaXMuX3JlbW92ZUVycm9yKCQoZS50YXJnZXQpKSk7XG4gICAgfVxuXG4gICAgaXNWYWxpZElucHV0cygpIHtcbiAgICAgICAgY29uc3QgJGlucHV0cyA9IHRoaXMuJGlucHV0cztcbiAgICAgICAgbGV0IGVycm9yID0gMDtcblxuICAgICAgICAkaW5wdXRzLmVhY2goKGluZGV4LCBpbnB1dCkgPT4ge1xuICAgICAgICAgICAgY29uc3QgJGlucHV0ID0gJChpbnB1dCk7XG5cbiAgICAgICAgICAgIGlmICghdGhpcy5faXNWYWxpZElucHV0KCRpbnB1dCkpIGVycm9yICs9IDE7XG4gICAgICAgIH0pO1xuICAgICAgICByZXR1cm4gQm9vbGVhbighZXJyb3IpO1xuICAgIH1cblxuICAgIC8qKlxuICAgICAqIENoZWNrIGdpdmVuIGlucHV0LCBpcyBpdCB2YWxpZD9cbiAgICAgKiBAcGFyYW0ge2pRdWVyeX0gJGlucHV0XG4gICAgICogQHJldHVybnMge2Jvb2xlYW59IC0gSXMgdmFsaWQgaW5wdXQ/XG4gICAgICovXG4gICAgX2lzVmFsaWRJbnB1dCgkaW5wdXQpIHtcbiAgICAgICAgY29uc3QgdmFsdWUgPSAkLnRyaW0oJGlucHV0LnZhbCgpKTtcblxuICAgICAgICBpZiAoIXZhbHVlKSB7XG4gICAgICAgICAgICB0aGlzLl9zZXRFcnJvcigkaW5wdXQsICdFbXB0eScpO1xuICAgICAgICAgICAgcmV0dXJuIGZhbHNlO1xuICAgICAgICB9XG5cbiAgICAgICAgaWYgKCgkaW5wdXQuaGFzQ2xhc3MoJ3R5cGUtZW1haWwnKSkgJiYgIXRoaXMuX2lzVmFsaWRFbWFpbCh2YWx1ZSkpIHtcbiAgICAgICAgICAgIHRoaXMuX3NldEVycm9yKCRpbnB1dCwgJ0VtYWlsIGlzIG5vdCB2YWxpZCcpO1xuICAgICAgICAgICAgcmV0dXJuIGZhbHNlO1xuICAgICAgICB9XG4gICAgICAgIHJldHVybiB0cnVlO1xuICAgIH1cblxuICAgIC8qKlxuICAgICAqIElzIEVtYWlsIHZhbGlkP1xuICAgICAqIEBwYXJhbSB7c3RyaW5nfSBlbWFpbFxuICAgICAqIEByZXR1cm5zIHtib29sZWFufVxuICAgICAqL1xuICAgIF9pc1ZhbGlkRW1haWwoZW1haWwpIHtcbiAgICAgICAgdmFyIHJlID0gL14oKFtePD4oKVxcW1xcXVxcXFwuLDs6XFxzQFwiXSsoXFwuW148PigpXFxbXFxdXFxcXC4sOzpcXHNAXCJdKykqKXwoXCIuK1wiKSlAKChcXFtbMC05XXsxLDN9XFwuWzAtOV17MSwzfVxcLlswLTldezEsM31cXC5bMC05XXsxLDN9XSl8KChbYS16QS1aXFwtMC05XStcXC4pK1thLXpBLVpdezIsfSkpJC87XG4gICAgICAgIHJldHVybiByZS50ZXN0KGVtYWlsKTtcbiAgICB9XG5cbiAgICAvKipcbiAgICAgKiBTZXQgZXJyb3IgZm9yIGlucHV0XG4gICAgICogQHBhcmFtIHtqUXVlcnl9ICRpbnB1dFxuICAgICAqIEBwYXJhbSB7c3RyaW5nfSBlcnJvclRleHRcbiAgICAgKi9cbiAgICBfc2V0RXJyb3IoJGlucHV0LCBlcnJvclRleHQpIHtcbiAgICAgICAgY29uc3QgJHBhcmVudCA9ICRpbnB1dC5wYXJlbnQoKTtcbiAgICAgICAgY29uc3QgJGVycm9yID0gJHBhcmVudC5maW5kKCcuYi1lcnJvcicpO1xuXG4gICAgICAgIGlmICgkZXJyb3IubGVuZ3RoKSByZXR1cm47XG5cbiAgICAgICAgJHBhcmVudC5hZGRDbGFzcygnYi1lcnJvcl9zaG93Jyk7XG4gICAgICAgICQoJzxkaXYgY2xhc3M9XCJiLWVycm9yXCIgLz4nKVxuICAgICAgICAgICAgLnRleHQoZXJyb3JUZXh0KVxuICAgICAgICAgICAgLnByZXBlbmRUbygkcGFyZW50KTtcblxuICAgICAgICB0aGlzLmFyckVycm9ycy5wdXNoKHtcbiAgICAgICAgICAgIG5hbWU6ICRpbnB1dC5hdHRyKCduYW1lJyksXG4gICAgICAgICAgICBlcnJvcjogZXJyb3JUZXh0XG4gICAgICAgIH0pXG4gICAgfVxuXG4gICAgLyoqXG4gICAgICogUmVtb3ZlIGVycm9yIGZvciBpbnB1dFxuICAgICAqIEBwYXJhbSB7alF1ZXJ5fSAkaW5wdXRcbiAgICAgKi9cbiAgICBfcmVtb3ZlRXJyb3IoJGlucHV0KSB7XG4gICAgICAgIGNvbnN0ICRwYXJlbnQgPSAkaW5wdXQucGFyZW50KCk7XG5cbiAgICAgICAgJHBhcmVudFxuICAgICAgICAgICAgLnJlbW92ZUNsYXNzKCdiLWVycm9yX3Nob3cnKVxuICAgICAgICAgICAgLmZpbmQoJy5iLWVycm9yJykucmVtb3ZlKCk7XG5cbiAgICAgICAgdGhpcy5hcnJFcnJvcnMgPSB0aGlzLmFyckVycm9ycy5maWx0ZXIoZnVuY3Rpb24gKGl0ZW0pIHtcbiAgICAgICAgICAgIHJldHVybiBpdGVtLm5hbWUgIT09ICRpbnB1dC5hdHRyKCduYW1lJylcbiAgICAgICAgfSlcbiAgICB9XG5cbiAgICAvKipcbiAgICAgKiBTZXQgZXJyb3JzXG4gICAgICogQHBhcmFtIHtBcnJheX0gZXJyb3JzIC0gW3tuYW1lOiBcImVtYWlsXCIsIGVycm9yOiBcImVtcHR5XCJ9LCB7bmFtZTogXCJwYXNzd29yZFwiLCBlcnJvcjogXCJlbXB0eVwifV1cbiAgICAgKi9cbiAgICBzZXRFcnJvcnMoZXJyb3JzKSB7XG4gICAgICAgIGVycm9ycy5mb3JFYWNoKChpdGVtKSA9PiB7XG4gICAgICAgICAgICBjb25zdCAkY3VycmVudElucHV0ID0gdGhpcy4kaW5wdXRzLmZpbHRlcignW25hbWU9XCInICsgaXRlbS5uYW1lICsgJ1wiXScpLmZpcnN0KCk7XG5cbiAgICAgICAgICAgIGlmICgkY3VycmVudElucHV0Lmxlbmd0aCkgdGhpcy5fc2V0RXJyb3IoJGN1cnJlbnRJbnB1dCwgaXRlbS5lcnJvcilcbiAgICAgICAgfSlcbiAgICB9XG5cbiAgICAvKipcbiAgICAgKiBHZXQgdHh0IHZlcnNpb24gb2YgYWxsIGVycm9yc1xuICAgICAqL1xuICAgIGdldEVycm9yc1RleHQoZXJyb3JzKSB7XG4gICAgICAgIGNvbnN0IGFyckVycm9ycyA9IGVycm9ycyB8fCB0aGlzLmFyckVycm9ycztcbiAgICAgICAgbGV0IGVycm9yVHh0ID0gJyc7XG5cbiAgICAgICAgYXJyRXJyb3JzLmZvckVhY2goKGl0ZW0pID0+IHtcbiAgICAgICAgICAgIGNvbnN0IG5hbWUgPSBpdGVtLm5hbWVbMF0udG9VcHBlckNhc2UoKSArIGl0ZW0ubmFtZS5zdWJzdHIoMSk7XG5cbiAgICAgICAgICAgIGVycm9yVHh0ICs9IGAke25hbWV9IHZhbHVlIGlzICR7aXRlbS5lcnJvci50b0xvd2VyQ2FzZSgpfS4gYDtcbiAgICAgICAgfSk7XG5cbiAgICAgICAgcmV0dXJuIGVycm9yVHh0O1xuICAgIH1cblxuICAgIC8qKlxuICAgICAqIFJlbW92ZSBhbGwgZXJyb3JzXG4gICAgICovXG4gICAgcmVtb3ZlRXJyb3JzKCkge1xuICAgICAgICB0aGlzLiRpbnB1dHMuZWFjaCgoaW5kZXgsIGVsKSA9PiB7XG4gICAgICAgICAgICBjb25zdCAkZWwgPSAkKGVsKTtcbiAgICAgICAgICAgIHRoaXMuX3JlbW92ZUVycm9yKCRlbClcbiAgICAgICAgfSlcbiAgICB9XG5cbiAgICBjbGVhckZvcm0oKSB7XG4gICAgICAgIHRoaXMuJGlucHV0cy5lYWNoKChpbmRleCwgZWwpID0+IHtcbiAgICAgICAgICAgIGNvbnN0ICRlbCA9ICQoZWwpO1xuICAgICAgICAgICAgaWYgKCEkZWwuYXR0cihcImRpc2FibGVkXCIpKSAgJGVsLnZhbCgnJyk7XG4gICAgICAgIH0pXG4gICAgfVxufVxuXG5cblxuLyoqIFdFQlBBQ0sgRk9PVEVSICoqXG4gKiogLi9mcm9udGVuZC9qcy9jb21tb24vX2Zvcm0taGVscGVyLmpzXG4gKiovIl0sInNvdXJjZVJvb3QiOiIifQ==