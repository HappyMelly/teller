'use strict';

import SetCredits from "./widgets/_set-credits";
import SetApi from "./widgets/_set-api";

$(function(){
    App.events
        .sub('hmt.tab.shown', function(){
            // Set credits
            SetCredits.plugin('.js-set-credits');

            //tab api
            $('[data-toggle="tooltip"]').tooltip();
            SetApi.plugin('.js-set-api');

        })
});