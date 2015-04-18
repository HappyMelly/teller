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

import controllers.TellerResourceHandler
import helpers.EventHelper
import models.UserRole.DynamicRole
import models.service.BrandService
import models.{ UserAccount, UserRole }
import org.scalamock.specs2.MockContext
import org.specs2.mutable.Specification
import stubs.{ FakeServices, StubEventService }

class TellerResourceHandlerSpec extends Specification {

  class TestTellerResourceHandler(account: Option[UserAccount])
    extends TellerResourceHandler(account) with FakeServices {

    def callCheckBrandPermission(account: UserAccount, meta: String, url: String): Boolean =
      checkBrandPermission(account, meta, url)

    def callCheckEventPermission(account: UserAccount, meta: String, url: String): Boolean =
      checkEventPermission(account, meta, url)

    def callCheckEvaluationPermission(account: UserAccount, meta: String, url: String): Boolean =
      checkEvaluationPermission(account, meta, url)
  }

  val handler = new TestTellerResourceHandler(None)
  val editor = UserAccount(None, 1L, "editor", None, None, None, None)
  editor.roles_=(List(UserRole.forName("editor")))
  val viewer = editor.copy(role = "viewer")
  viewer.roles_=(List(UserRole.forName("viewer")))

  "When brand permissions are checked for coordinator" >> {
    "and user is an Editor then permission should be granted" in {
      handler.callCheckBrandPermission(editor, DynamicRole.Coordinator, "/1") must_== true
    }
    "and user is a coordinator then permission should be granted" in new MockContext {
      val brandService = mock[BrandService]
      (brandService.isCoordinator(_, _)).expects(1L, 1L).returning(true)
      handler.brandService_=(brandService)
      handler.callCheckBrandPermission(viewer, DynamicRole.Coordinator, "/1") must_== true
    }
    "and user is a Viewer then permission should not be granted" in new MockContext {
      val brandService = mock[BrandService]
      (brandService.isCoordinator(_, _)).expects(1L, 1L).returning(false)
      handler.brandService_=(brandService)
      handler.callCheckBrandPermission(viewer, DynamicRole.Coordinator, "/1") must_== false
    }
    "and url doesn't containt brand id then permission should not be granted" in {
      handler.callCheckBrandPermission(editor, DynamicRole.Coordinator, "/") must_== false
    }
  }
  "When brand permissions are checked not for coordinator" >> {
    "then permission should not be granted" in {
      handler.callCheckBrandPermission(editor, "anything", "/1") must_== false
    }
  }

  "When event permissions are checked for coordinator" >> {
    "and user is an Editor then permission should be granted" in {
      handler.callCheckEventPermission(editor, DynamicRole.Coordinator, "/1") must_== true
    }
    "and user is a coordinator then permission should be granted" in new MockContext {
      val brandService = mock[BrandService]
      (brandService.isCoordinator(_, _)).expects(1L, 1L).returning(true)
      val eventService = mock[StubEventService]
      (eventService.find(_)).expects(1L).returning(Some(EventHelper.one))
      handler.eventService_=(eventService)
      handler.brandService_=(brandService)
      handler.callCheckEventPermission(viewer, DynamicRole.Coordinator, "/1") must_== true
    }
    "and user is a Viewer then permission should not be granted" in new MockContext {
      val brandService = mock[BrandService]
      (brandService.isCoordinator(_, _)).expects(1L, 1L).returning(false)
      val eventService = mock[StubEventService]
      (eventService.find(_)).expects(1L).returning(Some(EventHelper.one))
      handler.eventService_=(eventService)
      handler.brandService_=(brandService)
      handler.callCheckEventPermission(viewer, DynamicRole.Coordinator, "/1") must_== false
    }
    "and requested event doesn't not exist then permission should not be granted" in new MockContext {
      val eventService = mock[StubEventService]
      (eventService.find(_)).expects(1L).returning(None)
      handler.eventService_=(eventService)

      handler.callCheckEventPermission(viewer, DynamicRole.Coordinator, "/1") must_== false
    }
    "and url doesn't contain event id then permission should not be granted" in {
      handler.callCheckEventPermission(editor, DynamicRole.Coordinator, "/") must_== false
    }
  }
  "When event permissions are checked for facilitator" >> {
    "and user is an Editor then permission should be granted" in {
      handler.callCheckEventPermission(editor, DynamicRole.Facilitator, "/1") must_== true
    }
    "and user is a coordinator then permission should be granted" in new MockContext {
      val event = EventHelper.one
      event.facilitatorIds_=(List())

      val brandService = mock[BrandService]
      (brandService.isCoordinator(_, _)).expects(1L, 1L).returning(true)
      val eventService = mock[StubEventService]
      (eventService.find(_)).expects(1L).returning(Some(event))
      handler.eventService_=(eventService)
      handler.brandService_=(brandService)
      handler.callCheckEventPermission(viewer, DynamicRole.Facilitator, "/1") must_== true
    }
    "and user is a facilitator then permission should be granted" in new MockContext {
      val event = EventHelper.one
      event.facilitatorIds_=(List(1))

      val eventService = mock[StubEventService]
      (eventService.find(_)).expects(1L).returning(Some(event))
      handler.eventService_=(eventService)
      handler.callCheckEventPermission(viewer, DynamicRole.Facilitator, "/1") must_== true
    }
    "and user is a Viewer then permission should not be granted" in new MockContext {
      val event = EventHelper.one
      event.facilitatorIds_=(List())

      val brandService = mock[BrandService]
      (brandService.isCoordinator(_, _)).expects(1L, 1L).returning(false)
      val eventService = mock[StubEventService]
      (eventService.find(_)).expects(1L).returning(Some(event))
      handler.eventService_=(eventService)
      handler.brandService_=(brandService)
      handler.callCheckEventPermission(viewer, DynamicRole.Facilitator, "/1") must_== false
    }
    "and requested event doesn't not exist then permission should not be granted" in new MockContext {
      val eventService = mock[StubEventService]
      (eventService.find(_)).expects(1L).returning(None)
      handler.eventService_=(eventService)

      handler.callCheckEventPermission(viewer, DynamicRole.Facilitator, "/1") must_== false
    }
    "and url doesn't contain event id then permission should not be granted" in {
      handler.callCheckEventPermission(editor, DynamicRole.Facilitator, "/") must_== false
    }
  }
  "When brand permissions are checked for random role" >> {
    "then permission should not be granted" in {
      handler.callCheckEventPermission(editor, "anything", "/1") must_== false
    }
  }

  "When evaluation permissions are checked for coordinator" >> {
    "and user is an Editor then permission should be granted" in {
      handler.callCheckEvaluationPermission(editor, DynamicRole.Coordinator, "/1") must_== true
    }
    "and user is a coordinator then permission should be granted" in new MockContext {
      val brandService = mock[BrandService]
      (brandService.isCoordinator(_, _)).expects(1L, 1L).returning(true)
      val eventService = mock[StubEventService]
      (eventService.findByEvaluation(_)).expects(1L).returning(Some(EventHelper.one))
      handler.eventService_=(eventService)
      handler.brandService_=(brandService)
      handler.callCheckEvaluationPermission(viewer, DynamicRole.Coordinator, "/1") must_== true
    }
    "and user is a Viewer then permission should not be granted" in new MockContext {
      val brandService = mock[BrandService]
      (brandService.isCoordinator(_, _)).expects(1L, 1L).returning(false)
      val eventService = mock[StubEventService]
      (eventService.findByEvaluation(_)).expects(1L).returning(Some(EventHelper.one))
      handler.eventService_=(eventService)
      handler.brandService_=(brandService)
      handler.callCheckEvaluationPermission(viewer, DynamicRole.Coordinator, "/1") must_== false
    }
    "and requested event doesn't not exist then permission should not be granted" in new MockContext {
      val eventService = mock[StubEventService]
      (eventService.findByEvaluation(_)).expects(1L).returning(None)
      handler.eventService_=(eventService)

      handler.callCheckEvaluationPermission(viewer, DynamicRole.Coordinator, "/1") must_== false
    }
    "and url doesn't contain event id then permission should not be granted" in {
      handler.callCheckEvaluationPermission(editor, DynamicRole.Coordinator, "/") must_== false
    }
  }
  "When evaluation permissions are checked for facilitator" >> {
    "and user is an Editor then permission should be granted" in {
      handler.callCheckEvaluationPermission(editor, DynamicRole.Facilitator, "/1") must_== true
    }
    "and user is a coordinator then permission should be granted" in new MockContext {
      val event = EventHelper.one
      event.facilitatorIds_=(List())

      val brandService = mock[BrandService]
      (brandService.isCoordinator(_, _)).expects(1L, 1L).returning(true)
      val eventService = mock[StubEventService]
      (eventService.findByEvaluation(_)).expects(1L).returning(Some(event))
      handler.eventService_=(eventService)
      handler.brandService_=(brandService)
      handler.callCheckEvaluationPermission(viewer, DynamicRole.Facilitator, "/1") must_== true
    }
    "and user is a facilitator then permission should be granted" in new MockContext {
      val event = EventHelper.one
      event.facilitatorIds_=(List(1))

      val eventService = mock[StubEventService]
      (eventService.findByEvaluation(_)).expects(1L).returning(Some(event))
      handler.eventService_=(eventService)
      handler.callCheckEvaluationPermission(viewer, DynamicRole.Facilitator, "/1") must_== true
    }
    "and user is a Viewer then permission should not be granted" in new MockContext {
      val event = EventHelper.one
      event.facilitatorIds_=(List())

      val brandService = mock[BrandService]
      (brandService.isCoordinator(_, _)).expects(1L, 1L).returning(false)
      val eventService = mock[StubEventService]
      (eventService.findByEvaluation(_)).expects(1L).returning(Some(event))
      handler.eventService_=(eventService)
      handler.brandService_=(brandService)
      handler.callCheckEvaluationPermission(viewer, DynamicRole.Facilitator, "/1") must_== false
    }
    "and requested event doesn't not exist then permission should not be granted" in new MockContext {
      val eventService = mock[StubEventService]
      (eventService.findByEvaluation(_)).expects(1L).returning(None)
      handler.eventService_=(eventService)

      handler.callCheckEvaluationPermission(viewer, DynamicRole.Facilitator, "/1") must_== false
    }
    "and url doesn't contain event id then permission should not be granted" in {
      handler.callCheckEvaluationPermission(editor, DynamicRole.Facilitator, "/") must_== false
    }
  }
  "When brand permissions are checked for random role" >> {
    "then permission should not be granted" in {
      handler.callCheckEvaluationPermission(editor, "anything", "/1") must_== false
    }
  }
}
