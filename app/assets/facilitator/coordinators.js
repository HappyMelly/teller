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

/**
 * Filter facilitators by license
 */
function filterByLicense(oSettings, aData, iDataIndex) {
    var type = $('.js-filter-link.active').data('type');
    switch(type) {
        case 'active':
            return aData[6] == 'true';
        case 'joined':
            return aData[7] == 'true';
        case 'left':
            return aData[8] == 'true';
        case 'expiring':
            return aData[9] == 'true';
        default:
            return true;
    }
}

$(document).ready( function() {

    new App.widgets.DataTable(
        '.js-brandcoordinator-table',
        {
            "sDom": '<"toolbar">frtip',
            "iDisplayLength": 25,
            "asStripeClasses":[],
            "bLengthChange": false,
            "order": [[ 0, "asc" ]],
            "columnDefs": [{
                "visible": false,
                "targets": [6, 7, 8, 9]
            }, {
                "bSortable": false,
                "targets": 10
            }]
        },
        [filterByLicense]
    );


    $('#facilitators tr').each(function(index, el){
        new App.widgets.TableRowWithBadges($(el));
        new App.widgets.TableRowWithSlidingBadges($(el));
    })
});