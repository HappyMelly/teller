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

/**
 * Changes UI on brand activation/deactivation
 *
 * @param active {boolean} if true, brand is active
 */
function switchState(active) {
    if (active) {
        $('#activate').html('Deactivate');
        $('#deactivatedStatus').hide();
        $('#brandState').data('value', true);
    } else {
        $('#activate').html('Activate');
        $('#deactivatedStatus').show();
        $('#brandState').data('value', false);
    }
}

/**
 * Updates the view after the link was added
 * @param linkId {int} Link id
 * @param brandId {int} Brand id
 * @param linkType {string} Link type
 * @param link {string} Link
 */
function addLink(linkId, brandId, linkType, link) {
    $('#links').append(
        $('<tr>')
            .attr('data-id', linkId)
            .attr('data-brandid', brandId)
            .append($('<td>').append(linkType))
            .append($('<td>')
                .append($('<a>')
                    .attr('href', link)
                    .append(link)))
            .append($('<td>')
                .append($('<a class="font-sm">')
                    .append('Remove')
                    .addClass('remove')
                    .attr('href', '#')
                    .attr('data-id', linkId)
                    .attr('data-href', jsRoutes.controllers.BrandLinks.remove(brandId, linkId).url)))
    );
}

/**
 * Updates the view after the link was deleted
 * @param linkId {int} Link id
 */
function removeLink(linkId) {
    $('tr[data-id="' + linkId + '"]').remove();
}

function initializeLinksActions() {
    $('#addLinkForm').submit(function(e) {
        $.post($(this).attr("action"), $(this).serialize(), null, "json").done(function(data) {
            addLink(data.id, data.brandId, data.linkType, data.link);
        }).fail(function(jqXHR, status, errorCode) {
            if (status == "error") {
                var errorCode = JSON.parse(jqXHR.responseText);
                error(errorCode.message);
            } else {
                var msg = "Internal error. Please try again or contant the support team.";
                error(msg);
            }
        });
        // Prevent the form from submitting with the default action
        return false;
    });
    $('#links').on('click', 'a.remove', function(e) {
        var linkId = $(this).data('id');
        $.ajax({
            type: "DELETE",
            url: $(this).data('href'),
            dataType: "json"
        }).done(function(data) {
            removeLink(linkId, name);
        }).fail(function(jqXHR, status, errorCode) {
            if (status == "error") {
                var response = JSON.parse(jqXHR.responseText);
                error(response.message);
            } else {
                var msg = "Internal error. Please try again or contant the support team.";
                error(msg);
            }
        });
        return false;
    });
}


function initializeTestimonialActions() {
    $('#testimonialList').on('click', 'a.remove', function(e) {
        var testimonialId = $(this).data('id');
        e.preventDefault();
        $.ajax({
            type: "DELETE",
            url: $(this).data('href'),
            dataType: "json"
        }).done(function(data) {
            $('div[data-id="' + testimonialId + '"]').remove();
            success(data.message);
        }).fail(function(jqXHR, status, errorCode) {
            if (status == "error") {
                var response = JSON.parse(jqXHR.responseText);
                error(response.message);
            } else {
                var msg = "Internal error. Please try again or contant the support team.";
                error(msg);
            }
        });
    });
}

function initializeActions() {
    initializeLinksActions();
    initializeTestimonialActions();
}

/**
 * Loads tab content (if needed) and shows it to a user
 * @param elem Tab button
 * @returns {boolean}
 */
function showTab(elem) {
    var url = $(elem).attr('data-href'),
        target = $(elem).attr('href');
    if ($.inArray(target, loadedTabs) < 0 && url) {
        $.get(url, function(data) {
            $(target).html(data);
            initializeActions();
        });
        loadedTabs[loadedTabs.length] = target;
    }
    $(elem).tab('show');
    return false;
}

var loadedTabs = [];

$(document).ready( function() {
    // Delete links.
    $('.delete').click(function() {
        return confirm('Delete this ' + $(this).attr('text') + '? You cannot undo this action.');
    });

    $('#sidemenu a').click(function (e) {
        showTab($(this));
    });
    var hash = window.location.hash.substring(1);
    if (!hash) {
        hash = 'general';
    }
    showTab($('#sidemenu a[href="#' + hash + '"]'));
    initializeActions();

    if ($('#brandState').data('value')) {
        $('#deactivatedStatus').hide();
    }
    $('#activate').on('click', function(e) {
        e.preventDefault();
        var url = jsRoutes.controllers.Brands.activation($(this).data('id')).url;
        var active = !($('#brandState').data('value'));
        $.post(url, {active: active}, function(data, textStatus, xhr) {
            switchState(active);
        });
    });

});

