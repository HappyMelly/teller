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

	module.exports = __webpack_require__(24);


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

/***/ },
/* 24 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';
	
	var _asyncTabs = __webpack_require__(25);
	
	var _asyncTabs2 = _interopRequireDefault(_asyncTabs);
	
	var _filterHistory = __webpack_require__(26);
	
	var _filterHistory2 = _interopRequireDefault(_filterHistory);
	
	var _sendCredits = __webpack_require__(27);
	
	var _sendCredits2 = _interopRequireDefault(_sendCredits);
	
	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }
	
	$(function () {
	    _asyncTabs2.default.plugin('.js-credits-tabs');
	
	    App.events.sub('hmt.asynctab.shown', function () {
	        _filterHistory2.default.plugin('.js-credit-history');
	        _sendCredits2.default.plugin('.js-form-credit');
	    });
	});

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
	            var url = $link.attr('data-href');
	            var target = $link.attr('href');
	            var self = this;
	
	            self._loadContent(url, target).done(function () {
	                $link.addClass('state_active').siblings().removeClass('state_active');
	                $link.tab('show');
	
	                App.events.pub('hmt.asynctab.shown');
	            });
	        }
	
	        /**
	         *  Load content and insert into target div
	         * @param {String} url      - url of loaded content
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
/* 26 */
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
/* 27 */
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
	
	/**
	 * Form for sending credit
	 */
	
	var Widget = function () {
	    function Widget(selector) {
	        (0, _classCallCheck3.default)(this, Widget);
	
	        this.$root = $(selector);
	        this.locals = this._getDom();
	        this.validation = new _formHelper2.default(this.$root.find('.b-credits__input'));
	
	        if (!Boolean($.fn.autocomplete)) {
	            console.log('jQuery autocomplete plugin is not include into page');
	            return;
	        }
	        this._initAutoComplete();
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
	                $toData: $root.find('[data-credit-to-data]'),
	                $message: $root.find('[data-credit-message]'),
	                $error: $root.find('[data-credit-error]')
	            };
	        }
	    }, {
	        key: '_initAutoComplete',
	        value: function _initAutoComplete() {
	            var locals = this.locals;
	            var url = jsRoutes.controllers.core.Organisations.search().url;
	
	            locals.$to.autocomplete({
	                serviceUrl: url,
	                paramName: 'query',
	                minChars: 3,
	                preserveInput: true,
	                onSelect: function onSelect(suggestion) {
	                    locals.$to.val(suggestion.value);
	                    locals.$toData.val(suggestion.data);
	                    return true;
	                }
	            });
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
	            e.preventDefault();
	            var self = this;
	
	            if (!self._isFormValid()) return false;
	
	            self._sendRequest().done(function () {
	                self.validation.clearForm();
	
	                self.$root.addClass('b-credits_state_send');
	                setTimeout(function () {
	                    self.$root.removeClass('b-credits_state_send');
	                }, 3000);
	            }).fail(function (response) {
	                var data = $.parseJSON(response.responseText).data;
	                var errorText = self.validation.getErrorsText(data.errors);
	
	                if (!data.errors) return;
	
	                self.locals.$error.text(errorText);
	                self.validation.setErrors(data.errors);
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
	                todata: this.locals.$toData.val(),
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

/***/ }
/******/ ]);
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIndlYnBhY2s6Ly8vd2VicGFjay9ib290c3RyYXAgYWIyZTViZDJjODJmOGE5YjU4ZTY/YWVlNyIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvaGVscGVycy9jbGFzc0NhbGxDaGVjay5qcz8yMWFmIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9oZWxwZXJzL2NyZWF0ZUNsYXNzLmpzPzFkZmUiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL2NvcmUtanMvb2JqZWN0L2RlZmluZS1wcm9wZXJ0eS5qcz81ZjcwIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9mbi9vYmplY3QvZGVmaW5lLXByb3BlcnR5LmpzPzRkMzMiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvZXM2Lm9iamVjdC5kZWZpbmUtcHJvcGVydHkuanM/OGJkZSIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZXhwb3J0LmpzPzhlNDAiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2dsb2JhbC5qcz8zYzUyIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19jb3JlLmpzP2Q2MTEiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2N0eC5qcz80ZTU5Iiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19hLWZ1bmN0aW9uLmpzPzA2OTkiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2hpZGUuanM/MGQyZSIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fb2JqZWN0LWRwLmpzPzU5ODYiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2FuLW9iamVjdC5qcz8zYWYyIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19pcy1vYmplY3QuanM/Y2ZkYSIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9faWU4LWRvbS1kZWZpbmUuanM/YjEwMiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZGVzY3JpcHRvcnMuanM/YzBmNSIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZmFpbHMuanM/YzZkZCIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZG9tLWNyZWF0ZS5qcz82MTJmIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL190by1wcmltaXRpdmUuanM/MWE2NSIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fcHJvcGVydHktZGVzYy5qcz8yNTZiIiwid2VicGFjazovLy8uL2Zyb250ZW5kL2pzL2NvbW1vbi9fZm9ybS1oZWxwZXIuanM/ODYzNiIsIndlYnBhY2s6Ly8vLi9mcm9udGVuZC9qcy9jcmVkaXRzL2NyZWRpdC1wYWdlLmpzIiwid2VicGFjazovLy8uL2Zyb250ZW5kL2pzL2NvbW1vbi9fYXN5bmMtdGFicy5qcyIsIndlYnBhY2s6Ly8vLi9mcm9udGVuZC9qcy9jcmVkaXRzL3dpZGdldHMvX2ZpbHRlci1oaXN0b3J5LmpzIiwid2VicGFjazovLy8uL2Zyb250ZW5kL2pzL2NyZWRpdHMvd2lkZ2V0cy9fc2VuZC1jcmVkaXRzLmpzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiI7QUFBQTtBQUNBOztBQUVBO0FBQ0E7O0FBRUE7QUFDQTtBQUNBOztBQUVBO0FBQ0E7QUFDQSx1QkFBZTtBQUNmO0FBQ0E7QUFDQTs7QUFFQTtBQUNBOztBQUVBO0FBQ0E7O0FBRUE7QUFDQTtBQUNBOzs7QUFHQTtBQUNBOztBQUVBO0FBQ0E7O0FBRUE7QUFDQTs7QUFFQTtBQUNBOzs7Ozs7Ozs7Ozs7Ozs7O0FDdENBOztBQUVBOztBQUVBO0FBQ0E7QUFDQTtBQUNBO0FBQ0EsRzs7Ozs7O0FDUkE7O0FBRUE7O0FBRUE7O0FBRUE7O0FBRUEsdUNBQXNDLHVDQUF1QyxnQkFBZ0I7O0FBRTdGO0FBQ0E7QUFDQSxvQkFBbUIsa0JBQWtCO0FBQ3JDO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBOztBQUVBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQSxFQUFDLEc7Ozs7OztBQzFCRCxtQkFBa0IsdUQ7Ozs7OztBQ0FsQjtBQUNBO0FBQ0E7QUFDQTtBQUNBLEc7Ozs7OztBQ0pBO0FBQ0E7QUFDQSxzRUFBdUUsMENBQTBDLEU7Ozs7OztBQ0ZqSDtBQUNBO0FBQ0E7QUFDQTtBQUNBOztBQUVBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0Esb0VBQW1FO0FBQ25FO0FBQ0Esc0ZBQXFGO0FBQ3JGO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQSxZQUFXO0FBQ1gsVUFBUztBQUNUO0FBQ0E7QUFDQTtBQUNBO0FBQ0EsTUFBSztBQUNMO0FBQ0E7QUFDQSxnREFBK0M7QUFDL0M7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0EsZUFBYztBQUNkLGVBQWM7QUFDZCxlQUFjO0FBQ2QsZUFBYztBQUNkLGdCQUFlO0FBQ2YsZ0JB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WCxDQUZnQzs7QUFJcEMscUJBQUksQ0FBQyxJQUFELEVBQU87QUFDUCw0QkFBTyxJQUFJLE1BQUosQ0FBVyxFQUFYLENBQVAsQ0FETztBQUVQLDhCQUFTLElBQVQsQ0FBYyxRQUFkLEVBQXdCLElBQXhCLEVBRk87a0JBQVg7Y0FKZSxDQUFuQixDQUpvQjs7O1lBakhQIiwiZmlsZSI6ImNyZWRpdC1wYWdlLmpzIiwic291cmNlc0NvbnRlbnQiOlsiIFx0Ly8gVGhlIG1vZHVsZSBjYWNoZVxuIFx0dmFyIGluc3RhbGxlZE1vZHVsZXMgPSB7fTtcblxuIFx0Ly8gVGhlIHJlcXVpcmUgZnVuY3Rpb25cbiBcdGZ1bmN0aW9uIF9fd2VicGFja19yZXF1aXJlX18obW9kdWxlSWQpIHtcblxuIFx0XHQvLyBDaGVjayBpZiBtb2R1bGUgaXMgaW4gY2FjaGVcbiBcdFx0aWYoaW5zdGFsbGVkTW9kdWxlc1ttb2R1bGVJZF0pXG4gXHRcdFx0cmV0dXJuIGluc3RhbGxlZE1vZHVsZXNbbW9kdWxlSWRdLmV4cG9ydHM7XG5cbiBcdFx0Ly8gQ3JlYXRlIGEgbmV3IG1vZHVsZSAoYW5kIHB1dCBpdCBpbnRvIHRoZSBjYWNoZSlcbiBcdFx0dmFyIG1vZHVsZSA9IGluc3RhbGxlZE1vZHVsZXNbbW9kdWxlSWRdID0ge1xuIFx0XHRcdGV4cG9ydHM6IHt9LFxuIFx0XHRcdGlkOiBtb2R1bGVJZCxcbiBcdFx0XHRsb2FkZWQ6IGZhbHNlXG4gXHRcdH07XG5cbiBcdFx0Ly8gRXhlY3V0ZSB0aGUgbW9kdWxlIGZ1bmN0aW9uXG4gXHRcdG1vZHVsZXNbbW9kdWxlSWRdLmNhbGwobW9kdWxlLmV4cG9ydHMsIG1vZHVsZSwgbW9kdWxlLmV4cG9ydHMsIF9fd2VicGFja19yZXF1aXJlX18pO1xuXG4gXHRcdC8vIEZsYWcgdGhlIG1vZHVsZSBhcyBsb2FkZWRcbiBcdFx0bW9kdWxlLmxvYWRlZCA9IHRydWU7XG5cbiBcdFx0Ly8gUmV0dXJuIHRoZSBleHBvcnRzIG9mIHRoZSBtb2R1bGVcbiBcdFx0cmV0dXJuIG1vZHVsZS5leHBvcnRzO1xuIFx0fVxuXG5cbiBcdC8vIGV4cG9zZSB0aGUgbW9kdWxlcyBvYmplY3QgKF9fd2VicGFja19tb2R1bGVzX18pXG4gXHRfX3dlYnBhY2tfcmVxdWlyZV9fLm0gPSBtb2R1bGVzO1xuXG4gXHQvLyBleHBvc2UgdGhlIG1vZHVsZSBjYWNoZVxuIFx0X193ZWJwYWNrX3JlcXVpcmVfXy5jID0gaW5zdGFsbGVkTW9kdWxlcztcblxuIFx0Ly8gX193ZWJwYWNrX3B1YmxpY19wYXRoX19cbiBcdF9fd2VicGFja19yZXF1aXJlX18ucCA9IFwiXCI7XG5cbiBcdC8vIExvYWQgZW50cnkgbW9kdWxlIGFuZCByZXR1cm4gZXhwb3J0c1xuIFx0cmV0dXJuIF9fd2VicGFja19yZXF1aXJlX18oMCk7XG5cblxuXG4vKiogV0VCUEFDSyBGT09URVIgKipcbiAqKiB3ZWJwYWNrL2Jvb3RzdHJhcCBhYjJlNWJkMmM4MmY4YTliNThlNlxuICoqLyIsIlwidXNlIHN0cmljdFwiO1xuXG5leHBvcnRzLl9fZXNNb2R1bGUgPSB0cnVlO1xuXG5leHBvcnRzLmRlZmF1bHQgPSBmdW5jdGlvbiAoaW5zdGFuY2UsIENvbnN0cnVjdG9yKSB7XG4gIGlmICghKGluc3RhbmNlIGluc3RhbmNlb2YgQ29uc3RydWN0b3IpKSB7XG4gICAgdGhyb3cgbmV3IFR5cGVFcnJvcihcIkNhbm5vdCBjYWxsIGEgY2xhc3MgYXMgYSBmdW5jdGlvblwiKTtcbiAgfVxufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL2hlbHBlcnMvY2xhc3NDYWxsQ2hlY2suanNcbiAqKiBtb2R1bGUgaWQgPSAzXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMVxuICoqLyIsIlwidXNlIHN0cmljdFwiO1xuXG5leHBvcnRzLl9fZXNNb2R1bGUgPSB0cnVlO1xuXG52YXIgX2RlZmluZVByb3BlcnR5ID0gcmVxdWlyZShcImJhYmVsLXJ1bnRpbWUvY29yZS1qcy9vYmplY3QvZGVmaW5lLXByb3BlcnR5XCIpO1xuXG52YXIgX2RlZmluZVByb3BlcnR5MiA9IF9pbnRlcm9wUmVxdWlyZURlZmF1bHQoX2RlZmluZVByb3BlcnR5KTtcblxuZnVuY3Rpb24gX2ludGVyb3BSZXF1aXJlRGVmYXVsdChvYmopIHsgcmV0dXJuIG9iaiAmJiBvYmouX19lc01vZHVsZSA/IG9iaiA6IHsgZGVmYXVsdDogb2JqIH07IH1cblxuZXhwb3J0cy5kZWZhdWx0ID0gZnVuY3Rpb24gKCkge1xuICBmdW5jdGlvbiBkZWZpbmVQcm9wZXJ0aWVzKHRhcmdldCwgcHJvcHMpIHtcbiAgICBmb3IgKHZhciBpID0gMDsgaSA8IHByb3BzLmxlbmd0aDsgaSsrKSB7XG4gICAgICB2YXIgZGVzY3JpcHRvciA9IHByb3BzW2ldO1xuICAgICAgZGVzY3JpcHRvci5lbnVtZXJhYmxlID0gZGVzY3JpcHRvci5lbnVtZXJhYmxlIHx8IGZhbHNlO1xuICAgICAgZGVzY3JpcHRvci5jb25maWd1cmFibGUgPSB0cnVlO1xuICAgICAgaWYgKFwidmFsdWVcIiBpbiBkZXNjcmlwdG9yKSBkZXNjcmlwdG9yLndyaXRhYmxlID0gdHJ1ZTtcbiAgICAgICgwLCBfZGVmaW5lUHJvcGVydHkyLmRlZmF1bHQpKHRhcmdldCwgZGVzY3JpcHRvci5rZXksIGRlc2NyaXB0b3IpO1xuICAgIH1cbiAgfVxuXG4gIHJldHVybiBmdW5jdGlvbiAoQ29uc3RydWN0b3IsIHByb3RvUHJvcHMsIHN0YXRpY1Byb3BzKSB7XG4gICAgaWYgKHByb3RvUHJvcHMpIGRlZmluZVByb3BlcnRpZXMoQ29uc3RydWN0b3IucHJvdG90eXBlLCBwcm90b1Byb3BzKTtcbiAgICBpZiAoc3RhdGljUHJvcHMpIGRlZmluZVByb3BlcnRpZXMoQ29uc3RydWN0b3IsIHN0YXRpY1Byb3BzKTtcbiAgICByZXR1cm4gQ29uc3RydWN0b3I7XG4gIH07XG59KCk7XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9oZWxwZXJzL2NyZWF0ZUNsYXNzLmpzXG4gKiogbW9kdWxlIGlkID0gNFxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDFcbiAqKi8iLCJtb2R1bGUuZXhwb3J0cyA9IHsgXCJkZWZhdWx0XCI6IHJlcXVpcmUoXCJjb3JlLWpzL2xpYnJhcnkvZm4vb2JqZWN0L2RlZmluZS1wcm9wZXJ0eVwiKSwgX19lc01vZHVsZTogdHJ1ZSB9O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvY29yZS1qcy9vYmplY3QvZGVmaW5lLXByb3BlcnR5LmpzXG4gKiogbW9kdWxlIGlkID0gNVxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDFcbiAqKi8iLCJyZXF1aXJlKCcuLi8uLi9tb2R1bGVzL2VzNi5vYmplY3QuZGVmaW5lLXByb3BlcnR5Jyk7XG52YXIgJE9iamVjdCA9IHJlcXVpcmUoJy4uLy4uL21vZHVsZXMvX2NvcmUnKS5PYmplY3Q7XG5tb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uIGRlZmluZVByb3BlcnR5KGl0LCBrZXksIGRlc2Mpe1xuICByZXR1cm4gJE9iamVjdC5kZWZpbmVQcm9wZXJ0eShpdCwga2V5LCBkZXNjKTtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9mbi9vYmplY3QvZGVmaW5lLXByb3BlcnR5LmpzXG4gKiogbW9kdWxlIGlkID0gNlxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDFcbiAqKi8iLCJ2YXIgJGV4cG9ydCA9IHJlcXVpcmUoJy4vX2V4cG9ydCcpO1xyXG4vLyAxOS4xLjIuNCAvIDE1LjIuMy42IE9iamVjdC5kZWZpbmVQcm9wZXJ0eShPLCBQLCBBdHRyaWJ1dGVzKVxyXG4kZXhwb3J0KCRleHBvcnQuUyArICRleHBvcnQuRiAqICFyZXF1aXJlKCcuL19kZXNjcmlwdG9ycycpLCAnT2JqZWN0Jywge2RlZmluZVByb3BlcnR5OiByZXF1aXJlKCcuL19vYmplY3QtZHAnKS5mfSk7XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL2VzNi5vYmplY3QuZGVmaW5lLXByb3BlcnR5LmpzXG4gKiogbW9kdWxlIGlkID0gN1xuICoqIG1vZHVsZSBjaHVua3MgPSAwIDFcbiAqKi8iLCJ2YXIgZ2xvYmFsICAgID0gcmVxdWlyZSgnLi9fZ2xvYmFsJylcbiAgLCBjb3JlICAgICAgPSByZXF1aXJlKCcuL19jb3JlJylcbiAgLCBjdHggICAgICAgPSByZXF1aXJlKCcuL19jdHgnKVxuICAsIGhpZGUgICAgICA9IHJlcXVpcmUoJy4vX2hpZGUnKVxuICAsIFBST1RPVFlQRSA9ICdwcm90b3R5cGUnO1xuXG52YXIgJGV4cG9ydCA9IGZ1bmN0aW9uKHR5cGUsIG5hbWUsIHNvdXJjZSl7XG4gIHZhciBJU19GT1JDRUQgPSB0eXBlICYgJGV4cG9ydC5GXG4gICAgLCBJU19HTE9CQUwgPSB0eXBlICYgJGV4cG9ydC5HXG4gICAgLCBJU19TVEFUSUMgPSB0eXBlICYgJGV4cG9ydC5TXG4gICAgLCBJU19QUk9UTyAgPSB0eXBlICYgJGV4cG9ydC5QXG4gICAgLCBJU19CSU5EICAgPSB0eXBlICYgJGV4cG9ydC5CXG4gICAgLCBJU19XUkFQICAgPSB0eXBlICYgJGV4cG9ydC5XXG4gICAgLCBleHBvcnRzICAgPSBJU19HTE9CQUwgPyBjb3JlIDogY29yZVtuYW1lXSB8fCAoY29yZVtuYW1lXSA9IHt9KVxuICAgICwgZXhwUHJvdG8gID0gZXhwb3J0c1tQUk9UT1RZUEVdXG4gICAgLCB0YXJnZXQgICAgPSBJU19HTE9CQUwgPyBnbG9iYWwgOiBJU19TVEFUSUMgPyBnbG9iYWxbbmFtZV0gOiAoZ2xvYmFsW25hbWVdIHx8IHt9KVtQUk9UT1RZUEVdXG4gICAgLCBrZXksIG93biwgb3V0O1xuICBpZihJU19HTE9CQUwpc291cmNlID0gbmFtZTtcbiAgZm9yKGtleSBpbiBzb3VyY2Upe1xuICAgIC8vIGNvbnRhaW5zIGluIG5hdGl2ZVxuICAgIG93biA9ICFJU19GT1JDRUQgJiYgdGFyZ2V0ICYmIHRhcmdldFtrZXldICE9PSB1bmRlZmluZWQ7XG4gICAgaWYob3duICYmIGtleSBpbiBleHBvcnRzKWNvbnRpbnVlO1xuICAgIC8vIGV4cG9ydCBuYXRpdmUgb3IgcGFzc2VkXG4gICAgb3V0ID0gb3duID8gdGFyZ2V0W2tleV0gOiBzb3VyY2Vba2V5XTtcbiAgICAvLyBwcmV2ZW50IGdsb2JhbCBwb2xsdXRpb24gZm9yIG5hbWVzcGFjZXNcbiAgICBleHBvcnRzW2tleV0gPSBJU19HTE9CQUwgJiYgdHlwZW9mIHRhcmdldFtrZXldICE9ICdmdW5jdGlvbicgPyBzb3VyY2Vba2V5XVxuICAgIC8vIGJpbmQgdGltZXJzIHRvIGdsb2JhbCBmb3IgY2FsbCBmcm9tIGV4cG9ydCBjb250ZXh0XG4gICAgOiBJU19CSU5EICYmIG93biA/IGN0eChvdXQsIGdsb2JhbClcbiAgICAvLyB3cmFwIGdsb2JhbCBjb25zdHJ1Y3RvcnMgZm9yIHByZXZlbnQgY2hhbmdlIHRoZW0gaW4gbGlicmFyeVxuICAgIDogSVNfV1JBUCAmJiB0YXJnZXRba2V5XSA9PSBvdXQgPyAoZnVuY3Rpb24oQyl7XG4gICAgICB2YXIgRiA9IGZ1bmN0aW9uKGEsIGIsIGMpe1xuICAgICAgICBpZih0aGlzIGluc3RhbmNlb2YgQyl7XG4gICAgICAgICAgc3dpdGNoKGFyZ3VtZW50cy5sZW5ndGgpe1xuICAgICAgICAgICAgY2FzZSAwOiByZXR1cm4gbmV3IEM7XG4gICAgICAgICAgICBjYXNlIDE6IHJldHVybiBuZXcgQyhhKTtcbiAgICAgICAgICAgIGNhc2UgMjogcmV0dXJuIG5ldyBDKGEsIGIpO1xuICAgICAgICAgIH0gcmV0dXJuIG5ldyBDKGEsIGIsIGMpO1xuICAgICAgICB9IHJldHVybiBDLmFwcGx5KHRoaXMsIGFyZ3VtZW50cyk7XG4gICAgICB9O1xuICAgICAgRltQUk9UT1RZUEVdID0gQ1tQUk9UT1RZUEVdO1xuICAgICAgcmV0dXJuIEY7XG4gICAgLy8gbWFrZSBzdGF0aWMgdmVyc2lvbnMgZm9yIHByb3RvdHlwZSBtZXRob2RzXG4gICAgfSkob3V0KSA6IElTX1BST1RPICYmIHR5cGVvZiBvdXQgPT0gJ2Z1bmN0aW9uJyA/IGN0eChGdW5jdGlvbi5jYWxsLCBvdXQpIDogb3V0O1xuICAgIC8vIGV4cG9ydCBwcm90byBtZXRob2RzIHRvIGNvcmUuJUNPTlNUUlVDVE9SJS5tZXRob2RzLiVOQU1FJVxuICAgIGlmKElTX1BST1RPKXtcbiAgICAgIChleHBvcnRzLnZpcnR1YWwgfHwgKGV4cG9ydHMudmlydHVhbCA9IHt9KSlba2V5XSA9IG91dDtcbiAgICAgIC8vIGV4cG9ydCBwcm90byBtZXRob2RzIHRvIGNvcmUuJUNPTlNUUlVDVE9SJS5wcm90b3R5cGUuJU5BTUUlXG4gICAgICBpZih0eXBlICYgJGV4cG9ydC5SICYmIGV4cFByb3RvICYmICFleHBQcm90b1trZXldKWhpZGUoZXhwUHJvdG8sIGtleSwgb3V0KTtcbiAgICB9XG4gIH1cbn07XG4vLyB0eXBlIGJpdG1hcFxuJGV4cG9ydC5GID0gMTsgICAvLyBmb3JjZWRcbiRleHBvcnQuRyA9IDI7ICAgLy8gZ2xvYmFsXG4kZXhwb3J0LlMgPSA0OyAgIC8vIHN0YXRpY1xuJGV4cG9ydC5QID0gODsgICAvLyBwcm90b1xuJGV4cG9ydC5CID0gMTY7ICAvLyBiaW5kXG4kZXhwb3J0LlcgPSAzMjsgIC8vIHdyYXBcbiRleHBvcnQuVSA9IDY0OyAgLy8gc2FmZVxuJGV4cG9ydC5SID0gMTI4OyAvLyByZWFsIHByb3RvIG1ldGhvZCBmb3IgYGxpYnJhcnlgIFxubW9kdWxlLmV4cG9ydHMgPSAkZXhwb3J0O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZXhwb3J0LmpzXG4gKiogbW9kdWxlIGlkID0gOFxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDFcbiAqKi8iLCIvLyBodHRwczovL2dpdGh1Yi5jb20vemxvaXJvY2svY29yZS1qcy9pc3N1ZXMvODYjaXNzdWVjb21tZW50LTExNTc1OTAyOFxudmFyIGdsb2JhbCA9IG1vZHVsZS5leHBvcnRzID0gdHlwZW9mIHdpbmRvdyAhPSAndW5kZWZpbmVkJyAmJiB3aW5kb3cuTWF0aCA9PSBNYXRoXG4gID8gd2luZG93IDogdHlwZW9mIHNlbGYgIT0gJ3VuZGVmaW5lZCcgJiYgc2VsZi5NYXRoID09IE1hdGggPyBzZWxmIDogRnVuY3Rpb24oJ3JldHVybiB0aGlzJykoKTtcbmlmKHR5cGVvZiBfX2cgPT0gJ251bWJlcicpX19nID0gZ2xvYmFsOyAvLyBlc2xpbnQtZGlzYWJsZS1saW5lIG5vLXVuZGVmXG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19nbG9iYWwuanNcbiAqKiBtb2R1bGUgaWQgPSA5XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMVxuICoqLyIsInZhciBjb3JlID0gbW9kdWxlLmV4cG9ydHMgPSB7dmVyc2lvbjogJzIuMi4wJ307XG5pZih0eXBlb2YgX19lID09ICdudW1iZXInKV9fZSA9IGNvcmU7IC8vIGVzbGludC1kaXNhYmxlLWxpbmUgbm8tdW5kZWZcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2NvcmUuanNcbiAqKiBtb2R1bGUgaWQgPSAxMFxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDFcbiAqKi8iLCIvLyBvcHRpb25hbCAvIHNpbXBsZSBjb250ZXh0IGJpbmRpbmdcbnZhciBhRnVuY3Rpb24gPSByZXF1aXJlKCcuL19hLWZ1bmN0aW9uJyk7XG5tb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGZuLCB0aGF0LCBsZW5ndGgpe1xuICBhRnVuY3Rpb24oZm4pO1xuICBpZih0aGF0ID09PSB1bmRlZmluZWQpcmV0dXJuIGZuO1xuICBzd2l0Y2gobGVuZ3RoKXtcbiAgICBjYXNlIDE6IHJldHVybiBmdW5jdGlvbihhKXtcbiAgICAgIHJldHVybiBmbi5jYWxsKHRoYXQsIGEpO1xuICAgIH07XG4gICAgY2FzZSAyOiByZXR1cm4gZnVuY3Rpb24oYSwgYil7XG4gICAgICByZXR1cm4gZm4uY2FsbCh0aGF0LCBhLCBiKTtcbiAgICB9O1xuICAgIGNhc2UgMzogcmV0dXJuIGZ1bmN0aW9uKGEsIGIsIGMpe1xuICAgICAgcmV0dXJuIGZuLmNhbGwodGhhdCwgYSwgYiwgYyk7XG4gICAgfTtcbiAgfVxuICByZXR1cm4gZnVuY3Rpb24oLyogLi4uYXJncyAqLyl7XG4gICAgcmV0dXJuIGZuLmFwcGx5KHRoYXQsIGFyZ3VtZW50cyk7XG4gIH07XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fY3R4LmpzXG4gKiogbW9kdWxlIGlkID0gMTFcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxXG4gKiovIiwibW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihpdCl7XG4gIGlmKHR5cGVvZiBpdCAhPSAnZnVuY3Rpb24nKXRocm93IFR5cGVFcnJvcihpdCArICcgaXMgbm90IGEgZnVuY3Rpb24hJyk7XG4gIHJldHVybiBpdDtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19hLWZ1bmN0aW9uLmpzXG4gKiogbW9kdWxlIGlkID0gMTJcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxXG4gKiovIiwidmFyIGRQICAgICAgICAgPSByZXF1aXJlKCcuL19vYmplY3QtZHAnKVxuICAsIGNyZWF0ZURlc2MgPSByZXF1aXJlKCcuL19wcm9wZXJ0eS1kZXNjJyk7XG5tb2R1bGUuZXhwb3J0cyA9IHJlcXVpcmUoJy4vX2Rlc2NyaXB0b3JzJykgPyBmdW5jdGlvbihvYmplY3QsIGtleSwgdmFsdWUpe1xuICByZXR1cm4gZFAuZihvYmplY3QsIGtleSwgY3JlYXRlRGVzYygxLCB2YWx1ZSkpO1xufSA6IGZ1bmN0aW9uKG9iamVjdCwga2V5LCB2YWx1ZSl7XG4gIG9iamVjdFtrZXldID0gdmFsdWU7XG4gIHJldHVybiBvYmplY3Q7XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9faGlkZS5qc1xuICoqIG1vZHVsZSBpZCA9IDEzXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMVxuICoqLyIsInZhciBhbk9iamVjdCAgICAgICA9IHJlcXVpcmUoJy4vX2FuLW9iamVjdCcpXG4gICwgSUU4X0RPTV9ERUZJTkUgPSByZXF1aXJlKCcuL19pZTgtZG9tLWRlZmluZScpXG4gICwgdG9QcmltaXRpdmUgICAgPSByZXF1aXJlKCcuL190by1wcmltaXRpdmUnKVxuICAsIGRQICAgICAgICAgICAgID0gT2JqZWN0LmRlZmluZVByb3BlcnR5O1xuXG5leHBvcnRzLmYgPSByZXF1aXJlKCcuL19kZXNjcmlwdG9ycycpID8gT2JqZWN0LmRlZmluZVByb3BlcnR5IDogZnVuY3Rpb24gZGVmaW5lUHJvcGVydHkoTywgUCwgQXR0cmlidXRlcyl7XG4gIGFuT2JqZWN0KE8pO1xuICBQID0gdG9QcmltaXRpdmUoUCwgdHJ1ZSk7XG4gIGFuT2JqZWN0KEF0dHJpYnV0ZXMpO1xuICBpZihJRThfRE9NX0RFRklORSl0cnkge1xuICAgIHJldHVybiBkUChPLCBQLCBBdHRyaWJ1dGVzKTtcbiAgfSBjYXRjaChlKXsgLyogZW1wdHkgKi8gfVxuICBpZignZ2V0JyBpbiBBdHRyaWJ1dGVzIHx8ICdzZXQnIGluIEF0dHJpYnV0ZXMpdGhyb3cgVHlwZUVycm9yKCdBY2Nlc3NvcnMgbm90IHN1cHBvcnRlZCEnKTtcbiAgaWYoJ3ZhbHVlJyBpbiBBdHRyaWJ1dGVzKU9bUF0gPSBBdHRyaWJ1dGVzLnZhbHVlO1xuICByZXR1cm4gTztcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19vYmplY3QtZHAuanNcbiAqKiBtb2R1bGUgaWQgPSAxNFxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDFcbiAqKi8iLCJ2YXIgaXNPYmplY3QgPSByZXF1aXJlKCcuL19pcy1vYmplY3QnKTtcbm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oaXQpe1xuICBpZighaXNPYmplY3QoaXQpKXRocm93IFR5cGVFcnJvcihpdCArICcgaXMgbm90IGFuIG9iamVjdCEnKTtcbiAgcmV0dXJuIGl0O1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2FuLW9iamVjdC5qc1xuICoqIG1vZHVsZSBpZCA9IDE1XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMVxuICoqLyIsIm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oaXQpe1xuICByZXR1cm4gdHlwZW9mIGl0ID09PSAnb2JqZWN0JyA/IGl0ICE9PSBudWxsIDogdHlwZW9mIGl0ID09PSAnZnVuY3Rpb24nO1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2lzLW9iamVjdC5qc1xuICoqIG1vZHVsZSBpZCA9IDE2XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMVxuICoqLyIsIm1vZHVsZS5leHBvcnRzID0gIXJlcXVpcmUoJy4vX2Rlc2NyaXB0b3JzJykgJiYgIXJlcXVpcmUoJy4vX2ZhaWxzJykoZnVuY3Rpb24oKXtcclxuICByZXR1cm4gT2JqZWN0LmRlZmluZVByb3BlcnR5KHJlcXVpcmUoJy4vX2RvbS1jcmVhdGUnKSgnZGl2JyksICdhJywge2dldDogZnVuY3Rpb24oKXsgcmV0dXJuIDc7IH19KS5hICE9IDc7XHJcbn0pO1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9faWU4LWRvbS1kZWZpbmUuanNcbiAqKiBtb2R1bGUgaWQgPSAxN1xuICoqIG1vZHVsZSBjaHVua3MgPSAwIDFcbiAqKi8iLCIvLyBUaGFuaydzIElFOCBmb3IgaGlzIGZ1bm55IGRlZmluZVByb3BlcnR5XG5tb2R1bGUuZXhwb3J0cyA9ICFyZXF1aXJlKCcuL19mYWlscycpKGZ1bmN0aW9uKCl7XG4gIHJldHVybiBPYmplY3QuZGVmaW5lUHJvcGVydHkoe30sICdhJywge2dldDogZnVuY3Rpb24oKXsgcmV0dXJuIDc7IH19KS5hICE9IDc7XG59KTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2Rlc2NyaXB0b3JzLmpzXG4gKiogbW9kdWxlIGlkID0gMThcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxXG4gKiovIiwibW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihleGVjKXtcbiAgdHJ5IHtcbiAgICByZXR1cm4gISFleGVjKCk7XG4gIH0gY2F0Y2goZSl7XG4gICAgcmV0dXJuIHRydWU7XG4gIH1cbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19mYWlscy5qc1xuICoqIG1vZHVsZSBpZCA9IDE5XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMVxuICoqLyIsInZhciBpc09iamVjdCA9IHJlcXVpcmUoJy4vX2lzLW9iamVjdCcpXG4gICwgZG9jdW1lbnQgPSByZXF1aXJlKCcuL19nbG9iYWwnKS5kb2N1bWVudFxuICAvLyBpbiBvbGQgSUUgdHlwZW9mIGRvY3VtZW50LmNyZWF0ZUVsZW1lbnQgaXMgJ29iamVjdCdcbiAgLCBpcyA9IGlzT2JqZWN0KGRvY3VtZW50KSAmJiBpc09iamVjdChkb2N1bWVudC5jcmVhdGVFbGVtZW50KTtcbm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oaXQpe1xuICByZXR1cm4gaXMgPyBkb2N1bWVudC5jcmVhdGVFbGVtZW50KGl0KSA6IHt9O1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2RvbS1jcmVhdGUuanNcbiAqKiBtb2R1bGUgaWQgPSAyMFxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDFcbiAqKi8iLCIvLyA3LjEuMSBUb1ByaW1pdGl2ZShpbnB1dCBbLCBQcmVmZXJyZWRUeXBlXSlcbnZhciBpc09iamVjdCA9IHJlcXVpcmUoJy4vX2lzLW9iamVjdCcpO1xuLy8gaW5zdGVhZCBvZiB0aGUgRVM2IHNwZWMgdmVyc2lvbiwgd2UgZGlkbid0IGltcGxlbWVudCBAQHRvUHJpbWl0aXZlIGNhc2Vcbi8vIGFuZCB0aGUgc2Vjb25kIGFyZ3VtZW50IC0gZmxhZyAtIHByZWZlcnJlZCB0eXBlIGlzIGEgc3RyaW5nXG5tb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGl0LCBTKXtcbiAgaWYoIWlzT2JqZWN0KGl0KSlyZXR1cm4gaXQ7XG4gIHZhciBmbiwgdmFsO1xuICBpZihTICYmIHR5cGVvZiAoZm4gPSBpdC50b1N0cmluZykgPT0gJ2Z1bmN0aW9uJyAmJiAhaXNPYmplY3QodmFsID0gZm4uY2FsbChpdCkpKXJldHVybiB2YWw7XG4gIGlmKHR5cGVvZiAoZm4gPSBpdC52YWx1ZU9mKSA9PSAnZnVuY3Rpb24nICYmICFpc09iamVjdCh2YWwgPSBmbi5jYWxsKGl0KSkpcmV0dXJuIHZhbDtcbiAgaWYoIVMgJiYgdHlwZW9mIChmbiA9IGl0LnRvU3RyaW5nKSA9PSAnZnVuY3Rpb24nICYmICFpc09iamVjdCh2YWwgPSBmbi5jYWxsKGl0KSkpcmV0dXJuIHZhbDtcbiAgdGhyb3cgVHlwZUVycm9yKFwiQ2FuJ3QgY29udmVydCBvYmplY3QgdG8gcHJpbWl0aXZlIHZhbHVlXCIpO1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX3RvLXByaW1pdGl2ZS5qc1xuICoqIG1vZHVsZSBpZCA9IDIxXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMVxuICoqLyIsIm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oYml0bWFwLCB2YWx1ZSl7XG4gIHJldHVybiB7XG4gICAgZW51bWVyYWJsZSAgOiAhKGJpdG1hcCAmIDEpLFxuICAgIGNvbmZpZ3VyYWJsZTogIShiaXRtYXAgJiAyKSxcbiAgICB3cml0YWJsZSAgICA6ICEoYml0bWFwICYgNCksXG4gICAgdmFsdWUgICAgICAgOiB2YWx1ZVxuICB9O1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX3Byb3BlcnR5LWRlc2MuanNcbiAqKiBtb2R1bGUgaWQgPSAyMlxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDFcbiAqKi8iLCIndXNlIHN0cmljdCc7XG5cbmV4cG9ydCBkZWZhdWx0IGNsYXNzIEZvcm1IZWxwZXIge1xuICAgIC8qKlxuICAgICAqIFZhbGlkYXRlIGZvcm0gdGhyb3VnaCBpbnB1dHNcbiAgICAgKiBAcGFyYW0ge2pRdWVyeX0gJGlucHV0c1xuICAgICAqL1xuICAgIGNvbnN0cnVjdG9yKCRpbnB1dHMpIHtcbiAgICAgICAgdGhpcy4kaW5wdXRzID0gJGlucHV0cztcbiAgICAgICAgdGhpcy5hcnJFcnJvcnMgPSBbXTtcbiAgICAgICAgdGhpcy5fYXNzaWduRXZlbnRzKCk7XG4gICAgfVxuXG4gICAgX2Fzc2lnbkV2ZW50cygpIHtcbiAgICAgICAgdGhpcy4kaW5wdXRzLm9uKCdpbnB1dCcsIChlKSA9PiB0aGlzLl9yZW1vdmVFcnJvcigkKGUudGFyZ2V0KSkpO1xuICAgIH1cblxuICAgIGlzVmFsaWRJbnB1dHMoKSB7XG4gICAgICAgIGNvbnN0ICRpbnB1dHMgPSB0aGlzLiRpbnB1dHM7XG4gICAgICAgIGxldCBlcnJvciA9IDA7XG5cbiAgICAgICAgJGlucHV0cy5lYWNoKChpbmRleCwgaW5wdXQpID0+IHtcbiAgICAgICAgICAgIGNvbnN0ICRpbnB1dCA9ICQoaW5wdXQpO1xuXG4gICAgICAgICAgICBpZiAoIXRoaXMuX2lzVmFsaWRJbnB1dCgkaW5wdXQpKSBlcnJvciArPSAxO1xuICAgICAgICB9KTtcbiAgICAgICAgcmV0dXJuIEJvb2xlYW4oIWVycm9yKTtcbiAgICB9XG5cbiAgICAvKipcbiAgICAgKiBDaGVjayBnaXZlbiBpbnB1dCwgaXMgaXQgdmFsaWQ/XG4gICAgICogQHBhcmFtIHtqUXVlcnl9ICRpbnB1dFxuICAgICAqIEByZXR1cm5zIHtib29sZWFufSAtIElzIHZhbGlkIGlucHV0P1xuICAgICAqL1xuICAgIF9pc1ZhbGlkSW5wdXQoJGlucHV0KSB7XG4gICAgICAgIGNvbnN0IHZhbHVlID0gJC50cmltKCRpbnB1dC52YWwoKSk7XG5cbiAgICAgICAgaWYgKCF2YWx1ZSkge1xuICAgICAgICAgICAgdGhpcy5fc2V0RXJyb3IoJGlucHV0LCAnRW1wdHknKTtcbiAgICAgICAgICAgIHJldHVybiBmYWxzZTtcbiAgICAgICAgfVxuXG4gICAgICAgIGlmICgoJGlucHV0Lmhhc0NsYXNzKCd0eXBlLWVtYWlsJykpICYmICF0aGlzLl9pc1ZhbGlkRW1haWwodmFsdWUpKSB7XG4gICAgICAgICAgICB0aGlzLl9zZXRFcnJvcigkaW5wdXQsICdFbWFpbCBpcyBub3QgdmFsaWQnKTtcbiAgICAgICAgICAgIHJldHVybiBmYWxzZTtcbiAgICAgICAgfVxuICAgICAgICByZXR1cm4gdHJ1ZTtcbiAgICB9XG5cbiAgICAvKipcbiAgICAgKiBJcyBFbWFpbCB2YWxpZD9cbiAgICAgKiBAcGFyYW0ge3N0cmluZ30gZW1haWxcbiAgICAgKiBAcmV0dXJucyB7Ym9vbGVhbn1cbiAgICAgKi9cbiAgICBfaXNWYWxpZEVtYWlsKGVtYWlsKSB7XG4gICAgICAgIHZhciByZSA9IC9eKChbXjw+KClcXFtcXF1cXFxcLiw7Olxcc0BcIl0rKFxcLltePD4oKVxcW1xcXVxcXFwuLDs6XFxzQFwiXSspKil8KFwiLitcIikpQCgoXFxbWzAtOV17MSwzfVxcLlswLTldezEsM31cXC5bMC05XXsxLDN9XFwuWzAtOV17MSwzfV0pfCgoW2EtekEtWlxcLTAtOV0rXFwuKStbYS16QS1aXXsyLH0pKSQvO1xuICAgICAgICByZXR1cm4gcmUudGVzdChlbWFpbCk7XG4gICAgfVxuXG4gICAgLyoqXG4gICAgICogU2V0IGVycm9yIGZvciBpbnB1dFxuICAgICAqIEBwYXJhbSB7alF1ZXJ5fSAkaW5wdXRcbiAgICAgKiBAcGFyYW0ge3N0cmluZ30gZXJyb3JUZXh0XG4gICAgICovXG4gICAgX3NldEVycm9yKCRpbnB1dCwgZXJyb3JUZXh0KSB7XG4gICAgICAgIGNvbnN0ICRwYXJlbnQgPSAkaW5wdXQucGFyZW50KCk7XG4gICAgICAgIGNvbnN0ICRlcnJvciA9ICRwYXJlbnQuZmluZCgnLmItZXJyb3InKTtcblxuICAgICAgICBpZiAoJGVycm9yLmxlbmd0aCkgcmV0dXJuO1xuXG4gICAgICAgICRwYXJlbnQuYWRkQ2xhc3MoJ2ItZXJyb3Jfc2hvdycpO1xuICAgICAgICAkKCc8ZGl2IGNsYXNzPVwiYi1lcnJvclwiIC8+JylcbiAgICAgICAgICAgIC50ZXh0KGVycm9yVGV4dClcbiAgICAgICAgICAgIC5wcmVwZW5kVG8oJHBhcmVudCk7XG5cbiAgICAgICAgdGhpcy5hcnJFcnJvcnMucHVzaCh7XG4gICAgICAgICAgICBuYW1lOiAkaW5wdXQuYXR0cignbmFtZScpLFxuICAgICAgICAgICAgZXJyb3I6IGVycm9yVGV4dFxuICAgICAgICB9KVxuICAgIH1cblxuICAgIC8qKlxuICAgICAqIFJlbW92ZSBlcnJvciBmb3IgaW5wdXRcbiAgICAgKiBAcGFyYW0ge2pRdWVyeX0gJGlucHV0XG4gICAgICovXG4gICAgX3JlbW92ZUVycm9yKCRpbnB1dCkge1xuICAgICAgICBjb25zdCAkcGFyZW50ID0gJGlucHV0LnBhcmVudCgpO1xuXG4gICAgICAgICRwYXJlbnRcbiAgICAgICAgICAgIC5yZW1vdmVDbGFzcygnYi1lcnJvcl9zaG93JylcbiAgICAgICAgICAgIC5maW5kKCcuYi1lcnJvcicpLnJlbW92ZSgpO1xuXG4gICAgICAgIHRoaXMuYXJyRXJyb3JzID0gdGhpcy5hcnJFcnJvcnMuZmlsdGVyKGZ1bmN0aW9uIChpdGVtKSB7XG4gICAgICAgICAgICByZXR1cm4gaXRlbS5uYW1lICE9PSAkaW5wdXQuYXR0cignbmFtZScpXG4gICAgICAgIH0pXG4gICAgfVxuXG4gICAgLyoqXG4gICAgICogU2V0IGVycm9yc1xuICAgICAqIEBwYXJhbSB7QXJyYXl9IGVycm9ycyAtIFt7bmFtZTogXCJlbWFpbFwiLCBlcnJvcjogXCJlbXB0eVwifSwge25hbWU6IFwicGFzc3dvcmRcIiwgZXJyb3I6IFwiZW1wdHlcIn1dXG4gICAgICovXG4gICAgc2V0RXJyb3JzKGVycm9ycykge1xuICAgICAgICBlcnJvcnMuZm9yRWFjaCgoaXRlbSkgPT4ge1xuICAgICAgICAgICAgY29uc3QgJGN1cnJlbnRJbnB1dCA9IHRoaXMuJGlucHV0cy5maWx0ZXIoJ1tuYW1lPVwiJyArIGl0ZW0ubmFtZSArICdcIl0nKS5maXJzdCgpO1xuXG4gICAgICAgICAgICBpZiAoJGN1cnJlbnRJbnB1dC5sZW5ndGgpIHRoaXMuX3NldEVycm9yKCRjdXJyZW50SW5wdXQsIGl0ZW0uZXJyb3IpXG4gICAgICAgIH0pXG4gICAgfVxuXG4gICAgLyoqXG4gICAgICogR2V0IHR4dCB2ZXJzaW9uIG9mIGFsbCBlcnJvcnNcbiAgICAgKi9cbiAgICBnZXRFcnJvcnNUZXh0KGVycm9ycykge1xuICAgICAgICBjb25zdCBhcnJFcnJvcnMgPSBlcnJvcnMgfHwgdGhpcy5hcnJFcnJvcnM7XG4gICAgICAgIGxldCBlcnJvclR4dCA9ICcnO1xuXG4gICAgICAgIGFyckVycm9ycy5mb3JFYWNoKChpdGVtKSA9PiB7XG4gICAgICAgICAgICBjb25zdCBuYW1lID0gaXRlbS5uYW1lWzBdLnRvVXBwZXJDYXNlKCkgKyBpdGVtLm5hbWUuc3Vic3RyKDEpO1xuXG4gICAgICAgICAgICBlcnJvclR4dCArPSBgJHtuYW1lfSB2YWx1ZSBpcyAke2l0ZW0uZXJyb3IudG9Mb3dlckNhc2UoKX0uIGA7XG4gICAgICAgIH0pO1xuXG4gICAgICAgIHJldHVybiBlcnJvclR4dDtcbiAgICB9XG5cbiAgICAvKipcbiAgICAgKiBSZW1vdmUgYWxsIGVycm9yc1xuICAgICAqL1xuICAgIHJlbW92ZUVycm9ycygpIHtcbiAgICAgICAgdGhpcy4kaW5wdXRzLmVhY2goKGluZGV4LCBlbCkgPT4ge1xuICAgICAgICAgICAgY29uc3QgJGVsID0gJChlbCk7XG4gICAgICAgICAgICB0aGlzLl9yZW1vdmVFcnJvcigkZWwpXG4gICAgICAgIH0pXG4gICAgfVxuXG4gICAgY2xlYXJGb3JtKCkge1xuICAgICAgICB0aGlzLiRpbnB1dHMuZWFjaCgoaW5kZXgsIGVsKSA9PiB7XG4gICAgICAgICAgICBjb25zdCAkZWwgPSAkKGVsKTtcbiAgICAgICAgICAgIGlmICghJGVsLmF0dHIoXCJkaXNhYmxlZFwiKSkgICRlbC52YWwoJycpO1xuICAgICAgICB9KVxuICAgIH1cbn1cblxuXG5cbi8qKiBXRUJQQUNLIEZPT1RFUiAqKlxuICoqIC4vZnJvbnRlbmQvanMvY29tbW9uL19mb3JtLWhlbHBlci5qc1xuICoqLyIsIlxuaW1wb3J0IEFzeW5jVGFiIGZyb20gJy4vLi4vY29tbW9uL19hc3luYy10YWJzJztcbmltcG9ydCBGaWx0ZXJIaXN0b3J5IGZyb20gJy4vd2lkZ2V0cy9fZmlsdGVyLWhpc3RvcnknO1xuaW1wb3J0IENyZWRpdEZvcm0gZnJvbSAnLi93aWRnZXRzL19zZW5kLWNyZWRpdHMnO1xuXG4kKGZ1bmN0aW9uKCl7XG4gICAgQXN5bmNUYWIucGx1Z2luKCcuanMtY3JlZGl0cy10YWJzJyk7XG5cbiAgIEFwcC5ldmVudHNcbiAgICAgICAgLnN1YignaG10LmFzeW5jdGFiLnNob3duJywgKCk9PiB7XG4gICAgICAgICAgICBGaWx0ZXJIaXN0b3J5LnBsdWdpbignLmpzLWNyZWRpdC1oaXN0b3J5Jyk7XG4gICAgICAgICAgICBDcmVkaXRGb3JtLnBsdWdpbignLmpzLWZvcm0tY3JlZGl0Jyk7XG4gICAgICAgIH0pO1xufSk7XG5cblxuXG5cblxuXG5cblxuXG4vKiogV0VCUEFDSyBGT09URVIgKipcbiAqKiAuL2Zyb250ZW5kL2pzL2NyZWRpdHMvY3JlZGl0LXBhZ2UuanNcbiAqKi8iLCIndXNlIHN0cmljdCc7XG5cbmV4cG9ydCBkZWZhdWx0IGNsYXNzIFdpZGdldCB7XG5cbiAgICBjb25zdHJ1Y3RvcihzZWxlY3Rvcikge1xuICAgICAgICB0aGlzLiRyb290ID0gJChzZWxlY3Rvcik7XG4gICAgICAgIHRoaXMubG9hZGVkVGFicyA9IFtdO1xuICAgICAgICB0aGlzLmxvY2FscyA9IHRoaXMuX2dldERvbSgpO1xuXG4gICAgICAgIHRoaXMuX2Fzc2lnbkV2ZW50cygpO1xuXG4gICAgICAgIGxldCAkZmlyc3RMaW5rID0gdGhpcy5sb2NhbHMuJGxpbmtzLmZpcnN0KCk7XG4gICAgICAgIHRoaXMuc2hvd1RhYkJ5TGluaygkZmlyc3RMaW5rKTtcbiAgICB9XG5cbiAgICBfZ2V0RG9tKCkge1xuICAgICAgICByZXR1cm4ge1xuICAgICAgICAgICAgJGxpbmtzOiB0aGlzLiRyb290LmZpbmQoJ1tkYXRhLXRhYi1saW5rXScpXG4gICAgICAgIH1cbiAgICB9XG5cbiAgICBfYXNzaWduRXZlbnRzKCkge1xuICAgICAgICB0aGlzLiRyb290Lm9uKCdjbGljaycsICdbZGF0YS10YWItbGlua10nLCB0aGlzLl9vbkNsaWNrTGluay5iaW5kKHRoaXMpKTtcbiAgICB9XG5cbiAgICBfb25DbGlja0xpbmsoZSkge1xuICAgICAgICBlLnByZXZlbnREZWZhdWx0KCk7XG4gICAgICAgIGxldCAkbGluayA9ICQoZS50YXJnZXQpO1xuXG4gICAgICAgIGlmICgkbGluay5oYXNDbGFzcygnc3RhdGVfYWN0aXZlJykpIHJldHVybjtcbiAgICAgICAgdGhpcy5zaG93VGFiQnlMaW5rKCRsaW5rKTtcbiAgICB9XG5cbiAgICAvKipcbiAgICAgKiBcbiAgICAgKiBAcGFyYW0ge2pRdWVyeX0gJGxpbmsgLSBjbGlja2VkIGxpbmtcbiAgICAgKiBAcHJpdmF0ZVxuICAgICAqL1xuICAgIHNob3dUYWJCeUxpbmsoJGxpbmspe1xuICAgICAgICBjb25zdCB1cmwgPSAkbGluay5hdHRyKCdkYXRhLWhyZWYnKTtcbiAgICAgICAgY29uc3QgdGFyZ2V0ID0gJGxpbmsuYXR0cignaHJlZicpO1xuICAgICAgICBjb25zdCBzZWxmID0gdGhpcztcblxuICAgICAgICBzZWxmLl9sb2FkQ29udGVudCh1cmwsIHRhcmdldClcbiAgICAgICAgICAgIC5kb25lKCAgKCk9PntcbiAgICAgICAgICAgICAgICAkbGluay5hZGRDbGFzcygnc3RhdGVfYWN0aXZlJykuc2libGluZ3MoKS5yZW1vdmVDbGFzcygnc3RhdGVfYWN0aXZlJyk7XG4gICAgICAgICAgICAgICAgJGxpbmsudGFiKCdzaG93Jyk7XG5cbiAgICAgICAgICAgICAgICBBcHAuZXZlbnRzLnB1YignaG10LmFzeW5jdGFiLnNob3duJyk7XG4gICAgICAgICAgICB9KVxuICAgIH1cblxuICAgIC8qKlxuICAgICAqICBMb2FkIGNvbnRlbnQgYW5kIGluc2VydCBpbnRvIHRhcmdldCBkaXZcbiAgICAgKiBAcGFyYW0ge1N0cmluZ30gdXJsICAgICAgLSB1cmwgb2YgbG9hZGVkIGNvbnRlbnRcbiAgICAgKiBAcGFyYW0ge2pRdWVyeX0gdGFyZ2V0ICAgLSBkaXYgd2hlcmUgd2Ugc2hvdWxkIGluc2VydCBjb250ZW50XG4gICAgICovXG4gICAgX2xvYWRDb250ZW50KHVybCwgdGFyZ2V0KXtcbiAgICAgICAgY29uc3Qgc2VsZiA9IHRoaXM7XG4gICAgICAgIGxldCBkZWZlciA9ICQuRGVmZXJyZWQoKTtcblxuICAgICAgICBpZiAoJC5pbkFycmF5KHRhcmdldCwgc2VsZi5sb2FkZWRUYWJzKSA8IDAgJiYgdXJsKSB7XG4gICAgICAgICAgICAkLmdldCh1cmwsIChkYXRhKSA9PiB7XG4gICAgICAgICAgICAgICAgc2VsZi5sb2FkZWRUYWJzLnB1c2godGFyZ2V0KTtcbiAgICAgICAgICAgICAgICAkKHRhcmdldCkuaHRtbChkYXRhKTtcblxuICAgICAgICAgICAgICAgIGRlZmVyLnJlc29sdmUoKTtcbiAgICAgICAgICAgIH0pO1xuICAgICAgICB9IGVsc2Uge1xuICAgICAgICAgICAgZGVmZXIucmVzb2x2ZSgpO1xuICAgICAgICB9XG4gICAgICAgIFxuICAgICAgICByZXR1cm4gZGVmZXIucHJvbWlzZSgpO1xuICAgIH0gICAgXG5cbiAgICAvLyBzdGF0aWNcbiAgICBzdGF0aWMgcGx1Z2luKHNlbGVjdG9yKSB7XG4gICAgICAgIGNvbnN0ICRlbGVtcyA9ICQoc2VsZWN0b3IpO1xuICAgICAgICBpZiAoISRlbGVtcy5sZW5ndGgpIHJldHVybjtcblxuICAgICAgICByZXR1cm4gJGVsZW1zLmVhY2goZnVuY3Rpb24gKGluZGV4LCBlbCkge1xuICAgICAgICAgICAgbGV0ICRlbGVtZW50ID0gJChlbCk7XG4gICAgICAgICAgICBsZXQgZGF0YSAgICAgPSAkZWxlbWVudC5kYXRhKCd3aWRnZXQnKTtcblxuICAgICAgICAgICAgaWYgKCFkYXRhKSB7XG4gICAgICAgICAgICAgICAgZGF0YSA9IG5ldyBXaWRnZXQoZWwpO1xuICAgICAgICAgICAgICAgICRlbGVtZW50LmRhdGEoJ3dpZGdldCcsIGRhdGEpO1xuICAgICAgICAgICAgfVxuICAgICAgICB9KVxuICAgIH1cbn1cblxuXG5cblxuXG4vKiogV0VCUEFDSyBGT09URVIgKipcbiAqKiAuL2Zyb250ZW5kL2pzL2NvbW1vbi9fYXN5bmMtdGFicy5qc1xuICoqLyIsIid1c2Ugc3RyaWN0JztcblxuXG5leHBvcnQgZGVmYXVsdCBjbGFzcyBXaWRnZXQge1xuICAgIC8qKlxuICAgICAqIEZpbHRlciBoaXN0b3J5XG4gICAgICogQHBhcmFtIHtTdHJpbmd9IHNlbGVjdG9yXG4gICAgICovXG4gICAgY29uc3RydWN0b3Ioc2VsZWN0b3IpIHtcbiAgICAgICAgdGhpcy4kcm9vdCA9ICQoc2VsZWN0b3IpO1xuICAgICAgICB0aGlzLmxvY2FscyA9IHRoaXMuX2dldERvbSgpO1xuXG4gICAgICAgIHRoaXMuX2Fzc2lnbkV2ZW50cygpO1xuICAgIH1cblxuICAgIF9nZXREb20oKSB7XG4gICAgICAgIGNvbnN0ICRyb290ID0gdGhpcy4kcm9vdDtcblxuICAgICAgICByZXR1cm4ge1xuICAgICAgICAgICAgJGxpc3Q6ICRyb290LmZpbmQoJ1tkYXRhLWZpbHRlci1saXN0XScpLFxuICAgICAgICAgICAgJGl0ZW1zOiAkcm9vdC5maW5kKCdbZGF0YS1maWx0ZXItdGV4dF0nKSxcbiAgICAgICAgfTtcbiAgICB9XG5cbiAgICBfYXNzaWduRXZlbnRzKCkge1xuICAgICAgICB0aGlzLiRyb290Lm9uKCdjbGljaycsICdbZGF0YS1maWx0ZXItbGlua10nLCB0aGlzLl9vbkNsaWNrRmlsdGVyLmJpbmQodGhpcykpO1xuICAgIH1cblxuICAgIF9vbkNsaWNrRmlsdGVyKGUpIHtcbiAgICAgICAgY29uc3QgJGxpbmsgPSAkKGUudGFyZ2V0KTtcbiAgICAgICAgY29uc3QgZmlsdGVyVGV4dCA9ICRsaW5rLmRhdGEoJ2ZpbHRlci1saW5rJyk7XG5cbiAgICAgICAgZS5wcmV2ZW50RGVmYXVsdCgpO1xuXG4gICAgICAgIGlmICgkbGluay5oYXNDbGFzcygnc3RhdGVfc2VsZWN0ZWQnKSkgcmV0dXJuO1xuXG4gICAgICAgIHRoaXMuc2V0QWN0aXZlTGluaygkbGluayk7XG4gICAgICAgIHRoaXMuZmlsdGVyTGlzdChmaWx0ZXJUZXh0KTtcbiAgICB9O1xuXG4gICAgLyoqXG4gICAgICogRmlsdGVyIGxpc3QgdGhyb3VnaCB0ZXh0XG4gICAgICogQHBhcmFtIHtTdHJpbmd9IGZpbHRlclRleHRcbiAgICAgKi9cbiAgICBmaWx0ZXJMaXN0KGZpbHRlclRleHQpIHtcbiAgICAgICAgY29uc3QgJGl0ZW1zID0gdGhpcy5sb2NhbHMuJGl0ZW1zO1xuXG4gICAgICAgIGlmIChmaWx0ZXJUZXh0ID09ICdhbGwnKSB7XG4gICAgICAgICAgICAkaXRlbXMucmVtb3ZlQ2xhc3MoJ3N0YXRlX2hpZGRlbicpO1xuICAgICAgICAgICAgcmV0dXJuO1xuICAgICAgICB9XG5cbiAgICAgICAgJGl0ZW1zLmVhY2goKGluZGV4LCBlbCkgPT4ge1xuICAgICAgICAgICAgY29uc3QgJGVsID0gJChlbCk7XG4gICAgICAgICAgICBjb25zdCBpc0hpZGRlbiA9ICRlbC5kYXRhKCdmaWx0ZXItdGV4dCcpLmluZGV4T2YoZmlsdGVyVGV4dCkgPT09IC0xO1xuXG4gICAgICAgICAgICAkZWwudG9nZ2xlQ2xhc3MoJ3N0YXRlX2hpZGRlbicsIGlzSGlkZGVuKTtcbiAgICAgICAgfSk7XG4gICAgfTtcblxuICAgIC8qKlxuICAgICAqIFNldCBsaW5rIHRvIGFjdGl2ZSBhbmQgZGVhY3RpdmF0ZSBvdGhlclxuICAgICAqIEBwYXJhbSB7alF1ZXJ5fSAkZWxcbiAgICAgKi9cbiAgICBzZXRBY3RpdmVMaW5rKCRlbCkge1xuICAgICAgICAkZWwuYWRkQ2xhc3MoJ3N0YXRlX3NlbGVjdGVkJylcbiAgICAgICAgICAgIC5zaWJsaW5ncygpLnJlbW92ZUNsYXNzKCdzdGF0ZV9zZWxlY3RlZCcpO1xuICAgIH07XG4gICAgXG4gICAgLy8gc3RhdGljXG4gICAgc3RhdGljIHBsdWdpbihzZWxlY3Rvcikge1xuICAgICAgICBjb25zdCAkZWxlbXMgPSAkKHNlbGVjdG9yKTtcbiAgICAgICAgaWYgKCEkZWxlbXMubGVuZ3RoKSByZXR1cm47XG5cbiAgICAgICAgcmV0dXJuICRlbGVtcy5lYWNoKGZ1bmN0aW9uIChpbmRleCwgZWwpIHtcbiAgICAgICAgICAgIGxldCAkZWxlbWVudCA9ICQoZWwpO1xuICAgICAgICAgICAgbGV0IGRhdGEgICAgID0gJGVsZW1lbnQuZGF0YSgnd2lkZ2V0Jyk7XG5cbiAgICAgICAgICAgIGlmICghZGF0YSkge1xuICAgICAgICAgICAgICAgIGRhdGEgPSBuZXcgV2lkZ2V0KGVsKTtcbiAgICAgICAgICAgICAgICAkZWxlbWVudC5kYXRhKCd3aWRnZXQnLCBkYXRhKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgfSlcbiAgICB9XG59XG5cblxuXG5cblxuLyoqIFdFQlBBQ0sgRk9PVEVSICoqXG4gKiogLi9mcm9udGVuZC9qcy9jcmVkaXRzL3dpZGdldHMvX2ZpbHRlci1oaXN0b3J5LmpzXG4gKiovIiwiJ3VzZSBzdHJpY3QnO1xuXG5pbXBvcnQgRm9ybUhlbHBlciBmcm9tIFwiLi8uLi8uLi9jb21tb24vX2Zvcm0taGVscGVyXCI7XG5cbi8qKlxuICogRm9ybSBmb3Igc2VuZGluZyBjcmVkaXRcbiAqL1xuZXhwb3J0IGRlZmF1bHQgY2xhc3MgV2lkZ2V0e1xuXG4gICAgY29uc3RydWN0b3Ioc2VsZWN0b3IpIHtcbiAgICAgICAgdGhpcy4kcm9vdCA9ICQoc2VsZWN0b3IpO1xuICAgICAgICB0aGlzLmxvY2FscyA9IHRoaXMuX2dldERvbSgpO1xuICAgICAgICB0aGlzLnZhbGlkYXRpb24gPSBuZXcgRm9ybUhlbHBlcih0aGlzLiRyb290LmZpbmQoJy5iLWNyZWRpdHNfX2lucHV0JykpO1xuXG4gICAgICAgIGlmICghQm9vbGVhbigkLmZuLmF1dG9jb21wbGV0ZSkpe1xuICAgICAgICAgICAgY29uc29sZS5sb2coJ2pRdWVyeSBhdXRvY29tcGxldGUgcGx1Z2luIGlzIG5vdCBpbmNsdWRlIGludG8gcGFnZScpO1xuICAgICAgICAgICAgcmV0dXJuO1xuICAgICAgICB9XG4gICAgICAgIHRoaXMuX2luaXRBdXRvQ29tcGxldGUoKTtcbiAgICAgICAgdGhpcy5fYXNzaWduRXZlbnRzKCk7XG4gICAgfVxuXG4gICAgX2dldERvbSgpIHtcbiAgICAgICAgY29uc3QgJHJvb3QgPSB0aGlzLiRyb290O1xuXG4gICAgICAgIHJldHVybiB7XG4gICAgICAgICAgICAkY291bnQ6ICRyb290LmZpbmQoJ1tkYXRhLWNyZWRpY3QtY291bnRdJyksXG4gICAgICAgICAgICAkdmFsdWU6ICRyb290LmZpbmQoJ1tkYXRhLWNyZWRpdC12YWx1ZV0nKSxcbiAgICAgICAgICAgICR0bzogJHJvb3QuZmluZCgnW2RhdGEtY3JlZGl0LXRvXScpLFxuICAgICAgICAgICAgJHRvRGF0YTogJHJvb3QuZmluZCgnW2RhdGEtY3JlZGl0LXRvLWRhdGFdJyksXG4gICAgICAgICAgICAkbWVzc2FnZTogJHJvb3QuZmluZCgnW2RhdGEtY3JlZGl0LW1lc3NhZ2VdJyksXG4gICAgICAgICAgICAkZXJyb3I6ICRyb290LmZpbmQoJ1tkYXRhLWNyZWRpdC1lcnJvcl0nKVxuICAgICAgICB9O1xuICAgIH1cblxuICAgIF9pbml0QXV0b0NvbXBsZXRlKCkge1xuICAgICAgICBjb25zdCBsb2NhbHMgPSB0aGlzLmxvY2FscztcbiAgICAgICAgY29uc3QgdXJsID0ganNSb3V0ZXMuY29udHJvbGxlcnMuY29yZS5PcmdhbmlzYXRpb25zLnNlYXJjaCgpLnVybDtcbiAgICAgICAgXG4gICAgICAgIGxvY2Fscy4kdG8uYXV0b2NvbXBsZXRlKHtcbiAgICAgICAgICAgIHNlcnZpY2VVcmw6IHVybCxcbiAgICAgICAgICAgIHBhcmFtTmFtZTogJ3F1ZXJ5JyxcbiAgICAgICAgICAgIG1pbkNoYXJzOiAzLFxuICAgICAgICAgICAgcHJlc2VydmVJbnB1dDogdHJ1ZSwgICAgICAgICAgICBcbiAgICAgICAgICAgIG9uU2VsZWN0OiBmdW5jdGlvbiAoc3VnZ2VzdGlvbikge1xuICAgICAgICAgICAgICAgIGxvY2Fscy4kdG8udmFsKHN1Z2dlc3Rpb24udmFsdWUpO1xuICAgICAgICAgICAgICAgIGxvY2Fscy4kdG9EYXRhLnZhbChzdWdnZXN0aW9uLmRhdGEpO1xuICAgICAgICAgICAgICAgIHJldHVybiB0cnVlO1xuICAgICAgICAgICAgfVxuICAgICAgICB9KTtcbiAgICB9XG5cbiAgICBfYXNzaWduRXZlbnRzKCkge1xuICAgICAgICB0aGlzLiRyb290XG4gICAgICAgICAgICAub24oJ2lucHV0JywgJ2lucHV0JywgKGUpID0+IHRoaXMubG9jYWxzLiRlcnJvci50ZXh0KCcnKSlcbiAgICAgICAgICAgIC5vbignc3VibWl0JywgdGhpcy5fb25TdWJtaXRGb3JtLmJpbmQodGhpcykpO1xuICAgIH1cblxuICAgIF9vblN1Ym1pdEZvcm0oZSkge1xuICAgICAgICBlLnByZXZlbnREZWZhdWx0KCk7XG4gICAgICAgIGNvbnN0IHNlbGYgPSB0aGlzO1xuXG4gICAgICAgIGlmICghc2VsZi5faXNGb3JtVmFsaWQoKSkgcmV0dXJuIGZhbHNlO1xuXG4gICAgICAgIHNlbGYuX3NlbmRSZXF1ZXN0KClcbiAgICAgICAgICAgIC5kb25lKCgpID0+IHtcbiAgICAgICAgICAgICAgICBzZWxmLnZhbGlkYXRpb24uY2xlYXJGb3JtKCk7XG5cbiAgICAgICAgICAgICAgICBzZWxmLiRyb290LmFkZENsYXNzKCdiLWNyZWRpdHNfc3RhdGVfc2VuZCcpO1xuICAgICAgICAgICAgICAgIHNldFRpbWVvdXQoKCk9PiB7XG4gICAgICAgICAgICAgICAgICAgIHNlbGYuJHJvb3QucmVtb3ZlQ2xhc3MoJ2ItY3JlZGl0c19zdGF0ZV9zZW5kJyk7XG4gICAgICAgICAgICAgICAgfSwgMzAwMClcbiAgICAgICAgICAgIH0pXG4gICAgICAgICAgICAuZmFpbCgocmVzcG9uc2UpID0+IHtcbiAgICAgICAgICAgICAgICBjb25zdCBkYXRhID0gJC5wYXJzZUpTT04ocmVzcG9uc2UucmVzcG9uc2VUZXh0KS5kYXRhO1xuICAgICAgICAgICAgICAgIGNvbnN0IGVycm9yVGV4dCA9IHNlbGYudmFsaWRhdGlvbi5nZXRFcnJvcnNUZXh0KGRhdGEuZXJyb3JzKTtcblxuICAgICAgICAgICAgICAgIGlmICghZGF0YS5lcnJvcnMpIHJldHVybjtcblxuICAgICAgICAgICAgICAgIHNlbGYubG9jYWxzLiRlcnJvci50ZXh0KGVycm9yVGV4dCk7XG4gICAgICAgICAgICAgICAgc2VsZi52YWxpZGF0aW9uLnNldEVycm9ycyhkYXRhLmVycm9ycyk7XG4gICAgICAgICAgICB9KVxuICAgIH1cblxuICAgIF9pc0Zvcm1WYWxpZCgpIHtcbiAgICAgICAgY29uc3QgbG9jYWxzID0gdGhpcy5sb2NhbHM7XG4gICAgICAgIGNvbnN0IGlzRW5vdWdoQ3JlZGl0cyA9IE51bWJlcihsb2NhbHMuJHZhbHVlLnZhbCgpKSA8PSBOdW1iZXIobG9jYWxzLiRjb3VudC50ZXh0KCkpO1xuICAgICAgICBsZXQgdmFsaWQgPSB0cnVlO1xuICAgICAgICBsZXQgZXJyb3JUZXh0ID0gJyc7XG5cbiAgICAgICAgaWYgKCF0aGlzLnZhbGlkYXRpb24uaXNWYWxpZElucHV0cygpKSB7XG4gICAgICAgICAgICB2YWxpZCA9IGZhbHNlO1xuICAgICAgICAgICAgZXJyb3JUZXh0ICs9IHRoaXMudmFsaWRhdGlvbi5nZXRFcnJvcnNUZXh0KCk7XG4gICAgICAgIH1cblxuICAgICAgICBpZiAoIWlzRW5vdWdoQ3JlZGl0cykge1xuICAgICAgICAgICAgdmFsaWQgPSBmYWxzZTtcbiAgICAgICAgICAgIGVycm9yVGV4dCArPSAnWW91IGNhbm7igJl0IGdpdmUgbW9yZSB0aGFuICcgKyBsb2NhbHMuJGNvdW50LnRleHQoKSArICcgY3JlZGl0cy4gJztcbiAgICAgICAgfVxuXG4gICAgICAgIGlmICghdmFsaWQpIHtcbiAgICAgICAgICAgIGxvY2Fscy4kZXJyb3IudGV4dChlcnJvclRleHQpO1xuICAgICAgICB9XG5cbiAgICAgICAgcmV0dXJuIHZhbGlkO1xuICAgIH1cblxuICAgIF9zZW5kUmVxdWVzdCgpIHtcbiAgICAgICAgcmV0dXJuICQucG9zdCh0aGlzLiRyb290LmF0dHIoJ2FjdGlvbicpLFxuICAgICAgICAgICAge1xuICAgICAgICAgICAgICAgIGdpdmU6IHRoaXMubG9jYWxzLiR2YWx1ZS52YWwoKSxcbiAgICAgICAgICAgICAgICB0bzogdGhpcy5sb2NhbHMuJHRvLnZhbCgpLFxuICAgICAgICAgICAgICAgIHRvZGF0YTogdGhpcy5sb2NhbHMuJHRvRGF0YS52YWwoKSxcbiAgICAgICAgICAgICAgICBtZXNzYWdlOiB0aGlzLmxvY2Fscy4kbWVzc2FnZS52YWwoKVxuICAgICAgICAgICAgfVxuICAgICAgICApO1xuICAgIH1cblxuXG4gICAgLy8gc3RhdGljXG4gICAgc3RhdGljIHBsdWdpbihzZWxlY3Rvcikge1xuICAgICAgICBjb25zdCAkZWxlbXMgPSAkKHNlbGVjdG9yKTtcbiAgICAgICAgaWYgKCEkZWxlbXMubGVuZ3RoKSByZXR1cm47XG5cbiAgICAgICAgcmV0dXJuICRlbGVtcy5lYWNoKGZ1bmN0aW9uIChpbmRleCwgZWwpIHtcbiAgICAgICAgICAgIGxldCAkZWxlbWVudCA9ICQoZWwpO1xuICAgICAgICAgICAgbGV0IGRhdGEgICAgID0gJGVsZW1lbnQuZGF0YSgnd2lkZ2V0Jyk7XG5cbiAgICAgICAgICAgIGlmICghZGF0YSkge1xuICAgICAgICAgICAgICAgIGRhdGEgPSBuZXcgV2lkZ2V0KGVsKTtcbiAgICAgICAgICAgICAgICAkZWxlbWVudC5kYXRhKCd3aWRnZXQnLCBkYXRhKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgfSlcbiAgICB9XG59XG5cblxuXG5cbi8qKiBXRUJQQUNLIEZPT1RFUiAqKlxuICoqIC4vZnJvbnRlbmQvanMvY3JlZGl0cy93aWRnZXRzL19zZW5kLWNyZWRpdHMuanNcbiAqKi8iXSwic291cmNlUm9vdCI6IiJ9