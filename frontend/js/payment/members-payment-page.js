'use strict';

import CardForm from './payment-widgets/_card-form';
import FeeForm from './payment-widgets/_fee-form';
import SupportTable from './payment-widgets/_supporters-table';


$(function(){
    FeeForm.plugin('.js-fee-form');
    CardForm.plugin('.js-card-form');
    SupportTable.plugin('.js-support-table')
})