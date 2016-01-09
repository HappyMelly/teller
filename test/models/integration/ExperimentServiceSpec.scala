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

package models.integration

import helpers.MemberHelper
import integration.PlayAppSpec
import models._
import models.service._
import org.joda.time.DateTime

class ExperimentServiceSpec extends PlayAppSpec {

  val service = new ExperimentService

  override def setupDb() = {
    MemberService.get.insert(MemberHelper.make(Some(1L), 1, false, true))
    MemberService.get.insert(MemberHelper.make(Some(2L), 1, true, true))
    List(
      (1, "Exp 1", "Desc", "Url 1"),
      (1, "Exp 2", "Desc", "Url 2"),
      (2, "Exp 3", "Desc", "Url 3"),
      (2, "Exp 4", "Desc", "Url 4"),
      (2, "Exp 5", "Desc", "Url 5")).foreach {
        case (memberId, name, desciption, url) ⇒ {
          val recordInfo = DateStamp(createdBy = "Tester", updated = DateTime.now(), updatedBy = "Tester")
          val experiment = Experiment(None, memberId, name, desciption, false, Some(url), recordInfo)
          service.insert(experiment)
        }
      }
  }

  "Method 'findByMember' " should {
    "return 2 experiments for member id = 1" in {
      val experiments = service.findByMember(1L)
      experiments.length must_== 2
      experiments.exists(_.name == "Exp 1") must_== true
      experiments.exists(_.name == "Exp 2") must_== true
    }
    "return 3 experiments for member id = 2" in {
      val experiments = service.findByMember(2L)
      experiments.length must_== 3
      experiments.exists(_.name == "Exp 3") must_== true
      experiments.exists(_.name == "Exp 4") must_== true
      experiments.exists(_.name == "Exp 5") must_== true
    }
    "return 0 experiments for member id = 3" in {
      val experiments = service.findByMember(3L)
      experiments.length must_== 0
    }
  }
}