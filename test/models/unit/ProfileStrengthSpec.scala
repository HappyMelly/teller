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

import helpers._
import models.{ CompletionStep, ProfileStrength, Photo, Person }
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
    "contain 'Reason' step" in {
      val strength = ProfileStrength.forMember(ProfileStrength.empty(1L, false))
      strength.steps.exists(_.name == "reason") must_== true
    }
  }

  "When a person has a valid photo a photo step should be marked as Complete" >> {
    "if a photo is Facebook photo is added" in {
      val facebookPhoto = Photo(Some("facebook"),
        Some("http://graph.facebook.com/skotlov/picture?type=large"))
      val person = PersonHelper.one().copy(photo = facebookPhoto, bio = None)
      incompleteStep("photo", profileStrength(), person) must_== false
    }
    "if a photo is Gravatar photo is added" in {
      val gravatarPhoto = Photo(Some("gravatar"),
        Some("https://secure.gravatar.com/avatar/b642b4217b34b1e8d3bd915fc65c4452?s=300"))
      val person = PersonHelper.one().copy(photo = gravatarPhoto, bio = None)
      incompleteStep("photo", profileStrength(), person) must_== false
    }
  }
  "When a person has no valid photo" >> {
    "a photo step should be marked as Incomplete" in {
      val person = PersonHelper.one().copy(bio = None)
      incompleteStep("photo", profileStrength(), person) must_== true
    }
  }
  "When a person has no bio" >> {
    "a bio step should be marked as Incomplete" in {
      val person = PersonHelper.one().copy(bio = None)
      incompleteStep("about", profileStrength(about = true), person) must_== true
    }
  }
  "When a person has bio" >> {
    "a bio step should be marked as Complete" in {
      val person = PersonHelper.one().copy(bio = Some("test"))
      incompleteStep("about", profileStrength(), person) must_== false
    }
  }
  "A social step should be marked as Incomplete" >> {
    "when a person has no social network" in {
      val person = PersonHelper.one().copy(bio = None)
      incompleteStep("social", profileStrength(social = true), person) must_== true
    }
    "when a person has 1 social network" in {
      val person = PersonHelper.one().copy(bio = None)
      person.profile_=(person.profile.copy(twitterHandle = Some("test")))
      incompleteStep("social", profileStrength(social = true), person) must_== true
    }
  }
  "A social step should be marked as Complete" >> {
    "when a person has at least 2 social networks" in {
      val person = PersonHelper.one().copy(bio = None)
      person.profile_=(person.profile.copy(twitterHandle = Some("test"),
        facebookUrl = Some("test")))
      incompleteStep("social", profileStrength(), person) must_== false
    }
  }
  "A signature step should be marked as Incomplete" >> {
    "when a person has no signature" in {
      val person = PersonHelper.one().copy(bio = None)
      incompleteStep("signature", profileStrength(signature = true), person) must_== true
    }
  }
  "A signature step should be marked as Complete" >> {
    "when a person has a signature" in {
      val person = PersonHelper.one().copy(bio = None, signature = true)
      incompleteStep("signature", profileStrength(), person) must_== false
    }
  }
  "The distribution of ranks" should {
    val strengths = List(
      profileStrength(about = true, photo = true, signature = true, social = true), //25
      profileStrength(about = true, photo = true, signature = true, social = true), //25
      profileStrength(about = false, photo = true, signature = true, social = true), //20
      profileStrength(about = false, photo = true, signature = true, social = true), //20
      profileStrength(about = false, photo = true, signature = true, social = true), //20
      profileStrength(about = true, photo = false, signature = true, social = true), //15
      profileStrength(about = true, photo = false, signature = true, social = true), //15
      profileStrength(about = true, photo = false, signature = true, social = true), //15
      profileStrength(about = false, photo = false, signature = true, social = true), //10
      profileStrength(about = false, photo = false, signature = true, social = true), //10
      profileStrength(about = false, photo = false, signature = true, social = true), //10
      profileStrength(about = false, photo = false, signature = true, social = true), //10
      profileStrength(about = false, photo = false, signature = true, social = true), //10
      profileStrength(about = false, photo = false, signature = false, social = true), //5
      profileStrength(about = false, photo = false, signature = false, social = true), //5
      profileStrength(about = false, photo = false, signature = false, social = true), //5
      profileStrength(about = false, photo = false, signature = false, social = true), //5
      profileStrength(about = false, photo = false, signature = false, social = true), //5
      profileStrength(about = false, photo = false, signature = false, social = true), //5
      profileStrength(about = false, photo = false, signature = false, social = true), //5
      profileStrength(), //0
      profileStrength(), //0
      profileStrength(), //0
      profileStrength(), //0
      profileStrength() //0
      )
    "contain 0 profiles with a rank lower 15" in {
      ProfileStrength.calculateRanks(strengths).filter(_._2 < 15).length must_== 0
    }
    "contain 12 profiles with a rank lower 50" in {
      ProfileStrength.calculateRanks(strengths).filter(_._2 < 50).length must_== 12
    }
    "contain 17 profiles with a rank lower 80" in {
      ProfileStrength.calculateRanks(strengths).filter(_._2 < 80).length must_== 17
    }
    "contain 23 profiles with a rank lower 100" in {
      ProfileStrength.calculateRanks(strengths).filter(_._2 < 100).length must_== 23
    }
  }

  protected def incompleteStep(name: String,
    strength: ProfileStrength,
    person: Person): Boolean =
    ProfileStrength.forPerson(strength, person).incompleteSteps.exists(_.name == name)

  /**
   * Returns profile strength steps
   *
   * @param about Sets strength value for 'about' step
   * @param photo Sets strength value for 'photo' step
   * @param social Sets strength value for 'social' step
   * @param signature Sets strength value for 'signature' step
   */
  private def steps(about: Boolean = false,
    photo: Boolean = false,
    social: Boolean = false,
    signature: Boolean = false): JsArray = {
    Json.arr(
      Json.obj(
        "name" -> "about",
        "weight" -> 5,
        "done" -> about), Json.obj(
        "name" -> "photo",
        "weight" -> 10,
        "done" -> photo), Json.obj(
        "name" -> "signature",
        "weight" -> 5,
        "done" -> signature), Json.obj(
        "name" -> "social",
        "weight" -> 5,
        "done" -> social))
  }

  /**
   * Returns profile strength steps
   *
   * @param about Sets strength value for 'about' step
   * @param photo Sets strength value for 'photo' step
   * @param social Sets strength value for 'social' step
   * @param signature Sets strength value for 'signature' step
   */
  private def profileStrength(about: Boolean = false,
    photo: Boolean = false,
    social: Boolean = false,
    signature: Boolean = false): ProfileStrength =
    ProfileStrength(None, 1L, false, steps(about, photo, social, signature))
}