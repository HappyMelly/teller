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

	module.exports = __webpack_require__(32);


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
/* 32 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';
	
	var _asyncTabs = __webpack_require__(33);
	
	var _asyncTabs2 = _interopRequireDefault(_asyncTabs);
	
	var _filterHistory = __webpack_require__(34);
	
	var _filterHistory2 = _interopRequireDefault(_filterHistory);
	
	var _sendCredits = __webpack_require__(35);
	
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
/* 33 */
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
	
	            App.events.sub('hmt.asynctabs.refresh', this._onEventRefresh.bind(this));
	        }
	    }, {
	        key: '_onClickLink',
	        value: function _onClickLink(e) {
	            e.preventDefault();
	            var $link = $(e.currentTarget);
	
	            if ($link.hasClass('state_active')) return;
	            this.showTabByLink($link);
	        }
	    }, {
	        key: '_onEventRefresh',
	        value: function _onEventRefresh() {
	            var $currentLink = this.locals.$links.filter('.state_active').first();
	            var indexCurrentTab = this.loadedTabs.indexOf($currentLink.attr('href'));
	
	            this.loadedTabs.splice(indexCurrentTab, 1);
	            this.showTabByLink($currentLink);
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
	         * @param {jQuery} target   - div where we should insert conten
	         */
	
	    }, {
	        key: '_loadContent',
	        value: function _loadContent(url, target) {
	            var self = this;
	            var isShouldLoad = $.inArray(target, self.loadedTabs) < 0 && url;
	            var defer = $.Deferred();
	
	            if (isShouldLoad) {
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
/* 34 */
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
	
	            App.events.sub('hmt.sendCredit.success', this._onAddNewItem.bind(this));
	        }
	    }, {
	        key: '_onClickFilter',
	        value: function _onClickFilter(e) {
	            e.preventDefault();
	            var $link = $(e.currentTarget);
	
	            if ($link.hasClass('state_selected')) return;
	            this.filterByLink($link);
	        }
	    }, {
	        key: 'filterByLink',
	        value: function filterByLink($link) {
	            var filterText = $link.data('filter-link');
	
	            this.setActiveLink($link);
	            this.filterList(filterText);
	        }
	    }, {
	        key: '_onAddNewItem',
	        value: function _onAddNewItem(data) {}
	
	        /**
	         * Filter list through text
	         * @param {String} filterText
	         */
	
	    }, {
	        key: 'filterList',
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
	            if ($el.hasClass('state_selected')) return;
	
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
/* 35 */
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
	 * Suggestion item
	 * @typedef {Object} Suggestion
	 * @property {String} value - name of persion
	 * @property {Number} data.id - id of person
	 * @property {String} data.img - url of image
	 */
	
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
	            var self = this;
	            var locals = this.locals;
	            var url = jsRoutes.controllers.cm.Facilitators.search(this.$root.attr('data-brand-id')).url;
	
	            locals.$to.autocomplete({
	                serviceUrl: url,
	                paramName: 'query',
	                minChars: 3,
	                preserveInput: true,
	                onSelect: function onSelect(suggestion) {
	                    locals.$to.val(suggestion.name);
	                    locals.$toData.val(suggestion.data);
	                    return true;
	                },
	                formatResult: function formatResult(suggestion, currentValue) {
	                    return suggestion.value;
	                },
	                transformResult: function transformResult(response) {
	                    var suggestions = $.parseJSON(response).suggestions;
	
	                    return {
	                        suggestions: suggestions.map(function (item) {
	                            var template = self._getSuggestTemplate(item);
	
	                            return {
	                                value: template,
	                                data: item.data.id,
	                                name: item.value
	                            };
	                        })
	                    };
	                }
	            });
	        }
	
	        /**
	         * Render template for suggestion
	         * @param {Suggestion} data - suggestion object
	         * @private
	         */
	
	    }, {
	        key: '_getSuggestTemplate',
	        value: function _getSuggestTemplate(item) {
	            return '<div class="b-suggest__img" style="background-image: url(' + item.data.img + ')"></div><div class="b-suggest__name">' + item.value + '</div>';
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
	
	                success("You have sent credits successfully!", 4500);
	                self._setNewValues();
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
	            var creditsLeft = Number(locals.$count.text());
	            var isEnoughCredits = Number(locals.$value.val()) <= creditsLeft;
	            var valid = true;
	            var errorText = '';
	
	            if (!this.validation.isValidInputs()) {
	                valid = false;
	                errorText += this.validation.getErrorsText();
	            }
	
	            if (creditsLeft == 0) {
	                valid = false;
	                errorText += 'You have no more credits to share. ';
	            } else if (!isEnoughCredits) {
	                valid = false;
	                errorText += 'You cannot give more than ' + locals.$count.text() + ' credits. ';
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
	                amount: this.locals.$value.val(),
	                to: this.locals.$toData.val(),
	                reason: this.locals.$message.val()
	            });
	        }
	    }, {
	        key: '_setNewValues',
	        value: function _setNewValues() {
	            App.events.pub('hmt.asynctabs.refresh');
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
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIndlYnBhY2s6Ly8vd2VicGFjay9ib290c3RyYXAgZjBmMTY2OTBiZDk3YTI1NWNmYjk/N2VmNCoqIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9oZWxwZXJzL2NsYXNzQ2FsbENoZWNrLmpzPzIxYWYqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvaGVscGVycy9jcmVhdGVDbGFzcy5qcz8xZGZlKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL2NvcmUtanMvb2JqZWN0L2RlZmluZS1wcm9wZXJ0eS5qcz81ZjcwKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L2ZuL29iamVjdC9kZWZpbmUtcHJvcGVydHkuanM/NGQzMyoqIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL2VzNi5vYmplY3QuZGVmaW5lLXByb3BlcnR5LmpzPzhiZGUqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZXhwb3J0LmpzPzhlNDAqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZ2xvYmFsLmpzPzNjNTIqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fY29yZS5qcz9kNjExKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2N0eC5qcz80ZTU5KioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2EtZnVuY3Rpb24uanM/MDY5OSoqIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19oaWRlLmpzPzBkMmUqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fb2JqZWN0LWRwLmpzPzU5ODYqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fYW4tb2JqZWN0LmpzPzNhZjIqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9faXMtb2JqZWN0LmpzP2NmZGEqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9faWU4LWRvbS1kZWZpbmUuanM/YjEwMioqIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19kZXNjcmlwdG9ycy5qcz9jMGY1KioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2ZhaWxzLmpzP2M2ZGQqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZG9tLWNyZWF0ZS5qcz82MTJmKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX3RvLXByaW1pdGl2ZS5qcz8xYTY1KioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX3Byb3BlcnR5LWRlc2MuanM/MjU2YioqIiwid2VicGFjazovLy8uL2Zyb250ZW5kL2pzL2NvbW1vbi9fZm9ybS1oZWxwZXIuanM/ODYzNiIsIndlYnBhY2s6Ly8vLi9mcm9udGVuZC9qcy9jcmVkaXRzL2NyZWRpdC1wYWdlLmpzIiwid2VicGFjazovLy8uL2Zyb250ZW5kL2pzL2NvbW1vbi9fYXN5bmMtdGFicy5qcyIsIndlYnBhY2s6Ly8vLi9mcm9udGVuZC9qcy9jcmVkaXRzL3dpZGdldHMvX2ZpbHRlci1oaXN0b3J5LmpzIiwid2VicGFjazovLy8uL2Zyb250ZW5kL2pzL2NyZWRpdHMvd2lkZ2V0cy9fc2VuZC1jcmVkaXRzLmpzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiI7QUFBQTtBQUNBOztBQUVBO0FBQ0E7O0FBRUE7QUFDQTtBQUNBOztBQUVBO0FBQ0E7QUFDQSx1QkFBZTtBQUNmO0FBQ0E7QUFDQTs7QUFFQTtBQUNBOztBQUVBO0FBQ0E7O0FBRUE7QUFDQTtBQUNBOzs7QUFHQTtBQUNBOztBQUVBO0FBQ0E7O0FBRUE7QUFDQTs7QUFFQTtBQUNBOzs7Ozs7Ozs7Ozs7Ozs7O0FDdENBOztBQUVBOztBQUVBO0FBQ0E7QUFDQTtBQUNBO0FBQ0EsRzs7Ozs7O0FDUkE7O0FBRUE7O0FBRUE7O0FBRUE7O0FBRUEsdUNBQXNDLHVDQUF1QyxnQkFBZ0I7O0FBRTdGO0FBQ0E7QUFDQSxvQkFBbUIsa0JBQWtCO0FBQ3JDO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBOztBQUVBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQSxFQUFDLEc7Ozs7OztBQzFCRCxtQkFBa0IsdUQ7Ozs7OztBQ0FsQjtBQUNBO0FBQ0E7QUFDQTtBQUNBLEc7Ozs7OztBQ0pBO0FBQ0E7QUFDQSxzRUFBdUUsMENBQTBDLEU7Ozs7OztBQ0ZqSDtBQUNBO0FBQ0E7QUFDQTtBQUNBOztBQUVBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0Esb0VBQW1FO0FBQ25FO0FBQ0Esc0ZBQXFGO0FBQ3JGO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQSxZQUFXO0FBQ1gsVUFBUztBQUNUO0FBQ0E7QUFDQTtBQUNBO0FBQ0EsTUFBSztBQUNMO0FBQ0E7QUFDQSxnREFBK0M7QUFDL0M7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0EsZUFBYztBQUNkLGVBQWM7QUFDZCxlQUFjO0FBQ2QsZUFBYztBQUNkLGdCQUFlO0FBQ2YsZ0JBQWU7QUFDZixnQkFBZTtBQUNmLGlCQUFnQjtBQUNoQiwwQjs7Ozs7O0FDNURBO0FBQ0E7QUFDQTtBQUNBLHdDQUF1QyxnQzs7Ozs7O0FDSHZDLDhCQUE2QjtBQUM3QixzQ0FBcUMsZ0M7Ozs7OztBQ0RyQztBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBLEc7Ozs7OztBQ25CQTtBQUNBO0FBQ0E7QUFDQSxHOzs7Ozs7QUNIQTtBQUNBO0FBQ0E7QUFDQTtBQUNBLEVBQUM7QUFDRDtBQUNBO0FBQ0EsRzs7Ozs7O0FDUEE7QUFDQTtBQUNBO0FBQ0E7O0FBRUE7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0EsSUFBRyxVQUFVO0FBQ2I7QUFDQTtBQUNBO0FBQ0EsRzs7Ozs7O0FDZkE7QUFDQTtBQUNBO0FBQ0E7QUFDQSxHOzs7Ozs7QUNKQTtBQUNBO0FBQ0EsRzs7Ozs7O0FDRkE7QUFDQSxzRUFBc0UsZ0JBQWdCLFVBQVUsR0FBRztBQUNuRyxFQUFDLEU7Ozs7OztBQ0ZEO0FBQ0E7QUFDQSxrQ0FBaUMsUUFBUSxnQkFBZ0IsVUFBVSxHQUFHO0FBQ3RFLEVBQUMsRTs7Ozs7O0FDSEQ7QUFDQTtBQUNBO0FBQ0EsSUFBRztBQUNIO0FBQ0E7QUFDQSxHOzs7Ozs7QUNOQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQSxHOzs7Ozs7QUNOQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0EsRzs7Ozs7O0FDWEE7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQSxHOzs7Ozs7QUNQQTs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7S0FTcUI7Ozs7OztBQUtqQixjQUxpQixVQUtqQixDQUFZLFNBQVosRUFBdUI7NkNBTE4sWUFLTTs7QUFDbkIsY0FBSyxTQUFMLEdBQWlCLFNBQWpCLENBRG1CO0FBRW5CLGNBQUssU0FBTCxHQUFpQixFQUFqQixDQUZtQjtBQUduQixjQUFLLGFBQUwsR0FIbUI7TUFBdkI7O2dDQUxpQjs7eUNBV0Q7OztBQUNaLGtCQUFLLFNBQUwsQ0FBZSxFQUFmLENBQWtCLGNBQWxCLEVBQWtDLFVBQUMsQ0FBRCxFQUFPO0FBQ3JDLHFCQUFNLFdBQVcsRUFBRSxFQUFFLGFBQUYsQ0FBYixDQUQrQjs7QUFHckMsdUJBQUssa0JBQUwsQ0FBd0IsUUFBeEIsRUFIcUM7QUFJckMsdUJBQUssWUFBTCxDQUFrQixRQUFsQixFQUpxQztjQUFQLENBQWxDLENBRFk7Ozs7NENBU0csVUFBUztBQUN4QixpQkFBSSxTQUFTLFFBQVQsQ0FBa0IsY0FBbEIsQ0FBSixFQUF1QztBQUNuQywwQkFBUyxHQUFULENBQWEsU0FBUyxHQUFULEdBQWUsT0FBZixDQUF1QixTQUF2QixFQUFrQyxFQUFsQyxDQUFiLEVBRG1DO2NBQXZDOztBQUlBLGlCQUFJLFNBQVMsUUFBVCxDQUFrQixjQUFsQixDQUFKLEVBQXVDO0FBQ25DLDBCQUFTLEdBQVQsQ0FBYSxTQUFTLEdBQVQsR0FBZSxPQUFmLENBQXVCLEtBQXZCLEVBQThCLEVBQTlCLENBQWIsRUFEbUM7Y0FBdkM7Ozs7eUNBS1k7OztBQUNaLGlCQUFNLFlBQVksS0FBSyxTQUFMLENBRE47QUFFWixpQkFBSSxRQUFRLENBQVIsQ0FGUTs7QUFJWix1QkFBVSxJQUFWLENBQWUsVUFBQyxLQUFELEVBQVEsT0FBUixFQUFvQjtBQUMvQixxQkFBTSxXQUFXLEVBQUUsT0FBRixDQUFYLENBRHlCOztBQUcvQixxQkFBSSxDQUFDLE9BQUssYUFBTCxDQUFtQixRQUFuQixDQUFELEVBQStCO0FBQy9CLDhCQUFTLENBQVQsQ0FEK0I7a0JBQW5DO2NBSFcsQ0FBZixDQUpZO0FBV1osb0JBQU8sUUFBUSxDQUFDLEtBQUQsQ0FBZixDQVhZOzs7Ozs7Ozs7Ozt1Q0FtQkYsVUFBVTtBQUNwQixpQkFBTSxRQUFRLEVBQUUsSUFBRixDQUFPLFNBQVMsR0FBVCxFQUFQLENBQVIsQ0FEYzs7QUFHcEIsaUJBQUksQ0FBQyxLQUFELElBQVUsQ0FBQyxTQUFTLFFBQVQsQ0FBa0IsZUFBbEIsQ0FBRCxFQUFxQztBQUMvQyxzQkFBSyxTQUFMLENBQWUsUUFBZixFQUF5QixPQUF6QixFQUQrQztBQUUvQyx3QkFBTyxLQUFQLENBRitDO2NBQW5EOztBQUtBLGlCQUFJLFFBQUMsQ0FBUyxRQUFULENBQWtCLFlBQWxCLENBQUQsSUFBcUMsQ0FBQyxLQUFLLGFBQUwsQ0FBbUIsS0FBbkIsQ0FBRCxFQUE0QjtBQUNqRSxzQkFBSyxTQUFMLENBQWUsUUFBZixFQUF5QixvQkFBekIsRUFEaUU7QUFFakUsd0JBQU8sS0FBUCxDQUZpRTtjQUFyRTs7QUFLQSxvQkFBTyxJQUFQLENBYm9COzs7Ozs7Ozs7Ozt1Q0FxQlYsT0FBTztBQUNqQixpQkFBSSxLQUFLLHdKQUFMLENBRGE7QUFFakIsb0JBQU8sR0FBRyxJQUFILENBQVEsS0FBUixDQUFQLENBRmlCOzs7Ozs7Ozs7Ozs7bUNBV1gsVUFBVSxXQUErQjtpQkFBcEIsb0VBQWMsb0JBQU07O0FBQy9DLGlCQUFNLFVBQVUsU0FBUyxNQUFULEVBQVYsQ0FEeUM7QUFFL0MsaUJBQU0sU0FBUyxRQUFRLElBQVIsQ0FBYSxVQUFiLENBQVQsQ0FGeUM7O0FBSS9DLGlCQUFJLE9BQU8sTUFBUCxFQUFlLE9BQW5COztBQUVBLHFCQUFRLFFBQVIsQ0FBaUIsY0FBakIsRUFOK0M7O0FBUS9DLDRCQUFlLEVBQUUseUJBQUYsRUFDVixJQURVLENBQ0wsU0FESyxFQUVWLFFBRlUsQ0FFRCxPQUZDLENBQWYsQ0FSK0M7O0FBWS9DLGtCQUFLLFNBQUwsQ0FBZSxJQUFmLENBQW9CO0FBQ2hCLHVCQUFNLFNBQVMsSUFBVCxDQUFjLE1BQWQsQ0FBTjtBQUNBLHdCQUFPLFNBQVA7Y0FGSixFQVorQzs7Ozs7Ozs7OztzQ0FzQnRDLFVBQVU7QUFDbkIsaUJBQU0sVUFBVSxTQUFTLE1BQVQsRUFBVixDQURhOztBQUduQixxQkFDSyxXQURMLENBQ2lCLGNBRGpCLEVBRUssSUFGTCxDQUVVLFVBRlYsRUFFc0IsTUFGdEIsR0FIbUI7O0FBT25CLGtCQUFLLFNBQUwsR0FBaUIsS0FBSyxTQUFMLENBQWUsTUFBZixDQUFzQixVQUFVLElBQVYsRUFBZ0I7QUFDbkQsd0JBQU8sS0FBSyxJQUFMLEtBQWMsU0FBUyxJQUFULENBQWMsTUFBZCxDQUFkLENBRDRDO2NBQWhCLENBQXZDLENBUG1COzs7Ozs7Ozs7OzttQ0FpQmIsUUFBNEI7OztpQkFBcEIsb0VBQWMsb0JBQU07O0FBQ2xDLG9CQUFPLE9BQVAsQ0FBZSxVQUFDLElBQUQsRUFBVTtBQUNyQixxQkFBTSxrQkFBa0IsT0FBSyxTQUFMLENBQWUsTUFBZixDQUFzQixZQUFZLEtBQUssSUFBTCxHQUFZLElBQXhCLENBQXRCLENBQW9ELEtBQXBELEVBQWxCLENBRGU7O0FBR3JCLHFCQUFJLGdCQUFnQixNQUFoQixFQUF3QixPQUFLLFNBQUwsQ0FBZSxlQUFmLEVBQWdDLEtBQUssS0FBTCxFQUFZLFdBQTVDLEVBQTVCO2NBSFcsQ0FBZixDQURrQzs7Ozs7Ozs7Ozs7dUNBYXhCLFFBQVE7QUFDbEIsaUJBQU0sWUFBWSxVQUFVLEtBQUssU0FBTCxDQURWO0FBRWxCLGlCQUFJLFdBQVcsRUFBWCxDQUZjOztBQUlsQix1QkFBVSxPQUFWLENBQWtCLFVBQUMsSUFBRCxFQUFVO0FBQ3hCLHFCQUFNLE9BQU8sS0FBSyxJQUFMLENBQVUsQ0FBVixFQUFhLFdBQWIsS0FBNkIsS0FBSyxJQUFMLENBQVUsTUFBVixDQUFpQixDQUFqQixDQUE3QixDQURXOztBQUd4Qiw2QkFBZSxjQUFTLEtBQUssS0FBTCxPQUF4QixDQUh3QjtjQUFWLENBQWxCLENBSmtCOztBQVVsQixvQkFBTyxRQUFQLENBVmtCOzs7Ozs7Ozs7Ozt1Q0FrQlIsUUFBUTtBQUNsQixpQkFBTSxPQUFPLElBQVAsQ0FEWTtBQUVsQixpQkFBTSxZQUFZLFVBQVUsS0FBSyxTQUFMLENBRlY7QUFHbEIsaUJBQUksV0FBVyxFQUFYLENBSGM7O0FBS2xCLHVCQUFVLE9BQVYsQ0FBa0IsVUFBQyxJQUFELEVBQVU7QUFDeEIscUJBQU0sV0FBVyxLQUFLLFNBQUwsQ0FBZSxNQUFmLGFBQWdDLEtBQUssSUFBTCxPQUFoQyxFQUErQyxLQUEvQyxFQUFYLENBRGtCO0FBRXhCLHFCQUFNLE9BQU8sU0FBUyxNQUFULEdBQWlCLFNBQVMsSUFBVCxDQUFjLE9BQWQsQ0FBakIsR0FBeUMsS0FBSyxJQUFMLENBRjlCOztBQUl4QixxQ0FBa0Isa0JBQWEsS0FBSyxLQUFMLGdCQUEvQixDQUp3QjtjQUFWLENBQWxCLENBTGtCOztBQVlsQixvQkFBTyxRQUFQLENBWmtCOzs7O3VDQWVUO0FBQ1QsaUJBQUksV0FBVyxFQUFYLENBREs7O0FBR1Qsa0JBQUssU0FBTCxDQUFlLEdBQWYsQ0FBbUIsVUFBQyxLQUFELEVBQVEsRUFBUixFQUFlO0FBQzlCLHFCQUFNLE1BQU0sRUFBRSxFQUFGLENBQU4sQ0FEd0I7QUFFOUIscUJBQU0sT0FBTyxJQUFJLElBQUosQ0FBUyxNQUFULENBQVAsQ0FGd0I7O0FBSTlCLHFCQUFJLENBQUMsSUFBRCxFQUFPLE9BQVg7O0FBRUEscUJBQUksSUFBSSxFQUFKLENBQU8sV0FBUCxDQUFKLEVBQXdCO0FBQ3BCLDhCQUFTLElBQVQsSUFBaUIsSUFBSSxJQUFKLENBQVMsU0FBVCxDQUFqQixDQURvQjtrQkFBeEIsTUFFTztBQUNILDhCQUFTLElBQVQsSUFBaUIsSUFBSSxHQUFKLEVBQWpCLENBREc7a0JBRlA7Y0FOZSxDQUFuQixDQUhTOztBQWdCVCxvQkFBTyxRQUFQLENBaEJTOzs7Ozs7Ozs7d0NBc0JFOzs7QUFDWCxrQkFBSyxTQUFMLENBQWUsSUFBZixDQUFvQixVQUFDLEtBQUQsRUFBUSxFQUFSLEVBQWU7QUFDL0IscUJBQU0sTUFBTSxFQUFFLEVBQUYsQ0FBTixDQUR5QjtBQUUvQix3QkFBSyxZQUFMLENBQWtCLEdBQWxCLEVBRitCO2NBQWYsQ0FBcEIsQ0FEVzs7OztxQ0FPSDtBQUNSLGtCQUFLLFNBQUwsQ0FBZSxJQUFmLENBQW9CLFVBQUMsS0FBRCxFQUFRLEVBQVIsRUFBZTtBQUMvQixxQkFBTSxNQUFNLEVBQUUsRUFBRixDQUFOLENBRHlCO0FBRS9CLHFCQUFJLENBQUMsSUFBSSxJQUFKLENBQVMsVUFBVCxDQUFELEVBQXdCLElBQUksR0FBSixDQUFRLEVBQVIsRUFBNUI7Y0FGZ0IsQ0FBcEIsQ0FEUTs7O1lBbk1LOzs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7QUNKckIsR0FBRSxZQUFVO0FBQ1IseUJBQVMsTUFBVCxDQUFnQixrQkFBaEIsRUFEUTs7QUFHVCxTQUFJLE1BQUosQ0FDTSxHQUROLENBQ1Usb0JBRFYsRUFDZ0MsWUFBSztBQUM1QixpQ0FBYyxNQUFkLENBQXFCLG9CQUFyQixFQUQ0QjtBQUU1QiwrQkFBVyxNQUFYLENBQWtCLGlCQUFsQixFQUY0QjtNQUFMLENBRGhDLENBSFM7RUFBVixDQUFGLEM7Ozs7OztBQ0xBOzs7Ozs7Ozs7Ozs7Ozs7O0tBRXFCO0FBRWpCLGNBRmlCLE1BRWpCLENBQVksUUFBWixFQUFzQjs2Q0FGTCxRQUVLOztBQUNsQixjQUFLLEtBQUwsR0FBYSxFQUFFLFFBQUYsQ0FBYixDQURrQjtBQUVsQixjQUFLLFVBQUwsR0FBa0IsRUFBbEIsQ0FGa0I7QUFHbEIsY0FBSyxNQUFMLEdBQWMsS0FBSyxPQUFMLEVBQWQsQ0FIa0I7O0FBS2xCLGNBQUssYUFBTCxHQUxrQjs7QUFPbEIsYUFBSSxhQUFhLEtBQUssTUFBTCxDQUFZLE1BQVosQ0FBbUIsS0FBbkIsRUFBYixDQVBjO0FBUWxCLGNBQUssYUFBTCxDQUFtQixVQUFuQixFQVJrQjtNQUF0Qjs7Z0NBRmlCOzttQ0FhUDtBQUNOLG9CQUFPO0FBQ0gseUJBQVEsS0FBSyxLQUFMLENBQVcsSUFBWCxDQUFnQixpQkFBaEIsQ0FBUjtjQURKLENBRE07Ozs7eUNBTU07QUFDWixrQkFBSyxLQUFMLENBQVcsRUFBWCxDQUFjLE9BQWQsRUFBdUIsaUJBQXZCLEVBQTBDLEtBQUssWUFBTCxDQUFrQixJQUFsQixDQUF1QixJQUF2QixDQUExQyxFQURZOztBQUdaLGlCQUFJLE1BQUosQ0FBVyxHQUFYLENBQWUsdUJBQWYsRUFBd0MsS0FBSyxlQUFMLENBQXFCLElBQXJCLENBQTBCLElBQTFCLENBQXhDLEVBSFk7Ozs7c0NBTUgsR0FBRztBQUNaLGVBQUUsY0FBRixHQURZO0FBRVosaUJBQUksUUFBUSxFQUFFLEVBQUUsYUFBRixDQUFWLENBRlE7O0FBSVosaUJBQUksTUFBTSxRQUFOLENBQWUsY0FBZixDQUFKLEVBQW9DLE9BQXBDO0FBQ0Esa0JBQUssYUFBTCxDQUFtQixLQUFuQixFQUxZOzs7OzJDQVFDO0FBQ2IsaUJBQU0sZUFBZSxLQUFLLE1BQUwsQ0FBWSxNQUFaLENBQW1CLE1BQW5CLENBQTBCLGVBQTFCLEVBQTJDLEtBQTNDLEVBQWYsQ0FETztBQUViLGlCQUFNLGtCQUFrQixLQUFLLFVBQUwsQ0FBZ0IsT0FBaEIsQ0FBd0IsYUFBYSxJQUFiLENBQWtCLE1BQWxCLENBQXhCLENBQWxCLENBRk87O0FBSWIsa0JBQUssVUFBTCxDQUFnQixNQUFoQixDQUF1QixlQUF2QixFQUF3QyxDQUF4QyxFQUphO0FBS2Isa0JBQUssYUFBTCxDQUFtQixZQUFuQixFQUxhOzs7Ozs7Ozs7Ozt1Q0FhSCxPQUFNO0FBQ2hCLGlCQUFNLE1BQU0sTUFBTSxJQUFOLENBQVcsV0FBWCxDQUFOLENBRFU7QUFFaEIsaUJBQU0sU0FBUyxNQUFNLElBQU4sQ0FBVyxNQUFYLENBQVQsQ0FGVTtBQUdoQixpQkFBTSxPQUFPLElBQVAsQ0FIVTs7QUFLaEIsa0JBQUssWUFBTCxDQUFrQixHQUFsQixFQUF1QixNQUF2QixFQUNLLElBREwsQ0FDWSxZQUFJO0FBQ1IsdUJBQU0sUUFBTixDQUFlLGNBQWYsRUFBK0IsUUFBL0IsR0FBMEMsV0FBMUMsQ0FBc0QsY0FBdEQsRUFEUTtBQUVSLHVCQUFNLEdBQU4sQ0FBVSxNQUFWLEVBRlE7O0FBSVIscUJBQUksTUFBSixDQUFXLEdBQVgsQ0FBZSxvQkFBZixFQUpRO2NBQUosQ0FEWixDQUxnQjs7Ozs7Ozs7Ozs7c0NBbUJQLEtBQUssUUFBTztBQUNyQixpQkFBTSxPQUFPLElBQVAsQ0FEZTtBQUVyQixpQkFBTSxlQUFnQixFQUFFLE9BQUYsQ0FBVSxNQUFWLEVBQWtCLEtBQUssVUFBTCxDQUFsQixHQUFxQyxDQUFyQyxJQUEwQyxHQUExQyxDQUZEO0FBR3JCLGlCQUFJLFFBQVEsRUFBRSxRQUFGLEVBQVIsQ0FIaUI7O0FBS3JCLGlCQUFJLFlBQUosRUFBa0I7QUFDZCxtQkFBRSxHQUFGLENBQU0sR0FBTixFQUFXLFVBQUMsSUFBRCxFQUFVO0FBQ2pCLDBCQUFLLFVBQUwsQ0FBZ0IsSUFBaEIsQ0FBcUIsTUFBckIsRUFEaUI7QUFFakIsdUJBQUUsTUFBRixFQUFVLElBQVYsQ0FBZSxJQUFmLEVBRmlCOztBQUlqQiwyQkFBTSxPQUFOLEdBSmlCO2tCQUFWLENBQVgsQ0FEYztjQUFsQixNQU9PO0FBQ0gsdUJBQU0sT0FBTixHQURHO2NBUFA7O0FBV0Esb0JBQU8sTUFBTSxPQUFOLEVBQVAsQ0FoQnFCOzs7Ozs7O2dDQW9CWCxVQUFVO0FBQ3BCLGlCQUFNLFNBQVMsRUFBRSxRQUFGLENBQVQsQ0FEYztBQUVwQixpQkFBSSxDQUFDLE9BQU8sTUFBUCxFQUFlLE9BQXBCOztBQUVBLG9CQUFPLE9BQU8sSUFBUCxDQUFZLFVBQVUsS0FBVixFQUFpQixFQUFqQixFQUFxQjtBQUNwQyxxQkFBSSxXQUFXLEVBQUUsRUFBRixDQUFYLENBRGdDO0FBRXBDLHFCQUFJLE9BQVcsU0FBUyxJQUFULENBQWMsUUFBZCxDQUFYLENBRmdDOztBQUlwQyxxQkFBSSxDQUFDLElBQUQsRUFBTztBQUNQLDRCQUFPLElBQUksTUFBSixDQUFXLEVBQVgsQ0FBUCxDQURPO0FBRVAsOEJBQVMsSUFBVCxDQUFjLFFBQWQsRUFBd0IsSUFBeEIsRUFGTztrQkFBWDtjQUplLENBQW5CLENBSm9COzs7WUFyRlA7Ozs7Ozs7OztBQ0ZyQjs7Ozs7Ozs7Ozs7Ozs7OztLQUdxQjs7Ozs7O0FBS2pCLGNBTGlCLE1BS2pCLENBQVksUUFBWixFQUFzQjs2Q0FMTCxRQUtLOztBQUNsQixjQUFLLEtBQUwsR0FBYSxFQUFFLFFBQUYsQ0FBYixDQURrQjtBQUVsQixjQUFLLE1BQUwsR0FBYyxLQUFLLE9BQUwsRUFBZCxDQUZrQjs7QUFJbEIsY0FBSyxhQUFMLEdBSmtCO01BQXRCOztnQ0FMaUI7O21DQVlQO0FBQ04saUJBQU0sUUFBUSxLQUFLLEtBQUwsQ0FEUjs7QUFHTixvQkFBTztBQUNILHdCQUFPLE1BQU0sSUFBTixDQUFXLG9CQUFYLENBQVA7QUFDQSx5QkFBUSxNQUFNLElBQU4sQ0FBVyxvQkFBWCxDQUFSO2NBRkosQ0FITTs7Ozt5Q0FTTTtBQUNaLGtCQUFLLEtBQUwsQ0FBVyxFQUFYLENBQWMsT0FBZCxFQUF1QixvQkFBdkIsRUFBNkMsS0FBSyxjQUFMLENBQW9CLElBQXBCLENBQXlCLElBQXpCLENBQTdDLEVBRFk7O0FBR1osaUJBQUksTUFBSixDQUFXLEdBQVgsQ0FBZSx3QkFBZixFQUF5QyxLQUFLLGFBQUwsQ0FBbUIsSUFBbkIsQ0FBd0IsSUFBeEIsQ0FBekMsRUFIWTs7Ozt3Q0FNRCxHQUFHO0FBQ2QsZUFBRSxjQUFGLEdBRGM7QUFFZCxpQkFBTSxRQUFRLEVBQUUsRUFBRSxhQUFGLENBQVYsQ0FGUTs7QUFJZCxpQkFBSSxNQUFNLFFBQU4sQ0FBZSxnQkFBZixDQUFKLEVBQXNDLE9BQXRDO0FBQ0Esa0JBQUssWUFBTCxDQUFrQixLQUFsQixFQUxjOzs7O3NDQVFMLE9BQU07QUFDZixpQkFBTSxhQUFhLE1BQU0sSUFBTixDQUFXLGFBQVgsQ0FBYixDQURTOztBQUdmLGtCQUFLLGFBQUwsQ0FBbUIsS0FBbkIsRUFIZTtBQUlmLGtCQUFLLFVBQUwsQ0FBZ0IsVUFBaEIsRUFKZTs7Ozt1Q0FPTCxNQUFLOzs7Ozs7Ozs7b0NBUVIsWUFBWTtBQUNuQixpQkFBTSxTQUFTLEtBQUssTUFBTCxDQUFZLE1BQVosQ0FESTs7QUFHbkIsaUJBQUksY0FBYyxLQUFkLEVBQXFCO0FBQ3JCLHdCQUFPLFdBQVAsQ0FBbUIsY0FBbkIsRUFEcUI7QUFFckIsd0JBRnFCO2NBQXpCOztBQUtBLG9CQUFPLElBQVAsQ0FBWSxVQUFDLEtBQUQsRUFBUSxFQUFSLEVBQWU7QUFDdkIscUJBQU0sTUFBTSxFQUFFLEVBQUYsQ0FBTixDQURpQjtBQUV2QixxQkFBTSxXQUFXLElBQUksSUFBSixDQUFTLGFBQVQsRUFBd0IsT0FBeEIsQ0FBZ0MsVUFBaEMsTUFBZ0QsQ0FBQyxDQUFELENBRjFDOztBQUl2QixxQkFBSSxXQUFKLENBQWdCLGNBQWhCLEVBQWdDLFFBQWhDLEVBSnVCO2NBQWYsQ0FBWixDQVJtQjs7Ozs7Ozs7Ozt1Q0FvQlQsS0FBSztBQUNmLGlCQUFJLElBQUksUUFBSixDQUFhLGdCQUFiLENBQUosRUFBb0MsT0FBcEM7O0FBRUEsaUJBQUksUUFBSixDQUFhLGdCQUFiLEVBQ0ssUUFETCxHQUNnQixXQURoQixDQUM0QixnQkFENUIsRUFIZTs7Ozs7OztnQ0FRTCxVQUFVO0FBQ3BCLGlCQUFNLFNBQVMsRUFBRSxRQUFGLENBQVQsQ0FEYztBQUVwQixpQkFBSSxDQUFDLE9BQU8sTUFBUCxFQUFlLE9BQXBCOztBQUVBLG9CQUFPLE9BQU8sSUFBUCxDQUFZLFVBQVUsS0FBVixFQUFpQixFQUFqQixFQUFxQjtBQUNwQyxxQkFBSSxXQUFXLEVBQUUsRUFBRixDQUFYLENBRGdDO0FBRXBDLHFCQUFJLE9BQVcsU0FBUyxJQUFULENBQWMsUUFBZCxDQUFYLENBRmdDOztBQUlwQyxxQkFBSSxDQUFDLElBQUQsRUFBTztBQUNQLDRCQUFPLElBQUksTUFBSixDQUFXLEVBQVgsQ0FBUCxDQURPO0FBRVAsOEJBQVMsSUFBVCxDQUFjLFFBQWQsRUFBd0IsSUFBeEIsRUFGTztrQkFBWDtjQUplLENBQW5CLENBSm9COzs7WUE5RVA7Ozs7Ozs7OztBQ0hyQjs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7S0FnQnFCO0FBRWpCLGNBRmlCLE1BRWpCLENBQVksUUFBWixFQUFzQjs2Q0FGTCxRQUVLOztBQUNsQixjQUFLLEtBQUwsR0FBYSxFQUFFLFFBQUYsQ0FBYixDQURrQjtBQUVsQixjQUFLLE1BQUwsR0FBYyxLQUFLLE9BQUwsRUFBZCxDQUZrQjtBQUdsQixjQUFLLFVBQUwsR0FBa0IseUJBQWUsS0FBSyxLQUFMLENBQVcsSUFBWCxDQUFnQixtQkFBaEIsQ0FBZixDQUFsQixDQUhrQjs7QUFLbEIsYUFBSSxDQUFDLFFBQVEsRUFBRSxFQUFGLENBQUssWUFBTCxDQUFULEVBQTRCO0FBQzVCLHFCQUFRLEdBQVIsQ0FBWSxxREFBWixFQUQ0QjtBQUU1QixvQkFGNEI7VUFBaEM7QUFJQSxjQUFLLGlCQUFMLEdBVGtCO0FBVWxCLGNBQUssYUFBTCxHQVZrQjtNQUF0Qjs7Z0NBRmlCOzttQ0FlUDtBQUNOLGlCQUFNLFFBQVEsS0FBSyxLQUFMLENBRFI7O0FBR04sb0JBQU87QUFDSCx5QkFBUSxNQUFNLElBQU4sQ0FBVyxzQkFBWCxDQUFSO0FBQ0EseUJBQVEsTUFBTSxJQUFOLENBQVcscUJBQVgsQ0FBUjtBQUNBLHNCQUFLLE1BQU0sSUFBTixDQUFXLGtCQUFYLENBQUw7QUFDQSwwQkFBUyxNQUFNLElBQU4sQ0FBVyx1QkFBWCxDQUFUO0FBQ0EsMkJBQVUsTUFBTSxJQUFOLENBQVcsdUJBQVgsQ0FBVjtBQUNBLHlCQUFRLE1BQU0sSUFBTixDQUFXLHFCQUFYLENBQVI7Y0FOSixDQUhNOzs7OzZDQWFVO0FBQ2hCLGlCQUFNLE9BQU8sSUFBUCxDQURVO0FBRWhCLGlCQUFNLFNBQVMsS0FBSyxNQUFMLENBRkM7QUFHaEIsaUJBQU0sTUFBTSxTQUFTLFdBQVQsQ0FBcUIsRUFBckIsQ0FBd0IsWUFBeEIsQ0FBcUMsTUFBckMsQ0FBNEMsS0FBSyxLQUFMLENBQVcsSUFBWCxDQUFnQixlQUFoQixDQUE1QyxFQUE4RSxHQUE5RSxDQUhJOztBQUtoQixvQkFBTyxHQUFQLENBQVcsWUFBWCxDQUF3QjtBQUNwQiw2QkFBWSxHQUFaO0FBQ0EsNEJBQVcsT0FBWDtBQUNBLDJCQUFVLENBQVY7QUFDQSxnQ0FBZSxJQUFmO0FBQ0EsMkJBQVUsa0JBQVUsVUFBVixFQUFzQjtBQUM1Qiw0QkFBTyxHQUFQLENBQVcsR0FBWCxDQUFlLFdBQVcsSUFBWCxDQUFmLENBRDRCO0FBRTVCLDRCQUFPLE9BQVAsQ0FBZSxHQUFmLENBQW1CLFdBQVcsSUFBWCxDQUFuQixDQUY0QjtBQUc1Qiw0QkFBTyxJQUFQLENBSDRCO2tCQUF0QjtBQUtWLCtCQUFjLHNCQUFVLFVBQVYsRUFBc0IsWUFBdEIsRUFBb0M7QUFDOUMsNEJBQU8sV0FBVyxLQUFYLENBRHVDO2tCQUFwQztBQUdkLGtDQUFpQix5QkFBUyxRQUFULEVBQW1CO0FBQ2hDLHlCQUFNLGNBQWMsRUFBRSxTQUFGLENBQVksUUFBWixFQUFzQixXQUF0QixDQURZOztBQUdoQyw0QkFBTztBQUNILHNDQUFhLFlBQVksR0FBWixDQUFnQixVQUFTLElBQVQsRUFBYztBQUN2QyxpQ0FBSSxXQUFXLEtBQUssbUJBQUwsQ0FBeUIsSUFBekIsQ0FBWCxDQURtQzs7QUFHdkMsb0NBQU87QUFDSCx3Q0FBTyxRQUFQO0FBQ0EsdUNBQU0sS0FBSyxJQUFMLENBQVUsRUFBVjtBQUNOLHVDQUFNLEtBQUssS0FBTDs4QkFIVixDQUh1QzswQkFBZCxDQUE3QjtzQkFESixDQUhnQztrQkFBbkI7Y0FickIsRUFMZ0I7Ozs7Ozs7Ozs7OzZDQXlDQyxNQUFLO0FBQ3RCLGtGQUFtRSxLQUFLLElBQUwsQ0FBVSxHQUFWLDhDQUFzRCxLQUFLLEtBQUwsV0FBekgsQ0FEc0I7Ozs7eUNBSVY7OztBQUNaLGtCQUFLLEtBQUwsQ0FDSyxFQURMLENBQ1EsT0FEUixFQUNpQixPQURqQixFQUMwQixVQUFDLENBQUQ7d0JBQU8sTUFBSyxNQUFMLENBQVksTUFBWixDQUFtQixJQUFuQixDQUF3QixFQUF4QjtjQUFQLENBRDFCLENBRUssRUFGTCxDQUVRLFFBRlIsRUFFa0IsS0FBSyxhQUFMLENBQW1CLElBQW5CLENBQXdCLElBQXhCLENBRmxCLEVBRFk7Ozs7dUNBTUYsR0FBRztBQUNiLGVBQUUsY0FBRixHQURhO0FBRWIsaUJBQU0sT0FBTyxJQUFQLENBRk87O0FBSWIsaUJBQUksQ0FBQyxLQUFLLFlBQUwsRUFBRCxFQUFzQixPQUFPLEtBQVAsQ0FBMUI7O0FBRUEsa0JBQUssWUFBTCxHQUNLLElBREwsQ0FDVSxZQUFNO0FBQ1Isc0JBQUssVUFBTCxDQUFnQixTQUFoQixHQURROztBQUdSLHlCQUFRLHFDQUFSLEVBQStDLElBQS9DLEVBSFE7QUFJUixzQkFBSyxhQUFMLEdBSlE7Y0FBTixDQURWLENBT0ssSUFQTCxDQU9VLFVBQUMsUUFBRCxFQUFjO0FBQ2hCLHFCQUFNLE9BQU8sRUFBRSxTQUFGLENBQVksU0FBUyxZQUFULENBQVosQ0FBbUMsSUFBbkMsQ0FERztBQUVoQixxQkFBTSxZQUFZLEtBQUssVUFBTCxDQUFnQixhQUFoQixDQUE4QixLQUFLLE1BQUwsQ0FBMUMsQ0FGVTs7QUFJaEIscUJBQUksQ0FBQyxLQUFLLE1BQUwsRUFBYSxPQUFsQjs7QUFFQSxzQkFBSyxNQUFMLENBQVksTUFBWixDQUFtQixJQUFuQixDQUF3QixTQUF4QixFQU5nQjtBQU9oQixzQkFBSyxVQUFMLENBQWdCLFNBQWhCLENBQTBCLEtBQUssTUFBTCxDQUExQixDQVBnQjtjQUFkLENBUFYsQ0FOYTs7Ozt3Q0F3QkY7QUFDWCxpQkFBTSxTQUFTLEtBQUssTUFBTCxDQURKO0FBRVgsaUJBQU0sY0FBYyxPQUFPLE9BQU8sTUFBUCxDQUFjLElBQWQsRUFBUCxDQUFkLENBRks7QUFHWCxpQkFBTSxrQkFBa0IsT0FBTyxPQUFPLE1BQVAsQ0FBYyxHQUFkLEVBQVAsS0FBK0IsV0FBL0IsQ0FIYjtBQUlYLGlCQUFJLFFBQVEsSUFBUixDQUpPO0FBS1gsaUJBQUksWUFBWSxFQUFaLENBTE87O0FBT1gsaUJBQUksQ0FBQyxLQUFLLFVBQUwsQ0FBZ0IsYUFBaEIsRUFBRCxFQUFrQztBQUNsQyx5QkFBUSxLQUFSLENBRGtDO0FBRWxDLDhCQUFhLEtBQUssVUFBTCxDQUFnQixhQUFoQixFQUFiLENBRmtDO2NBQXRDOztBQUtBLGlCQUFJLGVBQWUsQ0FBZixFQUFrQjtBQUNsQix5QkFBUSxLQUFSLENBRGtCO0FBRWxCLDhCQUFhLHFDQUFiLENBRmtCO2NBQXRCLE1BR08sSUFBSSxDQUFDLGVBQUQsRUFBa0I7QUFDekIseUJBQVEsS0FBUixDQUR5QjtBQUV6Qiw4QkFBYSwrQkFBK0IsT0FBTyxNQUFQLENBQWMsSUFBZCxFQUEvQixHQUFzRCxZQUF0RCxDQUZZO2NBQXRCOztBQUtQLGlCQUFJLENBQUMsS0FBRCxFQUFRO0FBQ1Isd0JBQU8sTUFBUCxDQUFjLElBQWQsQ0FBbUIsU0FBbkIsRUFEUTtjQUFaOztBQUlBLG9CQUFPLEtBQVAsQ0F4Qlc7Ozs7d0NBMkJBO0FBQ1gsb0JBQU8sRUFBRSxJQUFGLENBQU8sS0FBSyxLQUFMLENBQVcsSUFBWCxDQUFnQixRQUFoQixDQUFQLEVBQ0g7QUFDSSx5QkFBUSxLQUFLLE1BQUwsQ0FBWSxNQUFaLENBQW1CLEdBQW5CLEVBQVI7QUFDQSxxQkFBSSxLQUFLLE1BQUwsQ0FBWSxPQUFaLENBQW9CLEdBQXBCLEVBQUo7QUFDQSx5QkFBUSxLQUFLLE1BQUwsQ0FBWSxRQUFaLENBQXFCLEdBQXJCLEVBQVI7Y0FKRCxDQUFQLENBRFc7Ozs7eUNBVUM7QUFDWixpQkFBSSxNQUFKLENBQVcsR0FBWCxDQUFlLHVCQUFmLEVBRFk7Ozs7Ozs7Z0NBS0YsVUFBVTtBQUNwQixpQkFBTSxTQUFTLEVBQUUsUUFBRixDQUFULENBRGM7QUFFcEIsaUJBQUksQ0FBQyxPQUFPLE1BQVAsRUFBZSxPQUFwQjs7QUFFQSxvQkFBTyxPQUFPLElBQVAsQ0FBWSxVQUFVLEtBQVYsRUFBaUIsRUFBakIsRUFBcUI7QUFDcEMscUJBQUksV0FBVyxFQUFFLEVBQUYsQ0FBWCxDQURnQztBQUVwQyxxQkFBSSxPQUFXLFNBQVMsSUFBVCxDQUFjLFFBQWQsQ0FBWCxDQUZnQzs7QUFJcEMscUJBQUksQ0FBQyxJQUFELEVBQU87QUFDUCw0QkFBTyxJQUFJLE1BQUosQ0FBVyxFQUFYLENBQVAsQ0FETztBQUVQLDhCQUFTLElBQVQsQ0FBYyxRQUFkLEVBQXdCLElBQXhCLEVBRk87a0JBQVg7Y0FKZSxDQUFuQixDQUpvQjs7O1lBakpQIiwiZmlsZSI6ImNyZWRpdC1wYWdlLmpzIiwic291cmNlc0NvbnRlbnQiOlsiIFx0Ly8gVGhlIG1vZHVsZSBjYWNoZVxuIFx0dmFyIGluc3RhbGxlZE1vZHVsZXMgPSB7fTtcblxuIFx0Ly8gVGhlIHJlcXVpcmUgZnVuY3Rpb25cbiBcdGZ1bmN0aW9uIF9fd2VicGFja19yZXF1aXJlX18obW9kdWxlSWQpIHtcblxuIFx0XHQvLyBDaGVjayBpZiBtb2R1bGUgaXMgaW4gY2FjaGVcbiBcdFx0aWYoaW5zdGFsbGVkTW9kdWxlc1ttb2R1bGVJZF0pXG4gXHRcdFx0cmV0dXJuIGluc3RhbGxlZE1vZHVsZXNbbW9kdWxlSWRdLmV4cG9ydHM7XG5cbiBcdFx0Ly8gQ3JlYXRlIGEgbmV3IG1vZHVsZSAoYW5kIHB1dCBpdCBpbnRvIHRoZSBjYWNoZSlcbiBcdFx0dmFyIG1vZHVsZSA9IGluc3RhbGxlZE1vZHVsZXNbbW9kdWxlSWRdID0ge1xuIFx0XHRcdGV4cG9ydHM6IHt9LFxuIFx0XHRcdGlkOiBtb2R1bGVJZCxcbiBcdFx0XHRsb2FkZWQ6IGZhbHNlXG4gXHRcdH07XG5cbiBcdFx0Ly8gRXhlY3V0ZSB0aGUgbW9kdWxlIGZ1bmN0aW9uXG4gXHRcdG1vZHVsZXNbbW9kdWxlSWRdLmNhbGwobW9kdWxlLmV4cG9ydHMsIG1vZHVsZSwgbW9kdWxlLmV4cG9ydHMsIF9fd2VicGFja19yZXF1aXJlX18pO1xuXG4gXHRcdC8vIEZsYWcgdGhlIG1vZHVsZSBhcyBsb2FkZWRcbiBcdFx0bW9kdWxlLmxvYWRlZCA9IHRydWU7XG5cbiBcdFx0Ly8gUmV0dXJuIHRoZSBleHBvcnRzIG9mIHRoZSBtb2R1bGVcbiBcdFx0cmV0dXJuIG1vZHVsZS5leHBvcnRzO1xuIFx0fVxuXG5cbiBcdC8vIGV4cG9zZSB0aGUgbW9kdWxlcyBvYmplY3QgKF9fd2VicGFja19tb2R1bGVzX18pXG4gXHRfX3dlYnBhY2tfcmVxdWlyZV9fLm0gPSBtb2R1bGVzO1xuXG4gXHQvLyBleHBvc2UgdGhlIG1vZHVsZSBjYWNoZVxuIFx0X193ZWJwYWNrX3JlcXVpcmVfXy5jID0gaW5zdGFsbGVkTW9kdWxlcztcblxuIFx0Ly8gX193ZWJwYWNrX3B1YmxpY19wYXRoX19cbiBcdF9fd2VicGFja19yZXF1aXJlX18ucCA9IFwiXCI7XG5cbiBcdC8vIExvYWQgZW50cnkgbW9kdWxlIGFuZCByZXR1cm4gZXhwb3J0c1xuIFx0cmV0dXJuIF9fd2VicGFja19yZXF1aXJlX18oMCk7XG5cblxuXG4vKiogV0VCUEFDSyBGT09URVIgKipcbiAqKiB3ZWJwYWNrL2Jvb3RzdHJhcCBmMGYxNjY5MGJkOTdhMjU1Y2ZiOVxuICoqLyIsIlwidXNlIHN0cmljdFwiO1xuXG5leHBvcnRzLl9fZXNNb2R1bGUgPSB0cnVlO1xuXG5leHBvcnRzLmRlZmF1bHQgPSBmdW5jdGlvbiAoaW5zdGFuY2UsIENvbnN0cnVjdG9yKSB7XG4gIGlmICghKGluc3RhbmNlIGluc3RhbmNlb2YgQ29uc3RydWN0b3IpKSB7XG4gICAgdGhyb3cgbmV3IFR5cGVFcnJvcihcIkNhbm5vdCBjYWxsIGEgY2xhc3MgYXMgYSBmdW5jdGlvblwiKTtcbiAgfVxufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL2hlbHBlcnMvY2xhc3NDYWxsQ2hlY2suanNcbiAqKiBtb2R1bGUgaWQgPSAzXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJcInVzZSBzdHJpY3RcIjtcblxuZXhwb3J0cy5fX2VzTW9kdWxlID0gdHJ1ZTtcblxudmFyIF9kZWZpbmVQcm9wZXJ0eSA9IHJlcXVpcmUoXCJiYWJlbC1ydW50aW1lL2NvcmUtanMvb2JqZWN0L2RlZmluZS1wcm9wZXJ0eVwiKTtcblxudmFyIF9kZWZpbmVQcm9wZXJ0eTIgPSBfaW50ZXJvcFJlcXVpcmVEZWZhdWx0KF9kZWZpbmVQcm9wZXJ0eSk7XG5cbmZ1bmN0aW9uIF9pbnRlcm9wUmVxdWlyZURlZmF1bHQob2JqKSB7IHJldHVybiBvYmogJiYgb2JqLl9fZXNNb2R1bGUgPyBvYmogOiB7IGRlZmF1bHQ6IG9iaiB9OyB9XG5cbmV4cG9ydHMuZGVmYXVsdCA9IGZ1bmN0aW9uICgpIHtcbiAgZnVuY3Rpb24gZGVmaW5lUHJvcGVydGllcyh0YXJnZXQsIHByb3BzKSB7XG4gICAgZm9yICh2YXIgaSA9IDA7IGkgPCBwcm9wcy5sZW5ndGg7IGkrKykge1xuICAgICAgdmFyIGRlc2NyaXB0b3IgPSBwcm9wc1tpXTtcbiAgICAgIGRlc2NyaXB0b3IuZW51bWVyYWJsZSA9IGRlc2NyaXB0b3IuZW51bWVyYWJsZSB8fCBmYWxzZTtcbiAgICAgIGRlc2NyaXB0b3IuY29uZmlndXJhYmxlID0gdHJ1ZTtcbiAgICAgIGlmIChcInZhbHVlXCIgaW4gZGVzY3JpcHRvcikgZGVzY3JpcHRvci53cml0YWJsZSA9IHRydWU7XG4gICAgICAoMCwgX2RlZmluZVByb3BlcnR5Mi5kZWZhdWx0KSh0YXJnZXQsIGRlc2NyaXB0b3Iua2V5LCBkZXNjcmlwdG9yKTtcbiAgICB9XG4gIH1cblxuICByZXR1cm4gZnVuY3Rpb24gKENvbnN0cnVjdG9yLCBwcm90b1Byb3BzLCBzdGF0aWNQcm9wcykge1xuICAgIGlmIChwcm90b1Byb3BzKSBkZWZpbmVQcm9wZXJ0aWVzKENvbnN0cnVjdG9yLnByb3RvdHlwZSwgcHJvdG9Qcm9wcyk7XG4gICAgaWYgKHN0YXRpY1Byb3BzKSBkZWZpbmVQcm9wZXJ0aWVzKENvbnN0cnVjdG9yLCBzdGF0aWNQcm9wcyk7XG4gICAgcmV0dXJuIENvbnN0cnVjdG9yO1xuICB9O1xufSgpO1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvaGVscGVycy9jcmVhdGVDbGFzcy5qc1xuICoqIG1vZHVsZSBpZCA9IDRcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsIm1vZHVsZS5leHBvcnRzID0geyBcImRlZmF1bHRcIjogcmVxdWlyZShcImNvcmUtanMvbGlicmFyeS9mbi9vYmplY3QvZGVmaW5lLXByb3BlcnR5XCIpLCBfX2VzTW9kdWxlOiB0cnVlIH07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9jb3JlLWpzL29iamVjdC9kZWZpbmUtcHJvcGVydHkuanNcbiAqKiBtb2R1bGUgaWQgPSA1XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJyZXF1aXJlKCcuLi8uLi9tb2R1bGVzL2VzNi5vYmplY3QuZGVmaW5lLXByb3BlcnR5Jyk7XG52YXIgJE9iamVjdCA9IHJlcXVpcmUoJy4uLy4uL21vZHVsZXMvX2NvcmUnKS5PYmplY3Q7XG5tb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uIGRlZmluZVByb3BlcnR5KGl0LCBrZXksIGRlc2Mpe1xuICByZXR1cm4gJE9iamVjdC5kZWZpbmVQcm9wZXJ0eShpdCwga2V5LCBkZXNjKTtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9mbi9vYmplY3QvZGVmaW5lLXByb3BlcnR5LmpzXG4gKiogbW9kdWxlIGlkID0gNlxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwidmFyICRleHBvcnQgPSByZXF1aXJlKCcuL19leHBvcnQnKTtcclxuLy8gMTkuMS4yLjQgLyAxNS4yLjMuNiBPYmplY3QuZGVmaW5lUHJvcGVydHkoTywgUCwgQXR0cmlidXRlcylcclxuJGV4cG9ydCgkZXhwb3J0LlMgKyAkZXhwb3J0LkYgKiAhcmVxdWlyZSgnLi9fZGVzY3JpcHRvcnMnKSwgJ09iamVjdCcsIHtkZWZpbmVQcm9wZXJ0eTogcmVxdWlyZSgnLi9fb2JqZWN0LWRwJykuZn0pO1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9lczYub2JqZWN0LmRlZmluZS1wcm9wZXJ0eS5qc1xuICoqIG1vZHVsZSBpZCA9IDdcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsInZhciBnbG9iYWwgICAgPSByZXF1aXJlKCcuL19nbG9iYWwnKVxuICAsIGNvcmUgICAgICA9IHJlcXVpcmUoJy4vX2NvcmUnKVxuICAsIGN0eCAgICAgICA9IHJlcXVpcmUoJy4vX2N0eCcpXG4gICwgaGlkZSAgICAgID0gcmVxdWlyZSgnLi9faGlkZScpXG4gICwgUFJPVE9UWVBFID0gJ3Byb3RvdHlwZSc7XG5cbnZhciAkZXhwb3J0ID0gZnVuY3Rpb24odHlwZSwgbmFtZSwgc291cmNlKXtcbiAgdmFyIElTX0ZPUkNFRCA9IHR5cGUgJiAkZXhwb3J0LkZcbiAgICAsIElTX0dMT0JBTCA9IHR5cGUgJiAkZXhwb3J0LkdcbiAgICAsIElTX1NUQVRJQyA9IHR5cGUgJiAkZXhwb3J0LlNcbiAgICAsIElTX1BST1RPICA9IHR5cGUgJiAkZXhwb3J0LlBcbiAgICAsIElTX0JJTkQgICA9IHR5cGUgJiAkZXhwb3J0LkJcbiAgICAsIElTX1dSQVAgICA9IHR5cGUgJiAkZXhwb3J0LldcbiAgICAsIGV4cG9ydHMgICA9IElTX0dMT0JBTCA/IGNvcmUgOiBjb3JlW25hbWVdIHx8IChjb3JlW25hbWVdID0ge30pXG4gICAgLCBleHBQcm90byAgPSBleHBvcnRzW1BST1RPVFlQRV1cbiAgICAsIHRhcmdldCAgICA9IElTX0dMT0JBTCA/IGdsb2JhbCA6IElTX1NUQVRJQyA/IGdsb2JhbFtuYW1lXSA6IChnbG9iYWxbbmFtZV0gfHwge30pW1BST1RPVFlQRV1cbiAgICAsIGtleSwgb3duLCBvdXQ7XG4gIGlmKElTX0dMT0JBTClzb3VyY2UgPSBuYW1lO1xuICBmb3Ioa2V5IGluIHNvdXJjZSl7XG4gICAgLy8gY29udGFpbnMgaW4gbmF0aXZlXG4gICAgb3duID0gIUlTX0ZPUkNFRCAmJiB0YXJnZXQgJiYgdGFyZ2V0W2tleV0gIT09IHVuZGVmaW5lZDtcbiAgICBpZihvd24gJiYga2V5IGluIGV4cG9ydHMpY29udGludWU7XG4gICAgLy8gZXhwb3J0IG5hdGl2ZSBvciBwYXNzZWRcbiAgICBvdXQgPSBvd24gPyB0YXJnZXRba2V5XSA6IHNvdXJjZVtrZXldO1xuICAgIC8vIHByZXZlbnQgZ2xvYmFsIHBvbGx1dGlvbiBmb3IgbmFtZXNwYWNlc1xuICAgIGV4cG9ydHNba2V5XSA9IElTX0dMT0JBTCAmJiB0eXBlb2YgdGFyZ2V0W2tleV0gIT0gJ2Z1bmN0aW9uJyA/IHNvdXJjZVtrZXldXG4gICAgLy8gYmluZCB0aW1lcnMgdG8gZ2xvYmFsIGZvciBjYWxsIGZyb20gZXhwb3J0IGNvbnRleHRcbiAgICA6IElTX0JJTkQgJiYgb3duID8gY3R4KG91dCwgZ2xvYmFsKVxuICAgIC8vIHdyYXAgZ2xvYmFsIGNvbnN0cnVjdG9ycyBmb3IgcHJldmVudCBjaGFuZ2UgdGhlbSBpbiBsaWJyYXJ5XG4gICAgOiBJU19XUkFQICYmIHRhcmdldFtrZXldID09IG91dCA/IChmdW5jdGlvbihDKXtcbiAgICAgIHZhciBGID0gZnVuY3Rpb24oYSwgYiwgYyl7XG4gICAgICAgIGlmKHRoaXMgaW5zdGFuY2VvZiBDKXtcbiAgICAgICAgICBzd2l0Y2goYXJndW1lbnRzLmxlbmd0aCl7XG4gICAgICAgICAgICBjYXNlIDA6IHJldHVybiBuZXcgQztcbiAgICAgICAgICAgIGNhc2UgMTogcmV0dXJuIG5ldyBDKGEpO1xuICAgICAgICAgICAgY2FzZSAyOiByZXR1cm4gbmV3IEMoYSwgYik7XG4gICAgICAgICAgfSByZXR1cm4gbmV3IEMoYSwgYiwgYyk7XG4gICAgICAgIH0gcmV0dXJuIEMuYXBwbHkodGhpcywgYXJndW1lbnRzKTtcbiAgICAgIH07XG4gICAgICBGW1BST1RPVFlQRV0gPSBDW1BST1RPVFlQRV07XG4gICAgICByZXR1cm4gRjtcbiAgICAvLyBtYWtlIHN0YXRpYyB2ZXJzaW9ucyBmb3IgcHJvdG90eXBlIG1ldGhvZHNcbiAgICB9KShvdXQpIDogSVNfUFJPVE8gJiYgdHlwZW9mIG91dCA9PSAnZnVuY3Rpb24nID8gY3R4KEZ1bmN0aW9uLmNhbGwsIG91dCkgOiBvdXQ7XG4gICAgLy8gZXhwb3J0IHByb3RvIG1ldGhvZHMgdG8gY29yZS4lQ09OU1RSVUNUT1IlLm1ldGhvZHMuJU5BTUUlXG4gICAgaWYoSVNfUFJPVE8pe1xuICAgICAgKGV4cG9ydHMudmlydHVhbCB8fCAoZXhwb3J0cy52aXJ0dWFsID0ge30pKVtrZXldID0gb3V0O1xuICAgICAgLy8gZXhwb3J0IHByb3RvIG1ldGhvZHMgdG8gY29yZS4lQ09OU1RSVUNUT1IlLnByb3RvdHlwZS4lTkFNRSVcbiAgICAgIGlmKHR5cGUgJiAkZXhwb3J0LlIgJiYgZXhwUHJvdG8gJiYgIWV4cFByb3RvW2tleV0paGlkZShleHBQcm90bywga2V5LCBvdXQpO1xuICAgIH1cbiAgfVxufTtcbi8vIHR5cGUgYml0bWFwXG4kZXhwb3J0LkYgPSAxOyAgIC8vIGZvcmNlZFxuJGV4cG9ydC5HID0gMjsgICAvLyBnbG9iYWxcbiRleHBvcnQuUyA9IDQ7ICAgLy8gc3RhdGljXG4kZXhwb3J0LlAgPSA4OyAgIC8vIHByb3RvXG4kZXhwb3J0LkIgPSAxNjsgIC8vIGJpbmRcbiRleHBvcnQuVyA9IDMyOyAgLy8gd3JhcFxuJGV4cG9ydC5VID0gNjQ7ICAvLyBzYWZlXG4kZXhwb3J0LlIgPSAxMjg7IC8vIHJlYWwgcHJvdG8gbWV0aG9kIGZvciBgbGlicmFyeWAgXG5tb2R1bGUuZXhwb3J0cyA9ICRleHBvcnQ7XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19leHBvcnQuanNcbiAqKiBtb2R1bGUgaWQgPSA4XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCIvLyBodHRwczovL2dpdGh1Yi5jb20vemxvaXJvY2svY29yZS1qcy9pc3N1ZXMvODYjaXNzdWVjb21tZW50LTExNTc1OTAyOFxudmFyIGdsb2JhbCA9IG1vZHVsZS5leHBvcnRzID0gdHlwZW9mIHdpbmRvdyAhPSAndW5kZWZpbmVkJyAmJiB3aW5kb3cuTWF0aCA9PSBNYXRoXG4gID8gd2luZG93IDogdHlwZW9mIHNlbGYgIT0gJ3VuZGVmaW5lZCcgJiYgc2VsZi5NYXRoID09IE1hdGggPyBzZWxmIDogRnVuY3Rpb24oJ3JldHVybiB0aGlzJykoKTtcbmlmKHR5cGVvZiBfX2cgPT0gJ251bWJlcicpX19nID0gZ2xvYmFsOyAvLyBlc2xpbnQtZGlzYWJsZS1saW5lIG5vLXVuZGVmXG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19nbG9iYWwuanNcbiAqKiBtb2R1bGUgaWQgPSA5XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJ2YXIgY29yZSA9IG1vZHVsZS5leHBvcnRzID0ge3ZlcnNpb246ICcyLjIuMCd9O1xuaWYodHlwZW9mIF9fZSA9PSAnbnVtYmVyJylfX2UgPSBjb3JlOyAvLyBlc2xpbnQtZGlzYWJsZS1saW5lIG5vLXVuZGVmXG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19jb3JlLmpzXG4gKiogbW9kdWxlIGlkID0gMTBcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsIi8vIG9wdGlvbmFsIC8gc2ltcGxlIGNvbnRleHQgYmluZGluZ1xudmFyIGFGdW5jdGlvbiA9IHJlcXVpcmUoJy4vX2EtZnVuY3Rpb24nKTtcbm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oZm4sIHRoYXQsIGxlbmd0aCl7XG4gIGFGdW5jdGlvbihmbik7XG4gIGlmKHRoYXQgPT09IHVuZGVmaW5lZClyZXR1cm4gZm47XG4gIHN3aXRjaChsZW5ndGgpe1xuICAgIGNhc2UgMTogcmV0dXJuIGZ1bmN0aW9uKGEpe1xuICAgICAgcmV0dXJuIGZuLmNhbGwodGhhdCwgYSk7XG4gICAgfTtcbiAgICBjYXNlIDI6IHJldHVybiBmdW5jdGlvbihhLCBiKXtcbiAgICAgIHJldHVybiBmbi5jYWxsKHRoYXQsIGEsIGIpO1xuICAgIH07XG4gICAgY2FzZSAzOiByZXR1cm4gZnVuY3Rpb24oYSwgYiwgYyl7XG4gICAgICByZXR1cm4gZm4uY2FsbCh0aGF0LCBhLCBiLCBjKTtcbiAgICB9O1xuICB9XG4gIHJldHVybiBmdW5jdGlvbigvKiAuLi5hcmdzICovKXtcbiAgICByZXR1cm4gZm4uYXBwbHkodGhhdCwgYXJndW1lbnRzKTtcbiAgfTtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19jdHguanNcbiAqKiBtb2R1bGUgaWQgPSAxMVxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwibW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihpdCl7XG4gIGlmKHR5cGVvZiBpdCAhPSAnZnVuY3Rpb24nKXRocm93IFR5cGVFcnJvcihpdCArICcgaXMgbm90IGEgZnVuY3Rpb24hJyk7XG4gIHJldHVybiBpdDtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19hLWZ1bmN0aW9uLmpzXG4gKiogbW9kdWxlIGlkID0gMTJcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsInZhciBkUCAgICAgICAgID0gcmVxdWlyZSgnLi9fb2JqZWN0LWRwJylcbiAgLCBjcmVhdGVEZXNjID0gcmVxdWlyZSgnLi9fcHJvcGVydHktZGVzYycpO1xubW9kdWxlLmV4cG9ydHMgPSByZXF1aXJlKCcuL19kZXNjcmlwdG9ycycpID8gZnVuY3Rpb24ob2JqZWN0LCBrZXksIHZhbHVlKXtcbiAgcmV0dXJuIGRQLmYob2JqZWN0LCBrZXksIGNyZWF0ZURlc2MoMSwgdmFsdWUpKTtcbn0gOiBmdW5jdGlvbihvYmplY3QsIGtleSwgdmFsdWUpe1xuICBvYmplY3Rba2V5XSA9IHZhbHVlO1xuICByZXR1cm4gb2JqZWN0O1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2hpZGUuanNcbiAqKiBtb2R1bGUgaWQgPSAxM1xuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwidmFyIGFuT2JqZWN0ICAgICAgID0gcmVxdWlyZSgnLi9fYW4tb2JqZWN0JylcbiAgLCBJRThfRE9NX0RFRklORSA9IHJlcXVpcmUoJy4vX2llOC1kb20tZGVmaW5lJylcbiAgLCB0b1ByaW1pdGl2ZSAgICA9IHJlcXVpcmUoJy4vX3RvLXByaW1pdGl2ZScpXG4gICwgZFAgICAgICAgICAgICAgPSBPYmplY3QuZGVmaW5lUHJvcGVydHk7XG5cbmV4cG9ydHMuZiA9IHJlcXVpcmUoJy4vX2Rlc2NyaXB0b3JzJykgPyBPYmplY3QuZGVmaW5lUHJvcGVydHkgOiBmdW5jdGlvbiBkZWZpbmVQcm9wZXJ0eShPLCBQLCBBdHRyaWJ1dGVzKXtcbiAgYW5PYmplY3QoTyk7XG4gIFAgPSB0b1ByaW1pdGl2ZShQLCB0cnVlKTtcbiAgYW5PYmplY3QoQXR0cmlidXRlcyk7XG4gIGlmKElFOF9ET01fREVGSU5FKXRyeSB7XG4gICAgcmV0dXJuIGRQKE8sIFAsIEF0dHJpYnV0ZXMpO1xuICB9IGNhdGNoKGUpeyAvKiBlbXB0eSAqLyB9XG4gIGlmKCdnZXQnIGluIEF0dHJpYnV0ZXMgfHwgJ3NldCcgaW4gQXR0cmlidXRlcyl0aHJvdyBUeXBlRXJyb3IoJ0FjY2Vzc29ycyBub3Qgc3VwcG9ydGVkIScpO1xuICBpZigndmFsdWUnIGluIEF0dHJpYnV0ZXMpT1tQXSA9IEF0dHJpYnV0ZXMudmFsdWU7XG4gIHJldHVybiBPO1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX29iamVjdC1kcC5qc1xuICoqIG1vZHVsZSBpZCA9IDE0XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJ2YXIgaXNPYmplY3QgPSByZXF1aXJlKCcuL19pcy1vYmplY3QnKTtcbm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oaXQpe1xuICBpZighaXNPYmplY3QoaXQpKXRocm93IFR5cGVFcnJvcihpdCArICcgaXMgbm90IGFuIG9iamVjdCEnKTtcbiAgcmV0dXJuIGl0O1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2FuLW9iamVjdC5qc1xuICoqIG1vZHVsZSBpZCA9IDE1XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJtb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGl0KXtcbiAgcmV0dXJuIHR5cGVvZiBpdCA9PT0gJ29iamVjdCcgPyBpdCAhPT0gbnVsbCA6IHR5cGVvZiBpdCA9PT0gJ2Z1bmN0aW9uJztcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19pcy1vYmplY3QuanNcbiAqKiBtb2R1bGUgaWQgPSAxNlxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwibW9kdWxlLmV4cG9ydHMgPSAhcmVxdWlyZSgnLi9fZGVzY3JpcHRvcnMnKSAmJiAhcmVxdWlyZSgnLi9fZmFpbHMnKShmdW5jdGlvbigpe1xyXG4gIHJldHVybiBPYmplY3QuZGVmaW5lUHJvcGVydHkocmVxdWlyZSgnLi9fZG9tLWNyZWF0ZScpKCdkaXYnKSwgJ2EnLCB7Z2V0OiBmdW5jdGlvbigpeyByZXR1cm4gNzsgfX0pLmEgIT0gNztcclxufSk7XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19pZTgtZG9tLWRlZmluZS5qc1xuICoqIG1vZHVsZSBpZCA9IDE3XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCIvLyBUaGFuaydzIElFOCBmb3IgaGlzIGZ1bm55IGRlZmluZVByb3BlcnR5XG5tb2R1bGUuZXhwb3J0cyA9ICFyZXF1aXJlKCcuL19mYWlscycpKGZ1bmN0aW9uKCl7XG4gIHJldHVybiBPYmplY3QuZGVmaW5lUHJvcGVydHkoe30sICdhJywge2dldDogZnVuY3Rpb24oKXsgcmV0dXJuIDc7IH19KS5hICE9IDc7XG59KTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2Rlc2NyaXB0b3JzLmpzXG4gKiogbW9kdWxlIGlkID0gMThcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsIm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oZXhlYyl7XG4gIHRyeSB7XG4gICAgcmV0dXJuICEhZXhlYygpO1xuICB9IGNhdGNoKGUpe1xuICAgIHJldHVybiB0cnVlO1xuICB9XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZmFpbHMuanNcbiAqKiBtb2R1bGUgaWQgPSAxOVxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwidmFyIGlzT2JqZWN0ID0gcmVxdWlyZSgnLi9faXMtb2JqZWN0JylcbiAgLCBkb2N1bWVudCA9IHJlcXVpcmUoJy4vX2dsb2JhbCcpLmRvY3VtZW50XG4gIC8vIGluIG9sZCBJRSB0eXBlb2YgZG9jdW1lbnQuY3JlYXRlRWxlbWVudCBpcyAnb2JqZWN0J1xuICAsIGlzID0gaXNPYmplY3QoZG9jdW1lbnQpICYmIGlzT2JqZWN0KGRvY3VtZW50LmNyZWF0ZUVsZW1lbnQpO1xubW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihpdCl7XG4gIHJldHVybiBpcyA/IGRvY3VtZW50LmNyZWF0ZUVsZW1lbnQoaXQpIDoge307XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZG9tLWNyZWF0ZS5qc1xuICoqIG1vZHVsZSBpZCA9IDIwXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCIvLyA3LjEuMSBUb1ByaW1pdGl2ZShpbnB1dCBbLCBQcmVmZXJyZWRUeXBlXSlcbnZhciBpc09iamVjdCA9IHJlcXVpcmUoJy4vX2lzLW9iamVjdCcpO1xuLy8gaW5zdGVhZCBvZiB0aGUgRVM2IHNwZWMgdmVyc2lvbiwgd2UgZGlkbid0IGltcGxlbWVudCBAQHRvUHJpbWl0aXZlIGNhc2Vcbi8vIGFuZCB0aGUgc2Vjb25kIGFyZ3VtZW50IC0gZmxhZyAtIHByZWZlcnJlZCB0eXBlIGlzIGEgc3RyaW5nXG5tb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGl0LCBTKXtcbiAgaWYoIWlzT2JqZWN0KGl0KSlyZXR1cm4gaXQ7XG4gIHZhciBmbiwgdmFsO1xuICBpZihTICYmIHR5cGVvZiAoZm4gPSBpdC50b1N0cmluZykgPT0gJ2Z1bmN0aW9uJyAmJiAhaXNPYmplY3QodmFsID0gZm4uY2FsbChpdCkpKXJldHVybiB2YWw7XG4gIGlmKHR5cGVvZiAoZm4gPSBpdC52YWx1ZU9mKSA9PSAnZnVuY3Rpb24nICYmICFpc09iamVjdCh2YWwgPSBmbi5jYWxsKGl0KSkpcmV0dXJuIHZhbDtcbiAgaWYoIVMgJiYgdHlwZW9mIChmbiA9IGl0LnRvU3RyaW5nKSA9PSAnZnVuY3Rpb24nICYmICFpc09iamVjdCh2YWwgPSBmbi5jYWxsKGl0KSkpcmV0dXJuIHZhbDtcbiAgdGhyb3cgVHlwZUVycm9yKFwiQ2FuJ3QgY29udmVydCBvYmplY3QgdG8gcHJpbWl0aXZlIHZhbHVlXCIpO1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX3RvLXByaW1pdGl2ZS5qc1xuICoqIG1vZHVsZSBpZCA9IDIxXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJtb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGJpdG1hcCwgdmFsdWUpe1xuICByZXR1cm4ge1xuICAgIGVudW1lcmFibGUgIDogIShiaXRtYXAgJiAxKSxcbiAgICBjb25maWd1cmFibGU6ICEoYml0bWFwICYgMiksXG4gICAgd3JpdGFibGUgICAgOiAhKGJpdG1hcCAmIDQpLFxuICAgIHZhbHVlICAgICAgIDogdmFsdWVcbiAgfTtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19wcm9wZXJ0eS1kZXNjLmpzXG4gKiogbW9kdWxlIGlkID0gMjJcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsIid1c2Ugc3RyaWN0JztcblxuLyoqXG4gKiBFcnJvcnNcbiAqIEB0eXBlZGVmIHtPYmplY3R9IExpc3RFcnJvcnNcbiAqIEBwcm9wZXJ0eSB7U3RyaW5nfSBuYW1lIC0gbmFtZSBvZiBmaWVsZFxuICogQHByb3BlcnR5IHtTdHJpbmd9IGVycm9yIC0gZXJyb3IgZGVzY3JpcHRpb25cbiAqL1xuXG5leHBvcnQgZGVmYXVsdCBjbGFzcyBGb3JtSGVscGVyIHtcbiAgICAvKipcbiAgICAgKiBWYWxpZGF0ZSBmb3JtIHRocm91Z2ggaW5wdXRzXG4gICAgICogQHBhcmFtIHtqUXVlcnl9ICRjb250cm9sc1xuICAgICAqL1xuICAgIGNvbnN0cnVjdG9yKCRjb250cm9scykge1xuICAgICAgICB0aGlzLiRjb250cm9scyA9ICRjb250cm9scztcbiAgICAgICAgdGhpcy5hcnJFcnJvcnMgPSBbXTtcbiAgICAgICAgdGhpcy5fYXNzaWduRXZlbnRzKCk7XG4gICAgfVxuXG4gICAgX2Fzc2lnbkV2ZW50cygpIHtcbiAgICAgICAgdGhpcy4kY29udHJvbHMub24oJ2lucHV0IGNoYW5nZScsIChlKSA9PiB7XG4gICAgICAgICAgICBjb25zdCAkY29udHJvbCA9ICQoZS5jdXJyZW50VGFyZ2V0KTtcblxuICAgICAgICAgICAgdGhpcy5fdmFsaWRhdGVJbW1lZGlhdGUoJGNvbnRyb2wpO1xuICAgICAgICAgICAgdGhpcy5fcmVtb3ZlRXJyb3IoJGNvbnRyb2wpO1xuICAgICAgICB9KTtcbiAgICB9XG5cbiAgICBfdmFsaWRhdGVJbW1lZGlhdGUoJGNvbnRyb2wpe1xuICAgICAgICBpZiAoJGNvbnRyb2wuaGFzQ2xhc3MoJ3R5cGUtbnVtZXJpYycpKSB7XG4gICAgICAgICAgICAkY29udHJvbC52YWwoJGNvbnRyb2wudmFsKCkucmVwbGFjZSgvW15cXGRdKy9nLCAnJykpO1xuICAgICAgICB9XG5cbiAgICAgICAgaWYgKCRjb250cm9sLmhhc0NsYXNzKCd0eXBlLW5vc3BhY2UnKSkge1xuICAgICAgICAgICAgJGNvbnRyb2wudmFsKCRjb250cm9sLnZhbCgpLnJlcGxhY2UoL1xccy9nLCAnJykpO1xuICAgICAgICB9XG4gICAgfVxuXG4gICAgaXNWYWxpZElucHV0cygpIHtcbiAgICAgICAgY29uc3QgJGNvbnRyb2xzID0gdGhpcy4kY29udHJvbHM7XG4gICAgICAgIGxldCBlcnJvciA9IDA7XG5cbiAgICAgICAgJGNvbnRyb2xzLmVhY2goKGluZGV4LCBjb250cm9sKSA9PiB7XG4gICAgICAgICAgICBjb25zdCAkY29udHJvbCA9ICQoY29udHJvbCk7XG5cbiAgICAgICAgICAgIGlmICghdGhpcy5faXNWYWxpZElucHV0KCRjb250cm9sKSkge1xuICAgICAgICAgICAgICAgIGVycm9yICs9IDE7XG4gICAgICAgICAgICB9XG4gICAgICAgIH0pO1xuICAgICAgICByZXR1cm4gQm9vbGVhbighZXJyb3IpO1xuICAgIH1cblxuICAgIC8qKlxuICAgICAqIENoZWNrIGdpdmVuIGNvbnRyb2wsIGlzIGl0IHZhbGlkP1xuICAgICAqIEBwYXJhbSB7alF1ZXJ5fSAkY29udHJvbFxuICAgICAqIEByZXR1cm5zIHtib29sZWFufSAtIElzIHZhbGlkIGNvbnRyb2w/XG4gICAgICovXG4gICAgX2lzVmFsaWRJbnB1dCgkY29udHJvbCkge1xuICAgICAgICBjb25zdCB2YWx1ZSA9ICQudHJpbSgkY29udHJvbC52YWwoKSk7XG5cbiAgICAgICAgaWYgKCF2YWx1ZSAmJiAhJGNvbnRyb2wuaGFzQ2xhc3MoJ3R5cGUtb3B0aW9uYWwnKSkge1xuICAgICAgICAgICAgdGhpcy5fc2V0RXJyb3IoJGNvbnRyb2wsICdFbXB0eScpO1xuICAgICAgICAgICAgcmV0dXJuIGZhbHNlO1xuICAgICAgICB9XG5cbiAgICAgICAgaWYgKCgkY29udHJvbC5oYXNDbGFzcygndHlwZS1lbWFpbCcpKSAmJiAhdGhpcy5faXNWYWxpZEVtYWlsKHZhbHVlKSkge1xuICAgICAgICAgICAgdGhpcy5fc2V0RXJyb3IoJGNvbnRyb2wsICdFbWFpbCBpcyBub3QgdmFsaWQnKTtcbiAgICAgICAgICAgIHJldHVybiBmYWxzZTtcbiAgICAgICAgfVxuXG4gICAgICAgIHJldHVybiB0cnVlO1xuICAgIH1cblxuICAgIC8qKlxuICAgICAqIElzIEVtYWlsIHZhbGlkP1xuICAgICAqIEBwYXJhbSB7c3RyaW5nfSBlbWFpbFxuICAgICAqIEByZXR1cm5zIHtib29sZWFufVxuICAgICAqL1xuICAgIF9pc1ZhbGlkRW1haWwoZW1haWwpIHtcbiAgICAgICAgdmFyIHJlID0gL14oKFtePD4oKVxcW1xcXVxcXFwuLDs6XFxzQFwiXSsoXFwuW148PigpXFxbXFxdXFxcXC4sOzpcXHNAXCJdKykqKXwoXCIuK1wiKSlAKChcXFtbMC05XXsxLDN9XFwuWzAtOV17MSwzfVxcLlswLTldezEsM31cXC5bMC05XXsxLDN9XSl8KChbYS16QS1aXFwtMC05XStcXC4pK1thLXpBLVpdezIsfSkpJC87XG4gICAgICAgIHJldHVybiByZS50ZXN0KGVtYWlsKTtcbiAgICB9XG5cbiAgICAvKipcbiAgICAgKiBTZXQgZXJyb3IgZm9yIGNvbnRyb2xcbiAgICAgKiBAcGFyYW0ge2pRdWVyeX0gJGNvbnRyb2xcbiAgICAgKiBAcGFyYW0ge1N0cmluZ30gZXJyb3JUZXh0XG4gICAgICogQHBhcmFtIHtCb29sZWFufSBpbnNlcnRFcnJvclxuICAgICAqL1xuICAgIF9zZXRFcnJvcigkY29udHJvbCwgZXJyb3JUZXh0LCBpbnNlcnRFcnJvciA9IHRydWUpIHtcbiAgICAgICAgY29uc3QgJHBhcmVudCA9ICRjb250cm9sLnBhcmVudCgpO1xuICAgICAgICBjb25zdCAkZXJyb3IgPSAkcGFyZW50LmZpbmQoJy5iLWVycm9yJyk7XG5cbiAgICAgICAgaWYgKCRlcnJvci5sZW5ndGgpIHJldHVybjtcblxuICAgICAgICAkcGFyZW50LmFkZENsYXNzKCdiLWVycm9yX3Nob3cnKTtcbiAgICAgICAgXG4gICAgICAgIGluc2VydEVycm9yICYmICQoJzxkaXYgY2xhc3M9XCJiLWVycm9yXCIgLz4nKVxuICAgICAgICAgICAgLnRleHQoZXJyb3JUZXh0KVxuICAgICAgICAgICAgLmFwcGVuZFRvKCRwYXJlbnQpO1xuXG4gICAgICAgIHRoaXMuYXJyRXJyb3JzLnB1c2goe1xuICAgICAgICAgICAgbmFtZTogJGNvbnRyb2wuYXR0cignbmFtZScpLFxuICAgICAgICAgICAgZXJyb3I6IGVycm9yVGV4dFxuICAgICAgICB9KVxuICAgIH1cblxuICAgIC8qKlxuICAgICAqIFJlbW92ZSBlcnJvciBmb3IgY29udHJvbFxuICAgICAqIEBwYXJhbSB7alF1ZXJ5fSAkY29udHJvbFxuICAgICAqL1xuICAgIF9yZW1vdmVFcnJvcigkY29udHJvbCkge1xuICAgICAgICBjb25zdCAkcGFyZW50ID0gJGNvbnRyb2wucGFyZW50KCk7XG5cbiAgICAgICAgJHBhcmVudFxuICAgICAgICAgICAgLnJlbW92ZUNsYXNzKCdiLWVycm9yX3Nob3cnKVxuICAgICAgICAgICAgLmZpbmQoJy5iLWVycm9yJykucmVtb3ZlKCk7XG5cbiAgICAgICAgdGhpcy5hcnJFcnJvcnMgPSB0aGlzLmFyckVycm9ycy5maWx0ZXIoZnVuY3Rpb24gKGl0ZW0pIHtcbiAgICAgICAgICAgIHJldHVybiBpdGVtLm5hbWUgIT09ICRjb250cm9sLmF0dHIoJ25hbWUnKVxuICAgICAgICB9KVxuICAgIH1cblxuICAgIC8qKlxuICAgICAqIFNldCBlcnJvcnNcbiAgICAgKiBAcGFyYW0ge0FycmF5fSBlcnJvcnMgLSBbe25hbWU6IFwiZW1haWxcIiwgZXJyb3I6IFwiZW1wdHlcIn0sIHtuYW1lOiBcInBhc3N3b3JkXCIsIGVycm9yOiBcImVtcHR5XCJ9XVxuICAgICAqIEBwYXJhbSB7Qm9vbGVhbn0gaW5zZXJ0RXJyb3IgLSBpbnNlcnQgZXJyb3IgZGVzY3JpcHRpb24gdG8gdGhlIERvbSBcbiAgICAgKi9cbiAgICBzZXRFcnJvcnMoZXJyb3JzLCBpbnNlcnRFcnJvciA9IHRydWUpIHtcbiAgICAgICAgZXJyb3JzLmZvckVhY2goKGl0ZW0pID0+IHtcbiAgICAgICAgICAgIGNvbnN0ICRjdXJyZW50Q29udHJvbCA9IHRoaXMuJGNvbnRyb2xzLmZpbHRlcignW25hbWU9XCInICsgaXRlbS5uYW1lICsgJ1wiXScpLmZpcnN0KCk7XG5cbiAgICAgICAgICAgIGlmICgkY3VycmVudENvbnRyb2wubGVuZ3RoKSB0aGlzLl9zZXRFcnJvcigkY3VycmVudENvbnRyb2wsIGl0ZW0uZXJyb3IsIGluc2VydEVycm9yKVxuICAgICAgICB9KVxuICAgIH1cblxuICAgIC8qKlxuICAgICAqIEdldCB0ZXh0IHZlcnNpb24gb2YgZXJyb3JzIGluIG9uZSBsaW5lLlxuICAgICAqIEBwYXJhbSB7TGlzdEVycm9yc30gZXJyb3JzXG4gICAgICogQHJldHVybnMge3N0cmluZ31cbiAgICAgKi9cbiAgICBnZXRFcnJvcnNUZXh0KGVycm9ycykge1xuICAgICAgICBjb25zdCBhcnJFcnJvcnMgPSBlcnJvcnMgfHwgdGhpcy5hcnJFcnJvcnM7XG4gICAgICAgIGxldCBlcnJvclR4dCA9ICcnO1xuXG4gICAgICAgIGFyckVycm9ycy5mb3JFYWNoKChpdGVtKSA9PiB7XG4gICAgICAgICAgICBjb25zdCBuYW1lID0gaXRlbS5uYW1lWzBdLnRvVXBwZXJDYXNlKCkgKyBpdGVtLm5hbWUuc3Vic3RyKDEpO1xuXG4gICAgICAgICAgICBlcnJvclR4dCArPSBgJHtuYW1lfTogJHtpdGVtLmVycm9yfS4gYDtcbiAgICAgICAgfSk7XG5cbiAgICAgICAgcmV0dXJuIGVycm9yVHh0O1xuICAgIH1cblxuICAgIC8qKlxuICAgICAqIEdldCBsaXN0IG9mIGVycm9ycyB3aXRoIGZ1bGwgdGl0bGUgKGZyb20gY29udHJvbCB0aXRsZSBhdHRyaWJ1dGUpXG4gICAgICogQHBhcmFtIHtMaXN0RXJyb3JzfSBlcnJvcnMgLSBsaXN0IG9mIGVycm9yc1xuICAgICAqIEByZXR1cm5zIHtzdHJpbmd9XG4gICAgICovXG4gICAgZ2V0RXJyb3JzRnVsbChlcnJvcnMpIHtcbiAgICAgICAgY29uc3Qgc2VsZiA9IHRoaXM7XG4gICAgICAgIGNvbnN0IGFyckVycm9ycyA9IGVycm9ycyB8fCB0aGlzLmFyckVycm9ycztcbiAgICAgICAgbGV0IGVycm9yVHh0ID0gJyc7XG5cbiAgICAgICAgYXJyRXJyb3JzLmZvckVhY2goKGl0ZW0pID0+IHtcbiAgICAgICAgICAgIGNvbnN0ICRjb250cm9sID0gc2VsZi4kY29udHJvbHMuZmlsdGVyKGBbbmFtZT1cIiR7aXRlbS5uYW1lfVwiXWApLmZpcnN0KCk7XG4gICAgICAgICAgICBjb25zdCBuYW1lID0gJGNvbnRyb2wubGVuZ3RoPyAkY29udHJvbC5hdHRyKCd0aXRsZScpOiBpdGVtLm5hbWU7XG5cbiAgICAgICAgICAgIGVycm9yVHh0ICs9IGA8Yj4ke25hbWV9PC9iPjogJHtpdGVtLmVycm9yfS4gIDxicj48YnI+YDtcbiAgICAgICAgfSk7XG5cbiAgICAgICAgcmV0dXJuIGVycm9yVHh0O1xuICAgIH1cblxuICAgIGdldEZvcm1EYXRhKCl7XG4gICAgICAgIGxldCBhamF4RGF0YSA9IHt9O1xuXG4gICAgICAgIHRoaXMuJGNvbnRyb2xzLm1hcCgoaW5kZXgsIGVsKSA9PiB7XG4gICAgICAgICAgICBjb25zdCAkZWwgPSAkKGVsKTtcbiAgICAgICAgICAgIGNvbnN0IG5hbWUgPSAkZWwuYXR0cignbmFtZScpO1xuXG4gICAgICAgICAgICBpZiAoIW5hbWUpIHJldHVybjtcblxuICAgICAgICAgICAgaWYgKCRlbC5pcygnOmNoZWNrYm94Jykpe1xuICAgICAgICAgICAgICAgIGFqYXhEYXRhW25hbWVdID0gJGVsLnByb3AoJ2NoZWNrZWQnKVxuICAgICAgICAgICAgfSBlbHNlIHtcbiAgICAgICAgICAgICAgICBhamF4RGF0YVtuYW1lXSA9ICRlbC52YWwoKVxuICAgICAgICAgICAgfVxuICAgICAgICB9KTtcblxuICAgICAgICByZXR1cm4gYWpheERhdGE7XG4gICAgfVxuXG4gICAgLyoqXG4gICAgICogUmVtb3ZlIGFsbCBlcnJvcnNcbiAgICAgKi9cbiAgICByZW1vdmVFcnJvcnMoKSB7XG4gICAgICAgIHRoaXMuJGNvbnRyb2xzLmVhY2goKGluZGV4LCBlbCkgPT4ge1xuICAgICAgICAgICAgY29uc3QgJGVsID0gJChlbCk7XG4gICAgICAgICAgICB0aGlzLl9yZW1vdmVFcnJvcigkZWwpXG4gICAgICAgIH0pXG4gICAgfVxuXG4gICAgY2xlYXJGb3JtKCkge1xuICAgICAgICB0aGlzLiRjb250cm9scy5lYWNoKChpbmRleCwgZWwpID0+IHtcbiAgICAgICAgICAgIGNvbnN0ICRlbCA9ICQoZWwpO1xuICAgICAgICAgICAgaWYgKCEkZWwuYXR0cihcImRpc2FibGVkXCIpKSAgJGVsLnZhbCgnJyk7XG4gICAgICAgIH0pXG4gICAgfVxufVxuXG5cblxuLyoqIFdFQlBBQ0sgRk9PVEVSICoqXG4gKiogLi9mcm9udGVuZC9qcy9jb21tb24vX2Zvcm0taGVscGVyLmpzXG4gKiovIiwiXG5pbXBvcnQgQXN5bmNUYWIgZnJvbSAnLi8uLi9jb21tb24vX2FzeW5jLXRhYnMnO1xuaW1wb3J0IEZpbHRlckhpc3RvcnkgZnJvbSAnLi93aWRnZXRzL19maWx0ZXItaGlzdG9yeSc7XG5pbXBvcnQgQ3JlZGl0Rm9ybSBmcm9tICcuL3dpZGdldHMvX3NlbmQtY3JlZGl0cyc7XG5cbiQoZnVuY3Rpb24oKXtcbiAgICBBc3luY1RhYi5wbHVnaW4oJy5qcy1jcmVkaXRzLXRhYnMnKTtcblxuICAgQXBwLmV2ZW50c1xuICAgICAgICAuc3ViKCdobXQuYXN5bmN0YWIuc2hvd24nLCAoKT0+IHtcbiAgICAgICAgICAgIEZpbHRlckhpc3RvcnkucGx1Z2luKCcuanMtY3JlZGl0LWhpc3RvcnknKTtcbiAgICAgICAgICAgIENyZWRpdEZvcm0ucGx1Z2luKCcuanMtZm9ybS1jcmVkaXQnKTtcbiAgICAgICAgfSk7XG59KTtcblxuXG5cblxuXG5cblxuXG5cbi8qKiBXRUJQQUNLIEZPT1RFUiAqKlxuICoqIC4vZnJvbnRlbmQvanMvY3JlZGl0cy9jcmVkaXQtcGFnZS5qc1xuICoqLyIsIid1c2Ugc3RyaWN0JztcblxuZXhwb3J0IGRlZmF1bHQgY2xhc3MgV2lkZ2V0IHtcblxuICAgIGNvbnN0cnVjdG9yKHNlbGVjdG9yKSB7XG4gICAgICAgIHRoaXMuJHJvb3QgPSAkKHNlbGVjdG9yKTtcbiAgICAgICAgdGhpcy5sb2FkZWRUYWJzID0gW107XG4gICAgICAgIHRoaXMubG9jYWxzID0gdGhpcy5fZ2V0RG9tKCk7XG5cbiAgICAgICAgdGhpcy5fYXNzaWduRXZlbnRzKCk7XG5cbiAgICAgICAgbGV0ICRmaXJzdExpbmsgPSB0aGlzLmxvY2Fscy4kbGlua3MuZmlyc3QoKTtcbiAgICAgICAgdGhpcy5zaG93VGFiQnlMaW5rKCRmaXJzdExpbmspO1xuICAgIH1cblxuICAgIF9nZXREb20oKSB7XG4gICAgICAgIHJldHVybiB7XG4gICAgICAgICAgICAkbGlua3M6IHRoaXMuJHJvb3QuZmluZCgnW2RhdGEtdGFiLWxpbmtdJylcbiAgICAgICAgfVxuICAgIH1cblxuICAgIF9hc3NpZ25FdmVudHMoKSB7XG4gICAgICAgIHRoaXMuJHJvb3Qub24oJ2NsaWNrJywgJ1tkYXRhLXRhYi1saW5rXScsIHRoaXMuX29uQ2xpY2tMaW5rLmJpbmQodGhpcykpO1xuICAgICAgICBcbiAgICAgICAgQXBwLmV2ZW50cy5zdWIoJ2htdC5hc3luY3RhYnMucmVmcmVzaCcsIHRoaXMuX29uRXZlbnRSZWZyZXNoLmJpbmQodGhpcykpO1xuICAgIH1cblxuICAgIF9vbkNsaWNrTGluayhlKSB7XG4gICAgICAgIGUucHJldmVudERlZmF1bHQoKTtcbiAgICAgICAgbGV0ICRsaW5rID0gJChlLmN1cnJlbnRUYXJnZXQpO1xuXG4gICAgICAgIGlmICgkbGluay5oYXNDbGFzcygnc3RhdGVfYWN0aXZlJykpIHJldHVybjtcbiAgICAgICAgdGhpcy5zaG93VGFiQnlMaW5rKCRsaW5rKTtcbiAgICB9XG5cbiAgICBfb25FdmVudFJlZnJlc2goKXtcbiAgICAgICAgY29uc3QgJGN1cnJlbnRMaW5rID0gdGhpcy5sb2NhbHMuJGxpbmtzLmZpbHRlcignLnN0YXRlX2FjdGl2ZScpLmZpcnN0KCk7XG4gICAgICAgIGNvbnN0IGluZGV4Q3VycmVudFRhYiA9IHRoaXMubG9hZGVkVGFicy5pbmRleE9mKCRjdXJyZW50TGluay5hdHRyKCdocmVmJykpO1xuXG4gICAgICAgIHRoaXMubG9hZGVkVGFicy5zcGxpY2UoaW5kZXhDdXJyZW50VGFiLCAxKTtcbiAgICAgICAgdGhpcy5zaG93VGFiQnlMaW5rKCRjdXJyZW50TGluayk7XG4gICAgfVxuXG4gICAgLyoqXG4gICAgICogXG4gICAgICogQHBhcmFtIHtqUXVlcnl9ICRsaW5rIC0gY2xpY2tlZCBsaW5rXG4gICAgICogQHByaXZhdGVcbiAgICAgKi9cbiAgICBzaG93VGFiQnlMaW5rKCRsaW5rKXtcbiAgICAgICAgY29uc3QgdXJsID0gJGxpbmsuYXR0cignZGF0YS1ocmVmJyk7XG4gICAgICAgIGNvbnN0IHRhcmdldCA9ICRsaW5rLmF0dHIoJ2hyZWYnKTtcbiAgICAgICAgY29uc3Qgc2VsZiA9IHRoaXM7XG5cbiAgICAgICAgc2VsZi5fbG9hZENvbnRlbnQodXJsLCB0YXJnZXQpXG4gICAgICAgICAgICAuZG9uZSggICgpPT57XG4gICAgICAgICAgICAgICAgJGxpbmsuYWRkQ2xhc3MoJ3N0YXRlX2FjdGl2ZScpLnNpYmxpbmdzKCkucmVtb3ZlQ2xhc3MoJ3N0YXRlX2FjdGl2ZScpO1xuICAgICAgICAgICAgICAgICRsaW5rLnRhYignc2hvdycpO1xuXG4gICAgICAgICAgICAgICAgQXBwLmV2ZW50cy5wdWIoJ2htdC5hc3luY3RhYi5zaG93bicpO1xuICAgICAgICAgICAgfSlcbiAgICB9XG5cbiAgICAvKipcbiAgICAgKiAgTG9hZCBjb250ZW50IGFuZCBpbnNlcnQgaW50byB0YXJnZXQgZGl2XG4gICAgICogQHBhcmFtIHtTdHJpbmd9IHVybCAgICAgIC0gdXJsIG9mIGxvYWRlZCBjb250ZW50XG4gICAgICogQHBhcmFtIHtqUXVlcnl9IHRhcmdldCAgIC0gZGl2IHdoZXJlIHdlIHNob3VsZCBpbnNlcnQgY29udGVuXG4gICAgICovXG4gICAgX2xvYWRDb250ZW50KHVybCwgdGFyZ2V0KXtcbiAgICAgICAgY29uc3Qgc2VsZiA9IHRoaXM7XG4gICAgICAgIGNvbnN0IGlzU2hvdWxkTG9hZCA9ICgkLmluQXJyYXkodGFyZ2V0LCBzZWxmLmxvYWRlZFRhYnMpIDwgMCAmJiB1cmwpO1xuICAgICAgICBsZXQgZGVmZXIgPSAkLkRlZmVycmVkKCk7XG5cbiAgICAgICAgaWYgKGlzU2hvdWxkTG9hZCkge1xuICAgICAgICAgICAgJC5nZXQodXJsLCAoZGF0YSkgPT4ge1xuICAgICAgICAgICAgICAgIHNlbGYubG9hZGVkVGFicy5wdXNoKHRhcmdldCk7XG4gICAgICAgICAgICAgICAgJCh0YXJnZXQpLmh0bWwoZGF0YSk7XG5cbiAgICAgICAgICAgICAgICBkZWZlci5yZXNvbHZlKCk7XG4gICAgICAgICAgICB9KTtcbiAgICAgICAgfSBlbHNlIHtcbiAgICAgICAgICAgIGRlZmVyLnJlc29sdmUoKTtcbiAgICAgICAgfVxuICAgICAgICBcbiAgICAgICAgcmV0dXJuIGRlZmVyLnByb21pc2UoKTtcbiAgICB9ICAgIFxuXG4gICAgLy8gc3RhdGljXG4gICAgc3RhdGljIHBsdWdpbihzZWxlY3Rvcikge1xuICAgICAgICBjb25zdCAkZWxlbXMgPSAkKHNlbGVjdG9yKTtcbiAgICAgICAgaWYgKCEkZWxlbXMubGVuZ3RoKSByZXR1cm47XG5cbiAgICAgICAgcmV0dXJuICRlbGVtcy5lYWNoKGZ1bmN0aW9uIChpbmRleCwgZWwpIHtcbiAgICAgICAgICAgIGxldCAkZWxlbWVudCA9ICQoZWwpO1xuICAgICAgICAgICAgbGV0IGRhdGEgICAgID0gJGVsZW1lbnQuZGF0YSgnd2lkZ2V0Jyk7XG5cbiAgICAgICAgICAgIGlmICghZGF0YSkge1xuICAgICAgICAgICAgICAgIGRhdGEgPSBuZXcgV2lkZ2V0KGVsKTtcbiAgICAgICAgICAgICAgICAkZWxlbWVudC5kYXRhKCd3aWRnZXQnLCBkYXRhKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgfSlcbiAgICB9XG59XG5cblxuXG5cblxuLyoqIFdFQlBBQ0sgRk9PVEVSICoqXG4gKiogLi9mcm9udGVuZC9qcy9jb21tb24vX2FzeW5jLXRhYnMuanNcbiAqKi8iLCIndXNlIHN0cmljdCc7XG5cblxuZXhwb3J0IGRlZmF1bHQgY2xhc3MgV2lkZ2V0IHtcbiAgICAvKipcbiAgICAgKiBGaWx0ZXIgaGlzdG9yeVxuICAgICAqIEBwYXJhbSB7U3RyaW5nfSBzZWxlY3RvclxuICAgICAqL1xuICAgIGNvbnN0cnVjdG9yKHNlbGVjdG9yKSB7XG4gICAgICAgIHRoaXMuJHJvb3QgPSAkKHNlbGVjdG9yKTtcbiAgICAgICAgdGhpcy5sb2NhbHMgPSB0aGlzLl9nZXREb20oKTtcblxuICAgICAgICB0aGlzLl9hc3NpZ25FdmVudHMoKTtcbiAgICB9XG5cbiAgICBfZ2V0RG9tKCkge1xuICAgICAgICBjb25zdCAkcm9vdCA9IHRoaXMuJHJvb3Q7XG5cbiAgICAgICAgcmV0dXJuIHtcbiAgICAgICAgICAgICRsaXN0OiAkcm9vdC5maW5kKCdbZGF0YS1maWx0ZXItbGlzdF0nKSxcbiAgICAgICAgICAgICRpdGVtczogJHJvb3QuZmluZCgnW2RhdGEtZmlsdGVyLXRleHRdJyksXG4gICAgICAgIH07XG4gICAgfVxuXG4gICAgX2Fzc2lnbkV2ZW50cygpIHtcbiAgICAgICAgdGhpcy4kcm9vdC5vbignY2xpY2snLCAnW2RhdGEtZmlsdGVyLWxpbmtdJywgdGhpcy5fb25DbGlja0ZpbHRlci5iaW5kKHRoaXMpKTtcbiAgICAgICAgXG4gICAgICAgIEFwcC5ldmVudHMuc3ViKCdobXQuc2VuZENyZWRpdC5zdWNjZXNzJywgdGhpcy5fb25BZGROZXdJdGVtLmJpbmQodGhpcykpO1xuICAgIH1cblxuICAgIF9vbkNsaWNrRmlsdGVyKGUpIHtcbiAgICAgICAgZS5wcmV2ZW50RGVmYXVsdCgpO1xuICAgICAgICBjb25zdCAkbGluayA9ICQoZS5jdXJyZW50VGFyZ2V0KTtcblxuICAgICAgICBpZiAoJGxpbmsuaGFzQ2xhc3MoJ3N0YXRlX3NlbGVjdGVkJykpIHJldHVybjsgICAgICAgIFxuICAgICAgICB0aGlzLmZpbHRlckJ5TGluaygkbGluayk7XG4gICAgfVxuXG4gICAgZmlsdGVyQnlMaW5rKCRsaW5rKXtcbiAgICAgICAgY29uc3QgZmlsdGVyVGV4dCA9ICRsaW5rLmRhdGEoJ2ZpbHRlci1saW5rJyk7XG4gICAgICAgIFxuICAgICAgICB0aGlzLnNldEFjdGl2ZUxpbmsoJGxpbmspO1xuICAgICAgICB0aGlzLmZpbHRlckxpc3QoZmlsdGVyVGV4dCk7XG4gICAgfVxuXG4gICAgX29uQWRkTmV3SXRlbShkYXRhKXtcbiAgICAgICAgXG4gICAgfVxuXG4gICAgLyoqXG4gICAgICogRmlsdGVyIGxpc3QgdGhyb3VnaCB0ZXh0XG4gICAgICogQHBhcmFtIHtTdHJpbmd9IGZpbHRlclRleHRcbiAgICAgKi9cbiAgICBmaWx0ZXJMaXN0KGZpbHRlclRleHQpIHtcbiAgICAgICAgY29uc3QgJGl0ZW1zID0gdGhpcy5sb2NhbHMuJGl0ZW1zO1xuXG4gICAgICAgIGlmIChmaWx0ZXJUZXh0ID09ICdhbGwnKSB7XG4gICAgICAgICAgICAkaXRlbXMucmVtb3ZlQ2xhc3MoJ3N0YXRlX2hpZGRlbicpO1xuICAgICAgICAgICAgcmV0dXJuO1xuICAgICAgICB9XG5cbiAgICAgICAgJGl0ZW1zLmVhY2goKGluZGV4LCBlbCkgPT4ge1xuICAgICAgICAgICAgY29uc3QgJGVsID0gJChlbCk7XG4gICAgICAgICAgICBjb25zdCBpc0hpZGRlbiA9ICRlbC5kYXRhKCdmaWx0ZXItdGV4dCcpLmluZGV4T2YoZmlsdGVyVGV4dCkgPT09IC0xO1xuXG4gICAgICAgICAgICAkZWwudG9nZ2xlQ2xhc3MoJ3N0YXRlX2hpZGRlbicsIGlzSGlkZGVuKTtcbiAgICAgICAgfSk7XG4gICAgfTtcblxuICAgIC8qKlxuICAgICAqIFNldCBsaW5rIHRvIGFjdGl2ZSBhbmQgZGVhY3RpdmF0ZSBvdGhlclxuICAgICAqIEBwYXJhbSB7alF1ZXJ5fSAkZWxcbiAgICAgKi9cbiAgICBzZXRBY3RpdmVMaW5rKCRlbCkge1xuICAgICAgICBpZiAoJGVsLmhhc0NsYXNzKCdzdGF0ZV9zZWxlY3RlZCcpKSByZXR1cm47XG4gICAgICAgIFxuICAgICAgICAkZWwuYWRkQ2xhc3MoJ3N0YXRlX3NlbGVjdGVkJylcbiAgICAgICAgICAgIC5zaWJsaW5ncygpLnJlbW92ZUNsYXNzKCdzdGF0ZV9zZWxlY3RlZCcpO1xuICAgIH07XG4gICAgXG4gICAgLy8gc3RhdGljXG4gICAgc3RhdGljIHBsdWdpbihzZWxlY3Rvcikge1xuICAgICAgICBjb25zdCAkZWxlbXMgPSAkKHNlbGVjdG9yKTtcbiAgICAgICAgaWYgKCEkZWxlbXMubGVuZ3RoKSByZXR1cm47XG5cbiAgICAgICAgcmV0dXJuICRlbGVtcy5lYWNoKGZ1bmN0aW9uIChpbmRleCwgZWwpIHtcbiAgICAgICAgICAgIGxldCAkZWxlbWVudCA9ICQoZWwpO1xuICAgICAgICAgICAgbGV0IGRhdGEgICAgID0gJGVsZW1lbnQuZGF0YSgnd2lkZ2V0Jyk7XG5cbiAgICAgICAgICAgIGlmICghZGF0YSkge1xuICAgICAgICAgICAgICAgIGRhdGEgPSBuZXcgV2lkZ2V0KGVsKTtcbiAgICAgICAgICAgICAgICAkZWxlbWVudC5kYXRhKCd3aWRnZXQnLCBkYXRhKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgfSlcbiAgICB9XG59XG5cblxuXG5cblxuLyoqIFdFQlBBQ0sgRk9PVEVSICoqXG4gKiogLi9mcm9udGVuZC9qcy9jcmVkaXRzL3dpZGdldHMvX2ZpbHRlci1oaXN0b3J5LmpzXG4gKiovIiwiJ3VzZSBzdHJpY3QnO1xuXG5pbXBvcnQgRm9ybUhlbHBlciBmcm9tIFwiLi8uLi8uLi9jb21tb24vX2Zvcm0taGVscGVyXCI7XG5cblxuLyoqXG4gKiBTdWdnZXN0aW9uIGl0ZW1cbiAqIEB0eXBlZGVmIHtPYmplY3R9IFN1Z2dlc3Rpb25cbiAqIEBwcm9wZXJ0eSB7U3RyaW5nfSB2YWx1ZSAtIG5hbWUgb2YgcGVyc2lvblxuICogQHByb3BlcnR5IHtOdW1iZXJ9IGRhdGEuaWQgLSBpZCBvZiBwZXJzb25cbiAqIEBwcm9wZXJ0eSB7U3RyaW5nfSBkYXRhLmltZyAtIHVybCBvZiBpbWFnZVxuICovXG5cbi8qKlxuICogRm9ybSBmb3Igc2VuZGluZyBjcmVkaXRcbiAqL1xuZXhwb3J0IGRlZmF1bHQgY2xhc3MgV2lkZ2V0e1xuXG4gICAgY29uc3RydWN0b3Ioc2VsZWN0b3IpIHtcbiAgICAgICAgdGhpcy4kcm9vdCA9ICQoc2VsZWN0b3IpO1xuICAgICAgICB0aGlzLmxvY2FscyA9IHRoaXMuX2dldERvbSgpO1xuICAgICAgICB0aGlzLnZhbGlkYXRpb24gPSBuZXcgRm9ybUhlbHBlcih0aGlzLiRyb290LmZpbmQoJy5iLWNyZWRpdHNfX2lucHV0JykpO1xuXG4gICAgICAgIGlmICghQm9vbGVhbigkLmZuLmF1dG9jb21wbGV0ZSkpe1xuICAgICAgICAgICAgY29uc29sZS5sb2coJ2pRdWVyeSBhdXRvY29tcGxldGUgcGx1Z2luIGlzIG5vdCBpbmNsdWRlIGludG8gcGFnZScpO1xuICAgICAgICAgICAgcmV0dXJuO1xuICAgICAgICB9XG4gICAgICAgIHRoaXMuX2luaXRBdXRvQ29tcGxldGUoKTtcbiAgICAgICAgdGhpcy5fYXNzaWduRXZlbnRzKCk7XG4gICAgfVxuXG4gICAgX2dldERvbSgpIHtcbiAgICAgICAgY29uc3QgJHJvb3QgPSB0aGlzLiRyb290O1xuXG4gICAgICAgIHJldHVybiB7XG4gICAgICAgICAgICAkY291bnQ6ICRyb290LmZpbmQoJ1tkYXRhLWNyZWRpY3QtY291bnRdJyksXG4gICAgICAgICAgICAkdmFsdWU6ICRyb290LmZpbmQoJ1tkYXRhLWNyZWRpdC12YWx1ZV0nKSxcbiAgICAgICAgICAgICR0bzogJHJvb3QuZmluZCgnW2RhdGEtY3JlZGl0LXRvXScpLFxuICAgICAgICAgICAgJHRvRGF0YTogJHJvb3QuZmluZCgnW2RhdGEtY3JlZGl0LXRvLWRhdGFdJyksXG4gICAgICAgICAgICAkbWVzc2FnZTogJHJvb3QuZmluZCgnW2RhdGEtY3JlZGl0LW1lc3NhZ2VdJyksXG4gICAgICAgICAgICAkZXJyb3I6ICRyb290LmZpbmQoJ1tkYXRhLWNyZWRpdC1lcnJvcl0nKVxuICAgICAgICB9O1xuICAgIH1cblxuICAgIF9pbml0QXV0b0NvbXBsZXRlKCkge1xuICAgICAgICBjb25zdCBzZWxmID0gdGhpcztcbiAgICAgICAgY29uc3QgbG9jYWxzID0gdGhpcy5sb2NhbHM7XG4gICAgICAgIGNvbnN0IHVybCA9IGpzUm91dGVzLmNvbnRyb2xsZXJzLmNtLkZhY2lsaXRhdG9ycy5zZWFyY2godGhpcy4kcm9vdC5hdHRyKCdkYXRhLWJyYW5kLWlkJykpLnVybDtcblxuICAgICAgICBsb2NhbHMuJHRvLmF1dG9jb21wbGV0ZSh7XG4gICAgICAgICAgICBzZXJ2aWNlVXJsOiB1cmwsXG4gICAgICAgICAgICBwYXJhbU5hbWU6ICdxdWVyeScsXG4gICAgICAgICAgICBtaW5DaGFyczogMyxcbiAgICAgICAgICAgIHByZXNlcnZlSW5wdXQ6IHRydWUsICAgICAgICAgICAgXG4gICAgICAgICAgICBvblNlbGVjdDogZnVuY3Rpb24gKHN1Z2dlc3Rpb24pIHtcbiAgICAgICAgICAgICAgICBsb2NhbHMuJHRvLnZhbChzdWdnZXN0aW9uLm5hbWUpO1xuICAgICAgICAgICAgICAgIGxvY2Fscy4kdG9EYXRhLnZhbChzdWdnZXN0aW9uLmRhdGEpO1xuICAgICAgICAgICAgICAgIHJldHVybiB0cnVlO1xuICAgICAgICAgICAgfSxcbiAgICAgICAgICAgIGZvcm1hdFJlc3VsdDogZnVuY3Rpb24gKHN1Z2dlc3Rpb24sIGN1cnJlbnRWYWx1ZSkge1xuICAgICAgICAgICAgICAgIHJldHVybiBzdWdnZXN0aW9uLnZhbHVlO1xuICAgICAgICAgICAgfSxcbiAgICAgICAgICAgIHRyYW5zZm9ybVJlc3VsdDogZnVuY3Rpb24ocmVzcG9uc2UpIHtcbiAgICAgICAgICAgICAgICBjb25zdCBzdWdnZXN0aW9ucyA9ICQucGFyc2VKU09OKHJlc3BvbnNlKS5zdWdnZXN0aW9ucztcblxuICAgICAgICAgICAgICAgIHJldHVybiB7XG4gICAgICAgICAgICAgICAgICAgIHN1Z2dlc3Rpb25zOiBzdWdnZXN0aW9ucy5tYXAoZnVuY3Rpb24oaXRlbSl7XG4gICAgICAgICAgICAgICAgICAgICAgICBsZXQgdGVtcGxhdGUgPSBzZWxmLl9nZXRTdWdnZXN0VGVtcGxhdGUoaXRlbSk7XG5cbiAgICAgICAgICAgICAgICAgICAgICAgIHJldHVybiB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgdmFsdWU6IHRlbXBsYXRlLFxuICAgICAgICAgICAgICAgICAgICAgICAgICAgIGRhdGE6IGl0ZW0uZGF0YS5pZCxcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBuYW1lOiBpdGVtLnZhbHVlXG4gICAgICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgIH0pXG4gICAgICAgICAgICAgICAgfTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgfSk7XG4gICAgfVxuXG4gICAgLyoqXG4gICAgICogUmVuZGVyIHRlbXBsYXRlIGZvciBzdWdnZXN0aW9uXG4gICAgICogQHBhcmFtIHtTdWdnZXN0aW9ufSBkYXRhIC0gc3VnZ2VzdGlvbiBvYmplY3RcbiAgICAgKiBAcHJpdmF0ZVxuICAgICAqL1xuICAgICBfZ2V0U3VnZ2VzdFRlbXBsYXRlKGl0ZW0pe1xuICAgICAgICByZXR1cm4gYDxkaXYgY2xhc3M9XCJiLXN1Z2dlc3RfX2ltZ1wiIHN0eWxlPVwiYmFja2dyb3VuZC1pbWFnZTogdXJsKCR7aXRlbS5kYXRhLmltZ30pXCI+PC9kaXY+PGRpdiBjbGFzcz1cImItc3VnZ2VzdF9fbmFtZVwiPiR7aXRlbS52YWx1ZX08L2Rpdj5gXG4gICAgfVxuXG4gICAgX2Fzc2lnbkV2ZW50cygpIHtcbiAgICAgICAgdGhpcy4kcm9vdFxuICAgICAgICAgICAgLm9uKCdpbnB1dCcsICdpbnB1dCcsIChlKSA9PiB0aGlzLmxvY2Fscy4kZXJyb3IudGV4dCgnJykpXG4gICAgICAgICAgICAub24oJ3N1Ym1pdCcsIHRoaXMuX29uU3VibWl0Rm9ybS5iaW5kKHRoaXMpKTtcbiAgICB9XG5cbiAgICBfb25TdWJtaXRGb3JtKGUpIHtcbiAgICAgICAgZS5wcmV2ZW50RGVmYXVsdCgpO1xuICAgICAgICBjb25zdCBzZWxmID0gdGhpcztcblxuICAgICAgICBpZiAoIXNlbGYuX2lzRm9ybVZhbGlkKCkpIHJldHVybiBmYWxzZTtcblxuICAgICAgICBzZWxmLl9zZW5kUmVxdWVzdCgpXG4gICAgICAgICAgICAuZG9uZSgoKSA9PiB7XG4gICAgICAgICAgICAgICAgc2VsZi52YWxpZGF0aW9uLmNsZWFyRm9ybSgpO1xuXG4gICAgICAgICAgICAgICAgc3VjY2VzcyhcIllvdSBoYXZlIHNlbnQgY3JlZGl0cyBzdWNjZXNzZnVsbHkhXCIsIDQ1MDApO1xuICAgICAgICAgICAgICAgIHNlbGYuX3NldE5ld1ZhbHVlcygpO1xuICAgICAgICAgICAgfSlcbiAgICAgICAgICAgIC5mYWlsKChyZXNwb25zZSkgPT4ge1xuICAgICAgICAgICAgICAgIGNvbnN0IGRhdGEgPSAkLnBhcnNlSlNPTihyZXNwb25zZS5yZXNwb25zZVRleHQpLmRhdGE7XG4gICAgICAgICAgICAgICAgY29uc3QgZXJyb3JUZXh0ID0gc2VsZi52YWxpZGF0aW9uLmdldEVycm9yc1RleHQoZGF0YS5lcnJvcnMpO1xuXG4gICAgICAgICAgICAgICAgaWYgKCFkYXRhLmVycm9ycykgcmV0dXJuO1xuXG4gICAgICAgICAgICAgICAgc2VsZi5sb2NhbHMuJGVycm9yLnRleHQoZXJyb3JUZXh0KTtcbiAgICAgICAgICAgICAgICBzZWxmLnZhbGlkYXRpb24uc2V0RXJyb3JzKGRhdGEuZXJyb3JzKTtcbiAgICAgICAgICAgIH0pXG4gICAgfVxuXG4gICAgX2lzRm9ybVZhbGlkKCkge1xuICAgICAgICBjb25zdCBsb2NhbHMgPSB0aGlzLmxvY2FscztcbiAgICAgICAgY29uc3QgY3JlZGl0c0xlZnQgPSBOdW1iZXIobG9jYWxzLiRjb3VudC50ZXh0KCkpO1xuICAgICAgICBjb25zdCBpc0Vub3VnaENyZWRpdHMgPSBOdW1iZXIobG9jYWxzLiR2YWx1ZS52YWwoKSkgPD0gY3JlZGl0c0xlZnQ7XG4gICAgICAgIGxldCB2YWxpZCA9IHRydWU7XG4gICAgICAgIGxldCBlcnJvclRleHQgPSAnJztcblxuICAgICAgICBpZiAoIXRoaXMudmFsaWRhdGlvbi5pc1ZhbGlkSW5wdXRzKCkpIHtcbiAgICAgICAgICAgIHZhbGlkID0gZmFsc2U7XG4gICAgICAgICAgICBlcnJvclRleHQgKz0gdGhpcy52YWxpZGF0aW9uLmdldEVycm9yc1RleHQoKTtcbiAgICAgICAgfVxuXG4gICAgICAgIGlmIChjcmVkaXRzTGVmdCA9PSAwKSB7XG4gICAgICAgICAgICB2YWxpZCA9IGZhbHNlO1xuICAgICAgICAgICAgZXJyb3JUZXh0ICs9ICdZb3UgaGF2ZSBubyBtb3JlIGNyZWRpdHMgdG8gc2hhcmUuICc7XG4gICAgICAgIH0gZWxzZSBpZiAoIWlzRW5vdWdoQ3JlZGl0cykge1xuICAgICAgICAgICAgdmFsaWQgPSBmYWxzZTtcbiAgICAgICAgICAgIGVycm9yVGV4dCArPSAnWW91IGNhbm5vdCBnaXZlIG1vcmUgdGhhbiAnICsgbG9jYWxzLiRjb3VudC50ZXh0KCkgKyAnIGNyZWRpdHMuICc7XG4gICAgICAgIH1cblxuICAgICAgICBpZiAoIXZhbGlkKSB7XG4gICAgICAgICAgICBsb2NhbHMuJGVycm9yLnRleHQoZXJyb3JUZXh0KTtcbiAgICAgICAgfVxuXG4gICAgICAgIHJldHVybiB2YWxpZDtcbiAgICB9XG5cbiAgICBfc2VuZFJlcXVlc3QoKSB7XG4gICAgICAgIHJldHVybiAkLnBvc3QodGhpcy4kcm9vdC5hdHRyKCdhY3Rpb24nKSxcbiAgICAgICAgICAgIHtcbiAgICAgICAgICAgICAgICBhbW91bnQ6IHRoaXMubG9jYWxzLiR2YWx1ZS52YWwoKSxcbiAgICAgICAgICAgICAgICB0bzogdGhpcy5sb2NhbHMuJHRvRGF0YS52YWwoKSxcbiAgICAgICAgICAgICAgICByZWFzb246IHRoaXMubG9jYWxzLiRtZXNzYWdlLnZhbCgpXG4gICAgICAgICAgICB9XG4gICAgICAgICk7XG4gICAgfVxuXG4gICAgX3NldE5ld1ZhbHVlcygpIHtcbiAgICAgICAgQXBwLmV2ZW50cy5wdWIoJ2htdC5hc3luY3RhYnMucmVmcmVzaCcpO1xuICAgIH1cblxuICAgIC8vIHN0YXRpY1xuICAgIHN0YXRpYyBwbHVnaW4oc2VsZWN0b3IpIHtcbiAgICAgICAgY29uc3QgJGVsZW1zID0gJChzZWxlY3Rvcik7XG4gICAgICAgIGlmICghJGVsZW1zLmxlbmd0aCkgcmV0dXJuO1xuXG4gICAgICAgIHJldHVybiAkZWxlbXMuZWFjaChmdW5jdGlvbiAoaW5kZXgsIGVsKSB7XG4gICAgICAgICAgICBsZXQgJGVsZW1lbnQgPSAkKGVsKTtcbiAgICAgICAgICAgIGxldCBkYXRhICAgICA9ICRlbGVtZW50LmRhdGEoJ3dpZGdldCcpO1xuXG4gICAgICAgICAgICBpZiAoIWRhdGEpIHtcbiAgICAgICAgICAgICAgICBkYXRhID0gbmV3IFdpZGdldChlbCk7XG4gICAgICAgICAgICAgICAgJGVsZW1lbnQuZGF0YSgnd2lkZ2V0JywgZGF0YSk7XG4gICAgICAgICAgICB9XG4gICAgICAgIH0pXG4gICAgfVxufVxuXG5cblxuXG4vKiogV0VCUEFDSyBGT09URVIgKipcbiAqKiAuL2Zyb250ZW5kL2pzL2NyZWRpdHMvd2lkZ2V0cy9fc2VuZC1jcmVkaXRzLmpzXG4gKiovIl0sInNvdXJjZVJvb3QiOiIifQ==