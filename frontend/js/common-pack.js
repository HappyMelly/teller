'use strict';

import NotifCommercial from "./layout/_notification-commercial";
import ScrollToElement from "./layout/_scroll-to";
import NotificationList from "./layout/_notifation-list";
import PreviewMarkdown from "./plugins/markdown/_plugin";

$(function(){
    ScrollToElement.plugin('.js-link-target');
    NotificationList.plugin('.js-notification-list');
    PreviewMarkdown.plugin('[markdownpreview]');
    NotifCommercial.plugin('.js-notif-commercial');

    const $dataField = $('[data-type="date"]');
    $dataField.length && $dataField.datetimepicker({
        useCurrent: false,
        pickTime: false
    });


});

