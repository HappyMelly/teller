
import AsyncTab from './../common/_async-tabs';
import FilterHistory from './widgets/_filter-history';
import CreditForm from './widgets/_send-credits';

$(function(){
    AsyncTab.plugin('.js-credits-tabs');

   App.events
        .sub('hmt.asynctab.shown', ()=> {
            FilterHistory.plugin('.js-credit-history');
            CreditForm.plugin('.js-form-credit');
        });
});






