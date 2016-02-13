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

package controllers.acceptance

import controllers.Statistics
import helpers.EventHelper
import integration.PlayAppSpec
import models.License
import models.repository.{ EventRepository, LicenseRepository }
import org.joda.money.Money
import org.joda.time.LocalDate
import org.scalamock.specs2.IsolatedMockFactory
import play.api.libs.json.JsObject
import stubs.{ FakeRuntimeEnvironment, FakeRepositories, FakeSocialIdentity, FakeSecurity }

class StatisticsSpec extends PlayAppSpec with IsolatedMockFactory {

  override def is = s2"""

  After calculating number of brand facilitators per quarter results should
    include accumulated statistics per number of facilitators per quarter   $e1
    not crash when there are no facilitators                                $e2

  After calculating number of events per quarter the system should
    not crash when there are no events                                      $e3

  """
  class TestStatistics extends Statistics(FakeRuntimeEnvironment)
    with FakeRepositories
    with FakeSecurity

  val controller = new TestStatistics
  val licenseService = mock[LicenseRepository]
  controller.licenseService_=(licenseService)
  val eventService = mock[EventRepository]
  controller.eventService_=(eventService)

  val default = License(None, 1L, 1L, "1", LocalDate.parse("2011-01-01"),
    LocalDate.parse("2011-01-01"), LocalDate.now().plusYears(1),
    confirmed = true, Money.parse("EUR 100"), Some(Money.parse("EUR 100")))

  val licenses = List(
    default,
    default.copy(start = LocalDate.parse("2011-02-12")),
    default.copy(start = LocalDate.parse("2011-02-13")),
    default.copy(start = LocalDate.parse("2011-08-15")),
    default.copy(start = LocalDate.parse("2012-04-12")),
    default.copy(start = LocalDate.parse("2013-05-11")))

  def e1 = {
    (services.licenseService.findByBrand _) expects 1L returning licenses
    val res = controller.byFacilitators(1L).apply(fakeGetRequest())
    status(res) must equalTo(OK)
    val dataset = (contentAsJson(res) \ "datasets").as[List[JsObject]]
    val data = (dataset.head \ "data").as[List[Int]]
    data(1) must_== 3 // first quarter
    data(3) must_== 4 // third quarter
    data(6) must_== 5 // sixth quarter
  }

  def e2 = {
    (services.licenseService.findByBrand _) expects 1L returning List()
    val res = controller.byFacilitators(1L).apply(fakeGetRequest())
    status(res) must equalTo(OK)
    val months = (contentAsJson(res) \ "labels").as[List[String]]
    months.length must_== 0
  }

  def e3 = {
    (services.eventService.findByParameters _)
      .expects(Some(1L), None, None, None, None, None, None)
      .returning(List())
    (services.eventService.applyFacilitators _) expects List()
    val res = controller.byEvents(1L).apply(fakeGetRequest())
    status(res) must equalTo(OK)
  }
}
