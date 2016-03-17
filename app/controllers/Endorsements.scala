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
 * If you have questions concerning this license or the applicable additional terms,
 * you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */
package controllers

import javax.inject.Inject

import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import be.objectify.deadbolt.scala.cache.HandlerCache
import models.UserRole.Role
import models.cm.facilitator.Endorsement
import models.repository.Repositories
import models.Brand
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{MessagesApi, I18nSupport, Messages}
import play.api.libs.json.{JsArray, JsValue, Json, Writes}
import services.TellerRuntimeEnvironment

import scala.concurrent.Future

case class EndorsementFormData(content: String,
  name: String,
  brandId: Long,
  company: Option[String])

class Endorsements @Inject() (override implicit val env: TellerRuntimeEnvironment,
                              override val messagesApi: MessagesApi,
                              val repos: Repositories,
                              deadbolt: DeadboltActions, handlers: HandlerCache, actionBuilder: ActionBuilders)
  extends Security(deadbolt, handlers, actionBuilder, repos)(messagesApi, env)
  with I18nSupport{

  implicit val EndorsementWrites = new Writes[Endorsement] {
    def writes(endorsement: Endorsement): JsValue = {
      Json.obj(
        "brandId" -> endorsement.brandId,
        "personId" -> endorsement.personId,
        "content" -> endorsement.content,
        "name" -> endorsement.name,
        "company" -> endorsement.company,
        "id" -> endorsement.id.get)
    }
  }

  val form = Form(mapping(
    "content" -> nonEmptyText,
    "name" -> nonEmptyText,
    "brandId" -> longNumber,
    "company" -> optional(nonEmptyText))(EndorsementFormData.apply)(EndorsementFormData.unapply))

  /**
   * Renders endorsement add form
    *
    * @param personId Person identifier
   */
  def add(personId: Long) = ProfileAction(personId) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      (for {
        person <- repos.person.find(personId)
        brands <- brands(personId)
      } yield (person, brands)) flatMap {
        case (None, _) => notFound(Messages("error.person.notFound"))
        case (Some(person), brands) => ok(views.html.v2.endorsement.addForm(user, personId, brands, form))
      }
  }

  /**
   * Renders the form where a facilitator could select an endorsement from
   *  existing evaluations
   *
   * @param personId Person id
   */
  def renderSelectForm(personId: Long) = ProfileAction(personId) { implicit request =>
    implicit handler => implicit user =>
      (for {
        brands <- repos.cm.brand.findAll
        events <- repos.cm.event.findByFacilitator(personId)
        endorsements <- repos.person.endorsements(personId)
      } yield (brands, events, endorsements)) flatMap { case (brands, events, endorsements) =>
        val filteredEvents = events.map { x =>
          (x, brands.find(_.id.get == x.brandId).map(_.name).getOrElse(""))
        }
        val evaluationIds = endorsements.filter(_.evaluationId != 0).map(_.evaluationId)
        repos.cm.evaluation.findByEvents(filteredEvents.map(_._1.id.get)) map { evaluations =>
          evaluations.filterNot(x => evaluationIds.contains(x.id.get))
        } flatMap { evaluations =>
          repos.person.find(evaluations.map(_.attendeeId).distinct) flatMap { people =>
            val content = evaluations.sortBy(_.impression).reverse.map { x =>
              (x,
                people.find(_.identifier == x.attendeeId).map(_.fullName).getOrElse(""),
                filteredEvents.find(_._1.id.get == x.eventId).map(_._2).getOrElse(""))
            }
            ok(views.html.v2.endorsement.selectForm(user, personId, content))

          }
        }
      }
  }

  /**
   * Renders endorsement edit form
    *
    * @param personId Person identifier
   * @param id Endorsement identifier
   */
  def edit(personId: Long, id: Long) = ProfileAction(personId) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      (for {
        person <- repos.person.find(personId)
        endorsement <- repos.person.findEndorsement(id)
        brands <- brands(personId)
      } yield (person, endorsement, brands)) flatMap {
        case (None, _, _) => notFound(Messages("error.person.notFound"))
        case (_, None, _) => notFound("Endorsement not found")
        case (Some(person), Some(endorsement), brands) =>
          val formData = EndorsementFormData(endorsement.content,
            endorsement.name, endorsement.brandId, endorsement.company)
          ok(views.html.v2.endorsement.editForm(user, personId, brands, form.fill(formData), id))
      }
  }

  /**
   * Adds new endorsement if the endorsement is valid
   *
   * @param personId Person identifier
   */
  def create(personId: Long) = ProfileAction(personId) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      form.bindFromRequest.fold(
        error ⇒ brands(personId) flatMap { brands =>
          badRequest(views.html.v2.endorsement.addForm(user, personId, brands, error))},
        endorsementData ⇒
          (for {
            person <- repos.person.find(personId)
            endorsements <- repos.person.endorsements(personId)
          } yield (person, endorsements)) flatMap {
            case (None, _) => notFound(Messages("error.person.notFound"))
            case (Some(person), endorsements) =>
              val maxPosition = if (endorsements.nonEmpty)
                endorsements.last.position
              else
                0
              val endorsement = Endorsement(None, personId, endorsementData.brandId,
                endorsementData.content, endorsementData.name,
                endorsementData.company, maxPosition + 1)
              repos.person.insertEndorsement(endorsement) flatMap { _ =>
                redirect(core.routes.People.details(personId).url + "#experience")
              }
          })
  }

  /**
   * Adds new endorsements from a set of selected evaluations
   *
   * @param personId Person identifier
   */
  def createFromSelected(personId: Long) = ProfileAction(personId) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒
      val form = Form(single("evaluations" -> nonEmptyText))
      form.bindFromRequest.fold(
        error => jsonBadRequest("'evaluations' param is empty"),
        formData => {
          (for {
            events <- repos.cm.event.findByFacilitator(personId)
            endorsements <- repos.person.endorsements(personId)
            evaluations <- repos.cm.evaluation.findByEventsWithAttendees(events.map(_.identifier))
          } yield (events, endorsements, evaluations)) flatMap { case (events, endorsements, evaluations) =>
            val evaluationIds = endorsements.filter(_.evaluationId != 0).map(_.evaluationId)
            val receivedIds = Json.parse(formData).as[JsArray].value.toList.map(_.as[Long])
            val filteredEvaluations = evaluations.filter(x => receivedIds.contains(x._3.identifier)).
              filterNot(x => evaluationIds.contains(x._3.identifier))
            val maxPosition = maxEndorsementPosition(endorsements)
            val newEndorsements = filteredEvaluations.map { view =>
              val name = view._2.fullName
              val brandId = view._1.brandId
              Endorsement(None, personId, brandId, view._3.facilitatorReview, name,
                evaluationId = view._3.identifier, rating = Some(view._3.impression))
            }.zipWithIndex.map { x => x._1.copy(position = x._2 + 1 + maxPosition) }
            newEndorsements.foreach { x => repos.person.insertEndorsement(x) }
            val url = core.routes.People.details(personId).url + "#experience"
            jsonOk(Json.obj("url" -> url))
          }
        }
      )
  }

  /**
   * Creates an endorsement from the given evaluation
    *
    * @param eventId Event identifier
   * @param evaluationId Evaluation identifier
   */
  def createFromEvaluation(eventId: Long, evaluationId: Long) =
    EventAction(List(Role.Facilitator), eventId) {
      implicit request ⇒ implicit handler ⇒ implicit user ⇒ implicit event =>
        val personId = user.person.identifier
        if (event.facilitatorIds(repos).contains(personId)) {
          (for {
            view <- repos.cm.evaluation.findWithAttendee(evaluationId)
            endorsements <- repos.person.endorsements(personId)
          } yield (view, endorsements)) flatMap {
            case (None, _) => jsonNotFound("Evaluation doesn't exist")
            case (Some(view), endorsements) =>
              val maxPosition = maxEndorsementPosition(endorsements)
              val endorsement = Endorsement(None, personId,
                event.brandId, view.evaluation.facilitatorReview, view.attendee.fullName,
                position = maxPosition + 1, evaluationId = view.evaluation.id.get,
                rating = Some(view.evaluation.impression))
              repos.person.insertEndorsement(endorsement) flatMap { endorsement =>
                jsonOk(Json.obj("endorsementId" -> endorsement.id.get))
              }
          }
        } else {
          jsonBadRequest("Internal error. You shouldn't be able to make this request")
        }
  }

  /**
   * Updates positions of endorsements in bulk
   *
   * @param personId Person identifier
   */
  def updatePositions(personId: Long) = ProfileAction(personId) {
    implicit request => implicit handler => implicit user =>
      val form = Form(single("positions" -> nonEmptyText))
      form.bindFromRequest.fold(
        error => jsonBadRequest("Positions param is empty"),
        formData => {
          val positions = Json.parse(formData).as[JsArray]
          positions.value.foreach { x =>
            val id = (x \ "id").as[Long]
            val position = (x \ "position").as[Int]
            repos.person.updateEndorsementPosition(personId, id, position)
          }
          jsonSuccess("ok")
        }
      )
  }

  /**
   * Deletes the given endorsement if the endorsement exists and is belonged
   * to the given person
   *
   * Person identifier is used to check access rights
   *
   * @param personId Person identifier
   * @param id endorsement identifier
   */
  def remove(personId: Long, id: Long) = ProfileAction(personId) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      repos.person.deleteEndorsement(personId, id) flatMap { _ =>
        jsonSuccess("ok")
      }
  }

  /**
   * Updates the given endorsement if it's valid
   *
   * @param personId Person identifier
   * @param id Endorsement identifier
   */
  def update(personId: Long, id: Long) = ProfileAction(personId) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      form.bindFromRequest.fold(
        error ⇒ brands(personId) flatMap { brands =>
          badRequest(views.html.v2.endorsement.editForm(user, personId, brands, error, id))},
        endorsementData ⇒ {
          val endorsement = Endorsement(Some(id), personId, endorsementData.brandId,
            endorsementData.content, endorsementData.name,
            endorsementData.company)
          repos.person.updateEndorsement(endorsement) flatMap { _ =>
            val url: String = core.routes.People.details(personId).url + "#experience"
            redirect(url)
          }
        })
  }

  /**
   * Returns list of brands for which the given person has licenses
    *
    * @param personId Person identifier
   */
  protected def brands(personId: Long): Future[List[Brand]] =
    repos.cm.brand.findByLicense(personId).map(_.map(_.brand))

  /**
   * Returns maximum position of endorsements from the given list
    *
    * @param endorsements Endorsements
   */
  protected def maxEndorsementPosition(endorsements: List[Endorsement]): Int = {
    if (endorsements.nonEmpty)
      endorsements.last.position
    else
      0
  }
}
