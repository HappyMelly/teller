/*
* Happy Melly Teller
* Copyright (C) 2013 - 2015, Happy Melly http -> //www.happymelly.com
*
* This file is part of the Happy Melly Teller.
*
* Happy Melly Teller is free software ->  you can redistribute it and/or modify
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
* along with Happy Melly Teller.  If not, see <http -> //www.gnu.org/licenses/>.
*
* If you have questions concerning this license or the applicable additional
* terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com
* or in writing Happy Melly One, Handelsplein 37, Rotterdam,
* The Netherlands, 3071 PR
*/
package controllers.acceptance.people

import _root_.integration.PlayAppSpec
import controllers.People
import helpers.{MemberHelper, PersonHelper}
import models.payment.Record
import models.service.{PaymentRecordService, PersonService}
import org.joda.money.Money
import org.scalamock.specs2.IsolatedMockFactory
import stubs._

class TabsSpec extends PlayAppSpec with IsolatedMockFactory {

  override def is = s2"""

  Given a person is a not member and a user is a viewer
    when the person's membership tab is requested
      then it should contain "Person is not a member"                        $e1

  Given a person is a member and a user is an admin
    when the person's membership tab is requested
      then it should contain a list of payments                              $e2

  Given a person is a member and a user is an Admin
    when the person's membership tab is requested
      then a list of payments shouldn't have links to remote payments        $e3

  Given a person is a member, a user is an Admin and subscription exists
    when the person's membership tab is requested
      then 'Stop subscription' button should be visible and
      'No automatic renewal' button should not be visible                    $e4

  Given a person is a member, a user is an Admin
      and subscription doesn't exist
    when the person's membership tab is requested
      then 'Stop subscription' button should NOT be visible and
      'No automatic renewal' button should be visible                        $e6

  Given a user is a viewer and an owner of the profile
      and subscription doesn't exist
    when the user's membership tab is requested
      then 'Stop subscription' button should NOT be visible and
      'No automatic renewal' button should be visible                        $e7

  Given a user is a viewer and is NOT an owner of the profile
      and subscription exists
    when the user's membership tab is requested
      then 'Stop subscription' button should NOT be visible and
      'No automatic renewal' button should NOT be visible                    $e8

  Given a user is a viewer and a person is a membership
    when the user's membership tab is requested
      then membership related buttons shouldn't be visible                   $e9

  Given a user is an Admin and a person has a membership
    when the user's membership tab is requested
      then membership related buttons should be visible                     $e10
  """
  class TestPeople() extends People(FakeRuntimeEnvironment)
    with FakeSecurity with FakeServices

  val controller = new TestPeople()
  val personService = mock[PersonService]
  val paymentService = mock[PaymentRecordService]
  controller.personService_=(personService)
  controller.paymentRecordService_=(paymentService)

  val payments = List(
    Record("remote1", 1L, 1L, person = true, "One Year Membership Fee", Money.parse("EUR 100")),
    Record("remote2", 1L, 1L, person = true, "One Year Membership Fee 2", Money.parse("EUR 200")))
  val person = PersonHelper.one
  person.member_=(MemberHelper.make(Some(1L), 1L, person = true, funder = true))

  def e1 = {
    (personService.find(_: Long)) expects 1L returning Some(PersonHelper.one)
    val res = controller.renderTabs(1L, "membership").apply(fakeGetRequest())

    contentAsString(res) must contain("Person is not a member")
  }

  def e2 = {
    (paymentService.findByPerson _) expects 1L returning payments
    (personService.find(_: Long)) expects 1L returning Some(person)
    controller.identity_=(FakeSocialIdentity.admin)
    val res = controller.renderTabs(1L, "membership").apply(fakeGetRequest())
    val strings = Seq("One Year Membership Fee", "<td>100.00</td>",
      "One Year Membership Fee 2", "<td>200.00</td>")

    strings.foreach(contentAsString(res) must contain(_))
    ok
  }

  def e3 = {
    (paymentService.findByPerson _) expects 1L returning payments
    (personService.find(_: Long)) expects 1L returning Some(person)
    controller.identity_=(FakeSocialIdentity.admin)
    val res = controller.renderTabs(1L, "membership").apply(fakeGetRequest())

    contentAsString(res) must contain("One Year Membership Fee")
    contentAsString(res) must contain("One Year Membership Fee 2")
  }

  def e4 = {
    (personService.find(_: Long)) expects 1L returning Some(person)
    (paymentService.findByPerson _) expects 1L returning List()
    controller.identity_=(FakeSocialIdentity.admin)
    val res = controller.renderTabs(1L, "membership").apply(fakeGetRequest("/1"))

    contentAsString(res) must contain("stop automatic renewal")
    contentAsString(res) must contain("person/1/cancel")
    contentAsString(res) must not contain "Renew subscription"
    contentAsString(res) must not contain "Automatic renewal is stopped"
  }

  def e6 = {
    val member = MemberHelper.make(Some(1L), 1L, person = true, funder = true,
      renewal = false)
    person.member_=(member)
    (personService.find(_: Long)) expects 1L returning Some(person)
    (paymentService.findByPerson _) expects 1L returning List()
    controller.identity_=(FakeSocialIdentity.admin)
    val res = controller.renderTabs(1L, "membership").apply(fakeGetRequest())

    contentAsString(res) must not contain "stop automatic renewal"
    contentAsString(res) must not contain "person/1/renew"
    contentAsString(res) must not contain "Renew subscription"
    contentAsString(res) must contain("Automatic renewal is stopped")
  }

  def e7 = {
    val member = MemberHelper.make(Some(1L), 1L, person = true, funder = true,
      renewal = false)
    person.member_=(member)
    (personService.find(_: Long)) expects 1L returning Some(person)
    (paymentService.findByPerson _) expects 1L returning List()
    controller.identity_=(FakeSocialIdentity.viewer)
    val res = controller.renderTabs(1L, "membership").apply(fakeGetRequest())

    contentAsString(res) must not contain "stop automatic renewal"
    contentAsString(res) must not contain "person/1/renew"
    contentAsString(res) must not contain "Renew subscription"
    contentAsString(res) must contain("Automatic renewal is stopped")
  }

  def e8 = {
    val person = PersonHelper.two
    val member = MemberHelper.make(Some(1L), 2L, person = true, funder = true)
    person.member_=(member)
    (personService.find(_: Long)) expects 2L returning Some(person)
    (paymentService.findByPerson _) expects 2L returning List()
    val res = controller.renderTabs(2L, "membership").apply(fakeGetRequest())

    contentAsString(res) must not contain "stop automatic renewal"
    contentAsString(res) must not contain "person/2/renew"
    contentAsString(res) must not contain "Renew subscription"
    contentAsString(res) must not contain "Automatic renewal is stopped"
  }

  def e9 = {
    (personService.find(_: Long)) expects 1L returning Some(person)
    (paymentService.findByPerson _) expects 1L returning List()
    val res = controller.renderTabs(1L, "membership").apply(fakeGetRequest())

    contentAsString(res) must not contain "Edit Membership"
    contentAsString(res) must not contain "member/1/edit"
    contentAsString(res) must not contain "Delete Membership"
    contentAsString(res) must not contain "member/1/delete"
  }

  def e10 = {
    (personService.find(_: Long)) expects 1L returning Some(person)
    (paymentService.findByPerson _) expects 1L returning List()
    controller.identity_=(FakeSocialIdentity.admin)
    val res = controller.renderTabs(1L, "membership").apply(fakeGetRequest())

    contentAsString(res) must contain("Edit Membership")
    contentAsString(res) must contain("member/1/edit")
    contentAsString(res) must contain("Delete Membership")
    contentAsString(res) must contain("member/1/delete")
  }
}