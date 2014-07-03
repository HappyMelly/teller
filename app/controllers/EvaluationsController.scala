/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2014, Happy Melly http://www.happymelly.com
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
package controllers

import models._
import org.joda.time.DateTime
import play.api.data.{ FormError, Form }
import play.api.data.Forms._
import play.api.data.format.Formatter
import play.mvc.Controller

trait EvaluationsController extends Controller {

  /**
   * Formatter used to define a form mapping for the `EvaluationStatus` enumeration.
   */
  implicit def statusFormat: Formatter[EvaluationStatus.Value] = new Formatter[EvaluationStatus.Value] {

    def bind(key: String, data: Map[String, String]) = {
      try {
        data.get(key).map(EvaluationStatus.withName) match {
          case Some(status) ⇒ Right(status)
          case None ⇒ Right(EvaluationStatus.Pending)
        }
      } catch {
        case e: NoSuchElementException ⇒ Left(List(FormError(key, "error.authorisation")))
      }
    }

    def unbind(key: String, value: EvaluationStatus.Value) = Map(key -> value.toString)
  }

  val participantIdFormatter = new Formatter[Long] {
    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Long] = {
      // "data" lets you access all form data values
      if (data.get("eventId").isEmpty || data.get(key).isEmpty) {
        Left(List(FormError(key, "error.required")))
      } else {
        try {
          val eventId = data.get("eventId").get.toLong
          val personId = data.get(key).get.toLong
          if (Person.find(personId).isEmpty) {
            Left(List(FormError(key, "error.person.invalid")))
          } else {
            if (Evaluation.findByEventAndPerson(personId, eventId).isDefined) {
              Left(List(FormError(key, "error.evaluation.exist")))
            } else if (Event.find(eventId).get.participants.find(_.id.get == personId).isEmpty) {
              Left(List(FormError(key, "error.participant.notExist")))
            } else {
              Right(personId)
            }
          }
        } catch {
          case e: NumberFormatException ⇒ Left(List(FormError(key, "Numeric value expected")))
        }
      }
    }

    override def unbind(key: String, value: Long): Map[String, String] = {
      Map(key -> value.toString)
    }
  }

  val participantIdOnEditFormatter = new Formatter[Long] {
    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Long] = {
      // "data" lets you access all form data values
      if (data.get("eventId").isEmpty || data.get(key).isEmpty) {
        Left(List(FormError(key, "error.required")))
      } else {
        try {
          val eventId = data.get("eventId").get.toLong
          val personId = data.get(key).get.toLong
          if (Person.find(personId).isEmpty) {
            Left(List(FormError(key, "error.person.invalid")))
          } else {
            if (Event.find(eventId).get.participants.find(_.id.get == personId).isEmpty) {
              Left(List(FormError(key, "error.participant.notExist")))
            } else {
              Right(personId)
            }
          }
        } catch {
          case e: NumberFormatException ⇒ Left(List(FormError(key, "Numeric value expected")))
        }
      }
    }

    override def unbind(key: String, value: Long): Map[String, String] = {
      Map(key -> value.toString)
    }
  }

  val statusMapping = of[EvaluationStatus.Value]

  /** HTML form mapping for creating and editing. */
  def evaluationForm(userName: String, edit: Boolean = false) = Form(mapping(
    "id" -> ignored(Option.empty[Long]),
    "eventId" -> longNumber.verifying(
      "An event doesn't exist", (eventId: Long) ⇒ Event.find(eventId).isDefined),
    "participantId" -> { if (edit) of(participantIdOnEditFormatter) else of(participantIdFormatter) },
    "question1" -> nonEmptyText,
    "question2" -> nonEmptyText,
    "question3" -> nonEmptyText,
    "question4" -> nonEmptyText,
    "question5" -> nonEmptyText,
    "question6" -> number(min = 0, max = 10),
    "question7" -> number(min = 0, max = 10),
    "question8" -> nonEmptyText,
    "status" -> statusMapping,
    "handled" -> optional(jodaLocalDate),
    "certificate" -> optional(nonEmptyText),
    "created" -> ignored(DateTime.now),
    "createdBy" -> ignored(userName),
    "updated" -> ignored(DateTime.now),
    "updatedBy" -> ignored(userName))(Evaluation.apply)(Evaluation.unapply))

}
