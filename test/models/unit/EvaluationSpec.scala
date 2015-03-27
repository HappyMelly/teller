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

package models.unit

import models.service.EvaluationService
import models.{ EvaluationStatus, Evaluation, Activity }
import org.joda.time.{ LocalDate, DateTime }
import org.scalamock.specs2.MockContext
import org.specs2.mutable._
import stubs.FakeServices
import stubs.services.FakeNotifiers

class EvaluationSpec extends Specification {

  class TestEvaluation(id: Option[Long],
    eventId: Long,
    personId: Long,
    question1: String,
    question2: String,
    question3: String,
    question4: String,
    question5: String,
    question6: Int,
    question7: Int,
    question8: String,
    status: EvaluationStatus.Value,
    handled: Option[LocalDate],
    confirmationId: Option[String],
    created: DateTime,
    createdBy: String,
    updated: DateTime,
    updatedBy: String) extends Evaluation(id,
    eventId, personId, question1, question2, question3, question4,
    question5, question6, question7, question8, status, handled,
    confirmationId, created, createdBy, updated, updatedBy) with FakeServices {

    var newEvalCallCount = 0
    var confirmEvalCallCount = 0

    override def sendNewEvaluationNotification() = {
      newEvalCallCount += 1
      this
    }

    override def sendConfirmationRequest() = {
      confirmEvalCallCount += 1
      this
    }

    override def copy(id: Option[Long] = id,
      eventId: Long = eventId,
      personId: Long = personId,
      question1: String = question1,
      question2: String = question2,
      question3: String = question3,
      question4: String = question4,
      question5: String = question5,
      question6: Int = question6,
      question7: Int = question7,
      question8: String = question8,
      status: EvaluationStatus.Value = status,
      handled: Option[LocalDate] = handled,
      confirmationId: Option[String] = confirmationId,
      created: DateTime = created,
      createdBy: String = createdBy,
      updated: DateTime = updated,
      updatedBy: String = updatedBy): TestEvaluation = {
      val eval = new TestEvaluation(id, eventId, personId, question1, question2, question3,
        question4, question5, question6, question7, question8, status,
        handled, confirmationId, created, createdBy, updated, updatedBy)
      eval.evaluationService_=(this.evaluationService)
      eval
    }
  }

  /**
   * Test class which returns unchanged evaluations without executing any actions.
   * It allows us to check that evaluations are changed before being passed
   * to the service.
   */
  class TestEvaluationService extends EvaluationService {
    override def add(eval: Evaluation): Evaluation = eval
    override def update(eval: Evaluation): Evaluation = eval
  }

  val eval = new TestEvaluation(Some(1L), 1L, 2L, "", "", "", "", "",
    1, 1, "", EvaluationStatus.Pending, None, None, DateTime.now(), "",
    DateTime.now(), "")

  "Evaluation" should {
    "have well-formed activity attributes" in {
      eval.objectType must_== Activity.Type.Evaluation
      eval.identifier must_== 1
      eval.humanIdentifier must_== "to event (id = 1) for person (id = 2)"
      val evaluation2 = eval.copy(id = Some(2L), eventId = 2L, personId = 3L)
      evaluation2.objectType must_== Activity.Type.Evaluation
      evaluation2.identifier must_== 2
      evaluation2.humanIdentifier must_== "to event (id = 2) for person (id = 3)"
    }
  }
  "If a participant hasn't finished evaluation process, the evaluation" should {
    "not be approvable or rejectable" in {
      val e1 = eval.copy(status = EvaluationStatus.Unconfirmed)
      e1.approvable must_== false
      e1.rejectable must_== false
    }
  }
  "If an evaluation is pending, it" should {
    "be approvable and rejectable" in {
      eval.approvable must_== true
      eval.rejectable must_== true
    }
  }
  "If an evaluation is approved it" should {
    "be rejectable but not approvable" in {
      val e1 = eval.copy(status = EvaluationStatus.Approved)
      e1.rejectable must_== true
      e1.approvable must_== false
    }
  }
  "If an evaluation is rejected it" should {
    "be approvable but not rejectable" in {
      val e1 = eval.copy(status = EvaluationStatus.Rejected)
      e1.rejectable must_== false
      e1.approvable must_== true
    }
  }
  "When a confirmation for an evaluation is requested" >> {
    "the evaluation should be added as Unconfirmed & the notification should be sent to participant" in {
      eval.evaluationService_=(new TestEvaluationService)
      val added = eval.add(withConfirmation = true).asInstanceOf[TestEvaluation]
      added.status must_== EvaluationStatus.Unconfirmed
      added.confirmationId must_!= None
      added.confirmEvalCallCount must_== 1
    }
  }
  "When a confirmation is NOT requested" >> {
    "the evaluation should be added as Pending & the email notification should be sent to facilitator" in new MockContext {
      val evalService = mock[EvaluationService]
      val pending = eval.copy(status = EvaluationStatus.Pending)
      (evalService.add(_)).expects(pending).returning(pending)
      val approved = eval.copy(status = EvaluationStatus.Approved)
      approved.evaluationService_=(evalService)
      val added = approved.add().asInstanceOf[TestEvaluation]
      added.newEvalCallCount must_== 1
    }
  }
  "When an evaluation is not confirmed and 'confirm' method is called" >> {
    "the evaluation should become confirmed afterwards & the email notification should be sent to facilitator" in {
      val tested = eval.copy(status = EvaluationStatus.Unconfirmed)
      tested.evaluationService_=(new TestEvaluationService)
      val confirmed = tested.confirm().asInstanceOf[TestEvaluation]
      confirmed.status must_== EvaluationStatus.Pending
      confirmed.newEvalCallCount must_== 1
    }
  }
}
