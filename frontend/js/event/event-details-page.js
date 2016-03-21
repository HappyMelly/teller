'use strict';

import EventBlock from "./../common/_event-block";

$(function(){
    EventBlock.plugin('.js-event-controls');

    App.events
        .sub('hmt.event.cancel', function(){
            const brandId = $('#brandId').val();
            window.location.replace(jsRoutes.controllers.Events.index(brandId).url);
        })
});