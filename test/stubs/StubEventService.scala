/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2015, Happy Melly http://www.happymelly.com
 *
 * This file is part of the Happy Melly Teller.
 *
 * Happy Melly Teller is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Happy Melly Teller is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Happy Melly Teller.  If not, see <http://www.gnu.org/licenses/>.
 *
 * If you have questions concerning this license or the applicable additional
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com
 * or in writing Happy Melly One, Handelsplein 37, Rotterdam,
 * The Netherlands, 3071 PR
 */
package stubs

import models.service.EventService
import models.{ Person, Event }
import helpers.{ PersonHelper, EventHelper }
import org.joda.time.LocalDate

class StubEventService extends EventService {

  override def find(id: Long): Option[Event] = id match {
    case 1 ⇒
      val event = EventHelper.makeEvent(
        id = Some(1),
        startDate = Some(LocalDate.parse("2015-01-20")),
        endDate = Some(LocalDate.parse("2015-01-20")))
      event.facilitators_=(List[Person](PersonHelper.one(), PersonHelper.two()))
      Some(event)
    case _ ⇒ None
  }
}
