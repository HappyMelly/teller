/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2016, Happy Melly http://www.happymelly.com
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
package controllers.cm

import javax.inject.{Inject, Named}

import akka.actor.{Actor, ActorRef}
import controllers.{Activities, AsyncController, Utilities}
import models.cm.event.Attendee
import models.cm.facilitator.MailChimpSubscriber
import models.cm.{Evaluation, EvaluationStatus}
import models.repository.Repositories
import models.{Address, DateStamp}
import modules.Actors
import org.joda.time.DateTime
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Action
import services.TellerRuntimeEnvironment
import views.Countries

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Contains a set of methods for managing evaluation pages, accessible by unregistered users
  */
class PublicEvaluations @Inject() (implicit val env: TellerRuntimeEnvironment,
                                   override val messagesApi: MessagesApi,
                                   @Named("evaluation-mailer") mailer: ActorRef,
                                   @Named("mailchimp-subscriber") subscriber: ActorRef,
                                   val repos: Repositories)
  extends AsyncController
    with I18nSupport
    with Activities {

  val APP_NAME = "System"

  /**
    * Renders evaluation form for the given event
 *
    * @param eventId Hashed event identifier
    */
  def add(eventId: String) = Action.async { implicit request =>
    repos.cm.event.find(eventId) flatMap {
      case None => notFound(views.html.notFoundPage("/"))
      case Some(event) =>
        (for {
          f <- repos.cm.event.facilitators(event.identifier)
          b <- repos.cm.brand.get(event.brandId)
        } yield (f, b)) flatMap { case (facilitators, brand) =>
          if (!event.past)
            ok(views.html.v2.evaluation.notfinished(event, facilitators, brand))
          else if (event.archived)
            ok(views.html.v2.evaluation.closed(event, facilitators, brand))
          else
            ok(views.html.v2.evaluation.public(event, facilitators, brand))
        }
    }
  }

  /**
    * Confirms the given evaluation
    *
    * @param confirmationId Confirmation unique id
    */
  def confirm(confirmationId: String) = Action.async { implicit request ⇒
    repos.cm.evaluation.findByConfirmationId(confirmationId) flatMap {
      case None => notFound(views.html.evaluation.notfound())
      case Some(evaluation) =>
        evaluation.confirm(repos, mailer)
        ok(views.html.evaluation.confirmed())
    }
  }

  /**
    * Adds new attendee and evaluation for the event and sends confirmation email
    *
    * @param eventId Hashed event identifier
    */
  def create(eventId: String) = Action.async { implicit request =>
    repos.cm.event.find(eventId) flatMap {
      case None => jsonNotFound("You try to evaluate an event which doesn't exist")
      case Some(event) =>
        if (!event.past)
          jsonConflict("The event is not ended yet. Please come back later")
        else if (event.archived)
          jsonConflict("The facilitators closed this event for accepting evaluations")
        else
          form(event.identifier).bindFromRequest.fold(
            errors => jsonFormError(Utilities.errorsToJson(errors)),
            { case (attendee, evaluation) =>
              (for {
                a <- repos.cm.rep.event.attendee.insert(attendee)
                e <- evaluation.copy(attendeeId = a.identifier).add(withConfirmation = true, repos, mailer)
                _ <- repos.cm.rep.event.attendee.update(attendee.copy(evaluationId = e.id))
              } yield a) flatMap { attendee: Attendee =>
                (subscriber ! (attendee.identifier, attendee.eventId, true))(Actor.noSender)
                jsonSuccess("")
              }
            }
          )
    }
  }

  protected def form(eventId: Long) = Form(mapping(
    "question_1" -> nonEmptyText,
    "question_2" -> number(min = 0, max = 10),
    "question_3" -> nonEmptyText,
    "question_4" -> nonEmptyText,
    "question_5" -> number(min = 0, max = 10),
    "question_6" -> nonEmptyText,
    "question_7" -> number(min = 0, max = 10),
    "question_8" -> nonEmptyText,
    "question_9" -> nonEmptyText,
    "question_10" -> number(min = 0, max = 10),
    "first_name" -> nonEmptyText,
    "last_name" -> nonEmptyText,
    "date_of_birth" -> optional(jodaLocalDate),
    "email" -> play.api.data.Forms.email,
    "address" -> mapping(
      "id" -> ignored(None.asInstanceOf[Option[Long]]),
      "street_1" -> optional(nonEmptyText),
      "street_2" -> optional(nonEmptyText),
      "city" -> optional(nonEmptyText),
      "province" -> optional(nonEmptyText),
      "postcode" -> optional(nonEmptyText),
      "country" -> nonEmptyText.verifying(
        "error.unknown_country",
        (country: String) ⇒ Countries.all.exists(_._1 == country)))(Address.apply)(Address.unapply),
    "organisation" -> optional(nonEmptyText),
    "comment" -> optional(nonEmptyText),
    "role" -> optional(nonEmptyText))({
    (reasonToRegister, facilitatorImpression, facilitatorReview, changesToEvent, contentImpression, changesToContent,
     hostImpression, changesToHost, actionItems, recommendationScore,
     firstName, lastName, dateOfBirth, email, address, organisation, comment, role) ⇒

      val recordInfo = DateStamp(DateTime.now, APP_NAME, DateTime.now, APP_NAME)
      val evaluation = Evaluation(None, eventId, 0, reasonToRegister, actionItems, changesToContent, facilitatorReview,
        changesToHost, facilitatorImpression, recommendationScore, changesToEvent, None, None,
        EvaluationStatus.Unconfirmed, None, None, recordInfo)
      val attendee = Attendee(None, eventId, None, firstName, lastName, email, dateOfBirth, Some(address.countryCode),
        address.city, address.street1, address.street2, address.province, address.postCode, None, None, None,
        organisation, comment, role, recordInfo = recordInfo)
      (attendee, evaluation)
  })({
    (v: (Attendee, Evaluation)) ⇒
      Some(v._2.reasonToRegister, v._2.facilitatorImpression, v._2.facilitatorReview, v._2.changesToEvent,
        v._2.contentImpression.getOrElse(0), v._2.changesToContent, v._2.hostImpression.getOrElse(0),
        v._2.changesToHost, v._2.actionItems, v._2.recommendationScore,
        v._1.firstName, v._1.lastName, v._1.dateOfBirth, v._1.email,
        Address(None, v._1.street_1, v._1.street_2, v._1.city, v._1.province, v._1.postcode, v._1.countryCode.getOrElse("")),
        v._1.organisation, v._1.comment, v._1.role)
  }))
}
