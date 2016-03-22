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
	        value: function _assignEvents() {}
	
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
	
	/**
	 *  Header notification
	 */
	
	
	exports.default = Widget;
	(function ($, App) {
	    'use strict';
	
	    function TopNotification(selector, options) {
	        var self = this;
	
	        self.$root = $(selector);
	        self.options = $.extend({}, options, self.$root.data());
	        self.options.uniqueKey = 'topNotifiction';
	
	        if (!self.isShowed()) {
	            self.show();
	        };
	        self.assignEvents();
	    }
	
	    TopNotification.prototype.assignEvents = function () {
	        var self = this;
	
	        self.$root.on('click', '[data-notification-close]', function (e) {
	            self.setIsShowed({
	                status: 'close'
	            });
	            self.hide();
	            e.preventDefault();
	        }).on('click', '[data-notification-accept]', function () {
	            self.setIsShowed({
	                status: 'accept'
	            });
	        });
	    };
	
	    TopNotification.prototype.isShowed = function () {
	        var self = this,
	            value = localStorage.getItem(self.options.uniqueKey);
	
	        return value && value == 'showed';
	    };
	
	    TopNotification.prototype.setIsShowed = function (data) {
	        var self = this;
	
	        localStorage.setItem(self.options.uniqueKey, 'showed');
	        $.post('/', {
	            status: data.status
	        });
	    };
	
	    TopNotification.prototype.hide = function () {
	        this.$root.removeClass('state_show');
	    };
	
	    TopNotification.prototype.show = function () {
	        this.$root.addClass('state_show');
	    };
	
	    App.widgets.TopNotification = TopNotification;
	})(jQuery, App);

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
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIndlYnBhY2s6Ly8vd2VicGFjay9ib290c3RyYXAgYzVmM2FkYmFhODRlZmRmZTFlYTI/MmExNSIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvaGVscGVycy9jbGFzc0NhbGxDaGVjay5qcz8yMWFmIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9oZWxwZXJzL2NyZWF0ZUNsYXNzLmpzPzFkZmUiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL2NvcmUtanMvb2JqZWN0L2RlZmluZS1wcm9wZXJ0eS5qcz81ZjcwIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9mbi9vYmplY3QvZGVmaW5lLXByb3BlcnR5LmpzPzRkMzMiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvZXM2Lm9iamVjdC5kZWZpbmUtcHJvcGVydHkuanM/OGJkZSIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZXhwb3J0LmpzPzhlNDAiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2dsb2JhbC5qcz8zYzUyIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19jb3JlLmpzP2Q2MTEiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2N0eC5qcz80ZTU5Iiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19hLWZ1bmN0aW9uLmpzPzA2OTkiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2hpZGUuanM/MGQyZSIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fb2JqZWN0LWRwLmpzPzU5ODYiLCJ3ZWJwYWNrOi8vLy4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2FuLW9iamVjdC5qcz8zYWYyIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19pcy1vYmplY3QuanM/Y2ZkYSIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9faWU4LWRvbS1kZWZpbmUuanM/YjEwMiIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZGVzY3JpcHRvcnMuanM/YzBmNSIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZmFpbHMuanM/YzZkZCIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fZG9tLWNyZWF0ZS5qcz82MTJmIiwid2VicGFjazovLy8uL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL190by1wcmltaXRpdmUuanM/MWE2NSIsIndlYnBhY2s6Ly8vLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fcHJvcGVydHktZGVzYy5qcz8yNTZiIiwid2VicGFjazovLy8uL2Zyb250ZW5kL2pzL2NvbW1vbi1wYWNrLmpzIiwid2VicGFjazovLy8uL2Zyb250ZW5kL2pzL2xheW91dC9fbm90aWZpY2F0aW9uLWNvbW1lcmNpYWwuanMiLCJ3ZWJwYWNrOi8vLy4vZnJvbnRlbmQvanMvbGF5b3V0L19zY3JvbGwtdG8uanMiLCJ3ZWJwYWNrOi8vLy4vZnJvbnRlbmQvanMvbGF5b3V0L19ub3RpZmF0aW9uLWxpc3QuanMiLCJ3ZWJwYWNrOi8vLy4vZnJv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LFNBQUwsR0FBZ0IsTUFBaEIsR0FBd0IsTUFBeEIsQ0FBTCxHQURJOzs7O3NDQUlLLE1BQUs7QUFDZCxvQkFBTyxJQUFDLElBQVEsRUFBUixJQUFjLFFBQVEsRUFBUixJQUFlLElBQUMsSUFBUSxHQUFSLElBQWlCLFFBQVEsR0FBUixJQUFpQixRQUFRLEVBQVIsSUFBZ0IsUUFBUSxFQUFSLElBQWdCLFFBQVEsRUFBUixJQUFnQixRQUFRLENBQVIsQ0FEMUc7Ozs7Ozs7Z0NBT0osVUFBVSxTQUFTO0FBQzdCLGlCQUFNLFNBQVMsRUFBRSxRQUFGLENBQVQsQ0FEdUI7QUFFN0IsaUJBQUksQ0FBQyxPQUFPLE1BQVAsRUFBZSxPQUFwQjs7QUFFQSxvQkFBTyxPQUFPLElBQVAsQ0FBWSxVQUFVLEtBQVYsRUFBaUIsRUFBakIsRUFBcUI7QUFDcEMscUJBQUksV0FBVyxFQUFFLEVBQUYsQ0FBWCxDQURnQztBQUVwQyxxQkFBSSxPQUFXLFNBQVMsSUFBVCxDQUFjLHlCQUFkLENBQVgsQ0FGZ0M7O0FBSXBDLHFCQUFJLENBQUMsSUFBRCxFQUFPO0FBQ1AsNEJBQU8sSUFBSSxNQUFKLENBQVcsRUFBWCxFQUFlLE9BQWYsQ0FBUCxDQURPO0FBRVAsOEJBQVMsSUFBVCxDQUFjLFFBQWQsRUFBd0IsSUFBeEIsRUFGTztrQkFBWDtjQUplLENBQW5CLENBSjZCOzs7WUE3SWhCIiwiZmlsZSI6ImNvbW1vbi1wYWNrLmpzIiwic291cmNlc0NvbnRlbnQiOlsiIFx0Ly8gVGhlIG1vZHVsZSBjYWNoZVxuIFx0dmFyIGluc3RhbGxlZE1vZHVsZXMgPSB7fTtcblxuIFx0Ly8gVGhlIHJlcXVpcmUgZnVuY3Rpb25cbiBcdGZ1bmN0aW9uIF9fd2VicGFja19yZXF1aXJlX18obW9kdWxlSWQpIHtcblxuIFx0XHQvLyBDaGVjayBpZiBtb2R1bGUgaXMgaW4gY2FjaGVcbiBcdFx0aWYoaW5zdGFsbGVkTW9kdWxlc1ttb2R1bGVJZF0pXG4gXHRcdFx0cmV0dXJuIGluc3RhbGxlZE1vZHVsZXNbbW9kdWxlSWRdLmV4cG9ydHM7XG5cbiBcdFx0Ly8gQ3JlYXRlIGEgbmV3IG1vZHVsZSAoYW5kIHB1dCBpdCBpbnRvIHRoZSBjYWNoZSlcbiBcdFx0dmFyIG1vZHVsZSA9IGluc3RhbGxlZE1vZHVsZXNbbW9kdWxlSWRdID0ge1xuIFx0XHRcdGV4cG9ydHM6IHt9LFxuIFx0XHRcdGlkOiBtb2R1bGVJZCxcbiBcdFx0XHRsb2FkZWQ6IGZhbHNlXG4gXHRcdH07XG5cbiBcdFx0Ly8gRXhlY3V0ZSB0aGUgbW9kdWxlIGZ1bmN0aW9uXG4gXHRcdG1vZHVsZXNbbW9kdWxlSWRdLmNhbGwobW9kdWxlLmV4cG9ydHMsIG1vZHVsZSwgbW9kdWxlLmV4cG9ydHMsIF9fd2VicGFja19yZXF1aXJlX18pO1xuXG4gXHRcdC8vIEZsYWcgdGhlIG1vZHVsZSBhcyBsb2FkZWRcbiBcdFx0bW9kdWxlLmxvYWRlZCA9IHRydWU7XG5cbiBcdFx0Ly8gUmV0dXJuIHRoZSBleHBvcnRzIG9mIHRoZSBtb2R1bGVcbiBcdFx0cmV0dXJuIG1vZHVsZS5leHBvcnRzO1xuIFx0fVxuXG5cbiBcdC8vIGV4cG9zZSB0aGUgbW9kdWxlcyBvYmplY3QgKF9fd2VicGFja19tb2R1bGVzX18pXG4gXHRfX3dlYnBhY2tfcmVxdWlyZV9fLm0gPSBtb2R1bGVzO1xuXG4gXHQvLyBleHBvc2UgdGhlIG1vZHVsZSBjYWNoZVxuIFx0X193ZWJwYWNrX3JlcXVpcmVfXy5jID0gaW5zdGFsbGVkTW9kdWxlcztcblxuIFx0Ly8gX193ZWJwYWNrX3B1YmxpY19wYXRoX19cbiBcdF9fd2VicGFja19yZXF1aXJlX18ucCA9IFwiXCI7XG5cbiBcdC8vIExvYWQgZW50cnkgbW9kdWxlIGFuZCByZXR1cm4gZXhwb3J0c1xuIFx0cmV0dXJuIF9fd2VicGFja19yZXF1aXJlX18oMCk7XG5cblxuXG4vKiogV0VCUEFDSyBGT09URVIgKipcbiAqKiB3ZWJwYWNrL2Jvb3RzdHJhcCBjNWYzYWRiYWE4NGVmZGZlMWVhMlxuICoqLyIsIlwidXNlIHN0cmljdFwiO1xuXG5leHBvcnRzLl9fZXNNb2R1bGUgPSB0cnVlO1xuXG5leHBvcnRzLmRlZmF1bHQgPSBmdW5jdGlvbiAoaW5zdGFuY2UsIENvbnN0cnVjdG9yKSB7XG4gIGlmICghKGluc3RhbmNlIGluc3RhbmNlb2YgQ29uc3RydWN0b3IpKSB7XG4gICAgdGhyb3cgbmV3IFR5cGVFcnJvcihcIkNhbm5vdCBjYWxsIGEgY2xhc3MgYXMgYSBmdW5jdGlvblwiKTtcbiAgfVxufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL2hlbHBlcnMvY2xhc3NDYWxsQ2hlY2suanNcbiAqKiBtb2R1bGUgaWQgPSAzXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1XG4gKiovIiwiXCJ1c2Ugc3RyaWN0XCI7XG5cbmV4cG9ydHMuX19lc01vZHVsZSA9IHRydWU7XG5cbnZhciBfZGVmaW5lUHJvcGVydHkgPSByZXF1aXJlKFwiYmFiZWwtcnVudGltZS9jb3JlLWpzL29iamVjdC9kZWZpbmUtcHJvcGVydHlcIik7XG5cbnZhciBfZGVmaW5lUHJvcGVydHkyID0gX2ludGVyb3BSZXF1aXJlRGVmYXVsdChfZGVmaW5lUHJvcGVydHkpO1xuXG5mdW5jdGlvbiBfaW50ZXJvcFJlcXVpcmVEZWZhdWx0KG9iaikgeyByZXR1cm4gb2JqICYmIG9iai5fX2VzTW9kdWxlID8gb2JqIDogeyBkZWZhdWx0OiBvYmogfTsgfVxuXG5leHBvcnRzLmRlZmF1bHQgPSBmdW5jdGlvbiAoKSB7XG4gIGZ1bmN0aW9uIGRlZmluZVByb3BlcnRpZXModGFyZ2V0LCBwcm9wcykge1xuICAgIGZvciAodmFyIGkgPSAwOyBpIDwgcHJvcHMubGVuZ3RoOyBpKyspIHtcbiAgICAgIHZhciBkZXNjcmlwdG9yID0gcHJvcHNbaV07XG4gICAgICBkZXNjcmlwdG9yLmVudW1lcmFibGUgPSBkZXNjcmlwdG9yLmVudW1lcmFibGUgfHwgZmFsc2U7XG4gICAgICBkZXNjcmlwdG9yLmNvbmZpZ3VyYWJsZSA9IHRydWU7XG4gICAgICBpZiAoXCJ2YWx1ZVwiIGluIGRlc2NyaXB0b3IpIGRlc2NyaXB0b3Iud3JpdGFibGUgPSB0cnVlO1xuICAgICAgKDAsIF9kZWZpbmVQcm9wZXJ0eTIuZGVmYXVsdCkodGFyZ2V0LCBkZXNjcmlwdG9yLmtleSwgZGVzY3JpcHRvcik7XG4gICAgfVxuICB9XG5cbiAgcmV0dXJuIGZ1bmN0aW9uIChDb25zdHJ1Y3RvciwgcHJvdG9Qcm9wcywgc3RhdGljUHJvcHMpIHtcbiAgICBpZiAocHJvdG9Qcm9wcykgZGVmaW5lUHJvcGVydGllcyhDb25zdHJ1Y3Rvci5wcm90b3R5cGUsIHByb3RvUHJvcHMpO1xuICAgIGlmIChzdGF0aWNQcm9wcykgZGVmaW5lUHJvcGVydGllcyhDb25zdHJ1Y3Rvciwgc3RhdGljUHJvcHMpO1xuICAgIHJldHVybiBDb25zdHJ1Y3RvcjtcbiAgfTtcbn0oKTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL2hlbHBlcnMvY3JlYXRlQ2xhc3MuanNcbiAqKiBtb2R1bGUgaWQgPSA0XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1XG4gKiovIiwibW9kdWxlLmV4cG9ydHMgPSB7IFwiZGVmYXVsdFwiOiByZXF1aXJlKFwiY29yZS1qcy9saWJyYXJ5L2ZuL29iamVjdC9kZWZpbmUtcHJvcGVydHlcIiksIF9fZXNNb2R1bGU6IHRydWUgfTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL2NvcmUtanMvb2JqZWN0L2RlZmluZS1wcm9wZXJ0eS5qc1xuICoqIG1vZHVsZSBpZCA9IDVcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDVcbiAqKi8iLCJyZXF1aXJlKCcuLi8uLi9tb2R1bGVzL2VzNi5vYmplY3QuZGVmaW5lLXByb3BlcnR5Jyk7XG52YXIgJE9iamVjdCA9IHJlcXVpcmUoJy4uLy4uL21vZHVsZXMvX2NvcmUnKS5PYmplY3Q7XG5tb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uIGRlZmluZVByb3BlcnR5KGl0LCBrZXksIGRlc2Mpe1xuICByZXR1cm4gJE9iamVjdC5kZWZpbmVQcm9wZXJ0eShpdCwga2V5LCBkZXNjKTtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9mbi9vYmplY3QvZGVmaW5lLXByb3BlcnR5LmpzXG4gKiogbW9kdWxlIGlkID0gNlxuICoqIG1vZHVsZSBjaHVua3MgPSAwIDEgMiAzIDQgNVxuICoqLyIsInZhciAkZXhwb3J0ID0gcmVxdWlyZSgnLi9fZXhwb3J0Jyk7XHJcbi8vIDE5LjEuMi40IC8gMTUuMi4zLjYgT2JqZWN0LmRlZmluZVByb3BlcnR5KE8sIFAsIEF0dHJpYnV0ZXMpXHJcbiRleHBvcnQoJGV4cG9ydC5TICsgJGV4cG9ydC5GICogIXJlcXVpcmUoJy4vX2Rlc2NyaXB0b3JzJyksICdPYmplY3QnLCB7ZGVmaW5lUHJvcGVydHk6IHJlcXVpcmUoJy4vX29iamVjdC1kcCcpLmZ9KTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvZXM2Lm9iamVjdC5kZWZpbmUtcHJvcGVydHkuanNcbiAqKiBtb2R1bGUgaWQgPSA3XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1XG4gKiovIiwidmFyIGdsb2JhbCAgICA9IHJlcXVpcmUoJy4vX2dsb2JhbCcpXG4gICwgY29yZSAgICAgID0gcmVxdWlyZSgnLi9fY29yZScpXG4gICwgY3R4ICAgICAgID0gcmVxdWlyZSgnLi9fY3R4JylcbiAgLCBoaWRlICAgICAgPSByZXF1aXJlKCcuL19oaWRlJylcbiAgLCBQUk9UT1RZUEUgPSAncHJvdG90eXBlJztcblxudmFyICRleHBvcnQgPSBmdW5jdGlvbih0eXBlLCBuYW1lLCBzb3VyY2Upe1xuICB2YXIgSVNfRk9SQ0VEID0gdHlwZSAmICRleHBvcnQuRlxuICAgICwgSVNfR0xPQkFMID0gdHlwZSAmICRleHBvcnQuR1xuICAgICwgSVNfU1RBVElDID0gdHlwZSAmICRleHBvcnQuU1xuICAgICwgSVNfUFJPVE8gID0gdHlwZSAmICRleHBvcnQuUFxuICAgICwgSVNfQklORCAgID0gdHlwZSAmICRleHBvcnQuQlxuICAgICwgSVNfV1JBUCAgID0gdHlwZSAmICRleHBvcnQuV1xuICAgICwgZXhwb3J0cyAgID0gSVNfR0xPQkFMID8gY29yZSA6IGNvcmVbbmFtZV0gfHwgKGNvcmVbbmFtZV0gPSB7fSlcbiAgICAsIGV4cFByb3RvICA9IGV4cG9ydHNbUFJPVE9UWVBFXVxuICAgICwgdGFyZ2V0ICAgID0gSVNfR0xPQkFMID8gZ2xvYmFsIDogSVNfU1RBVElDID8gZ2xvYmFsW25hbWVdIDogKGdsb2JhbFtuYW1lXSB8fCB7fSlbUFJPVE9UWVBFXVxuICAgICwga2V5LCBvd24sIG91dDtcbiAgaWYoSVNfR0xPQkFMKXNvdXJjZSA9IG5hbWU7XG4gIGZvcihrZXkgaW4gc291cmNlKXtcbiAgICAvLyBjb250YWlucyBpbiBuYXRpdmVcbiAgICBvd24gPSAhSVNfRk9SQ0VEICYmIHRhcmdldCAmJiB0YXJnZXRba2V5XSAhPT0gdW5kZWZpbmVkO1xuICAgIGlmKG93biAmJiBrZXkgaW4gZXhwb3J0cyljb250aW51ZTtcbiAgICAvLyBleHBvcnQgbmF0aXZlIG9yIHBhc3NlZFxuICAgIG91dCA9IG93biA/IHRhcmdldFtrZXldIDogc291cmNlW2tleV07XG4gICAgLy8gcHJldmVudCBnbG9iYWwgcG9sbHV0aW9uIGZvciBuYW1lc3BhY2VzXG4gICAgZXhwb3J0c1trZXldID0gSVNfR0xPQkFMICYmIHR5cGVvZiB0YXJnZXRba2V5XSAhPSAnZnVuY3Rpb24nID8gc291cmNlW2tleV1cbiAgICAvLyBiaW5kIHRpbWVycyB0byBnbG9iYWwgZm9yIGNhbGwgZnJvbSBleHBvcnQgY29udGV4dFxuICAgIDogSVNfQklORCAmJiBvd24gPyBjdHgob3V0LCBnbG9iYWwpXG4gICAgLy8gd3JhcCBnbG9iYWwgY29uc3RydWN0b3JzIGZvciBwcmV2ZW50IGNoYW5nZSB0aGVtIGluIGxpYnJhcnlcbiAgICA6IElTX1dSQVAgJiYgdGFyZ2V0W2tleV0gPT0gb3V0ID8gKGZ1bmN0aW9uKEMpe1xuICAgICAgdmFyIEYgPSBmdW5jdGlvbihhLCBiLCBjKXtcbiAgICAgICAgaWYodGhpcyBpbnN0YW5jZW9mIEMpe1xuICAgICAgICAgIHN3aXRjaChhcmd1bWVudHMubGVuZ3RoKXtcbiAgICAgICAgICAgIGNhc2UgMDogcmV0dXJuIG5ldyBDO1xuICAgICAgICAgICAgY2FzZSAxOiByZXR1cm4gbmV3IEMoYSk7XG4gICAgICAgICAgICBjYXNlIDI6IHJldHVybiBuZXcgQyhhLCBiKTtcbiAgICAgICAgICB9IHJldHVybiBuZXcgQyhhLCBiLCBjKTtcbiAgICAgICAgfSByZXR1cm4gQy5hcHBseSh0aGlzLCBhcmd1bWVudHMpO1xuICAgICAgfTtcbiAgICAgIEZbUFJPVE9UWVBFXSA9IENbUFJPVE9UWVBFXTtcbiAgICAgIHJldHVybiBGO1xuICAgIC8vIG1ha2Ugc3RhdGljIHZlcnNpb25zIGZvciBwcm90b3R5cGUgbWV0aG9kc1xuICAgIH0pKG91dCkgOiBJU19QUk9UTyAmJiB0eXBlb2Ygb3V0ID09ICdmdW5jdGlvbicgPyBjdHgoRnVuY3Rpb24uY2FsbCwgb3V0KSA6IG91dDtcbiAgICAvLyBleHBvcnQgcHJvdG8gbWV0aG9kcyB0byBjb3JlLiVDT05TVFJVQ1RPUiUubWV0aG9kcy4lTkFNRSVcbiAgICBpZihJU19QUk9UTyl7XG4gICAgICAoZXhwb3J0cy52aXJ0dWFsIHx8IChleHBvcnRzLnZpcnR1YWwgPSB7fSkpW2tleV0gPSBvdXQ7XG4gICAgICAvLyBleHBvcnQgcHJvdG8gbWV0aG9kcyB0byBjb3JlLiVDT05TVFJVQ1RPUiUucHJvdG90eXBlLiVOQU1FJVxuICAgICAgaWYodHlwZSAmICRleHBvcnQuUiAmJiBleHBQcm90byAmJiAhZXhwUHJvdG9ba2V5XSloaWRlKGV4cFByb3RvLCBrZXksIG91dCk7XG4gICAgfVxuICB9XG59O1xuLy8gdHlwZSBiaXRtYXBcbiRleHBvcnQuRiA9IDE7ICAgLy8gZm9yY2VkXG4kZXhwb3J0LkcgPSAyOyAgIC8vIGdsb2JhbFxuJGV4cG9ydC5TID0gNDsgICAvLyBzdGF0aWNcbiRleHBvcnQuUCA9IDg7ICAgLy8gcHJvdG9cbiRleHBvcnQuQiA9IDE2OyAgLy8gYmluZFxuJGV4cG9ydC5XID0gMzI7ICAvLyB3cmFwXG4kZXhwb3J0LlUgPSA2NDsgIC8vIHNhZmVcbiRleHBvcnQuUiA9IDEyODsgLy8gcmVhbCBwcm90byBtZXRob2QgZm9yIGBsaWJyYXJ5YCBcbm1vZHVsZS5leHBvcnRzID0gJGV4cG9ydDtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2V4cG9ydC5qc1xuICoqIG1vZHVsZSBpZCA9IDhcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDVcbiAqKi8iLCIvLyBodHRwczovL2dpdGh1Yi5jb20vemxvaXJvY2svY29yZS1qcy9pc3N1ZXMvODYjaXNzdWVjb21tZW50LTExNTc1OTAyOFxudmFyIGdsb2JhbCA9IG1vZHVsZS5leHBvcnRzID0gdHlwZW9mIHdpbmRvdyAhPSAndW5kZWZpbmVkJyAmJiB3aW5kb3cuTWF0aCA9PSBNYXRoXG4gID8gd2luZG93IDogdHlwZW9mIHNlbGYgIT0gJ3VuZGVmaW5lZCcgJiYgc2VsZi5NYXRoID09IE1hdGggPyBzZWxmIDogRnVuY3Rpb24oJ3JldHVybiB0aGlzJykoKTtcbmlmKHR5cGVvZiBfX2cgPT0gJ251bWJlcicpX19nID0gZ2xvYmFsOyAvLyBlc2xpbnQtZGlzYWJsZS1saW5lIG5vLXVuZGVmXG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19nbG9iYWwuanNcbiAqKiBtb2R1bGUgaWQgPSA5XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1XG4gKiovIiwidmFyIGNvcmUgPSBtb2R1bGUuZXhwb3J0cyA9IHt2ZXJzaW9uOiAnMi4yLjAnfTtcbmlmKHR5cGVvZiBfX2UgPT0gJ251bWJlcicpX19lID0gY29yZTsgLy8gZXNsaW50LWRpc2FibGUtbGluZSBuby11bmRlZlxuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fY29yZS5qc1xuICoqIG1vZHVsZSBpZCA9IDEwXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1XG4gKiovIiwiLy8gb3B0aW9uYWwgLyBzaW1wbGUgY29udGV4dCBiaW5kaW5nXG52YXIgYUZ1bmN0aW9uID0gcmVxdWlyZSgnLi9fYS1mdW5jdGlvbicpO1xubW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihmbiwgdGhhdCwgbGVuZ3RoKXtcbiAgYUZ1bmN0aW9uKGZuKTtcbiAgaWYodGhhdCA9PT0gdW5kZWZpbmVkKXJldHVybiBmbjtcbiAgc3dpdGNoKGxlbmd0aCl7XG4gICAgY2FzZSAxOiByZXR1cm4gZnVuY3Rpb24oYSl7XG4gICAgICByZXR1cm4gZm4uY2FsbCh0aGF0LCBhKTtcbiAgICB9O1xuICAgIGNhc2UgMjogcmV0dXJuIGZ1bmN0aW9uKGEsIGIpe1xuICAgICAgcmV0dXJuIGZuLmNhbGwodGhhdCwgYSwgYik7XG4gICAgfTtcbiAgICBjYXNlIDM6IHJldHVybiBmdW5jdGlvbihhLCBiLCBjKXtcbiAgICAgIHJldHVybiBmbi5jYWxsKHRoYXQsIGEsIGIsIGMpO1xuICAgIH07XG4gIH1cbiAgcmV0dXJuIGZ1bmN0aW9uKC8qIC4uLmFyZ3MgKi8pe1xuICAgIHJldHVybiBmbi5hcHBseSh0aGF0LCBhcmd1bWVudHMpO1xuICB9O1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2N0eC5qc1xuICoqIG1vZHVsZSBpZCA9IDExXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1XG4gKiovIiwibW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihpdCl7XG4gIGlmKHR5cGVvZiBpdCAhPSAnZnVuY3Rpb24nKXRocm93IFR5cGVFcnJvcihpdCArICcgaXMgbm90IGEgZnVuY3Rpb24hJyk7XG4gIHJldHVybiBpdDtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19hLWZ1bmN0aW9uLmpzXG4gKiogbW9kdWxlIGlkID0gMTJcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDVcbiAqKi8iLCJ2YXIgZFAgICAgICAgICA9IHJlcXVpcmUoJy4vX29iamVjdC1kcCcpXG4gICwgY3JlYXRlRGVzYyA9IHJlcXVpcmUoJy4vX3Byb3BlcnR5LWRlc2MnKTtcbm1vZHVsZS5leHBvcnRzID0gcmVxdWlyZSgnLi9fZGVzY3JpcHRvcnMnKSA/IGZ1bmN0aW9uKG9iamVjdCwga2V5LCB2YWx1ZSl7XG4gIHJldHVybiBkUC5mKG9iamVjdCwga2V5LCBjcmVhdGVEZXNjKDEsIHZhbHVlKSk7XG59IDogZnVuY3Rpb24ob2JqZWN0LCBrZXksIHZhbHVlKXtcbiAgb2JqZWN0W2tleV0gPSB2YWx1ZTtcbiAgcmV0dXJuIG9iamVjdDtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19oaWRlLmpzXG4gKiogbW9kdWxlIGlkID0gMTNcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDVcbiAqKi8iLCJ2YXIgYW5PYmplY3QgICAgICAgPSByZXF1aXJlKCcuL19hbi1vYmplY3QnKVxuICAsIElFOF9ET01fREVGSU5FID0gcmVxdWlyZSgnLi9faWU4LWRvbS1kZWZpbmUnKVxuICAsIHRvUHJpbWl0aXZlICAgID0gcmVxdWlyZSgnLi9fdG8tcHJpbWl0aXZlJylcbiAgLCBkUCAgICAgICAgICAgICA9IE9iamVjdC5kZWZpbmVQcm9wZXJ0eTtcblxuZXhwb3J0cy5mID0gcmVxdWlyZSgnLi9fZGVzY3JpcHRvcnMnKSA/IE9iamVjdC5kZWZpbmVQcm9wZXJ0eSA6IGZ1bmN0aW9uIGRlZmluZVByb3BlcnR5KE8sIFAsIEF0dHJpYnV0ZXMpe1xuICBhbk9iamVjdChPKTtcbiAgUCA9IHRvUHJpbWl0aXZlKFAsIHRydWUpO1xuICBhbk9iamVjdChBdHRyaWJ1dGVzKTtcbiAgaWYoSUU4X0RPTV9ERUZJTkUpdHJ5IHtcbiAgICByZXR1cm4gZFAoTywgUCwgQXR0cmlidXRlcyk7XG4gIH0gY2F0Y2goZSl7IC8qIGVtcHR5ICovIH1cbiAgaWYoJ2dldCcgaW4gQXR0cmlidXRlcyB8fCAnc2V0JyBpbiBBdHRyaWJ1dGVzKXRocm93IFR5cGVFcnJvcignQWNjZXNzb3JzIG5vdCBzdXBwb3J0ZWQhJyk7XG4gIGlmKCd2YWx1ZScgaW4gQXR0cmlidXRlcylPW1BdID0gQXR0cmlidXRlcy52YWx1ZTtcbiAgcmV0dXJuIE87XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fb2JqZWN0LWRwLmpzXG4gKiogbW9kdWxlIGlkID0gMTRcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDVcbiAqKi8iLCJ2YXIgaXNPYmplY3QgPSByZXF1aXJlKCcuL19pcy1vYmplY3QnKTtcbm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oaXQpe1xuICBpZighaXNPYmplY3QoaXQpKXRocm93IFR5cGVFcnJvcihpdCArICcgaXMgbm90IGFuIG9iamVjdCEnKTtcbiAgcmV0dXJuIGl0O1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2FuLW9iamVjdC5qc1xuICoqIG1vZHVsZSBpZCA9IDE1XG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1XG4gKiovIiwibW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihpdCl7XG4gIHJldHVybiB0eXBlb2YgaXQgPT09ICdvYmplY3QnID8gaXQgIT09IG51bGwgOiB0eXBlb2YgaXQgPT09ICdmdW5jdGlvbic7XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9faXMtb2JqZWN0LmpzXG4gKiogbW9kdWxlIGlkID0gMTZcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDVcbiAqKi8iLCJtb2R1bGUuZXhwb3J0cyA9ICFyZXF1aXJlKCcuL19kZXNjcmlwdG9ycycpICYmICFyZXF1aXJlKCcuL19mYWlscycpKGZ1bmN0aW9uKCl7XHJcbiAgcmV0dXJuIE9iamVjdC5kZWZpbmVQcm9wZXJ0eShyZXF1aXJlKCcuL19kb20tY3JlYXRlJykoJ2RpdicpLCAnYScsIHtnZXQ6IGZ1bmN0aW9uKCl7IHJldHVybiA3OyB9fSkuYSAhPSA3O1xyXG59KTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2llOC1kb20tZGVmaW5lLmpzXG4gKiogbW9kdWxlIGlkID0gMTdcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDVcbiAqKi8iLCIvLyBUaGFuaydzIElFOCBmb3IgaGlzIGZ1bm55IGRlZmluZVByb3BlcnR5XG5tb2R1bGUuZXhwb3J0cyA9ICFyZXF1aXJlKCcuL19mYWlscycpKGZ1bmN0aW9uKCl7XG4gIHJldHVybiBPYmplY3QuZGVmaW5lUHJvcGVydHkoe30sICdhJywge2dldDogZnVuY3Rpb24oKXsgcmV0dXJuIDc7IH19KS5hICE9IDc7XG59KTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2Rlc2NyaXB0b3JzLmpzXG4gKiogbW9kdWxlIGlkID0gMThcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDVcbiAqKi8iLCJtb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGV4ZWMpe1xuICB0cnkge1xuICAgIHJldHVybiAhIWV4ZWMoKTtcbiAgfSBjYXRjaChlKXtcbiAgICByZXR1cm4gdHJ1ZTtcbiAgfVxufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX2ZhaWxzLmpzXG4gKiogbW9kdWxlIGlkID0gMTlcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDVcbiAqKi8iLCJ2YXIgaXNPYmplY3QgPSByZXF1aXJlKCcuL19pcy1vYmplY3QnKVxuICAsIGRvY3VtZW50ID0gcmVxdWlyZSgnLi9fZ2xvYmFsJykuZG9jdW1lbnRcbiAgLy8gaW4gb2xkIElFIHR5cGVvZiBkb2N1bWVudC5jcmVhdGVFbGVtZW50IGlzICdvYmplY3QnXG4gICwgaXMgPSBpc09iamVjdChkb2N1bWVudCkgJiYgaXNPYmplY3QoZG9jdW1lbnQuY3JlYXRlRWxlbWVudCk7XG5tb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGl0KXtcbiAgcmV0dXJuIGlzID8gZG9jdW1lbnQuY3JlYXRlRWxlbWVudChpdCkgOiB7fTtcbn07XG5cblxuLyoqKioqKioqKioqKioqKioqXG4gKiogV0VCUEFDSyBGT09URVJcbiAqKiAuL34vYmFiZWwtcnVudGltZS9+L2NvcmUtanMvbGlicmFyeS9tb2R1bGVzL19kb20tY3JlYXRlLmpzXG4gKiogbW9kdWxlIGlkID0gMjBcbiAqKiBtb2R1bGUgY2h1bmtzID0gMCAxIDIgMyA0IDVcbiAqKi8iLCIvLyA3LjEuMSBUb1ByaW1pdGl2ZShpbnB1dCBbLCBQcmVmZXJyZWRUeXBlXSlcbnZhciBpc09iamVjdCA9IHJlcXVpcmUoJy4vX2lzLW9iamVjdCcpO1xuLy8gaW5zdGVhZCBvZiB0aGUgRVM2IHNwZWMgdmVyc2lvbiwgd2UgZGlkbid0IGltcGxlbWVudCBAQHRvUHJpbWl0aXZlIGNhc2Vcbi8vIGFuZCB0aGUgc2Vjb25kIGFyZ3VtZW50IC0gZmxhZyAtIHByZWZlcnJlZCB0eXBlIGlzIGEgc3RyaW5nXG5tb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKGl0LCBTKXtcbiAgaWYoIWlzT2JqZWN0KGl0KSlyZXR1cm4gaXQ7XG4gIHZhciBmbiwgdmFsO1xuICBpZihTICYmIHR5cGVvZiAoZm4gPSBpdC50b1N0cmluZykgPT0gJ2Z1bmN0aW9uJyAmJiAhaXNPYmplY3QodmFsID0gZm4uY2FsbChpdCkpKXJldHVybiB2YWw7XG4gIGlmKHR5cGVvZiAoZm4gPSBpdC52YWx1ZU9mKSA9PSAnZnVuY3Rpb24nICYmICFpc09iamVjdCh2YWwgPSBmbi5jYWxsKGl0KSkpcmV0dXJuIHZhbDtcbiAgaWYoIVMgJiYgdHlwZW9mIChmbiA9IGl0LnRvU3RyaW5nKSA9PSAnZnVuY3Rpb24nICYmICFpc09iamVjdCh2YWwgPSBmbi5jYWxsKGl0KSkpcmV0dXJuIHZhbDtcbiAgdGhyb3cgVHlwZUVycm9yKFwiQ2FuJ3QgY29udmVydCBvYmplY3QgdG8gcHJpbWl0aXZlIHZhbHVlXCIpO1xufTtcblxuXG4vKioqKioqKioqKioqKioqKipcbiAqKiBXRUJQQUNLIEZPT1RFUlxuICoqIC4vfi9iYWJlbC1ydW50aW1lL34vY29yZS1qcy9saWJyYXJ5L21vZHVsZXMvX3RvLXByaW1pdGl2ZS5qc1xuICoqIG1vZHVsZSBpZCA9IDIxXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1XG4gKiovIiwibW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihiaXRtYXAsIHZhbHVlKXtcbiAgcmV0dXJuIHtcbiAgICBlbnVtZXJhYmxlICA6ICEoYml0bWFwICYgMSksXG4gICAgY29uZmlndXJhYmxlOiAhKGJpdG1hcCAmIDIpLFxuICAgIHdyaXRhYmxlICAgIDogIShiaXRtYXAgJiA0KSxcbiAgICB2YWx1ZSAgICAgICA6IHZhbHVlXG4gIH07XG59O1xuXG5cbi8qKioqKioqKioqKioqKioqKlxuICoqIFdFQlBBQ0sgRk9PVEVSXG4gKiogLi9+L2JhYmVsLXJ1bnRpbWUvfi9jb3JlLWpzL2xpYnJhcnkvbW9kdWxlcy9fcHJvcGVydHktZGVzYy5qc1xuICoqIG1vZHVsZSBpZCA9IDIyXG4gKiogbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1XG4gKiovIiwiJ3VzZSBzdHJpY3QnO1xuXG5pbXBvcnQgTm90aWZDb21tZXJjaWFsIGZyb20gXCIuL2xheW91dC9fbm90aWZpY2F0aW9uLWNvbW1lcmNpYWxcIjtcbmltcG9ydCBTY3JvbGxUb0VsZW1lbnQgZnJvbSBcIi4vbGF5b3V0L19zY3JvbGwtdG9cIjtcbmltcG9ydCBOb3RpZmljYXRpb25MaXN0IGZyb20gXCIuL2xheW91dC9fbm90aWZhdGlvbi1saXN0XCI7XG5pbXBvcnQgUHJldmlld01hcmtkb3duIGZyb20gXCIuL2xheW91dC9fcHJldmlldy1tYXJrZG93blwiO1xuXG4kKGZ1bmN0aW9uKCl7XG4gICAgU2Nyb2xsVG9FbGVtZW50LnBsdWdpbignLmpzLWxpbmstdGFyZ2V0Jyk7XG4gICAgTm90aWZpY2F0aW9uTGlzdC5wbHVnaW4oJy5qcy1ub3RpZmljYXRpb24tbGlzdCcpO1xuICAgIFByZXZpZXdNYXJrZG93bi5wbHVnaW4oJ1ttYXJrZG93bnByZXZpZXddJyk7XG4gICAgTm90aWZDb21tZXJjaWFsLnBsdWdpbignLmpzLW5vdGlmLWNvbW1lcmNpYWwnKTtcblxuICAgIGNvbnN0ICRkYXRhRmllbGQgPSAkKCdbZGF0YS10eXBlPVwiZGF0ZVwiXScpO1xuICAgICRkYXRhRmllbGQubGVuZ3RoICYmICRkYXRhRmllbGQuZGF0ZXRpbWVwaWNrZXIoe1xuICAgICAgICB1c2VDdXJyZW50OiBmYWxzZSxcbiAgICAgICAgcGlja1RpbWU6IGZhbHNlXG4gICAgfSk7XG5cblxufSk7XG5cblxuXG5cbi8qKiBXRUJQQUNLIEZPT1RFUiAqKlxuICoqIC4vZnJvbnRlbmQvanMvY29tbW9uLXBhY2suanNcbiAqKi8iLCIndXNlIHN0cmljdCc7XG5cbmV4cG9ydCBkZWZhdWx0IGNsYXNzIFdpZGdldCB7XG4gICAgLyoqXG4gICAgICogRmlsdGVyIGhpc3RvcnlcbiAgICAgKiBAcGFyYW0ge1N0cmluZ30gc2VsZWN0b3JcbiAgICAgKi9cbiAgICBjb25zdHJ1Y3RvcihzZWxlY3Rvcikge1xuICAgICAgICB0aGlzLiRyb290ID0gJChzZWxlY3Rvcik7XG4gICAgICAgIHRoaXMubG9jYWxzID0gdGhpcy5fZ2V0RG9tKCk7XG5cbiAgICAgICAgdGhpcy5fYXNzaWduRXZlbnRzKCk7XG4gICAgfVxuXG4gICAgX2dldERvbSgpIHtcbiAgICAgICAgY29uc3QgJHJvb3QgPSB0aGlzLiRyb290O1xuXG4gICAgICAgIHJldHVybiB7XG4gICAgICAgICAgICAkbGlzdDogJHJvb3QuZmluZCgnW2RhdGEtZmlsdGVyLWxpc3RdJyksXG4gICAgICAgICAgICAkaXRlbXM6ICRyb290LmZpbmQoJ1tkYXRhLWZpbHRlci10ZXh0XScpLFxuICAgICAgICB9O1xuICAgIH1cblxuICAgIF9hc3NpZ25FdmVudHMoKSB7XG5cbiAgICB9XG5cbiAgICBcblxuICAgIC8vIHN0YXRpY1xuICAgIHN0YXRpYyBwbHVnaW4oc2VsZWN0b3IpIHtcbiAgICAgICAgY29uc3QgJGVsZW1zID0gJChzZWxlY3Rvcik7XG4gICAgICAgIGlmICghJGVsZW1zLmxlbmd0aCkgcmV0dXJuO1xuXG4gICAgICAgIHJldHVybiAkZWxlbXMuZWFjaChmdW5jdGlvbiAoaW5kZXgsIGVsKSB7XG4gICAgICAgICAgICBsZXQgJGVsZW1lbnQgPSAkKGVsKTtcbiAgICAgICAgICAgIGxldCBkYXRhICAgICA9ICRlbGVtZW50LmRhdGEoJ3dpZGdldCcpO1xuXG4gICAgICAgICAgICBpZiAoIWRhdGEpIHtcbiAgICAgICAgICAgICAgICBkYXRhID0gbmV3IFdpZGdldChlbCk7XG4gICAgICAgICAgICAgICAgJGVsZW1lbnQuZGF0YSgnd2lkZ2V0JywgZGF0YSk7XG4gICAgICAgICAgICB9XG4gICAgICAgIH0pXG4gICAgfVxufVxuXG5cblxuLyoqXG4gKiAgSGVhZGVyIG5vdGlmaWNhdGlvblxuICovXG4oZnVuY3Rpb24gKCQsIEFwcCkge1xuICAgICd1c2Ugc3RyaWN0JztcblxuICAgIGZ1bmN0aW9uIFRvcE5vdGlmaWNhdGlvbihzZWxlY3Rvciwgb3B0aW9ucyl7XG4gICAgICAgIHZhciBzZWxmID0gdGhpcztcblxuICAgICAgICBzZWxmLiRyb290ID0gJChzZWxlY3Rvcik7XG4gICAgICAgIHNlbGYub3B0aW9ucyA9ICQuZXh0ZW5kKHt9LCBvcHRpb25zLCBzZWxmLiRyb290LmRhdGEoKSk7XG4gICAgICAgIHNlbGYub3B0aW9ucy51bmlxdWVLZXkgPSAndG9wTm90aWZpY3Rpb24nO1xuXG4gICAgICAgIGlmICghc2VsZi5pc1Nob3dlZCgpKSB7XG4gICAgICAgICAgICBzZWxmLnNob3coKTtcbiAgICAgICAgfTtcbiAgICAgICAgc2VsZi5hc3NpZ25FdmVudHMoKTtcbiAgICB9XG5cbiAgICBUb3BOb3RpZmljYXRpb24ucHJvdG90eXBlLmFzc2lnbkV2ZW50cyA9IGZ1bmN0aW9uKCl7XG4gICAgICAgIHZhciBzZWxmID0gdGhpcztcblxuICAgICAgICBzZWxmLiRyb290XG4gICAgICAgICAgICAub24oJ2NsaWNrJywgJ1tkYXRhLW5vdGlmaWNhdGlvbi1jbG9zZV0nLCBmdW5jdGlvbiAoZSkge1xuICAgICAgICAgICAgICAgIHNlbGYuc2V0SXNTaG93ZWQoe1xuICAgICAgICAgICAgICAgICAgICBzdGF0dXM6ICdjbG9zZSdcbiAgICAgICAgICAgICAgICB9KTtcbiAgICAgICAgICAgICAgICBzZWxmLmhpZGUoKTtcbiAgICAgICAgICAgICAgICBlLnByZXZlbnREZWZhdWx0KCk7XG4gICAgICAgICAgICB9KVxuICAgICAgICAgICAgLm9uKCdjbGljaycsICdbZGF0YS1ub3RpZmljYXRpb24tYWNjZXB0XScsIGZ1bmN0aW9uICgpIHtcbiAgICAgICAgICAgICAgICBzZWxmLnNldElzU2hvd2VkKHtcbiAgICAgICAgICAgICAgICAgICAgc3RhdHVzOiAnYWNjZXB0J1xuICAgICAgICAgICAgICAgIH0pO1xuICAgICAgICAgICAgfSlcbiAgICB9O1xuXG4gICAgVG9wTm90aWZpY2F0aW9uLnByb3RvdHlwZS5pc1Nob3dlZCA9IGZ1bmN0aW9uKCl7XG4gICAgICAgIHZhciBzZWxmID0gdGhpcyxcbiAgICAgICAgICAgIHZhbHVlID0gbG9jYWxTdG9yYWdlLmdldEl0ZW0oc2VsZi5vcHRpb25zLnVuaXF1ZUtleSk7XG5cbiAgICAgICAgcmV0dXJuIHZhbHVlICYmIHZhbHVlID09ICdzaG93ZWQnO1xuICAgIH1cblxuICAgIFRvcE5vdGlmaWNhdGlvbi5wcm90b3R5cGUuc2V0SXNTaG93ZWQgPSBmdW5jdGlvbihkYXRhKXtcbiAgICAgICAgdmFyIHNlbGYgPSB0aGlzO1xuXG4gICAgICAgIGxvY2FsU3RvcmFnZS5zZXRJdGVtKHNlbGYub3B0aW9ucy51bmlxdWVLZXksICdzaG93ZWQnKTtcbiAgICAgICAgJC5wb3N0KCcvJywge1xuICAgICAgICAgICAgc3RhdHVzOiBkYXRhLnN0YXR1c1xuICAgICAgICB9KVxuICAgIH1cblxuICAgIFRvcE5vdGlmaWNhdGlvbi5wcm90b3R5cGUuaGlkZSA9IGZ1bmN0aW9uKCl7XG4gICAgICAgIHRoaXMuJHJvb3QucmVtb3ZlQ2xhc3MoJ3N0YXRlX3Nob3cnKTtcbiAgICB9XG5cbiAgICBUb3BOb3RpZmljYXRpb24ucHJvdG90eXBlLnNob3cgPSBmdW5jdGlvbigpe1xuICAgICAgICB0aGlzLiRyb290LmFkZENsYXNzKCdzdGF0ZV9zaG93Jyk7XG4gICAgfVxuXG4gICAgQXBwLndpZGdldHMuVG9wTm90aWZpY2F0aW9uID0gVG9wTm90aWZpY2F0aW9uO1xuXG59KShqUXVlcnksIEFwcCk7XG5cblxuXG5cbi8qKiBXRUJQQUNLIEZPT1RFUiAqKlxuICoqIC4vZnJvbnRlbmQvanMvbGF5b3V0L19ub3RpZmljYXRpb24tY29tbWVyY2lhbC5qc1xuICoqLyIsIid1c2Ugc3RyaWN0JztcblxuZXhwb3J0IGRlZmF1bHQgY2xhc3MgV2lkZ2V0IHtcbiAgICAvKipcbiAgICAgKiBGaWx0ZXIgaGlzdG9yeVxuICAgICAqIEBwYXJhbSB7U3RyaW5nfSBzZWxlY3RvclxuICAgICAqL1xuICAgIGNvbnN0cnVjdG9yKHNlbGVjdG9yKSB7XG4gICAgICAgIHRoaXMuJHJvb3QgPSAkKHNlbGVjdG9yKTtcbiAgICAgICAgdGhpcy5fYXNzaWduRXZlbnRzKCk7XG4gICAgfVxuXG4gICAgX2Fzc2lnbkV2ZW50cygpIHtcbiAgICAgICAgdGhpcy4kcm9vdC5vbignY2xpY2snLCB0aGlzLl9vbkNsaWNrTGluay5iaW5kKHRoaXMpKTtcbiAgICB9XG5cbiAgICBfb25DbGlja0xpbmsoZSkge1xuICAgICAgICBjb25zdCAkbGluayA9ICQoZS5jdXJyZW50VGFyZ2V0KTtcbiAgICAgICAgY29uc3QgdGFyZ2V0ID0gJGxpbmsuZGF0YSgndGFyZ2V0Jyk7XG5cbiAgICAgICAgaWYgKCF0aGlzLl9pc1RhcmdldFZhbGlkKHRhcmdldCkpIHJldHVybiBmYWxzZTtcblxuICAgICAgICB0aGlzLnNjcm9sbFRvVGFyZ2V0KCcjJyArIHRhcmdldCk7XG4gICAgICAgIGUucHJldmVudERlZmF1bHQoKTtcbiAgICB9XG5cbiAgICBfaXNUYXJnZXRWYWxpZCh0YXJnZXQpe1xuICAgICAgICBsZXQgdmFsaWQgPSB0cnVlO1xuXG4gICAgICAgIGlmICghdGFyZ2V0KSB7XG4gICAgICAgICAgICBjb25zb2xlLmxvZygnVGhlcmUgaXMgbm8gZGF0YS10YXJnZXQgYXR0cmlidXRlIHdpdGggaWQtbmFtZSBmb3IgdGhpcyBsaW5rJyk7XG4gICAgICAgICAgICB2YWxpZCA9IGZhbHNlO1xuICAgICAgICB9XG5cbiAgICAgICAgaWYgKCEkKCcjJyArIHRhcmdldCkubGVuZ3RoKSB7XG4gICAgICAgICAgICBjb25zb2xlLmxvZygnVGhlcmUgaXMgbm8gZWxlbWVudCB3aXRoIHN1Y2ggaWQgbmFtZScpO1xuICAgICAgICAgICAgdmFsaWQgPSBmYWxzZTtcbiAgICAgICAgfVxuXG4gICAgICAgIHJldHVybiB2YWxpZDtcbiAgICB9XG5cbiAgICAvKipcbiAgICAgKiBTY3JvbGwgdG8gdGhlIGVsZW1lbnQgd2l0aCBcInRhcmdldFwiIGlkXG4gICAgICogQHBhcmFtIHtTdHJpbmd9IHRhcmdldCAtIGlkIHNlbGVjdG9yIG9mIGVsZW1lbnRcbiAgICAgKiBAcmV0dXJucyB7Ym9vbGVhbn1cbiAgICAgKi9cbiAgICBzY3JvbGxUb1RhcmdldCh0YXJnZXQpIHtcbiAgICAgICAgY29uc3QgJHRhcmdldCA9ICQodGFyZ2V0KTtcblxuICAgICAgICBpZiAoISR0YXJnZXQubGVuZ3RoKSByZXR1cm4gZmFsc2U7XG5cbiAgICAgICAgJCgnaHRtbCwgYm9keScpLmFuaW1hdGUoe1xuICAgICAgICAgICAgc2Nyb2xsVG9wOiAkdGFyZ2V0Lm9mZnNldCgpLnRvcFxuICAgICAgICB9LCA0MDApO1xuICAgIH1cblxuICAgIC8vIHN0YXRpY1xuICAgIHN0YXRpYyBwbHVnaW4oc2VsZWN0b3IpIHtcbiAgICAgICAgY29uc3QgJGVsZW1zID0gJChzZWxlY3Rvcik7XG4gICAgICAgIGlmICghJGVsZW1zLmxlbmd0aCkgcmV0dXJuO1xuXG4gICAgICAgIHJldHVybiAkZWxlbXMuZWFjaChmdW5jdGlvbiAoaW5kZXgsIGVsKSB7XG4gICAgICAgICAgICBsZXQgJGVsZW1lbnQgPSAkKGVsKTtcbiAgICAgICAgICAgIGxldCBkYXRhICAgICA9ICRlbGVtZW50LmRhdGEoJ3dpZGdldC5zY3JvbGx0bycpO1xuXG4gICAgICAgICAgICBpZiAoIWRhdGEpIHtcbiAgICAgICAgICAgICAgICBkYXRhID0gbmV3IFdpZGdldChlbCk7XG4gICAgICAgICAgICAgICAgJGVsZW1lbnQuZGF0YSgnd2lkZ2V0JywgZGF0YSk7XG4gICAgICAgICAgICB9XG4gICAgICAgIH0pXG4gICAgfVxufVxuXG5cblxuLyoqIFdFQlBBQ0sgRk9PVEVSICoqXG4gKiogLi9mcm9udGVuZC9qcy9sYXlvdXQvX3Njcm9sbC10by5qc1xuICoqLyIsIid1c2Ugc3RyaWN0JztcblxuLyoqXG4gKiBOb3RpZmljYXRpb24gZm9yIHVzZXIgYWJvdXQgbmV3IGV2ZW50c1xuICovXG5cbi8qKlxuICogTm90aWZpY2F0aW9uIG9iamVjdFxuICogQHR5cGVkZWYge09iamVjdH0gTm90aWZpY2F0aW9uSXRlbVxuICogQHByb3BlcnR5IHtTdHJpbmd9IGJvZHkgICAgICAtIGh0bWwgb2YgdGhlIG5vdGlmaWNhdGlvblxuICogQHByb3BlcnR5IHtCb29sZW59IHVucmVhZCAgICAtIGlzIGN1cnJlbnQgbm90aWZpY2F0aW9uIGFscmVhZHkgdmlld2VkP1xuICogQHByb3BlcnR5IHtOdW1iZXJ9IGlkICAgICAgICAtIGlkIG9mIHRoZSBub3RpZmljYXRpb25cbiAqIEBwcm9wZXJ0eSB7U3RyaW5nfSB0eXBlICAgICAgLSB0eXBlIG9mIHRoZSBub3RpZmljYXRpb25cbiAqL1xuXG5leHBvcnQgZGVmYXVsdCBjbGFzcyBXaWRnZXQge1xuICAgIC8qKlxuICAgICAqXG4gICAgICogQHBhcmFtIHsoU3RyaW5nfGRvbUVsZW1lbnQpfSBzZWxlY3RvciAgLSBzZWxlY3RvciBvciBkb21FbGVtZW50IGFzIHJvb3QgZWxlbWVudCBvZiB0aGUgd2lkZ2V0XG4gICAgICogQGNvbnN0cnVjdG9yXG4gICAgICovXG4gICAgY29uc3RydWN0b3Ioc2VsZWN0b3IpIHtcbiAgICAgICAgY29uc3Qgc2VsZiA9IHRoaXM7XG5cbiAgICAgICAgc2VsZi4kcm9vdCA9ICQoc2VsZWN0b3IpO1xuICAgICAgICBzZWxmLmxvY2FscyA9IHNlbGYuX2dldERvbSgpO1xuXG4gICAgICAgIHNlbGYuX2luaXQoKTtcbiAgICAgICAgc2VsZi5fYXNzaWduRXZlbnRzKCk7XG4gICAgfVxuXG4gICAgLyoqXG4gICAgICogR2V0ICQgZWxlbWVudHMgb2YgdGhlIHdpZGdldFxuICAgICAqIEByZXR1cm5zIHtPYmplY3R9IC0galF1ZXJ5IGxpbmtzIG90IHRoZSBlbGVtZW50cyBvZiB0aGUgd2lkZ2V0XG4gICAgICogQHByaXZhdGVcbiAgICAgKi9cbiAgICBfZ2V0RG9tKCkge1xuICAgICAgICBjb25zdCAkcm9vdCA9IHRoaXMuJHJvb3Q7XG5cbiAgICAgICAgcmV0dXJuIHtcbiAgICAgICAgICAgICRsaXN0OiAkcm9vdC5maW5kKCdbZGF0YS1ub3RpZmxpc3QtbGlzdF0nKSxcbiAgICAgICAgICAgICRjbG9zZTogJHJvb3QuZmluZCgnW2RhdGEtbm90aWZsaXN0LWNsb3NlXScpLFxuICAgICAgICAgICAgJGxpbms6ICRyb290LmZpbmQoJ1tkYXRhLW5vdGlmbGlzdC1zaG93XScpLFxuICAgICAgICAgICAgJGxvYWQ6ICRyb290LmZpbmQoJ1tkYXRhLW5vdGlmbGlzdC1sb2FkXScpLFxuICAgICAgICAgICAgJGNvdW50OiAkcm9vdC5maW5kKCdbZGF0YS1ub3RpZmxpc3QtY291bnRdJyksXG4gICAgICAgIH07XG4gICAgfVxuXG4gICAgLyoqXG4gICAgICogSW5pdGlhdGUgYmFzZSB2YXJpYWJsZXMgZm9yIHdpZGdldFxuICAgICAqIEBwcml2YXRlXG4gICAgICovXG4gICAgX2luaXQoKSB7XG4gICAgICAgIGNvbnN0IHNlbGYgPSB0aGlzO1xuXG4gICAgICAgIHNlbGYuaXNMb2FkZWQgPSBmYWxzZTtcbiAgICAgICAgc2VsZi5vZmZzZXQgPSAwO1xuICAgICAgICBzZWxmLmlzVmlzaWJsZSA9IGZhbHNlO1xuXG4gICAgICAgIHNlbGYuX3JlY2lldmVVbnJlYWRDb3VudCgpXG4gICAgICAgICAgICAuZG9uZShmdW5jdGlvbiAoY291bnQpIHtcbiAgICAgICAgICAgICAgICBzZWxmLnNldFVucmVhZENvdW50KGNvdW50KTtcbiAgICAgICAgICAgIH0pO1xuICAgIH1cblxuICAgIF9hc3NpZ25FdmVudHMoKSB7XG4gICAgICAgIGNvbnN0IHNlbGYgPSB0aGlzO1xuXG4gICAgICAgIHNlbGYuJHJvb3RcbiAgICAgICAgICAgIC5vbignY2xpY2snLCAnW2RhdGEtbm90aWZsaXN0LXNob3ddLCBbZGF0YS1ub3RpZmxpc3QtY2xvc2VdJywgc2VsZi5fb25DbGlja1RvZ2dsZVNob3cuYmluZChzZWxmKSlcbiAgICAgICAgICAgIC5vbignY2xpY2snLCAnW2RhdGEtbm90aWZsaXN0LWxvYWRdJywgc2VsZi5fb25DbGlja0xvYWROb3RpZmljYXRpb24uYmluZChzZWxmKSlcbiAgICB9XG5cbiAgICAvKipcbiAgICAgKiBFdmVudCBoYW5kbGVyIGZvciBzaG93aW5nIGFuZCBoaWRpbmcgcG9wdXBcbiAgICAgKiBAcGFyYW0ge0V2ZW50fSBlIC0gRXZlbnQgT2JqZWN0XG4gICAgICogQHByaXZhdGVcbiAgICAgKi9cbiAgICBfb25DbGlja1RvZ2dsZVNob3coZSl7XG4gICAgICAgIHRoaXMudG9nZ2xlUG9wdXAoKTtcblxuICAgICAgICBpZiAoIXRoaXMuaXNMb2FkZWQpe1xuICAgICAgICAgICAgdGhpcy5pc0xvYWRlZCAgPSB0cnVlO1xuICAgICAgICAgICAgdGhpcy5sb2FkTm90aWZpY2F0aW9uKHRoaXMub2Zmc2V0KTtcbiAgICAgICAgfVxuXG4gICAgICAgIGUucHJldmVudERlZmF1bHQoKTtcbiAgICB9O1xuXG4gICAgLyoqXG4gICAgICogRXZlbnQgaGFuZGxlciBmb3IgdGhlIGJ1dHRvbiBcImxvYWQgbW9yZSBub3RpZmljYXRpb25cIlxuICAgICAqIEBwYXJhbSB7RXZlbnR9IGUgLSBFdmVudCBPYmplY3RcbiAgICAgKiBAcHJpdmF0ZVxuICAgICAqL1xuICAgIF9vbkNsaWNrTG9hZE5vdGlmaWNhdGlvbihlKXtcbiAgICAgICAgZS5wcmV2ZW50RGVmYXVsdCgpO1xuXG4gICAgICAgIHRoaXMubG9hZE5vdGlmaWNhdGlvbih0aGlzLm9mZnNldCk7XG4gICAgfTtcblxuICAgIC8qKlxuICAgICAqIEFkZCB0byB0aGUgZG9tXG4gICAgICogQHBhcmFtIHtOb3RpZmljYXRpb25JdGVtW119IG5vdGlmTGlzdCAtIGxpc3Qgb2YgdGhlIG5vdGlmaWNhdGlvblxuICAgICAqIEBwcml2YXRlXG4gICAgICovXG4gICAgX3JlbmRlck5vdGlmaWNhdGlvbihub3RpZkxpc3Qpe1xuICAgICAgICBjb25zdCBzZWxmID0gdGhpcztcblxuICAgICAgICBub3RpZkxpc3QuZm9yRWFjaChmdW5jdGlvbihpdGVtKXtcbiAgICAgICAgICAgICQoaXRlbS5ib2R5KVxuICAgICAgICAgICAgICAgIC5hZGRDbGFzcyhpdGVtLnR5cGUpXG4gICAgICAgICAgICAgICAgLnRvZ2dsZUNsYXNzKCdpcy1uZXcnLCBCb29sZWFuKGl0ZW0udW5yZWFkKSlcbiAgICAgICAgICAgICAgICAuZGF0YSgnaWQnLCBpdGVtLmlkKVxuICAgICAgICAgICAgICAgIC5hcHBlbmRUbyhzZWxmLmxvY2Fscy4kbGlzdCk7XG4gICAgICAgIH0pXG4gICAgfVxuXG4gICAgLyoqXG4gICAgICogRmlsdGVyIG5vdGlmaWNhdGlvbiBhbmQgcmV0dXJuIG9ubHkgbmV3XG4gICAgICogQHBhcmFtIHtOb3RpZmljYXRpb25JdGVtW119IG5vdGlmTGlzdCAtIGxpc3Qgb2YgdGhlIG5vdGlmaWNhdGlvblxuICAgICAqIEByZXR1cm5zIHtOb3RpZmljYXRpb25JdGVtW119IG5vdGlmTGlzdCAtIG9ubHkgbmV3IG5vdGlmaWNhdGlvbnNcbiAgICAgKiBAcHJpdmF0ZVxuICAgICAqL1xuICAgIF9maWx0ZXJPbmx5TmV3KG5vdGlmTGlzdCl7XG4gICAgICAgIHJldHVybiBub3RpZkxpc3QuZmlsdGVyKGZ1bmN0aW9uKGl0ZW0pe1xuICAgICAgICAgICAgcmV0dXJuIGl0ZW0udW5yZWFkO1xuICAgICAgICB9KVxuICAgIH07XG5cbiAgICBfaXNIYXZlTm90aWZpY2F0aW9uKG5vdGlmTGlzdCl7XG4gICAgICAgIGlmICghbm90aWZMaXN0Lmxlbmd0aCAmJiAhdGhpcy5vZmZzZXQpIHtcbiAgICAgICAgICAgIHRoaXMuJHJvb3QuYWRkQ2xhc3MoJ2Itbm90aWZsaXN0X2VtcHR5Jyk7XG4gICAgICAgICAgICByZXR1cm4gZmFsc2U7XG4gICAgICAgIH1cblxuICAgICAgICBpZiAobm90aWZMaXN0Lmxlbmd0aCA8IDUpe1xuICAgICAgICAgICAgdGhpcy4kcm9vdC5hZGRDbGFzcygnYi1ub3RpZmxpc3RfbG9hZF9hbGwnKTtcbiAgICAgICAgfVxuICAgICAgICByZXR1cm4gdHJ1ZTtcbiAgICB9XG5cbiAgICAvKipcbiAgICAgKiBHZXQgbmV3IGxvYWQgbm90aWZpY2F0aW9uIGFuZCByZW5kZXIgdGhlbVxuICAgICAqIEBwYXJhbSB7TnVtYmVyfSBvZmZzZXQgLVxuICAgICAqL1xuICAgIGxvYWROb3RpZmljYXRpb24ob2Zmc2V0KXtcbiAgICAgICAgY29uc3Qgc2VsZiA9IHRoaXM7XG5cbiAgICAgICAgc2VsZi5fcmVjaWV2ZU5vdGlmaWNhdGlvbihvZmZzZXQpXG4gICAgICAgICAgICAuZG9uZShmdW5jdGlvbihub3RpZkxpc3Qpe1xuICAgICAgICAgICAgICAgIGlmICghc2VsZi5faXNIYXZlTm90aWZpY2F0aW9uKG5vdGlmTGlzdCkpIHJldHVybjtcblxuICAgICAgICAgICAgICAgIHNlbGYub2Zmc2V0ICs9IG5vdGlmTGlzdC5sZW5ndGg7XG4gICAgICAgICAgICAgICAgc2VsZi5fcmVuZGVyTm90aWZpY2F0aW9uKG5vdGlmTGlzdCk7XG5cbiAgICAgICAgICAgICAgICB2YXIgbmV3Tm90aWZMaXN0ID0gc2VsZi5fZmlsdGVyT25seU5ldyhub3RpZkxpc3QpO1xuXG4gICAgICAgICAgICAgICAgaWYgKCFuZXdOb3RpZkxpc3QubGVuZ3RoKSByZXR1cm47XG5cbiAgICAgICAgICAgICAgICBzZWxmLl9zZW5kVmlld2VkTmV3Tm90aWYobmV3Tm90aWZMaXN0KTtcbiAgICAgICAgICAgICAgICBzZWxmLnNldFVucmVhZENvdW50KHNlbGYudW5yZWFkQ291bnQgLSBuZXdOb3RpZkxpc3QubGVuZ3RoKTtcbiAgICAgICAgICAgIH0pO1xuICAgIH1cblxuICAgIC8qKlxuICAgICAqIFNldCB1bnJlYWQgY291bnRcbiAgICAgKiBAcGFyYW0ge051bWJlcn0gY291bnQgLSBuZXcgdmFsdWUgZm9yIHVucmVhZCBjb3VudFxuICAgICAqL1xuICAgIHNldFVucmVhZENvdW50KGNvdW50KXtcbiAgICAgICAgdGhpcy51bnJlYWRDb3VudCA9IChjb3VudCA+IDApPyBjb3VudDogMDtcblxuICAgICAgICB0aGlzLmxvY2Fscy4kY291bnQudGV4dCh0aGlzLnVucmVhZENvdW50KTtcbiAgICAgICAgdGhpcy4kcm9vdC50b2dnbGVDbGFzcygnYi1ub3RpZmxpc3RfaGF2ZV9ub3RpZmljYXRpb24nLCBCb29sZWFuKHRoaXMudW5yZWFkQ291bnQpKTtcbiAgICB9XG5cbiAgICBzaG93UG9wdXAoKXtcbiAgICAgICAgaWYgKHRoaXMuaXNWaXNpYmxlKSByZXR1cm47XG5cbiAgICAgICAgdGhpcy5pc1Zpc2libGUgPSB0cnVlO1xuICAgICAgICB0aGlzLiRyb290LmFkZENsYXNzKCdiLW5vdGlmbGlzdF9zaG93Jyk7XG4gICAgfVxuXG4gICAgaGlkZVBvcHVwKCl7XG4gICAgICAgIGlmICghdGhpcy5pc1Zpc2libGUpIHJldHVybjtcblxuICAgICAgICB0aGlzLmlzVmlzaWJsZSA9IGZhbHNlO1xuICAgICAgICB0aGlzLiRyb290LnJlbW92ZUNsYXNzKCdiLW5vdGlmbGlzdF9zaG93Jyk7XG4gICAgfVxuXG4gICAgdG9nZ2xlUG9wdXAoKXtcbiAgICAgICAgdGhpcy5pc1Zpc2libGU/IHRoaXMuaGlkZVBvcHVwKCk6IHRoaXMuc2hvd1BvcHVwKCk7XG4gICAgfVxuXG4gICAgLy8gdHJhbnNwb3J0XG4gICAgX3JlY2lldmVVbnJlYWRDb3VudCgpe1xuICAgICAgICBsZXQgZGVmZXIgPSAkLkRlZmVycmVkKCk7XG4gICAgICAgIGNvbnN0IHVybCA9IGpzUm91dGVzLmNvbnRyb2xsZXJzLmNvcmUuTm90aWZpY2F0aW9ucy51bnJlYWQoKS51cmw7XG5cbiAgICAgICAgJC5nZXQodXJsLCBmdW5jdGlvbiAoZGF0YSkge1xuICAgICAgICAgICAgdmFyIGNvdW50ID0gKCQucGFyc2VKU09OKGRhdGEpKS51bnJlYWQ7XG4gICAgICAgICAgICBkZWZlci5yZXNvbHZlKGNvdW50KTtcbiAgICAgICAgfSk7XG5cbiAgICAgICAgcmV0dXJuIGRlZmVyLnByb21pc2UoKTtcbiAgICB9XG5cbiAgICBfcmVjaWV2ZU5vdGlmaWNhdGlvbihvZmZzZXQpe1xuICAgICAgICBsZXQgZGVmZXIgPSAkLkRlZmVycmVkKCk7XG4gICAgICAgIGNvbnN0IGxpbWl0ID0gNTtcbiAgICAgICAgY29uc3QgdXJsID0ganNSb3V0ZXMuY29udHJvbGxlcnMuY29yZS5Ob3RpZmljYXRpb25zLmxpc3Qob2Zmc2V0LCBsaW1pdCkudXJsO1xuXG4gICAgICAgICQuZ2V0KHVybCwgZnVuY3Rpb24oZGF0YSl7XG4gICAgICAgICAgICB2YXIgbm90aWZMaXN0ID0gJC5wYXJzZUpTT04oZGF0YSlbMF07XG5cbiAgICAgICAgICAgIGRlZmVyLnJlc29sdmUobm90aWZMaXN0KTtcbiAgICAgICAgfSk7XG5cbiAgICAgICAgcmV0dXJuIGRlZmVyLnByb21pc2UoKTtcbiAgICB9XG5cbiAgICAvKipcbiAgICAgKiBTZW5kIHRvIHRoZSBzZXJ2ZXIgaWQgb2YgdGhlIHZpZXdlZCBub3RpZmljYXRpb25cbiAgICAgKiBAcGFyYW0ge05vdGlmaWNhdGlvbkl0ZW1bXX0gbm90aWZMaXN0XG4gICAgICogQHByaXZhdGVcbiAgICAgKi9cbiAgICBfc2VuZFZpZXdlZE5ld05vdGlmKG5vdGlmTGlzdCl7XG4gICAgICAgIGxldCBpZHMgPSBbXTtcbiAgICAgICAgY29uc3QgdXJsID0ganNSb3V0ZXMuY29udHJvbGxlcnMuY29yZS5Ob3RpZmljYXRpb25zLnJlYWQoKS51cmw7XG5cbiAgICAgICAgbm90aWZMaXN0LmZvckVhY2goZnVuY3Rpb24oaXRlbSl7XG4gICAgICAgICAgICBpZHMucHVzaChpdGVtLmlkKTtcbiAgICAgICAgfSk7XG5cbiAgICAgICAgJC5wb3N0KHVybCwge2lkczogaWRzfSlcbiAgICB9O1xuXG4gIFxuICAgIC8vIHN0YXRpY1xuICAgIHN0YXRpYyBwbHVnaW4oc2VsZWN0b3IpIHtcbiAgICAgICAgY29uc3QgJGVsZW1zID0gJChzZWxlY3Rvcik7XG4gICAgICAgIGlmICghJGVsZW1zLmxlbmd0aCkgcmV0dXJuO1xuXG4gICAgICAgIHJldHVybiAkZWxlbXMuZWFjaChmdW5jdGlvbiAoaW5kZXgsIGVsKSB7XG4gICAgICAgICAgICBsZXQgJGVsZW1lbnQgPSAkKGVsKTtcbiAgICAgICAgICAgIGxldCBkYXRhICAgICA9ICRlbGVtZW50LmRhdGEoJ3dpZGdldC5zY3JvbGx0bycpO1xuXG4gICAgICAgICAgICBpZiAoIWRhdGEpIHtcbiAgICAgICAgICAgICAgICBkYXRhID0gbmV3IFdpZGdldChlbCk7XG4gICAgICAgICAgICAgICAgJGVsZW1lbnQuZGF0YSgnd2lkZ2V0JywgZGF0YSk7XG4gICAgICAgICAgICB9XG4gICAgICAgIH0pXG4gICAgfVxufVxuXG5cblxuLyoqIFdFQlBBQ0sgRk9PVEVSICoqXG4gKiogLi9mcm9udGVuZC9qcy9sYXlvdXQvX25vdGlmYXRpb24tbGlzdC5qc1xuICoqLyIsIid1c2Ugc3RyaWN0JztcblxuZXhwb3J0IGRlZmF1bHQgY2xhc3MgV2lkZ2V0IHtcblxuICAgIGNvbnN0cnVjdG9yKHNlbGVjdG9yLCBvcHRpb25zKSB7XG4gICAgICAgIGxldCBkZWZhdWx0T3B0aW9ucyA9IHtcbiAgICAgICAgICAgIHVybDoganNSb3V0ZXMuY29udHJvbGxlcnMuVXRpbGl0aWVzLm1hcmtkb3duKCkudXJsLFxuICAgICAgICAgICAgaW50ZXJ2YWw6IDEwMDAsXG4gICAgICAgICAgICBtYXJrZG93bnBvc2l0aW9uOiBcImJvZHlcIixcbiAgICAgICAgICAgIHRlbXBsYXRlOiBcIjxkaXYgY2xhc3M9J3BvcG92ZXItYmwnPjxpIGNsYXNzPSdmYSBmYS1zcGlubmVyIGZhLXNwaW4nPjwvaT48ZGl2IGNsYXNzPSdwb3BvdmVyLWJsX19jb250ZW50JyBkYXRhLWNvbnRlbnQ+PC9kaXY+PC9kaXY+XCJcbiAgICAgICAgfTtcblxuICAgICAgICB0aGlzLiRyb290ID0gJChzZWxlY3Rvcik7XG4gICAgICAgIHRoaXMub3B0aW9ucyA9ICQuZXh0ZW5kKHt9LCBkZWZhdWx0T3B0aW9ucywgb3B0aW9ucyk7XG5cbiAgICAgICAgdGhpcy5yZXNldFN0YXRlKCk7XG4gICAgICAgIHRoaXMuYXNzaWduRXZlbnRzKCk7XG4gICAgfVxuXG4gICAgcmVzZXRTdGF0ZSgpe1xuICAgICAgICB0aGlzLnNlbmRpbmcgPSBudWxsO1xuICAgICAgICB0aGlzLnNlbmRpbmcgPSBudWxsO1xuICAgICAgICB0aGlzLmlzTmVlZFVwZGF0aW5nID0gbnVsbDtcbiAgICAgICAgdGhpcy53YWl0aW5nVGltZXIgPSBudWxsXG4gICAgfVxuXG4gICAgY3JlYXRlUG9wb3Zlcigpe1xuICAgICAgICB0aGlzLiRwb3BvdmVyID0gJCh0aGlzLm9wdGlvbnMudGVtcGxhdGUpO1xuXG4gICAgICAgIGlmICh0aGlzLm9wdGlvbnMubWFya2Rvd25jbGFzcyl7XG4gICAgICAgICAgICB0aGlzLiRwb3BvdmVyLmFkZENsYXNzKHRoaXMub3B0aW9ucy5tYXJrZG93bmNsYXNzKTtcbiAgICAgICAgfVxuXG4gICAgICAgIGlmICh0aGlzLm9wdGlvbnMubWFya2Rvd25wb3NpdGlvbiA9PSBcImJvZHlcIil7XG4gICAgICAgICAgICB0aGlzLiRwb3BvdmVyLmFwcGVuZFRvKCdib2R5Jyk7XG4gICAgICAgIH0gZWxzZSB7XG4gICAgICAgICAgICB0aGlzLiRwb3BvdmVyLmluc2VydEFmdGVyKHRoaXMuJHJvb3QpO1xuICAgICAgICB9XG4gICAgfVxuXG4gICAgZ2V0UG9zaXRpb24oKXtcbiAgICAgICAgY29uc3QgJHJvb3QgPSB0aGlzLiRyb290O1xuICAgICAgICBsZXQgb2Zmc2V0Qm9keSA9IHtcbiAgICAgICAgICAgICAgICB0b3A6ICRyb290Lm9mZnNldCgpLnRvcCxcbiAgICAgICAgICAgICAgICBsZWZ0OiAkcm9vdC5vZmZzZXQoKS5sZWZ0ICsgJHJvb3Qub3V0ZXJXaWR0aCgpXG4gICAgICAgICAgICB9O1xuXG4gICAgICAgIHJldHVybiB0aGlzLm9wdGlvbnMubWFya2Rvd25wb3NpdGlvbiA9PSBcImJvZHlcIj8gb2Zmc2V0Qm9keToge307XG4gICAgfVxuXG4gICAgYXNzaWduRXZlbnRzICgpe1xuICAgICAgICBjb25zdCBzZWxmID0gdGhpcztcblxuICAgICAgICBzZWxmLiRyb290XG4gICAgICAgICAgICAub24oJ2ZvY3VzJywgZnVuY3Rpb24oKXtcbiAgICAgICAgICAgICAgICBzZWxmLnRvZ2dsZSgpO1xuICAgICAgICAgICAgICAgIHNlbGYuY29tcGlsZUNvbnRlbnQoKTtcbiAgICAgICAgICAgIH0pXG4gICAgICAgICAgICAub24oJ2tleXVwJywgZnVuY3Rpb24oZSl7XG4gICAgICAgICAgICAgICAgaWYgKCFzZWxmLmlzS2V5VHJpZ2dlcihlLndoaWNoKSkgcmV0dXJuIHRydWU7XG5cbiAgICAgICAgICAgICAgICBzZWxmLnBhdXNpbmcgJiYgKHNlbGYuaXNOZWVkVXBkYXRpbmcgPSB0cnVlKTtcbiAgICAgICAgICAgICAgICBzZWxmLmNvbXBpbGVDb250ZW50KCk7XG4gICAgICAgICAgICB9KVxuICAgICAgICAgICAgLm9uKCdibHVyJywgZnVuY3Rpb24oKXtcbiAgICAgICAgICAgICAgICBzZWxmLnJlc2V0U3RhdGUoKTtcbiAgICAgICAgICAgICAgICBzZWxmLnRvZ2dsZSgpO1xuICAgICAgICAgICAgfSk7XG5cbiAgICAgICAgJCh3aW5kb3cpLnJlc2l6ZShmdW5jdGlvbiAoKSB7XG4gICAgICAgICAgICBzZWxmLiRwb3BvdmVyICYmIHNlbGYuJHBvcG92ZXIuY3NzKHNlbGYuZ2V0UG9zaXRpb24oKSk7XG4gICAgICAgIH0pO1xuICAgIH1cblxuICAgIGNvbXBpbGVDb250ZW50KGNvbnRlbnQpe1xuICAgICAgICB2YXIgc2VsZiA9IHRoaXM7XG4gICAgICAgIGNvbnRlbnQgPSBjb250ZW50IHx8IHNlbGYuJHJvb3QudmFsKCk7XG5cbiAgICAgICAgaWYgKHNlbGYuc2VuZGluZyB8fCBzZWxmLnBhdXNpbmcpIHJldHVybjtcblxuICAgICAgICBzZWxmLnBhdXNpbmcgPSB0cnVlO1xuICAgICAgICBzZWxmLnNldFNlbmRpbmcoKTtcbiAgICAgICAgc2VsZi5zZXRXYWl0aW5nKCk7XG5cbiAgICAgICAgJC5wb3N0KHNlbGYub3B0aW9ucy51cmwsIHtcbiAgICAgICAgICAgIGRhdGE6IGNvbnRlbnRcbiAgICAgICAgfSwgZnVuY3Rpb24oZGF0YSl7XG4gICAgICAgICAgICBzZWxmLnNldFNlbmRlZCgpO1xuICAgICAgICAgICAgc2VsZi4kcG9wb3Zlci5maW5kKCdbZGF0YS1jb250ZW50XScpLmh0bWwoZGF0YSk7XG4gICAgICAgIH0pO1xuICAgIH1cblxuICAgIHNldFNlbmRpbmcoKXtcbiAgICAgICAgdGhpcy5zZW5kaW5nID0gdHJ1ZTtcbiAgICAgICAgdGhpcy4kcG9wb3Zlci5hZGRDbGFzcygncG9wb3Zlci1ibF9sb2FkaW5nJyk7XG4gICAgfVxuXG4gICAgc2V0U2VuZGVkKCl7XG4gICAgICAgIHRoaXMuc2VuZGluZyA9IGZhbHNlO1xuICAgICAgICB0aGlzLiRwb3BvdmVyLnJlbW92ZUNsYXNzKCdwb3BvdmVyLWJsX2xvYWRpbmcnKTtcbiAgICB9XG5cbiAgICBzZXRXYWl0aW5nKCl7XG4gICAgICAgIHZhciBzZWxmID0gdGhpcztcblxuICAgICAgICBzZWxmLndhaXRpbmdUaW1lciA9IHNldFRpbWVvdXQoXG4gICAgICAgICAgICBmdW5jdGlvbiAoKSB7XG4gICAgICAgICAgICAgICAgc2VsZi5wYXVzaW5nID0gZmFsc2U7XG4gICAgICAgICAgICAgICAgaWYgKHNlbGYuaXNOZWVkVXBkYXRpbmcpIHtcbiAgICAgICAgICAgICAgICAgICAgc2VsZi5pc05lZWRVcGRhdGluZyA9IGZhbHNlO1xuICAgICAgICAgICAgICAgICAgICBzZWxmLmNvbXBpbGVDb250ZW50KCk7XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgfSwgc2VsZi5vcHRpb25zLmludGVydmFsKTtcbiAgICB9XG5cbiAgICBzaG93KCl7XG4gICAgICAgIGlmICggIXRoaXMuJHBvcG92ZXIpIHRoaXMuY3JlYXRlUG9wb3ZlcigpO1xuICAgICAgICB0aGlzLiRwb3BvdmVyLmNzcyh0aGlzLmdldFBvc2l0aW9uKCkpO1xuXG4gICAgICAgIGlmICh0aGlzLmlzVmlzaWJsZSkgcmV0dXJuO1xuXG4gICAgICAgIHRoaXMuaXNWaXNpYmxlID0gdHJ1ZTtcbiAgICAgICAgdGhpcy4kcG9wb3Zlci5hZGRDbGFzcygncG9wb3Zlci1ibF9zaG93Jyk7XG4gICAgfVxuXG4gICAgaGlkZSgpe1xuICAgICAgICBpZiAoIXRoaXMuaXNWaXNpYmxlKSByZXR1cm47XG5cbiAgICAgICAgdGhpcy5pc1Zpc2libGUgPSBmYWxzZTtcbiAgICAgICAgdGhpcy4kcG9wb3Zlci5yZW1vdmVDbGFzcygncG9wb3Zlci1ibF9zaG93Jyk7XG4gICAgfVxuXG4gICAgdG9nZ2xlKCl7XG4gICAgICAgIHRoaXNbdGhpcy5pc1Zpc2libGU/ICdoaWRlJzogJ3Nob3cnXSgpO1xuICAgIH1cblxuICAgIGlzS2V5VHJpZ2dlcihjb2RlKXtcbiAgICAgICAgcmV0dXJuIChjb2RlID49IDQ1ICYmIGNvZGUgPD0gOTApIHx8IChjb2RlID49IDE4NikgJiYgKGNvZGUgPD0gMjIyKSB8fCAoY29kZSA9PSAxMykgfHwgKGNvZGUgPT0gMjcpIHx8IChjb2RlID09IDMyKSB8fCAoY29kZSA9PSA4KTtcbiAgICB9XG5cblxuXG4gICAgLy8gc3RhdGljXG4gICAgc3RhdGljIHBsdWdpbihzZWxlY3Rvciwgb3B0aW9ucykge1xuICAgICAgICBjb25zdCAkZWxlbXMgPSAkKHNlbGVjdG9yKTtcbiAgICAgICAgaWYgKCEkZWxlbXMubGVuZ3RoKSByZXR1cm47XG5cbiAgICAgICAgcmV0dXJuICRlbGVtcy5lYWNoKGZ1bmN0aW9uIChpbmRleCwgZWwpIHtcbiAgICAgICAgICAgIGxldCAkZWxlbWVudCA9ICQoZWwpO1xuICAgICAgICAgICAgbGV0IGRhdGEgICAgID0gJGVsZW1lbnQuZGF0YSgnd2lkZ2V0LnByZXZpZXcubWFya2Rvd24nKTtcblxuICAgICAgICAgICAgaWYgKCFkYXRhKSB7XG4gICAgICAgICAgICAgICAgZGF0YSA9IG5ldyBXaWRnZXQoZWwsIG9wdGlvbnMpO1xuICAgICAgICAgICAgICAgICRlbGVtZW50LmRhdGEoJ3dpZGdldCcsIGRhdGEpO1xuICAgICAgICAgICAgfVxuICAgICAgICB9KVxuICAgIH1cbn1cblxuXG5cblxuLyoqIFdFQlBBQ0sgRk9PVEVSICoqXG4gKiogLi9mcm9udGVuZC9qcy9sYXlvdXQvX3ByZXZpZXctbWFya2Rvd24uanNcbiAqKi8iXSwic291cmNlUm9vdCI6IiJ9