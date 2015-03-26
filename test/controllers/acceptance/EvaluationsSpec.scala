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
import models.service.{ BrandService, EvaluationService, TranslationService }
import models.{ EvaluationPair, EvaluationStatus }
import org.joda.time.DateTime
import org.scalamock.specs2.IsolatedMockFactory
import stubs.{ FakePersonService, FakeServices, FakeUserIdentity }

class EvaluationsSpec extends PlayAppSpec with IsolatedMockFactory {
  override def is = s2"""

  On an evaluation page a user
    should not see Approve/Reject buttons if the evalution is not validated $e1
    should see Approve AND Reject buttons if the evaluation is validated    $e2
    should not see Approve button if the evaluation is approved             $e3
    should not see Reject button if the evaluation is rejected              $e4
  """

  class TestEvaluations() extends Evaluations with FakeServices
  val controller = new TestEvaluations
  val brandService = mock[BrandService]
  val brand = BrandHelper.one
  controller.brandService_=(brandService)
  val translationService = mock[TranslationService]
  val translation = TranslationHelper.en
  controller.translationService_=(translationService)
  val personService = mock[FakePersonService]
  controller.personService_=(personService)
  val evaluation = EvaluationHelper.make(Some(1L), 1L, 1L,
    EvaluationStatus.Unvalidated, 10, DateTime.now())
  val evalService = mock[EvaluationService]
  controller.evaluationService_=(evalService)

  def e1 = {
    val req = prepareSecuredGetRequest(FakeUserIdentity.editor, "/")
    val evalPair = EvaluationPair(evaluation, EventHelper.one)
    (evalService.find _).expects(1L).returning(Some(evalPair))
    expectations()
    val res = controller.details(1L).apply(req)

    status(res) must equalTo(OK)
    contentAsString(res) must not contain "evaluation/1/approve"
    contentAsString(res) must not contain "evaluation/1/reject"
  }

  def e2 = {
    val req = prepareSecuredGetRequest(FakeUserIdentity.editor, "/")
    val eval = evaluation.copy(status = EvaluationStatus.Pending)
    val evalPair = EvaluationPair(eval, EventHelper.one)
    (evalService.find _).expects(1L).returning(Some(evalPair))
    expectations()
    val res = controller.details(1L).apply(req)

    status(res) must equalTo(OK)
    contentAsString(res) must contain("evaluation/1/approve")
    contentAsString(res) must contain("evaluation/1/reject")
  }

  def e3 = {
    val req = prepareSecuredGetRequest(FakeUserIdentity.editor, "/")
    val eval = evaluation.copy(status = EvaluationStatus.Approved)
    val evalPair = EvaluationPair(eval, EventHelper.one)
    (evalService.find _).expects(1L).returning(Some(evalPair))
    expectations()
    val res = controller.details(1L).apply(req)

    status(res) must equalTo(OK)
    contentAsString(res) must not contain "evaluation/1/approve"
    contentAsString(res) must contain("evaluation/1/reject")
  }

  def e4 = {
    val req = prepareSecuredGetRequest(FakeUserIdentity.editor, "/")
    val eval = evaluation.copy(status = EvaluationStatus.Rejected)
    val evalPair = EvaluationPair(eval, EventHelper.one)
    (evalService.find _).expects(1L).returning(Some(evalPair))
    expectations()
    val res = controller.details(1L).apply(req)

    status(res) must equalTo(OK)
    contentAsString(res) must contain("evaluation/1/approve")
    contentAsString(res) must not contain "evaluation/1/reject"
  }

  private def expectations(): Unit = {
    (brandService.find(_: String)).expects(brand.code).returning(Some(brand))
    (translationService.find _).expects("EN").returning(Some(translation))
    (personService.find(_: Long)).expects(1L).returning(Some(PersonHelper.one()))
  }
}
