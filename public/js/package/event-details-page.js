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

	module.exports = __webpack_require__(43);


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
/* 37 */
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
	        this.locals = this._getDom();
	        this.$confirmDialog = null;
	
	        this._assignEvents();
	    }
	
	    (0, _createClass3.default)(Widget, [{
	        key: '_getDom',
	        value: function _getDom() {
	            return {
	                $confirm: this.$root.find('[data-event-confirm]'),
	                $cancel: this.$root.find('[data-event-cancel]')
	            };
	        }
	    }, {
	        key: '_assignEvents',
	        value: function _assignEvents() {
	            this.$root.on('click', '[data-event-confirm]', this._onClickConfirm.bind(this)).on('click', '[data-event-cancel]', this._onClickCancel.bind(this)).on('click', '#eventCancelButton', this._onClickAcceptCancel.bind(this));
	        }
	    }, {
	        key: '_onClickConfirm',
	        value: function _onClickConfirm(e) {
	            e.preventDefault();
	            var self = this;
	            var eventId = self.locals.$confirm.data('id');
	
	            self._sendConfirm(eventId).done(function () {
	                self.locals.$confirm.addClass('disabled').text('Confirmed').off('click');
	                success("Event was successfully confirmed");
	            });
	        }
	    }, {
	        key: '_onClickCancel',
	        value: function _onClickCancel(e) {
	            e.preventDefault();
	            var self = this;
	            var eventId = self.locals.$cancel.data('id');
	
	            self._sendCancel(eventId).done(function (response) {
	                self.$confirmDialog = self._createDialog(response);
	                self.$confirmDialog.modal('show');
	            });
	        }
	    }, {
	        key: '_onClickAcceptCancel',
	        value: function _onClickAcceptCancel(e) {
	            e.preventDefault();
	
	            var self = this;
	            var formData = $("#cancelForm").serialize();
	            var eventId = self.locals.$cancel.data('id');
	
	            self._sendAcceptCancel(formData, eventId).done(function () {
	                self.$confirmDialog.on('hidden.bs.modal', function () {
	                    App.events.pub('hmt.event.cancel');
	                }).modal('hide');
	
	                success("Event was successfully canceled");
	            });
	        }
	    }, {
	        key: '_createDialog',
	        value: function _createDialog(content) {
	            var selector = '#cancelDialog';
	            var $dialog = undefined;
	
	            $(selector).remove();
	            $dialog = $('<div id="' + selector + '" class="b-modal modal fade" tabindex="-1">').attr('role', 'dialog').attr('aria-hidden', 'true').append(content);
	
	            $dialog.appendTo(this.$root);
	            return $dialog;
	        }
	
	        //transport
	
	    }, {
	        key: '_sendConfirm',
	        value: function _sendConfirm(eventId) {
	            var url = jsRoutes.controllers.cm.Events.confirm(eventId).url;
	            return $.post(url, {});
	        }
	    }, {
	        key: '_sendCancel',
	        value: function _sendCancel(eventId) {
	            var url = jsRoutes.controllers.cm.Events.reason(eventId).url;
	            return $.get(url, {});
	        }
	    }, {
	        key: '_sendAcceptCancel',
	        value: function _sendAcceptCancel(data, id) {
	            var url = jsRoutes.controllers.cm.Events.cancel(id).url;
	            return $.post(url, data);
	        }
	
	        // static
	
	    }], [{
	        key: 'plugin',
	        value: function plugin(selector) {
	            var $elems = $(selector);
	            if (!$elems.length) return;
	
	            return $elems.each(function (index, el) {
	                var $element = $(el);
	                var data = $element.data('hmt.event.block');
	
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
/* 38 */,
/* 39 */,
/* 40 */,
/* 41 */,
/* 42 */,
/* 43 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';
	
	var _eventBlock = __webpack_require__(37);
	
	var _eventBlock2 = _interopRequireDefault(_eventBlock);
	
	var _clipboard = __webpack_require__(44);
	
	var _clipboard2 = _interopRequireDefault(_clipboard);
	
	var _evaluationDlg = __webpack_require__(45);
	
	var _evaluationDlg2 = _interopRequireDefault(_evaluationDlg);
	
	var _modifyEmail = __webpack_require__(46);
	
	var _modifyEmail2 = _interopRequireDefault(_modifyEmail);
	
	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }
	
	$(function () {
	    _eventBlock2.default.plugin('.js-event-controls');
	    _evaluationDlg2.default.plugin('.js-request-evaluation');
	    _clipboard2.default.plugin('.js-clipboard');
	    _modifyEmail2.default.plugin('.js-modify-email');
	
	    App.events.sub('hmt.event.cancel', function () {
	        var brandId = $('#brandId').val();
	        window.location.replace(jsRoutes.controllers.cm.Events.index(brandId).url);
	    });
	});

/***/ },
/* 44 */
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
	    function Widget(element) {
	        (0, _classCallCheck3.default)(this, Widget);
	
	        this.root = element;
	        this.$root = $(element);
	
	        if (!ZeroClipboard) {
	            console.log('there is no zeroclipboard dependency');
	            return;
	        }
	
	        this.client = new ZeroClipboard(element);
	        this.$client = $('.global-zeroclipboard-container').first();
	
	        this._assignEvents();
	    }
	
	    (0, _createClass3.default)(Widget, [{
	        key: '_assignEvents',
	        value: function _assignEvents() {
	            var self = this;
	
	            this.client.on('aftercopy', this._onEventAfter.bind(this));
	            this.$client.on('mouseenter', function () {
	                self.$client.attr('title', 'Copy link').tooltip('show');
	            });
	        }
	    }, {
	        key: '_onEventAfter',
	        value: function _onEventAfter() {
	            var $root = this.$root;
	            this.$client.tooltip('hide');
	
	            $root.attr('title', 'Copied').tooltip('show');
	
	            setTimeout(function () {
	                $root.tooltip('hide').attr('title', '');
	            }, 2500);
	        }
	
	        // static
	
	    }], [{
	        key: 'plugin',
	        value: function plugin(selector) {
	            var $elems = $(selector);
	            if (!$elems.length) return;
	
	            return $elems.each(function (index, el) {
	                var $element = $(el);
	                var data = $element.data('widget-clipboard');
	
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
/* 45 */
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
	        this.locals = this._getDom();
	        this.template = _.template($('#request-dlg-template').html());
	
	        this._checkFormValidation();
	        this._assignEvent();
	    }
	
	    (0, _createClass3.default)(Widget, [{
	        key: '_getDom',
	        value: function _getDom() {
	            var $root = this.$root;
	
	            return {
	                $listParticipants: $root.find('[data-requesteval-list]'),
	                $textarea: $root.find('[data-requesteval-textarea]'),
	                $filterLinks: $root.find('[data-requesteval-filter]'),
	                $submit: $root.find('[data-requesteval-submit]')
	            };
	        }
	    }, {
	        key: '_assignEvent',
	        value: function _assignEvent() {
	            var _this = this;
	
	            this.$root.on('change', '[data-requesteval-list] input', this._checkFormValidation.bind(this)).on('input propertychange', '[data-requesteval-textarea]', this._checkFormValidation.bind(this)).on('click', '[data-requesteval-filter]', function (e) {
	                e.preventDefault();
	
	                var $link = $(e.currentTarget);
	                _this._filterParticipants($link);
	            });
	
	            App.events.sub('hmt.requestDlg.render', this._renderCheckboxes.bind(this));
	        }
	    }, {
	        key: '_checkFormValidation',
	        value: function _checkFormValidation() {
	            var locals = this.locals;
	            var $participants = this.$root.find('[data-requesteval-list] input');
	            var valid = true;
	
	            if (!$participants.filter(':checked').length) {
	                valid = false;
	            }
	
	            if (!/https?:/i.test(locals.$textarea.val())) {
	                valid = false;
	            }
	
	            if (!valid) {
	                locals.$submit.attr('disabled', 'disabled');
	            } else {
	                locals.$submit.removeAttr('disabled');
	            }
	        }
	
	        /**
	         * Render list of checkboxes
	         * @param {jQuery} participant - participant table
	         * @private
	         */
	
	    }, {
	        key: '_renderCheckboxes',
	        value: function _renderCheckboxes(participant) {
	            var rowsInfo = participant.table._('tr', {});
	            var i = undefined,
	                n = undefined;
	
	            for (i = 0, n = rowsInfo.length; i < n; i++) {
	                var data = {
	                    index: i,
	                    value: rowsInfo[i].person.id,
	                    name: rowsInfo[i].person.name,
	                    status: $.isPlainObject(rowsInfo[i].evaluation.status)
	                };
	                var label = this.template(data);
	
	                this.locals.$listParticipants.append(label);
	            }
	        }
	
	        /**
	         *
	         * @param {jQuery} $link
	         * @private
	         */
	
	    }, {
	        key: '_filterParticipants',
	        value: function _filterParticipants($link) {
	            var $participants = this.$root.find('[data-requesteval-list] input');
	            var filterText = $link.data('requesteval-filter');
	            var $filterLinks = this.locals.$filterLinks;
	
	            if ($link.hasClass('state_selected')) return;
	
	            switch (filterText) {
	                case 'all':
	                    {
	                        $participants.prop('checked', true);
	                        break;
	                    }
	                case 'with':
	                    {
	                        $participants.prop('checked', false).filter('.have-evaluation').prop('checked', true);
	                        break;
	                    }
	                case 'without':
	                    {
	                        $participants.prop('checked', true).filter('.have-evaluation').prop('checked', false);
	                        break;
	                    }
	                default:
	                    {
	                        $participants.prop('checked', false);
	                        break;
	                    }
	            }
	
	            $filterLinks.removeClass('state_selected');
	            $link.addClass('state_selected');
	
	            this._checkFormValidation();
	        }
	
	        // static
	
	    }], [{
	        key: 'plugin',
	        value: function plugin(selector) {
	            var $elems = $(selector);
	            if (!$elems.length) return;
	
	            return $elems.each(function (index, el) {
	                var $element = $(el);
	                var data = $element.data('hmt.events.upcoming');
	
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
/* 46 */
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
	    function Widget(selector) {
	        (0, _classCallCheck3.default)(this, Widget);
	
	        this.$root = $(selector);
	        this.locals = this._getDom();
	        this.formHelper = new _formHelper2.default(this.locals.$textarea);
	        this.template = this.locals.$textarea.val();
	
	        this._assignEvent();
	    }
	
	    (0, _createClass3.default)(Widget, [{
	        key: '_getDom',
	        value: function _getDom() {
	            var $root = this.$root;
	
	            return {
	                $link: $root.find('[data-emailmod-link]'),
	                $defaultTemplate: $root.find('[data-emailmod-default]'),
	                $modal: $root.find('[data-emailmod-dlg]'),
	                $form: $root.find('[data-emailmod-form]'),
	                $textarea: $root.find('[data-emailmod-textarea]'),
	                $cancel: $root.find('[data-emailmod-cancel]')
	            };
	        }
	    }, {
	        key: '_assignEvent',
	        value: function _assignEvent() {
	            var self = this;
	
	            this.$root.on('click', '[data-emailmod-link]', this._onClickShowModal.bind(this)).on('click', '[data-emailmod-mark]', this._onClickUseTemplate.bind(this)).on('hide.bs.modal', function (e) {
	                e.stopPropagation();
	                self._onCloseModal();
	            });
	
	            this.locals.$form.on('submit', this._onSubmitForm.bind(this));
	        }
	    }, {
	        key: '_onClickShowModal',
	        value: function _onClickShowModal(e) {
	            e.preventDefault();
	            this.locals.$modal.modal('show');
	        }
	    }, {
	        key: '_onClickUseTemplate',
	        value: function _onClickUseTemplate(e) {
	            e.preventDefault();
	            var locals = this.locals;
	
	            locals.$textarea.val(locals.$defaultTemplate.text()).trigger('markdown.render');
	        }
	    }, {
	        key: '_onCloseModal',
	        value: function _onCloseModal() {
	            this.locals.$textarea.val(this.template);
	            this.formHelper.removeErrors();
	        }
	    }, {
	        key: '_onSubmitForm',
	        value: function _onSubmitForm(e) {
	            e.preventDefault();
	            var self = this;
	            var locals = this.locals;
	
	            if (!self.formHelper.isValidInputs()) return;
	            var formData = self.formHelper.getFormData();
	
	            self._sendEmailContent(formData).done(function () {
	                self.template = locals.$textarea.val();
	                locals.$modal.modal('hide');
	
	                success('Email is modified');
	            }).fail(function (response) {
	                var data = $.parseJSON(response.responseText).data;
	
	                if (!data.errors) return;
	
	                self.formHelper.setErrors(data.errors);
	            });
	        }
	
	        //transport
	
	    }, {
	        key: '_sendEmailContent',
	        value: function _sendEmailContent(formData) {
	            var url = this.locals.$form.attr('action');
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
	                var data = $element.data('hmt.events.modify_email');
	
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
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIndlYnBhY2s6Ly8vd2VicGFjay9ib290c3RyYXAgZjBmMTY2OTBiZDk3YTI1NWNmYjk/N2VmNCoqKioqIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9oZWxwZXJzL2NsYXNzQ2FsbENoZWNrLmpzPzIxYWYqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvaGVscGVycy9jcmVhdGVDbGFzcy5qcz8xZGZlKioqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL2NvcmUtanMvb2JqZWN0L2RlZmluZS1wcm9wZXJ0eS5qcz81ZjcwKioqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L2ZuL29iamVjdC9kZWZpbmUtcHJvcGVydHkuanM/NGQzMyoqKioqIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL2VzNi5vYmplY3QuZGVmaW5lLXByb3BlcnR5LmpzPzhiZGUqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZXhwb3J0LmpzPzhlNDAqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZ2xvYmFsLmpzPzNjNTIqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fY29yZS5qcz9kNjExKioqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2N0eC5qcz80ZTU5KioqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2EtZnVuY3Rpb24uanM/MDY5OSoqKioqIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19oaWRlLmpzPzBkMmUqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fb2JqZWN0LWRwLmpzPzU5ODYqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fYW4tb2JqZWN0LmpzPzNhZjIqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9faXMtb2JqZWN0LmpzP2NmZGEqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9faWU4LWRvbS1kZWZpbmUuanM/YjEwMioqKioqIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19kZXNjcmlwdG9ycy5qcz9jMGY1KioqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2ZhaWxzLmpzP2M2ZGQqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZG9tLWNyZWF0ZS5qcz82MTJmKioqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX3RvLXByaW1pdGl2ZS5qcz8xYTY1KioqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX3Byb3BlcnR5LWRlc2MuanM/MjU2YioqKioqIiwid2VicGFjazovLy8uL2Zyb250ZW5kL2pzL2NvbW1vbi9fZm9ybS1oZWxwZXIuanM/ODYzNioqIiwid2VicGFjazovLy8uL2Zyb250ZW5kL2pzL2NvbW1vbi9fZXZlbnQtYmxvY2suanM/NWYzOCIsIndlYnBhY2s6Ly8vLi9mcm9udGVuZC9qcy9ldmVudC9ldmVudC1kZXRhaWxzLXBhZ2UuanMiLCJ3ZWJwYWNrOi8vLy4vZnJvbnRlbmQvanMvY29tbW9uL19jbGlwYm9hcmQuanMiLCJ3ZWJwYWNrOi8vLy4vZnJvbnRlbmQvanMvZXZlbnQvZGV0YWlsLXdpZGdldHMvX2V2YWx1YXRpb24tZGxnLmpzIiwid2VicGFjazovLy8uL2Zyb250ZW5kL2pzL2V2ZW50L2RldGFpbC13aWRnZXRzL19tb2RpZnktZW1haWwuanMiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IjtBQUFBO0FBQ0E7O0FBRUE7QUFDQTs7QUFFQTtBQUNBO0FBQ0E7O0FBRUE7QUFDQTtBQUNBLHVCQUFlO0FBQ2Y7QUFDQTtBQUNBOztBQUVBO0FBQ0E7O0FBRUE7QUFDQTs7QUFFQTtBQUNBO0FBQ0E7OztBQUdBO0FBQ0E7O0FBRUE7QUFDQTs7QUFFQTtBQUNBOztBQUVBO0FBQ0E7Ozs7Ozs7Ozs7Ozs7Ozs7QUN0Q0E7O0FBRUE7O0FBRUE7QUFDQTtBQUNBO0FBQ0E7QUFDQSxHOzs7Ozs7QUNSQTs7QUFFQTs7QUFFQTs7QUFFQTs7QUFFQSx1Q0FBc0MsdUNBQXVDLGdCQUFnQjs7QUFFN0Y7QUFDQTtBQUNBLG9CQUFtQixrQkFBa0I7QUFDckM7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7O0FBRUE7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBLEVBQUMsRzs7Ozs7O0FDMUJELG1CQUFrQix1RDs7Ozs7O0FDQWxCO0FBQ0E7QUFDQTtBQUNBO0FBQ0EsRzs7Ozs7O0FDSkE7QUFDQTtBQUNBLHNFQUF1RSwwQ0FBMEMsRTs7Ozs7O0FDRmpIO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7O0FBRUE7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQSxvRUFBbUU7QUFDbkU7QUFDQSxzRkFBcUY7QUFDckY7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBLFlBQVc7QUFDWCxVQUFTO0FBQ1Q7QUFDQTtBQUNBO0FBQ0E7QUFDQSxNQUFLO0FBQ0w7QUFDQTtBQUNBLGdEQUErQztBQUMvQztBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQSxlQUFjO0FBQ2QsZUFBYztBQUNkLGVBQWM7QUFDZCxlQUFjO0FBQ2QsZ0JBQWU7QUFDZixnQkFBZTtBQUNmLGdCQUFlO0FBQ2YsaUJBQWdCO0FBQ2hCLDBCOzs7Ozs7QUM1REE7QUFDQTtBQUNBO0FBQ0Esd0NBQXVDLGdDOzs7Ozs7QUNIdkMsOEJBQTZCO0FBQzdCLHNDQUFxQyxnQzs7Ozs7O0FDRHJDO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0EsRzs7Ozs7O0FDbkJBO0FBQ0E7QUFDQTtBQUNBLEc7Ozs7OztBQ0hBO0FBQ0E7QUFDQTtBQUNBO0FBQ0EsRUFBQztBQUNEO0FBQ0E7QUFDQSxHOzs7Ozs7QUNQQTtBQUNBO0FBQ0E7QUFDQTs7QUFFQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQSxJQUFHLFVBQVU7QUFDYjtBQUNBO0FBQ0E7QUFDQSxHOzs7Ozs7QUNmQTtBQUNBO0FBQ0E7QUFDQTtBQUNBLEc7Ozs7OztBQ0pBO0FBQ0E7QUFDQSxHOzs7Ozs7QUNGQTtBQUNBLHNFQUFzRSxnQkFBZ0IsVUFBVSxHQUFHO0FBQ25HLEVBQUMsRTs7Ozs7O0FDRkQ7QUFDQTtBQUNBLGtDQUFpQyxRQUFRLGdCQUFnQixVQUFVLEdBQUc7QUFDdEUsRUFBQyxFOzs7Ozs7QUNIRDtBQUNBO0FBQ0E7QUFDQSxJQUFHO0FBQ0g7QUFDQTtBQUNBLEc7Ozs7OztBQ05BO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBLEc7Ozs7OztBQ05BO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQSxHOzs7Ozs7QUNYQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBLEc7Ozs7OztBQ1BBOzs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7OztLQVNxQjs7Ozs7O0FBS2pCLGNBTGlCLFVBS2pCLENBQVksU0FBWixFQUF1Qjs2Q0FMTixZQUtNOztBQUNuQixjQUFLLFNBQUwsR0FBaUIsU0FBakIsQ0FEbUI7QUFFbkIsY0FBSyxTQUFMLEdBQWlCLEVBQWpCLENBRm1CO0FBR25CLGNBQUssYUFBTCxHQUhtQjtNQUF2Qjs7Z0NBTGlCOzt5Q0FXRDs7O0FBQ1osa0JBQUssU0FBTCxDQUFlLEVBQWYsQ0FBa0IsY0FBbEIsRUFBa0MsVUFBQyxDQUFELEVBQU87QUFDckMscUJBQU0sV0FBVyxFQUFFLEVBQUUsYUFBRixDQUFiLENBRCtCOztBQUdyQyx1QkFBSyxrQkFBTCxDQUF3QixRQUF4QixFQUhxQztBQUlyQyx1QkFBSyxZQUFMLENBQWtCLFFBQWxCLEVBSnFDO2NBQVAsQ0FBbEMsQ0FEWTs7Ozs0Q0FTRyxVQUFTO0FBQ3hCLGlCQUFJLFNBQVMsUUFBVCxDQUFrQixjQUFsQixDQUFKLEVBQXVDO0FBQ25DLDBCQUFTLEdBQVQsQ0FBYSxTQUFTLEdBQVQsR0FBZSxPQUFmLENBQXVCLFNBQXZCLEVBQWtDLEVBQWxDLENBQWIsRUFEbUM7Y0FBdkM7O0FBSUEsaUJBQUksU0FBUyxRQUFULENBQWtCLGNBQWxCLENBQUosRUFBdUM7QUFDbkMsMEJBQVMsR0FBVCxDQUFhLFNBQVMsR0FBVCxHQUFlLE9BQWYsQ0FBdUIsS0FBdkIsRUFBOEIsRUFBOUIsQ0FBYixFQURtQztjQUF2Qzs7Ozt5Q0FLWTs7O0FBQ1osaUJBQU0sWUFBWSxLQUFLLFNBQUwsQ0FETjtBQUVaLGlCQUFJLFFBQVEsQ0FBUixDQUZROztBQUlaLHVCQUFVLElBQVYsQ0FBZSxVQUFDLEtBQUQsRUFBUSxPQUFSLEVBQW9CO0FBQy9CLHFCQUFNLFdBQVcsRUFBRSxPQUFGLENBQVgsQ0FEeUI7O0FBRy9CLHFCQUFJLENBQUMsT0FBSyxhQUFMLENBQW1CLFFBQW5CLENBQUQsRUFBK0I7QUFDL0IsOEJBQVMsQ0FBVCxDQUQrQjtrQkFBbkM7Y0FIVyxDQUFmLENBSlk7QUFXWixvQkFBTyxRQUFRLENBQUMsS0FBRCxDQUFmLENBWFk7Ozs7Ozs7Ozs7O3VDQW1CRixVQUFVO0FBQ3BCLGlCQUFNLFFBQVEsRUFBRSxJQUFGLENBQU8sU0FBUyxHQUFULEVBQVAsQ0FBUixDQURjOztBQUdwQixpQkFBSSxDQUFDLEtBQUQsSUFBVSxDQUFDLFNBQVMsUUFBVCxDQUFrQixlQUFsQixDQUFELEVBQXFDO0FBQy9DLHNCQUFLLFNBQUwsQ0FBZSxRQUFmLEVBQXlCLE9BQXpCLEVBRCtDO0FBRS9DLHdCQUFPLEtBQVAsQ0FGK0M7Y0FBbkQ7O0FBS0EsaUJBQUksUUFBQyxDQUFTLFFBQVQsQ0FBa0IsWUFBbEIsQ0FBRCxJQUFxQyxDQUFDLEtBQUssYUFBTCxDQUFtQixLQUFuQixDQUFELEVBQTRCO0FBQ2pFLHNCQUFLLFNBQUwsQ0FBZSxRQUFmLEVBQXlCLG9CQUF6QixFQURpRTtBQUVqRSx3QkFBTyxLQUFQLENBRmlFO2NBQXJFOztBQUtBLG9CQUFPLElBQVAsQ0Fib0I7Ozs7Ozs7Ozs7O3VDQXFCVixPQUFPO0FBQ2pCLGlCQUFJLEtBQUssd0pBQUwsQ0FEYTtBQUVqQixvQkFBTyxHQUFHLElBQUgsQ0FBUSxLQUFSLENBQVAsQ0FGaUI7Ozs7Ozs7Ozs7OzttQ0FXWCxVQUFVLFdBQStCO2lCQUFwQixvRUFBYyxvQkFBTTs7QUFDL0MsaUJBQU0sVUFBVSxTQUFTLE1BQVQsRUFBVixDQUR5QztBQUUvQyxpQkFBTSxTQUFTLFFBQVEsSUFBUixDQUFhLFVBQWIsQ0FBVCxDQUZ5Qzs7QUFJL0MsaUJBQUksT0FBTyxNQUFQLEVBQWUsT0FBbkI7O0FBRUEscUJBQVEsUUFBUixDQUFpQixjQUFqQixFQU4rQzs7QUFRL0MsNEJBQWUsRUFBRSx5QkFBRixFQUNWLElBRFUsQ0FDTCxTQURLLEVBRVYsUUFGVSxDQUVELE9BRkMsQ0FBZixDQVIrQzs7QUFZL0Msa0JBQUssU0FBTCxDQUFlLElBQWYsQ0FBb0I7QUFDaEIsdUJBQU0sU0FBUyxJQUFULENBQWMsTUFBZCxDQUFOO0FBQ0Esd0JBQU8sU0FBUDtjQUZKLEVBWitDOzs7Ozs7Ozs7O3NDQXNCdEMsVUFBVTtBQUNuQixpQkFBTSxVQUFVLFNBQVMsTUFBVCxFQUFWLENBRGE7O0FBR25CLHFCQUNLLFdBREwsQ0FDaUIsY0FEakIsRUFFSyxJQUZMLENBRVUsVUFGVixFQUVzQixNQUZ0QixHQUhtQjs7QUFPbkIsa0JBQUssU0FBTCxHQUFpQixLQUFLLFNBQUwsQ0FBZSxNQUFmLENBQXNCLFVBQVUsSUFBVixFQUFnQjtBQUNuRCx3QkFBTyxLQUFLLElBQUwsS0FBYyxTQUFTLElBQVQsQ0FBYyxNQUFkLENBQWQsQ0FENEM7Y0FBaEIsQ0FBdkMsQ0FQbUI7Ozs7Ozs7Ozs7O21DQWlCYixRQUE0Qjs7O2lCQUFwQixvRUFBYyxvQkFBTTs7QUFDbEMsb0JBQU8sT0FBUCxDQUFlLFVBQUMsSUFBRCxFQUFVO0FBQ3JCLHFCQUFNLGtCQUFrQixPQUFLLFNBQUwsQ0FBZSxNQUFmLENBQXNCLFlBQVksS0FBSyxJQUFMLEdBQVksSUFBeEIsQ0FBdEIsQ0FBb0QsS0FBcEQsRUFBbEIsQ0FEZTs7QUFHckIscUJBQUksZ0JBQWdCLE1BQWhCLEVBQXdCLE9BQUssU0FBTCxDQUFlLGVBQWYsRUFBZ0MsS0FBSyxLQUFMLEVBQVksV0FBNUMsRUFBNUI7Y0FIVyxDQUFmLENBRGtDOzs7Ozs7Ozs7Ozt1Q0FheEIsUUFBUTtBQUNsQixpQkFBTSxZQUFZLFVBQVUsS0FBSyxTQUFMLENBRFY7QUFFbEIsaUJBQUksV0FBVyxFQUFYLENBRmM7O0FBSWxCLHVCQUFVLE9BQVYsQ0FBa0IsVUFBQyxJQUFELEVBQVU7QUFDeEIscUJBQU0sT0FBTyxLQUFLLElBQUwsQ0FBVSxDQUFWLEVBQWEsV0FBYixLQUE2QixLQUFLLElBQUwsQ0FBVSxNQUFWLENBQWlCLENBQWpCLENBQTdCLENBRFc7O0FBR3hCLDZCQUFlLGNBQVMsS0FBSyxLQUFMLE9BQXhCLENBSHdCO2NBQVYsQ0FBbEIsQ0FKa0I7O0FBVWxCLG9CQUFPLFFBQVAsQ0FWa0I7Ozs7Ozs7Ozs7O3VDQWtCUixRQUFRO0FBQ2xCLGlCQUFNLE9BQU8sSUFBUCxDQURZO0FBRWxCLGlCQUFNLFlBQVksVUFBVSxLQUFLLFNBQUwsQ0FGVjtBQUdsQixpQkFBSSxXQUFXLEVBQVgsQ0FIYzs7QUFLbEIsdUJBQVUsT0FBVixDQUFrQixVQUFDLElBQUQsRUFBVTtBQUN4QixxQkFBTSxXQUFXLEtBQUssU0FBTCxDQUFlLE1BQWYsYUFBZ0MsS0FBSyxJQUFMLE9BQWhDLEVBQStDLEtBQS9DLEVBQVgsQ0FEa0I7QUFFeEIscUJBQU0sT0FBTyxTQUFTLE1BQVQsR0FBaUIsU0FBUyxJQUFULENBQWMsT0FBZCxDQUFqQixHQUF5QyxLQUFLLElBQUwsQ0FGOUI7O0FBSXhCLHFDQUFrQixrQkFBYSxLQUFLLEtBQUwsZ0JBQS9CLENBSndCO2NBQVYsQ0FBbEIsQ0FMa0I7O0FBWWxCLG9CQUFPLFFBQVAsQ0Faa0I7Ozs7dUNBZVQ7QUFDVCxpQkFBSSxXQUFXLEVBQVgsQ0FESzs7QUFHVCxrQkFBSyxTQUFMLENBQWUsR0FBZixDQUFtQixVQUFDLEtBQUQsRUFBUSxFQUFSLEVBQWU7QUFDOUIscUJBQU0sTUFBTSxFQUFFLEVBQUYsQ0FBTixDQUR3QjtBQUU5QixxQkFBTSxPQUFPLElBQUksSUFBSixDQUFTLE1BQVQsQ0FBUCxDQUZ3Qjs7QUFJOUIscUJBQUksQ0FBQyxJQUFELEVBQU8sT0FBWDs7QUFFQSxxQkFBSSxJQUFJLEVBQUosQ0FBTyxXQUFQLENBQUosRUFBd0I7QUFDcEIsOEJBQVMsSUFBVCxJQUFpQixJQUFJLElBQUosQ0FBUyxTQUFULENBQWpCLENBRG9CO2tCQUF4QixNQUVPO0FBQ0gsOEJBQVMsSUFBVCxJQUFpQixJQUFJLEdBQUosRUFBakIsQ0FERztrQkFGUDtjQU5lLENBQW5CLENBSFM7O0FBZ0JULG9CQUFPLFFBQVAsQ0FoQlM7Ozs7Ozs7Ozt3Q0FzQkU7OztBQUNYLGtCQUFLLFNBQUwsQ0FBZSxJQUFmLENBQW9CLFVBQUMsS0FBRCxFQUFRLEVBQVIsRUFBZTtBQUMvQixxQkFBTSxNQUFNLEVBQUUsRUFBRixDQUFOLENBRHlCO0FBRS9CLHdCQUFLLFlBQUwsQ0FBa0IsR0FBbEIsRUFGK0I7Y0FBZixDQUFwQixDQURXOzs7O3FDQU9IO0FBQ1Isa0JBQUssU0FBTCxDQUFlLElBQWYsQ0FBb0IsVUFBQyxLQUFELEVBQVEsRUFBUixFQUFlO0FBQy9CLHFCQUFNLE1BQU0sRUFBRSxFQUFGLENBQU4sQ0FEeUI7QUFFL0IscUJBQUksQ0FBQyxJQUFJLElBQUosQ0FBUyxVQUFULENBQUQsRUFBd0IsSUFBSSxHQUFKLENBQVEsRUFBUixFQUE1QjtjQUZnQixDQUFwQixDQURROzs7WUFuTUs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7QUNUckI7Ozs7Ozs7Ozs7Ozs7Ozs7S0FHcUI7QUFDakIsY0FEaUIsTUFDakIsQ0FBWSxRQUFaLEVBQXNCOzZDQURMLFFBQ0s7O0FBQ2xCLGNBQUssS0FBTCxHQUFhLEVBQUUsUUFBRixDQUFiLENBRGtCO0FBRWxCLGNBQUssTUFBTCxHQUFjLEtBQUssT0FBTCxFQUFkLENBRmtCO0FBR2xCLGNBQUssY0FBTCxHQUFzQixJQUF0QixDQUhrQjs7QUFLbEIsY0FBSyxhQUFMLEdBTGtCO01BQXRCOztnQ0FEaUI7O21DQVNQO0FBQ04sb0JBQU87QUFDSCwyQkFBVSxLQUFLLEtBQUwsQ0FBVyxJQUFYLENBQWdCLHNCQUFoQixDQUFWO0FBQ0EsMEJBQVMsS0FBSyxLQUFMLENBQVcsSUFBWCxDQUFnQixxQkFBaEIsQ0FBVDtjQUZKLENBRE07Ozs7eUNBT007QUFDWixrQkFBSyxLQUFMLENBQ0ssRUFETCxDQUNRLE9BRFIsRUFDaUIsc0JBRGpCLEVBQ3lDLEtBQUssZUFBTCxDQUFxQixJQUFyQixDQUEwQixJQUExQixDQUR6QyxFQUVLLEVBRkwsQ0FFUSxPQUZSLEVBRWlCLHFCQUZqQixFQUV3QyxLQUFLLGNBQUwsQ0FBb0IsSUFBcEIsQ0FBeUIsSUFBekIsQ0FGeEMsRUFHSyxFQUhMLENBR1EsT0FIUixFQUdpQixvQkFIakIsRUFHdUMsS0FBSyxvQkFBTCxDQUEwQixJQUExQixDQUErQixJQUEvQixDQUh2QyxFQURZOzs7O3lDQU9BLEdBQUc7QUFDZixlQUFFLGNBQUYsR0FEZTtBQUVmLGlCQUFNLE9BQU8sSUFBUCxDQUZTO0FBR2YsaUJBQU0sVUFBVSxLQUFLLE1BQUwsQ0FBWSxRQUFaLENBQXFCLElBQXJCLENBQTBCLElBQTFCLENBQVYsQ0FIUzs7QUFLZixrQkFBSyxZQUFMLENBQWtCLE9BQWxCLEVBQ0ssSUFETCxDQUNVLFlBQUk7QUFDTixzQkFBSyxNQUFMLENBQVksUUFBWixDQUFxQixRQUFyQixDQUE4QixVQUE5QixFQUEwQyxJQUExQyxDQUErQyxXQUEvQyxFQUE0RCxHQUE1RCxDQUFnRSxPQUFoRSxFQURNO0FBRU4seUJBQVEsa0NBQVIsRUFGTTtjQUFKLENBRFYsQ0FMZTs7Ozt3Q0FZSixHQUFHO0FBQ2QsZUFBRSxjQUFGLEdBRGM7QUFFZCxpQkFBTSxPQUFPLElBQVAsQ0FGUTtBQUdkLGlCQUFNLFVBQVUsS0FBSyxNQUFMLENBQVksT0FBWixDQUFvQixJQUFwQixDQUF5QixJQUF6QixDQUFWLENBSFE7O0FBS2Qsa0JBQUssV0FBTCxDQUFpQixPQUFqQixFQUNLLElBREwsQ0FDVSxVQUFDLFFBQUQsRUFBWTtBQUNkLHNCQUFLLGNBQUwsR0FBc0IsS0FBSyxhQUFMLENBQW1CLFFBQW5CLENBQXRCLENBRGM7QUFFZCxzQkFBSyxjQUFMLENBQW9CLEtBQXBCLENBQTBCLE1BQTFCLEVBRmM7Y0FBWixDQURWLENBTGM7Ozs7OENBWUcsR0FBRTtBQUNuQixlQUFFLGNBQUYsR0FEbUI7O0FBR25CLGlCQUFNLE9BQU8sSUFBUCxDQUhhO0FBSW5CLGlCQUFNLFdBQVcsRUFBRSxhQUFGLEVBQWlCLFNBQWpCLEVBQVgsQ0FKYTtBQUtuQixpQkFBTSxVQUFVLEtBQUssTUFBTCxDQUFZLE9BQVosQ0FBb0IsSUFBcEIsQ0FBeUIsSUFBekIsQ0FBVixDQUxhOztBQU9uQixrQkFBSyxpQkFBTCxDQUF1QixRQUF2QixFQUFpQyxPQUFqQyxFQUNLLElBREwsQ0FDVSxZQUFJO0FBQ04sc0JBQUssY0FBTCxDQUNLLEVBREwsQ0FDUSxpQkFEUixFQUMyQixZQUFJO0FBQ3ZCLHlCQUFJLE1BQUosQ0FBVyxHQUFYLENBQWUsa0JBQWYsRUFEdUI7a0JBQUosQ0FEM0IsQ0FJSyxLQUpMLENBSVcsTUFKWCxFQURNOztBQU9OLHlCQUFRLGlDQUFSLEVBUE07Y0FBSixDQURWLENBUG1COzs7O3VDQW1CVCxTQUFRO0FBQ2xCLGlCQUFNLFdBQVcsZUFBWCxDQURZO0FBRWxCLGlCQUFJLG1CQUFKLENBRmtCOztBQUlsQixlQUFFLFFBQUYsRUFBWSxNQUFaLEdBSmtCO0FBS2xCLHVCQUFXLEVBQUUsY0FBYyxRQUFkLEdBQXlCLDZDQUF6QixDQUFGLENBQ04sSUFETSxDQUNELE1BREMsRUFDTyxRQURQLEVBRU4sSUFGTSxDQUVELGFBRkMsRUFFYyxNQUZkLEVBR04sTUFITSxDQUdDLE9BSEQsQ0FBWCxDQUxrQjs7QUFVbEIscUJBQVEsUUFBUixDQUFpQixLQUFLLEtBQUwsQ0FBakIsQ0FWa0I7QUFXbEIsb0JBQU8sT0FBUCxDQVhrQjs7Ozs7OztzQ0FlVCxTQUFRO0FBQ2pCLGlCQUFNLE1BQU0sU0FBUyxXQUFULENBQXFCLEVBQXJCLENBQXdCLE1BQXhCLENBQStCLE9BQS9CLENBQXVDLE9BQXZDLEVBQWdELEdBQWhELENBREs7QUFFakIsb0JBQU8sRUFBRSxJQUFGLENBQU8sR0FBUCxFQUFZLEVBQVosQ0FBUCxDQUZpQjs7OztxQ0FLVCxTQUFRO0FBQ2hCLGlCQUFNLE1BQU0sU0FBUyxXQUFULENBQXFCLEVBQXJCLENBQXdCLE1BQXhCLENBQStCLE1BQS9CLENBQXNDLE9BQXRDLEVBQStDLEdBQS9DLENBREk7QUFFaEIsb0JBQU8sRUFBRSxHQUFGLENBQU0sR0FBTixFQUFXLEVBQVgsQ0FBUCxDQUZnQjs7OzsyQ0FLRixNQUFNLElBQUc7QUFDdkIsaUJBQU0sTUFBTSxTQUFTLFdBQVQsQ0FBcUIsRUFBckIsQ0FBd0IsTUFBeEIsQ0FBK0IsTUFBL0IsQ0FBc0MsRUFBdEMsRUFBMEMsR0FBMUMsQ0FEVztBQUV2QixvQkFBTyxFQUFFLElBQUYsQ0FBTyxHQUFQLEVBQVksSUFBWixDQUFQLENBRnVCOzs7Ozs7O2dDQU1iLFVBQVU7QUFDcEIsaUJBQU0sU0FBUyxFQUFFLFFBQUYsQ0FBVCxDQURjO0FBRXBCLGlCQUFJLENBQUMsT0FBTyxNQUFQLEVBQWUsT0FBcEI7O0FBRUEsb0JBQU8sT0FBTyxJQUFQLENBQVksVUFBVSxLQUFWLEVBQWlCLEVBQWpCLEVBQXFCO0FBQ3BDLHFCQUFJLFdBQVcsRUFBRSxFQUFGLENBQVgsQ0FEZ0M7QUFFcEMscUJBQUksT0FBVyxTQUFTLElBQVQsQ0FBYyxpQkFBZCxDQUFYLENBRmdDOztBQUlwQyxxQkFBSSxDQUFDLElBQUQsRUFBTztBQUNQLDRCQUFPLElBQUksTUFBSixDQUFXLEVBQVgsQ0FBUCxDQURPO0FBRVAsOEJBQVMsSUFBVCxDQUFjLFFBQWQsRUFBd0IsSUFBeEIsRUFGTztrQkFBWDtjQUplLENBQW5CLENBSm9COzs7WUFqR1A7Ozs7Ozs7Ozs7Ozs7O0FDSHJCOzs7Ozs7Ozs7Ozs7Ozs7Ozs7OztBQVFBLEdBQUUsWUFBVTtBQUNSLDBCQUFXLE1BQVgsQ0FBa0Isb0JBQWxCLEVBRFE7QUFFUiw2QkFBZ0IsTUFBaEIsQ0FBdUIsd0JBQXZCLEVBRlE7QUFHUix5QkFBVSxNQUFWLENBQWlCLGVBQWpCLEVBSFE7QUFJUiwyQkFBWSxNQUFaLENBQW1CLGtCQUFuQixFQUpROztBQU1SLFNBQUksTUFBSixDQUNLLEdBREwsQ0FDUyxrQkFEVCxFQUM2QixZQUFVO0FBQy9CLGFBQU0sVUFBVSxFQUFFLFVBQUYsRUFBYyxHQUFkLEVBQVYsQ0FEeUI7QUFFL0IsZ0JBQU8sUUFBUCxDQUFnQixPQUFoQixDQUF3QixTQUFTLFdBQVQsQ0FBcUIsRUFBckIsQ0FBd0IsTUFBeEIsQ0FBK0IsS0FBL0IsQ0FBcUMsT0FBckMsRUFBOEMsR0FBOUMsQ0FBeEIsQ0FGK0I7TUFBVixDQUQ3QixDQU5RO0VBQVYsQ0FBRixDOzs7Ozs7QUNSQTs7Ozs7Ozs7Ozs7Ozs7OztLQUVxQjtBQUVqQixjQUZpQixNQUVqQixDQUFZLE9BQVosRUFBcUI7NkNBRkosUUFFSTs7QUFDakIsY0FBSyxJQUFMLEdBQVksT0FBWixDQURpQjtBQUVqQixjQUFLLEtBQUwsR0FBYSxFQUFFLE9BQUYsQ0FBYixDQUZpQjs7QUFJakIsYUFBSSxDQUFDLGFBQUQsRUFBZ0I7QUFDaEIscUJBQVEsR0FBUixDQUFZLHNDQUFaLEVBRGdCO0FBRWhCLG9CQUZnQjtVQUFwQjs7QUFLQSxjQUFLLE1BQUwsR0FBYyxJQUFJLGFBQUosQ0FBa0IsT0FBbEIsQ0FBZCxDQVRpQjtBQVVqQixjQUFLLE9BQUwsR0FBZSxFQUFFLGlDQUFGLEVBQXFDLEtBQXJDLEVBQWYsQ0FWaUI7O0FBWWpCLGNBQUssYUFBTCxHQVppQjtNQUFyQjs7Z0NBRmlCOzt5Q0FpQkQ7QUFDWixpQkFBTSxPQUFPLElBQVAsQ0FETTs7QUFHWixrQkFBSyxNQUFMLENBQVksRUFBWixDQUFlLFdBQWYsRUFBNEIsS0FBSyxhQUFMLENBQW1CLElBQW5CLENBQXdCLElBQXhCLENBQTVCLEVBSFk7QUFJWixrQkFBSyxPQUFMLENBQWEsRUFBYixDQUFnQixZQUFoQixFQUE4QixZQUFLO0FBQzNCLHNCQUFLLE9BQUwsQ0FDSyxJQURMLENBQ1UsT0FEVixFQUNtQixXQURuQixFQUVLLE9BRkwsQ0FFYSxNQUZiLEVBRDJCO2NBQUwsQ0FBOUIsQ0FKWTs7Ozt5Q0FXRDtBQUNYLGlCQUFNLFFBQVEsS0FBSyxLQUFMLENBREg7QUFFWCxrQkFBSyxPQUFMLENBQWEsT0FBYixDQUFxQixNQUFyQixFQUZXOztBQUlYLG1CQUFNLElBQU4sQ0FBVyxPQUFYLEVBQW9CLFFBQXBCLEVBQ0ssT0FETCxDQUNhLE1BRGIsRUFKVzs7QUFPWCx3QkFBVyxZQUFJO0FBQ1gsdUJBQU0sT0FBTixDQUFjLE1BQWQsRUFDSyxJQURMLENBQ1UsT0FEVixFQUNtQixFQURuQixFQURXO2NBQUosRUFHUixJQUhILEVBUFc7Ozs7Ozs7Z0NBY0QsVUFBVTtBQUNwQixpQkFBTSxTQUFTLEVBQUUsUUFBRixDQUFULENBRGM7QUFFcEIsaUJBQUksQ0FBQyxPQUFPLE1BQVAsRUFBZSxPQUFwQjs7QUFFQSxvQkFBTyxPQUFPLElBQVAsQ0FBWSxVQUFVLEtBQVYsRUFBaUIsRUFBakIsRUFBcUI7QUFDcEMscUJBQUksV0FBVyxFQUFFLEVBQUYsQ0FBWCxDQURnQztBQUVwQyxxQkFBSSxPQUFXLFNBQVMsSUFBVCxDQUFjLGtCQUFkLENBQVgsQ0FGZ0M7O0FBSXBDLHFCQUFJLENBQUMsSUFBRCxFQUFPO0FBQ1AsNEJBQU8sSUFBSSxNQUFKLENBQVcsRUFBWCxDQUFQLENBRE87QUFFUCw4QkFBUyxJQUFULENBQWMsUUFBZCxFQUF3QixJQUF4QixFQUZPO2tCQUFYO2NBSmUsQ0FBbkIsQ0FKb0I7OztZQTFDUDs7Ozs7Ozs7O0FDRnJCOzs7Ozs7Ozs7Ozs7Ozs7O0tBRXFCO0FBQ2pCLGNBRGlCLE1BQ2pCLENBQVksUUFBWixFQUFxQjs2Q0FESixRQUNJOztBQUNqQixjQUFLLEtBQUwsR0FBYSxFQUFFLFFBQUYsQ0FBYixDQURpQjtBQUVqQixjQUFLLE1BQUwsR0FBYyxLQUFLLE9BQUwsRUFBZCxDQUZpQjtBQUdqQixjQUFLLFFBQUwsR0FBZ0IsRUFBRSxRQUFGLENBQVksRUFBRSx1QkFBRixFQUEyQixJQUEzQixFQUFaLENBQWhCLENBSGlCOztBQUtqQixjQUFLLG9CQUFMLEdBTGlCO0FBTWpCLGNBQUssWUFBTCxHQU5pQjtNQUFyQjs7Z0NBRGlCOzttQ0FVUjtBQUNMLGlCQUFNLFFBQVEsS0FBSyxLQUFMLENBRFQ7O0FBR0wsb0JBQU87QUFDSCxvQ0FBbUIsTUFBTSxJQUFOLENBQVcseUJBQVgsQ0FBbkI7QUFDQSw0QkFBVyxNQUFNLElBQU4sQ0FBVyw2QkFBWCxDQUFYO0FBQ0EsK0JBQWMsTUFBTSxJQUFOLENBQVcsMkJBQVgsQ0FBZDtBQUNBLDBCQUFTLE1BQU0sSUFBTixDQUFXLDJCQUFYLENBQVQ7Y0FKSixDQUhLOzs7O3dDQVdLOzs7QUFDVixrQkFBSyxLQUFMLENBQ0ssRUFETCxDQUNRLFFBRFIsRUFDa0IsK0JBRGxCLEVBQ21ELEtBQUssb0JBQUwsQ0FBMEIsSUFBMUIsQ0FBK0IsSUFBL0IsQ0FEbkQsRUFFSyxFQUZMLENBRVEsc0JBRlIsRUFFZ0MsNkJBRmhDLEVBRStELEtBQUssb0JBQUwsQ0FBMEIsSUFBMUIsQ0FBK0IsSUFBL0IsQ0FGL0QsRUFHSyxFQUhMLENBR1EsT0FIUixFQUdpQiwyQkFIakIsRUFHOEMsVUFBQyxDQUFELEVBQU07QUFDNUMsbUJBQUUsY0FBRixHQUQ0Qzs7QUFHNUMscUJBQU0sUUFBUSxFQUFFLEVBQUUsYUFBRixDQUFWLENBSHNDO0FBSTVDLHVCQUFLLG1CQUFMLENBQXlCLEtBQXpCLEVBSjRDO2NBQU4sQ0FIOUMsQ0FEVTs7QUFXVixpQkFBSSxNQUFKLENBQVcsR0FBWCxDQUFlLHVCQUFmLEVBQXdDLEtBQUssaUJBQUwsQ0FBdUIsSUFBdkIsQ0FBNEIsSUFBNUIsQ0FBeEMsRUFYVTs7OztnREFjUTtBQUNsQixpQkFBTSxTQUFTLEtBQUssTUFBTCxDQURHO0FBRWxCLGlCQUFNLGdCQUFnQixLQUFLLEtBQUwsQ0FBVyxJQUFYLENBQWdCLCtCQUFoQixDQUFoQixDQUZZO0FBR2xCLGlCQUFJLFFBQVEsSUFBUixDQUhjOztBQUtsQixpQkFBSSxDQUFDLGNBQWMsTUFBZCxDQUFxQixVQUFyQixFQUFpQyxNQUFqQyxFQUF3QztBQUN6Qyx5QkFBUSxLQUFSLENBRHlDO2NBQTdDOztBQUlBLGlCQUFJLENBQUMsV0FBVyxJQUFYLENBQWdCLE9BQU8sU0FBUCxDQUFpQixHQUFqQixFQUFoQixDQUFELEVBQTBDO0FBQzFDLHlCQUFRLEtBQVIsQ0FEMEM7Y0FBOUM7O0FBSUEsaUJBQUksQ0FBQyxLQUFELEVBQU87QUFDUCx3QkFBTyxPQUFQLENBQWUsSUFBZixDQUFvQixVQUFwQixFQUFnQyxVQUFoQyxFQURPO2NBQVgsTUFFTztBQUNILHdCQUFPLE9BQVAsQ0FBZSxVQUFmLENBQTBCLFVBQTFCLEVBREc7Y0FGUDs7Ozs7Ozs7Ozs7MkNBWWMsYUFBYTtBQUMzQixpQkFBSSxXQUFXLFlBQVksS0FBWixDQUFrQixDQUFsQixDQUFvQixJQUFwQixFQUEwQixFQUExQixDQUFYLENBRHVCO0FBRTNCLGlCQUFJLGFBQUo7aUJBQU8sYUFBUCxDQUYyQjs7QUFJM0Isa0JBQUssSUFBSSxDQUFKLEVBQU8sSUFBSSxTQUFTLE1BQVQsRUFBaUIsSUFBSSxDQUFKLEVBQU8sR0FBeEMsRUFBNkM7QUFDekMscUJBQU0sT0FBTztBQUNULDRCQUFPLENBQVA7QUFDQSw0QkFBTyxTQUFTLENBQVQsRUFBWSxNQUFaLENBQW1CLEVBQW5CO0FBQ1AsMkJBQU0sU0FBUyxDQUFULEVBQVksTUFBWixDQUFtQixJQUFuQjtBQUNOLDZCQUFRLEVBQUUsYUFBRixDQUFnQixTQUFTLENBQVQsRUFBWSxVQUFaLENBQXVCLE1BQXZCLENBQXhCO2tCQUpFLENBRG1DO0FBT3pDLHFCQUFNLFFBQVEsS0FBSyxRQUFMLENBQWMsSUFBZCxDQUFSLENBUG1DOztBQVN6QyxzQkFBSyxNQUFMLENBQVksaUJBQVosQ0FBOEIsTUFBOUIsQ0FBcUMsS0FBckMsRUFUeUM7Y0FBN0M7Ozs7Ozs7Ozs7OzZDQWtCZ0IsT0FBTztBQUN2QixpQkFBTSxnQkFBZ0IsS0FBSyxLQUFMLENBQVcsSUFBWCxDQUFnQiwrQkFBaEIsQ0FBaEIsQ0FEaUI7QUFFdkIsaUJBQU0sYUFBYSxNQUFNLElBQU4sQ0FBVyxvQkFBWCxDQUFiLENBRmlCO0FBR3ZCLGlCQUFNLGVBQWUsS0FBSyxNQUFMLENBQVksWUFBWixDQUhFOztBQUt2QixpQkFBSSxNQUFNLFFBQU4sQ0FBZSxnQkFBZixDQUFKLEVBQXNDLE9BQXRDOztBQUVBLHFCQUFRLFVBQVI7QUFDSSxzQkFBSyxLQUFMO0FBQVk7QUFDUix1Q0FBYyxJQUFkLENBQW1CLFNBQW5CLEVBQThCLElBQTlCLEVBRFE7QUFFUiwrQkFGUTtzQkFBWjtBQURKLHNCQUtTLE1BQUw7QUFBYTtBQUNULHVDQUFjLElBQWQsQ0FBbUIsU0FBbkIsRUFBOEIsS0FBOUIsRUFDSyxNQURMLENBQ1ksa0JBRFosRUFDZ0MsSUFEaEMsQ0FDcUMsU0FEckMsRUFDZ0QsSUFEaEQsRUFEUztBQUdULCtCQUhTO3NCQUFiO0FBTEosc0JBVVMsU0FBTDtBQUFnQjtBQUNaLHVDQUNLLElBREwsQ0FDVSxTQURWLEVBQ3FCLElBRHJCLEVBRUssTUFGTCxDQUVZLGtCQUZaLEVBRWdDLElBRmhDLENBRXFDLFNBRnJDLEVBRWdELEtBRmhELEVBRFk7QUFJWiwrQkFKWTtzQkFBaEI7QUFWSjtBQWdCYTtBQUNMLHVDQUFjLElBQWQsQ0FBbUIsU0FBbkIsRUFBOEIsS0FBOUIsRUFESztBQUVMLCtCQUZLO3NCQUFUO0FBaEJKLGNBUHVCOztBQTZCdkIsMEJBQWEsV0FBYixDQUF5QixnQkFBekIsRUE3QnVCO0FBOEJ2QixtQkFBTSxRQUFOLENBQWUsZ0JBQWYsRUE5QnVCOztBQWdDdkIsa0JBQUssb0JBQUwsR0FoQ3VCOzs7Ozs7O2dDQW9DYixVQUFVO0FBQ3BCLGlCQUFNLFNBQVMsRUFBRSxRQUFGLENBQVQsQ0FEYztBQUVwQixpQkFBSSxDQUFDLE9BQU8sTUFBUCxFQUFlLE9BQXBCOztBQUVBLG9CQUFPLE9BQU8sSUFBUCxDQUFZLFVBQVUsS0FBVixFQUFpQixFQUFqQixFQUFxQjtBQUNwQyxxQkFBSSxXQUFXLEVBQUUsRUFBRixDQUFYLENBRGdDO0FBRXBDLHFCQUFJLE9BQVcsU0FBUyxJQUFULENBQWMscUJBQWQsQ0FBWCxDQUZnQzs7QUFJcEMscUJBQUksQ0FBQyxJQUFELEVBQU87QUFDUCw0QkFBTyxJQUFJLE1BQUosQ0FBVyxFQUFYLENBQVAsQ0FETztBQUVQLDhCQUFTLElBQVQsQ0FBYyxRQUFkLEVBQXdCLElBQXhCLEVBRk87a0JBQVg7Y0FKZSxDQUFuQixDQUpvQjs7O1lBdEhQOzs7Ozs7Ozs7QUNGckI7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7O0tBSXFCO0FBQ2pCLGNBRGlCLE1BQ2pCLENBQVksUUFBWixFQUFxQjs2Q0FESixRQUNJOztBQUNqQixjQUFLLEtBQUwsR0FBYSxFQUFFLFFBQUYsQ0FBYixDQURpQjtBQUVqQixjQUFLLE1BQUwsR0FBYyxLQUFLLE9BQUwsRUFBZCxDQUZpQjtBQUdqQixjQUFLLFVBQUwsR0FBa0IseUJBQWUsS0FBSyxNQUFMLENBQVksU0FBWixDQUFqQyxDQUhpQjtBQUlqQixjQUFLLFFBQUwsR0FBZ0IsS0FBSyxNQUFMLENBQVksU0FBWixDQUFzQixHQUF0QixFQUFoQixDQUppQjs7QUFNakIsY0FBSyxZQUFMLEdBTmlCO01BQXJCOztnQ0FEaUI7O21DQVVSO0FBQ0wsaUJBQU0sUUFBUSxLQUFLLEtBQUwsQ0FEVDs7QUFHTCxvQkFBTztBQUNILHdCQUFPLE1BQU0sSUFBTixDQUFXLHNCQUFYLENBQVA7QUFDQSxtQ0FBa0IsTUFBTSxJQUFOLENBQVcseUJBQVgsQ0FBbEI7QUFDQSx5QkFBUSxNQUFNLElBQU4sQ0FBVyxxQkFBWCxDQUFSO0FBQ0Esd0JBQU8sTUFBTSxJQUFOLENBQVcsc0JBQVgsQ0FBUDtBQUNBLDRCQUFXLE1BQU0sSUFBTixDQUFXLDBCQUFYLENBQVg7QUFDQSwwQkFBUyxNQUFNLElBQU4sQ0FBVyx3QkFBWCxDQUFUO2NBTkosQ0FISzs7Ozt3Q0FhSztBQUNWLGlCQUFNLE9BQU8sSUFBUCxDQURJOztBQUdWLGtCQUFLLEtBQUwsQ0FDSyxFQURMLENBQ1EsT0FEUixFQUNpQixzQkFEakIsRUFDeUMsS0FBSyxpQkFBTCxDQUF1QixJQUF2QixDQUE0QixJQUE1QixDQUR6QyxFQUVLLEVBRkwsQ0FFUSxPQUZSLEVBRWlCLHNCQUZqQixFQUV5QyxLQUFLLG1CQUFMLENBQXlCLElBQXpCLENBQThCLElBQTlCLENBRnpDLEVBR0ssRUFITCxDQUdRLGVBSFIsRUFHeUIsVUFBQyxDQUFELEVBQUs7QUFDdEIsbUJBQUUsZUFBRixHQURzQjtBQUV0QixzQkFBSyxhQUFMLEdBRnNCO2NBQUwsQ0FIekIsQ0FIVTs7QUFXVixrQkFBSyxNQUFMLENBQVksS0FBWixDQUFrQixFQUFsQixDQUFxQixRQUFyQixFQUErQixLQUFLLGFBQUwsQ0FBbUIsSUFBbkIsQ0FBd0IsSUFBeEIsQ0FBL0IsRUFYVTs7OzsyQ0FjSSxHQUFFO0FBQ2hCLGVBQUUsY0FBRixHQURnQjtBQUVoQixrQkFBSyxNQUFMLENBQVksTUFBWixDQUFtQixLQUFuQixDQUF5QixNQUF6QixFQUZnQjs7Ozs2Q0FLQSxHQUFFO0FBQ2xCLGVBQUUsY0FBRixHQURrQjtBQUVsQixpQkFBTSxTQUFTLEtBQUssTUFBTCxDQUZHOztBQUlsQixvQkFBTyxTQUFQLENBQ0ssR0FETCxDQUNTLE9BQU8sZ0JBQVAsQ0FBd0IsSUFBeEIsRUFEVCxFQUVLLE9BRkwsQ0FFYSxpQkFGYixFQUprQjs7Ozt5Q0FTUDtBQUNYLGtCQUFLLE1BQUwsQ0FBWSxTQUFaLENBQXNCLEdBQXRCLENBQTBCLEtBQUssUUFBTCxDQUExQixDQURXO0FBRVgsa0JBQUssVUFBTCxDQUFnQixZQUFoQixHQUZXOzs7O3VDQUtELEdBQUU7QUFDWixlQUFFLGNBQUYsR0FEWTtBQUVaLGlCQUFNLE9BQU8sSUFBUCxDQUZNO0FBR1osaUJBQU0sU0FBUyxLQUFLLE1BQUwsQ0FISDs7QUFLWixpQkFBSSxDQUFDLEtBQUssVUFBTCxDQUFnQixhQUFoQixFQUFELEVBQWtDLE9BQXRDO0FBQ0EsaUJBQU0sV0FBVyxLQUFLLFVBQUwsQ0FBZ0IsV0FBaEIsRUFBWCxDQU5NOztBQVFaLGtCQUFLLGlCQUFMLENBQXVCLFFBQXZCLEVBQ0ssSUFETCxDQUNVLFlBQUk7QUFDTixzQkFBSyxRQUFMLEdBQWdCLE9BQU8sU0FBUCxDQUFpQixHQUFqQixFQUFoQixDQURNO0FBRU4sd0JBQU8sTUFBUCxDQUFjLEtBQWQsQ0FBb0IsTUFBcEIsRUFGTTs7QUFJTix5QkFBUSxtQkFBUixFQUpNO2NBQUosQ0FEVixDQU9LLElBUEwsQ0FPVSxVQUFDLFFBQUQsRUFBWTtBQUNkLHFCQUFNLE9BQU8sRUFBRSxTQUFGLENBQVksU0FBUyxZQUFULENBQVosQ0FBbUMsSUFBbkMsQ0FEQzs7QUFHZCxxQkFBSSxDQUFDLEtBQUssTUFBTCxFQUFhLE9BQWxCOztBQUVBLHNCQUFLLFVBQUwsQ0FBZ0IsU0FBaEIsQ0FBMEIsS0FBSyxNQUFMLENBQTFCLENBTGM7Y0FBWixDQVBWLENBUlk7Ozs7Ozs7MkNBeUJFLFVBQVM7QUFDdkIsaUJBQU0sTUFBTSxLQUFLLE1BQUwsQ0FBWSxLQUFaLENBQWtCLElBQWxCLENBQXVCLFFBQXZCLENBQU4sQ0FEaUI7QUFFdkIsb0JBQU8sRUFBRSxJQUFGLENBQU8sR0FBUCxFQUFZLFFBQVosQ0FBUCxDQUZ1Qjs7Ozs7OztnQ0FNYixVQUFVO0FBQ3BCLGlCQUFNLFNBQVMsRUFBRSxRQUFGLENBQVQsQ0FEYztBQUVwQixpQkFBSSxDQUFDLE9BQU8sTUFBUCxFQUFlLE9BQXBCOztBQUVBLG9CQUFPLE9BQU8sSUFBUCxDQUFZLFVBQVUsS0FBVixFQUFpQixFQUFqQixFQUFxQjtBQUNwQyxxQkFBSSxXQUFXLEVBQUUsRUFBRixDQUFYLENBRGdDO0FBRXBDLHFCQUFJLE9BQVcsU0FBUyxJQUFULENBQWMseUJBQWQsQ0FBWCxDQUZnQzs7QUFJcEMscUJBQUksQ0FBQyxJQUFELEVBQU87QUFDUCw0QkFBTyxJQUFJLE1BQUosQ0FBVyxFQUFYLENBQVAsQ0FETztBQUVQLDhCQUFTLElBQVQsQ0FBYyxRQUFkLEVBQXdCLElBQXhCLEVBRk87a0JBQVg7Y0FKZSxDQUFuQixDQUpvQjs7O1lBdkZQIiwiZmlsZSI6ImV2ZW50LWRldGFpbHMtcGFnZS5qcyIsInNvdXJjZXNDb250ZW50IjpbIiBcdC8vIFRoZSBtb2R1bGUgY2FjaGVcbiBcdHZhciBpbnN0YWxsZWRNb2R1bGVzID0ge307XG5cbiBcdC8vIFRoZSByZXF1aXJlIGZ1bmN0aW9uXG4gXHRmdW5jdGlvbiBfX3dlYnBhY2tfcmVxdWlyZV9fKG1vZHVsZUlkKSB7XG5cbiBcdFx0Ly8gQ2hlY2sgaWYgbW9kdWxlIGlzIGluIGNhY2hlXG4gXHRcdGlmKGluc3RhbGxlZE1vZHVsZXNbbW9kdWxlSWRdKVxuIFx0XHRcdHJldHVybiBpbnN0YWxsZWRNb2R1bGVzW21vZHVsZUlkXS5leHBvcnRzO1xuXG4gXHRcdC8vIENyZWF0ZSBhIG5ldyBtb2R1bGUgKGFuZCBwdXQgaXQgaW50byB0aGUgY2FjaGUpXG4gXHRcdHZhciBtb2R1bGUgPSBpbnN0YWxsZWRNb2R1bGVzW21vZHVsZUlkXSA9IHtcbiBcdFx0XHRleHBvcnRzOiB7fSxcbiBcdFx0XHRpZDogbW9kdWxlSWQsXG4gXHRcdFx0bG9hZGVkOiBmYWxzZVxuIFx0XHR9O1xuXG4gXHRcdC8vIEV4ZWN1dGUgdGhlIG1vZHVsZSBmdW5jdGlvblxuIFx0XHRtb2R1bGVzW21vZHVsZUlkXS5jYWxsKG1vZHVsZS5leHBvcnRzLCBtb2R1bGUsIG1vZHVsZS5leHBvcnRzLCBfX3dlYnBhY2tfcmVxdWlyZV9fKTtcblxuIFx0XHQvLyBGbGFnIHRoZSBtb2R1bGUgYXMgbG9hZGVkXG4gXHRcdG1vZHVsZS5sb2FkZWQgPSB0cnVlO1xuXG4gXHRcdC8vIFJldHVybiB0aGUgZXhwb3J0cyBvZiB0aGUgbW9kdWxlXG4gXHRcdHJldHVybiBtb2R1bGUuZXhwb3J0cztcbiBcdH1cblxuXG4gXHQvLyBleHBvc2UgdGhlIG1vZHVsZXMgb2JqZWN0IChfX3dlYnBhY2tfbW9kdWxlc19fKVxuIFx0X193ZWJwYWNrX3JlcXVpcmVfXy5tID0gbW9kdWxlcztcblxuIFx0Ly8gZXhwb3NlIHRoZSBtb2R1bGUgY2FjaGVcbiBcdF9fd2VicGFja19yZXF1aXJlX18uYyA9IGluc3RhbGxlZE1vZHVsZXM7XG5cbiBcdC8vIF9fd2VicGFja19wdWJsaWNfcGF0aF9fXG4gXHRfX3dlYnBhY2tfcmVxdWlyZV9fLnAgPSBcIlwiO1xuXG4gXHQvLyBMb2FkIGVudHJ5IG1vZHVsZSBhbmQgcmV0dXJuIGV4cG9ydHNcbiBcdHJldHVybiBfX3dlYnBhY2tfcmVxdWlyZV9fKDApO1xuXG5cblxuLyoqIFdFQlBBQ0sgRk9PVEVSICoqXG4gKiogd2VicGFjay9ib290c3RyYXAgZjBmMTY2OTBiZDk3YTI1NWNmYjlcbiAqKi8iLCJcInVzZSBzdHJpY3RcIjtcblxuZXhwb3J0cy5fX2VzTW9kdWxlID0gdHJ1ZTtcblxuZXhwb3J0cy5kZWZhdWx0ID0gZnVuY3Rpb24gKGluc3RhbmNlLCBDb25zdHJ1Y3Rvcikge1xuICBpZiAoIShpbnN0YW5jZSBpbnN0YW5jZW9mIENvbnN0cnVjdG9yKSkge1xuICAgIHRocm93IG5ldyBUeXBlRXJyb3IoXCJDYW5ub3QgY2FsbCBhIGNsYXNzIGFzIGEgZnVuY3Rpb25cIik7XG4gIH1cbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9oZWxwZXJzL2NsYXNzQ2FsbENoZWNrLmpzXG4gKiogbW9kdWxlIGlkID0gM1xuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwiXCJ1c2Ugc3RyaWN0XCI7XG5cbmV4cG9ydHMuX19lc01vZHVsZSA9IHRydWU7XG5cbnZhciBfZGVmaW5lUHJvcGVydHkgPSByZXF1aXJlKFwiYmFiZWwtcnVudGltZS9jb3JlLWpzL29iamVjdC9kZWZpbmUtcHJvcGVydHlcIik7XG5cbnZhciBfZGVmaW5lUHJvcGVydHkyID0gX2ludGVyb3BSZXF1aXJlRGVmYXVsdChfZGVmaW5lUHJvcGVydHkpO1xuXG5mdW5jdGlvbiBfaW50ZXJvcFJlcXVpcmVEZWZhdWx0KG9iaikgeyByZXR1cm4gb2JqICYmIG9iai5fX2VzTW9kdWxlID8gb2JqIDogeyBkZWZhdWx0OiBvYmogfTsgfVxuXG5leHBvcnRzLmRlZmF1bHQgPSBmdW5jdGlvbiAoKSB7XG4gIGZ1bmN0aW9uIGRlZmluZVByb3BlcnRpZXModGFyZ2V0LCBwcm9wcykge1xuICAgIGZvciAodmFyIGkgPSAwOyBpIDwgcHJvcHMubGVuZ3RoOyBpKyspIHtcbiAgICAgIHZhciBkZXNjcmlwdG9yID0gcHJvcHNbaV07XG4gICAgICBkZXNjcmlwdG9yLmVudW1lcmFibGUgPSBkZXNjcmlwdG9yLmVudW1lcmFibGUgfHwgZmFsc2U7XG4gICAgICBkZXNjcmlwdG9yLmNvbmZpZ3VyYWJsZSA9IHRydWU7XG4gICAgICBpZiAoXCJ2YWx1ZVwiIGluIGRlc2NyaXB0b3IpIGRlc2NyaXB0b3Iud3JpdGFibGUgPSB0cnVlO1xuICAgICAgKDAsIF9kZWZpbmVQcm9wZXJ0eTIuZGVmYXVsdCkodGFyZ2V0LCBkZXNjcmlwdG9yLmtleSwgZGVzY3JpcHRvcik7XG4gICAgfVxuICB9XG5cbiAgcmV0dXJuIGZ1bmN0aW9uIChDb25zdHJ1Y3RvciwgcHJvdG9Qcm9wcywgc3RhdGljUHJvcHMpIHtcbiAgICBpZiAocHJvdG9Qcm9wcykgZGVmaW5lUHJvcGVydGllcyhDb25zdHJ1Y3Rvci5wcm90b3R5cGUsIHByb3RvUHJvcHMpO1xuICAgIGlmIChzdGF0aWNQcm9wcykgZGVmaW5lUHJvcGVydGllcyhDb25zdHJ1Y3Rvciwgc3RhdGljUHJvcHMpO1xuICAgIHJldHVybiBDb25zdHJ1Y3RvcjtcbiAgfTtcbn0oKTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL2hlbHBlcnMvY3JlYXRlQ2xhc3MuanNcbiAqKiBtb2R1bGUgaWQgPSA0XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJtb2R1bGUuZXhwb3J0cyA9IHsgXCJkZWZhdWx0XCI6IHJlcXVpcmUoXCJjb3JlLWpzL2xpYnJhcnkvZm4vb2JqZWN0L2RlZmluZS1wcm9wZXJ0eVwiKSwgX19lc01vZHVsZTogdHJ1ZSB9O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvY29yZS1qcy9vYmplY3QvZGVmaW5lLXByb3BlcnR5LmpzXG4gKiogbW9kdWxlIGlkID0gNVxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwicmVxdWlyZSgnLi4vLi4vbW9kdWxlcy9lczYub2JqZWN0LmRlZmluZS1wcm9wZXJ0eScpO1xudmFyICRPYmplY3QgPSByZXF1aXJlKCcuLi8uLi9tb2R1bGVzL19jb3JlJykuT2JqZWN0O1xubW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbiBkZWZpbmVQcm9wZXJ0eShpdCwga2V5LCBkZXNjKXtcbiAgcmV0dXJuICRPYmplY3QuZGVmaW5lUHJvcGVydHkoaXQsIGtleSwgZGVzYyk7XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvZm4vb2JqZWN0L2RlZmluZS1wcm9wZXJ0eS5qc1xuICoqIG1vZHVsZSBpZCA9IDZcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsInZhciAkZXhwb3J0ID0gcmVxdWlyZSgnLi9fZXhwb3J0Jyk7XHJcbi8vIDE5LjEuMi40IC8gMTUuMi4zLjYgT2JqZWN0LmRlZmluZVByb3BlcnR5KE8sIFAsIEF0dHJpYnV0ZXMpXHJcbiRleHBvcnQoJGV4cG9ydC5TICsgJGV4cG9ydC5GICogIXJlcXVpcmUoJy4vX2Rlc2NyaXB0b3JzJyksICdPYmplY3QnLCB7ZGVmaW5lUHJvcGVydHk6IHJlcXVpcmUoJy4vX29iamVjdC1kcCcpLmZ9KTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvZXM2Lm9iamVjdC5kZWZpbmUtcHJvcGVydHkuanNcbiAqKiBtb2R1bGUgaWQgPSA3XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJ2YXIgZ2xvYmFsICAgID0gcmVxdWlyZSgnLi9fZ2xvYmFsJylcbiAgLCBjb3JlICAgICAgPSByZXF1aXJlKCcuL19jb3JlJylcbiAgLCBjdHggICAgICAgPSByZXF1aXJlKCcuL19jdHgnKVxuICAsIGhpZGUgICAgICA9IHJlcXVpcmUoJy4vX2hpZGUnKVxuICAsIFBST1RPVFlQRSA9ICdwcm90b3R5cGUnO1xuXG52YXIgJGV4cG9ydCA9IGZ1bmN0aW9uKHR5cGUsIG5hbWUsIHNvdXJjZSl7XG4gIHZhciBJU19GT1JDRUQgPSB0eXBlICYgJGV4cG9ydC5GXG4gICAgLCBJU19HTE9CQUwgPSB0eXBlICYgJGV4cG9ydC5HXG4gICAgLCBJU19TVEFUSUMgPSB0eXBlICYgJGV4cG9ydC5TXG4gICAgLCBJU19QUk9UTyAgPSB0eXBlICYgJGV4cG9ydC5QXG4gICAgLCBJU19CSU5EICAgPSB0eXBlICYgJGV4cG9ydC5CXG4gICAgLCBJU19XUkFQICAgPSB0eXBlICYgJGV4cG9ydC5XXG4gICAgLCBleHBvcnRzICAgPSBJU19HTE9CQUwgPyBjb3JlIDogY29yZVtuYW1lXSB8fCAoY29yZVtuYW1lXSA9IHt9KVxuICAgICwgZXhwUHJvdG8gID0gZXhwb3J0c1tQUk9UT1RZUEVdXG4gICAgLCB0YXJnZXQgICAgPSBJU19HTE9CQUwgPyBnbG9iYWwgOiBJU19TVEFUSUMgPyBnbG9iYWxbbmFtZV0gOiAoZ2xvYmFsW25hbWVdIHx8IHt9KVtQUk9UT1RZUEVdXG4gICAgLCBrZXksIG93biwgb3V0O1xuICBpZihJU19HTE9CQUwpc291cmNlID0gbmFtZTtcbiAgZm9yKGtleSBpbiBzb3VyY2Upe1xuICAgIC8vIGNvbnRhaW5zIGluIG5hdGl2ZVxuICAgIG93biA9ICFJU19GT1JDRUQgJiYgdGFyZ2V0ICYmIHRhcmdldFtrZXldICE9PSB1bmRlZmluZWQ7XG4gICAgaWYob3duICYmIGtleSBpbiBleHBvcnRzKWNvbnRpbnVlO1xuICAgIC8vIGV4cG9ydCBuYXRpdmUgb3IgcGFzc2VkXG4gICAgb3V0ID0gb3duID8gdGFyZ2V0W2tleV0gOiBzb3VyY2Vba2V5XTtcbiAgICAvLyBwcmV2ZW50IGdsb2JhbCBwb2xsdXRpb24gZm9yIG5hbWVzcGFjZXNcbiAgICBleHBvcnRzW2tleV0gPSBJU19HTE9CQUwgJiYgdHlwZW9mIHRhcmdldFtrZXldICE9ICdmdW5jdGlvbicgPyBzb3VyY2Vba2V5XVxuICAgIC8vIGJpbmQgdGltZXJzIHRvIGdsb2JhbCBmb3IgY2FsbCBmcm9tIGV4cG9ydCBjb250ZXh0XG4gICAgOiBJU19CSU5EICYmIG93biA/IGN0eChvdXQsIGdsb2JhbClcbiAgICAvLyB3cmFwIGdsb2JhbCBjb25zdHJ1Y3RvcnMgZm9yIHByZXZlbnQgY2hhbmdlIHRoZW0gaW4gbGlicmFyeVxuICAgIDogSVNfV1JBUCAmJiB0YXJnZXRba2V5XSA9PSBvdXQgPyAoZnVuY3Rpb24oQyl7XG4gICAgICB2YXIgRiA9IGZ1bmN0aW9uKGEsIGIsIGMpe1xuICAgICAgICBpZih0aGlzIGluc3RhbmNlb2YgQyl7XG4gICAgICAgICAgc3dpdGNoKGFyZ3VtZW50cy5sZW5ndGgpe1xuICAgICAgICAgICAgY2FzZSAwOiByZXR1cm4gbmV3IEM7XG4gICAgICAgICAgICBjYXNlIDE6IHJldHVybiBuZXcgQyhhKTtcbiAgICAgICAgICAgIGNhc2UgMjogcmV0dXJuIG5ldyBDKGEsIGIpO1xuICAgICAgICAgIH0gcmV0dXJuIG5ldyBDKGEsIGIsIGMpO1xuICAgICAgICB9IHJldHVybiBDLmFwcGx5KHRoaXMsIGFyZ3VtZW50cyk7XG4gICAgICB9O1xuICAgICAgRltQUk9UT1RZUEVdID0gQ1tQUk9UT1RZUEVdO1xuICAgICAgcmV0dXJuIEY7XG4gICAgLy8gbWFrZSBzdGF0aWMgdmVyc2lvbnMgZm9yIHByb3RvdHlwZSBtZXRob2RzXG4gICAgfSkob3V0KSA6IElTX1BST1RPICYmIHR5cGVvZiBvdXQgPT0gJ2Z1bmN0aW9uJyA/IGN0eChGdW5jdGlvbi5jYWxsLCBvdXQpIDogb3V0O1xuICAgIC8vIGV4cG9ydCBwcm90byBtZXRob2RzIHRvIGNvcmUuJUNPTlNUUlVDVE9SJS5tZXRob2RzLiVOQU1FJVxuICAgIGlmKElTX1BST1RPKXtcbiAgICAgIChleHBvcnRzLnZpcnR1YWwgfHwgKGV4cG9ydHMudmlydHVhbCA9IHt9KSlba2V5XSA9IG91dDtcbiAgICAgIC8vIGV4cG9ydCBwcm90byBtZXRob2RzIHRvIGNvcmUuJUNPTlNUUlVDVE9SJS5wcm90b3R5cGUuJU5BTUUlXG4gICAgICBpZih0eXBlICYgJGV4cG9ydC5SICYmIGV4cFByb3RvICYmICFleHBQcm90b1trZXldKWhpZGUoZXhwUHJvdG8sIGtleSwgb3V0KTtcbiAgICB9XG4gIH1cbn07XG4vLyB0eXBlIGJpdG1hcFxuJGV4cG9ydC5GID0gMTsgICAvLyBmb3JjZWRcbiRleHBvcnQuRyA9IDI7ICAgLy8gZ2xvYmFsXG4kZXhwb3J0LlMgPSA0OyAgIC8vIHN0YXRpY1xuJGV4cG9ydC5QID0gODsgICAvLyBwcm90b1xuJGV4cG9ydC5CID0gMTY7ICAvLyBiaW5kXG4kZXhwb3J0LlcgPSAzMjsgIC8vIHdyYXBcbiRleHBvcnQuVSA9IDY0OyAgLy8gc2FmZVxuJGV4cG9ydC5SID0gMTI4OyAvLyByZWFsIHByb3RvIG1ldGhvZCBmb3IgYGxpYnJhcnlgIFxubW9kdWxlLmV4cG9ydHMgPSAkZXhwb3J0O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZXhwb3J0LmpzXG4gKiogbW9kdWxlIGlkID0gOFxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwiLy8gaHR0cHM6Ly9naXRodWIuY29tL3psb2lyb2NrL2NvcmUtanMvaXNzdWVzLzg2I2lzc3VlY29tbWVudC0xMTU3NTkwMjhcbnZhciBnbG9iYWwgPSBtb2R1bGUuZXhwb3J0cyA9IHR5cGVvZiB3aW5kb3cgIT0gJ3VuZGVmaW5lZCcgJiYgd2luZG93Lk1hdGggPT0gTWF0aFxuICA/IHdpbmRvdyA6IHR5cGVvZiBzZWxmICE9ICd1bmRlZmluZWQnICYmIHNlbGYuTWF0aCA9PSBNYXRoID8gc2VsZiA6IEZ1bmN0aW9uKCdyZXR1cm4gdGhpcycpKCk7XG5pZih0eXBlb2YgX19nID09ICdudW1iZXInKV9fZyA9IGdsb2JhbDsgLy8gZXNsaW50LWRpc2FibGUtbGluZSBuby11bmRlZlxuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZ2xvYmFsLmpzXG4gKiogbW9kdWxlIGlkID0gOVxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwidmFyIGNvcmUgPSBtb2R1bGUuZXhwb3J0cyA9IHt2ZXJzaW9uOiAnMi4yLjAnfTtcbmlmKHR5cGVvZiBfX2UgPT0gJ251bWJlcicpX19lID0gY29yZTsgLy8gZXNsaW50LWRpc2FibGUtbGluZSBuby11bmRlZlxuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fY29yZS5qc1xuICoqIG1vZHVsZSBpZCA9IDEwXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCIvLyBvcHRpb25hbCAvIHNpbXBsZSBjb250ZXh0IGJpbmRpbmdcbnZhciBhRnVuY3Rpb24gPSByZXF1aXJlKCcuL19hLWZ1bmN0aW9uJyk7XG5tb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGZuLCB0aGF0LCBsZW5ndGgpe1xuICBhRnVuY3Rpb24oZm4pO1xuICBpZih0aGF0ID09PSB1bmRlZmluZWQpcmV0dXJuIGZuO1xuICBzd2l0Y2gobGVuZ3RoKXtcbiAgICBjYXNlIDE6IHJldHVybiBmdW5jdGlvbihhKXtcbiAgICAgIHJldHVybiBmbi5jYWxsKHRoYXQsIGEpO1xuICAgIH07XG4gICAgY2FzZSAyOiByZXR1cm4gZnVuY3Rpb24oYSwgYil7XG4gICAgICByZXR1cm4gZm4uY2FsbCh0aGF0LCBhLCBiKTtcbiAgICB9O1xuICAgIGNhc2UgMzogcmV0dXJuIGZ1bmN0aW9uKGEsIGIsIGMpe1xuICAgICAgcmV0dXJuIGZuLmNhbGwodGhhdCwgYSwgYiwgYyk7XG4gICAgfTtcbiAgfVxuICByZXR1cm4gZnVuY3Rpb24oLyogLi4uYXJncyAqLyl7XG4gICAgcmV0dXJuIGZuLmFwcGx5KHRoYXQsIGFyZ3VtZW50cyk7XG4gIH07XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fY3R4LmpzXG4gKiogbW9kdWxlIGlkID0gMTFcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsIm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oaXQpe1xuICBpZih0eXBlb2YgaXQgIT0gJ2Z1bmN0aW9uJyl0aHJvdyBUeXBlRXJyb3IoaXQgKyAnIGlzIG5vdCBhIGZ1bmN0aW9uIScpO1xuICByZXR1cm4gaXQ7XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fYS1mdW5jdGlvbi5qc1xuICoqIG1vZHVsZSBpZCA9IDEyXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJ2YXIgZFAgICAgICAgICA9IHJlcXVpcmUoJy4vX29iamVjdC1kcCcpXG4gICwgY3JlYXRlRGVzYyA9IHJlcXVpcmUoJy4vX3Byb3BlcnR5LWRlc2MnKTtcbm1vZHVsZS5leHBvcnRzID0gcmVxdWlyZSgnLi9fZGVzY3JpcHRvcnMnKSA/IGZ1bmN0aW9uKG9iamVjdCwga2V5LCB2YWx1ZSl7XG4gIHJldHVybiBkUC5mKG9iamVjdCwga2V5LCBjcmVhdGVEZXNjKDEsIHZhbHVlKSk7XG59IDogZnVuY3Rpb24ob2JqZWN0LCBrZXksIHZhbHVlKXtcbiAgb2JqZWN0W2tleV0gPSB2YWx1ZTtcbiAgcmV0dXJuIG9iamVjdDtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19oaWRlLmpzXG4gKiogbW9kdWxlIGlkID0gMTNcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsInZhciBhbk9iamVjdCAgICAgICA9IHJlcXVpcmUoJy4vX2FuLW9iamVjdCcpXG4gICwgSUU4X0RPTV9ERUZJTkUgPSByZXF1aXJlKCcuL19pZTgtZG9tLWRlZmluZScpXG4gICwgdG9QcmltaXRpdmUgICAgPSByZXF1aXJlKCcuL190by1wcmltaXRpdmUnKVxuICAsIGRQICAgICAgICAgICAgID0gT2JqZWN0LmRlZmluZVByb3BlcnR5O1xuXG5leHBvcnRzLmYgPSByZXF1aXJlKCcuL19kZXNjcmlwdG9ycycpID8gT2JqZWN0LmRlZmluZVByb3BlcnR5IDogZnVuY3Rpb24gZGVmaW5lUHJvcGVydHkoTywgUCwgQXR0cmlidXRlcyl7XG4gIGFuT2JqZWN0KE8pO1xuICBQID0gdG9QcmltaXRpdmUoUCwgdHJ1ZSk7XG4gIGFuT2JqZWN0KEF0dHJpYnV0ZXMpO1xuICBpZihJRThfRE9NX0RFRklORSl0cnkge1xuICAgIHJldHVybiBkUChPLCBQLCBBdHRyaWJ1dGVzKTtcbiAgfSBjYXRjaChlKXsgLyogZW1wdHkgKi8gfVxuICBpZignZ2V0JyBpbiBBdHRyaWJ1dGVzIHx8ICdzZXQnIGluIEF0dHJpYnV0ZXMpdGhyb3cgVHlwZUVycm9yKCdBY2Nlc3NvcnMgbm90IHN1cHBvcnRlZCEnKTtcbiAgaWYoJ3ZhbHVlJyBpbiBBdHRyaWJ1dGVzKU9bUF0gPSBBdHRyaWJ1dGVzLnZhbHVlO1xuICByZXR1cm4gTztcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19vYmplY3QtZHAuanNcbiAqKiBtb2R1bGUgaWQgPSAxNFxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwidmFyIGlzT2JqZWN0ID0gcmVxdWlyZSgnLi9faXMtb2JqZWN0Jyk7XG5tb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGl0KXtcbiAgaWYoIWlzT2JqZWN0KGl0KSl0aHJvdyBUeXBlRXJyb3IoaXQgKyAnIGlzIG5vdCBhbiBvYmplY3QhJyk7XG4gIHJldHVybiBpdDtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19hbi1vYmplY3QuanNcbiAqKiBtb2R1bGUgaWQgPSAxNVxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwibW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihpdCl7XG4gIHJldHVybiB0eXBlb2YgaXQgPT09ICdvYmplY3QnID8gaXQgIT09IG51bGwgOiB0eXBlb2YgaXQgPT09ICdmdW5jdGlvbic7XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9faXMtb2JqZWN0LmpzXG4gKiogbW9kdWxlIGlkID0gMTZcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsIm1vZHVsZS5leHBvcnRzID0gIXJlcXVpcmUoJy4vX2Rlc2NyaXB0b3JzJykgJiYgIXJlcXVpcmUoJy4vX2ZhaWxzJykoZnVuY3Rpb24oKXtcclxuICByZXR1cm4gT2JqZWN0LmRlZmluZVByb3BlcnR5KHJlcXVpcmUoJy4vX2RvbS1jcmVhdGUnKSgnZGl2JyksICdhJywge2dldDogZnVuY3Rpb24oKXsgcmV0dXJuIDc7IH19KS5hICE9IDc7XHJcbn0pO1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9faWU4LWRvbS1kZWZpbmUuanNcbiAqKiBtb2R1bGUgaWQgPSAxN1xuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwiLy8gVGhhbmsncyBJRTggZm9yIGhpcyBmdW5ueSBkZWZpbmVQcm9wZXJ0eVxubW9kdWxlLmV4cG9ydHMgPSAhcmVxdWlyZSgnLi9fZmFpbHMnKShmdW5jdGlvbigpe1xuICByZXR1cm4gT2JqZWN0LmRlZmluZVByb3BlcnR5KHt9LCAnYScsIHtnZXQ6IGZ1bmN0aW9uKCl7IHJldHVybiA3OyB9fSkuYSAhPSA3O1xufSk7XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19kZXNjcmlwdG9ycy5qc1xuICoqIG1vZHVsZSBpZCA9IDE4XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJtb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGV4ZWMpe1xuICB0cnkge1xuICAgIHJldHVybiAhIWV4ZWMoKTtcbiAgfSBjYXRjaChlKXtcbiAgICByZXR1cm4gdHJ1ZTtcbiAgfVxufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2ZhaWxzLmpzXG4gKiogbW9kdWxlIGlkID0gMTlcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsInZhciBpc09iamVjdCA9IHJlcXVpcmUoJy4vX2lzLW9iamVjdCcpXG4gICwgZG9jdW1lbnQgPSByZXF1aXJlKCcuL19nbG9iYWwnKS5kb2N1bWVudFxuICAvLyBpbiBvbGQgSUUgdHlwZW9mIGRvY3VtZW50LmNyZWF0ZUVsZW1lbnQgaXMgJ29iamVjdCdcbiAgLCBpcyA9IGlzT2JqZWN0KGRvY3VtZW50KSAmJiBpc09iamVjdChkb2N1bWVudC5jcmVhdGVFbGVtZW50KTtcbm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oaXQpe1xuICByZXR1cm4gaXMgPyBkb2N1bWVudC5jcmVhdGVFbGVtZW50KGl0KSA6IHt9O1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2RvbS1jcmVhdGUuanNcbiAqKiBtb2R1bGUgaWQgPSAyMFxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwiLy8gNy4xLjEgVG9QcmltaXRpdmUoaW5wdXQgWywgUHJlZmVycmVkVHlwZV0pXG52YXIgaXNPYmplY3QgPSByZXF1aXJlKCcuL19pcy1vYmplY3QnKTtcbi8vIGluc3RlYWQgb2YgdGhlIEVTNiBzcGVjIHZlcnNpb24sIHdlIGRpZG4ndCBpbXBsZW1lbnQgQEB0b1ByaW1pdGl2ZSBjYXNlXG4vLyBhbmQgdGhlIHNlY29uZCBhcmd1bWVudCAtIGZsYWcgLSBwcmVmZXJyZWQgdHlwZSBpcyBhIHN0cmluZ1xubW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihpdCwgUyl7XG4gIGlmKCFpc09iamVjdChpdCkpcmV0dXJuIGl0O1xuICB2YXIgZm4sIHZhbDtcbiAgaWYoUyAmJiB0eXBlb2YgKGZuID0gaXQudG9TdHJpbmcpID09ICdmdW5jdGlvbicgJiYgIWlzT2JqZWN0KHZhbCA9IGZuLmNhbGwoaXQpKSlyZXR1cm4gdmFsO1xuICBpZih0eXBlb2YgKGZuID0gaXQudmFsdWVPZikgPT0gJ2Z1bmN0aW9uJyAmJiAhaXNPYmplY3QodmFsID0gZm4uY2FsbChpdCkpKXJldHVybiB2YWw7XG4gIGlmKCFTICYmIHR5cGVvZiAoZm4gPSBpdC50b1N0cmluZykgPT0gJ2Z1bmN0aW9uJyAmJiAhaXNPYmplY3QodmFsID0gZm4uY2FsbChpdCkpKXJldHVybiB2YWw7XG4gIHRocm93IFR5cGVFcnJvcihcIkNhbid0IGNvbnZlcnQgb2JqZWN0IHRvIHByaW1pdGl2ZSB2YWx1ZVwiKTtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL190by1wcmltaXRpdmUuanNcbiAqKiBtb2R1bGUgaWQgPSAyMVxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwibW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihiaXRtYXAsIHZhbHVlKXtcbiAgcmV0dXJuIHtcbiAgICBlbnVtZXJhYmxlICA6ICEoYml0bWFwICYgMSksXG4gICAgY29uZmlndXJhYmxlOiAhKGJpdG1hcCAmIDIpLFxuICAgIHdyaXRhYmxlICAgIDogIShiaXRtYXAgJiA0KSxcbiAgICB2YWx1ZSAgICAgICA6IHZhbHVlXG4gIH07XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fcHJvcGVydHktZGVzYy5qc1xuICoqIG1vZHVsZSBpZCA9IDIyXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCIndXNlIHN0cmljdCc7XG5cbi8qKlxuICogRXJyb3JzXG4gKiBAdHlwZWRlZiB7T2JqZWN0fSBMaXN0RXJyb3JzXG4gKiBAcHJvcGVydHkge1N0cmluZ30gbmFtZSAtIG5hbWUgb2YgZmllbGRcbiAqIEBwcm9wZXJ0eSB7U3RyaW5nfSBlcnJvciAtIGVycm9yIGRlc2NyaXB0aW9uXG4gKi9cblxuZXhwb3J0IGRlZmF1bHQgY2xhc3MgRm9ybUhlbHBlciB7XG4gICAgLyoqXG4gICAgICogVmFsaWRhdGUgZm9ybSB0aHJvdWdoIGlucHV0c1xuICAgICAqIEBwYXJhbSB7alF1ZXJ5fSAkY29udHJvbHNcbiAgICAgKi9cbiAgICBjb25zdHJ1Y3RvcigkY29udHJvbHMpIHtcbiAgICAgICAgdGhpcy4kY29udHJvbHMgPSAkY29udHJvbHM7XG4gICAgICAgIHRoaXMuYXJyRXJyb3JzID0gW107XG4gICAgICAgIHRoaXMuX2Fzc2lnbkV2ZW50cygpO1xuICAgIH1cblxuICAgIF9hc3NpZ25FdmVudHMoKSB7XG4gICAgICAgIHRoaXMuJGNvbnRyb2xzLm9uKCdpbnB1dCBjaGFuZ2UnLCAoZSkgPT4ge1xuICAgICAgICAgICAgY29uc3QgJGNvbnRyb2wgPSAkKGUuY3VycmVudFRhcmdldCk7XG5cbiAgICAgICAgICAgIHRoaXMuX3ZhbGlkYXRlSW1tZWRpYXRlKCRjb250cm9sKTtcbiAgICAgICAgICAgIHRoaXMuX3JlbW92ZUVycm9yKCRjb250cm9sKTtcbiAgICAgICAgfSk7XG4gICAgfVxuXG4gICAgX3ZhbGlkYXRlSW1tZWRpYXRlKCRjb250cm9sKXtcbiAgICAgICAgaWYgKCRjb250cm9sLmhhc0NsYXNzKCd0eXBlLW51bWVyaWMnKSkge1xuICAgICAgICAgICAgJGNvbnRyb2wudmFsKCRjb250cm9sLnZhbCgpLnJlcGxhY2UoL1teXFxkXSsvZywgJycpKTtcbiAgICAgICAgfVxuXG4gICAgICAgIGlmICgkY29udHJvbC5oYXNDbGFzcygndHlwZS1ub3NwYWNlJykpIHtcbiAgICAgICAgICAgICRjb250cm9sLnZhbCgkY29udHJvbC52YWwoKS5yZXBsYWNlKC9cXHMvZywgJycpKTtcbiAgICAgICAgfVxuICAgIH1cblxuICAgIGlzVmFsaWRJbnB1dHMoKSB7XG4gICAgICAgIGNvbnN0ICRjb250cm9scyA9IHRoaXMuJGNvbnRyb2xzO1xuICAgICAgICBsZXQgZXJyb3IgPSAwO1xuXG4gICAgICAgICRjb250cm9scy5lYWNoKChpbmRleCwgY29udHJvbCkgPT4ge1xuICAgICAgICAgICAgY29uc3QgJGNvbnRyb2wgPSAkKGNvbnRyb2wpO1xuXG4gICAgICAgICAgICBpZiAoIXRoaXMuX2lzVmFsaWRJbnB1dCgkY29udHJvbCkpIHtcbiAgICAgICAgICAgICAgICBlcnJvciArPSAxO1xuICAgICAgICAgICAgfVxuICAgICAgICB9KTtcbiAgICAgICAgcmV0dXJuIEJvb2xlYW4oIWVycm9yKTtcbiAgICB9XG5cbiAgICAvKipcbiAgICAgKiBDaGVjayBnaXZlbiBjb250cm9sLCBpcyBpdCB2YWxpZD9cbiAgICAgKiBAcGFyYW0ge2pRdWVyeX0gJGNvbnRyb2xcbiAgICAgKiBAcmV0dXJucyB7Ym9vbGVhbn0gLSBJcyB2YWxpZCBjb250cm9sP1xuICAgICAqL1xuICAgIF9pc1ZhbGlkSW5wdXQoJGNvbnRyb2wpIHtcbiAgICAgICAgY29uc3QgdmFsdWUgPSAkLnRyaW0oJGNvbnRyb2wudmFsKCkpO1xuXG4gICAgICAgIGlmICghdmFsdWUgJiYgISRjb250cm9sLmhhc0NsYXNzKCd0eXBlLW9wdGlvbmFsJykpIHtcbiAgICAgICAgICAgIHRoaXMuX3NldEVycm9yKCRjb250cm9sLCAnRW1wdHknKTtcbiAgICAgICAgICAgIHJldHVybiBmYWxzZTtcbiAgICAgICAgfVxuXG4gICAgICAgIGlmICgoJGNvbnRyb2wuaGFzQ2xhc3MoJ3R5cGUtZW1haWwnKSkgJiYgIXRoaXMuX2lzVmFsaWRFbWFpbCh2YWx1ZSkpIHtcbiAgICAgICAgICAgIHRoaXMuX3NldEVycm9yKCRjb250cm9sLCAnRW1haWwgaXMgbm90IHZhbGlkJyk7XG4gICAgICAgICAgICByZXR1cm4gZmFsc2U7XG4gICAgICAgIH1cblxuICAgICAgICByZXR1cm4gdHJ1ZTtcbiAgICB9XG5cbiAgICAvKipcbiAgICAgKiBJcyBFbWFpbCB2YWxpZD9cbiAgICAgKiBAcGFyYW0ge3N0cmluZ30gZW1haWxcbiAgICAgKiBAcmV0dXJucyB7Ym9vbGVhbn1cbiAgICAgKi9cbiAgICBfaXNWYWxpZEVtYWlsKGVtYWlsKSB7XG4gICAgICAgIHZhciByZSA9IC9eKChbXjw+KClcXFtcXF1cXFxcLiw7Olxcc0BcIl0rKFxcLltePD4oKVxcW1xcXVxcXFwuLDs6XFxzQFwiXSspKil8KFwiLitcIikpQCgoXFxbWzAtOV17MSwzfVxcLlswLTldezEsM31cXC5bMC05XXsxLDN9XFwuWzAtOV17MSwzfV0pfCgoW2EtekEtWlxcLTAtOV0rXFwuKStbYS16QS1aXXsyLH0pKSQvO1xuICAgICAgICByZXR1cm4gcmUudGVzdChlbWFpbCk7XG4gICAgfVxuXG4gICAgLyoqXG4gICAgICogU2V0IGVycm9yIGZvciBjb250cm9sXG4gICAgICogQHBhcmFtIHtqUXVlcnl9ICRjb250cm9sXG4gICAgICogQHBhcmFtIHtTdHJpbmd9IGVycm9yVGV4dFxuICAgICAqIEBwYXJhbSB7Qm9vbGVhbn0gaW5zZXJ0RXJyb3JcbiAgICAgKi9cbiAgICBfc2V0RXJyb3IoJGNvbnRyb2wsIGVycm9yVGV4dCwgaW5zZXJ0RXJyb3IgPSB0cnVlKSB7XG4gICAgICAgIGNvbnN0ICRwYXJlbnQgPSAkY29udHJvbC5wYXJlbnQoKTtcbiAgICAgICAgY29uc3QgJGVycm9yID0gJHBhcmVudC5maW5kKCcuYi1lcnJvcicpO1xuXG4gICAgICAgIGlmICgkZXJyb3IubGVuZ3RoKSByZXR1cm47XG5cbiAgICAgICAgJHBhcmVudC5hZGRDbGFzcygnYi1lcnJvcl9zaG93Jyk7XG4gICAgICAgIFxuICAgICAgICBpbnNlcnRFcnJvciAmJiAkKCc8ZGl2IGNsYXNzPVwiYi1lcnJvclwiIC8+JylcbiAgICAgICAgICAgIC50ZXh0KGVycm9yVGV4dClcbiAgICAgICAgICAgIC5hcHBlbmRUbygkcGFyZW50KTtcblxuICAgICAgICB0aGlzLmFyckVycm9ycy5wdXNoKHtcbiAgICAgICAgICAgIG5hbWU6ICRjb250cm9sLmF0dHIoJ25hbWUnKSxcbiAgICAgICAgICAgIGVycm9yOiBlcnJvclRleHRcbiAgICAgICAgfSlcbiAgICB9XG5cbiAgICAvKipcbiAgICAgKiBSZW1vdmUgZXJyb3IgZm9yIGNvbnRyb2xcbiAgICAgKiBAcGFyYW0ge2pRdWVyeX0gJGNvbnRyb2xcbiAgICAgKi9cbiAgICBfcmVtb3ZlRXJyb3IoJGNvbnRyb2wpIHtcbiAgICAgICAgY29uc3QgJHBhcmVudCA9ICRjb250cm9sLnBhcmVudCgpO1xuXG4gICAgICAgICRwYXJlbnRcbiAgICAgICAgICAgIC5yZW1vdmVDbGFzcygnYi1lcnJvcl9zaG93JylcbiAgICAgICAgICAgIC5maW5kKCcuYi1lcnJvcicpLnJlbW92ZSgpO1xuXG4gICAgICAgIHRoaXMuYXJyRXJyb3JzID0gdGhpcy5hcnJFcnJvcnMuZmlsdGVyKGZ1bmN0aW9uIChpdGVtKSB7XG4gICAgICAgICAgICByZXR1cm4gaXRlbS5uYW1lICE9PSAkY29udHJvbC5hdHRyKCduYW1lJylcbiAgICAgICAgfSlcbiAgICB9XG5cbiAgICAvKipcbiAgICAgKiBTZXQgZXJyb3JzXG4gICAgICogQHBhcmFtIHtBcnJheX0gZXJyb3JzIC0gW3tuYW1lOiBcImVtYWlsXCIsIGVycm9yOiBcImVtcHR5XCJ9LCB7bmFtZTogXCJwYXNzd29yZFwiLCBlcnJvcjogXCJlbXB0eVwifV1cbiAgICAgKiBAcGFyYW0ge0Jvb2xlYW59IGluc2VydEVycm9yIC0gaW5zZXJ0IGVycm9yIGRlc2NyaXB0aW9uIHRvIHRoZSBEb20gXG4gICAgICovXG4gICAgc2V0RXJyb3JzKGVycm9ycywgaW5zZXJ0RXJyb3IgPSB0cnVlKSB7XG4gICAgICAgIGVycm9ycy5mb3JFYWNoKChpdGVtKSA9PiB7XG4gICAgICAgICAgICBjb25zdCAkY3VycmVudENvbnRyb2wgPSB0aGlzLiRjb250cm9scy5maWx0ZXIoJ1tuYW1lPVwiJyArIGl0ZW0ubmFtZSArICdcIl0nKS5maXJzdCgpO1xuXG4gICAgICAgICAgICBpZiAoJGN1cnJlbnRDb250cm9sLmxlbmd0aCkgdGhpcy5fc2V0RXJyb3IoJGN1cnJlbnRDb250cm9sLCBpdGVtLmVycm9yLCBpbnNlcnRFcnJvcilcbiAgICAgICAgfSlcbiAgICB9XG5cbiAgICAvKipcbiAgICAgKiBHZXQgdGV4dCB2ZXJzaW9uIG9mIGVycm9ycyBpbiBvbmUgbGluZS5cbiAgICAgKiBAcGFyYW0ge0xpc3RFcnJvcnN9IGVycm9yc1xuICAgICAqIEByZXR1cm5zIHtzdHJpbmd9XG4gICAgICovXG4gICAgZ2V0RXJyb3JzVGV4dChlcnJvcnMpIHtcbiAgICAgICAgY29uc3QgYXJyRXJyb3JzID0gZXJyb3JzIHx8IHRoaXMuYXJyRXJyb3JzO1xuICAgICAgICBsZXQgZXJyb3JUeHQgPSAnJztcblxuICAgICAgICBhcnJFcnJvcnMuZm9yRWFjaCgoaXRlbSkgPT4ge1xuICAgICAgICAgICAgY29uc3QgbmFtZSA9IGl0ZW0ubmFtZVswXS50b1VwcGVyQ2FzZSgpICsgaXRlbS5uYW1lLnN1YnN0cigxKTtcblxuICAgICAgICAgICAgZXJyb3JUeHQgKz0gYCR7bmFtZX06ICR7aXRlbS5lcnJvcn0uIGA7XG4gICAgICAgIH0pO1xuXG4gICAgICAgIHJldHVybiBlcnJvclR4dDtcbiAgICB9XG5cbiAgICAvKipcbiAgICAgKiBHZXQgbGlzdCBvZiBlcnJvcnMgd2l0aCBmdWxsIHRpdGxlIChmcm9tIGNvbnRyb2wgdGl0bGUgYXR0cmlidXRlKVxuICAgICAqIEBwYXJhbSB7TGlzdEVycm9yc30gZXJyb3JzIC0gbGlzdCBvZiBlcnJvcnNcbiAgICAgKiBAcmV0dXJucyB7c3RyaW5nfVxuICAgICAqL1xuICAgIGdldEVycm9yc0Z1bGwoZXJyb3JzKSB7XG4gICAgICAgIGNvbnN0IHNlbGYgPSB0aGlzO1xuICAgICAgICBjb25zdCBhcnJFcnJvcnMgPSBlcnJvcnMgfHwgdGhpcy5hcnJFcnJvcnM7XG4gICAgICAgIGxldCBlcnJvclR4dCA9ICcnO1xuXG4gICAgICAgIGFyckVycm9ycy5mb3JFYWNoKChpdGVtKSA9PiB7XG4gICAgICAgICAgICBjb25zdCAkY29udHJvbCA9IHNlbGYuJGNvbnRyb2xzLmZpbHRlcihgW25hbWU9XCIke2l0ZW0ubmFtZX1cIl1gKS5maXJzdCgpO1xuICAgICAgICAgICAgY29uc3QgbmFtZSA9ICRjb250cm9sLmxlbmd0aD8gJGNvbnRyb2wuYXR0cigndGl0bGUnKTogaXRlbS5uYW1lO1xuXG4gICAgICAgICAgICBlcnJvclR4dCArPSBgPGI+JHtuYW1lfTwvYj46ICR7aXRlbS5lcnJvcn0uICA8YnI+PGJyPmA7XG4gICAgICAgIH0pO1xuXG4gICAgICAgIHJldHVybiBlcnJvclR4dDtcbiAgICB9XG5cbiAgICBnZXRGb3JtRGF0YSgpe1xuICAgICAgICBsZXQgYWpheERhdGEgPSB7fTtcblxuICAgICAgICB0aGlzLiRjb250cm9scy5tYXAoKGluZGV4LCBlbCkgPT4ge1xuICAgICAgICAgICAgY29uc3QgJGVsID0gJChlbCk7XG4gICAgICAgICAgICBjb25zdCBuYW1lID0gJGVsLmF0dHIoJ25hbWUnKTtcblxuICAgICAgICAgICAgaWYgKCFuYW1lKSByZXR1cm47XG5cbiAgICAgICAgICAgIGlmICgkZWwuaXMoJzpjaGVja2JveCcpKXtcbiAgICAgICAgICAgICAgICBhamF4RGF0YVtuYW1lXSA9ICRlbC5wcm9wKCdjaGVja2VkJylcbiAgICAgICAgICAgIH0gZWxzZSB7XG4gICAgICAgICAgICAgICAgYWpheERhdGFbbmFtZV0gPSAkZWwudmFsKClcbiAgICAgICAgICAgIH1cbiAgICAgICAgfSk7XG5cbiAgICAgICAgcmV0dXJuIGFqYXhEYXRhO1xuICAgIH1cblxuICAgIC8qKlxuICAgICAqIFJlbW92ZSBhbGwgZXJyb3JzXG4gICAgICovXG4gICAgcmVtb3ZlRXJyb3JzKCkge1xuICAgICAgICB0aGlzLiRjb250cm9scy5lYWNoKChpbmRleCwgZWwpID0+IHtcbiAgICAgICAgICAgIGNvbnN0ICRlbCA9ICQoZWwpO1xuICAgICAgICAgICAgdGhpcy5fcmVtb3ZlRXJyb3IoJGVsKVxuICAgICAgICB9KVxuICAgIH1cblxuICAgIGNsZWFyRm9ybSgpIHtcbiAgICAgICAgdGhpcy4kY29udHJvbHMuZWFjaCgoaW5kZXgsIGVsKSA9PiB7XG4gICAgICAgICAgICBjb25zdCAkZWwgPSAkKGVsKTtcbiAgICAgICAgICAgIGlmICghJGVsLmF0dHIoXCJkaXNhYmxlZFwiKSkgICRlbC52YWwoJycpO1xuICAgICAgICB9KVxuICAgIH1cbn1cblxuXG5cbi8qKiBXRUJQQUNLIEZPT1RFUiAqKlxuICoqIC4vZnJvbnRlbmQvanMvY29tbW9uL19mb3JtLWhlbHBlci5qc1xuICoqLyIsIid1c2Ugc3RyaWN0JztcblxuXG5leHBvcnQgZGVmYXVsdCBjbGFzcyBXaWRnZXQge1xuICAgIGNvbnN0cnVjdG9yKHNlbGVjdG9yKSB7XG4gICAgICAgIHRoaXMuJHJvb3QgPSAkKHNlbGVjdG9yKTtcbiAgICAgICAgdGhpcy5sb2NhbHMgPSB0aGlzLl9nZXREb20oKTtcbiAgICAgICAgdGhpcy4kY29uZmlybURpYWxvZyA9IG51bGw7XG5cbiAgICAgICAgdGhpcy5fYXNzaWduRXZlbnRzKCk7XG4gICAgfVxuXG4gICAgX2dldERvbSgpIHtcbiAgICAgICAgcmV0dXJuIHtcbiAgICAgICAgICAgICRjb25maXJtOiB0aGlzLiRyb290LmZpbmQoJ1tkYXRhLWV2ZW50LWNvbmZpcm1dJyksXG4gICAgICAgICAgICAkY2FuY2VsOiB0aGlzLiRyb290LmZpbmQoJ1tkYXRhLWV2ZW50LWNhbmNlbF0nKVxuICAgICAgICB9O1xuICAgIH1cblxuICAgIF9hc3NpZ25FdmVudHMoKSB7XG4gICAgICAgIHRoaXMuJHJvb3RcbiAgICAgICAgICAgIC5vbignY2xpY2snLCAnW2RhdGEtZXZlbnQtY29uZmlybV0nLCB0aGlzLl9vbkNsaWNrQ29uZmlybS5iaW5kKHRoaXMpKVxuICAgICAgICAgICAgLm9uKCdjbGljaycsICdbZGF0YS1ldmVudC1jYW5jZWxdJywgdGhpcy5fb25DbGlja0NhbmNlbC5iaW5kKHRoaXMpKVxuICAgICAgICAgICAgLm9uKCdjbGljaycsICcjZXZlbnRDYW5jZWxCdXR0b24nLCB0aGlzLl9vbkNsaWNrQWNjZXB0Q2FuY2VsLmJpbmQodGhpcykpO1xuICAgIH1cblxuICAgIF9vbkNsaWNrQ29uZmlybShlKSB7XG4gICAgICAgIGUucHJldmVudERlZmF1bHQoKTtcbiAgICAgICAgY29uc3Qgc2VsZiA9IHRoaXM7XG4gICAgICAgIGNvbnN0IGV2ZW50SWQgPSBzZWxmLmxvY2Fscy4kY29uZmlybS5kYXRhKCdpZCcpO1xuXG4gICAgICAgIHNlbGYuX3NlbmRDb25maXJtKGV2ZW50SWQpXG4gICAgICAgICAgICAuZG9uZSgoKT0+e1xuICAgICAgICAgICAgICAgIHNlbGYubG9jYWxzLiRjb25maXJtLmFkZENsYXNzKCdkaXNhYmxlZCcpLnRleHQoJ0NvbmZpcm1lZCcpLm9mZignY2xpY2snKTtcbiAgICAgICAgICAgICAgICBzdWNjZXNzKFwiRXZlbnQgd2FzIHN1Y2Nlc3NmdWxseSBjb25maXJtZWRcIik7XG4gICAgICAgICAgICB9KVxuICAgIH1cblxuICAgIF9vbkNsaWNrQ2FuY2VsKGUpIHtcbiAgICAgICAgZS5wcmV2ZW50RGVmYXVsdCgpO1xuICAgICAgICBjb25zdCBzZWxmID0gdGhpcztcbiAgICAgICAgY29uc3QgZXZlbnRJZCA9IHNlbGYubG9jYWxzLiRjYW5jZWwuZGF0YSgnaWQnKTtcblxuICAgICAgICBzZWxmLl9zZW5kQ2FuY2VsKGV2ZW50SWQpXG4gICAgICAgICAgICAuZG9uZSgocmVzcG9uc2UpPT57XG4gICAgICAgICAgICAgICAgc2VsZi4kY29uZmlybURpYWxvZyA9IHNlbGYuX2NyZWF0ZURpYWxvZyhyZXNwb25zZSk7XG4gICAgICAgICAgICAgICAgc2VsZi4kY29uZmlybURpYWxvZy5tb2RhbCgnc2hvdycpO1xuICAgICAgICAgICAgfSlcbiAgICB9XG5cbiAgICBfb25DbGlja0FjY2VwdENhbmNlbChlKXtcbiAgICAgICAgZS5wcmV2ZW50RGVmYXVsdCgpO1xuXG4gICAgICAgIGNvbnN0IHNlbGYgPSB0aGlzO1xuICAgICAgICBjb25zdCBmb3JtRGF0YSA9ICQoXCIjY2FuY2VsRm9ybVwiKS5zZXJpYWxpemUoKTtcbiAgICAgICAgY29uc3QgZXZlbnRJZCA9IHNlbGYubG9jYWxzLiRjYW5jZWwuZGF0YSgnaWQnKTtcblxuICAgICAgICBzZWxmLl9zZW5kQWNjZXB0Q2FuY2VsKGZvcm1EYXRhLCBldmVudElkKVxuICAgICAgICAgICAgLmRvbmUoKCk9PntcbiAgICAgICAgICAgICAgICBzZWxmLiRjb25maXJtRGlhbG9nXG4gICAgICAgICAgICAgICAgICAgIC5vbignaGlkZGVuLmJzLm1vZGFsJywgKCk9PntcbiAgICAgICAgICAgICAgICAgICAgICAgIEFwcC5ldmVudHMucHViKCdobXQuZXZlbnQuY2FuY2VsJyk7XG4gICAgICAgICAgICAgICAgICAgIH0pXG4gICAgICAgICAgICAgICAgICAgIC5tb2RhbCgnaGlkZScpO1xuXG4gICAgICAgICAgICAgICAgc3VjY2VzcyhcIkV2ZW50IHdhcyBzdWNjZXNzZnVsbHkgY2FuY2VsZWRcIik7XG4gICAgICAgICAgICB9KVxuICAgIH1cblxuICAgIF9jcmVhdGVEaWFsb2coY29udGVudCl7XG4gICAgICAgIGNvbnN0IHNlbGVjdG9yID0gJyNjYW5jZWxEaWFsb2cnO1xuICAgICAgICBsZXQgJGRpYWxvZztcblxuICAgICAgICAkKHNlbGVjdG9yKS5yZW1vdmUoKTtcbiAgICAgICAgJGRpYWxvZyA9ICAkKCc8ZGl2IGlkPVwiJyArIHNlbGVjdG9yICsgJ1wiIGNsYXNzPVwiYi1tb2RhbCBtb2RhbCBmYWRlXCIgdGFiaW5kZXg9XCItMVwiPicpXG4gICAgICAgICAgICAuYXR0cigncm9sZScsICdkaWFsb2cnKVxuICAgICAgICAgICAgLmF0dHIoJ2FyaWEtaGlkZGVuJywgJ3RydWUnKVxuICAgICAgICAgICAgLmFwcGVuZChjb250ZW50KTtcblxuICAgICAgICAkZGlhbG9nLmFwcGVuZFRvKHRoaXMuJHJvb3QpO1xuICAgICAgICByZXR1cm4gJGRpYWxvZztcbiAgICB9XG5cbiAgICAvL3RyYW5zcG9ydFxuICAgIF9zZW5kQ29uZmlybShldmVudElkKXtcbiAgICAgICAgY29uc3QgdXJsID0ganNSb3V0ZXMuY29udHJvbGxlcnMuY20uRXZlbnRzLmNvbmZpcm0oZXZlbnRJZCkudXJsO1xuICAgICAgICByZXR1cm4gJC5wb3N0KHVybCwge30pO1xuICAgIH1cblxuICAgIF9zZW5kQ2FuY2VsKGV2ZW50SWQpe1xuICAgICAgICBjb25zdCB1cmwgPSBqc1JvdXRlcy5jb250cm9sbGVycy5jbS5FdmVudHMucmVhc29uKGV2ZW50SWQpLnVybDtcbiAgICAgICAgcmV0dXJuICQuZ2V0KHVybCwge30pO1xuICAgIH1cblxuICAgIF9zZW5kQWNjZXB0Q2FuY2VsKGRhdGEsIGlkKXtcbiAgICAgICAgY29uc3QgdXJsID0ganNSb3V0ZXMuY29udHJvbGxlcnMuY20uRXZlbnRzLmNhbmNlbChpZCkudXJsO1xuICAgICAgICByZXR1cm4gJC5wb3N0KHVybCwgZGF0YSk7XG4gICAgfVxuXG4gICAgLy8gc3RhdGljXG4gICAgc3RhdGljIHBsdWdpbihzZWxlY3Rvcikge1xuICAgICAgICBjb25zdCAkZWxlbXMgPSAkKHNlbGVjdG9yKTtcbiAgICAgICAgaWYgKCEkZWxlbXMubGVuZ3RoKSByZXR1cm47XG5cbiAgICAgICAgcmV0dXJuICRlbGVtcy5lYWNoKGZ1bmN0aW9uIChpbmRleCwgZWwpIHtcbiAgICAgICAgICAgIGxldCAkZWxlbWVudCA9ICQoZWwpO1xuICAgICAgICAgICAgbGV0IGRhdGEgICAgID0gJGVsZW1lbnQuZGF0YSgnaG10LmV2ZW50LmJsb2NrJyk7XG5cbiAgICAgICAgICAgIGlmICghZGF0YSkge1xuICAgICAgICAgICAgICAgIGRhdGEgPSBuZXcgV2lkZ2V0KGVsKTtcbiAgICAgICAgICAgICAgICAkZWxlbWVudC5kYXRhKCd3aWRnZXQnLCBkYXRhKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgfSlcbiAgICB9XG59XG5cblxuXG5cbi8qKiBXRUJQQUNLIEZPT1RFUiAqKlxuICoqIC4vZnJvbnRlbmQvanMvY29tbW9uL19ldmVudC1ibG9jay5qc1xuICoqLyIsIid1c2Ugc3RyaWN0JztcblxuaW1wb3J0IEV2ZW50QmxvY2sgZnJvbSBcIi4vLi4vY29tbW9uL19ldmVudC1ibG9ja1wiO1xuaW1wb3J0IENsaXBCb2FyZCBmcm9tIFwiLi8uLi9jb21tb24vX2NsaXBib2FyZFwiO1xuaW1wb3J0IFJlcXVlc3RFdmVudERsZyBmcm9tIFwiLi9kZXRhaWwtd2lkZ2V0cy9fZXZhbHVhdGlvbi1kbGdcIjtcbmltcG9ydCBNb2RpZnlFbWFpbCBmcm9tIFwiLi9kZXRhaWwtd2lkZ2V0cy9fbW9kaWZ5LWVtYWlsXCI7XG5cblxuJChmdW5jdGlvbigpe1xuICAgIEV2ZW50QmxvY2sucGx1Z2luKCcuanMtZXZlbnQtY29udHJvbHMnKTtcbiAgICBSZXF1ZXN0RXZlbnREbGcucGx1Z2luKCcuanMtcmVxdWVzdC1ldmFsdWF0aW9uJyk7XG4gICAgQ2xpcEJvYXJkLnBsdWdpbignLmpzLWNsaXBib2FyZCcpO1xuICAgIE1vZGlmeUVtYWlsLnBsdWdpbignLmpzLW1vZGlmeS1lbWFpbCcpO1xuXG4gICAgQXBwLmV2ZW50c1xuICAgICAgICAuc3ViKCdobXQuZXZlbnQuY2FuY2VsJywgZnVuY3Rpb24oKXtcbiAgICAgICAgICAgIGNvbnN0IGJyYW5kSWQgPSAkKCcjYnJhbmRJZCcpLnZhbCgpO1xuICAgICAgICAgICAgd2luZG93LmxvY2F0aW9uLnJlcGxhY2UoanNSb3V0ZXMuY29udHJvbGxlcnMuY20uRXZlbnRzLmluZGV4KGJyYW5kSWQpLnVybCk7XG4gICAgICAgIH0pXG59KTtcblxuXG4vKiogV0VCUEFDSyBGT09URVIgKipcbiAqKiAuL2Zyb250ZW5kL2pzL2V2ZW50L2V2ZW50LWRldGFpbHMtcGFnZS5qc1xuICoqLyIsIid1c2Ugc3RyaWN0JztcblxuZXhwb3J0IGRlZmF1bHQgY2xhc3MgV2lkZ2V0IHtcblxuICAgIGNvbnN0cnVjdG9yKGVsZW1lbnQpIHtcbiAgICAgICAgdGhpcy5yb290ID0gZWxlbWVudDtcbiAgICAgICAgdGhpcy4kcm9vdCA9ICQoZWxlbWVudCk7XG5cbiAgICAgICAgaWYgKCFaZXJvQ2xpcGJvYXJkKSB7XG4gICAgICAgICAgICBjb25zb2xlLmxvZygndGhlcmUgaXMgbm8gemVyb2NsaXBib2FyZCBkZXBlbmRlbmN5Jyk7XG4gICAgICAgICAgICByZXR1cm47XG4gICAgICAgIH1cblxuICAgICAgICB0aGlzLmNsaWVudCA9IG5ldyBaZXJvQ2xpcGJvYXJkKGVsZW1lbnQpO1xuICAgICAgICB0aGlzLiRjbGllbnQgPSAkKCcuZ2xvYmFsLXplcm9jbGlwYm9hcmQtY29udGFpbmVyJykuZmlyc3QoKTtcblxuICAgICAgICB0aGlzLl9hc3NpZ25FdmVudHMoKTtcbiAgICB9XG5cbiAgICBfYXNzaWduRXZlbnRzKCkge1xuICAgICAgICBjb25zdCBzZWxmID0gdGhpcztcblxuICAgICAgICB0aGlzLmNsaWVudC5vbignYWZ0ZXJjb3B5JywgdGhpcy5fb25FdmVudEFmdGVyLmJpbmQodGhpcykpO1xuICAgICAgICB0aGlzLiRjbGllbnQub24oJ21vdXNlZW50ZXInLCAoKSA9PntcbiAgICAgICAgICAgICAgICBzZWxmLiRjbGllbnRcbiAgICAgICAgICAgICAgICAgICAgLmF0dHIoJ3RpdGxlJywgJ0NvcHkgbGluaycpXG4gICAgICAgICAgICAgICAgICAgIC50b29sdGlwKCdzaG93Jyk7XG4gICAgICAgICAgICB9KVxuICAgIH1cblxuICAgIF9vbkV2ZW50QWZ0ZXIoKXtcbiAgICAgICAgY29uc3QgJHJvb3QgPSB0aGlzLiRyb290O1xuICAgICAgICB0aGlzLiRjbGllbnQudG9vbHRpcCgnaGlkZScpO1xuXG4gICAgICAgICRyb290LmF0dHIoJ3RpdGxlJywgJ0NvcGllZCcpXG4gICAgICAgICAgICAudG9vbHRpcCgnc2hvdycpO1xuXG4gICAgICAgIHNldFRpbWVvdXQoKCk9PntcbiAgICAgICAgICAgICRyb290LnRvb2x0aXAoJ2hpZGUnKVxuICAgICAgICAgICAgICAgIC5hdHRyKCd0aXRsZScsICcnKTtcbiAgICAgICAgfSwgMjUwMClcbiAgICB9XG5cbiAgICAvLyBzdGF0aWNcbiAgICBzdGF0aWMgcGx1Z2luKHNlbGVjdG9yKSB7XG4gICAgICAgIGNvbnN0ICRlbGVtcyA9ICQoc2VsZWN0b3IpO1xuICAgICAgICBpZiAoISRlbGVtcy5sZW5ndGgpIHJldHVybjtcblxuICAgICAgICByZXR1cm4gJGVsZW1zLmVhY2goZnVuY3Rpb24gKGluZGV4LCBlbCkge1xuICAgICAgICAgICAgbGV0ICRlbGVtZW50ID0gJChlbCk7XG4gICAgICAgICAgICBsZXQgZGF0YSAgICAgPSAkZWxlbWVudC5kYXRhKCd3aWRnZXQtY2xpcGJvYXJkJyk7XG5cbiAgICAgICAgICAgIGlmICghZGF0YSkge1xuICAgICAgICAgICAgICAgIGRhdGEgPSBuZXcgV2lkZ2V0KGVsKTtcbiAgICAgICAgICAgICAgICAkZWxlbWVudC5kYXRhKCd3aWRnZXQnLCBkYXRhKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgfSlcbiAgICB9XG59XG5cblxuXG5cblxuLyoqIFdFQlBBQ0sgRk9PVEVSICoqXG4gKiogLi9mcm9udGVuZC9qcy9jb21tb24vX2NsaXBib2FyZC5qc1xuICoqLyIsIid1c2Ugc3RyaWN0JztcblxuZXhwb3J0IGRlZmF1bHQgY2xhc3MgV2lkZ2V0IHtcbiAgICBjb25zdHJ1Y3RvcihzZWxlY3Rvcil7XG4gICAgICAgIHRoaXMuJHJvb3QgPSAkKHNlbGVjdG9yKTtcbiAgICAgICAgdGhpcy5sb2NhbHMgPSB0aGlzLl9nZXREb20oKTtcbiAgICAgICAgdGhpcy50ZW1wbGF0ZSA9IF8udGVtcGxhdGUoICQoJyNyZXF1ZXN0LWRsZy10ZW1wbGF0ZScpLmh0bWwoKSk7XG5cbiAgICAgICAgdGhpcy5fY2hlY2tGb3JtVmFsaWRhdGlvbigpO1xuICAgICAgICB0aGlzLl9hc3NpZ25FdmVudCgpO1xuICAgIH1cblxuICAgIF9nZXREb20oKXtcbiAgICAgICAgY29uc3QgJHJvb3QgPSB0aGlzLiRyb290O1xuXG4gICAgICAgIHJldHVybiB7XG4gICAgICAgICAgICAkbGlzdFBhcnRpY2lwYW50czogJHJvb3QuZmluZCgnW2RhdGEtcmVxdWVzdGV2YWwtbGlzdF0nKSxcbiAgICAgICAgICAgICR0ZXh0YXJlYTogJHJvb3QuZmluZCgnW2RhdGEtcmVxdWVzdGV2YWwtdGV4dGFyZWFdJyksXG4gICAgICAgICAgICAkZmlsdGVyTGlua3M6ICRyb290LmZpbmQoJ1tkYXRhLXJlcXVlc3RldmFsLWZpbHRlcl0nKSxcbiAgICAgICAgICAgICRzdWJtaXQ6ICRyb290LmZpbmQoJ1tkYXRhLXJlcXVlc3RldmFsLXN1Ym1pdF0nKVxuICAgICAgICB9XG4gICAgfVxuXG4gICAgX2Fzc2lnbkV2ZW50KCl7XG4gICAgICAgIHRoaXMuJHJvb3RcbiAgICAgICAgICAgIC5vbignY2hhbmdlJywgJ1tkYXRhLXJlcXVlc3RldmFsLWxpc3RdIGlucHV0JywgdGhpcy5fY2hlY2tGb3JtVmFsaWRhdGlvbi5iaW5kKHRoaXMpKVxuICAgICAgICAgICAgLm9uKCdpbnB1dCBwcm9wZXJ0eWNoYW5nZScsICdbZGF0YS1yZXF1ZXN0ZXZhbC10ZXh0YXJlYV0nLCB0aGlzLl9jaGVja0Zvcm1WYWxpZGF0aW9uLmJpbmQodGhpcykpXG4gICAgICAgICAgICAub24oJ2NsaWNrJywgJ1tkYXRhLXJlcXVlc3RldmFsLWZpbHRlcl0nLCAoZSkgPT57XG4gICAgICAgICAgICAgICAgZS5wcmV2ZW50RGVmYXVsdCgpO1xuXG4gICAgICAgICAgICAgICAgY29uc3QgJGxpbmsgPSAkKGUuY3VycmVudFRhcmdldCk7XG4gICAgICAgICAgICAgICAgdGhpcy5fZmlsdGVyUGFydGljaXBhbnRzKCRsaW5rKTtcbiAgICAgICAgICAgIH0pO1xuXG4gICAgICAgIEFwcC5ldmVudHMuc3ViKCdobXQucmVxdWVzdERsZy5yZW5kZXInLCB0aGlzLl9yZW5kZXJDaGVja2JveGVzLmJpbmQodGhpcykpO1xuICAgIH1cblxuICAgIF9jaGVja0Zvcm1WYWxpZGF0aW9uKCl7XG4gICAgICAgIGNvbnN0IGxvY2FscyA9IHRoaXMubG9jYWxzO1xuICAgICAgICBjb25zdCAkcGFydGljaXBhbnRzID0gdGhpcy4kcm9vdC5maW5kKCdbZGF0YS1yZXF1ZXN0ZXZhbC1saXN0XSBpbnB1dCcpO1xuICAgICAgICBsZXQgdmFsaWQgPSB0cnVlO1xuXG4gICAgICAgIGlmICghJHBhcnRpY2lwYW50cy5maWx0ZXIoJzpjaGVja2VkJykubGVuZ3RoKXtcbiAgICAgICAgICAgIHZhbGlkID0gZmFsc2U7XG4gICAgICAgIH1cblxuICAgICAgICBpZiAoIS9odHRwcz86L2kudGVzdChsb2NhbHMuJHRleHRhcmVhLnZhbCgpKSkge1xuICAgICAgICAgICAgdmFsaWQgPSBmYWxzZTtcbiAgICAgICAgfVxuXG4gICAgICAgIGlmICghdmFsaWQpe1xuICAgICAgICAgICAgbG9jYWxzLiRzdWJtaXQuYXR0cignZGlzYWJsZWQnLCAnZGlzYWJsZWQnKTtcbiAgICAgICAgfSBlbHNlIHtcbiAgICAgICAgICAgIGxvY2Fscy4kc3VibWl0LnJlbW92ZUF0dHIoJ2Rpc2FibGVkJyk7XG4gICAgICAgIH1cbiAgICB9XG5cbiAgICAvKipcbiAgICAgKiBSZW5kZXIgbGlzdCBvZiBjaGVja2JveGVzXG4gICAgICogQHBhcmFtIHtqUXVlcnl9IHBhcnRpY2lwYW50IC0gcGFydGljaXBhbnQgdGFibGVcbiAgICAgKiBAcHJpdmF0ZVxuICAgICAqL1xuICAgIF9yZW5kZXJDaGVja2JveGVzKHBhcnRpY2lwYW50KSB7XG4gICAgICAgIGxldCByb3dzSW5mbyA9IHBhcnRpY2lwYW50LnRhYmxlLl8oJ3RyJywge30pO1xuICAgICAgICBsZXQgaSwgbjtcblxuICAgICAgICBmb3IgKGkgPSAwLCBuID0gcm93c0luZm8ubGVuZ3RoOyBpIDwgbjsgaSsrKSB7XG4gICAgICAgICAgICBjb25zdCBkYXRhID0ge1xuICAgICAgICAgICAgICAgIGluZGV4OiBpLFxuICAgICAgICAgICAgICAgIHZhbHVlOiByb3dzSW5mb1tpXS5wZXJzb24uaWQsXG4gICAgICAgICAgICAgICAgbmFtZTogcm93c0luZm9baV0ucGVyc29uLm5hbWUsXG4gICAgICAgICAgICAgICAgc3RhdHVzOiAkLmlzUGxhaW5PYmplY3Qocm93c0luZm9baV0uZXZhbHVhdGlvbi5zdGF0dXMpXG4gICAgICAgICAgICB9O1xuICAgICAgICAgICAgY29uc3QgbGFiZWwgPSB0aGlzLnRlbXBsYXRlKGRhdGEpO1xuXG4gICAgICAgICAgICB0aGlzLmxvY2Fscy4kbGlzdFBhcnRpY2lwYW50cy5hcHBlbmQobGFiZWwpO1xuICAgICAgICB9XG4gICAgfVxuXG4gICAgLyoqXG4gICAgICpcbiAgICAgKiBAcGFyYW0ge2pRdWVyeX0gJGxpbmtcbiAgICAgKiBAcHJpdmF0ZVxuICAgICAqL1xuICAgIF9maWx0ZXJQYXJ0aWNpcGFudHMoJGxpbmspIHtcbiAgICAgICAgY29uc3QgJHBhcnRpY2lwYW50cyA9IHRoaXMuJHJvb3QuZmluZCgnW2RhdGEtcmVxdWVzdGV2YWwtbGlzdF0gaW5wdXQnKTtcbiAgICAgICAgY29uc3QgZmlsdGVyVGV4dCA9ICRsaW5rLmRhdGEoJ3JlcXVlc3RldmFsLWZpbHRlcicpO1xuICAgICAgICBjb25zdCAkZmlsdGVyTGlua3MgPSB0aGlzLmxvY2Fscy4kZmlsdGVyTGlua3M7XG5cbiAgICAgICAgaWYgKCRsaW5rLmhhc0NsYXNzKCdzdGF0ZV9zZWxlY3RlZCcpKSByZXR1cm47XG5cbiAgICAgICAgc3dpdGNoIChmaWx0ZXJUZXh0KXtcbiAgICAgICAgICAgIGNhc2UgJ2FsbCc6IHtcbiAgICAgICAgICAgICAgICAkcGFydGljaXBhbnRzLnByb3AoJ2NoZWNrZWQnLCB0cnVlKTtcbiAgICAgICAgICAgICAgICBicmVhaztcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIGNhc2UgJ3dpdGgnOiB7XG4gICAgICAgICAgICAgICAgJHBhcnRpY2lwYW50cy5wcm9wKCdjaGVja2VkJywgZmFsc2UpXG4gICAgICAgICAgICAgICAgICAgIC5maWx0ZXIoJy5oYXZlLWV2YWx1YXRpb24nKS5wcm9wKCdjaGVja2VkJywgdHJ1ZSk7XG4gICAgICAgICAgICAgICAgYnJlYWs7XG4gICAgICAgICAgICB9XG4gICAgICAgICAgICBjYXNlICd3aXRob3V0Jzoge1xuICAgICAgICAgICAgICAgICRwYXJ0aWNpcGFudHNcbiAgICAgICAgICAgICAgICAgICAgLnByb3AoJ2NoZWNrZWQnLCB0cnVlKVxuICAgICAgICAgICAgICAgICAgICAuZmlsdGVyKCcuaGF2ZS1ldmFsdWF0aW9uJykucHJvcCgnY2hlY2tlZCcsIGZhbHNlKTtcbiAgICAgICAgICAgICAgICBicmVhaztcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIGRlZmF1bHQ6IHtcbiAgICAgICAgICAgICAgICAkcGFydGljaXBhbnRzLnByb3AoJ2NoZWNrZWQnLCBmYWxzZSk7XG4gICAgICAgICAgICAgICAgYnJlYWs7XG4gICAgICAgICAgICB9XG4gICAgICAgIH1cblxuICAgICAgICAkZmlsdGVyTGlua3MucmVtb3ZlQ2xhc3MoJ3N0YXRlX3NlbGVjdGVkJyk7XG4gICAgICAgICRsaW5rLmFkZENsYXNzKCdzdGF0ZV9zZWxlY3RlZCcpO1xuXG4gICAgICAgIHRoaXMuX2NoZWNrRm9ybVZhbGlkYXRpb24oKTtcbiAgICB9XG5cbiAgICAvLyBzdGF0aWNcbiAgICBzdGF0aWMgcGx1Z2luKHNlbGVjdG9yKSB7XG4gICAgICAgIGNvbnN0ICRlbGVtcyA9ICQoc2VsZWN0b3IpO1xuICAgICAgICBpZiAoISRlbGVtcy5sZW5ndGgpIHJldHVybjtcblxuICAgICAgICByZXR1cm4gJGVsZW1zLmVhY2goZnVuY3Rpb24gKGluZGV4LCBlbCkge1xuICAgICAgICAgICAgbGV0ICRlbGVtZW50ID0gJChlbCk7XG4gICAgICAgICAgICBsZXQgZGF0YSAgICAgPSAkZWxlbWVudC5kYXRhKCdobXQuZXZlbnRzLnVwY29taW5nJyk7XG5cbiAgICAgICAgICAgIGlmICghZGF0YSkge1xuICAgICAgICAgICAgICAgIGRhdGEgPSBuZXcgV2lkZ2V0KGVsKTtcbiAgICAgICAgICAgICAgICAkZWxlbWVudC5kYXRhKCd3aWRnZXQnLCBkYXRhKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgfSlcbiAgICB9XG59XG5cblxuXG5cbi8qKiBXRUJQQUNLIEZPT1RFUiAqKlxuICoqIC4vZnJvbnRlbmQvanMvZXZlbnQvZGV0YWlsLXdpZGdldHMvX2V2YWx1YXRpb24tZGxnLmpzXG4gKiovIiwiJ3VzZSBzdHJpY3QnO1xuXG5pbXBvcnQgRm9ybUhlbHBlciBmcm9tIFwiLi8uLi8uLi9jb21tb24vX2Zvcm0taGVscGVyXCI7XG5cbmV4cG9ydCBkZWZhdWx0IGNsYXNzIFdpZGdldCB7XG4gICAgY29uc3RydWN0b3Ioc2VsZWN0b3Ipe1xuICAgICAgICB0aGlzLiRyb290ID0gJChzZWxlY3Rvcik7XG4gICAgICAgIHRoaXMubG9jYWxzID0gdGhpcy5fZ2V0RG9tKCk7XG4gICAgICAgIHRoaXMuZm9ybUhlbHBlciA9IG5ldyBGb3JtSGVscGVyKHRoaXMubG9jYWxzLiR0ZXh0YXJlYSk7XG4gICAgICAgIHRoaXMudGVtcGxhdGUgPSB0aGlzLmxvY2Fscy4kdGV4dGFyZWEudmFsKCk7XG5cbiAgICAgICAgdGhpcy5fYXNzaWduRXZlbnQoKTtcbiAgICB9XG5cbiAgICBfZ2V0RG9tKCl7XG4gICAgICAgIGNvbnN0ICRyb290ID0gdGhpcy4kcm9vdDtcblxuICAgICAgICByZXR1cm4ge1xuICAgICAgICAgICAgJGxpbms6ICRyb290LmZpbmQoJ1tkYXRhLWVtYWlsbW9kLWxpbmtdJyksXG4gICAgICAgICAgICAkZGVmYXVsdFRlbXBsYXRlOiAkcm9vdC5maW5kKCdbZGF0YS1lbWFpbG1vZC1kZWZhdWx0XScpLFxuICAgICAgICAgICAgJG1vZGFsOiAkcm9vdC5maW5kKCdbZGF0YS1lbWFpbG1vZC1kbGddJyksXG4gICAgICAgICAgICAkZm9ybTogJHJvb3QuZmluZCgnW2RhdGEtZW1haWxtb2QtZm9ybV0nKSxcbiAgICAgICAgICAgICR0ZXh0YXJlYTogJHJvb3QuZmluZCgnW2RhdGEtZW1haWxtb2QtdGV4dGFyZWFdJyksXG4gICAgICAgICAgICAkY2FuY2VsOiAkcm9vdC5maW5kKCdbZGF0YS1lbWFpbG1vZC1jYW5jZWxdJylcbiAgICAgICAgfVxuICAgIH1cblxuICAgIF9hc3NpZ25FdmVudCgpe1xuICAgICAgICBjb25zdCBzZWxmID0gdGhpcztcblxuICAgICAgICB0aGlzLiRyb290XG4gICAgICAgICAgICAub24oJ2NsaWNrJywgJ1tkYXRhLWVtYWlsbW9kLWxpbmtdJywgdGhpcy5fb25DbGlja1Nob3dNb2RhbC5iaW5kKHRoaXMpKVxuICAgICAgICAgICAgLm9uKCdjbGljaycsICdbZGF0YS1lbWFpbG1vZC1tYXJrXScsIHRoaXMuX29uQ2xpY2tVc2VUZW1wbGF0ZS5iaW5kKHRoaXMpKVxuICAgICAgICAgICAgLm9uKCdoaWRlLmJzLm1vZGFsJywgKGUpPT57XG4gICAgICAgICAgICAgICAgZS5zdG9wUHJvcGFnYXRpb24oKTtcbiAgICAgICAgICAgICAgICBzZWxmLl9vbkNsb3NlTW9kYWwoKTtcbiAgICAgICAgICAgIH0pO1xuXG4gICAgICAgIHRoaXMubG9jYWxzLiRmb3JtLm9uKCdzdWJtaXQnLCB0aGlzLl9vblN1Ym1pdEZvcm0uYmluZCh0aGlzKSk7XG4gICAgfVxuXG4gICAgX29uQ2xpY2tTaG93TW9kYWwoZSl7XG4gICAgICAgIGUucHJldmVudERlZmF1bHQoKTtcbiAgICAgICAgdGhpcy5sb2NhbHMuJG1vZGFsLm1vZGFsKCdzaG93Jyk7XG4gICAgfVxuXG4gICAgX29uQ2xpY2tVc2VUZW1wbGF0ZShlKXtcbiAgICAgICAgZS5wcmV2ZW50RGVmYXVsdCgpO1xuICAgICAgICBjb25zdCBsb2NhbHMgPSB0aGlzLmxvY2FscztcblxuICAgICAgICBsb2NhbHMuJHRleHRhcmVhXG4gICAgICAgICAgICAudmFsKGxvY2Fscy4kZGVmYXVsdFRlbXBsYXRlLnRleHQoKSlcbiAgICAgICAgICAgIC50cmlnZ2VyKCdtYXJrZG93bi5yZW5kZXInKVxuICAgIH1cblxuICAgIF9vbkNsb3NlTW9kYWwoKXtcbiAgICAgICAgdGhpcy5sb2NhbHMuJHRleHRhcmVhLnZhbCh0aGlzLnRlbXBsYXRlKTtcbiAgICAgICAgdGhpcy5mb3JtSGVscGVyLnJlbW92ZUVycm9ycygpO1xuICAgIH1cblxuICAgIF9vblN1Ym1pdEZvcm0oZSl7XG4gICAgICAgIGUucHJldmVudERlZmF1bHQoKTtcbiAgICAgICAgY29uc3Qgc2VsZiA9IHRoaXM7XG4gICAgICAgIGNvbnN0IGxvY2FscyA9IHRoaXMubG9jYWxzO1xuXG4gICAgICAgIGlmICghc2VsZi5mb3JtSGVscGVyLmlzVmFsaWRJbnB1dHMoKSkgcmV0dXJuO1xuICAgICAgICBjb25zdCBmb3JtRGF0YSA9IHNlbGYuZm9ybUhlbHBlci5nZXRGb3JtRGF0YSgpO1xuXG4gICAgICAgIHNlbGYuX3NlbmRFbWFpbENvbnRlbnQoZm9ybURhdGEpXG4gICAgICAgICAgICAuZG9uZSgoKT0+e1xuICAgICAgICAgICAgICAgIHNlbGYudGVtcGxhdGUgPSBsb2NhbHMuJHRleHRhcmVhLnZhbCgpO1xuICAgICAgICAgICAgICAgIGxvY2Fscy4kbW9kYWwubW9kYWwoJ2hpZGUnKTtcblxuICAgICAgICAgICAgICAgIHN1Y2Nlc3MoJ0VtYWlsIGlzIG1vZGlmaWVkJyk7XG4gICAgICAgICAgICB9KVxuICAgICAgICAgICAgLmZhaWwoKHJlc3BvbnNlKT0+e1xuICAgICAgICAgICAgICAgIGNvbnN0IGRhdGEgPSAkLnBhcnNlSlNPTihyZXNwb25zZS5yZXNwb25zZVRleHQpLmRhdGE7XG5cbiAgICAgICAgICAgICAgICBpZiAoIWRhdGEuZXJyb3JzKSByZXR1cm47XG5cbiAgICAgICAgICAgICAgICBzZWxmLmZvcm1IZWxwZXIuc2V0RXJyb3JzKGRhdGEuZXJyb3JzKTtcbiAgICAgICAgICAgIH0pXG4gICAgfVxuXG4gICAgLy90cmFuc3BvcnRcbiAgICBfc2VuZEVtYWlsQ29udGVudChmb3JtRGF0YSl7XG4gICAgICAgIGNvbnN0IHVybCA9IHRoaXMubG9jYWxzLiRmb3JtLmF0dHIoJ2FjdGlvbicpO1xuICAgICAgICByZXR1cm4gJC5wb3N0KHVybCwgZm9ybURhdGEpO1xuICAgIH1cblxuICAgIC8vIHN0YXRpY1xuICAgIHN0YXRpYyBwbHVnaW4oc2VsZWN0b3IpIHtcbiAgICAgICAgY29uc3QgJGVsZW1zID0gJChzZWxlY3Rvcik7XG4gICAgICAgIGlmICghJGVsZW1zLmxlbmd0aCkgcmV0dXJuO1xuXG4gICAgICAgIHJldHVybiAkZWxlbXMuZWFjaChmdW5jdGlvbiAoaW5kZXgsIGVsKSB7XG4gICAgICAgICAgICBsZXQgJGVsZW1lbnQgPSAkKGVsKTtcbiAgICAgICAgICAgIGxldCBkYXRhICAgICA9ICRlbGVtZW50LmRhdGEoJ2htdC5ldmVudHMubW9kaWZ5X2VtYWlsJyk7XG5cbiAgICAgICAgICAgIGlmICghZGF0YSkge1xuICAgICAgICAgICAgICAgIGRhdGEgPSBuZXcgV2lkZ2V0KGVsKTtcbiAgICAgICAgICAgICAgICAkZWxlbWVudC5kYXRhKCd3aWRnZXQnLCBkYXRhKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgfSlcbiAgICB9XG59XG5cblxuXG5cbi8qKiBXRUJQQUNLIEZPT1RFUiAqKlxuICoqIC4vZnJvbnRlbmQvanMvZXZlbnQvZGV0YWlsLXdpZGdldHMvX21vZGlmeS1lbWFpbC5qc1xuICoqLyJdLCJzb3VyY2VSb290IjoiIn0=