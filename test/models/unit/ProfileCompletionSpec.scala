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

import models.{ CompletionStep, ProfileCompletion }
import org.specs2.mutable._
import play.api.libs.json.{ Json, JsArray }

class ProfileCompletionSpec extends Specification {

  "Unfinished steps should be taken into account" >> {
    "while calculating a completion progress" in {
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
      val completion = ProfileCompletion(None, 1L, false, steps);
      completion.progress must_== 50
    }
  }
  "Completion progress should be 100" >> {
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
      val completion = ProfileCompletion(None, 1L, false, steps);
      completion.progress must_== 100
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
      val completion = ProfileCompletion(None, 1L, false, steps)
      val photo = CompletionStep("photo", 50, false)
      val about = CompletionStep("about", 25, false)
      completion.incompleteSteps.contains(photo) must_== true
      completion.incompleteSteps.contains(about) must_== true
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
      val completion = ProfileCompletion(None, 1L, false, steps)
      completion.incompleteSteps.length must_== 2
      val photo = CompletionStep("photo", 50, false)
      val updatedCompletion = completion.markComplete("about")
      updatedCompletion.incompleteSteps.length must_== 1
      updatedCompletion.incompleteSteps.contains(photo) must_== true
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
      val completion = ProfileCompletion(None, 1L, false, steps)
      completion.incompleteSteps.length must_== 1
      completion.incompleteSteps.exists(_.name == "photo") must_== false
      val photo = CompletionStep("photo", 50, false)
      val updatedCompletion = completion.markIncomplete("photo")
      updatedCompletion.incompleteSteps.length must_== 2
      updatedCompletion.incompleteSteps.contains(photo) must_== true
    }
  }
}