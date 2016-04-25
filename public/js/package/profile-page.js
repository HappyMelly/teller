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

	module.exports = __webpack_require__(62);


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
/* 51 */,
/* 52 */,
/* 53 */,
/* 54 */,
/* 55 */,
/* 56 */,
/* 57 */,
/* 58 */,
/* 59 */,
/* 60 */,
/* 61 */,
/* 62 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';
	
	var _integration = __webpack_require__(63);
	
	var _integration2 = _interopRequireDefault(_integration);
	
	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }
	
	$(function () {
	    App.events.sub('hmt.tab.shown', function () {
	        _integration2.default.plugin('.js-mailchimp-integration', {
	            activate: jsRoutes.controllers.cm.facilitator.MailChimp.activate().url,
	            deactivate: jsRoutes.controllers.cm.facilitator.MailChimp.deactivate().url,
	
	            getAvailableLists: jsRoutes.controllers.cm.facilitator.MailChimp.lists().url,
	
	            createImport: jsRoutes.controllers.cm.facilitator.MailChimp.connect().url,
	            updateImport: jsRoutes.controllers.cm.facilitator.MailChimp.update().url,
	            disableImport: jsRoutes.controllers.cm.facilitator.MailChimp.disconnect().url
	        });
	    });
	});

/***/ },
/* 63 */
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
	
	var _intergrationHelpers = __webpack_require__(64);
	
	var _intergrationHelpers2 = _interopRequireDefault(_intergrationHelpers);
	
	var _integrationImport = __webpack_require__(65);
	
	var _integrationImport2 = _interopRequireDefault(_integrationImport);
	
	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }
	
	var Widget = function () {
	    function Widget(selector, options) {
	        (0, _classCallCheck3.default)(this, Widget);
	
	        this.$root = $(selector);
	        this.options = options;
	        this.locals = this._getDom();
	
	        this.importDlgHelper = new _formHelper2.default(this.locals.$controls);
	
	        this._init();
	        this._assignEvents();
	    }
	
	    (0, _createClass3.default)(Widget, [{
	        key: '_getDom',
	        value: function _getDom() {
	            var $root = this.$root;
	
	            return {
	                $list: $root.find('[data-integ-list]'),
	                $controls: $root.find('[data-control]'),
	                $editDlg: $root.find('[data-integcreate-dlg]'),
	                $availableLists: $root.find('[data-integcreate-list]'),
	                $modalDisableInteg: $root.find('[data-integdisable-dlg]'),
	                $listName: $root.find('[data-list-name]')
	            };
	        }
	    }, {
	        key: '_init',
	        value: function _init() {
	            if (this.isIntegrationActive) {
	                this._checkAndInitExporting();
	            }
	        }
	    }, {
	        key: '_assignEvents',
	        value: function _assignEvents() {
	            this.$root.on('click', '[data-integdisable-yes]', this._ClickDeactivate.bind(this)).on('click', '[data-integ-import-btn]', this._onClickShowImport.bind(this)).on('click', '[data-integcreate-btn]', this._onEventSubmitEdit.bind(this)).on('click', '[data-integcreate-cancel]', this._onEventCancelEdit.bind(this));
	        }
	    }, {
	        key: '_ClickDeactivate',
	        value: function _ClickDeactivate(e) {
	            var $root = this.$root;
	
	            this._sendDeactivate(this.options.deactivate).done(function (data) {
	                $root.removeClass('b-integr_state_active b-integr_state_import b-integr_state_nolist');
	                success(data.message);
	            });
	        }
	    }, {
	        key: '_onClickShowImport',
	        value: function _onClickShowImport(e) {
	            e.preventDefault();
	            var self = this;
	
	            self.locals.$editDlg.modal('show');
	        }
	    }, {
	        key: '_onEventSubmitEdit',
	        value: function _onEventSubmitEdit(e) {
	            e.preventDefault();
	            var self = this;
	            var data = self.importDlgHelper.getFormData();
	            self._createImportList(this.options.createImport, data).done(function (data) {
	                self.locals.$list.append(data.body);
	                self.locals.$editDlg.modal('hide');
	                success(data.message);
	            }).fail(function (jqXHR, textStatus, errorThrown) {
	                var msg = JSON.parse(jqXHR.responseText);
	                error(msg.message);
	            });
	        }
	    }, {
	        key: '_onEventCancelEdit',
	        value: function _onEventCancelEdit(e) {
	            e.preventDefault();
	            this.locals.$editDlg.modal('hide');
	        }
	    }, {
	        key: '_checkAndInitExporting',
	        value: function _checkAndInitExporting() {
	            var $listItems = this.locals.$list.children();
	            var self = this;
	
	            if (!$listItems.length) {
	                this.$root.addClass('b-integr_state_loading');
	                self._getAvailableList(this.options.getAvailableLists).done(function (list) {
	                    _intergrationHelpers2.default._prepareSelectWithLists(self.locals.$availableLists, list, self.locals.$listName);
	                    self.$root.removeClass('b-integr_state_loading');
	                    if (list.length) {
	                        self.$root.addClass('b-integr_state_import');
	                    } else {
	                        self.$root.addClass('b-integr_state_nolist');
	                    }
	                });
	                return;
	            } else {
	                self._getAvailableList(this.options.getAvailableLists).done(function (list) {
	                    _intergrationHelpers2.default._prepareSelectWithLists(self.locals.$availableLists, list, self.locals.$listName);
	                });
	            }
	
	            this.$root.addClass('b-integr_state_import');
	            _integrationImport2.default.plugin($listItems, this.options);
	        }
	    }, {
	        key: 'isIntegrationActive',
	        value: function isIntegrationActive() {
	            return this.$root.hasClass('b-integr_state_active');
	        }
	
	        //transport
	
	    }, {
	        key: '_sendDeactivate',
	        value: function _sendDeactivate(url) {
	            return $.ajax({
	                type: "POST",
	                url: url,
	                data: {},
	                dataType: "json"
	            });
	        }
	    }, {
	        key: '_getAvailableList',
	        value: function _getAvailableList(url) {
	            var defer = $.Deferred();
	
	            $.get(url).done(function (data) {
	                var list = $.parseJSON(data).lists;
	                defer.resolve(list);
	            });
	            return defer.promise();
	        }
	    }, {
	        key: '_createImportList',
	        value: function _createImportList(url, data) {
	            return $.ajax({
	                type: "POST",
	                url: url,
	                data: data,
	                dataType: "json"
	            });
	        }
	
	        // static
	
	    }], [{
	        key: 'plugin',
	        value: function plugin(selector, options) {
	            var $elems = $(selector);
	            if (!$elems.length) return;
	
	            return $elems.each(function (index, el) {
	                var $element = $(el);
	                var data = $element.data('widget.integration');
	
	                if (!data) {
	                    data = new Widget(el, options);
	                    $element.data('widget.integration', data);
	                }
	            });
	        }
	    }]);
	    return Widget;
	}();

	exports.default = Widget;

/***/ },
/* 64 */
/***/ function(module, exports) {

	'use strict';
	
	Object.defineProperty(exports, "__esModule", {
	    value: true
	});
	var helpers = {
	    _prepareSelectWithLists: function _prepareSelectWithLists($select, list, $listName) {
	        $select.children().remove();
	
	        if (!list.length) {
	            $select.append('<option value="" disabled checked>No available lists </option>');
	            return;
	        }
	
	        list.forEach(function (item) {
	            $select.append('<option value="' + item.id + '">' + item.name + '</option>');
	        });
	        $select.on('change', function (e) {
	            $listName.val($select.find('option:selected').text());
	        });
	        $listName.val($select.find('option:selected').text());
	    }
	};
	
	exports.default = helpers;

/***/ },
/* 65 */
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
	
	var _intergrationHelpers = __webpack_require__(64);
	
	var _intergrationHelpers2 = _interopRequireDefault(_intergrationHelpers);
	
	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }
	
	var Widget = function () {
	    function Widget(selector, options) {
	        (0, _classCallCheck3.default)(this, Widget);
	
	        this.$root = $(selector);
	        this.options = $.extend({}, options, {
	            id: this.$root.data('import-id')
	        });
	        this.locals = this._getDom();
	
	        this.editDlgData = null;
	        this.editDlgHelper = new _formHelper2.default(this.locals.$controls);
	
	        this._assignEvents();
	    }
	
	    (0, _createClass3.default)(Widget, [{
	        key: '_getDom',
	        value: function _getDom() {
	            var $root = this.$root;
	
	            return {
	                $view: $root.find('[data-import-view]'),
	                $controls: $root.find('[data-control]'),
	                $editDlg: $root.find('[data-import-dlg]'),
	                $availableThemes: $root.find('[data-import-select]'),
	                $disableDlg: $root.find('[data-import-tooltip]'),
	                $listName: $root.find('[data-list-name]')
	            };
	        }
	    }, {
	        key: '_assignEvents',
	        value: function _assignEvents() {
	            this.$root.on('click', '[data-import-btn-edit]', this._onClickEditImport.bind(this)).on('click', '[data-import-btn-disable]', this._onClickShowTooltip.bind(this)).on('click', '[data-import-disable]', this._onEventDisableImport.bind(this)).on('click', '[data-import-cancel]', this._onClickCancelEdit.bind(this)).on('click', '[data-import-submit]', this._onClickSubmitEdit.bind(this));
	        }
	    }, {
	        key: '_onClickEditImport',
	        value: function _onClickEditImport(e) {
	            var _this = this;
	
	            e.preventDefault();
	            var self = this;
	
	            self._getAvailableList(self.options.getAvailableLists).done(function (list) {
	                _intergrationHelpers2.default._prepareSelectWithLists(self.locals.$availableThemes, list, self.locals.$listName);
	                _this.editDlgData = self.editDlgHelper.getFormData();
	
	                self.locals.$editDlg.modal('show');
	            });
	        }
	    }, {
	        key: '_onClickShowTooltip',
	        value: function _onClickShowTooltip(e) {
	            e.preventDefault();
	            this.locals.$disableDlg.modal('show');
	        }
	    }, {
	        key: '_onEventDisableImport',
	        value: function _onEventDisableImport(e) {
	            var self = this;
	
	            self._sendDisableList(self.options.disableImport, self.options.id).done(function (data) {
	                self.locals.$view.slideUp(400, function () {
	                    self.$root.remove();
	                });
	                success(data.message);
	            }).fail(function (jqXHR, textStatus, errorThrown) {
	                var msg = JSON.parse(jqXHR.responseText);
	                error(msg.message);
	            });
	        }
	    }, {
	        key: '_onClickCancelEdit',
	        value: function _onClickCancelEdit(e) {
	            e.preventDefault();
	
	            this._setDefaultValues();
	            this.locals.$editDlg.modal('hide');
	        }
	    }, {
	        key: '_onClickSubmitEdit',
	        value: function _onClickSubmitEdit(e) {
	            e.preventDefault();
	
	            var self = this;
	            var update = {
	                url: self.options.updateImport,
	                id: self.options.id,
	                formData: self.editDlgHelper.getFormData()
	            };
	            self.editDlgData = update.formData;
	
	            this._sendUpdateList(sendData).done(function () {
	                self.locals.$editDlg.modal('hide');
	                success('You are successfully update importing list');
	            }).fail(function (jqXHR, textStatus, errorThrown) {
	                var msg = JSON.parse(jqXHR.responseText);
	                self.locals.$editDlg.modal('hide');
	                error(msg.message);
	            });
	        }
	    }, {
	        key: '_setDefaultValues',
	        value: function _setDefaultValues() {
	            var $controls = this.locals.$controls;
	            var data = this.editDlgData;
	
	            for (var field in data) {
	                if (data.hasOwnProperty(field)) {
	                    var $control = $controls.filter('[name="' + field + '"]').first();
	
	                    if (!$control.length) return;
	
	                    if ($control.is(':checkbox')) {
	                        $control.prop('checked', data[field]);
	                    } else {
	                        $control.val(data[field]);
	                    }
	                }
	            }
	        }
	
	        //transport
	
	    }, {
	        key: '_getAvailableList',
	        value: function _getAvailableList(url) {
	            var defer = $.Deferred();
	
	            $.get(url).done(function (data) {
	                var list = $.parseJSON(data).lists;
	                defer.resolve(list);
	            });
	            return defer.promise();
	        }
	    }, {
	        key: '_sendDisableList',
	        value: function _sendDisableList(url, id) {
	            return $.ajax({
	                type: "POST",
	                url: url,
	                data: {
	                    list_id: id
	                },
	                dataType: "json"
	            });
	        }
	
	        /**
	         * Update Info about list
	         * @param {Object} data
	         * @param {String} data.url
	         * @param {Number} data.id
	         * @param {Object} data.formData - object with field/value
	         * @returns {Promise}
	         * @private
	         */
	
	    }, {
	        key: '_sendUpdateList',
	        value: function _sendUpdateList(data) {
	            return $.post(data.url, {
	                list_id: data.id,
	                data: data.formData
	            });
	        }
	    }], [{
	        key: 'plugin',
	        value: function plugin($elems, options) {
	            if (!$elems.length) return;
	
	            return $elems.each(function (index, el) {
	                var $element = $(el);
	                var data = $element.data('widget.importing');
	
	                if (!data) {
	                    data = new Widget(el, options);
	                    $element.data('widget.importing', data);
	                }
	            });
	        }
	    }]);
	    return Widget;
	}();

	exports.default = Widget;

/***/ }
/******/ ]);
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIndlYnBhY2s6Ly8vd2VicGFjay9ib290c3RyYXAgZjBmMTY2OTBiZDk3YTI1NWNmYjk/N2VmNCoqKioqKioqKioqIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9oZWxwZXJzL2NsYXNzQ2FsbENoZWNrLmpzPzIxYWYqKioqKioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvaGVscGVycy9jcmVhdGVDbGFzcy5qcz8xZGZlKioqKioqKioqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL2NvcmUtanMvb2JqZWN0L2RlZmluZS1wcm9wZXJ0eS5qcz81ZjcwKioqKioqKioqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L2ZuL29iamVjdC9kZWZpbmUtcHJvcGVydHkuanM/NGQzMyoqKioqKioqKioqIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL2VzNi5vYmplY3QuZGVmaW5lLXByb3BlcnR5LmpzPzhiZGUqKioqKioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZXhwb3J0LmpzPzhlNDAqKioqKioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZ2xvYmFsLmpzPzNjNTIqKioqKioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fY29yZS5qcz9kNjExKioqKioqKioqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2N0eC5qcz80ZTU5KioqKioqKioqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2EtZnVuY3Rpb24uanM/MDY5OSoqKioqKioqKioqIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19oaWRlLmpzPzBkMmUqKioqKioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fb2JqZWN0LWRwLmpzPzU5ODYqKioqKioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fYW4tb2JqZWN0LmpzPzNhZjIqKioqKioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9faXMtb2JqZWN0LmpzP2NmZGEqKioqKioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9faWU4LWRvbS1kZWZpbmUuanM/YjEwMioqKioqKioqKioqIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19kZXNjcmlwdG9ycy5qcz9jMGY1KioqKioqKioqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2ZhaWxzLmpzP2M2ZGQqKioqKioqKioqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZG9tLWNyZWF0ZS5qcz82MTJmKioqKioqKioqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX3RvLXByaW1pdGl2ZS5qcz8xYTY1KioqKioqKioqKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX3Byb3BlcnR5LWRlc2MuanM/MjU2YioqKioqKioqKioqIiwid2VicGFjazovLy8uL2Zyb250ZW5kL2pzL2NvbW1vbi9fZm9ybS1oZWxwZXIuanM/ODYzNioqKioqIiwid2VicGFjazovLy8uL2Zyb250ZW5kL2pzL3BlcnNvbi9wcm9maWxlLXBhZ2UuanMiLCJ3ZWJwYWNrOi8vLy4vZnJvbnRlbmQvanMvcGVyc29uL3BlcnNvbi13aWRnZXRzL19pbnRlZ3JhdGlvbi5qcyIsIndlYnBhY2s6Ly8vLi9mcm9udGVuZC9qcy9wZXJzb24vcGVyc29uLXdpZGdldHMvX2ludGVyZ3JhdGlvbi1oZWxwZXJzLmpzIiwid2VicGFjazovLy8uL2Zyb250ZW5kL2pzL3BlcnNvbi9wZXJzb24td2lkZ2V0cy9faW50ZWdyYXRpb24taW1wb3J0LmpzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiI7QUFBQTtBQUNBOztBQUVBO0FBQ0E7O0FBRUE7QUFDQTtBQUNBOztBQUVBO0FBQ0E7QUFDQSx1QkFBZTtBQUNmO0FBQ0E7QUFDQTs7QUFFQTtBQUNBOztBQUVBO0FBQ0E7O0FBRUE7QUFDQTtBQUNBOzs7QUFHQTtBQUNBOztBQUVBO0FBQ0E7O0FBRUE7QUFDQTs7QUFFQTtBQUNBOzs7Ozs7Ozs7Ozs7Ozs7O0FDdENBOztBQUVBOztBQUVBO0FBQ0E7QUFDQTtBQUNBO0FBQ0EsRzs7Ozs7O0FDUkE7O0FBRUE7O0FBRUE7O0FBRUE7O0FBRUEsdUNBQXNDLHVDQUF1QyxnQkFBZ0I7O0FBRTdGO0FBQ0E7QUFDQSxvQkFBbUIsa0JBQWtCO0FBQ3JDO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBOztBQUVBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQSxFQUFDLEc7Ozs7OztBQzFCRCxtQkFBa0IsdUQ7Ozs7OztBQ0FsQjtBQUNBO0FBQ0E7QUFDQTtBQUNBLEc7Ozs7OztBQ0pBO0FBQ0E7QUFDQSxzRUFBdUUsMENBQTBDLEU7Ozs7OztBQ0ZqSDtBQUNBO0FBQ0E7QUFDQTtBQUNBOztBQUVBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0Esb0VBQW1FO0FBQ25F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QUYsQ0FBTyxLQUFLLEdBQUwsRUFBVTtBQUNwQiwwQkFBUyxLQUFLLEVBQUw7QUFDVCx1QkFBTSxLQUFLLFFBQUw7Y0FGSCxDQUFQLENBRGlCOzs7O2dDQU9QLFFBQVEsU0FBUztBQUMzQixpQkFBSSxDQUFDLE9BQU8sTUFBUCxFQUFlLE9BQXBCOztBQUVBLG9CQUFPLE9BQU8sSUFBUCxDQUFZLFVBQVUsS0FBVixFQUFpQixFQUFqQixFQUFxQjtBQUNwQyxxQkFBSSxXQUFXLEVBQUUsRUFBRixDQUFYLENBRGdDO0FBRXBDLHFCQUFJLE9BQU8sU0FBUyxJQUFULENBQWMsa0JBQWQsQ0FBUCxDQUZnQzs7QUFJcEMscUJBQUksQ0FBQyxJQUFELEVBQU87QUFDUCw0QkFBTyxJQUFJLE1BQUosQ0FBVyxFQUFYLEVBQWUsT0FBZixDQUFQLENBRE87QUFFUCw4QkFBUyxJQUFULENBQWMsa0JBQWQsRUFBa0MsSUFBbEMsRUFGTztrQkFBWDtjQUplLENBQW5CLENBSDJCOzs7WUE5SmQiLCJmaWxlIjoicHJvZmlsZS1wYWdlLmpzIiwic291cmNlc0NvbnRlbnQiOlsiIFx0Ly8gVGhlIG1vZHVsZSBjYWNoZVxuIFx0dmFyIGluc3RhbGxlZE1vZHVsZXMgPSB7fTtcblxuIFx0Ly8gVGhlIHJlcXVpcmUgZnVuY3Rpb25cbiBcdGZ1bmN0aW9uIF9fd2VicGFja19yZXF1aXJlX18obW9kdWxlSWQpIHtcblxuIFx0XHQvLyBDaGVjayBpZiBtb2R1bGUgaXMgaW4gY2FjaGVcbiBcdFx0aWYoaW5zdGFsbGVkTW9kdWxlc1ttb2R1bGVJZF0pXG4gXHRcdFx0cmV0dXJuIGluc3RhbGxlZE1vZHVsZXNbbW9kdWxlSWRdLmV4cG9ydHM7XG5cbiBcdFx0Ly8gQ3JlYXRlIGEgbmV3IG1vZHVsZSAoYW5kIHB1dCBpdCBpbnRvIHRoZSBjYWNoZSlcbiBcdFx0dmFyIG1vZHVsZSA9IGluc3RhbGxlZE1vZHVsZXNbbW9kdWxlSWRdID0ge1xuIFx0XHRcdGV4cG9ydHM6IHt9LFxuIFx0XHRcdGlkOiBtb2R1bGVJZCxcbiBcdFx0XHRsb2FkZWQ6IGZhbHNlXG4gXHRcdH07XG5cbiBcdFx0Ly8gRXhlY3V0ZSB0aGUgbW9kdWxlIGZ1bmN0aW9uXG4gXHRcdG1vZHVsZXNbbW9kdWxlSWRdLmNhbGwobW9kdWxlLmV4cG9ydHMsIG1vZHVsZSwgbW9kdWxlLmV4cG9ydHMsIF9fd2VicGFja19yZXF1aXJlX18pO1xuXG4gXHRcdC8vIEZsYWcgdGhlIG1vZHVsZSBhcyBsb2FkZWRcbiBcdFx0bW9kdWxlLmxvYWRlZCA9IHRydWU7XG5cbiBcdFx0Ly8gUmV0dXJuIHRoZSBleHBvcnRzIG9mIHRoZSBtb2R1bGVcbiBcdFx0cmV0dXJuIG1vZHVsZS5leHBvcnRzO1xuIFx0fVxuXG5cbiBcdC8vIGV4cG9zZSB0aGUgbW9kdWxlcyBvYmplY3QgKF9fd2VicGFja19tb2R1bGVzX18pXG4gXHRfX3dlYnBhY2tfcmVxdWlyZV9fLm0gPSBtb2R1bGVzO1xuXG4gXHQvLyBleHBvc2UgdGhlIG1vZHVsZSBjYWNoZVxuIFx0X193ZWJwYWNrX3JlcXVpcmVfXy5jID0gaW5zdGFsbGVkTW9kdWxlcztcblxuIFx0Ly8gX193ZWJwYWNrX3B1YmxpY19wYXRoX19cbiBcdF9fd2VicGFja19yZXF1aXJlX18ucCA9IFwiXCI7XG5cbiBcdC8vIExvYWQgZW50cnkgbW9kdWxlIGFuZCByZXR1cm4gZXhwb3J0c1xuIFx0cmV0dXJuIF9fd2VicGFja19yZXF1aXJlX18oMCk7XG5cblxuXG4vKiogV0VCUEFDSyBGT09URVIgKipcbiAqKiB3ZWJwYWNrL2Jvb3RzdHJhcCBmMGYxNjY5MGJkOTdhMjU1Y2ZiOVxuICoqLyIsIlwidXNlIHN0cmljdFwiO1xuXG5leHBvcnRzLl9fZXNNb2R1bGUgPSB0cnVlO1xuXG5leHBvcnRzLmRlZmF1bHQgPSBmdW5jdGlvbiAoaW5zdGFuY2UsIENvbnN0cnVjdG9yKSB7XG4gIGlmICghKGluc3RhbmNlIGluc3RhbmNlb2YgQ29uc3RydWN0b3IpKSB7XG4gICAgdGhyb3cgbmV3IFR5cGVFcnJvcihcIkNhbm5vdCBjYWxsIGEgY2xhc3MgYXMgYSBmdW5jdGlvblwiKTtcbiAgfVxufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL2hlbHBlcnMvY2xhc3NDYWxsQ2hlY2suanNcbiAqKiBtb2R1bGUgaWQgPSAzXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJcInVzZSBzdHJpY3RcIjtcblxuZXhwb3J0cy5fX2VzTW9kdWxlID0gdHJ1ZTtcblxudmFyIF9kZWZpbmVQcm9wZXJ0eSA9IHJlcXVpcmUoXCJiYWJlbC1ydW50aW1lL2NvcmUtanMvb2JqZWN0L2RlZmluZS1wcm9wZXJ0eVwiKTtcblxudmFyIF9kZWZpbmVQcm9wZXJ0eTIgPSBfaW50ZXJvcFJlcXVpcmVEZWZhdWx0KF9kZWZpbmVQcm9wZXJ0eSk7XG5cbmZ1bmN0aW9uIF9pbnRlcm9wUmVxdWlyZURlZmF1bHQob2JqKSB7IHJldHVybiBvYmogJiYgb2JqLl9fZXNNb2R1bGUgPyBvYmogOiB7IGRlZmF1bHQ6IG9iaiB9OyB9XG5cbmV4cG9ydHMuZGVmYXVsdCA9IGZ1bmN0aW9uICgpIHtcbiAgZnVuY3Rpb24gZGVmaW5lUHJvcGVydGllcyh0YXJnZXQsIHByb3BzKSB7XG4gICAgZm9yICh2YXIgaSA9IDA7IGkgPCBwcm9wcy5sZW5ndGg7IGkrKykge1xuICAgICAgdmFyIGRlc2NyaXB0b3IgPSBwcm9wc1tpXTtcbiAgICAgIGRlc2NyaXB0b3IuZW51bWVyYWJsZSA9IGRlc2NyaXB0b3IuZW51bWVyYWJsZSB8fCBmYWxzZTtcbiAgICAgIGRlc2NyaXB0b3IuY29uZmlndXJhYmxlID0gdHJ1ZTtcbiAgICAgIGlmIChcInZhbHVlXCIgaW4gZGVzY3JpcHRvcikgZGVzY3JpcHRvci53cml0YWJsZSA9IHRydWU7XG4gICAgICAoMCwgX2RlZmluZVByb3BlcnR5Mi5kZWZhdWx0KSh0YXJnZXQsIGRlc2NyaXB0b3Iua2V5LCBkZXNjcmlwdG9yKTtcbiAgICB9XG4gIH1cblxuICByZXR1cm4gZnVuY3Rpb24gKENvbnN0cnVjdG9yLCBwcm90b1Byb3BzLCBzdGF0aWNQcm9wcykge1xuICAgIGlmIChwcm90b1Byb3BzKSBkZWZpbmVQcm9wZXJ0aWVzKENvbnN0cnVjdG9yLnByb3RvdHlwZSwgcHJvdG9Qcm9wcyk7XG4gICAgaWYgKHN0YXRpY1Byb3BzKSBkZWZpbmVQcm9wZXJ0aWVzKENvbnN0cnVjdG9yLCBzdGF0aWNQcm9wcyk7XG4gICAgcmV0dXJuIENvbnN0cnVjdG9yO1xuICB9O1xufSgpO1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvaGVscGVycy9jcmVhdGVDbGFzcy5qc1xuICoqIG1vZHVsZSBpZCA9IDRcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsIm1vZHVsZS5leHBvcnRzID0geyBcImRlZmF1bHRcIjogcmVxdWlyZShcImNvcmUtanMvbGlicmFyeS9mbi9vYmplY3QvZGVmaW5lLXByb3BlcnR5XCIpLCBfX2VzTW9kdWxlOiB0cnVlIH07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9jb3JlLWpzL29iamVjdC9kZWZpbmUtcHJvcGVydHkuanNcbiAqKiBtb2R1bGUgaWQgPSA1XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJyZXF1aXJlKCcuLi8uLi9tb2R1bGVzL2VzNi5vYmplY3QuZGVmaW5lLXByb3BlcnR5Jyk7XG52YXIgJE9iamVjdCA9IHJlcXVpcmUoJy4uLy4uL21vZHVsZXMvX2NvcmUnKS5PYmplY3Q7XG5tb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uIGRlZmluZVByb3BlcnR5KGl0LCBrZXksIGRlc2Mpe1xuICByZXR1cm4gJE9iamVjdC5kZWZpbmVQcm9wZXJ0eShpdCwga2V5LCBkZXNjKTtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9mbi9vYmplY3QvZGVmaW5lLXByb3BlcnR5LmpzXG4gKiogbW9kdWxlIGlkID0gNlxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwidmFyICRleHBvcnQgPSByZXF1aXJlKCcuL19leHBvcnQnKTtcclxuLy8gMTkuMS4yLjQgLyAxNS4yLjMuNiBPYmplY3QuZGVmaW5lUHJvcGVydHkoTywgUCwgQXR0cmlidXRlcylcclxuJGV4cG9ydCgkZXhwb3J0LlMgKyAkZXhwb3J0LkYgKiAhcmVxdWlyZSgnLi9fZGVzY3JpcHRvcnMnKSwgJ09iamVjdCcsIHtkZWZpbmVQcm9wZXJ0eTogcmVxdWlyZSgnLi9fb2JqZWN0LWRwJykuZn0pO1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9lczYub2JqZWN0LmRlZmluZS1wcm9wZXJ0eS5qc1xuICoqIG1vZHVsZSBpZCA9IDdcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsInZhciBnbG9iYWwgICAgPSByZXF1aXJlKCcuL19nbG9iYWwnKVxuICAsIGNvcmUgICAgICA9IHJlcXVpcmUoJy4vX2NvcmUnKVxuICAsIGN0eCAgICAgICA9IHJlcXVpcmUoJy4vX2N0eCcpXG4gICwgaGlkZSAgICAgID0gcmVxdWlyZSgnLi9faGlkZScpXG4gICwgUFJPVE9UWVBFID0gJ3Byb3RvdHlwZSc7XG5cbnZhciAkZXhwb3J0ID0gZnVuY3Rpb24odHlwZSwgbmFtZSwgc291cmNlKXtcbiAgdmFyIElTX0ZPUkNFRCA9IHR5cGUgJiAkZXhwb3J0LkZcbiAgICAsIElTX0dMT0JBTCA9IHR5cGUgJiAkZXhwb3J0LkdcbiAgICAsIElTX1NUQVRJQyA9IHR5cGUgJiAkZXhwb3J0LlNcbiAgICAsIElTX1BST1RPICA9IHR5cGUgJiAkZXhwb3J0LlBcbiAgICAsIElTX0JJTkQgICA9IHR5cGUgJiAkZXhwb3J0LkJcbiAgICAsIElTX1dSQVAgICA9IHR5cGUgJiAkZXhwb3J0LldcbiAgICAsIGV4cG9ydHMgICA9IElTX0dMT0JBTCA/IGNvcmUgOiBjb3JlW25hbWVdIHx8IChjb3JlW25hbWVdID0ge30pXG4gICAgLCBleHBQcm90byAgPSBleHBvcnRzW1BST1RPVFlQRV1cbiAgICAsIHRhcmdldCAgICA9IElTX0dMT0JBTCA/IGdsb2JhbCA6IElTX1NUQVRJQyA/IGdsb2JhbFtuYW1lXSA6IChnbG9iYWxbbmFtZV0gfHwge30pW1BST1RPVFlQRV1cbiAgICAsIGtleSwgb3duLCBvdXQ7XG4gIGlmKElTX0dMT0JBTClzb3VyY2UgPSBuYW1lO1xuICBmb3Ioa2V5IGluIHNvdXJjZSl7XG4gICAgLy8gY29udGFpbnMgaW4gbmF0aXZlXG4gICAgb3duID0gIUlTX0ZPUkNFRCAmJiB0YXJnZXQgJiYgdGFyZ2V0W2tleV0gIT09IHVuZGVmaW5lZDtcbiAgICBpZihvd24gJiYga2V5IGluIGV4cG9ydHMpY29udGludWU7XG4gICAgLy8gZXhwb3J0IG5hdGl2ZSBvciBwYXNzZWRcbiAgICBvdXQgPSBvd24gPyB0YXJnZXRba2V5XSA6IHNvdXJjZVtrZXldO1xuICAgIC8vIHByZXZlbnQgZ2xvYmFsIHBvbGx1dGlvbiBmb3IgbmFtZXNwYWNlc1xuICAgIGV4cG9ydHNba2V5XSA9IElTX0dMT0JBTCAmJiB0eXBlb2YgdGFyZ2V0W2tleV0gIT0gJ2Z1bmN0aW9uJyA/IHNvdXJjZVtrZXldXG4gICAgLy8gYmluZCB0aW1lcnMgdG8gZ2xvYmFsIGZvciBjYWxsIGZyb20gZXhwb3J0IGNvbnRleHRcbiAgICA6IElTX0JJTkQgJiYgb3duID8gY3R4KG91dCwgZ2xvYmFsKVxuICAgIC8vIHdyYXAgZ2xvYmFsIGNvbnN0cnVjdG9ycyBmb3IgcHJldmVudCBjaGFuZ2UgdGhlbSBpbiBsaWJyYXJ5XG4gICAgOiBJU19XUkFQICYmIHRhcmdldFtrZXldID09IG91dCA/IChmdW5jdGlvbihDKXtcbiAgICAgIHZhciBGID0gZnVuY3Rpb24oYSwgYiwgYyl7XG4gICAgICAgIGlmKHRoaXMgaW5zdGFuY2VvZiBDKXtcbiAgICAgICAgICBzd2l0Y2goYXJndW1lbnRzLmxlbmd0aCl7XG4gICAgICAgICAgICBjYXNlIDA6IHJldHVybiBuZXcgQztcbiAgICAgICAgICAgIGNhc2UgMTogcmV0dXJuIG5ldyBDKGEpO1xuICAgICAgICAgICAgY2FzZSAyOiByZXR1cm4gbmV3IEMoYSwgYik7XG4gICAgICAgICAgfSByZXR1cm4gbmV3IEMoYSwgYiwgYyk7XG4gICAgICAgIH0gcmV0dXJuIEMuYXBwbHkodGhpcywgYXJndW1lbnRzKTtcbiAgICAgIH07XG4gICAgICBGW1BST1RPVFlQRV0gPSBDW1BST1RPVFlQRV07XG4gICAgICByZXR1cm4gRjtcbiAgICAvLyBtYWtlIHN0YXRpYyB2ZXJzaW9ucyBmb3IgcHJvdG90eXBlIG1ldGhvZHNcbiAgICB9KShvdXQpIDogSVNfUFJPVE8gJiYgdHlwZW9mIG91dCA9PSAnZnVuY3Rpb24nID8gY3R4KEZ1bmN0aW9uLmNhbGwsIG91dCkgOiBvdXQ7XG4gICAgLy8gZXhwb3J0IHByb3RvIG1ldGhvZHMgdG8gY29yZS4lQ09OU1RSVUNUT1IlLm1ldGhvZHMuJU5BTUUlXG4gICAgaWYoSVNfUFJPVE8pe1xuICAgICAgKGV4cG9ydHMudmlydHVhbCB8fCAoZXhwb3J0cy52aXJ0dWFsID0ge30pKVtrZXldID0gb3V0O1xuICAgICAgLy8gZXhwb3J0IHByb3RvIG1ldGhvZHMgdG8gY29yZS4lQ09OU1RSVUNUT1IlLnByb3RvdHlwZS4lTkFNRSVcbiAgICAgIGlmKHR5cGUgJiAkZXhwb3J0LlIgJiYgZXhwUHJvdG8gJiYgIWV4cFByb3RvW2tleV0paGlkZShleHBQcm90bywga2V5LCBvdXQpO1xuICAgIH1cbiAgfVxufTtcbi8vIHR5cGUgYml0bWFwXG4kZXhwb3J0LkYgPSAxOyAgIC8vIGZvcmNlZFxuJGV4cG9ydC5HID0gMjsgICAvLyBnbG9iYWxcbiRleHBvcnQuUyA9IDQ7ICAgLy8gc3RhdGljXG4kZXhwb3J0LlAgPSA4OyAgIC8vIHByb3RvXG4kZXhwb3J0LkIgPSAxNjsgIC8vIGJpbmRcbiRleHBvcnQuVyA9IDMyOyAgLy8gd3JhcFxuJGV4cG9ydC5VID0gNjQ7ICAvLyBzYWZlXG4kZXhwb3J0LlIgPSAxMjg7IC8vIHJlYWwgcHJvdG8gbWV0aG9kIGZvciBgbGlicmFyeWAgXG5tb2R1bGUuZXhwb3J0cyA9ICRleHBvcnQ7XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19leHBvcnQuanNcbiAqKiBtb2R1bGUgaWQgPSA4XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCIvLyBodHRwczovL2dpdGh1Yi5jb20vemxvaXJvY2svY29yZS1qcy9pc3N1ZXMvODYjaXNzdWVjb21tZW50LTExNTc1OTAyOFxudmFyIGdsb2JhbCA9IG1vZHVsZS5leHBvcnRzID0gdHlwZW9mIHdpbmRvdyAhPSAndW5kZWZpbmVkJyAmJiB3aW5kb3cuTWF0aCA9PSBNYXRoXG4gID8gd2luZG93IDogdHlwZW9mIHNlbGYgIT0gJ3VuZGVmaW5lZCcgJiYgc2VsZi5NYXRoID09IE1hdGggPyBzZWxmIDogRnVuY3Rpb24oJ3JldHVybiB0aGlzJykoKTtcbmlmKHR5cGVvZiBfX2cgPT0gJ251bWJlcicpX19nID0gZ2xvYmFsOyAvLyBlc2xpbnQtZGlzYWJsZS1saW5lIG5vLXVuZGVmXG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19nbG9iYWwuanNcbiAqKiBtb2R1bGUgaWQgPSA5XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJ2YXIgY29yZSA9IG1vZHVsZS5leHBvcnRzID0ge3ZlcnNpb246ICcyLjIuMCd9O1xuaWYodHlwZW9mIF9fZSA9PSAnbnVtYmVyJylfX2UgPSBjb3JlOyAvLyBlc2xpbnQtZGlzYWJsZS1saW5lIG5vLXVuZGVmXG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19jb3JlLmpzXG4gKiogbW9kdWxlIGlkID0gMTBcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsIi8vIG9wdGlvbmFsIC8gc2ltcGxlIGNvbnRleHQgYmluZGluZ1xudmFyIGFGdW5jdGlvbiA9IHJlcXVpcmUoJy4vX2EtZnVuY3Rpb24nKTtcbm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oZm4sIHRoYXQsIGxlbmd0aCl7XG4gIGFGdW5jdGlvbihmbik7XG4gIGlmKHRoYXQgPT09IHVuZGVmaW5lZClyZXR1cm4gZm47XG4gIHN3aXRjaChsZW5ndGgpe1xuICAgIGNhc2UgMTogcmV0dXJuIGZ1bmN0aW9uKGEpe1xuICAgICAgcmV0dXJuIGZuLmNhbGwodGhhdCwgYSk7XG4gICAgfTtcbiAgICBjYXNlIDI6IHJldHVybiBmdW5jdGlvbihhLCBiKXtcbiAgICAgIHJldHVybiBmbi5jYWxsKHRoYXQsIGEsIGIpO1xuICAgIH07XG4gICAgY2FzZSAzOiByZXR1cm4gZnVuY3Rpb24oYSwgYiwgYyl7XG4gICAgICByZXR1cm4gZm4uY2FsbCh0aGF0LCBhLCBiLCBjKTtcbiAgICB9O1xuICB9XG4gIHJldHVybiBmdW5jdGlvbigvKiAuLi5hcmdzICovKXtcbiAgICByZXR1cm4gZm4uYXBwbHkodGhhdCwgYXJndW1lbnRzKTtcbiAgfTtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19jdHguanNcbiAqKiBtb2R1bGUgaWQgPSAxMVxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwibW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihpdCl7XG4gIGlmKHR5cGVvZiBpdCAhPSAnZnVuY3Rpb24nKXRocm93IFR5cGVFcnJvcihpdCArICcgaXMgbm90IGEgZnVuY3Rpb24hJyk7XG4gIHJldHVybiBpdDtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19hLWZ1bmN0aW9uLmpzXG4gKiogbW9kdWxlIGlkID0gMTJcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsInZhciBkUCAgICAgICAgID0gcmVxdWlyZSgnLi9fb2JqZWN0LWRwJylcbiAgLCBjcmVhdGVEZXNjID0gcmVxdWlyZSgnLi9fcHJvcGVydHktZGVzYycpO1xubW9kdWxlLmV4cG9ydHMgPSByZXF1aXJlKCcuL19kZXNjcmlwdG9ycycpID8gZnVuY3Rpb24ob2JqZWN0LCBrZXksIHZhbHVlKXtcbiAgcmV0dXJuIGRQLmYob2JqZWN0LCBrZXksIGNyZWF0ZURlc2MoMSwgdmFsdWUpKTtcbn0gOiBmdW5jdGlvbihvYmplY3QsIGtleSwgdmFsdWUpe1xuICBvYmplY3Rba2V5XSA9IHZhbHVlO1xuICByZXR1cm4gb2JqZWN0O1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2hpZGUuanNcbiAqKiBtb2R1bGUgaWQgPSAxM1xuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwidmFyIGFuT2JqZWN0ICAgICAgID0gcmVxdWlyZSgnLi9fYW4tb2JqZWN0JylcbiAgLCBJRThfRE9NX0RFRklORSA9IHJlcXVpcmUoJy4vX2llOC1kb20tZGVmaW5lJylcbiAgLCB0b1ByaW1pdGl2ZSAgICA9IHJlcXVpcmUoJy4vX3RvLXByaW1pdGl2ZScpXG4gICwgZFAgICAgICAgICAgICAgPSBPYmplY3QuZGVmaW5lUHJvcGVydHk7XG5cbmV4cG9ydHMuZiA9IHJlcXVpcmUoJy4vX2Rlc2NyaXB0b3JzJykgPyBPYmplY3QuZGVmaW5lUHJvcGVydHkgOiBmdW5jdGlvbiBkZWZpbmVQcm9wZXJ0eShPLCBQLCBBdHRyaWJ1dGVzKXtcbiAgYW5PYmplY3QoTyk7XG4gIFAgPSB0b1ByaW1pdGl2ZShQLCB0cnVlKTtcbiAgYW5PYmplY3QoQXR0cmlidXRlcyk7XG4gIGlmKElFOF9ET01fREVGSU5FKXRyeSB7XG4gICAgcmV0dXJuIGRQKE8sIFAsIEF0dHJpYnV0ZXMpO1xuICB9IGNhdGNoKGUpeyAvKiBlbXB0eSAqLyB9XG4gIGlmKCdnZXQnIGluIEF0dHJpYnV0ZXMgfHwgJ3NldCcgaW4gQXR0cmlidXRlcyl0aHJvdyBUeXBlRXJyb3IoJ0FjY2Vzc29ycyBub3Qgc3VwcG9ydGVkIScpO1xuICBpZigndmFsdWUnIGluIEF0dHJpYnV0ZXMpT1tQXSA9IEF0dHJpYnV0ZXMudmFsdWU7XG4gIHJldHVybiBPO1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX29iamVjdC1kcC5qc1xuICoqIG1vZHVsZSBpZCA9IDE0XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJ2YXIgaXNPYmplY3QgPSByZXF1aXJlKCcuL19pcy1vYmplY3QnKTtcbm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oaXQpe1xuICBpZighaXNPYmplY3QoaXQpKXRocm93IFR5cGVFcnJvcihpdCArICcgaXMgbm90IGFuIG9iamVjdCEnKTtcbiAgcmV0dXJuIGl0O1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2FuLW9iamVjdC5qc1xuICoqIG1vZHVsZSBpZCA9IDE1XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJtb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGl0KXtcbiAgcmV0dXJuIHR5cGVvZiBpdCA9PT0gJ29iamVjdCcgPyBpdCAhPT0gbnVsbCA6IHR5cGVvZiBpdCA9PT0gJ2Z1bmN0aW9uJztcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19pcy1vYmplY3QuanNcbiAqKiBtb2R1bGUgaWQgPSAxNlxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwibW9kdWxlLmV4cG9ydHMgPSAhcmVxdWlyZSgnLi9fZGVzY3JpcHRvcnMnKSAmJiAhcmVxdWlyZSgnLi9fZmFpbHMnKShmdW5jdGlvbigpe1xyXG4gIHJldHVybiBPYmplY3QuZGVmaW5lUHJvcGVydHkocmVxdWlyZSgnLi9fZG9tLWNyZWF0ZScpKCdkaXYnKSwgJ2EnLCB7Z2V0OiBmdW5jdGlvbigpeyByZXR1cm4gNzsgfX0pLmEgIT0gNztcclxufSk7XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19pZTgtZG9tLWRlZmluZS5qc1xuICoqIG1vZHVsZSBpZCA9IDE3XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCIvLyBUaGFuaydzIElFOCBmb3IgaGlzIGZ1bm55IGRlZmluZVByb3BlcnR5XG5tb2R1bGUuZXhwb3J0cyA9ICFyZXF1aXJlKCcuL19mYWlscycpKGZ1bmN0aW9uKCl7XG4gIHJldHVybiBPYmplY3QuZGVmaW5lUHJvcGVydHkoe30sICdhJywge2dldDogZnVuY3Rpb24oKXsgcmV0dXJuIDc7IH19KS5hICE9IDc7XG59KTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2Rlc2NyaXB0b3JzLmpzXG4gKiogbW9kdWxlIGlkID0gMThcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsIm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oZXhlYyl7XG4gIHRyeSB7XG4gICAgcmV0dXJuICEhZXhlYygpO1xuICB9IGNhdGNoKGUpe1xuICAgIHJldHVybiB0cnVlO1xuICB9XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZmFpbHMuanNcbiAqKiBtb2R1bGUgaWQgPSAxOVxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwidmFyIGlzT2JqZWN0ID0gcmVxdWlyZSgnLi9faXMtb2JqZWN0JylcbiAgLCBkb2N1bWVudCA9IHJlcXVpcmUoJy4vX2dsb2JhbCcpLmRvY3VtZW50XG4gIC8vIGluIG9sZCBJRSB0eXBlb2YgZG9jdW1lbnQuY3JlYXRlRWxlbWVudCBpcyAnb2JqZWN0J1xuICAsIGlzID0gaXNPYmplY3QoZG9jdW1lbnQpICYmIGlzT2JqZWN0KGRvY3VtZW50LmNyZWF0ZUVsZW1lbnQpO1xubW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihpdCl7XG4gIHJldHVybiBpcyA/IGRvY3VtZW50LmNyZWF0ZUVsZW1lbnQoaXQpIDoge307XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZG9tLWNyZWF0ZS5qc1xuICoqIG1vZHVsZSBpZCA9IDIwXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCIvLyA3LjEuMSBUb1ByaW1pdGl2ZShpbnB1dCBbLCBQcmVmZXJyZWRUeXBlXSlcbnZhciBpc09iamVjdCA9IHJlcXVpcmUoJy4vX2lzLW9iamVjdCcpO1xuLy8gaW5zdGVhZCBvZiB0aGUgRVM2IHNwZWMgdmVyc2lvbiwgd2UgZGlkbid0IGltcGxlbWVudCBAQHRvUHJpbWl0aXZlIGNhc2Vcbi8vIGFuZCB0aGUgc2Vjb25kIGFyZ3VtZW50IC0gZmxhZyAtIHByZWZlcnJlZCB0eXBlIGlzIGEgc3RyaW5nXG5tb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGl0LCBTKXtcbiAgaWYoIWlzT2JqZWN0KGl0KSlyZXR1cm4gaXQ7XG4gIHZhciBmbiwgdmFsO1xuICBpZihTICYmIHR5cGVvZiAoZm4gPSBpdC50b1N0cmluZykgPT0gJ2Z1bmN0aW9uJyAmJiAhaXNPYmplY3QodmFsID0gZm4uY2FsbChpdCkpKXJldHVybiB2YWw7XG4gIGlmKHR5cGVvZiAoZm4gPSBpdC52YWx1ZU9mKSA9PSAnZnVuY3Rpb24nICYmICFpc09iamVjdCh2YWwgPSBmbi5jYWxsKGl0KSkpcmV0dXJuIHZhbDtcbiAgaWYoIVMgJiYgdHlwZW9mIChmbiA9IGl0LnRvU3RyaW5nKSA9PSAnZnVuY3Rpb24nICYmICFpc09iamVjdCh2YWwgPSBmbi5jYWxsKGl0KSkpcmV0dXJuIHZhbDtcbiAgdGhyb3cgVHlwZUVycm9yKFwiQ2FuJ3QgY29udmVydCBvYmplY3QgdG8gcHJpbWl0aXZlIHZhbHVlXCIpO1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX3RvLXByaW1pdGl2ZS5qc1xuICoqIG1vZHVsZSBpZCA9IDIxXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJtb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGJpdG1hcCwgdmFsdWUpe1xuICByZXR1cm4ge1xuICAgIGVudW1lcmFibGUgIDogIShiaXRtYXAgJiAxKSxcbiAgICBjb25maWd1cmFibGU6ICEoYml0bWFwICYgMiksXG4gICAgd3JpdGFibGUgICAgOiAhKGJpdG1hcCAmIDQpLFxuICAgIHZhbHVlICAgICAgIDogdmFsdWVcbiAgfTtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19wcm9wZXJ0eS1kZXNjLmpzXG4gKiogbW9kdWxlIGlkID0gMjJcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsIid1c2Ugc3RyaWN0JztcblxuLyoqXG4gKiBFcnJvcnNcbiAqIEB0eXBlZGVmIHtPYmplY3R9IExpc3RFcnJvcnNcbiAqIEBwcm9wZXJ0eSB7U3RyaW5nfSBuYW1lIC0gbmFtZSBvZiBmaWVsZFxuICogQHByb3BlcnR5IHtTdHJpbmd9IGVycm9yIC0gZXJyb3IgZGVzY3JpcHRpb25cbiAqL1xuXG5leHBvcnQgZGVmYXVsdCBjbGFzcyBGb3JtSGVscGVyIHtcbiAgICAvKipcbiAgICAgKiBWYWxpZGF0ZSBmb3JtIHRocm91Z2ggaW5wdXRzXG4gICAgICogQHBhcmFtIHtqUXVlcnl9ICRjb250cm9sc1xuICAgICAqL1xuICAgIGNvbnN0cnVjdG9yKCRjb250cm9scykge1xuICAgICAgICB0aGlzLiRjb250cm9scyA9ICRjb250cm9scztcbiAgICAgICAgdGhpcy5hcnJFcnJvcnMgPSBbXTtcbiAgICAgICAgdGhpcy5fYXNzaWduRXZlbnRzKCk7XG4gICAgfVxuXG4gICAgX2Fzc2lnbkV2ZW50cygpIHtcbiAgICAgICAgdGhpcy4kY29udHJvbHMub24oJ2lucHV0IGNoYW5nZScsIChlKSA9PiB7XG4gICAgICAgICAgICBjb25zdCAkY29udHJvbCA9ICQoZS5jdXJyZW50VGFyZ2V0KTtcblxuICAgICAgICAgICAgdGhpcy5fdmFsaWRhdGVJbW1lZGlhdGUoJGNvbnRyb2wpO1xuICAgICAgICAgICAgdGhpcy5fcmVtb3ZlRXJyb3IoJGNvbnRyb2wpO1xuICAgICAgICB9KTtcbiAgICB9XG5cbiAgICBfdmFsaWRhdGVJbW1lZGlhdGUoJGNvbnRyb2wpe1xuICAgICAgICBpZiAoJGNvbnRyb2wuaGFzQ2xhc3MoJ3R5cGUtbnVtZXJpYycpKSB7XG4gICAgICAgICAgICAkY29udHJvbC52YWwoJGNvbnRyb2wudmFsKCkucmVwbGFjZSgvW15cXGRdKy9nLCAnJykpO1xuICAgICAgICB9XG5cbiAgICAgICAgaWYgKCRjb250cm9sLmhhc0NsYXNzKCd0eXBlLW5vc3BhY2UnKSkge1xuICAgICAgICAgICAgJGNvbnRyb2wudmFsKCRjb250cm9sLnZhbCgpLnJlcGxhY2UoL1xccy9nLCAnJykpO1xuICAgICAgICB9XG4gICAgfVxuXG4gICAgaXNWYWxpZElucHV0cygpIHtcbiAgICAgICAgY29uc3QgJGNvbnRyb2xzID0gdGhpcy4kY29udHJvbHM7XG4gICAgICAgIGxldCBlcnJvciA9IDA7XG5cbiAgICAgICAgJGNvbnRyb2xzLmVhY2goKGluZGV4LCBjb250cm9sKSA9PiB7XG4gICAgICAgICAgICBjb25zdCAkY29udHJvbCA9ICQoY29udHJvbCk7XG5cbiAgICAgICAgICAgIGlmICghdGhpcy5faXNWYWxpZElucHV0KCRjb250cm9sKSkge1xuICAgICAgICAgICAgICAgIGVycm9yICs9IDE7XG4gICAgICAgICAgICB9XG4gICAgICAgIH0pO1xuICAgICAgICByZXR1cm4gQm9vbGVhbighZXJyb3IpO1xuICAgIH1cblxuICAgIC8qKlxuICAgICAqIENoZWNrIGdpdmVuIGNvbnRyb2wsIGlzIGl0IHZhbGlkP1xuICAgICAqIEBwYXJhbSB7alF1ZXJ5fSAkY29udHJvbFxuICAgICAqIEByZXR1cm5zIHtib29sZWFufSAtIElzIHZhbGlkIGNvbnRyb2w/XG4gICAgICovXG4gICAgX2lzVmFsaWRJbnB1dCgkY29udHJvbCkge1xuICAgICAgICBjb25zdCB2YWx1ZSA9ICQudHJpbSgkY29udHJvbC52YWwoKSk7XG5cbiAgICAgICAgaWYgKCF2YWx1ZSAmJiAhJGNvbnRyb2wuaGFzQ2xhc3MoJ3R5cGUtb3B0aW9uYWwnKSkge1xuICAgICAgICAgICAgdGhpcy5fc2V0RXJyb3IoJGNvbnRyb2wsICdFbXB0eScpO1xuICAgICAgICAgICAgcmV0dXJuIGZhbHNlO1xuICAgICAgICB9XG5cbiAgICAgICAgaWYgKCgkY29udHJvbC5oYXNDbGFzcygndHlwZS1lbWFpbCcpKSAmJiAhdGhpcy5faXNWYWxpZEVtYWlsKHZhbHVlKSkge1xuICAgICAgICAgICAgdGhpcy5fc2V0RXJyb3IoJGNvbnRyb2wsICdFbWFpbCBpcyBub3QgdmFsaWQnKTtcbiAgICAgICAgICAgIHJldHVybiBmYWxzZTtcbiAgICAgICAgfVxuXG4gICAgICAgIHJldHVybiB0cnVlO1xuICAgIH1cblxuICAgIC8qKlxuICAgICAqIElzIEVtYWlsIHZhbGlkP1xuICAgICAqIEBwYXJhbSB7c3RyaW5nfSBlbWFpbFxuICAgICAqIEByZXR1cm5zIHtib29sZWFufVxuICAgICAqL1xuICAgIF9pc1ZhbGlkRW1haWwoZW1haWwpIHtcbiAgICAgICAgdmFyIHJlID0gL14oKFtePD4oKVxcW1xcXVxcXFwuLDs6XFxzQFwiXSsoXFwuW148PigpXFxbXFxdXFxcXC4sOzpcXHNAXCJdKykqKXwoXCIuK1wiKSlAKChcXFtbMC05XXsxLDN9XFwuWzAtOV17MSwzfVxcLlswLTldezEsM31cXC5bMC05XXsxLDN9XSl8KChbYS16QS1aXFwtMC05XStcXC4pK1thLXpBLVpdezIsfSkpJC87XG4gICAgICAgIHJldHVybiByZS50ZXN0KGVtYWlsKTtcbiAgICB9XG5cbiAgICAvKipcbiAgICAgKiBTZXQgZXJyb3IgZm9yIGNvbnRyb2xcbiAgICAgKiBAcGFyYW0ge2pRdWVyeX0gJGNvbnRyb2xcbiAgICAgKiBAcGFyYW0ge1N0cmluZ30gZXJyb3JUZXh0XG4gICAgICogQHBhcmFtIHtCb29sZWFufSBpbnNlcnRFcnJvclxuICAgICAqL1xuICAgIF9zZXRFcnJvcigkY29udHJvbCwgZXJyb3JUZXh0LCBpbnNlcnRFcnJvciA9IHRydWUpIHtcbiAgICAgICAgY29uc3QgJHBhcmVudCA9ICRjb250cm9sLnBhcmVudCgpO1xuICAgICAgICBjb25zdCAkZXJyb3IgPSAkcGFyZW50LmZpbmQoJy5iLWVycm9yJyk7XG5cbiAgICAgICAgaWYgKCRlcnJvci5sZW5ndGgpIHJldHVybjtcblxuICAgICAgICAkcGFyZW50LmFkZENsYXNzKCdiLWVycm9yX3Nob3cnKTtcbiAgICAgICAgXG4gICAgICAgIGluc2VydEVycm9yICYmICQoJzxkaXYgY2xhc3M9XCJiLWVycm9yXCIgLz4nKVxuICAgICAgICAgICAgLnRleHQoZXJyb3JUZXh0KVxuICAgICAgICAgICAgLmFwcGVuZFRvKCRwYXJlbnQpO1xuXG4gICAgICAgIHRoaXMuYXJyRXJyb3JzLnB1c2goe1xuICAgICAgICAgICAgbmFtZTogJGNvbnRyb2wuYXR0cignbmFtZScpLFxuICAgICAgICAgICAgZXJyb3I6IGVycm9yVGV4dFxuICAgICAgICB9KVxuICAgIH1cblxuICAgIC8qKlxuICAgICAqIFJlbW92ZSBlcnJvciBmb3IgY29udHJvbFxuICAgICAqIEBwYXJhbSB7alF1ZXJ5fSAkY29udHJvbFxuICAgICAqL1xuICAgIF9yZW1vdmVFcnJvcigkY29udHJvbCkge1xuICAgICAgICBjb25zdCAkcGFyZW50ID0gJGNvbnRyb2wucGFyZW50KCk7XG5cbiAgICAgICAgJHBhcmVudFxuICAgICAgICAgICAgLnJlbW92ZUNsYXNzKCdiLWVycm9yX3Nob3cnKVxuICAgICAgICAgICAgLmZpbmQoJy5iLWVycm9yJykucmVtb3ZlKCk7XG5cbiAgICAgICAgdGhpcy5hcnJFcnJvcnMgPSB0aGlzLmFyckVycm9ycy5maWx0ZXIoZnVuY3Rpb24gKGl0ZW0pIHtcbiAgICAgICAgICAgIHJldHVybiBpdGVtLm5hbWUgIT09ICRjb250cm9sLmF0dHIoJ25hbWUnKVxuICAgICAgICB9KVxuICAgIH1cblxuICAgIC8qKlxuICAgICAqIFNldCBlcnJvcnNcbiAgICAgKiBAcGFyYW0ge0FycmF5fSBlcnJvcnMgLSBbe25hbWU6IFwiZW1haWxcIiwgZXJyb3I6IFwiZW1wdHlcIn0sIHtuYW1lOiBcInBhc3N3b3JkXCIsIGVycm9yOiBcImVtcHR5XCJ9XVxuICAgICAqIEBwYXJhbSB7Qm9vbGVhbn0gaW5zZXJ0RXJyb3IgLSBpbnNlcnQgZXJyb3IgZGVzY3JpcHRpb24gdG8gdGhlIERvbSBcbiAgICAgKi9cbiAgICBzZXRFcnJvcnMoZXJyb3JzLCBpbnNlcnRFcnJvciA9IHRydWUpIHtcbiAgICAgICAgZXJyb3JzLmZvckVhY2goKGl0ZW0pID0+IHtcbiAgICAgICAgICAgIGNvbnN0ICRjdXJyZW50Q29udHJvbCA9IHRoaXMuJGNvbnRyb2xzLmZpbHRlcignW25hbWU9XCInICsgaXRlbS5uYW1lICsgJ1wiXScpLmZpcnN0KCk7XG5cbiAgICAgICAgICAgIGlmICgkY3VycmVudENvbnRyb2wubGVuZ3RoKSB0aGlzLl9zZXRFcnJvcigkY3VycmVudENvbnRyb2wsIGl0ZW0uZXJyb3IsIGluc2VydEVycm9yKVxuICAgICAgICB9KVxuICAgIH1cblxuICAgIC8qKlxuICAgICAqIEdldCB0ZXh0IHZlcnNpb24gb2YgZXJyb3JzIGluIG9uZSBsaW5lLlxuICAgICAqIEBwYXJhbSB7TGlzdEVycm9yc30gZXJyb3JzXG4gICAgICogQHJldHVybnMge3N0cmluZ31cbiAgICAgKi9cbiAgICBnZXRFcnJvcnNUZXh0KGVycm9ycykge1xuICAgICAgICBjb25zdCBhcnJFcnJvcnMgPSBlcnJvcnMgfHwgdGhpcy5hcnJFcnJvcnM7XG4gICAgICAgIGxldCBlcnJvclR4dCA9ICcnO1xuXG4gICAgICAgIGFyckVycm9ycy5mb3JFYWNoKChpdGVtKSA9PiB7XG4gICAgICAgICAgICBjb25zdCBuYW1lID0gaXRlbS5uYW1lWzBdLnRvVXBwZXJDYXNlKCkgKyBpdGVtLm5hbWUuc3Vic3RyKDEpO1xuXG4gICAgICAgICAgICBlcnJvclR4dCArPSBgJHtuYW1lfTogJHtpdGVtLmVycm9yfS4gYDtcbiAgICAgICAgfSk7XG5cbiAgICAgICAgcmV0dXJuIGVycm9yVHh0O1xuICAgIH1cblxuICAgIC8qKlxuICAgICAqIEdldCBsaXN0IG9mIGVycm9ycyB3aXRoIGZ1bGwgdGl0bGUgKGZyb20gY29udHJvbCB0aXRsZSBhdHRyaWJ1dGUpXG4gICAgICogQHBhcmFtIHtMaXN0RXJyb3JzfSBlcnJvcnMgLSBsaXN0IG9mIGVycm9yc1xuICAgICAqIEByZXR1cm5zIHtzdHJpbmd9XG4gICAgICovXG4gICAgZ2V0RXJyb3JzRnVsbChlcnJvcnMpIHtcbiAgICAgICAgY29uc3Qgc2VsZiA9IHRoaXM7XG4gICAgICAgIGNvbnN0IGFyckVycm9ycyA9IGVycm9ycyB8fCB0aGlzLmFyckVycm9ycztcbiAgICAgICAgbGV0IGVycm9yVHh0ID0gJyc7XG5cbiAgICAgICAgYXJyRXJyb3JzLmZvckVhY2goKGl0ZW0pID0+IHtcbiAgICAgICAgICAgIGNvbnN0ICRjb250cm9sID0gc2VsZi4kY29udHJvbHMuZmlsdGVyKGBbbmFtZT1cIiR7aXRlbS5uYW1lfVwiXWApLmZpcnN0KCk7XG4gICAgICAgICAgICBjb25zdCBuYW1lID0gJGNvbnRyb2wubGVuZ3RoPyAkY29udHJvbC5hdHRyKCd0aXRsZScpOiBpdGVtLm5hbWU7XG5cbiAgICAgICAgICAgIGVycm9yVHh0ICs9IGA8Yj4ke25hbWV9PC9iPjogJHtpdGVtLmVycm9yfS4gIDxicj48YnI+YDtcbiAgICAgICAgfSk7XG5cbiAgICAgICAgcmV0dXJuIGVycm9yVHh0O1xuICAgIH1cblxuICAgIGdldEZvcm1EYXRhKCl7XG4gICAgICAgIGxldCBhamF4RGF0YSA9IHt9O1xuXG4gICAgICAgIHRoaXMuJGNvbnRyb2xzLm1hcCgoaW5kZXgsIGVsKSA9PiB7XG4gICAgICAgICAgICBjb25zdCAkZWwgPSAkKGVsKTtcbiAgICAgICAgICAgIGNvbnN0IG5hbWUgPSAkZWwuYXR0cignbmFtZScpO1xuXG4gICAgICAgICAgICBpZiAoIW5hbWUpIHJldHVybjtcblxuICAgICAgICAgICAgaWYgKCRlbC5pcygnOmNoZWNrYm94Jykpe1xuICAgICAgICAgICAgICAgIGFqYXhEYXRhW25hbWVdID0gJGVsLnByb3AoJ2NoZWNrZWQnKVxuICAgICAgICAgICAgfSBlbHNlIHtcbiAgICAgICAgICAgICAgICBhamF4RGF0YVtuYW1lXSA9ICRlbC52YWwoKVxuICAgICAgICAgICAgfVxuICAgICAgICB9KTtcblxuICAgICAgICByZXR1cm4gYWpheERhdGE7XG4gICAgfVxuXG4gICAgLyoqXG4gICAgICogUmVtb3ZlIGFsbCBlcnJvcnNcbiAgICAgKi9cbiAgICByZW1vdmVFcnJvcnMoKSB7XG4gICAgICAgIHRoaXMuJGNvbnRyb2xzLmVhY2goKGluZGV4LCBlbCkgPT4ge1xuICAgICAgICAgICAgY29uc3QgJGVsID0gJChlbCk7XG4gICAgICAgICAgICB0aGlzLl9yZW1vdmVFcnJvcigkZWwpXG4gICAgICAgIH0pXG4gICAgfVxuXG4gICAgY2xlYXJGb3JtKCkge1xuICAgICAgICB0aGlzLiRjb250cm9scy5lYWNoKChpbmRleCwgZWwpID0+IHtcbiAgICAgICAgICAgIGNvbnN0ICRlbCA9ICQoZWwpO1xuICAgICAgICAgICAgaWYgKCEkZWwuYXR0cihcImRpc2FibGVkXCIpKSAgJGVsLnZhbCgnJyk7XG4gICAgICAgIH0pXG4gICAgfVxufVxuXG5cblxuLyoqIFdFQlBBQ0sgRk9PVEVSICoqXG4gKiogLi9mcm9udGVuZC9qcy9jb21tb24vX2Zvcm0taGVscGVyLmpzXG4gKiovIiwiJ3VzZSBzdHJpY3QnO1xuXG5pbXBvcnQgSW50ZWdyYXRpb25NYWlsY2hpbXAgZnJvbSBcIi4vcGVyc29uLXdpZGdldHMvX2ludGVncmF0aW9uXCI7XG5cblxuJChmdW5jdGlvbigpe1xuICAgIEFwcC5ldmVudHMuc3ViKCdobXQudGFiLnNob3duJywgKCkgPT57XG4gICAgICAgIEludGVncmF0aW9uTWFpbGNoaW1wLnBsdWdpbignLmpzLW1haWxjaGltcC1pbnRlZ3JhdGlvbicsIHtcbiAgICAgICAgICAgIGFjdGl2YXRlOiBqc1JvdXRlcy5jb250cm9sbGVycy5jbS5mYWNpbGl0YXRvci5NYWlsQ2hpbXAuYWN0aXZhdGUoKS51cmwsXG4gICAgICAgICAgICBkZWFjdGl2YXRlOiBqc1JvdXRlcy5jb250cm9sbGVycy5jbS5mYWNpbGl0YXRvci5NYWlsQ2hpbXAuZGVhY3RpdmF0ZSgpLnVybCxcblxuICAgICAgICAgICAgZ2V0QXZhaWxhYmxlTGlzdHM6IGpzUm91dGVzLmNvbnRyb2xsZXJzLmNtLmZhY2lsaXRhdG9yLk1haWxDaGltcC5saXN0cygpLnVybCxcblxuICAgICAgICAgICAgY3JlYXRlSW1wb3J0OiBqc1JvdXRlcy5jb250cm9sbGVycy5jbS5mYWNpbGl0YXRvci5NYWlsQ2hpbXAuY29ubmVjdCgpLnVybCxcbiAgICAgICAgICAgIHVwZGF0ZUltcG9ydDoganNSb3V0ZXMuY29udHJvbGxlcnMuY20uZmFjaWxpdGF0b3IuTWFpbENoaW1wLnVwZGF0ZSgpLnVybCxcbiAgICAgICAgICAgIGRpc2FibGVJbXBvcnQ6IGpzUm91dGVzLmNvbnRyb2xsZXJzLmNtLmZhY2lsaXRhdG9yLk1haWxDaGltcC5kaXNjb25uZWN0KCkudXJsXG4gICAgICAgIH0pXG4gICAgfSlcblxufSk7XG5cblxuXG5cblxuXG4vKiogV0VCUEFDSyBGT09URVIgKipcbiAqKiAuL2Zyb250ZW5kL2pzL3BlcnNvbi9wcm9maWxlLXBhZ2UuanNcbiAqKi8iLCIndXNlIHN0cmljdCc7XG5cbmltcG9ydCBGb3JtSGVscGVyIGZyb20gJy4vLi4vLi4vY29tbW9uL19mb3JtLWhlbHBlcic7XG5pbXBvcnQgaW50ZWdyYXRpb25IZWxwZXJzIGZyb20gJy4vX2ludGVyZ3JhdGlvbi1oZWxwZXJzJztcbmltcG9ydCBJbXBvcnRpbmdJdGVtIGZyb20gJy4vX2ludGVncmF0aW9uLWltcG9ydCc7XG5cblxuZXhwb3J0IGRlZmF1bHQgY2xhc3MgV2lkZ2V0IHtcbiAgICBjb25zdHJ1Y3RvcihzZWxlY3Rvciwgb3B0aW9ucykge1xuICAgICAgICB0aGlzLiRyb290ID0gJChzZWxlY3Rvcik7XG4gICAgICAgIHRoaXMub3B0aW9ucyA9IG9wdGlvbnM7XG4gICAgICAgIHRoaXMubG9jYWxzID0gdGhpcy5fZ2V0RG9tKCk7XG5cbiAgICAgICAgdGhpcy5pbXBvcnREbGdIZWxwZXIgPSBuZXcgRm9ybUhlbHBlcih0aGlzLmxvY2Fscy4kY29udHJvbHMpO1xuXG4gICAgICAgIHRoaXMuX2luaXQoKTtcbiAgICAgICAgdGhpcy5fYXNzaWduRXZlbnRzKCk7XG4gICAgfVxuXG4gICAgX2dldERvbSgpe1xuICAgICAgICBjb25zdCAkcm9vdCA9IHRoaXMuJHJvb3Q7XG4gICAgICAgIFxuICAgICAgICByZXR1cm4ge1xuICAgICAgICAgICAgJGxpc3Q6ICRyb290LmZpbmQoJ1tkYXRhLWludGVnLWxpc3RdJyksXG4gICAgICAgICAgICAkY29udHJvbHM6ICRyb290LmZpbmQoJ1tkYXRhLWNvbnRyb2xdJyksXG4gICAgICAgICAgICAkZWRpdERsZzogJHJvb3QuZmluZCgnW2RhdGEtaW50ZWdjcmVhdGUtZGxnXScpLFxuICAgICAgICAgICAgJGF2YWlsYWJsZUxpc3RzOiAkcm9vdC5maW5kKCdbZGF0YS1pbnRlZ2NyZWF0ZS1saXN0XScpLFxuICAgICAgICAgICAgJG1vZGFsRGlzYWJsZUludGVnOiAkcm9vdC5maW5kKCdbZGF0YS1pbnRlZ2Rpc2FibGUtZGxnXScpLFxuICAgICAgICAgICAgJGxpc3ROYW1lOiAkcm9vdC5maW5kKCdbZGF0YS1saXN0LW5hbWVdJylcbiAgICAgICAgfVxuICAgIH1cbiAgICBcbiAgICBfaW5pdCgpe1xuICAgICAgICBpZiAodGhpcy5pc0ludGVncmF0aW9uQWN0aXZlKSB7XG4gICAgICAgICAgICB0aGlzLl9jaGVja0FuZEluaXRFeHBvcnRpbmcoKVxuICAgICAgICB9ICAgIFxuICAgIH1cblxuICAgIF9hc3NpZ25FdmVudHMoKXtcbiAgICAgICAgdGhpcy4kcm9vdCAgXG4gICAgICAgICAgICAub24oJ2NsaWNrJywgJ1tkYXRhLWludGVnZGlzYWJsZS15ZXNdJywgdGhpcy5fQ2xpY2tEZWFjdGl2YXRlLmJpbmQodGhpcykpXG4gICAgICAgICAgICAub24oJ2NsaWNrJywgJ1tkYXRhLWludGVnLWltcG9ydC1idG5dJywgdGhpcy5fb25DbGlja1Nob3dJbXBvcnQuYmluZCh0aGlzKSlcbiAgICAgICAgICAgIC5vbignY2xpY2snLCAnW2RhdGEtaW50ZWdjcmVhdGUtYnRuXScsIHRoaXMuX29uRXZlbnRTdWJtaXRFZGl0LmJpbmQodGhpcykpXG4gICAgICAgICAgICAub24oJ2NsaWNrJywgJ1tkYXRhLWludGVnY3JlYXRlLWNhbmNlbF0nLCB0aGlzLl9vbkV2ZW50Q2FuY2VsRWRpdC5iaW5kKHRoaXMpKVxuICAgIH1cblxuICAgIF9DbGlja0RlYWN0aXZhdGUoZSl7XG4gICAgICAgIGNvbnN0ICRyb290ID0gdGhpcy4kcm9vdDtcblxuICAgICAgICB0aGlzLl9zZW5kRGVhY3RpdmF0ZSh0aGlzLm9wdGlvbnMuZGVhY3RpdmF0ZSlcbiAgICAgICAgICAgIC5kb25lKChkYXRhKT0+e1xuICAgICAgICAgICAgICAgICRyb290LnJlbW92ZUNsYXNzKCdiLWludGVncl9zdGF0ZV9hY3RpdmUgYi1pbnRlZ3Jfc3RhdGVfaW1wb3J0IGItaW50ZWdyX3N0YXRlX25vbGlzdCcpO1xuICAgICAgICAgICAgICAgIHN1Y2Nlc3MoZGF0YS5tZXNzYWdlKTtcbiAgICAgICAgICAgIH0pXG4gICAgfVxuXG4gICAgX29uQ2xpY2tTaG93SW1wb3J0KGUpe1xuICAgICAgICBlLnByZXZlbnREZWZhdWx0KCk7XG4gICAgICAgIGNvbnN0IHNlbGYgPSB0aGlzO1xuXG4gICAgICAgIHNlbGYubG9jYWxzLiRlZGl0RGxnLm1vZGFsKCdzaG93Jyk7XG4gICAgfVxuXG4gICAgX29uRXZlbnRTdWJtaXRFZGl0KGUpe1xuICAgICAgICBlLnByZXZlbnREZWZhdWx0KCk7XG4gICAgICAgIGNvbnN0IHNlbGYgPSB0aGlzO1xuICAgICAgICBjb25zdCBkYXRhID0gc2VsZi5pbXBvcnREbGdIZWxwZXIuZ2V0Rm9ybURhdGEoKTtcbiAgICAgICAgc2VsZi5fY3JlYXRlSW1wb3J0TGlzdCh0aGlzLm9wdGlvbnMuY3JlYXRlSW1wb3J0LCBkYXRhKVxuICAgICAgICAgICAgLmRvbmUoKGRhdGEpID0+IHtcbiAgICAgICAgICAgICAgICBzZWxmLmxvY2Fscy4kbGlzdC5hcHBlbmQoZGF0YS5ib2R5KTtcbiAgICAgICAgICAgICAgICBzZWxmLmxvY2Fscy4kZWRpdERsZy5tb2RhbCgnaGlkZScpO1xuICAgICAgICAgICAgICAgIHN1Y2Nlc3MoZGF0YS5tZXNzYWdlKVxuICAgICAgICAgICAgfSlcbiAgICAgICAgICAgIC5mYWlsKChqcVhIUiwgdGV4dFN0YXR1cywgZXJyb3JUaHJvd24pID0+IHtcbiAgICAgICAgICAgICAgICB2YXIgbXNnID0gSlNPTi5wYXJzZShqcVhIUi5yZXNwb25zZVRleHQpO1xuICAgICAgICAgICAgICAgIGVycm9yKG1zZy5tZXNzYWdlKTtcbiAgICAgICAgICAgIH0pXG4gICAgfVxuXG4gICAgX29uRXZlbnRDYW5jZWxFZGl0KGUpe1xuICAgICAgICBlLnByZXZlbnREZWZhdWx0KCk7XG4gICAgICAgIHRoaXMubG9jYWxzLiRlZGl0RGxnLm1vZGFsKCdoaWRlJyk7XG4gICAgfVxuXG4gICAgX2NoZWNrQW5kSW5pdEV4cG9ydGluZygpe1xuICAgICAgICBjb25zdCAkbGlzdEl0ZW1zID0gdGhpcy5sb2NhbHMuJGxpc3QuY2hpbGRyZW4oKTtcbiAgICAgICAgY29uc3Qgc2VsZiA9IHRoaXM7XG5cbiAgICAgICAgaWYgKCEkbGlzdEl0ZW1zLmxlbmd0aCl7XG4gICAgICAgICAgICB0aGlzLiRyb290LmFkZENsYXNzKCdiLWludGVncl9zdGF0ZV9sb2FkaW5nJyk7XG4gICAgICAgICAgICBzZWxmLl9nZXRBdmFpbGFibGVMaXN0KHRoaXMub3B0aW9ucy5nZXRBdmFpbGFibGVMaXN0cylcbiAgICAgICAgICAgICAgICAuZG9uZSgobGlzdCk9PntcbiAgICAgICAgICAgICAgICAgICAgaW50ZWdyYXRpb25IZWxwZXJzLl9wcmVwYXJlU2VsZWN0V2l0aExpc3RzKHNlbGYubG9jYWxzLiRhdmFpbGFibGVMaXN0cywgbGlzdCwgc2VsZi5sb2NhbHMuJGxpc3ROYW1lKTtcbiAgICAgICAgICAgICAgICAgICAgc2VsZi4kcm9vdC5yZW1vdmVDbGFzcygnYi1pbnRlZ3Jfc3RhdGVfbG9hZGluZycpO1xuICAgICAgICAgICAgICAgICAgICBpZiAobGlzdC5sZW5ndGgpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgIHNlbGYuJHJvb3QuYWRkQ2xhc3MoJ2ItaW50ZWdyX3N0YXRlX2ltcG9ydCcpO1xuICAgICAgICAgICAgICAgICAgICB9IGVsc2Uge1xuICAgICAgICAgICAgICAgICAgICAgICAgc2VsZi4kcm9vdC5hZGRDbGFzcygnYi1pbnRlZ3Jfc3RhdGVfbm9saXN0Jyk7XG4gICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICB9KTtcbiAgICAgICAgICAgIHJldHVybjtcbiAgICAgICAgfSBlbHNlIHtcbiAgICAgICAgICAgIHNlbGYuX2dldEF2YWlsYWJsZUxpc3QodGhpcy5vcHRpb25zLmdldEF2YWlsYWJsZUxpc3RzKVxuICAgICAgICAgICAgICAgIC5kb25lKChsaXN0KT0+IHtcbiAgICAgICAgICAgICAgICAgICAgaW50ZWdyYXRpb25IZWxwZXJzLl9wcmVwYXJlU2VsZWN0V2l0aExpc3RzKHNlbGYubG9jYWxzLiRhdmFpbGFibGVMaXN0cywgbGlzdCwgc2VsZi5sb2NhbHMuJGxpc3ROYW1lKTtcbiAgICAgICAgICAgICAgICB9KTtcbiAgICAgICAgfVxuXG4gICAgICAgIHRoaXMuJHJvb3QuYWRkQ2xhc3MoJ2ItaW50ZWdyX3N0YXRlX2ltcG9ydCcpO1xuICAgICAgICBJbXBvcnRpbmdJdGVtLnBsdWdpbigkbGlzdEl0ZW1zLCB0aGlzLm9wdGlvbnMpOyAgICAgIFxuICAgIH1cbiAgICBcbiAgICBpc0ludGVncmF0aW9uQWN0aXZlKCl7XG4gICAgICAgIHJldHVybiB0aGlzLiRyb290Lmhhc0NsYXNzKCdiLWludGVncl9zdGF0ZV9hY3RpdmUnKTtcbiAgICB9ICAgXG4gICAgXG4gICAgLy90cmFuc3BvcnRcbiAgICBfc2VuZERlYWN0aXZhdGUodXJsKXtcbiAgICAgICAgcmV0dXJuICQuYWpheCh7XG4gICAgICAgICAgICB0eXBlOiBcIlBPU1RcIixcbiAgICAgICAgICAgIHVybDogdXJsLFxuICAgICAgICAgICAgZGF0YToge30sXG4gICAgICAgICAgICBkYXRhVHlwZTogXCJqc29uXCJcbiAgICAgICAgfSk7XG4gICAgfVxuXG4gICAgX2dldEF2YWlsYWJsZUxpc3QodXJsKXtcbiAgICAgICAgbGV0IGRlZmVyID0gJC5EZWZlcnJlZCgpO1xuXG4gICAgICAgICQuZ2V0KHVybClcbiAgICAgICAgICAgIC5kb25lKChkYXRhKSA9PiB7XG4gICAgICAgICAgICAgICAgY29uc3QgbGlzdCA9ICQucGFyc2VKU09OKGRhdGEpLmxpc3RzO1xuICAgICAgICAgICAgICAgIGRlZmVyLnJlc29sdmUobGlzdCk7XG4gICAgICAgICAgICB9KTtcbiAgICAgICAgcmV0dXJuIGRlZmVyLnByb21pc2UoKTtcbiAgICB9XG5cbiAgICBfY3JlYXRlSW1wb3J0TGlzdCh1cmwsIGRhdGEpe1xuICAgICAgICByZXR1cm4gJC5hamF4KHtcbiAgICAgICAgICAgIHR5cGU6IFwiUE9TVFwiLFxuICAgICAgICAgICAgdXJsOiB1cmwsXG4gICAgICAgICAgICBkYXRhOiBkYXRhLFxuICAgICAgICAgICAgZGF0YVR5cGU6IFwianNvblwiXG4gICAgICAgIH0pO1xuICAgIH1cblxuICAgIC8vIHN0YXRpY1xuICAgIHN0YXRpYyBwbHVnaW4oc2VsZWN0b3IsIG9wdGlvbnMpIHtcbiAgICAgICAgY29uc3QgJGVsZW1zID0gJChzZWxlY3Rvcik7XG4gICAgICAgIGlmICghJGVsZW1zLmxlbmd0aCkgcmV0dXJuO1xuXG4gICAgICAgIHJldHVybiAkZWxlbXMuZWFjaChmdW5jdGlvbiAoaW5kZXgsIGVsKSB7XG4gICAgICAgICAgICBsZXQgJGVsZW1lbnQgPSAkKGVsKTtcbiAgICAgICAgICAgIGxldCBkYXRhICAgICA9ICRlbGVtZW50LmRhdGEoJ3dpZGdldC5pbnRlZ3JhdGlvbicpO1xuXG4gICAgICAgICAgICBpZiAoIWRhdGEpIHtcbiAgICAgICAgICAgICAgICBkYXRhID0gbmV3IFdpZGdldChlbCwgb3B0aW9ucyk7XG4gICAgICAgICAgICAgICAgJGVsZW1lbnQuZGF0YSgnd2lkZ2V0LmludGVncmF0aW9uJywgZGF0YSk7XG4gICAgICAgICAgICB9XG4gICAgICAgIH0pXG4gICAgfVxufVxuXG5cblxuXG5cblxuXG5cbi8qKiBXRUJQQUNLIEZPT1RFUiAqKlxuICoqIC4vZnJvbnRlbmQvanMvcGVyc29uL3BlcnNvbi13aWRnZXRzL19pbnRlZ3JhdGlvbi5qc1xuICoqLyIsImxldCBoZWxwZXJzID0ge1xuICAgIF9wcmVwYXJlU2VsZWN0V2l0aExpc3RzKCRzZWxlY3QsIGxpc3QsICRsaXN0TmFtZSl7XG4gICAgICAgICRzZWxlY3QuY2hpbGRyZW4oKS5yZW1vdmUoKTtcblxuICAgICAgICBpZiAoIWxpc3QubGVuZ3RoKXtcbiAgICAgICAgICAgICRzZWxlY3QuYXBwZW5kKGA8b3B0aW9uIHZhbHVlPVwiXCIgZGlzYWJsZWQgY2hlY2tlZD5ObyBhdmFpbGFibGUgbGlzdHMgPC9vcHRpb24+YCk7XG4gICAgICAgICAgICByZXR1cm47XG4gICAgICAgIH1cblxuICAgICAgICBsaXN0LmZvckVhY2goKGl0ZW0pPT57XG4gICAgICAgICAgICAkc2VsZWN0LmFwcGVuZChgPG9wdGlvbiB2YWx1ZT1cIiR7aXRlbS5pZH1cIj4ke2l0ZW0ubmFtZX08L29wdGlvbj5gKVxuICAgICAgICB9KTtcbiAgICAgICAgJHNlbGVjdC5vbignY2hhbmdlJywgZnVuY3Rpb24oZSkge1xuICAgICAgICAgICAgJGxpc3ROYW1lLnZhbCgkc2VsZWN0LmZpbmQoJ29wdGlvbjpzZWxlY3RlZCcpLnRleHQoKSk7XG4gICAgICAgIH0pO1xuICAgICAgICAkbGlzdE5hbWUudmFsKCRzZWxlY3QuZmluZCgnb3B0aW9uOnNlbGVjdGVkJykudGV4dCgpKTtcbiAgICB9XG59XG5cbmV4cG9ydCBkZWZhdWx0IGhlbHBlcnM7XG5cblxuLyoqIFdFQlBBQ0sgRk9PVEVSICoqXG4gKiogLi9mcm9udGVuZC9qcy9wZXJzb24vcGVyc29uLXdpZGdldHMvX2ludGVyZ3JhdGlvbi1oZWxwZXJzLmpzXG4gKiovIiwiJ3VzZSBzdHJpY3QnO1xuXG5pbXBvcnQgRm9ybUhlbHBlciBmcm9tICcuLy4uLy4uL2NvbW1vbi9fZm9ybS1oZWxwZXInO1xuaW1wb3J0IGludGVncmF0aW9uSGVscGVycyBmcm9tICcuL19pbnRlcmdyYXRpb24taGVscGVycyc7XG5cblxuZXhwb3J0IGRlZmF1bHQgY2xhc3MgV2lkZ2V0IHtcbiAgICBjb25zdHJ1Y3RvcihzZWxlY3Rvciwgb3B0aW9ucykge1xuICAgICAgICB0aGlzLiRyb290ID0gJChzZWxlY3Rvcik7XG4gICAgICAgIHRoaXMub3B0aW9ucyA9ICQuZXh0ZW5kKHt9LCBvcHRpb25zLCB7XG4gICAgICAgICAgICBpZDogdGhpcy4kcm9vdC5kYXRhKCdpbXBvcnQtaWQnKVxuICAgICAgICB9KTtcbiAgICAgICAgdGhpcy5sb2NhbHMgPSB0aGlzLl9nZXREb20oKTtcblxuICAgICAgICB0aGlzLmVkaXREbGdEYXRhID0gbnVsbDtcbiAgICAgICAgdGhpcy5lZGl0RGxnSGVscGVyID0gbmV3IEZvcm1IZWxwZXIodGhpcy5sb2NhbHMuJGNvbnRyb2xzKTtcblxuICAgICAgICB0aGlzLl9hc3NpZ25FdmVudHMoKTtcbiAgICB9XG5cbiAgICBfZ2V0RG9tKCl7XG4gICAgICAgIGNvbnN0ICRyb290ID0gdGhpcy4kcm9vdDtcblxuICAgICAgICByZXR1cm4ge1xuICAgICAgICAgICAgJHZpZXc6ICRyb290LmZpbmQoJ1tkYXRhLWltcG9ydC12aWV3XScpLFxuICAgICAgICAgICAgJGNvbnRyb2xzOiAkcm9vdC5maW5kKCdbZGF0YS1jb250cm9sXScpLFxuICAgICAgICAgICAgJGVkaXREbGc6ICRyb290LmZpbmQoJ1tkYXRhLWltcG9ydC1kbGddJyksXG4gICAgICAgICAgICAkYXZhaWxhYmxlVGhlbWVzOiAkcm9vdC5maW5kKCdbZGF0YS1pbXBvcnQtc2VsZWN0XScpLFxuICAgICAgICAgICAgJGRpc2FibGVEbGc6ICRyb290LmZpbmQoJ1tkYXRhLWltcG9ydC10b29sdGlwXScpLFxuICAgICAgICAgICAgJGxpc3ROYW1lOiAkcm9vdC5maW5kKCdbZGF0YS1saXN0LW5hbWVdJylcbiAgICAgICAgfVxuICAgIH1cblxuICAgIF9hc3NpZ25FdmVudHMoKXtcbiAgICAgICAgdGhpcy4kcm9vdFxuICAgICAgICAgICAgLm9uKCdjbGljaycsICdbZGF0YS1pbXBvcnQtYnRuLWVkaXRdJywgdGhpcy5fb25DbGlja0VkaXRJbXBvcnQuYmluZCh0aGlzKSlcbiAgICAgICAgICAgIC5vbignY2xpY2snLCAnW2RhdGEtaW1wb3J0LWJ0bi1kaXNhYmxlXScsIHRoaXMuX29uQ2xpY2tTaG93VG9vbHRpcC5iaW5kKHRoaXMpKVxuICAgICAgICAgICAgLm9uKCdjbGljaycsICdbZGF0YS1pbXBvcnQtZGlzYWJsZV0nLCB0aGlzLl9vbkV2ZW50RGlzYWJsZUltcG9ydC5iaW5kKHRoaXMpKVxuICAgICAgICAgICAgLm9uKCdjbGljaycsICdbZGF0YS1pbXBvcnQtY2FuY2VsXScsIHRoaXMuX29uQ2xpY2tDYW5jZWxFZGl0LmJpbmQodGhpcykpXG4gICAgICAgICAgICAub24oJ2NsaWNrJywgJ1tkYXRhLWltcG9ydC1zdWJtaXRdJywgdGhpcy5fb25DbGlja1N1Ym1pdEVkaXQuYmluZCh0aGlzKSlcbiAgICB9XG5cbiAgICBfb25DbGlja0VkaXRJbXBvcnQoZSl7XG4gICAgICAgIGUucHJldmVudERlZmF1bHQoKTtcbiAgICAgICAgY29uc3Qgc2VsZiA9IHRoaXM7XG5cbiAgICAgICAgc2VsZi5fZ2V0QXZhaWxhYmxlTGlzdChzZWxmLm9wdGlvbnMuZ2V0QXZhaWxhYmxlTGlzdHMpXG4gICAgICAgICAgICAuZG9uZSgobGlzdCk9PntcbiAgICAgICAgICAgICAgICBpbnRlZ3JhdGlvbkhlbHBlcnMuX3ByZXBhcmVTZWxlY3RXaXRoTGlzdHMoc2VsZi5sb2NhbHMuJGF2YWlsYWJsZVRoZW1lcywgbGlzdCwgc2VsZi5sb2NhbHMuJGxpc3ROYW1lKTtcbiAgICAgICAgICAgICAgICB0aGlzLmVkaXREbGdEYXRhID0gc2VsZi5lZGl0RGxnSGVscGVyLmdldEZvcm1EYXRhKCk7XG5cbiAgICAgICAgICAgICAgICBzZWxmLmxvY2Fscy4kZWRpdERsZy5tb2RhbCgnc2hvdycpO1xuICAgICAgICAgICAgfSk7XG4gICAgfVxuXG4gICAgX29uQ2xpY2tTaG93VG9vbHRpcChlKXtcbiAgICAgICAgZS5wcmV2ZW50RGVmYXVsdCgpO1xuICAgICAgICB0aGlzLmxvY2Fscy4kZGlzYWJsZURsZy5tb2RhbCgnc2hvdycpO1xuICAgIH1cblxuICAgIF9vbkV2ZW50RGlzYWJsZUltcG9ydChlKXtcbiAgICAgICAgY29uc3Qgc2VsZiA9IHRoaXM7XG4gICAgICAgIFxuICAgICAgICBzZWxmLl9zZW5kRGlzYWJsZUxpc3Qoc2VsZi5vcHRpb25zLmRpc2FibGVJbXBvcnQsIHNlbGYub3B0aW9ucy5pZClcbiAgICAgICAgICAgIC5kb25lKChkYXRhKSA9PiB7XG4gICAgICAgICAgICAgICAgc2VsZi5sb2NhbHMuJHZpZXcuc2xpZGVVcCg0MDAsICgpPT57XG4gICAgICAgICAgICAgICAgICAgIHNlbGYuJHJvb3QucmVtb3ZlKCk7XG4gICAgICAgICAgICAgICAgfSk7XG4gICAgICAgICAgICAgICAgc3VjY2VzcyhkYXRhLm1lc3NhZ2UpO1xuICAgICAgICAgICAgfSlcbiAgICAgICAgICAgIC5mYWlsKChqcVhIUiwgdGV4dFN0YXR1cywgZXJyb3JUaHJvd24pID0+IHtcbiAgICAgICAgICAgICAgICB2YXIgbXNnID0gSlNPTi5wYXJzZShqcVhIUi5yZXNwb25zZVRleHQpO1xuICAgICAgICAgICAgICAgIGVycm9yKG1zZy5tZXNzYWdlKTtcbiAgICAgICAgICAgIH0pXG4gICAgfVxuXG4gICAgX29uQ2xpY2tDYW5jZWxFZGl0KGUpe1xuICAgICAgICBlLnByZXZlbnREZWZhdWx0KCk7XG5cbiAgICAgICAgdGhpcy5fc2V0RGVmYXVsdFZhbHVlcygpO1xuICAgICAgICB0aGlzLmxvY2Fscy4kZWRpdERsZy5tb2RhbCgnaGlkZScpO1xuICAgIH1cblxuICAgIF9vbkNsaWNrU3VibWl0RWRpdChlKXtcbiAgICAgICAgZS5wcmV2ZW50RGVmYXVsdCgpO1xuXG4gICAgICAgIGNvbnN0IHNlbGYgPSB0aGlzO1xuICAgICAgICBsZXQgdXBkYXRlID0ge1xuICAgICAgICAgICAgdXJsOiBzZWxmLm9wdGlvbnMudXBkYXRlSW1wb3J0LFxuICAgICAgICAgICAgaWQ6IHNlbGYub3B0aW9ucy5pZCxcbiAgICAgICAgICAgIGZvcm1EYXRhOiBzZWxmLmVkaXREbGdIZWxwZXIuZ2V0Rm9ybURhdGEoKVxuICAgICAgICB9O1xuICAgICAgICBzZWxmLmVkaXREbGdEYXRhID0gdXBkYXRlLmZvcm1EYXRhO1xuXG4gICAgICAgIHRoaXMuX3NlbmRVcGRhdGVMaXN0KHNlbmREYXRhKVxuICAgICAgICAgICAgLmRvbmUoKCkgPT57XG4gICAgICAgICAgICAgICAgc2VsZi5sb2NhbHMuJGVkaXREbGcubW9kYWwoJ2hpZGUnKTtcbiAgICAgICAgICAgICAgICBzdWNjZXNzKCdZb3UgYXJlIHN1Y2Nlc3NmdWxseSB1cGRhdGUgaW1wb3J0aW5nIGxpc3QnKVxuICAgICAgICAgICAgfSlcbiAgICAgICAgICAgIC5mYWlsKChqcVhIUiwgdGV4dFN0YXR1cywgZXJyb3JUaHJvd24pID0+IHtcbiAgICAgICAgICAgICAgICB2YXIgbXNnID0gSlNPTi5wYXJzZShqcVhIUi5yZXNwb25zZVRleHQpO1xuICAgICAgICAgICAgICAgIHNlbGYubG9jYWxzLiRlZGl0RGxnLm1vZGFsKCdoaWRlJyk7XG4gICAgICAgICAgICAgICAgZXJyb3IobXNnLm1lc3NhZ2UpO1xuICAgICAgICAgICAgfSlcbiAgICB9XG5cbiAgICBfc2V0RGVmYXVsdFZhbHVlcygpe1xuICAgICAgICBjb25zdCAkY29udHJvbHMgPSB0aGlzLmxvY2Fscy4kY29udHJvbHM7XG4gICAgICAgIGNvbnN0IGRhdGEgPSB0aGlzLmVkaXREbGdEYXRhO1xuXG4gICAgICAgIGZvciggbGV0IGZpZWxkIGluIGRhdGEpe1xuICAgICAgICAgICAgaWYgKGRhdGEuaGFzT3duUHJvcGVydHkoZmllbGQpKXtcbiAgICAgICAgICAgICAgICBsZXQgJGNvbnRyb2wgPSAkY29udHJvbHMuZmlsdGVyKGBbbmFtZT1cIiR7ZmllbGR9XCJdYCkuZmlyc3QoKTtcblxuICAgICAgICAgICAgICAgIGlmICghJGNvbnRyb2wubGVuZ3RoKSByZXR1cm47XG5cbiAgICAgICAgICAgICAgICBpZiAoJGNvbnRyb2wuaXMoJzpjaGVja2JveCcpKXtcbiAgICAgICAgICAgICAgICAgICAgJGNvbnRyb2wucHJvcCgnY2hlY2tlZCcsIGRhdGFbZmllbGRdKVxuICAgICAgICAgICAgICAgIH0gZWxzZXtcbiAgICAgICAgICAgICAgICAgICAgJGNvbnRyb2wudmFsKGRhdGFbZmllbGRdKTtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICB9XG4gICAgICAgIH1cbiAgICB9XG5cbiAgICAvL3RyYW5zcG9ydFxuICAgIF9nZXRBdmFpbGFibGVMaXN0KHVybCl7XG4gICAgICAgIGxldCBkZWZlciA9ICQuRGVmZXJyZWQoKTtcblxuICAgICAgICAkLmdldCh1cmwpXG4gICAgICAgICAgICAuZG9uZSgoZGF0YSkgPT4ge1xuICAgICAgICAgICAgICAgIGNvbnN0IGxpc3QgPSAkLnBhcnNlSlNPTihkYXRhKS5saXN0cztcbiAgICAgICAgICAgICAgICBkZWZlci5yZXNvbHZlKGxpc3QpO1xuICAgICAgICAgICAgfSk7XG4gICAgICAgIHJldHVybiBkZWZlci5wcm9taXNlKCk7XG4gICAgfVxuXG4gICAgX3NlbmREaXNhYmxlTGlzdCh1cmwsIGlkKXtcbiAgICAgICAgcmV0dXJuICQuYWpheCh7XG4gICAgICAgICAgICB0eXBlOiBcIlBPU1RcIixcbiAgICAgICAgICAgIHVybDogdXJsLFxuICAgICAgICAgICAgZGF0YToge1xuICAgICAgICAgICAgICAgIGxpc3RfaWQ6IGlkXG4gICAgICAgICAgICB9LFxuICAgICAgICAgICAgZGF0YVR5cGU6IFwianNvblwiXG4gICAgICAgIH0pO1xuICAgIH1cblxuICAgIC8qKlxuICAgICAqIFVwZGF0ZSBJbmZvIGFib3V0IGxpc3RcbiAgICAgKiBAcGFyYW0ge09iamVjdH0gZGF0YVxuICAgICAqIEBwYXJhbSB7U3RyaW5nfSBkYXRhLnVybFxuICAgICAqIEBwYXJhbSB7TnVtYmVyfSBkYXRhLmlkXG4gICAgICogQHBhcmFtIHtPYmplY3R9IGRhdGEuZm9ybURhdGEgLSBvYmplY3Qgd2l0aCBmaWVsZC92YWx1ZVxuICAgICAqIEByZXR1cm5zIHtQcm9taXNlfVxuICAgICAqIEBwcml2YXRlXG4gICAgICovXG4gICAgX3NlbmRVcGRhdGVMaXN0KGRhdGEpe1xuICAgICAgICByZXR1cm4gJC5wb3N0KGRhdGEudXJsLCB7XG4gICAgICAgICAgICBsaXN0X2lkOiBkYXRhLmlkLFxuICAgICAgICAgICAgZGF0YTogZGF0YS5mb3JtRGF0YVxuICAgICAgICB9KTtcbiAgICB9XG5cbiAgICBzdGF0aWMgcGx1Z2luKCRlbGVtcywgb3B0aW9ucykge1xuICAgICAgICBpZiAoISRlbGVtcy5sZW5ndGgpIHJldHVybjtcblxuICAgICAgICByZXR1cm4gJGVsZW1zLmVhY2goZnVuY3Rpb24gKGluZGV4LCBlbCkge1xuICAgICAgICAgICAgbGV0ICRlbGVtZW50ID0gJChlbCk7XG4gICAgICAgICAgICBsZXQgZGF0YSA9ICRlbGVtZW50LmRhdGEoJ3dpZGdldC5pbXBvcnRpbmcnKTtcblxuICAgICAgICAgICAgaWYgKCFkYXRhKSB7XG4gICAgICAgICAgICAgICAgZGF0YSA9IG5ldyBXaWRnZXQoZWwsIG9wdGlvbnMpO1xuICAgICAgICAgICAgICAgICRlbGVtZW50LmRhdGEoJ3dpZGdldC5pbXBvcnRpbmcnLCBkYXRhKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgfSlcbiAgICB9XG59XG5cblxuXG4vKiogV0VCUEFDSyBGT09URVIgKipcbiAqKiAuL2Zyb250ZW5kL2pzL3BlcnNvbi9wZXJzb24td2lkZ2V0cy9faW50ZWdyYXRpb24taW1wb3J0LmpzXG4gKiovIl0sInNvdXJjZVJvb3QiOiIifQ==