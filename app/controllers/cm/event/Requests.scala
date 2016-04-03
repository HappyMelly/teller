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
 * If you have questions concerning this license or the applicable additional
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */
package controllers.cm.event

import javax.inject.{Inject, Named}

import akka.actor.ActorRef
import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import controllers.{Activities, Security}
import models.Activity
import models.UserRole.Role
import models.cm.event.Attendee
import models.repository.Repositories
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.MessagesApi
import services.TellerRuntimeEnvironment
import services.integrations.EmailComponent

/**
  * Manages evaluation requests for an event
  */
class Requests @Inject() (override implicit val env: TellerRuntimeEnvironment,
                          override val messagesApi: MessagesApi,
                          val email: EmailComponent,
                          @Named("evaluation-mailer") mailer: ActorRef,
                          val repos: Repositories,
                          deadbolt: DeadboltActions, handlers: HandlerCache, actionBuilder: ActionBuilders)
  extends Security(deadbolt, handlers, actionBuilder, repos)(messagesApi, env)
  with Activities
  with Helpers {

  case class EvaluationRequestData(attendeeIds: List[Long], body: String)

  /**
    * Send requests for evaluation to participants of the event
    *
    * @param id Event ID
    */
  def send(id: Long) = EventAction(List(Role.Facilitator, Role.Coordinator), id) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒ implicit event =>

      form.bindFromRequest.fold(
        formWithErrors ⇒
          redirect(controllers.cm.routes.Events.details(id), "error" -> "Provided data are wrong. Please, check a request form."),
        requestData ⇒ {
          (for {
            a <- repos.cm.rep.event.attendee.findByEvents(List(event.identifier))
            b <- repos.cm.brand.get(event.brandId)
          } yield (a, b)) flatMap { case (unfilteredAttendees, brand) =>
            val attendees = unfilteredAttendees.map(_._2).filter(a => requestData.attendeeIds.contains(a.identifier))
            if (requestData.attendeeIds.forall(p ⇒ attendees.exists(_.identifier == p))) {
              import scala.util.matching.Regex
              val namePattern = new Regex( """(PARTICIPANT_NAME_TOKEN)""", "name")
              attendees.foreach { attendee ⇒
                val body = namePattern replaceAllIn(requestData.body, m ⇒ attendee.fullName)
                mailer ! ("request", attendee, brand, body)
              }

              Activity.insert(user.name, Activity.Predicate.Sent, event.title)(repos)
              success(id, requestMessage(attendees))
            } else {
              error(id, "Some people are not the attendees of the event")
            }
          }
        })
  }

  protected def form = Form(mapping(
    "participantIds" -> list(longNumber),
    "body" -> nonEmptyText.verifying(
      "The letter's body doesn't contains a link",
      (b: String) ⇒ {
        val url = """https?:\/\/""".r findFirstIn b
        url.isDefined
      }))(EvaluationRequestData.apply)(EvaluationRequestData.unapply))


  protected def requestMessage(attendees: Seq[Attendee]): String =
    if (attendees.length == 1)
      "Evaluation request was sent to an attendee"
    else
      s"Evaluation request was sent to ${attendees.length} attendees"
}
