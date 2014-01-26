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

$(document).ready( function() {

    $("#brandId").change(function() {
        getAvailableFacilitators($(this).find(':selected').val());
    });

    function getAvailableFacilitators(brandCode) {
        $.ajax({
            url: '/license/' + brandCode,
            dataType: "json"
        }).done(function(data) {
            for(var i = 0; i < data.length; i++) {
                var name = data[i]["first_name"] + " " + data[i]["last_name"]
                $('#facilitatorIds').append($("<option></option>").attr("value", data[i]["id"]).text(name));
            }
        }).fail(function() {
            alert("Oops!");
        });
    }
});

