'use strict';

import IntegrationMailchimp from "./intergration-widgets/_mailchimp_integration";
import IntegrationSlack from "./intergration-widgets/_slack_integration";


$(function(){
    App.events.sub('hmt.tab.shown', () =>{
        IntegrationMailchimp.plugin('.js-mailchimp-integration', {
            activate: jsRoutes.controllers.cm.facilitator.MailChimp.activate().url,
            deactivate: jsRoutes.controllers.cm.facilitator.MailChimp.deactivate().url,

            getAvailableLists: jsRoutes.controllers.cm.facilitator.MailChimp.lists().url,

            createImport: jsRoutes.controllers.cm.facilitator.MailChimp.connect().url,
            updateImport: jsRoutes.controllers.cm.facilitator.MailChimp.update().url,
            disableImport: jsRoutes.controllers.cm.facilitator.MailChimp.disconnect().url
        });
        IntegrationSlack.plugin('.js-slack-integration', {
            activate: jsRoutes.controllers.cm.facilitator.Slack.activate().url,
            deactivate: jsRoutes.controllers.cm.facilitator.Slack.deactivate().url,

            getAvailableLists: jsRoutes.controllers.cm.facilitator.Slack.channels().url,

            create: jsRoutes.controllers.cm.facilitator.Slack.create().url,
            createImport: jsRoutes.controllers.cm.facilitator.Slack.connect().url,
            updateImport: jsRoutes.controllers.cm.facilitator.Slack.update().url,
            disableImport: jsRoutes.controllers.cm.facilitator.Slack.disconnect().url
        })
    })

});



