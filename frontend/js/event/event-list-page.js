'use strict';

import EventBlock from "./../common/_event-block";

$(function(){

    $('.js-event-list')       
        .on('hmt.eventList.show', ()=>{
            EventBlock.plugin('.js-events-control');
        })
        .on('hmt.event.cancel', ()=>{
            $('#events').find('.active').remove();
        })
});

