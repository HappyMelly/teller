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
package controllers

import helpers.{ EventHelper, BrandHelper }
import integration.{ PlayAppSpec }
import models.{ Event, Brand }
import models.event.{ EventServiceTrait, EventService }
import org.specs2.mutable._
import org.scalamock.specs2.MockContext
import org.joda.time.LocalDate
import play.api.test.{ FakeHeaders, FakeRequest }
import play.api.test.Helpers._
import play.api.mvc._
import scala.concurrent.Future

class TestEventsApi() extends Controller with EventsApi with FakeApiAuthentication

class EventApiSpec extends PlayAppSpec {

  def setupDb() {
    BrandHelper.defaultBrand.insert
    EventHelper.addEvents(BrandHelper.defaultBrand.code)
    EventHelper.addEvents("MGT30")
  }

  def cleanupDb() {
    Brand.find(BrandHelper.defaultBrand.code).map(_.brand.delete())
    EventService.findAll.map(_.delete())
  }
  lazy val event = EventHelper.makeEvent(
    id = Some(1L),
    title = Some("Daily Workshop"),
    city = Some("spb"),
    startDate = Some(LocalDate.parse("2014-05-12")))

  "Event details API call" should {
    "return event details in JSON format" in new MockContext {
      val s = stub[EventServiceTrait]
      (s.find _).when(toMockParameter(event.id.get)).returns(Some(event))
      val controller = new TestEventsApi()
      val result: Future[SimpleResult] = controller.event(1).apply(FakeRequest())
      status(result) must equalTo(OK)
    }
  }

}
