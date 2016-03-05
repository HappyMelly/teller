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
 * If you have questions concerning this license or the applicable additional
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */

/**
 * This class adds hide/show details functionality for DataTables.
 *
 * If you have a datatable defined the way
 *       var requests = $('#label').dataTable({});
 *  when your create this class
 *      (new TableWithDetails(requests, 'label', somecallback);
 *
 * DataTable row object is passed to the callback to render details. Server requests
 *  could be freely used in callback functions - the class renders the details just once
 *  for any given row and then cache the results.
 *
 */

/**
 * @param obj {object} DataTable object
 * @param id {string} DataTable identifier
 * @param callback {function} Function to render the details
 * @constructor
 */
function TableWithDetails(obj, id, callback) {
    this.obj = obj;
    this.id = id;
    this.callback = callback;
    this.init();
}

TableWithDetails.prototype.init = function () {
    var table = this;
    $('#' + this.id).on('click', '.circle-show-more', function () {
        var tr = $(this).closest('tr');
        var row = table.obj.api().row(tr);

        if (row.child.isShown()) {
            row.child.hide();
            table.deactivate(tr);
        } else {
            table.obj.api().rows().every(function (rowIdx, tableLoop, rowLoop) {
                this.child.hide();
                table.deactivate($(this.node()));
            });
            if (row.child() === undefined) {
                table.callback(row);
            } else {
                row.child.show();
            }
            table.activate(tr);
        }
    });
};

TableWithDetails.prototype.activate = function (row) {
    var $chevron = row.children('.circle-show-more');
    var $button = $chevron.children('span');
    $button.removeClass('glyphicon-chevron-down').addClass('glyphicon-chevron-up');
    $chevron.addClass('active');
    row.addClass('active shown');
};

TableWithDetails.prototype.deactivate = function (row) {
    if (row.hasClass('active')) {
        var $chevron = row.children('.circle-show-more');
        var $button = $chevron.children('span');
        $button.removeClass('glyphicon-chevron-up').addClass('glyphicon-chevron-down');
        $chevron.removeClass('active');
        row.removeClass('shown active');
    }
};