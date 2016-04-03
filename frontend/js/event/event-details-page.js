'use strict';

import EventBlock from "./../common/_event-block";
import ClipBoard from "./../common/_clipboard";
import RequestEventDlg from "./detail-widgets/_evaluation-dlg";
import ModifyEmail from "./detail-widgets/_modify-email";


$(function(){
    EventBlock.plugin('.js-event-controls');
    RequestEventDlg.plugin('.js-request-evaluation');
    ClipBoard.plugin('.js-clipboard');
    ModifyEmail.plugin('.js-modify-email');

    App.events
        .sub('hmt.event.cancel', function(){
            const brandId = $('#brandId').val();
            window.location.replace(jsRoutes.controllers.cm.Events.index(brandId).url);
        })
});