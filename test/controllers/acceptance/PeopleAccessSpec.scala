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

import controllers.{ Security, People }
import integration.PlayAppSpec
import play.api.mvc.SimpleResult
import stubs.{ StubUserIdentity, FakeServices }

import scala.concurrent.Future

class PeopleAccessSpec extends PlayAppSpec {
  class TestPeople() extends People with Security with FakeServices

  def setupDb() {}
  def cleanupDb() {}

  override def is = s2"""
    'Cancel' action should
      be accessible to Editors                               $e7
      be accessible to the owner of the profile              $e8
      not be accessible to Viewers                           $e9
  """

  val controller = new TestPeople()

  def e7 = {
    val req = prepareSecuredGetRequest(StubUserIdentity.editor, "/")
    val result: Future[SimpleResult] = controller.cancel(1L).apply(req)

    status(result) must equalTo(NOT_FOUND)
  }

  def e8 = {
    val req = prepareSecuredGetRequest(StubUserIdentity.viewer, "/membership/1/cancel")
    val result: Future[SimpleResult] = controller.cancel(1L).apply(req)

    status(result) must equalTo(NOT_FOUND)
  }

  def e9 = {
    val req = prepareSecuredGetRequest(StubUserIdentity.viewer, "/membership/2/cancel")
    val result: Future[SimpleResult] = controller.cancel(2L).apply(req)

    status(result) must equalTo(SEE_OTHER)
  }
}
