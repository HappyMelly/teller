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
 * Filter evaluations by an event
 */
function filterByEvent(oSettings, aData, iDataIndex) {
    var index = 8;
    var filter = $('#events').find(':selected').val();
    if (filter == '') {
        return true;
    }
    return aData[index] == filter;
}

function filterByTime(oSettings, aData, iDataIndex){
    var index = 11; 
    var filter = $('#past-future').find(':selected').val();
    if(filter == 'all'){
        return true;
    } else if(filter == 'future'){
        var date = new Date(aData[index].start);
        return date > new Date();
    } else {
        var date = new Date(aData[index].end);
        return date <= new Date();
    }
    return aData[index] == filter;
}


/**
 * Filter evaluations checking if they are pending, approved or rejected
 */
function filterByStatus(oSettings, aData, iDataIndex) {
    var index = 10;
    var evalTypeIndex = 12;
    var filter = $('#status').find(':selected').val();
    if(filter == 3){
        var forEvaluation = aData[evalTypeIndex];
        var isValid = forEvaluation !== undefined && forEvaluation !== null;
        if(isValid && aData[index] == filter){
            var dataId = aData[evalTypeIndex].id;
            var dataIds = [];
            if($('.resend').data() !== undefined){
                dataIds = $('.resend').data().ids !== undefined ? $('.resend').data().ids : [];
                if(dataId !== null && dataId !== undefined)
                    dataIds.push(dataId);
                $('.resend').data({'ids':dataIds});
                dataIds = [];
            }
        }
        $('.resend').removeClass('hidden');
    } else {
        $('.resend').addClass('hidden');
        if (filter == 'all') {
            return true;
        }
    }
    return aData[index] == filter;
}

$.fn.dataTableExt.afnFiltering.push(filterByStatus);
$.fn.dataTableExt.afnFiltering.push(filterByEvent);
$.fn.dataTableExt.afnFiltering.push(filterByTime);

function loadEventList(events) {
    $('#events').empty().append($("<option></option>").attr("value", "").text("Specific"));
    for(var i = 0; i < events.length; i++) {
        var event = events[i];
        $('#events').append( $('<option value="'+ event.id +'">' + event.longTitle +'</option>') );
    }
    $('#events').selectpicker('refresh');
}


$(document).ready( function() {
    var currentBrand = $('#activeBrandId').val();
    var events = [];
    var participantTable = $('#participants').dataTable({
        "sDom": '<"toolbar">rtip',
        "iDisplayLength": 25,
        "asStripeClasses":[],
        "aaSorting": [],
        "bLengthChange": false,
        "ajax": {
            "url" : "participants/brand/" + currentBrand,
            "dataSrc": "",
            "deferRender": true
        },
        "order": [[ 6, "desc" ]],
        "columns": [
            { "data": "person" },
            { "data": "event" },
            { "data": "location" },
            { "data": "schedule" },
            { "data": "evaluation.impression" },
            { "data": "evaluation.creation" },
            { "data": "evaluation" },
            { "data": "participant" },
            { "data": "event" },
            { "data": "participant"},
            { "data": "evaluation.status" },
            { "data": "schedule" },
            { "data": "evaluation"}
        ],
        "columnDefs": [{
                "render": function(data) {
                    return '<a href="' + data.url + '">' + data.name + '</a>';
                },
                "targets": 0
            }, {
                "render": function(data) {
                    var result = $.grep(events, function(e){ return e.url == data.url; });
                    if (result.length == 0) {
                        events.push(data);
                    }
                    return '<a href="' + data.url + '">' + data.title + '</a>';
                },
                "targets": 1
            }, {
                "targets": 2
            }, {
               "render" : function(data){ return data.formatted; },
                "targets": 3
            },{
                "className": "evaluation-field",
                "targets": [4, 5, 6, 7]
            },{
                "render": function(data) { return drawStatus(data); },
                "targets": 6,
                "className": "status"
            }, {
                "render": function(data) { return drawCertificate(data); },
                "targets": 7,
                "className": "certificate"
            }, {
                "render": function(data) { return data.id; },
                "visible": false,
                "targets": 8
            }, {
               "render": function(data) {
                   var html = '<div class="circle-show-more" data-event="' + data.event + '"';
                   html += ' data-person="' + data.person + '">';
                   html += '<span class="glyphicon glyphicon-chevron-down"></span></div>';
                   return html;
               },
               "targets": 9,
               "bSortable": false
            }, {
                "render": function(data) {
                    if (data) {
                        return data.value;
                    } else {
                        return "";
                    }
                },
                "visible": false,
                "targets": 10
            },{
                "render": function(data){ return data; },
                "visible": false,
                "targets": 11
            },{
                "render": function(data){ 
                    // evaluation data
                    return data; },
                "visible": false,
                "targets": 12
            }
        ]
    });
    participantTable
        .api()
        .on('init.dt', function (e, settings, data) {
            loadEventList(events);
            initializeParticipantActions("table");
        });

    $("div.toolbar").html($('#filter-containter').html());
    $('#filter-containter').empty();
    $('#status').on('change', function() {
        participantTable.fnDraw();
    });

    $("#events").on('change', function() {
        participantTable.fnDraw();
    });

    $("#past-future").on('change', function(){
        participantTable.fnDraw();
    });

    $('#participants').on('draw.dt', function() {
        calculateAverageImpression(participantTable);
        initializeParticipantActions("table");
    });
    $('#exportLink').on('click', function() {
        buildExportLink(false)
    });

    $('.resend').on('click',function(){
        var arrIds = $('.resend').data().ids;
        var distinct = [];
        $.each(arrIds, function(i,el){
            if($.inArray(el,distinct) === -1) distinct.push(el);
        });
    });
});
