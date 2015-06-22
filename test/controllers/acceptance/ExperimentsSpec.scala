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
import models.Experiment
import models.service.ExperimentService
import org.scalamock.specs2.IsolatedMockFactory
import stubs._
import stubs.services.FakeIntegrations

class ExperimentsSpec extends PlayAppSpec with IsolatedMockFactory {

  override def is = s2"""

  Given a member has experiments when they are requested
    then a user should get a well-formed list of experiments           $e1
  """

  class TestExperiments extends Experiments
    with FakeServices with FakeSecurity with FakeIntegrations

  val controller = new TestExperiments
  val experimentService = mock[ExperimentService]
  controller.experimentService_=(experimentService)

  def e1 = {
    (experimentService.findByMember _) expects 1L returning experiments
    val result = controller.experiments(1L).apply(fakeGetRequest())
    contentAsString(result) must contain("Exp 1")
    contentAsString(result) must contain("Exp 2")
  }

  private def experiments: List[Experiment] = {
    List(Experiment(Some(1L), 1L, "Exp 1", "Desc", true, None),
      Experiment(Some(2L), 1L, "Exp 2", "Desc", true, Some("http://test2.ru")))
  }
}