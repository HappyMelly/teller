/*
* Happy Melly Teller
* Copyright (C) 2013 - 2015, Happy Melly http -> //www.happymelly.com
*
* This file is part of the Happy Melly Teller.
*
* Happy Melly Teller is free software ->  you can redistribute it and/or modify
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
* along with Happy Melly Teller.  If not, see <http -> //www.gnu.org/licenses/>.
*
* If you have questions concerning this license or the applicable additional
* terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com
* or in writing Happy Melly One, Handelsplein 37, Rotterdam,
* The Netherlands, 3071 PR
*/
package controllers.unit

import controllers.Events
import org.specs2.mutable._
import org.scalamock.specs2.IsolatedMockFactory
import models.{ Event, UserAccount, UserRole, DynamicResourceChecker }
import models.brand.EventType
import models.service.brand.EventTypeService
import stubs.{ FakeServices, FakeLicenseService }
import helpers.EventHelper
import org.joda.time.LocalDate

class EventsSpec extends Specification with IsolatedMockFactory {

  class TestEvents extends Events with FakeServices {
    def callValidateEvent(event: Event, user: UserAccount): Option[List[(String, String)]] =
      validateEvent(event, user)

    def callValidateEventType(event: Event): Option[(String, String)] =
      validateEventType(event)

    def callValidateLicenses(event: Event): Option[(String, String)] =
      validateLicenses(event)
  }

  class AnotherTestEvents(checker: DynamicResourceChecker) extends TestEvents {
    override def checker(account: UserAccount): DynamicResourceChecker = checker
  }

  val controller = new TestEvents
  val user = UserAccount(None, 1L, "editor", None, None, None, None)
  user.roles_=(List(UserRole.forName("editor")))
  val licenseService = mock[FakeLicenseService]
  val eventTypeService = mock[EventTypeService]
  controller.licenseService_=(licenseService)
  controller.eventTypeService_=(eventTypeService)

  "Event validation should fail" >> {
    "if no facilitator has a valid license" in {
      (licenseService.licensees _) expects (1L, LocalDate.now()) returning List()
      val event = EventHelper.one
      val res = controller.callValidateLicenses(event)
      res.nonEmpty must_== true
      res.get._1 == "facilitatorIds"
      res.get._2 == "error.event.invalidLicense"
    }
    "if user is not allowed to facilitate the brand" in {
      class MockedChecker extends DynamicResourceChecker(user)
      val checker = mock[MockedChecker]
      (checker.isBrandFacilitator _) expects 1L returning false
      val anotherController = new AnotherTestEvents(checker)
      user.roles_=(List(UserRole.forName("viewer")))
      val event = EventHelper.one
      val res = anotherController.callValidateEvent(event, user)
      res.nonEmpty must_== true
      res.get.exists(_._1 == "brandId")
      res.get.exists(_._2 == "error.brand.invalid")
    }
    "if the event type doesn't belong to the brand" in {
      val eventType = EventType(None, 2L, "test", None, 16)
      (eventTypeService.find _) expects 1L returning Some(eventType)
      val event = EventHelper.one
      val res = controller.callValidateEventType(event)
      res.nonEmpty must_== true
      res.get._1 must_== "eventTypeId"
      res.get._2 must_== "error.eventType.wrongBrand"
    }
    "if the event type doesn't exist" in {
      (eventTypeService.find _) expects 1L returning None
      val event = EventHelper.one
      val res = controller.callValidateEventType(event)
      res.nonEmpty must_== true
      res.get._1 must_== "eventTypeId"
      res.get._2 must_== "error.eventType.notFound"
    }
  }
  "On event validation several errors should be returned" >> {
    "when several validations fail" in {
      class YetAnotherTestEvent extends TestEvents {
        override def validateEventType(event: Event): Option[(String, String)] =
          Some(("eventTypeId", "error.eventType.notFound"))

        override def validateLicenses(event: Event): Option[(String, String)] =
          Some(("facilitatorIds", "error.event.invalidLicense"))
      }
      val controller = new YetAnotherTestEvent
      val event = EventHelper.one
      val res = controller.callValidateEvent(event, user)
      res.nonEmpty must_== true
      res.get.exists(_._1 == "eventTypeId") must_== true
      res.get.exists(_._2 == "error.eventType.notFound") must_== true
      res.get.exists(_._1 == "facilitatorIds") must_== true
      res.get.exists(_._2 == "error.event.invalidLicense") must_== true
    }
  }

}