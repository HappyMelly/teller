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
	
	var _emailConnect = __webpack_require__(52);
	
	var _emailConnect2 = _interopRequireDefault(_emailConnect);
	
	var _socialConnect = __webpack_require__(53);
	
	var _socialConnect2 = _interopRequireDefault(_socialConnect);
	
	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }
	
	$(function () {
	    _socialConnect2.default.plugin('.js-social-connect');
	
	    _emailConnect2.default.plugin('#dlg-email-connect', {
	        url: jsRoutes.controllers.core.UserAccounts.handleNewPassword().url
	    }).on('hmt.emailconnect.success', function (e) {
	        $('.js-email-connect').addClass('show_connected');
	        success("You created new email account");
	    });
	
	    _emailConnect2.default.plugin('#dlg-change-password', {
	        url: jsRoutes.controllers.core.UserAccounts.changePassword().url
	    }).on('hmt.emailconnect.success', function (e) {
	        var msg = "Your password was successfully updated";
	        success(msg);
	    });
	
	    _emailConnect2.default.plugin('#dlg-change-email', {
	        url: jsRoutes.controllers.core.UserAccounts.changeEmail().url
	    }).on('hmt.emailconnect.success', function (e) {
	        var msg = "Please check your mailbox and click a confirmation link to complete an email change process";
	        success(msg);
	    });
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
	
	var _formHelper = __webpack_require__(23);
	
	var _formHelper2 = _interopRequireDefault(_formHelper);
	
	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }
	
	var Widget = function () {
	    function Widget(selector, options) {
	        (0, _classCallCheck3.default)(this, Widget);
	
	        this.$root = $(selector);
	        this.locals = this._getDom();
	        this.options = $.extend({}, options, this.$root.data());
	
	        this.validation = new _formHelper2.default(this.locals.$inputs);
	        this._assignEvents();
	    }
	
	    (0, _createClass3.default)(Widget, [{
	        key: '_getDom',
	        value: function _getDom() {
	            var $root = this.$root;
	            return {
	                $form: $root.find('form'),
	                $inputs: $root.find('form input')
	            };
	        }
	    }, {
	        key: '_assignEvents',
	        value: function _assignEvents() {
	            this.locals.$form.on('submit', this._onSubmitForm.bind(this));
	            this.$root.on('hide.bs.modal', this._onHideModal.bind(this));
	        }
	    }, {
	        key: '_onSubmitForm',
	        value: function _onSubmitForm(e) {
	            var self = this;
	            e.preventDefault();
	
	            if (!self.validation.isValidInputs()) return;
	
	            var formData = self.validation.getFormData();
	            self._sendData(formData).done(function () {
	                self.$root.modal('hide');
	                self.validation.clearForm();
	
	                self.$root.trigger('hmt.emailconnect.success');
	            }).fail(function (response) {
	                var data = $.parseJSON(response.responseText).data;
	
	                if (!data.errors) return;
	                self.validation.setErrors(data.errors);
	            });
	        }
	    }, {
	        key: '_onHideModal',
	        value: function _onHideModal() {
	            this.validation.clearForm();
	            this.validation.removeErrors();
	        }
	    }, {
	        key: '_sendData',
	        value: function _sendData(data) {
	            return $.post(this.options.url, data);
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
	
	var _formHelper = __webpack_require__(23);
	
	var _formHelper2 = _interopRequireDefault(_formHelper);
	
	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }
	
	var Widget = function () {
	    function Widget(selector, options) {
	        (0, _classCallCheck3.default)(this, Widget);
	
	        this.$root = $(selector);
	        this.options = $.extend({}, options, this.$root.data());
	
	        this._assignEvents();
	    }
	
	    (0, _createClass3.default)(Widget, [{
	        key: '_assignEvents',
	        value: function _assignEvents() {
	            this.$root.on('click', '[data-social-connect]', this._onClickConnect.bind(this));
	        }
	    }, {
	        key: '_onClickConnect',
	        value: function _onClickConnect(e) {
	            e.preventDefault();
	            var $root = $(e.currentTarget).closest('.b-connect-i');
	
	            this.toggleConnect($root);
	        }
	    }, {
	        key: '_setConnect',
	        value: function _setConnect($el) {
	            window.location = $el.data('url');
	        }
	    }, {
	        key: '_unSetConnect',
	        value: function _unSetConnect($el) {
	            var socialType = $el.data('social');
	            var url = jsRoutes.controllers.core.UserAccounts.disconnect(socialType).url;
	
	            $.post(url, {}, function (data) {
	                $el.removeClass('state-complete');
	                success(data.message);
	            }, "json").fail(function (jqXHR, textStatus, errorThrown) {
	                var response = JSON.parse(jqXHR.responseText);
	                error(response.message);
	            });
	        }
	    }, {
	        key: 'toggleConnect',
	        value: function toggleConnect($el) {
	            if ($el.hasClass('state-complete')) {
	                this._unSetConnect($el);
	            } else {
	                this._setConnect($el);
	            }
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
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIndlYnBhY2s6Ly8vd2VicGFjay9ib290c3RyYXAgZjBmMTY2OTBiZDk3YTI1NWNmYjk/N2VmNCoqKioqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL2hlbHBlcnMvY2xhc3NDYWxsQ2hlY2suanM/MjFhZioqKioqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL2hlbHBlcnMvY3JlYXRlQ2xhc3MuanM/MWRmZSoqKioqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL2NvcmUtanMvb2JqZWN0L2RlZmluZS1wcm9wZXJ0eS5qcz81ZjcwKioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvZm4vb2JqZWN0L2RlZmluZS1wcm9wZXJ0eS5qcz80ZDMzKioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9lczYub2JqZWN0LmRlZmluZS1wcm9wZXJ0eS5qcz84YmRlKioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZXhwb3J0LmpzPzhlNDAqKioqKioqIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19nbG9iYWwuanM/M2M1MioqKioqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2NvcmUuanM/ZDYxMSoqKioqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2N0eC5qcz80ZTU5KioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fYS1mdW5jdGlvbi5qcz8wNjk5KioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9faGlkZS5qcz8wZDJlKioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fb2JqZWN0LWRwLmpzPzU5ODYqKioqKioqIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19hbi1vYmplY3QuanM/M2FmMioqKioqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2lzLW9iamVjdC5qcz9jZmRhKioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9faWU4LWRvbS1kZWZpbmUuanM/YjEwMioqKioqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2Rlc2NyaXB0b3JzLmpzP2MwZjUqKioqKioqIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19mYWlscy5qcz9jNmRkKioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZG9tLWNyZWF0ZS5qcz82MTJmKioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fdG8tcHJpbWl0aXZlLmpzPzFhNjUqKioqKioqIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19wcm9wZXJ0eS1kZXNjLmpzPzI1NmIqKioqKioqIiwid2VicGFjazovLy8uL2Zyb250ZW5kL2pzL2NvbW1vbi9fZm9ybS1oZWxwZXIuanM/ODYzNioqKiIsIndlYnBhY2s6Ly8vLi9mcm9udGVuZC9qcy9wZXJzb24vbWFuYWdlLWFjY291bnQuanMiLCJ3ZWJwYWNrOi8vLy4vZnJvbnRlbmQvanMvcGVyc29uL21hbmFnZS13aWRnZXRzL19lbWFpbC1jb25uZWN0LmpzIiwid2VicGFjazovLy8uL2Zyb250ZW5kL2pzL3BlcnNvbi9tYW5hZ2Utd2lkZ2V0cy9fc29jaWFsLWNvbm5lY3QuanMiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IjtBQUFBO0FBQ0E7O0FBRUE7QUFDQTs7QUFFQTtBQUNBO0FBQ0E7O0FBRUE7QUFDQTtBQUNBLHVCQUFlO0FBQ2Y7QUFDQTtBQUNBOztBQUVBO0FBQ0E7O0FBRUE7QUFDQTs7QUFFQTtBQUNBO0FBQ0E7OztBQUdBO0FBQ0E7O0FBRUE7QUFDQTs7QUFFQTtBQUNBOztBQUVBO0FBQ0E7Ozs7Ozs7Ozs7Ozs7Ozs7QUN0Q0E7O0FBRUE7O0FBRUE7QUFDQTtBQUNBO0FBQ0E7QUFDQSxHOzs7Ozs7QUNSQTs7QUFFQTs7QUFFQTs7QUFFQTs7QUFFQSx1Q0FBc0MsdUNBQXVDLGdCQUFnQjs7QUFFN0Y7QUFDQTtBQUNBLG9CQUFtQixrQkFBa0I7QUFDckM7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7O0FBRUE7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBLEVBQUMsRzs7Ozs7O0FDMUJELG1CQUFrQix1RDs7Ozs7O0FDQWxCO0FBQ0E7QUFDQTtBQUNBO0FBQ0EsRzs7Ozs7O0FDSkE7QUFDQTtBQUNBLHNFQUF1RSwwQ0FBMEMsRTs7Ozs7O0FDRmpIO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7O0FBRUE7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQSxvRUFBbUU7QUFDbkU7QUFDQSxzRkFBcUY7QUFDckY7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBLFlBQVc7QUFDWCxVQUFTO0FBQ1Q7QUFDQTtBQUNBO0FBQ0E7QUFDQSxN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QUMzQixxQkFBSSxXQUFKLENBQWdCLGdCQUFoQixFQUQyQjtBQUUzQix5QkFBUSxLQUFLLE9BQUwsQ0FBUixDQUYyQjtjQUFmLEVBR2IsTUFISCxFQUdXLElBSFgsQ0FHZ0IsVUFBUyxLQUFULEVBQWdCLFVBQWhCLEVBQTRCLFdBQTVCLEVBQXlDO0FBQ3JELHFCQUFJLFdBQVcsS0FBSyxLQUFMLENBQVcsTUFBTSxZQUFOLENBQXRCLENBRGlEO0FBRXJELHVCQUFNLFNBQVMsT0FBVCxDQUFOLENBRnFEO2NBQXpDLENBSGhCLENBSmM7Ozs7dUNBYUosS0FBSTtBQUNkLGlCQUFJLElBQUksUUFBSixDQUFhLGdCQUFiLENBQUosRUFBbUM7QUFDL0Isc0JBQUssYUFBTCxDQUFtQixHQUFuQixFQUQrQjtjQUFuQyxNQUVPO0FBQ0gsc0JBQUssV0FBTCxDQUFpQixHQUFqQixFQURHO2NBRlA7Ozs7Ozs7Z0NBUVUsVUFBVSxTQUFTO0FBQzdCLGlCQUFNLFNBQVMsRUFBRSxRQUFGLENBQVQsQ0FEdUI7QUFFN0IsaUJBQUksQ0FBQyxPQUFPLE1BQVAsRUFBZSxPQUFwQjs7QUFFQSxvQkFBTyxPQUFPLElBQVAsQ0FBWSxVQUFVLEtBQVYsRUFBaUIsRUFBakIsRUFBcUI7QUFDcEMscUJBQUksV0FBVyxFQUFFLEVBQUYsQ0FBWCxDQURnQztBQUVwQyxxQkFBSSxPQUFXLFNBQVMsSUFBVCxDQUFjLGlCQUFkLENBQVgsQ0FGZ0M7O0FBSXBDLHFCQUFJLENBQUMsSUFBRCxFQUFPO0FBQ1AsNEJBQU8sSUFBSSxNQUFKLENBQVcsRUFBWCxFQUFlLE9BQWYsQ0FBUCxDQURPO0FBRVAsOEJBQVMsSUFBVCxDQUFjLFFBQWQsRUFBd0IsSUFBeEIsRUFGTztrQkFBWDtjQUplLENBQW5CLENBSjZCOzs7WUE3Q2hCIiwiZmlsZSI6Im1hbmFnZS1hY2NvdW50LmpzIiwic291cmNlc0NvbnRlbnQiOlsiIFx0Ly8gVGhlIG1vZHVsZSBjYWNoZVxuIFx0dmFyIGluc3RhbGxlZE1vZHVsZXMgPSB7fTtcblxuIFx0Ly8gVGhlIHJlcXVpcmUgZnVuY3Rpb25cbiBcdGZ1bmN0aW9uIF9fd2VicGFja19yZXF1aXJlX18obW9kdWxlSWQpIHtcblxuIFx0XHQvLyBDaGVjayBpZiBtb2R1bGUgaXMgaW4gY2FjaGVcbiBcdFx0aWYoaW5zdGFsbGVkTW9kdWxlc1ttb2R1bGVJZF0pXG4gXHRcdFx0cmV0dXJuIGluc3RhbGxlZE1vZHVsZXNbbW9kdWxlSWRdLmV4cG9ydHM7XG5cbiBcdFx0Ly8gQ3JlYXRlIGEgbmV3IG1vZHVsZSAoYW5kIHB1dCBpdCBpbnRvIHRoZSBjYWNoZSlcbiBcdFx0dmFyIG1vZHVsZSA9IGluc3RhbGxlZE1vZHVsZXNbbW9kdWxlSWRdID0ge1xuIFx0XHRcdGV4cG9ydHM6IHt9LFxuIFx0XHRcdGlkOiBtb2R1bGVJZCxcbiBcdFx0XHRsb2FkZWQ6IGZhbHNlXG4gXHRcdH07XG5cbiBcdFx0Ly8gRXhlY3V0ZSB0aGUgbW9kdWxlIGZ1bmN0aW9uXG4gXHRcdG1vZHVsZXNbbW9kdWxlSWRdLmNhbGwobW9kdWxlLmV4cG9ydHMsIG1vZHVsZSwgbW9kdWxlLmV4cG9ydHMsIF9fd2VicGFja19yZXF1aXJlX18pO1xuXG4gXHRcdC8vIEZsYWcgdGhlIG1vZHVsZSBhcyBsb2FkZWRcbiBcdFx0bW9kdWxlLmxvYWRlZCA9IHRydWU7XG5cbiBcdFx0Ly8gUmV0dXJuIHRoZSBleHBvcnRzIG9mIHRoZSBtb2R1bGVcbiBcdFx0cmV0dXJuIG1vZHVsZS5leHBvcnRzO1xuIFx0fVxuXG5cbiBcdC8vIGV4cG9zZSB0aGUgbW9kdWxlcyBvYmplY3QgKF9fd2VicGFja19tb2R1bGVzX18pXG4gXHRfX3dlYnBhY2tfcmVxdWlyZV9fLm0gPSBtb2R1bGVzO1xuXG4gXHQvLyBleHBvc2UgdGhlIG1vZHVsZSBjYWNoZVxuIFx0X193ZWJwYWNrX3JlcXVpcmVfXy5jID0gaW5zdGFsbGVkTW9kdWxlcztcblxuIFx0Ly8gX193ZWJwYWNrX3B1YmxpY19wYXRoX19cbiBcdF9fd2VicGFja19yZXF1aXJlX18ucCA9IFwiXCI7XG5cbiBcdC8vIExvYWQgZW50cnkgbW9kdWxlIGFuZCByZXR1cm4gZXhwb3J0c1xuIFx0cmV0dXJuIF9fd2VicGFja19yZXF1aXJlX18oMCk7XG5cblxuXG4vKiogV0VCUEFDSyBGT09URVIgKipcbiAqKiB3ZWJwYWNrL2Jvb3RzdHJhcCBmMGYxNjY5MGJkOTdhMjU1Y2ZiOVxuICoqLyIsIlwidXNlIHN0cmljdFwiO1xuXG5leHBvcnRzLl9fZXNNb2R1bGUgPSB0cnVlO1xuXG5leHBvcnRzLmRlZmF1bHQgPSBmdW5jdGlvbiAoaW5zdGFuY2UsIENvbnN0cnVjdG9yKSB7XG4gIGlmICghKGluc3RhbmNlIGluc3RhbmNlb2YgQ29uc3RydWN0b3IpKSB7XG4gICAgdGhyb3cgbmV3IFR5cGVFcnJvcihcIkNhbm5vdCBjYWxsIGEgY2xhc3MgYXMgYSBmdW5jdGlvblwiKTtcbiAgfVxufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL2hlbHBlcnMvY2xhc3NDYWxsQ2hlY2suanNcbiAqKiBtb2R1bGUgaWQgPSAzXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJcInVzZSBzdHJpY3RcIjtcblxuZXhwb3J0cy5fX2VzTW9kdWxlID0gdHJ1ZTtcblxudmFyIF9kZWZpbmVQcm9wZXJ0eSA9IHJlcXVpcmUoXCJiYWJlbC1ydW50aW1lL2NvcmUtanMvb2JqZWN0L2RlZmluZS1wcm9wZXJ0eVwiKTtcblxudmFyIF9kZWZpbmVQcm9wZXJ0eTIgPSBfaW50ZXJvcFJlcXVpcmVEZWZhdWx0KF9kZWZpbmVQcm9wZXJ0eSk7XG5cbmZ1bmN0aW9uIF9pbnRlcm9wUmVxdWlyZURlZmF1bHQob2JqKSB7IHJldHVybiBvYmogJiYgb2JqLl9fZXNNb2R1bGUgPyBvYmogOiB7IGRlZmF1bHQ6IG9iaiB9OyB9XG5cbmV4cG9ydHMuZGVmYXVsdCA9IGZ1bmN0aW9uICgpIHtcbiAgZnVuY3Rpb24gZGVmaW5lUHJvcGVydGllcyh0YXJnZXQsIHByb3BzKSB7XG4gICAgZm9yICh2YXIgaSA9IDA7IGkgPCBwcm9wcy5sZW5ndGg7IGkrKykge1xuICAgICAgdmFyIGRlc2NyaXB0b3IgPSBwcm9wc1tpXTtcbiAgICAgIGRlc2NyaXB0b3IuZW51bWVyYWJsZSA9IGRlc2NyaXB0b3IuZW51bWVyYWJsZSB8fCBmYWxzZTtcbiAgICAgIGRlc2NyaXB0b3IuY29uZmlndXJhYmxlID0gdHJ1ZTtcbiAgICAgIGlmIChcInZhbHVlXCIgaW4gZGVzY3JpcHRvcikgZGVzY3JpcHRvci53cml0YWJsZSA9IHRydWU7XG4gICAgICAoMCwgX2RlZmluZVByb3BlcnR5Mi5kZWZhdWx0KSh0YXJnZXQsIGRlc2NyaXB0b3Iua2V5LCBkZXNjcmlwdG9yKTtcbiAgICB9XG4gIH1cblxuICByZXR1cm4gZnVuY3Rpb24gKENvbnN0cnVjdG9yLCBwcm90b1Byb3BzLCBzdGF0aWNQcm9wcykge1xuICAgIGlmIChwcm90b1Byb3BzKSBkZWZpbmVQcm9wZXJ0aWVzKENvbnN0cnVjdG9yLnByb3RvdHlwZSwgcHJvdG9Qcm9wcyk7XG4gICAgaWYgKHN0YXRpY1Byb3BzKSBkZWZpbmVQcm9wZXJ0aWVzKENvbnN0cnVjdG9yLCBzdGF0aWNQcm9wcyk7XG4gICAgcmV0dXJuIENvbnN0cnVjdG9yO1xuICB9O1xufSgpO1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvaGVscGVycy9jcmVhdGVDbGFzcy5qc1xuICoqIG1vZHVsZSBpZCA9IDRcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsIm1vZHVsZS5leHBvcnRzID0geyBcImRlZmF1bHRcIjogcmVxdWlyZShcImNvcmUtanMvbGlicmFyeS9mbi9vYmplY3QvZGVmaW5lLXByb3BlcnR5XCIpLCBfX2VzTW9kdWxlOiB0cnVlIH07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9jb3JlLWpzL29iamVjdC9kZWZpbmUtcHJvcGVydHkuanNcbiAqKiBtb2R1bGUgaWQgPSA1XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJyZXF1aXJlKCcuLi8uLi9tb2R1bGVzL2VzNi5vYmplY3QuZGVmaW5lLXByb3BlcnR5Jyk7XG52YXIgJE9iamVjdCA9IHJlcXVpcmUoJy4uLy4uL21vZHVsZXMvX2NvcmUnKS5PYmplY3Q7XG5tb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uIGRlZmluZVByb3BlcnR5KGl0LCBrZXksIGRlc2Mpe1xuICByZXR1cm4gJE9iamVjdC5kZWZpbmVQcm9wZXJ0eShpdCwga2V5LCBkZXNjKTtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9mbi9vYmplY3QvZGVmaW5lLXByb3BlcnR5LmpzXG4gKiogbW9kdWxlIGlkID0gNlxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwidmFyICRleHBvcnQgPSByZXF1aXJlKCcuL19leHBvcnQnKTtcclxuLy8gMTkuMS4yLjQgLyAxNS4yLjMuNiBPYmplY3QuZGVmaW5lUHJvcGVydHkoTywgUCwgQXR0cmlidXRlcylcclxuJGV4cG9ydCgkZXhwb3J0LlMgKyAkZXhwb3J0LkYgKiAhcmVxdWlyZSgnLi9fZGVzY3JpcHRvcnMnKSwgJ09iamVjdCcsIHtkZWZpbmVQcm9wZXJ0eTogcmVxdWlyZSgnLi9fb2JqZWN0LWRwJykuZn0pO1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9lczYub2JqZWN0LmRlZmluZS1wcm9wZXJ0eS5qc1xuICoqIG1vZHVsZSBpZCA9IDdcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsInZhciBnbG9iYWwgICAgPSByZXF1aXJlKCcuL19nbG9iYWwnKVxuICAsIGNvcmUgICAgICA9IHJlcXVpcmUoJy4vX2NvcmUnKVxuICAsIGN0eCAgICAgICA9IHJlcXVpcmUoJy4vX2N0eCcpXG4gICwgaGlkZSAgICAgID0gcmVxdWlyZSgnLi9faGlkZScpXG4gICwgUFJPVE9UWVBFID0gJ3Byb3RvdHlwZSc7XG5cbnZhciAkZXhwb3J0ID0gZnVuY3Rpb24odHlwZSwgbmFtZSwgc291cmNlKXtcbiAgdmFyIElTX0ZPUkNFRCA9IHR5cGUgJiAkZXhwb3J0LkZcbiAgICAsIElTX0dMT0JBTCA9IHR5cGUgJiAkZXhwb3J0LkdcbiAgICAsIElTX1NUQVRJQyA9IHR5cGUgJiAkZXhwb3J0LlNcbiAgICAsIElTX1BST1RPICA9IHR5cGUgJiAkZXhwb3J0LlBcbiAgICAsIElTX0JJTkQgICA9IHR5cGUgJiAkZXhwb3J0LkJcbiAgICAsIElTX1dSQVAgICA9IHR5cGUgJiAkZXhwb3J0LldcbiAgICAsIGV4cG9ydHMgICA9IElTX0dMT0JBTCA/IGNvcmUgOiBjb3JlW25hbWVdIHx8IChjb3JlW25hbWVdID0ge30pXG4gICAgLCBleHBQcm90byAgPSBleHBvcnRzW1BST1RPVFlQRV1cbiAgICAsIHRhcmdldCAgICA9IElTX0dMT0JBTCA/IGdsb2JhbCA6IElTX1NUQVRJQyA/IGdsb2JhbFtuYW1lXSA6IChnbG9iYWxbbmFtZV0gfHwge30pW1BST1RPVFlQRV1cbiAgICAsIGtleSwgb3duLCBvdXQ7XG4gIGlmKElTX0dMT0JBTClzb3VyY2UgPSBuYW1lO1xuICBmb3Ioa2V5IGluIHNvdXJjZSl7XG4gICAgLy8gY29udGFpbnMgaW4gbmF0aXZlXG4gICAgb3duID0gIUlTX0ZPUkNFRCAmJiB0YXJnZXQgJiYgdGFyZ2V0W2tleV0gIT09IHVuZGVmaW5lZDtcbiAgICBpZihvd24gJiYga2V5IGluIGV4cG9ydHMpY29udGludWU7XG4gICAgLy8gZXhwb3J0IG5hdGl2ZSBvciBwYXNzZWRcbiAgICBvdXQgPSBvd24gPyB0YXJnZXRba2V5XSA6IHNvdXJjZVtrZXldO1xuICAgIC8vIHByZXZlbnQgZ2xvYmFsIHBvbGx1dGlvbiBmb3IgbmFtZXNwYWNlc1xuICAgIGV4cG9ydHNba2V5XSA9IElTX0dMT0JBTCAmJiB0eXBlb2YgdGFyZ2V0W2tleV0gIT0gJ2Z1bmN0aW9uJyA/IHNvdXJjZVtrZXldXG4gICAgLy8gYmluZCB0aW1lcnMgdG8gZ2xvYmFsIGZvciBjYWxsIGZyb20gZXhwb3J0IGNvbnRleHRcbiAgICA6IElTX0JJTkQgJiYgb3duID8gY3R4KG91dCwgZ2xvYmFsKVxuICAgIC8vIHdyYXAgZ2xvYmFsIGNvbnN0cnVjdG9ycyBmb3IgcHJldmVudCBjaGFuZ2UgdGhlbSBpbiBsaWJyYXJ5XG4gICAgOiBJU19XUkFQICYmIHRhcmdldFtrZXldID09IG91dCA/IChmdW5jdGlvbihDKXtcbiAgICAgIHZhciBGID0gZnVuY3Rpb24oYSwgYiwgYyl7XG4gICAgICAgIGlmKHRoaXMgaW5zdGFuY2VvZiBDKXtcbiAgICAgICAgICBzd2l0Y2goYXJndW1lbnRzLmxlbmd0aCl7XG4gICAgICAgICAgICBjYXNlIDA6IHJldHVybiBuZXcgQztcbiAgICAgICAgICAgIGNhc2UgMTogcmV0dXJuIG5ldyBDKGEpO1xuICAgICAgICAgICAgY2FzZSAyOiByZXR1cm4gbmV3IEMoYSwgYik7XG4gICAgICAgICAgfSByZXR1cm4gbmV3IEMoYSwgYiwgYyk7XG4gICAgICAgIH0gcmV0dXJuIEMuYXBwbHkodGhpcywgYXJndW1lbnRzKTtcbiAgICAgIH07XG4gICAgICBGW1BST1RPVFlQRV0gPSBDW1BST1RPVFlQRV07XG4gICAgICByZXR1cm4gRjtcbiAgICAvLyBtYWtlIHN0YXRpYyB2ZXJzaW9ucyBmb3IgcHJvdG90eXBlIG1ldGhvZHNcbiAgICB9KShvdXQpIDogSVNfUFJPVE8gJiYgdHlwZW9mIG91dCA9PSAnZnVuY3Rpb24nID8gY3R4KEZ1bmN0aW9uLmNhbGwsIG91dCkgOiBvdXQ7XG4gICAgLy8gZXhwb3J0IHByb3RvIG1ldGhvZHMgdG8gY29yZS4lQ09OU1RSVUNUT1IlLm1ldGhvZHMuJU5BTUUlXG4gICAgaWYoSVNfUFJPVE8pe1xuICAgICAgKGV4cG9ydHMudmlydHVhbCB8fCAoZXhwb3J0cy52aXJ0dWFsID0ge30pKVtrZXldID0gb3V0O1xuICAgICAgLy8gZXhwb3J0IHByb3RvIG1ldGhvZHMgdG8gY29yZS4lQ09OU1RSVUNUT1IlLnByb3RvdHlwZS4lTkFNRSVcbiAgICAgIGlmKHR5cGUgJiAkZXhwb3J0LlIgJiYgZXhwUHJvdG8gJiYgIWV4cFByb3RvW2tleV0paGlkZShleHBQcm90bywga2V5LCBvdXQpO1xuICAgIH1cbiAgfVxufTtcbi8vIHR5cGUgYml0bWFwXG4kZXhwb3J0LkYgPSAxOyAgIC8vIGZvcmNlZFxuJGV4cG9ydC5HID0gMjsgICAvLyBnbG9iYWxcbiRleHBvcnQuUyA9IDQ7ICAgLy8gc3RhdGljXG4kZXhwb3J0LlAgPSA4OyAgIC8vIHByb3RvXG4kZXhwb3J0LkIgPSAxNjsgIC8vIGJpbmRcbiRleHBvcnQuVyA9IDMyOyAgLy8gd3JhcFxuJGV4cG9ydC5VID0gNjQ7ICAvLyBzYWZlXG4kZXhwb3J0LlIgPSAxMjg7IC8vIHJlYWwgcHJvdG8gbWV0aG9kIGZvciBgbGlicmFyeWAgXG5tb2R1bGUuZXhwb3J0cyA9ICRleHBvcnQ7XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19leHBvcnQuanNcbiAqKiBtb2R1bGUgaWQgPSA4XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCIvLyBodHRwczovL2dpdGh1Yi5jb20vemxvaXJvY2svY29yZS1qcy9pc3N1ZXMvODYjaXNzdWVjb21tZW50LTExNTc1OTAyOFxudmFyIGdsb2JhbCA9IG1vZHVsZS5leHBvcnRzID0gdHlwZW9mIHdpbmRvdyAhPSAndW5kZWZpbmVkJyAmJiB3aW5kb3cuTWF0aCA9PSBNYXRoXG4gID8gd2luZG93IDogdHlwZW9mIHNlbGYgIT0gJ3VuZGVmaW5lZCcgJiYgc2VsZi5NYXRoID09IE1hdGggPyBzZWxmIDogRnVuY3Rpb24oJ3JldHVybiB0aGlzJykoKTtcbmlmKHR5cGVvZiBfX2cgPT0gJ251bWJlcicpX19nID0gZ2xvYmFsOyAvLyBlc2xpbnQtZGlzYWJsZS1saW5lIG5vLXVuZGVmXG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19nbG9iYWwuanNcbiAqKiBtb2R1bGUgaWQgPSA5XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJ2YXIgY29yZSA9IG1vZHVsZS5leHBvcnRzID0ge3ZlcnNpb246ICcyLjIuMCd9O1xuaWYodHlwZW9mIF9fZSA9PSAnbnVtYmVyJylfX2UgPSBjb3JlOyAvLyBlc2xpbnQtZGlzYWJsZS1saW5lIG5vLXVuZGVmXG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19jb3JlLmpzXG4gKiogbW9kdWxlIGlkID0gMTBcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsIi8vIG9wdGlvbmFsIC8gc2ltcGxlIGNvbnRleHQgYmluZGluZ1xudmFyIGFGdW5jdGlvbiA9IHJlcXVpcmUoJy4vX2EtZnVuY3Rpb24nKTtcbm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oZm4sIHRoYXQsIGxlbmd0aCl7XG4gIGFGdW5jdGlvbihmbik7XG4gIGlmKHRoYXQgPT09IHVuZGVmaW5lZClyZXR1cm4gZm47XG4gIHN3aXRjaChsZW5ndGgpe1xuICAgIGNhc2UgMTogcmV0dXJuIGZ1bmN0aW9uKGEpe1xuICAgICAgcmV0dXJuIGZuLmNhbGwodGhhdCwgYSk7XG4gICAgfTtcbiAgICBjYXNlIDI6IHJldHVybiBmdW5jdGlvbihhLCBiKXtcbiAgICAgIHJldHVybiBmbi5jYWxsKHRoYXQsIGEsIGIpO1xuICAgIH07XG4gICAgY2FzZSAzOiByZXR1cm4gZnVuY3Rpb24oYSwgYiwgYyl7XG4gICAgICByZXR1cm4gZm4uY2FsbCh0aGF0LCBhLCBiLCBjKTtcbiAgICB9O1xuICB9XG4gIHJldHVybiBmdW5jdGlvbigvKiAuLi5hcmdzICovKXtcbiAgICByZXR1cm4gZm4uYXBwbHkodGhhdCwgYXJndW1lbnRzKTtcbiAgfTtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19jdHguanNcbiAqKiBtb2R1bGUgaWQgPSAxMVxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwibW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihpdCl7XG4gIGlmKHR5cGVvZiBpdCAhPSAnZnVuY3Rpb24nKXRocm93IFR5cGVFcnJvcihpdCArICcgaXMgbm90IGEgZnVuY3Rpb24hJyk7XG4gIHJldHVybiBpdDtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19hLWZ1bmN0aW9uLmpzXG4gKiogbW9kdWxlIGlkID0gMTJcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsInZhciBkUCAgICAgICAgID0gcmVxdWlyZSgnLi9fb2JqZWN0LWRwJylcbiAgLCBjcmVhdGVEZXNjID0gcmVxdWlyZSgnLi9fcHJvcGVydHktZGVzYycpO1xubW9kdWxlLmV4cG9ydHMgPSByZXF1aXJlKCcuL19kZXNjcmlwdG9ycycpID8gZnVuY3Rpb24ob2JqZWN0LCBrZXksIHZhbHVlKXtcbiAgcmV0dXJuIGRQLmYob2JqZWN0LCBrZXksIGNyZWF0ZURlc2MoMSwgdmFsdWUpKTtcbn0gOiBmdW5jdGlvbihvYmplY3QsIGtleSwgdmFsdWUpe1xuICBvYmplY3Rba2V5XSA9IHZhbHVlO1xuICByZXR1cm4gb2JqZWN0O1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2hpZGUuanNcbiAqKiBtb2R1bGUgaWQgPSAxM1xuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwidmFyIGFuT2JqZWN0ICAgICAgID0gcmVxdWlyZSgnLi9fYW4tb2JqZWN0JylcbiAgLCBJRThfRE9NX0RFRklORSA9IHJlcXVpcmUoJy4vX2llOC1kb20tZGVmaW5lJylcbiAgLCB0b1ByaW1pdGl2ZSAgICA9IHJlcXVpcmUoJy4vX3RvLXByaW1pdGl2ZScpXG4gICwgZFAgICAgICAgICAgICAgPSBPYmplY3QuZGVmaW5lUHJvcGVydHk7XG5cbmV4cG9ydHMuZiA9IHJlcXVpcmUoJy4vX2Rlc2NyaXB0b3JzJykgPyBPYmplY3QuZGVmaW5lUHJvcGVydHkgOiBmdW5jdGlvbiBkZWZpbmVQcm9wZXJ0eShPLCBQLCBBdHRyaWJ1dGVzKXtcbiAgYW5PYmplY3QoTyk7XG4gIFAgPSB0b1ByaW1pdGl2ZShQLCB0cnVlKTtcbiAgYW5PYmplY3QoQXR0cmlidXRlcyk7XG4gIGlmKElFOF9ET01fREVGSU5FKXRyeSB7XG4gICAgcmV0dXJuIGRQKE8sIFAsIEF0dHJpYnV0ZXMpO1xuICB9IGNhdGNoKGUpeyAvKiBlbXB0eSAqLyB9XG4gIGlmKCdnZXQnIGluIEF0dHJpYnV0ZXMgfHwgJ3NldCcgaW4gQXR0cmlidXRlcyl0aHJvdyBUeXBlRXJyb3IoJ0FjY2Vzc29ycyBub3Qgc3VwcG9ydGVkIScpO1xuICBpZigndmFsdWUnIGluIEF0dHJpYnV0ZXMpT1tQXSA9IEF0dHJpYnV0ZXMudmFsdWU7XG4gIHJldHVybiBPO1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX29iamVjdC1kcC5qc1xuICoqIG1vZHVsZSBpZCA9IDE0XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJ2YXIgaXNPYmplY3QgPSByZXF1aXJlKCcuL19pcy1vYmplY3QnKTtcbm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oaXQpe1xuICBpZighaXNPYmplY3QoaXQpKXRocm93IFR5cGVFcnJvcihpdCArICcgaXMgbm90IGFuIG9iamVjdCEnKTtcbiAgcmV0dXJuIGl0O1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2FuLW9iamVjdC5qc1xuICoqIG1vZHVsZSBpZCA9IDE1XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJtb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGl0KXtcbiAgcmV0dXJuIHR5cGVvZiBpdCA9PT0gJ29iamVjdCcgPyBpdCAhPT0gbnVsbCA6IHR5cGVvZiBpdCA9PT0gJ2Z1bmN0aW9uJztcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19pcy1vYmplY3QuanNcbiAqKiBtb2R1bGUgaWQgPSAxNlxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwibW9kdWxlLmV4cG9ydHMgPSAhcmVxdWlyZSgnLi9fZGVzY3JpcHRvcnMnKSAmJiAhcmVxdWlyZSgnLi9fZmFpbHMnKShmdW5jdGlvbigpe1xyXG4gIHJldHVybiBPYmplY3QuZGVmaW5lUHJvcGVydHkocmVxdWlyZSgnLi9fZG9tLWNyZWF0ZScpKCdkaXYnKSwgJ2EnLCB7Z2V0OiBmdW5jdGlvbigpeyByZXR1cm4gNzsgfX0pLmEgIT0gNztcclxufSk7XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19pZTgtZG9tLWRlZmluZS5qc1xuICoqIG1vZHVsZSBpZCA9IDE3XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCIvLyBUaGFuaydzIElFOCBmb3IgaGlzIGZ1bm55IGRlZmluZVByb3BlcnR5XG5tb2R1bGUuZXhwb3J0cyA9ICFyZXF1aXJlKCcuL19mYWlscycpKGZ1bmN0aW9uKCl7XG4gIHJldHVybiBPYmplY3QuZGVmaW5lUHJvcGVydHkoe30sICdhJywge2dldDogZnVuY3Rpb24oKXsgcmV0dXJuIDc7IH19KS5hICE9IDc7XG59KTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2Rlc2NyaXB0b3JzLmpzXG4gKiogbW9kdWxlIGlkID0gMThcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsIm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oZXhlYyl7XG4gIHRyeSB7XG4gICAgcmV0dXJuICEhZXhlYygpO1xuICB9IGNhdGNoKGUpe1xuICAgIHJldHVybiB0cnVlO1xuICB9XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZmFpbHMuanNcbiAqKiBtb2R1bGUgaWQgPSAxOVxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwidmFyIGlzT2JqZWN0ID0gcmVxdWlyZSgnLi9faXMtb2JqZWN0JylcbiAgLCBkb2N1bWVudCA9IHJlcXVpcmUoJy4vX2dsb2JhbCcpLmRvY3VtZW50XG4gIC8vIGluIG9sZCBJRSB0eXBlb2YgZG9jdW1lbnQuY3JlYXRlRWxlbWVudCBpcyAnb2JqZWN0J1xuICAsIGlzID0gaXNPYmplY3QoZG9jdW1lbnQpICYmIGlzT2JqZWN0KGRvY3VtZW50LmNyZWF0ZUVsZW1lbnQpO1xubW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihpdCl7XG4gIHJldHVybiBpcyA/IGRvY3VtZW50LmNyZWF0ZUVsZW1lbnQoaXQpIDoge307XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZG9tLWNyZWF0ZS5qc1xuICoqIG1vZHVsZSBpZCA9IDIwXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCIvLyA3LjEuMSBUb1ByaW1pdGl2ZShpbnB1dCBbLCBQcmVmZXJyZWRUeXBlXSlcbnZhciBpc09iamVjdCA9IHJlcXVpcmUoJy4vX2lzLW9iamVjdCcpO1xuLy8gaW5zdGVhZCBvZiB0aGUgRVM2IHNwZWMgdmVyc2lvbiwgd2UgZGlkbid0IGltcGxlbWVudCBAQHRvUHJpbWl0aXZlIGNhc2Vcbi8vIGFuZCB0aGUgc2Vjb25kIGFyZ3VtZW50IC0gZmxhZyAtIHByZWZlcnJlZCB0eXBlIGlzIGEgc3RyaW5nXG5tb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGl0LCBTKXtcbiAgaWYoIWlzT2JqZWN0KGl0KSlyZXR1cm4gaXQ7XG4gIHZhciBmbiwgdmFsO1xuICBpZihTICYmIHR5cGVvZiAoZm4gPSBpdC50b1N0cmluZykgPT0gJ2Z1bmN0aW9uJyAmJiAhaXNPYmplY3QodmFsID0gZm4uY2FsbChpdCkpKXJldHVybiB2YWw7XG4gIGlmKHR5cGVvZiAoZm4gPSBpdC52YWx1ZU9mKSA9PSAnZnVuY3Rpb24nICYmICFpc09iamVjdCh2YWwgPSBmbi5jYWxsKGl0KSkpcmV0dXJuIHZhbDtcbiAgaWYoIVMgJiYgdHlwZW9mIChmbiA9IGl0LnRvU3RyaW5nKSA9PSAnZnVuY3Rpb24nICYmICFpc09iamVjdCh2YWwgPSBmbi5jYWxsKGl0KSkpcmV0dXJuIHZhbDtcbiAgdGhyb3cgVHlwZUVycm9yKFwiQ2FuJ3QgY29udmVydCBvYmplY3QgdG8gcHJpbWl0aXZlIHZhbHVlXCIpO1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX3RvLXByaW1pdGl2ZS5qc1xuICoqIG1vZHVsZSBpZCA9IDIxXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJtb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGJpdG1hcCwgdmFsdWUpe1xuICByZXR1cm4ge1xuICAgIGVudW1lcmFibGUgIDogIShiaXRtYXAgJiAxKSxcbiAgICBjb25maWd1cmFibGU6ICEoYml0bWFwICYgMiksXG4gICAgd3JpdGFibGUgICAgOiAhKGJpdG1hcCAmIDQpLFxuICAgIHZhbHVlICAgICAgIDogdmFsdWVcbiAgfTtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19wcm9wZXJ0eS1kZXNjLmpzXG4gKiogbW9kdWxlIGlkID0gMjJcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsIid1c2Ugc3RyaWN0JztcblxuLyoqXG4gKiBFcnJvcnNcbiAqIEB0eXBlZGVmIHtPYmplY3R9IExpc3RFcnJvcnNcbiAqIEBwcm9wZXJ0eSB7U3RyaW5nfSBuYW1lIC0gbmFtZSBvZiBmaWVsZFxuICogQHByb3BlcnR5IHtTdHJpbmd9IGVycm9yIC0gZXJyb3IgZGVzY3JpcHRpb25cbiAqL1xuXG5leHBvcnQgZGVmYXVsdCBjbGFzcyBGb3JtSGVscGVyIHtcbiAgICAvKipcbiAgICAgKiBWYWxpZGF0ZSBmb3JtIHRocm91Z2ggaW5wdXRzXG4gICAgICogQHBhcmFtIHtqUXVlcnl9ICRjb250cm9sc1xuICAgICAqL1xuICAgIGNvbnN0cnVjdG9yKCRjb250cm9scykge1xuICAgICAgICB0aGlzLiRjb250cm9scyA9ICRjb250cm9scztcbiAgICAgICAgdGhpcy5hcnJFcnJvcnMgPSBbXTtcbiAgICAgICAgdGhpcy5fYXNzaWduRXZlbnRzKCk7XG4gICAgfVxuXG4gICAgX2Fzc2lnbkV2ZW50cygpIHtcbiAgICAgICAgdGhpcy4kY29udHJvbHMub24oJ2lucHV0IGNoYW5nZScsIChlKSA9PiB7XG4gICAgICAgICAgICBjb25zdCAkY29udHJvbCA9ICQoZS5jdXJyZW50VGFyZ2V0KTtcblxuICAgICAgICAgICAgdGhpcy5fdmFsaWRhdGVJbW1lZGlhdGUoJGNvbnRyb2wpO1xuICAgICAgICAgICAgdGhpcy5fcmVtb3ZlRXJyb3IoJGNvbnRyb2wpO1xuICAgICAgICB9KTtcbiAgICB9XG5cbiAgICBfdmFsaWRhdGVJbW1lZGlhdGUoJGNvbnRyb2wpe1xuICAgICAgICBpZiAoJGNvbnRyb2wuaGFzQ2xhc3MoJ3R5cGUtbnVtZXJpYycpKSB7XG4gICAgICAgICAgICAkY29udHJvbC52YWwoJGNvbnRyb2wudmFsKCkucmVwbGFjZSgvW15cXGRdKy9nLCAnJykpO1xuICAgICAgICB9XG5cbiAgICAgICAgaWYgKCRjb250cm9sLmhhc0NsYXNzKCd0eXBlLW5vc3BhY2UnKSkge1xuICAgICAgICAgICAgJGNvbnRyb2wudmFsKCRjb250cm9sLnZhbCgpLnJlcGxhY2UoL1xccy9nLCAnJykpO1xuICAgICAgICB9XG4gICAgfVxuXG4gICAgaXNWYWxpZElucHV0cygpIHtcbiAgICAgICAgY29uc3QgJGNvbnRyb2xzID0gdGhpcy4kY29udHJvbHM7XG4gICAgICAgIGxldCBlcnJvciA9IDA7XG5cbiAgICAgICAgJGNvbnRyb2xzLmVhY2goKGluZGV4LCBjb250cm9sKSA9PiB7XG4gICAgICAgICAgICBjb25zdCAkY29udHJvbCA9ICQoY29udHJvbCk7XG5cbiAgICAgICAgICAgIGlmICghdGhpcy5faXNWYWxpZElucHV0KCRjb250cm9sKSkge1xuICAgICAgICAgICAgICAgIGVycm9yICs9IDE7XG4gICAgICAgICAgICB9XG4gICAgICAgIH0pO1xuICAgICAgICByZXR1cm4gQm9vbGVhbighZXJyb3IpO1xuICAgIH1cblxuICAgIC8qKlxuICAgICAqIENoZWNrIGdpdmVuIGNvbnRyb2wsIGlzIGl0IHZhbGlkP1xuICAgICAqIEBwYXJhbSB7alF1ZXJ5fSAkY29udHJvbFxuICAgICAqIEByZXR1cm5zIHtib29sZWFufSAtIElzIHZhbGlkIGNvbnRyb2w/XG4gICAgICovXG4gICAgX2lzVmFsaWRJbnB1dCgkY29udHJvbCkge1xuICAgICAgICBjb25zdCB2YWx1ZSA9ICQudHJpbSgkY29udHJvbC52YWwoKSk7XG5cbiAgICAgICAgaWYgKCF2YWx1ZSAmJiAhJGNvbnRyb2wuaGFzQ2xhc3MoJ3R5cGUtb3B0aW9uYWwnKSkge1xuICAgICAgICAgICAgdGhpcy5fc2V0RXJyb3IoJGNvbnRyb2wsICdFbXB0eScpO1xuICAgICAgICAgICAgcmV0dXJuIGZhbHNlO1xuICAgICAgICB9XG5cbiAgICAgICAgaWYgKCgkY29udHJvbC5oYXNDbGFzcygndHlwZS1lbWFpbCcpKSAmJiAhdGhpcy5faXNWYWxpZEVtYWlsKHZhbHVlKSkge1xuICAgICAgICAgICAgdGhpcy5fc2V0RXJyb3IoJGNvbnRyb2wsICdFbWFpbCBpcyBub3QgdmFsaWQnKTtcbiAgICAgICAgICAgIHJldHVybiBmYWxzZTtcbiAgICAgICAgfVxuXG4gICAgICAgIHJldHVybiB0cnVlO1xuICAgIH1cblxuICAgIC8qKlxuICAgICAqIElzIEVtYWlsIHZhbGlkP1xuICAgICAqIEBwYXJhbSB7c3RyaW5nfSBlbWFpbFxuICAgICAqIEByZXR1cm5zIHtib29sZWFufVxuICAgICAqL1xuICAgIF9pc1ZhbGlkRW1haWwoZW1haWwpIHtcbiAgICAgICAgdmFyIHJlID0gL14oKFtePD4oKVxcW1xcXVxcXFwuLDs6XFxzQFwiXSsoXFwuW148PigpXFxbXFxdXFxcXC4sOzpcXHNAXCJdKykqKXwoXCIuK1wiKSlAKChcXFtbMC05XXsxLDN9XFwuWzAtOV17MSwzfVxcLlswLTldezEsM31cXC5bMC05XXsxLDN9XSl8KChbYS16QS1aXFwtMC05XStcXC4pK1thLXpBLVpdezIsfSkpJC87XG4gICAgICAgIHJldHVybiByZS50ZXN0KGVtYWlsKTtcbiAgICB9XG5cbiAgICAvKipcbiAgICAgKiBTZXQgZXJyb3IgZm9yIGNvbnRyb2xcbiAgICAgKiBAcGFyYW0ge2pRdWVyeX0gJGNvbnRyb2xcbiAgICAgKiBAcGFyYW0ge1N0cmluZ30gZXJyb3JUZXh0XG4gICAgICogQHBhcmFtIHtCb29sZWFufSBpbnNlcnRFcnJvclxuICAgICAqL1xuICAgIF9zZXRFcnJvcigkY29udHJvbCwgZXJyb3JUZXh0LCBpbnNlcnRFcnJvciA9IHRydWUpIHtcbiAgICAgICAgY29uc3QgJHBhcmVudCA9ICRjb250cm9sLnBhcmVudCgpO1xuICAgICAgICBjb25zdCAkZXJyb3IgPSAkcGFyZW50LmZpbmQoJy5iLWVycm9yJyk7XG5cbiAgICAgICAgaWYgKCRlcnJvci5sZW5ndGgpIHJldHVybjtcblxuICAgICAgICAkcGFyZW50LmFkZENsYXNzKCdiLWVycm9yX3Nob3cnKTtcbiAgICAgICAgXG4gICAgICAgIGluc2VydEVycm9yICYmICQoJzxkaXYgY2xhc3M9XCJiLWVycm9yXCIgLz4nKVxuICAgICAgICAgICAgLnRleHQoZXJyb3JUZXh0KVxuICAgICAgICAgICAgLmFwcGVuZFRvKCRwYXJlbnQpO1xuXG4gICAgICAgIHRoaXMuYXJyRXJyb3JzLnB1c2goe1xuICAgICAgICAgICAgbmFtZTogJGNvbnRyb2wuYXR0cignbmFtZScpLFxuICAgICAgICAgICAgZXJyb3I6IGVycm9yVGV4dFxuICAgICAgICB9KVxuICAgIH1cblxuICAgIC8qKlxuICAgICAqIFJlbW92ZSBlcnJvciBmb3IgY29udHJvbFxuICAgICAqIEBwYXJhbSB7alF1ZXJ5fSAkY29udHJvbFxuICAgICAqL1xuICAgIF9yZW1vdmVFcnJvcigkY29udHJvbCkge1xuICAgICAgICBjb25zdCAkcGFyZW50ID0gJGNvbnRyb2wucGFyZW50KCk7XG5cbiAgICAgICAgJHBhcmVudFxuICAgICAgICAgICAgLnJlbW92ZUNsYXNzKCdiLWVycm9yX3Nob3cnKVxuICAgICAgICAgICAgLmZpbmQoJy5iLWVycm9yJykucmVtb3ZlKCk7XG5cbiAgICAgICAgdGhpcy5hcnJFcnJvcnMgPSB0aGlzLmFyckVycm9ycy5maWx0ZXIoZnVuY3Rpb24gKGl0ZW0pIHtcbiAgICAgICAgICAgIHJldHVybiBpdGVtLm5hbWUgIT09ICRjb250cm9sLmF0dHIoJ25hbWUnKVxuICAgICAgICB9KVxuICAgIH1cblxuICAgIC8qKlxuICAgICAqIFNldCBlcnJvcnNcbiAgICAgKiBAcGFyYW0ge0FycmF5fSBlcnJvcnMgLSBbe25hbWU6IFwiZW1haWxcIiwgZXJyb3I6IFwiZW1wdHlcIn0sIHtuYW1lOiBcInBhc3N3b3JkXCIsIGVycm9yOiBcImVtcHR5XCJ9XVxuICAgICAqIEBwYXJhbSB7Qm9vbGVhbn0gaW5zZXJ0RXJyb3IgLSBpbnNlcnQgZXJyb3IgZGVzY3JpcHRpb24gdG8gdGhlIERvbSBcbiAgICAgKi9cbiAgICBzZXRFcnJvcnMoZXJyb3JzLCBpbnNlcnRFcnJvciA9IHRydWUpIHtcbiAgICAgICAgZXJyb3JzLmZvckVhY2goKGl0ZW0pID0+IHtcbiAgICAgICAgICAgIGNvbnN0ICRjdXJyZW50Q29udHJvbCA9IHRoaXMuJGNvbnRyb2xzLmZpbHRlcignW25hbWU9XCInICsgaXRlbS5uYW1lICsgJ1wiXScpLmZpcnN0KCk7XG5cbiAgICAgICAgICAgIGlmICgkY3VycmVudENvbnRyb2wubGVuZ3RoKSB0aGlzLl9zZXRFcnJvcigkY3VycmVudENvbnRyb2wsIGl0ZW0uZXJyb3IsIGluc2VydEVycm9yKVxuICAgICAgICB9KVxuICAgIH1cblxuICAgIC8qKlxuICAgICAqIEdldCB0ZXh0IHZlcnNpb24gb2YgZXJyb3JzIGluIG9uZSBsaW5lLlxuICAgICAqIEBwYXJhbSB7TGlzdEVycm9yc30gZXJyb3JzXG4gICAgICogQHJldHVybnMge3N0cmluZ31cbiAgICAgKi9cbiAgICBnZXRFcnJvcnNUZXh0KGVycm9ycykge1xuICAgICAgICBjb25zdCBhcnJFcnJvcnMgPSBlcnJvcnMgfHwgdGhpcy5hcnJFcnJvcnM7XG4gICAgICAgIGxldCBlcnJvclR4dCA9ICcnO1xuXG4gICAgICAgIGFyckVycm9ycy5mb3JFYWNoKChpdGVtKSA9PiB7XG4gICAgICAgICAgICBjb25zdCBuYW1lID0gaXRlbS5uYW1lWzBdLnRvVXBwZXJDYXNlKCkgKyBpdGVtLm5hbWUuc3Vic3RyKDEpO1xuXG4gICAgICAgICAgICBlcnJvclR4dCArPSBgJHtuYW1lfTogJHtpdGVtLmVycm9yfS4gYDtcbiAgICAgICAgfSk7XG5cbiAgICAgICAgcmV0dXJuIGVycm9yVHh0O1xuICAgIH1cblxuICAgIC8qKlxuICAgICAqIEdldCBsaXN0IG9mIGVycm9ycyB3aXRoIGZ1bGwgdGl0bGUgKGZyb20gY29udHJvbCB0aXRsZSBhdHRyaWJ1dGUpXG4gICAgICogQHBhcmFtIHtMaXN0RXJyb3JzfSBlcnJvcnMgLSBsaXN0IG9mIGVycm9yc1xuICAgICAqIEByZXR1cm5zIHtzdHJpbmd9XG4gICAgICovXG4gICAgZ2V0RXJyb3JzRnVsbChlcnJvcnMpIHtcbiAgICAgICAgY29uc3Qgc2VsZiA9IHRoaXM7XG4gICAgICAgIGNvbnN0IGFyckVycm9ycyA9IGVycm9ycyB8fCB0aGlzLmFyckVycm9ycztcbiAgICAgICAgbGV0IGVycm9yVHh0ID0gJyc7XG5cbiAgICAgICAgYXJyRXJyb3JzLmZvckVhY2goKGl0ZW0pID0+IHtcbiAgICAgICAgICAgIGNvbnN0ICRjb250cm9sID0gc2VsZi4kY29udHJvbHMuZmlsdGVyKGBbbmFtZT1cIiR7aXRlbS5uYW1lfVwiXWApLmZpcnN0KCk7XG4gICAgICAgICAgICBjb25zdCBuYW1lID0gJGNvbnRyb2wubGVuZ3RoPyAkY29udHJvbC5hdHRyKCd0aXRsZScpOiBpdGVtLm5hbWU7XG5cbiAgICAgICAgICAgIGVycm9yVHh0ICs9IGA8Yj4ke25hbWV9PC9iPjogJHtpdGVtLmVycm9yfS4gIDxicj48YnI+YDtcbiAgICAgICAgfSk7XG5cbiAgICAgICAgcmV0dXJuIGVycm9yVHh0O1xuICAgIH1cblxuICAgIGdldEZvcm1EYXRhKCl7XG4gICAgICAgIGxldCBhamF4RGF0YSA9IHt9O1xuXG4gICAgICAgIHRoaXMuJGNvbnRyb2xzLm1hcCgoaW5kZXgsIGVsKSA9PiB7XG4gICAgICAgICAgICBjb25zdCAkZWwgPSAkKGVsKTtcbiAgICAgICAgICAgIGNvbnN0IG5hbWUgPSAkZWwuYXR0cignbmFtZScpO1xuXG4gICAgICAgICAgICBpZiAoIW5hbWUpIHJldHVybjtcblxuICAgICAgICAgICAgaWYgKCRlbC5pcygnOmNoZWNrYm94Jykpe1xuICAgICAgICAgICAgICAgIGFqYXhEYXRhW25hbWVdID0gJGVsLnByb3AoJ2NoZWNrZWQnKVxuICAgICAgICAgICAgfSBlbHNlIHtcbiAgICAgICAgICAgICAgICBhamF4RGF0YVtuYW1lXSA9ICRlbC52YWwoKVxuICAgICAgICAgICAgfVxuICAgICAgICB9KTtcblxuICAgICAgICByZXR1cm4gYWpheERhdGE7XG4gICAgfVxuXG4gICAgLyoqXG4gICAgICogUmVtb3ZlIGFsbCBlcnJvcnNcbiAgICAgKi9cbiAgICByZW1vdmVFcnJvcnMoKSB7XG4gICAgICAgIHRoaXMuJGNvbnRyb2xzLmVhY2goKGluZGV4LCBlbCkgPT4ge1xuICAgICAgICAgICAgY29uc3QgJGVsID0gJChlbCk7XG4gICAgICAgICAgICB0aGlzLl9yZW1vdmVFcnJvcigkZWwpXG4gICAgICAgIH0pXG4gICAgfVxuXG4gICAgY2xlYXJGb3JtKCkge1xuICAgICAgICB0aGlzLiRjb250cm9scy5lYWNoKChpbmRleCwgZWwpID0+IHtcbiAgICAgICAgICAgIGNvbnN0ICRlbCA9ICQoZWwpO1xuICAgICAgICAgICAgaWYgKCEkZWwuYXR0cihcImRpc2FibGVkXCIpKSAgJGVsLnZhbCgnJyk7XG4gICAgICAgIH0pXG4gICAgfVxufVxuXG5cblxuLyoqIFdFQlBBQ0sgRk9PVEVSICoqXG4gKiogLi9mcm9udGVuZC9qcy9jb21tb24vX2Zvcm0taGVscGVyLmpzXG4gKiovIiwiJ3VzZSBzdHJpY3QnO1xuXG5pbXBvcnQgRW1haWxDb25uZWN0IGZyb20gXCIuL21hbmFnZS13aWRnZXRzL19lbWFpbC1jb25uZWN0XCI7XG5pbXBvcnQgU29jaWFsQ29ubmVjdCBmcm9tIFwiLi9tYW5hZ2Utd2lkZ2V0cy9fc29jaWFsLWNvbm5lY3RcIjtcblxuXG4kKGZ1bmN0aW9uKCl7XG4gICAgU29jaWFsQ29ubmVjdC5wbHVnaW4oJy5qcy1zb2NpYWwtY29ubmVjdCcpO1xuXG4gICAgRW1haWxDb25uZWN0XG4gICAgICAgIC5wbHVnaW4oJyNkbGctZW1haWwtY29ubmVjdCcsIHtcbiAgICAgICAgICAgIHVybDoganNSb3V0ZXMuY29udHJvbGxlcnMuY29yZS5Vc2VyQWNjb3VudHMuaGFuZGxlTmV3UGFzc3dvcmQoKS51cmxcbiAgICAgICAgfSlcbiAgICAgICAgLm9uKCdobXQuZW1haWxjb25uZWN0LnN1Y2Nlc3MnLCAoZSk9PntcbiAgICAgICAgICAgICQoJy5qcy1lbWFpbC1jb25uZWN0JykuYWRkQ2xhc3MoJ3Nob3dfY29ubmVjdGVkJyk7XG4gICAgICAgICAgICBzdWNjZXNzKFwiWW91IGNyZWF0ZWQgbmV3IGVtYWlsIGFjY291bnRcIik7XG4gICAgICAgIH0pO1xuXG4gICAgRW1haWxDb25uZWN0XG4gICAgICAgIC5wbHVnaW4oJyNkbGctY2hhbmdlLXBhc3N3b3JkJywge1xuICAgICAgICAgICAgdXJsOiBqc1JvdXRlcy5jb250cm9sbGVycy5jb3JlLlVzZXJBY2NvdW50cy5jaGFuZ2VQYXNzd29yZCgpLnVybCxcbiAgICAgICAgfSlcbiAgICAgICAgLm9uKCdobXQuZW1haWxjb25uZWN0LnN1Y2Nlc3MnLCAoZSk9PntcbiAgICAgICAgICAgIGxldCBtc2cgPSBcIllvdXIgcGFzc3dvcmQgd2FzIHN1Y2Nlc3NmdWxseSB1cGRhdGVkXCI7XG4gICAgICAgICAgICBzdWNjZXNzKG1zZyk7XG4gICAgICAgIH0pO1xuXG4gICAgRW1haWxDb25uZWN0XG4gICAgICAgIC5wbHVnaW4oJyNkbGctY2hhbmdlLWVtYWlsJywge1xuICAgICAgICAgICAgdXJsOiBqc1JvdXRlcy5jb250cm9sbGVycy5jb3JlLlVzZXJBY2NvdW50cy5jaGFuZ2VFbWFpbCgpLnVybCxcbiAgICAgICAgfSlcbiAgICAgICAgLm9uKCdobXQuZW1haWxjb25uZWN0LnN1Y2Nlc3MnLCAoZSk9PntcbiAgICAgICAgICAgIGxldCBtc2cgPSBcIlBsZWFzZSBjaGVjayB5b3VyIG1haWxib3ggYW5kIGNsaWNrIGEgY29uZmlybWF0aW9uIGxpbmsgdG8gY29tcGxldGUgYW4gZW1haWwgY2hhbmdlIHByb2Nlc3NcIjtcbiAgICAgICAgICAgIHN1Y2Nlc3MobXNnKTtcbiAgICAgICAgfSk7XG59KTtcblxuXG5cblxuXG5cbi8qKiBXRUJQQUNLIEZPT1RFUiAqKlxuICoqIC4vZnJvbnRlbmQvanMvcGVyc29uL21hbmFnZS1hY2NvdW50LmpzXG4gKiovIiwiJ3VzZSBzdHJpY3QnO1xuXG5pbXBvcnQgRm9ybUhlbHBlciBmcm9tIFwiLi8uLi8uLi9jb21tb24vX2Zvcm0taGVscGVyXCI7XG5cbmV4cG9ydCBkZWZhdWx0IGNsYXNzIFdpZGdldCB7XG4gICAgY29uc3RydWN0b3Ioc2VsZWN0b3IsIG9wdGlvbnMpIHtcbiAgICAgICAgdGhpcy4kcm9vdCA9ICQoc2VsZWN0b3IpO1xuICAgICAgICB0aGlzLmxvY2FscyA9IHRoaXMuX2dldERvbSgpO1xuICAgICAgICB0aGlzLm9wdGlvbnMgPSAkLmV4dGVuZCh7fSwgb3B0aW9ucywgdGhpcy4kcm9vdC5kYXRhKCkpO1xuXG4gICAgICAgIHRoaXMudmFsaWRhdGlvbiA9IG5ldyBGb3JtSGVscGVyKHRoaXMubG9jYWxzLiRpbnB1dHMpO1xuICAgICAgICB0aGlzLl9hc3NpZ25FdmVudHMoKTtcbiAgICB9XG5cbiAgICBfZ2V0RG9tKCl7XG4gICAgICAgIGNvbnN0ICRyb290ID0gdGhpcy4kcm9vdDtcbiAgICAgICAgcmV0dXJuIHtcbiAgICAgICAgICAgICRmb3JtOiAkcm9vdC5maW5kKCdmb3JtJyksXG4gICAgICAgICAgICAkaW5wdXRzOiAkcm9vdC5maW5kKCdmb3JtIGlucHV0JylcbiAgICAgICAgfTtcbiAgICB9XG5cbiAgICBfYXNzaWduRXZlbnRzKCl7XG4gICAgICAgIHRoaXMubG9jYWxzLiRmb3JtLm9uKCdzdWJtaXQnLCB0aGlzLl9vblN1Ym1pdEZvcm0uYmluZCh0aGlzKSk7XG4gICAgICAgIHRoaXMuJHJvb3Qub24oJ2hpZGUuYnMubW9kYWwnLCB0aGlzLl9vbkhpZGVNb2RhbC5iaW5kKHRoaXMpKTtcbiAgICB9XG5cbiAgICBfb25TdWJtaXRGb3JtKGUpe1xuICAgICAgICBjb25zdCBzZWxmID0gdGhpcztcbiAgICAgICAgZS5wcmV2ZW50RGVmYXVsdCgpO1xuXG4gICAgICAgIGlmKCFzZWxmLnZhbGlkYXRpb24uaXNWYWxpZElucHV0cygpKSByZXR1cm47XG5cbiAgICAgICAgY29uc3QgZm9ybURhdGEgPSBzZWxmLnZhbGlkYXRpb24uZ2V0Rm9ybURhdGEoKTtcbiAgICAgICAgc2VsZi5fc2VuZERhdGEoZm9ybURhdGEpXG4gICAgICAgICAgICAuZG9uZShmdW5jdGlvbigpe1xuICAgICAgICAgICAgICAgIHNlbGYuJHJvb3QubW9kYWwoJ2hpZGUnKTtcbiAgICAgICAgICAgICAgICBzZWxmLnZhbGlkYXRpb24uY2xlYXJGb3JtKCk7XG5cbiAgICAgICAgICAgICAgICBzZWxmLiRyb290LnRyaWdnZXIoJ2htdC5lbWFpbGNvbm5lY3Quc3VjY2VzcycpO1xuICAgICAgICAgICAgfSlcbiAgICAgICAgICAgIC5mYWlsKGZ1bmN0aW9uKHJlc3BvbnNlKXtcbiAgICAgICAgICAgICAgICBjb25zdCBkYXRhID0gJC5wYXJzZUpTT04ocmVzcG9uc2UucmVzcG9uc2VUZXh0KS5kYXRhO1xuXG4gICAgICAgICAgICAgICAgaWYgKCFkYXRhLmVycm9ycykgcmV0dXJuO1xuICAgICAgICAgICAgICAgIHNlbGYudmFsaWRhdGlvbi5zZXRFcnJvcnMoZGF0YS5lcnJvcnMpO1xuICAgICAgICAgICAgfSlcbiAgICB9XG5cbiAgICBfb25IaWRlTW9kYWwoKXtcbiAgICAgICAgdGhpcy52YWxpZGF0aW9uLmNsZWFyRm9ybSgpO1xuICAgICAgICB0aGlzLnZhbGlkYXRpb24ucmVtb3ZlRXJyb3JzKCk7XG4gICAgfVxuXG4gICAgX3NlbmREYXRhKGRhdGEpe1xuICAgICAgICByZXR1cm4gJC5wb3N0KHRoaXMub3B0aW9ucy51cmwsIGRhdGEpO1xuICAgIH1cblxuICAgIC8vIHN0YXRpY1xuICAgIHN0YXRpYyBwbHVnaW4oc2VsZWN0b3IsIG9wdGlvbnMpIHtcbiAgICAgICAgY29uc3QgJGVsZW1zID0gJChzZWxlY3Rvcik7XG4gICAgICAgIGlmICghJGVsZW1zLmxlbmd0aCkgcmV0dXJuO1xuXG4gICAgICAgIHJldHVybiAkZWxlbXMuZWFjaChmdW5jdGlvbiAoaW5kZXgsIGVsKSB7XG4gICAgICAgICAgICBsZXQgJGVsZW1lbnQgPSAkKGVsKTtcbiAgICAgICAgICAgIGxldCBkYXRhICAgICA9ICRlbGVtZW50LmRhdGEoJ3dpZGdldC5zY3JvbGx0bycpO1xuXG4gICAgICAgICAgICBpZiAoIWRhdGEpIHtcbiAgICAgICAgICAgICAgICBkYXRhID0gbmV3IFdpZGdldChlbCwgb3B0aW9ucyk7XG4gICAgICAgICAgICAgICAgJGVsZW1lbnQuZGF0YSgnd2lkZ2V0JywgZGF0YSk7XG4gICAgICAgICAgICB9XG4gICAgICAgIH0pXG4gICAgfVxufVxuXG5cblxuXG5cblxuXG5cbi8qKiBXRUJQQUNLIEZPT1RFUiAqKlxuICoqIC4vZnJvbnRlbmQvanMvcGVyc29uL21hbmFnZS13aWRnZXRzL19lbWFpbC1jb25uZWN0LmpzXG4gKiovIiwiJ3VzZSBzdHJpY3QnO1xuXG5pbXBvcnQgRm9ybUhlbHBlciBmcm9tIFwiLi8uLi8uLi9jb21tb24vX2Zvcm0taGVscGVyXCI7XG5cbmV4cG9ydCBkZWZhdWx0IGNsYXNzIFdpZGdldCB7XG4gICAgY29uc3RydWN0b3Ioc2VsZWN0b3IsIG9wdGlvbnMpIHtcbiAgICAgICAgdGhpcy4kcm9vdCA9ICQoc2VsZWN0b3IpO1xuICAgICAgICB0aGlzLm9wdGlvbnMgPSAkLmV4dGVuZCh7fSwgb3B0aW9ucywgdGhpcy4kcm9vdC5kYXRhKCkpO1xuXG4gICAgICAgIHRoaXMuX2Fzc2lnbkV2ZW50cygpO1xuICAgIH1cblxuICAgIF9hc3NpZ25FdmVudHMoKXtcbiAgICAgICAgdGhpcy4kcm9vdC5vbignY2xpY2snLCAnW2RhdGEtc29jaWFsLWNvbm5lY3RdJywgdGhpcy5fb25DbGlja0Nvbm5lY3QuYmluZCh0aGlzKSk7XG4gICAgfVxuICAgIFxuICAgIF9vbkNsaWNrQ29ubmVjdChlKXtcbiAgICAgICAgZS5wcmV2ZW50RGVmYXVsdCgpO1xuICAgICAgICBjb25zdCAkcm9vdCA9ICQoZS5jdXJyZW50VGFyZ2V0KS5jbG9zZXN0KCcuYi1jb25uZWN0LWknKTtcblxuICAgICAgICB0aGlzLnRvZ2dsZUNvbm5lY3QoJHJvb3QpO1xuICAgIH1cblxuICAgIF9zZXRDb25uZWN0KCRlbCkge1xuICAgICAgICB3aW5kb3cubG9jYXRpb24gPSAkZWwuZGF0YSgndXJsJyk7XG4gICAgfVxuXG4gICAgX3VuU2V0Q29ubmVjdCgkZWwpe1xuICAgICAgICBjb25zdCBzb2NpYWxUeXBlID0gJGVsLmRhdGEoJ3NvY2lhbCcpO1xuICAgICAgICBjb25zdCB1cmwgPSBqc1JvdXRlcy5jb250cm9sbGVycy5jb3JlLlVzZXJBY2NvdW50cy5kaXNjb25uZWN0KHNvY2lhbFR5cGUpLnVybDtcblxuICAgICAgICAkLnBvc3QodXJsLCB7fSwgZnVuY3Rpb24oZGF0YSkge1xuICAgICAgICAgICAgJGVsLnJlbW92ZUNsYXNzKCdzdGF0ZS1jb21wbGV0ZScpO1xuICAgICAgICAgICAgc3VjY2VzcyhkYXRhLm1lc3NhZ2UpO1xuICAgICAgICB9LCBcImpzb25cIikuZmFpbChmdW5jdGlvbihqcVhIUiwgdGV4dFN0YXR1cywgZXJyb3JUaHJvd24pIHtcbiAgICAgICAgICAgIHZhciByZXNwb25zZSA9IEpTT04ucGFyc2UoanFYSFIucmVzcG9uc2VUZXh0KTtcbiAgICAgICAgICAgIGVycm9yKHJlc3BvbnNlLm1lc3NhZ2UpO1xuICAgICAgICB9KTtcbiAgICB9XG5cbiAgICB0b2dnbGVDb25uZWN0KCRlbCl7XG4gICAgICAgIGlmICgkZWwuaGFzQ2xhc3MoJ3N0YXRlLWNvbXBsZXRlJykpe1xuICAgICAgICAgICAgdGhpcy5fdW5TZXRDb25uZWN0KCRlbCk7XG4gICAgICAgIH0gZWxzZSB7XG4gICAgICAgICAgICB0aGlzLl9zZXRDb25uZWN0KCRlbCk7XG4gICAgICAgIH1cbiAgICB9XG5cbiAgICAvLyBzdGF0aWNcbiAgICBzdGF0aWMgcGx1Z2luKHNlbGVjdG9yLCBvcHRpb25zKSB7XG4gICAgICAgIGNvbnN0ICRlbGVtcyA9ICQoc2VsZWN0b3IpO1xuICAgICAgICBpZiAoISRlbGVtcy5sZW5ndGgpIHJldHVybjtcblxuICAgICAgICByZXR1cm4gJGVsZW1zLmVhY2goZnVuY3Rpb24gKGluZGV4LCBlbCkge1xuICAgICAgICAgICAgbGV0ICRlbGVtZW50ID0gJChlbCk7XG4gICAgICAgICAgICBsZXQgZGF0YSAgICAgPSAkZWxlbWVudC5kYXRhKCd3aWRnZXQuc2Nyb2xsdG8nKTtcblxuICAgICAgICAgICAgaWYgKCFkYXRhKSB7XG4gICAgICAgICAgICAgICAgZGF0YSA9IG5ldyBXaWRnZXQoZWwsIG9wdGlvbnMpO1xuICAgICAgICAgICAgICAgICRlbGVtZW50LmRhdGEoJ3dpZGdldCcsIGRhdGEpO1xuICAgICAgICAgICAgfVxuICAgICAgICB9KVxuICAgIH1cbn1cblxuXG5cbi8qKiBXRUJQQUNLIEZPT1RFUiAqKlxuICoqIC4vZnJvbnRlbmQvanMvcGVyc29uL21hbmFnZS13aWRnZXRzL19zb2NpYWwtY29ubmVjdC5qc1xuICoqLyJdLCJzb3VyY2VSb290IjoiIn0=