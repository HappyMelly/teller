'use strict';

import EventBlock from './../common/_event-block';
import UpcominEvents from './widgets/_upcoming-events';

$(function () {
    EventBlock.plugin('.js-event-future');
    UpcominEvents.plugin('.js-upcoming-events')
});

