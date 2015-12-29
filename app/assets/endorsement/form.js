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


var EndorsementPage = (function($){

    /**
     * Assign events on the page
     *
     */
    function assignEvents(){
        $('body')
            .on('click', '.js-evaluation-checkbox', function(){
                showFixedButtons(true);
            });

        $(window)
            .scroll(function (e) {
                var $window = $(window),
                    isFixedButtons = $('.p-endors__buttons-con').offset().top > ($window.scrollTop() + $window.height());

                showFixedButtons(isFixedButtons);
            })
    }

    /**
     * Show fixed block of buttons in the bottom of the page
     *
     * @param condition Boolean value for showing fixed block
     */
    function showFixedButtons(condition){
        var toggleClass = 'show-fixed-buttons',
            $body = $('body'),
            $checkboxes = $('.js-evaluation-checkbox').filter(':checked');

        if (condition && $checkboxes.length){
            $body.addClass(toggleClass);
        } else {
            $body.removeClass(toggleClass);
        }
    }


    return {
        init: function(){
            assignEvents();
        }
    }
})(jQuery);

/**
 * Updates the order of evaluations and renders updated evaluations
 *
 * @param order Ascending/Descending
 */
function reorderEvaluations(order) {
    var container = $('.strip').find('.list-group'),
        children = $(container).children('.list-group-item');

    children.sort(
        function (left, right) {
            return sortEvaluations(left, right, order);
        }
    );

    children.detach()
        .appendTo(container);
}

/**
 * Sorts evaluations by their ratings
 * @param left Evaluation on the left
 * @param right Evaluations on the right
 * @param order Ascending/Descending
 * @returns {int}
 */
function sortEvaluations(left, right, order) {
    var leftRating = $(left).data('rating'),
        rightRating = $(right).data('rating');

    if (leftRating > rightRating) {
        return order;
    }
    if (leftRating < rightRating) {
        return -order;
    }
    return 0;
}

/**
 * Switches action button representation and behaviour on tab change
 */
function updateButtonState() {
    var submitBtn = $('#submit');

    if ($('#addForm').hasClass('active')) {
        submitBtn.prop('disabled', false);
        submitBtn.text('Save');
        $('form').off('submit');
    } else {
        var evaluations = $('input:checked').length;

        if (evaluations == 0) {
            submitBtn.text('Select evaluations');
            submitBtn.prop('disabled', true);
        } else if (evaluations == 1) {
            submitBtn.text('Add 1 endorsement');
            submitBtn.prop('disabled', false);
        } else {
            submitBtn.text('Add ' + evaluations + ' endorsements');
            submitBtn.prop('disabled', false);
        }
        $('form').off('submit');
        $('form').on('submit', function(e) {
            e.preventDefault();
            var evaluationIds = [];
            $('input:checked').each(function(e) {
                evaluationIds[evaluationIds.length] = parseInt($(this).val());
            });
            if (evaluationIds.length > 0) {
                var url = jsRoutes.controllers.Endorsements.createFromSelected($('#personId').val()).url;
                $.post(url, { evaluations: JSON.stringify(evaluationIds) },
                    function(data) {
                        var response = JSON.parse(data);
                        console.log(response);
                        window.location = response.url;
                });
            }
        });
    }
}

$(document).ready(function() {
    $.get($('#selectForm').data('url'), {}, function(data) {
        $('#selectForm').html(data);
        $('#toTen').click(function (e) {
            e.preventDefault();
            $(this).parent().addClass('active');
            $('#toZero').parent().removeClass('active');
            reorderEvaluations(1);
        });
        $('#toZero').click(function (e) {
            e.preventDefault();
            $(this).parent().addClass('active');
            $('#toTen').parent().removeClass('active');
            reorderEvaluations(-1);
        });
        $('#selectForm [type=checkbox]').on('change', function(e) {
            if($(this).is(":checked")) {
                $(this).parents('.list-group-item').addClass('selected');
            } else {
                $(this).parents('.list-group-item').removeClass('selected');
            }
            updateButtonState();
        });
        updateButtonState();
    }, "html");

    $('#switcher a').click(function (e) {
        e.preventDefault();
        $(this).tab('show');
        updateButtonState();
    });


    EndorsementPage.init();

});