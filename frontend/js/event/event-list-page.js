'use strict';

import EventBlock from "./../common/_event-block";

$(function(){

    App.events
        .sub('hmt.event.cancel', ()=>{
            $('#events').find('.active').remove();
        })
        .sub('hmt.eventList.show', ()=>{
            EventBlock.plugin('.js-events-control');
        })

});

