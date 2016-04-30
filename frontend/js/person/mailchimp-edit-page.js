'use strict';

import EditForm from "./intergration-widgets/_mailchimp-form";

$(function(){
    EditForm.plugin('.js-edit-mailchimp', {
        url: jsRoutes.controllers.cm.facilitator.MailChimp.create().url
    });
});



