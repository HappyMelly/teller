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

function User(data) {
    this.name = data["first_name"] + " " + data["last_name"];
    this.id = data["id"];
    this.coordinator = data["coordinator"];
}

User.prototype.isFacilitator = function(userId) {
    return this.id == userId;
}
User.prototype.isCoordinator = function() {
    return this.coordinator;
}

function showError(message) {
    $('#error').append(
        $('<div class="alert alert-error">')
            .text(message)
            .append('<button type="button" class="close" data-dismiss="alert">&times;</button>')
    );
}

/**
 * Retrieve a list of event types for the brand
 * @param brandCode String
 */
function getEventTypes(brandCode) {
    $.ajax({
        url: '/eventtypes/' + brandCode,
        dataType: "json"
    }).done(function(data) {
            var selector = "#eventTypeId";
            var value = parseInt($(selector).attr('value'));
            $(selector)
                .empty()
                .append($("<option></option>").attr("value", 0).text("Choose an event type"));
            for(var i = 0; i < data.length; i++) {
                var option = $("<option></option>")
                    .attr("value", data[i].id)
                    .attr("defaultTitle", data[i].defaultTitle)
                    .text(data[i].name);
                if (value == data[i].id) {
                    option.attr('selected', 'selected');
                }
                $(selector).append(option);
            }
        }).fail(function() {
            showError("Sorry we don't know anything about the brand you try to request")
        });
}

/**
 * Update 'Title' field
 * @param title String
 */
function updateTitle(title) {
    $("#title").val(title);
}

/**
 * Automatically set an end date to a start date + 1 day
 * @param dateString String
 * @param locator String
 */
function updateEndDate(dateString, locator) {
    try {
        var endDate = Date.parse(dateString).addDays(1);
        $(locator).val(endDate.toString("yyyy-MM-dd"));
    } catch (RangeError) {
        // empty body
    }
}

$(document).ready( function() {

    /**
     * Check if we should add a user to the list of chosen facilitators or not
     * @param user User
     * @param chosenFacilitators Array[Int]
     * @returns Boolean
     */
    function isChosenOne(user, chosenFacilitators) {
        if (user.isFacilitator(facilitators.userId)) return true;
        return (chosenFacilitators && chosenFacilitators.indexOf(user.id.toString()) >= 0);
    }

    /**
     * Retrieve a list of facilitators for the brand and fill 'chosen' and 'retrieved' arrays
     * in 'facilitators' object
     * @param brandCode String
     * @param chosenFacilitators Array[Int] or null
     */
    function getFacilitators(brandCode, chosenFacilitators) {
        $.ajax({
            url: '/facilitators/' + brandCode,
            dataType: "json"
        }).done(function(data) {
                for(var i = 0; i < data.length; i++) {
                    var user = new User(data[i]);
                    if (isChosenOne(user, chosenFacilitators)) {
                        facilitators.chosen[facilitators.chosen.length] = user
                    } else {
                        facilitators.retrieved[facilitators.retrieved.length] = user
                    }
                }
                facilitators.updateState();
            }).fail(function() {
                showError("Sorry we don't know anything about the brand you try to request")
            });
    }

    var facilitators = {
        retrieved: [],
        chosen: [],
        userId: 0,
        initialize: function(brandCode) {
            this.userId = parseInt($('#currentUserId').attr('value'));
            var values = $('#chosenFacilitators').attr('value').split(',');
            getFacilitators(brandCode, values);
        },
        retrieve: function(brandCode) {
            this.retrieved = [];
            this.chosen = [];
            getFacilitators(brandCode, null);
        },
        updateState: function() {
            // update a list of available facilitators
            $('#facilitatorIds')
                .empty()
                .append($("<option></option>").attr("value", 0).text(""));
            this.retrieved.sort(this.sortByName)
            for(var i = 0; i < this.retrieved.length; i++) {
                var name = this.retrieved[i].name;
                $('#facilitatorIds').append($("<option></option>").attr("value", this.retrieved[i].id).text(name));
            }
            // update a list of chosen facilitators
            $('#chosenFacilitators').empty();
            this.chosen.sort(this.sortByName);
            for(var i = 0; i < this.chosen.length; i++) {
                var user = this.chosen[i];
                var div = $("<div>").append(
                    $("<input readonly type='hidden'>")
                        .attr("value", user.id)
                        .attr("id", "facilitatorIds_" + i)
                        .attr('name', 'facilitatorIds[' + i + ']')
                ).append(
                    $("<input readonly type='text'>")
                        .attr("value", user.name)
                );
                if (!user.isFacilitator(this.userId) || user.isCoordinator()) {
                    div.append(
                        $("<a href='#' class='btn btn-mini btn-link deselect'>Remove</a>")
                    );
                }
                $('#chosenFacilitators').append(div);
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
                if (from[i].id == id) {
                    index = i;
                    break;
                }
            }
            if (index != -1) {
                to[to.length] = from[index];
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
        var code = $(this).find(':selected').val();
        getEventTypes(code);
        facilitators.retrieve(code);
    });
    $('#facilitatorIds').change(function() {
        facilitators.select($(this).find(':selected').val());
    });
    $(this).on('click', '.deselect', function(event) {
        event.preventDefault();
        var id = $(this).parent('div').children('input').first().val();
        facilitators.deselect(id);
    });
    var code = $('#brandCode').find(':selected').val();
    getEventTypes(code);
    facilitators.initialize(code);
    // turn it on only for a new event
    if ($("form").attr("action").indexOf('events') != -1) {
        // clean default values here because it's too difficult to implement it on the server side
        // they'll also be cleaned on duplication but it's not a big deal as the dates are different in most cases
        $("#schedule_start").val('');
        $("#schedule_end").val('');
        $("#schedule_start").on('input', function() {
            updateEndDate($(this).val(), "#schedule_end");
        });
        $('#schedule_end').on('input', function() {
           $('#schedule_start').unbind('input');
        });

        $("#eventTypeId").change(function() {
            var option = $(this).find(':selected');
            if (option.attr('defaultTitle')) {
                updateTitle(option.attr('defaultTitle'));
            }
        });
        $("#title").on('keyup', function() {
            $("#eventTypeId").unbind('change');
        });
    }
});

