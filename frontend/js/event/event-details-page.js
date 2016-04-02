'use strict';

import EventBlock from "./../common/_event-block";
import RequestEventDlg from "./widgets/_evaluation-dlg";
import ClipBoard from "./../common/_clipboard";


$(function(){
    EventBlock.plugin('.js-event-controls');
    RequestEventDlg.plugin('.js-request-evaluation');
    ClipBoard.plugin('.js-clipboard');

    App.events
        .sub('hmt.event.cancel', function(){
            const brandId = $('#brandId').val();
            window.location.replace(jsRoutes.controllers.cm.Events.index(brandId).url);
        })
});