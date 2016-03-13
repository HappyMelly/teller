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

import controllers.api.EvaluationsApi
import models.repository.EvaluationRepository
import org.scalamock.specs2.MockContext
import org.specs2.Specification
import play.api.test.FakeRequest
import play.api.test.Helpers._
import stubs.{ FakeRepositories, FakeApiAuthentication, FakeSecurity }

class EvaluationsApiSpec extends Specification {
  def is = s2"""

  When third-party system confirms non-existent evaluation
    Teller should return NotFound   $e1
  """

  class TestEvaluationsApi extends EvaluationsApi
    with FakeApiAuthentication
    with FakeRepositories

  def e1 = new MockContext {
    val evalService = mock[EvaluationRepository]
    val id = "test"
    (evalService.findByConfirmationId(_: String)).expects(id).returning(None)
    val controller = new TestEvaluationsApi
    controller.evaluationService_=(evalService)
    val res = controller.confirm(id).apply(FakeRequest("POST", ""))

    status(res) must beEqualTo(NOT_FOUND)
    contentAsString(res) must contain("Unknown evaluation")
  }
}
