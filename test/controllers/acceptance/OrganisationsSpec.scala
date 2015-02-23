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

import controllers.Organisations
import helpers.{ PersonHelper, OrganisationHelper }
import integration.PlayAppSpec
import models.{ Organisation, Person, SocialProfile }
import org.scalamock.specs2.{ MockContext, IsolatedMockFactory }
import play.api.mvc.SimpleResult
import stubs._

import scala.concurrent.Future

class TestOrganisations() extends Organisations with FakeServices

class OrganisationsSpec extends PlayAppSpec with IsolatedMockFactory {

  def setupDb() {}
  def cleanupDb() {}

  val personService = mock[FakePersonService]
  val orgService = mock[FakeOrganisationService]
  val productService = mock[FakeProductService]
  val contributionService = mock[FakeContributionService]
  val org = OrganisationHelper.one
  val id = 1L

  trait DefaultMockContext extends MockContext {
    truncateTables()
    (contributionService.contributions(_, _)).expects(id, false).returning(List())
    (orgService.find _).expects(id).returning(Some(org))
    (productService.findAll _).expects().returning(List())
  }

  override def is = s2"""

  When an organisation is not a member, 'Become a Member' button should
    be visible to members of this organisation                              $e1
    be invisible to Editors                                                 $e2
    be invisible to Viewers                                                 $e3
  """

  def e1 = new DefaultMockContext {
    // we insert an org object here to prevent crashing on account retrieval
    // when @org.deletable is called
    org.insert
    org.members_=(List(PersonHelper.one()))

    val controller = fakedController()
    val req = prepareSecuredGetRequest(StubUserIdentity.viewer, "/organisation/1")
    val result: Future[SimpleResult] = controller.details(id).apply(req)

    status(result) must equalTo(OK)
    contentAsString(result) must contain("Become a Member")
    contentAsString(result) must contain("/membership/welcome")
  }

  def e2 = new DefaultMockContext {
    // we insert an org object here to prevent crashing on account retrieval
    // when @org.deletable is called
    org.insert
    org.members_=(List())
    val controller = fakedController()
    val req = prepareSecuredGetRequest(StubUserIdentity.editor, "/organisation/1")
    val result: Future[SimpleResult] = controller.details(id).apply(req)

    status(result) must equalTo(OK)
    contentAsString(result) must not contain "Become a Member"
    contentAsString(result) must not contain "/membership/welcome"
  }

  def e3 = new DefaultMockContext {
    // we insert an org object here to prevent crashing on account retrieval
    // when @org.deletable is called
    org.insert
    org.members_=(List())
    val controller = fakedController()
    val req = prepareSecuredGetRequest(StubUserIdentity.viewer, "/organisation/1")
    val result: Future[SimpleResult] = controller.details(id).apply(req)

    status(result) must equalTo(OK)
    contentAsString(result) must not contain "Become a Member"
    contentAsString(result) must not contain "/membership/welcome"
  }

  private def fakedController(): TestOrganisations = {
    val controller = new TestOrganisations()
    controller.contributionService_=(contributionService)
    controller.orgService_=(orgService)
    controller.personService_=(personService)
    controller.productService_=(productService)
    controller
  }
}
