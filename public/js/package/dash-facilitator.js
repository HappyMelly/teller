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
	
	var _eventBlock = __webpack_require__(2);
	
	var _eventBlock2 = _interopRequireDefault(_eventBlock);
	
	var _upcomingEvents = __webpack_require__(23);
	
	var _upcomingEvents2 = _interopRequireDefault(_upcomingEvents);
	
	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }
	
	$(function () {
	    _eventBlock2.default.plugin('.js-event-future');
	    _upcomingEvents2.default.plugin('.js-upcoming-events');
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
	            var url = jsRoutes.controllers.Events.confirm(eventId).url;
	            return $.post(url, {});
	        }
	    }, {
	        key: '_sendCancel',
	        value: function _sendCancel(eventId) {
	            var url = jsRoutes.controllers.Events.reason(eventId).url;
	            return $.get(url, {});
	        }
	    }, {
	        key: '_sendAcceptCancel',
	        value: function _sendAcceptCancel(data, id) {
	            var url = jsRoutes.controllers.Events.cancel(id).url;
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
	    function Widget(selector) {
	        (0, _classCallCheck3.default)(this, Widget);
	
	        this.$root = $(selector);
	        this.locals = this._getDom();
	
	        var $firstLink = this.locals.$links.first();
	        this._setInitialValues();
	        this.filterListByLink($firstLink);
	        this._assignEvents();
	    }
	
	    (0, _createClass3.default)(Widget, [{
	        key: '_getDom',
	        value: function _getDom() {
	            return {
	                $links: this.$root.find('[data-upevent-link]'),
	                $total: this.$root.find('[data-upevent-total]'),
	                $now: this.$root.find('[data-upevent-now]'),
	                $items: this.$root.find('.b-eventfut'),
	                $text: this.$root.find('[data-upevent-text]')
	            };
	        }
	    }, {
	        key: '_setInitialValues',
	        value: function _setInitialValues() {
	            var locals = this.locals;
	            var currentCount = locals.$items.filter('.current').length;
	
	            locals.$total.text(locals.$items.length);
	            locals.$now.text(locals.$items.filter('.current').length);
	
	            if (currentCount > 1) {
	                locals.$text.text('are running now');
	            } else {
	                locals.$text.text('is running now');
	            }
	        }
	    }, {
	        key: '_assignEvents',
	        value: function _assignEvents() {
	            this.$root.on('click', '[data-upevent-link]', this._onClickFilter.bind(this));
	        }
	    }, {
	        key: '_onClickFilter',
	        value: function _onClickFilter(e) {
	            e.preventDefault();
	            var $link = $(e.target).closest('[data-upevent-link]');
	
	            if ($link.hasClass('state_active')) return;
	            this.filterListByLink($link);
	        }
	    }, {
	        key: 'filterListByLink',
	        value: function filterListByLink($el) {
	            var locals = this.locals;
	            var filterClass = $el.data('upevent-link');
	            var $filtered = filterClass ? locals.$items.filter('.' + filterClass) : null;
	
	            locals.$items.removeClass('b-eventfut_state_disabled');
	            $filtered && $filtered.addClass('b-eventfut_state_disabled');
	
	            locals.$links.removeClass('state_active');
	            $el.addClass('state_active');
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

/***/ }
/******/ ]);
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIndlYnBhY2s6Ly8vd2VicGFjay9ib290c3RyYXAgMzZhMTVlN2JhYWZjNzU1MGE0OGYiLCJ3ZWJwYWNrOi8vLy4vZnJvbnRlbmQvanMvZGFzaGJvYXJkL2Rhc2gtZmFjaWxpdGF0b3IuanMiLCJ3ZWJwYWNrOi8vLy4vZnJvbnRlbmQvanMvY29tbW9uL19ldmVudC1ibG9jay5qcyIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvaGVscGVycy9jbGFzc0NhbGxDaGVjay5qcyIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvaGVscGVycy9jcmVhdGVDbGFzcy5qcyIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvY29yZS1qcy9vYmplY3QvZGVmaW5lLXByb3BlcnR5LmpzIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9mbi9vYmplY3QvZGVmaW5lLXByb3BlcnR5LmpzIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL2VzNi5vYmplY3QuZGVmaW5lLXByb3BlcnR5LmpzIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19leHBvcnQuanMiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2dsb2JhbC5qcyIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fY29yZS5qcyIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fY3R4LmpzIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19hLWZ1bmN0aW9uLmpzIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19oaWRlLmpzIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19vYmplY3QtZHAuanMiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2FuLW9iamVjdC5qcyIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9faXMtb2JqZWN0LmpzIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19pZTgtZG9tLWRlZmluZS5qcyIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZGVzY3JpcHRvcnMuanMiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2ZhaWxzLmpzIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19kb20tY3JlYXRlLmpzIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL190by1wcmltaXRpdmUuanMiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX3Byb3BlcnR5LWRlc2MuanMiLCJ3ZWJwYWNrOi8vLy4vZnJvbnRlbmQvanMvZGFzaGJvYXJkL3dpZGdldHMvX3VwY29taW5nLWV2ZW50cy5qcyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiO0FBQUE7QUFDQTs7QUFFQTtBQUNBOztBQUVBO0FBQ0E7QUFDQTs7QUFFQTtBQUNBO0FBQ0EsdUJBQWU7QUFDZjtBQUNBO0FBQ0E7O0FBRUE7QUFDQTs7QUFFQTtBQUNBOztBQUVBO0FBQ0E7QUFDQTs7O0FBR0E7QUFDQTs7QUFFQTtB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LFFBQWQsRUFBd0IsSUFBeEIsRUFGTztrQkFBWDtjQUplLENBQW5CLENBSm9COzs7WUE1RFAiLCJmaWxlIjoiZGFzaC1mYWNpbGl0YXRvci5qcyIsInNvdXJjZXNDb250ZW50IjpbIiBcdC8vIFRoZSBtb2R1bGUgY2FjaGVcbiBcdHZhciBpbnN0YWxsZWRNb2R1bGVzID0ge307XG5cbiBcdC8vIFRoZSByZXF1aXJlIGZ1bmN0aW9uXG4gXHRmdW5jdGlvbiBfX3dlYnBhY2tfcmVxdWlyZV9fKG1vZHVsZUlkKSB7XG5cbiBcdFx0Ly8gQ2hlY2sgaWYgbW9kdWxlIGlzIGluIGNhY2hlXG4gXHRcdGlmKGluc3RhbGxlZE1vZHVsZXNbbW9kdWxlSWRdKVxuIFx0XHRcdHJldHVybiBpbnN0YWxsZWRNb2R1bGVzW21vZHVsZUlkXS5leHBvcnRzO1xuXG4gXHRcdC8vIENyZWF0ZSBhIG5ldyBtb2R1bGUgKGFuZCBwdXQgaXQgaW50byB0aGUgY2FjaGUpXG4gXHRcdHZhciBtb2R1bGUgPSBpbnN0YWxsZWRNb2R1bGVzW21vZHVsZUlkXSA9IHtcbiBcdFx0XHRleHBvcnRzOiB7fSxcbiBcdFx0XHRpZDogbW9kdWxlSWQsXG4gXHRcdFx0bG9hZGVkOiBmYWxzZVxuIFx0XHR9O1xuXG4gXHRcdC8vIEV4ZWN1dGUgdGhlIG1vZHVsZSBmdW5jdGlvblxuIFx0XHRtb2R1bGVzW21vZHVsZUlkXS5jYWxsKG1vZHVsZS5leHBvcnRzLCBtb2R1bGUsIG1vZHVsZS5leHBvcnRzLCBfX3dlYnBhY2tfcmVxdWlyZV9fKTtcblxuIFx0XHQvLyBGbGFnIHRoZSBtb2R1bGUgYXMgbG9hZGVkXG4gXHRcdG1vZHVsZS5sb2FkZWQgPSB0cnVlO1xuXG4gXHRcdC8vIFJldHVybiB0aGUgZXhwb3J0cyBvZiB0aGUgbW9kdWxlXG4gXHRcdHJldHVybiBtb2R1bGUuZXhwb3J0cztcbiBcdH1cblxuXG4gXHQvLyBleHBvc2UgdGhlIG1vZHVsZXMgb2JqZWN0IChfX3dlYnBhY2tfbW9kdWxlc19fKVxuIFx0X193ZWJwYWNrX3JlcXVpcmVfXy5tID0gbW9kdWxlcztcblxuIFx0Ly8gZXhwb3NlIHRoZSBtb2R1bGUgY2FjaGVcbiBcdF9fd2VicGFja19yZXF1aXJlX18uYyA9IGluc3RhbGxlZE1vZHVsZXM7XG5cbiBcdC8vIF9fd2VicGFja19wdWJsaWNfcGF0aF9fXG4gXHRfX3dlYnBhY2tfcmVxdWlyZV9fLnAgPSBcIlwiO1xuXG4gXHQvLyBMb2FkIGVudHJ5IG1vZHVsZSBhbmQgcmV0dXJuIGV4cG9ydHNcbiBcdHJldHVybiBfX3dlYnBhY2tfcmVxdWlyZV9fKDApO1xuXG5cblxuLyoqIFdFQlBBQ0sgRk9PVEVSICoqXG4gKiogd2VicGFjay9ib290c3RyYXAgMzZhMTVlN2JhYWZjNzU1MGE0OGZcbiAqKi8iLCIndXNlIHN0cmljdCc7XG5cbmltcG9ydCBFdmVudEJsb2NrIGZyb20gJy4vLi4vY29tbW9uL19ldmVudC1ibG9jayc7XG5pbXBvcnQgVXBjb21pbmdFdmVudHMgZnJvbSAnLi93aWRnZXRzL191cGNvbWluZy1ldmVudHMnO1xuXG4kKGZ1bmN0aW9uICgpIHtcbiAgICBFdmVudEJsb2NrLnBsdWdpbignLmpzLWV2ZW50LWZ1dHVyZScpO1xuICAgIFVwY29taW5nRXZlbnRzLnBsdWdpbignLmpzLXVwY29taW5nLWV2ZW50cycpXG59KTtcblxuXG5cblxuLyoqIFdFQlBBQ0sgRk9PVEVSICoqXG4gKiogLi9mcm9udGVuZC9qcy9kYXNoYm9hcmQvZGFzaC1mYWNpbGl0YXRvci5qc1xuICoqLyIsIid1c2Ugc3RyaWN0JztcblxuXG5leHBvcnQgZGVmYXVsdCBjbGFzcyBXaWRnZXQge1xuICAgIGNvbnN0cnVjdG9yKHNlbGVjdG9yKSB7XG4gICAgICAgIHRoaXMuJHJvb3QgPSAkKHNlbGVjdG9yKTtcbiAgICAgICAgdGhpcy5sb2NhbHMgPSB0aGlzLl9nZXREb20oKTtcbiAgICAgICAgdGhpcy4kY29uZmlybURpYWxvZyA9IG51bGw7XG5cbiAgICAgICAgdGhpcy5fYXNzaWduRXZlbnRzKCk7XG4gICAgfVxuXG4gICAgX2dldERvbSgpIHtcbiAgICAgICAgcmV0dXJuIHtcbiAgICAgICAgICAgICRjb25maXJtOiB0aGlzLiRyb290LmZpbmQoJ1tkYXRhLWV2ZW50LWNvbmZpcm1dJyksXG4gICAgICAgICAgICAkY2FuY2VsOiB0aGlzLiRyb290LmZpbmQoJ1tkYXRhLWV2ZW50LWNhbmNlbF0nKVxuICAgICAgICB9O1xuICAgIH1cblxuICAgIF9hc3NpZ25FdmVudHMoKSB7XG4gICAgICAgIHRoaXMuJHJvb3RcbiAgICAgICAgICAgIC5vbignY2xpY2snLCAnW2RhdGEtZXZlbnQtY29uZmlybV0nLCB0aGlzLl9vbkNsaWNrQ29uZmlybS5iaW5kKHRoaXMpKVxuICAgICAgICAgICAgLm9uKCdjbGljaycsICdbZGF0YS1ldmVudC1jYW5jZWxdJywgdGhpcy5fb25DbGlja0NhbmNlbC5iaW5kKHRoaXMpKVxuICAgICAgICAgICAgLm9uKCdjbGljaycsICcjZXZlbnRDYW5jZWxCdXR0b24nLCB0aGlzLl9vbkNsaWNrQWNjZXB0Q2FuY2VsLmJpbmQodGhpcykpO1xuICAgIH1cblxuICAgIF9vbkNsaWNrQ29uZmlybShlKSB7XG4gICAgICAgIGUucHJldmVudERlZmF1bHQoKTtcbiAgICAgICAgY29uc3Qgc2VsZiA9IHRoaXM7XG4gICAgICAgIGNvbnN0IGV2ZW50SWQgPSBzZWxmLmxvY2Fscy4kY29uZmlybS5kYXRhKCdpZCcpO1xuXG4gICAgICAgIHNlbGYuX3NlbmRDb25maXJtKGV2ZW50SWQpXG4gICAgICAgICAgICAuZG9uZSgoKT0+e1xuICAgICAgICAgICAgICAgIHNlbGYubG9jYWxzLiRjb25maXJtLmFkZENsYXNzKCdkaXNhYmxlZCcpLnRleHQoJ0NvbmZpcm1lZCcpLm9mZignY2xpY2snKTtcbiAgICAgICAgICAgICAgICBzdWNjZXNzKFwiRXZlbnQgd2FzIHN1Y2Nlc3NmdWxseSBjb25maXJtZWRcIik7XG4gICAgICAgICAgICB9KVxuICAgIH1cblxuICAgIF9vbkNsaWNrQ2FuY2VsKGUpIHtcbiAgICAgICAgZS5wcmV2ZW50RGVmYXVsdCgpO1xuICAgICAgICBjb25zdCBzZWxmID0gdGhpcztcbiAgICAgICAgY29uc3QgZXZlbnRJZCA9IHNlbGYubG9jYWxzLiRjYW5jZWwuZGF0YSgnaWQnKTtcblxuICAgICAgICBzZWxmLl9zZW5kQ2FuY2VsKGV2ZW50SWQpXG4gICAgICAgICAgICAuZG9uZSgocmVzcG9uc2UpPT57XG4gICAgICAgICAgICAgICAgc2VsZi4kY29uZmlybURpYWxvZyA9IHNlbGYuX2NyZWF0ZURpYWxvZyhyZXNwb25zZSk7XG4gICAgICAgICAgICAgICAgc2VsZi4kY29uZmlybURpYWxvZy5tb2RhbCgnc2hvdycpO1xuICAgICAgICAgICAgfSlcbiAgICB9XG5cbiAgICBfb25DbGlja0FjY2VwdENhbmNlbChlKXtcbiAgICAgICAgZS5wcmV2ZW50RGVmYXVsdCgpO1xuXG4gICAgICAgIGNvbnN0IHNlbGYgPSB0aGlzO1xuICAgICAgICBjb25zdCBmb3JtRGF0YSA9ICQoXCIjY2FuY2VsRm9ybVwiKS5zZXJpYWxpemUoKTtcbiAgICAgICAgY29uc3QgZXZlbnRJZCA9IHNlbGYubG9jYWxzLiRjYW5jZWwuZGF0YSgnaWQnKTtcblxuICAgICAgICBzZWxmLl9zZW5kQWNjZXB0Q2FuY2VsKGZvcm1EYXRhLCBldmVudElkKVxuICAgICAgICAgICAgLmRvbmUoKCk9PntcbiAgICAgICAgICAgICAgICBzZWxmLiRjb25maXJtRGlhbG9nXG4gICAgICAgICAgICAgICAgICAgIC5vbignaGlkZGVuLmJzLm1vZGFsJywgKCk9PntcbiAgICAgICAgICAgICAgICAgICAgICAgIEFwcC5ldmVudHMucHViKCdobXQuZXZlbnQuY2FuY2VsJyk7XG4gICAgICAgICAgICAgICAgICAgIH0pXG4gICAgICAgICAgICAgICAgICAgIC5tb2RhbCgnaGlkZScpO1xuXG4gICAgICAgICAgICAgICAgc3VjY2VzcyhcIkV2ZW50IHdhcyBzdWNjZXNzZnVsbHkgY2FuY2VsZWRcIik7XG4gICAgICAgICAgICB9KVxuICAgIH1cblxuICAgIF9jcmVhdGVEaWFsb2coY29udGVudCl7XG4gICAgICAgIGNvbnN0IHNlbGVjdG9yID0gJyNjYW5jZWxEaWFsb2cnO1xuICAgICAgICBsZXQgJGRpYWxvZztcblxuICAgICAgICAkKHNlbGVjdG9yKS5yZW1vdmUoKTtcbiAgICAgICAgJGRpYWxvZyA9ICAkKCc8ZGl2IGlkPVwiJyArIHNlbGVjdG9yICsgJ1wiIGNsYXNzPVwiYi1tb2RhbCBtb2RhbCBmYWRlXCIgdGFiaW5kZXg9XCItMVwiPicpXG4gICAgICAgICAgICAuYXR0cigncm9sZScsICdkaWFsb2cnKVxuICAgICAgICAgICAgLmF0dHIoJ2FyaWEtaGlkZGVuJywgJ3RydWUnKVxuICAgICAgICAgICAgLmFwcGVuZChjb250ZW50KTtcblxuICAgICAgICAkZGlhbG9nLmFwcGVuZFRvKHRoaXMuJHJvb3QpO1xuICAgICAgICByZXR1cm4gJGRpYWxvZztcbiAgICB9XG5cbiAgICAvL3RyYW5zcG9ydFxuICAgIF9zZW5kQ29uZmlybShldmVudElkKXtcbiAgICAgICAgY29uc3QgdXJsID0ganNSb3V0ZXMuY29udHJvbGxlcnMuRXZlbnRzLmNvbmZpcm0oZXZlbnRJZCkudXJsO1xuICAgICAgICByZXR1cm4gJC5wb3N0KHVybCwge30pO1xuICAgIH1cblxuICAgIF9zZW5kQ2FuY2VsKGV2ZW50SWQpe1xuICAgICAgICBjb25zdCB1cmwgPSBqc1JvdXRlcy5jb250cm9sbGVycy5FdmVudHMucmVhc29uKGV2ZW50SWQpLnVybDtcbiAgICAgICAgcmV0dXJuICQuZ2V0KHVybCwge30pO1xuICAgIH1cblxuICAgIF9zZW5kQWNjZXB0Q2FuY2VsKGRhdGEsIGlkKXtcbiAgICAgICAgY29uc3QgdXJsID0ganNSb3V0ZXMuY29udHJvbGxlcnMuRXZlbnRzLmNhbmNlbChpZCkudXJsO1xuICAgICAgICByZXR1cm4gJC5wb3N0KHVybCwgZGF0YSk7XG4gICAgfVxuXG4gICAgLy8gc3RhdGljXG4gICAgc3RhdGljIHBsdWdpbihzZWxlY3Rvcikge1xuICAgICAgICBjb25zdCAkZWxlbXMgPSAkKHNlbGVjdG9yKTtcbiAgICAgICAgaWYgKCEkZWxlbXMubGVuZ3RoKSByZXR1cm47XG5cbiAgICAgICAgcmV0dXJuICRlbGVtcy5lYWNoKGZ1bmN0aW9uIChpbmRleCwgZWwpIHtcbiAgICAgICAgICAgIGxldCAkZWxlbWVudCA9ICQoZWwpO1xuICAgICAgICAgICAgbGV0IGRhdGEgICAgID0gJGVsZW1lbnQuZGF0YSgnaG10LmV2ZW50LmJsb2NrJyk7XG5cbiAgICAgICAgICAgIGlmICghZGF0YSkge1xuICAgICAgICAgICAgICAgIGRhdGEgPSBuZXcgV2lkZ2V0KGVsKTtcbiAgICAgICAgICAgICAgICAkZWxlbWVudC5kYXRhKCd3aWRnZXQnLCBkYXRhKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgfSlcbiAgICB9XG59XG5cblxuXG5cbi8qKiBXRUJQQUNLIEZPT1RFUiAqKlxuICoqIC4vZnJvbnRlbmQvanMvY29tbW9uL19ldmVudC1ibG9jay5qc1xuICoqLyIsIlwidXNlIHN0cmljdFwiO1xuXG5leHBvcnRzLl9fZXNNb2R1bGUgPSB0cnVlO1xuXG5leHBvcnRzLmRlZmF1bHQgPSBmdW5jdGlvbiAoaW5zdGFuY2UsIENvbnN0cnVjdG9yKSB7XG4gIGlmICghKGluc3RhbmNlIGluc3RhbmNlb2YgQ29uc3RydWN0b3IpKSB7XG4gICAgdGhyb3cgbmV3IFR5cGVFcnJvcihcIkNhbm5vdCBjYWxsIGEgY2xhc3MgYXMgYSBmdW5jdGlvblwiKTtcbiAgfVxufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL2hlbHBlcnMvY2xhc3NDYWxsQ2hlY2suanNcbiAqKiBtb2R1bGUgaWQgPSAzXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyXG4gKiovIiwiXCJ1c2Ugc3RyaWN0XCI7XG5cbmV4cG9ydHMuX19lc01vZHVsZSA9IHRydWU7XG5cbnZhciBfZGVmaW5lUHJvcGVydHkgPSByZXF1aXJlKFwiYmFiZWwtcnVudGltZS9jb3JlLWpzL29iamVjdC9kZWZpbmUtcHJvcGVydHlcIik7XG5cbnZhciBfZGVmaW5lUHJvcGVydHkyID0gX2ludGVyb3BSZXF1aXJlRGVmYXVsdChfZGVmaW5lUHJvcGVydHkpO1xuXG5mdW5jdGlvbiBfaW50ZXJvcFJlcXVpcmVEZWZhdWx0KG9iaikgeyByZXR1cm4gb2JqICYmIG9iai5fX2VzTW9kdWxlID8gb2JqIDogeyBkZWZhdWx0OiBvYmogfTsgfVxuXG5leHBvcnRzLmRlZmF1bHQgPSBmdW5jdGlvbiAoKSB7XG4gIGZ1bmN0aW9uIGRlZmluZVByb3BlcnRpZXModGFyZ2V0LCBwcm9wcykge1xuICAgIGZvciAodmFyIGkgPSAwOyBpIDwgcHJvcHMubGVuZ3RoOyBpKyspIHtcbiAgICAgIHZhciBkZXNjcmlwdG9yID0gcHJvcHNbaV07XG4gICAgICBkZXNjcmlwdG9yLmVudW1lcmFibGUgPSBkZXNjcmlwdG9yLmVudW1lcmFibGUgfHwgZmFsc2U7XG4gICAgICBkZXNjcmlwdG9yLmNvbmZpZ3VyYWJsZSA9IHRydWU7XG4gICAgICBpZiAoXCJ2YWx1ZVwiIGluIGRlc2NyaXB0b3IpIGRlc2NyaXB0b3Iud3JpdGFibGUgPSB0cnVlO1xuICAgICAgKDAsIF9kZWZpbmVQcm9wZXJ0eTIuZGVmYXVsdCkodGFyZ2V0LCBkZXNjcmlwdG9yLmtleSwgZGVzY3JpcHRvcik7XG4gICAgfVxuICB9XG5cbiAgcmV0dXJuIGZ1bmN0aW9uIChDb25zdHJ1Y3RvciwgcHJvdG9Qcm9wcywgc3RhdGljUHJvcHMpIHtcbiAgICBpZiAocHJvdG9Qcm9wcykgZGVmaW5lUHJvcGVydGllcyhDb25zdHJ1Y3Rvci5wcm90b3R5cGUsIHByb3RvUHJvcHMpO1xuICAgIGlmIChzdGF0aWNQcm9wcykgZGVmaW5lUHJvcGVydGllcyhDb25zdHJ1Y3Rvciwgc3RhdGljUHJvcHMpO1xuICAgIHJldHVybiBDb25zdHJ1Y3RvcjtcbiAgfTtcbn0oKTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL2hlbHBlcnMvY3JlYXRlQ2xhc3MuanNcbiAqKiBtb2R1bGUgaWQgPSA0XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyXG4gKiovIiwibW9kdWxlLmV4cG9ydHMgPSB7IFwiZGVmYXVsdFwiOiByZXF1aXJlKFwiY29yZS1qcy9saWJyYXJ5L2ZuL29iamVjdC9kZWZpbmUtcHJvcGVydHlcIiksIF9fZXNNb2R1bGU6IHRydWUgfTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL2NvcmUtanMvb2JqZWN0L2RlZmluZS1wcm9wZXJ0eS5qc1xuICoqIG1vZHVsZSBpZCA9IDVcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDJcbiAqKi8iLCJyZXF1aXJlKCcuLi8uLi9tb2R1bGVzL2VzNi5vYmplY3QuZGVmaW5lLXByb3BlcnR5Jyk7XG52YXIgJE9iamVjdCA9IHJlcXVpcmUoJy4uLy4uL21vZHVsZXMvX2NvcmUnKS5PYmplY3Q7XG5tb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uIGRlZmluZVByb3BlcnR5KGl0LCBrZXksIGRlc2Mpe1xuICByZXR1cm4gJE9iamVjdC5kZWZpbmVQcm9wZXJ0eShpdCwga2V5LCBkZXNjKTtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9mbi9vYmplY3QvZGVmaW5lLXByb3BlcnR5LmpzXG4gKiogbW9kdWxlIGlkID0gNlxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMlxuICoqLyIsInZhciAkZXhwb3J0ID0gcmVxdWlyZSgnLi9fZXhwb3J0Jyk7XHJcbi8vIDE5LjEuMi40IC8gMTUuMi4zLjYgT2JqZWN0LmRlZmluZVByb3BlcnR5KE8sIFAsIEF0dHJpYnV0ZXMpXHJcbiRleHBvcnQoJGV4cG9ydC5TICsgJGV4cG9ydC5GICogIXJlcXVpcmUoJy4vX2Rlc2NyaXB0b3JzJyksICdPYmplY3QnLCB7ZGVmaW5lUHJvcGVydHk6IHJlcXVpcmUoJy4vX29iamVjdC1kcCcpLmZ9KTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvZXM2Lm9iamVjdC5kZWZpbmUtcHJvcGVydHkuanNcbiAqKiBtb2R1bGUgaWQgPSA3XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyXG4gKiovIiwidmFyIGdsb2JhbCAgICA9IHJlcXVpcmUoJy4vX2dsb2JhbCcpXG4gICwgY29yZSAgICAgID0gcmVxdWlyZSgnLi9fY29yZScpXG4gICwgY3R4ICAgICAgID0gcmVxdWlyZSgnLi9fY3R4JylcbiAgLCBoaWRlICAgICAgPSByZXF1aXJlKCcuL19oaWRlJylcbiAgLCBQUk9UT1RZUEUgPSAncHJvdG90eXBlJztcblxudmFyICRleHBvcnQgPSBmdW5jdGlvbih0eXBlLCBuYW1lLCBzb3VyY2Upe1xuICB2YXIgSVNfRk9SQ0VEID0gdHlwZSAmICRleHBvcnQuRlxuICAgICwgSVNfR0xPQkFMID0gdHlwZSAmICRleHBvcnQuR1xuICAgICwgSVNfU1RBVElDID0gdHlwZSAmICRleHBvcnQuU1xuICAgICwgSVNfUFJPVE8gID0gdHlwZSAmICRleHBvcnQuUFxuICAgICwgSVNfQklORCAgID0gdHlwZSAmICRleHBvcnQuQlxuICAgICwgSVNfV1JBUCAgID0gdHlwZSAmICRleHBvcnQuV1xuICAgICwgZXhwb3J0cyAgID0gSVNfR0xPQkFMID8gY29yZSA6IGNvcmVbbmFtZV0gfHwgKGNvcmVbbmFtZV0gPSB7fSlcbiAgICAsIGV4cFByb3RvICA9IGV4cG9ydHNbUFJPVE9UWVBFXVxuICAgICwgdGFyZ2V0ICAgID0gSVNfR0xPQkFMID8gZ2xvYmFsIDogSVNfU1RBVElDID8gZ2xvYmFsW25hbWVdIDogKGdsb2JhbFtuYW1lXSB8fCB7fSlbUFJPVE9UWVBFXVxuICAgICwga2V5LCBvd24sIG91dDtcbiAgaWYoSVNfR0xPQkFMKXNvdXJjZSA9IG5hbWU7XG4gIGZvcihrZXkgaW4gc291cmNlKXtcbiAgICAvLyBjb250YWlucyBpbiBuYXRpdmVcbiAgICBvd24gPSAhSVNfRk9SQ0VEICYmIHRhcmdldCAmJiB0YXJnZXRba2V5XSAhPT0gdW5kZWZpbmVkO1xuICAgIGlmKG93biAmJiBrZXkgaW4gZXhwb3J0cyljb250aW51ZTtcbiAgICAvLyBleHBvcnQgbmF0aXZlIG9yIHBhc3NlZFxuICAgIG91dCA9IG93biA/IHRhcmdldFtrZXldIDogc291cmNlW2tleV07XG4gICAgLy8gcHJldmVudCBnbG9iYWwgcG9sbHV0aW9uIGZvciBuYW1lc3BhY2VzXG4gICAgZXhwb3J0c1trZXldID0gSVNfR0xPQkFMICYmIHR5cGVvZiB0YXJnZXRba2V5XSAhPSAnZnVuY3Rpb24nID8gc291cmNlW2tleV1cbiAgICAvLyBiaW5kIHRpbWVycyB0byBnbG9iYWwgZm9yIGNhbGwgZnJvbSBleHBvcnQgY29udGV4dFxuICAgIDogSVNfQklORCAmJiBvd24gPyBjdHgob3V0LCBnbG9iYWwpXG4gICAgLy8gd3JhcCBnbG9iYWwgY29uc3RydWN0b3JzIGZvciBwcmV2ZW50IGNoYW5nZSB0aGVtIGluIGxpYnJhcnlcbiAgICA6IElTX1dSQVAgJiYgdGFyZ2V0W2tleV0gPT0gb3V0ID8gKGZ1bmN0aW9uKEMpe1xuICAgICAgdmFyIEYgPSBmdW5jdGlvbihhLCBiLCBjKXtcbiAgICAgICAgaWYodGhpcyBpbnN0YW5jZW9mIEMpe1xuICAgICAgICAgIHN3aXRjaChhcmd1bWVudHMubGVuZ3RoKXtcbiAgICAgICAgICAgIGNhc2UgMDogcmV0dXJuIG5ldyBDO1xuICAgICAgICAgICAgY2FzZSAxOiByZXR1cm4gbmV3IEMoYSk7XG4gICAgICAgICAgICBjYXNlIDI6IHJldHVybiBuZXcgQyhhLCBiKTtcbiAgICAgICAgICB9IHJldHVybiBuZXcgQyhhLCBiLCBjKTtcbiAgICAgICAgfSByZXR1cm4gQy5hcHBseSh0aGlzLCBhcmd1bWVudHMpO1xuICAgICAgfTtcbiAgICAgIEZbUFJPVE9UWVBFXSA9IENbUFJPVE9UWVBFXTtcbiAgICAgIHJldHVybiBGO1xuICAgIC8vIG1ha2Ugc3RhdGljIHZlcnNpb25zIGZvciBwcm90b3R5cGUgbWV0aG9kc1xuICAgIH0pKG91dCkgOiBJU19QUk9UTyAmJiB0eXBlb2Ygb3V0ID09ICdmdW5jdGlvbicgPyBjdHgoRnVuY3Rpb24uY2FsbCwgb3V0KSA6IG91dDtcbiAgICAvLyBleHBvcnQgcHJvdG8gbWV0aG9kcyB0byBjb3JlLiVDT05TVFJVQ1RPUiUubWV0aG9kcy4lTkFNRSVcbiAgICBpZihJU19QUk9UTyl7XG4gICAgICAoZXhwb3J0cy52aXJ0dWFsIHx8IChleHBvcnRzLnZpcnR1YWwgPSB7fSkpW2tleV0gPSBvdXQ7XG4gICAgICAvLyBleHBvcnQgcHJvdG8gbWV0aG9kcyB0byBjb3JlLiVDT05TVFJVQ1RPUiUucHJvdG90eXBlLiVOQU1FJVxuICAgICAgaWYodHlwZSAmICRleHBvcnQuUiAmJiBleHBQcm90byAmJiAhZXhwUHJvdG9ba2V5XSloaWRlKGV4cFByb3RvLCBrZXksIG91dCk7XG4gICAgfVxuICB9XG59O1xuLy8gdHlwZSBiaXRtYXBcbiRleHBvcnQuRiA9IDE7ICAgLy8gZm9yY2VkXG4kZXhwb3J0LkcgPSAyOyAgIC8vIGdsb2JhbFxuJGV4cG9ydC5TID0gNDsgICAvLyBzdGF0aWNcbiRleHBvcnQuUCA9IDg7ICAgLy8gcHJvdG9cbiRleHBvcnQuQiA9IDE2OyAgLy8gYmluZFxuJGV4cG9ydC5XID0gMzI7ICAvLyB3cmFwXG4kZXhwb3J0LlUgPSA2NDsgIC8vIHNhZmVcbiRleHBvcnQuUiA9IDEyODsgLy8gcmVhbCBwcm90byBtZXRob2QgZm9yIGBsaWJyYXJ5YCBcbm1vZHVsZS5leHBvcnRzID0gJGV4cG9ydDtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2V4cG9ydC5qc1xuICoqIG1vZHVsZSBpZCA9IDhcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDJcbiAqKi8iLCIvLyBodHRwczovL2dpdGh1Yi5jb20vemxvaXJvY2svY29yZS1qcy9pc3N1ZXMvODYjaXNzdWVjb21tZW50LTExNTc1OTAyOFxudmFyIGdsb2JhbCA9IG1vZHVsZS5leHBvcnRzID0gdHlwZW9mIHdpbmRvdyAhPSAndW5kZWZpbmVkJyAmJiB3aW5kb3cuTWF0aCA9PSBNYXRoXG4gID8gd2luZG93IDogdHlwZW9mIHNlbGYgIT0gJ3VuZGVmaW5lZCcgJiYgc2VsZi5NYXRoID09IE1hdGggPyBzZWxmIDogRnVuY3Rpb24oJ3JldHVybiB0aGlzJykoKTtcbmlmKHR5cGVvZiBfX2cgPT0gJ251bWJlcicpX19nID0gZ2xvYmFsOyAvLyBlc2xpbnQtZGlzYWJsZS1saW5lIG5vLXVuZGVmXG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19nbG9iYWwuanNcbiAqKiBtb2R1bGUgaWQgPSA5XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyXG4gKiovIiwidmFyIGNvcmUgPSBtb2R1bGUuZXhwb3J0cyA9IHt2ZXJzaW9uOiAnMi4yLjAnfTtcbmlmKHR5cGVvZiBfX2UgPT0gJ251bWJlcicpX19lID0gY29yZTsgLy8gZXNsaW50LWRpc2FibGUtbGluZSBuby11bmRlZlxuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fY29yZS5qc1xuICoqIG1vZHVsZSBpZCA9IDEwXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyXG4gKiovIiwiLy8gb3B0aW9uYWwgLyBzaW1wbGUgY29udGV4dCBiaW5kaW5nXG52YXIgYUZ1bmN0aW9uID0gcmVxdWlyZSgnLi9fYS1mdW5jdGlvbicpO1xubW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihmbiwgdGhhdCwgbGVuZ3RoKXtcbiAgYUZ1bmN0aW9uKGZuKTtcbiAgaWYodGhhdCA9PT0gdW5kZWZpbmVkKXJldHVybiBmbjtcbiAgc3dpdGNoKGxlbmd0aCl7XG4gICAgY2FzZSAxOiByZXR1cm4gZnVuY3Rpb24oYSl7XG4gICAgICByZXR1cm4gZm4uY2FsbCh0aGF0LCBhKTtcbiAgICB9O1xuICAgIGNhc2UgMjogcmV0dXJuIGZ1bmN0aW9uKGEsIGIpe1xuICAgICAgcmV0dXJuIGZuLmNhbGwodGhhdCwgYSwgYik7XG4gICAgfTtcbiAgICBjYXNlIDM6IHJldHVybiBmdW5jdGlvbihhLCBiLCBjKXtcbiAgICAgIHJldHVybiBmbi5jYWxsKHRoYXQsIGEsIGIsIGMpO1xuICAgIH07XG4gIH1cbiAgcmV0dXJuIGZ1bmN0aW9uKC8qIC4uLmFyZ3MgKi8pe1xuICAgIHJldHVybiBmbi5hcHBseSh0aGF0LCBhcmd1bWVudHMpO1xuICB9O1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2N0eC5qc1xuICoqIG1vZHVsZSBpZCA9IDExXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyXG4gKiovIiwibW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihpdCl7XG4gIGlmKHR5cGVvZiBpdCAhPSAnZnVuY3Rpb24nKXRocm93IFR5cGVFcnJvcihpdCArICcgaXMgbm90IGEgZnVuY3Rpb24hJyk7XG4gIHJldHVybiBpdDtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19hLWZ1bmN0aW9uLmpzXG4gKiogbW9kdWxlIGlkID0gMTJcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDJcbiAqKi8iLCJ2YXIgZFAgICAgICAgICA9IHJlcXVpcmUoJy4vX29iamVjdC1kcCcpXG4gICwgY3JlYXRlRGVzYyA9IHJlcXVpcmUoJy4vX3Byb3BlcnR5LWRlc2MnKTtcbm1vZHVsZS5leHBvcnRzID0gcmVxdWlyZSgnLi9fZGVzY3JpcHRvcnMnKSA/IGZ1bmN0aW9uKG9iamVjdCwga2V5LCB2YWx1ZSl7XG4gIHJldHVybiBkUC5mKG9iamVjdCwga2V5LCBjcmVhdGVEZXNjKDEsIHZhbHVlKSk7XG59IDogZnVuY3Rpb24ob2JqZWN0LCBrZXksIHZhbHVlKXtcbiAgb2JqZWN0W2tleV0gPSB2YWx1ZTtcbiAgcmV0dXJuIG9iamVjdDtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19oaWRlLmpzXG4gKiogbW9kdWxlIGlkID0gMTNcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDJcbiAqKi8iLCJ2YXIgYW5PYmplY3QgICAgICAgPSByZXF1aXJlKCcuL19hbi1vYmplY3QnKVxuICAsIElFOF9ET01fREVGSU5FID0gcmVxdWlyZSgnLi9faWU4LWRvbS1kZWZpbmUnKVxuICAsIHRvUHJpbWl0aXZlICAgID0gcmVxdWlyZSgnLi9fdG8tcHJpbWl0aXZlJylcbiAgLCBkUCAgICAgICAgICAgICA9IE9iamVjdC5kZWZpbmVQcm9wZXJ0eTtcblxuZXhwb3J0cy5mID0gcmVxdWlyZSgnLi9fZGVzY3JpcHRvcnMnKSA/IE9iamVjdC5kZWZpbmVQcm9wZXJ0eSA6IGZ1bmN0aW9uIGRlZmluZVByb3BlcnR5KE8sIFAsIEF0dHJpYnV0ZXMpe1xuICBhbk9iamVjdChPKTtcbiAgUCA9IHRvUHJpbWl0aXZlKFAsIHRydWUpO1xuICBhbk9iamVjdChBdHRyaWJ1dGVzKTtcbiAgaWYoSUU4X0RPTV9ERUZJTkUpdHJ5IHtcbiAgICByZXR1cm4gZFAoTywgUCwgQXR0cmlidXRlcyk7XG4gIH0gY2F0Y2goZSl7IC8qIGVtcHR5ICovIH1cbiAgaWYoJ2dldCcgaW4gQXR0cmlidXRlcyB8fCAnc2V0JyBpbiBBdHRyaWJ1dGVzKXRocm93IFR5cGVFcnJvcignQWNjZXNzb3JzIG5vdCBzdXBwb3J0ZWQhJyk7XG4gIGlmKCd2YWx1ZScgaW4gQXR0cmlidXRlcylPW1BdID0gQXR0cmlidXRlcy52YWx1ZTtcbiAgcmV0dXJuIE87XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fb2JqZWN0LWRwLmpzXG4gKiogbW9kdWxlIGlkID0gMTRcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDJcbiAqKi8iLCJ2YXIgaXNPYmplY3QgPSByZXF1aXJlKCcuL19pcy1vYmplY3QnKTtcbm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oaXQpe1xuICBpZighaXNPYmplY3QoaXQpKXRocm93IFR5cGVFcnJvcihpdCArICcgaXMgbm90IGFuIG9iamVjdCEnKTtcbiAgcmV0dXJuIGl0O1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2FuLW9iamVjdC5qc1xuICoqIG1vZHVsZSBpZCA9IDE1XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyXG4gKiovIiwibW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihpdCl7XG4gIHJldHVybiB0eXBlb2YgaXQgPT09ICdvYmplY3QnID8gaXQgIT09IG51bGwgOiB0eXBlb2YgaXQgPT09ICdmdW5jdGlvbic7XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9faXMtb2JqZWN0LmpzXG4gKiogbW9kdWxlIGlkID0gMTZcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDJcbiAqKi8iLCJtb2R1bGUuZXhwb3J0cyA9ICFyZXF1aXJlKCcuL19kZXNjcmlwdG9ycycpICYmICFyZXF1aXJlKCcuL19mYWlscycpKGZ1bmN0aW9uKCl7XHJcbiAgcmV0dXJuIE9iamVjdC5kZWZpbmVQcm9wZXJ0eShyZXF1aXJlKCcuL19kb20tY3JlYXRlJykoJ2RpdicpLCAnYScsIHtnZXQ6IGZ1bmN0aW9uKCl7IHJldHVybiA3OyB9fSkuYSAhPSA3O1xyXG59KTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2llOC1kb20tZGVmaW5lLmpzXG4gKiogbW9kdWxlIGlkID0gMTdcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDJcbiAqKi8iLCIvLyBUaGFuaydzIElFOCBmb3IgaGlzIGZ1bm55IGRlZmluZVByb3BlcnR5XG5tb2R1bGUuZXhwb3J0cyA9ICFyZXF1aXJlKCcuL19mYWlscycpKGZ1bmN0aW9uKCl7XG4gIHJldHVybiBPYmplY3QuZGVmaW5lUHJvcGVydHkoe30sICdhJywge2dldDogZnVuY3Rpb24oKXsgcmV0dXJuIDc7IH19KS5hICE9IDc7XG59KTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2Rlc2NyaXB0b3JzLmpzXG4gKiogbW9kdWxlIGlkID0gMThcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDJcbiAqKi8iLCJtb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGV4ZWMpe1xuICB0cnkge1xuICAgIHJldHVybiAhIWV4ZWMoKTtcbiAgfSBjYXRjaChlKXtcbiAgICByZXR1cm4gdHJ1ZTtcbiAgfVxufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2ZhaWxzLmpzXG4gKiogbW9kdWxlIGlkID0gMTlcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDJcbiAqKi8iLCJ2YXIgaXNPYmplY3QgPSByZXF1aXJlKCcuL19pcy1vYmplY3QnKVxuICAsIGRvY3VtZW50ID0gcmVxdWlyZSgnLi9fZ2xvYmFsJykuZG9jdW1lbnRcbiAgLy8gaW4gb2xkIElFIHR5cGVvZiBkb2N1bWVudC5jcmVhdGVFbGVtZW50IGlzICdvYmplY3QnXG4gICwgaXMgPSBpc09iamVjdChkb2N1bWVudCkgJiYgaXNPYmplY3QoZG9jdW1lbnQuY3JlYXRlRWxlbWVudCk7XG5tb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGl0KXtcbiAgcmV0dXJuIGlzID8gZG9jdW1lbnQuY3JlYXRlRWxlbWVudChpdCkgOiB7fTtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19kb20tY3JlYXRlLmpzXG4gKiogbW9kdWxlIGlkID0gMjBcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDJcbiAqKi8iLCIvLyA3LjEuMSBUb1ByaW1pdGl2ZShpbnB1dCBbLCBQcmVmZXJyZWRUeXBlXSlcbnZhciBpc09iamVjdCA9IHJlcXVpcmUoJy4vX2lzLW9iamVjdCcpO1xuLy8gaW5zdGVhZCBvZiB0aGUgRVM2IHNwZWMgdmVyc2lvbiwgd2UgZGlkbid0IGltcGxlbWVudCBAQHRvUHJpbWl0aXZlIGNhc2Vcbi8vIGFuZCB0aGUgc2Vjb25kIGFyZ3VtZW50IC0gZmxhZyAtIHByZWZlcnJlZCB0eXBlIGlzIGEgc3RyaW5nXG5tb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGl0LCBTKXtcbiAgaWYoIWlzT2JqZWN0KGl0KSlyZXR1cm4gaXQ7XG4gIHZhciBmbiwgdmFsO1xuICBpZihTICYmIHR5cGVvZiAoZm4gPSBpdC50b1N0cmluZykgPT0gJ2Z1bmN0aW9uJyAmJiAhaXNPYmplY3QodmFsID0gZm4uY2FsbChpdCkpKXJldHVybiB2YWw7XG4gIGlmKHR5cGVvZiAoZm4gPSBpdC52YWx1ZU9mKSA9PSAnZnVuY3Rpb24nICYmICFpc09iamVjdCh2YWwgPSBmbi5jYWxsKGl0KSkpcmV0dXJuIHZhbDtcbiAgaWYoIVMgJiYgdHlwZW9mIChmbiA9IGl0LnRvU3RyaW5nKSA9PSAnZnVuY3Rpb24nICYmICFpc09iamVjdCh2YWwgPSBmbi5jYWxsKGl0KSkpcmV0dXJuIHZhbDtcbiAgdGhyb3cgVHlwZUVycm9yKFwiQ2FuJ3QgY29udmVydCBvYmplY3QgdG8gcHJpbWl0aXZlIHZhbHVlXCIpO1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX3RvLXByaW1pdGl2ZS5qc1xuICoqIG1vZHVsZSBpZCA9IDIxXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyXG4gKiovIiwibW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihiaXRtYXAsIHZhbHVlKXtcbiAgcmV0dXJuIHtcbiAgICBlbnVtZXJhYmxlICA6ICEoYml0bWFwICYgMSksXG4gICAgY29uZmlndXJhYmxlOiAhKGJpdG1hcCAmIDIpLFxuICAgIHdyaXRhYmxlICAgIDogIShiaXRtYXAgJiA0KSxcbiAgICB2YWx1ZSAgICAgICA6IHZhbHVlXG4gIH07XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fcHJvcGVydHktZGVzYy5qc1xuICoqIG1vZHVsZSBpZCA9IDIyXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyXG4gKiovIiwiJ3VzZSBzdHJpY3QnO1xuXG5leHBvcnQgZGVmYXVsdCBjbGFzcyBXaWRnZXQge1xuICAgIGNvbnN0cnVjdG9yKHNlbGVjdG9yKSB7XG4gICAgICAgIHRoaXMuJHJvb3QgPSAkKHNlbGVjdG9yKTtcbiAgICAgICAgdGhpcy5sb2NhbHMgPSB0aGlzLl9nZXREb20oKTtcblxuICAgICAgICBjb25zdCAkZmlyc3RMaW5rID0gdGhpcy5sb2NhbHMuJGxpbmtzLmZpcnN0KCk7XG4gICAgICAgIHRoaXMuX3NldEluaXRpYWxWYWx1ZXMoKTtcbiAgICAgICAgdGhpcy5maWx0ZXJMaXN0QnlMaW5rKCRmaXJzdExpbmspO1xuICAgICAgICB0aGlzLl9hc3NpZ25FdmVudHMoKTtcbiAgICB9XG5cbiAgICBfZ2V0RG9tKCkge1xuICAgICAgICByZXR1cm4ge1xuICAgICAgICAgICAgJGxpbmtzOiB0aGlzLiRyb290LmZpbmQoJ1tkYXRhLXVwZXZlbnQtbGlua10nKSxcbiAgICAgICAgICAgICR0b3RhbDogdGhpcy4kcm9vdC5maW5kKCdbZGF0YS11cGV2ZW50LXRvdGFsXScpLFxuICAgICAgICAgICAgJG5vdzogdGhpcy4kcm9vdC5maW5kKCdbZGF0YS11cGV2ZW50LW5vd10nKSxcbiAgICAgICAgICAgICRpdGVtczogdGhpcy4kcm9vdC5maW5kKCcuYi1ldmVudGZ1dCcpLFxuICAgICAgICAgICAgJHRleHQ6IHRoaXMuJHJvb3QuZmluZCgnW2RhdGEtdXBldmVudC10ZXh0XScpXG4gICAgICAgIH07XG4gICAgfVxuXG4gICAgX3NldEluaXRpYWxWYWx1ZXMoKXtcbiAgICAgICAgY29uc3QgbG9jYWxzID0gdGhpcy5sb2NhbHM7XG4gICAgICAgIGNvbnN0IGN1cnJlbnRDb3VudCA9IGxvY2Fscy4kaXRlbXMuZmlsdGVyKCcuY3VycmVudCcpLmxlbmd0aDtcblxuICAgICAgICBsb2NhbHMuJHRvdGFsLnRleHQobG9jYWxzLiRpdGVtcy5sZW5ndGgpO1xuICAgICAgICBsb2NhbHMuJG5vdy50ZXh0KGxvY2Fscy4kaXRlbXMuZmlsdGVyKCcuY3VycmVudCcpLmxlbmd0aCk7XG5cbiAgICAgICAgaWYgKGN1cnJlbnRDb3VudCA+IDEpe1xuICAgICAgICAgICAgbG9jYWxzLiR0ZXh0LnRleHQoJ2FyZSBydW5uaW5nIG5vdycpO1xuICAgICAgICB9IGVsc2Uge1xuICAgICAgICAgICAgbG9jYWxzLiR0ZXh0LnRleHQoJ2lzIHJ1bm5pbmcgbm93Jyk7XG4gICAgICAgIH1cbiAgICB9XG5cbiAgICBfYXNzaWduRXZlbnRzKCkge1xuICAgICAgICB0aGlzLiRyb290Lm9uKCdjbGljaycsICdbZGF0YS11cGV2ZW50LWxpbmtdJywgdGhpcy5fb25DbGlja0ZpbHRlci5iaW5kKHRoaXMpKTtcbiAgICB9XG5cbiAgICBfb25DbGlja0ZpbHRlcihlKXtcbiAgICAgICAgZS5wcmV2ZW50RGVmYXVsdCgpO1xuICAgICAgICBjb25zdCAkbGluayA9ICQoZS50YXJnZXQpLmNsb3Nlc3QoJ1tkYXRhLXVwZXZlbnQtbGlua10nKTtcblxuICAgICAgICBpZiAoJGxpbmsuaGFzQ2xhc3MoJ3N0YXRlX2FjdGl2ZScpKSByZXR1cm47XG4gICAgICAgIHRoaXMuZmlsdGVyTGlzdEJ5TGluaygkbGluayk7XG4gICAgfVxuXG4gICAgZmlsdGVyTGlzdEJ5TGluaygkZWwpe1xuICAgICAgICBjb25zdCBsb2NhbHMgPSB0aGlzLmxvY2FscztcbiAgICAgICAgY29uc3QgZmlsdGVyQ2xhc3MgPSAkZWwuZGF0YSgndXBldmVudC1saW5rJyk7XG4gICAgICAgIGNvbnN0ICRmaWx0ZXJlZCA9IGZpbHRlckNsYXNzPyBsb2NhbHMuJGl0ZW1zLmZpbHRlcignLicgKyBmaWx0ZXJDbGFzcyk6IG51bGwgO1xuXG4gICAgICAgIGxvY2Fscy4kaXRlbXMucmVtb3ZlQ2xhc3MoJ2ItZXZlbnRmdXRfc3RhdGVfZGlzYWJsZWQnKTtcbiAgICAgICAgJGZpbHRlcmVkICYmICRmaWx0ZXJlZC5hZGRDbGFzcygnYi1ldmVudGZ1dF9zdGF0ZV9kaXNhYmxlZCcpO1xuXG4gICAgICAgIGxvY2Fscy4kbGlua3MucmVtb3ZlQ2xhc3MoJ3N0YXRlX2FjdGl2ZScpO1xuICAgICAgICAkZWwuYWRkQ2xhc3MoJ3N0YXRlX2FjdGl2ZScpO1xuICAgIH1cblxuICAgIC8vIHN0YXRpY1xuICAgIHN0YXRpYyBwbHVnaW4oc2VsZWN0b3IpIHtcbiAgICAgICAgY29uc3QgJGVsZW1zID0gJChzZWxlY3Rvcik7XG4gICAgICAgIGlmICghJGVsZW1zLmxlbmd0aCkgcmV0dXJuO1xuXG4gICAgICAgIHJldHVybiAkZWxlbXMuZWFjaChmdW5jdGlvbiAoaW5kZXgsIGVsKSB7XG4gICAgICAgICAgICBsZXQgJGVsZW1lbnQgPSAkKGVsKTtcbiAgICAgICAgICAgIGxldCBkYXRhICAgICA9ICRlbGVtZW50LmRhdGEoJ2htdC5ldmVudHMudXBjb21pbmcnKTtcblxuICAgICAgICAgICAgaWYgKCFkYXRhKSB7XG4gICAgICAgICAgICAgICAgZGF0YSA9IG5ldyBXaWRnZXQoZWwpO1xuICAgICAgICAgICAgICAgICRlbGVtZW50LmRhdGEoJ3dpZGdldCcsIGRhdGEpO1xuICAgICAgICAgICAgfVxuICAgICAgICB9KVxuICAgIH1cbn1cblxuXG5cblxuLyoqIFdFQlBBQ0sgRk9PVEVSICoqXG4gKiogLi9mcm9udGVuZC9qcy9kYXNoYm9hcmQvd2lkZ2V0cy9fdXBjb21pbmctZXZlbnRzLmpzXG4gKiovIl0sInNvdXJjZVJvb3QiOiIifQ==