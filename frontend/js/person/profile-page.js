'use strict';

import IntegrationMailchimp from "./person-widgets/_integration";


$(function(){
    App.events.sub('hmt.tab.shown', () =>{
        IntegrationMailchimp.plugin('.js-mailchimp-integration', {
            activate: '/integration/activate',
            deactivate: '/integration/deactivate',
            getLists: '/lists',
            createList: '/lists/create',
            disableList: '/lists/disable/1',           
        })
    })

});



