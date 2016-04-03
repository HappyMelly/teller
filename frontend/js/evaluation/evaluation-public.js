'use strict';

import EvaluationForm from "./public-widgets/_evaluation-form";

$(function(){
    EvaluationForm.plugin('.js-evaluation-form');
    $('[data-evaluat-birth]').inputmask("99.99.9999");
});