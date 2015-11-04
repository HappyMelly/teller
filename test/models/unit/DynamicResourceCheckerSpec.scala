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
 * If you have questions concerning this license or the applicable additional terms, you may contact
 * by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */

package models

import helpers.{ EventHelper, PersonHelper }
import models.service.{ BrandService, EventService, LicenseService, PersonService }
import org.joda.money.Money
import org.joda.time.LocalDate
import org.specs2.mutable.Specification
import org.scalamock.specs2.IsolatedMockFactory
import stubs.FakeServices

class DynamicResourceCheckerSpec extends Specification with IsolatedMockFactory {

  override def is = s2"""

  Given the user is an Editor
    when permissions to edit a person are checked
      then an access should be granted                                       $e1

    when permissions for brand coordinator are checked
      then an access should be granted                                       $e2

    when permissions for brand facilitator are checked
      then an access should be granted                                       $e3

    when permissions for event coordinator are checked
      then an access should be granted                                       $e4

    when permissions for event facilitator are checked
      then an access should be granted                                       $e5

    when permissions for evaluation coordinator are checked
      then an access should be granted                                       $e6

    when permissions for evaluation facilitator are checked
      then an access should be granted                                       $e7

    when permissions to delete a person are checked
      then an access should be granted                                       $e8

  Given the user is a Viewer
    when permissions to edit this user are checked
      then an access should be granted                                      $e20

    and the given person is not virtual
      when permissions to edit this person are checked
        then an access should not be granted                                $e21

    and the user is a coordinator
      when permissions for brand coordinator are checked
        then an access should be granted                                    $e22

    when permissions for brand coordinator are checked
      then an access should not be granted                                  $e23

    and the user is a coordinator
      when permissions for brand facilitator are checked
        then an access should be granted                                    $e24

    and the user is a facilitator
      when permissions for brand facilitator are checked
        then an access should be granted                                    $e25

    when permissions for brand facilitator are checked
      then an access should not be granted                                  $e26

    and the user is a coordinator
      when permissions for event coordinator are checked
        then an access should be granted                                    $e27

    when permissions for event coordinator are checked
      then an access should not be granted                                  $e28

  Given the requested event doesn't exist
    when permissions for event coordinator are checked
      then an access should not be granted                                  $e29

  Given the user is a Viewer
    and the user is a coordinator
      when permissions for event facilitator are checked
        then an access should be granted                                    $e30

    and the user is a facilitator
      when permissions for event facilitator are checked
        then an access should be granted                                    $e31

    when permissions for event facilitator are checked
      then an access should not be granted                                  $e32

  Given the given event doesn't exist
    when permissions for event facilitator are checked
      then an access should not be granted                                  $e33

  Given the user is a Viewer
    and the user is a coordinator
      when permissions for evaluation coordinator are checked
        then an access should be granted                                    $e34

    when permissions for evaluation coordinator are checked
      then an access should not be granted                                  $e35

  Given the given evaluation doesn't exist
    when permissions for evaluation coordinator are checked
      then an access should not be granted                                  $e36

  Given the user is a Viewer
    and the user is a coordinator
      when permissions for evaluation facilitator are checked
        then an access should be granted                                    $e37

    and the user is a facilitator
      when permissions for evaluation facilitator are checked
        then an access should be granted                                    $e38

    when permissions for evaluation facilitator are checked
      then an access should not be granted                                  $e39

  Given the given evaluation doesn't exist
    when permissions for evaluation facilitator are checked
      then an access should not be granted                                  $e40

  Given the user is a Viewer
    and the request person doesn't exist
      when permissions to edit a person are checked
        then an access should not be granted                                $e41

    and the given person is virtual
    and didn't participate in events of the user
      when permissions to edit a person are checked
        then an access should not be granted                                $e42

    and the given person is virtual
    and participated in events of the user
      when permissions to edit a person are checked
        then an access should be granted                                    $e43

  Given the user is a Viewer
    and the request person doesn't exist
      when permissions to delete a person are checked
        then an access should not be granted                                $e44

    and the requested person is not virtual
      when permissions to delete a person are checked
        then an access should not be granted                                $e45

    and the given person is virtual
    and didn't participate in events of the user
      when permissions to delete a person are checked
        then an access should not be granted                                $e46

    and the given person is virtual
    and participated in events of the user
      when permissions to delete a person are checked
        then an access should be granted                                    $e47
  """

  class TestDynamicResourceChecker(user: UserAccount)
    extends DynamicResourceChecker(user)
    with FakeServices

  val editor = UserAccount(None, 1L, "editor", None, None, None, None)
  val viewer = editor.copy(role = "viewer")
  val viewerChecker = new TestDynamicResourceChecker(viewer)
  val editorChecker = new TestDynamicResourceChecker(editor)
  val brandService = mock[BrandService]
  val licenseService = mock[LicenseService]
  val eventService = mock[EventService]
  val personService = mock[PersonService]
  viewerChecker.brandService_=(brandService)
  viewerChecker.eventService_=(eventService)
  viewerChecker.licenseService_=(licenseService)
  viewerChecker.personService_=(personService)

  def e1 = editorChecker.canEditPerson(2L) must_== true
  def e2 = editorChecker.isBrandCoordinator(2L) must_== true
  def e3 = editorChecker.isBrandFacilitator(2L) must_== true
  def e4 = editorChecker.isEventCoordinator(2L) must_== true
  def e5 = editorChecker.isEventFacilitator(2L) must_== true
  def e6 = editorChecker.isEvaluationCoordinator(1L) must_== true
  def e7 = editorChecker.isEvaluationFacilitator(1L) must_== true
  def e8 = editorChecker.canDeletePerson(2L) must_== true

  def e20 = viewerChecker.canEditPerson(1L) must_== true

  def e21 = {
    (personService.find(_: Long)) expects 2L returning Some(PersonHelper.one)
    viewerChecker.canEditPerson(2L) must_== false
  }

  def e22 = {
    (brandService.isCoordinator(_, _)) expects (1L, 1L) returning true
    viewerChecker.isBrandCoordinator(1L) must_== true
  }

  def e23 = {
    (brandService.isCoordinator(_, _)) expects (1L, 1L) returning false
    viewerChecker.isBrandCoordinator(1L) must_== false
  }

  def e24 = {
    (brandService.isCoordinator(_, _)) expects (1L, 1L) returning true
    viewerChecker.isBrandFacilitator(1L) must_== true
  }

  def e25 = {
    (brandService.isCoordinator(_, _)) expects (1L, 1L) returning false
    val license = License(None, 1L, 1L, "1", LocalDate.now(),
      LocalDate.now(), LocalDate.now(), true, Money.parse("EUR 100"),
      Some(Money.parse("EUR 100")))
    (licenseService.activeLicense _) expects (1L, 1L) returning Some(license)
    viewerChecker.isBrandFacilitator(1L) must_== true
  }

  def e26 = {
    (brandService.isCoordinator(_, _)) expects (1L, 1L) returning false
    (licenseService.activeLicense _) expects (1L, 1L) returning None
    viewerChecker.isBrandFacilitator(1L) must_== false
  }

  def e27 = {
    (brandService.isCoordinator(_, _)) expects (1L, 1L) returning true
    (eventService.find(_)) expects 1L returning Some(EventHelper.one)
    viewerChecker.isEventCoordinator(1L) must_== true
  }

  def e28 = {
    (brandService.isCoordinator(_, _)) expects (1L, 1L) returning false
    (eventService.find(_)) expects 1L returning Some(EventHelper.one)
    viewerChecker.isEventCoordinator(1L) must_== false
  }

  def e29 = {
    (eventService.find(_)) expects 1L returning None
    viewerChecker.isEventCoordinator(1L) must_== false
  }

  def e30 = {
    val event = EventHelper.one
    event.facilitatorIds_=(List())
    (brandService.isCoordinator(_, _)) expects (1L, 1L) returning true
    (eventService.find(_)) expects 1L returning Some(event)
    viewerChecker.isEventFacilitator(1L) must_== true
  }

  def e31 = {
    val event = EventHelper.one
    event.facilitatorIds_=(List(1))
    (eventService.find(_)) expects 1L returning Some(event)
    viewerChecker.isEventFacilitator(1L) must_== true
  }

  def e32 = {
    val event = EventHelper.one
    event.facilitatorIds_=(List())
    (brandService.isCoordinator(_, _)) expects (1L, 1L) returning false
    (eventService.find(_)) expects 1L returning Some(event)
    viewerChecker.isEventFacilitator(1L) must_== false
  }

  def e33 = {
    (eventService.find(_)) expects 1L returning None
    viewerChecker.isEventFacilitator(1L) must_== false
  }

  def e34 = {
    (eventService.findByEvaluation(_)) expects 1L returning Some(EventHelper.one)
    (brandService.isCoordinator(_, _)) expects (1L, 1L) returning true
    viewerChecker.isEvaluationCoordinator(1L) must_== true
  }

  def e35 = {
    (eventService.findByEvaluation(_)) expects 1L returning Some(EventHelper.one)
    (brandService.isCoordinator(_, _)) expects (1L, 1L) returning false
    viewerChecker.isEvaluationCoordinator(1L) must_== false
  }

  def e36 = {
    (eventService.findByEvaluation(_)) expects 1L returning None
    viewerChecker.isEvaluationCoordinator(1L) must_== false
  }

  def e37 = {
    val event = EventHelper.one
    event.facilitatorIds_=(List())
    (eventService.findByEvaluation(_)) expects 1L returning Some(event)
    (brandService.isCoordinator(_, _)) expects (1L, 1L) returning true
    viewerChecker.isEvaluationFacilitator(1L) must_== true
  }

  def e38 = {
    val event = EventHelper.one
    event.facilitatorIds_=(List(1))
    (eventService.findByEvaluation(_)) expects 1L returning Some(event)
    viewerChecker.isEvaluationFacilitator(1L) must_== true
  }

  def e39 = {
    val event = EventHelper.one
    event.facilitatorIds_=(List())
    (eventService.findByEvaluation(_)) expects 1L returning Some(event)
    (brandService.isCoordinator(_, _)) expects (1L, 1L) returning false
    viewerChecker.isEvaluationFacilitator(1L) must_== false
  }

  def e40 = {
    (eventService.findByEvaluation(_)) expects 1L returning None
    viewerChecker.isEvaluationFacilitator(1L) must_== false
  }

  def e41 = {
    (personService.find(_: Long)) expects 2L returning None
    viewerChecker.canEditPerson(2L) must_== false
  }

  def e42 = {
    val person = PersonHelper.two.copy(virtual = true)
    (personService.find(_: Long)) expects 2L returning Some(person)
    (eventService.findByParticipation(_, _)) expects (2L, 1L) returning List()
    viewerChecker.canEditPerson(2L) must_== false
  }

  def e43 = {
    val person = PersonHelper.two.copy(virtual = true)
    (personService.find(_: Long)) expects 2L returning Some(person)
    val events = List(EventHelper.one)
    (eventService.findByParticipation(_, _)) expects (2L, 1L) returning events
    viewerChecker.canEditPerson(2L) must_== true
  }

  def e44 = {
    (personService.find(_: Long)) expects 2L returning None
    viewerChecker.canDeletePerson(2L) must_== false
  }

  def e45 = {
    (personService.find(_: Long)) expects 2L returning Some(PersonHelper.two)
    viewerChecker.canDeletePerson(2L) must_== false
  }

  def e46 = {
    val person = PersonHelper.two.copy(virtual = true)
    (personService.find(_: Long)) expects 2L returning Some(person)
    (eventService.findByParticipation(_, _)) expects (2L, 1L) returning List()
    viewerChecker.canDeletePerson(2L) must_== false
  }

  def e47 = {
    val person = PersonHelper.two.copy(virtual = true)
    (personService.find(_: Long)) expects 2L returning Some(person)
    val events = List(EventHelper.one)
    (eventService.findByParticipation(_, _)) expects (2L, 1L) returning events
    viewerChecker.canDeletePerson(2L) must_== true
  }
}