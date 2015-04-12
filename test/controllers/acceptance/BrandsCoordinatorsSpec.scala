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

import controllers.Brands
import helpers.{ BrandHelper, PersonHelper }
import integration.PlayAppSpec
import models.service.BrandService
import models.service.brand.BrandTeamMemberService
import org.scalamock.specs2.IsolatedMockFactory
import stubs.{ FakePersonService, FakeServices, FakeUserIdentity }

/**
 * Tests Brands controller methods, managing brand coordinators
 */
class BrandsCoordinatorsSpec extends PlayAppSpec with IsolatedMockFactory {
  override def is = s2"""

  Given a user adds a person as new coordinator when the brand doesn't exist
    then the system returns an error $e1

  Given a user adds a person as new coordinator when this person doesn't exist
    then the system returns an error $e2

  Given a user adds a person as new coordinator when this person is already a member
    then the system returns an error $e3

  Given a user adds a person as new coordinator when this person exists and is not a member
    then the system adds the person $e4

  Given a user adds a person as new coordinator when person id is not > 0
    then the system returns an error $e5


  Given a user removes a member when any parameters are provided
    then the system tries to remove the member and always returns success $e6
  """

  class TestBrands extends Brands with FakeServices

  val controller = new TestBrands
  val brandTeamMemberService = mock[BrandTeamMemberService]
  controller.brandTeamMemberService_=(brandTeamMemberService)
  val brandService = mock[BrandService]
  controller.brandService_=(brandService)
  val personService = mock[FakePersonService]
  controller.personService_=(personService)

  def e1 = {
    val req = prepareSecuredPostRequest(FakeUserIdentity.editor, "/").
      withFormUrlEncodedBody("personId" -> "1")
    (brandService.find(_: Long)).expects(1L).returning(None)
    val res = controller.addCoordinator(1L).apply(req)
    status(res) must equalTo(NOT_FOUND)
    contentAsString(res) must contain("brand not found")
  }

  def e2 = {
    val req = prepareSecuredPostRequest(FakeUserIdentity.editor, "/").
      withFormUrlEncodedBody("personId" -> "1")
    val brand = BrandHelper.one
    (brandService.find(_: Long)).expects(1L).returning(Some(brand))
    (personService.find(_: Long)).expects(1L).returning(None)
    val res = controller.addCoordinator(1L).apply(req)
    status(res) must equalTo(NOT_FOUND)
    contentAsString(res) must contain("person not found")
  }

  def e3 = {
    val req = prepareSecuredPostRequest(FakeUserIdentity.editor, "/").
      withFormUrlEncodedBody("personId" -> "1")
    val brand = BrandHelper.one
    val person = PersonHelper.one()
    (brandService.find(_: Long)).expects(1L).returning(Some(brand))
    (personService.find(_: Long)).expects(1L).returning(Some(person))
    (brandService.coordinators(_)).expects(1L).returning(List(person))
    val res = controller.addCoordinator(1L).apply(req)
    status(res) must equalTo(CONFLICT)
    contentAsString(res) must contain("This person is already a coordinator")
  }

  def e4 = {
    val req = prepareSecuredPostRequest(FakeUserIdentity.editor, "/").
      withFormUrlEncodedBody("personId" -> "1")
    val brand = BrandHelper.one
    val person = PersonHelper.one()
    (brandService.find(_: Long)).expects(1L).returning(Some(brand))
    (personService.find(_: Long)).expects(1L).returning(Some(person))
    (brandService.coordinators(_)).expects(1L).returning(List())
    (brandTeamMemberService.insert(_, _)).expects(1L, 1L)
    val res = controller.addCoordinator(1L).apply(req)
    status(res) must equalTo(OK)
    contentAsString(res) must contain("You added new coordinator")
    contentAsString(res) must contain("First Tester")
  }

  def e5 = {
    val req1 = prepareSecuredPostRequest(FakeUserIdentity.editor, "/").
      withFormUrlEncodedBody("personId" -> "0")
    status(controller.addCoordinator(1L).apply(req1)) must equalTo(BAD_REQUEST)
    val req2 = prepareSecuredPostRequest(FakeUserIdentity.editor, "/").
      withFormUrlEncodedBody("personId" -> "2adf")
    status(controller.addCoordinator(1L).apply(req2)) must equalTo(BAD_REQUEST)
  }

  def e6 = {
    val req = prepareSecuredDeleteRequest(FakeUserIdentity.editor, "/")
    (brandTeamMemberService.delete(_, _)).expects(1L, 1L)
    val res = controller.removeCoordinator(1L, 1L).apply(req)
    status(res) must equalTo(OK)
    contentAsString(res) must contain("You removed a coordinator")
  }
}