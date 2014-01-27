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

    var facilitators = {
        retrieved: [],
        chosen: [],
        retrieve: function(brandCode) {
            this.retrieved = []
            $.ajax({
                url: '/license/' + brandCode,
                dataType: "json"
            }).done(function(data) {
                for(var i = 0; i < data.length; i++) {
                    var name = data[i]["first_name"] + " " + data[i]["last_name"];
                    facilitators.retrieved[i] = { name: name, id: data[i]['id'] }
                }
                facilitators.updateState();
            }).fail(function() {
                alert("Oops!");
            });
        },
        updateState: function() {
            // update a list of available facilitators
            $('#facilitatorIds')
                .empty()
                .append($("<option></option>").attr("value", 0).text(""));
            this.retrieved.sort(this.sortByName)
            for(var i = 0; i < this.retrieved.length; i++) {
                var name = this.retrieved[i]['name'];
                $('#facilitatorIds').append($("<option></option>").attr("value", this.retrieved[i]["id"]).text(name));
            }
            // update a list of chosen facilitators
            $('#chosenFacilitators').empty();
            this.chosen.sort(this.sortByName);
            for(var i = 0; i < this.chosen.length; i++) {
                var person = this.chosen[i];
                $('#chosenFacilitators').append(
                    $("<div>").append(
                        $("<input readonly type='hidden'>")
                            .attr("value", person['id'])
                            .attr('name', 'details.facilitatorIds[' + (i + 1) + ']')
                    ).append(
                        $("<input readonly type='text'>")
                            .attr("value", person['name'])
                    ).append(
                        $("<button class='btn btn-mini btn-danger deselect'>Delete</button>")
                    )
                )
            }
        },
        select: function(id) {
            this.move(id, this.retrieved, this.chosen);
        },
        deselect: function(id) {
            this.move(id, this.chosen, this.retrieved);
        },
        move: function(id, from, to) {
            var index = -1;
            for(var i = 0; i < from.length; i++) {
                if (from[i]['id'] == id) {
                    index = i;
                    break;
                }
            }
            if (index != -1) {
                var name = from[index]["name"];
                to[to.length] = { name: name, id: id };
                from.splice(index, 1);
            }
            this.updateState();
        },
        sortByName: function(left, right) {
            return left.name > right.name
        }
    };

    // Binds
    $("#brandCode").change(function() {
        facilitators.retrieve($(this).find(':selected').val());
    });
    $('#facilitatorIds').change(function() {
        facilitators.select($(this).find(':selected').val());
    });
    $(this).on('click', '.deselect', function(event) {
        event.preventDefault();
        var id = $(this).parent('div').children('input').first().val();
        facilitators.deselect(id);
    });
    facilitators.retrieve($('#brandCode').find(':selected').val());
});

