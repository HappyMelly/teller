/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2015, Happy Melly http://www.happymelly.com
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

function BadgeItem(el){
    this.el = el;
    this.$el = $(el);

    this.assignEvents();
}

BadgeItem.prototype.assignEvents = function(){
    var self = this;

    self.$el
        .on('click', '.js-badge-delete', function(e){
            var result,
                $this = $(this),
                $root = $this.closest('.b-badge');

            result = confirm("Remove this badge? You cannot undo this action");
            if (result) {
                 self.deleteBadge($root, $this.data('id'), $this.data('href'));
            }
            e.preventDefault();
        })
};

/**
 * Delete badge
 * @param {object} jquery element
 * @param {int} badgeId
 * @param {string} url
 */
BadgeItem.prototype.deleteBadge = function($root, badgeId, badgeLink){
    $.ajax({
        type: "DELETE",
        url:badgeLink,
        dataType: "json"
    }).done(function(data) {
        $root.remove();
        success("Badge is deleted")
    });
};

$(document).ready( function() {

    $('.js-badge-item').each(function(index, item){
        new BadgeItem(item);
    });
});