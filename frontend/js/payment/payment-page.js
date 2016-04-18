'use strict';

import PaymentForm from './payment-widgets/_payment-form';
import SupportTable from './payment-widgets/_supporters-table';


$(function(){
    PaymentForm.plugin('.js-payment-form');
    SupportTable.plugin('.js-support-table')
});
