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
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */
package controllers.unit

import controllers.EventTypes
import helpers.BrandHelper
import models.brand.EventType
import models.service.BrandService
import models.service.brand.EventTypeService
import org.specs2.mutable._
import org.scalamock.specs2.IsolatedMockFactory
import play.api.http.Status._
import stubs.FakeServices

class EventTypesSpec extends Specification with IsolatedMockFactory {

  class TestEventTypes extends EventTypes with FakeServices {

    def callValidatedEventType(brandId: Long,
      value: EventType): Option[(String, String)] =
      validateEventType(brandId, value)

    def callValidatedUpdatedEventType(id: Long,
      value: EventType): Option[(Int, String)] =
      validateUpdatedEventType(id, value)
  }

  val controller = new TestEventTypes
  val eventTypeService = mock[EventTypeService]
  controller.eventTypeService_=(eventTypeService)
  val eventType = EventType(Some(1L), 1L, "one", None, 8, false)

  "Event type validation should fail" >> {
    "if another event type with the same name already exists" in {
      val anotherType = eventType.copy(id = Some(2L))
      (eventTypeService.findByBrand _) expects 1L returning List(anotherType)

      val res = controller.callValidatedEventType(1L, eventType)
      res map { x ⇒
        x._1 must_== "name"
        x._2 must_== "error.eventType.nameExists"
      } getOrElse ko
    }
  }
  "Event type validation on update should fail" >> {
    "if the updated event doesn't exist" in {
      (eventTypeService.find _) expects 1L returning None

      val res = controller.callValidatedUpdatedEventType(1L, eventType)
      res map { x ⇒
        x._1 must_== BAD_REQUEST
        x._2 must_== "error.eventType.notFound"
      } getOrElse ko
    }
    "if another event type with the same name already exists" in {
      val anotherType = eventType.copy(id = Some(2L))
      (eventTypeService.find _) expects 1L returning Some(eventType)
      (eventTypeService.findByBrand _) expects 1L returning List(anotherType)

      val res = controller.callValidatedUpdatedEventType(1L, eventType)
      res map { x ⇒
        x._1 must_== CONFLICT
        x._2 must_== "error.eventType.nameExists"
      } getOrElse ko
    }
  }
  "Event type validation should pass" >> {
    "if its brand exists and its name is unique for this brand" in {
      (eventTypeService.findByBrand _) expects 1L returning List()
      controller.callValidatedEventType(1L, eventType) must_== None
    }
    "if it and its brand exists and its name is unique for this brand" in {
      (eventTypeService.find _) expects 1L returning Some(eventType)
      (eventTypeService.findByBrand _) expects 1L returning List()
      controller.callValidatedUpdatedEventType(1L, eventType) must_== None
    }
  }

}
