'use strict';

import SetCredits from "./widgets/_set-credits";

$(function(){
    App.events
        .sub('hmt.tab.shown', function(){
            // Set credits
            SetCredits.plugin('.js-set-credits');

            //tab api
            $('[data-toggle="tooltip"]').tooltip();

        })
});