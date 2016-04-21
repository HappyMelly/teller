'use strict';

import IntegrationMailchimp from "./person-widgets/_integration";


$(function(){
    App.events.sub('hmt.tab.shown', () =>{
        IntegrationMailchimp.plugin('.js-mailchimp-integration', {
            activate: '/integration/activate',
            deactivate: '/integration/deactivate',

            getAvailableLists: '/lists/',

            createImport: '/imports/create',
            updateImport: '/imports/update/1',
            disableImport: '/imports/disable/1'
        })
    })

});



