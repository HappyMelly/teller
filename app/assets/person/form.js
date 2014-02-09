/*
 * Happy Melly Teller
 * Copyright (C) 2013, Happy Melly http://www.happymelly.com
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

function PhotoButton(name, state) {
    this.name = name;
    this.state = state;
}

PhotoButton.prototype.active = function() {
    return this.state
}

PhotoButton.prototype.deactivate = function() {
    if (this.state) {
        $('#' + this.name).removeClass('btn-danger').addClass('btn-success').text('Use profile photo');
        $('#photo').val('');
    }
    this.state = false;

}

PhotoButton.prototype.activate = function() {
    this.state = true;
    $('#' + this.name).addClass('btn-danger').removeClass('btn-success').text("Don't use profile photo");
    $('#photo').val(this.name);
}

function PhotoButtonGroup(buttons, active) {
    this.buttons = buttons;
    this.active = active;
    this.update();
}

PhotoButtonGroup.prototype.update = function() {
    for(var i = 0; i < this.buttons.length; i++) {
        this.buttons[i].deactivate();
    }
    for(var i = 0; i < this.buttons.length; i++) {
        if (this.buttons[i].name == this.active) {
            this.buttons[i].activate();
        }
    }
}
PhotoButtonGroup.prototype.click = function(name) {
    if (this.active == name) {
        this.active = '';
    } else {
        this.active = name;
    }
    this.update();
}

$(document).ready( function() {
    var buttons = [new PhotoButton("twitter", false), new PhotoButton("facebook", false)];
    var group = new PhotoButtonGroup(buttons, $('#photo').val());
    $(".photo-button").click(function(event) {
        event.preventDefault();
        group.click($(this).attr('id'));
    })
});

