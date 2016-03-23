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
/* 23 */,
/* 24 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';
	
	var _notificationCommercial = __webpack_require__(25);
	
	var _notificationCommercial2 = _interopRequireDefault(_notificationCommercial);
	
	var _scrollTo = __webpack_require__(26);
	
	var _scrollTo2 = _interopRequireDefault(_scrollTo);
	
	var _notifationList = __webpack_require__(27);
	
	var _notifationList2 = _interopRequireDefault(_notifationList);
	
	var _previewMarkdown = __webpack_require__(28);
	
	var _previewMarkdown2 = _interopRequireDefault(_previewMarkdown);
	
	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }
	
	$(function () {
	    _scrollTo2.default.plugin('.js-link-target');
	    _notifationList2.default.plugin('.js-notification-list');
	    _previewMarkdown2.default.plugin('[markdownpreview]');
	    _notificationCommercial2.default.plugin('.js-notif-commercial');
	
	    var $dataField = $('[data-type="date"]');
	    $dataField.length && $dataField.datetimepicker({
	        useCurrent: false,
	        pickTime: false
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
/* 27 */
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
/* 28 */
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
	    function Widget(selector, options) {
	        (0, _classCallCheck3.default)(this, Widget);
	
	        var defaultOptions = {
	            url: jsRoutes.controllers.Utilities.markdown().url,
	            interval: 1000,
	            markdownposition: "body",
	            template: "<div class='popover-bl'><i class='fa fa-spinner fa-spin'></i><div class='popover-bl__content' data-content></div></div>"
	        };
	
	        this.$root = $(selector);
	        this.options = $.extend({}, defaultOptions, options);
	
	        this.resetState();
	        this.assignEvents();
	    }
	
	    (0, _createClass3.default)(Widget, [{
	        key: "resetState",
	        value: function resetState() {
	            this.sending = null;
	            this.sending = null;
	            this.isNeedUpdating = null;
	            this.waitingTimer = null;
	        }
	    }, {
	        key: "createPopover",
	        value: function createPopover() {
	            this.$popover = $(this.options.template);
	
	            if (this.options.markdownclass) {
	                this.$popover.addClass(this.options.markdownclass);
	            }
	
	            if (this.options.markdownposition == "body") {
	                this.$popover.appendTo('body');
	            } else {
	                this.$popover.insertAfter(this.$root);
	            }
	        }
	    }, {
	        key: "getPosition",
	        value: function getPosition() {
	            var $root = this.$root;
	            var offsetBody = {
	                top: $root.offset().top,
	                left: $root.offset().left + $root.outerWidth()
	            };
	
	            return this.options.markdownposition == "body" ? offsetBody : {};
	        }
	    }, {
	        key: "assignEvents",
	        value: function assignEvents() {
	            var self = this;
	
	            self.$root.on('focus', function () {
	                self.toggle();
	                self.compileContent();
	            }).on('keyup', function (e) {
	                if (!self.isKeyTrigger(e.which)) return true;
	
	                self.pausing && (self.isNeedUpdating = true);
	                self.compileContent();
	            }).on('blur', function () {
	                self.resetState();
	                self.toggle();
	            });
	
	            $(window).resize(function () {
	                self.$popover && self.$popover.css(self.getPosition());
	            });
	        }
	    }, {
	        key: "compileContent",
	        value: function compileContent(content) {
	            var self = this;
	            content = content || self.$root.val();
	
	            if (self.sending || self.pausing) return;
	
	            self.pausing = true;
	            self.setSending();
	            self.setWaiting();
	
	            $.post(self.options.url, {
	                data: content
	            }, function (data) {
	                self.setSended();
	                self.$popover.find('[data-content]').html(data);
	            });
	        }
	    }, {
	        key: "setSending",
	        value: function setSending() {
	            this.sending = true;
	            this.$popover.addClass('popover-bl_loading');
	        }
	    }, {
	        key: "setSended",
	        value: function setSended() {
	            this.sending = false;
	            this.$popover.removeClass('popover-bl_loading');
	        }
	    }, {
	        key: "setWaiting",
	        value: function setWaiting() {
	            var self = this;
	
	            self.waitingTimer = setTimeout(function () {
	                self.pausing = false;
	                if (self.isNeedUpdating) {
	                    self.isNeedUpdating = false;
	                    self.compileContent();
	                }
	            }, self.options.interval);
	        }
	    }, {
	        key: "show",
	        value: function show() {
	            if (!this.$popover) this.createPopover();
	            this.$popover.css(this.getPosition());
	
	            if (this.isVisible) return;
	
	            this.isVisible = true;
	            this.$popover.addClass('popover-bl_show');
	        }
	    }, {
	        key: "hide",
	        value: function hide() {
	            if (!this.isVisible) return;
	
	            this.isVisible = false;
	            this.$popover.removeClass('popover-bl_show');
	        }
	    }, {
	        key: "toggle",
	        value: function toggle() {
	            this[this.isVisible ? 'hide' : 'show']();
	        }
	    }, {
	        key: "isKeyTrigger",
	        value: function isKeyTrigger(code) {
	            return code >= 45 && code <= 90 || code >= 186 && code <= 222 || code == 13 || code == 27 || code == 32 || code == 8;
	        }
	
	        // static
	
	    }], [{
	        key: "plugin",
	        value: function plugin(selector, options) {
	            var $elems = $(selector);
	            if (!$elems.length) return;
	
	            return $elems.each(function (index, el) {
	                var $element = $(el);
	                var data = $element.data('widget.preview.markdown');
	
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
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIndlYnBhY2s6Ly8vd2VicGFjay9ib290c3RyYXAgYTNlODM1N2MwMzkxODU2OTU4MDE/MWE3OCIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvaGVscGVycy9jbGFzc0NhbGxDaGVjay5qcz8yMWFmIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9oZWxwZXJzL2NyZWF0ZUNsYXNzLmpzPzFkZmUiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL2NvcmUtanMvb2JqZWN0L2RlZmluZS1wcm9wZXJ0eS5qcz81ZjcwIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9mbi9vYmplY3QvZGVmaW5lLXByb3BlcnR5LmpzPzRkMzMiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvZXM2Lm9iamVjdC5kZWZpbmUtcHJvcGVydHkuanM/OGJkZSIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZXhwb3J0LmpzPzhlNDAiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2dsb2JhbC5qcz8zYzUyIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19jb3JlLmpzP2Q2MTEiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2N0eC5qcz80ZTU5Iiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19hLWZ1bmN0aW9uLmpzPzA2OTkiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2hpZGUuanM/MGQyZSIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fb2JqZWN0LWRwLmpzPzU5ODYiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2FuLW9iamVjdC5qcz8zYWYyIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19pcy1vYmplY3QuanM/Y2ZkYSIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9faWU4LWRvbS1kZWZpbmUuanM/YjEwMiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZGVzY3JpcHRvcnMuanM/YzBmNSIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZmFpbHMuanM/YzZkZCIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZG9tLWNyZWF0ZS5qcz82MTJmIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL190by1wcmltaXRpdmUuanM/MWE2NSIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fcHJvcGVydHktZGVzYy5qcz8yNTZiIiwid2VicGFjazovLy8uL2Zyb250ZW5kL2pzL2NvbW1vbi1wYWNrLmpzIiwid2VicGFjazovLy8uL2Zyb250ZW5kL2pzL2xheW91dC9fbm90aWZpY2F0aW9uLWNvbW1lcmNpYWwuanMiLCJ3ZWJwYWNrOi8vLy4vZnJvbnRlbmQvanMvbGF5b3V0L19zY3JvbGwtdG8uanMiLCJ3ZWJwYWNrOi8vLy4vZnJvbnRlbmQvanMvbGF5b3V0L19ub3RpZmF0aW9uLWxpc3QuanMiLCJ3ZWJwYWNrOi8vLy4vZnJv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LElBQXhCLEVBRk87a0JBQVg7Y0FKZSxDQUFuQixDQUo2Qjs7O1lBN0loQiIsImZpbGUiOiJjb21tb24tcGFjay5qcyIsInNvdXJjZXNDb250ZW50IjpbIiBcdC8vIFRoZSBtb2R1bGUgY2FjaGVcbiBcdHZhciBpbnN0YWxsZWRNb2R1bGVzID0ge307XG5cbiBcdC8vIFRoZSByZXF1aXJlIGZ1bmN0aW9uXG4gXHRmdW5jdGlvbiBfX3dlYnBhY2tfcmVxdWlyZV9fKG1vZHVsZUlkKSB7XG5cbiBcdFx0Ly8gQ2hlY2sgaWYgbW9kdWxlIGlzIGluIGNhY2hlXG4gXHRcdGlmKGluc3RhbGxlZE1vZHVsZXNbbW9kdWxlSWRdKVxuIFx0XHRcdHJldHVybiBpbnN0YWxsZWRNb2R1bGVzW21vZHVsZUlkXS5leHBvcnRzO1xuXG4gXHRcdC8vIENyZWF0ZSBhIG5ldyBtb2R1bGUgKGFuZCBwdXQgaXQgaW50byB0aGUgY2FjaGUpXG4gXHRcdHZhciBtb2R1bGUgPSBpbnN0YWxsZWRNb2R1bGVzW21vZHVsZUlkXSA9IHtcbiBcdFx0XHRleHBvcnRzOiB7fSxcbiBcdFx0XHRpZDogbW9kdWxlSWQsXG4gXHRcdFx0bG9hZGVkOiBmYWxzZVxuIFx0XHR9O1xuXG4gXHRcdC8vIEV4ZWN1dGUgdGhlIG1vZHVsZSBmdW5jdGlvblxuIFx0XHRtb2R1bGVzW21vZHVsZUlkXS5jYWxsKG1vZHVsZS5leHBvcnRzLCBtb2R1bGUsIG1vZHVsZS5leHBvcnRzLCBfX3dlYnBhY2tfcmVxdWlyZV9fKTtcblxuIFx0XHQvLyBGbGFnIHRoZSBtb2R1bGUgYXMgbG9hZGVkXG4gXHRcdG1vZHVsZS5sb2FkZWQgPSB0cnVlO1xuXG4gXHRcdC8vIFJldHVybiB0aGUgZXhwb3J0cyBvZiB0aGUgbW9kdWxlXG4gXHRcdHJldHVybiBtb2R1bGUuZXhwb3J0cztcbiBcdH1cblxuXG4gXHQvLyBleHBvc2UgdGhlIG1vZHVsZXMgb2JqZWN0IChfX3dlYnBhY2tfbW9kdWxlc19fKVxuIFx0X193ZWJwYWNrX3JlcXVpcmVfXy5tID0gbW9kdWxlcztcblxuIFx0Ly8gZXhwb3NlIHRoZSBtb2R1bGUgY2FjaGVcbiBcdF9fd2VicGFja19yZXF1aXJlX18uYyA9IGluc3RhbGxlZE1vZHVsZXM7XG5cbiBcdC8vIF9fd2VicGFja19wdWJsaWNfcGF0aF9fXG4gXHRfX3dlYnBhY2tfcmVxdWlyZV9fLnAgPSBcIlwiO1xuXG4gXHQvLyBMb2FkIGVudHJ5IG1vZHVsZSBhbmQgcmV0dXJuIGV4cG9ydHNcbiBcdHJldHVybiBfX3dlYnBhY2tfcmVxdWlyZV9fKDApO1xuXG5cblxuLyoqIFdFQlBBQ0sgRk9PVEVSICoqXG4gKiogd2VicGFjay9ib290c3RyYXAgYTNlODM1N2MwMzkxODU2OTU4MDFcbiAqKi8iLCJcInVzZSBzdHJpY3RcIjtcblxuZXhwb3J0cy5fX2VzTW9kdWxlID0gdHJ1ZTtcblxuZXhwb3J0cy5kZWZhdWx0ID0gZnVuY3Rpb24gKGluc3RhbmNlLCBDb25zdHJ1Y3Rvcikge1xuICBpZiAoIShpbnN0YW5jZSBpbnN0YW5jZW9mIENvbnN0cnVjdG9yKSkge1xuICAgIHRocm93IG5ldyBUeXBlRXJyb3IoXCJDYW5ub3QgY2FsbCBhIGNsYXNzIGFzIGEgZnVuY3Rpb25cIik7XG4gIH1cbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9oZWxwZXJzL2NsYXNzQ2FsbENoZWNrLmpzXG4gKiogbW9kdWxlIGlkID0gM1xuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNVxuICoqLyIsIlwidXNlIHN0cmljdFwiO1xuXG5leHBvcnRzLl9fZXNNb2R1bGUgPSB0cnVlO1xuXG52YXIgX2RlZmluZVByb3BlcnR5ID0gcmVxdWlyZShcImJhYmVsLXJ1bnRpbWUvY29yZS1qcy9vYmplY3QvZGVmaW5lLXByb3BlcnR5XCIpO1xuXG52YXIgX2RlZmluZVByb3BlcnR5MiA9IF9pbnRlcm9wUmVxdWlyZURlZmF1bHQoX2RlZmluZVByb3BlcnR5KTtcblxuZnVuY3Rpb24gX2ludGVyb3BSZXF1aXJlRGVmYXVsdChvYmopIHsgcmV0dXJuIG9iaiAmJiBvYmouX19lc01vZHVsZSA/IG9iaiA6IHsgZGVmYXVsdDogb2JqIH07IH1cblxuZXhwb3J0cy5kZWZhdWx0ID0gZnVuY3Rpb24gKCkge1xuICBmdW5jdGlvbiBkZWZpbmVQcm9wZXJ0aWVzKHRhcmdldCwgcHJvcHMpIHtcbiAgICBmb3IgKHZhciBpID0gMDsgaSA8IHByb3BzLmxlbmd0aDsgaSsrKSB7XG4gICAgICB2YXIgZGVzY3JpcHRvciA9IHByb3BzW2ldO1xuICAgICAgZGVzY3JpcHRvci5lbnVtZXJhYmxlID0gZGVzY3JpcHRvci5lbnVtZXJhYmxlIHx8IGZhbHNlO1xuICAgICAgZGVzY3JpcHRvci5jb25maWd1cmFibGUgPSB0cnVlO1xuICAgICAgaWYgKFwidmFsdWVcIiBpbiBkZXNjcmlwdG9yKSBkZXNjcmlwdG9yLndyaXRhYmxlID0gdHJ1ZTtcbiAgICAgICgwLCBfZGVmaW5lUHJvcGVydHkyLmRlZmF1bHQpKHRhcmdldCwgZGVzY3JpcHRvci5rZXksIGRlc2NyaXB0b3IpO1xuICAgIH1cbiAgfVxuXG4gIHJldHVybiBmdW5jdGlvbiAoQ29uc3RydWN0b3IsIHByb3RvUHJvcHMsIHN0YXRpY1Byb3BzKSB7XG4gICAgaWYgKHByb3RvUHJvcHMpIGRlZmluZVByb3BlcnRpZXMoQ29uc3RydWN0b3IucHJvdG90eXBlLCBwcm90b1Byb3BzKTtcbiAgICBpZiAoc3RhdGljUHJvcHMpIGRlZmluZVByb3BlcnRpZXMoQ29uc3RydWN0b3IsIHN0YXRpY1Byb3BzKTtcbiAgICByZXR1cm4gQ29uc3RydWN0b3I7XG4gIH07XG59KCk7XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9oZWxwZXJzL2NyZWF0ZUNsYXNzLmpzXG4gKiogbW9kdWxlIGlkID0gNFxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNVxuICoqLyIsIm1vZHVsZS5leHBvcnRzID0geyBcImRlZmF1bHRcIjogcmVxdWlyZShcImNvcmUtanMvbGlicmFyeS9mbi9vYmplY3QvZGVmaW5lLXByb3BlcnR5XCIpLCBfX2VzTW9kdWxlOiB0cnVlIH07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9jb3JlLWpzL29iamVjdC9kZWZpbmUtcHJvcGVydHkuanNcbiAqKiBtb2R1bGUgaWQgPSA1XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1XG4gKiovIiwicmVxdWlyZSgnLi4vLi4vbW9kdWxlcy9lczYub2JqZWN0LmRlZmluZS1wcm9wZXJ0eScpO1xudmFyICRPYmplY3QgPSByZXF1aXJlKCcuLi8uLi9tb2R1bGVzL19jb3JlJykuT2JqZWN0O1xubW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbiBkZWZpbmVQcm9wZXJ0eShpdCwga2V5LCBkZXNjKXtcbiAgcmV0dXJuICRPYmplY3QuZGVmaW5lUHJvcGVydHkoaXQsIGtleSwgZGVzYyk7XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvZm4vb2JqZWN0L2RlZmluZS1wcm9wZXJ0eS5qc1xuICoqIG1vZHVsZSBpZCA9IDZcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDVcbiAqKi8iLCJ2YXIgJGV4cG9ydCA9IHJlcXVpcmUoJy4vX2V4cG9ydCcpO1xyXG4vLyAxOS4xLjIuNCAvIDE1LjIuMy42IE9iamVjdC5kZWZpbmVQcm9wZXJ0eShPLCBQLCBBdHRyaWJ1dGVzKVxyXG4kZXhwb3J0KCRleHBvcnQuUyArICRleHBvcnQuRiAqICFyZXF1aXJlKCcuL19kZXNjcmlwdG9ycycpLCAnT2JqZWN0Jywge2RlZmluZVByb3BlcnR5OiByZXF1aXJlKCcuL19vYmplY3QtZHAnKS5mfSk7XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL2VzNi5vYmplY3QuZGVmaW5lLXByb3BlcnR5LmpzXG4gKiogbW9kdWxlIGlkID0gN1xuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNVxuICoqLyIsInZhciBnbG9iYWwgICAgPSByZXF1aXJlKCcuL19nbG9iYWwnKVxuICAsIGNvcmUgICAgICA9IHJlcXVpcmUoJy4vX2NvcmUnKVxuICAsIGN0eCAgICAgICA9IHJlcXVpcmUoJy4vX2N0eCcpXG4gICwgaGlkZSAgICAgID0gcmVxdWlyZSgnLi9faGlkZScpXG4gICwgUFJPVE9UWVBFID0gJ3Byb3RvdHlwZSc7XG5cbnZhciAkZXhwb3J0ID0gZnVuY3Rpb24odHlwZSwgbmFtZSwgc291cmNlKXtcbiAgdmFyIElTX0ZPUkNFRCA9IHR5cGUgJiAkZXhwb3J0LkZcbiAgICAsIElTX0dMT0JBTCA9IHR5cGUgJiAkZXhwb3J0LkdcbiAgICAsIElTX1NUQVRJQyA9IHR5cGUgJiAkZXhwb3J0LlNcbiAgICAsIElTX1BST1RPICA9IHR5cGUgJiAkZXhwb3J0LlBcbiAgICAsIElTX0JJTkQgICA9IHR5cGUgJiAkZXhwb3J0LkJcbiAgICAsIElTX1dSQVAgICA9IHR5cGUgJiAkZXhwb3J0LldcbiAgICAsIGV4cG9ydHMgICA9IElTX0dMT0JBTCA/IGNvcmUgOiBjb3JlW25hbWVdIHx8IChjb3JlW25hbWVdID0ge30pXG4gICAgLCBleHBQcm90byAgPSBleHBvcnRzW1BST1RPVFlQRV1cbiAgICAsIHRhcmdldCAgICA9IElTX0dMT0JBTCA/IGdsb2JhbCA6IElTX1NUQVRJQyA/IGdsb2JhbFtuYW1lXSA6IChnbG9iYWxbbmFtZV0gfHwge30pW1BST1RPVFlQRV1cbiAgICAsIGtleSwgb3duLCBvdXQ7XG4gIGlmKElTX0dMT0JBTClzb3VyY2UgPSBuYW1lO1xuICBmb3Ioa2V5IGluIHNvdXJjZSl7XG4gICAgLy8gY29udGFpbnMgaW4gbmF0aXZlXG4gICAgb3duID0gIUlTX0ZPUkNFRCAmJiB0YXJnZXQgJiYgdGFyZ2V0W2tleV0gIT09IHVuZGVmaW5lZDtcbiAgICBpZihvd24gJiYga2V5IGluIGV4cG9ydHMpY29udGludWU7XG4gICAgLy8gZXhwb3J0IG5hdGl2ZSBvciBwYXNzZWRcbiAgICBvdXQgPSBvd24gPyB0YXJnZXRba2V5XSA6IHNvdXJjZVtrZXldO1xuICAgIC8vIHByZXZlbnQgZ2xvYmFsIHBvbGx1dGlvbiBmb3IgbmFtZXNwYWNlc1xuICAgIGV4cG9ydHNba2V5XSA9IElTX0dMT0JBTCAmJiB0eXBlb2YgdGFyZ2V0W2tleV0gIT0gJ2Z1bmN0aW9uJyA/IHNvdXJjZVtrZXldXG4gICAgLy8gYmluZCB0aW1lcnMgdG8gZ2xvYmFsIGZvciBjYWxsIGZyb20gZXhwb3J0IGNvbnRleHRcbiAgICA6IElTX0JJTkQgJiYgb3duID8gY3R4KG91dCwgZ2xvYmFsKVxuICAgIC8vIHdyYXAgZ2xvYmFsIGNvbnN0cnVjdG9ycyBmb3IgcHJldmVudCBjaGFuZ2UgdGhlbSBpbiBsaWJyYXJ5XG4gICAgOiBJU19XUkFQICYmIHRhcmdldFtrZXldID09IG91dCA/IChmdW5jdGlvbihDKXtcbiAgICAgIHZhciBGID0gZnVuY3Rpb24oYSwgYiwgYyl7XG4gICAgICAgIGlmKHRoaXMgaW5zdGFuY2VvZiBDKXtcbiAgICAgICAgICBzd2l0Y2goYXJndW1lbnRzLmxlbmd0aCl7XG4gICAgICAgICAgICBjYXNlIDA6IHJldHVybiBuZXcgQztcbiAgICAgICAgICAgIGNhc2UgMTogcmV0dXJuIG5ldyBDKGEpO1xuICAgICAgICAgICAgY2FzZSAyOiByZXR1cm4gbmV3IEMoYSwgYik7XG4gICAgICAgICAgfSByZXR1cm4gbmV3IEMoYSwgYiwgYyk7XG4gICAgICAgIH0gcmV0dXJuIEMuYXBwbHkodGhpcywgYXJndW1lbnRzKTtcbiAgICAgIH07XG4gICAgICBGW1BST1RPVFlQRV0gPSBDW1BST1RPVFlQRV07XG4gICAgICByZXR1cm4gRjtcbiAgICAvLyBtYWtlIHN0YXRpYyB2ZXJzaW9ucyBmb3IgcHJvdG90eXBlIG1ldGhvZHNcbiAgICB9KShvdXQpIDogSVNfUFJPVE8gJiYgdHlwZW9mIG91dCA9PSAnZnVuY3Rpb24nID8gY3R4KEZ1bmN0aW9uLmNhbGwsIG91dCkgOiBvdXQ7XG4gICAgLy8gZXhwb3J0IHByb3RvIG1ldGhvZHMgdG8gY29yZS4lQ09OU1RSVUNUT1IlLm1ldGhvZHMuJU5BTUUlXG4gICAgaWYoSVNfUFJPVE8pe1xuICAgICAgKGV4cG9ydHMudmlydHVhbCB8fCAoZXhwb3J0cy52aXJ0dWFsID0ge30pKVtrZXldID0gb3V0O1xuICAgICAgLy8gZXhwb3J0IHByb3RvIG1ldGhvZHMgdG8gY29yZS4lQ09OU1RSVUNUT1IlLnByb3RvdHlwZS4lTkFNRSVcbiAgICAgIGlmKHR5cGUgJiAkZXhwb3J0LlIgJiYgZXhwUHJvdG8gJiYgIWV4cFByb3RvW2tleV0paGlkZShleHBQcm90bywga2V5LCBvdXQpO1xuICAgIH1cbiAgfVxufTtcbi8vIHR5cGUgYml0bWFwXG4kZXhwb3J0LkYgPSAxOyAgIC8vIGZvcmNlZFxuJGV4cG9ydC5HID0gMjsgICAvLyBnbG9iYWxcbiRleHBvcnQuUyA9IDQ7ICAgLy8gc3RhdGljXG4kZXhwb3J0LlAgPSA4OyAgIC8vIHByb3RvXG4kZXhwb3J0LkIgPSAxNjsgIC8vIGJpbmRcbiRleHBvcnQuVyA9IDMyOyAgLy8gd3JhcFxuJGV4cG9ydC5VID0gNjQ7ICAvLyBzYWZlXG4kZXhwb3J0LlIgPSAxMjg7IC8vIHJlYWwgcHJvdG8gbWV0aG9kIGZvciBgbGlicmFyeWAgXG5tb2R1bGUuZXhwb3J0cyA9ICRleHBvcnQ7XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19leHBvcnQuanNcbiAqKiBtb2R1bGUgaWQgPSA4XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1XG4gKiovIiwiLy8gaHR0cHM6Ly9naXRodWIuY29tL3psb2lyb2NrL2NvcmUtanMvaXNzdWVzLzg2I2lzc3VlY29tbWVudC0xMTU3NTkwMjhcbnZhciBnbG9iYWwgPSBtb2R1bGUuZXhwb3J0cyA9IHR5cGVvZiB3aW5kb3cgIT0gJ3VuZGVmaW5lZCcgJiYgd2luZG93Lk1hdGggPT0gTWF0aFxuICA/IHdpbmRvdyA6IHR5cGVvZiBzZWxmICE9ICd1bmRlZmluZWQnICYmIHNlbGYuTWF0aCA9PSBNYXRoID8gc2VsZiA6IEZ1bmN0aW9uKCdyZXR1cm4gdGhpcycpKCk7XG5pZih0eXBlb2YgX19nID09ICdudW1iZXInKV9fZyA9IGdsb2JhbDsgLy8gZXNsaW50LWRpc2FibGUtbGluZSBuby11bmRlZlxuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZ2xvYmFsLmpzXG4gKiogbW9kdWxlIGlkID0gOVxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNVxuICoqLyIsInZhciBjb3JlID0gbW9kdWxlLmV4cG9ydHMgPSB7dmVyc2lvbjogJzIuMi4wJ307XG5pZih0eXBlb2YgX19lID09ICdudW1iZXInKV9fZSA9IGNvcmU7IC8vIGVzbGludC1kaXNhYmxlLWxpbmUgbm8tdW5kZWZcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2NvcmUuanNcbiAqKiBtb2R1bGUgaWQgPSAxMFxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNVxuICoqLyIsIi8vIG9wdGlvbmFsIC8gc2ltcGxlIGNvbnRleHQgYmluZGluZ1xudmFyIGFGdW5jdGlvbiA9IHJlcXVpcmUoJy4vX2EtZnVuY3Rpb24nKTtcbm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oZm4sIHRoYXQsIGxlbmd0aCl7XG4gIGFGdW5jdGlvbihmbik7XG4gIGlmKHRoYXQgPT09IHVuZGVmaW5lZClyZXR1cm4gZm47XG4gIHN3aXRjaChsZW5ndGgpe1xuICAgIGNhc2UgMTogcmV0dXJuIGZ1bmN0aW9uKGEpe1xuICAgICAgcmV0dXJuIGZuLmNhbGwodGhhdCwgYSk7XG4gICAgfTtcbiAgICBjYXNlIDI6IHJldHVybiBmdW5jdGlvbihhLCBiKXtcbiAgICAgIHJldHVybiBmbi5jYWxsKHRoYXQsIGEsIGIpO1xuICAgIH07XG4gICAgY2FzZSAzOiByZXR1cm4gZnVuY3Rpb24oYSwgYiwgYyl7XG4gICAgICByZXR1cm4gZm4uY2FsbCh0aGF0LCBhLCBiLCBjKTtcbiAgICB9O1xuICB9XG4gIHJldHVybiBmdW5jdGlvbigvKiAuLi5hcmdzICovKXtcbiAgICByZXR1cm4gZm4uYXBwbHkodGhhdCwgYXJndW1lbnRzKTtcbiAgfTtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19jdHguanNcbiAqKiBtb2R1bGUgaWQgPSAxMVxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNVxuICoqLyIsIm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oaXQpe1xuICBpZih0eXBlb2YgaXQgIT0gJ2Z1bmN0aW9uJyl0aHJvdyBUeXBlRXJyb3IoaXQgKyAnIGlzIG5vdCBhIGZ1bmN0aW9uIScpO1xuICByZXR1cm4gaXQ7XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fYS1mdW5jdGlvbi5qc1xuICoqIG1vZHVsZSBpZCA9IDEyXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1XG4gKiovIiwidmFyIGRQICAgICAgICAgPSByZXF1aXJlKCcuL19vYmplY3QtZHAnKVxuICAsIGNyZWF0ZURlc2MgPSByZXF1aXJlKCcuL19wcm9wZXJ0eS1kZXNjJyk7XG5tb2R1bGUuZXhwb3J0cyA9IHJlcXVpcmUoJy4vX2Rlc2NyaXB0b3JzJykgPyBmdW5jdGlvbihvYmplY3QsIGtleSwgdmFsdWUpe1xuICByZXR1cm4gZFAuZihvYmplY3QsIGtleSwgY3JlYXRlRGVzYygxLCB2YWx1ZSkpO1xufSA6IGZ1bmN0aW9uKG9iamVjdCwga2V5LCB2YWx1ZSl7XG4gIG9iamVjdFtrZXldID0gdmFsdWU7XG4gIHJldHVybiBvYmplY3Q7XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9faGlkZS5qc1xuICoqIG1vZHVsZSBpZCA9IDEzXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1XG4gKiovIiwidmFyIGFuT2JqZWN0ICAgICAgID0gcmVxdWlyZSgnLi9fYW4tb2JqZWN0JylcbiAgLCBJRThfRE9NX0RFRklORSA9IHJlcXVpcmUoJy4vX2llOC1kb20tZGVmaW5lJylcbiAgLCB0b1ByaW1pdGl2ZSAgICA9IHJlcXVpcmUoJy4vX3RvLXByaW1pdGl2ZScpXG4gICwgZFAgICAgICAgICAgICAgPSBPYmplY3QuZGVmaW5lUHJvcGVydHk7XG5cbmV4cG9ydHMuZiA9IHJlcXVpcmUoJy4vX2Rlc2NyaXB0b3JzJykgPyBPYmplY3QuZGVmaW5lUHJvcGVydHkgOiBmdW5jdGlvbiBkZWZpbmVQcm9wZXJ0eShPLCBQLCBBdHRyaWJ1dGVzKXtcbiAgYW5PYmplY3QoTyk7XG4gIFAgPSB0b1ByaW1pdGl2ZShQLCB0cnVlKTtcbiAgYW5PYmplY3QoQXR0cmlidXRlcyk7XG4gIGlmKElFOF9ET01fREVGSU5FKXRyeSB7XG4gICAgcmV0dXJuIGRQKE8sIFAsIEF0dHJpYnV0ZXMpO1xuICB9IGNhdGNoKGUpeyAvKiBlbXB0eSAqLyB9XG4gIGlmKCdnZXQnIGluIEF0dHJpYnV0ZXMgfHwgJ3NldCcgaW4gQXR0cmlidXRlcyl0aHJvdyBUeXBlRXJyb3IoJ0FjY2Vzc29ycyBub3Qgc3VwcG9ydGVkIScpO1xuICBpZigndmFsdWUnIGluIEF0dHJpYnV0ZXMpT1tQXSA9IEF0dHJpYnV0ZXMudmFsdWU7XG4gIHJldHVybiBPO1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX29iamVjdC1kcC5qc1xuICoqIG1vZHVsZSBpZCA9IDE0XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1XG4gKiovIiwidmFyIGlzT2JqZWN0ID0gcmVxdWlyZSgnLi9faXMtb2JqZWN0Jyk7XG5tb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGl0KXtcbiAgaWYoIWlzT2JqZWN0KGl0KSl0aHJvdyBUeXBlRXJyb3IoaXQgKyAnIGlzIG5vdCBhbiBvYmplY3QhJyk7XG4gIHJldHVybiBpdDtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19hbi1vYmplY3QuanNcbiAqKiBtb2R1bGUgaWQgPSAxNVxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNVxuICoqLyIsIm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oaXQpe1xuICByZXR1cm4gdHlwZW9mIGl0ID09PSAnb2JqZWN0JyA/IGl0ICE9PSBudWxsIDogdHlwZW9mIGl0ID09PSAnZnVuY3Rpb24nO1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2lzLW9iamVjdC5qc1xuICoqIG1vZHVsZSBpZCA9IDE2XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1XG4gKiovIiwibW9kdWxlLmV4cG9ydHMgPSAhcmVxdWlyZSgnLi9fZGVzY3JpcHRvcnMnKSAmJiAhcmVxdWlyZSgnLi9fZmFpbHMnKShmdW5jdGlvbigpe1xyXG4gIHJldHVybiBPYmplY3QuZGVmaW5lUHJvcGVydHkocmVxdWlyZSgnLi9fZG9tLWNyZWF0ZScpKCdkaXYnKSwgJ2EnLCB7Z2V0OiBmdW5jdGlvbigpeyByZXR1cm4gNzsgfX0pLmEgIT0gNztcclxufSk7XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19pZTgtZG9tLWRlZmluZS5qc1xuICoqIG1vZHVsZSBpZCA9IDE3XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1XG4gKiovIiwiLy8gVGhhbmsncyBJRTggZm9yIGhpcyBmdW5ueSBkZWZpbmVQcm9wZXJ0eVxubW9kdWxlLmV4cG9ydHMgPSAhcmVxdWlyZSgnLi9fZmFpbHMnKShmdW5jdGlvbigpe1xuICByZXR1cm4gT2JqZWN0LmRlZmluZVByb3BlcnR5KHt9LCAnYScsIHtnZXQ6IGZ1bmN0aW9uKCl7IHJldHVybiA3OyB9fSkuYSAhPSA3O1xufSk7XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19kZXNjcmlwdG9ycy5qc1xuICoqIG1vZHVsZSBpZCA9IDE4XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1XG4gKiovIiwibW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihleGVjKXtcbiAgdHJ5IHtcbiAgICByZXR1cm4gISFleGVjKCk7XG4gIH0gY2F0Y2goZSl7XG4gICAgcmV0dXJuIHRydWU7XG4gIH1cbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19mYWlscy5qc1xuICoqIG1vZHVsZSBpZCA9IDE5XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1XG4gKiovIiwidmFyIGlzT2JqZWN0ID0gcmVxdWlyZSgnLi9faXMtb2JqZWN0JylcbiAgLCBkb2N1bWVudCA9IHJlcXVpcmUoJy4vX2dsb2JhbCcpLmRvY3VtZW50XG4gIC8vIGluIG9sZCBJRSB0eXBlb2YgZG9jdW1lbnQuY3JlYXRlRWxlbWVudCBpcyAnb2JqZWN0J1xuICAsIGlzID0gaXNPYmplY3QoZG9jdW1lbnQpICYmIGlzT2JqZWN0KGRvY3VtZW50LmNyZWF0ZUVsZW1lbnQpO1xubW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihpdCl7XG4gIHJldHVybiBpcyA/IGRvY3VtZW50LmNyZWF0ZUVsZW1lbnQoaXQpIDoge307XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZG9tLWNyZWF0ZS5qc1xuICoqIG1vZHVsZSBpZCA9IDIwXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1XG4gKiovIiwiLy8gNy4xLjEgVG9QcmltaXRpdmUoaW5wdXQgWywgUHJlZmVycmVkVHlwZV0pXG52YXIgaXNPYmplY3QgPSByZXF1aXJlKCcuL19pcy1vYmplY3QnKTtcbi8vIGluc3RlYWQgb2YgdGhlIEVTNiBzcGVjIHZlcnNpb24sIHdlIGRpZG4ndCBpbXBsZW1lbnQgQEB0b1ByaW1pdGl2ZSBjYXNlXG4vLyBhbmQgdGhlIHNlY29uZCBhcmd1bWVudCAtIGZsYWcgLSBwcmVmZXJyZWQgdHlwZSBpcyBhIHN0cmluZ1xubW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihpdCwgUyl7XG4gIGlmKCFpc09iamVjdChpdCkpcmV0dXJuIGl0O1xuICB2YXIgZm4sIHZhbDtcbiAgaWYoUyAmJiB0eXBlb2YgKGZuID0gaXQudG9TdHJpbmcpID09ICdmdW5jdGlvbicgJiYgIWlzT2JqZWN0KHZhbCA9IGZuLmNhbGwoaXQpKSlyZXR1cm4gdmFsO1xuICBpZih0eXBlb2YgKGZuID0gaXQudmFsdWVPZikgPT0gJ2Z1bmN0aW9uJyAmJiAhaXNPYmplY3QodmFsID0gZm4uY2FsbChpdCkpKXJldHVybiB2YWw7XG4gIGlmKCFTICYmIHR5cGVvZiAoZm4gPSBpdC50b1N0cmluZykgPT0gJ2Z1bmN0aW9uJyAmJiAhaXNPYmplY3QodmFsID0gZm4uY2FsbChpdCkpKXJldHVybiB2YWw7XG4gIHRocm93IFR5cGVFcnJvcihcIkNhbid0IGNvbnZlcnQgb2JqZWN0IHRvIHByaW1pdGl2ZSB2YWx1ZVwiKTtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL190by1wcmltaXRpdmUuanNcbiAqKiBtb2R1bGUgaWQgPSAyMVxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNVxuICoqLyIsIm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oYml0bWFwLCB2YWx1ZSl7XG4gIHJldHVybiB7XG4gICAgZW51bWVyYWJsZSAgOiAhKGJpdG1hcCAmIDEpLFxuICAgIGNvbmZpZ3VyYWJsZTogIShiaXRtYXAgJiAyKSxcbiAgICB3cml0YWJsZSAgICA6ICEoYml0bWFwICYgNCksXG4gICAgdmFsdWUgICAgICAgOiB2YWx1ZVxuICB9O1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX3Byb3BlcnR5LWRlc2MuanNcbiAqKiBtb2R1bGUgaWQgPSAyMlxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNVxuICoqLyIsIid1c2Ugc3RyaWN0JztcblxuaW1wb3J0IE5vdGlmQ29tbWVyY2lhbCBmcm9tIFwiLi9sYXlvdXQvX25vdGlmaWNhdGlvbi1jb21tZXJjaWFsXCI7XG5pbXBvcnQgU2Nyb2xsVG9FbGVtZW50IGZyb20gXCIuL2xheW91dC9fc2Nyb2xsLXRvXCI7XG5pbXBvcnQgTm90aWZpY2F0aW9uTGlzdCBmcm9tIFwiLi9sYXlvdXQvX25vdGlmYXRpb24tbGlzdFwiO1xuaW1wb3J0IFByZXZpZXdNYXJrZG93biBmcm9tIFwiLi9sYXlvdXQvX3ByZXZpZXctbWFya2Rvd25cIjtcblxuJChmdW5jdGlvbigpe1xuICAgIFNjcm9sbFRvRWxlbWVudC5wbHVnaW4oJy5qcy1saW5rLXRhcmdldCcpO1xuICAgIE5vdGlmaWNhdGlvbkxpc3QucGx1Z2luKCcuanMtbm90aWZpY2F0aW9uLWxpc3QnKTtcbiAgICBQcmV2aWV3TWFya2Rvd24ucGx1Z2luKCdbbWFya2Rvd25wcmV2aWV3XScpO1xuICAgIE5vdGlmQ29tbWVyY2lhbC5wbHVnaW4oJy5qcy1ub3RpZi1jb21tZXJjaWFsJyk7XG5cbiAgICBjb25zdCAkZGF0YUZpZWxkID0gJCgnW2RhdGEtdHlwZT1cImRhdGVcIl0nKTtcbiAgICAkZGF0YUZpZWxkLmxlbmd0aCAmJiAkZGF0YUZpZWxkLmRhdGV0aW1lcGlja2VyKHtcbiAgICAgICAgdXNlQ3VycmVudDogZmFsc2UsXG4gICAgICAgIHBpY2tUaW1lOiBmYWxzZVxuICAgIH0pO1xuXG5cbn0pO1xuXG5cblxuXG4vKiogV0VCUEFDSyBGT09URVIgKipcbiAqKiAuL2Zyb250ZW5kL2pzL2NvbW1vbi1wYWNrLmpzXG4gKiovIiwiJ3VzZSBzdHJpY3QnO1xuXG5leHBvcnQgZGVmYXVsdCBjbGFzcyBXaWRnZXQge1xuICAgIC8qKlxuICAgICAqIEZpbHRlciBoaXN0b3J5XG4gICAgICogQHBhcmFtIHtTdHJpbmd9IHNlbGVjdG9yXG4gICAgICovXG4gICAgY29uc3RydWN0b3Ioc2VsZWN0b3IpIHtcbiAgICAgICAgdGhpcy4kcm9vdCA9ICQoc2VsZWN0b3IpO1xuICAgICAgICB0aGlzLmxvY2FscyA9IHRoaXMuX2dldERvbSgpO1xuICAgICAgICB0aGlzLnVuaXF1ZUtleSA9ICdub3RpZmljdGlvbic7XG5cbiAgICAgICAgaWYgKCF0aGlzLl9pc1Nob3dlZCgpKSB7XG4gICAgICAgICAgICB0aGlzLiRyb290LnNsaWRlRG93bigpO1xuICAgICAgICB9XG4gICAgICAgIHRoaXMuX2Fzc2lnbkV2ZW50cygpO1xuICAgIH1cblxuICAgIF9nZXREb20oKSB7XG4gICAgICAgIGNvbnN0ICRyb290ID0gdGhpcy4kcm9vdDtcblxuICAgICAgICByZXR1cm4ge1xuICAgICAgICAgICAgJGxpc3Q6ICRyb290LmZpbmQoJ1tkYXRhLWZpbHRlci1saXN0XScpLFxuICAgICAgICAgICAgJGl0ZW1zOiAkcm9vdC5maW5kKCdbZGF0YS1maWx0ZXItdGV4dF0nKSxcbiAgICAgICAgfTtcbiAgICB9XG5cbiAgICBfYXNzaWduRXZlbnRzKCkge1xuICAgICAgICB0aGlzLiRyb290Lm9uKCdjbGljaycsICdbZGF0YS1ub3RpZl0nLCB0aGlzLl9vbkNsaWNrQnRuLmJpbmQodGhpcykpO1xuICAgIH1cblxuICAgIF9vbkNsaWNrQnRuKGUpe1xuICAgICAgICBlLnByZXZlbnREZWZhdWx0KCk7XG4gICAgICAgIGNvbnN0IHNlbGYgPSB0aGlzO1xuICAgICAgICBjb25zdCAkbGluayA9ICQoZS5jdXJyZW50VGFyZ2V0KTtcbiAgICAgICAgY29uc3QgdXJsID0gJGxpbmsuYXR0cignaHJlZicpO1xuXG4gICAgICAgIHNlbGYuX3NlbmRJc1Nob3dlZCh1cmwpXG4gICAgICAgICAgICAuZG9uZShmdW5jdGlvbigpe1xuICAgICAgICAgICAgICAgIHNlbGYuJHJvb3QuYWRkQ2xhc3MoJ2Itbm90aWZpY2F0aW9uX3N0YXRlX3RoYW5rJyk7XG4gICAgICAgICAgICAgICAgXG4gICAgICAgICAgICAgICAgc2V0VGltZW91dChmdW5jdGlvbigpe1xuICAgICAgICAgICAgICAgICAgICBzZWxmLmhpZGUoKTtcbiAgICAgICAgICAgICAgICB9LCAzMDAwKTtcbiAgICAgICAgICAgIH0pO1xuICAgIH1cblxuICAgIF9pc1Nob3dlZCgpIHtcbiAgICAgICAgY29uc3QgdmFsdWUgPSBsb2NhbFN0b3JhZ2UuZ2V0SXRlbSh0aGlzLnVuaXF1ZUtleSk7XG4gICAgICAgIHJldHVybiB2YWx1ZSAmJiB2YWx1ZSA9PSAnc2hvd2VkJztcbiAgICB9XG5cbiAgICBfc2VuZElzU2hvd2VkKHVybCkge1xuICAgICAgICBjb25zdCBzZWxmID0gdGhpcztcbiAgICAgICAgbGV0IGRlZmVyID0gJC5EZWZlcnJlZCgpO1xuXG4gICAgICAgICQucG9zdCh1cmwsIGZ1bmN0aW9uKCl7XG4gICAgICAgICAgICBsb2NhbFN0b3JhZ2Uuc2V0SXRlbShzZWxmLnVuaXF1ZUtleSwgJ3Nob3dlZCcpO1xuICAgICAgICAgICAgZGVmZXIucmVzb2x2ZSgpO1xuICAgICAgICB9KTtcbiAgICAgICAgXG4gICAgICAgIHJldHVybiBkZWZlci5wcm9taXNlKCk7XG4gICAgfVxuXG4gICAgaGlkZSgpIHtcbiAgICAgICAgdGhpcy4kcm9vdC5zbGlkZVVwKCk7XG4gICAgfVxuXG4gICAgLy8gc3RhdGljXG4gICAgc3RhdGljIHBsdWdpbihzZWxlY3Rvcikge1xuICAgICAgICBjb25zdCAkZWxlbXMgPSAkKHNlbGVjdG9yKTtcbiAgICAgICAgaWYgKCEkZWxlbXMubGVuZ3RoKSByZXR1cm47XG5cbiAgICAgICAgcmV0dXJuICRlbGVtcy5lYWNoKGZ1bmN0aW9uIChpbmRleCwgZWwpIHtcbiAgICAgICAgICAgIGxldCAkZWxlbWVudCA9ICQoZWwpO1xuICAgICAgICAgICAgbGV0IGRhdGEgPSAkZWxlbWVudC5kYXRhKCd3aWRnZXQnKTtcblxuICAgICAgICAgICAgaWYgKCFkYXRhKSB7XG4gICAgICAgICAgICAgICAgZGF0YSA9IG5ldyBXaWRnZXQoZWwpO1xuICAgICAgICAgICAgICAgICRlbGVtZW50LmRhdGEoJ3dpZGdldCcsIGRhdGEpO1xuICAgICAgICAgICAgfVxuICAgICAgICB9KVxuICAgIH1cbn1cblxuXG5cbi8qKiBXRUJQQUNLIEZPT1RFUiAqKlxuICoqIC4vZnJvbnRlbmQvanMvbGF5b3V0L19ub3RpZmljYXRpb24tY29tbWVyY2lhbC5qc1xuICoqLyIsIid1c2Ugc3RyaWN0JztcblxuZXhwb3J0IGRlZmF1bHQgY2xhc3MgV2lkZ2V0IHtcbiAgICAvKipcbiAgICAgKiBGaWx0ZXIgaGlzdG9yeVxuICAgICAqIEBwYXJhbSB7U3RyaW5nfSBzZWxlY3RvclxuICAgICAqL1xuICAgIGNvbnN0cnVjdG9yKHNlbGVjdG9yKSB7XG4gICAgICAgIHRoaXMuJHJvb3QgPSAkKHNlbGVjdG9yKTtcbiAgICAgICAgdGhpcy5fYXNzaWduRXZlbnRzKCk7XG4gICAgfVxuXG4gICAgX2Fzc2lnbkV2ZW50cygpIHtcbiAgICAgICAgdGhpcy4kcm9vdC5vbignY2xpY2snLCB0aGlzLl9vbkNsaWNrTGluay5iaW5kKHRoaXMpKTtcbiAgICB9XG5cbiAgICBfb25DbGlja0xpbmsoZSkge1xuICAgICAgICBjb25zdCAkbGluayA9ICQoZS5jdXJyZW50VGFyZ2V0KTtcbiAgICAgICAgY29uc3QgdGFyZ2V0ID0gJGxpbmsuZGF0YSgndGFyZ2V0Jyk7XG5cbiAgICAgICAgaWYgKCF0aGlzLl9pc1RhcmdldFZhbGlkKHRhcmdldCkpIHJldHVybiBmYWxzZTtcblxuICAgICAgICB0aGlzLnNjcm9sbFRvVGFyZ2V0KCcjJyArIHRhcmdldCk7XG4gICAgICAgIGUucHJldmVudERlZmF1bHQoKTtcbiAgICB9XG5cbiAgICBfaXNUYXJnZXRWYWxpZCh0YXJnZXQpe1xuICAgICAgICBsZXQgdmFsaWQgPSB0cnVlO1xuXG4gICAgICAgIGlmICghdGFyZ2V0KSB7XG4gICAgICAgICAgICBjb25zb2xlLmxvZygnVGhlcmUgaXMgbm8gZGF0YS10YXJnZXQgYXR0cmlidXRlIHdpdGggaWQtbmFtZSBmb3IgdGhpcyBsaW5rJyk7XG4gICAgICAgICAgICB2YWxpZCA9IGZhbHNlO1xuICAgICAgICB9XG5cbiAgICAgICAgaWYgKCEkKCcjJyArIHRhcmdldCkubGVuZ3RoKSB7XG4gICAgICAgICAgICBjb25zb2xlLmxvZygnVGhlcmUgaXMgbm8gZWxlbWVudCB3aXRoIHN1Y2ggaWQgbmFtZScpO1xuICAgICAgICAgICAgdmFsaWQgPSBmYWxzZTtcbiAgICAgICAgfVxuXG4gICAgICAgIHJldHVybiB2YWxpZDtcbiAgICB9XG5cbiAgICAvKipcbiAgICAgKiBTY3JvbGwgdG8gdGhlIGVsZW1lbnQgd2l0aCBcInRhcmdldFwiIGlkXG4gICAgICogQHBhcmFtIHtTdHJpbmd9IHRhcmdldCAtIGlkIHNlbGVjdG9yIG9mIGVsZW1lbnRcbiAgICAgKiBAcmV0dXJucyB7Ym9vbGVhbn1cbiAgICAgKi9cbiAgICBzY3JvbGxUb1RhcmdldCh0YXJnZXQpIHtcbiAgICAgICAgY29uc3QgJHRhcmdldCA9ICQodGFyZ2V0KTtcblxuICAgICAgICBpZiAoISR0YXJnZXQubGVuZ3RoKSByZXR1cm4gZmFsc2U7XG5cbiAgICAgICAgJCgnaHRtbCwgYm9keScpLmFuaW1hdGUoe1xuICAgICAgICAgICAgc2Nyb2xsVG9wOiAkdGFyZ2V0Lm9mZnNldCgpLnRvcFxuICAgICAgICB9LCA0MDApO1xuICAgIH1cblxuICAgIC8vIHN0YXRpY1xuICAgIHN0YXRpYyBwbHVnaW4oc2VsZWN0b3IpIHtcbiAgICAgICAgY29uc3QgJGVsZW1zID0gJChzZWxlY3Rvcik7XG4gICAgICAgIGlmICghJGVsZW1zLmxlbmd0aCkgcmV0dXJuO1xuXG4gICAgICAgIHJldHVybiAkZWxlbXMuZWFjaChmdW5jdGlvbiAoaW5kZXgsIGVsKSB7XG4gICAgICAgICAgICBsZXQgJGVsZW1lbnQgPSAkKGVsKTtcbiAgICAgICAgICAgIGxldCBkYXRhICAgICA9ICRlbGVtZW50LmRhdGEoJ3dpZGdldC5zY3JvbGx0bycpO1xuXG4gICAgICAgICAgICBpZiAoIWRhdGEpIHtcbiAgICAgICAgICAgICAgICBkYXRhID0gbmV3IFdpZGdldChlbCk7XG4gICAgICAgICAgICAgICAgJGVsZW1lbnQuZGF0YSgnd2lkZ2V0JywgZGF0YSk7XG4gICAgICAgICAgICB9XG4gICAgICAgIH0pXG4gICAgfVxufVxuXG5cblxuLyoqIFdFQlBBQ0sgRk9PVEVSICoqXG4gKiogLi9mcm9udGVuZC9qcy9sYXlvdXQvX3Njcm9sbC10by5qc1xuICoqLyIsIid1c2Ugc3RyaWN0JztcblxuLyoqXG4gKiBOb3RpZmljYXRpb24gZm9yIHVzZXIgYWJvdXQgbmV3IGV2ZW50c1xuICovXG5cbi8qKlxuICogTm90aWZpY2F0aW9uIG9iamVjdFxuICogQHR5cGVkZWYge09iamVjdH0gTm90aWZpY2F0aW9uSXRlbVxuICogQHByb3BlcnR5IHtTdHJpbmd9IGJvZHkgICAgICAtIGh0bWwgb2YgdGhlIG5vdGlmaWNhdGlvblxuICogQHByb3BlcnR5IHtCb29sZW59IHVucmVhZCAgICAtIGlzIGN1cnJlbnQgbm90aWZpY2F0aW9uIGFscmVhZHkgdmlld2VkP1xuICogQHByb3BlcnR5IHtOdW1iZXJ9IGlkICAgICAgICAtIGlkIG9mIHRoZSBub3RpZmljYXRpb25cbiAqIEBwcm9wZXJ0eSB7U3RyaW5nfSB0eXBlICAgICAgLSB0eXBlIG9mIHRoZSBub3RpZmljYXRpb25cbiAqL1xuXG5leHBvcnQgZGVmYXVsdCBjbGFzcyBXaWRnZXQge1xuICAgIC8qKlxuICAgICAqXG4gICAgICogQHBhcmFtIHsoU3RyaW5nfGRvbUVsZW1lbnQpfSBzZWxlY3RvciAgLSBzZWxlY3RvciBvciBkb21FbGVtZW50IGFzIHJvb3QgZWxlbWVudCBvZiB0aGUgd2lkZ2V0XG4gICAgICogQGNvbnN0cnVjdG9yXG4gICAgICovXG4gICAgY29uc3RydWN0b3Ioc2VsZWN0b3IpIHtcbiAgICAgICAgY29uc3Qgc2VsZiA9IHRoaXM7XG5cbiAgICAgICAgc2VsZi4kcm9vdCA9ICQoc2VsZWN0b3IpO1xuICAgICAgICBzZWxmLmxvY2FscyA9IHNlbGYuX2dldERvbSgpO1xuXG4gICAgICAgIHNlbGYuX2luaXQoKTtcbiAgICAgICAgc2VsZi5fYXNzaWduRXZlbnRzKCk7XG4gICAgfVxuXG4gICAgLyoqXG4gICAgICogR2V0ICQgZWxlbWVudHMgb2YgdGhlIHdpZGdldFxuICAgICAqIEByZXR1cm5zIHtPYmplY3R9IC0galF1ZXJ5IGxpbmtzIG90IHRoZSBlbGVtZW50cyBvZiB0aGUgd2lkZ2V0XG4gICAgICogQHByaXZhdGVcbiAgICAgKi9cbiAgICBfZ2V0RG9tKCkge1xuICAgICAgICBjb25zdCAkcm9vdCA9IHRoaXMuJHJvb3Q7XG5cbiAgICAgICAgcmV0dXJuIHtcbiAgICAgICAgICAgICRsaXN0OiAkcm9vdC5maW5kKCdbZGF0YS1ub3RpZmxpc3QtbGlzdF0nKSxcbiAgICAgICAgICAgICRjbG9zZTogJHJvb3QuZmluZCgnW2RhdGEtbm90aWZsaXN0LWNsb3NlXScpLFxuICAgICAgICAgICAgJGxpbms6ICRyb290LmZpbmQoJ1tkYXRhLW5vdGlmbGlzdC1zaG93XScpLFxuICAgICAgICAgICAgJGxvYWQ6ICRyb290LmZpbmQoJ1tkYXRhLW5vdGlmbGlzdC1sb2FkXScpLFxuICAgICAgICAgICAgJGNvdW50OiAkcm9vdC5maW5kKCdbZGF0YS1ub3RpZmxpc3QtY291bnRdJyksXG4gICAgICAgIH07XG4gICAgfVxuXG4gICAgLyoqXG4gICAgICogSW5pdGlhdGUgYmFzZSB2YXJpYWJsZXMgZm9yIHdpZGdldFxuICAgICAqIEBwcml2YXRlXG4gICAgICovXG4gICAgX2luaXQoKSB7XG4gICAgICAgIGNvbnN0IHNlbGYgPSB0aGlzO1xuXG4gICAgICAgIHNlbGYuaXNMb2FkZWQgPSBmYWxzZTtcbiAgICAgICAgc2VsZi5vZmZzZXQgPSAwO1xuICAgICAgICBzZWxmLmlzVmlzaWJsZSA9IGZhbHNlO1xuXG4gICAgICAgIHNlbGYuX3JlY2lldmVVbnJlYWRDb3VudCgpXG4gICAgICAgICAgICAuZG9uZShmdW5jdGlvbiAoY291bnQpIHtcbiAgICAgICAgICAgICAgICBzZWxmLnNldFVucmVhZENvdW50KGNvdW50KTtcbiAgICAgICAgICAgIH0pO1xuICAgIH1cblxuICAgIF9hc3NpZ25FdmVudHMoKSB7XG4gICAgICAgIGNvbnN0IHNlbGYgPSB0aGlzO1xuXG4gICAgICAgIHNlbGYuJHJvb3RcbiAgICAgICAgICAgIC5vbignY2xpY2snLCAnW2RhdGEtbm90aWZsaXN0LXNob3ddLCBbZGF0YS1ub3RpZmxpc3QtY2xvc2VdJywgc2VsZi5fb25DbGlja1RvZ2dsZVNob3cuYmluZChzZWxmKSlcbiAgICAgICAgICAgIC5vbignY2xpY2snLCAnW2RhdGEtbm90aWZsaXN0LWxvYWRdJywgc2VsZi5fb25DbGlja0xvYWROb3RpZmljYXRpb24uYmluZChzZWxmKSlcbiAgICB9XG5cbiAgICAvKipcbiAgICAgKiBFdmVudCBoYW5kbGVyIGZvciBzaG93aW5nIGFuZCBoaWRpbmcgcG9wdXBcbiAgICAgKiBAcGFyYW0ge0V2ZW50fSBlIC0gRXZlbnQgT2JqZWN0XG4gICAgICogQHByaXZhdGVcbiAgICAgKi9cbiAgICBfb25DbGlja1RvZ2dsZVNob3coZSl7XG4gICAgICAgIHRoaXMudG9nZ2xlUG9wdXAoKTtcblxuICAgICAgICBpZiAoIXRoaXMuaXNMb2FkZWQpe1xuICAgICAgICAgICAgdGhpcy5pc0xvYWRlZCAgPSB0cnVlO1xuICAgICAgICAgICAgdGhpcy5sb2FkTm90aWZpY2F0aW9uKHRoaXMub2Zmc2V0KTtcbiAgICAgICAgfVxuXG4gICAgICAgIGUucHJldmVudERlZmF1bHQoKTtcbiAgICB9O1xuXG4gICAgLyoqXG4gICAgICogRXZlbnQgaGFuZGxlciBmb3IgdGhlIGJ1dHRvbiBcImxvYWQgbW9yZSBub3RpZmljYXRpb25cIlxuICAgICAqIEBwYXJhbSB7RXZlbnR9IGUgLSBFdmVudCBPYmplY3RcbiAgICAgKiBAcHJpdmF0ZVxuICAgICAqL1xuICAgIF9vbkNsaWNrTG9hZE5vdGlmaWNhdGlvbihlKXtcbiAgICAgICAgZS5wcmV2ZW50RGVmYXVsdCgpO1xuXG4gICAgICAgIHRoaXMubG9hZE5vdGlmaWNhdGlvbih0aGlzLm9mZnNldCk7XG4gICAgfTtcblxuICAgIC8qKlxuICAgICAqIEFkZCB0byB0aGUgZG9tXG4gICAgICogQHBhcmFtIHtOb3RpZmljYXRpb25JdGVtW119IG5vdGlmTGlzdCAtIGxpc3Qgb2YgdGhlIG5vdGlmaWNhdGlvblxuICAgICAqIEBwcml2YXRlXG4gICAgICovXG4gICAgX3JlbmRlck5vdGlmaWNhdGlvbihub3RpZkxpc3Qpe1xuICAgICAgICBjb25zdCBzZWxmID0gdGhpcztcblxuICAgICAgICBub3RpZkxpc3QuZm9yRWFjaChmdW5jdGlvbihpdGVtKXtcbiAgICAgICAgICAgICQoaXRlbS5ib2R5KVxuICAgICAgICAgICAgICAgIC5hZGRDbGFzcyhpdGVtLnR5cGUpXG4gICAgICAgICAgICAgICAgLnRvZ2dsZUNsYXNzKCdpcy1uZXcnLCBCb29sZWFuKGl0ZW0udW5yZWFkKSlcbiAgICAgICAgICAgICAgICAuZGF0YSgnaWQnLCBpdGVtLmlkKVxuICAgICAgICAgICAgICAgIC5hcHBlbmRUbyhzZWxmLmxvY2Fscy4kbGlzdCk7XG4gICAgICAgIH0pXG4gICAgfVxuXG4gICAgLyoqXG4gICAgICogRmlsdGVyIG5vdGlmaWNhdGlvbiBhbmQgcmV0dXJuIG9ubHkgbmV3XG4gICAgICogQHBhcmFtIHtOb3RpZmljYXRpb25JdGVtW119IG5vdGlmTGlzdCAtIGxpc3Qgb2YgdGhlIG5vdGlmaWNhdGlvblxuICAgICAqIEByZXR1cm5zIHtOb3RpZmljYXRpb25JdGVtW119IG5vdGlmTGlzdCAtIG9ubHkgbmV3IG5vdGlmaWNhdGlvbnNcbiAgICAgKiBAcHJpdmF0ZVxuICAgICAqL1xuICAgIF9maWx0ZXJPbmx5TmV3KG5vdGlmTGlzdCl7XG4gICAgICAgIHJldHVybiBub3RpZkxpc3QuZmlsdGVyKGZ1bmN0aW9uKGl0ZW0pe1xuICAgICAgICAgICAgcmV0dXJuIGl0ZW0udW5yZWFkO1xuICAgICAgICB9KVxuICAgIH07XG5cbiAgICBfaXNIYXZlTm90aWZpY2F0aW9uKG5vdGlmTGlzdCl7XG4gICAgICAgIGlmICghbm90aWZMaXN0Lmxlbmd0aCAmJiAhdGhpcy5vZmZzZXQpIHtcbiAgICAgICAgICAgIHRoaXMuJHJvb3QuYWRkQ2xhc3MoJ2Itbm90aWZsaXN0X2VtcHR5Jyk7XG4gICAgICAgICAgICByZXR1cm4gZmFsc2U7XG4gICAgICAgIH1cblxuICAgICAgICBpZiAobm90aWZMaXN0Lmxlbmd0aCA8IDUpe1xuICAgICAgICAgICAgdGhpcy4kcm9vdC5hZGRDbGFzcygnYi1ub3RpZmxpc3RfbG9hZF9hbGwnKTtcbiAgICAgICAgfVxuICAgICAgICByZXR1cm4gdHJ1ZTtcbiAgICB9XG5cbiAgICAvKipcbiAgICAgKiBHZXQgbmV3IGxvYWQgbm90aWZpY2F0aW9uIGFuZCByZW5kZXIgdGhlbVxuICAgICAqIEBwYXJhbSB7TnVtYmVyfSBvZmZzZXQgLVxuICAgICAqL1xuICAgIGxvYWROb3RpZmljYXRpb24ob2Zmc2V0KXtcbiAgICAgICAgY29uc3Qgc2VsZiA9IHRoaXM7XG5cbiAgICAgICAgc2VsZi5fcmVjaWV2ZU5vdGlmaWNhdGlvbihvZmZzZXQpXG4gICAgICAgICAgICAuZG9uZShmdW5jdGlvbihub3RpZkxpc3Qpe1xuICAgICAgICAgICAgICAgIGlmICghc2VsZi5faXNIYXZlTm90aWZpY2F0aW9uKG5vdGlmTGlzdCkpIHJldHVybjtcblxuICAgICAgICAgICAgICAgIHNlbGYub2Zmc2V0ICs9IG5vdGlmTGlzdC5sZW5ndGg7XG4gICAgICAgICAgICAgICAgc2VsZi5fcmVuZGVyTm90aWZpY2F0aW9uKG5vdGlmTGlzdCk7XG5cbiAgICAgICAgICAgICAgICB2YXIgbmV3Tm90aWZMaXN0ID0gc2VsZi5fZmlsdGVyT25seU5ldyhub3RpZkxpc3QpO1xuXG4gICAgICAgICAgICAgICAgaWYgKCFuZXdOb3RpZkxpc3QubGVuZ3RoKSByZXR1cm47XG5cbiAgICAgICAgICAgICAgICBzZWxmLl9zZW5kVmlld2VkTmV3Tm90aWYobmV3Tm90aWZMaXN0KTtcbiAgICAgICAgICAgICAgICBzZWxmLnNldFVucmVhZENvdW50KHNlbGYudW5yZWFkQ291bnQgLSBuZXdOb3RpZkxpc3QubGVuZ3RoKTtcbiAgICAgICAgICAgIH0pO1xuICAgIH1cblxuICAgIC8qKlxuICAgICAqIFNldCB1bnJlYWQgY291bnRcbiAgICAgKiBAcGFyYW0ge051bWJlcn0gY291bnQgLSBuZXcgdmFsdWUgZm9yIHVucmVhZCBjb3VudFxuICAgICAqL1xuICAgIHNldFVucmVhZENvdW50KGNvdW50KXtcbiAgICAgICAgdGhpcy51bnJlYWRDb3VudCA9IChjb3VudCA+IDApPyBjb3VudDogMDtcblxuICAgICAgICB0aGlzLmxvY2Fscy4kY291bnQudGV4dCh0aGlzLnVucmVhZENvdW50KTtcbiAgICAgICAgdGhpcy4kcm9vdC50b2dnbGVDbGFzcygnYi1ub3RpZmxpc3RfaGF2ZV9ub3RpZmljYXRpb24nLCBCb29sZWFuKHRoaXMudW5yZWFkQ291bnQpKTtcbiAgICB9XG5cbiAgICBzaG93UG9wdXAoKXtcbiAgICAgICAgaWYgKHRoaXMuaXNWaXNpYmxlKSByZXR1cm47XG5cbiAgICAgICAgdGhpcy5pc1Zpc2libGUgPSB0cnVlO1xuICAgICAgICB0aGlzLiRyb290LmFkZENsYXNzKCdiLW5vdGlmbGlzdF9zaG93Jyk7XG4gICAgfVxuXG4gICAgaGlkZVBvcHVwKCl7XG4gICAgICAgIGlmICghdGhpcy5pc1Zpc2libGUpIHJldHVybjtcblxuICAgICAgICB0aGlzLmlzVmlzaWJsZSA9IGZhbHNlO1xuICAgICAgICB0aGlzLiRyb290LnJlbW92ZUNsYXNzKCdiLW5vdGlmbGlzdF9zaG93Jyk7XG4gICAgfVxuXG4gICAgdG9nZ2xlUG9wdXAoKXtcbiAgICAgICAgdGhpcy5pc1Zpc2libGU/IHRoaXMuaGlkZVBvcHVwKCk6IHRoaXMuc2hvd1BvcHVwKCk7XG4gICAgfVxuXG4gICAgLy8gdHJhbnNwb3J0XG4gICAgX3JlY2lldmVVbnJlYWRDb3VudCgpe1xuICAgICAgICBsZXQgZGVmZXIgPSAkLkRlZmVycmVkKCk7XG4gICAgICAgIGNvbnN0IHVybCA9IGpzUm91dGVzLmNvbnRyb2xsZXJzLmNvcmUuTm90aWZpY2F0aW9ucy51bnJlYWQoKS51cmw7XG5cbiAgICAgICAgJC5nZXQodXJsLCBmdW5jdGlvbiAoZGF0YSkge1xuICAgICAgICAgICAgdmFyIGNvdW50ID0gKCQucGFyc2VKU09OKGRhdGEpKS51bnJlYWQ7XG4gICAgICAgICAgICBkZWZlci5yZXNvbHZlKGNvdW50KTtcbiAgICAgICAgfSk7XG5cbiAgICAgICAgcmV0dXJuIGRlZmVyLnByb21pc2UoKTtcbiAgICB9XG5cbiAgICBfcmVjaWV2ZU5vdGlmaWNhdGlvbihvZmZzZXQpe1xuICAgICAgICBsZXQgZGVmZXIgPSAkLkRlZmVycmVkKCk7XG4gICAgICAgIGNvbnN0IGxpbWl0ID0gNTtcbiAgICAgICAgY29uc3QgdXJsID0ganNSb3V0ZXMuY29udHJvbGxlcnMuY29yZS5Ob3RpZmljYXRpb25zLmxpc3Qob2Zmc2V0LCBsaW1pdCkudXJsO1xuXG4gICAgICAgICQuZ2V0KHVybCwgZnVuY3Rpb24oZGF0YSl7XG4gICAgICAgICAgICB2YXIgbm90aWZMaXN0ID0gJC5wYXJzZUpTT04oZGF0YSlbMF07XG5cbiAgICAgICAgICAgIGRlZmVyLnJlc29sdmUobm90aWZMaXN0KTtcbiAgICAgICAgfSk7XG5cbiAgICAgICAgcmV0dXJuIGRlZmVyLnByb21pc2UoKTtcbiAgICB9XG5cbiAgICAvKipcbiAgICAgKiBTZW5kIHRvIHRoZSBzZXJ2ZXIgaWQgb2YgdGhlIHZpZXdlZCBub3RpZmljYXRpb25cbiAgICAgKiBAcGFyYW0ge05vdGlmaWNhdGlvbkl0ZW1bXX0gbm90aWZMaXN0XG4gICAgICogQHByaXZhdGVcbiAgICAgKi9cbiAgICBfc2VuZFZpZXdlZE5ld05vdGlmKG5vdGlmTGlzdCl7XG4gICAgICAgIGxldCBpZHMgPSBbXTtcbiAgICAgICAgY29uc3QgdXJsID0ganNSb3V0ZXMuY29udHJvbGxlcnMuY29yZS5Ob3RpZmljYXRpb25zLnJlYWQoKS51cmw7XG5cbiAgICAgICAgbm90aWZMaXN0LmZvckVhY2goZnVuY3Rpb24oaXRlbSl7XG4gICAgICAgICAgICBpZHMucHVzaChpdGVtLmlkKTtcbiAgICAgICAgfSk7XG5cbiAgICAgICAgJC5wb3N0KHVybCwge2lkczogaWRzfSlcbiAgICB9O1xuXG4gIFxuICAgIC8vIHN0YXRpY1xuICAgIHN0YXRpYyBwbHVnaW4oc2VsZWN0b3IpIHtcbiAgICAgICAgY29uc3QgJGVsZW1zID0gJChzZWxlY3Rvcik7XG4gICAgICAgIGlmICghJGVsZW1zLmxlbmd0aCkgcmV0dXJuO1xuXG4gICAgICAgIHJldHVybiAkZWxlbXMuZWFjaChmdW5jdGlvbiAoaW5kZXgsIGVsKSB7XG4gICAgICAgICAgICBsZXQgJGVsZW1lbnQgPSAkKGVsKTtcbiAgICAgICAgICAgIGxldCBkYXRhICAgICA9ICRlbGVtZW50LmRhdGEoJ3dpZGdldC5zY3JvbGx0bycpO1xuXG4gICAgICAgICAgICBpZiAoIWRhdGEpIHtcbiAgICAgICAgICAgICAgICBkYXRhID0gbmV3IFdpZGdldChlbCk7XG4gICAgICAgICAgICAgICAgJGVsZW1lbnQuZGF0YSgnd2lkZ2V0JywgZGF0YSk7XG4gICAgICAgICAgICB9XG4gICAgICAgIH0pXG4gICAgfVxufVxuXG5cblxuLyoqIFdFQlBBQ0sgRk9PVEVSICoqXG4gKiogLi9mcm9udGVuZC9qcy9sYXlvdXQvX25vdGlmYXRpb24tbGlzdC5qc1xuICoqLyIsIid1c2Ugc3RyaWN0JztcblxuZXhwb3J0IGRlZmF1bHQgY2xhc3MgV2lkZ2V0IHtcblxuICAgIGNvbnN0cnVjdG9yKHNlbGVjdG9yLCBvcHRpb25zKSB7XG4gICAgICAgIGxldCBkZWZhdWx0T3B0aW9ucyA9IHtcbiAgICAgICAgICAgIHVybDoganNSb3V0ZXMuY29udHJvbGxlcnMuVXRpbGl0aWVzLm1hcmtkb3duKCkudXJsLFxuICAgICAgICAgICAgaW50ZXJ2YWw6IDEwMDAsXG4gICAgICAgICAgICBtYXJrZG93bnBvc2l0aW9uOiBcImJvZHlcIixcbiAgICAgICAgICAgIHRlbXBsYXRlOiBcIjxkaXYgY2xhc3M9J3BvcG92ZXItYmwnPjxpIGNsYXNzPSdmYSBmYS1zcGlubmVyIGZhLXNwaW4nPjwvaT48ZGl2IGNsYXNzPSdwb3BvdmVyLWJsX19jb250ZW50JyBkYXRhLWNvbnRlbnQ+PC9kaXY+PC9kaXY+XCJcbiAgICAgICAgfTtcblxuICAgICAgICB0aGlzLiRyb290ID0gJChzZWxlY3Rvcik7XG4gICAgICAgIHRoaXMub3B0aW9ucyA9ICQuZXh0ZW5kKHt9LCBkZWZhdWx0T3B0aW9ucywgb3B0aW9ucyk7XG5cbiAgICAgICAgdGhpcy5yZXNldFN0YXRlKCk7XG4gICAgICAgIHRoaXMuYXNzaWduRXZlbnRzKCk7XG4gICAgfVxuXG4gICAgcmVzZXRTdGF0ZSgpe1xuICAgICAgICB0aGlzLnNlbmRpbmcgPSBudWxsO1xuICAgICAgICB0aGlzLnNlbmRpbmcgPSBudWxsO1xuICAgICAgICB0aGlzLmlzTmVlZFVwZGF0aW5nID0gbnVsbDtcbiAgICAgICAgdGhpcy53YWl0aW5nVGltZXIgPSBudWxsXG4gICAgfVxuXG4gICAgY3JlYXRlUG9wb3Zlcigpe1xuICAgICAgICB0aGlzLiRwb3BvdmVyID0gJCh0aGlzLm9wdGlvbnMudGVtcGxhdGUpO1xuXG4gICAgICAgIGlmICh0aGlzLm9wdGlvbnMubWFya2Rvd25jbGFzcyl7XG4gICAgICAgICAgICB0aGlzLiRwb3BvdmVyLmFkZENsYXNzKHRoaXMub3B0aW9ucy5tYXJrZG93bmNsYXNzKTtcbiAgICAgICAgfVxuXG4gICAgICAgIGlmICh0aGlzLm9wdGlvbnMubWFya2Rvd25wb3NpdGlvbiA9PSBcImJvZHlcIil7XG4gICAgICAgICAgICB0aGlzLiRwb3BvdmVyLmFwcGVuZFRvKCdib2R5Jyk7XG4gICAgICAgIH0gZWxzZSB7XG4gICAgICAgICAgICB0aGlzLiRwb3BvdmVyLmluc2VydEFmdGVyKHRoaXMuJHJvb3QpO1xuICAgICAgICB9XG4gICAgfVxuXG4gICAgZ2V0UG9zaXRpb24oKXtcbiAgICAgICAgY29uc3QgJHJvb3QgPSB0aGlzLiRyb290O1xuICAgICAgICBsZXQgb2Zmc2V0Qm9keSA9IHtcbiAgICAgICAgICAgICAgICB0b3A6ICRyb290Lm9mZnNldCgpLnRvcCxcbiAgICAgICAgICAgICAgICBsZWZ0OiAkcm9vdC5vZmZzZXQoKS5sZWZ0ICsgJHJvb3Qub3V0ZXJXaWR0aCgpXG4gICAgICAgICAgICB9O1xuXG4gICAgICAgIHJldHVybiB0aGlzLm9wdGlvbnMubWFya2Rvd25wb3NpdGlvbiA9PSBcImJvZHlcIj8gb2Zmc2V0Qm9keToge307XG4gICAgfVxuXG4gICAgYXNzaWduRXZlbnRzICgpe1xuICAgICAgICBjb25zdCBzZWxmID0gdGhpcztcblxuICAgICAgICBzZWxmLiRyb290XG4gICAgICAgICAgICAub24oJ2ZvY3VzJywgZnVuY3Rpb24oKXtcbiAgICAgICAgICAgICAgICBzZWxmLnRvZ2dsZSgpO1xuICAgICAgICAgICAgICAgIHNlbGYuY29tcGlsZUNvbnRlbnQoKTtcbiAgICAgICAgICAgIH0pXG4gICAgICAgICAgICAub24oJ2tleXVwJywgZnVuY3Rpb24oZSl7XG4gICAgICAgICAgICAgICAgaWYgKCFzZWxmLmlzS2V5VHJpZ2dlcihlLndoaWNoKSkgcmV0dXJuIHRydWU7XG5cbiAgICAgICAgICAgICAgICBzZWxmLnBhdXNpbmcgJiYgKHNlbGYuaXNOZWVkVXBkYXRpbmcgPSB0cnVlKTtcbiAgICAgICAgICAgICAgICBzZWxmLmNvbXBpbGVDb250ZW50KCk7XG4gICAgICAgICAgICB9KVxuICAgICAgICAgICAgLm9uKCdibHVyJywgZnVuY3Rpb24oKXtcbiAgICAgICAgICAgICAgICBzZWxmLnJlc2V0U3RhdGUoKTtcbiAgICAgICAgICAgICAgICBzZWxmLnRvZ2dsZSgpO1xuICAgICAgICAgICAgfSk7XG5cbiAgICAgICAgJCh3aW5kb3cpLnJlc2l6ZShmdW5jdGlvbiAoKSB7XG4gICAgICAgICAgICBzZWxmLiRwb3BvdmVyICYmIHNlbGYuJHBvcG92ZXIuY3NzKHNlbGYuZ2V0UG9zaXRpb24oKSk7XG4gICAgICAgIH0pO1xuICAgIH1cblxuICAgIGNvbXBpbGVDb250ZW50KGNvbnRlbnQpe1xuICAgICAgICB2YXIgc2VsZiA9IHRoaXM7XG4gICAgICAgIGNvbnRlbnQgPSBjb250ZW50IHx8IHNlbGYuJHJvb3QudmFsKCk7XG5cbiAgICAgICAgaWYgKHNlbGYuc2VuZGluZyB8fCBzZWxmLnBhdXNpbmcpIHJldHVybjtcblxuICAgICAgICBzZWxmLnBhdXNpbmcgPSB0cnVlO1xuICAgICAgICBzZWxmLnNldFNlbmRpbmcoKTtcbiAgICAgICAgc2VsZi5zZXRXYWl0aW5nKCk7XG5cbiAgICAgICAgJC5wb3N0KHNlbGYub3B0aW9ucy51cmwsIHtcbiAgICAgICAgICAgIGRhdGE6IGNvbnRlbnRcbiAgICAgICAgfSwgZnVuY3Rpb24oZGF0YSl7XG4gICAgICAgICAgICBzZWxmLnNldFNlbmRlZCgpO1xuICAgICAgICAgICAgc2VsZi4kcG9wb3Zlci5maW5kKCdbZGF0YS1jb250ZW50XScpLmh0bWwoZGF0YSk7XG4gICAgICAgIH0pO1xuICAgIH1cblxuICAgIHNldFNlbmRpbmcoKXtcbiAgICAgICAgdGhpcy5zZW5kaW5nID0gdHJ1ZTtcbiAgICAgICAgdGhpcy4kcG9wb3Zlci5hZGRDbGFzcygncG9wb3Zlci1ibF9sb2FkaW5nJyk7XG4gICAgfVxuXG4gICAgc2V0U2VuZGVkKCl7XG4gICAgICAgIHRoaXMuc2VuZGluZyA9IGZhbHNlO1xuICAgICAgICB0aGlzLiRwb3BvdmVyLnJlbW92ZUNsYXNzKCdwb3BvdmVyLWJsX2xvYWRpbmcnKTtcbiAgICB9XG5cbiAgICBzZXRXYWl0aW5nKCl7XG4gICAgICAgIHZhciBzZWxmID0gdGhpcztcblxuICAgICAgICBzZWxmLndhaXRpbmdUaW1lciA9IHNldFRpbWVvdXQoXG4gICAgICAgICAgICBmdW5jdGlvbiAoKSB7XG4gICAgICAgICAgICAgICAgc2VsZi5wYXVzaW5nID0gZmFsc2U7XG4gICAgICAgICAgICAgICAgaWYgKHNlbGYuaXNOZWVkVXBkYXRpbmcpIHtcbiAgICAgICAgICAgICAgICAgICAgc2VsZi5pc05lZWRVcGRhdGluZyA9IGZhbHNlO1xuICAgICAgICAgICAgICAgICAgICBzZWxmLmNvbXBpbGVDb250ZW50KCk7XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgfSwgc2VsZi5vcHRpb25zLmludGVydmFsKTtcbiAgICB9XG5cbiAgICBzaG93KCl7XG4gICAgICAgIGlmICggIXRoaXMuJHBvcG92ZXIpIHRoaXMuY3JlYXRlUG9wb3ZlcigpO1xuICAgICAgICB0aGlzLiRwb3BvdmVyLmNzcyh0aGlzLmdldFBvc2l0aW9uKCkpO1xuXG4gICAgICAgIGlmICh0aGlzLmlzVmlzaWJsZSkgcmV0dXJuO1xuXG4gICAgICAgIHRoaXMuaXNWaXNpYmxlID0gdHJ1ZTtcbiAgICAgICAgdGhpcy4kcG9wb3Zlci5hZGRDbGFzcygncG9wb3Zlci1ibF9zaG93Jyk7XG4gICAgfVxuXG4gICAgaGlkZSgpe1xuICAgICAgICBpZiAoIXRoaXMuaXNWaXNpYmxlKSByZXR1cm47XG5cbiAgICAgICAgdGhpcy5pc1Zpc2libGUgPSBmYWxzZTtcbiAgICAgICAgdGhpcy4kcG9wb3Zlci5yZW1vdmVDbGFzcygncG9wb3Zlci1ibF9zaG93Jyk7XG4gICAgfVxuXG4gICAgdG9nZ2xlKCl7XG4gICAgICAgIHRoaXNbdGhpcy5pc1Zpc2libGU/ICdoaWRlJzogJ3Nob3cnXSgpO1xuICAgIH1cblxuICAgIGlzS2V5VHJpZ2dlcihjb2RlKXtcbiAgICAgICAgcmV0dXJuIChjb2RlID49IDQ1ICYmIGNvZGUgPD0gOTApIHx8IChjb2RlID49IDE4NikgJiYgKGNvZGUgPD0gMjIyKSB8fCAoY29kZSA9PSAxMykgfHwgKGNvZGUgPT0gMjcpIHx8IChjb2RlID09IDMyKSB8fCAoY29kZSA9PSA4KTtcbiAgICB9XG5cblxuXG4gICAgLy8gc3RhdGljXG4gICAgc3RhdGljIHBsdWdpbihzZWxlY3Rvciwgb3B0aW9ucykge1xuICAgICAgICBjb25zdCAkZWxlbXMgPSAkKHNlbGVjdG9yKTtcbiAgICAgICAgaWYgKCEkZWxlbXMubGVuZ3RoKSByZXR1cm47XG5cbiAgICAgICAgcmV0dXJuICRlbGVtcy5lYWNoKGZ1bmN0aW9uIChpbmRleCwgZWwpIHtcbiAgICAgICAgICAgIGxldCAkZWxlbWVudCA9ICQoZWwpO1xuICAgICAgICAgICAgbGV0IGRhdGEgICAgID0gJGVsZW1lbnQuZGF0YSgnd2lkZ2V0LnByZXZpZXcubWFya2Rvd24nKTtcblxuICAgICAgICAgICAgaWYgKCFkYXRhKSB7XG4gICAgICAgICAgICAgICAgZGF0YSA9IG5ldyBXaWRnZXQoZWwsIG9wdGlvbnMpO1xuICAgICAgICAgICAgICAgICRlbGVtZW50LmRhdGEoJ3dpZGdldCcsIGRhdGEpO1xuICAgICAgICAgICAgfVxuICAgICAgICB9KVxuICAgIH1cbn1cblxuXG5cblxuLyoqIFdFQlBBQ0sgRk9PVEVSICoqXG4gKiogLi9mcm9udGVuZC9qcy9sYXlvdXQvX3ByZXZpZXctbWFya2Rvd24uanNcbiAqKi8iXSwic291cmNlUm9vdCI6IiJ9