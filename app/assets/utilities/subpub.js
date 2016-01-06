/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2014, Happy Melly http://www.happymelly.com
 *
 * This file is part of the Happy Melly Teller.
 *
 * Happy Melly Teller is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Happy Melly Teller is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Happy Melly Teller.  If not, see <http://www.gnu.org/licenses/>.
 *
 * If you have questions concerning this license or the applicable additional terms, you may contact
 * by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */


(function(App) {
    "use strict";

    // custom events
    App.events =  App.E || {
            subscribers: {},
            alreadyPublished: {},

            /**
             *
             * @param types
             * @param fn
             * @param context
             * @param once
             * @return {*}
             */
            sub: function (events, fn, context, once, runIfAlreadyPublished) {
                var self = this;
                (this.sanitizeEvents(events)).forEach(function(event){

                    var subscriberObject = {
                        fn: fn,
                        context: context,
                        once: once
                    };

                    if (runIfAlreadyPublished && this.alreadyPublished[event]) {
                        this.executeSubscriber(subscriberObject, null);

                        if (once) {
                            return;
                        }
                    }

                    (this.subscribers[event] = this.subscribers[event] || []).push(subscriberObject);
                }, this);

                return this;
            },

            /**
             * Add subscriber on events
             *
             * @param types
             * @param fn
             * @param context
             * @return this App.events
             */
            subOnce: function (types, fn, context) {
                return this.sub(types, fn, context, true);
            },

            /**
             * Delete subscribe
             *
             * @param types
             * @param fn callback
             * @return this App.events
             */
            unSub: function (types, fn, context) {
                this.sanitizeEvents(types).forEach(function (type) {
                    (this.subscribers[type] = this.subscribers[type] || []).forEach(function (subscriber, index) {
                        if (subscriber.fn === fn || subscriber.context && subscriber.context == context) {
                            this.subscribers[type].splice(index, 1);
                            return false;
                        }
                    }, this);
                }, this);

                return this;
            },

            /**
             * Delete all subscribers
             *
             * @param types
             * @return this App.events
             */
            unSubAll: function (types) {
                this.sanitizeEvents(types).forEach(function (type) {
                    (this.subscribers[type] = this.subscribers[type] || []).forEach(function (subscriber, index) {
                        this.subscribers[type] = [];
                        return false;
                    }, this);
                }, this);

                return this;
            },

            /**
             * Run event. The first parameter - events, other - parameters for  unction
             *
             * @param events события через пробел
             * @return {*}
             */
            pub: function(events/*, arg1, arg2, arg3*/) {
                var argsArr = this.argsToArray(arguments).slice(1);

                this.sanitizeEvents(events).forEach(function (event) {
                    this.subscribers[event] = this.subscribers[event] || [];
                    this.alreadyPublished[event] = true;

                    for(var index = 0, size = this.subscribers[event].length; index < size; index++) {
                        var subscriber = this.subscribers[event][index];

                        this.executeSubscriber(subscriber, argsArr);

                        if (subscriber.once) {
                            this.subscribers[event].splice(index, 1);
                            index--;
                            size--;
                        }
                    }

                }, this);

                return this;
            },

            executeSubscriber: function (subscriber, argsArr) {
                subscriber.fn.apply(subscriber.context, argsArr);
            },


            sanitizeEvents: function (events) {
                events = events.split(' ');
                if (events.length === 0) {
                    events = ['*'];
                }
                return events;
            },

            argsToArray: function (args) {
                var arr = [];
                for (var i = 0; typeof(args[i]) != "undefined"; i++) {
                    arr.push(args[i]);
                }
                return arr;
            }
        };
})(App);