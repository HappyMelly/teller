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

	module.exports = __webpack_require__(25);


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
/* 25 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';
	
	var _notificationCommercial = __webpack_require__(26);
	
	var _notificationCommercial2 = _interopRequireDefault(_notificationCommercial);
	
	var _scrollTo = __webpack_require__(27);
	
	var _scrollTo2 = _interopRequireDefault(_scrollTo);
	
	var _notifationList = __webpack_require__(28);
	
	var _notifationList2 = _interopRequireDefault(_notifationList);
	
	var _plugin = __webpack_require__(29);
	
	var _plugin2 = _interopRequireDefault(_plugin);
	
	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }
	
	$(function () {
	    _scrollTo2.default.plugin('.js-link-target');
	    _notifationList2.default.plugin('.js-notification-list');
	    _plugin2.default.plugin('[markdownpreview]');
	    _notificationCommercial2.default.plugin('.js-notif-commercial');
	
	    var $dataField = $('[data-type="date"]');
	    $dataField.length && $dataField.datetimepicker({
	        useCurrent: false,
	        pickTime: false
	    });
	});

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
	        this.uniqueKey = 'notifiction';
	
	        if (!this._isShowed()) {
	            this.$root.slideDown();
	        }
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
	            this.$root.on('click', '[data-notif]', this._onClickBtn.bind(this));
	        }
	    }, {
	        key: '_onClickBtn',
	        value: function _onClickBtn(e) {
	            e.preventDefault();
	            var self = this;
	            var $link = $(e.currentTarget);
	            var url = $link.attr('href');
	
	            self._sendIsShowed(url).done(function () {
	                self.$root.addClass('b-notification_state_thank');
	
	                setTimeout(function () {
	                    self.hide();
	                }, 3000);
	            });
	        }
	    }, {
	        key: '_isShowed',
	        value: function _isShowed() {
	            var value = localStorage.getItem(this.uniqueKey);
	            return value && value == 'showed';
	        }
	    }, {
	        key: '_sendIsShowed',
	        value: function _sendIsShowed(url) {
	            var self = this;
	            var defer = $.Deferred();
	
	            $.post(url, function () {
	                localStorage.setItem(self.uniqueKey, 'showed');
	                defer.resolve();
	            });
	
	            return defer.promise();
	        }
	    }, {
	        key: 'hide',
	        value: function hide() {
	            this.$root.slideUp();
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
	
	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }
	
	var Widget = function () {
	    /**
	     * Filter history
	     * @param {String} selector
	     */
	
	    function Widget(selector) {
	        (0, _classCallCheck3.default)(this, Widget);
	
	        this.$root = $(selector);
	        this._assignEvents();
	    }
	
	    (0, _createClass3.default)(Widget, [{
	        key: '_assignEvents',
	        value: function _assignEvents() {
	            this.$root.on('click', this._onClickLink.bind(this));
	        }
	    }, {
	        key: '_onClickLink',
	        value: function _onClickLink(e) {
	            var $link = $(e.currentTarget);
	            var target = $link.data('target');
	
	            if (!this._isTargetValid(target)) return false;
	
	            this.scrollToTarget('#' + target);
	            e.preventDefault();
	        }
	    }, {
	        key: '_isTargetValid',
	        value: function _isTargetValid(target) {
	            var valid = true;
	
	            if (!target) {
	                console.log('There is no data-target attribute with id-name for this link');
	                valid = false;
	            }
	
	            if (!$('#' + target).length) {
	                console.log('There is no element with such id name');
	                valid = false;
	            }
	
	            return valid;
	        }
	
	        /**
	         * Scroll to the element with "target" id
	         * @param {String} target - id selector of element
	         * @returns {boolean}
	         */
	
	    }, {
	        key: 'scrollToTarget',
	        value: function scrollToTarget(target) {
	            var $target = $(target);
	
	            if (!$target.length) return false;
	
	            $('html, body').animate({
	                scrollTop: $target.offset().top
	            }, 400);
	        }
	
	        // static
	
	    }], [{
	        key: 'plugin',
	        value: function plugin(selector) {
	            var $elems = $(selector);
	            if (!$elems.length) return;
	
	            return $elems.each(function (index, el) {
	                var $element = $(el);
	                var data = $element.data('widget.scrollto');
	
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
/* 28 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';
	
	/**
	 * Notification for user about new events
	 */
	
	/**
	 * Notification object
	 * @typedef {Object} NotificationItem
	 * @property {String} body      - html of the notification
	 * @property {Boolen} unread    - is current notification already viewed?
	 * @property {Number} id        - id of the notification
	 * @property {String} type      - type of the notification
	 */
	
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
	     *
	     * @param {(String|domElement)} selector  - selector or domElement as root element of the widget
	     * @constructor
	     */
	
	    function Widget(selector) {
	        (0, _classCallCheck3.default)(this, Widget);
	
	        var self = this;
	
	        self.$root = $(selector);
	        self.locals = self._getDom();
	
	        self._init();
	        self._assignEvents();
	    }
	
	    /**
	     * Get $ elements of the widget
	     * @returns {Object} - jQuery links ot the elements of the widget
	     * @private
	     */
	
	
	    (0, _createClass3.default)(Widget, [{
	        key: '_getDom',
	        value: function _getDom() {
	            var $root = this.$root;
	
	            return {
	                $list: $root.find('[data-notiflist-list]'),
	                $close: $root.find('[data-notiflist-close]'),
	                $link: $root.find('[data-notiflist-show]'),
	                $load: $root.find('[data-notiflist-load]'),
	                $count: $root.find('[data-notiflist-count]')
	            };
	        }
	
	        /**
	         * Initiate base variables for widget
	         * @private
	         */
	
	    }, {
	        key: '_init',
	        value: function _init() {
	            var self = this;
	
	            self.isLoaded = false;
	            self.offset = 0;
	            self.isVisible = false;
	
	            self._recieveUnreadCount().done(function (count) {
	                self.setUnreadCount(count);
	            });
	        }
	    }, {
	        key: '_assignEvents',
	        value: function _assignEvents() {
	            var self = this;
	
	            self.$root.on('click', '[data-notiflist-show], [data-notiflist-close]', self._onClickToggleShow.bind(self)).on('click', '[data-notiflist-load]', self._onClickLoadNotification.bind(self));
	        }
	
	        /**
	         * Event handler for showing and hiding popup
	         * @param {Event} e - Event Object
	         * @private
	         */
	
	    }, {
	        key: '_onClickToggleShow',
	        value: function _onClickToggleShow(e) {
	            this.togglePopup();
	
	            if (!this.isLoaded) {
	                this.isLoaded = true;
	                this.loadNotification(this.offset);
	            }
	
	            e.preventDefault();
	        }
	    }, {
	        key: '_onClickLoadNotification',
	
	
	        /**
	         * Event handler for the button "load more notification"
	         * @param {Event} e - Event Object
	         * @private
	         */
	        value: function _onClickLoadNotification(e) {
	            e.preventDefault();
	
	            this.loadNotification(this.offset);
	        }
	    }, {
	        key: '_renderNotification',
	
	
	        /**
	         * Add to the dom
	         * @param {NotificationItem[]} notifList - list of the notification
	         * @private
	         */
	        value: function _renderNotification(notifList) {
	            var self = this;
	
	            notifList.forEach(function (item) {
	                $(item.body).addClass(item.type).toggleClass('is-new', Boolean(item.unread)).data('id', item.id).appendTo(self.locals.$list);
	            });
	        }
	
	        /**
	         * Filter notification and return only new
	         * @param {NotificationItem[]} notifList - list of the notification
	         * @returns {NotificationItem[]} notifList - only new notifications
	         * @private
	         */
	
	    }, {
	        key: '_filterOnlyNew',
	        value: function _filterOnlyNew(notifList) {
	            return notifList.filter(function (item) {
	                return item.unread;
	            });
	        }
	    }, {
	        key: '_isHaveNotification',
	        value: function _isHaveNotification(notifList) {
	            if (!notifList.length && !this.offset) {
	                this.$root.addClass('b-notiflist_empty');
	                return false;
	            }
	
	            if (notifList.length < 5) {
	                this.$root.addClass('b-notiflist_load_all');
	            }
	            return true;
	        }
	
	        /**
	         * Get new load notification and render them
	         * @param {Number} offset -
	         */
	
	    }, {
	        key: 'loadNotification',
	        value: function loadNotification(offset) {
	            var self = this;
	
	            self._recieveNotification(offset).done(function (notifList) {
	                if (!self._isHaveNotification(notifList)) return;
	
	                self.offset += notifList.length;
	                self._renderNotification(notifList);
	
	                var newNotifList = self._filterOnlyNew(notifList);
	
	                if (!newNotifList.length) return;
	
	                self._sendViewedNewNotif(newNotifList);
	                self.setUnreadCount(self.unreadCount - newNotifList.length);
	            });
	        }
	
	        /**
	         * Set unread count
	         * @param {Number} count - new value for unread count
	         */
	
	    }, {
	        key: 'setUnreadCount',
	        value: function setUnreadCount(count) {
	            this.unreadCount = count > 0 ? count : 0;
	
	            this.locals.$count.text(this.unreadCount);
	            this.$root.toggleClass('b-notiflist_have_notification', Boolean(this.unreadCount));
	        }
	    }, {
	        key: 'showPopup',
	        value: function showPopup() {
	            if (this.isVisible) return;
	
	            this.isVisible = true;
	            this.$root.addClass('b-notiflist_show');
	        }
	    }, {
	        key: 'hidePopup',
	        value: function hidePopup() {
	            if (!this.isVisible) return;
	
	            this.isVisible = false;
	            this.$root.removeClass('b-notiflist_show');
	        }
	    }, {
	        key: 'togglePopup',
	        value: function togglePopup() {
	            this.isVisible ? this.hidePopup() : this.showPopup();
	        }
	
	        // transport
	
	    }, {
	        key: '_recieveUnreadCount',
	        value: function _recieveUnreadCount() {
	            var defer = $.Deferred();
	            var url = jsRoutes.controllers.core.Notifications.unread().url;
	
	            $.get(url, function (data) {
	                var count = $.parseJSON(data).unread;
	                defer.resolve(count);
	            });
	
	            return defer.promise();
	        }
	    }, {
	        key: '_recieveNotification',
	        value: function _recieveNotification(offset) {
	            var defer = $.Deferred();
	            var limit = 5;
	            var url = jsRoutes.controllers.core.Notifications.list(offset, limit).url;
	
	            $.get(url, function (data) {
	                var notifList = $.parseJSON(data)[0];
	
	                defer.resolve(notifList);
	            });
	
	            return defer.promise();
	        }
	
	        /**
	         * Send to the server id of the viewed notification
	         * @param {NotificationItem[]} notifList
	         * @private
	         */
	
	    }, {
	        key: '_sendViewedNewNotif',
	        value: function _sendViewedNewNotif(notifList) {
	            var ids = [];
	            var url = jsRoutes.controllers.core.Notifications.read().url;
	
	            notifList.forEach(function (item) {
	                ids.push(item.id);
	            });
	
	            $.post(url, { ids: ids });
	        }
	    }], [{
	        key: 'plugin',
	
	
	        // static
	        value: function plugin(selector) {
	            var $elems = $(selector);
	            if (!$elems.length) return;
	
	            return $elems.each(function (index, el) {
	                var $element = $(el);
	                var data = $element.data('widget.scrollto');
	
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
/* 29 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';
	
	Object.defineProperty(exports, "__esModule", {
	    value: true
	});
	
	var _classCallCheck2 = __webpack_require__(3);
	
	var _classCallCheck3 = _interopRequireDefault(_classCallCheck2);
	
	var _createClass2 = __webpack_require__(4);
	
	var _createClass3 = _interopRequireDefault(_createClass2);
	
	var _set = __webpack_require__(30);
	
	var _set2 = _interopRequireDefault(_set);
	
	var _jquery = __webpack_require__(31);
	
	var _jquery2 = _interopRequireDefault(_jquery);
	
	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }
	
	var Widget = function () {
	    function Widget(selector) {
	        (0, _classCallCheck3.default)(this, Widget);
	
	        this.$root = $(selector);
	        this.isEmailPreview = this.$root.data('markdownpreview');
	        this.url = jsRoutes.controllers.Utilities.markdown().url;
	
	        if (!$.fn.markItUp) {
	            console.log('invalid dependency');
	            return;
	        }
	        this._init();
	
	        this.locals = this._getDom();
	        this._assignEvents();
	    }
	
	    (0, _createClass3.default)(Widget, [{
	        key: '_init',
	        value: function _init() {
	            var linksTemplate = '<div class="markitup__link type-write state_active">Write</div><div class="markitup__link type-preview">Preview</div> <div class="markitup__preview"></div>';
	            var $root = this.$root;
	
	            $root.wrap('<div class="markitup__con"></div>').closest('.markitup__con').toggleClass('markitup_state_email', this.isEmailPreview);
	
	            $root.after(linksTemplate).markItUp(_set2.default);
	        }
	    }, {
	        key: '_getDom',
	        value: function _getDom() {
	            var $container = this.$root.closest('.markitup__con');
	
	            return {
	                $container: $container,
	                $textarea: $container.find('textarea'),
	                $preview: $container.find('.markitup__preview')
	            };
	        }
	    }, {
	        key: '_assignEvents',
	        value: function _assignEvents() {
	            this.locals.$container.on('click', '.markitup__link', this._onClickToggle.bind(this)).on('markdown.render', 'textarea', this._onUpdatePreview.bind(this));
	        }
	    }, {
	        key: '_onClickToggle',
	        value: function _onClickToggle(e) {
	            var $link = $(e.currentTarget);
	            e.preventDefault();
	
	            if ($link.hasClass('state_active')) return;
	
	            var isShowPreview = $link.hasClass('type-preview');
	            this._togglePreview(isShowPreview);
	
	            $link.addClass('state_active').siblings().removeClass('state_active');
	        }
	    }, {
	        key: '_togglePreview',
	        value: function _togglePreview(isShowPreview) {
	            var locals = this.locals;
	            var nameClass = 'markitup_state_preview';
	
	            if (!isShowPreview) {
	                locals.$container.removeClass(nameClass);
	                return;
	            }
	
	            this._compileContent(locals.$textarea.val()).done(function (data) {
	                locals.$preview.html(data);
	
	                if (!locals.$container.hasClass(nameClass)) {
	                    locals.$container.addClass(nameClass);
	                }
	            });
	        }
	    }, {
	        key: '_onUpdatePreview',
	        value: function _onUpdatePreview() {
	            var locals = this.locals;
	
	            this._compileContent(locals.$textarea.val()).done(function (data) {
	                locals.$preview.html(data);
	            });
	        }
	
	        //transport
	
	    }, {
	        key: '_compileContent',
	        value: function _compileContent(content) {
	            return $.post(this.url, {
	                data: content
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
	                var data = $element.data('widget.preview.markdown');
	
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
/* 30 */
/***/ function(module, exports) {

	'use strict';
	
	Object.defineProperty(exports, "__esModule", {
	    value: true
	});
	var markdownSettings = {
	    nameSpace: 'markdown', // Useful to prevent multi-instances CSS conflict
	    previewParserPath: '~/sets/markdown/preview.php',
	    onShiftEnter: { keepDefault: false, openWith: '\n\n' },
	    markupSet: [{ name: 'Bold', key: "B", openWith: '**', closeWith: '**' }, { name: 'Italic', key: "I", openWith: '_', closeWith: '_' }, { separator: '---------------' }, { name: 'Quotes', openWith: '> ' }, { name: 'Link', key: "L", openWith: '[', closeWith: ']([![Url:!:http://]!] "[![Title]!]")', placeHolder: 'Your text to link here...' }, { separator: '---------------' }, { name: 'Bulleted List', openWith: '\n\n - ' }, { name: 'Numeric List', openWith: '\n\n 1. ' }]
	};
	
	exports.default = markdownSettings;

/***/ },
/* 31 */
/***/ function(module, exports) {

	'use strict';
	
	// ----------------------------------------------------------------------------
	// markItUp! Universal MarkUp Engine, JQuery plugin
	// v 1.1.x
	// Dual licensed under the MIT and GPL licenses.
	// ----------------------------------------------------------------------------
	// Copyright (C) 2007-2012 Jay Salvat
	// http://markitup.jaysalvat.com/
	// ----------------------------------------------------------------------------
	// Permission is hereby granted, free of charge, to any person obtaining a copy
	// of this software and associated documentation files (the "Software"), to deal
	// in the Software without restriction, including without limitation the rights
	// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	// copies of the Software, and to permit persons to whom the Software is
	// furnished to do so, subject to the following conditions:
	//
	// The above copyright notice and this permission notice shall be included in
	// all copies or substantial portions of the Software.
	//
	// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
	// THE SOFTWARE.
	// ----------------------------------------------------------------------------
	(function () {
	    $.fn.markItUp = function (settings, extraSettings) {
	        var method, params, line, selection, options, ctrlKey, shiftKey, altKey;
	        ctrlKey = shiftKey = altKey = false;
	
	        if (typeof settings == 'string') {
	            method = settings;
	            params = extraSettings;
	        }
	
	        options = {
	            id: '',
	            nameSpace: '',
	            root: '',
	            previewHandler: false,
	            previewInWindow: '', // 'width=800, height=600, resizable=yes, scrollbars=yes'
	            previewInElement: '',
	            previewAutoRefresh: true,
	            previewPosition: 'after',
	            previewTemplatePath: '~/templates/preview.html',
	            previewParser: false,
	            previewParserPath: '',
	            previewParserVar: 'data',
	            resizeHandle: true,
	            beforeInsert: '',
	            afterInsert: '',
	            onEnter: {},
	            onShiftEnter: {},
	            onCtrlEnter: {},
	            onTab: {},
	            markupSet: [{/* set */}]
	        };
	        $.extend(options, settings, extraSettings);
	
	        // Quick patch to keep compatibility with jQuery 1.9
	        var uaMatch = function uaMatch(ua) {
	            ua = ua.toLowerCase();
	
	            var match = /(chrome)[ \/]([\w.]+)/.exec(ua) || /(webkit)[ \/]([\w.]+)/.exec(ua) || /(opera)(?:.*version|)[ \/]([\w.]+)/.exec(ua) || /(msie) ([\w.]+)/.exec(ua) || ua.indexOf("compatible") < 0 && /(mozilla)(?:.*? rv:([\w.]+)|)/.exec(ua) || [];
	
	            return {
	                browser: match[1] || "",
	                version: match[2] || "0"
	            };
	        };
	        var matched = uaMatch(navigator.userAgent);
	        var browser = {};
	
	        if (matched.browser) {
	            browser[matched.browser] = true;
	            browser.version = matched.version;
	        }
	        if (browser.chrome) {
	            browser.webkit = true;
	        } else if (browser.webkit) {
	            browser.safari = true;
	        }
	
	        return this.each(function () {
	            var $$, textarea, levels, scrollPosition, caretPosition, caretOffset, clicked, hash, header, footer, previewWindow, template, iFrame, abort;
	            $$ = $(this);
	            textarea = this;
	            levels = [];
	            abort = false;
	            scrollPosition = caretPosition = 0;
	            caretOffset = -1;
	
	            options.previewParserPath = localize(options.previewParserPath);
	            options.previewTemplatePath = localize(options.previewTemplatePath);
	
	            if (method) {
	                switch (method) {
	                    case 'remove':
	                        remove();
	                        break;
	                    case 'insert':
	                        markup(params);
	                        break;
	                    default:
	                        $.error('Method ' + method + ' does not exist on jQuery.markItUp');
	                }
	                return;
	            }
	
	            // apply the computed path to ~/
	            function localize(data, inText) {
	                if (inText) {
	                    return data.replace(/("|')~\//g, "$1" + options.root);
	                }
	                return data.replace(/^~\//, options.root);
	            }
	
	            // init and build editor
	            function init() {
	                var id = '',
	                    nameSpace = '',
	                    resizeHandle;
	
	                if (options.id) {
	                    id = 'id="' + options.id + '"';
	                } else if ($$.attr("id")) {
	                    id = 'id="markItUp' + $$.attr("id").substr(0, 1).toUpperCase() + $$.attr("id").substr(1) + '"';
	                }
	                if (options.nameSpace) {
	                    nameSpace = 'class="' + options.nameSpace + '"';
	                }
	                $$.wrap('<div ' + nameSpace + '></div>');
	                $$.wrap('<div ' + id + ' class="markItUp"></div>');
	                $$.wrap('<div class="markItUpContainer"></div>');
	                $$.addClass("markItUpEditor");
	
	                // add the header before the textarea
	                header = $('<div class="markItUpHeader"></div>').insertBefore($$);
	                $(dropMenus(options.markupSet)).appendTo(header);
	
	                // add the footer after the textarea
	                footer = $('<div class="markItUpFooter"></div>').insertAfter($$);
	
	                // add the resize handle after textarea
	                if (options.resizeHandle === true && browser.safari !== true) {
	                    resizeHandle = $('<div class="markItUpResizeHandle"></div>').insertAfter($$).bind("mousedown.markItUp", function (e) {
	                        var h = $$.height(),
	                            y = e.clientY,
	                            mouseMove,
	                            _mouseUp;
	                        mouseMove = function mouseMove(e) {
	                            $$.css("height", Math.max(20, e.clientY + h - y) + "px");
	                            return false;
	                        };
	                        _mouseUp = function mouseUp(e) {
	                            $("html").unbind("mousemove.markItUp", mouseMove).unbind("mouseup.markItUp", _mouseUp);
	                            return false;
	                        };
	                        $("html").bind("mousemove.markItUp", mouseMove).bind("mouseup.markItUp", _mouseUp);
	                    });
	                    footer.append(resizeHandle);
	                }
	
	                // listen key events
	                $$.bind('keydown.markItUp', keyPressed).bind('keyup', keyPressed);
	
	                // bind an event to catch external calls
	                $$.bind("insertion.markItUp", function (e, settings) {
	                    if (settings.target !== false) {
	                        get();
	                    }
	                    if (textarea === $.markItUp.focused) {
	                        markup(settings);
	                    }
	                });
	
	                // remember the last focus
	                $$.bind('focus.markItUp', function () {
	                    $.markItUp.focused = this;
	                });
	
	                if (options.previewInElement) {
	                    refreshPreview();
	                }
	            }
	
	            // recursively build header with dropMenus from markupset
	            function dropMenus(markupSet) {
	                var ul = $('<ul></ul>'),
	                    i = 0;
	                $('li:hover > ul', ul).css('display', 'block');
	                $.each(markupSet, function () {
	                    var button = this,
	                        t = '',
	                        li,
	                        j;
	                    var title = button.key ? (button.name || '') + ' [Ctrl+' + button.key + ']' : button.name || '';
	                    var key = button.key ? 'accesskey="' + button.key + '"' : '';
	                    if (button.separator) {
	                        li = $('<li class="markItUpSeparator">' + (button.separator || '') + '</li>').appendTo(ul);
	                    } else {
	                        i++;
	                        for (j = levels.length - 1; j >= 0; j--) {
	                            t += levels[j] + "-";
	                        }
	                        li = $('<li class="markItUpButton markItUpButton' + t + i + ' ' + (button.className || '') + '"><a href="" ' + key + ' title="' + title + '">' + (button.name || '') + '</a></li>').bind("contextmenu.markItUp", function () {
	                            // prevent contextmenu on mac and allow ctrl+click
	                            return false;
	                        }).bind('click.markItUp', function (e) {
	                            e.preventDefault();
	                        }).bind("focusin.markItUp", function () {
	                            $$.focus();
	                        }).bind('mouseup', function () {
	                            if (button.call) {
	                                eval(button.call)();
	                            }
	                            setTimeout(function () {
	                                markup(button);
	                            }, 1);
	                            return false;
	                        }).bind('mouseenter.markItUp', function () {
	                            $('> ul', this).show();
	                            $(document).one('click', function () {
	                                // close dropmenu if click outside
	                                $('ul ul', header).hide();
	                            });
	                        }).bind('mouseleave.markItUp', function () {
	                            $('> ul', this).hide();
	                        }).appendTo(ul);
	                        if (button.dropMenu) {
	                            levels.push(i);
	                            $(li).addClass('markItUpDropMenu').append(dropMenus(button.dropMenu));
	                        }
	                    }
	                });
	                levels.pop();
	                return ul;
	            }
	
	            // markItUp! markups
	            function magicMarkups(string) {
	                var value;
	
	                if (string) {
	                    string = string.toString();
	                    string = string.replace(/\(\!\(([\s\S]*?)\)\!\)/g, function (x, a) {
	                        var b = a.split('|!|');
	                        if (altKey === true) {
	                            return b[1] !== undefined ? b[1] : b[0];
	                        } else {
	                            return b[1] === undefined ? "" : b[0];
	                        }
	                    });
	                    // [![prompt]!], [![prompt:!:value]!]
	                    string = string.replace(/\[\!\[([\s\S]*?)\]\!\]/g, function (x, a) {
	                        var b = a.split(':!:');
	                        if (abort === true) {
	                            return false;
	                        }
	                        value = prompt(b[0], b[1] ? b[1] : '');
	                        if (value === null) {
	                            abort = true;
	                        }
	                        return value;
	                    });
	                    return string;
	                }
	                return "";
	            }
	
	            // prepare action
	            function prepare(action) {
	                if ($.isFunction(action)) {
	                    action = action(hash);
	                }
	                return magicMarkups(action);
	            }
	
	            // build block to insert
	            function build(string) {
	                var openWith = prepare(clicked.openWith);
	                var placeHolder = prepare(clicked.placeHolder);
	                var replaceWith = prepare(clicked.replaceWith);
	                var closeWith = prepare(clicked.closeWith);
	                var openBlockWith = prepare(clicked.openBlockWith);
	                var closeBlockWith = prepare(clicked.closeBlockWith);
	                var multiline = clicked.multiline;
	                var block;
	
	                if (replaceWith !== "") {
	                    block = openWith + replaceWith + closeWith;
	                } else if (selection === '' && placeHolder !== '') {
	                    block = openWith + placeHolder + closeWith;
	                } else {
	                    string = string || selection;
	
	                    var lines = [string],
	                        blocks = [];
	
	                    if (multiline === true) {
	                        lines = string.split(/\r?\n/);
	                    }
	
	                    for (var l = 0; l < lines.length; l++) {
	                        line = lines[l];
	                        var trailingSpaces;
	                        if (trailingSpaces = line.match(/ *$/)) {
	                            blocks.push(openWith + line.replace(/ *$/g, '') + closeWith + trailingSpaces);
	                        } else {
	                            blocks.push(openWith + line + closeWith);
	                        }
	                    }
	
	                    block = blocks.join("\n");
	                }
	
	                block = openBlockWith + block + closeBlockWith;
	
	                return {
	                    block: block,
	                    openBlockWith: openBlockWith,
	                    openWith: openWith,
	                    replaceWith: replaceWith,
	                    placeHolder: placeHolder,
	                    closeWith: closeWith,
	                    closeBlockWith: closeBlockWith
	                };
	            }
	
	            // define markup to insert
	            function markup(button) {
	                var len, j, n, i, string, start;
	                hash = clicked = button;
	                get();
	                $.extend(hash, {
	                    line: "",
	                    root: options.root,
	                    textarea: textarea,
	                    selection: selection || '',
	                    caretPosition: caretPosition,
	                    ctrlKey: ctrlKey,
	                    shiftKey: shiftKey,
	                    altKey: altKey
	                });
	                // callbacks before insertion
	                prepare(options.beforeInsert);
	                prepare(clicked.beforeInsert);
	                if (ctrlKey === true && shiftKey === true || button.multiline === true) {
	                    prepare(clicked.beforeMultiInsert);
	                }
	                $.extend(hash, { line: 1 });
	
	                if (ctrlKey === true && shiftKey === true) {
	                    lines = selection.split(/\r?\n/);
	                    for (j = 0, n = lines.length, i = 0; i < n; i++) {
	                        if ($.trim(lines[i]) !== '') {
	                            $.extend(hash, { line: ++j, selection: lines[i] });
	                            lines[i] = build(lines[i]).block;
	                        } else {
	                            lines[i] = "";
	                        }
	                    }
	
	                    string = { block: lines.join('\n') };
	                    start = caretPosition;
	                    len = string.block.length + (browser.opera ? n - 1 : 0);
	                } else if (ctrlKey === true) {
	                    string = build(selection);
	                    start = caretPosition + string.openWith.length;
	                    len = string.block.length - string.openWith.length - string.closeWith.length;
	                    len = len - (string.block.match(/ $/) ? 1 : 0);
	                    len -= fixIeBug(string.block);
	                } else if (shiftKey === true) {
	                    string = build(selection);
	                    start = caretPosition;
	                    len = string.block.length;
	                    len -= fixIeBug(string.block);
	                } else {
	                    string = build(selection);
	                    start = caretPosition + string.block.length;
	                    len = 0;
	                    start -= fixIeBug(string.block);
	                }
	                if (selection === '' && string.replaceWith === '') {
	                    caretOffset += fixOperaBug(string.block);
	
	                    start = caretPosition + string.openBlockWith.length + string.openWith.length;
	                    len = string.block.length - string.openBlockWith.length - string.openWith.length - string.closeWith.length - string.closeBlockWith.length;
	
	                    caretOffset = $$.val().substring(caretPosition, $$.val().length).length;
	                    caretOffset -= fixOperaBug($$.val().substring(0, caretPosition));
	                }
	                $.extend(hash, { caretPosition: caretPosition, scrollPosition: scrollPosition });
	
	                if (string.block !== selection && abort === false) {
	                    insert(string.block);
	                    set(start, len);
	                } else {
	                    caretOffset = -1;
	                }
	                get();
	
	                $.extend(hash, { line: '', selection: selection });
	
	                // callbacks after insertion
	                if (ctrlKey === true && shiftKey === true || button.multiline === true) {
	                    prepare(clicked.afterMultiInsert);
	                }
	                prepare(clicked.afterInsert);
	                prepare(options.afterInsert);
	
	                // refresh preview if opened
	                if (previewWindow && options.previewAutoRefresh) {
	                    refreshPreview();
	                }
	
	                // reinit keyevent
	                shiftKey = altKey = ctrlKey = abort = false;
	            }
	
	            // Substract linefeed in Opera
	            function fixOperaBug(string) {
	                if (browser.opera) {
	                    return string.length - string.replace(/\n*/g, '').length;
	                }
	                return 0;
	            }
	
	            // Substract linefeed in IE
	            function fixIeBug(string) {
	                if (browser.msie) {
	                    return string.length - string.replace(/\r*/g, '').length;
	                }
	                return 0;
	            }
	
	            // add markup
	            function insert(block) {
	                if (document.selection) {
	                    var newSelection = document.selection.createRange();
	                    newSelection.text = block;
	                } else {
	                    textarea.value = textarea.value.substring(0, caretPosition) + block + textarea.value.substring(caretPosition + selection.length, textarea.value.length);
	                }
	            }
	
	            // set a selection
	            function set(start, len) {
	                if (textarea.createTextRange) {
	                    // quick fix to make it work on Opera 9.5
	                    if (browser.opera && browser.version >= 9.5 && len == 0) {
	                        return false;
	                    }
	                    range = textarea.createTextRange();
	                    range.collapse(true);
	                    range.moveStart('character', start);
	                    range.moveEnd('character', len);
	                    range.select();
	                } else if (textarea.setSelectionRange) {
	                    textarea.setSelectionRange(start, start + len);
	                }
	                textarea.scrollTop = scrollPosition;
	                textarea.focus();
	            }
	
	            // get the selection
	            function get() {
	                textarea.focus();
	
	                scrollPosition = textarea.scrollTop;
	                if (document.selection) {
	                    selection = document.selection.createRange().text;
	                    if (browser.msie) {
	                        // ie
	                        var range = document.selection.createRange(),
	                            rangeCopy = range.duplicate();
	                        rangeCopy.moveToElementText(textarea);
	                        caretPosition = -1;
	                        while (rangeCopy.inRange(range)) {
	                            rangeCopy.moveStart('character');
	                            caretPosition++;
	                        }
	                    } else {
	                        // opera
	                        caretPosition = textarea.selectionStart;
	                    }
	                } else {
	                    // gecko & webkit
	                    caretPosition = textarea.selectionStart;
	
	                    selection = textarea.value.substring(caretPosition, textarea.selectionEnd);
	                }
	                return selection;
	            }
	
	            // open preview window
	            function preview() {
	                if (typeof options.previewHandler === 'function') {
	                    previewWindow = true;
	                } else if (options.previewInElement) {
	                    previewWindow = $(options.previewInElement);
	                } else if (!previewWindow || previewWindow.closed) {
	                    if (options.previewInWindow) {
	                        previewWindow = window.open('', 'preview', options.previewInWindow);
	                        $(window).unload(function () {
	                            previewWindow.close();
	                        });
	                    } else {
	                        iFrame = $('<iframe class="markItUpPreviewFrame"></iframe>');
	                        if (options.previewPosition == 'after') {
	                            iFrame.insertAfter(footer);
	                        } else {
	                            iFrame.insertBefore(header);
	                        }
	                        previewWindow = iFrame[iFrame.length - 1].contentWindow || frame[iFrame.length - 1];
	                    }
	                } else if (altKey === true) {
	                    if (iFrame) {
	                        iFrame.remove();
	                    } else {
	                        previewWindow.close();
	                    }
	                    previewWindow = iFrame = false;
	                }
	                if (!options.previewAutoRefresh) {
	                    refreshPreview();
	                }
	                if (options.previewInWindow) {
	                    previewWindow.focus();
	                }
	            }
	
	            // refresh Preview window
	            function refreshPreview() {
	                renderPreview();
	            }
	
	            function renderPreview() {
	                var phtml;
	                if (options.previewHandler && typeof options.previewHandler === 'function') {
	                    options.previewHandler($$.val());
	                } else if (options.previewParser && typeof options.previewParser === 'function') {
	                    var data = options.previewParser($$.val());
	                    writeInPreview(localize(data, 1));
	                } else if (options.previewParserPath !== '') {
	                    $.ajax({
	                        type: 'POST',
	                        dataType: 'text',
	                        global: false,
	                        url: options.previewParserPath,
	                        data: options.previewParserVar + '=' + encodeURIComponent($$.val()),
	                        success: function success(data) {
	                            writeInPreview(localize(data, 1));
	                        }
	                    });
	                } else {
	                    if (!template) {
	                        $.ajax({
	                            url: options.previewTemplatePath,
	                            dataType: 'text',
	                            global: false,
	                            success: function success(data) {
	                                writeInPreview(localize(data, 1).replace(/<!-- content -->/g, $$.val()));
	                            }
	                        });
	                    }
	                }
	                return false;
	            }
	
	            function writeInPreview(data) {
	                if (options.previewInElement) {
	                    $(options.previewInElement).html(data);
	                } else if (previewWindow && previewWindow.document) {
	                    try {
	                        sp = previewWindow.document.documentElement.scrollTop;
	                    } catch (e) {
	                        sp = 0;
	                    }
	                    previewWindow.document.open();
	                    previewWindow.document.write(data);
	                    previewWindow.document.close();
	                    previewWindow.document.documentElement.scrollTop = sp;
	                }
	            }
	
	            // set keys pressed
	            function keyPressed(e) {
	                var li;
	
	                shiftKey = e.shiftKey;
	                altKey = e.altKey;
	                ctrlKey = !(e.altKey && e.ctrlKey) ? e.ctrlKey || e.metaKey : false;
	
	                if (e.type === 'keydown') {
	                    if (ctrlKey === true) {
	                        li = $('a[accesskey="' + (e.keyCode == 13 ? '\\n' : String.fromCharCode(e.keyCode)) + '"]', header).parent('li');
	                        if (li.length !== 0) {
	                            ctrlKey = false;
	                            setTimeout(function () {
	                                li.triggerHandler('mouseup');
	                            }, 1);
	                            return false;
	                        }
	                    }
	                    if (e.keyCode === 13 || e.keyCode === 10) {
	                        // Enter key
	                        if (ctrlKey === true) {
	                            // Enter + Ctrl
	                            ctrlKey = false;
	                            markup(options.onCtrlEnter);
	                            return options.onCtrlEnter.keepDefault;
	                        } else if (shiftKey === true) {
	                            // Enter + Shift
	                            shiftKey = false;
	                            markup(options.onShiftEnter);
	                            return options.onShiftEnter.keepDefault;
	                        } else {
	                            // only Enter
	                            markup(options.onEnter);
	                            return options.onEnter.keepDefault;
	                        }
	                    }
	                    if (e.keyCode === 9) {
	                        // Tab key
	                        if (shiftKey == true || ctrlKey == true || altKey == true) {
	                            return false;
	                        }
	                        if (caretOffset !== -1) {
	                            get();
	                            caretOffset = $$.val().length - caretOffset;
	                            set(caretOffset, 0);
	                            caretOffset = -1;
	                            return false;
	                        } else {
	                            markup(options.onTab);
	                            return options.onTab.keepDefault;
	                        }
	                    }
	                }
	            }
	
	            function remove() {
	                $$.unbind(".markItUp").removeClass('markItUpEditor');
	                $$.parent('div').parent('div.markItUp').parent('div').replaceWith($$);
	                $$.data('markItUp', null);
	            }
	
	            init();
	        });
	    };
	
	    $.fn.markItUpRemove = function () {
	        return this.each(function () {
	            $(this).markItUp('remove');
	        });
	    };
	
	    $.markItUp = function (settings) {
	        var options = { target: false };
	        $.extend(options, settings);
	        if (options.target) {
	            return $(options.target).each(function () {
	                $(this).focus();
	                $(this).trigger('insertion', [options]);
	            });
	        } else {
	            $('textarea').trigger('insertion', [options]);
	        }
	    };
	})();

/***/ }
/******/ ]);
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIndlYnBhY2s6Ly8vd2VicGFjay9ib290c3RyYXAgZjBmMTY2OTBiZDk3YTI1NWNmYjk/N2VmNCoiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL2hlbHBlcnMvY2xhc3NDYWxsQ2hlY2suanM/MjFhZioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL2hlbHBlcnMvY3JlYXRlQ2xhc3MuanM/MWRmZSoiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL2NvcmUtanMvb2JqZWN0L2RlZmluZS1wcm9wZXJ0eS5qcz81ZjcwKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvZm4vb2JqZWN0L2RlZmluZS1wcm9wZXJ0eS5qcz80ZDMzKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9lczYub2JqZWN0LmRlZmluZS1wcm9wZXJ0eS5qcz84YmRlKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZXhwb3J0LmpzPzhlNDAqIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19nbG9iYWwuanM/M2M1MioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2NvcmUuanM/ZDYxMSoiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2N0eC5qcz80ZTU5KiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fYS1mdW5jdGlvbi5qcz8wNjk5KiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9faGlkZS5qcz8wZDJlKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fb2JqZWN0LWRwLmpzPzU5ODYqIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19hbi1vYmplY3QuanM/M2FmMioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2lzLW9iamVjdC5qcz9jZmRhKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9faWU4LWRvbS1kZWZpbmUuanM/YjEwMioiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2Rlc2NyaXB0b3JzLmpzP2MwZjUqIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19mYWlscy5qcz9jNmRkKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZG9tLWNyZWF0ZS5qcz82MTJmKiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fdG8tcHJpbWl0aXZlLmpzPzFhNjUqIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19wcm9wZXJ0eS1kZXNjLmpzPzI1NmIqIiwid2VicGFjazovLy8uL2Zyb250ZW5kL2pzL2NvbW1vbi1wYWNrLmpzIiwid2VicGFjazovLy8uL2Zyb250ZW5kL2pzL2xheW91dC9fbm90aWZpY2F0aW9uLWNvbW1lcmNpYWwuanMiLCJ3ZWJwYWNrOi8vLy4vZnJvbnRlbmQvanMvbGF5b3V0L19zY3JvbGwtdG8uanMiLCJ3ZWJwYWNrOi8vLy4vZnJvbnRlbmQvanMvbGF5b3V0L19ub3RpZmF0aW9uLWxpc3QuanMiLCJ3ZWJwYWNrOi8vLy4vZnJvbnRlbmQvanMvcGx1Z2lucy9tYXJrZG93bi9fcGx1Z2luLmpzIiwid2VicGFjazovLy8uL2Zyb250ZW5kL2pzL3BsdWdpbnMvbWFya2Rvd24vX3NldC5qcyIsIndlYnBhY2s6Ly8vLi9mcm9udGVuZC9qcy9wbHVnaW5zL21hcmtkb3duL19qcXVlcnkubWFya2l0dXAuanMiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IjtBQUFBO0FBQ0E7O0FBRUE7QUFDQTs7QUFFQTtBQUNBO0FBQ0E7O0FBRUE7QUFDQTtBQUNBLHVCQUFlO0FBQ2Y7QUFDQTtBQUNBOztBQUVBO0FBQ0E7O0FBRUE7QUFDQTs7QUFFQTtBQUNBO0FBQ0E7OztBQUdBO0FBQ0E7O0FBRUE7QUFDQTs7QUFFQTtBQUNBOztBQUVBO0FBQ0E7Ozs7Ozs7Ozs7Ozs7Ozs7QUN0Q0E7O0FBRUE7O0FBRUE7QUFDQTtBQUNBO0FBQ0E7QUFDQSxHOzs7Ozs7QUNSQTs7QUFFQTs7QUFFQTs7QUFFQTs7QUFFQSx1Q0FBc0MsdUNBQXVDLGdCQUFnQjs7QUFFN0Y7QUFDQTtBQUNBLG9CQUFtQixrQkFBa0I7QUFDckM7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7O0FBRUE7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBLEVBQUMsRzs7Ozs7O0FDMUJELG1CQUFrQix1RDs7Ozs7O0FDQWxCO0FBQ0E7QUFDQTtBQUNBO0FBQ0EsRzs7Ozs7O0FDSkE7QUFDQTtBQUNBLHNFQUF1RSwwQ0FBMEMsRTs7Ozs7O0FDRmpIO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7O0FBRUE7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQSxvRUFBbUU7QUFDbkU7QUFDQSxzRkFBcUY7QUFDckY7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBLFlBQVc7QUFDWCxVQUFTO0FBQ1Q7QUFDQTtBQUNBO0FBQ0E7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QW9CLElBQXBCLEVBSGM7Y0FBbEI7O0FBTUEsb0JBN2lCeUI7VUFBWixDQUFqQixDQTlEK0M7TUFBbkMsQ0FEUDs7QUFnbkJULE9BQUUsRUFBRixDQUFLLGNBQUwsR0FBc0IsWUFBWTtBQUM5QixnQkFBTyxLQUFLLElBQUwsQ0FBVSxZQUFZO0FBQ3JCLGVBQUUsSUFBRixFQUFRLFFBQVIsQ0FBaUIsUUFBakIsRUFEcUI7VUFBWixDQUFqQixDQUQ4QjtNQUFaLENBaG5CYjs7QUF1bkJULE9BQUUsUUFBRixHQUFhLFVBQVUsUUFBVixFQUFvQjtBQUM3QixhQUFJLFVBQVUsRUFBQyxRQUFRLEtBQVIsRUFBWCxDQUR5QjtBQUU3QixXQUFFLE1BQUYsQ0FBUyxPQUFULEVBQWtCLFFBQWxCLEVBRjZCO0FBRzdCLGFBQUksUUFBUSxNQUFSLEVBQWdCO0FBQ2hCLG9CQUFPLEVBQUUsUUFBUSxNQUFSLENBQUYsQ0FBa0IsSUFBbEIsQ0FBdUIsWUFBWTtBQUN0QyxtQkFBRSxJQUFGLEVBQVEsS0FBUixHQURzQztBQUV0QyxtQkFBRSxJQUFGLEVBQVEsT0FBUixDQUFnQixXQUFoQixFQUE2QixDQUFDLE9BQUQsQ0FBN0IsRUFGc0M7Y0FBWixDQUE5QixDQURnQjtVQUFwQixNQUtPO0FBQ0gsZUFBRSxVQUFGLEVBQWMsT0FBZCxDQUFzQixXQUF0QixFQUFtQyxDQUFDLE9BQUQsQ0FBbkMsRUFERztVQUxQO01BSFMsQ0F2bkJKO0VBQVosQ0FBRCxHIiwiZmlsZSI6ImNvbW1vbi1wYWNrLmpzIiwic291cmNlc0NvbnRlbnQiOlsiIFx0Ly8gVGhlIG1vZHVsZSBjYWNoZVxuIFx0dmFyIGluc3RhbGxlZE1vZHVsZXMgPSB7fTtcblxuIFx0Ly8gVGhlIHJlcXVpcmUgZnVuY3Rpb25cbiBcdGZ1bmN0aW9uIF9fd2VicGFja19yZXF1aXJlX18obW9kdWxlSWQpIHtcblxuIFx0XHQvLyBDaGVjayBpZiBtb2R1bGUgaXMgaW4gY2FjaGVcbiBcdFx0aWYoaW5zdGFsbGVkTW9kdWxlc1ttb2R1bGVJZF0pXG4gXHRcdFx0cmV0dXJuIGluc3RhbGxlZE1vZHVsZXNbbW9kdWxlSWRdLmV4cG9ydHM7XG5cbiBcdFx0Ly8gQ3JlYXRlIGEgbmV3IG1vZHVsZSAoYW5kIHB1dCBpdCBpbnRvIHRoZSBjYWNoZSlcbiBcdFx0dmFyIG1vZHVsZSA9IGluc3RhbGxlZE1vZHVsZXNbbW9kdWxlSWRdID0ge1xuIFx0XHRcdGV4cG9ydHM6IHt9LFxuIFx0XHRcdGlkOiBtb2R1bGVJZCxcbiBcdFx0XHRsb2FkZWQ6IGZhbHNlXG4gXHRcdH07XG5cbiBcdFx0Ly8gRXhlY3V0ZSB0aGUgbW9kdWxlIGZ1bmN0aW9uXG4gXHRcdG1vZHVsZXNbbW9kdWxlSWRdLmNhbGwobW9kdWxlLmV4cG9ydHMsIG1vZHVsZSwgbW9kdWxlLmV4cG9ydHMsIF9fd2VicGFja19yZXF1aXJlX18pO1xuXG4gXHRcdC8vIEZsYWcgdGhlIG1vZHVsZSBhcyBsb2FkZWRcbiBcdFx0bW9kdWxlLmxvYWRlZCA9IHRydWU7XG5cbiBcdFx0Ly8gUmV0dXJuIHRoZSBleHBvcnRzIG9mIHRoZSBtb2R1bGVcbiBcdFx0cmV0dXJuIG1vZHVsZS5leHBvcnRzO1xuIFx0fVxuXG5cbiBcdC8vIGV4cG9zZSB0aGUgbW9kdWxlcyBvYmplY3QgKF9fd2VicGFja19tb2R1bGVzX18pXG4gXHRfX3dlYnBhY2tfcmVxdWlyZV9fLm0gPSBtb2R1bGVzO1xuXG4gXHQvLyBleHBvc2UgdGhlIG1vZHVsZSBjYWNoZVxuIFx0X193ZWJwYWNrX3JlcXVpcmVfXy5jID0gaW5zdGFsbGVkTW9kdWxlcztcblxuIFx0Ly8gX193ZWJwYWNrX3B1YmxpY19wYXRoX19cbiBcdF9fd2VicGFja19yZXF1aXJlX18ucCA9IFwiXCI7XG5cbiBcdC8vIExvYWQgZW50cnkgbW9kdWxlIGFuZCByZXR1cm4gZXhwb3J0c1xuIFx0cmV0dXJuIF9fd2VicGFja19yZXF1aXJlX18oMCk7XG5cblxuXG4vKiogV0VCUEFDSyBGT09URVIgKipcbiAqKiB3ZWJwYWNrL2Jvb3RzdHJhcCBmMGYxNjY5MGJkOTdhMjU1Y2ZiOVxuICoqLyIsIlwidXNlIHN0cmljdFwiO1xuXG5leHBvcnRzLl9fZXNNb2R1bGUgPSB0cnVlO1xuXG5leHBvcnRzLmRlZmF1bHQgPSBmdW5jdGlvbiAoaW5zdGFuY2UsIENvbnN0cnVjdG9yKSB7XG4gIGlmICghKGluc3RhbmNlIGluc3RhbmNlb2YgQ29uc3RydWN0b3IpKSB7XG4gICAgdGhyb3cgbmV3IFR5cGVFcnJvcihcIkNhbm5vdCBjYWxsIGEgY2xhc3MgYXMgYSBmdW5jdGlvblwiKTtcbiAgfVxufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL2hlbHBlcnMvY2xhc3NDYWxsQ2hlY2suanNcbiAqKiBtb2R1bGUgaWQgPSAzXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJcInVzZSBzdHJpY3RcIjtcblxuZXhwb3J0cy5fX2VzTW9kdWxlID0gdHJ1ZTtcblxudmFyIF9kZWZpbmVQcm9wZXJ0eSA9IHJlcXVpcmUoXCJiYWJlbC1ydW50aW1lL2NvcmUtanMvb2JqZWN0L2RlZmluZS1wcm9wZXJ0eVwiKTtcblxudmFyIF9kZWZpbmVQcm9wZXJ0eTIgPSBfaW50ZXJvcFJlcXVpcmVEZWZhdWx0KF9kZWZpbmVQcm9wZXJ0eSk7XG5cbmZ1bmN0aW9uIF9pbnRlcm9wUmVxdWlyZURlZmF1bHQob2JqKSB7IHJldHVybiBvYmogJiYgb2JqLl9fZXNNb2R1bGUgPyBvYmogOiB7IGRlZmF1bHQ6IG9iaiB9OyB9XG5cbmV4cG9ydHMuZGVmYXVsdCA9IGZ1bmN0aW9uICgpIHtcbiAgZnVuY3Rpb24gZGVmaW5lUHJvcGVydGllcyh0YXJnZXQsIHByb3BzKSB7XG4gICAgZm9yICh2YXIgaSA9IDA7IGkgPCBwcm9wcy5sZW5ndGg7IGkrKykge1xuICAgICAgdmFyIGRlc2NyaXB0b3IgPSBwcm9wc1tpXTtcbiAgICAgIGRlc2NyaXB0b3IuZW51bWVyYWJsZSA9IGRlc2NyaXB0b3IuZW51bWVyYWJsZSB8fCBmYWxzZTtcbiAgICAgIGRlc2NyaXB0b3IuY29uZmlndXJhYmxlID0gdHJ1ZTtcbiAgICAgIGlmIChcInZhbHVlXCIgaW4gZGVzY3JpcHRvcikgZGVzY3JpcHRvci53cml0YWJsZSA9IHRydWU7XG4gICAgICAoMCwgX2RlZmluZVByb3BlcnR5Mi5kZWZhdWx0KSh0YXJnZXQsIGRlc2NyaXB0b3Iua2V5LCBkZXNjcmlwdG9yKTtcbiAgICB9XG4gIH1cblxuICByZXR1cm4gZnVuY3Rpb24gKENvbnN0cnVjdG9yLCBwcm90b1Byb3BzLCBzdGF0aWNQcm9wcykge1xuICAgIGlmIChwcm90b1Byb3BzKSBkZWZpbmVQcm9wZXJ0aWVzKENvbnN0cnVjdG9yLnByb3RvdHlwZSwgcHJvdG9Qcm9wcyk7XG4gICAgaWYgKHN0YXRpY1Byb3BzKSBkZWZpbmVQcm9wZXJ0aWVzKENvbnN0cnVjdG9yLCBzdGF0aWNQcm9wcyk7XG4gICAgcmV0dXJuIENvbnN0cnVjdG9yO1xuICB9O1xufSgpO1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvaGVscGVycy9jcmVhdGVDbGFzcy5qc1xuICoqIG1vZHVsZSBpZCA9IDRcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsIm1vZHVsZS5leHBvcnRzID0geyBcImRlZmF1bHRcIjogcmVxdWlyZShcImNvcmUtanMvbGlicmFyeS9mbi9vYmplY3QvZGVmaW5lLXByb3BlcnR5XCIpLCBfX2VzTW9kdWxlOiB0cnVlIH07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9jb3JlLWpzL29iamVjdC9kZWZpbmUtcHJvcGVydHkuanNcbiAqKiBtb2R1bGUgaWQgPSA1XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJyZXF1aXJlKCcuLi8uLi9tb2R1bGVzL2VzNi5vYmplY3QuZGVmaW5lLXByb3BlcnR5Jyk7XG52YXIgJE9iamVjdCA9IHJlcXVpcmUoJy4uLy4uL21vZHVsZXMvX2NvcmUnKS5PYmplY3Q7XG5tb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uIGRlZmluZVByb3BlcnR5KGl0LCBrZXksIGRlc2Mpe1xuICByZXR1cm4gJE9iamVjdC5kZWZpbmVQcm9wZXJ0eShpdCwga2V5LCBkZXNjKTtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9mbi9vYmplY3QvZGVmaW5lLXByb3BlcnR5LmpzXG4gKiogbW9kdWxlIGlkID0gNlxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwidmFyICRleHBvcnQgPSByZXF1aXJlKCcuL19leHBvcnQnKTtcclxuLy8gMTkuMS4yLjQgLyAxNS4yLjMuNiBPYmplY3QuZGVmaW5lUHJvcGVydHkoTywgUCwgQXR0cmlidXRlcylcclxuJGV4cG9ydCgkZXhwb3J0LlMgKyAkZXhwb3J0LkYgKiAhcmVxdWlyZSgnLi9fZGVzY3JpcHRvcnMnKSwgJ09iamVjdCcsIHtkZWZpbmVQcm9wZXJ0eTogcmVxdWlyZSgnLi9fb2JqZWN0LWRwJykuZn0pO1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9lczYub2JqZWN0LmRlZmluZS1wcm9wZXJ0eS5qc1xuICoqIG1vZHVsZSBpZCA9IDdcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsInZhciBnbG9iYWwgICAgPSByZXF1aXJlKCcuL19nbG9iYWwnKVxuICAsIGNvcmUgICAgICA9IHJlcXVpcmUoJy4vX2NvcmUnKVxuICAsIGN0eCAgICAgICA9IHJlcXVpcmUoJy4vX2N0eCcpXG4gICwgaGlkZSAgICAgID0gcmVxdWlyZSgnLi9faGlkZScpXG4gICwgUFJPVE9UWVBFID0gJ3Byb3RvdHlwZSc7XG5cbnZhciAkZXhwb3J0ID0gZnVuY3Rpb24odHlwZSwgbmFtZSwgc291cmNlKXtcbiAgdmFyIElTX0ZPUkNFRCA9IHR5cGUgJiAkZXhwb3J0LkZcbiAgICAsIElTX0dMT0JBTCA9IHR5cGUgJiAkZXhwb3J0LkdcbiAgICAsIElTX1NUQVRJQyA9IHR5cGUgJiAkZXhwb3J0LlNcbiAgICAsIElTX1BST1RPICA9IHR5cGUgJiAkZXhwb3J0LlBcbiAgICAsIElTX0JJTkQgICA9IHR5cGUgJiAkZXhwb3J0LkJcbiAgICAsIElTX1dSQVAgICA9IHR5cGUgJiAkZXhwb3J0LldcbiAgICAsIGV4cG9ydHMgICA9IElTX0dMT0JBTCA/IGNvcmUgOiBjb3JlW25hbWVdIHx8IChjb3JlW25hbWVdID0ge30pXG4gICAgLCBleHBQcm90byAgPSBleHBvcnRzW1BST1RPVFlQRV1cbiAgICAsIHRhcmdldCAgICA9IElTX0dMT0JBTCA/IGdsb2JhbCA6IElTX1NUQVRJQyA/IGdsb2JhbFtuYW1lXSA6IChnbG9iYWxbbmFtZV0gfHwge30pW1BST1RPVFlQRV1cbiAgICAsIGtleSwgb3duLCBvdXQ7XG4gIGlmKElTX0dMT0JBTClzb3VyY2UgPSBuYW1lO1xuICBmb3Ioa2V5IGluIHNvdXJjZSl7XG4gICAgLy8gY29udGFpbnMgaW4gbmF0aXZlXG4gICAgb3duID0gIUlTX0ZPUkNFRCAmJiB0YXJnZXQgJiYgdGFyZ2V0W2tleV0gIT09IHVuZGVmaW5lZDtcbiAgICBpZihvd24gJiYga2V5IGluIGV4cG9ydHMpY29udGludWU7XG4gICAgLy8gZXhwb3J0IG5hdGl2ZSBvciBwYXNzZWRcbiAgICBvdXQgPSBvd24gPyB0YXJnZXRba2V5XSA6IHNvdXJjZVtrZXldO1xuICAgIC8vIHByZXZlbnQgZ2xvYmFsIHBvbGx1dGlvbiBmb3IgbmFtZXNwYWNlc1xuICAgIGV4cG9ydHNba2V5XSA9IElTX0dMT0JBTCAmJiB0eXBlb2YgdGFyZ2V0W2tleV0gIT0gJ2Z1bmN0aW9uJyA/IHNvdXJjZVtrZXldXG4gICAgLy8gYmluZCB0aW1lcnMgdG8gZ2xvYmFsIGZvciBjYWxsIGZyb20gZXhwb3J0IGNvbnRleHRcbiAgICA6IElTX0JJTkQgJiYgb3duID8gY3R4KG91dCwgZ2xvYmFsKVxuICAgIC8vIHdyYXAgZ2xvYmFsIGNvbnN0cnVjdG9ycyBmb3IgcHJldmVudCBjaGFuZ2UgdGhlbSBpbiBsaWJyYXJ5XG4gICAgOiBJU19XUkFQICYmIHRhcmdldFtrZXldID09IG91dCA/IChmdW5jdGlvbihDKXtcbiAgICAgIHZhciBGID0gZnVuY3Rpb24oYSwgYiwgYyl7XG4gICAgICAgIGlmKHRoaXMgaW5zdGFuY2VvZiBDKXtcbiAgICAgICAgICBzd2l0Y2goYXJndW1lbnRzLmxlbmd0aCl7XG4gICAgICAgICAgICBjYXNlIDA6IHJldHVybiBuZXcgQztcbiAgICAgICAgICAgIGNhc2UgMTogcmV0dXJuIG5ldyBDKGEpO1xuICAgICAgICAgICAgY2FzZSAyOiByZXR1cm4gbmV3IEMoYSwgYik7XG4gICAgICAgICAgfSByZXR1cm4gbmV3IEMoYSwgYiwgYyk7XG4gICAgICAgIH0gcmV0dXJuIEMuYXBwbHkodGhpcywgYXJndW1lbnRzKTtcbiAgICAgIH07XG4gICAgICBGW1BST1RPVFlQRV0gPSBDW1BST1RPVFlQRV07XG4gICAgICByZXR1cm4gRjtcbiAgICAvLyBtYWtlIHN0YXRpYyB2ZXJzaW9ucyBmb3IgcHJvdG90eXBlIG1ldGhvZHNcbiAgICB9KShvdXQpIDogSVNfUFJPVE8gJiYgdHlwZW9mIG91dCA9PSAnZnVuY3Rpb24nID8gY3R4KEZ1bmN0aW9uLmNhbGwsIG91dCkgOiBvdXQ7XG4gICAgLy8gZXhwb3J0IHByb3RvIG1ldGhvZHMgdG8gY29yZS4lQ09OU1RSVUNUT1IlLm1ldGhvZHMuJU5BTUUlXG4gICAgaWYoSVNfUFJPVE8pe1xuICAgICAgKGV4cG9ydHMudmlydHVhbCB8fCAoZXhwb3J0cy52aXJ0dWFsID0ge30pKVtrZXldID0gb3V0O1xuICAgICAgLy8gZXhwb3J0IHByb3RvIG1ldGhvZHMgdG8gY29yZS4lQ09OU1RSVUNUT1IlLnByb3RvdHlwZS4lTkFNRSVcbiAgICAgIGlmKHR5cGUgJiAkZXhwb3J0LlIgJiYgZXhwUHJvdG8gJiYgIWV4cFByb3RvW2tleV0paGlkZShleHBQcm90bywga2V5LCBvdXQpO1xuICAgIH1cbiAgfVxufTtcbi8vIHR5cGUgYml0bWFwXG4kZXhwb3J0LkYgPSAxOyAgIC8vIGZvcmNlZFxuJGV4cG9ydC5HID0gMjsgICAvLyBnbG9iYWxcbiRleHBvcnQuUyA9IDQ7ICAgLy8gc3RhdGljXG4kZXhwb3J0LlAgPSA4OyAgIC8vIHByb3RvXG4kZXhwb3J0LkIgPSAxNjsgIC8vIGJpbmRcbiRleHBvcnQuVyA9IDMyOyAgLy8gd3JhcFxuJGV4cG9ydC5VID0gNjQ7ICAvLyBzYWZlXG4kZXhwb3J0LlIgPSAxMjg7IC8vIHJlYWwgcHJvdG8gbWV0aG9kIGZvciBgbGlicmFyeWAgXG5tb2R1bGUuZXhwb3J0cyA9ICRleHBvcnQ7XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19leHBvcnQuanNcbiAqKiBtb2R1bGUgaWQgPSA4XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCIvLyBodHRwczovL2dpdGh1Yi5jb20vemxvaXJvY2svY29yZS1qcy9pc3N1ZXMvODYjaXNzdWVjb21tZW50LTExNTc1OTAyOFxudmFyIGdsb2JhbCA9IG1vZHVsZS5leHBvcnRzID0gdHlwZW9mIHdpbmRvdyAhPSAndW5kZWZpbmVkJyAmJiB3aW5kb3cuTWF0aCA9PSBNYXRoXG4gID8gd2luZG93IDogdHlwZW9mIHNlbGYgIT0gJ3VuZGVmaW5lZCcgJiYgc2VsZi5NYXRoID09IE1hdGggPyBzZWxmIDogRnVuY3Rpb24oJ3JldHVybiB0aGlzJykoKTtcbmlmKHR5cGVvZiBfX2cgPT0gJ251bWJlcicpX19nID0gZ2xvYmFsOyAvLyBlc2xpbnQtZGlzYWJsZS1saW5lIG5vLXVuZGVmXG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19nbG9iYWwuanNcbiAqKiBtb2R1bGUgaWQgPSA5XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJ2YXIgY29yZSA9IG1vZHVsZS5leHBvcnRzID0ge3ZlcnNpb246ICcyLjIuMCd9O1xuaWYodHlwZW9mIF9fZSA9PSAnbnVtYmVyJylfX2UgPSBjb3JlOyAvLyBlc2xpbnQtZGlzYWJsZS1saW5lIG5vLXVuZGVmXG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19jb3JlLmpzXG4gKiogbW9kdWxlIGlkID0gMTBcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsIi8vIG9wdGlvbmFsIC8gc2ltcGxlIGNvbnRleHQgYmluZGluZ1xudmFyIGFGdW5jdGlvbiA9IHJlcXVpcmUoJy4vX2EtZnVuY3Rpb24nKTtcbm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oZm4sIHRoYXQsIGxlbmd0aCl7XG4gIGFGdW5jdGlvbihmbik7XG4gIGlmKHRoYXQgPT09IHVuZGVmaW5lZClyZXR1cm4gZm47XG4gIHN3aXRjaChsZW5ndGgpe1xuICAgIGNhc2UgMTogcmV0dXJuIGZ1bmN0aW9uKGEpe1xuICAgICAgcmV0dXJuIGZuLmNhbGwodGhhdCwgYSk7XG4gICAgfTtcbiAgICBjYXNlIDI6IHJldHVybiBmdW5jdGlvbihhLCBiKXtcbiAgICAgIHJldHVybiBmbi5jYWxsKHRoYXQsIGEsIGIpO1xuICAgIH07XG4gICAgY2FzZSAzOiByZXR1cm4gZnVuY3Rpb24oYSwgYiwgYyl7XG4gICAgICByZXR1cm4gZm4uY2FsbCh0aGF0LCBhLCBiLCBjKTtcbiAgICB9O1xuICB9XG4gIHJldHVybiBmdW5jdGlvbigvKiAuLi5hcmdzICovKXtcbiAgICByZXR1cm4gZm4uYXBwbHkodGhhdCwgYXJndW1lbnRzKTtcbiAgfTtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19jdHguanNcbiAqKiBtb2R1bGUgaWQgPSAxMVxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwibW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihpdCl7XG4gIGlmKHR5cGVvZiBpdCAhPSAnZnVuY3Rpb24nKXRocm93IFR5cGVFcnJvcihpdCArICcgaXMgbm90IGEgZnVuY3Rpb24hJyk7XG4gIHJldHVybiBpdDtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19hLWZ1bmN0aW9uLmpzXG4gKiogbW9kdWxlIGlkID0gMTJcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsInZhciBkUCAgICAgICAgID0gcmVxdWlyZSgnLi9fb2JqZWN0LWRwJylcbiAgLCBjcmVhdGVEZXNjID0gcmVxdWlyZSgnLi9fcHJvcGVydHktZGVzYycpO1xubW9kdWxlLmV4cG9ydHMgPSByZXF1aXJlKCcuL19kZXNjcmlwdG9ycycpID8gZnVuY3Rpb24ob2JqZWN0LCBrZXksIHZhbHVlKXtcbiAgcmV0dXJuIGRQLmYob2JqZWN0LCBrZXksIGNyZWF0ZURlc2MoMSwgdmFsdWUpKTtcbn0gOiBmdW5jdGlvbihvYmplY3QsIGtleSwgdmFsdWUpe1xuICBvYmplY3Rba2V5XSA9IHZhbHVlO1xuICByZXR1cm4gb2JqZWN0O1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2hpZGUuanNcbiAqKiBtb2R1bGUgaWQgPSAxM1xuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwidmFyIGFuT2JqZWN0ICAgICAgID0gcmVxdWlyZSgnLi9fYW4tb2JqZWN0JylcbiAgLCBJRThfRE9NX0RFRklORSA9IHJlcXVpcmUoJy4vX2llOC1kb20tZGVmaW5lJylcbiAgLCB0b1ByaW1pdGl2ZSAgICA9IHJlcXVpcmUoJy4vX3RvLXByaW1pdGl2ZScpXG4gICwgZFAgICAgICAgICAgICAgPSBPYmplY3QuZGVmaW5lUHJvcGVydHk7XG5cbmV4cG9ydHMuZiA9IHJlcXVpcmUoJy4vX2Rlc2NyaXB0b3JzJykgPyBPYmplY3QuZGVmaW5lUHJvcGVydHkgOiBmdW5jdGlvbiBkZWZpbmVQcm9wZXJ0eShPLCBQLCBBdHRyaWJ1dGVzKXtcbiAgYW5PYmplY3QoTyk7XG4gIFAgPSB0b1ByaW1pdGl2ZShQLCB0cnVlKTtcbiAgYW5PYmplY3QoQXR0cmlidXRlcyk7XG4gIGlmKElFOF9ET01fREVGSU5FKXRyeSB7XG4gICAgcmV0dXJuIGRQKE8sIFAsIEF0dHJpYnV0ZXMpO1xuICB9IGNhdGNoKGUpeyAvKiBlbXB0eSAqLyB9XG4gIGlmKCdnZXQnIGluIEF0dHJpYnV0ZXMgfHwgJ3NldCcgaW4gQXR0cmlidXRlcyl0aHJvdyBUeXBlRXJyb3IoJ0FjY2Vzc29ycyBub3Qgc3VwcG9ydGVkIScpO1xuICBpZigndmFsdWUnIGluIEF0dHJpYnV0ZXMpT1tQXSA9IEF0dHJpYnV0ZXMudmFsdWU7XG4gIHJldHVybiBPO1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX29iamVjdC1kcC5qc1xuICoqIG1vZHVsZSBpZCA9IDE0XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJ2YXIgaXNPYmplY3QgPSByZXF1aXJlKCcuL19pcy1vYmplY3QnKTtcbm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oaXQpe1xuICBpZighaXNPYmplY3QoaXQpKXRocm93IFR5cGVFcnJvcihpdCArICcgaXMgbm90IGFuIG9iamVjdCEnKTtcbiAgcmV0dXJuIGl0O1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2FuLW9iamVjdC5qc1xuICoqIG1vZHVsZSBpZCA9IDE1XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJtb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGl0KXtcbiAgcmV0dXJuIHR5cGVvZiBpdCA9PT0gJ29iamVjdCcgPyBpdCAhPT0gbnVsbCA6IHR5cGVvZiBpdCA9PT0gJ2Z1bmN0aW9uJztcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19pcy1vYmplY3QuanNcbiAqKiBtb2R1bGUgaWQgPSAxNlxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwibW9kdWxlLmV4cG9ydHMgPSAhcmVxdWlyZSgnLi9fZGVzY3JpcHRvcnMnKSAmJiAhcmVxdWlyZSgnLi9fZmFpbHMnKShmdW5jdGlvbigpe1xyXG4gIHJldHVybiBPYmplY3QuZGVmaW5lUHJvcGVydHkocmVxdWlyZSgnLi9fZG9tLWNyZWF0ZScpKCdkaXYnKSwgJ2EnLCB7Z2V0OiBmdW5jdGlvbigpeyByZXR1cm4gNzsgfX0pLmEgIT0gNztcclxufSk7XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19pZTgtZG9tLWRlZmluZS5qc1xuICoqIG1vZHVsZSBpZCA9IDE3XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCIvLyBUaGFuaydzIElFOCBmb3IgaGlzIGZ1bm55IGRlZmluZVByb3BlcnR5XG5tb2R1bGUuZXhwb3J0cyA9ICFyZXF1aXJlKCcuL19mYWlscycpKGZ1bmN0aW9uKCl7XG4gIHJldHVybiBPYmplY3QuZGVmaW5lUHJvcGVydHkoe30sICdhJywge2dldDogZnVuY3Rpb24oKXsgcmV0dXJuIDc7IH19KS5hICE9IDc7XG59KTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2Rlc2NyaXB0b3JzLmpzXG4gKiogbW9kdWxlIGlkID0gMThcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsIm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oZXhlYyl7XG4gIHRyeSB7XG4gICAgcmV0dXJuICEhZXhlYygpO1xuICB9IGNhdGNoKGUpe1xuICAgIHJldHVybiB0cnVlO1xuICB9XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZmFpbHMuanNcbiAqKiBtb2R1bGUgaWQgPSAxOVxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNSA2IDcgOCA5IDEwIDExIDEyXG4gKiovIiwidmFyIGlzT2JqZWN0ID0gcmVxdWlyZSgnLi9faXMtb2JqZWN0JylcbiAgLCBkb2N1bWVudCA9IHJlcXVpcmUoJy4vX2dsb2JhbCcpLmRvY3VtZW50XG4gIC8vIGluIG9sZCBJRSB0eXBlb2YgZG9jdW1lbnQuY3JlYXRlRWxlbWVudCBpcyAnb2JqZWN0J1xuICAsIGlzID0gaXNPYmplY3QoZG9jdW1lbnQpICYmIGlzT2JqZWN0KGRvY3VtZW50LmNyZWF0ZUVsZW1lbnQpO1xubW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihpdCl7XG4gIHJldHVybiBpcyA/IGRvY3VtZW50LmNyZWF0ZUVsZW1lbnQoaXQpIDoge307XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZG9tLWNyZWF0ZS5qc1xuICoqIG1vZHVsZSBpZCA9IDIwXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCIvLyA3LjEuMSBUb1ByaW1pdGl2ZShpbnB1dCBbLCBQcmVmZXJyZWRUeXBlXSlcbnZhciBpc09iamVjdCA9IHJlcXVpcmUoJy4vX2lzLW9iamVjdCcpO1xuLy8gaW5zdGVhZCBvZiB0aGUgRVM2IHNwZWMgdmVyc2lvbiwgd2UgZGlkbid0IGltcGxlbWVudCBAQHRvUHJpbWl0aXZlIGNhc2Vcbi8vIGFuZCB0aGUgc2Vjb25kIGFyZ3VtZW50IC0gZmxhZyAtIHByZWZlcnJlZCB0eXBlIGlzIGEgc3RyaW5nXG5tb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGl0LCBTKXtcbiAgaWYoIWlzT2JqZWN0KGl0KSlyZXR1cm4gaXQ7XG4gIHZhciBmbiwgdmFsO1xuICBpZihTICYmIHR5cGVvZiAoZm4gPSBpdC50b1N0cmluZykgPT0gJ2Z1bmN0aW9uJyAmJiAhaXNPYmplY3QodmFsID0gZm4uY2FsbChpdCkpKXJldHVybiB2YWw7XG4gIGlmKHR5cGVvZiAoZm4gPSBpdC52YWx1ZU9mKSA9PSAnZnVuY3Rpb24nICYmICFpc09iamVjdCh2YWwgPSBmbi5jYWxsKGl0KSkpcmV0dXJuIHZhbDtcbiAgaWYoIVMgJiYgdHlwZW9mIChmbiA9IGl0LnRvU3RyaW5nKSA9PSAnZnVuY3Rpb24nICYmICFpc09iamVjdCh2YWwgPSBmbi5jYWxsKGl0KSkpcmV0dXJuIHZhbDtcbiAgdGhyb3cgVHlwZUVycm9yKFwiQ2FuJ3QgY29udmVydCBvYmplY3QgdG8gcHJpbWl0aXZlIHZhbHVlXCIpO1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX3RvLXByaW1pdGl2ZS5qc1xuICoqIG1vZHVsZSBpZCA9IDIxXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkgMTAgMTEgMTJcbiAqKi8iLCJtb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGJpdG1hcCwgdmFsdWUpe1xuICByZXR1cm4ge1xuICAgIGVudW1lcmFibGUgIDogIShiaXRtYXAgJiAxKSxcbiAgICBjb25maWd1cmFibGU6ICEoYml0bWFwICYgMiksXG4gICAgd3JpdGFibGUgICAgOiAhKGJpdG1hcCAmIDQpLFxuICAgIHZhbHVlICAgICAgIDogdmFsdWVcbiAgfTtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19wcm9wZXJ0eS1kZXNjLmpzXG4gKiogbW9kdWxlIGlkID0gMjJcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDUgNiA3IDggOSAxMCAxMSAxMlxuICoqLyIsIid1c2Ugc3RyaWN0JztcblxuaW1wb3J0IE5vdGlmQ29tbWVyY2lhbCBmcm9tIFwiLi9sYXlvdXQvX25vdGlmaWNhdGlvbi1jb21tZXJjaWFsXCI7XG5pbXBvcnQgU2Nyb2xsVG9FbGVtZW50IGZyb20gXCIuL2xheW91dC9fc2Nyb2xsLXRvXCI7XG5pbXBvcnQgTm90aWZpY2F0aW9uTGlzdCBmcm9tIFwiLi9sYXlvdXQvX25vdGlmYXRpb24tbGlzdFwiO1xuaW1wb3J0IFByZXZpZXdNYXJrZG93biBmcm9tIFwiLi9wbHVnaW5zL21hcmtkb3duL19wbHVnaW5cIjtcblxuJChmdW5jdGlvbigpe1xuICAgIFNjcm9sbFRvRWxlbWVudC5wbHVnaW4oJy5qcy1saW5rLXRhcmdldCcpO1xuICAgIE5vdGlmaWNhdGlvbkxpc3QucGx1Z2luKCcuanMtbm90aWZpY2F0aW9uLWxpc3QnKTtcbiAgICBQcmV2aWV3TWFya2Rvd24ucGx1Z2luKCdbbWFya2Rvd25wcmV2aWV3XScpO1xuICAgIE5vdGlmQ29tbWVyY2lhbC5wbHVnaW4oJy5qcy1ub3RpZi1jb21tZXJjaWFsJyk7XG5cbiAgICBjb25zdCAkZGF0YUZpZWxkID0gJCgnW2RhdGEtdHlwZT1cImRhdGVcIl0nKTtcbiAgICAkZGF0YUZpZWxkLmxlbmd0aCAmJiAkZGF0YUZpZWxkLmRhdGV0aW1lcGlja2VyKHtcbiAgICAgICAgdXNlQ3VycmVudDogZmFsc2UsXG4gICAgICAgIHBpY2tUaW1lOiBmYWxzZVxuICAgIH0pO1xuXG5cbn0pO1xuXG5cblxuXG4vKiogV0VCUEFDSyBGT09URVIgKipcbiAqKiAuL2Zyb250ZW5kL2pzL2NvbW1vbi1wYWNrLmpzXG4gKiovIiwiJ3VzZSBzdHJpY3QnO1xuXG5leHBvcnQgZGVmYXVsdCBjbGFzcyBXaWRnZXQge1xuICAgIC8qKlxuICAgICAqIEZpbHRlciBoaXN0b3J5XG4gICAgICogQHBhcmFtIHtTdHJpbmd9IHNlbGVjdG9yXG4gICAgICovXG4gICAgY29uc3RydWN0b3Ioc2VsZWN0b3IpIHtcbiAgICAgICAgdGhpcy4kcm9vdCA9ICQoc2VsZWN0b3IpO1xuICAgICAgICB0aGlzLmxvY2FscyA9IHRoaXMuX2dldERvbSgpO1xuICAgICAgICB0aGlzLnVuaXF1ZUtleSA9ICdub3RpZmljdGlvbic7XG5cbiAgICAgICAgaWYgKCF0aGlzLl9pc1Nob3dlZCgpKSB7XG4gICAgICAgICAgICB0aGlzLiRyb290LnNsaWRlRG93bigpO1xuICAgICAgICB9XG4gICAgICAgIHRoaXMuX2Fzc2lnbkV2ZW50cygpO1xuICAgIH1cblxuICAgIF9nZXREb20oKSB7XG4gICAgICAgIGNvbnN0ICRyb290ID0gdGhpcy4kcm9vdDtcblxuICAgICAgICByZXR1cm4ge1xuICAgICAgICAgICAgJGxpc3Q6ICRyb290LmZpbmQoJ1tkYXRhLWZpbHRlci1saXN0XScpLFxuICAgICAgICAgICAgJGl0ZW1zOiAkcm9vdC5maW5kKCdbZGF0YS1maWx0ZXItdGV4dF0nKSxcbiAgICAgICAgfTtcbiAgICB9XG5cbiAgICBfYXNzaWduRXZlbnRzKCkge1xuICAgICAgICB0aGlzLiRyb290Lm9uKCdjbGljaycsICdbZGF0YS1ub3RpZl0nLCB0aGlzLl9vbkNsaWNrQnRuLmJpbmQodGhpcykpO1xuICAgIH1cblxuICAgIF9vbkNsaWNrQnRuKGUpe1xuICAgICAgICBlLnByZXZlbnREZWZhdWx0KCk7XG4gICAgICAgIGNvbnN0IHNlbGYgPSB0aGlzO1xuICAgICAgICBjb25zdCAkbGluayA9ICQoZS5jdXJyZW50VGFyZ2V0KTtcbiAgICAgICAgY29uc3QgdXJsID0gJGxpbmsuYXR0cignaHJlZicpO1xuXG4gICAgICAgIHNlbGYuX3NlbmRJc1Nob3dlZCh1cmwpXG4gICAgICAgICAgICAuZG9uZShmdW5jdGlvbigpe1xuICAgICAgICAgICAgICAgIHNlbGYuJHJvb3QuYWRkQ2xhc3MoJ2Itbm90aWZpY2F0aW9uX3N0YXRlX3RoYW5rJyk7XG4gICAgICAgICAgICAgICAgXG4gICAgICAgICAgICAgICAgc2V0VGltZW91dChmdW5jdGlvbigpe1xuICAgICAgICAgICAgICAgICAgICBzZWxmLmhpZGUoKTtcbiAgICAgICAgICAgICAgICB9LCAzMDAwKTtcbiAgICAgICAgICAgIH0pO1xuICAgIH1cblxuICAgIF9pc1Nob3dlZCgpIHtcbiAgICAgICAgY29uc3QgdmFsdWUgPSBsb2NhbFN0b3JhZ2UuZ2V0SXRlbSh0aGlzLnVuaXF1ZUtleSk7XG4gICAgICAgIHJldHVybiB2YWx1ZSAmJiB2YWx1ZSA9PSAnc2hvd2VkJztcbiAgICB9XG5cbiAgICBfc2VuZElzU2hvd2VkKHVybCkge1xuICAgICAgICBjb25zdCBzZWxmID0gdGhpcztcbiAgICAgICAgbGV0IGRlZmVyID0gJC5EZWZlcnJlZCgpO1xuXG4gICAgICAgICQucG9zdCh1cmwsIGZ1bmN0aW9uKCl7XG4gICAgICAgICAgICBsb2NhbFN0b3JhZ2Uuc2V0SXRlbShzZWxmLnVuaXF1ZUtleSwgJ3Nob3dlZCcpO1xuICAgICAgICAgICAgZGVmZXIucmVzb2x2ZSgpO1xuICAgICAgICB9KTtcbiAgICAgICAgXG4gICAgICAgIHJldHVybiBkZWZlci5wcm9taXNlKCk7XG4gICAgfVxuXG4gICAgaGlkZSgpIHtcbiAgICAgICAgdGhpcy4kcm9vdC5zbGlkZVVwKCk7XG4gICAgfVxuXG4gICAgLy8gc3RhdGljXG4gICAgc3RhdGljIHBsdWdpbihzZWxlY3Rvcikge1xuICAgICAgICBjb25zdCAkZWxlbXMgPSAkKHNlbGVjdG9yKTtcbiAgICAgICAgaWYgKCEkZWxlbXMubGVuZ3RoKSByZXR1cm47XG5cbiAgICAgICAgcmV0dXJuICRlbGVtcy5lYWNoKGZ1bmN0aW9uIChpbmRleCwgZWwpIHtcbiAgICAgICAgICAgIGxldCAkZWxlbWVudCA9ICQoZWwpO1xuICAgICAgICAgICAgbGV0IGRhdGEgPSAkZWxlbWVudC5kYXRhKCd3aWRnZXQnKTtcblxuICAgICAgICAgICAgaWYgKCFkYXRhKSB7XG4gICAgICAgICAgICAgICAgZGF0YSA9IG5ldyBXaWRnZXQoZWwpO1xuICAgICAgICAgICAgICAgICRlbGVtZW50LmRhdGEoJ3dpZGdldCcsIGRhdGEpO1xuICAgICAgICAgICAgfVxuICAgICAgICB9KVxuICAgIH1cbn1cblxuXG5cbi8qKiBXRUJQQUNLIEZPT1RFUiAqKlxuICoqIC4vZnJvbnRlbmQvanMvbGF5b3V0L19ub3RpZmljYXRpb24tY29tbWVyY2lhbC5qc1xuICoqLyIsIid1c2Ugc3RyaWN0JztcblxuZXhwb3J0IGRlZmF1bHQgY2xhc3MgV2lkZ2V0IHtcbiAgICAvKipcbiAgICAgKiBGaWx0ZXIgaGlzdG9yeVxuICAgICAqIEBwYXJhbSB7U3RyaW5nfSBzZWxlY3RvclxuICAgICAqL1xuICAgIGNvbnN0cnVjdG9yKHNlbGVjdG9yKSB7XG4gICAgICAgIHRoaXMuJHJvb3QgPSAkKHNlbGVjdG9yKTtcbiAgICAgICAgdGhpcy5fYXNzaWduRXZlbnRzKCk7XG4gICAgfVxuXG4gICAgX2Fzc2lnbkV2ZW50cygpIHtcbiAgICAgICAgdGhpcy4kcm9vdC5vbignY2xpY2snLCB0aGlzLl9vbkNsaWNrTGluay5iaW5kKHRoaXMpKTtcbiAgICB9XG5cbiAgICBfb25DbGlja0xpbmsoZSkge1xuICAgICAgICBjb25zdCAkbGluayA9ICQoZS5jdXJyZW50VGFyZ2V0KTtcbiAgICAgICAgY29uc3QgdGFyZ2V0ID0gJGxpbmsuZGF0YSgndGFyZ2V0Jyk7XG5cbiAgICAgICAgaWYgKCF0aGlzLl9pc1RhcmdldFZhbGlkKHRhcmdldCkpIHJldHVybiBmYWxzZTtcblxuICAgICAgICB0aGlzLnNjcm9sbFRvVGFyZ2V0KCcjJyArIHRhcmdldCk7XG4gICAgICAgIGUucHJldmVudERlZmF1bHQoKTtcbiAgICB9XG5cbiAgICBfaXNUYXJnZXRWYWxpZCh0YXJnZXQpe1xuICAgICAgICBsZXQgdmFsaWQgPSB0cnVlO1xuXG4gICAgICAgIGlmICghdGFyZ2V0KSB7XG4gICAgICAgICAgICBjb25zb2xlLmxvZygnVGhlcmUgaXMgbm8gZGF0YS10YXJnZXQgYXR0cmlidXRlIHdpdGggaWQtbmFtZSBmb3IgdGhpcyBsaW5rJyk7XG4gICAgICAgICAgICB2YWxpZCA9IGZhbHNlO1xuICAgICAgICB9XG5cbiAgICAgICAgaWYgKCEkKCcjJyArIHRhcmdldCkubGVuZ3RoKSB7XG4gICAgICAgICAgICBjb25zb2xlLmxvZygnVGhlcmUgaXMgbm8gZWxlbWVudCB3aXRoIHN1Y2ggaWQgbmFtZScpO1xuICAgICAgICAgICAgdmFsaWQgPSBmYWxzZTtcbiAgICAgICAgfVxuXG4gICAgICAgIHJldHVybiB2YWxpZDtcbiAgICB9XG5cbiAgICAvKipcbiAgICAgKiBTY3JvbGwgdG8gdGhlIGVsZW1lbnQgd2l0aCBcInRhcmdldFwiIGlkXG4gICAgICogQHBhcmFtIHtTdHJpbmd9IHRhcmdldCAtIGlkIHNlbGVjdG9yIG9mIGVsZW1lbnRcbiAgICAgKiBAcmV0dXJucyB7Ym9vbGVhbn1cbiAgICAgKi9cbiAgICBzY3JvbGxUb1RhcmdldCh0YXJnZXQpIHtcbiAgICAgICAgY29uc3QgJHRhcmdldCA9ICQodGFyZ2V0KTtcblxuICAgICAgICBpZiAoISR0YXJnZXQubGVuZ3RoKSByZXR1cm4gZmFsc2U7XG5cbiAgICAgICAgJCgnaHRtbCwgYm9keScpLmFuaW1hdGUoe1xuICAgICAgICAgICAgc2Nyb2xsVG9wOiAkdGFyZ2V0Lm9mZnNldCgpLnRvcFxuICAgICAgICB9LCA0MDApO1xuICAgIH1cblxuICAgIC8vIHN0YXRpY1xuICAgIHN0YXRpYyBwbHVnaW4oc2VsZWN0b3IpIHtcbiAgICAgICAgY29uc3QgJGVsZW1zID0gJChzZWxlY3Rvcik7XG4gICAgICAgIGlmICghJGVsZW1zLmxlbmd0aCkgcmV0dXJuO1xuXG4gICAgICAgIHJldHVybiAkZWxlbXMuZWFjaChmdW5jdGlvbiAoaW5kZXgsIGVsKSB7XG4gICAgICAgICAgICBsZXQgJGVsZW1lbnQgPSAkKGVsKTtcbiAgICAgICAgICAgIGxldCBkYXRhICAgICA9ICRlbGVtZW50LmRhdGEoJ3dpZGdldC5zY3JvbGx0bycpO1xuXG4gICAgICAgICAgICBpZiAoIWRhdGEpIHtcbiAgICAgICAgICAgICAgICBkYXRhID0gbmV3IFdpZGdldChlbCk7XG4gICAgICAgICAgICAgICAgJGVsZW1lbnQuZGF0YSgnd2lkZ2V0JywgZGF0YSk7XG4gICAgICAgICAgICB9XG4gICAgICAgIH0pXG4gICAgfVxufVxuXG5cblxuLyoqIFdFQlBBQ0sgRk9PVEVSICoqXG4gKiogLi9mcm9udGVuZC9qcy9sYXlvdXQvX3Njcm9sbC10by5qc1xuICoqLyIsIid1c2Ugc3RyaWN0JztcblxuLyoqXG4gKiBOb3RpZmljYXRpb24gZm9yIHVzZXIgYWJvdXQgbmV3IGV2ZW50c1xuICovXG5cbi8qKlxuICogTm90aWZpY2F0aW9uIG9iamVjdFxuICogQHR5cGVkZWYge09iamVjdH0gTm90aWZpY2F0aW9uSXRlbVxuICogQHByb3BlcnR5IHtTdHJpbmd9IGJvZHkgICAgICAtIGh0bWwgb2YgdGhlIG5vdGlmaWNhdGlvblxuICogQHByb3BlcnR5IHtCb29sZW59IHVucmVhZCAgICAtIGlzIGN1cnJlbnQgbm90aWZpY2F0aW9uIGFscmVhZHkgdmlld2VkP1xuICogQHByb3BlcnR5IHtOdW1iZXJ9IGlkICAgICAgICAtIGlkIG9mIHRoZSBub3RpZmljYXRpb25cbiAqIEBwcm9wZXJ0eSB7U3RyaW5nfSB0eXBlICAgICAgLSB0eXBlIG9mIHRoZSBub3RpZmljYXRpb25cbiAqL1xuXG5leHBvcnQgZGVmYXVsdCBjbGFzcyBXaWRnZXQge1xuICAgIC8qKlxuICAgICAqXG4gICAgICogQHBhcmFtIHsoU3RyaW5nfGRvbUVsZW1lbnQpfSBzZWxlY3RvciAgLSBzZWxlY3RvciBvciBkb21FbGVtZW50IGFzIHJvb3QgZWxlbWVudCBvZiB0aGUgd2lkZ2V0XG4gICAgICogQGNvbnN0cnVjdG9yXG4gICAgICovXG4gICAgY29uc3RydWN0b3Ioc2VsZWN0b3IpIHtcbiAgICAgICAgY29uc3Qgc2VsZiA9IHRoaXM7XG5cbiAgICAgICAgc2VsZi4kcm9vdCA9ICQoc2VsZWN0b3IpO1xuICAgICAgICBzZWxmLmxvY2FscyA9IHNlbGYuX2dldERvbSgpO1xuXG4gICAgICAgIHNlbGYuX2luaXQoKTtcbiAgICAgICAgc2VsZi5fYXNzaWduRXZlbnRzKCk7XG4gICAgfVxuXG4gICAgLyoqXG4gICAgICogR2V0ICQgZWxlbWVudHMgb2YgdGhlIHdpZGdldFxuICAgICAqIEByZXR1cm5zIHtPYmplY3R9IC0galF1ZXJ5IGxpbmtzIG90IHRoZSBlbGVtZW50cyBvZiB0aGUgd2lkZ2V0XG4gICAgICogQHByaXZhdGVcbiAgICAgKi9cbiAgICBfZ2V0RG9tKCkge1xuICAgICAgICBjb25zdCAkcm9vdCA9IHRoaXMuJHJvb3Q7XG5cbiAgICAgICAgcmV0dXJuIHtcbiAgICAgICAgICAgICRsaXN0OiAkcm9vdC5maW5kKCdbZGF0YS1ub3RpZmxpc3QtbGlzdF0nKSxcbiAgICAgICAgICAgICRjbG9zZTogJHJvb3QuZmluZCgnW2RhdGEtbm90aWZsaXN0LWNsb3NlXScpLFxuICAgICAgICAgICAgJGxpbms6ICRyb290LmZpbmQoJ1tkYXRhLW5vdGlmbGlzdC1zaG93XScpLFxuICAgICAgICAgICAgJGxvYWQ6ICRyb290LmZpbmQoJ1tkYXRhLW5vdGlmbGlzdC1sb2FkXScpLFxuICAgICAgICAgICAgJGNvdW50OiAkcm9vdC5maW5kKCdbZGF0YS1ub3RpZmxpc3QtY291bnRdJyksXG4gICAgICAgIH07XG4gICAgfVxuXG4gICAgLyoqXG4gICAgICogSW5pdGlhdGUgYmFzZSB2YXJpYWJsZXMgZm9yIHdpZGdldFxuICAgICAqIEBwcml2YXRlXG4gICAgICovXG4gICAgX2luaXQoKSB7XG4gICAgICAgIGNvbnN0IHNlbGYgPSB0aGlzO1xuXG4gICAgICAgIHNlbGYuaXNMb2FkZWQgPSBmYWxzZTtcbiAgICAgICAgc2VsZi5vZmZzZXQgPSAwO1xuICAgICAgICBzZWxmLmlzVmlzaWJsZSA9IGZhbHNlO1xuXG4gICAgICAgIHNlbGYuX3JlY2lldmVVbnJlYWRDb3VudCgpXG4gICAgICAgICAgICAuZG9uZShmdW5jdGlvbiAoY291bnQpIHtcbiAgICAgICAgICAgICAgICBzZWxmLnNldFVucmVhZENvdW50KGNvdW50KTtcbiAgICAgICAgICAgIH0pO1xuICAgIH1cblxuICAgIF9hc3NpZ25FdmVudHMoKSB7XG4gICAgICAgIGNvbnN0IHNlbGYgPSB0aGlzO1xuXG4gICAgICAgIHNlbGYuJHJvb3RcbiAgICAgICAgICAgIC5vbignY2xpY2snLCAnW2RhdGEtbm90aWZsaXN0LXNob3ddLCBbZGF0YS1ub3RpZmxpc3QtY2xvc2VdJywgc2VsZi5fb25DbGlja1RvZ2dsZVNob3cuYmluZChzZWxmKSlcbiAgICAgICAgICAgIC5vbignY2xpY2snLCAnW2RhdGEtbm90aWZsaXN0LWxvYWRdJywgc2VsZi5fb25DbGlja0xvYWROb3RpZmljYXRpb24uYmluZChzZWxmKSlcbiAgICB9XG5cbiAgICAvKipcbiAgICAgKiBFdmVudCBoYW5kbGVyIGZvciBzaG93aW5nIGFuZCBoaWRpbmcgcG9wdXBcbiAgICAgKiBAcGFyYW0ge0V2ZW50fSBlIC0gRXZlbnQgT2JqZWN0XG4gICAgICogQHByaXZhdGVcbiAgICAgKi9cbiAgICBfb25DbGlja1RvZ2dsZVNob3coZSl7XG4gICAgICAgIHRoaXMudG9nZ2xlUG9wdXAoKTtcblxuICAgICAgICBpZiAoIXRoaXMuaXNMb2FkZWQpe1xuICAgICAgICAgICAgdGhpcy5pc0xvYWRlZCAgPSB0cnVlO1xuICAgICAgICAgICAgdGhpcy5sb2FkTm90aWZpY2F0aW9uKHRoaXMub2Zmc2V0KTtcbiAgICAgICAgfVxuXG4gICAgICAgIGUucHJldmVudERlZmF1bHQoKTtcbiAgICB9O1xuXG4gICAgLyoqXG4gICAgICogRXZlbnQgaGFuZGxlciBmb3IgdGhlIGJ1dHRvbiBcImxvYWQgbW9yZSBub3RpZmljYXRpb25cIlxuICAgICAqIEBwYXJhbSB7RXZlbnR9IGUgLSBFdmVudCBPYmplY3RcbiAgICAgKiBAcHJpdmF0ZVxuICAgICAqL1xuICAgIF9vbkNsaWNrTG9hZE5vdGlmaWNhdGlvbihlKXtcbiAgICAgICAgZS5wcmV2ZW50RGVmYXVsdCgpO1xuXG4gICAgICAgIHRoaXMubG9hZE5vdGlmaWNhdGlvbih0aGlzLm9mZnNldCk7XG4gICAgfTtcblxuICAgIC8qKlxuICAgICAqIEFkZCB0byB0aGUgZG9tXG4gICAgICogQHBhcmFtIHtOb3RpZmljYXRpb25JdGVtW119IG5vdGlmTGlzdCAtIGxpc3Qgb2YgdGhlIG5vdGlmaWNhdGlvblxuICAgICAqIEBwcml2YXRlXG4gICAgICovXG4gICAgX3JlbmRlck5vdGlmaWNhdGlvbihub3RpZkxpc3Qpe1xuICAgICAgICBjb25zdCBzZWxmID0gdGhpcztcblxuICAgICAgICBub3RpZkxpc3QuZm9yRWFjaChmdW5jdGlvbihpdGVtKXtcbiAgICAgICAgICAgICQoaXRlbS5ib2R5KVxuICAgICAgICAgICAgICAgIC5hZGRDbGFzcyhpdGVtLnR5cGUpXG4gICAgICAgICAgICAgICAgLnRvZ2dsZUNsYXNzKCdpcy1uZXcnLCBCb29sZWFuKGl0ZW0udW5yZWFkKSlcbiAgICAgICAgICAgICAgICAuZGF0YSgnaWQnLCBpdGVtLmlkKVxuICAgICAgICAgICAgICAgIC5hcHBlbmRUbyhzZWxmLmxvY2Fscy4kbGlzdCk7XG4gICAgICAgIH0pXG4gICAgfVxuXG4gICAgLyoqXG4gICAgICogRmlsdGVyIG5vdGlmaWNhdGlvbiBhbmQgcmV0dXJuIG9ubHkgbmV3XG4gICAgICogQHBhcmFtIHtOb3RpZmljYXRpb25JdGVtW119IG5vdGlmTGlzdCAtIGxpc3Qgb2YgdGhlIG5vdGlmaWNhdGlvblxuICAgICAqIEByZXR1cm5zIHtOb3RpZmljYXRpb25JdGVtW119IG5vdGlmTGlzdCAtIG9ubHkgbmV3IG5vdGlmaWNhdGlvbnNcbiAgICAgKiBAcHJpdmF0ZVxuICAgICAqL1xuICAgIF9maWx0ZXJPbmx5TmV3KG5vdGlmTGlzdCl7XG4gICAgICAgIHJldHVybiBub3RpZkxpc3QuZmlsdGVyKGZ1bmN0aW9uKGl0ZW0pe1xuICAgICAgICAgICAgcmV0dXJuIGl0ZW0udW5yZWFkO1xuICAgICAgICB9KVxuICAgIH07XG5cbiAgICBfaXNIYXZlTm90aWZpY2F0aW9uKG5vdGlmTGlzdCl7XG4gICAgICAgIGlmICghbm90aWZMaXN0Lmxlbmd0aCAmJiAhdGhpcy5vZmZzZXQpIHtcbiAgICAgICAgICAgIHRoaXMuJHJvb3QuYWRkQ2xhc3MoJ2Itbm90aWZsaXN0X2VtcHR5Jyk7XG4gICAgICAgICAgICByZXR1cm4gZmFsc2U7XG4gICAgICAgIH1cblxuICAgICAgICBpZiAobm90aWZMaXN0Lmxlbmd0aCA8IDUpe1xuICAgICAgICAgICAgdGhpcy4kcm9vdC5hZGRDbGFzcygnYi1ub3RpZmxpc3RfbG9hZF9hbGwnKTtcbiAgICAgICAgfVxuICAgICAgICByZXR1cm4gdHJ1ZTtcbiAgICB9XG5cbiAgICAvKipcbiAgICAgKiBHZXQgbmV3IGxvYWQgbm90aWZpY2F0aW9uIGFuZCByZW5kZXIgdGhlbVxuICAgICAqIEBwYXJhbSB7TnVtYmVyfSBvZmZzZXQgLVxuICAgICAqL1xuICAgIGxvYWROb3RpZmljYXRpb24ob2Zmc2V0KXtcbiAgICAgICAgY29uc3Qgc2VsZiA9IHRoaXM7XG5cbiAgICAgICAgc2VsZi5fcmVjaWV2ZU5vdGlmaWNhdGlvbihvZmZzZXQpXG4gICAgICAgICAgICAuZG9uZShmdW5jdGlvbihub3RpZkxpc3Qpe1xuICAgICAgICAgICAgICAgIGlmICghc2VsZi5faXNIYXZlTm90aWZpY2F0aW9uKG5vdGlmTGlzdCkpIHJldHVybjtcblxuICAgICAgICAgICAgICAgIHNlbGYub2Zmc2V0ICs9IG5vdGlmTGlzdC5sZW5ndGg7XG4gICAgICAgICAgICAgICAgc2VsZi5fcmVuZGVyTm90aWZpY2F0aW9uKG5vdGlmTGlzdCk7XG5cbiAgICAgICAgICAgICAgICB2YXIgbmV3Tm90aWZMaXN0ID0gc2VsZi5fZmlsdGVyT25seU5ldyhub3RpZkxpc3QpO1xuXG4gICAgICAgICAgICAgICAgaWYgKCFuZXdOb3RpZkxpc3QubGVuZ3RoKSByZXR1cm47XG5cbiAgICAgICAgICAgICAgICBzZWxmLl9zZW5kVmlld2VkTmV3Tm90aWYobmV3Tm90aWZMaXN0KTtcbiAgICAgICAgICAgICAgICBzZWxmLnNldFVucmVhZENvdW50KHNlbGYudW5yZWFkQ291bnQgLSBuZXdOb3RpZkxpc3QubGVuZ3RoKTtcbiAgICAgICAgICAgIH0pO1xuICAgIH1cblxuICAgIC8qKlxuICAgICAqIFNldCB1bnJlYWQgY291bnRcbiAgICAgKiBAcGFyYW0ge051bWJlcn0gY291bnQgLSBuZXcgdmFsdWUgZm9yIHVucmVhZCBjb3VudFxuICAgICAqL1xuICAgIHNldFVucmVhZENvdW50KGNvdW50KXtcbiAgICAgICAgdGhpcy51bnJlYWRDb3VudCA9IChjb3VudCA+IDApPyBjb3VudDogMDtcblxuICAgICAgICB0aGlzLmxvY2Fscy4kY291bnQudGV4dCh0aGlzLnVucmVhZENvdW50KTtcbiAgICAgICAgdGhpcy4kcm9vdC50b2dnbGVDbGFzcygnYi1ub3RpZmxpc3RfaGF2ZV9ub3RpZmljYXRpb24nLCBCb29sZWFuKHRoaXMudW5yZWFkQ291bnQpKTtcbiAgICB9XG5cbiAgICBzaG93UG9wdXAoKXtcbiAgICAgICAgaWYgKHRoaXMuaXNWaXNpYmxlKSByZXR1cm47XG5cbiAgICAgICAgdGhpcy5pc1Zpc2libGUgPSB0cnVlO1xuICAgICAgICB0aGlzLiRyb290LmFkZENsYXNzKCdiLW5vdGlmbGlzdF9zaG93Jyk7XG4gICAgfVxuXG4gICAgaGlkZVBvcHVwKCl7XG4gICAgICAgIGlmICghdGhpcy5pc1Zpc2libGUpIHJldHVybjtcblxuICAgICAgICB0aGlzLmlzVmlzaWJsZSA9IGZhbHNlO1xuICAgICAgICB0aGlzLiRyb290LnJlbW92ZUNsYXNzKCdiLW5vdGlmbGlzdF9zaG93Jyk7XG4gICAgfVxuXG4gICAgdG9nZ2xlUG9wdXAoKXtcbiAgICAgICAgdGhpcy5pc1Zpc2libGU/IHRoaXMuaGlkZVBvcHVwKCk6IHRoaXMuc2hvd1BvcHVwKCk7XG4gICAgfVxuXG4gICAgLy8gdHJhbnNwb3J0XG4gICAgX3JlY2lldmVVbnJlYWRDb3VudCgpe1xuICAgICAgICBsZXQgZGVmZXIgPSAkLkRlZmVycmVkKCk7XG4gICAgICAgIGNvbnN0IHVybCA9IGpzUm91dGVzLmNvbnRyb2xsZXJzLmNvcmUuTm90aWZpY2F0aW9ucy51bnJlYWQoKS51cmw7XG5cbiAgICAgICAgJC5nZXQodXJsLCBmdW5jdGlvbiAoZGF0YSkge1xuICAgICAgICAgICAgdmFyIGNvdW50ID0gKCQucGFyc2VKU09OKGRhdGEpKS51bnJlYWQ7XG4gICAgICAgICAgICBkZWZlci5yZXNvbHZlKGNvdW50KTtcbiAgICAgICAgfSk7XG5cbiAgICAgICAgcmV0dXJuIGRlZmVyLnByb21pc2UoKTtcbiAgICB9XG5cbiAgICBfcmVjaWV2ZU5vdGlmaWNhdGlvbihvZmZzZXQpe1xuICAgICAgICBsZXQgZGVmZXIgPSAkLkRlZmVycmVkKCk7XG4gICAgICAgIGNvbnN0IGxpbWl0ID0gNTtcbiAgICAgICAgY29uc3QgdXJsID0ganNSb3V0ZXMuY29udHJvbGxlcnMuY29yZS5Ob3RpZmljYXRpb25zLmxpc3Qob2Zmc2V0LCBsaW1pdCkudXJsO1xuXG4gICAgICAgICQuZ2V0KHVybCwgZnVuY3Rpb24oZGF0YSl7XG4gICAgICAgICAgICB2YXIgbm90aWZMaXN0ID0gJC5wYXJzZUpTT04oZGF0YSlbMF07XG5cbiAgICAgICAgICAgIGRlZmVyLnJlc29sdmUobm90aWZMaXN0KTtcbiAgICAgICAgfSk7XG5cbiAgICAgICAgcmV0dXJuIGRlZmVyLnByb21pc2UoKTtcbiAgICB9XG5cbiAgICAvKipcbiAgICAgKiBTZW5kIHRvIHRoZSBzZXJ2ZXIgaWQgb2YgdGhlIHZpZXdlZCBub3RpZmljYXRpb25cbiAgICAgKiBAcGFyYW0ge05vdGlmaWNhdGlvbkl0ZW1bXX0gbm90aWZMaXN0XG4gICAgICogQHByaXZhdGVcbiAgICAgKi9cbiAgICBfc2VuZFZpZXdlZE5ld05vdGlmKG5vdGlmTGlzdCl7XG4gICAgICAgIGxldCBpZHMgPSBbXTtcbiAgICAgICAgY29uc3QgdXJsID0ganNSb3V0ZXMuY29udHJvbGxlcnMuY29yZS5Ob3RpZmljYXRpb25zLnJlYWQoKS51cmw7XG5cbiAgICAgICAgbm90aWZMaXN0LmZvckVhY2goZnVuY3Rpb24oaXRlbSl7XG4gICAgICAgICAgICBpZHMucHVzaChpdGVtLmlkKTtcbiAgICAgICAgfSk7XG5cbiAgICAgICAgJC5wb3N0KHVybCwge2lkczogaWRzfSlcbiAgICB9O1xuXG4gIFxuICAgIC8vIHN0YXRpY1xuICAgIHN0YXRpYyBwbHVnaW4oc2VsZWN0b3IpIHtcbiAgICAgICAgY29uc3QgJGVsZW1zID0gJChzZWxlY3Rvcik7XG4gICAgICAgIGlmICghJGVsZW1zLmxlbmd0aCkgcmV0dXJuO1xuXG4gICAgICAgIHJldHVybiAkZWxlbXMuZWFjaChmdW5jdGlvbiAoaW5kZXgsIGVsKSB7XG4gICAgICAgICAgICBsZXQgJGVsZW1lbnQgPSAkKGVsKTtcbiAgICAgICAgICAgIGxldCBkYXRhICAgICA9ICRlbGVtZW50LmRhdGEoJ3dpZGdldC5zY3JvbGx0bycpO1xuXG4gICAgICAgICAgICBpZiAoIWRhdGEpIHtcbiAgICAgICAgICAgICAgICBkYXRhID0gbmV3IFdpZGdldChlbCk7XG4gICAgICAgICAgICAgICAgJGVsZW1lbnQuZGF0YSgnd2lkZ2V0JywgZGF0YSk7XG4gICAgICAgICAgICB9XG4gICAgICAgIH0pXG4gICAgfVxufVxuXG5cblxuLyoqIFdFQlBBQ0sgRk9PVEVSICoqXG4gKiogLi9mcm9udGVuZC9qcy9sYXlvdXQvX25vdGlmYXRpb24tbGlzdC5qc1xuICoqLyIsIid1c2Ugc3RyaWN0JztcblxuaW1wb3J0IGNvbmYgZnJvbSAnLi9fc2V0JztcbmltcG9ydCBwbHVnaW4gZnJvbSAnLi9fanF1ZXJ5Lm1hcmtpdHVwJztcblxuZXhwb3J0IGRlZmF1bHQgY2xhc3MgV2lkZ2V0IHtcblxuICAgIGNvbnN0cnVjdG9yKHNlbGVjdG9yKSB7XG4gICAgICAgIHRoaXMuJHJvb3QgPSAkKHNlbGVjdG9yKTtcbiAgICAgICAgdGhpcy5pc0VtYWlsUHJldmlldyA9IHRoaXMuJHJvb3QuZGF0YSgnbWFya2Rvd25wcmV2aWV3Jyk7XG4gICAgICAgIHRoaXMudXJsID0ganNSb3V0ZXMuY29udHJvbGxlcnMuVXRpbGl0aWVzLm1hcmtkb3duKCkudXJsO1xuXG4gICAgICAgIGlmICghJC5mbi5tYXJrSXRVcCl7XG4gICAgICAgICAgICBjb25zb2xlLmxvZygnaW52YWxpZCBkZXBlbmRlbmN5Jyk7XG4gICAgICAgICAgICByZXR1cm47XG4gICAgICAgIH1cbiAgICAgICAgdGhpcy5faW5pdCgpO1xuXG4gICAgICAgIHRoaXMubG9jYWxzID0gdGhpcy5fZ2V0RG9tKCk7XG4gICAgICAgIHRoaXMuX2Fzc2lnbkV2ZW50cygpO1xuICAgIH1cbiAgICBcbiAgICBfaW5pdCgpe1xuICAgICAgICBjb25zdCBsaW5rc1RlbXBsYXRlID0gJzxkaXYgY2xhc3M9XCJtYXJraXR1cF9fbGluayB0eXBlLXdyaXRlIHN0YXRlX2FjdGl2ZVwiPldyaXRlPC9kaXY+PGRpdiBjbGFzcz1cIm1hcmtpdHVwX19saW5rIHR5cGUtcHJldmlld1wiPlByZXZpZXc8L2Rpdj4gPGRpdiBjbGFzcz1cIm1hcmtpdHVwX19wcmV2aWV3XCI+PC9kaXY+JztcbiAgICAgICAgY29uc3QgJHJvb3QgPSB0aGlzLiRyb290O1xuXG4gICAgICAgICRyb290LndyYXAoJzxkaXYgY2xhc3M9XCJtYXJraXR1cF9fY29uXCI+PC9kaXY+JylcbiAgICAgICAgICAgIC5jbG9zZXN0KCcubWFya2l0dXBfX2NvbicpXG4gICAgICAgICAgICAudG9nZ2xlQ2xhc3MoJ21hcmtpdHVwX3N0YXRlX2VtYWlsJywgdGhpcy5pc0VtYWlsUHJldmlldylcblxuICAgICAgICAkcm9vdC5hZnRlcihsaW5rc1RlbXBsYXRlKVxuICAgICAgICAgICAgLm1hcmtJdFVwKGNvbmYpO1xuICAgIH1cblxuICAgIF9nZXREb20oKXtcbiAgICAgICAgY29uc3QgJGNvbnRhaW5lciA9IHRoaXMuJHJvb3QuY2xvc2VzdCgnLm1hcmtpdHVwX19jb24nKTtcblxuICAgICAgICByZXR1cm4ge1xuICAgICAgICAgICAgJGNvbnRhaW5lcjogJGNvbnRhaW5lcixcbiAgICAgICAgICAgICR0ZXh0YXJlYTogJGNvbnRhaW5lci5maW5kKCd0ZXh0YXJlYScpLFxuICAgICAgICAgICAgJHByZXZpZXc6ICRjb250YWluZXIuZmluZCgnLm1hcmtpdHVwX19wcmV2aWV3JylcbiAgICAgICAgfVxuICAgIH1cblxuICAgIF9hc3NpZ25FdmVudHMoKSB7XG4gICAgICAgIHRoaXMubG9jYWxzLiRjb250YWluZXJcbiAgICAgICAgICAgIC5vbignY2xpY2snLCAnLm1hcmtpdHVwX19saW5rJywgdGhpcy5fb25DbGlja1RvZ2dsZS5iaW5kKHRoaXMpKVxuICAgICAgICAgICAgLm9uKCdtYXJrZG93bi5yZW5kZXInLCAndGV4dGFyZWEnLCB0aGlzLl9vblVwZGF0ZVByZXZpZXcuYmluZCh0aGlzKSk7XG4gICAgfVxuXG4gICAgX29uQ2xpY2tUb2dnbGUoZSl7XG4gICAgICAgIGNvbnN0ICRsaW5rID0gJChlLmN1cnJlbnRUYXJnZXQpO1xuICAgICAgICBlLnByZXZlbnREZWZhdWx0KCk7XG5cbiAgICAgICAgaWYgKCRsaW5rLmhhc0NsYXNzKCdzdGF0ZV9hY3RpdmUnKSkgcmV0dXJuO1xuXG4gICAgICAgIGNvbnN0IGlzU2hvd1ByZXZpZXcgPSAkbGluay5oYXNDbGFzcygndHlwZS1wcmV2aWV3Jyk7XG4gICAgICAgIHRoaXMuX3RvZ2dsZVByZXZpZXcoaXNTaG93UHJldmlldyk7XG5cbiAgICAgICAgJGxpbmsuYWRkQ2xhc3MoJ3N0YXRlX2FjdGl2ZScpXG4gICAgICAgICAgICAuc2libGluZ3MoKVxuICAgICAgICAgICAgLnJlbW92ZUNsYXNzKCdzdGF0ZV9hY3RpdmUnKTtcbiAgICB9XG5cbiAgICBfdG9nZ2xlUHJldmlldyhpc1Nob3dQcmV2aWV3KXtcbiAgICAgICAgY29uc3QgbG9jYWxzID0gdGhpcy5sb2NhbHM7XG4gICAgICAgIGNvbnN0IG5hbWVDbGFzcyA9ICdtYXJraXR1cF9zdGF0ZV9wcmV2aWV3JztcblxuICAgICAgICBpZiAoIWlzU2hvd1ByZXZpZXcpe1xuICAgICAgICAgICAgbG9jYWxzLiRjb250YWluZXIucmVtb3ZlQ2xhc3MobmFtZUNsYXNzKTtcbiAgICAgICAgICAgIHJldHVybjtcbiAgICAgICAgfVxuXG4gICAgICAgIHRoaXMuX2NvbXBpbGVDb250ZW50KGxvY2Fscy4kdGV4dGFyZWEudmFsKCkpXG4gICAgICAgICAgICAuZG9uZSgoZGF0YSkgPT4ge1xuICAgICAgICAgICAgICAgIGxvY2Fscy4kcHJldmlldy5odG1sKGRhdGEpO1xuXG4gICAgICAgICAgICAgICAgaWYgKCFsb2NhbHMuJGNvbnRhaW5lci5oYXNDbGFzcyhuYW1lQ2xhc3MpKXtcbiAgICAgICAgICAgICAgICAgICAgbG9jYWxzLiRjb250YWluZXIuYWRkQ2xhc3MobmFtZUNsYXNzKVxuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgIH0pXG4gICAgfVxuXG4gICAgX29uVXBkYXRlUHJldmlldygpIHtcbiAgICAgICAgY29uc3QgbG9jYWxzID0gdGhpcy5sb2NhbHM7XG5cbiAgICAgICAgdGhpcy5fY29tcGlsZUNvbnRlbnQobG9jYWxzLiR0ZXh0YXJlYS52YWwoKSlcbiAgICAgICAgICAgIC5kb25lKChkYXRhKSA9PiB7XG4gICAgICAgICAgICAgICAgbG9jYWxzLiRwcmV2aWV3Lmh0bWwoZGF0YSk7XG4gICAgICAgICAgICB9KVxuICAgIH1cblxuICAgIC8vdHJhbnNwb3J0XG4gICAgX2NvbXBpbGVDb250ZW50KGNvbnRlbnQpIHtcbiAgICAgICAgIHJldHVybiAkLnBvc3QodGhpcy51cmwsIHtcbiAgICAgICAgICAgIGRhdGE6IGNvbnRlbnRcbiAgICAgICAgfSk7XG4gICAgfVxuXG4gICAgLy8gc3RhdGljXG4gICAgc3RhdGljIHBsdWdpbihzZWxlY3Rvcikge1xuICAgICAgICBjb25zdCAkZWxlbXMgPSAkKHNlbGVjdG9yKTtcbiAgICAgICAgaWYgKCEkZWxlbXMubGVuZ3RoKSByZXR1cm47XG5cbiAgICAgICAgcmV0dXJuICRlbGVtcy5lYWNoKGZ1bmN0aW9uIChpbmRleCwgZWwpIHtcbiAgICAgICAgICAgIGxldCAkZWxlbWVudCA9ICQoZWwpO1xuICAgICAgICAgICAgbGV0IGRhdGEgPSAkZWxlbWVudC5kYXRhKCd3aWRnZXQucHJldmlldy5tYXJrZG93bicpO1xuXG4gICAgICAgICAgICBpZiAoIWRhdGEpIHtcbiAgICAgICAgICAgICAgICBkYXRhID0gbmV3IFdpZGdldChlbCk7XG4gICAgICAgICAgICAgICAgJGVsZW1lbnQuZGF0YSgnd2lkZ2V0JywgZGF0YSk7XG4gICAgICAgICAgICB9XG4gICAgICAgIH0pXG4gICAgfVxufVxuXG5cblxuXG4vKiogV0VCUEFDSyBGT09URVIgKipcbiAqKiAuL2Zyb250ZW5kL2pzL3BsdWdpbnMvbWFya2Rvd24vX3BsdWdpbi5qc1xuICoqLyIsImNvbnN0IG1hcmtkb3duU2V0dGluZ3MgPSB7XG4gICAgbmFtZVNwYWNlOiAgICAgICAgICAnbWFya2Rvd24nLCAvLyBVc2VmdWwgdG8gcHJldmVudCBtdWx0aS1pbnN0YW5jZXMgQ1NTIGNvbmZsaWN0XG4gICAgcHJldmlld1BhcnNlclBhdGg6ICAnfi9zZXRzL21hcmtkb3duL3ByZXZpZXcucGhwJyxcbiAgICBvblNoaWZ0RW50ZXI6ICAgICAgIHtrZWVwRGVmYXVsdDpmYWxzZSwgb3BlbldpdGg6J1xcblxcbid9LFxuICAgIG1hcmt1cFNldDogW1xuXG4gICAgICAgIHtuYW1lOidCb2xkJywga2V5OlwiQlwiLCBvcGVuV2l0aDonKionLCBjbG9zZVdpdGg6JyoqJ30sXG4gICAgICAgIHtuYW1lOidJdGFsaWMnLCBrZXk6XCJJXCIsIG9wZW5XaXRoOidfJywgY2xvc2VXaXRoOidfJ30sXG4gICAgICAgIHtzZXBhcmF0b3I6Jy0tLS0tLS0tLS0tLS0tLScgfSxcbiAgICAgICAge25hbWU6J1F1b3RlcycsIG9wZW5XaXRoOic+ICd9LFxuICAgICAgICB7bmFtZTonTGluaycsIGtleTpcIkxcIiwgb3BlbldpdGg6J1snLCBjbG9zZVdpdGg6J10oWyFbVXJsOiE6aHR0cDovL10hXSBcIlshW1RpdGxlXSFdXCIpJywgcGxhY2VIb2xkZXI6J1lvdXIgdGV4dCB0byBsaW5rIGhlcmUuLi4nIH0sXG4gICAgICAgIHtzZXBhcmF0b3I6Jy0tLS0tLS0tLS0tLS0tLSd9LFxuICAgICAgICB7bmFtZTonQnVsbGV0ZWQgTGlzdCcsIG9wZW5XaXRoOidcXG5cXG4gLSAnIH0sXG4gICAgICAgIHtuYW1lOidOdW1lcmljIExpc3QnLCBvcGVuV2l0aDogJ1xcblxcbiAxLiAnfSxcbiAgICBdXG59XG5cbmV4cG9ydCBkZWZhdWx0IG1hcmtkb3duU2V0dGluZ3M7XG5cblxuXG4vKiogV0VCUEFDSyBGT09URVIgKipcbiAqKiAuL2Zyb250ZW5kL2pzL3BsdWdpbnMvbWFya2Rvd24vX3NldC5qc1xuICoqLyIsIi8vIC0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS1cbi8vIG1hcmtJdFVwISBVbml2ZXJzYWwgTWFya1VwIEVuZ2luZSwgSlF1ZXJ5IHBsdWdpblxuLy8gdiAxLjEueFxuLy8gRHVhbCBsaWNlbnNlZCB1bmRlciB0aGUgTUlUIGFuZCBHUEwgbGljZW5zZXMuXG4vLyAtLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tXG4vLyBDb3B5cmlnaHQgKEMpIDIwMDctMjAxMiBKYXkgU2FsdmF0XG4vLyBodHRwOi8vbWFya2l0dXAuamF5c2FsdmF0LmNvbS9cbi8vIC0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS1cbi8vIFBlcm1pc3Npb24gaXMgaGVyZWJ5IGdyYW50ZWQsIGZyZWUgb2YgY2hhcmdlLCB0byBhbnkgcGVyc29uIG9idGFpbmluZyBhIGNvcHlcbi8vIG9mIHRoaXMgc29mdHdhcmUgYW5kIGFzc29jaWF0ZWQgZG9jdW1lbnRhdGlvbiBmaWxlcyAodGhlIFwiU29mdHdhcmVcIiksIHRvIGRlYWxcbi8vIGluIHRoZSBTb2Z0d2FyZSB3aXRob3V0IHJlc3RyaWN0aW9uLCBpbmNsdWRpbmcgd2l0aG91dCBsaW1pdGF0aW9uIHRoZSByaWdodHNcbi8vIHRvIHVzZSwgY29weSwgbW9kaWZ5LCBtZXJnZSwgcHVibGlzaCwgZGlzdHJpYnV0ZSwgc3VibGljZW5zZSwgYW5kL29yIHNlbGxcbi8vIGNvcGllcyBvZiB0aGUgU29mdHdhcmUsIGFuZCB0byBwZXJtaXQgcGVyc29ucyB0byB3aG9tIHRoZSBTb2Z0d2FyZSBpc1xuLy8gZnVybmlzaGVkIHRvIGRvIHNvLCBzdWJqZWN0IHRvIHRoZSBmb2xsb3dpbmcgY29uZGl0aW9uczpcbi8vIFxuLy8gVGhlIGFib3ZlIGNvcHlyaWdodCBub3RpY2UgYW5kIHRoaXMgcGVybWlzc2lvbiBub3RpY2Ugc2hhbGwgYmUgaW5jbHVkZWQgaW5cbi8vIGFsbCBjb3BpZXMgb3Igc3Vic3RhbnRpYWwgcG9ydGlvbnMgb2YgdGhlIFNvZnR3YXJlLlxuLy8gXG4vLyBUSEUgU09GVFdBUkUgSVMgUFJPVklERUQgXCJBUyBJU1wiLCBXSVRIT1VUIFdBUlJBTlRZIE9GIEFOWSBLSU5ELCBFWFBSRVNTIE9SXG4vLyBJTVBMSUVELCBJTkNMVURJTkcgQlVUIE5PVCBMSU1JVEVEIFRPIFRIRSBXQVJSQU5USUVTIE9GIE1FUkNIQU5UQUJJTElUWSxcbi8vIEZJVE5FU1MgRk9SIEEgUEFSVElDVUxBUiBQVVJQT1NFIEFORCBOT05JTkZSSU5HRU1FTlQuIElOIE5PIEVWRU5UIFNIQUxMIFRIRVxuLy8gQVVUSE9SUyBPUiBDT1BZUklHSFQgSE9MREVSUyBCRSBMSUFCTEUgRk9SIEFOWSBDTEFJTSwgREFNQUdFUyBPUiBPVEhFUlxuLy8gTElBQklMSVRZLCBXSEVUSEVSIElOIEFOIEFDVElPTiBPRiBDT05UUkFDVCwgVE9SVCBPUiBPVEhFUldJU0UsIEFSSVNJTkcgRlJPTSxcbi8vIE9VVCBPRiBPUiBJTiBDT05ORUNUSU9OIFdJVEggVEhFIFNPRlRXQVJFIE9SIFRIRSBVU0UgT1IgT1RIRVIgREVBTElOR1MgSU5cbi8vIFRIRSBTT0ZUV0FSRS5cbi8vIC0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS1cbihmdW5jdGlvbiAoKSB7XG4gICAgJC5mbi5tYXJrSXRVcCA9IGZ1bmN0aW9uIChzZXR0aW5ncywgZXh0cmFTZXR0aW5ncykge1xuICAgICAgICB2YXIgbWV0aG9kLCBwYXJhbXMsIGxpbmUsIHNlbGVjdGlvbiwgb3B0aW9ucywgY3RybEtleSwgc2hpZnRLZXksIGFsdEtleTtcbiAgICAgICAgY3RybEtleSA9IHNoaWZ0S2V5ID0gYWx0S2V5ID0gZmFsc2U7XG5cbiAgICAgICAgaWYgKHR5cGVvZiBzZXR0aW5ncyA9PSAnc3RyaW5nJykge1xuICAgICAgICAgICAgbWV0aG9kID0gc2V0dGluZ3M7XG4gICAgICAgICAgICBwYXJhbXMgPSBleHRyYVNldHRpbmdzO1xuICAgICAgICB9XG5cbiAgICAgICAgb3B0aW9ucyA9IHtcbiAgICAgICAgICAgIGlkOiAnJyxcbiAgICAgICAgICAgIG5hbWVTcGFjZTogJycsXG4gICAgICAgICAgICByb290OiAnJyxcbiAgICAgICAgICAgIHByZXZpZXdIYW5kbGVyOiBmYWxzZSxcbiAgICAgICAgICAgIHByZXZpZXdJbldpbmRvdzogJycsIC8vICd3aWR0aD04MDAsIGhlaWdodD02MDAsIHJlc2l6YWJsZT15ZXMsIHNjcm9sbGJhcnM9eWVzJ1xuICAgICAgICAgICAgcHJldmlld0luRWxlbWVudDogJycsXG4gICAgICAgICAgICBwcmV2aWV3QXV0b1JlZnJlc2g6IHRydWUsXG4gICAgICAgICAgICBwcmV2aWV3UG9zaXRpb246ICdhZnRlcicsXG4gICAgICAgICAgICBwcmV2aWV3VGVtcGxhdGVQYXRoOiAnfi90ZW1wbGF0ZXMvcHJldmlldy5odG1sJyxcbiAgICAgICAgICAgIHByZXZpZXdQYXJzZXI6IGZhbHNlLFxuICAgICAgICAgICAgcHJldmlld1BhcnNlclBhdGg6ICcnLFxuICAgICAgICAgICAgcHJldmlld1BhcnNlclZhcjogJ2RhdGEnLFxuICAgICAgICAgICAgcmVzaXplSGFuZGxlOiB0cnVlLFxuICAgICAgICAgICAgYmVmb3JlSW5zZXJ0OiAnJyxcbiAgICAgICAgICAgIGFmdGVySW5zZXJ0OiAnJyxcbiAgICAgICAgICAgIG9uRW50ZXI6IHt9LFxuICAgICAgICAgICAgb25TaGlmdEVudGVyOiB7fSxcbiAgICAgICAgICAgIG9uQ3RybEVudGVyOiB7fSxcbiAgICAgICAgICAgIG9uVGFiOiB7fSxcbiAgICAgICAgICAgIG1hcmt1cFNldDogW3svKiBzZXQgKi99XVxuICAgICAgICB9O1xuICAgICAgICAkLmV4dGVuZChvcHRpb25zLCBzZXR0aW5ncywgZXh0cmFTZXR0aW5ncyk7XG5cbiAgICAgICAgLy8gUXVpY2sgcGF0Y2ggdG8ga2VlcCBjb21wYXRpYmlsaXR5IHdpdGggalF1ZXJ5IDEuOVxuICAgICAgICB2YXIgdWFNYXRjaCA9IGZ1bmN0aW9uICh1YSkge1xuICAgICAgICAgICAgdWEgPSB1YS50b0xvd2VyQ2FzZSgpO1xuXG4gICAgICAgICAgICB2YXIgbWF0Y2ggPSAvKGNocm9tZSlbIFxcL10oW1xcdy5dKykvLmV4ZWModWEpIHx8XG4gICAgICAgICAgICAgICAgLyh3ZWJraXQpWyBcXC9dKFtcXHcuXSspLy5leGVjKHVhKSB8fFxuICAgICAgICAgICAgICAgIC8ob3BlcmEpKD86Lip2ZXJzaW9ufClbIFxcL10oW1xcdy5dKykvLmV4ZWModWEpIHx8XG4gICAgICAgICAgICAgICAgLyhtc2llKSAoW1xcdy5dKykvLmV4ZWModWEpIHx8XG4gICAgICAgICAgICAgICAgdWEuaW5kZXhPZihcImNvbXBhdGlibGVcIikgPCAwICYmIC8obW96aWxsYSkoPzouKj8gcnY6KFtcXHcuXSspfCkvLmV4ZWModWEpIHx8XG4gICAgICAgICAgICAgICAgW107XG5cbiAgICAgICAgICAgIHJldHVybiB7XG4gICAgICAgICAgICAgICAgYnJvd3NlcjogbWF0Y2hbMV0gfHwgXCJcIixcbiAgICAgICAgICAgICAgICB2ZXJzaW9uOiBtYXRjaFsyXSB8fCBcIjBcIlxuICAgICAgICAgICAgfTtcbiAgICAgICAgfTtcbiAgICAgICAgdmFyIG1hdGNoZWQgPSB1YU1hdGNoKG5hdmlnYXRvci51c2VyQWdlbnQpO1xuICAgICAgICB2YXIgYnJvd3NlciA9IHt9O1xuXG4gICAgICAgIGlmIChtYXRjaGVkLmJyb3dzZXIpIHtcbiAgICAgICAgICAgIGJyb3dzZXJbbWF0Y2hlZC5icm93c2VyXSA9IHRydWU7XG4gICAgICAgICAgICBicm93c2VyLnZlcnNpb24gPSBtYXRjaGVkLnZlcnNpb247XG4gICAgICAgIH1cbiAgICAgICAgaWYgKGJyb3dzZXIuY2hyb21lKSB7XG4gICAgICAgICAgICBicm93c2VyLndlYmtpdCA9IHRydWU7XG4gICAgICAgIH0gZWxzZSBpZiAoYnJvd3Nlci53ZWJraXQpIHtcbiAgICAgICAgICAgIGJyb3dzZXIuc2FmYXJpID0gdHJ1ZTtcbiAgICAgICAgfVxuXG4gICAgICAgIHJldHVybiB0aGlzLmVhY2goZnVuY3Rpb24gKCkge1xuICAgICAgICAgICAgdmFyICQkLCB0ZXh0YXJlYSwgbGV2ZWxzLCBzY3JvbGxQb3NpdGlvbiwgY2FyZXRQb3NpdGlvbiwgY2FyZXRPZmZzZXQsXG4gICAgICAgICAgICAgICAgY2xpY2tlZCwgaGFzaCwgaGVhZGVyLCBmb290ZXIsIHByZXZpZXdXaW5kb3csIHRlbXBsYXRlLCBpRnJhbWUsIGFib3J0O1xuICAgICAgICAgICAgJCQgPSAkKHRoaXMpO1xuICAgICAgICAgICAgdGV4dGFyZWEgPSB0aGlzO1xuICAgICAgICAgICAgbGV2ZWxzID0gW107XG4gICAgICAgICAgICBhYm9ydCA9IGZhbHNlO1xuICAgICAgICAgICAgc2Nyb2xsUG9zaXRpb24gPSBjYXJldFBvc2l0aW9uID0gMDtcbiAgICAgICAgICAgIGNhcmV0T2Zmc2V0ID0gLTE7XG5cbiAgICAgICAgICAgIG9wdGlvbnMucHJldmlld1BhcnNlclBhdGggPSBsb2NhbGl6ZShvcHRpb25zLnByZXZpZXdQYXJzZXJQYXRoKTtcbiAgICAgICAgICAgIG9wdGlvbnMucHJldmlld1RlbXBsYXRlUGF0aCA9IGxvY2FsaXplKG9wdGlvbnMucHJldmlld1RlbXBsYXRlUGF0aCk7XG5cbiAgICAgICAgICAgIGlmIChtZXRob2QpIHtcbiAgICAgICAgICAgICAgICBzd2l0Y2ggKG1ldGhvZCkge1xuICAgICAgICAgICAgICAgICAgICBjYXNlICdyZW1vdmUnOlxuICAgICAgICAgICAgICAgICAgICAgICAgcmVtb3ZlKCk7XG4gICAgICAgICAgICAgICAgICAgICAgICBicmVhaztcbiAgICAgICAgICAgICAgICAgICAgY2FzZSAnaW5zZXJ0JzpcbiAgICAgICAgICAgICAgICAgICAgICAgIG1hcmt1cChwYXJhbXMpO1xuICAgICAgICAgICAgICAgICAgICAgICAgYnJlYWs7XG4gICAgICAgICAgICAgICAgICAgIGRlZmF1bHQ6XG4gICAgICAgICAgICAgICAgICAgICAgICAkLmVycm9yKCdNZXRob2QgJyArIG1ldGhvZCArICcgZG9lcyBub3QgZXhpc3Qgb24galF1ZXJ5Lm1hcmtJdFVwJyk7XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIHJldHVybjtcbiAgICAgICAgICAgIH1cblxuICAgICAgICAgICAgLy8gYXBwbHkgdGhlIGNvbXB1dGVkIHBhdGggdG8gfi9cbiAgICAgICAgICAgIGZ1bmN0aW9uIGxvY2FsaXplKGRhdGEsIGluVGV4dCkge1xuICAgICAgICAgICAgICAgIGlmIChpblRleHQpIHtcbiAgICAgICAgICAgICAgICAgICAgcmV0dXJuIGRhdGEucmVwbGFjZSgvKFwifCcpflxcLy9nLCBcIiQxXCIgKyBvcHRpb25zLnJvb3QpO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICByZXR1cm4gZGF0YS5yZXBsYWNlKC9eflxcLy8sIG9wdGlvbnMucm9vdCk7XG4gICAgICAgICAgICB9XG5cbiAgICAgICAgICAgIC8vIGluaXQgYW5kIGJ1aWxkIGVkaXRvclxuICAgICAgICAgICAgZnVuY3Rpb24gaW5pdCgpIHtcbiAgICAgICAgICAgICAgICB2YXIgaWQgPSAnJyxcbiAgICAgICAgICAgICAgICAgICAgbmFtZVNwYWNlID0gJycsXG4gICAgICAgICAgICAgICAgICAgIHJlc2l6ZUhhbmRsZTtcblxuICAgICAgICAgICAgICAgIGlmIChvcHRpb25zLmlkKSB7XG4gICAgICAgICAgICAgICAgICAgIGlkID0gJ2lkPVwiJyArIG9wdGlvbnMuaWQgKyAnXCInO1xuICAgICAgICAgICAgICAgIH0gZWxzZSBpZiAoJCQuYXR0cihcImlkXCIpKSB7XG4gICAgICAgICAgICAgICAgICAgIGlkID0gJ2lkPVwibWFya0l0VXAnICsgKCQkLmF0dHIoXCJpZFwiKS5zdWJzdHIoMCwgMSkudG9VcHBlckNhc2UoKSkgKyAoJCQuYXR0cihcImlkXCIpLnN1YnN0cigxKSkgKyAnXCInO1xuXG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIGlmIChvcHRpb25zLm5hbWVTcGFjZSkge1xuICAgICAgICAgICAgICAgICAgICBuYW1lU3BhY2UgPSAnY2xhc3M9XCInICsgb3B0aW9ucy5uYW1lU3BhY2UgKyAnXCInO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAkJC53cmFwKCc8ZGl2ICcgKyBuYW1lU3BhY2UgKyAnPjwvZGl2PicpO1xuICAgICAgICAgICAgICAgICQkLndyYXAoJzxkaXYgJyArIGlkICsgJyBjbGFzcz1cIm1hcmtJdFVwXCI+PC9kaXY+Jyk7XG4gICAgICAgICAgICAgICAgJCQud3JhcCgnPGRpdiBjbGFzcz1cIm1hcmtJdFVwQ29udGFpbmVyXCI+PC9kaXY+Jyk7XG4gICAgICAgICAgICAgICAgJCQuYWRkQ2xhc3MoXCJtYXJrSXRVcEVkaXRvclwiKTtcblxuICAgICAgICAgICAgICAgIC8vIGFkZCB0aGUgaGVhZGVyIGJlZm9yZSB0aGUgdGV4dGFyZWFcbiAgICAgICAgICAgICAgICBoZWFkZXIgPSAkKCc8ZGl2IGNsYXNzPVwibWFya0l0VXBIZWFkZXJcIj48L2Rpdj4nKS5pbnNlcnRCZWZvcmUoJCQpO1xuICAgICAgICAgICAgICAgICQoZHJvcE1lbnVzKG9wdGlvbnMubWFya3VwU2V0KSkuYXBwZW5kVG8oaGVhZGVyKTtcblxuICAgICAgICAgICAgICAgIC8vIGFkZCB0aGUgZm9vdGVyIGFmdGVyIHRoZSB0ZXh0YXJlYVxuICAgICAgICAgICAgICAgIGZvb3RlciA9ICQoJzxkaXYgY2xhc3M9XCJtYXJrSXRVcEZvb3RlclwiPjwvZGl2PicpLmluc2VydEFmdGVyKCQkKTtcblxuICAgICAgICAgICAgICAgIC8vIGFkZCB0aGUgcmVzaXplIGhhbmRsZSBhZnRlciB0ZXh0YXJlYVxuICAgICAgICAgICAgICAgIGlmIChvcHRpb25zLnJlc2l6ZUhhbmRsZSA9PT0gdHJ1ZSAmJiBicm93c2VyLnNhZmFyaSAhPT0gdHJ1ZSkge1xuICAgICAgICAgICAgICAgICAgICByZXNpemVIYW5kbGUgPSAkKCc8ZGl2IGNsYXNzPVwibWFya0l0VXBSZXNpemVIYW5kbGVcIj48L2Rpdj4nKVxuICAgICAgICAgICAgICAgICAgICAgICAgLmluc2VydEFmdGVyKCQkKVxuICAgICAgICAgICAgICAgICAgICAgICAgLmJpbmQoXCJtb3VzZWRvd24ubWFya0l0VXBcIiwgZnVuY3Rpb24gKGUpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICB2YXIgaCA9ICQkLmhlaWdodCgpLCB5ID0gZS5jbGllbnRZLCBtb3VzZU1vdmUsIG1vdXNlVXA7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgbW91c2VNb3ZlID0gZnVuY3Rpb24gKGUpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgJCQuY3NzKFwiaGVpZ2h0XCIsIE1hdGgubWF4KDIwLCBlLmNsaWVudFkgKyBoIC0geSkgKyBcInB4XCIpO1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICByZXR1cm4gZmFsc2U7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgfTtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBtb3VzZVVwID0gZnVuY3Rpb24gKGUpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgJChcImh0bWxcIikudW5iaW5kKFwibW91c2Vtb3ZlLm1hcmtJdFVwXCIsIG1vdXNlTW92ZSkudW5iaW5kKFwibW91c2V1cC5tYXJrSXRVcFwiLCBtb3VzZVVwKTtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgcmV0dXJuIGZhbHNlO1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIH07XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgJChcImh0bWxcIikuYmluZChcIm1vdXNlbW92ZS5tYXJrSXRVcFwiLCBtb3VzZU1vdmUpLmJpbmQoXCJtb3VzZXVwLm1hcmtJdFVwXCIsIG1vdXNlVXApO1xuICAgICAgICAgICAgICAgICAgICAgICAgfSk7XG4gICAgICAgICAgICAgICAgICAgIGZvb3Rlci5hcHBlbmQocmVzaXplSGFuZGxlKTtcbiAgICAgICAgICAgICAgICB9XG5cbiAgICAgICAgICAgICAgICAvLyBsaXN0ZW4ga2V5IGV2ZW50c1xuICAgICAgICAgICAgICAgICQkLmJpbmQoJ2tleWRvd24ubWFya0l0VXAnLCBrZXlQcmVzc2VkKS5iaW5kKCdrZXl1cCcsIGtleVByZXNzZWQpO1xuXG4gICAgICAgICAgICAgICAgLy8gYmluZCBhbiBldmVudCB0byBjYXRjaCBleHRlcm5hbCBjYWxsc1xuICAgICAgICAgICAgICAgICQkLmJpbmQoXCJpbnNlcnRpb24ubWFya0l0VXBcIiwgZnVuY3Rpb24gKGUsIHNldHRpbmdzKSB7XG4gICAgICAgICAgICAgICAgICAgIGlmIChzZXR0aW5ncy50YXJnZXQgIT09IGZhbHNlKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICBnZXQoKTtcbiAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICBpZiAodGV4dGFyZWEgPT09ICQubWFya0l0VXAuZm9jdXNlZCkge1xuICAgICAgICAgICAgICAgICAgICAgICAgbWFya3VwKHNldHRpbmdzKTtcbiAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIH0pO1xuXG4gICAgICAgICAgICAgICAgLy8gcmVtZW1iZXIgdGhlIGxhc3QgZm9jdXNcbiAgICAgICAgICAgICAgICAkJC5iaW5kKCdmb2N1cy5tYXJrSXRVcCcsIGZ1bmN0aW9uICgpIHtcbiAgICAgICAgICAgICAgICAgICAgJC5tYXJrSXRVcC5mb2N1c2VkID0gdGhpcztcbiAgICAgICAgICAgICAgICB9KTtcblxuICAgICAgICAgICAgICAgIGlmIChvcHRpb25zLnByZXZpZXdJbkVsZW1lbnQpIHtcbiAgICAgICAgICAgICAgICAgICAgcmVmcmVzaFByZXZpZXcoKTtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICB9XG5cbiAgICAgICAgICAgIC8vIHJlY3Vyc2l2ZWx5IGJ1aWxkIGhlYWRlciB3aXRoIGRyb3BNZW51cyBmcm9tIG1hcmt1cHNldFxuICAgICAgICAgICAgZnVuY3Rpb24gZHJvcE1lbnVzKG1hcmt1cFNldCkge1xuICAgICAgICAgICAgICAgIHZhciB1bCA9ICQoJzx1bD48L3VsPicpLCBpID0gMDtcbiAgICAgICAgICAgICAgICAkKCdsaTpob3ZlciA+IHVsJywgdWwpLmNzcygnZGlzcGxheScsICdibG9jaycpO1xuICAgICAgICAgICAgICAgICQuZWFjaChtYXJrdXBTZXQsIGZ1bmN0aW9uICgpIHtcbiAgICAgICAgICAgICAgICAgICAgdmFyIGJ1dHRvbiA9IHRoaXMsIHQgPSAnJywgbGksIGo7XG4gICAgICAgICAgICAgICAgICAgIHZhciB0aXRsZSA9IChidXR0b24ua2V5KSA/IChidXR0b24ubmFtZSB8fCAnJykgKyAnIFtDdHJsKycgKyBidXR0b24ua2V5ICsgJ10nIDogKGJ1dHRvbi5uYW1lIHx8ICcnKTtcbiAgICAgICAgICAgICAgICAgICAgdmFyIGtleSA9IChidXR0b24ua2V5KSA/ICdhY2Nlc3NrZXk9XCInICsgYnV0dG9uLmtleSArICdcIicgOiAnJztcbiAgICAgICAgICAgICAgICAgICAgaWYgKGJ1dHRvbi5zZXBhcmF0b3IpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgIGxpID0gJCgnPGxpIGNsYXNzPVwibWFya0l0VXBTZXBhcmF0b3JcIj4nICsgKGJ1dHRvbi5zZXBhcmF0b3IgfHwgJycpICsgJzwvbGk+JykuYXBwZW5kVG8odWwpO1xuICAgICAgICAgICAgICAgICAgICB9IGVsc2Uge1xuICAgICAgICAgICAgICAgICAgICAgICAgaSsrO1xuICAgICAgICAgICAgICAgICAgICAgICAgZm9yIChqID0gbGV2ZWxzLmxlbmd0aCAtIDE7IGogPj0gMDsgai0tKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgdCArPSBsZXZlbHNbal0gKyBcIi1cIjtcbiAgICAgICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgICAgIGxpID0gJCgnPGxpIGNsYXNzPVwibWFya0l0VXBCdXR0b24gbWFya0l0VXBCdXR0b24nICsgdCArIChpKSArICcgJyArIChidXR0b24uY2xhc3NOYW1lIHx8ICcnKSArICdcIj48YSBocmVmPVwiXCIgJyArIGtleSArICcgdGl0bGU9XCInICsgdGl0bGUgKyAnXCI+JyArIChidXR0b24ubmFtZSB8fCAnJykgKyAnPC9hPjwvbGk+JylcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAuYmluZChcImNvbnRleHRtZW51Lm1hcmtJdFVwXCIsIGZ1bmN0aW9uICgpIHsgLy8gcHJldmVudCBjb250ZXh0bWVudSBvbiBtYWMgYW5kIGFsbG93IGN0cmwrY2xpY2tcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgcmV0dXJuIGZhbHNlO1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIH0pLmJpbmQoJ2NsaWNrLm1hcmtJdFVwJywgZnVuY3Rpb24gKGUpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgZS5wcmV2ZW50RGVmYXVsdCgpO1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIH0pLmJpbmQoXCJmb2N1c2luLm1hcmtJdFVwXCIsIGZ1bmN0aW9uICgpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgJCQuZm9jdXMoKTtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICB9KS5iaW5kKCdtb3VzZXVwJywgZnVuY3Rpb24gKCkge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBpZiAoYnV0dG9uLmNhbGwpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIGV2YWwoYnV0dG9uLmNhbGwpKCk7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgc2V0VGltZW91dChmdW5jdGlvbiAoKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBtYXJrdXAoYnV0dG9uKVxuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICB9LCAxKTtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgcmV0dXJuIGZhbHNlO1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIH0pLmJpbmQoJ21vdXNlZW50ZXIubWFya0l0VXAnLCBmdW5jdGlvbiAoKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICQoJz4gdWwnLCB0aGlzKS5zaG93KCk7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICQoZG9jdW1lbnQpLm9uZSgnY2xpY2snLCBmdW5jdGlvbiAoKSB7IC8vIGNsb3NlIGRyb3BtZW51IGlmIGNsaWNrIG91dHNpZGVcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAkKCd1bCB1bCcsIGhlYWRlcikuaGlkZSgpO1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICApO1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIH0pLmJpbmQoJ21vdXNlbGVhdmUubWFya0l0VXAnLCBmdW5jdGlvbiAoKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICQoJz4gdWwnLCB0aGlzKS5oaWRlKCk7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgfSkuYXBwZW5kVG8odWwpO1xuICAgICAgICAgICAgICAgICAgICAgICAgaWYgKGJ1dHRvbi5kcm9wTWVudSkge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIGxldmVscy5wdXNoKGkpO1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICQobGkpLmFkZENsYXNzKCdtYXJrSXRVcERyb3BNZW51JykuYXBwZW5kKGRyb3BNZW51cyhidXR0b24uZHJvcE1lbnUpKTtcbiAgICAgICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIH0pO1xuICAgICAgICAgICAgICAgIGxldmVscy5wb3AoKTtcbiAgICAgICAgICAgICAgICByZXR1cm4gdWw7XG4gICAgICAgICAgICB9XG5cbiAgICAgICAgICAgIC8vIG1hcmtJdFVwISBtYXJrdXBzXG4gICAgICAgICAgICBmdW5jdGlvbiBtYWdpY01hcmt1cHMoc3RyaW5nKSB7XG4gICAgICAgICAgICAgICAgdmFyIHZhbHVlO1xuXG4gICAgICAgICAgICAgICAgaWYgKHN0cmluZykge1xuICAgICAgICAgICAgICAgICAgICBzdHJpbmcgPSBzdHJpbmcudG9TdHJpbmcoKTtcbiAgICAgICAgICAgICAgICAgICAgc3RyaW5nID0gc3RyaW5nLnJlcGxhY2UoL1xcKFxcIVxcKChbXFxzXFxTXSo/KVxcKVxcIVxcKS9nLFxuICAgICAgICAgICAgICAgICAgICAgICAgZnVuY3Rpb24gKHgsIGEpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICB2YXIgYiA9IGEuc3BsaXQoJ3whfCcpO1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIGlmIChhbHRLZXkgPT09IHRydWUpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgcmV0dXJuIChiWzFdICE9PSB1bmRlZmluZWQpID8gYlsxXSA6IGJbMF07XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgfSBlbHNlIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgcmV0dXJuIChiWzFdID09PSB1bmRlZmluZWQpID8gXCJcIiA6IGJbMF07XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICApO1xuICAgICAgICAgICAgICAgICAgICAvLyBbIVtwcm9tcHRdIV0sIFshW3Byb21wdDohOnZhbHVlXSFdXG4gICAgICAgICAgICAgICAgICAgIHN0cmluZyA9IHN0cmluZy5yZXBsYWNlKC9cXFtcXCFcXFsoW1xcc1xcU10qPylcXF1cXCFcXF0vZyxcbiAgICAgICAgICAgICAgICAgICAgICAgIGZ1bmN0aW9uICh4LCBhKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgdmFyIGIgPSBhLnNwbGl0KCc6ITonKTtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBpZiAoYWJvcnQgPT09IHRydWUpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgcmV0dXJuIGZhbHNlO1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgICAgICAgICB2YWx1ZSA9IHByb21wdChiWzBdLCAoYlsxXSkgPyBiWzFdIDogJycpO1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIGlmICh2YWx1ZSA9PT0gbnVsbCkge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBhYm9ydCA9IHRydWU7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICAgICAgICAgIHJldHVybiB2YWx1ZTtcbiAgICAgICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgKTtcbiAgICAgICAgICAgICAgICAgICAgcmV0dXJuIHN0cmluZztcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgcmV0dXJuIFwiXCI7XG4gICAgICAgICAgICB9XG5cbiAgICAgICAgICAgIC8vIHByZXBhcmUgYWN0aW9uXG4gICAgICAgICAgICBmdW5jdGlvbiBwcmVwYXJlKGFjdGlvbikge1xuICAgICAgICAgICAgICAgIGlmICgkLmlzRnVuY3Rpb24oYWN0aW9uKSkge1xuICAgICAgICAgICAgICAgICAgICBhY3Rpb24gPSBhY3Rpb24oaGFzaCk7XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIHJldHVybiBtYWdpY01hcmt1cHMoYWN0aW9uKTtcbiAgICAgICAgICAgIH1cblxuICAgICAgICAgICAgLy8gYnVpbGQgYmxvY2sgdG8gaW5zZXJ0XG4gICAgICAgICAgICBmdW5jdGlvbiBidWlsZChzdHJpbmcpIHtcbiAgICAgICAgICAgICAgICB2YXIgb3BlbldpdGggPSBwcmVwYXJlKGNsaWNrZWQub3BlbldpdGgpO1xuICAgICAgICAgICAgICAgIHZhciBwbGFjZUhvbGRlciA9IHByZXBhcmUoY2xpY2tlZC5wbGFjZUhvbGRlcik7XG4gICAgICAgICAgICAgICAgdmFyIHJlcGxhY2VXaXRoID0gcHJlcGFyZShjbGlja2VkLnJlcGxhY2VXaXRoKTtcbiAgICAgICAgICAgICAgICB2YXIgY2xvc2VXaXRoID0gcHJlcGFyZShjbGlja2VkLmNsb3NlV2l0aCk7XG4gICAgICAgICAgICAgICAgdmFyIG9wZW5CbG9ja1dpdGggPSBwcmVwYXJlKGNsaWNrZWQub3BlbkJsb2NrV2l0aCk7XG4gICAgICAgICAgICAgICAgdmFyIGNsb3NlQmxvY2tXaXRoID0gcHJlcGFyZShjbGlja2VkLmNsb3NlQmxvY2tXaXRoKTtcbiAgICAgICAgICAgICAgICB2YXIgbXVsdGlsaW5lID0gY2xpY2tlZC5tdWx0aWxpbmU7XG4gICAgICAgICAgICAgICAgdmFyIGJsb2NrO1xuXG4gICAgICAgICAgICAgICAgaWYgKHJlcGxhY2VXaXRoICE9PSBcIlwiKSB7XG4gICAgICAgICAgICAgICAgICAgIGJsb2NrID0gb3BlbldpdGggKyByZXBsYWNlV2l0aCArIGNsb3NlV2l0aDtcbiAgICAgICAgICAgICAgICB9IGVsc2UgaWYgKHNlbGVjdGlvbiA9PT0gJycgJiYgcGxhY2VIb2xkZXIgIT09ICcnKSB7XG4gICAgICAgICAgICAgICAgICAgIGJsb2NrID0gb3BlbldpdGggKyBwbGFjZUhvbGRlciArIGNsb3NlV2l0aDtcbiAgICAgICAgICAgICAgICB9IGVsc2Uge1xuICAgICAgICAgICAgICAgICAgICBzdHJpbmcgPSBzdHJpbmcgfHwgc2VsZWN0aW9uO1xuXG4gICAgICAgICAgICAgICAgICAgIHZhciBsaW5lcyA9IFtzdHJpbmddLCBibG9ja3MgPSBbXTtcblxuICAgICAgICAgICAgICAgICAgICBpZiAobXVsdGlsaW5lID09PSB0cnVlKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICBsaW5lcyA9IHN0cmluZy5zcGxpdCgvXFxyP1xcbi8pO1xuICAgICAgICAgICAgICAgICAgICB9XG5cbiAgICAgICAgICAgICAgICAgICAgZm9yICh2YXIgbCA9IDA7IGwgPCBsaW5lcy5sZW5ndGg7IGwrKykge1xuICAgICAgICAgICAgICAgICAgICAgICAgbGluZSA9IGxpbmVzW2xdO1xuICAgICAgICAgICAgICAgICAgICAgICAgdmFyIHRyYWlsaW5nU3BhY2VzO1xuICAgICAgICAgICAgICAgICAgICAgICAgaWYgKHRyYWlsaW5nU3BhY2VzID0gbGluZS5tYXRjaCgvICokLykpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBibG9ja3MucHVzaChvcGVuV2l0aCArIGxpbmUucmVwbGFjZSgvICokL2csICcnKSArIGNsb3NlV2l0aCArIHRyYWlsaW5nU3BhY2VzKTtcbiAgICAgICAgICAgICAgICAgICAgICAgIH0gZWxzZSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgYmxvY2tzLnB1c2gob3BlbldpdGggKyBsaW5lICsgY2xvc2VXaXRoKTtcbiAgICAgICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgfVxuXG4gICAgICAgICAgICAgICAgICAgIGJsb2NrID0gYmxvY2tzLmpvaW4oXCJcXG5cIik7XG4gICAgICAgICAgICAgICAgfVxuXG4gICAgICAgICAgICAgICAgYmxvY2sgPSBvcGVuQmxvY2tXaXRoICsgYmxvY2sgKyBjbG9zZUJsb2NrV2l0aDtcblxuICAgICAgICAgICAgICAgIHJldHVybiB7XG4gICAgICAgICAgICAgICAgICAgIGJsb2NrOiBibG9jayxcbiAgICAgICAgICAgICAgICAgICAgb3BlbkJsb2NrV2l0aDogb3BlbkJsb2NrV2l0aCxcbiAgICAgICAgICAgICAgICAgICAgb3BlbldpdGg6IG9wZW5XaXRoLFxuICAgICAgICAgICAgICAgICAgICByZXBsYWNlV2l0aDogcmVwbGFjZVdpdGgsXG4gICAgICAgICAgICAgICAgICAgIHBsYWNlSG9sZGVyOiBwbGFjZUhvbGRlcixcbiAgICAgICAgICAgICAgICAgICAgY2xvc2VXaXRoOiBjbG9zZVdpdGgsXG4gICAgICAgICAgICAgICAgICAgIGNsb3NlQmxvY2tXaXRoOiBjbG9zZUJsb2NrV2l0aFxuICAgICAgICAgICAgICAgIH07XG4gICAgICAgICAgICB9XG5cbiAgICAgICAgICAgIC8vIGRlZmluZSBtYXJrdXAgdG8gaW5zZXJ0XG4gICAgICAgICAgICBmdW5jdGlvbiBtYXJrdXAoYnV0dG9uKSB7XG4gICAgICAgICAgICAgICAgdmFyIGxlbiwgaiwgbiwgaSwgc3RyaW5nLCBzdGFydDtcbiAgICAgICAgICAgICAgICBoYXNoID0gY2xpY2tlZCA9IGJ1dHRvbjtcbiAgICAgICAgICAgICAgICBnZXQoKTtcbiAgICAgICAgICAgICAgICAkLmV4dGVuZChoYXNoLCB7XG4gICAgICAgICAgICAgICAgICAgICAgICBsaW5lOiBcIlwiLFxuICAgICAgICAgICAgICAgICAgICAgICAgcm9vdDogb3B0aW9ucy5yb290LFxuICAgICAgICAgICAgICAgICAgICAgICAgdGV4dGFyZWE6IHRleHRhcmVhLFxuICAgICAgICAgICAgICAgICAgICAgICAgc2VsZWN0aW9uOiAoc2VsZWN0aW9uIHx8ICcnKSxcbiAgICAgICAgICAgICAgICAgICAgICAgIGNhcmV0UG9zaXRpb246IGNhcmV0UG9zaXRpb24sXG4gICAgICAgICAgICAgICAgICAgICAgICBjdHJsS2V5OiBjdHJsS2V5LFxuICAgICAgICAgICAgICAgICAgICAgICAgc2hpZnRLZXk6IHNoaWZ0S2V5LFxuICAgICAgICAgICAgICAgICAgICAgICAgYWx0S2V5OiBhbHRLZXlcbiAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICk7XG4gICAgICAgICAgICAgICAgLy8gY2FsbGJhY2tzIGJlZm9yZSBpbnNlcnRpb25cbiAgICAgICAgICAgICAgICBwcmVwYXJlKG9wdGlvbnMuYmVmb3JlSW5zZXJ0KTtcbiAgICAgICAgICAgICAgICBwcmVwYXJlKGNsaWNrZWQuYmVmb3JlSW5zZXJ0KTtcbiAgICAgICAgICAgICAgICBpZiAoKGN0cmxLZXkgPT09IHRydWUgJiYgc2hpZnRLZXkgPT09IHRydWUpIHx8IGJ1dHRvbi5tdWx0aWxpbmUgPT09IHRydWUpIHtcbiAgICAgICAgICAgICAgICAgICAgcHJlcGFyZShjbGlja2VkLmJlZm9yZU11bHRpSW5zZXJ0KTtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgJC5leHRlbmQoaGFzaCwge2xpbmU6IDF9KTtcblxuICAgICAgICAgICAgICAgIGlmICgoY3RybEtleSA9PT0gdHJ1ZSAmJiBzaGlmdEtleSA9PT0gdHJ1ZSkpIHtcbiAgICAgICAgICAgICAgICAgICAgbGluZXMgPSBzZWxlY3Rpb24uc3BsaXQoL1xccj9cXG4vKTtcbiAgICAgICAgICAgICAgICAgICAgZm9yIChqID0gMCwgbiA9IGxpbmVzLmxlbmd0aCwgaSA9IDA7IGkgPCBuOyBpKyspIHtcbiAgICAgICAgICAgICAgICAgICAgICAgIGlmICgkLnRyaW0obGluZXNbaV0pICE9PSAnJykge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICQuZXh0ZW5kKGhhc2gsIHtsaW5lOiArK2osIHNlbGVjdGlvbjogbGluZXNbaV19KTtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBsaW5lc1tpXSA9IGJ1aWxkKGxpbmVzW2ldKS5ibG9jaztcbiAgICAgICAgICAgICAgICAgICAgICAgIH0gZWxzZSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgbGluZXNbaV0gPSBcIlwiO1xuICAgICAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICB9XG5cbiAgICAgICAgICAgICAgICAgICAgc3RyaW5nID0ge2Jsb2NrOiBsaW5lcy5qb2luKCdcXG4nKX07XG4gICAgICAgICAgICAgICAgICAgIHN0YXJ0ID0gY2FyZXRQb3NpdGlvbjtcbiAgICAgICAgICAgICAgICAgICAgbGVuID0gc3RyaW5nLmJsb2NrLmxlbmd0aCArICgoYnJvd3Nlci5vcGVyYSkgPyBuIC0gMSA6IDApO1xuICAgICAgICAgICAgICAgIH0gZWxzZSBpZiAoY3RybEtleSA9PT0gdHJ1ZSkge1xuICAgICAgICAgICAgICAgICAgICBzdHJpbmcgPSBidWlsZChzZWxlY3Rpb24pO1xuICAgICAgICAgICAgICAgICAgICBzdGFydCA9IGNhcmV0UG9zaXRpb24gKyBzdHJpbmcub3BlbldpdGgubGVuZ3RoO1xuICAgICAgICAgICAgICAgICAgICBsZW4gPSBzdHJpbmcuYmxvY2subGVuZ3RoIC0gc3RyaW5nLm9wZW5XaXRoLmxlbmd0aCAtIHN0cmluZy5jbG9zZVdpdGgubGVuZ3RoO1xuICAgICAgICAgICAgICAgICAgICBsZW4gPSBsZW4gLSAoc3RyaW5nLmJsb2NrLm1hdGNoKC8gJC8pID8gMSA6IDApO1xuICAgICAgICAgICAgICAgICAgICBsZW4gLT0gZml4SWVCdWcoc3RyaW5nLmJsb2NrKTtcbiAgICAgICAgICAgICAgICB9IGVsc2UgaWYgKHNoaWZ0S2V5ID09PSB0cnVlKSB7XG4gICAgICAgICAgICAgICAgICAgIHN0cmluZyA9IGJ1aWxkKHNlbGVjdGlvbik7XG4gICAgICAgICAgICAgICAgICAgIHN0YXJ0ID0gY2FyZXRQb3NpdGlvbjtcbiAgICAgICAgICAgICAgICAgICAgbGVuID0gc3RyaW5nLmJsb2NrLmxlbmd0aDtcbiAgICAgICAgICAgICAgICAgICAgbGVuIC09IGZpeEllQnVnKHN0cmluZy5ibG9jayk7XG4gICAgICAgICAgICAgICAgfSBlbHNlIHtcbiAgICAgICAgICAgICAgICAgICAgc3RyaW5nID0gYnVpbGQoc2VsZWN0aW9uKTtcbiAgICAgICAgICAgICAgICAgICAgc3RhcnQgPSBjYXJldFBvc2l0aW9uICsgc3RyaW5nLmJsb2NrLmxlbmd0aDtcbiAgICAgICAgICAgICAgICAgICAgbGVuID0gMDtcbiAgICAgICAgICAgICAgICAgICAgc3RhcnQgLT0gZml4SWVCdWcoc3RyaW5nLmJsb2NrKTtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgaWYgKChzZWxlY3Rpb24gPT09ICcnICYmIHN0cmluZy5yZXBsYWNlV2l0aCA9PT0gJycpKSB7XG4gICAgICAgICAgICAgICAgICAgIGNhcmV0T2Zmc2V0ICs9IGZpeE9wZXJhQnVnKHN0cmluZy5ibG9jayk7XG5cbiAgICAgICAgICAgICAgICAgICAgc3RhcnQgPSBjYXJldFBvc2l0aW9uICsgc3RyaW5nLm9wZW5CbG9ja1dpdGgubGVuZ3RoICsgc3RyaW5nLm9wZW5XaXRoLmxlbmd0aDtcbiAgICAgICAgICAgICAgICAgICAgbGVuID0gc3RyaW5nLmJsb2NrLmxlbmd0aCAtIHN0cmluZy5vcGVuQmxvY2tXaXRoLmxlbmd0aCAtIHN0cmluZy5vcGVuV2l0aC5sZW5ndGggLSBzdHJpbmcuY2xvc2VXaXRoLmxlbmd0aCAtIHN0cmluZy5jbG9zZUJsb2NrV2l0aC5sZW5ndGg7XG5cbiAgICAgICAgICAgICAgICAgICAgY2FyZXRPZmZzZXQgPSAkJC52YWwoKS5zdWJzdHJpbmcoY2FyZXRQb3NpdGlvbiwgJCQudmFsKCkubGVuZ3RoKS5sZW5ndGg7XG4gICAgICAgICAgICAgICAgICAgIGNhcmV0T2Zmc2V0IC09IGZpeE9wZXJhQnVnKCQkLnZhbCgpLnN1YnN0cmluZygwLCBjYXJldFBvc2l0aW9uKSk7XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICQuZXh0ZW5kKGhhc2gsIHtjYXJldFBvc2l0aW9uOiBjYXJldFBvc2l0aW9uLCBzY3JvbGxQb3NpdGlvbjogc2Nyb2xsUG9zaXRpb259KTtcblxuICAgICAgICAgICAgICAgIGlmIChzdHJpbmcuYmxvY2sgIT09IHNlbGVjdGlvbiAmJiBhYm9ydCA9PT0gZmFsc2UpIHtcbiAgICAgICAgICAgICAgICAgICAgaW5zZXJ0KHN0cmluZy5ibG9jayk7XG4gICAgICAgICAgICAgICAgICAgIHNldChzdGFydCwgbGVuKTtcbiAgICAgICAgICAgICAgICB9IGVsc2Uge1xuICAgICAgICAgICAgICAgICAgICBjYXJldE9mZnNldCA9IC0xO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICBnZXQoKTtcblxuICAgICAgICAgICAgICAgICQuZXh0ZW5kKGhhc2gsIHtsaW5lOiAnJywgc2VsZWN0aW9uOiBzZWxlY3Rpb259KTtcblxuICAgICAgICAgICAgICAgIC8vIGNhbGxiYWNrcyBhZnRlciBpbnNlcnRpb25cbiAgICAgICAgICAgICAgICBpZiAoKGN0cmxLZXkgPT09IHRydWUgJiYgc2hpZnRLZXkgPT09IHRydWUpIHx8IGJ1dHRvbi5tdWx0aWxpbmUgPT09IHRydWUpIHtcbiAgICAgICAgICAgICAgICAgICAgcHJlcGFyZShjbGlja2VkLmFmdGVyTXVsdGlJbnNlcnQpO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICBwcmVwYXJlKGNsaWNrZWQuYWZ0ZXJJbnNlcnQpO1xuICAgICAgICAgICAgICAgIHByZXBhcmUob3B0aW9ucy5hZnRlckluc2VydCk7XG5cbiAgICAgICAgICAgICAgICAvLyByZWZyZXNoIHByZXZpZXcgaWYgb3BlbmVkXG4gICAgICAgICAgICAgICAgaWYgKHByZXZpZXdXaW5kb3cgJiYgb3B0aW9ucy5wcmV2aWV3QXV0b1JlZnJlc2gpIHtcbiAgICAgICAgICAgICAgICAgICAgcmVmcmVzaFByZXZpZXcoKTtcbiAgICAgICAgICAgICAgICB9XG5cbiAgICAgICAgICAgICAgICAvLyByZWluaXQga2V5ZXZlbnRcbiAgICAgICAgICAgICAgICBzaGlmdEtleSA9IGFsdEtleSA9IGN0cmxLZXkgPSBhYm9ydCA9IGZhbHNlO1xuICAgICAgICAgICAgfVxuXG4gICAgICAgICAgICAvLyBTdWJzdHJhY3QgbGluZWZlZWQgaW4gT3BlcmFcbiAgICAgICAgICAgIGZ1bmN0aW9uIGZpeE9wZXJhQnVnKHN0cmluZykge1xuICAgICAgICAgICAgICAgIGlmIChicm93c2VyLm9wZXJhKSB7XG4gICAgICAgICAgICAgICAgICAgIHJldHVybiBzdHJpbmcubGVuZ3RoIC0gc3RyaW5nLnJlcGxhY2UoL1xcbiovZywgJycpLmxlbmd0aDtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgcmV0dXJuIDA7XG4gICAgICAgICAgICB9XG5cbiAgICAgICAgICAgIC8vIFN1YnN0cmFjdCBsaW5lZmVlZCBpbiBJRVxuICAgICAgICAgICAgZnVuY3Rpb24gZml4SWVCdWcoc3RyaW5nKSB7XG4gICAgICAgICAgICAgICAgaWYgKGJyb3dzZXIubXNpZSkge1xuICAgICAgICAgICAgICAgICAgICByZXR1cm4gc3RyaW5nLmxlbmd0aCAtIHN0cmluZy5yZXBsYWNlKC9cXHIqL2csICcnKS5sZW5ndGg7XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIHJldHVybiAwO1xuICAgICAgICAgICAgfVxuXG4gICAgICAgICAgICAvLyBhZGQgbWFya3VwXG4gICAgICAgICAgICBmdW5jdGlvbiBpbnNlcnQoYmxvY2spIHtcbiAgICAgICAgICAgICAgICBpZiAoZG9jdW1lbnQuc2VsZWN0aW9uKSB7XG4gICAgICAgICAgICAgICAgICAgIHZhciBuZXdTZWxlY3Rpb24gPSBkb2N1bWVudC5zZWxlY3Rpb24uY3JlYXRlUmFuZ2UoKTtcbiAgICAgICAgICAgICAgICAgICAgbmV3U2VsZWN0aW9uLnRleHQgPSBibG9jaztcbiAgICAgICAgICAgICAgICB9IGVsc2Uge1xuICAgICAgICAgICAgICAgICAgICB0ZXh0YXJlYS52YWx1ZSA9IHRleHRhcmVhLnZhbHVlLnN1YnN0cmluZygwLCBjYXJldFBvc2l0aW9uKSArIGJsb2NrICsgdGV4dGFyZWEudmFsdWUuc3Vic3RyaW5nKGNhcmV0UG9zaXRpb24gKyBzZWxlY3Rpb24ubGVuZ3RoLCB0ZXh0YXJlYS52YWx1ZS5sZW5ndGgpO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgIH1cblxuICAgICAgICAgICAgLy8gc2V0IGEgc2VsZWN0aW9uXG4gICAgICAgICAgICBmdW5jdGlvbiBzZXQoc3RhcnQsIGxlbikge1xuICAgICAgICAgICAgICAgIGlmICh0ZXh0YXJlYS5jcmVhdGVUZXh0UmFuZ2UpIHtcbiAgICAgICAgICAgICAgICAgICAgLy8gcXVpY2sgZml4IHRvIG1ha2UgaXQgd29yayBvbiBPcGVyYSA5LjVcbiAgICAgICAgICAgICAgICAgICAgaWYgKGJyb3dzZXIub3BlcmEgJiYgYnJvd3Nlci52ZXJzaW9uID49IDkuNSAmJiBsZW4gPT0gMCkge1xuICAgICAgICAgICAgICAgICAgICAgICAgcmV0dXJuIGZhbHNlO1xuICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgIHJhbmdlID0gdGV4dGFyZWEuY3JlYXRlVGV4dFJhbmdlKCk7XG4gICAgICAgICAgICAgICAgICAgIHJhbmdlLmNvbGxhcHNlKHRydWUpO1xuICAgICAgICAgICAgICAgICAgICByYW5nZS5tb3ZlU3RhcnQoJ2NoYXJhY3RlcicsIHN0YXJ0KTtcbiAgICAgICAgICAgICAgICAgICAgcmFuZ2UubW92ZUVuZCgnY2hhcmFjdGVyJywgbGVuKTtcbiAgICAgICAgICAgICAgICAgICAgcmFuZ2Uuc2VsZWN0KCk7XG4gICAgICAgICAgICAgICAgfSBlbHNlIGlmICh0ZXh0YXJlYS5zZXRTZWxlY3Rpb25SYW5nZSkge1xuICAgICAgICAgICAgICAgICAgICB0ZXh0YXJlYS5zZXRTZWxlY3Rpb25SYW5nZShzdGFydCwgc3RhcnQgKyBsZW4pO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICB0ZXh0YXJlYS5zY3JvbGxUb3AgPSBzY3JvbGxQb3NpdGlvbjtcbiAgICAgICAgICAgICAgICB0ZXh0YXJlYS5mb2N1cygpO1xuICAgICAgICAgICAgfVxuXG4gICAgICAgICAgICAvLyBnZXQgdGhlIHNlbGVjdGlvblxuICAgICAgICAgICAgZnVuY3Rpb24gZ2V0KCkge1xuICAgICAgICAgICAgICAgIHRleHRhcmVhLmZvY3VzKCk7XG5cbiAgICAgICAgICAgICAgICBzY3JvbGxQb3NpdGlvbiA9IHRleHRhcmVhLnNjcm9sbFRvcDtcbiAgICAgICAgICAgICAgICBpZiAoZG9jdW1lbnQuc2VsZWN0aW9uKSB7XG4gICAgICAgICAgICAgICAgICAgIHNlbGVjdGlvbiA9IGRvY3VtZW50LnNlbGVjdGlvbi5jcmVhdGVSYW5nZSgpLnRleHQ7XG4gICAgICAgICAgICAgICAgICAgIGlmIChicm93c2VyLm1zaWUpIHsgLy8gaWVcbiAgICAgICAgICAgICAgICAgICAgICAgIHZhciByYW5nZSA9IGRvY3VtZW50LnNlbGVjdGlvbi5jcmVhdGVSYW5nZSgpLCByYW5nZUNvcHkgPSByYW5nZS5kdXBsaWNhdGUoKTtcbiAgICAgICAgICAgICAgICAgICAgICAgIHJhbmdlQ29weS5tb3ZlVG9FbGVtZW50VGV4dCh0ZXh0YXJlYSk7XG4gICAgICAgICAgICAgICAgICAgICAgICBjYXJldFBvc2l0aW9uID0gLTE7XG4gICAgICAgICAgICAgICAgICAgICAgICB3aGlsZSAocmFuZ2VDb3B5LmluUmFuZ2UocmFuZ2UpKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgcmFuZ2VDb3B5Lm1vdmVTdGFydCgnY2hhcmFjdGVyJyk7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgY2FyZXRQb3NpdGlvbisrO1xuICAgICAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICB9IGVsc2UgeyAvLyBvcGVyYVxuICAgICAgICAgICAgICAgICAgICAgICAgY2FyZXRQb3NpdGlvbiA9IHRleHRhcmVhLnNlbGVjdGlvblN0YXJ0O1xuICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgfSBlbHNlIHsgLy8gZ2Vja28gJiB3ZWJraXRcbiAgICAgICAgICAgICAgICAgICAgY2FyZXRQb3NpdGlvbiA9IHRleHRhcmVhLnNlbGVjdGlvblN0YXJ0O1xuXG4gICAgICAgICAgICAgICAgICAgIHNlbGVjdGlvbiA9IHRleHRhcmVhLnZhbHVlLnN1YnN0cmluZyhjYXJldFBvc2l0aW9uLCB0ZXh0YXJlYS5zZWxlY3Rpb25FbmQpO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICByZXR1cm4gc2VsZWN0aW9uO1xuICAgICAgICAgICAgfVxuXG4gICAgICAgICAgICAvLyBvcGVuIHByZXZpZXcgd2luZG93XG4gICAgICAgICAgICBmdW5jdGlvbiBwcmV2aWV3KCkge1xuICAgICAgICAgICAgICAgIGlmICh0eXBlb2Ygb3B0aW9ucy5wcmV2aWV3SGFuZGxlciA9PT0gJ2Z1bmN0aW9uJykge1xuICAgICAgICAgICAgICAgICAgICBwcmV2aWV3V2luZG93ID0gdHJ1ZTtcbiAgICAgICAgICAgICAgICB9IGVsc2UgaWYgKG9wdGlvbnMucHJldmlld0luRWxlbWVudCkge1xuICAgICAgICAgICAgICAgICAgICBwcmV2aWV3V2luZG93ID0gJChvcHRpb25zLnByZXZpZXdJbkVsZW1lbnQpO1xuICAgICAgICAgICAgICAgIH0gZWxzZSBpZiAoIXByZXZpZXdXaW5kb3cgfHwgcHJldmlld1dpbmRvdy5jbG9zZWQpIHtcbiAgICAgICAgICAgICAgICAgICAgaWYgKG9wdGlvbnMucHJldmlld0luV2luZG93KSB7XG4gICAgICAgICAgICAgICAgICAgICAgICBwcmV2aWV3V2luZG93ID0gd2luZG93Lm9wZW4oJycsICdwcmV2aWV3Jywgb3B0aW9ucy5wcmV2aWV3SW5XaW5kb3cpO1xuICAgICAgICAgICAgICAgICAgICAgICAgJCh3aW5kb3cpLnVubG9hZChmdW5jdGlvbiAoKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgcHJldmlld1dpbmRvdy5jbG9zZSgpO1xuICAgICAgICAgICAgICAgICAgICAgICAgfSk7XG4gICAgICAgICAgICAgICAgICAgIH0gZWxzZSB7XG4gICAgICAgICAgICAgICAgICAgICAgICBpRnJhbWUgPSAkKCc8aWZyYW1lIGNsYXNzPVwibWFya0l0VXBQcmV2aWV3RnJhbWVcIj48L2lmcmFtZT4nKTtcbiAgICAgICAgICAgICAgICAgICAgICAgIGlmIChvcHRpb25zLnByZXZpZXdQb3NpdGlvbiA9PSAnYWZ0ZXInKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgaUZyYW1lLmluc2VydEFmdGVyKGZvb3Rlcik7XG4gICAgICAgICAgICAgICAgICAgICAgICB9IGVsc2Uge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIGlGcmFtZS5pbnNlcnRCZWZvcmUoaGVhZGVyKTtcbiAgICAgICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgICAgIHByZXZpZXdXaW5kb3cgPSBpRnJhbWVbaUZyYW1lLmxlbmd0aCAtIDFdLmNvbnRlbnRXaW5kb3cgfHwgZnJhbWVbaUZyYW1lLmxlbmd0aCAtIDFdO1xuICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgfSBlbHNlIGlmIChhbHRLZXkgPT09IHRydWUpIHtcbiAgICAgICAgICAgICAgICAgICAgaWYgKGlGcmFtZSkge1xuICAgICAgICAgICAgICAgICAgICAgICAgaUZyYW1lLnJlbW92ZSgpO1xuICAgICAgICAgICAgICAgICAgICB9IGVsc2Uge1xuICAgICAgICAgICAgICAgICAgICAgICAgcHJldmlld1dpbmRvdy5jbG9zZSgpO1xuICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgIHByZXZpZXdXaW5kb3cgPSBpRnJhbWUgPSBmYWxzZTtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgaWYgKCFvcHRpb25zLnByZXZpZXdBdXRvUmVmcmVzaCkge1xuICAgICAgICAgICAgICAgICAgICByZWZyZXNoUHJldmlldygpO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICBpZiAob3B0aW9ucy5wcmV2aWV3SW5XaW5kb3cpIHtcbiAgICAgICAgICAgICAgICAgICAgcHJldmlld1dpbmRvdy5mb2N1cygpO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgIH1cblxuICAgICAgICAgICAgLy8gcmVmcmVzaCBQcmV2aWV3IHdpbmRvd1xuICAgICAgICAgICAgZnVuY3Rpb24gcmVmcmVzaFByZXZpZXcoKSB7XG4gICAgICAgICAgICAgICAgcmVuZGVyUHJldmlldygpO1xuICAgICAgICAgICAgfVxuXG4gICAgICAgICAgICBmdW5jdGlvbiByZW5kZXJQcmV2aWV3KCkge1xuICAgICAgICAgICAgICAgIHZhciBwaHRtbDtcbiAgICAgICAgICAgICAgICBpZiAob3B0aW9ucy5wcmV2aWV3SGFuZGxlciAmJiB0eXBlb2Ygb3B0aW9ucy5wcmV2aWV3SGFuZGxlciA9PT0gJ2Z1bmN0aW9uJykge1xuICAgICAgICAgICAgICAgICAgICBvcHRpb25zLnByZXZpZXdIYW5kbGVyKCQkLnZhbCgpKTtcbiAgICAgICAgICAgICAgICB9IGVsc2UgaWYgKG9wdGlvbnMucHJldmlld1BhcnNlciAmJiB0eXBlb2Ygb3B0aW9ucy5wcmV2aWV3UGFyc2VyID09PSAnZnVuY3Rpb24nKSB7XG4gICAgICAgICAgICAgICAgICAgIHZhciBkYXRhID0gb3B0aW9ucy5wcmV2aWV3UGFyc2VyKCQkLnZhbCgpKTtcbiAgICAgICAgICAgICAgICAgICAgd3JpdGVJblByZXZpZXcobG9jYWxpemUoZGF0YSwgMSkpO1xuICAgICAgICAgICAgICAgIH0gZWxzZSBpZiAob3B0aW9ucy5wcmV2aWV3UGFyc2VyUGF0aCAhPT0gJycpIHtcbiAgICAgICAgICAgICAgICAgICAgJC5hamF4KHtcbiAgICAgICAgICAgICAgICAgICAgICAgIHR5cGU6ICdQT1NUJyxcbiAgICAgICAgICAgICAgICAgICAgICAgIGRhdGFUeXBlOiAndGV4dCcsXG4gICAgICAgICAgICAgICAgICAgICAgICBnbG9iYWw6IGZhbHNlLFxuICAgICAgICAgICAgICAgICAgICAgICAgdXJsOiBvcHRpb25zLnByZXZpZXdQYXJzZXJQYXRoLFxuICAgICAgICAgICAgICAgICAgICAgICAgZGF0YTogb3B0aW9ucy5wcmV2aWV3UGFyc2VyVmFyICsgJz0nICsgZW5jb2RlVVJJQ29tcG9uZW50KCQkLnZhbCgpKSxcbiAgICAgICAgICAgICAgICAgICAgICAgIHN1Y2Nlc3M6IGZ1bmN0aW9uIChkYXRhKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgd3JpdGVJblByZXZpZXcobG9jYWxpemUoZGF0YSwgMSkpO1xuICAgICAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICB9KTtcbiAgICAgICAgICAgICAgICB9IGVsc2Uge1xuICAgICAgICAgICAgICAgICAgICBpZiAoIXRlbXBsYXRlKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAkLmFqYXgoe1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIHVybDogb3B0aW9ucy5wcmV2aWV3VGVtcGxhdGVQYXRoLFxuICAgICAgICAgICAgICAgICAgICAgICAgICAgIGRhdGFUeXBlOiAndGV4dCcsXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgZ2xvYmFsOiBmYWxzZSxcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBzdWNjZXNzOiBmdW5jdGlvbiAoZGF0YSkge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICB3cml0ZUluUHJldmlldyhsb2NhbGl6ZShkYXRhLCAxKS5yZXBsYWNlKC88IS0tIGNvbnRlbnQgLS0+L2csICQkLnZhbCgpKSk7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICAgICAgfSk7XG4gICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgcmV0dXJuIGZhbHNlO1xuICAgICAgICAgICAgfVxuXG4gICAgICAgICAgICBmdW5jdGlvbiB3cml0ZUluUHJldmlldyhkYXRhKSB7XG4gICAgICAgICAgICAgICAgaWYgKG9wdGlvbnMucHJldmlld0luRWxlbWVudCkge1xuICAgICAgICAgICAgICAgICAgICAkKG9wdGlvbnMucHJldmlld0luRWxlbWVudCkuaHRtbChkYXRhKTtcbiAgICAgICAgICAgICAgICB9IGVsc2UgaWYgKHByZXZpZXdXaW5kb3cgJiYgcHJldmlld1dpbmRvdy5kb2N1bWVudCkge1xuICAgICAgICAgICAgICAgICAgICB0cnkge1xuICAgICAgICAgICAgICAgICAgICAgICAgc3AgPSBwcmV2aWV3V2luZG93LmRvY3VtZW50LmRvY3VtZW50RWxlbWVudC5zY3JvbGxUb3BcbiAgICAgICAgICAgICAgICAgICAgfSBjYXRjaCAoZSkge1xuICAgICAgICAgICAgICAgICAgICAgICAgc3AgPSAwO1xuICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgIHByZXZpZXdXaW5kb3cuZG9jdW1lbnQub3BlbigpO1xuICAgICAgICAgICAgICAgICAgICBwcmV2aWV3V2luZG93LmRvY3VtZW50LndyaXRlKGRhdGEpO1xuICAgICAgICAgICAgICAgICAgICBwcmV2aWV3V2luZG93LmRvY3VtZW50LmNsb3NlKCk7XG4gICAgICAgICAgICAgICAgICAgIHByZXZpZXdXaW5kb3cuZG9jdW1lbnQuZG9jdW1lbnRFbGVtZW50LnNjcm9sbFRvcCA9IHNwO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgIH1cblxuICAgICAgICAgICAgLy8gc2V0IGtleXMgcHJlc3NlZFxuICAgICAgICAgICAgZnVuY3Rpb24ga2V5UHJlc3NlZChlKSB7XG4gICAgICAgICAgICAgICAgdmFyIGxpO1xuXG4gICAgICAgICAgICAgICAgc2hpZnRLZXkgPSBlLnNoaWZ0S2V5O1xuICAgICAgICAgICAgICAgIGFsdEtleSA9IGUuYWx0S2V5O1xuICAgICAgICAgICAgICAgIGN0cmxLZXkgPSAoIShlLmFsdEtleSAmJiBlLmN0cmxLZXkpKSA/IChlLmN0cmxLZXkgfHwgZS5tZXRhS2V5KSA6IGZhbHNlO1xuXG4gICAgICAgICAgICAgICAgaWYgKGUudHlwZSA9PT0gJ2tleWRvd24nKSB7XG4gICAgICAgICAgICAgICAgICAgIGlmIChjdHJsS2V5ID09PSB0cnVlKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICBsaSA9ICQoJ2FbYWNjZXNza2V5PVwiJyArICgoZS5rZXlDb2RlID09IDEzKSA/ICdcXFxcbicgOiBTdHJpbmcuZnJvbUNoYXJDb2RlKGUua2V5Q29kZSkpICsgJ1wiXScsIGhlYWRlcikucGFyZW50KCdsaScpO1xuICAgICAgICAgICAgICAgICAgICAgICAgaWYgKGxpLmxlbmd0aCAhPT0gMCkge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIGN0cmxLZXkgPSBmYWxzZTtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBzZXRUaW1lb3V0KGZ1bmN0aW9uICgpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgbGkudHJpZ2dlckhhbmRsZXIoJ21vdXNldXAnKTtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICB9LCAxKTtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICByZXR1cm4gZmFsc2U7XG4gICAgICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgaWYgKGUua2V5Q29kZSA9PT0gMTMgfHwgZS5rZXlDb2RlID09PSAxMCkgeyAvLyBFbnRlciBrZXlcbiAgICAgICAgICAgICAgICAgICAgICAgIGlmIChjdHJsS2V5ID09PSB0cnVlKSB7ICAvLyBFbnRlciArIEN0cmxcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBjdHJsS2V5ID0gZmFsc2U7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgbWFya3VwKG9wdGlvbnMub25DdHJsRW50ZXIpO1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIHJldHVybiBvcHRpb25zLm9uQ3RybEVudGVyLmtlZXBEZWZhdWx0O1xuICAgICAgICAgICAgICAgICAgICAgICAgfSBlbHNlIGlmIChzaGlmdEtleSA9PT0gdHJ1ZSkgeyAvLyBFbnRlciArIFNoaWZ0XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgc2hpZnRLZXkgPSBmYWxzZTtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBtYXJrdXAob3B0aW9ucy5vblNoaWZ0RW50ZXIpO1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIHJldHVybiBvcHRpb25zLm9uU2hpZnRFbnRlci5rZWVwRGVmYXVsdDtcbiAgICAgICAgICAgICAgICAgICAgICAgIH0gZWxzZSB7IC8vIG9ubHkgRW50ZXJcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBtYXJrdXAob3B0aW9ucy5vbkVudGVyKTtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICByZXR1cm4gb3B0aW9ucy5vbkVudGVyLmtlZXBEZWZhdWx0O1xuICAgICAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgIGlmIChlLmtleUNvZGUgPT09IDkpIHsgLy8gVGFiIGtleVxuICAgICAgICAgICAgICAgICAgICAgICAgaWYgKHNoaWZ0S2V5ID09IHRydWUgfHwgY3RybEtleSA9PSB0cnVlIHx8IGFsdEtleSA9PSB0cnVlKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgcmV0dXJuIGZhbHNlO1xuICAgICAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICAgICAgaWYgKGNhcmV0T2Zmc2V0ICE9PSAtMSkge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIGdldCgpO1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIGNhcmV0T2Zmc2V0ID0gJCQudmFsKCkubGVuZ3RoIC0gY2FyZXRPZmZzZXQ7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgc2V0KGNhcmV0T2Zmc2V0LCAwKTtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBjYXJldE9mZnNldCA9IC0xO1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIHJldHVybiBmYWxzZTtcbiAgICAgICAgICAgICAgICAgICAgICAgIH0gZWxzZSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgbWFya3VwKG9wdGlvbnMub25UYWIpO1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIHJldHVybiBvcHRpb25zLm9uVGFiLmtlZXBEZWZhdWx0O1xuICAgICAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgfVxuXG4gICAgICAgICAgICBmdW5jdGlvbiByZW1vdmUoKSB7XG4gICAgICAgICAgICAgICAgJCQudW5iaW5kKFwiLm1hcmtJdFVwXCIpLnJlbW92ZUNsYXNzKCdtYXJrSXRVcEVkaXRvcicpO1xuICAgICAgICAgICAgICAgICQkLnBhcmVudCgnZGl2JykucGFyZW50KCdkaXYubWFya0l0VXAnKS5wYXJlbnQoJ2RpdicpLnJlcGxhY2VXaXRoKCQkKTtcbiAgICAgICAgICAgICAgICAkJC5kYXRhKCdtYXJrSXRVcCcsIG51bGwpO1xuICAgICAgICAgICAgfVxuXG4gICAgICAgICAgICBpbml0KCk7XG4gICAgICAgIH0pO1xuICAgIH07XG5cbiAgICAkLmZuLm1hcmtJdFVwUmVtb3ZlID0gZnVuY3Rpb24gKCkge1xuICAgICAgICByZXR1cm4gdGhpcy5lYWNoKGZ1bmN0aW9uICgpIHtcbiAgICAgICAgICAgICAgICAkKHRoaXMpLm1hcmtJdFVwKCdyZW1vdmUnKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgKTtcbiAgICB9O1xuXG4gICAgJC5tYXJrSXRVcCA9IGZ1bmN0aW9uIChzZXR0aW5ncykge1xuICAgICAgICB2YXIgb3B0aW9ucyA9IHt0YXJnZXQ6IGZhbHNlfTtcbiAgICAgICAgJC5leHRlbmQob3B0aW9ucywgc2V0dGluZ3MpO1xuICAgICAgICBpZiAob3B0aW9ucy50YXJnZXQpIHtcbiAgICAgICAgICAgIHJldHVybiAkKG9wdGlvbnMudGFyZ2V0KS5lYWNoKGZ1bmN0aW9uICgpIHtcbiAgICAgICAgICAgICAgICAkKHRoaXMpLmZvY3VzKCk7XG4gICAgICAgICAgICAgICAgJCh0aGlzKS50cmlnZ2VyKCdpbnNlcnRpb24nLCBbb3B0aW9uc10pO1xuICAgICAgICAgICAgfSk7XG4gICAgICAgIH0gZWxzZSB7XG4gICAgICAgICAgICAkKCd0ZXh0YXJlYScpLnRyaWdnZXIoJ2luc2VydGlvbicsIFtvcHRpb25zXSk7XG4gICAgICAgIH1cbiAgICB9O1xufSkoKTtcblxuXG5cblxuLyoqIFdFQlBBQ0sgRk9PVEVSICoqXG4gKiogLi9mcm9udGVuZC9qcy9wbHVnaW5zL21hcmtkb3duL19qcXVlcnkubWFya2l0dXAuanNcbiAqKi8iXSwic291cmNlUm9vdCI6IiJ9