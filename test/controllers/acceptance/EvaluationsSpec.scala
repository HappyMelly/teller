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

import controllers.Evaluations
import helpers._
import integration.PlayAppSpec
import models.service._
import models.{ EvaluationEventView, EvaluationStatus }
import org.joda.time.DateTime
import org.scalamock.specs2.IsolatedMockFactory
import play.api.test.FakeRequest
import stubs.{ FakeRuntimeEnvironment, FakeServices, FakeUserIdentity, FakeSecurity }

class EvaluationsSpec extends PlayAppSpec with IsolatedMockFactory {
  override def is = s2"""

  On an evaluation page a user
    should not see Approve/Reject buttons if the evalution is not validated $e1
    should see Approve AND Reject buttons if the evaluation is validated    $e2
    should not see Approve button if the evaluation is approved             $e3
    should not see Reject button if the evaluation is rejected              $e4

  When a participant confirms an evaluation
    she should get an error if the requested evaluation does not exist      $e5
  """

  class TestEvaluations() extends Evaluations(FakeRuntimeEnvironment)
    with FakeSecurity
    with FakeServices

  val controller = new TestEvaluations
  val brandService = mock[BrandService]
  val brand = BrandHelper.one
  controller.brandService_=(brandService)
  val personService = mock[PersonService]
  controller.personService_=(personService)
  val evaluation = EvaluationHelper.make(Some(1L), 1L, 1L,
    EvaluationStatus.Unconfirmed, 10, DateTime.now())
  val evalService = mock[EvaluationService]
  controller.evaluationService_=(evalService)

  def e1 = {
    controller.identity_=(FakeUserIdentity.editor)
    val evalPair = EvaluationEventView(evaluation, EventHelper.one)
    (evalService.findWithEvent(_: Long)).expects(1L).returning(Some(evalPair))
    expectations()
    val res = controller.details(1L).apply(fakeGetRequest())

    status(res) must equalTo(OK)
    contentAsString(res) must not contain "evaluation/1/approve"
    contentAsString(res) must not contain "evaluation/1/reject"
  }

  def e2 = {
    controller.identity_=(FakeUserIdentity.editor)
    val eval = evaluation.copy(status = EvaluationStatus.Pending)
    val evalPair = EvaluationEventView(eval, EventHelper.one)
    (evalService.findWithEvent(_: Long)).expects(1L).returning(Some(evalPair))
    expectations()
    val res = controller.details(1L).apply(fakeGetRequest("/1"))

    status(res) must equalTo(OK)
    contentAsString(res) must contain("evaluation/1/approve")
    contentAsString(res) must contain("evaluation/1/reject")
  }

  def e3 = {
    controller.identity_=(FakeUserIdentity.editor)
    val eval = evaluation.copy(status = EvaluationStatus.Approved)
    val evalPair = EvaluationEventView(eval, EventHelper.one)
    (evalService.findWithEvent(_: Long)).expects(1L).returning(Some(evalPair))
    expectations()
    val res = controller.details(1L).apply(fakeGetRequest("/1"))

    status(res) must equalTo(OK)
    contentAsString(res) must not contain "evaluation/1/approve"
    contentAsString(res) must contain("evaluation/1/reject")
  }

  def e4 = {
    controller.identity_=(FakeUserIdentity.editor)
    val eval = evaluation.copy(status = EvaluationStatus.Rejected)
    val evalPair = EvaluationEventView(eval, EventHelper.one)
    (evalService.findWithEvent(_: Long)).expects(1L).returning(Some(evalPair))
    expectations()
    val res = controller.details(1L).apply(fakeGetRequest("/1"))

    status(res) must equalTo(OK)
    contentAsString(res) must contain("evaluation/1/approve")
    contentAsString(res) must not contain "evaluation/1/reject"
  }

  def e5 = {
    val id = "test"
    (evalService.findByConfirmationId(_: String)).expects(id).returning(None)
    val res = controller.confirm(id).apply(FakeRequest())

    status(res) must equalTo(NOT_FOUND)
    contentAsString(res) must contain("not exist")
  }

  private def expectations(): Unit = {
    (brandService.find(_: Long)).expects(1L).returning(Some(brand))
    (personService.find(_: Long)).expects(1L).returning(Some(PersonHelper.one()))
  }
}
