
import SideMenu from './../common/_side-menu';
import FilterHistory from './widgets-credit/_filter-history';
import CreditForm from './widgets-credit/_send-credits';


SideMenu.plugin('.js-sidemenu-tabs');
FilterHistory.plugin('.js-credit-history');
CreditForm.plugin('.js-form-credit');

$('.js-sidemenu-tabs').on('hmt.menuLoadTab', ()=>{
    console.log('tab is loaded');
});



