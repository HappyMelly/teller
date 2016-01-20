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
 *  Table row with badges
 */
(function ($, App) {
    'use strict';

    function TableRowWithBadges($el){
        this.$el = $el;
        this.$modal = $el.find('.js-row-modal');
        this.$modalContent = $el.find('.js-modalbadge-content');

        this.data = {
            brandId: $el.data('brand'),
            personId: $el.data('id')
        };

        this.assignEvents();
    }

    TableRowWithBadges.prototype.assignEvents= function(){
        var self = this;

        this.$el
            .on('click', '.js-row-badges', function (e) {
                self.getBadgesList(function(){
                    self.showPopup();
                });

                e.preventDefault();
            })
            .on('click', '.js-modal-save', function (e) {
                self.saveBadges();

                e.stopPropagation();
                e.preventDefault();
            })
            .on('hide.bs.modal', function(e){
                self.isModalVisible = false;

                e.stopPropagation();
            })
            .on('change', '.js-badge-input', function(e){
                var $this = $(this);

                self.toggleBadge($this);
            })
    };

    TableRowWithBadges.prototype.showPopup = function(){
        var self = this;

        if (self.isModalVisible) return;

        self.isModalVisible = true;
        self.$modal.modal('show');
    };

    TableRowWithBadges.prototype.hidePopup = function(){
        var self = this;

        if (!self.isModalVisible) return;

        self.isModalVisible = false;
        self.$modal.modal('hide');
    };

    TableRowWithBadges.prototype.getBadgesList = function(cb){
        var self = this,
            url = jsRoutes.controllers.Facilitators.details(this.data.personId, this.data.brandId).url;

        $.get(url).done(function (content) {
            self.$modalContent.html(content);

            cb && cb();
        });
    };

    TableRowWithBadges.prototype.prepareBadgesList = function(){
        var self = this,
            badges = [],
            $badges = self.$modalContent.find('.b-badge');

        $badges.each(function(index, el){
            var $el = $(el),
                $input = $el.find('.js-badge-input');

            if ($input.prop('checked')) {
                badges.push($input.data('id'));
            }
        });

        return badges;
    };

    TableRowWithBadges.prototype.saveBadges = function(){
        var self = this,
            arrBadges,
            url = jsRoutes.controllers.Facilitators.badges(this.data.personId, this.data.brandId).url;

        arrBadges = self.prepareBadgesList();

        $.post(
            url,
            {
                badges: arrBadges
            }, function (data) {
                self.hidePopup();
                success(data.message)
            }, "json");
    };


    TableRowWithBadges.prototype.toggleBadge = function($el) {
        var $root = $el.closest('.b-badge');

        $root.toggleClass('is-selected', $el.prop('checked'));
    };

    App.widgets.TableRowWithBadges = TableRowWithBadges;

})(jQuery, App);


