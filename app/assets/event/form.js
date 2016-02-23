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

function showError(message) {
    $('#error').append(
        $('<div class="alert alert-danger">')
            .text(message)
            .append('<button type="button" class="close" data-dismiss="alert">&times;</button>')
    );
}

/**
 * Retrieve a list of event types for the brand
 * @param brandId {int}
 * @param currentEventType String
 */
function getEventTypes(brandId, currentEventType) {
    $.ajax({
        url: '/brand/' + brandId + '/eventtypes',
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
                    .attr("defaultTitle", data[i].title)
                    .attr("free", data[i].free)
                    .text(data[i].name);
                if (value == data[i].id) {
                    option.attr('selected', 'selected');
                }
                $(selector).append(option);
            }
            if (currentEventType) {
                selector = 'option[value="' + currentEventType + '"]';
                var object = $('#eventTypeId').find(selector)
                object.attr('selected', 'selected');
                toggleFreeCheckbox(object.attr('free'));
            }
        }).fail(function() {
            showError("Sorry we don't know anything about the brand you try to request");
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
 * Update a list of organisations for invoicing
 * @param organisations Array
 * @param selectedId Int
 */
function updateInvoicingOrganisations(organisations, selectedId) {
    $('#invoice')
        .empty()
        .append($("<option></option>").attr("value", 0).text("Choose an organisation"));
    if (organisations.length == 0) {
        var message = "This event cannot be saved. You chose no facilitators with active organisations. ";
        message += "Please ask the person who added ";
        message += "your account also connect it to an organization that will be used for invoicing.";
        $('#no_org_warning').empty().append(
            $('<div class="alert alert-danger">')
                .text(message)
        );
    } else {
        $('#no_org_warning').empty();
    }
    for(var key in organisations) {
        var option = $("<option></option>").attr("value", key).text(organisations[key]);
        if (key == selectedId) {
            option.attr('selected', 'selected');
        }
        $('#invoice').append(option);
    }
}

/**
 * Calculates number of total hours for event based on its number of days and
 * hours per day
 */
function calculateTotalHours() {
    var end = $('#schedule_end').data("DateTimePicker").getDate();
    var start = $('#schedule_start').data('DateTimePicker').getDate();
    var days = end.diff(start, 'days') + 1;
    return $('#schedule_hoursPerDay').val() * days;
}
/**
 * Updates number of total hours for event based on its number
 * of days and hours per day
 */
function updateTotalHours() {
    var totalHours = calculateTotalHours();
    $('#schedule_totalHours').val(totalHours);
}

/**
 * Checks if the entered number of total hours is move than minimum threshold
 * and shows an alert
 * @param hours Number of entered total hours
 */
function checkTotalHours(hours) {
    var idealHours = calculateTotalHours();
    var threshold = 0.2;
    var difference = (idealHours - hours) / parseFloat(idealHours);
    if (difference > threshold) {
        $('#totalHours-alert').show();
    } else {
        $('#totalHours-alert').hide();
    }
}

/**
 * Checks if the given url points to an existing page and notifies a user about
 *  the results of the check
 *
 * @param url {string} The url of interest
 * @param element {string} jQuery selector
 */
function checkUrl(url, element) {
    var field = element + '_field';
    if ($.trim(url).length == 0 || (url.substring(0, 6) == "mailto")) {
        $(field).removeClass('has-error');
        $(field).removeClass('has-success');
        $(element).siblings('span').each(function() {
            $(this).text("Web site URL");
        });
    } else {
        var fullUrl = jsRoutes.controllers.Utilities.validate(url).url;
        $.post(fullUrl, {}, null, "json").done(function(data) {
            if (data.result == "invalid") {
                $(field).addClass('has-error');
                $(element).siblings('span').each(function() {
                    $(this).text("URL is not correct");
                });
            } else {
                $(field).removeClass('has-error');
                $(field).addClass('has-success');
                $(element).siblings('span').each(function() {
                    $(this).text("URL is correct");
                });
            }
        });
    }
}

/**
 * Shows/hides free checkbox depending on if event type allows free events
 *
 * @param value True if free events are allowed
 */
function toggleFreeCheckbox(value) {
    if (value == "true") {
        $("#free_field").show();
    } else {
        $("#free_field").hide();
    }
}

/**
 * Updates city depending on the chosen country
 *
 * @param obj Country selector
 */
function updateCity(obj) {
    if ($(obj).find(':selected').val() == "00") {
        $('#location_city').val('online');
    } else if ($('#location_city').val() == 'online') {
        $('#location_city').val('');
    }
}

/**
 * Loads organizer's name
 *
 * @param id Organizer id
 */
function updateOrganizer(id) {
    if (id != 0) {
        var url = jsRoutes.controllers.Organisations.name(id).url
        $.get(url, function(data) {
            $('#organizerSearch').val(data.name);
        }, "json");
    }
}

/**
 * Adds organizer
 */
function addOrganizer(e) {
    e.preventDefault();
    $.ajax({
        type: "POST",
        url: jsRoutes.controllers.Organisations.createOrganizer().url,
        data: $("#addOrgForm").serialize(),
        dataType: "json"
    }).done(function(data){
        $('#searchBlock').show();
        $('#addOrgFormContainer').append($('#addOrgForm'));
        $('#organizerSearch').val(data.name);
        $('#organizer_id').val(data.id);
    }).fail(function(jqXHR, status, error) {
    });
}

function initializeEmptyForm() {
    $("#schedule_start").on("dp.change", function (e) {
        $('#schedule_end').data("DateTimePicker").setDate(e.date.add(1, 'days'))
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
    updateTotalHours(8);
    $("#free_field").hide();
}


function connectAddOrganizerAction(object) {
    $(object).on('click', function () {
        $('.search-block').hide();
        $('#addOrgBlock').append($('#addOrgForm'));
        $('.autocomplete-suggestions').hide();
        $('#addOrgFormBack').on('click', function () {
            $('.search-block').show();
            $('#addOrgFormContainer').append($('#addOrgForm'));
            return false;
        });
        return false;
    });
}

$(document).ready( function() {

    // Binds
    $("#brandId").change(function() {
        var id = $(this).find(':selected').val();
        getEventTypes(id, "");
        facilitators.retrieve(id);
    });
    $('#facilitatorIds').change(function() {
        facilitators.select($(this).find(':selected').val());
    });
    $(this).on('click', '.deselect', function(event) {
        event.preventDefault();
        var id = $(this).parent().parent().parent('div').children('input').first().val();
        facilitators.deselect(id);
    });
    $("#schedule_start").on("dp.change", function (e) {
        $('#schedule_end').data("DateTimePicker").setMinDate(e.date);
        updateTotalHours();
    });
    $("#schedule_end").on("dp.change", function(e) {
        updateTotalHours();
    });
    $('#schedule_totalHours').on('change', function(e) {
        checkTotalHours($(this).val());
    });
    $('#organizer_webSite').on('change', function(e) {
        checkUrl($(this).val(), '#organizer_webSite');
    });
    $('#organizer_registrationPage').on('change', function(e) {
        checkUrl($(this).val(), '#organizer_registrationPage');
    });
    var brandId = $('#brandId').find(':selected').val();
    getEventTypes(brandId, $('#currentEventTypeId').attr('value'));
    facilitators.initialize(brandId);
    if ($("#emptyForm").attr("value") == 'true') {
        initializeEmptyForm();
    }
    $("#eventTypeId").change(function(event) {
        var option = $(this).find(':selected');
        toggleFreeCheckbox(option.attr('free'));
    });
    checkTotalHours($('#schedule_totalHours').val());

    if ($("#confirmed").attr("checked") != "checked") {
        $("#confirmed-alert").hide();
    }
    $("#confirmed").on("change", function(e) {
        if (this.checked) {
            $("#confirmed-alert").show();
        } else {
            $("#confirmed-alert").hide();
        }
    });
    $('#location_country').chosen().change(function(e) {
        updateCity($(this));
    });
    updateCity($('#location_country'));
    updateOrganizer($('#organizer_id').val());

    $("#organizerSearch").autocomplete({
        serviceUrl: jsRoutes.controllers.Organisations.search().url,
        paramName: 'query',
        minChars: 3,
        preserveInput: true,
        showNoSuggestionNotice: true,
        noSuggestionNotice: function () {
            var link = $('<a id="addOrg">').attr('href', '#').text("Add new organizer");
            connectAddOrganizerAction(link);
            return $('<div class="new-organizer">').append(link);
        },
        formatResult: function (suggestion, currentValue) {
            return suggestion.value;
        },
        onSelect: function (suggestion) {
            $(this).val(suggestion.name);
            $('#organizer_id').val(suggestion.data);
            return true;
        },
        transformResult: function(response) {
            return {
                suggestions: $.map($.parseJSON(response), function(dataItem) {
                    var filename = dataItem.countryCode.toLowerCase() + '.png';
                    var url = jsRoutes.controllers.Assets.at('images/flags/16/' + filename).url;
                    var text = '<img src="' + url + '"/>&nbsp;' + dataItem.name;
                    return { data: dataItem.id, value: text, name: dataItem.name };
                })
            };
        },
        beforeRender: function(container) {
            var action = $('<div class="new-organizer">').append(
                $('<a id="addOrg">').attr('href', '#').text("Add new organizer"));
            $(container).append(action);
            connectAddOrganizerAction('#addOrg');
        }
    });
    $('#addOrgForm').on('submit', function(e) {
        addOrganizer(e);
        return false;
    });
});

