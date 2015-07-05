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
package models

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class CompletionStep(name: String, weight: Int, done: Boolean = false)

case class ProfileStrength(id: Option[Long],
    objectId: Long,
    org: Boolean = false,
    steps: List[CompletionStep]) {

  /**
   * Returns a profile strength progress in percents
   */
  def progress: Int = {
    val total = steps.foldRight(0)(_.weight + _)
    if (total == 0) {
      100
    } else {
      val complete = steps.filter(_.done).foldRight(0)(_.weight + _)
      (complete.toFloat / total * 100).toInt
    }
  }

  /**
   * Returns a list of incomplete steps with their weight recalculated to percents
   */
  def incompleteSteps: List[CompletionStep] = {
    val total = steps.foldRight(0)(_.weight + _)
    if (total == 0)
      List()
    else
      steps.filterNot(_.done).map(x ⇒ x.copy(weight = (x.weight.toFloat / total * 100).toInt))
  }

  /**
   * Completes the given step and returns an updated object
   *
   * @param name Step name
   */
  def markComplete(name: String): ProfileStrength = markStep(name, true)

  /**
   * Incompletes the given step and returns an updated object
   *
   * @param name Step name
   */
  def markIncomplete(name: String): ProfileStrength = markStep(name, false)

  def stepsInJson: JsArray = {
    implicit val stepWriter = new Writes[CompletionStep] {
      def writes(data: CompletionStep): JsValue = {
        Json.obj(
          "name" -> data.name,
          "weight" -> data.weight,
          "done" -> data.done)
      }
    }
    Json.toJson(steps).as[JsArray]
  }

  /**
   * Marks the given step as complete or incomplete
   *
   * @param name Name of the step
   * @param done Completion state
   * @return Updated profile strength object
   */
  private def markStep(name: String, done: Boolean): ProfileStrength = {
    steps.find(_.name == name).map { x ⇒
      val completedStep = x.copy(done = done)
      val oldSteps = steps.filterNot(_.name == name)
      val sortedSteps = (completedStep :: oldSteps).sortBy(_.name)
      this.copy(steps = sortedSteps)
    } getOrElse this
  }
}

object ProfileStrength {

  def apply(id: Option[Long],
    objectId: Long,
    org: Boolean,
    stepsInJson: JsArray): ProfileStrength = {

    implicit val stepReader = (
      (__ \ "name").read[String] and
      (__ \ "weight").read[Int] and
      (__ \ "done").read[Boolean])(CompletionStep)

    ProfileStrength(id, objectId, org, stepsInJson.as[List[CompletionStep]])
  }

  /**
   * Creates a profile strength for a new person
   *
   * @param objectId Person or org identifier
   * @param org If true objectId is org identifier
   */
  def empty(objectId: Long, org: Boolean): ProfileStrength = {
    val steps = List(CompletionStep("about", 1),
      CompletionStep("photo", 4),
      CompletionStep("social", 1),
      CompletionStep("member", 2))
    ProfileStrength(None, objectId, org, steps)
  }

  /**
   * Adds steps for a facilitator to the given profile strength
   *
   * @param strength Profile strength
   * @return Updated profile strength with added steps
   */
  def forFacilitator(strength: ProfileStrength): ProfileStrength = {
    val withSignature = if (strength.steps.exists(_.name == "signature"))
      strength.steps
    else
      strength.steps :+ CompletionStep("signature", 1)
    val withLanguage = if (withSignature.exists(_.name == "language"))
      withSignature
    else
      withSignature :+ CompletionStep("language", 1)
    strength.copy(steps = withLanguage)
  }

  /**
   * Updates steps for a member to the given profile strength
   *
   * @param strength Profile strength
   * @return Updated profile strength
   */
  def forMember(strength: ProfileStrength): ProfileStrength = {
    val withoutMember = strength.steps.filterNot(_.name == "member")
    val withReason = if (withoutMember.exists(_.name == "reason"))
      withoutMember
    else
      withoutMember :+ CompletionStep("reason", 2)
    strength.copy(steps = withReason)
  }

  /**
   * Updates steps for a person to the given profile strength
   *
   * @param strength Profile strength
   * @param person Person object
   * @return Updated profile strength
   */
  def forPerson(strength: ProfileStrength, person: Person): ProfileStrength = {
    val strengthWithDesc = if (person.bio.isDefined)
      strength.markComplete("about")
    else
      strength.markIncomplete("about")
    val strengthWithSocial = if (person.socialProfile.complete)
      strengthWithDesc.markComplete("social")
    else
      strengthWithDesc.markIncomplete("social")
    val strengthWithPhoto = if (person.photo.id.isDefined)
      strengthWithSocial.markComplete("photo")
    else
      strengthWithSocial.markIncomplete("photo")
    if (person.signature)
      strengthWithPhoto.markComplete("signature")
    else
      strengthWithPhoto.markIncomplete("signature")
  }

}
