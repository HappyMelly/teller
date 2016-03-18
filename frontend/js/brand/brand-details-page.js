'use strict';

import SetCredits from "./widgets/_set-credist";

$(function(){
    $('.js-brand-detail')
        .on('hmt.tab.shown', function(){
            SetCredits.plugin('.js-set-credits');
        })
});