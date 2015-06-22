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

import _root_.integration.PlayAppSpec
import controllers.{ Security, People }
import models.service.PersonService
import org.scalamock.specs2.IsolatedMockFactory
import play.api.mvc.SimpleResult
import stubs.{ FakeUserIdentity, FakeServices }

import scala.concurrent.Future

class PeopleAccessSpec extends PlayAppSpec with IsolatedMockFactory {
  class TestPeople() extends People with Security with FakeServices

  override def is = s2"""
    'Cancel' action should
      be accessible to Editors                               $e1
      be accessible to the owner of the profile              $e2
      not be accessible to Viewers                           $e3

  """

  val controller = new TestPeople()
  val personService = mock[PersonService]
  controller.personService_=(personService)

  def e1 = {
    (personService.find(_: Long)) expects 1L returning None
    val req = prepareSecuredGetRequest(FakeUserIdentity.editor, "/")
    val result: Future[SimpleResult] = controller.cancel(1L).apply(req)

    status(result) must equalTo(NOT_FOUND)
  }

  def e2 = {
    (personService.find(_: Long)) expects 1L returning None
    val req = prepareSecuredGetRequest(FakeUserIdentity.viewer, "/membership/1/cancel")
    val result: Future[SimpleResult] = controller.cancel(1L).apply(req)

    status(result) must equalTo(NOT_FOUND)
  }

  def e3 = {
    val req = prepareSecuredGetRequest(FakeUserIdentity.viewer, "/membership/2/cancel")
    val result: Future[SimpleResult] = controller.cancel(2L).apply(req)

    status(result) must equalTo(SEE_OTHER)
  }
}
