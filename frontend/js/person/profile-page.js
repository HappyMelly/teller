'use strict';

import IntegrationMailchimp from "./person-widgets/_integration";


$(function(){
    App.events.sub('hmt.tab.shown', () =>{
        IntegrationMailchimp.plugin('.js-mailchimp-integration', {
            activate: jsRoutes.controllers.cm.facilitator.MailChimp.activate().url,
            deactivate: jsRoutes.controllers.cm.facilitator.MailChimp.deactivate().url,

            getAvailableLists: jsRoutes.controllers.cm.facilitator.MailChimp.lists().url,

            createImport: jsRoutes.controllers.cm.facilitator.MailChimp.connect().url,
            updateImport: '/imports/update/1',
            disableImport: jsRoutes.controllers.cm.facilitator.MailChimp.disconnect().url
        })
    })

});



