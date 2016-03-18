
import AsyncTab from './../common/_async-tabs';
import FilterHistory from './widgets/_filter-history';
import CreditForm from './widgets/_send-credits';


AsyncTab.plugin('.js-credits-tabs');

$('.js-credit-page')
    .on('hmt.tab.shown', ()=> {
        FilterHistory.plugin('.js-credit-history');
        CreditForm.plugin('.js-form-credit');
    });



