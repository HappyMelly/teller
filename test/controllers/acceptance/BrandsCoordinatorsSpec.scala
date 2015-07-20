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
import models.brand.{ BrandCoordinator, BrandNotifications }
import models.service.{ BrandService, PersonService }
import models.service.brand.BrandCoordinatorService
import org.scalamock.specs2.IsolatedMockFactory
import stubs.{ FakeRuntimeEnvironment, FakeServices, FakeUserIdentity, FakeSecurity }

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

  Given a user removes a coordinator when the brand doesn't exist
    then the system returns an error $e6

  Given a user removes a coordinator when the coordinator is the brand owner
    then the system returns an error $e7

  Given a user removes a coordinator when any parameters are provided
    then the system tries to remove the member and always returns success $e8
  """

  class TestBrands extends Brands(FakeRuntimeEnvironment)
    with FakeServices
    with FakeSecurity

  val controller = new TestBrands
  val brandTeamMemberService = mock[BrandCoordinatorService]
  controller.brandCoordinatorService_=(brandTeamMemberService)
  val brandService = mock[BrandService]
  controller.brandService_=(brandService)
  val personService = mock[PersonService]
  controller.personService_=(personService)

  def e1 = {
    val req = fakePostRequest().withFormUrlEncodedBody("personId" -> "1")
    (brandService.find(_: Long)) expects 1L returning None
    val res = controller.addCoordinator(1L).apply(req)
    status(res) must equalTo(NOT_FOUND)
    contentAsString(res) must contain("brand not found")
  }

  def e2 = {
    val req = fakePostRequest().withFormUrlEncodedBody("personId" -> "1")
    val brand = BrandHelper.one
    (brandService.find(_: Long)) expects 1L returning Some(brand)
    (personService.find(_: Long)) expects 1L returning None
    val res = controller.addCoordinator(1L).apply(req)
    status(res) must equalTo(NOT_FOUND)
    contentAsString(res) must contain("person not found")
  }

  def e3 = {
    val req = fakePostRequest().withFormUrlEncodedBody("personId" -> "1")
    val brand = BrandHelper.one
    val person = PersonHelper.one()
    val coordinator = BrandCoordinator(Some(1L), 1L, 1L)
    (brandService.find(_: Long)) expects 1L returning Some(brand)
    (personService.find(_: Long)) expects 1L returning Some(person)
    (brandService.coordinators(_)) expects 1L returning List((person, coordinator))
    val res = controller.addCoordinator(1L).apply(req)
    status(res) must equalTo(CONFLICT)
    contentAsString(res) must contain("This person is already a coordinator")
  }

  def e4 = {
    val req = fakePostRequest().withFormUrlEncodedBody("personId" -> "1")
    val brand = BrandHelper.one
    val person = PersonHelper.one()
    (brandService.find(_: Long)) expects 1L returning Some(brand)
    (personService.find(_: Long)) expects 1L returning Some(person)
    (brandService.coordinators _) expects 1L returning List()
    val coordinator = BrandCoordinator(None, 1L, 1L, BrandNotifications())
    (brandTeamMemberService.insert _) expects coordinator
    val res = controller.addCoordinator(1L).apply(req)
    status(res) must equalTo(OK)
    contentAsString(res) must contain("You added new coordinator")
    contentAsString(res) must contain("First Tester")
  }

  def e5 = {
    val req1 = fakePostRequest().withFormUrlEncodedBody("personId" -> "0")
    status(controller.addCoordinator(1L).apply(req1)) must equalTo(BAD_REQUEST)
    val req2 = fakePostRequest().
      withFormUrlEncodedBody("personId" -> "2adf")
    status(controller.addCoordinator(1L).apply(req2)) must equalTo(BAD_REQUEST)
  }

  def e6 = {
    (brandService.find(_: Long)) expects 1L returning None
    val res = controller.removeCoordinator(1L, 1L).apply(fakeDeleteRequest())
    status(res) must equalTo(NOT_FOUND)
    contentAsString(res) must contain("brand not found")
  }

  def e7 = {
    val brand = BrandHelper.one
    (brandService.find(_: Long)) expects 1L returning Some(brand)
    val res = controller.removeCoordinator(1L, 1L).apply(fakeDeleteRequest())
    status(res) must equalTo(CONFLICT)
    contentAsString(res) must contain("You cannot remove brand owner from the list of coordinators")
  }

  def e8 = {
    val brand = BrandHelper.one
    (brandService.find(_: Long)) expects 1L returning Some(brand)
    (brandTeamMemberService.delete(_, _)) expects (1L, 2L)
    val res = controller.removeCoordinator(1L, 2L).apply(fakeDeleteRequest())
    status(res) must equalTo(OK)
    contentAsString(res) must contain("You removed a coordinator")
  }
}