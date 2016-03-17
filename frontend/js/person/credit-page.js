
import SideMenu from './../common/_side-menu';
import FilterHistory from './widgets-credit/_filter-history';
import CreditForm from './widgets-credit/_send-credits';


SideMenu.interface('.js-sidemenu-tabs');
FilterHistory.interface('.js-credit-history');
CreditForm.interface('.js-form-credit');

$('.js-sidemenu-tabs').on('hmt.menuLoadTab', ()=>{
    console.log('tab is loaded');
});



