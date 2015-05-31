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
package models.unit

import models.{ CompletionStep, ProfileStrength }
import org.specs2.mutable._
import play.api.libs.json.{ Json, JsArray }

class ProfileStrengthSpec extends Specification {

  "Unfinished steps should be taken into account" >> {
    "while calculating a strength progress" in {
      val steps = Json.arr(
        Json.obj(
          "name" -> "photo",
          "weight" -> 10,
          "done" -> false), Json.obj(
          "name" -> "about",
          "weight" -> 5,
          "done" -> true), Json.obj(
          "name" -> "reason",
          "weight" -> 5,
          "done" -> true))
      val strength = ProfileStrength(None, 1L, false, steps);
      strength.progress must_== 50
    }
  }
  "Strength progress should be 100" >> {
    "when all steps are completed" in {
      val steps = Json.arr(
        Json.obj(
          "name" -> "photo",
          "weight" -> 10,
          "done" -> true), Json.obj(
          "name" -> "about",
          "weight" -> 5,
          "done" -> true), Json.obj(
          "name" -> "reason",
          "weight" -> 5,
          "done" -> true))
      val strength = ProfileStrength(None, 1L, false, steps);
      strength.progress must_== 100
    }
  }

  "List of incomplete steps" should {
    "contain steps' weights recalculated in percents" in {
      val steps = Json.arr(
        Json.obj(
          "name" -> "photo",
          "weight" -> 10,
          "done" -> false), Json.obj(
          "name" -> "about",
          "weight" -> 5,
          "done" -> false), Json.obj(
          "name" -> "reason",
          "weight" -> 5,
          "done" -> true))
      val strength = ProfileStrength(None, 1L, false, steps)
      val photo = CompletionStep("photo", 50, false)
      val about = CompletionStep("about", 25, false)
      strength.incompleteSteps.contains(photo) must_== true
      strength.incompleteSteps.contains(about) must_== true
    }
  }

  "An incomplete step" should {
    "disappear from a list of incomplete steps when it's marked as complete" in {
      val steps = Json.arr(
        Json.obj(
          "name" -> "photo",
          "weight" -> 10,
          "done" -> false), Json.obj(
          "name" -> "about",
          "weight" -> 5,
          "done" -> false), Json.obj(
          "name" -> "reason",
          "weight" -> 5,
          "done" -> true))
      val strength = ProfileStrength(None, 1L, false, steps)
      strength.incompleteSteps.length must_== 2
      val photo = CompletionStep("photo", 50, false)
      val updatedStrength = strength.markComplete("about")
      updatedStrength.incompleteSteps.length must_== 1
      updatedStrength.incompleteSteps.contains(photo) must_== true
    }
  }

  "A complete step" should {
    "appear in a list of incomplete steps when it's marked as incomplete" in {
      val steps = Json.arr(
        Json.obj(
          "name" -> "photo",
          "weight" -> 10,
          "done" -> true), Json.obj(
          "name" -> "about",
          "weight" -> 5,
          "done" -> false), Json.obj(
          "name" -> "reason",
          "weight" -> 5,
          "done" -> true))
      val strength = ProfileStrength(None, 1L, false, steps)
      strength.incompleteSteps.length must_== 1
      strength.incompleteSteps.exists(_.name == "photo") must_== false
      val photo = CompletionStep("photo", 50, false)
      val updatedStrength = strength.markIncomplete("photo")
      updatedStrength.incompleteSteps.length must_== 2
      updatedStrength.incompleteSteps.contains(photo) must_== true
    }
  }

  "Empty profile strength" should {
    "contains 4 incomplete steps" in {
      val strength = ProfileStrength.empty(1L, false)
      strength.steps.length must_== 4
      strength.steps.exists(_.name == "about") must_== true
      strength.steps.exists(_.name == "photo") must_== true
      strength.steps.exists(_.name == "social") must_== true
      strength.steps.exists(_.name == "member") must_== true
    }
  }

  "For facilitator profile strength" should {
    "contain 2 additional steps" in {
      val strength = ProfileStrength.forFacilitator(ProfileStrength.empty(1L, false))
      strength.steps.length must_== 6
      strength.steps.exists(_.name == "signature") must_== true
      strength.steps.exists(_.name == "language") must_== true
    }
  }

  "For member profile strength" should {
    "not contain 'Member' step" in {
      val strength = ProfileStrength.forMember(ProfileStrength.empty(1L, false))
      strength.steps.exists(_.name == "member") must_== false
    }
    // "contain 'Reason' step" in {
    //   val strength = ProfileStrength.forMember(ProfileStrength.empty(1L, false))
    //   strength.steps.exists(_.name == "reason") must_== true
    // }
  }
}