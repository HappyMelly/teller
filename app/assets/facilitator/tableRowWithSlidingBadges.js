/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2016, Happy Melly http://www.happymelly.com
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

/**
 *  Table row with sliding badges
 */
(function ($, App) {
    'use strict';

    function TableRowWithSlidingBadges($el){
        this.$el = $el;

        this.assignEvents();
    }

    TableRowWithSlidingBadges.prototype.assignEvents= function(){
        var self = this;

        this.$el
            .on('mouseenter', function(){
                var $this = $(this),
                    $badgeSlider = $this.find('.js-badge-slider');

                if ($badgeSlider.length){
                    $badgeSlider.carousel({
                        interval: 1500
                    });
                }
            })
            .on('mouseleave', function(){
                var $this = $(this),
                    $badgeSlider = $this.find('.js-badge-slider');

                if ($badgeSlider.length){
                    $badgeSlider.carousel('pause');
                }
            })
            .on('hmtUpdateBadgesList', function(){
                self.updateBadgesList();
            })
    };

    TableRowWithSlidingBadges.prototype.updateBadgesList = function(){
        var self = this,
            url = '/',
            $placeholder = self.$el.find('.js-row-badgeslist');

        if (!$placeholder.length) return;

        console.log('test');

        $.post(
            url,
            {}, function (data) {
                $placeholder.html(data);
            }, "json");
    };

    App.widgets.TableRowWithSlidingBadges = TableRowWithSlidingBadges;

})(jQuery, App);


