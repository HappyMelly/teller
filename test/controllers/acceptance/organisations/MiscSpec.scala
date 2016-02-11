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

package controllers.acceptance.organisations

import _root_.integration.PlayAppSpec
import controllers.Organisations
import helpers.OrganisationHelper
import models.service.OrganisationService
import org.scalamock.specs2.IsolatedMockFactory
import play.api.libs.json.{ JsArray, Json }
import stubs.{ FakeRuntimeEnvironment, FakeServices, FakeSecurity }

class MiscSpec extends PlayAppSpec with IsolatedMockFactory {

  override def is = s2"""

  Given a query is less than 3 symbols
    when the search is made
      then an empty list should be returned                                  $e1

  Given a query is "Test" and only one organisation fits for this query
    when the search is made
      then well-formed json should be returned                               $e2
  """

  class TestOrganisations extends Organisations(FakeRuntimeEnvironment)
    with FakeServices with FakeSecurity

  val controller = new TestOrganisations
  val orgService = mock[OrganisationService]
  controller.orgService_=(orgService)

  def e1 = {
    (services.orgService.search _) expects "tt" returning List() never
    val result = controller.search(Some("tt")).apply(fakeGetRequest())
    contentAsString(result) must_== "[ ]"
  }

  def e2 = {
    val org = OrganisationHelper.make(id = Some(1L), name = "Test org")
    (services.orgService.search _) expects "Test" returning List(org)
    val result = controller.search(Some("Test")).apply(fakeGetRequest())
    val data = contentAsJson(result).as[JsArray]
    data.value.length must_== 1
    data.value(0) must_== Json.obj("id" -> 1, "name" -> "Test org",
      "countryCode" -> "RU")
  }
}