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

	module.exports = __webpack_require__(39);


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
/* 39 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';
	
	var _evaluationForm = __webpack_require__(40);
	
	var _evaluationForm2 = _interopRequireDefault(_evaluationForm);
	
	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }
	
	$(function () {
	    _evaluationForm2.default.plugin('.js-evaluation-form');
	    $('[data-evaluat-birth]').inputmask("99.99.9999");
	});

/***/ },
/* 40 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';
	
	Object.defineProperty(exports, "__esModule", {
	    value: true
	});
	
	var _stringify = __webpack_require__(41);
	
	var _stringify2 = _interopRequireDefault(_stringify);
	
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
	        this.currentStep = 1;
	        this.helperStep1 = new _formHelper2.default(this.locals.$controlsStep1);
	        this.helperStep2 = new _formHelper2.default(this.locals.$controlsStep2);
	
	        this._setDataFromLocal('step1', this.locals.$controlsStep1);
	        this._setDataFromLocal('step2', this.locals.$controlsStep2);
	        this._assignEvents();
	    }
	
	    (0, _createClass3.default)(Widget, [{
	        key: '_getDom',
	        value: function _getDom() {
	            var $root = this.$root;
	
	            return {
	                $controlsStep1: $root.find('.b-evalform__step.type-1 .form-control'),
	                $controlsStep2: $root.find('.b-evalform__step.type-2 .form-control'),
	                $addressBlock: $root.find('[data-eval-address]'),
	                $form: $root.find('[data-eval-form]'),
	                $step1: $root.find('[data-eval-step1]'),
	                $errorsStep1: $root.find('[data-eval-error1]'),
	                $step2: $root.find('[data-eval-step2]'),
	                $errorsStep2: $root.find('[data-eval-error2]')
	            };
	        }
	    }, {
	        key: '_assignEvents',
	        value: function _assignEvents() {
	            this.$root.on('click', '[data-eval-next]', this._onClickNextStep.bind(this)).on('click', '[data-eval-previous]', this._onClickPreviousStep.bind(this)).on('click', '[data-eval-toggle]', this._onClickToggleAddress.bind(this)).on('click', '[data-eval-submit]', this._onClickSubmitBtn.bind(this));
	
	            this.locals.$form.on('submit', this._onEventSubmit.bind(this));
	        }
	    }, {
	        key: '_onClickNextStep',
	        value: function _onClickNextStep(e) {
	            e && e.preventDefault();
	
	            if (!this.helperStep1.isValidInputs()) return;
	
	            this._saveDataToLocal('step1', this.helperStep1.getFormData());
	            this.showStep(2);
	        }
	    }, {
	        key: '_onClickPreviousStep',
	        value: function _onClickPreviousStep(e) {
	            e.preventDefault();
	
	            this._saveDataToLocal('step2', this.helperStep2.getFormData());
	            this.showStep(1);
	        }
	    }, {
	        key: '_onClickToggleAddress',
	        value: function _onClickToggleAddress(e) {
	            e.preventDefault();
	
	            var $link = $(e.currentTarget);
	            var isShowDetail = !$link.hasClass('state_active');
	
	            $link.toggleClass('state_active', isShowDetail);
	            this.locals.$addressBlock.slideToggle();
	        }
	    }, {
	        key: '_onClickSubmitBtn',
	        value: function _onClickSubmitBtn(e) {
	            e && e.preventDefault();
	            var self = this;
	
	            if (!self.helperStep2.isValidInputs()) return;
	            self._saveDataToLocal('step2', self.helperStep2.getFormData());
	
	            var formData = $.extend({}, self.helperStep1.getFormData(), self.helperStep2.getFormData());
	            self._sendEvaluation(formData).done(function () {
	                self._resetForm();
	                self.$root.addClass('p-evaluat_state_success');
	            }).fail(function (response) {
	                var data = $.parseJSON(response.responseText).data;
	                var errorText = self.helperStep2.getErrorsFull(data.errors);
	
	                if (!data.errors) return;
	
	                self.helperStep2.setErrors(data.errors, false);
	                self.locals.$errorsStep2.html(errorText);
	            });
	        }
	
	        /**
	         * Try to submit form on both steps
	         * @param {Event} e
	         * @private
	         */
	
	    }, {
	        key: '_onEventSubmit',
	        value: function _onEventSubmit(e) {
	            e.preventDefault();
	
	            if (this.currentStep == 1) {
	                this._onClickNextStep();
	            } else {
	                this._onClickSubmitBtn();
	            }
	        }
	
	        /**
	         * Show given step
	         * @param {Number} number
	         */
	
	    }, {
	        key: 'showStep',
	        value: function showStep() {
	            var number = arguments.length <= 0 || arguments[0] === undefined ? 1 : arguments[0];
	
	            var $root = this.$root;
	            var isShowStep2 = number == 2 && !$root.hasClass('p-evaluat_state_second');
	
	            this.currentStep = number;
	            $root.toggleClass('p-evaluat_state_second', isShowStep2);
	        }
	
	        /**
	         * Save data into localStorage
	         * @param {String} key
	         * @param {Object|null} data
	         * @private
	         */
	
	    }, {
	        key: '_saveDataToLocal',
	        value: function _saveDataToLocal(key, data) {
	            localStorage.setItem(key, (0, _stringify2.default)(data));
	        }
	
	        /**
	         * Get data from localStorage and set values to html controls
	         * @param {String} key - unique key for LocalStorage
	         * @param {jQuery} $controls - list of controls
	         * @private
	         */
	
	    }, {
	        key: '_setDataFromLocal',
	        value: function _setDataFromLocal(key, $controls) {
	            var item = localStorage.getItem(key);
	            if (!item) return false;
	
	            var formData = JSON.parse(item);
	            var $control = undefined;
	
	            for (var fieldName in formData) {
	                if (formData.hasOwnProperty(fieldName)) {
	                    $control = $controls.filter('[name="' + fieldName + '"]').first();
	
	                    if (!$control.length) continue;
	                    $control.val(formData[fieldName]);
	                }
	            }
	        }
	    }, {
	        key: '_resetForm',
	        value: function _resetForm() {
	            this.helperStep1.clearForm();
	            this._saveDataToLocal('step1', null);
	
	            this.helperStep2.clearForm();
	            this.locals.$errorsStep2.html('');
	            this._saveDataToLocal('step2', null);
	        }
	
	        //transport
	
	    }, {
	        key: '_sendEvaluation',
	        value: function _sendEvaluation(formData) {
	            var url = this.locals.$form.data('action');
	            return $.post(url, formData);
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
/* 41 */
/***/ function(module, exports, __webpack_require__) {

	module.exports = { "default": __webpack_require__(42), __esModule: true };

/***/ },
/* 42 */
/***/ function(module, exports, __webpack_require__) {

	var core  = __webpack_require__(10)
	  , $JSON = core.JSON || (core.JSON = {stringify: JSON.stringify});
	module.exports = function stringify(it){ // eslint-disable-line no-unused-vars
	  return $JSON.stringify.apply($JSON, arguments);
	};

/***/ }
/******/ ]);
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIndlYnBhY2s6Ly8vd2VicGFjay9ib290c3RyYXAgZjBmMTY2OTBiZDk3YTI1NWNmYjk/N2VmNCoqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL2hlbHBlcnMvY2xhc3NDYWxsQ2hlY2suanM/MjFhZioqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL2hlbHBlcnMvY3JlYXRlQ2xhc3MuanM/MWRmZSoqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL2NvcmUtanMvb2JqZWN0L2RlZmluZS1wcm9wZXJ0eS5qcz81ZjcwKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvZm4vb2JqZWN0L2RlZmluZS1wcm9wZXJ0eS5qcz80ZDMzKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9lczYub2JqZWN0LmRlZmluZS1wcm9wZXJ0eS5qcz84YmRlKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZXhwb3J0LmpzPzhlNDAqKioqIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19nbG9iYWwuanM/M2M1MioqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2NvcmUuanM/ZDYxMSoqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2N0eC5qcz80ZTU5KioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fYS1mdW5jdGlvbi5qcz8wNjk5KioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9faGlkZS5qcz8wZDJlKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fb2JqZWN0LWRwLmpzPzU5ODYqKioqIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19hbi1vYmplY3QuanM/M2FmMioqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2lzLW9iamVjdC5qcz9jZmRhKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9faWU4LWRvbS1kZWZpbmUuanM/YjEwMioqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2Rlc2NyaXB0b3JzLmpzP2MwZjUqKioqIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19mYWlscy5qcz9jNmRkKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZG9tLWNyZWF0ZS5qcz82MTJmKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fdG8tcHJpbWl0aXZlLmpzPzFhNjUqKioqIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19wcm9wZXJ0eS1kZXNjLmpzPzI1NmIqKioqIiwid2VicGFjazovLy8uL2Zyb250ZW5kL2pzL2NvbW1vbi9fZm9ybS1oZWxwZXIuanM/ODYzNioiLCJ3ZWJwYWNrOi8vLy4vZnJvbnRlbmQvanMvZXZhbHVhdGlvbi9ldmFsdWF0aW9uLXB1YmxpYy5qcyIsIndlYnBhY2s6Ly8vLi9mcm9udGVuZC9qcy9ldmFsdWF0aW9uL3B1YmxpYy13aWRnZXRzL19ldmFsdWF0aW9uLWZvcm0uanMiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL2NvcmUtanMvanNvbi9zdHJpbmdpZnkuanMiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L2ZuL2pzb24vc3RyaW5naWZ5LmpzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiI7QUFBQTtBQUNBOztBQUVBO0FBQ0E7O0FBRUE7QUFDQTtBQUNBOztBQUVBO0FBQ0E7QUFDQSx1QkFBZTtBQUNmO0FBQ0E7QUFDQTs7QUFFQTtBQUNBOztBQUVBO0FBQ0E7O0FBRUE7QUFDQTtBQUNBOzs7QUFHQTtBQUNBOztBQUVBO0FBQ0E7O0FBRUE7QUFDQTs7QUFFQTtBQUNBOzs7Ozs7Ozs7Ozs7Ozs7O0FDdENBOztBQUVBOztBQUVBO0FBQ0E7QUFDQTtBQUNBO0FBQ0EsRzs7Ozs7O0FDUkE7O0FBRUE7O0FBRUE7O0FBRUE7O0FBRUEsdUNBQXNDLHVDQUF1QyxnQkFBZ0I7O0FBRTdGO0FBQ0E7QUFDQSxvQkFBbUIsa0JBQWtCO0FBQ3JDO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBOztBQUVBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQSxFQUFDLEc7Ozs7OztBQzFCRCxtQkFBa0IsdUQ7Ozs7OztBQ0FsQjtBQUNBO0FBQ0E7QUFDQTtBQUNBLEc7Ozs7OztBQ0pBO0FBQ0E7QUFDQSxzRUFBdUUsMENBQTBDLEU7Ozs7OztBQ0ZqSDtBQUNBO0FBQ0E7QUFDQTtBQUNBOztBQUVBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0Esb0VBQW1FO0FBQ25FO0FBQ0Esc0ZBQXFGO0FBQ3JGO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQSxZQUFXO0FBQ1gsVUFBUztBQUNUO0FBQ0E7QUFDQTtBQUNB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Q0FGZ0M7O0FBSXBDLHFCQUFJLENBQUMsSUFBRCxFQUFPO0FBQ1AsNEJBQU8sSUFBSSxNQUFKLENBQVcsRUFBWCxDQUFQLENBRE87QUFFUCw4QkFBUyxJQUFULENBQWMsUUFBZCxFQUF3QixJQUF4QixFQUZPO2tCQUFYO2NBSmUsQ0FBbkIsQ0FKb0I7OztZQXhLUDs7Ozs7Ozs7O0FDSnJCLG1CQUFrQix3RDs7Ozs7O0FDQWxCO0FBQ0Esd0NBQXVDLDBCQUEwQjtBQUNqRSx5Q0FBd0M7QUFDeEM7QUFDQSxHIiwiZmlsZSI6ImV2YWx1YXRpb24tcHVibGljLmpzIiwic291cmNlc0NvbnRlbnQiOlsiIFx0Ly8gVGhlIG1vZHVsZSBjYWNoZVxuIFx0dmFyIGluc3RhbGxlZE1vZHVsZXMgPSB7fTtcblxuIFx0Ly8gVGhlIHJlcXVpcmUgZnVuY3Rpb25cbiBcdGZ1bmN0aW9uIF9fd2VicGFja19yZXF1aXJlX18obW9kdWxlSWQpIHtcblxuIFx0XHQvLyBDaGVjayBpZiBtb2R1bGUgaXMgaW4gY2FjaGVcbiBcdFx0aWYoaW5zdGFsbGVkTW9kdWxlc1ttb2R1bGVJZF0pXG4gXHRcdFx0cmV0dXJuIGluc3RhbGxlZE1vZHVsZXNbbW9kdWxlSWRdLmV4cG9ydHM7XG5cbiBcdFx0Ly8gQ3JlYXRlIGEgbmV3IG1vZHVsZSAoYW5kIHB1dCBpdCBpbnRvIHRoZSBjYWNoZSlcbiBcdFx0dmFyIG1vZHVsZSA9IGluc3RhbGxlZE1vZHVsZXNbbW9kdWxlSWRdID0ge1xuIFx0XHRcdGV4cG9ydHM6IHt9LFxuIFx0XHRcdGlkOiBtb2R1bGVJZCxcbiBcdFx0XHRsb2FkZWQ6IGZhbHNlXG4gXHRcdH07XG5cbiBcdFx0Ly8gRXhlY3V0ZSB0aGUgbW9kdWxlIGZ1bmN0aW9uXG4gXHRcdG1vZHVsZXNbbW9kdWxlSWRdLmNhbGwobW9kdWxlLmV4cG9ydHMsIG1vZHVsZSwgbW9kdWxlLmV4cG9ydHMsIF9fd2VicGFja19yZXF1aXJlX18pO1xuXG4gXHRcdC8vIEZsYWcgdGhlIG1vZHVsZSBhcyBsb2FkZWRcbiBcdFx0bW9kdWxlLmxvYWRlZCA9IHRydWU7XG5cbiBcdFx0Ly8gUmV0dXJuIHRoZSBleHBvcnRzIG9mIHRoZSBtb2R1bGVcbiBcdFx0cmV0dXJuIG1vZHVsZS5leHBvcnRzO1xuIFx0fVxuXG5cbiBcdC8vIGV4cG9zZSB0aGUgbW9kdWxlcyBvYmplY3QgKF9fd2VicGFja19tb2R1bGVzX18pXG4gXHRfX3dlYnBhY2tfcmVxdWlyZV9fLm0gPSBtb2R1bGVzO1xuXG4gXHQvLyBleHBvc2UgdGhlIG1vZHVsZSBjYWNoZVxuIFx0X193ZWJwYWNrX3JlcXVpcmVfXy5jID0gaW5zdGFsbGVkTW9kdWxlcztcblxuIFx0Ly8gX193ZWJwYWNrX3B1YmxpY19wYXRoX19cbiBcdF9fd2VicGFja19yZXF1aXJlX18ucCA9IFwiXCI7XG5cbiBcdC8vIExvYWQgZW50cnkgbW9kdWxlIGFuZCByZXR1cm4gZXhwb3J0c1xuIFx0cmV0dXJuIF9fd2VicGFja19yZXF1aXJlX18oMCk7XG5cblxuXG4vKiogV0VCUEFDSyBGT09URVIgKipcbiAqKiB3ZWJwYWNrL2Jvb3RzdHJhcCBmMGYxNjY5MGJkOTdhMjU1Y2ZiOVxuICoqLyIsIlwidXNlIHN0cmljdFwiO1xuXG5leHBvcnRzLl9fZXNNb2R1bGUgPSB0cnVlO1xuXG5leHBvcnRzLmRlZmF1bHQgPSBmdW5jdGlvbiAoaW5zdGFuY2UsIENvbnN0cnVjdG9yKSB7XG4gIGlmICghKGluc3RhbmNlIGluc3RhbmNlb2YgQ29uc3RydWN0b3IpKSB7XG4gICAgdGhyb3cgbmV3IFR5cGVFcnJvcihcIkNhbm5vdCBjYWxsIGEgY2xhc3MgYXMgYSBmdW5jdGlvblwiKTtcbiAgfVxufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL2hlbHBlcnMvY2xhc3NDYWxsQ2hlY2suanNcbiAqKiBtb2R1bGUgaWQgPSAzXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJcInVzZSBzdHJpY3RcIjtcblxuZXhwb3J0cy5fX2VzTW9kdWxlID0gdHJ1ZTtcblxudmFyIF9kZWZpbmVQcm9wZXJ0eSA9IHJlcXVpcmUoXCJiYWJlbC1ydW50aW1lL2NvcmUtanMvb2JqZWN0L2RlZmluZS1wcm9wZXJ0eVwiKTtcblxudmFyIF9kZWZpbmVQcm9wZXJ0eTIgPSBfaW50ZXJvcFJlcXVpcmVEZWZhdWx0KF9kZWZpbmVQcm9wZXJ0eSk7XG5cbmZ1bmN0aW9uIF9pbnRlcm9wUmVxdWlyZURlZmF1bHQob2JqKSB7IHJldHVybiBvYmogJiYgb2JqLl9fZXNNb2R1bGUgPyBvYmogOiB7IGRlZmF1bHQ6IG9iaiB9OyB9XG5cbmV4cG9ydHMuZGVmYXVsdCA9IGZ1bmN0aW9uICgpIHtcbiAgZnVuY3Rpb24gZGVmaW5lUHJvcGVydGllcyh0YXJnZXQsIHByb3BzKSB7XG4gICAgZm9yICh2YXIgaSA9IDA7IGkgPCBwcm9wcy5sZW5ndGg7IGkrKykge1xuICAgICAgdmFyIGRlc2NyaXB0b3IgPSBwcm9wc1tpXTtcbiAgICAgIGRlc2NyaXB0b3IuZW51bWVyYWJsZSA9IGRlc2NyaXB0b3IuZW51bWVyYWJsZSB8fCBmYWxzZTtcbiAgICAgIGRlc2NyaXB0b3IuY29uZmlndXJhYmxlID0gdHJ1ZTtcbiAgICAgIGlmIChcInZhbHVlXCIgaW4gZGVzY3JpcHRvcikgZGVzY3JpcHRvci53cml0YWJsZSA9IHRydWU7XG4gICAgICAoMCwgX2RlZmluZVByb3BlcnR5Mi5kZWZhdWx0KSh0YXJnZXQsIGRlc2NyaXB0b3Iua2V5LCBkZXNjcmlwdG9yKTtcbiAgICB9XG4gIH1cblxuICByZXR1cm4gZnVuY3Rpb24gKENvbnN0cnVjdG9yLCBwcm90b1Byb3BzLCBzdGF0aWNQcm9wcykge1xuICAgIGlmIChwcm90b1Byb3BzKSBkZWZpbmVQcm9wZXJ0aWVzKENvbnN0cnVjdG9yLnByb3RvdHlwZSwgcHJvdG9Qcm9wcyk7XG4gICAgaWYgKHN0YXRpY1Byb3BzKSBkZWZpbmVQcm9wZXJ0aWVzKENvbnN0cnVjdG9yLCBzdGF0aWNQcm9wcyk7XG4gICAgcmV0dXJuIENvbnN0cnVjdG9yO1xuICB9O1xufSgpO1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvaGVscGVycy9jcmVhdGVDbGFzcy5qc1xuICoqIG1vZHVsZSBpZCA9IDRcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsIm1vZHVsZS5leHBvcnRzID0geyBcImRlZmF1bHRcIjogcmVxdWlyZShcImNvcmUtanMvbGlicmFyeS9mbi9vYmplY3QvZGVmaW5lLXByb3BlcnR5XCIpLCBfX2VzTW9kdWxlOiB0cnVlIH07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9jb3JlLWpzL29iamVjdC9kZWZpbmUtcHJvcGVydHkuanNcbiAqKiBtb2R1bGUgaWQgPSA1XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJyZXF1aXJlKCcuLi8uLi9tb2R1bGVzL2VzNi5vYmplY3QuZGVmaW5lLXByb3BlcnR5Jyk7XG52YXIgJE9iamVjdCA9IHJlcXVpcmUoJy4uLy4uL21vZHVsZXMvX2NvcmUnKS5PYmplY3Q7XG5tb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uIGRlZmluZVByb3BlcnR5KGl0LCBrZXksIGRlc2Mpe1xuICByZXR1cm4gJE9iamVjdC5kZWZpbmVQcm9wZXJ0eShpdCwga2V5LCBkZXNjKTtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9mbi9vYmplY3QvZGVmaW5lLXByb3BlcnR5LmpzXG4gKiogbW9kdWxlIGlkID0gNlxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwidmFyICRleHBvcnQgPSByZXF1aXJlKCcuL19leHBvcnQnKTtcclxuLy8gMTkuMS4yLjQgLyAxNS4yLjMuNiBPYmplY3QuZGVmaW5lUHJvcGVydHkoTywgUCwgQXR0cmlidXRlcylcclxuJGV4cG9ydCgkZXhwb3J0LlMgKyAkZXhwb3J0LkYgKiAhcmVxdWlyZSgnLi9fZGVzY3JpcHRvcnMnKSwgJ09iamVjdCcsIHtkZWZpbmVQcm9wZXJ0eTogcmVxdWlyZSgnLi9fb2JqZWN0LWRwJykuZn0pO1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9lczYub2JqZWN0LmRlZmluZS1wcm9wZXJ0eS5qc1xuICoqIG1vZHVsZSBpZCA9IDdcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsInZhciBnbG9iYWwgICAgPSByZXF1aXJlKCcuL19nbG9iYWwnKVxuICAsIGNvcmUgICAgICA9IHJlcXVpcmUoJy4vX2NvcmUnKVxuICAsIGN0eCAgICAgICA9IHJlcXVpcmUoJy4vX2N0eCcpXG4gICwgaGlkZSAgICAgID0gcmVxdWlyZSgnLi9faGlkZScpXG4gICwgUFJPVE9UWVBFID0gJ3Byb3RvdHlwZSc7XG5cbnZhciAkZXhwb3J0ID0gZnVuY3Rpb24odHlwZSwgbmFtZSwgc291cmNlKXtcbiAgdmFyIElTX0ZPUkNFRCA9IHR5cGUgJiAkZXhwb3J0LkZcbiAgICAsIElTX0dMT0JBTCA9IHR5cGUgJiAkZXhwb3J0LkdcbiAgICAsIElTX1NUQVRJQyA9IHR5cGUgJiAkZXhwb3J0LlNcbiAgICAsIElTX1BST1RPICA9IHR5cGUgJiAkZXhwb3J0LlBcbiAgICAsIElTX0JJTkQgICA9IHR5cGUgJiAkZXhwb3J0LkJcbiAgICAsIElTX1dSQVAgICA9IHR5cGUgJiAkZXhwb3J0LldcbiAgICAsIGV4cG9ydHMgICA9IElTX0dMT0JBTCA/IGNvcmUgOiBjb3JlW25hbWVdIHx8IChjb3JlW25hbWVdID0ge30pXG4gICAgLCBleHBQcm90byAgPSBleHBvcnRzW1BST1RPVFlQRV1cbiAgICAsIHRhcmdldCAgICA9IElTX0dMT0JBTCA/IGdsb2JhbCA6IElTX1NUQVRJQyA/IGdsb2JhbFtuYW1lXSA6IChnbG9iYWxbbmFtZV0gfHwge30pW1BST1RPVFlQRV1cbiAgICAsIGtleSwgb3duLCBvdXQ7XG4gIGlmKElTX0dMT0JBTClzb3VyY2UgPSBuYW1lO1xuICBmb3Ioa2V5IGluIHNvdXJjZSl7XG4gICAgLy8gY29udGFpbnMgaW4gbmF0aXZlXG4gICAgb3duID0gIUlTX0ZPUkNFRCAmJiB0YXJnZXQgJiYgdGFyZ2V0W2tleV0gIT09IHVuZGVmaW5lZDtcbiAgICBpZihvd24gJiYga2V5IGluIGV4cG9ydHMpY29udGludWU7XG4gICAgLy8gZXhwb3J0IG5hdGl2ZSBvciBwYXNzZWRcbiAgICBvdXQgPSBvd24gPyB0YXJnZXRba2V5XSA6IHNvdXJjZVtrZXldO1xuICAgIC8vIHByZXZlbnQgZ2xvYmFsIHBvbGx1dGlvbiBmb3IgbmFtZXNwYWNlc1xuICAgIGV4cG9ydHNba2V5XSA9IElTX0dMT0JBTCAmJiB0eXBlb2YgdGFyZ2V0W2tleV0gIT0gJ2Z1bmN0aW9uJyA/IHNvdXJjZVtrZXldXG4gICAgLy8gYmluZCB0aW1lcnMgdG8gZ2xvYmFsIGZvciBjYWxsIGZyb20gZXhwb3J0IGNvbnRleHRcbiAgICA6IElTX0JJTkQgJiYgb3duID8gY3R4KG91dCwgZ2xvYmFsKVxuICAgIC8vIHdyYXAgZ2xvYmFsIGNvbnN0cnVjdG9ycyBmb3IgcHJldmVudCBjaGFuZ2UgdGhlbSBpbiBsaWJyYXJ5XG4gICAgOiBJU19XUkFQICYmIHRhcmdldFtrZXldID09IG91dCA/IChmdW5jdGlvbihDKXtcbiAgICAgIHZhciBGID0gZnVuY3Rpb24oYSwgYiwgYyl7XG4gICAgICAgIGlmKHRoaXMgaW5zdGFuY2VvZiBDKXtcbiAgICAgICAgICBzd2l0Y2goYXJndW1lbnRzLmxlbmd0aCl7XG4gICAgICAgICAgICBjYXNlIDA6IHJldHVybiBuZXcgQztcbiAgICAgICAgICAgIGNhc2UgMTogcmV0dXJuIG5ldyBDKGEpO1xuICAgICAgICAgICAgY2FzZSAyOiByZXR1cm4gbmV3IEMoYSwgYik7XG4gICAgICAgICAgfSByZXR1cm4gbmV3IEMoYSwgYiwgYyk7XG4gICAgICAgIH0gcmV0dXJuIEMuYXBwbHkodGhpcywgYXJndW1lbnRzKTtcbiAgICAgIH07XG4gICAgICBGW1BST1RPVFlQRV0gPSBDW1BST1RPVFlQRV07XG4gICAgICByZXR1cm4gRjtcbiAgICAvLyBtYWtlIHN0YXRpYyB2ZXJzaW9ucyBmb3IgcHJvdG90eXBlIG1ldGhvZHNcbiAgICB9KShvdXQpIDogSVNfUFJPVE8gJiYgdHlwZW9mIG91dCA9PSAnZnVuY3Rpb24nID8gY3R4KEZ1bmN0aW9uLmNhbGwsIG91dCkgOiBvdXQ7XG4gICAgLy8gZXhwb3J0IHByb3RvIG1ldGhvZHMgdG8gY29yZS4lQ09OU1RSVUNUT1IlLm1ldGhvZHMuJU5BTUUlXG4gICAgaWYoSVNfUFJPVE8pe1xuICAgICAgKGV4cG9ydHMudmlydHVhbCB8fCAoZXhwb3J0cy52aXJ0dWFsID0ge30pKVtrZXldID0gb3V0O1xuICAgICAgLy8gZXhwb3J0IHByb3RvIG1ldGhvZHMgdG8gY29yZS4lQ09OU1RSVUNUT1IlLnByb3RvdHlwZS4lTkFNRSVcbiAgICAgIGlmKHR5cGUgJiAkZXhwb3J0LlIgJiYgZXhwUHJvdG8gJiYgIWV4cFByb3RvW2tleV0paGlkZShleHBQcm90bywga2V5LCBvdXQpO1xuICAgIH1cbiAgfVxufTtcbi8vIHR5cGUgYml0bWFwXG4kZXhwb3J0LkYgPSAxOyAgIC8vIGZvcmNlZFxuJGV4cG9ydC5HID0gMjsgICAvLyBnbG9iYWxcbiRleHBvcnQuUyA9IDQ7ICAgLy8gc3RhdGljXG4kZXhwb3J0LlAgPSA4OyAgIC8vIHByb3RvXG4kZXhwb3J0LkIgPSAxNjsgIC8vIGJpbmRcbiRleHBvcnQuVyA9IDMyOyAgLy8gd3JhcFxuJGV4cG9ydC5VID0gNjQ7ICAvLyBzYWZlXG4kZXhwb3J0LlIgPSAxMjg7IC8vIHJlYWwgcHJvdG8gbWV0aG9kIGZvciBgbGlicmFyeWAgXG5tb2R1bGUuZXhwb3J0cyA9ICRleHBvcnQ7XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19leHBvcnQuanNcbiAqKiBtb2R1bGUgaWQgPSA4XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCIvLyBodHRwczovL2dpdGh1Yi5jb20vemxvaXJvY2svY29yZS1qcy9pc3N1ZXMvODYjaXNzdWVjb21tZW50LTExNTc1OTAyOFxudmFyIGdsb2JhbCA9IG1vZHVsZS5leHBvcnRzID0gdHlwZW9mIHdpbmRvdyAhPSAndW5kZWZpbmVkJyAmJiB3aW5kb3cuTWF0aCA9PSBNYXRoXG4gID8gd2luZG93IDogdHlwZW9mIHNlbGYgIT0gJ3VuZGVmaW5lZCcgJiYgc2VsZi5NYXRoID09IE1hdGggPyBzZWxmIDogRnVuY3Rpb24oJ3JldHVybiB0aGlzJykoKTtcbmlmKHR5cGVvZiBfX2cgPT0gJ251bWJlcicpX19nID0gZ2xvYmFsOyAvLyBlc2xpbnQtZGlzYWJsZS1saW5lIG5vLXVuZGVmXG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19nbG9iYWwuanNcbiAqKiBtb2R1bGUgaWQgPSA5XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJ2YXIgY29yZSA9IG1vZHVsZS5leHBvcnRzID0ge3ZlcnNpb246ICcyLjIuMCd9O1xuaWYodHlwZW9mIF9fZSA9PSAnbnVtYmVyJylfX2UgPSBjb3JlOyAvLyBlc2xpbnQtZGlzYWJsZS1saW5lIG5vLXVuZGVmXG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19jb3JlLmpzXG4gKiogbW9kdWxlIGlkID0gMTBcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsIi8vIG9wdGlvbmFsIC8gc2ltcGxlIGNvbnRleHQgYmluZGluZ1xudmFyIGFGdW5jdGlvbiA9IHJlcXVpcmUoJy4vX2EtZnVuY3Rpb24nKTtcbm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oZm4sIHRoYXQsIGxlbmd0aCl7XG4gIGFGdW5jdGlvbihmbik7XG4gIGlmKHRoYXQgPT09IHVuZGVmaW5lZClyZXR1cm4gZm47XG4gIHN3aXRjaChsZW5ndGgpe1xuICAgIGNhc2UgMTogcmV0dXJuIGZ1bmN0aW9uKGEpe1xuICAgICAgcmV0dXJuIGZuLmNhbGwodGhhdCwgYSk7XG4gICAgfTtcbiAgICBjYXNlIDI6IHJldHVybiBmdW5jdGlvbihhLCBiKXtcbiAgICAgIHJldHVybiBmbi5jYWxsKHRoYXQsIGEsIGIpO1xuICAgIH07XG4gICAgY2FzZSAzOiByZXR1cm4gZnVuY3Rpb24oYSwgYiwgYyl7XG4gICAgICByZXR1cm4gZm4uY2FsbCh0aGF0LCBhLCBiLCBjKTtcbiAgICB9O1xuICB9XG4gIHJldHVybiBmdW5jdGlvbigvKiAuLi5hcmdzICovKXtcbiAgICByZXR1cm4gZm4uYXBwbHkodGhhdCwgYXJndW1lbnRzKTtcbiAgfTtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19jdHguanNcbiAqKiBtb2R1bGUgaWQgPSAxMVxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwibW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihpdCl7XG4gIGlmKHR5cGVvZiBpdCAhPSAnZnVuY3Rpb24nKXRocm93IFR5cGVFcnJvcihpdCArICcgaXMgbm90IGEgZnVuY3Rpb24hJyk7XG4gIHJldHVybiBpdDtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19hLWZ1bmN0aW9uLmpzXG4gKiogbW9kdWxlIGlkID0gMTJcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsInZhciBkUCAgICAgICAgID0gcmVxdWlyZSgnLi9fb2JqZWN0LWRwJylcbiAgLCBjcmVhdGVEZXNjID0gcmVxdWlyZSgnLi9fcHJvcGVydHktZGVzYycpO1xubW9kdWxlLmV4cG9ydHMgPSByZXF1aXJlKCcuL19kZXNjcmlwdG9ycycpID8gZnVuY3Rpb24ob2JqZWN0LCBrZXksIHZhbHVlKXtcbiAgcmV0dXJuIGRQLmYob2JqZWN0LCBrZXksIGNyZWF0ZURlc2MoMSwgdmFsdWUpKTtcbn0gOiBmdW5jdGlvbihvYmplY3QsIGtleSwgdmFsdWUpe1xuICBvYmplY3Rba2V5XSA9IHZhbHVlO1xuICByZXR1cm4gb2JqZWN0O1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2hpZGUuanNcbiAqKiBtb2R1bGUgaWQgPSAxM1xuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwidmFyIGFuT2JqZWN0ICAgICAgID0gcmVxdWlyZSgnLi9fYW4tb2JqZWN0JylcbiAgLCBJRThfRE9NX0RFRklORSA9IHJlcXVpcmUoJy4vX2llOC1kb20tZGVmaW5lJylcbiAgLCB0b1ByaW1pdGl2ZSAgICA9IHJlcXVpcmUoJy4vX3RvLXByaW1pdGl2ZScpXG4gICwgZFAgICAgICAgICAgICAgPSBPYmplY3QuZGVmaW5lUHJvcGVydHk7XG5cbmV4cG9ydHMuZiA9IHJlcXVpcmUoJy4vX2Rlc2NyaXB0b3JzJykgPyBPYmplY3QuZGVmaW5lUHJvcGVydHkgOiBmdW5jdGlvbiBkZWZpbmVQcm9wZXJ0eShPLCBQLCBBdHRyaWJ1dGVzKXtcbiAgYW5PYmplY3QoTyk7XG4gIFAgPSB0b1ByaW1pdGl2ZShQLCB0cnVlKTtcbiAgYW5PYmplY3QoQXR0cmlidXRlcyk7XG4gIGlmKElFOF9ET01fREVGSU5FKXRyeSB7XG4gICAgcmV0dXJuIGRQKE8sIFAsIEF0dHJpYnV0ZXMpO1xuICB9IGNhdGNoKGUpeyAvKiBlbXB0eSAqLyB9XG4gIGlmKCdnZXQnIGluIEF0dHJpYnV0ZXMgfHwgJ3NldCcgaW4gQXR0cmlidXRlcyl0aHJvdyBUeXBlRXJyb3IoJ0FjY2Vzc29ycyBub3Qgc3VwcG9ydGVkIScpO1xuICBpZigndmFsdWUnIGluIEF0dHJpYnV0ZXMpT1tQXSA9IEF0dHJpYnV0ZXMudmFsdWU7XG4gIHJldHVybiBPO1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX29iamVjdC1kcC5qc1xuICoqIG1vZHVsZSBpZCA9IDE0XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJ2YXIgaXNPYmplY3QgPSByZXF1aXJlKCcuL19pcy1vYmplY3QnKTtcbm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oaXQpe1xuICBpZighaXNPYmplY3QoaXQpKXRocm93IFR5cGVFcnJvcihpdCArICcgaXMgbm90IGFuIG9iamVjdCEnKTtcbiAgcmV0dXJuIGl0O1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2FuLW9iamVjdC5qc1xuICoqIG1vZHVsZSBpZCA9IDE1XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJtb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGl0KXtcbiAgcmV0dXJuIHR5cGVvZiBpdCA9PT0gJ29iamVjdCcgPyBpdCAhPT0gbnVsbCA6IHR5cGVvZiBpdCA9PT0gJ2Z1bmN0aW9uJztcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19pcy1vYmplY3QuanNcbiAqKiBtb2R1bGUgaWQgPSAxNlxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwibW9kdWxlLmV4cG9ydHMgPSAhcmVxdWlyZSgnLi9fZGVzY3JpcHRvcnMnKSAmJiAhcmVxdWlyZSgnLi9fZmFpbHMnKShmdW5jdGlvbigpe1xyXG4gIHJldHVybiBPYmplY3QuZGVmaW5lUHJvcGVydHkocmVxdWlyZSgnLi9fZG9tLWNyZWF0ZScpKCdkaXYnKSwgJ2EnLCB7Z2V0OiBmdW5jdGlvbigpeyByZXR1cm4gNzsgfX0pLmEgIT0gNztcclxufSk7XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19pZTgtZG9tLWRlZmluZS5qc1xuICoqIG1vZHVsZSBpZCA9IDE3XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCIvLyBUaGFuaydzIElFOCBmb3IgaGlzIGZ1bm55IGRlZmluZVByb3BlcnR5XG5tb2R1bGUuZXhwb3J0cyA9ICFyZXF1aXJlKCcuL19mYWlscycpKGZ1bmN0aW9uKCl7XG4gIHJldHVybiBPYmplY3QuZGVmaW5lUHJvcGVydHkoe30sICdhJywge2dldDogZnVuY3Rpb24oKXsgcmV0dXJuIDc7IH19KS5hICE9IDc7XG59KTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2Rlc2NyaXB0b3JzLmpzXG4gKiogbW9kdWxlIGlkID0gMThcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsIm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oZXhlYyl7XG4gIHRyeSB7XG4gICAgcmV0dXJuICEhZXhlYygpO1xuICB9IGNhdGNoKGUpe1xuICAgIHJldHVybiB0cnVlO1xuICB9XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZmFpbHMuanNcbiAqKiBtb2R1bGUgaWQgPSAxOVxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwidmFyIGlzT2JqZWN0ID0gcmVxdWlyZSgnLi9faXMtb2JqZWN0JylcbiAgLCBkb2N1bWVudCA9IHJlcXVpcmUoJy4vX2dsb2JhbCcpLmRvY3VtZW50XG4gIC8vIGluIG9sZCBJRSB0eXBlb2YgZG9jdW1lbnQuY3JlYXRlRWxlbWVudCBpcyAnb2JqZWN0J1xuICAsIGlzID0gaXNPYmplY3QoZG9jdW1lbnQpICYmIGlzT2JqZWN0KGRvY3VtZW50LmNyZWF0ZUVsZW1lbnQpO1xubW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihpdCl7XG4gIHJldHVybiBpcyA/IGRvY3VtZW50LmNyZWF0ZUVsZW1lbnQoaXQpIDoge307XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZG9tLWNyZWF0ZS5qc1xuICoqIG1vZHVsZSBpZCA9IDIwXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCIvLyA3LjEuMSBUb1ByaW1pdGl2ZShpbnB1dCBbLCBQcmVmZXJyZWRUeXBlXSlcbnZhciBpc09iamVjdCA9IHJlcXVpcmUoJy4vX2lzLW9iamVjdCcpO1xuLy8gaW5zdGVhZCBvZiB0aGUgRVM2IHNwZWMgdmVyc2lvbiwgd2UgZGlkbid0IGltcGxlbWVudCBAQHRvUHJpbWl0aXZlIGNhc2Vcbi8vIGFuZCB0aGUgc2Vjb25kIGFyZ3VtZW50IC0gZmxhZyAtIHByZWZlcnJlZCB0eXBlIGlzIGEgc3RyaW5nXG5tb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGl0LCBTKXtcbiAgaWYoIWlzT2JqZWN0KGl0KSlyZXR1cm4gaXQ7XG4gIHZhciBmbiwgdmFsO1xuICBpZihTICYmIHR5cGVvZiAoZm4gPSBpdC50b1N0cmluZykgPT0gJ2Z1bmN0aW9uJyAmJiAhaXNPYmplY3QodmFsID0gZm4uY2FsbChpdCkpKXJldHVybiB2YWw7XG4gIGlmKHR5cGVvZiAoZm4gPSBpdC52YWx1ZU9mKSA9PSAnZnVuY3Rpb24nICYmICFpc09iamVjdCh2YWwgPSBmbi5jYWxsKGl0KSkpcmV0dXJuIHZhbDtcbiAgaWYoIVMgJiYgdHlwZW9mIChmbiA9IGl0LnRvU3RyaW5nKSA9PSAnZnVuY3Rpb24nICYmICFpc09iamVjdCh2YWwgPSBmbi5jYWxsKGl0KSkpcmV0dXJuIHZhbDtcbiAgdGhyb3cgVHlwZUVycm9yKFwiQ2FuJ3QgY29udmVydCBvYmplY3QgdG8gcHJpbWl0aXZlIHZhbHVlXCIpO1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX3RvLXByaW1pdGl2ZS5qc1xuICoqIG1vZHVsZSBpZCA9IDIxXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJtb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGJpdG1hcCwgdmFsdWUpe1xuICByZXR1cm4ge1xuICAgIGVudW1lcmFibGUgIDogIShiaXRtYXAgJiAxKSxcbiAgICBjb25maWd1cmFibGU6ICEoYml0bWFwICYgMiksXG4gICAgd3JpdGFibGUgICAgOiAhKGJpdG1hcCAmIDQpLFxuICAgIHZhbHVlICAgICAgIDogdmFsdWVcbiAgfTtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19wcm9wZXJ0eS1kZXNjLmpzXG4gKiogbW9kdWxlIGlkID0gMjJcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsIid1c2Ugc3RyaWN0JztcblxuLyoqXG4gKiBFcnJvcnNcbiAqIEB0eXBlZGVmIHtPYmplY3R9IExpc3RFcnJvcnNcbiAqIEBwcm9wZXJ0eSB7U3RyaW5nfSBuYW1lIC0gbmFtZSBvZiBmaWVsZFxuICogQHByb3BlcnR5IHtTdHJpbmd9IGVycm9yIC0gZXJyb3IgZGVzY3JpcHRpb25cbiAqL1xuXG5leHBvcnQgZGVmYXVsdCBjbGFzcyBGb3JtSGVscGVyIHtcbiAgICAvKipcbiAgICAgKiBWYWxpZGF0ZSBmb3JtIHRocm91Z2ggaW5wdXRzXG4gICAgICogQHBhcmFtIHtqUXVlcnl9ICRjb250cm9sc1xuICAgICAqL1xuICAgIGNvbnN0cnVjdG9yKCRjb250cm9scykge1xuICAgICAgICB0aGlzLiRjb250cm9scyA9ICRjb250cm9scztcbiAgICAgICAgdGhpcy5hcnJFcnJvcnMgPSBbXTtcbiAgICAgICAgdGhpcy5fYXNzaWduRXZlbnRzKCk7XG4gICAgfVxuXG4gICAgX2Fzc2lnbkV2ZW50cygpIHtcbiAgICAgICAgdGhpcy4kY29udHJvbHMub24oJ2lucHV0IGNoYW5nZScsIChlKSA9PiB7XG4gICAgICAgICAgICBjb25zdCAkY29udHJvbCA9ICQoZS5jdXJyZW50VGFyZ2V0KTtcblxuICAgICAgICAgICAgdGhpcy5fdmFsaWRhdGVJbW1lZGlhdGUoJGNvbnRyb2wpO1xuICAgICAgICAgICAgdGhpcy5fcmVtb3ZlRXJyb3IoJGNvbnRyb2wpO1xuICAgICAgICB9KTtcbiAgICB9XG5cbiAgICBfdmFsaWRhdGVJbW1lZGlhdGUoJGNvbnRyb2wpe1xuICAgICAgICBpZiAoJGNvbnRyb2wuaGFzQ2xhc3MoJ3R5cGUtbnVtZXJpYycpKSB7XG4gICAgICAgICAgICAkY29udHJvbC52YWwoJGNvbnRyb2wudmFsKCkucmVwbGFjZSgvW15cXGRdKy9nLCAnJykpO1xuICAgICAgICB9XG5cbiAgICAgICAgaWYgKCRjb250cm9sLmhhc0NsYXNzKCd0eXBlLW5vc3BhY2UnKSkge1xuICAgICAgICAgICAgJGNvbnRyb2wudmFsKCRjb250cm9sLnZhbCgpLnJlcGxhY2UoL1xccy9nLCAnJykpO1xuICAgICAgICB9XG4gICAgfVxuXG4gICAgaXNWYWxpZElucHV0cygpIHtcbiAgICAgICAgY29uc3QgJGNvbnRyb2xzID0gdGhpcy4kY29udHJvbHM7XG4gICAgICAgIGxldCBlcnJvciA9IDA7XG5cbiAgICAgICAgJGNvbnRyb2xzLmVhY2goKGluZGV4LCBjb250cm9sKSA9PiB7XG4gICAgICAgICAgICBjb25zdCAkY29udHJvbCA9ICQoY29udHJvbCk7XG5cbiAgICAgICAgICAgIGlmICghdGhpcy5faXNWYWxpZElucHV0KCRjb250cm9sKSkge1xuICAgICAgICAgICAgICAgIGVycm9yICs9IDE7XG4gICAgICAgICAgICB9XG4gICAgICAgIH0pO1xuICAgICAgICByZXR1cm4gQm9vbGVhbighZXJyb3IpO1xuICAgIH1cblxuICAgIC8qKlxuICAgICAqIENoZWNrIGdpdmVuIGNvbnRyb2wsIGlzIGl0IHZhbGlkP1xuICAgICAqIEBwYXJhbSB7alF1ZXJ5fSAkY29udHJvbFxuICAgICAqIEByZXR1cm5zIHtib29sZWFufSAtIElzIHZhbGlkIGNvbnRyb2w/XG4gICAgICovXG4gICAgX2lzVmFsaWRJbnB1dCgkY29udHJvbCkge1xuICAgICAgICBjb25zdCB2YWx1ZSA9ICQudHJpbSgkY29udHJvbC52YWwoKSk7XG5cbiAgICAgICAgaWYgKCF2YWx1ZSAmJiAhJGNvbnRyb2wuaGFzQ2xhc3MoJ3R5cGUtb3B0aW9uYWwnKSkge1xuICAgICAgICAgICAgdGhpcy5fc2V0RXJyb3IoJGNvbnRyb2wsICdFbXB0eScpO1xuICAgICAgICAgICAgcmV0dXJuIGZhbHNlO1xuICAgICAgICB9XG5cbiAgICAgICAgaWYgKCgkY29udHJvbC5oYXNDbGFzcygndHlwZS1lbWFpbCcpKSAmJiAhdGhpcy5faXNWYWxpZEVtYWlsKHZhbHVlKSkge1xuICAgICAgICAgICAgdGhpcy5fc2V0RXJyb3IoJGNvbnRyb2wsICdFbWFpbCBpcyBub3QgdmFsaWQnKTtcbiAgICAgICAgICAgIHJldHVybiBmYWxzZTtcbiAgICAgICAgfVxuXG4gICAgICAgIHJldHVybiB0cnVlO1xuICAgIH1cblxuICAgIC8qKlxuICAgICAqIElzIEVtYWlsIHZhbGlkP1xuICAgICAqIEBwYXJhbSB7c3RyaW5nfSBlbWFpbFxuICAgICAqIEByZXR1cm5zIHtib29sZWFufVxuICAgICAqL1xuICAgIF9pc1ZhbGlkRW1haWwoZW1haWwpIHtcbiAgICAgICAgdmFyIHJlID0gL14oKFtePD4oKVxcW1xcXVxcXFwuLDs6XFxzQFwiXSsoXFwuW148PigpXFxbXFxdXFxcXC4sOzpcXHNAXCJdKykqKXwoXCIuK1wiKSlAKChcXFtbMC05XXsxLDN9XFwuWzAtOV17MSwzfVxcLlswLTldezEsM31cXC5bMC05XXsxLDN9XSl8KChbYS16QS1aXFwtMC05XStcXC4pK1thLXpBLVpdezIsfSkpJC87XG4gICAgICAgIHJldHVybiByZS50ZXN0KGVtYWlsKTtcbiAgICB9XG5cbiAgICAvKipcbiAgICAgKiBTZXQgZXJyb3IgZm9yIGNvbnRyb2xcbiAgICAgKiBAcGFyYW0ge2pRdWVyeX0gJGNvbnRyb2xcbiAgICAgKiBAcGFyYW0ge1N0cmluZ30gZXJyb3JUZXh0XG4gICAgICogQHBhcmFtIHtCb29sZWFufSBpbnNlcnRFcnJvclxuICAgICAqL1xuICAgIF9zZXRFcnJvcigkY29udHJvbCwgZXJyb3JUZXh0LCBpbnNlcnRFcnJvciA9IHRydWUpIHtcbiAgICAgICAgY29uc3QgJHBhcmVudCA9ICRjb250cm9sLnBhcmVudCgpO1xuICAgICAgICBjb25zdCAkZXJyb3IgPSAkcGFyZW50LmZpbmQoJy5iLWVycm9yJyk7XG5cbiAgICAgICAgaWYgKCRlcnJvci5sZW5ndGgpIHJldHVybjtcblxuICAgICAgICAkcGFyZW50LmFkZENsYXNzKCdiLWVycm9yX3Nob3cnKTtcbiAgICAgICAgXG4gICAgICAgIGluc2VydEVycm9yICYmICQoJzxkaXYgY2xhc3M9XCJiLWVycm9yXCIgLz4nKVxuICAgICAgICAgICAgLnRleHQoZXJyb3JUZXh0KVxuICAgICAgICAgICAgLmFwcGVuZFRvKCRwYXJlbnQpO1xuXG4gICAgICAgIHRoaXMuYXJyRXJyb3JzLnB1c2goe1xuICAgICAgICAgICAgbmFtZTogJGNvbnRyb2wuYXR0cignbmFtZScpLFxuICAgICAgICAgICAgZXJyb3I6IGVycm9yVGV4dFxuICAgICAgICB9KVxuICAgIH1cblxuICAgIC8qKlxuICAgICAqIFJlbW92ZSBlcnJvciBmb3IgY29udHJvbFxuICAgICAqIEBwYXJhbSB7alF1ZXJ5fSAkY29udHJvbFxuICAgICAqL1xuICAgIF9yZW1vdmVFcnJvcigkY29udHJvbCkge1xuICAgICAgICBjb25zdCAkcGFyZW50ID0gJGNvbnRyb2wucGFyZW50KCk7XG5cbiAgICAgICAgJHBhcmVudFxuICAgICAgICAgICAgLnJlbW92ZUNsYXNzKCdiLWVycm9yX3Nob3cnKVxuICAgICAgICAgICAgLmZpbmQoJy5iLWVycm9yJykucmVtb3ZlKCk7XG5cbiAgICAgICAgdGhpcy5hcnJFcnJvcnMgPSB0aGlzLmFyckVycm9ycy5maWx0ZXIoZnVuY3Rpb24gKGl0ZW0pIHtcbiAgICAgICAgICAgIHJldHVybiBpdGVtLm5hbWUgIT09ICRjb250cm9sLmF0dHIoJ25hbWUnKVxuICAgICAgICB9KVxuICAgIH1cblxuICAgIC8qKlxuICAgICAqIFNldCBlcnJvcnNcbiAgICAgKiBAcGFyYW0ge0FycmF5fSBlcnJvcnMgLSBbe25hbWU6IFwiZW1haWxcIiwgZXJyb3I6IFwiZW1wdHlcIn0sIHtuYW1lOiBcInBhc3N3b3JkXCIsIGVycm9yOiBcImVtcHR5XCJ9XVxuICAgICAqIEBwYXJhbSB7Qm9vbGVhbn0gaW5zZXJ0RXJyb3IgLSBpbnNlcnQgZXJyb3IgZGVzY3JpcHRpb24gdG8gdGhlIERvbSBcbiAgICAgKi9cbiAgICBzZXRFcnJvcnMoZXJyb3JzLCBpbnNlcnRFcnJvciA9IHRydWUpIHtcbiAgICAgICAgZXJyb3JzLmZvckVhY2goKGl0ZW0pID0+IHtcbiAgICAgICAgICAgIGNvbnN0ICRjdXJyZW50Q29udHJvbCA9IHRoaXMuJGNvbnRyb2xzLmZpbHRlcignW25hbWU9XCInICsgaXRlbS5uYW1lICsgJ1wiXScpLmZpcnN0KCk7XG5cbiAgICAgICAgICAgIGlmICgkY3VycmVudENvbnRyb2wubGVuZ3RoKSB0aGlzLl9zZXRFcnJvcigkY3VycmVudENvbnRyb2wsIGl0ZW0uZXJyb3IsIGluc2VydEVycm9yKVxuICAgICAgICB9KVxuICAgIH1cblxuICAgIC8qKlxuICAgICAqIEdldCB0ZXh0IHZlcnNpb24gb2YgZXJyb3JzIGluIG9uZSBsaW5lLlxuICAgICAqIEBwYXJhbSB7TGlzdEVycm9yc30gZXJyb3JzXG4gICAgICogQHJldHVybnMge3N0cmluZ31cbiAgICAgKi9cbiAgICBnZXRFcnJvcnNUZXh0KGVycm9ycykge1xuICAgICAgICBjb25zdCBhcnJFcnJvcnMgPSBlcnJvcnMgfHwgdGhpcy5hcnJFcnJvcnM7XG4gICAgICAgIGxldCBlcnJvclR4dCA9ICcnO1xuXG4gICAgICAgIGFyckVycm9ycy5mb3JFYWNoKChpdGVtKSA9PiB7XG4gICAgICAgICAgICBjb25zdCBuYW1lID0gaXRlbS5uYW1lWzBdLnRvVXBwZXJDYXNlKCkgKyBpdGVtLm5hbWUuc3Vic3RyKDEpO1xuXG4gICAgICAgICAgICBlcnJvclR4dCArPSBgJHtuYW1lfTogJHtpdGVtLmVycm9yfS4gYDtcbiAgICAgICAgfSk7XG5cbiAgICAgICAgcmV0dXJuIGVycm9yVHh0O1xuICAgIH1cblxuICAgIC8qKlxuICAgICAqIEdldCBsaXN0IG9mIGVycm9ycyB3aXRoIGZ1bGwgdGl0bGUgKGZyb20gY29udHJvbCB0aXRsZSBhdHRyaWJ1dGUpXG4gICAgICogQHBhcmFtIHtMaXN0RXJyb3JzfSBlcnJvcnMgLSBsaXN0IG9mIGVycm9yc1xuICAgICAqIEByZXR1cm5zIHtzdHJpbmd9XG4gICAgICovXG4gICAgZ2V0RXJyb3JzRnVsbChlcnJvcnMpIHtcbiAgICAgICAgY29uc3Qgc2VsZiA9IHRoaXM7XG4gICAgICAgIGNvbnN0IGFyckVycm9ycyA9IGVycm9ycyB8fCB0aGlzLmFyckVycm9ycztcbiAgICAgICAgbGV0IGVycm9yVHh0ID0gJyc7XG5cbiAgICAgICAgYXJyRXJyb3JzLmZvckVhY2goKGl0ZW0pID0+IHtcbiAgICAgICAgICAgIGNvbnN0ICRjb250cm9sID0gc2VsZi4kY29udHJvbHMuZmlsdGVyKGBbbmFtZT1cIiR7aXRlbS5uYW1lfVwiXWApLmZpcnN0KCk7XG4gICAgICAgICAgICBjb25zdCBuYW1lID0gJGNvbnRyb2wubGVuZ3RoPyAkY29udHJvbC5hdHRyKCd0aXRsZScpOiBpdGVtLm5hbWU7XG5cbiAgICAgICAgICAgIGVycm9yVHh0ICs9IGA8Yj4ke25hbWV9PC9iPjogJHtpdGVtLmVycm9yfS4gIDxicj48YnI+YDtcbiAgICAgICAgfSk7XG5cbiAgICAgICAgcmV0dXJuIGVycm9yVHh0O1xuICAgIH1cblxuICAgIGdldEZvcm1EYXRhKCl7XG4gICAgICAgIGxldCBhamF4RGF0YSA9IHt9O1xuXG4gICAgICAgIHRoaXMuJGNvbnRyb2xzLm1hcCgoaW5kZXgsIGVsKSA9PiB7XG4gICAgICAgICAgICBjb25zdCAkZWwgPSAkKGVsKTtcbiAgICAgICAgICAgIGNvbnN0IG5hbWUgPSAkZWwuYXR0cignbmFtZScpO1xuXG4gICAgICAgICAgICBpZiAoIW5hbWUpIHJldHVybjtcblxuICAgICAgICAgICAgaWYgKCRlbC5pcygnOmNoZWNrYm94Jykpe1xuICAgICAgICAgICAgICAgIGFqYXhEYXRhW25hbWVdID0gJGVsLnByb3AoJ2NoZWNrZWQnKVxuICAgICAgICAgICAgfSBlbHNlIHtcbiAgICAgICAgICAgICAgICBhamF4RGF0YVtuYW1lXSA9ICRlbC52YWwoKVxuICAgICAgICAgICAgfVxuICAgICAgICB9KTtcblxuICAgICAgICByZXR1cm4gYWpheERhdGE7XG4gICAgfVxuXG4gICAgLyoqXG4gICAgICogUmVtb3ZlIGFsbCBlcnJvcnNcbiAgICAgKi9cbiAgICByZW1vdmVFcnJvcnMoKSB7XG4gICAgICAgIHRoaXMuJGNvbnRyb2xzLmVhY2goKGluZGV4LCBlbCkgPT4ge1xuICAgICAgICAgICAgY29uc3QgJGVsID0gJChlbCk7XG4gICAgICAgICAgICB0aGlzLl9yZW1vdmVFcnJvcigkZWwpXG4gICAgICAgIH0pXG4gICAgfVxuXG4gICAgY2xlYXJGb3JtKCkge1xuICAgICAgICB0aGlzLiRjb250cm9scy5lYWNoKChpbmRleCwgZWwpID0+IHtcbiAgICAgICAgICAgIGNvbnN0ICRlbCA9ICQoZWwpO1xuICAgICAgICAgICAgaWYgKCEkZWwuYXR0cihcImRpc2FibGVkXCIpKSAgJGVsLnZhbCgnJyk7XG4gICAgICAgIH0pXG4gICAgfVxufVxuXG5cblxuLyoqIFdFQlBBQ0sgRk9PVEVSICoqXG4gKiogLi9mcm9udGVuZC9qcy9jb21tb24vX2Zvcm0taGVscGVyLmpzXG4gKiovIiwiJ3VzZSBzdHJpY3QnO1xuXG5pbXBvcnQgRXZhbHVhdGlvbkZvcm0gZnJvbSBcIi4vcHVibGljLXdpZGdldHMvX2V2YWx1YXRpb24tZm9ybVwiO1xuXG4kKGZ1bmN0aW9uKCl7XG4gICAgRXZhbHVhdGlvbkZvcm0ucGx1Z2luKCcuanMtZXZhbHVhdGlvbi1mb3JtJyk7XG4gICAgJCgnW2RhdGEtZXZhbHVhdC1iaXJ0aF0nKS5pbnB1dG1hc2soXCI5OS45OS45OTk5XCIpO1xufSk7XG5cblxuLyoqIFdFQlBBQ0sgRk9PVEVSICoqXG4gKiogLi9mcm9udGVuZC9qcy9ldmFsdWF0aW9uL2V2YWx1YXRpb24tcHVibGljLmpzXG4gKiovIiwiJ3VzZSBzdHJpY3QnO1xuXG5pbXBvcnQgRm9ybUhlbHBlciBmcm9tIFwiLi8uLi8uLi9jb21tb24vX2Zvcm0taGVscGVyXCI7XG5cbmV4cG9ydCBkZWZhdWx0IGNsYXNzIFdpZGdldCB7XG4gICAgLyoqXG4gICAgICogRmlsdGVyIGhpc3RvcnlcbiAgICAgKiBAcGFyYW0ge1N0cmluZ30gc2VsZWN0b3JcbiAgICAgKi9cbiAgICBjb25zdHJ1Y3RvcihzZWxlY3Rvcikge1xuICAgICAgICB0aGlzLiRyb290ID0gJChzZWxlY3Rvcik7XG4gICAgICAgIHRoaXMubG9jYWxzID0gdGhpcy5fZ2V0RG9tKCk7XG4gICAgICAgIHRoaXMuY3VycmVudFN0ZXAgPSAxO1xuICAgICAgICB0aGlzLmhlbHBlclN0ZXAxID0gbmV3IEZvcm1IZWxwZXIodGhpcy5sb2NhbHMuJGNvbnRyb2xzU3RlcDEpO1xuICAgICAgICB0aGlzLmhlbHBlclN0ZXAyID0gbmV3IEZvcm1IZWxwZXIodGhpcy5sb2NhbHMuJGNvbnRyb2xzU3RlcDIpO1xuXG4gICAgICAgIHRoaXMuX3NldERhdGFGcm9tTG9jYWwoJ3N0ZXAxJywgdGhpcy5sb2NhbHMuJGNvbnRyb2xzU3RlcDEpO1xuICAgICAgICB0aGlzLl9zZXREYXRhRnJvbUxvY2FsKCdzdGVwMicsIHRoaXMubG9jYWxzLiRjb250cm9sc1N0ZXAyKTtcbiAgICAgICAgdGhpcy5fYXNzaWduRXZlbnRzKCk7XG4gICAgfVxuXG4gICAgX2dldERvbSgpIHtcbiAgICAgICAgY29uc3QgJHJvb3QgPSB0aGlzLiRyb290O1xuXG4gICAgICAgIHJldHVybiB7XG4gICAgICAgICAgICAkY29udHJvbHNTdGVwMTogJHJvb3QuZmluZCgnLmItZXZhbGZvcm1fX3N0ZXAudHlwZS0xIC5mb3JtLWNvbnRyb2wnKSxcbiAgICAgICAgICAgICRjb250cm9sc1N0ZXAyOiAkcm9vdC5maW5kKCcuYi1ldmFsZm9ybV9fc3RlcC50eXBlLTIgLmZvcm0tY29udHJvbCcpLFxuICAgICAgICAgICAgJGFkZHJlc3NCbG9jazogJHJvb3QuZmluZCgnW2RhdGEtZXZhbC1hZGRyZXNzXScpLFxuICAgICAgICAgICAgJGZvcm06ICRyb290LmZpbmQoJ1tkYXRhLWV2YWwtZm9ybV0nKSxcbiAgICAgICAgICAgICRzdGVwMTogJHJvb3QuZmluZCgnW2RhdGEtZXZhbC1zdGVwMV0nKSxcbiAgICAgICAgICAgICRlcnJvcnNTdGVwMTogJHJvb3QuZmluZCgnW2RhdGEtZXZhbC1lcnJvcjFdJyksXG4gICAgICAgICAgICAkc3RlcDI6ICRyb290LmZpbmQoJ1tkYXRhLWV2YWwtc3RlcDJdJyksXG4gICAgICAgICAgICAkZXJyb3JzU3RlcDI6ICRyb290LmZpbmQoJ1tkYXRhLWV2YWwtZXJyb3IyXScpXG4gICAgICAgIH07XG4gICAgfVxuXG4gICAgX2Fzc2lnbkV2ZW50cygpIHtcbiAgICAgICAgdGhpcy4kcm9vdFxuICAgICAgICAgICAgLm9uKCdjbGljaycsICdbZGF0YS1ldmFsLW5leHRdJywgdGhpcy5fb25DbGlja05leHRTdGVwLmJpbmQodGhpcykpXG4gICAgICAgICAgICAub24oJ2NsaWNrJywgJ1tkYXRhLWV2YWwtcHJldmlvdXNdJywgdGhpcy5fb25DbGlja1ByZXZpb3VzU3RlcC5iaW5kKHRoaXMpKVxuICAgICAgICAgICAgLm9uKCdjbGljaycsICdbZGF0YS1ldmFsLXRvZ2dsZV0nLCB0aGlzLl9vbkNsaWNrVG9nZ2xlQWRkcmVzcy5iaW5kKHRoaXMpKVxuICAgICAgICAgICAgLm9uKCdjbGljaycsICdbZGF0YS1ldmFsLXN1Ym1pdF0nLCB0aGlzLl9vbkNsaWNrU3VibWl0QnRuLmJpbmQodGhpcykpXG5cbiAgICAgICAgdGhpcy5sb2NhbHMuJGZvcm0ub24oJ3N1Ym1pdCcsIHRoaXMuX29uRXZlbnRTdWJtaXQuYmluZCh0aGlzKSk7XG4gICAgfVxuXG4gICAgX29uQ2xpY2tOZXh0U3RlcChlKXtcbiAgICAgICAgZSAmJiBlLnByZXZlbnREZWZhdWx0KCk7XG5cbiAgICAgICAgaWYgKCF0aGlzLmhlbHBlclN0ZXAxLmlzVmFsaWRJbnB1dHMoKSkgcmV0dXJuO1xuXG4gICAgICAgIHRoaXMuX3NhdmVEYXRhVG9Mb2NhbCgnc3RlcDEnLCB0aGlzLmhlbHBlclN0ZXAxLmdldEZvcm1EYXRhKCkpO1xuICAgICAgICB0aGlzLnNob3dTdGVwKDIpO1xuICAgIH1cblxuICAgIF9vbkNsaWNrUHJldmlvdXNTdGVwKGUpe1xuICAgICAgICBlLnByZXZlbnREZWZhdWx0KCk7XG5cbiAgICAgICAgdGhpcy5fc2F2ZURhdGFUb0xvY2FsKCdzdGVwMicsIHRoaXMuaGVscGVyU3RlcDIuZ2V0Rm9ybURhdGEoKSk7XG4gICAgICAgIHRoaXMuc2hvd1N0ZXAoMSk7XG4gICAgfVxuXG4gICAgX29uQ2xpY2tUb2dnbGVBZGRyZXNzKGUpe1xuICAgICAgICBlLnByZXZlbnREZWZhdWx0KCk7XG4gICAgICAgIFxuICAgICAgICBjb25zdCAkbGluayA9ICQoZS5jdXJyZW50VGFyZ2V0KTtcbiAgICAgICAgY29uc3QgaXNTaG93RGV0YWlsID0gISRsaW5rLmhhc0NsYXNzKCdzdGF0ZV9hY3RpdmUnKTtcblxuICAgICAgICAkbGluay50b2dnbGVDbGFzcygnc3RhdGVfYWN0aXZlJywgaXNTaG93RGV0YWlsKTtcbiAgICAgICAgdGhpcy5sb2NhbHMuJGFkZHJlc3NCbG9jay5zbGlkZVRvZ2dsZSgpO1xuICAgIH1cblxuICAgIF9vbkNsaWNrU3VibWl0QnRuKGUpe1xuICAgICAgICBlICYmIGUucHJldmVudERlZmF1bHQoKTtcbiAgICAgICAgY29uc3Qgc2VsZiA9IHRoaXM7XG5cbiAgICAgICAgaWYgKCFzZWxmLmhlbHBlclN0ZXAyLmlzVmFsaWRJbnB1dHMoKSkgcmV0dXJuO1xuICAgICAgICBzZWxmLl9zYXZlRGF0YVRvTG9jYWwoJ3N0ZXAyJywgc2VsZi5oZWxwZXJTdGVwMi5nZXRGb3JtRGF0YSgpKTtcblxuICAgICAgICBjb25zdCBmb3JtRGF0YSA9ICQuZXh0ZW5kKHt9LCBzZWxmLmhlbHBlclN0ZXAxLmdldEZvcm1EYXRhKCksIHNlbGYuaGVscGVyU3RlcDIuZ2V0Rm9ybURhdGEoKSk7XG4gICAgICAgIHNlbGYuX3NlbmRFdmFsdWF0aW9uKGZvcm1EYXRhKVxuICAgICAgICAgICAgLmRvbmUoZnVuY3Rpb24oKXtcbiAgICAgICAgICAgICAgICBzZWxmLl9yZXNldEZvcm0oKTtcbiAgICAgICAgICAgICAgICBzZWxmLiRyb290LmFkZENsYXNzKCdwLWV2YWx1YXRfc3RhdGVfc3VjY2VzcycpO1xuICAgICAgICAgICAgfSlcbiAgICAgICAgICAgIC5mYWlsKGZ1bmN0aW9uKHJlc3BvbnNlKXtcbiAgICAgICAgICAgICAgICBjb25zdCBkYXRhID0gJC5wYXJzZUpTT04ocmVzcG9uc2UucmVzcG9uc2VUZXh0KS5kYXRhO1xuICAgICAgICAgICAgICAgIGNvbnN0IGVycm9yVGV4dCA9IHNlbGYuaGVscGVyU3RlcDIuZ2V0RXJyb3JzRnVsbChkYXRhLmVycm9ycyk7XG5cbiAgICAgICAgICAgICAgICBpZiAoIWRhdGEuZXJyb3JzKSByZXR1cm47XG5cbiAgICAgICAgICAgICAgICBzZWxmLmhlbHBlclN0ZXAyLnNldEVycm9ycyhkYXRhLmVycm9ycywgZmFsc2UpO1xuICAgICAgICAgICAgICAgIHNlbGYubG9jYWxzLiRlcnJvcnNTdGVwMi5odG1sKGVycm9yVGV4dCk7XG4gICAgICAgICAgICB9KVxuICAgIH1cblxuICAgIC8qKlxuICAgICAqIFRyeSB0byBzdWJtaXQgZm9ybSBvbiBib3RoIHN0ZXBzXG4gICAgICogQHBhcmFtIHtFdmVudH0gZVxuICAgICAqIEBwcml2YXRlXG4gICAgICovXG4gICAgX29uRXZlbnRTdWJtaXQoZSl7XG4gICAgICAgIGUucHJldmVudERlZmF1bHQoKTtcblxuICAgICAgICBpZiAodGhpcy5jdXJyZW50U3RlcCA9PSAxKXtcbiAgICAgICAgICAgIHRoaXMuX29uQ2xpY2tOZXh0U3RlcCgpO1xuICAgICAgICB9IGVsc2Uge1xuICAgICAgICAgICAgdGhpcy5fb25DbGlja1N1Ym1pdEJ0bigpO1xuICAgICAgICB9XG4gICAgfVxuXG4gICAgLyoqXG4gICAgICogU2hvdyBnaXZlbiBzdGVwXG4gICAgICogQHBhcmFtIHtOdW1iZXJ9IG51bWJlclxuICAgICAqL1xuICAgIHNob3dTdGVwKG51bWJlciA9IDEpe1xuICAgICAgICBjb25zdCAkcm9vdCA9IHRoaXMuJHJvb3Q7XG4gICAgICAgIGNvbnN0IGlzU2hvd1N0ZXAyID0gKG51bWJlciA9PSAyKSAmJiAoISRyb290Lmhhc0NsYXNzKCdwLWV2YWx1YXRfc3RhdGVfc2Vjb25kJykpO1xuXG4gICAgICAgIHRoaXMuY3VycmVudFN0ZXAgPSBudW1iZXI7XG4gICAgICAgICRyb290LnRvZ2dsZUNsYXNzKCdwLWV2YWx1YXRfc3RhdGVfc2Vjb25kJywgaXNTaG93U3RlcDIpO1xuICAgIH1cblxuICAgIC8qKlxuICAgICAqIFNhdmUgZGF0YSBpbnRvIGxvY2FsU3RvcmFnZVxuICAgICAqIEBwYXJhbSB7U3RyaW5nfSBrZXlcbiAgICAgKiBAcGFyYW0ge09iamVjdHxudWxsfSBkYXRhXG4gICAgICogQHByaXZhdGVcbiAgICAgKi9cbiAgICBfc2F2ZURhdGFUb0xvY2FsKGtleSwgZGF0YSl7XG4gICAgICAgIGxvY2FsU3RvcmFnZS5zZXRJdGVtKGtleSwgSlNPTi5zdHJpbmdpZnkoZGF0YSkpO1xuICAgIH1cblxuICAgIC8qKlxuICAgICAqIEdldCBkYXRhIGZyb20gbG9jYWxTdG9yYWdlIGFuZCBzZXQgdmFsdWVzIHRvIGh0bWwgY29udHJvbHNcbiAgICAgKiBAcGFyYW0ge1N0cmluZ30ga2V5IC0gdW5pcXVlIGtleSBmb3IgTG9jYWxTdG9yYWdlXG4gICAgICogQHBhcmFtIHtqUXVlcnl9ICRjb250cm9scyAtIGxpc3Qgb2YgY29udHJvbHNcbiAgICAgKiBAcHJpdmF0ZVxuICAgICAqL1xuICAgIF9zZXREYXRhRnJvbUxvY2FsKGtleSwgJGNvbnRyb2xzKXtcbiAgICAgICAgY29uc3QgaXRlbSA9IGxvY2FsU3RvcmFnZS5nZXRJdGVtKGtleSk7XG4gICAgICAgIGlmICghaXRlbSkgcmV0dXJuIGZhbHNlO1xuXG4gICAgICAgIGNvbnN0IGZvcm1EYXRhID0gSlNPTi5wYXJzZShpdGVtKTtcbiAgICAgICAgbGV0ICRjb250cm9sO1xuXG4gICAgICAgIGZvcihsZXQgZmllbGROYW1lIGluIGZvcm1EYXRhKXtcbiAgICAgICAgICAgIGlmIChmb3JtRGF0YS5oYXNPd25Qcm9wZXJ0eShmaWVsZE5hbWUpKXtcbiAgICAgICAgICAgICAgICAkY29udHJvbCA9ICRjb250cm9scy5maWx0ZXIoYFtuYW1lPVwiJHtmaWVsZE5hbWV9XCJdYCkuZmlyc3QoKTtcblxuICAgICAgICAgICAgICAgIGlmICghJGNvbnRyb2wubGVuZ3RoKSBjb250aW51ZTtcbiAgICAgICAgICAgICAgICAkY29udHJvbC52YWwoZm9ybURhdGFbZmllbGROYW1lXSk7XG4gICAgICAgICAgICB9XG4gICAgICAgIH1cbiAgICB9XG5cbiAgICBfcmVzZXRGb3JtKCl7XG4gICAgICAgIHRoaXMuaGVscGVyU3RlcDEuY2xlYXJGb3JtKCk7XG4gICAgICAgIHRoaXMuX3NhdmVEYXRhVG9Mb2NhbCgnc3RlcDEnLCBudWxsKTtcblxuICAgICAgICB0aGlzLmhlbHBlclN0ZXAyLmNsZWFyRm9ybSgpO1xuICAgICAgICB0aGlzLmxvY2Fscy4kZXJyb3JzU3RlcDIuaHRtbCgnJyk7XG4gICAgICAgIHRoaXMuX3NhdmVEYXRhVG9Mb2NhbCgnc3RlcDInLCBudWxsKTtcbiAgICB9XG5cbiAgICAvL3RyYW5zcG9ydFxuICAgIF9zZW5kRXZhbHVhdGlvbihmb3JtRGF0YSl7XG4gICAgICAgIGNvbnN0IHVybCA9IHRoaXMubG9jYWxzLiRmb3JtLmRhdGEoJ2FjdGlvbicpO1xuICAgICAgICByZXR1cm4gJC5wb3N0KHVybCwgZm9ybURhdGEpO1xuICAgIH1cblxuICAgIC8vIHN0YXRpY1xuICAgIHN0YXRpYyBwbHVnaW4oc2VsZWN0b3IpIHtcbiAgICAgICAgY29uc3QgJGVsZW1zID0gJChzZWxlY3Rvcik7XG4gICAgICAgIGlmICghJGVsZW1zLmxlbmd0aCkgcmV0dXJuO1xuXG4gICAgICAgIHJldHVybiAkZWxlbXMuZWFjaChmdW5jdGlvbiAoaW5kZXgsIGVsKSB7XG4gICAgICAgICAgICBsZXQgJGVsZW1lbnQgPSAkKGVsKTtcbiAgICAgICAgICAgIGxldCBkYXRhID0gJGVsZW1lbnQuZGF0YSgnd2lkZ2V0Jyk7XG5cbiAgICAgICAgICAgIGlmICghZGF0YSkge1xuICAgICAgICAgICAgICAgIGRhdGEgPSBuZXcgV2lkZ2V0KGVsKTtcbiAgICAgICAgICAgICAgICAkZWxlbWVudC5kYXRhKCd3aWRnZXQnLCBkYXRhKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgfSlcbiAgICB9XG59XG5cblxuXG5cblxuLyoqIFdFQlBBQ0sgRk9PVEVSICoqXG4gKiogLi9mcm9udGVuZC9qcy9ldmFsdWF0aW9uL3B1YmxpYy13aWRnZXRzL19ldmFsdWF0aW9uLWZvcm0uanNcbiAqKi8iLCJtb2R1bGUuZXhwb3J0cyA9IHsgXCJkZWZhdWx0XCI6IHJlcXVpcmUoXCJjb3JlLWpzL2xpYnJhcnkvZm4vanNvbi9zdHJpbmdpZnlcIiksIF9fZXNNb2R1bGU6IHRydWUgfTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL2NvcmUtanMvanNvbi9zdHJpbmdpZnkuanNcbiAqKiBtb2R1bGUgaWQgPSA0MVxuICoqIG1vZHVsZSBjaHVua3MgPSA0XG4gKiovIiwidmFyIGNvcmUgID0gcmVxdWlyZSgnLi4vLi4vbW9kdWxlcy9fY29yZScpXG4gICwgJEpTT04gPSBjb3JlLkpTT04gfHwgKGNvcmUuSlNPTiA9IHtzdHJpbmdpZnk6IEpTT04uc3RyaW5naWZ5fSk7XG5tb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uIHN0cmluZ2lmeShpdCl7IC8vIGVzbGludC1kaXNhYmxlLWxpbmUgbm8tdW51c2VkLXZhcnNcbiAgcmV0dXJuICRKU09OLnN0cmluZ2lmeS5hcHBseSgkSlNPTiwgYXJndW1lbnRzKTtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9mbi9qc29uL3N0cmluZ2lmeS5qc1xuICoqIG1vZHVsZSBpZCA9IDQyXG4gKiogbW9kdWxlIGNodW5rcyA9IDRcbiAqKi8iXSwic291cmNlUm9vdCI6IiJ9