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

function User(data) {
    this.name = data["first_name"] + " " + data["last_name"];
    this.id = data["id"];
    this.memberships = data["memberships"];
}

User.prototype.isFacilitator = function(userId) {
    return this.id == userId;
};

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
 * @param brandId String
 * @param chosenFacilitators Array[Int] or null
 */
function getFacilitators(brandId, chosenFacilitators) {
    $.ajax({
        url: '/brand/' + brandId + '/facilitators',
        dataType: "json"
    }).done(function(data) {
        for(var i = 0; i < data.length; i++) {
            var user = new User(data[i]);
            if (isChosenOne(user, chosenFacilitators)) {
                facilitators.chosen[facilitators.chosen.length] = user;
            } else {
                facilitators.retrieved[facilitators.retrieved.length] = user;
            }
        }
        facilitators.updateState();
    }).fail(function() {
        showError("Sorry we don't know anything about the brand you try to request");
    });
}

var facilitators = {
    retrieved: [],
    chosen: [],
    userId: 0,
    invoiceOrgId: 0,
    initialize: function(brandId) {
        this.userId = parseInt($('#currentUserId').attr('value'));
        this.invoiceOrgId = parseInt($('#currentInvoiceToId').attr('value'));
        var values = $('#chosenFacilitators').attr('value').split(',');
        getFacilitators(brandId, values);
    },
    retrieve: function(brandId) {
        this.retrieved = [];
        this.chosen = [];
        getFacilitators(brandId, null);
    },
    updateState: function() {
        // update a list of available facilitators
        $('#facilitatorIds')
            .empty()
            .append($("<option></option>").attr("value", 0).text(""));
        this.retrieved.sort(this.sortByName);
        for(var i = 0; i < this.retrieved.length; i++) {
            var name = this.retrieved[i].name;
            $('#facilitatorIds').append($("<option></option>").attr("value", this.retrieved[i].id).text(name));
        }
        // update a list of chosen facilitators
        $('#chosenFacilitators').empty();
        this.chosen.sort(this.sortByName);
        var organisations = [];
        for(var i = 0; i < this.chosen.length; i++) {
            var user = this.chosen[i];
            for(var j = 0; j < user.memberships.length; j++) {
                var company = user.memberships[j];
                if (organisations.indexOf(company.id) > 0) {
                    continue;
                } else {
                    organisations[company.id] = company.name;
                }
            }
            var parentDiv = $("<div>").append(
                $("<input readonly type='hidden'>")
                    .attr("value", user.id)
                    .attr("id", "facilitatorIds_" + i)
                    .attr('name', 'facilitatorIds[' + i + ']')
            );
            var div = $("<div class='input-group'>").append(
                $("<input readonly type='text' class='form-control'>")
                    .attr("value", user.name)
            );
            parentDiv.append(div);
            var trashCan = $('<i>')
                .attr('class', 'glyphicon glyphicon-trash');
            var button = $('<button>').attr('type', 'button');
            if (!user.isFacilitator(this.userId)) {
                button.attr('class', 'btn btn-danger deselect');
            } else {
                button
                    .attr('class', 'btn btn-danger')
                    .attr('disabled', 'disabled');
            }
            button.append(trashCan);
            var span = $('<span class="input-group-btn">').append(button);
            div.append(span);
            $('#chosenFacilitators').append(parentDiv);
        }
        updateInvoicingOrganisations(organisations, this.invoiceOrgId);
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
        return left.name.localeCompare(right.name);
    }
};