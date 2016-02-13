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

package controllers.integration

import controllers.Enrollment
import helpers.{ MemberHelper, OrganisationHelper, PersonHelper }
import integration.PlayAppSpec
import models.{ Member, Organisation, Person }
import org.joda.money.Money
import play.api.mvc.Action
import stubs.FakeRepositories
import stubs.services.FakeIntegrations

class EnrollmentSpec extends PlayAppSpec {

  class TestEnrollment extends Enrollment
      with FakeIntegrations
      with FakeRepositories {

    /**
     * Renders welcome screen for existing users with two options:
     * Become a funder and Become a supporter
     */
    def welcome = Action { Ok("") }

    /**
     * Renders congratulations screen
     * If orgId is not empty payment is done for the organisation
     *
     * @param orgId Organisation identifier
     */
    def congratulations(orgId: Option[Long]) = Action { Ok("") }

    /**
     * Renders payment form
     * If orgId is not empty payment is done for the organisation
     *
     * @param orgId Organisation identifier
     */
    def payment(orgId: Option[Long]) = Action { Ok("") }

    /**
     * Charges card
     */
    def charge = Action { Ok("") }

    def callNotify(person: Person,
      org: Option[Organisation],
      member: Member) =
      notify(person, org, member)

    def callSubscribe(person: Person, member: Member) = subscribe(person, member)
  }

  val controller = new TestEnrollment

  "Method 'notify'" should {
    "send Slack and Email notifications for a new person member" in {
      val person = PersonHelper.one()
      val member = MemberHelper.make(Some(1L), 1L, funder = false, person = true)
      controller.callNotify(person, None, member)

      controller.slack.message must contain("Hooray!! We have *new Supporter*")
      controller.slack.message must contain("First Tester")
      controller.slack.message must contain("/person/1")
      controller.email.to.exists(_.fullName == "First Tester") must_== true
      controller.email.cc must_== None
      controller.email.bcc must_== None
      controller.email.subject must_== "Welcome to Happy Melly network"
      controller.email.body must contain("Hi First,")
      controller.email.body must contain("Join Slack discussions")
      controller.email.body must contain(member.profileUrl)
    }
    "send Slack and Email notifications for a new organisation member" in {
      val person = PersonHelper.one()
      val org = OrganisationHelper.two
      val member = MemberHelper.make(Some(1L), 2L, funder = true, person = false)
      controller.callNotify(person, Some(org), member)

      controller.slack.message must contain("Hooray!! We have *new Funder*")
      controller.slack.message must contain("Two")
      controller.slack.message must contain("/organization/2")
      controller.email.to.exists(_.fullName == "First Tester") must_== true
      controller.email.cc must_== None
      controller.email.bcc must_== None
      controller.email.subject must_== "Welcome to Happy Melly network"
      controller.email.body must contain("Hi Two,")
      controller.email.body must contain("Join Slack discussions")
      controller.email.body must contain(member.profileUrl)
    }
  }

  "Method 'subscribe'" should {
    "subscribe a person to a MailChimp list" in {
      val member = MemberHelper.make(Some(1L), 1L, funder = true, person = true)
      controller.callSubscribe(PersonHelper.one(), member)
      controller.mailChimp.funder must_== true
      controller.mailChimp.listId must_== "testId"
      controller.mailChimp.personName must_== "First Tester"
    }
  }
}
