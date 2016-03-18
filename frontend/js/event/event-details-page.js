'use strict';

import EventBlock from "./../common/_event-block";

$(function(){
    EventBlock.plugin('.js-event-controls');    
    
    $('.js-event-details')
        .on('hmt.event.cancel', function(){
            const brandId = $('#brandId').val();
            window.location.replace(jsRoutes.controllers.Events.index(brandId).url);
        })
});