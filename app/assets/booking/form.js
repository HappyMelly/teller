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

function updateOwesToggle() {
  var owes = $('#owes_value').val() === 'true';
  if (owes) {
    $('#owes_toggle')
      .addClass('btn-success')
      .removeClass('btn-danger')
      .html('Owes To <i class="glyphicon glyphicon-arrow-right glyphicon-white"></i>');
    $('#conversionInfo').find('.fromId.converted .direction' ).text("credit");
    $('#conversionInfo').find('.toId.converted .direction' ).text("debit");
  }
  else {
    $('#owes_toggle')
      .removeClass('btn-success')
      .addClass('btn-danger')
      .html('Pays To <i class="glyphicon glyphicon-arrow-right glyphicon-white"></i>');
    $('#conversionInfo').find('.fromId.converted .direction' ).text("debit");
    $('#conversionInfo').find('.toId.converted .direction' ).text("credit");
  }
  $('#owes_value').val(owes);
}

$(document).ready( function() {
  $('#owes_toggle').click(function() {
    var owes = $('#owes_value').val() === 'true';
    $('#owes_value').val(!owes);
    updateOwesToggle();
  });
  updateOwesToggle();
});
