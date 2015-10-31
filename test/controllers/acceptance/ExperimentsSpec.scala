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
import controllers.Experiments
import helpers.MemberHelper
import models.{ Experiment, Member }
import models.service.{ ExperimentService, MemberService }
import org.scalamock.specs2.IsolatedMockFactory
import stubs._
import stubs.services.FakeIntegrations

class ExperimentsSpec extends PlayAppSpec with IsolatedMockFactory {

  override def is = s2"""

  Given a member has experiments
    when they are requested
      then a user should get a well-formed list of experiments               $e1

  Given a member doesn't exist
    when new experiment is created for this member
      then a user should get "Member not found" error                        $e2

  Given a member exists and this member is a person
      and the user has uploaded a file
    when new experiment is created for this member
      then a user should be redirected to the person's profile               $e3

  Given a member exists and this member is an organisation
      and the user hasn't uploaded a file
    when new experiment is created for this member
      then a user should be redirected to the organisation's profile         $e4

  Given an experiment doesn't exist
    when a user tries to edit this experiment
      then the user should get "Experiment not found" error                  $e5

  Given an experiment doesn't exist
    when a user tries to edit this experiment
      then the user should get "Experiment not found" error                  $e6

  Given an experiment exists
      and a member this experiment is belonged to is a person
    when new experiment is created for this member
      then a user should be redirected to the person's profile               $e7

  Given an experiment exists
      and a member this experiment is belonged to is an organisation
    when new experiment is created for this member
      then a user should be redirected to the organisation's profile         $e8

  Given a member doesn't exist
    when an experiment is updated for this member
      then a user should get "Member not found" error                        $e9

  Given an experiment doesn't exist
    when the experiment is updated for this member
      then a user should get "Experiment not found" error                   $e10

  Given a member exists and this member is a person
      and the user has uploaded a file
    when the experiment is updated for this member
      then a user should be redirected to the person's profile              $e11

  Given a member exists and this member is an organisation
      and the user hasn't uploaded a file
    when the experiment is updated for this member
      then a user should be redirected to the organisation's profile        $e12
  """

  class TestExperiments extends Experiments(FakeRuntimeEnvironment)
      with FakeServices with FakeSecurity with FakeIntegrations with FakeFiles {

    override def notifyMembers(member: Member,
      experiment: Experiment,
      url: String) {}
  }

  val controller = new TestExperiments
  val experimentService = mock[ExperimentService]
  val memberService = mock[MemberService]
  controller.memberService_=(memberService)
  controller.experimentService_=(experimentService)

  def e1 = {
    (experimentService.findByMember _) expects 1L returning experiments
    val result = controller.experiments(1L).apply(fakeGetRequest())
    contentAsString(result) must contain("Exp 1")
    contentAsString(result) must contain("Exp 2")
  }

  def e2 = {
    (memberService.find(_: Long)) expects 1L returning None
    val result = controller.create(1L).apply(postReq)

    status(result) must equalTo(NOT_FOUND)
  }

  def e3 = {
    val member = MemberHelper.make(Some(1L), 5L, person = true, funder = false)
    (memberService.find(_: Long)) expects 1L returning Some(member)
    val experiment = Experiment(None, 1L, "Test", "Test", false, None)
    (experimentService.insert _) expects experiment returning experiment.copy(id = Some(1L))
    (experimentService.update _) expects experiment.copy(id = Some(1L), picture = true)
    val result = controller.create(1L).apply(postReq)

    header("Location", result) must beSome.which(_.contains("/person/5"))
  }

  def e4 = {
    val member = MemberHelper.make(Some(1L), 5L, person = false, funder = false)
    (memberService.find(_: Long)) expects 1L returning Some(member)
    val experiment = Experiment(None, 1L, "Test", "Test", false, None)
    (experimentService.insert _) expects experiment returning experiment.copy(id = Some(1L))
    controller._uploadValue = false
    val result = controller.create(1L).apply(postReq)
    controller._uploadValue = true

    header("Location", result) must beSome.which(_.contains("/organization/5"))
  }

  def e5 = {
    (experimentService.find _) expects 1L returning None
    val result = controller.edit(1L, 1L).apply(fakeGetRequest())
    status(result) must equalTo(NOT_FOUND)
  }

  def e6 = {
    (experimentService.find _) expects 1L returning None
    val result = controller.update(1L, 1L).apply(postReq)

    status(result) must equalTo(NOT_FOUND)
  }

  def e7 = {
    val member = MemberHelper.make(Some(1L), 5L, person = true, funder = false)
    (memberService.find(_: Long)) expects 1L returning Some(member)
    val experiment = Experiment(Some(1L), 1L, "T", "T", false, None)
    (experimentService.find _) expects 1L returning Some(experiment)
    (experimentService.update _) expects experiment.copy(id = Some(1),
      memberId = 1, name = "Test", description = "Test", picture = true)
    val result = controller.update(1L, 1L).apply(postReq)

    header("Location", result) must beSome.which(_.contains("/person/5"))
  }

  def e8 = {
    val member = MemberHelper.make(Some(1L), 5L, person = false, funder = false)
    (memberService.find(_: Long)) expects 1L returning Some(member)
    val experiment = Experiment(Some(1L), 1L, "T", "T", false, None)
    (experimentService.find _) expects 1L returning Some(experiment)
    (experimentService.update _) expects experiment.copy(id = Some(1),
      memberId = 1, name = "Test", description = "Test", picture = true)
    val result = controller.update(1L, 1L).apply(postReq)

    header("Location", result) must beSome.which(_.contains("/organization/5"))
  }

  def e9 = {
    val experiment = Experiment(Some(1L), 1L, "T", "T", false, None)
    (experimentService.find _) expects 1L returning Some(experiment)
    (memberService.find(_: Long)) expects 5L returning None
    val result = controller.update(5L, 1L).apply(postReq)

    status(result) must equalTo(NOT_FOUND)
  }

  def e10 = {
    (experimentService.find _) expects 1L returning None
    val result = controller.update(5L, 1L).apply(postReq)

    status(result) must equalTo(NOT_FOUND)
  }

  def e11 = {
    val experiment = Experiment(Some(1L), 1L, "T", "T", false, None)
    (experimentService.find _) expects 1L returning Some(experiment)
    val member = MemberHelper.make(Some(5L), 5L, person = true, funder = false)
    (memberService.find(_: Long)) expects 5L returning Some(member)
    (experimentService.update _) expects experiment.copy(id = Some(1L),
      name = "Test", description = "Test", memberId = 5L, picture = true)
    val result = controller.update(5L, 1L).apply(postReq)
    header("Location", result) must beSome.which(_.contains("/person/5"))
  }

  def e12 = {
    val experiment = Experiment(Some(1L), 1L, "T", "T", false, None)
    (experimentService.find _) expects 1L returning Some(experiment)
    val member = MemberHelper.make(Some(5L), 5L, person = false, funder = false)
    (memberService.find(_: Long)) expects 5L returning Some(member)
    (experimentService.update _) expects experiment.copy(id = Some(1L),
      name = "Test", description = "Test", memberId = 5L) returning * once
    val req = fakePostRequest().withFormUrlEncodedBody("memberId" -> "2",
      "name" -> "Test", "description" -> "Test", "picture" -> "0")
    controller._uploadValue = false
    val result = controller.update(5L, 1L).apply(postReq)
    header("Location", result) must beSome.which(_.contains("/organization/5"))
  }

  private def experiments: List[Experiment] = {
    List(Experiment(Some(1L), 1L, "Exp 1", "Desc", true, None),
      Experiment(Some(2L), 1L, "Exp 2", "Desc", true, Some("http://test2.ru")))
  }

  private def postReq = fakePostRequest().withFormUrlEncodedBody(
    "memberId" -> "2", "name" -> "Test", "description" -> "Test", "picture" -> "0")
}