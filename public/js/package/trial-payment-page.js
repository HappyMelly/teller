!function(t){function e(r){if(n[r])return n[r].exports;var o=n[r]={exports:{},id:r,loaded:!1};return t[r].call(o.exports,o,o.exports,e),o.loaded=!0,o.exports}var n={};return e.m=t,e.c=n,e.p="",e(0)}([function(t,e,n){t.exports=n(70)},,,function(t,e){"use strict";e.__esModule=!0,e["default"]=function(t,e){if(!(t instanceof e))throw new TypeError("Cannot call a class as a function")}},function(t,e,n){"use strict";function r(t){return t&&t.__esModule?t:{"default":t}}e.__esModule=!0;var o=n(5),a=r(o);e["default"]=function(){function t(t,e){for(var n=0;n<e.length;n++){var r=e[n];r.enumerable=r.enumerable||!1,r.configurable=!0,"value"in r&&(r.writable=!0),(0,a["default"])(t,r.key,r)}}return function(e,n,r){return n&&t(e.prototype,n),r&&t(e,r),e}}()},function(t,e,n){t.exports={"default":n(6),__esModule:!0}},function(t,e,n){n(7);var r=n(10).Object;t.exports=function(t,e,n){return r.defineProperty(t,e,n)}},function(t,e,n){var r=n(8);r(r.S+r.F*!n(18),"Object",{defineProperty:n(14).f})},function(t,e,n){var r=n(9),o=n(10),a=n(11),i=n(13),u="prototype",c=function(t,e,n){var f,s,l,p=t&c.F,d=t&c.G,v=t&c.S,y=t&c.P,m=t&c.B,h=t&c.W,_=d?o:o[e]||(o[e]={}),$=_[u],b=d?r:v?r[e]:(r[e]||{})[u];d&&(n=e);for(f in n)s=!p&&b&&void 0!==b[f],s&&f in _||(l=s?b[f]:n[f],_[f]=d&&"function"!=typeof b[f]?n[f]:m&&s?a(l,r):h&&b[f]==l?function(t){var e=function(e,n,r){if(this instanceof t){switch(arguments.length){case 0:return new t;case 1:return new t(e);case 2:return new t(e,n)}return new t(e,n,r)}return t.apply(this,arguments)};return e[u]=t[u],e}(l):y&&"function"==typeof l?a(Function.call,l):l,y&&((_.virtual||(_.virtual={}))[f]=l,t&c.R&&$&&!$[f]&&i($,f,l)))};c.F=1,c.G=2,c.S=4,c.P=8,c.B=16,c.W=32,c.U=64,c.R=128,t.exports=c},function(t,e){var n=t.exports="undefined"!=typeof window&&window.Math==Math?window:"undefined"!=typeof self&&self.Math==Math?self:Function("return this")();"number"==typeof __g&&(__g=n)},function(t,e){var n=t.exports={version:"2.2.0"};"number"==typeof __e&&(__e=n)},function(t,e,n){var r=n(12);t.exports=function(t,e,n){if(r(t),void 0===e)return t;switch(n){case 1:return function(n){return t.call(e,n)};case 2:return function(n,r){return t.call(e,n,r)};case 3:return function(n,r,o){return t.call(e,n,r,o)}}return function(){return t.apply(e,arguments)}}},function(t,e){t.exports=function(t){if("function"!=typeof t)throw TypeError(t+" is not a function!");return t}},function(t,e,n){var r=n(14),o=n(22);t.exports=n(18)?function(t,e,n){return r.f(t,e,o(1,n))}:function(t,e,n){return t[e]=n,t}},function(t,e,n){var r=n(15),o=n(17),a=n(21),i=Object.defineProperty;e.f=n(18)?Object.defineProperty:function(t,e,n){if(r(t),e=a(e,!0),r(n),o)try{return i(t,e,n)}catch(u){}if("get"in n||"set"in n)throw TypeError("Accessors not supported!");return"value"in n&&(t[e]=n.value),t}},function(t,e,n){var r=n(16);t.exports=function(t){if(!r(t))throw TypeError(t+" is not an object!");return t}},function(t,e){t.exports=function(t){return"object"==typeof t?null!==t:"function"==typeof t}},function(t,e,n){t.exports=!n(18)&&!n(19)(function(){return 7!=Object.defineProperty(n(20)("div"),"a",{get:function(){return 7}}).a})},function(t,e,n){t.exports=!n(19)(function(){return 7!=Object.defineProperty({},"a",{get:function(){return 7}}).a})},function(t,e){t.exports=function(t){try{return!!t()}catch(e){return!0}}},function(t,e,n){var r=n(16),o=n(9).document,a=r(o)&&r(o.createElement);t.exports=function(t){return a?o.createElement(t):{}}},function(t,e,n){var r=n(16);t.exports=function(t,e){if(!r(t))return t;var n,o;if(e&&"function"==typeof(n=t.toString)&&!r(o=n.call(t)))return o;if("function"==typeof(n=t.valueOf)&&!r(o=n.call(t)))return o;if(!e&&"function"==typeof(n=t.toString)&&!r(o=n.call(t)))return o;throw TypeError("Can't convert object to primitive value")}},function(t,e){t.exports=function(t,e){return{enumerable:!(1&t),configurable:!(2&t),writable:!(4&t),value:e}}},,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,function(t,e,n){"use strict";function r(t){return t&&t.__esModule?t:{"default":t}}var o=n(71),a=r(o);$(function(){a["default"].plugin(".js-payment-form")})},function(t,e,n){"use strict";function r(t){return t&&t.__esModule?t:{"default":t}}Object.defineProperty(e,"__esModule",{value:!0});var o=n(3),a=r(o),i=n(4),u=r(i),c=function(){function t(e){(0,a["default"])(this,t);var n=this;n.$root=$(e),n.locals=n._getDom(),n.apikey=n.$root.data("apikey"),n._getStripeScript().done(function(){Stripe.setPublishableKey(n.apikey),n._init(),n._updateAmount(),n._assignEvents()})}return(0,u["default"])(t,[{key:"_getStripeScript",value:function(){return $.ajax({url:"https://js.stripe.com/v2/",dataType:"script"})}},{key:"_getDom",value:function(){var t=this.$root;return{$inputNumber:t.find("[data-card-number]"),$inputName:t.find("[data-card-name]"),$inputMonth:t.find("[data-card-month]"),$inputYear:t.find("[data-card-year]"),$inputCVC:t.find("[data-card-cvc]"),$amount:t.find("[data-payment-amount]"),$tax:t.find("[data-payment-tax]"),$submit:t.find("[data-payment-submit]"),$payPlace:t.find("[data-payment-price]")}}},{key:"_init",value:function(){return $.fn.payment?(this.locals.$inputNumber.payment("formatCardNumber"),void this.locals.$inputCVC.payment("formatCardCVC")):void console.log("There is no payment plugin on this page")}},{key:"_assignEvents",value:function(){var t=this;t.$root.on("keyup","input",function(){t._removeError($(this))}).on("change paste keyup","[data-card-name]",function(t){var e=$(this);e.val(e.val().toUpperCase())}).on("submit",t._onSubmitHandler.bind(t))}},{key:"_updateAmount",value:function(){var t=this.locals,e=parseFloat(t.$amount.text()),n=parseFloat(t.$tax.text()),r=e+n;t.$amount.text(e.toFixed(2)),t.$tax.text(n.toFixed(2)),t.$payPlace.text(r.toFixed(2))}},{key:"_setError",value:function(t){t.parent().addClass("has-error")}},{key:"_removeError",value:function(t){t.parent().removeClass("has-error")}},{key:"_disabledForm",value:function(){this.locals.$submit.prop("disabled",!0),$("body").css("cursor","progress")}},{key:"_enabledForm",value:function(){this.locals.$submit.prop("disabled",!1),$("body").css("cursor","default")}},{key:"_addTokenInput",value:function(t){var e=this.$root,n='<input type="hidden" value="'+t+'" name="token" />';e.find('input[name="token"]').remove(),e.append(n)}},{key:"_onSubmitHandler",value:function(t){var e=this;t.preventDefault(),this.isValidForm()&&(this._disabledForm(),Stripe.card.createToken(e.$root,e._stripeHandler.bind(e)))}},{key:"_stripeHandler",value:function(t,e){var n=this,r=void 0,o=void 0;e.error?n._enabledForm():(n._addTokenInput(e.id),r=this.$root.serialize(),n._sendFormData(r).done(function(t){if(t.hasOwnProperty("redirect"))window.location=t.redirect;else{var e="Internal error #2001. Your card has been charged. ";e+="Do not make payment again. Please proceed to your profile directly.",error(e,4500)}}).fail(function(t,e){"error"==e?(o=JSON.parse(t.responseText),error(o.message,4500)):error("Internal error #2000. Your card has not been charged. Please try again.",4500)}).complete(function(){n._enabledForm()}))}},{key:"_sendFormData",value:function(t){return $.ajax({type:"POST",url:this.$root.attr("action"),data:t})}},{key:"isValidForm",value:function(){var t=this,e=!0,n=this.locals,r=$.payment.validateCardNumber(n.$inputNumber.val()),o=$.payment.validateCardExpiry(n.$inputMonth.val(),n.$inputYear.val()),a=$.payment.validateCardCVC(n.$inputCVC.val()),i=+n.$inputName.val().length;return r||(t._setError(n.$inputNumber),e=!1),o||(t._setError(n.$inputMonth),t._setError(n.$inputYear),e=!1),a||(t._setError(n.$inputCVC),e=!1),i||(t._setError(n.$inputName),e=!1),e}}],[{key:"plugin",value:function(e,n){var r=$(e);if(r.length)return r.each(function(e,r){var o=$(r),a=o.data("widget.scrollto");a||(a=new t(r,n),o.data("widget",a))})}}]),t}();e["default"]=c}]);