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

	module.exports = __webpack_require__(33);


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
/* 33 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';
	
	var _eventBlock = __webpack_require__(34);
	
	var _eventBlock2 = _interopRequireDefault(_eventBlock);
	
	var _eventSchedule = __webpack_require__(35);
	
	var _eventSchedule2 = _interopRequireDefault(_eventSchedule);
	
	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }
	
	$(function () {
	    _eventBlock2.default.plugin('.js-event-future');
	    _eventSchedule2.default.plugin('.js-upcoming-events');
	});

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
	                $text: this.$root.find('[data-upevent-text]'),
	                $switcher: this.$root.find('[data-schedule-switcher]')
	            };
	        }
	    }, {
	        key: '_setInitialValues',
	        value: function _setInitialValues() {
	            var locals = this.locals;
	            var currentCount = locals.$items.filter('.current').length;
	
	            locals.$total.text(locals.$items.length);
	            locals.$now.text(locals.$items.filter('.current').length);
	
	            switch (currentCount) {
	                case 0:
	                    locals.$switcher.hide();
	                    break;
	                case 1:
	                    locals.$text.text('is running now');
	                    break;
	                default:
	                    locals.$text.text('are running now');
	                    break;
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
	            if ($filtered) {
	                locals.$items.addClass('b-eventfut_state_disabled');
	                $filtered.removeClass('b-eventfut_state_disabled');
	            }
	
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
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIndlYnBhY2s6Ly8vd2VicGFjay9ib290c3RyYXAgYTNlODM1N2MwMzkxODU2OTU4MDE/MWE3OCoqIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9oZWxwZXJzL2NsYXNzQ2FsbENoZWNrLmpzPzIxYWYqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvaGVscGVycy9jcmVhdGVDbGFzcy5qcz8xZGZlKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL2NvcmUtanMvb2JqZWN0L2RlZmluZS1wcm9wZXJ0eS5qcz81ZjcwKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L2ZuL29iamVjdC9kZWZpbmUtcHJvcGVydHkuanM/NGQzMyoqIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL2VzNi5vYmplY3QuZGVmaW5lLXByb3BlcnR5LmpzPzhiZGUqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZXhwb3J0LmpzPzhlNDAqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZ2xvYmFsLmpzPzNjNTIqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fY29yZS5qcz9kNjExKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2N0eC5qcz80ZTU5KioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2EtZnVuY3Rpb24uanM/MDY5OSoqIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19oaWRlLmpzPzBkMmUqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fb2JqZWN0LWRwLmpzPzU5ODYqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fYW4tb2JqZWN0LmpzPzNhZjIqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9faXMtb2JqZWN0LmpzP2NmZGEqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9faWU4LWRvbS1kZWZpbmUuanM/YjEwMioqIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19kZXNjcmlwdG9ycy5qcz9jMGY1KioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2ZhaWxzLmpzP2M2ZGQqKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZG9tLWNyZWF0ZS5qcz82MTJmKioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX3RvLXByaW1pdGl2ZS5qcz8xYTY1KioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX3Byb3BlcnR5LWRlc2MuanM/MjU2YioqIiwid2VicGFjazovLy8uL2Zyb250ZW5kL2pzL2Rhc2hib2FyZC9kYXNoLWZhY2lsaXRhdG9yLmpzIiwid2VicGFjazovLy8uL2Zyb250ZW5kL2pzL2NvbW1vbi9fZXZlbnQtYmxvY2suanMiLCJ3ZWJwYWNrOi8vLy4vZnJvbnRlbmQvanMvZGFzaGJvYXJkL3dpZGdldHMvX2V2ZW50LXNjaGVkdWxlLmpzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiI7QUFBQTtBQUNBOztBQUVBO0FBQ0E7O0FBRUE7QUFDQTtBQUNBOztBQUVBO0FBQ0E7QUFDQSx1QkFBZTtBQUNmO0FBQ0E7QUFDQTs7QUFFQTtBQUNBOztBQUVBO0FBQ0E7O0FBRUE7QUFDQTtBQUNBOzs7QUFHQTtBQUNBOztBQUVBO0FBQ0E7O0FBRUE7QUFDQTs7QUFFQTtBQUNBOzs7Ozs7Ozs7Ozs7Ozs7O0FDdENBOztBQUVBOztBQUVBO0FBQ0E7QUFDQTtBQUNBO0FBQ0EsRzs7Ozs7O0FDUkE7O0FBRUE7O0FBRUE7O0FBRUE7O0FBRUEsdUNBQXNDLHVDQUF1QyxnQkFBZ0I7O0FBRTdGO0FBQ0E7QUFDQSxvQkFBbUIsa0JBQWtCO0FBQ3JDO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBOztBQUVBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQSxFQUFDLEc7Ozs7OztBQzFCRCxtQkFBa0IsdUQ7Ozs7OztBQ0FsQjtBQUNBO0FBQ0E7QUFDQTtBQUNBLEc7Ozs7OztBQ0pBO0FBQ0E7QUFDQSxzRUFBdUUsMENBQTBDLEU7Ozs7OztBQ0ZqSDtBQUNBO0FBQ0E7QUFDQTtBQUNBOztBQUVBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0Esb0VBQW1FO0FBQ25FO0FBQ0Esc0ZBQXFGO0FBQ3JGO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQSxZQUFXO0FBQ1gsVUFBUztBQUNUO0FBQ0E7QUFDQTtBQUNBO0FBQ0EsTUFBSztBQUNMO0FBQ0E7QUFDQSxnREFBK0M7QUFDL0M7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0EsZUFBYztBQUNkLGVBQWM7QUFDZCxlQUFjO0FBQ2QsZUFBYztBQUNkLGdCQUFlO0FBQ2YsZ0JBQWU7QUFDZixnQkFBZTtBQUNmLGlCQUFnQjtBQUNoQiwwQjs7Ozs7O0FDNURBO0FBQ0E7QUFDQTtBQUNBLHdDQUF1QyxnQzs7Ozs7O0FDSHZDLDhCQUE2QjtBQUM3QixzQ0FBcUMsZ0M7Ozs7OztBQ0RyQztBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBLEc7Ozs7OztBQ25CQTtBQUNBO0FBQ0E7QUFDQSxHOzs7Ozs7QUNIQTtBQUNBO0FBQ0E7QUFDQTtBQUNBLEVBQUM7QUFDRDtBQUNBO0FBQ0EsRzs7Ozs7O0FDUEE7QUFDQTtBQUNBO0FBQ0E7O0FBRUE7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0EsSUFBRyxVQUFVO0FBQ2I7QUFDQTtBQUNBO0FBQ0EsRzs7Ozs7O0FDZkE7QUFDQTtBQUNBO0FBQ0E7QUFDQSxHOzs7Ozs7QUNKQTtBQUNBO0FBQ0EsRzs7Ozs7O0FDRkE7QUFDQSxzRUFBc0UsZ0JBQWdCLFVBQVUsR0FBRztBQUNuRyxFQUFDLEU7Ozs7OztBQ0ZEO0FBQ0E7QUFDQSxrQ0FBaUMsUUFBUSxnQkFBZ0IsVUFBVSxHQUFHO0FBQ3RFLEVBQUMsRTs7Ozs7O0FDSEQ7QUFDQTtBQUNBO0FBQ0EsSUFBRztBQUNIO0FBQ0E7QUFDQSxHOzs7Ozs7QUNOQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQSxHOzs7Ozs7QUNOQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0EsRzs7Ozs7O0FDWEE7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQSxHOzs7Ozs7Ozs7Ozs7Ozs7O0FDUEE7Ozs7Ozs7Ozs7OztBQUtBLEdBQUUsWUFBWTtBQUNWLDBCQUFXLE1BQVgsQ0FBa0Isa0JBQWxCLEVBRFU7QUFFViw2QkFBYyxNQUFkLENBQXFCLHFCQUFyQixFQUZVO0VBQVosQ0FBRixDOzs7Ozs7QUNMQTs7Ozs7Ozs7Ozs7Ozs7OztLQUdxQjtBQUNqQixjQURpQixNQUNqQixDQUFZLFFBQVosRUFBc0I7NkNBREwsUUFDSzs7QUFDbEIsY0FBSyxLQUFMLEdBQWEsRUFBRSxRQUFGLENBQWIsQ0FEa0I7QUFFbEIsY0FBSyxNQUFMLEdBQWMsS0FBSyxPQUFMLEVBQWQsQ0FGa0I7QUFHbEIsY0FBSyxjQUFMLEdBQXNCLElBQXRCLENBSGtCOztBQUtsQixjQUFLLGFBQUwsR0FMa0I7TUFBdEI7O2dDQURpQjs7bUNBU1A7QUFDTixvQkFBTztBQUNILDJCQUFVLEtBQUssS0FBTCxDQUFXLElBQVgsQ0FBZ0Isc0JBQWhCLENBQVY7QUFDQSwwQkFBUyxLQUFLLEtBQUwsQ0FBVyxJQUFYLENBQWdCLHFCQUFoQixDQUFUO2NBRkosQ0FETTs7Ozt5Q0FPTTtBQUNaLGtCQUFLLEtBQUwsQ0FDSyxFQURMLENBQ1EsT0FEUixFQUNpQixzQkFEakIsRUFDeUMsS0FBSyxlQUFMLENBQXFCLElBQXJCLENBQTBCLElBQTFCLENBRHpDLEVBRUssRUFGTCxDQUVRLE9BRlIsRUFFaUIscUJBRmpCLEVBRXdDLEtBQUssY0FBTCxDQUFvQixJQUFwQixDQUF5QixJQUF6QixDQUZ4QyxFQUdLLEVBSEwsQ0FHUSxPQUhSLEVBR2lCLG9CQUhqQixFQUd1QyxLQUFLLG9CQUFMLENBQTBCLElBQTFCLENBQStCLElBQS9CLENBSHZDLEVBRFk7Ozs7eUNBT0EsR0FBRztBQUNmLGVBQUUsY0FBRixHQURlO0FBRWYsaUJBQU0sT0FBTyxJQUFQLENBRlM7QUFHZixpQkFBTSxVQUFVLEtBQUssTUFBTCxDQUFZLFFBQVosQ0FBcUIsSUFBckIsQ0FBMEIsSUFBMUIsQ0FBVixDQUhTOztBQUtmLGtCQUFLLFlBQUwsQ0FBa0IsT0FBbEIsRUFDSyxJQURMLENBQ1UsWUFBSTtBQUNOLHNCQUFLLE1BQUwsQ0FBWSxRQUFaLENBQXFCLFFBQXJCLENBQThCLFVBQTlCLEVBQTBDLElBQTFDLENBQStDLFdBQS9DLEVBQTRELEdBQTVELENBQWdFLE9BQWhFLEVBRE07QUFFTix5QkFBUSxrQ0FBUixFQUZNO2NBQUosQ0FEVixDQUxlOzs7O3dDQVlKLEdBQUc7QUFDZCxlQUFFLGNBQUYsR0FEYztBQUVkLGlCQUFNLE9BQU8sSUFBUCxDQUZRO0FBR2QsaUJBQU0sVUFBVSxLQUFLLE1BQUwsQ0FBWSxPQUFaLENBQW9CLElBQXBCLENBQXlCLElBQXpCLENBQVYsQ0FIUTs7QUFLZCxrQkFBSyxXQUFMLENBQWlCLE9BQWpCLEVBQ0ssSUFETCxDQUNVLFVBQUMsUUFBRCxFQUFZO0FBQ2Qsc0JBQUssY0FBTCxHQUFzQixLQUFLLGFBQUwsQ0FBbUIsUUFBbkIsQ0FBdEIsQ0FEYztBQUVkLHNCQUFLLGNBQUwsQ0FBb0IsS0FBcEIsQ0FBMEIsTUFBMUIsRUFGYztjQUFaLENBRFYsQ0FMYzs7Ozs4Q0FZRyxHQUFFO0FBQ25CLGVBQUUsY0FBRixHQURtQjs7QUFHbkIsaUJBQU0sT0FBTyxJQUFQLENBSGE7QUFJbkIsaUJBQU0sV0FBVyxFQUFFLGFBQUYsRUFBaUIsU0FBakIsRUFBWCxDQUphO0FBS25CLGlCQUFNLFVBQVUsS0FBSyxNQUFMLENBQVksT0FBWixDQUFvQixJQUFwQixDQUF5QixJQUF6QixDQUFWLENBTGE7O0FBT25CLGtCQUFLLGlCQUFMLENBQXVCLFFBQXZCLEVBQWlDLE9BQWpDLEVBQ0ssSUFETCxDQUNVLFlBQUk7QUFDTixzQkFBSyxjQUFMLENBQ0ssRUFETCxDQUNRLGlCQURSLEVBQzJCLFlBQUk7QUFDdkIseUJBQUksTUFBSixDQUFXLEdBQVgsQ0FBZSxrQkFBZixFQUR1QjtrQkFBSixDQUQzQixDQUlLLEtBSkwsQ0FJVyxNQUpYLEVBRE07O0FBT04seUJBQVEsaUNBQVIsRUFQTTtjQUFKLENBRFYsQ0FQbUI7Ozs7dUNBbUJULFNBQVE7QUFDbEIsaUJBQU0sV0FBVyxlQUFYLENBRFk7QUFFbEIsaUJBQUksbUJBQUosQ0FGa0I7O0FBSWxCLGVBQUUsUUFBRixFQUFZLE1BQVosR0FKa0I7QUFLbEIsdUJBQVcsRUFBRSxjQUFjLFFBQWQsR0FBeUIsNkNBQXpCLENBQUYsQ0FDTixJQURNLENBQ0QsTUFEQyxFQUNPLFFBRFAsRUFFTixJQUZNLENBRUQsYUFGQyxFQUVjLE1BRmQsRUFHTixNQUhNLENBR0MsT0FIRCxDQUFYLENBTGtCOztBQVVsQixxQkFBUSxRQUFSLENBQWlCLEtBQUssS0FBTCxDQUFqQixDQVZrQjtBQVdsQixvQkFBTyxPQUFQLENBWGtCOzs7Ozs7O3NDQWVULFNBQVE7QUFDakIsaUJBQU0sTUFBTSxTQUFTLFdBQVQsQ0FBcUIsTUFBckIsQ0FBNEIsT0FBNUIsQ0FBb0MsT0FBcEMsRUFBNkMsR0FBN0MsQ0FESztBQUVqQixvQkFBTyxFQUFFLElBQUYsQ0FBTyxHQUFQLEVBQVksRUFBWixDQUFQLENBRmlCOzs7O3FDQUtULFNBQVE7QUFDaEIsaUJBQU0sTUFBTSxTQUFTLFdBQVQsQ0FBcUIsTUFBckIsQ0FBNEIsTUFBNUIsQ0FBbUMsT0FBbkMsRUFBNEMsR0FBNUMsQ0FESTtBQUVoQixvQkFBTyxFQUFFLEdBQUYsQ0FBTSxHQUFOLEVBQVcsRUFBWCxDQUFQLENBRmdCOzs7OzJDQUtGLE1BQU0sSUFBRztBQUN2QixpQkFBTSxNQUFNLFNBQVMsV0FBVCxDQUFxQixNQUFyQixDQUE0QixNQUE1QixDQUFtQyxFQUFuQyxFQUF1QyxHQUF2QyxDQURXO0FBRXZCLG9CQUFPLEVBQUUsSUFBRixDQUFPLEdBQVAsRUFBWSxJQUFaLENBQVAsQ0FGdUI7Ozs7Ozs7Z0NBTWIsVUFBVTtBQUNwQixpQkFBTSxTQUFTLEVBQUUsUUFBRixDQUFULENBRGM7QUFFcEIsaUJBQUksQ0FBQyxPQUFPLE1BQVAsRUFBZSxPQUFwQjs7QUFFQSxvQkFBTyxPQUFPLElBQVAsQ0FBWSxVQUFVLEtBQVYsRUFBaUIsRUFBakIsRUFBcUI7QUFDcEMscUJBQUksV0FBVyxFQUFFLEVBQUYsQ0FBWCxDQURnQztBQUVwQyxxQkFBSSxPQUFXLFNBQVMsSUFBVCxDQUFjLGlCQUFkLENBQVgsQ0FGZ0M7O0FBSXBDLHFCQUFJLENBQUMsSUFBRCxFQUFPO0FBQ1AsNEJBQU8sSUFBSSxNQUFKLENBQVcsRUFBWCxDQUFQLENBRE87QUFFUCw4QkFBUyxJQUFULENBQWMsUUFBZCxFQUF3QixJQUF4QixFQUZPO2tCQUFYO2NBSmUsQ0FBbkIsQ0FKb0I7OztZQWpHUDs7Ozs7Ozs7O0FDSHJCOzs7Ozs7Ozs7Ozs7Ozs7O0tBRXFCO0FBQ2pCLGNBRGlCLE1BQ2pCLENBQVksUUFBWixFQUFzQjs2Q0FETCxRQUNLOztBQUNsQixjQUFLLEtBQUwsR0FBYSxFQUFFLFFBQUYsQ0FBYixDQURrQjtBQUVsQixjQUFLLE1BQUwsR0FBYyxLQUFLLE9BQUwsRUFBZCxDQUZrQjs7QUFJbEIsYUFBTSxhQUFhLEtBQUssTUFBTCxDQUFZLE1BQVosQ0FBbUIsS0FBbkIsRUFBYixDQUpZO0FBS2xCLGNBQUssaUJBQUwsR0FMa0I7QUFNbEIsY0FBSyxnQkFBTCxDQUFzQixVQUF0QixFQU5rQjtBQU9sQixjQUFLLGFBQUwsR0FQa0I7TUFBdEI7O2dDQURpQjs7bUNBV1A7QUFDTixvQkFBTztBQUNILHlCQUFRLEtBQUssS0FBTCxDQUFXLElBQVgsQ0FBZ0IscUJBQWhCLENBQVI7QUFDQSx5QkFBUSxLQUFLLEtBQUwsQ0FBVyxJQUFYLENBQWdCLHNCQUFoQixDQUFSO0FBQ0EsdUJBQU0sS0FBSyxLQUFMLENBQVcsSUFBWCxDQUFnQixvQkFBaEIsQ0FBTjtBQUNBLHlCQUFRLEtBQUssS0FBTCxDQUFXLElBQVgsQ0FBZ0IsYUFBaEIsQ0FBUjtBQUNBLHdCQUFPLEtBQUssS0FBTCxDQUFXLElBQVgsQ0FBZ0IscUJBQWhCLENBQVA7QUFDQSw0QkFBVyxLQUFLLEtBQUwsQ0FBVyxJQUFYLENBQWdCLDBCQUFoQixDQUFYO2NBTkosQ0FETTs7Ozs2Q0FXUztBQUNmLGlCQUFNLFNBQVMsS0FBSyxNQUFMLENBREE7QUFFZixpQkFBTSxlQUFlLE9BQU8sTUFBUCxDQUFjLE1BQWQsQ0FBcUIsVUFBckIsRUFBaUMsTUFBakMsQ0FGTjs7QUFJZixvQkFBTyxNQUFQLENBQWMsSUFBZCxDQUFtQixPQUFPLE1BQVAsQ0FBYyxNQUFkLENBQW5CLENBSmU7QUFLZixvQkFBTyxJQUFQLENBQVksSUFBWixDQUFpQixPQUFPLE1BQVAsQ0FBYyxNQUFkLENBQXFCLFVBQXJCLEVBQWlDLE1BQWpDLENBQWpCLENBTGU7O0FBT2YscUJBQVEsWUFBUjtBQUNJLHNCQUFLLENBQUw7QUFDSSw0QkFBTyxTQUFQLENBQWlCLElBQWpCLEdBREo7QUFFSSwyQkFGSjtBQURKLHNCQUlTLENBQUw7QUFDSSw0QkFBTyxLQUFQLENBQWEsSUFBYixDQUFrQixnQkFBbEIsRUFESjtBQUVJLDJCQUZKO0FBSko7QUFRUSw0QkFBTyxLQUFQLENBQWEsSUFBYixDQUFrQixpQkFBbEIsRUFESjtBQUVJLDJCQUZKO0FBUEosY0FQZTs7Ozt5Q0FvQkg7QUFDWixrQkFBSyxLQUFMLENBQVcsRUFBWCxDQUFjLE9BQWQsRUFBdUIscUJBQXZCLEVBQThDLEtBQUssY0FBTCxDQUFvQixJQUFwQixDQUF5QixJQUF6QixDQUE5QyxFQURZOzs7O3dDQUlELEdBQUU7QUFDYixlQUFFLGNBQUYsR0FEYTtBQUViLGlCQUFNLFFBQVEsRUFBRSxFQUFFLE1BQUYsQ0FBRixDQUFZLE9BQVosQ0FBb0IscUJBQXBCLENBQVIsQ0FGTzs7QUFJYixpQkFBSSxNQUFNLFFBQU4sQ0FBZSxjQUFmLENBQUosRUFBb0MsT0FBcEM7QUFDQSxrQkFBSyxnQkFBTCxDQUFzQixLQUF0QixFQUxhOzs7OzBDQVFBLEtBQUk7QUFDakIsaUJBQU0sU0FBUyxLQUFLLE1BQUwsQ0FERTtBQUVqQixpQkFBTSxjQUFjLElBQUksSUFBSixDQUFTLGNBQVQsQ0FBZCxDQUZXO0FBR2pCLGlCQUFNLFlBQVksY0FBYSxPQUFPLE1BQVAsQ0FBYyxNQUFkLENBQXFCLE1BQU0sV0FBTixDQUFsQyxHQUFzRCxJQUF0RCxDQUhEOztBQUtqQixvQkFBTyxNQUFQLENBQWMsV0FBZCxDQUEwQiwyQkFBMUIsRUFMaUI7QUFNakIsaUJBQUksU0FBSixFQUFlO0FBQ1gsd0JBQU8sTUFBUCxDQUFjLFFBQWQsQ0FBdUIsMkJBQXZCLEVBRFc7QUFFWCwyQkFBVSxXQUFWLENBQXNCLDJCQUF0QixFQUZXO2NBQWY7O0FBS0Esb0JBQU8sTUFBUCxDQUFjLFdBQWQsQ0FBMEIsY0FBMUIsRUFYaUI7QUFZakIsaUJBQUksUUFBSixDQUFhLGNBQWIsRUFaaUI7Ozs7Ozs7Z0NBZ0JQLFVBQVU7QUFDcEIsaUJBQU0sU0FBUyxFQUFFLFFBQUYsQ0FBVCxDQURjO0FBRXBCLGlCQUFJLENBQUMsT0FBTyxNQUFQLEVBQWUsT0FBcEI7O0FBRUEsb0JBQU8sT0FBTyxJQUFQLENBQVksVUFBVSxLQUFWLEVBQWlCLEVBQWpCLEVBQXFCO0FBQ3BDLHFCQUFJLFdBQVcsRUFBRSxFQUFGLENBQVgsQ0FEZ0M7QUFFcEMscUJBQUksT0FBVyxTQUFTLElBQVQsQ0FBYyxxQkFBZCxDQUFYLENBRmdDOztBQUlwQyxxQkFBSSxDQUFDLElBQUQsRUFBTztBQUNQLDRCQUFPLElBQUksTUFBSixDQUFXLEVBQVgsQ0FBUCxDQURPO0FBRVAsOEJBQVMsSUFBVCxDQUFjLFFBQWQsRUFBd0IsSUFBeEIsRUFGTztrQkFBWDtjQUplLENBQW5CLENBSm9COzs7WUF0RVAiLCJmaWxlIjoiZGFzaC1mYWNpbGl0YXRvci5qcyIsInNvdXJjZXNDb250ZW50IjpbIiBcdC8vIFRoZSBtb2R1bGUgY2FjaGVcbiBcdHZhciBpbnN0YWxsZWRNb2R1bGVzID0ge307XG5cbiBcdC8vIFRoZSByZXF1aXJlIGZ1bmN0aW9uXG4gXHRmdW5jdGlvbiBfX3dlYnBhY2tfcmVxdWlyZV9fKG1vZHVsZUlkKSB7XG5cbiBcdFx0Ly8gQ2hlY2sgaWYgbW9kdWxlIGlzIGluIGNhY2hlXG4gXHRcdGlmKGluc3RhbGxlZE1vZHVsZXNbbW9kdWxlSWRdKVxuIFx0XHRcdHJldHVybiBpbnN0YWxsZWRNb2R1bGVzW21vZHVsZUlkXS5leHBvcnRzO1xuXG4gXHRcdC8vIENyZWF0ZSBhIG5ldyBtb2R1bGUgKGFuZCBwdXQgaXQgaW50byB0aGUgY2FjaGUpXG4gXHRcdHZhciBtb2R1bGUgPSBpbnN0YWxsZWRNb2R1bGVzW21vZHVsZUlkXSA9IHtcbiBcdFx0XHRleHBvcnRzOiB7fSxcbiBcdFx0XHRpZDogbW9kdWxlSWQsXG4gXHRcdFx0bG9hZGVkOiBmYWxzZVxuIFx0XHR9O1xuXG4gXHRcdC8vIEV4ZWN1dGUgdGhlIG1vZHVsZSBmdW5jdGlvblxuIFx0XHRtb2R1bGVzW21vZHVsZUlkXS5jYWxsKG1vZHVsZS5leHBvcnRzLCBtb2R1bGUsIG1vZHVsZS5leHBvcnRzLCBfX3dlYnBhY2tfcmVxdWlyZV9fKTtcblxuIFx0XHQvLyBGbGFnIHRoZSBtb2R1bGUgYXMgbG9hZGVkXG4gXHRcdG1vZHVsZS5sb2FkZWQgPSB0cnVlO1xuXG4gXHRcdC8vIFJldHVybiB0aGUgZXhwb3J0cyBvZiB0aGUgbW9kdWxlXG4gXHRcdHJldHVybiBtb2R1bGUuZXhwb3J0cztcbiBcdH1cblxuXG4gXHQvLyBleHBvc2UgdGhlIG1vZHVsZXMgb2JqZWN0IChfX3dlYnBhY2tfbW9kdWxlc19fKVxuIFx0X193ZWJwYWNrX3JlcXVpcmVfXy5tID0gbW9kdWxlcztcblxuIFx0Ly8gZXhwb3NlIHRoZSBtb2R1bGUgY2FjaGVcbiBcdF9fd2VicGFja19yZXF1aXJlX18uYyA9IGluc3RhbGxlZE1vZHVsZXM7XG5cbiBcdC8vIF9fd2VicGFja19wdWJsaWNfcGF0aF9fXG4gXHRfX3dlYnBhY2tfcmVxdWlyZV9fLnAgPSBcIlwiO1xuXG4gXHQvLyBMb2FkIGVudHJ5IG1vZHVsZSBhbmQgcmV0dXJuIGV4cG9ydHNcbiBcdHJldHVybiBfX3dlYnBhY2tfcmVxdWlyZV9fKDApO1xuXG5cblxuLyoqIFdFQlBBQ0sgRk9PVEVSICoqXG4gKiogd2VicGFjay9ib290c3RyYXAgYTNlODM1N2MwMzkxODU2OTU4MDFcbiAqKi8iLCJcInVzZSBzdHJpY3RcIjtcblxuZXhwb3J0cy5fX2VzTW9kdWxlID0gdHJ1ZTtcblxuZXhwb3J0cy5kZWZhdWx0ID0gZnVuY3Rpb24gKGluc3RhbmNlLCBDb25zdHJ1Y3Rvcikge1xuICBpZiAoIShpbnN0YW5jZSBpbnN0YW5jZW9mIENvbnN0cnVjdG9yKSkge1xuICAgIHRocm93IG5ldyBUeXBlRXJyb3IoXCJDYW5ub3QgY2FsbCBhIGNsYXNzIGFzIGEgZnVuY3Rpb25cIik7XG4gIH1cbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9oZWxwZXJzL2NsYXNzQ2FsbENoZWNrLmpzXG4gKiogbW9kdWxlIGlkID0gM1xuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNVxuICoqLyIsIlwidXNlIHN0cmljdFwiO1xuXG5leHBvcnRzLl9fZXNNb2R1bGUgPSB0cnVlO1xuXG52YXIgX2RlZmluZVByb3BlcnR5ID0gcmVxdWlyZShcImJhYmVsLXJ1bnRpbWUvY29yZS1qcy9vYmplY3QvZGVmaW5lLXByb3BlcnR5XCIpO1xuXG52YXIgX2RlZmluZVByb3BlcnR5MiA9IF9pbnRlcm9wUmVxdWlyZURlZmF1bHQoX2RlZmluZVByb3BlcnR5KTtcblxuZnVuY3Rpb24gX2ludGVyb3BSZXF1aXJlRGVmYXVsdChvYmopIHsgcmV0dXJuIG9iaiAmJiBvYmouX19lc01vZHVsZSA/IG9iaiA6IHsgZGVmYXVsdDogb2JqIH07IH1cblxuZXhwb3J0cy5kZWZhdWx0ID0gZnVuY3Rpb24gKCkge1xuICBmdW5jdGlvbiBkZWZpbmVQcm9wZXJ0aWVzKHRhcmdldCwgcHJvcHMpIHtcbiAgICBmb3IgKHZhciBpID0gMDsgaSA8IHByb3BzLmxlbmd0aDsgaSsrKSB7XG4gICAgICB2YXIgZGVzY3JpcHRvciA9IHByb3BzW2ldO1xuICAgICAgZGVzY3JpcHRvci5lbnVtZXJhYmxlID0gZGVzY3JpcHRvci5lbnVtZXJhYmxlIHx8IGZhbHNlO1xuICAgICAgZGVzY3JpcHRvci5jb25maWd1cmFibGUgPSB0cnVlO1xuICAgICAgaWYgKFwidmFsdWVcIiBpbiBkZXNjcmlwdG9yKSBkZXNjcmlwdG9yLndyaXRhYmxlID0gdHJ1ZTtcbiAgICAgICgwLCBfZGVmaW5lUHJvcGVydHkyLmRlZmF1bHQpKHRhcmdldCwgZGVzY3JpcHRvci5rZXksIGRlc2NyaXB0b3IpO1xuICAgIH1cbiAgfVxuXG4gIHJldHVybiBmdW5jdGlvbiAoQ29uc3RydWN0b3IsIHByb3RvUHJvcHMsIHN0YXRpY1Byb3BzKSB7XG4gICAgaWYgKHByb3RvUHJvcHMpIGRlZmluZVByb3BlcnRpZXMoQ29uc3RydWN0b3IucHJvdG90eXBlLCBwcm90b1Byb3BzKTtcbiAgICBpZiAoc3RhdGljUHJvcHMpIGRlZmluZVByb3BlcnRpZXMoQ29uc3RydWN0b3IsIHN0YXRpY1Byb3BzKTtcbiAgICByZXR1cm4gQ29uc3RydWN0b3I7XG4gIH07XG59KCk7XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9oZWxwZXJzL2NyZWF0ZUNsYXNzLmpzXG4gKiogbW9kdWxlIGlkID0gNFxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNVxuICoqLyIsIm1vZHVsZS5leHBvcnRzID0geyBcImRlZmF1bHRcIjogcmVxdWlyZShcImNvcmUtanMvbGlicmFyeS9mbi9vYmplY3QvZGVmaW5lLXByb3BlcnR5XCIpLCBfX2VzTW9kdWxlOiB0cnVlIH07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9jb3JlLWpzL29iamVjdC9kZWZpbmUtcHJvcGVydHkuanNcbiAqKiBtb2R1bGUgaWQgPSA1XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1XG4gKiovIiwicmVxdWlyZSgnLi4vLi4vbW9kdWxlcy9lczYub2JqZWN0LmRlZmluZS1wcm9wZXJ0eScpO1xudmFyICRPYmplY3QgPSByZXF1aXJlKCcuLi8uLi9tb2R1bGVzL19jb3JlJykuT2JqZWN0O1xubW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbiBkZWZpbmVQcm9wZXJ0eShpdCwga2V5LCBkZXNjKXtcbiAgcmV0dXJuICRPYmplY3QuZGVmaW5lUHJvcGVydHkoaXQsIGtleSwgZGVzYyk7XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvZm4vb2JqZWN0L2RlZmluZS1wcm9wZXJ0eS5qc1xuICoqIG1vZHVsZSBpZCA9IDZcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDVcbiAqKi8iLCJ2YXIgJGV4cG9ydCA9IHJlcXVpcmUoJy4vX2V4cG9ydCcpO1xyXG4vLyAxOS4xLjIuNCAvIDE1LjIuMy42IE9iamVjdC5kZWZpbmVQcm9wZXJ0eShPLCBQLCBBdHRyaWJ1dGVzKVxyXG4kZXhwb3J0KCRleHBvcnQuUyArICRleHBvcnQuRiAqICFyZXF1aXJlKCcuL19kZXNjcmlwdG9ycycpLCAnT2JqZWN0Jywge2RlZmluZVByb3BlcnR5OiByZXF1aXJlKCcuL19vYmplY3QtZHAnKS5mfSk7XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL2VzNi5vYmplY3QuZGVmaW5lLXByb3BlcnR5LmpzXG4gKiogbW9kdWxlIGlkID0gN1xuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNVxuICoqLyIsInZhciBnbG9iYWwgICAgPSByZXF1aXJlKCcuL19nbG9iYWwnKVxuICAsIGNvcmUgICAgICA9IHJlcXVpcmUoJy4vX2NvcmUnKVxuICAsIGN0eCAgICAgICA9IHJlcXVpcmUoJy4vX2N0eCcpXG4gICwgaGlkZSAgICAgID0gcmVxdWlyZSgnLi9faGlkZScpXG4gICwgUFJPVE9UWVBFID0gJ3Byb3RvdHlwZSc7XG5cbnZhciAkZXhwb3J0ID0gZnVuY3Rpb24odHlwZSwgbmFtZSwgc291cmNlKXtcbiAgdmFyIElTX0ZPUkNFRCA9IHR5cGUgJiAkZXhwb3J0LkZcbiAgICAsIElTX0dMT0JBTCA9IHR5cGUgJiAkZXhwb3J0LkdcbiAgICAsIElTX1NUQVRJQyA9IHR5cGUgJiAkZXhwb3J0LlNcbiAgICAsIElTX1BST1RPICA9IHR5cGUgJiAkZXhwb3J0LlBcbiAgICAsIElTX0JJTkQgICA9IHR5cGUgJiAkZXhwb3J0LkJcbiAgICAsIElTX1dSQVAgICA9IHR5cGUgJiAkZXhwb3J0LldcbiAgICAsIGV4cG9ydHMgICA9IElTX0dMT0JBTCA/IGNvcmUgOiBjb3JlW25hbWVdIHx8IChjb3JlW25hbWVdID0ge30pXG4gICAgLCBleHBQcm90byAgPSBleHBvcnRzW1BST1RPVFlQRV1cbiAgICAsIHRhcmdldCAgICA9IElTX0dMT0JBTCA/IGdsb2JhbCA6IElTX1NUQVRJQyA/IGdsb2JhbFtuYW1lXSA6IChnbG9iYWxbbmFtZV0gfHwge30pW1BST1RPVFlQRV1cbiAgICAsIGtleSwgb3duLCBvdXQ7XG4gIGlmKElTX0dMT0JBTClzb3VyY2UgPSBuYW1lO1xuICBmb3Ioa2V5IGluIHNvdXJjZSl7XG4gICAgLy8gY29udGFpbnMgaW4gbmF0aXZlXG4gICAgb3duID0gIUlTX0ZPUkNFRCAmJiB0YXJnZXQgJiYgdGFyZ2V0W2tleV0gIT09IHVuZGVmaW5lZDtcbiAgICBpZihvd24gJiYga2V5IGluIGV4cG9ydHMpY29udGludWU7XG4gICAgLy8gZXhwb3J0IG5hdGl2ZSBvciBwYXNzZWRcbiAgICBvdXQgPSBvd24gPyB0YXJnZXRba2V5XSA6IHNvdXJjZVtrZXldO1xuICAgIC8vIHByZXZlbnQgZ2xvYmFsIHBvbGx1dGlvbiBmb3IgbmFtZXNwYWNlc1xuICAgIGV4cG9ydHNba2V5XSA9IElTX0dMT0JBTCAmJiB0eXBlb2YgdGFyZ2V0W2tleV0gIT0gJ2Z1bmN0aW9uJyA/IHNvdXJjZVtrZXldXG4gICAgLy8gYmluZCB0aW1lcnMgdG8gZ2xvYmFsIGZvciBjYWxsIGZyb20gZXhwb3J0IGNvbnRleHRcbiAgICA6IElTX0JJTkQgJiYgb3duID8gY3R4KG91dCwgZ2xvYmFsKVxuICAgIC8vIHdyYXAgZ2xvYmFsIGNvbnN0cnVjdG9ycyBmb3IgcHJldmVudCBjaGFuZ2UgdGhlbSBpbiBsaWJyYXJ5XG4gICAgOiBJU19XUkFQICYmIHRhcmdldFtrZXldID09IG91dCA/IChmdW5jdGlvbihDKXtcbiAgICAgIHZhciBGID0gZnVuY3Rpb24oYSwgYiwgYyl7XG4gICAgICAgIGlmKHRoaXMgaW5zdGFuY2VvZiBDKXtcbiAgICAgICAgICBzd2l0Y2goYXJndW1lbnRzLmxlbmd0aCl7XG4gICAgICAgICAgICBjYXNlIDA6IHJldHVybiBuZXcgQztcbiAgICAgICAgICAgIGNhc2UgMTogcmV0dXJuIG5ldyBDKGEpO1xuICAgICAgICAgICAgY2FzZSAyOiByZXR1cm4gbmV3IEMoYSwgYik7XG4gICAgICAgICAgfSByZXR1cm4gbmV3IEMoYSwgYiwgYyk7XG4gICAgICAgIH0gcmV0dXJuIEMuYXBwbHkodGhpcywgYXJndW1lbnRzKTtcbiAgICAgIH07XG4gICAgICBGW1BST1RPVFlQRV0gPSBDW1BST1RPVFlQRV07XG4gICAgICByZXR1cm4gRjtcbiAgICAvLyBtYWtlIHN0YXRpYyB2ZXJzaW9ucyBmb3IgcHJvdG90eXBlIG1ldGhvZHNcbiAgICB9KShvdXQpIDogSVNfUFJPVE8gJiYgdHlwZW9mIG91dCA9PSAnZnVuY3Rpb24nID8gY3R4KEZ1bmN0aW9uLmNhbGwsIG91dCkgOiBvdXQ7XG4gICAgLy8gZXhwb3J0IHByb3RvIG1ldGhvZHMgdG8gY29yZS4lQ09OU1RSVUNUT1IlLm1ldGhvZHMuJU5BTUUlXG4gICAgaWYoSVNfUFJPVE8pe1xuICAgICAgKGV4cG9ydHMudmlydHVhbCB8fCAoZXhwb3J0cy52aXJ0dWFsID0ge30pKVtrZXldID0gb3V0O1xuICAgICAgLy8gZXhwb3J0IHByb3RvIG1ldGhvZHMgdG8gY29yZS4lQ09OU1RSVUNUT1IlLnByb3RvdHlwZS4lTkFNRSVcbiAgICAgIGlmKHR5cGUgJiAkZXhwb3J0LlIgJiYgZXhwUHJvdG8gJiYgIWV4cFByb3RvW2tleV0paGlkZShleHBQcm90bywga2V5LCBvdXQpO1xuICAgIH1cbiAgfVxufTtcbi8vIHR5cGUgYml0bWFwXG4kZXhwb3J0LkYgPSAxOyAgIC8vIGZvcmNlZFxuJGV4cG9ydC5HID0gMjsgICAvLyBnbG9iYWxcbiRleHBvcnQuUyA9IDQ7ICAgLy8gc3RhdGljXG4kZXhwb3J0LlAgPSA4OyAgIC8vIHByb3RvXG4kZXhwb3J0LkIgPSAxNjsgIC8vIGJpbmRcbiRleHBvcnQuVyA9IDMyOyAgLy8gd3JhcFxuJGV4cG9ydC5VID0gNjQ7ICAvLyBzYWZlXG4kZXhwb3J0LlIgPSAxMjg7IC8vIHJlYWwgcHJvdG8gbWV0aG9kIGZvciBgbGlicmFyeWAgXG5tb2R1bGUuZXhwb3J0cyA9ICRleHBvcnQ7XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19leHBvcnQuanNcbiAqKiBtb2R1bGUgaWQgPSA4XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1XG4gKiovIiwiLy8gaHR0cHM6Ly9naXRodWIuY29tL3psb2lyb2NrL2NvcmUtanMvaXNzdWVzLzg2I2lzc3VlY29tbWVudC0xMTU3NTkwMjhcbnZhciBnbG9iYWwgPSBtb2R1bGUuZXhwb3J0cyA9IHR5cGVvZiB3aW5kb3cgIT0gJ3VuZGVmaW5lZCcgJiYgd2luZG93Lk1hdGggPT0gTWF0aFxuICA/IHdpbmRvdyA6IHR5cGVvZiBzZWxmICE9ICd1bmRlZmluZWQnICYmIHNlbGYuTWF0aCA9PSBNYXRoID8gc2VsZiA6IEZ1bmN0aW9uKCdyZXR1cm4gdGhpcycpKCk7XG5pZih0eXBlb2YgX19nID09ICdudW1iZXInKV9fZyA9IGdsb2JhbDsgLy8gZXNsaW50LWRpc2FibGUtbGluZSBuby11bmRlZlxuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZ2xvYmFsLmpzXG4gKiogbW9kdWxlIGlkID0gOVxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNVxuICoqLyIsInZhciBjb3JlID0gbW9kdWxlLmV4cG9ydHMgPSB7dmVyc2lvbjogJzIuMi4wJ307XG5pZih0eXBlb2YgX19lID09ICdudW1iZXInKV9fZSA9IGNvcmU7IC8vIGVzbGludC1kaXNhYmxlLWxpbmUgbm8tdW5kZWZcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2NvcmUuanNcbiAqKiBtb2R1bGUgaWQgPSAxMFxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNVxuICoqLyIsIi8vIG9wdGlvbmFsIC8gc2ltcGxlIGNvbnRleHQgYmluZGluZ1xudmFyIGFGdW5jdGlvbiA9IHJlcXVpcmUoJy4vX2EtZnVuY3Rpb24nKTtcbm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oZm4sIHRoYXQsIGxlbmd0aCl7XG4gIGFGdW5jdGlvbihmbik7XG4gIGlmKHRoYXQgPT09IHVuZGVmaW5lZClyZXR1cm4gZm47XG4gIHN3aXRjaChsZW5ndGgpe1xuICAgIGNhc2UgMTogcmV0dXJuIGZ1bmN0aW9uKGEpe1xuICAgICAgcmV0dXJuIGZuLmNhbGwodGhhdCwgYSk7XG4gICAgfTtcbiAgICBjYXNlIDI6IHJldHVybiBmdW5jdGlvbihhLCBiKXtcbiAgICAgIHJldHVybiBmbi5jYWxsKHRoYXQsIGEsIGIpO1xuICAgIH07XG4gICAgY2FzZSAzOiByZXR1cm4gZnVuY3Rpb24oYSwgYiwgYyl7XG4gICAgICByZXR1cm4gZm4uY2FsbCh0aGF0LCBhLCBiLCBjKTtcbiAgICB9O1xuICB9XG4gIHJldHVybiBmdW5jdGlvbigvKiAuLi5hcmdzICovKXtcbiAgICByZXR1cm4gZm4uYXBwbHkodGhhdCwgYXJndW1lbnRzKTtcbiAgfTtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19jdHguanNcbiAqKiBtb2R1bGUgaWQgPSAxMVxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNVxuICoqLyIsIm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oaXQpe1xuICBpZih0eXBlb2YgaXQgIT0gJ2Z1bmN0aW9uJyl0aHJvdyBUeXBlRXJyb3IoaXQgKyAnIGlzIG5vdCBhIGZ1bmN0aW9uIScpO1xuICByZXR1cm4gaXQ7XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fYS1mdW5jdGlvbi5qc1xuICoqIG1vZHVsZSBpZCA9IDEyXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1XG4gKiovIiwidmFyIGRQICAgICAgICAgPSByZXF1aXJlKCcuL19vYmplY3QtZHAnKVxuICAsIGNyZWF0ZURlc2MgPSByZXF1aXJlKCcuL19wcm9wZXJ0eS1kZXNjJyk7XG5tb2R1bGUuZXhwb3J0cyA9IHJlcXVpcmUoJy4vX2Rlc2NyaXB0b3JzJykgPyBmdW5jdGlvbihvYmplY3QsIGtleSwgdmFsdWUpe1xuICByZXR1cm4gZFAuZihvYmplY3QsIGtleSwgY3JlYXRlRGVzYygxLCB2YWx1ZSkpO1xufSA6IGZ1bmN0aW9uKG9iamVjdCwga2V5LCB2YWx1ZSl7XG4gIG9iamVjdFtrZXldID0gdmFsdWU7XG4gIHJldHVybiBvYmplY3Q7XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9faGlkZS5qc1xuICoqIG1vZHVsZSBpZCA9IDEzXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1XG4gKiovIiwidmFyIGFuT2JqZWN0ICAgICAgID0gcmVxdWlyZSgnLi9fYW4tb2JqZWN0JylcbiAgLCBJRThfRE9NX0RFRklORSA9IHJlcXVpcmUoJy4vX2llOC1kb20tZGVmaW5lJylcbiAgLCB0b1ByaW1pdGl2ZSAgICA9IHJlcXVpcmUoJy4vX3RvLXByaW1pdGl2ZScpXG4gICwgZFAgICAgICAgICAgICAgPSBPYmplY3QuZGVmaW5lUHJvcGVydHk7XG5cbmV4cG9ydHMuZiA9IHJlcXVpcmUoJy4vX2Rlc2NyaXB0b3JzJykgPyBPYmplY3QuZGVmaW5lUHJvcGVydHkgOiBmdW5jdGlvbiBkZWZpbmVQcm9wZXJ0eShPLCBQLCBBdHRyaWJ1dGVzKXtcbiAgYW5PYmplY3QoTyk7XG4gIFAgPSB0b1ByaW1pdGl2ZShQLCB0cnVlKTtcbiAgYW5PYmplY3QoQXR0cmlidXRlcyk7XG4gIGlmKElFOF9ET01fREVGSU5FKXRyeSB7XG4gICAgcmV0dXJuIGRQKE8sIFAsIEF0dHJpYnV0ZXMpO1xuICB9IGNhdGNoKGUpeyAvKiBlbXB0eSAqLyB9XG4gIGlmKCdnZXQnIGluIEF0dHJpYnV0ZXMgfHwgJ3NldCcgaW4gQXR0cmlidXRlcyl0aHJvdyBUeXBlRXJyb3IoJ0FjY2Vzc29ycyBub3Qgc3VwcG9ydGVkIScpO1xuICBpZigndmFsdWUnIGluIEF0dHJpYnV0ZXMpT1tQXSA9IEF0dHJpYnV0ZXMudmFsdWU7XG4gIHJldHVybiBPO1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX29iamVjdC1kcC5qc1xuICoqIG1vZHVsZSBpZCA9IDE0XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1XG4gKiovIiwidmFyIGlzT2JqZWN0ID0gcmVxdWlyZSgnLi9faXMtb2JqZWN0Jyk7XG5tb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGl0KXtcbiAgaWYoIWlzT2JqZWN0KGl0KSl0aHJvdyBUeXBlRXJyb3IoaXQgKyAnIGlzIG5vdCBhbiBvYmplY3QhJyk7XG4gIHJldHVybiBpdDtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19hbi1vYmplY3QuanNcbiAqKiBtb2R1bGUgaWQgPSAxNVxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNVxuICoqLyIsIm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oaXQpe1xuICByZXR1cm4gdHlwZW9mIGl0ID09PSAnb2JqZWN0JyA/IGl0ICE9PSBudWxsIDogdHlwZW9mIGl0ID09PSAnZnVuY3Rpb24nO1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2lzLW9iamVjdC5qc1xuICoqIG1vZHVsZSBpZCA9IDE2XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1XG4gKiovIiwibW9kdWxlLmV4cG9ydHMgPSAhcmVxdWlyZSgnLi9fZGVzY3JpcHRvcnMnKSAmJiAhcmVxdWlyZSgnLi9fZmFpbHMnKShmdW5jdGlvbigpe1xyXG4gIHJldHVybiBPYmplY3QuZGVmaW5lUHJvcGVydHkocmVxdWlyZSgnLi9fZG9tLWNyZWF0ZScpKCdkaXYnKSwgJ2EnLCB7Z2V0OiBmdW5jdGlvbigpeyByZXR1cm4gNzsgfX0pLmEgIT0gNztcclxufSk7XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19pZTgtZG9tLWRlZmluZS5qc1xuICoqIG1vZHVsZSBpZCA9IDE3XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1XG4gKiovIiwiLy8gVGhhbmsncyBJRTggZm9yIGhpcyBmdW5ueSBkZWZpbmVQcm9wZXJ0eVxubW9kdWxlLmV4cG9ydHMgPSAhcmVxdWlyZSgnLi9fZmFpbHMnKShmdW5jdGlvbigpe1xuICByZXR1cm4gT2JqZWN0LmRlZmluZVByb3BlcnR5KHt9LCAnYScsIHtnZXQ6IGZ1bmN0aW9uKCl7IHJldHVybiA3OyB9fSkuYSAhPSA3O1xufSk7XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19kZXNjcmlwdG9ycy5qc1xuICoqIG1vZHVsZSBpZCA9IDE4XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1XG4gKiovIiwibW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihleGVjKXtcbiAgdHJ5IHtcbiAgICByZXR1cm4gISFleGVjKCk7XG4gIH0gY2F0Y2goZSl7XG4gICAgcmV0dXJuIHRydWU7XG4gIH1cbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19mYWlscy5qc1xuICoqIG1vZHVsZSBpZCA9IDE5XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1XG4gKiovIiwidmFyIGlzT2JqZWN0ID0gcmVxdWlyZSgnLi9faXMtb2JqZWN0JylcbiAgLCBkb2N1bWVudCA9IHJlcXVpcmUoJy4vX2dsb2JhbCcpLmRvY3VtZW50XG4gIC8vIGluIG9sZCBJRSB0eXBlb2YgZG9jdW1lbnQuY3JlYXRlRWxlbWVudCBpcyAnb2JqZWN0J1xuICAsIGlzID0gaXNPYmplY3QoZG9jdW1lbnQpICYmIGlzT2JqZWN0KGRvY3VtZW50LmNyZWF0ZUVsZW1lbnQpO1xubW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihpdCl7XG4gIHJldHVybiBpcyA/IGRvY3VtZW50LmNyZWF0ZUVsZW1lbnQoaXQpIDoge307XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZG9tLWNyZWF0ZS5qc1xuICoqIG1vZHVsZSBpZCA9IDIwXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1XG4gKiovIiwiLy8gNy4xLjEgVG9QcmltaXRpdmUoaW5wdXQgWywgUHJlZmVycmVkVHlwZV0pXG52YXIgaXNPYmplY3QgPSByZXF1aXJlKCcuL19pcy1vYmplY3QnKTtcbi8vIGluc3RlYWQgb2YgdGhlIEVTNiBzcGVjIHZlcnNpb24sIHdlIGRpZG4ndCBpbXBsZW1lbnQgQEB0b1ByaW1pdGl2ZSBjYXNlXG4vLyBhbmQgdGhlIHNlY29uZCBhcmd1bWVudCAtIGZsYWcgLSBwcmVmZXJyZWQgdHlwZSBpcyBhIHN0cmluZ1xubW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihpdCwgUyl7XG4gIGlmKCFpc09iamVjdChpdCkpcmV0dXJuIGl0O1xuICB2YXIgZm4sIHZhbDtcbiAgaWYoUyAmJiB0eXBlb2YgKGZuID0gaXQudG9TdHJpbmcpID09ICdmdW5jdGlvbicgJiYgIWlzT2JqZWN0KHZhbCA9IGZuLmNhbGwoaXQpKSlyZXR1cm4gdmFsO1xuICBpZih0eXBlb2YgKGZuID0gaXQudmFsdWVPZikgPT0gJ2Z1bmN0aW9uJyAmJiAhaXNPYmplY3QodmFsID0gZm4uY2FsbChpdCkpKXJldHVybiB2YWw7XG4gIGlmKCFTICYmIHR5cGVvZiAoZm4gPSBpdC50b1N0cmluZykgPT0gJ2Z1bmN0aW9uJyAmJiAhaXNPYmplY3QodmFsID0gZm4uY2FsbChpdCkpKXJldHVybiB2YWw7XG4gIHRocm93IFR5cGVFcnJvcihcIkNhbid0IGNvbnZlcnQgb2JqZWN0IHRvIHByaW1pdGl2ZSB2YWx1ZVwiKTtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL190by1wcmltaXRpdmUuanNcbiAqKiBtb2R1bGUgaWQgPSAyMVxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNVxuICoqLyIsIm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oYml0bWFwLCB2YWx1ZSl7XG4gIHJldHVybiB7XG4gICAgZW51bWVyYWJsZSAgOiAhKGJpdG1hcCAmIDEpLFxuICAgIGNvbmZpZ3VyYWJsZTogIShiaXRtYXAgJiAyKSxcbiAgICB3cml0YWJsZSAgICA6ICEoYml0bWFwICYgNCksXG4gICAgdmFsdWUgICAgICAgOiB2YWx1ZVxuICB9O1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX3Byb3BlcnR5LWRlc2MuanNcbiAqKiBtb2R1bGUgaWQgPSAyMlxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNVxuICoqLyIsIid1c2Ugc3RyaWN0JztcblxuaW1wb3J0IEV2ZW50QmxvY2sgZnJvbSAnLi8uLi9jb21tb24vX2V2ZW50LWJsb2NrJztcbmltcG9ydCBFdmVudFNjaGVkdWxlIGZyb20gJy4vd2lkZ2V0cy9fZXZlbnQtc2NoZWR1bGUnO1xuXG4kKGZ1bmN0aW9uICgpIHtcbiAgICBFdmVudEJsb2NrLnBsdWdpbignLmpzLWV2ZW50LWZ1dHVyZScpO1xuICAgIEV2ZW50U2NoZWR1bGUucGx1Z2luKCcuanMtdXBjb21pbmctZXZlbnRzJylcbn0pO1xuXG5cblxuXG4vKiogV0VCUEFDSyBGT09URVIgKipcbiAqKiAuL2Zyb250ZW5kL2pzL2Rhc2hib2FyZC9kYXNoLWZhY2lsaXRhdG9yLmpzXG4gKiovIiwiJ3VzZSBzdHJpY3QnO1xuXG5cbmV4cG9ydCBkZWZhdWx0IGNsYXNzIFdpZGdldCB7XG4gICAgY29uc3RydWN0b3Ioc2VsZWN0b3IpIHtcbiAgICAgICAgdGhpcy4kcm9vdCA9ICQoc2VsZWN0b3IpO1xuICAgICAgICB0aGlzLmxvY2FscyA9IHRoaXMuX2dldERvbSgpO1xuICAgICAgICB0aGlzLiRjb25maXJtRGlhbG9nID0gbnVsbDtcblxuICAgICAgICB0aGlzLl9hc3NpZ25FdmVudHMoKTtcbiAgICB9XG5cbiAgICBfZ2V0RG9tKCkge1xuICAgICAgICByZXR1cm4ge1xuICAgICAgICAgICAgJGNvbmZpcm06IHRoaXMuJHJvb3QuZmluZCgnW2RhdGEtZXZlbnQtY29uZmlybV0nKSxcbiAgICAgICAgICAgICRjYW5jZWw6IHRoaXMuJHJvb3QuZmluZCgnW2RhdGEtZXZlbnQtY2FuY2VsXScpXG4gICAgICAgIH07XG4gICAgfVxuXG4gICAgX2Fzc2lnbkV2ZW50cygpIHtcbiAgICAgICAgdGhpcy4kcm9vdFxuICAgICAgICAgICAgLm9uKCdjbGljaycsICdbZGF0YS1ldmVudC1jb25maXJtXScsIHRoaXMuX29uQ2xpY2tDb25maXJtLmJpbmQodGhpcykpXG4gICAgICAgICAgICAub24oJ2NsaWNrJywgJ1tkYXRhLWV2ZW50LWNhbmNlbF0nLCB0aGlzLl9vbkNsaWNrQ2FuY2VsLmJpbmQodGhpcykpXG4gICAgICAgICAgICAub24oJ2NsaWNrJywgJyNldmVudENhbmNlbEJ1dHRvbicsIHRoaXMuX29uQ2xpY2tBY2NlcHRDYW5jZWwuYmluZCh0aGlzKSk7XG4gICAgfVxuXG4gICAgX29uQ2xpY2tDb25maXJtKGUpIHtcbiAgICAgICAgZS5wcmV2ZW50RGVmYXVsdCgpO1xuICAgICAgICBjb25zdCBzZWxmID0gdGhpcztcbiAgICAgICAgY29uc3QgZXZlbnRJZCA9IHNlbGYubG9jYWxzLiRjb25maXJtLmRhdGEoJ2lkJyk7XG5cbiAgICAgICAgc2VsZi5fc2VuZENvbmZpcm0oZXZlbnRJZClcbiAgICAgICAgICAgIC5kb25lKCgpPT57XG4gICAgICAgICAgICAgICAgc2VsZi5sb2NhbHMuJGNvbmZpcm0uYWRkQ2xhc3MoJ2Rpc2FibGVkJykudGV4dCgnQ29uZmlybWVkJykub2ZmKCdjbGljaycpO1xuICAgICAgICAgICAgICAgIHN1Y2Nlc3MoXCJFdmVudCB3YXMgc3VjY2Vzc2Z1bGx5IGNvbmZpcm1lZFwiKTtcbiAgICAgICAgICAgIH0pXG4gICAgfVxuXG4gICAgX29uQ2xpY2tDYW5jZWwoZSkge1xuICAgICAgICBlLnByZXZlbnREZWZhdWx0KCk7XG4gICAgICAgIGNvbnN0IHNlbGYgPSB0aGlzO1xuICAgICAgICBjb25zdCBldmVudElkID0gc2VsZi5sb2NhbHMuJGNhbmNlbC5kYXRhKCdpZCcpO1xuXG4gICAgICAgIHNlbGYuX3NlbmRDYW5jZWwoZXZlbnRJZClcbiAgICAgICAgICAgIC5kb25lKChyZXNwb25zZSk9PntcbiAgICAgICAgICAgICAgICBzZWxmLiRjb25maXJtRGlhbG9nID0gc2VsZi5fY3JlYXRlRGlhbG9nKHJlc3BvbnNlKTtcbiAgICAgICAgICAgICAgICBzZWxmLiRjb25maXJtRGlhbG9nLm1vZGFsKCdzaG93Jyk7XG4gICAgICAgICAgICB9KVxuICAgIH1cblxuICAgIF9vbkNsaWNrQWNjZXB0Q2FuY2VsKGUpe1xuICAgICAgICBlLnByZXZlbnREZWZhdWx0KCk7XG5cbiAgICAgICAgY29uc3Qgc2VsZiA9IHRoaXM7XG4gICAgICAgIGNvbnN0IGZvcm1EYXRhID0gJChcIiNjYW5jZWxGb3JtXCIpLnNlcmlhbGl6ZSgpO1xuICAgICAgICBjb25zdCBldmVudElkID0gc2VsZi5sb2NhbHMuJGNhbmNlbC5kYXRhKCdpZCcpO1xuXG4gICAgICAgIHNlbGYuX3NlbmRBY2NlcHRDYW5jZWwoZm9ybURhdGEsIGV2ZW50SWQpXG4gICAgICAgICAgICAuZG9uZSgoKT0+e1xuICAgICAgICAgICAgICAgIHNlbGYuJGNvbmZpcm1EaWFsb2dcbiAgICAgICAgICAgICAgICAgICAgLm9uKCdoaWRkZW4uYnMubW9kYWwnLCAoKT0+e1xuICAgICAgICAgICAgICAgICAgICAgICAgQXBwLmV2ZW50cy5wdWIoJ2htdC5ldmVudC5jYW5jZWwnKTtcbiAgICAgICAgICAgICAgICAgICAgfSlcbiAgICAgICAgICAgICAgICAgICAgLm1vZGFsKCdoaWRlJyk7XG5cbiAgICAgICAgICAgICAgICBzdWNjZXNzKFwiRXZlbnQgd2FzIHN1Y2Nlc3NmdWxseSBjYW5jZWxlZFwiKTtcbiAgICAgICAgICAgIH0pXG4gICAgfVxuXG4gICAgX2NyZWF0ZURpYWxvZyhjb250ZW50KXtcbiAgICAgICAgY29uc3Qgc2VsZWN0b3IgPSAnI2NhbmNlbERpYWxvZyc7XG4gICAgICAgIGxldCAkZGlhbG9nO1xuXG4gICAgICAgICQoc2VsZWN0b3IpLnJlbW92ZSgpO1xuICAgICAgICAkZGlhbG9nID0gICQoJzxkaXYgaWQ9XCInICsgc2VsZWN0b3IgKyAnXCIgY2xhc3M9XCJiLW1vZGFsIG1vZGFsIGZhZGVcIiB0YWJpbmRleD1cIi0xXCI+JylcbiAgICAgICAgICAgIC5hdHRyKCdyb2xlJywgJ2RpYWxvZycpXG4gICAgICAgICAgICAuYXR0cignYXJpYS1oaWRkZW4nLCAndHJ1ZScpXG4gICAgICAgICAgICAuYXBwZW5kKGNvbnRlbnQpO1xuXG4gICAgICAgICRkaWFsb2cuYXBwZW5kVG8odGhpcy4kcm9vdCk7XG4gICAgICAgIHJldHVybiAkZGlhbG9nO1xuICAgIH1cblxuICAgIC8vdHJhbnNwb3J0XG4gICAgX3NlbmRDb25maXJtKGV2ZW50SWQpe1xuICAgICAgICBjb25zdCB1cmwgPSBqc1JvdXRlcy5jb250cm9sbGVycy5FdmVudHMuY29uZmlybShldmVudElkKS51cmw7XG4gICAgICAgIHJldHVybiAkLnBvc3QodXJsLCB7fSk7XG4gICAgfVxuXG4gICAgX3NlbmRDYW5jZWwoZXZlbnRJZCl7XG4gICAgICAgIGNvbnN0IHVybCA9IGpzUm91dGVzLmNvbnRyb2xsZXJzLkV2ZW50cy5yZWFzb24oZXZlbnRJZCkudXJsO1xuICAgICAgICByZXR1cm4gJC5nZXQodXJsLCB7fSk7XG4gICAgfVxuXG4gICAgX3NlbmRBY2NlcHRDYW5jZWwoZGF0YSwgaWQpe1xuICAgICAgICBjb25zdCB1cmwgPSBqc1JvdXRlcy5jb250cm9sbGVycy5FdmVudHMuY2FuY2VsKGlkKS51cmw7XG4gICAgICAgIHJldHVybiAkLnBvc3QodXJsLCBkYXRhKTtcbiAgICB9XG5cbiAgICAvLyBzdGF0aWNcbiAgICBzdGF0aWMgcGx1Z2luKHNlbGVjdG9yKSB7XG4gICAgICAgIGNvbnN0ICRlbGVtcyA9ICQoc2VsZWN0b3IpO1xuICAgICAgICBpZiAoISRlbGVtcy5sZW5ndGgpIHJldHVybjtcblxuICAgICAgICByZXR1cm4gJGVsZW1zLmVhY2goZnVuY3Rpb24gKGluZGV4LCBlbCkge1xuICAgICAgICAgICAgbGV0ICRlbGVtZW50ID0gJChlbCk7XG4gICAgICAgICAgICBsZXQgZGF0YSAgICAgPSAkZWxlbWVudC5kYXRhKCdobXQuZXZlbnQuYmxvY2snKTtcblxuICAgICAgICAgICAgaWYgKCFkYXRhKSB7XG4gICAgICAgICAgICAgICAgZGF0YSA9IG5ldyBXaWRnZXQoZWwpO1xuICAgICAgICAgICAgICAgICRlbGVtZW50LmRhdGEoJ3dpZGdldCcsIGRhdGEpO1xuICAgICAgICAgICAgfVxuICAgICAgICB9KVxuICAgIH1cbn1cblxuXG5cblxuLyoqIFdFQlBBQ0sgRk9PVEVSICoqXG4gKiogLi9mcm9udGVuZC9qcy9jb21tb24vX2V2ZW50LWJsb2NrLmpzXG4gKiovIiwiJ3VzZSBzdHJpY3QnO1xuXG5leHBvcnQgZGVmYXVsdCBjbGFzcyBXaWRnZXQge1xuICAgIGNvbnN0cnVjdG9yKHNlbGVjdG9yKSB7XG4gICAgICAgIHRoaXMuJHJvb3QgPSAkKHNlbGVjdG9yKTtcbiAgICAgICAgdGhpcy5sb2NhbHMgPSB0aGlzLl9nZXREb20oKTtcblxuICAgICAgICBjb25zdCAkZmlyc3RMaW5rID0gdGhpcy5sb2NhbHMuJGxpbmtzLmZpcnN0KCk7XG4gICAgICAgIHRoaXMuX3NldEluaXRpYWxWYWx1ZXMoKTtcbiAgICAgICAgdGhpcy5maWx0ZXJMaXN0QnlMaW5rKCRmaXJzdExpbmspO1xuICAgICAgICB0aGlzLl9hc3NpZ25FdmVudHMoKTtcbiAgICB9XG5cbiAgICBfZ2V0RG9tKCkge1xuICAgICAgICByZXR1cm4ge1xuICAgICAgICAgICAgJGxpbmtzOiB0aGlzLiRyb290LmZpbmQoJ1tkYXRhLXVwZXZlbnQtbGlua10nKSxcbiAgICAgICAgICAgICR0b3RhbDogdGhpcy4kcm9vdC5maW5kKCdbZGF0YS11cGV2ZW50LXRvdGFsXScpLFxuICAgICAgICAgICAgJG5vdzogdGhpcy4kcm9vdC5maW5kKCdbZGF0YS11cGV2ZW50LW5vd10nKSxcbiAgICAgICAgICAgICRpdGVtczogdGhpcy4kcm9vdC5maW5kKCcuYi1ldmVudGZ1dCcpLFxuICAgICAgICAgICAgJHRleHQ6IHRoaXMuJHJvb3QuZmluZCgnW2RhdGEtdXBldmVudC10ZXh0XScpLFxuICAgICAgICAgICAgJHN3aXRjaGVyOiB0aGlzLiRyb290LmZpbmQoJ1tkYXRhLXNjaGVkdWxlLXN3aXRjaGVyXScpXG4gICAgICAgIH07XG4gICAgfVxuXG4gICAgX3NldEluaXRpYWxWYWx1ZXMoKXtcbiAgICAgICAgY29uc3QgbG9jYWxzID0gdGhpcy5sb2NhbHM7XG4gICAgICAgIGNvbnN0IGN1cnJlbnRDb3VudCA9IGxvY2Fscy4kaXRlbXMuZmlsdGVyKCcuY3VycmVudCcpLmxlbmd0aDtcblxuICAgICAgICBsb2NhbHMuJHRvdGFsLnRleHQobG9jYWxzLiRpdGVtcy5sZW5ndGgpO1xuICAgICAgICBsb2NhbHMuJG5vdy50ZXh0KGxvY2Fscy4kaXRlbXMuZmlsdGVyKCcuY3VycmVudCcpLmxlbmd0aCk7XG5cbiAgICAgICAgc3dpdGNoIChjdXJyZW50Q291bnQpIHtcbiAgICAgICAgICAgIGNhc2UgMDpcbiAgICAgICAgICAgICAgICBsb2NhbHMuJHN3aXRjaGVyLmhpZGUoKTtcbiAgICAgICAgICAgICAgICBicmVhaztcbiAgICAgICAgICAgIGNhc2UgMTpcbiAgICAgICAgICAgICAgICBsb2NhbHMuJHRleHQudGV4dCgnaXMgcnVubmluZyBub3cnKTtcbiAgICAgICAgICAgICAgICBicmVhaztcbiAgICAgICAgICAgIGRlZmF1bHQ6XG4gICAgICAgICAgICAgICAgbG9jYWxzLiR0ZXh0LnRleHQoJ2FyZSBydW5uaW5nIG5vdycpO1xuICAgICAgICAgICAgICAgIGJyZWFrO1xuICAgICAgICB9XG4gICAgfVxuXG4gICAgX2Fzc2lnbkV2ZW50cygpIHtcbiAgICAgICAgdGhpcy4kcm9vdC5vbignY2xpY2snLCAnW2RhdGEtdXBldmVudC1saW5rXScsIHRoaXMuX29uQ2xpY2tGaWx0ZXIuYmluZCh0aGlzKSk7XG4gICAgfVxuXG4gICAgX29uQ2xpY2tGaWx0ZXIoZSl7XG4gICAgICAgIGUucHJldmVudERlZmF1bHQoKTtcbiAgICAgICAgY29uc3QgJGxpbmsgPSAkKGUudGFyZ2V0KS5jbG9zZXN0KCdbZGF0YS11cGV2ZW50LWxpbmtdJyk7XG5cbiAgICAgICAgaWYgKCRsaW5rLmhhc0NsYXNzKCdzdGF0ZV9hY3RpdmUnKSkgcmV0dXJuO1xuICAgICAgICB0aGlzLmZpbHRlckxpc3RCeUxpbmsoJGxpbmspO1xuICAgIH1cblxuICAgIGZpbHRlckxpc3RCeUxpbmsoJGVsKXtcbiAgICAgICAgY29uc3QgbG9jYWxzID0gdGhpcy5sb2NhbHM7XG4gICAgICAgIGNvbnN0IGZpbHRlckNsYXNzID0gJGVsLmRhdGEoJ3VwZXZlbnQtbGluaycpO1xuICAgICAgICBjb25zdCAkZmlsdGVyZWQgPSBmaWx0ZXJDbGFzcz8gbG9jYWxzLiRpdGVtcy5maWx0ZXIoJy4nICsgZmlsdGVyQ2xhc3MpOiBudWxsIDtcblxuICAgICAgICBsb2NhbHMuJGl0ZW1zLnJlbW92ZUNsYXNzKCdiLWV2ZW50ZnV0X3N0YXRlX2Rpc2FibGVkJyk7XG4gICAgICAgIGlmICgkZmlsdGVyZWQpIHtcbiAgICAgICAgICAgIGxvY2Fscy4kaXRlbXMuYWRkQ2xhc3MoJ2ItZXZlbnRmdXRfc3RhdGVfZGlzYWJsZWQnKTtcbiAgICAgICAgICAgICRmaWx0ZXJlZC5yZW1vdmVDbGFzcygnYi1ldmVudGZ1dF9zdGF0ZV9kaXNhYmxlZCcpO1xuICAgICAgICB9XG5cbiAgICAgICAgbG9jYWxzLiRsaW5rcy5yZW1vdmVDbGFzcygnc3RhdGVfYWN0aXZlJyk7XG4gICAgICAgICRlbC5hZGRDbGFzcygnc3RhdGVfYWN0aXZlJyk7XG4gICAgfVxuXG4gICAgLy8gc3RhdGljXG4gICAgc3RhdGljIHBsdWdpbihzZWxlY3Rvcikge1xuICAgICAgICBjb25zdCAkZWxlbXMgPSAkKHNlbGVjdG9yKTtcbiAgICAgICAgaWYgKCEkZWxlbXMubGVuZ3RoKSByZXR1cm47XG5cbiAgICAgICAgcmV0dXJuICRlbGVtcy5lYWNoKGZ1bmN0aW9uIChpbmRleCwgZWwpIHtcbiAgICAgICAgICAgIGxldCAkZWxlbWVudCA9ICQoZWwpO1xuICAgICAgICAgICAgbGV0IGRhdGEgICAgID0gJGVsZW1lbnQuZGF0YSgnaG10LmV2ZW50cy51cGNvbWluZycpO1xuXG4gICAgICAgICAgICBpZiAoIWRhdGEpIHtcbiAgICAgICAgICAgICAgICBkYXRhID0gbmV3IFdpZGdldChlbCk7XG4gICAgICAgICAgICAgICAgJGVsZW1lbnQuZGF0YSgnd2lkZ2V0JywgZGF0YSk7XG4gICAgICAgICAgICB9XG4gICAgICAgIH0pXG4gICAgfVxufVxuXG5cblxuXG4vKiogV0VCUEFDSyBGT09URVIgKipcbiAqKiAuL2Zyb250ZW5kL2pzL2Rhc2hib2FyZC93aWRnZXRzL19ldmVudC1zY2hlZHVsZS5qc1xuICoqLyJdLCJzb3VyY2VSb290IjoiIn0=