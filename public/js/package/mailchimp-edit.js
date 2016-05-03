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

	module.exports = __webpack_require__(51);


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
/* 51 */
/***/ function(module, exports, __webpack_require__) {

	
	'use strict';
	
	var _mailchimpForm = __webpack_require__(52);
	
	var _mailchimpForm2 = _interopRequireDefault(_mailchimpForm);
	
	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }
	
	$(function () {
	    _mailchimpForm2.default.plugin('.js-edit-mailchimp');
	});

/***/ },
/* 52 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';
	
	Object.defineProperty(exports, "__esModule", {
	    value: true
	});
	
	var _classCallCheck2 = __webpack_require__(3);
	
	var _classCallCheck3 = _interopRequireDefault(_classCallCheck2);
	
	var _createClass2 = __webpack_require__(4);
	
	var _createClass3 = _interopRequireDefault(_createClass2);
	
	var _formHelpers = __webpack_require__(53);
	
	var _formHelpers2 = _interopRequireDefault(_formHelpers);
	
	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }
	
	var Widget = function () {
	    function Widget(selector) {
	        (0, _classCallCheck3.default)(this, Widget);
	
	        this.$root = $(selector);
	        this.locals = this._getDom();
	
	        this.editDlgHelper = new _formHelpers2.default({
	            $controls: this.locals.$controls,
	            rules: this._getRules()
	        });
	        this._assignEvents();
	    }
	
	    (0, _createClass3.default)(Widget, [{
	        key: '_getDom',
	        value: function _getDom() {
	            var $root = this.$root;
	
	            return {
	                $form: $root.find('form'),
	                $controls: $root.find('input, select, textarea')
	            };
	        }
	    }, {
	        key: '_getRules',
	        value: function _getRules() {
	            return {
	                name: { required: true },
	                "defaults.fromEmail": { required: true },
	                "defaults.fromName": { required: true },
	                "defaults.subject": { required: true },
	                "defaults.language": { required: true },
	                reminder: { required: true },
	                "company.name": { required: true },
	                "company.address1": { required: true },
	                "company.zip": { required: true },
	                "company.city": { required: true },
	                "company.state": { required: true },
	                "company.countryCode": { required: true },
	                "allAttendees": { required: true }
	            };
	        }
	    }, {
	        key: '_assignEvents',
	        value: function _assignEvents() {
	            this.locals.$form.on('submit', this._onSubmitForm.bind(this));
	        }
	    }, {
	        key: '_onSubmitForm',
	        value: function _onSubmitForm(e) {}
	
	        // static
	
	    }], [{
	        key: 'plugin',
	        value: function plugin(selector, options) {
	            var $elems = $(selector);
	            if (!$elems.length) return;
	
	            return $elems.each(function (index, el) {
	                var $element = $(el);
	                var data = $element.data('widget.integration.mailchimp');
	
	                if (!data) {
	                    data = new Widget(el, options);
	                    $element.data('widget.integration.mailchimp', data);
	                }
	            });
	        }
	    }]);
	    return Widget;
	}();

	exports.default = Widget;

/***/ },
/* 53 */
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
	     * Validate given controls
	     * @param {Object} options
	     * @param {jQuery} options.$controls       - optional list of validating controls
	     * @param {Object} options.rules           - list of rule 
	     * @param {Object} [options.restriction]   - list of restriction
	     * @param {Object} messages
	     */
	
	    function FormHelper(options) {
	        var messages = arguments.length <= 1 || arguments[1] === undefined ? null : arguments[1];
	        (0, _classCallCheck3.default)(this, FormHelper);
	
	        this.$controls = options.$controls;
	
	        this.messages = messages || this._getDefaultMessages();
	        this.rules = $.extend({}, options.rules, this._getRulesFromHtml(this.$controls));
	        this.restriction = $.extend({}, options.restriction, this._getRestrictionFromHtml(this.$controls));
	        this.errors = [];
	
	        this._assignEvents();
	    }
	
	    (0, _createClass3.default)(FormHelper, [{
	        key: "_getDefaultMessages",
	        value: function _getDefaultMessages() {
	            return {
	                required: "This field is required.",
	                email: "Please enter a valid email address.",
	                url: "Please enter a valid URL.",
	                date: "Please enter a valid date.",
	                dateiso: "Please enter a valid date (ISO).",
	                nospace: "Please enter a valid number.",
	                digits: "Please enter only digits."
	            };
	        }
	
	        /**
	         * @param $controls
	         * @returns {Object} - list of rules
	         * @private
	         */
	
	    }, {
	        key: "_getRulesFromHtml",
	        value: function _getRulesFromHtml($controls) {
	            var self = this;
	            var rules = {};
	
	            $controls.each(function (index, item) {
	                var $item = $(item);
	                var nameField = $item.attr('name');
	                var possibleRules = self.messages;
	
	                if (!$item.attr('class').match(/_validate-/i)) return;
	                if (!rules[nameField]) rules[nameField] = {};
	
	                for (var rule in possibleRules) {
	                    var ruleClass = "_validate-" + rule;
	
	                    if ($item.hasClass(ruleClass)) {
	                        rules[nameField][rule] = true;
	                    }
	                }
	            });
	            return rules;
	        }
	
	        /**
	         * @param $controls
	         * @returns {Object} - list of rules
	         * @private
	         */
	
	    }, {
	        key: "_getRestrictionFromHtml",
	        value: function _getRestrictionFromHtml($controls) {
	            var self = this;
	            var restriction = {};
	
	            $controls.each(function (index, item) {
	                var $item = $(item);
	                var nameField = $item.attr('name');
	                var possibleRestrict = self.messages;
	
	                if (!$item.attr('class').match(/_restrict-/i)) return;
	                if (!restriction[nameField]) restriction[nameField] = {};
	
	                for (var restrict in possibleRestrict) {
	                    var restrictClass = "_validate-" + restrict;
	
	                    if ($item.hasClass(restrictClass)) {
	                        restriction[nameField][restrict] = true;
	                    }
	                }
	            });
	            return restriction;
	        }
	    }, {
	        key: "_assignEvents",
	        value: function _assignEvents() {
	            this.$controls.on('focus', this._onFocusControl.bind(this)).on('blur', this._onBlurControl.bind(this)).on('input', this._onInputControl.bind(this));
	        }
	    }, {
	        key: "_onFocusControl",
	        value: function _onFocusControl(e) {
	            var $el = $(e.currentTarget);
	        }
	    }, {
	        key: "_onBlurControl",
	        value: function _onBlurControl(e) {
	            var $el = $(e.currentTarget);
	            this._isValidControl($el);
	        }
	    }, {
	        key: "_onInputControl",
	        value: function _onInputControl(e) {
	            var $control = $(e.currentTarget);
	            this._removeError($control);
	            this._restrictInput($control);
	        }
	    }, {
	        key: "_isValidControl",
	        value: function _isValidControl($control) {
	            var validation = this._validateControl($control);
	
	            if (validation.isValid) {
	                this._removeError($control);
	                return true;
	            }
	
	            this._setError($control, validation.message);
	            return false;
	        }
	
	        /**
	         * Validate given control
	         * @param {jQuery} $control - element
	         * @returns {Object} = isValid(Boolean), message(String)
	         * @private
	         */
	
	    }, {
	        key: "_validateControl",
	        value: function _validateControl($control) {
	            var name = $control.attr('name');
	            var rules = this.rules[name];
	            var valueControl = this.getControlValue($control);
	            var valid = undefined;
	
	            for (var rule in rules) {
	                valid = this[rule + "Validator"](valueControl, $control);
	
	                if (!valid) return {
	                    isValid: false,
	                    message: this.messages[rule]
	                };
	            }
	
	            return {
	                isValid: true
	            };
	        }
	    }, {
	        key: "isValidFormData",
	        value: function isValidFormData() {
	            var self = this;
	            var valid = true;
	
	            this.removeErrors();
	            this.$controls.each(function (index, control) {
	                var isValidControl = self._isValidControl($(control));
	                valid = valid && isValidControl;
	            });
	
	            return valid;
	        }
	    }, {
	        key: "_restrictInput",
	        value: function _restrictInput($control) {
	            var name = $control.attr('name');
	            var restriction = this.restriction[name];
	            var value = this.getControlValue($control);
	
	            if (!restriction) return;
	
	            for (var restict in restriction) {
	                value = this[restict + "Restrict"](value);
	            }
	            this.setControlValue($control, value);
	        }
	
	        /**
	         * Show or hide last error
	         * @param {Boolean} condition
	         * @param {jQuery} $control
	         * @private
	         */
	
	    }, {
	        key: "_showPreviousError",
	        value: function _showPreviousError(condition) {
	            var $control = arguments.length <= 1 || arguments[1] === undefined ? null : arguments[1];
	
	            if (this.$inputWithError) {
	                this.$inputWithError.parent().toggleClass('b-error_state_high', !condition).toggleClass('b-error_state_error', condition);
	            }
	            this.$inputWithError = $control;
	        }
	
	        /**
	         * Set error for control
	         * @param {jQuery} $control
	         * @param {String} errorText
	         * @param {Boolean} showBubble
	         */
	
	    }, {
	        key: "_setError",
	        value: function _setError($control, errorText) {
	            var showBubble = arguments.length <= 2 || arguments[2] === undefined ? true : arguments[2];
	
	            var $parent = $control.parent();
	            var $error = $parent.find('.b-error');
	
	            if ($error.length) {
	                $error.text(errorText);
	            } else {
	                $('<div class="b-error" />').text(errorText).appendTo($parent);
	            }
	
	            $parent.addClass(showBubble ? 'b-error_state_error' : 'b-error_state_high');
	
	            this.errors.push({
	                name: $control.attr('name'),
	                error: errorText
	            });
	        }
	    }, {
	        key: "_removeError",
	        value: function _removeError($control) {
	            var $parent = $control.parent();
	
	            $parent.removeClass('b-error_state_error b-error_state_high');
	
	            this.errors = this.errors.filter(function (item) {
	                return item.name !== $control.attr('name');
	            });
	        }
	
	        /**
	         * Set errors
	         * @param {Array} errors - [{name: "email", error: "empty"}, {name: "password", error: "empty"}]
	         */
	
	    }, {
	        key: "setErrors",
	        value: function setErrors(errors) {
	            var _this = this;
	
	            this.$inputWithError = null;
	            var index = 0;
	
	            errors.forEach(function (item) {
	                var $currentControl = _this.$controls.filter('[name="' + item.name + '"]').first();
	
	                if (!$currentControl.length) return;
	
	                if (index == 0) {
	                    _this._setError($currentControl, item.error);
	                    $('html, body').animate({
	                        scrollTop: $currentControl.offset().top - 50
	                    }, 400);
	                } else {
	                    _this._setError($currentControl, item.error, false);
	                }
	            });
	        }
	    }, {
	        key: "removeErrors",
	        value: function removeErrors() {
	            var _this2 = this;
	
	            this.$controls.each(function (index, el) {
	                var $el = $(el);
	                _this2._removeError($el);
	            });
	        }
	
	        // validators
	
	    }, {
	        key: "requiredValidator",
	        value: function requiredValidator(value, $el) {
	            if ($el.is('select')) {
	                var val = $el.val();
	                return val && val.length > 0;
	            }
	            return value.length > 0;
	        }
	    }, {
	        key: "emailValidator",
	        value: function emailValidator(value, $el) {
	            return (/^[a-zA-Z0-9.!#$%&'*+\/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$/.test(value)
	            );
	        }
	    }, {
	        key: "urlValidator",
	        value: function urlValidator(value, $el) {
	            return (/^(?:(?:(?:https?|ftp):)?\/\/)(?:\S+(?::\S*)?@)?(?:(?!(?:10|127)(?:\.\d{1,3}){3})(?!(?:169\.254|192\.168)(?:\.\d{1,3}){2})(?!172\.(?:1[6-9]|2\d|3[0-1])(?:\.\d{1,3}){2})(?:[1-9]\d?|1\d\d|2[01]\d|22[0-3])(?:\.(?:1?\d{1,2}|2[0-4]\d|25[0-5])){2}(?:\.(?:[1-9]\d?|1\d\d|2[0-4]\d|25[0-4]))|(?:(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)(?:\.(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)*(?:\.(?:[a-z\u00a1-\uffff]{2,})).?)(?::\d{2,5})?(?:[/?#]\S*)?$/i.test(value)
	            );
	        }
	    }, {
	        key: "dateValidator",
	        value: function dateValidator(value, $el) {
	            return !/Invalid|NaN/.test(new Date(value).toString());
	        }
	    }, {
	        key: "dateisoValidator",
	        value: function dateisoValidator(value, $el) {
	            return (/^\d{4}[\/\-](0?[1-9]|1[012])[\/\-](0?[1-9]|[12][0-9]|3[01])$/.test(value)
	            );
	        }
	
	        // restriction
	
	    }, {
	        key: "digistsRestrict",
	        value: function digistsRestrict(value) {
	            return value.replace(/[^\d]+/g, '');
	        }
	    }, {
	        key: "nospaceRestrict",
	        value: function nospaceRestrict(value) {
	            return value.replace(/\s/g, '');
	        }
	
	        // Helper for form
	
	    }, {
	        key: "getFormData",
	        value: function getFormData() {
	            var _this3 = this;
	
	            var formData = {};
	
	            this.$controls.each(function (index, el) {
	                var $el = $(el);
	                var name = $el.attr('name');
	
	                if (name) {
	                    formData[name] = _this3.getControlValue($el);
	                }
	            });
	
	            return formData;
	        }
	    }, {
	        key: "setFormData",
	        value: function setFormData(formData) {
	            var $controls = this.$controls;
	
	            for (var field in formData) {
	                if (formData.hasOwnProperty(field)) {
	                    var $control = $controls.filter("[name=\"" + field + "\"]").first();
	
	                    if (!$control.length) return;
	
	                    this.setControlValue($control, data[field]);
	                }
	            }
	        }
	    }, {
	        key: "clearForm",
	        value: function clearForm() {
	            this.$controls.each(function (index, el) {
	                var $el = $(el);
	                if (!$el.attr("disabled")) $el.val('');
	            });
	        }
	
	        /**
	         * Universal assign value
	         * @param {jQuery} $control
	         * @param {String|Number|Boolean} value
	         */
	
	    }, {
	        key: "setControlValue",
	        value: function setControlValue($control, value) {
	            if ($control.is(':checkbox')) {
	                $control.prop('checked', value);
	            } else {
	                $control.val(value);
	            }
	        }
	
	        /**
	         * Universal get value helper
	         * @param {jQuery} $control
	         * @returns {String|Boolean}
	         */
	
	    }, {
	        key: "getControlValue",
	        value: function getControlValue($control) {
	            var value = null;
	
	            if ($control.is(':checkbox')) {
	                value = $control.prop('checked');
	            } else {
	                value = $control.val();
	            }
	
	            return value;
	        }
	    }]);
	    return FormHelper;
	}();

	exports.default = FormHelper;

/***/ }
/******/ ]);
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIndlYnBhY2s6Ly8vd2VicGFjay9ib290c3RyYXAgN2MxYzRlMGU2OWM5MjkyM2NkMDM/MzhjZCoqKioqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL2hlbHBlcnMvY2xhc3NDYWxsQ2hlY2suanM/MjFhZioqKioqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL2hlbHBlcnMvY3JlYXRlQ2xhc3MuanM/MWRmZSoqKioqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL2NvcmUtanMvb2JqZWN0L2RlZmluZS1wcm9wZXJ0eS5qcz81ZjcwKioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvZm4vb2JqZWN0L2RlZmluZS1wcm9wZXJ0eS5qcz80ZDMzKioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9lczYub2JqZWN0LmRlZmluZS1wcm9wZXJ0eS5qcz84YmRlKioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZXhwb3J0LmpzPzhlNDAqKioqKioqIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19nbG9iYWwuanM/M2M1MioqKioqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2NvcmUuanM/ZDYxMSoqKioqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2N0eC5qcz80ZTU5KioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fYS1mdW5jdGlvbi5qcz8wNjk5KioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9faGlkZS5qcz8wZDJlKioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fb2JqZWN0LWRwLmpzPzU5ODYqKioqKioqIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19hbi1vYmplY3QuanM/M2FmMioqKioqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2lzLW9iamVjdC5qcz9jZmRhKioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9faWU4LWRvbS1kZWZpbmUuanM/YjEwMioqKioqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2Rlc2NyaXB0b3JzLmpzP2MwZjUqKioqKioqIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19mYWlscy5qcz9jNmRkKioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZG9tLWNyZWF0ZS5qcz82MTJmKioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fdG8tcHJpbWl0aXZlLmpzPzFhNjUqKioqKioqIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19wcm9wZXJ0eS1kZXNjLmpzPzI1NmIqKioqKioqIiwid2VicGFjazovLy8uL2Zyb250ZW5kL2pzL3BlcnNvbi9tYWlsY2hpbXAtZWRpdC5qcyIsIndlYnBhY2s6Ly8vLi9mcm9udGVuZC9qcy9wZXJzb24vaW50ZXJncmF0aW9uLXdpZGdldHMvX21haWxjaGltcC1mb3JtLmpzIiwid2VicGFjazovLy8uL2Zyb250ZW5kL2pzL2NvbW1vbi9fZm9ybS1oZWxwZXJzLmpzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiI7QUFBQTtBQUNBOztBQUVBO0FBQ0E7O0FBRUE7QUFDQTtBQUNBOztBQUVBO0FBQ0E7QUFDQSx1QkFBZTtBQUNmO0FBQ0E7QUFDQTs7QUFFQTtBQUNBOztBQUVBO0FBQ0E7O0FBRUE7QUFDQTtBQUNBOzs7QUFHQTtBQUNBOztBQUVBO0FBQ0E7O0FBRUE7QUFDQTs7QUFFQTtBQUNBOzs7Ozs7Ozs7Ozs7Ozs7O0FDdENBOztBQUVBOztBQUVBO0FBQ0E7QUFDQTtBQUNBO0FBQ0EsRzs7Ozs7O0FDUkE7O0FBRUE7O0FBRUE7O0FBRUE7O0FBRUEsdUNBQXNDLHVDQUF1QyxnQkFBZ0I7O0FBRTdGO0FBQ0E7QUFDQSxvQkFBbUIsa0JBQWtCO0FBQ3JDO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBOztBQUVBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQSxFQUFDLEc7Ozs7OztBQzFCRCxtQkFBa0IsdUQ7Ozs7OztBQ0FsQjtBQUNBO0FBQ0E7QUFDQTtBQUNBLEc7Ozs7OztBQ0pBO0FBQ0E7QUFDQSxzRUFBdUUsMENBQTBDLEU7Ozs7OztBQ0ZqSDtBQUNBO0FBQ0E7QUFDQTtBQUNBOztBQUVBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0Esb0VBQW1FO0FBQ25FO0FBQ0Esc0ZBQXFGO0FBQ3JGO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQSxZQUFXO0FBQ1gsVUFBUztBQUNUO0FBQ0E7QUFDQTtBQUNBO0FBQ0EsTUFBSztBQUNMO0FBQ0E7QUFDQSxnREFBK0M7QUFDL0M7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0EsZUFBYztB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WixDQUFKLEVBQTZCO0FBQ3pCLDBCQUFTLElBQVQsQ0FBYyxTQUFkLEVBQXlCLEtBQXpCLEVBRHlCO2NBQTdCLE1BRU07QUFDRiwwQkFBUyxHQUFULENBQWEsS0FBYixFQURFO2NBRk47Ozs7Ozs7Ozs7O3lDQVlZLFVBQVM7QUFDckIsaUJBQUksUUFBUSxJQUFSLENBRGlCOztBQUdyQixpQkFBSSxTQUFTLEVBQVQsQ0FBWSxXQUFaLENBQUosRUFBOEI7QUFDMUIseUJBQVEsU0FBUyxJQUFULENBQWMsU0FBZCxDQUFSLENBRDBCO2NBQTlCLE1BRU87QUFDSCx5QkFBUSxTQUFTLEdBQVQsRUFBUixDQURHO2NBRlA7O0FBTUEsb0JBQU8sS0FBUCxDQVRxQjs7O1lBMVZSIiwiZmlsZSI6Im1haWxjaGltcC1lZGl0LmpzIiwic291cmNlc0NvbnRlbnQiOlsiIFx0Ly8gVGhlIG1vZHVsZSBjYWNoZVxuIFx0dmFyIGluc3RhbGxlZE1vZHVsZXMgPSB7fTtcblxuIFx0Ly8gVGhlIHJlcXVpcmUgZnVuY3Rpb25cbiBcdGZ1bmN0aW9uIF9fd2VicGFja19yZXF1aXJlX18obW9kdWxlSWQpIHtcblxuIFx0XHQvLyBDaGVjayBpZiBtb2R1bGUgaXMgaW4gY2FjaGVcbiBcdFx0aWYoaW5zdGFsbGVkTW9kdWxlc1ttb2R1bGVJZF0pXG4gXHRcdFx0cmV0dXJuIGluc3RhbGxlZE1vZHVsZXNbbW9kdWxlSWRdLmV4cG9ydHM7XG5cbiBcdFx0Ly8gQ3JlYXRlIGEgbmV3IG1vZHVsZSAoYW5kIHB1dCBpdCBpbnRvIHRoZSBjYWNoZSlcbiBcdFx0dmFyIG1vZHVsZSA9IGluc3RhbGxlZE1vZHVsZXNbbW9kdWxlSWRdID0ge1xuIFx0XHRcdGV4cG9ydHM6IHt9LFxuIFx0XHRcdGlkOiBtb2R1bGVJZCxcbiBcdFx0XHRsb2FkZWQ6IGZhbHNlXG4gXHRcdH07XG5cbiBcdFx0Ly8gRXhlY3V0ZSB0aGUgbW9kdWxlIGZ1bmN0aW9uXG4gXHRcdG1vZHVsZXNbbW9kdWxlSWRdLmNhbGwobW9kdWxlLmV4cG9ydHMsIG1vZHVsZSwgbW9kdWxlLmV4cG9ydHMsIF9fd2VicGFja19yZXF1aXJlX18pO1xuXG4gXHRcdC8vIEZsYWcgdGhlIG1vZHVsZSBhcyBsb2FkZWRcbiBcdFx0bW9kdWxlLmxvYWRlZCA9IHRydWU7XG5cbiBcdFx0Ly8gUmV0dXJuIHRoZSBleHBvcnRzIG9mIHRoZSBtb2R1bGVcbiBcdFx0cmV0dXJuIG1vZHVsZS5leHBvcnRzO1xuIFx0fVxuXG5cbiBcdC8vIGV4cG9zZSB0aGUgbW9kdWxlcyBvYmplY3QgKF9fd2VicGFja19tb2R1bGVzX18pXG4gXHRfX3dlYnBhY2tfcmVxdWlyZV9fLm0gPSBtb2R1bGVzO1xuXG4gXHQvLyBleHBvc2UgdGhlIG1vZHVsZSBjYWNoZVxuIFx0X193ZWJwYWNrX3JlcXVpcmVfXy5jID0gaW5zdGFsbGVkTW9kdWxlcztcblxuIFx0Ly8gX193ZWJwYWNrX3B1YmxpY19wYXRoX19cbiBcdF9fd2VicGFja19yZXF1aXJlX18ucCA9IFwiXCI7XG5cbiBcdC8vIExvYWQgZW50cnkgbW9kdWxlIGFuZCByZXR1cm4gZXhwb3J0c1xuIFx0cmV0dXJuIF9fd2VicGFja19yZXF1aXJlX18oMCk7XG5cblxuXG4vKiogV0VCUEFDSyBGT09URVIgKipcbiAqKiB3ZWJwYWNrL2Jvb3RzdHJhcCA3YzFjNGUwZTY5YzkyOTIzY2QwM1xuICoqLyIsIlwidXNlIHN0cmljdFwiO1xuXG5leHBvcnRzLl9fZXNNb2R1bGUgPSB0cnVlO1xuXG5leHBvcnRzLmRlZmF1bHQgPSBmdW5jdGlvbiAoaW5zdGFuY2UsIENvbnN0cnVjdG9yKSB7XG4gIGlmICghKGluc3RhbmNlIGluc3RhbmNlb2YgQ29uc3RydWN0b3IpKSB7XG4gICAgdGhyb3cgbmV3IFR5cGVFcnJvcihcIkNhbm5vdCBjYWxsIGEgY2xhc3MgYXMgYSBmdW5jdGlvblwiKTtcbiAgfVxufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL2hlbHBlcnMvY2xhc3NDYWxsQ2hlY2suanNcbiAqKiBtb2R1bGUgaWQgPSAzXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTIgMTNcbiAqKi8iLCJcInVzZSBzdHJpY3RcIjtcblxuZXhwb3J0cy5fX2VzTW9kdWxlID0gdHJ1ZTtcblxudmFyIF9kZWZpbmVQcm9wZXJ0eSA9IHJlcXVpcmUoXCJiYWJlbC1ydW50aW1lL2NvcmUtanMvb2JqZWN0L2RlZmluZS1wcm9wZXJ0eVwiKTtcblxudmFyIF9kZWZpbmVQcm9wZXJ0eTIgPSBfaW50ZXJvcFJlcXVpcmVEZWZhdWx0KF9kZWZpbmVQcm9wZXJ0eSk7XG5cbmZ1bmN0aW9uIF9pbnRlcm9wUmVxdWlyZURlZmF1bHQob2JqKSB7IHJldHVybiBvYmogJiYgb2JqLl9fZXNNb2R1bGUgPyBvYmogOiB7IGRlZmF1bHQ6IG9iaiB9OyB9XG5cbmV4cG9ydHMuZGVmYXVsdCA9IGZ1bmN0aW9uICgpIHtcbiAgZnVuY3Rpb24gZGVmaW5lUHJvcGVydGllcyh0YXJnZXQsIHByb3BzKSB7XG4gICAgZm9yICh2YXIgaSA9IDA7IGkgPCBwcm9wcy5sZW5ndGg7IGkrKykge1xuICAgICAgdmFyIGRlc2NyaXB0b3IgPSBwcm9wc1tpXTtcbiAgICAgIGRlc2NyaXB0b3IuZW51bWVyYWJsZSA9IGRlc2NyaXB0b3IuZW51bWVyYWJsZSB8fCBmYWxzZTtcbiAgICAgIGRlc2NyaXB0b3IuY29uZmlndXJhYmxlID0gdHJ1ZTtcbiAgICAgIGlmIChcInZhbHVlXCIgaW4gZGVzY3JpcHRvcikgZGVzY3JpcHRvci53cml0YWJsZSA9IHRydWU7XG4gICAgICAoMCwgX2RlZmluZVByb3BlcnR5Mi5kZWZhdWx0KSh0YXJnZXQsIGRlc2NyaXB0b3Iua2V5LCBkZXNjcmlwdG9yKTtcbiAgICB9XG4gIH1cblxuICByZXR1cm4gZnVuY3Rpb24gKENvbnN0cnVjdG9yLCBwcm90b1Byb3BzLCBzdGF0aWNQcm9wcykge1xuICAgIGlmIChwcm90b1Byb3BzKSBkZWZpbmVQcm9wZXJ0aWVzKENvbnN0cnVjdG9yLnByb3RvdHlwZSwgcHJvdG9Qcm9wcyk7XG4gICAgaWYgKHN0YXRpY1Byb3BzKSBkZWZpbmVQcm9wZXJ0aWVzKENvbnN0cnVjdG9yLCBzdGF0aWNQcm9wcyk7XG4gICAgcmV0dXJuIENvbnN0cnVjdG9yO1xuICB9O1xufSgpO1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvaGVscGVycy9jcmVhdGVDbGFzcy5qc1xuICoqIG1vZHVsZSBpZCA9IDRcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMiAxM1xuICoqLyIsIm1vZHVsZS5leHBvcnRzID0geyBcImRlZmF1bHRcIjogcmVxdWlyZShcImNvcmUtanMvbGlicmFyeS9mbi9vYmplY3QvZGVmaW5lLXByb3BlcnR5XCIpLCBfX2VzTW9kdWxlOiB0cnVlIH07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9jb3JlLWpzL29iamVjdC9kZWZpbmUtcHJvcGVydHkuanNcbiAqKiBtb2R1bGUgaWQgPSA1XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTIgMTNcbiAqKi8iLCJyZXF1aXJlKCcuLi8uLi9tb2R1bGVzL2VzNi5vYmplY3QuZGVmaW5lLXByb3BlcnR5Jyk7XG52YXIgJE9iamVjdCA9IHJlcXVpcmUoJy4uLy4uL21vZHVsZXMvX2NvcmUnKS5PYmplY3Q7XG5tb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uIGRlZmluZVByb3BlcnR5KGl0LCBrZXksIGRlc2Mpe1xuICByZXR1cm4gJE9iamVjdC5kZWZpbmVQcm9wZXJ0eShpdCwga2V5LCBkZXNjKTtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9mbi9vYmplY3QvZGVmaW5lLXByb3BlcnR5LmpzXG4gKiogbW9kdWxlIGlkID0gNlxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyIDEzXG4gKiovIiwidmFyICRleHBvcnQgPSByZXF1aXJlKCcuL19leHBvcnQnKTtcclxuLy8gMTkuMS4yLjQgLyAxNS4yLjMuNiBPYmplY3QuZGVmaW5lUHJvcGVydHkoTywgUCwgQXR0cmlidXRlcylcclxuJGV4cG9ydCgkZXhwb3J0LlMgKyAkZXhwb3J0LkYgKiAhcmVxdWlyZSgnLi9fZGVzY3JpcHRvcnMnKSwgJ09iamVjdCcsIHtkZWZpbmVQcm9wZXJ0eTogcmVxdWlyZSgnLi9fb2JqZWN0LWRwJykuZn0pO1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9lczYub2JqZWN0LmRlZmluZS1wcm9wZXJ0eS5qc1xuICoqIG1vZHVsZSBpZCA9IDdcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMiAxM1xuICoqLyIsInZhciBnbG9iYWwgICAgPSByZXF1aXJlKCcuL19nbG9iYWwnKVxuICAsIGNvcmUgICAgICA9IHJlcXVpcmUoJy4vX2NvcmUnKVxuICAsIGN0eCAgICAgICA9IHJlcXVpcmUoJy4vX2N0eCcpXG4gICwgaGlkZSAgICAgID0gcmVxdWlyZSgnLi9faGlkZScpXG4gICwgUFJPVE9UWVBFID0gJ3Byb3RvdHlwZSc7XG5cbnZhciAkZXhwb3J0ID0gZnVuY3Rpb24odHlwZSwgbmFtZSwgc291cmNlKXtcbiAgdmFyIElTX0ZPUkNFRCA9IHR5cGUgJiAkZXhwb3J0LkZcbiAgICAsIElTX0dMT0JBTCA9IHR5cGUgJiAkZXhwb3J0LkdcbiAgICAsIElTX1NUQVRJQyA9IHR5cGUgJiAkZXhwb3J0LlNcbiAgICAsIElTX1BST1RPICA9IHR5cGUgJiAkZXhwb3J0LlBcbiAgICAsIElTX0JJTkQgICA9IHR5cGUgJiAkZXhwb3J0LkJcbiAgICAsIElTX1dSQVAgICA9IHR5cGUgJiAkZXhwb3J0LldcbiAgICAsIGV4cG9ydHMgICA9IElTX0dMT0JBTCA/IGNvcmUgOiBjb3JlW25hbWVdIHx8IChjb3JlW25hbWVdID0ge30pXG4gICAgLCBleHBQcm90byAgPSBleHBvcnRzW1BST1RPVFlQRV1cbiAgICAsIHRhcmdldCAgICA9IElTX0dMT0JBTCA/IGdsb2JhbCA6IElTX1NUQVRJQyA/IGdsb2JhbFtuYW1lXSA6IChnbG9iYWxbbmFtZV0gfHwge30pW1BST1RPVFlQRV1cbiAgICAsIGtleSwgb3duLCBvdXQ7XG4gIGlmKElTX0dMT0JBTClzb3VyY2UgPSBuYW1lO1xuICBmb3Ioa2V5IGluIHNvdXJjZSl7XG4gICAgLy8gY29udGFpbnMgaW4gbmF0aXZlXG4gICAgb3duID0gIUlTX0ZPUkNFRCAmJiB0YXJnZXQgJiYgdGFyZ2V0W2tleV0gIT09IHVuZGVmaW5lZDtcbiAgICBpZihvd24gJiYga2V5IGluIGV4cG9ydHMpY29udGludWU7XG4gICAgLy8gZXhwb3J0IG5hdGl2ZSBvciBwYXNzZWRcbiAgICBvdXQgPSBvd24gPyB0YXJnZXRba2V5XSA6IHNvdXJjZVtrZXldO1xuICAgIC8vIHByZXZlbnQgZ2xvYmFsIHBvbGx1dGlvbiBmb3IgbmFtZXNwYWNlc1xuICAgIGV4cG9ydHNba2V5XSA9IElTX0dMT0JBTCAmJiB0eXBlb2YgdGFyZ2V0W2tleV0gIT0gJ2Z1bmN0aW9uJyA/IHNvdXJjZVtrZXldXG4gICAgLy8gYmluZCB0aW1lcnMgdG8gZ2xvYmFsIGZvciBjYWxsIGZyb20gZXhwb3J0IGNvbnRleHRcbiAgICA6IElTX0JJTkQgJiYgb3duID8gY3R4KG91dCwgZ2xvYmFsKVxuICAgIC8vIHdyYXAgZ2xvYmFsIGNvbnN0cnVjdG9ycyBmb3IgcHJldmVudCBjaGFuZ2UgdGhlbSBpbiBsaWJyYXJ5XG4gICAgOiBJU19XUkFQICYmIHRhcmdldFtrZXldID09IG91dCA/IChmdW5jdGlvbihDKXtcbiAgICAgIHZhciBGID0gZnVuY3Rpb24oYSwgYiwgYyl7XG4gICAgICAgIGlmKHRoaXMgaW5zdGFuY2VvZiBDKXtcbiAgICAgICAgICBzd2l0Y2goYXJndW1lbnRzLmxlbmd0aCl7XG4gICAgICAgICAgICBjYXNlIDA6IHJldHVybiBuZXcgQztcbiAgICAgICAgICAgIGNhc2UgMTogcmV0dXJuIG5ldyBDKGEpO1xuICAgICAgICAgICAgY2FzZSAyOiByZXR1cm4gbmV3IEMoYSwgYik7XG4gICAgICAgICAgfSByZXR1cm4gbmV3IEMoYSwgYiwgYyk7XG4gICAgICAgIH0gcmV0dXJuIEMuYXBwbHkodGhpcywgYXJndW1lbnRzKTtcbiAgICAgIH07XG4gICAgICBGW1BST1RPVFlQRV0gPSBDW1BST1RPVFlQRV07XG4gICAgICByZXR1cm4gRjtcbiAgICAvLyBtYWtlIHN0YXRpYyB2ZXJzaW9ucyBmb3IgcHJvdG90eXBlIG1ldGhvZHNcbiAgICB9KShvdXQpIDogSVNfUFJPVE8gJiYgdHlwZW9mIG91dCA9PSAnZnVuY3Rpb24nID8gY3R4KEZ1bmN0aW9uLmNhbGwsIG91dCkgOiBvdXQ7XG4gICAgLy8gZXhwb3J0IHByb3RvIG1ldGhvZHMgdG8gY29yZS4lQ09OU1RSVUNUT1IlLm1ldGhvZHMuJU5BTUUlXG4gICAgaWYoSVNfUFJPVE8pe1xuICAgICAgKGV4cG9ydHMudmlydHVhbCB8fCAoZXhwb3J0cy52aXJ0dWFsID0ge30pKVtrZXldID0gb3V0O1xuICAgICAgLy8gZXhwb3J0IHByb3RvIG1ldGhvZHMgdG8gY29yZS4lQ09OU1RSVUNUT1IlLnByb3RvdHlwZS4lTkFNRSVcbiAgICAgIGlmKHR5cGUgJiAkZXhwb3J0LlIgJiYgZXhwUHJvdG8gJiYgIWV4cFByb3RvW2tleV0paGlkZShleHBQcm90bywga2V5LCBvdXQpO1xuICAgIH1cbiAgfVxufTtcbi8vIHR5cGUgYml0bWFwXG4kZXhwb3J0LkYgPSAxOyAgIC8vIGZvcmNlZFxuJGV4cG9ydC5HID0gMjsgICAvLyBnbG9iYWxcbiRleHBvcnQuUyA9IDQ7ICAgLy8gc3RhdGljXG4kZXhwb3J0LlAgPSA4OyAgIC8vIHByb3RvXG4kZXhwb3J0LkIgPSAxNjsgIC8vIGJpbmRcbiRleHBvcnQuVyA9IDMyOyAgLy8gd3JhcFxuJGV4cG9ydC5VID0gNjQ7ICAvLyBzYWZlXG4kZXhwb3J0LlIgPSAxMjg7IC8vIHJlYWwgcHJvdG8gbWV0aG9kIGZvciBgbGlicmFyeWAgXG5tb2R1bGUuZXhwb3J0cyA9ICRleHBvcnQ7XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19leHBvcnQuanNcbiAqKiBtb2R1bGUgaWQgPSA4XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTIgMTNcbiAqKi8iLCIvLyBodHRwczovL2dpdGh1Yi5jb20vemxvaXJvY2svY29yZS1qcy9pc3N1ZXMvODYjaXNzdWVjb21tZW50LTExNTc1OTAyOFxudmFyIGdsb2JhbCA9IG1vZHVsZS5leHBvcnRzID0gdHlwZW9mIHdpbmRvdyAhPSAndW5kZWZpbmVkJyAmJiB3aW5kb3cuTWF0aCA9PSBNYXRoXG4gID8gd2luZG93IDogdHlwZW9mIHNlbGYgIT0gJ3VuZGVmaW5lZCcgJiYgc2VsZi5NYXRoID09IE1hdGggPyBzZWxmIDogRnVuY3Rpb24oJ3JldHVybiB0aGlzJykoKTtcbmlmKHR5cGVvZiBfX2cgPT0gJ251bWJlcicpX19nID0gZ2xvYmFsOyAvLyBlc2xpbnQtZGlzYWJsZS1saW5lIG5vLXVuZGVmXG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19nbG9iYWwuanNcbiAqKiBtb2R1bGUgaWQgPSA5XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTIgMTNcbiAqKi8iLCJ2YXIgY29yZSA9IG1vZHVsZS5leHBvcnRzID0ge3ZlcnNpb246ICcyLjIuMCd9O1xuaWYodHlwZW9mIF9fZSA9PSAnbnVtYmVyJylfX2UgPSBjb3JlOyAvLyBlc2xpbnQtZGlzYWJsZS1saW5lIG5vLXVuZGVmXG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19jb3JlLmpzXG4gKiogbW9kdWxlIGlkID0gMTBcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMiAxM1xuICoqLyIsIi8vIG9wdGlvbmFsIC8gc2ltcGxlIGNvbnRleHQgYmluZGluZ1xudmFyIGFGdW5jdGlvbiA9IHJlcXVpcmUoJy4vX2EtZnVuY3Rpb24nKTtcbm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oZm4sIHRoYXQsIGxlbmd0aCl7XG4gIGFGdW5jdGlvbihmbik7XG4gIGlmKHRoYXQgPT09IHVuZGVmaW5lZClyZXR1cm4gZm47XG4gIHN3aXRjaChsZW5ndGgpe1xuICAgIGNhc2UgMTogcmV0dXJuIGZ1bmN0aW9uKGEpe1xuICAgICAgcmV0dXJuIGZuLmNhbGwodGhhdCwgYSk7XG4gICAgfTtcbiAgICBjYXNlIDI6IHJldHVybiBmdW5jdGlvbihhLCBiKXtcbiAgICAgIHJldHVybiBmbi5jYWxsKHRoYXQsIGEsIGIpO1xuICAgIH07XG4gICAgY2FzZSAzOiByZXR1cm4gZnVuY3Rpb24oYSwgYiwgYyl7XG4gICAgICByZXR1cm4gZm4uY2FsbCh0aGF0LCBhLCBiLCBjKTtcbiAgICB9O1xuICB9XG4gIHJldHVybiBmdW5jdGlvbigvKiAuLi5hcmdzICovKXtcbiAgICByZXR1cm4gZm4uYXBwbHkodGhhdCwgYXJndW1lbnRzKTtcbiAgfTtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19jdHguanNcbiAqKiBtb2R1bGUgaWQgPSAxMVxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyIDEzXG4gKiovIiwibW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihpdCl7XG4gIGlmKHR5cGVvZiBpdCAhPSAnZnVuY3Rpb24nKXRocm93IFR5cGVFcnJvcihpdCArICcgaXMgbm90IGEgZnVuY3Rpb24hJyk7XG4gIHJldHVybiBpdDtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19hLWZ1bmN0aW9uLmpzXG4gKiogbW9kdWxlIGlkID0gMTJcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMiAxM1xuICoqLyIsInZhciBkUCAgICAgICAgID0gcmVxdWlyZSgnLi9fb2JqZWN0LWRwJylcbiAgLCBjcmVhdGVEZXNjID0gcmVxdWlyZSgnLi9fcHJvcGVydHktZGVzYycpO1xubW9kdWxlLmV4cG9ydHMgPSByZXF1aXJlKCcuL19kZXNjcmlwdG9ycycpID8gZnVuY3Rpb24ob2JqZWN0LCBrZXksIHZhbHVlKXtcbiAgcmV0dXJuIGRQLmYob2JqZWN0LCBrZXksIGNyZWF0ZURlc2MoMSwgdmFsdWUpKTtcbn0gOiBmdW5jdGlvbihvYmplY3QsIGtleSwgdmFsdWUpe1xuICBvYmplY3Rba2V5XSA9IHZhbHVlO1xuICByZXR1cm4gb2JqZWN0O1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2hpZGUuanNcbiAqKiBtb2R1bGUgaWQgPSAxM1xuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyIDEzXG4gKiovIiwidmFyIGFuT2JqZWN0ICAgICAgID0gcmVxdWlyZSgnLi9fYW4tb2JqZWN0JylcbiAgLCBJRThfRE9NX0RFRklORSA9IHJlcXVpcmUoJy4vX2llOC1kb20tZGVmaW5lJylcbiAgLCB0b1ByaW1pdGl2ZSAgICA9IHJlcXVpcmUoJy4vX3RvLXByaW1pdGl2ZScpXG4gICwgZFAgICAgICAgICAgICAgPSBPYmplY3QuZGVmaW5lUHJvcGVydHk7XG5cbmV4cG9ydHMuZiA9IHJlcXVpcmUoJy4vX2Rlc2NyaXB0b3JzJykgPyBPYmplY3QuZGVmaW5lUHJvcGVydHkgOiBmdW5jdGlvbiBkZWZpbmVQcm9wZXJ0eShPLCBQLCBBdHRyaWJ1dGVzKXtcbiAgYW5PYmplY3QoTyk7XG4gIFAgPSB0b1ByaW1pdGl2ZShQLCB0cnVlKTtcbiAgYW5PYmplY3QoQXR0cmlidXRlcyk7XG4gIGlmKElFOF9ET01fREVGSU5FKXRyeSB7XG4gICAgcmV0dXJuIGRQKE8sIFAsIEF0dHJpYnV0ZXMpO1xuICB9IGNhdGNoKGUpeyAvKiBlbXB0eSAqLyB9XG4gIGlmKCdnZXQnIGluIEF0dHJpYnV0ZXMgfHwgJ3NldCcgaW4gQXR0cmlidXRlcyl0aHJvdyBUeXBlRXJyb3IoJ0FjY2Vzc29ycyBub3Qgc3VwcG9ydGVkIScpO1xuICBpZigndmFsdWUnIGluIEF0dHJpYnV0ZXMpT1tQXSA9IEF0dHJpYnV0ZXMudmFsdWU7XG4gIHJldHVybiBPO1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX29iamVjdC1kcC5qc1xuICoqIG1vZHVsZSBpZCA9IDE0XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTIgMTNcbiAqKi8iLCJ2YXIgaXNPYmplY3QgPSByZXF1aXJlKCcuL19pcy1vYmplY3QnKTtcbm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oaXQpe1xuICBpZighaXNPYmplY3QoaXQpKXRocm93IFR5cGVFcnJvcihpdCArICcgaXMgbm90IGFuIG9iamVjdCEnKTtcbiAgcmV0dXJuIGl0O1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2FuLW9iamVjdC5qc1xuICoqIG1vZHVsZSBpZCA9IDE1XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTIgMTNcbiAqKi8iLCJtb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGl0KXtcbiAgcmV0dXJuIHR5cGVvZiBpdCA9PT0gJ29iamVjdCcgPyBpdCAhPT0gbnVsbCA6IHR5cGVvZiBpdCA9PT0gJ2Z1bmN0aW9uJztcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19pcy1vYmplY3QuanNcbiAqKiBtb2R1bGUgaWQgPSAxNlxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyIDEzXG4gKiovIiwibW9kdWxlLmV4cG9ydHMgPSAhcmVxdWlyZSgnLi9fZGVzY3JpcHRvcnMnKSAmJiAhcmVxdWlyZSgnLi9fZmFpbHMnKShmdW5jdGlvbigpe1xyXG4gIHJldHVybiBPYmplY3QuZGVmaW5lUHJvcGVydHkocmVxdWlyZSgnLi9fZG9tLWNyZWF0ZScpKCdkaXYnKSwgJ2EnLCB7Z2V0OiBmdW5jdGlvbigpeyByZXR1cm4gNzsgfX0pLmEgIT0gNztcclxufSk7XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19pZTgtZG9tLWRlZmluZS5qc1xuICoqIG1vZHVsZSBpZCA9IDE3XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTIgMTNcbiAqKi8iLCIvLyBUaGFuaydzIElFOCBmb3IgaGlzIGZ1bm55IGRlZmluZVByb3BlcnR5XG5tb2R1bGUuZXhwb3J0cyA9ICFyZXF1aXJlKCcuL19mYWlscycpKGZ1bmN0aW9uKCl7XG4gIHJldHVybiBPYmplY3QuZGVmaW5lUHJvcGVydHkoe30sICdhJywge2dldDogZnVuY3Rpb24oKXsgcmV0dXJuIDc7IH19KS5hICE9IDc7XG59KTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2Rlc2NyaXB0b3JzLmpzXG4gKiogbW9kdWxlIGlkID0gMThcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMiAxM1xuICoqLyIsIm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oZXhlYyl7XG4gIHRyeSB7XG4gICAgcmV0dXJuICEhZXhlYygpO1xuICB9IGNhdGNoKGUpe1xuICAgIHJldHVybiB0cnVlO1xuICB9XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZmFpbHMuanNcbiAqKiBtb2R1bGUgaWQgPSAxOVxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyIDEzXG4gKiovIiwidmFyIGlzT2JqZWN0ID0gcmVxdWlyZSgnLi9faXMtb2JqZWN0JylcbiAgLCBkb2N1bWVudCA9IHJlcXVpcmUoJy4vX2dsb2JhbCcpLmRvY3VtZW50XG4gIC8vIGluIG9sZCBJRSB0eXBlb2YgZG9jdW1lbnQuY3JlYXRlRWxlbWVudCBpcyAnb2JqZWN0J1xuICAsIGlzID0gaXNPYmplY3QoZG9jdW1lbnQpICYmIGlzT2JqZWN0KGRvY3VtZW50LmNyZWF0ZUVsZW1lbnQpO1xubW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihpdCl7XG4gIHJldHVybiBpcyA/IGRvY3VtZW50LmNyZWF0ZUVsZW1lbnQoaXQpIDoge307XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZG9tLWNyZWF0ZS5qc1xuICoqIG1vZHVsZSBpZCA9IDIwXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTIgMTNcbiAqKi8iLCIvLyA3LjEuMSBUb1ByaW1pdGl2ZShpbnB1dCBbLCBQcmVmZXJyZWRUeXBlXSlcbnZhciBpc09iamVjdCA9IHJlcXVpcmUoJy4vX2lzLW9iamVjdCcpO1xuLy8gaW5zdGVhZCBvZiB0aGUgRVM2IHNwZWMgdmVyc2lvbiwgd2UgZGlkbid0IGltcGxlbWVudCBAQHRvUHJpbWl0aXZlIGNhc2Vcbi8vIGFuZCB0aGUgc2Vjb25kIGFyZ3VtZW50IC0gZmxhZyAtIHByZWZlcnJlZCB0eXBlIGlzIGEgc3RyaW5nXG5tb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGl0LCBTKXtcbiAgaWYoIWlzT2JqZWN0KGl0KSlyZXR1cm4gaXQ7XG4gIHZhciBmbiwgdmFsO1xuICBpZihTICYmIHR5cGVvZiAoZm4gPSBpdC50b1N0cmluZykgPT0gJ2Z1bmN0aW9uJyAmJiAhaXNPYmplY3QodmFsID0gZm4uY2FsbChpdCkpKXJldHVybiB2YWw7XG4gIGlmKHR5cGVvZiAoZm4gPSBpdC52YWx1ZU9mKSA9PSAnZnVuY3Rpb24nICYmICFpc09iamVjdCh2YWwgPSBmbi5jYWxsKGl0KSkpcmV0dXJuIHZhbDtcbiAgaWYoIVMgJiYgdHlwZW9mIChmbiA9IGl0LnRvU3RyaW5nKSA9PSAnZnVuY3Rpb24nICYmICFpc09iamVjdCh2YWwgPSBmbi5jYWxsKGl0KSkpcmV0dXJuIHZhbDtcbiAgdGhyb3cgVHlwZUVycm9yKFwiQ2FuJ3QgY29udmVydCBvYmplY3QgdG8gcHJpbWl0aXZlIHZhbHVlXCIpO1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX3RvLXByaW1pdGl2ZS5qc1xuICoqIG1vZHVsZSBpZCA9IDIxXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTIgMTNcbiAqKi8iLCJtb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGJpdG1hcCwgdmFsdWUpe1xuICByZXR1cm4ge1xuICAgIGVudW1lcmFibGUgIDogIShiaXRtYXAgJiAxKSxcbiAgICBjb25maWd1cmFibGU6ICEoYml0bWFwICYgMiksXG4gICAgd3JpdGFibGUgICAgOiAhKGJpdG1hcCAmIDQpLFxuICAgIHZhbHVlICAgICAgIDogdmFsdWVcbiAgfTtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19wcm9wZXJ0eS1kZXNjLmpzXG4gKiogbW9kdWxlIGlkID0gMjJcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMiAxM1xuICoqLyIsIlxuJ3VzZSBzdHJpY3QnO1xuXG5pbXBvcnQgRWRpdEZvcm0gZnJvbSBcIi4vaW50ZXJncmF0aW9uLXdpZGdldHMvX21haWxjaGltcC1mb3JtXCI7XG5cbiQoZnVuY3Rpb24oKXtcbiAgICBFZGl0Rm9ybS5wbHVnaW4oJy5qcy1lZGl0LW1haWxjaGltcCcpO1xufSk7XG5cblxuXG5cblxuXG4vKiogV0VCUEFDSyBGT09URVIgKipcbiAqKiAuL2Zyb250ZW5kL2pzL3BlcnNvbi9tYWlsY2hpbXAtZWRpdC5qc1xuICoqLyIsIid1c2Ugc3RyaWN0JztcblxuaW1wb3J0IEZvcm1IZWxwZXIgZnJvbSAnLi8uLi8uLi9jb21tb24vX2Zvcm0taGVscGVycyc7XG5cbmV4cG9ydCBkZWZhdWx0IGNsYXNzIFdpZGdldCB7XG4gICAgY29uc3RydWN0b3Ioc2VsZWN0b3IpIHtcbiAgICAgICAgdGhpcy4kcm9vdCA9ICQoc2VsZWN0b3IpO1xuICAgICAgICB0aGlzLmxvY2FscyA9IHRoaXMuX2dldERvbSgpO1xuXG4gICAgICAgIHRoaXMuaW1wb3J0RGxnSGVscGVyID0gbmV3IEZvcm1IZWxwZXIoe1xuICAgICAgICAgICAgJGNvbnRyb2xzOiB0aGlzLmxvY2Fscy4kY29udHJvbHMsXG4gICAgICAgICAgICBydWxlczogdGhpcy5fZ2V0UnVsZXMoKVxuICAgICAgICB9KTtcbiAgICAgICAgdGhpcy5fYXNzaWduRXZlbnRzKCk7XG4gICAgfVxuXG4gICAgX2dldERvbSgpe1xuICAgICAgICBjb25zdCAkcm9vdCA9IHRoaXMuJHJvb3Q7XG5cbiAgICAgICAgcmV0dXJuIHtcbiAgICAgICAgICAgICRmb3JtOiAkcm9vdC5maW5kKCdmb3JtJyksXG4gICAgICAgICAgICAkY29udHJvbHM6ICRyb290LmZpbmQoJ2lucHV0LCBzZWxlY3QsIHRleHRhcmVhJylcbiAgICAgICAgfVxuICAgIH1cblxuICAgIF9nZXRSdWxlcygpe1xuICAgICAgICByZXR1cm4ge1xuICAgICAgICAgICAgbmFtZToge3JlcXVpcmVkOiB0cnVlfSxcbiAgICAgICAgICAgIFwiZGVmYXVsdHMuZnJvbUVtYWlsXCI6ICAge3JlcXVpcmVkOiB0cnVlfSxcbiAgICAgICAgICAgIFwiZGVmYXVsdHMuZnJvbU5hbWVcIjogICAge3JlcXVpcmVkOiB0cnVlfSxcbiAgICAgICAgICAgIFwiZGVmYXVsdHMuc3ViamVjdFwiOiAgICAge3JlcXVpcmVkOiB0cnVlfSxcbiAgICAgICAgICAgIFwiZGVmYXVsdHMubGFuZ3VhZ2VcIjogICAge3JlcXVpcmVkOiB0cnVlfSxcbiAgICAgICAgICAgIHJlbWluZGVyOiAgICAgICAgICAgICAgIHtyZXF1aXJlZDogdHJ1ZX0sXG4gICAgICAgICAgICBcImNvbXBhbnkubmFtZVwiOiAgICAgICAgIHtyZXF1aXJlZDogdHJ1ZX0sXG4gICAgICAgICAgICBcImNvbXBhbnkuYWRkcmVzczFcIjogICAgIHtyZXF1aXJlZDogdHJ1ZX0sXG4gICAgICAgICAgICBcImNvbXBhbnkuemlwXCI6ICAgICAgICAgIHtyZXF1aXJlZDogdHJ1ZX0sXG4gICAgICAgICAgICBcImNvbXBhbnkuY2l0eVwiOiAgICAgICAgIHtyZXF1aXJlZDogdHJ1ZX0sXG4gICAgICAgICAgICBcImNvbXBhbnkuc3RhdGVcIjogICAgICAgIHtyZXF1aXJlZDogdHJ1ZX0sXG4gICAgICAgICAgICBcImNvbXBhbnkuY291bnRyeUNvZGVcIjogIHtyZXF1aXJlZDogdHJ1ZX0sXG4gICAgICAgICAgICBcImFsbEF0dGVuZGVlc1wiOiAgICAgICAgIHtyZXF1aXJlZDogdHJ1ZX1cbiAgICAgICAgfVxuICAgIH1cblxuICAgIF9hc3NpZ25FdmVudHMoKXtcbiAgICAgICAgdGhpcy5sb2NhbHMuJGZvcm1cbiAgICAgICAgICAgIC5vbignc3VibWl0JywgdGhpcy5fb25TdWJtaXRGb3JtLmJpbmQodGhpcykpXG4gICAgfVxuXG4gICAgX29uU3VibWl0Rm9ybShlKXtcblxuICAgIH1cblxuICAgIC8vIHN0YXRpY1xuICAgIHN0YXRpYyBwbHVnaW4oc2VsZWN0b3IsIG9wdGlvbnMpIHtcbiAgICAgICAgY29uc3QgJGVsZW1zID0gJChzZWxlY3Rvcik7XG4gICAgICAgIGlmICghJGVsZW1zLmxlbmd0aCkgcmV0dXJuO1xuXG4gICAgICAgIHJldHVybiAkZWxlbXMuZWFjaChmdW5jdGlvbiAoaW5kZXgsIGVsKSB7XG4gICAgICAgICAgICBsZXQgJGVsZW1lbnQgPSAkKGVsKTtcbiAgICAgICAgICAgIGxldCBkYXRhICAgICA9ICRlbGVtZW50LmRhdGEoJ3dpZGdldC5pbnRlZ3JhdGlvbi5tYWlsY2hpbXAnKTtcblxuICAgICAgICAgICAgaWYgKCFkYXRhKSB7XG4gICAgICAgICAgICAgICAgZGF0YSA9IG5ldyBXaWRnZXQoZWwsIG9wdGlvbnMpO1xuICAgICAgICAgICAgICAgICRlbGVtZW50LmRhdGEoJ3dpZGdldC5pbnRlZ3JhdGlvbi5tYWlsY2hpbXAnLCBkYXRhKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgfSlcbiAgICB9XG59XG5cblxuXG5cblxuXG5cblxuLyoqIFdFQlBBQ0sgRk9PVEVSICoqXG4gKiogLi9mcm9udGVuZC9qcy9wZXJzb24vaW50ZXJncmF0aW9uLXdpZGdldHMvX21haWxjaGltcC1mb3JtLmpzXG4gKiovIiwiXG4ndXNlIHN0cmljdCc7XG5cbmV4cG9ydCBkZWZhdWx0IGNsYXNzIEZvcm1IZWxwZXIge1xuICAgIC8qKlxuICAgICAqIFZhbGlkYXRlIGdpdmVuIGNvbnRyb2xzXG4gICAgICogQHBhcmFtIHtPYmplY3R9IG9wdGlvbnNcbiAgICAgKiBAcGFyYW0ge2pRdWVyeX0gb3B0aW9ucy4kY29udHJvbHMgICAgICAgLSBvcHRpb25hbCBsaXN0IG9mIHZhbGlkYXRpbmcgY29udHJvbHNcbiAgICAgKiBAcGFyYW0ge09iamVjdH0gb3B0aW9ucy5ydWxlcyAgICAgICAgICAgLSBsaXN0IG9mIHJ1bGUgXG4gICAgICogQHBhcmFtIHtPYmplY3R9IFtvcHRpb25zLnJlc3RyaWN0aW9uXSAgIC0gbGlzdCBvZiByZXN0cmljdGlvblxuICAgICAqIEBwYXJhbSB7T2JqZWN0fSBtZXNzYWdlc1xuICAgICAqL1xuICAgIGNvbnN0cnVjdG9yKG9wdGlvbnMsIG1lc3NhZ2VzID0gbnVsbCkge1xuICAgICAgICB0aGlzLiRjb250cm9scyA9IG9wdGlvbnMuJGNvbnRyb2xzO1xuXG4gICAgICAgIHRoaXMubWVzc2FnZXMgPSBtZXNzYWdlcyB8fCB0aGlzLl9nZXREZWZhdWx0TWVzc2FnZXMoKTtcbiAgICAgICAgdGhpcy5ydWxlcyA9ICQuZXh0ZW5kKHt9LCBvcHRpb25zLnJ1bGVzLCB0aGlzLl9nZXRSdWxlc0Zyb21IdG1sKHRoaXMuJGNvbnRyb2xzKSk7XG4gICAgICAgIHRoaXMucmVzdHJpY3Rpb24gPSAkLmV4dGVuZCh7fSwgb3B0aW9ucy5yZXN0cmljdGlvbiwgdGhpcy5fZ2V0UmVzdHJpY3Rpb25Gcm9tSHRtbCh0aGlzLiRjb250cm9scykpXG4gICAgICAgIHRoaXMuZXJyb3JzID0gW107XG5cbiAgICAgICAgdGhpcy5fYXNzaWduRXZlbnRzKCk7XG4gICAgfVxuXG4gICAgX2dldERlZmF1bHRNZXNzYWdlcygpe1xuICAgICAgICByZXR1cm4ge1xuICAgICAgICAgICAgcmVxdWlyZWQ6IFwiVGhpcyBmaWVsZCBpcyByZXF1aXJlZC5cIixcbiAgICAgICAgICAgIGVtYWlsOiBcIlBsZWFzZSBlbnRlciBhIHZhbGlkIGVtYWlsIGFkZHJlc3MuXCIsXG4gICAgICAgICAgICB1cmw6IFwiUGxlYXNlIGVudGVyIGEgdmFsaWQgVVJMLlwiLFxuICAgICAgICAgICAgZGF0ZTogXCJQbGVhc2UgZW50ZXIgYSB2YWxpZCBkYXRlLlwiLFxuICAgICAgICAgICAgZGF0ZWlzbzogXCJQbGVhc2UgZW50ZXIgYSB2YWxpZCBkYXRlIChJU08pLlwiLFxuICAgICAgICAgICAgbm9zcGFjZTogXCJQbGVhc2UgZW50ZXIgYSB2YWxpZCBudW1iZXIuXCIsXG4gICAgICAgICAgICBkaWdpdHM6IFwiUGxlYXNlIGVudGVyIG9ubHkgZGlnaXRzLlwiXG4gICAgICAgIH1cbiAgICB9XG5cbiAgICAvKipcbiAgICAgKiBAcGFyYW0gJGNvbnRyb2xzXG4gICAgICogQHJldHVybnMge09iamVjdH0gLSBsaXN0IG9mIHJ1bGVzXG4gICAgICogQHByaXZhdGVcbiAgICAgKi9cbiAgICBfZ2V0UnVsZXNGcm9tSHRtbCgkY29udHJvbHMpe1xuICAgICAgICBjb25zdCBzZWxmID0gdGhpcztcbiAgICAgICAgbGV0IHJ1bGVzID0ge307XG5cbiAgICAgICAgJGNvbnRyb2xzLmVhY2goIChpbmRleCwgaXRlbSk9PntcbiAgICAgICAgICAgIGNvbnN0ICRpdGVtID0gJChpdGVtKTtcbiAgICAgICAgICAgIGNvbnN0IG5hbWVGaWVsZCA9ICRpdGVtLmF0dHIoJ25hbWUnKTtcbiAgICAgICAgICAgIGNvbnN0IHBvc3NpYmxlUnVsZXMgPSBzZWxmLm1lc3NhZ2VzO1xuXG4gICAgICAgICAgICBpZiAoISRpdGVtLmF0dHIoJ2NsYXNzJykubWF0Y2goL192YWxpZGF0ZS0vaSkpIHJldHVybjtcbiAgICAgICAgICAgIGlmICghcnVsZXNbbmFtZUZpZWxkXSkgcnVsZXNbbmFtZUZpZWxkXSA9IHt9O1xuXG4gICAgICAgICAgICBmb3IobGV0IHJ1bGUgaW4gcG9zc2libGVSdWxlcyl7XG4gICAgICAgICAgICAgICAgY29uc3QgcnVsZUNsYXNzID0gYF92YWxpZGF0ZS0ke3J1bGV9YDtcblxuICAgICAgICAgICAgICAgIGlmICgkaXRlbS5oYXNDbGFzcyhydWxlQ2xhc3MpKXtcbiAgICAgICAgICAgICAgICAgICAgcnVsZXNbbmFtZUZpZWxkXVtydWxlXSA9IHRydWU7XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgfVxuICAgICAgICB9KTtcbiAgICAgICAgcmV0dXJuIHJ1bGVzO1xuICAgIH1cblxuICAgIC8qKlxuICAgICAqIEBwYXJhbSAkY29udHJvbHNcbiAgICAgKiBAcmV0dXJucyB7T2JqZWN0fSAtIGxpc3Qgb2YgcnVsZXNcbiAgICAgKiBAcHJpdmF0ZVxuICAgICAqL1xuICAgIF9nZXRSZXN0cmljdGlvbkZyb21IdG1sKCRjb250cm9scyl7XG4gICAgICAgIGNvbnN0IHNlbGYgPSB0aGlzO1xuICAgICAgICBsZXQgcmVzdHJpY3Rpb24gPSB7fTtcblxuICAgICAgICAkY29udHJvbHMuZWFjaCggKGluZGV4LCBpdGVtKT0+e1xuICAgICAgICAgICAgY29uc3QgJGl0ZW0gPSAkKGl0ZW0pO1xuICAgICAgICAgICAgY29uc3QgbmFtZUZpZWxkID0gJGl0ZW0uYXR0cignbmFtZScpO1xuICAgICAgICAgICAgY29uc3QgcG9zc2libGVSZXN0cmljdCA9IHNlbGYubWVzc2FnZXM7XG5cbiAgICAgICAgICAgIGlmICghJGl0ZW0uYXR0cignY2xhc3MnKS5tYXRjaCgvX3Jlc3RyaWN0LS9pKSkgcmV0dXJuO1xuICAgICAgICAgICAgaWYgKCFyZXN0cmljdGlvbltuYW1lRmllbGRdKSByZXN0cmljdGlvbltuYW1lRmllbGRdID0ge307XG5cbiAgICAgICAgICAgIGZvcihsZXQgcmVzdHJpY3QgaW4gcG9zc2libGVSZXN0cmljdCl7XG4gICAgICAgICAgICAgICAgY29uc3QgcmVzdHJpY3RDbGFzcyA9IGBfdmFsaWRhdGUtJHtyZXN0cmljdH1gO1xuXG4gICAgICAgICAgICAgICAgaWYgKCRpdGVtLmhhc0NsYXNzKHJlc3RyaWN0Q2xhc3MpKXtcbiAgICAgICAgICAgICAgICAgICAgcmVzdHJpY3Rpb25bbmFtZUZpZWxkXVtyZXN0cmljdF0gPSB0cnVlO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgIH1cbiAgICAgICAgfSk7XG4gICAgICAgIHJldHVybiByZXN0cmljdGlvbjtcbiAgICB9XG5cbiAgICBfYXNzaWduRXZlbnRzKCkge1xuICAgICAgICB0aGlzLiRjb250cm9sc1xuICAgICAgICAgICAgLm9uKCdmb2N1cycsIHRoaXMuX29uRm9jdXNDb250cm9sLmJpbmQodGhpcykpXG4gICAgICAgICAgICAub24oJ2JsdXInLCB0aGlzLl9vbkJsdXJDb250cm9sLmJpbmQodGhpcykpXG4gICAgICAgICAgICAub24oJ2lucHV0JywgdGhpcy5fb25JbnB1dENvbnRyb2wuYmluZCh0aGlzKSlcbiAgICB9XG5cbiAgICBfb25Gb2N1c0NvbnRyb2woZSl7XG4gICAgICAgIGNvbnN0ICRlbCA9ICQoZS5jdXJyZW50VGFyZ2V0KTtcbiAgICB9XG5cbiAgICBfb25CbHVyQ29udHJvbChlKXtcbiAgICAgICAgY29uc3QgJGVsID0gJChlLmN1cnJlbnRUYXJnZXQpO1xuICAgICAgICB0aGlzLl9pc1ZhbGlkQ29udHJvbCgkZWwpO1xuICAgIH1cblxuICAgIF9vbklucHV0Q29udHJvbChlKXtcbiAgICAgICAgY29uc3QgJGNvbnRyb2wgPSAkKGUuY3VycmVudFRhcmdldCk7XG4gICAgICAgIHRoaXMuX3JlbW92ZUVycm9yKCRjb250cm9sKTtcbiAgICAgICAgdGhpcy5fcmVzdHJpY3RJbnB1dCgkY29udHJvbCk7XG4gICAgfVxuXG4gICAgX2lzVmFsaWRDb250cm9sKCRjb250cm9sKXtcbiAgICAgICAgY29uc3QgdmFsaWRhdGlvbiA9IHRoaXMuX3ZhbGlkYXRlQ29udHJvbCgkY29udHJvbCk7XG5cbiAgICAgICAgaWYgKHZhbGlkYXRpb24uaXNWYWxpZCkge1xuICAgICAgICAgICAgdGhpcy5fcmVtb3ZlRXJyb3IoJGNvbnRyb2wpO1xuICAgICAgICAgICAgcmV0dXJuIHRydWU7XG4gICAgICAgIH1cblxuICAgICAgICB0aGlzLl9zZXRFcnJvcigkY29udHJvbCwgdmFsaWRhdGlvbi5tZXNzYWdlKTtcbiAgICAgICAgcmV0dXJuIGZhbHNlO1xuICAgIH1cblxuICAgIC8qKlxuICAgICAqIFZhbGlkYXRlIGdpdmVuIGNvbnRyb2xcbiAgICAgKiBAcGFyYW0ge2pRdWVyeX0gJGNvbnRyb2wgLSBlbGVtZW50XG4gICAgICogQHJldHVybnMge09iamVjdH0gPSBpc1ZhbGlkKEJvb2xlYW4pLCBtZXNzYWdlKFN0cmluZylcbiAgICAgKiBAcHJpdmF0ZVxuICAgICAqL1xuICAgIF92YWxpZGF0ZUNvbnRyb2woJGNvbnRyb2wpe1xuICAgICAgICBjb25zdCBuYW1lID0gJGNvbnRyb2wuYXR0cignbmFtZScpO1xuICAgICAgICBjb25zdCBydWxlcyA9IHRoaXMucnVsZXNbbmFtZV07XG4gICAgICAgIGNvbnN0IHZhbHVlQ29udHJvbCA9IHRoaXMuZ2V0Q29udHJvbFZhbHVlKCRjb250cm9sKTtcbiAgICAgICAgbGV0IHZhbGlkO1xuXG4gICAgICAgIGZvciAobGV0IHJ1bGUgaW4gcnVsZXMpe1xuICAgICAgICAgICAgdmFsaWQgPSB0aGlzW2Ake3J1bGV9VmFsaWRhdG9yYF0odmFsdWVDb250cm9sLCAkY29udHJvbCk7XG5cbiAgICAgICAgICAgIGlmICghdmFsaWQpIHJldHVybiB7XG4gICAgICAgICAgICAgICAgaXNWYWxpZDogZmFsc2UsXG4gICAgICAgICAgICAgICAgbWVzc2FnZTogdGhpcy5tZXNzYWdlc1tydWxlXVxuICAgICAgICAgICAgfTtcbiAgICAgICAgfVxuXG4gICAgICAgIHJldHVybiB7XG4gICAgICAgICAgICBpc1ZhbGlkOiB0cnVlXG4gICAgICAgIH07XG4gICAgfVxuXG4gICAgaXNWYWxpZEZvcm1EYXRhKCl7XG4gICAgICAgIGNvbnN0IHNlbGYgPSB0aGlzO1xuICAgICAgICBsZXQgdmFsaWQgPSB0cnVlO1xuXG4gICAgICAgIHRoaXMucmVtb3ZlRXJyb3JzKCk7XG4gICAgICAgIHRoaXMuJGNvbnRyb2xzLmVhY2goKGluZGV4LCBjb250cm9sKSA9PiB7XG4gICAgICAgICAgICBsZXQgaXNWYWxpZENvbnRyb2wgID0gc2VsZi5faXNWYWxpZENvbnRyb2woJChjb250cm9sKSk7XG4gICAgICAgICAgICB2YWxpZCA9IHZhbGlkICYmIGlzVmFsaWRDb250cm9sO1xuICAgICAgICB9KTtcbiAgICAgICAgXG4gICAgICAgIHJldHVybiB2YWxpZDtcbiAgICB9XG5cbiAgICBfcmVzdHJpY3RJbnB1dCgkY29udHJvbCl7XG4gICAgICAgIGNvbnN0IG5hbWUgPSAkY29udHJvbC5hdHRyKCduYW1lJyk7XG4gICAgICAgIGNvbnN0IHJlc3RyaWN0aW9uID0gdGhpcy5yZXN0cmljdGlvbltuYW1lXTtcbiAgICAgICAgbGV0IHZhbHVlID0gdGhpcy5nZXRDb250cm9sVmFsdWUoJGNvbnRyb2wpO1xuXG4gICAgICAgIGlmICghcmVzdHJpY3Rpb24pIHJldHVybjtcblxuICAgICAgICBmb3IgKGxldCByZXN0aWN0IGluIHJlc3RyaWN0aW9uKXtcbiAgICAgICAgICAgIHZhbHVlID0gdGhpc1tgJHtyZXN0aWN0fVJlc3RyaWN0YF0odmFsdWUpO1xuICAgICAgICB9XG4gICAgICAgIHRoaXMuc2V0Q29udHJvbFZhbHVlKCRjb250cm9sLCB2YWx1ZSk7XG4gICAgfVxuXG4gICAgLyoqXG4gICAgICogU2hvdyBvciBoaWRlIGxhc3QgZXJyb3JcbiAgICAgKiBAcGFyYW0ge0Jvb2xlYW59IGNvbmRpdGlvblxuICAgICAqIEBwYXJhbSB7alF1ZXJ5fSAkY29udHJvbFxuICAgICAqIEBwcml2YXRlXG4gICAgICovXG4gICAgX3Nob3dQcmV2aW91c0Vycm9yKGNvbmRpdGlvbiwgJGNvbnRyb2wgPSBudWxsKXtcbiAgICAgICAgaWYgKHRoaXMuJGlucHV0V2l0aEVycm9yKSB7XG4gICAgICAgICAgICB0aGlzLiRpbnB1dFdpdGhFcnJvclxuICAgICAgICAgICAgICAgIC5wYXJlbnQoKVxuICAgICAgICAgICAgICAgIC50b2dnbGVDbGFzcygnYi1lcnJvcl9zdGF0ZV9oaWdoJywgIWNvbmRpdGlvbilcbiAgICAgICAgICAgICAgICAudG9nZ2xlQ2xhc3MoJ2ItZXJyb3Jfc3RhdGVfZXJyb3InLCBjb25kaXRpb24pXG4gICAgICAgIH1cbiAgICAgICAgdGhpcy4kaW5wdXRXaXRoRXJyb3IgPSAkY29udHJvbDtcbiAgICB9XG5cbiAgICAvKipcbiAgICAgKiBTZXQgZXJyb3IgZm9yIGNvbnRyb2xcbiAgICAgKiBAcGFyYW0ge2pRdWVyeX0gJGNvbnRyb2xcbiAgICAgKiBAcGFyYW0ge1N0cmluZ30gZXJyb3JUZXh0XG4gICAgICogQHBhcmFtIHtCb29sZWFufSBzaG93QnViYmxlXG4gICAgICovXG4gICAgX3NldEVycm9yKCRjb250cm9sLCBlcnJvclRleHQsIHNob3dCdWJibGUgPSB0cnVlKSB7XG4gICAgICAgIGNvbnN0ICRwYXJlbnQgPSAkY29udHJvbC5wYXJlbnQoKTtcbiAgICAgICAgY29uc3QgJGVycm9yID0gJHBhcmVudC5maW5kKCcuYi1lcnJvcicpO1xuXG4gICAgICAgIGlmICgkZXJyb3IubGVuZ3RoKSB7XG4gICAgICAgICAgICAkZXJyb3IudGV4dChlcnJvclRleHQpO1xuICAgICAgICB9IGVsc2Uge1xuICAgICAgICAgICAgJCgnPGRpdiBjbGFzcz1cImItZXJyb3JcIiAvPicpXG4gICAgICAgICAgICAgICAgLnRleHQoZXJyb3JUZXh0KVxuICAgICAgICAgICAgICAgIC5hcHBlbmRUbygkcGFyZW50KTtcbiAgICAgICAgfVxuXG4gICAgICAgICRwYXJlbnQuYWRkQ2xhc3Moc2hvd0J1YmJsZT8nYi1lcnJvcl9zdGF0ZV9lcnJvcic6ICdiLWVycm9yX3N0YXRlX2hpZ2gnKTtcblxuICAgICAgICB0aGlzLmVycm9ycy5wdXNoKHtcbiAgICAgICAgICAgIG5hbWU6ICRjb250cm9sLmF0dHIoJ25hbWUnKSxcbiAgICAgICAgICAgIGVycm9yOiBlcnJvclRleHRcbiAgICAgICAgfSlcbiAgICB9XG5cbiAgICBfcmVtb3ZlRXJyb3IoJGNvbnRyb2wpe1xuICAgICAgICBjb25zdCAkcGFyZW50ID0gJGNvbnRyb2wucGFyZW50KCk7XG5cbiAgICAgICAgJHBhcmVudC5yZW1vdmVDbGFzcygnYi1lcnJvcl9zdGF0ZV9lcnJvciBiLWVycm9yX3N0YXRlX2hpZ2gnKVxuXG4gICAgICAgIHRoaXMuZXJyb3JzID0gdGhpcy5lcnJvcnMuZmlsdGVyKGZ1bmN0aW9uIChpdGVtKSB7XG4gICAgICAgICAgICByZXR1cm4gaXRlbS5uYW1lICE9PSAkY29udHJvbC5hdHRyKCduYW1lJylcbiAgICAgICAgfSlcbiAgICB9ICAgIFxuXG4gICAgLyoqXG4gICAgICogU2V0IGVycm9yc1xuICAgICAqIEBwYXJhbSB7QXJyYXl9IGVycm9ycyAtIFt7bmFtZTogXCJlbWFpbFwiLCBlcnJvcjogXCJlbXB0eVwifSwge25hbWU6IFwicGFzc3dvcmRcIiwgZXJyb3I6IFwiZW1wdHlcIn1dXG4gICAgICovXG4gICAgc2V0RXJyb3JzKGVycm9ycykge1xuICAgICAgICB0aGlzLiRpbnB1dFdpdGhFcnJvciA9IG51bGw7XG4gICAgICAgIGxldCBpbmRleCA9IDA7XG5cbiAgICAgICAgZXJyb3JzLmZvckVhY2goKGl0ZW0pID0+IHtcbiAgICAgICAgICAgIGNvbnN0ICRjdXJyZW50Q29udHJvbCA9IHRoaXMuJGNvbnRyb2xzLmZpbHRlcignW25hbWU9XCInICsgaXRlbS5uYW1lICsgJ1wiXScpLmZpcnN0KCk7XG5cbiAgICAgICAgICAgIGlmICghJGN1cnJlbnRDb250cm9sLmxlbmd0aCkgcmV0dXJuO1xuXG4gICAgICAgICAgICBpZiAoaW5kZXggPT0gMCl7XG4gICAgICAgICAgICAgICAgdGhpcy5fc2V0RXJyb3IoJGN1cnJlbnRDb250cm9sLCBpdGVtLmVycm9yKTtcbiAgICAgICAgICAgICAgICAkKCdodG1sLCBib2R5JykuYW5pbWF0ZSh7XG4gICAgICAgICAgICAgICAgICAgIHNjcm9sbFRvcDogJGN1cnJlbnRDb250cm9sLm9mZnNldCgpLnRvcCAtIDUwXG4gICAgICAgICAgICAgICAgfSwgNDAwKVxuICAgICAgICAgICAgfSBlbHNlIHtcbiAgICAgICAgICAgICAgICB0aGlzLl9zZXRFcnJvcigkY3VycmVudENvbnRyb2wsIGl0ZW0uZXJyb3IsIGZhbHNlKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgfSlcbiAgICB9XG5cbiAgICByZW1vdmVFcnJvcnMoKSB7XG4gICAgICAgIHRoaXMuJGNvbnRyb2xzLmVhY2goKGluZGV4LCBlbCkgPT4ge1xuICAgICAgICAgICAgY29uc3QgJGVsID0gJChlbCk7XG4gICAgICAgICAgICB0aGlzLl9yZW1vdmVFcnJvcigkZWwpXG4gICAgICAgIH0pXG4gICAgfVxuICAgIFxuICAgIC8vIHZhbGlkYXRvcnNcbiAgICByZXF1aXJlZFZhbGlkYXRvcih2YWx1ZSwgJGVsKXtcbiAgICAgICAgaWYgKCRlbC5pcygnc2VsZWN0JykpIHtcbiAgICAgICAgICAgIHZhciB2YWwgPSAkZWwudmFsKCk7XG4gICAgICAgICAgICByZXR1cm4gdmFsICYmIHZhbC5sZW5ndGggPiAwO1xuICAgICAgICB9XG4gICAgICAgIHJldHVybiB2YWx1ZS5sZW5ndGggPiAwO1xuICAgIH1cblxuICAgIGVtYWlsVmFsaWRhdG9yKHZhbHVlLCAkZWwpIHtcbiAgICAgICAgcmV0dXJuIC9eW2EtekEtWjAtOS4hIyQlJicqK1xcLz0/Xl9ge3x9fi1dK0BbYS16QS1aMC05XSg/OlthLXpBLVowLTktXXswLDYxfVthLXpBLVowLTldKT8oPzpcXC5bYS16QS1aMC05XSg/OlthLXpBLVowLTktXXswLDYxfVthLXpBLVowLTldKT8pKiQvLnRlc3QodmFsdWUpO1xuICAgIH1cblxuICAgIHVybFZhbGlkYXRvcih2YWx1ZSwgJGVsKSB7XG4gICAgICAgIHJldHVybiAvXig/Oig/Oig/Omh0dHBzP3xmdHApOik/XFwvXFwvKSg/OlxcUysoPzo6XFxTKik/QCk/KD86KD8hKD86MTB8MTI3KSg/OlxcLlxcZHsxLDN9KXszfSkoPyEoPzoxNjlcXC4yNTR8MTkyXFwuMTY4KSg/OlxcLlxcZHsxLDN9KXsyfSkoPyExNzJcXC4oPzoxWzYtOV18MlxcZHwzWzAtMV0pKD86XFwuXFxkezEsM30pezJ9KSg/OlsxLTldXFxkP3wxXFxkXFxkfDJbMDFdXFxkfDIyWzAtM10pKD86XFwuKD86MT9cXGR7MSwyfXwyWzAtNF1cXGR8MjVbMC01XSkpezJ9KD86XFwuKD86WzEtOV1cXGQ/fDFcXGRcXGR8MlswLTRdXFxkfDI1WzAtNF0pKXwoPzooPzpbYS16XFx1MDBhMS1cXHVmZmZmMC05XS0qKSpbYS16XFx1MDBhMS1cXHVmZmZmMC05XSspKD86XFwuKD86W2EtelxcdTAwYTEtXFx1ZmZmZjAtOV0tKikqW2EtelxcdTAwYTEtXFx1ZmZmZjAtOV0rKSooPzpcXC4oPzpbYS16XFx1MDBhMS1cXHVmZmZmXXsyLH0pKS4/KSg/OjpcXGR7Miw1fSk/KD86Wy8/I11cXFMqKT8kL2kudGVzdCh2YWx1ZSk7XG4gICAgfVxuXG4gICAgZGF0ZVZhbGlkYXRvcih2YWx1ZSwgJGVsKSB7XG4gICAgICAgIHJldHVybiAhL0ludmFsaWR8TmFOLy50ZXN0KG5ldyBEYXRlKHZhbHVlKS50b1N0cmluZygpKTtcbiAgICB9XG5cbiAgICBkYXRlaXNvVmFsaWRhdG9yKHZhbHVlLCAkZWwpIHtcbiAgICAgICAgcmV0dXJuIC9eXFxkezR9W1xcL1xcLV0oMD9bMS05XXwxWzAxMl0pW1xcL1xcLV0oMD9bMS05XXxbMTJdWzAtOV18M1swMV0pJC8udGVzdCh2YWx1ZSk7XG4gICAgfVxuXG4gICAgLy8gcmVzdHJpY3Rpb25cbiAgICBkaWdpc3RzUmVzdHJpY3QodmFsdWUpe1xuICAgICAgICByZXR1cm4gdmFsdWUucmVwbGFjZSgvW15cXGRdKy9nLCAnJyk7XG4gICAgfVxuXG4gICAgbm9zcGFjZVJlc3RyaWN0KHZhbHVlKXtcbiAgICAgICAgcmV0dXJuIHZhbHVlLnJlcGxhY2UoL1xccy9nLCAnJyk7XG4gICAgfVxuXG4gICAgLy8gSGVscGVyIGZvciBmb3JtIFxuICAgIGdldEZvcm1EYXRhKCl7XG4gICAgICAgIGxldCBmb3JtRGF0YSA9IHt9O1xuXG4gICAgICAgIHRoaXMuJGNvbnRyb2xzLmVhY2goKGluZGV4LCBlbCkgPT4ge1xuICAgICAgICAgICAgY29uc3QgJGVsID0gJChlbCk7XG4gICAgICAgICAgICBjb25zdCBuYW1lID0gJGVsLmF0dHIoJ25hbWUnKTtcblxuICAgICAgICAgICAgaWYgKG5hbWUpIHtcbiAgICAgICAgICAgICAgICBmb3JtRGF0YVtuYW1lXSA9IHRoaXMuZ2V0Q29udHJvbFZhbHVlKCRlbClcbiAgICAgICAgICAgIH1cbiAgICAgICAgfSk7XG5cbiAgICAgICAgcmV0dXJuIGZvcm1EYXRhO1xuICAgIH1cblxuICAgIHNldEZvcm1EYXRhKGZvcm1EYXRhKXtcbiAgICAgICAgY29uc3QgJGNvbnRyb2xzID0gdGhpcy4kY29udHJvbHM7XG5cbiAgICAgICAgZm9yKCBsZXQgZmllbGQgaW4gZm9ybURhdGEpe1xuICAgICAgICAgICAgaWYgKGZvcm1EYXRhLmhhc093blByb3BlcnR5KGZpZWxkKSl7XG4gICAgICAgICAgICAgICAgbGV0ICRjb250cm9sID0gJGNvbnRyb2xzLmZpbHRlcihgW25hbWU9XCIke2ZpZWxkfVwiXWApLmZpcnN0KCk7XG5cbiAgICAgICAgICAgICAgICBpZiAoISRjb250cm9sLmxlbmd0aCkgcmV0dXJuO1xuXG4gICAgICAgICAgICAgICAgdGhpcy5zZXRDb250cm9sVmFsdWUoJGNvbnRyb2wsIGRhdGFbZmllbGRdKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgfVxuICAgIH1cblxuICAgIGNsZWFyRm9ybSgpIHtcbiAgICAgICAgdGhpcy4kY29udHJvbHMuZWFjaCgoaW5kZXgsIGVsKSA9PiB7XG4gICAgICAgICAgICBjb25zdCAkZWwgPSAkKGVsKTtcbiAgICAgICAgICAgIGlmICghJGVsLmF0dHIoXCJkaXNhYmxlZFwiKSkgICRlbC52YWwoJycpO1xuICAgICAgICB9KVxuICAgIH1cblxuICAgIC8qKlxuICAgICAqIFVuaXZlcnNhbCBhc3NpZ24gdmFsdWVcbiAgICAgKiBAcGFyYW0ge2pRdWVyeX0gJGNvbnRyb2xcbiAgICAgKiBAcGFyYW0ge1N0cmluZ3xOdW1iZXJ8Qm9vbGVhbn0gdmFsdWVcbiAgICAgKi9cbiAgICBzZXRDb250cm9sVmFsdWUoJGNvbnRyb2wsIHZhbHVlKXtcbiAgICAgICAgaWYgKCRjb250cm9sLmlzKCc6Y2hlY2tib3gnKSl7XG4gICAgICAgICAgICAkY29udHJvbC5wcm9wKCdjaGVja2VkJywgdmFsdWUpXG4gICAgICAgIH0gZWxzZXtcbiAgICAgICAgICAgICRjb250cm9sLnZhbCh2YWx1ZSk7XG4gICAgICAgIH1cbiAgICB9XG5cbiAgICAvKipcbiAgICAgKiBVbml2ZXJzYWwgZ2V0IHZhbHVlIGhlbHBlclxuICAgICAqIEBwYXJhbSB7alF1ZXJ5fSAkY29udHJvbFxuICAgICAqIEByZXR1cm5zIHtTdHJpbmd8Qm9vbGVhbn1cbiAgICAgKi9cbiAgICBnZXRDb250cm9sVmFsdWUoJGNvbnRyb2wpe1xuICAgICAgICBsZXQgdmFsdWUgPSBudWxsO1xuXG4gICAgICAgIGlmICgkY29udHJvbC5pcygnOmNoZWNrYm94JykpIHtcbiAgICAgICAgICAgIHZhbHVlID0gJGNvbnRyb2wucHJvcCgnY2hlY2tlZCcpO1xuICAgICAgICB9IGVsc2Uge1xuICAgICAgICAgICAgdmFsdWUgPSAkY29udHJvbC52YWwoKTtcbiAgICAgICAgfVxuXG4gICAgICAgIHJldHVybiB2YWx1ZTtcbiAgICB9XG59XG5cblxuXG4vKiogV0VCUEFDSyBGT09URVIgKipcbiAqKiAuL2Zyb250ZW5kL2pzL2NvbW1vbi9fZm9ybS1oZWxwZXJzLmpzXG4gKiovIl0sInNvdXJjZVJvb3QiOiIifQ==