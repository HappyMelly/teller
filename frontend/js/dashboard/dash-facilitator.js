'use strict';

import EventBlock from './../common/_event-block';
import UpcomingEvents from './widgets/_upcoming-events';

$(function () {
    EventBlock.plugin('.js-event-future');
    UpcomingEvents.plugin('.js-upcoming-events')
});

