'use strict';

import EventBlock from './../common/_event-block';
import EventSchedule from './widgets/_event-schedule';

$(function () {
    EventBlock.plugin('.js-event-future');
    EventSchedule.plugin('.js-upcoming-events')
});

