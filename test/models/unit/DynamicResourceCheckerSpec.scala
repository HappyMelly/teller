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
 * If you have questions concerning this license or the applicable additional terms, you may contact
 * by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */

package models

import helpers.EventHelper
import models.service.{ BrandService, EventService, LicenseService }
import org.joda.money.Money
import org.joda.time.LocalDate
import org.specs2.mutable.Specification
import org.scalamock.specs2.MockContext
import stubs.FakeServices

class DynamicResourceCheckerSpec extends Specification {

  class TestDynamicResourceChecker(user: UserAccount) extends DynamicResourceChecker(user)
    with FakeServices {}

  val editor = UserAccount(None, 1L, "editor", None, None, None, None)
  editor.roles_=(List(UserRole.forName("editor")))
  val viewer = editor.copy(role = "viewer")
  viewer.roles_=(List(UserRole.forName("viewer")))

  "When brand permissions are checked for coordinator" >> {
    "and user is an Editor then permission should be granted" in {
      val checker = new TestDynamicResourceChecker(editor)
      checker.isBrandCoordinator(1L) must_== true
    }
    "and user is a coordinator then permission should be granted" in new MockContext {
      val brandService = mock[BrandService]
      (brandService.isCoordinator(_, _)).expects(1L, 1L).returning(true)
      val checker = new TestDynamicResourceChecker(viewer)
      checker.brandService_=(brandService)
      checker.isBrandCoordinator(1L) must_== true
    }
    "and user is a Viewer then permission should not be granted" in new MockContext {
      val brandService = mock[BrandService]
      (brandService.isCoordinator(_, _)).expects(1L, 1L).returning(false)
      val checker = new TestDynamicResourceChecker(viewer)
      checker.brandService_=(brandService)
      checker.isBrandCoordinator(1L) must_== false
    }
  }
  "When brand permissions are checked for facilitator" >> {
    "and user is an Editor then permission should be granted" in {
      val checker = new TestDynamicResourceChecker(editor)
      checker.isBrandFacilitator(1L) must_== true
    }
    "and user is a coordinator then permission should be granted" in new MockContext {
      val brandService = mock[BrandService]
      (brandService.isCoordinator(_, _)).expects(1L, 1L).returning(true)
      val checker = new TestDynamicResourceChecker(viewer)
      checker.brandService_=(brandService)
      checker.isBrandFacilitator(1L) must_== true
    }
    "and user is a facilitator then permission should be granted" in new MockContext {
      val brandService = mock[BrandService]
      (brandService.isCoordinator(_, _)).expects(1L, 1L).returning(false)
      val licenseService = mock[LicenseService]
      val license = License(None, 1L, 1L, "1", LocalDate.now(),
        LocalDate.now(), LocalDate.now(), true, Money.parse("EUR 100"),
        Some(Money.parse("EUR 100")))
      (licenseService.activeLicense _) expects (1L, 1L) returning Some(license)
      val checker = new TestDynamicResourceChecker(viewer)
      checker.licenseService_=(licenseService)
      checker.brandService_=(brandService)
      checker.isBrandFacilitator(1L) must_== true
    }
    "and user is a Viewer then permission should not be granted" in new MockContext {
      val brandService = mock[BrandService]
      (brandService.isCoordinator(_, _)).expects(1L, 1L).returning(false)
      val licenseService = mock[LicenseService]
      (licenseService.activeLicense _) expects (1L, 1L) returning None
      val checker = new TestDynamicResourceChecker(viewer)
      checker.licenseService_=(licenseService)
      checker.brandService_=(brandService)
      checker.isBrandFacilitator(1L) must_== false
    }
  }

  "When event permissions are checked for coordinator" >> {
    "and user is an Editor then permission should be granted" in {
      val checker = new TestDynamicResourceChecker(editor)
      checker.isEventCoordinator(1L) must_== true
    }
    "and user is a coordinator then permission should be granted" in new MockContext {
      val brandService = mock[BrandService]
      (brandService.isCoordinator(_, _)).expects(1L, 1L).returning(true)
      val eventService = mock[EventService]
      (eventService.find(_)).expects(1L).returning(Some(EventHelper.one))
      val checker = new TestDynamicResourceChecker(viewer)
      checker.eventService_=(eventService)
      checker.brandService_=(brandService)
      checker.isEventCoordinator(1L) must_== true
    }
    "and user is a Viewer then permission should not be granted" in new MockContext {
      val brandService = mock[BrandService]
      (brandService.isCoordinator(_, _)).expects(1L, 1L).returning(false)
      val eventService = mock[EventService]
      (eventService.find(_)).expects(1L).returning(Some(EventHelper.one))
      val checker = new TestDynamicResourceChecker(viewer)
      checker.eventService_=(eventService)
      checker.brandService_=(brandService)
      checker.isEventCoordinator(1L) must_== false
    }
    "and requested event doesn't not exist then permission should not be granted" in new MockContext {
      val eventService = mock[EventService]
      (eventService.find(_)).expects(1L).returning(None)
      val checker = new TestDynamicResourceChecker(viewer)
      checker.eventService_=(eventService)
      checker.isEventCoordinator(1L) must_== false
    }
  }
  "When event permissions are checked for facilitator" >> {
    "and user is an Editor then permission should be granted" in {
      val checker = new TestDynamicResourceChecker(editor)
      checker.isEventFacilitator(1L) must_== true
    }
    "and user is a coordinator then permission should be granted" in new MockContext {
      val event = EventHelper.one
      event.facilitatorIds_=(List())

      val brandService = mock[BrandService]
      (brandService.isCoordinator(_, _)).expects(1L, 1L).returning(true)
      val eventService = mock[EventService]
      (eventService.find(_)).expects(1L).returning(Some(event))
      val checker = new TestDynamicResourceChecker(viewer)
      checker.eventService_=(eventService)
      checker.brandService_=(brandService)
      checker.isEventFacilitator(1L) must_== true
    }
    "and user is a facilitator then permission should be granted" in new MockContext {
      val event = EventHelper.one
      event.facilitatorIds_=(List(1))

      val eventService = mock[EventService]
      (eventService.find(_)).expects(1L).returning(Some(event))
      val checker = new TestDynamicResourceChecker(viewer)
      checker.eventService_=(eventService)
      checker.isEventFacilitator(1L) must_== true
    }
    "and user is a Viewer then permission should not be granted" in new MockContext {
      val event = EventHelper.one
      event.facilitatorIds_=(List())

      val brandService = mock[BrandService]
      (brandService.isCoordinator(_, _)).expects(1L, 1L).returning(false)
      val eventService = mock[EventService]
      (eventService.find(_)).expects(1L).returning(Some(event))
      val checker = new TestDynamicResourceChecker(viewer)
      checker.eventService_=(eventService)
      checker.brandService_=(brandService)
      checker.isEventFacilitator(1L) must_== false
    }
    "and requested event doesn't not exist then permission should not be granted" in new MockContext {
      val eventService = mock[EventService]
      (eventService.find(_)).expects(1L).returning(None)
      val checker = new TestDynamicResourceChecker(viewer)
      checker.eventService_=(eventService)
      checker.isEventFacilitator(1L) must_== false
    }
  }

  "When evaluation permissions are checked for coordinator" >> {
    "and user is an Editor then permission should be granted" in {
      val checker = new TestDynamicResourceChecker(editor)
      checker.isEvaluationCoordinator(1L) must_== true
    }
    "and user is a coordinator then permission should be granted" in new MockContext {
      val brandService = mock[BrandService]
      (brandService.isCoordinator(_, _)).expects(1L, 1L).returning(true)
      val eventService = mock[EventService]
      (eventService.findByEvaluation(_)).expects(1L).returning(Some(EventHelper.one))
      val checker = new TestDynamicResourceChecker(viewer)
      checker.eventService_=(eventService)
      checker.brandService_=(brandService)
      checker.isEvaluationCoordinator(1L) must_== true
    }
    "and user is a Viewer then permission should not be granted" in new MockContext {
      val brandService = mock[BrandService]
      (brandService.isCoordinator(_, _)).expects(1L, 1L).returning(false)
      val eventService = mock[EventService]
      (eventService.findByEvaluation(_)).expects(1L).returning(Some(EventHelper.one))
      val checker = new TestDynamicResourceChecker(viewer)
      checker.eventService_=(eventService)
      checker.brandService_=(brandService)
      checker.isEvaluationCoordinator(1L) must_== false
    }
    "and requested event doesn't not exist then permission should not be granted" in new MockContext {
      val eventService = mock[EventService]
      (eventService.findByEvaluation(_)).expects(1L).returning(None)
      val checker = new TestDynamicResourceChecker(viewer)
      checker.eventService_=(eventService)
      checker.isEvaluationCoordinator(1L) must_== false
    }
  }

  "When evaluation permissions are checked for facilitator" >> {
    "and user is an Editor then permission should be granted" in {
      val checker = new TestDynamicResourceChecker(editor)
      checker.isEvaluationFacilitator(1L) must_== true
    }
    "and user is a coordinator then permission should be granted" in new MockContext {
      val event = EventHelper.one
      event.facilitatorIds_=(List())

      val brandService = mock[BrandService]
      (brandService.isCoordinator(_, _)).expects(1L, 1L).returning(true)
      val eventService = mock[EventService]
      (eventService.findByEvaluation(_)).expects(1L).returning(Some(event))
      val checker = new TestDynamicResourceChecker(viewer)
      checker.eventService_=(eventService)
      checker.brandService_=(brandService)
      checker.isEvaluationFacilitator(1L) must_== true
    }
    "and user is a facilitator then permission should be granted" in new MockContext {
      val event = EventHelper.one
      event.facilitatorIds_=(List(1))

      val eventService = mock[EventService]
      (eventService.findByEvaluation(_)).expects(1L).returning(Some(event))
      val checker = new TestDynamicResourceChecker(viewer)
      checker.eventService_=(eventService)
      checker.isEvaluationFacilitator(1L) must_== true
    }
    "and user is a Viewer then permission should not be granted" in new MockContext {
      val event = EventHelper.one
      event.facilitatorIds_=(List())

      val brandService = mock[BrandService]
      (brandService.isCoordinator(_, _)).expects(1L, 1L).returning(false)
      val eventService = mock[EventService]
      (eventService.findByEvaluation(_)).expects(1L).returning(Some(event))
      val checker = new TestDynamicResourceChecker(viewer)
      checker.eventService_=(eventService)
      checker.brandService_=(brandService)
      checker.isEvaluationFacilitator(1L) must_== false
    }
    "and requested event doesn't not exist then permission should not be granted" in new MockContext {
      val eventService = mock[EventService]
      (eventService.findByEvaluation(_)).expects(1L).returning(None)
      val checker = new TestDynamicResourceChecker(viewer)
      checker.eventService_=(eventService)
      checker.isEvaluationFacilitator(1L) must_== false
    }
  }
}