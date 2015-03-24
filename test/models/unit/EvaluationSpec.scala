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

import models.{ EvaluationStatus, Evaluation, Activity }
import org.joda.time.DateTime
import org.specs2.mutable._

class EvaluationSpec extends Specification {

  val eval = Evaluation(Some(1L), 1L, 2L, "", "", "", "", "",
    1, 1, "", EvaluationStatus.Pending, None, DateTime.now(), "",
    DateTime.now(), "")

  "Evaluation" should {
    "have well-formed activity attributes" in {
      eval.objectType must_== Activity.Type.Evaluation
      eval.identifier must_== 1
      eval.humanIdentifier must_== "to event (id = 1) for person (id = 2)"
      val evaluation2 = new Evaluation(Some(2L), 2L, 3L, "", "", "", "", "",
        1, 1, "", EvaluationStatus.Pending, None, DateTime.now(), "",
        DateTime.now(), "")
      evaluation2.objectType must_== Activity.Type.Evaluation
      evaluation2.identifier must_== 2
      evaluation2.humanIdentifier must_== "to event (id = 2) for person (id = 3)"
    }
  }
  "If a participant hasn't finished evaluation process, the evaluation" should {
    "not be approvable or rejectable" in {
      val e1 = eval.copy(status = EvaluationStatus.InProgress)
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
}
