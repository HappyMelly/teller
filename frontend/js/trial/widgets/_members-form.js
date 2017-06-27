/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2017, Happy Melly http://www.happymelly.com
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
 * If you have questions concerning this license or the applicable additional
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */

'use strict';

import InputChecking from './../../common/_input-checking';

/**
 * Form for adding new trial members
 * @param selector
 * @constructor
 */
export default class Widget {
    constructor(selector) {
        const self = this;

        self.$root = $(selector);
        self.locals = self._getDom();

        self.coupon = new InputChecking({
            $root: self.$root.find('.b-inputcheck'),
            url: jsRoutes.controllers.core.TrialCoupons.get
        });

        self._assignEvents();
    }

    _getDom() {
        const $root = this.$root;

        return {
            $inputCoupon: $root.find('[data-coupon]'),
            $members: $root.find('[data-members]'),
            $btnContinue: $root.find('[data-continue-btn]')
        }
    }

    _assignEvents() {
        const self = this;
        self.$root
            .on('click', '[data-add-member]', self._addMember.bind(self))
            .on('click', '[data-remove-member]', self._removeMember.bind(self))
            .on('change paste keyup', '[data-coupon]', function (e) {
                let $this = $(this);
                $this.val($this.val().toUpperCase())
            });

        App.events
            .sub('hmt.inputcheck.success', ()=> {
                self.locals.$btnContinue.removeProp('disabled');
            });
    }

    _addMember(e) {
        e.preventDefault();
        const self = this;

        let $child = self.locals.$members.find('[data-member]').last().clone();
        $child.find('input').val('');
        self.locals.$members.append($child);
        self._recalculateFieldIndex();
    }

    _removeMember(e) {
        e.preventDefault();
        const self = this;

        let block = $(e.currentTarget).parents('[data-member]');
        let blocksNumber = self.locals.$members.find('[data-member]').size();
        if (blocksNumber > 1) {
            $(block).remove();
        }
        self._recalculateFieldIndex();
    }

    _recalculateFieldIndex() {
        const self = this;

        let $members = self.locals.$members.find('[data-member]');
        $members.each(function (index, member) {
            $(member).find('[data_control]').each(function(childIndex, child) {
                let fullName = $(child).attr('name');
                if (fullName !== undefined) {
                    let prefix = fullName.split('[')[0];
                    let name = fullName.split('.')[1];
                    let nameWithCorrectIndex = prefix + '[' + (index + 1) + ']' + '.' + name;
                    $(child).attr('name', nameWithCorrectIndex);
                }
            });
        });
    }

    _setError($el) {
        $el.parent().addClass('has-error');
    }

    _removeError($el) {
        $el.parent().removeClass('has-error');
    }

    _disabledForm() {
        this.locals.$submit.prop('disabled', true);
        $("body").css("cursor", "progress");
    }

    _enabledForm() {
        this.locals.$submit.prop('disabled', false);
        $("body").css("cursor", "default");
    }

    _sendFormData(dataForm) {
        return $.ajax({
            type: "POST",
            url: this.$root.attr("action"),
            data: dataForm
        })
    };


    // static
    static plugin(selector, options) {
        const $elems = $(selector);
        if (!$elems.length) return;

        return $elems.each(function (index, el) {
            let $element = $(el);
            let data = $element.data('widget.newMembersForm');

            if (!data) {
                data = new Widget(el, options);
                $element.data('widget', data);
            }
        })
    }
}

