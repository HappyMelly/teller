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
 * If you have questions concerning this license or the applicable additional terms,
 * you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */
package controllers

import models.UserRole.Role
import models.service.Services
import models.{ActiveUser, Brand, Endorsement}
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages
import play.api.libs.json.{JsArray, JsValue, Json, Writes}
import securesocial.core.RuntimeEnvironment

import scala.concurrent.Future

case class EndorsementFormData(content: String,
  name: String,
  brandId: Long,
  company: Option[String])

class Endorsements(environment: RuntimeEnvironment[ActiveUser])
    extends JsonController
    with Services
    with Security {

  override implicit val env: RuntimeEnvironment[ActiveUser] = environment

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
   * @param personId Person identifier
   */
  def add(personId: Long) = SecuredProfileAction(personId) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        personService.find(personId) map { person ⇒
          Ok(views.html.v2.endorsement.addForm(user, personId, brands(personId), form))
        } getOrElse NotFound(Messages("error.person.notFound"))
  }

  /**
   * Renders the form where a facilitator could select an endorsement from
   *  existing evaluations
   *
   * @param personId Person id
   */
  def renderSelectForm(personId: Long) = SecuredProfileAction(personId) {
    implicit request =>
      implicit handler => implicit user =>
        val brands = brandService.findAll
        val events = eventService.findByFacilitator(personId).map { x =>
          (x, brands.find(_.id.get == x.brandId).map(_.name).getOrElse(""))
        }
        val evaluationIds = personService.endorsements(personId).
          filter(_.evaluationId != 0).map(_.evaluationId)
        val evaluations = evaluationService.
          findByEvents(events.map(_._1.id.get)).
          filterNot(x => evaluationIds.contains(x.id.get))

        val people = personService.find(evaluations.map(_.personId).distinct)
        val content = evaluations.sortBy(_.impression).reverse.map { x =>
          (x,
            people.find(_._1.id.get == x.personId).map(_._1.fullName).getOrElse(""),
            events.find(_._1.id.get == x.eventId).map(_._2).getOrElse(""))
        }
        Ok(views.html.v2.endorsement.selectForm(user, personId, content))
  }

  /**
   * Renders endorsement edit form
   * @param personId Person identifier
   * @param id Endorsement identifier
   */
  def edit(personId: Long, id: Long) = SecuredProfileAction(personId) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        personService.find(personId) map { person ⇒
          personService.findEndorsement(id) map { endorsement ⇒
            val formData = EndorsementFormData(endorsement.content,
              endorsement.name, endorsement.brandId, endorsement.company)
            Ok(views.html.v2.endorsement.editForm(user, personId, brands(personId), form.fill(formData), id))
          } getOrElse NotFound("Endorsement is not found")
        } getOrElse NotFound(Messages("error.person.notFound"))
  }

  /**
   * Adds new endorsement if the endorsement is valid
   *
   * @param personId Person identifier
   */
  def create(personId: Long) = SecuredProfileAction(personId) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        personService.find(personId) map { person ⇒
          form.bindFromRequest.fold(
            error ⇒
              BadRequest(views.html.v2.endorsement.addForm(user, personId, brands(personId), error)),
            endorsementData ⇒ {
              val endorsements = personService.endorsements(personId)
              val maxPosition = if (endorsements.nonEmpty)
                endorsements.last.position
              else
                0
              val endorsement = Endorsement(None, personId, endorsementData.brandId,
                endorsementData.content, endorsementData.name,
                endorsementData.company, maxPosition + 1)
              personService.insertEndorsement(endorsement)
              val url = routes.People.details(personId).url + "#experience"
              Redirect(url)
            })
        } getOrElse NotFound(Messages("error.person.notFound"))
  }

  /**
   * Adds new endorsements from a set of selected evaluations
   *
   * @param personId Person identifier
   */
  def createFromSelected(personId: Long) = SecuredProfileAction(personId) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒
      val form = Form(single("evaluations" -> nonEmptyText))
      form.bindFromRequest.fold(
        error => jsonBadRequest("'evaluations' param is empty"),
        formData => {
          val receivedIds = Json.parse(formData).as[JsArray].value.toList.map(_.as[Long])
          val brands = brandService.findAll
          val events = eventService.findByFacilitator(personId).map { x =>
            (x, brands.find(_.id.get == x.brandId).map(_.name).getOrElse(""))
          }
          val endorsements = personService.endorsements(personId)
          val evaluationIds = endorsements.filter(_.evaluationId != 0).map(_.evaluationId)
          val evaluations = evaluationService.
            findByEvents(events.map(_._1.id.get)).
            filter(x => receivedIds.contains(x.id.get)).
            filterNot(x => evaluationIds.contains(x.id.get))
          val people = personService.find(evaluations.map(_.personId).distinct)
          val maxPosition = maxEndorsementPosition(endorsements)
          val newEndorsements = evaluations.map { x =>
            val name = people.find(_._1.id.get == x.personId).map(_._1.fullName).getOrElse("")
            val brandId = events.find(_._1.id.get == x.eventId).map(_._1.brandId).getOrElse(0L)
            Endorsement(None, personId, brandId, x.facilitatorReview, name,
              evaluationId = x.id.get, rating = Some(x.impression))
          }.zipWithIndex.map { x => x._1.copy(position = x._2 + 1 + maxPosition) }
          newEndorsements.foreach { x => personService.insertEndorsement(x) }
          val url = routes.People.details(personId).url + "#experience"
          jsonOk(Json.obj("url" -> url))
        }
      )
  }

  /**
   * Creates an endorsement from the given evaluation
   * @param eventId Event identifier
   * @param evaluationId Evaluation identifier
   */
  def createFromEvaluation(eventId: Long, evaluationId: Long) =
    AsyncSecuredEventAction(eventId, Role.Facilitator) {
      implicit request ⇒ implicit handler ⇒ implicit user ⇒ implicit event =>
        val personId = user.person.identifier
        if (event.facilitatorIds.contains(personId)) {
          evaluationService.findWithParticipant(evaluationId) map { view =>
            val endorsements = personService.endorsements(personId)
            val maxPosition = maxEndorsementPosition(endorsements)
            val endorsement = Endorsement(None, personId,
              event.brandId, view.evaluation.facilitatorReview, view.person.fullName,
              position = maxPosition + 1, evaluationId = view.evaluation.id.get,
              rating = Some(view.evaluation.impression))
            val id = personService.insertEndorsement(endorsement).id.get
            Future.successful(jsonOk(Json.obj("endorsementId" -> id)))
          } getOrElse Future.successful(jsonNotFound("Evaluation doesn't exist"))
        } else {
          Future.successful(jsonBadRequest("Internal error. You shouldn't be able to make this request"))
        }
  }

        /**
   * Updates positions of endorsements in bulk
   *
   * @param personId Person identifier
   */
  def updatePositions(personId: Long) = SecuredProfileAction(personId) {
    implicit request => implicit handler => implicit user =>
        val form = Form(single("positions" -> nonEmptyText))
        form.bindFromRequest.fold(
          error => jsonBadRequest("Positions param is empty"),
          formData => {
            val positions = Json.parse(formData).as[JsArray]
            positions.value.foreach { x =>
              val id = (x \ "id").as[Long]
              val position = (x \ "position").as[Int]
              personService.updateEndorsementPosition(personId, id, position)
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
  def remove(personId: Long, id: Long) = SecuredProfileAction(personId) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        personService.deleteEndorsement(personId, id)
        jsonSuccess("ok")
  }

  /**
   * Updates the given endorsement if it's valid
   *
   * @param personId Person identifier
   * @param id Endorsement identifier
   */
  def update(personId: Long, id: Long) = SecuredProfileAction(personId) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        form.bindFromRequest.fold(
          error ⇒ BadRequest(views.html.v2.endorsement.editForm(user, personId, brands(personId), error, id)),
          endorsementData ⇒ {
            val endorsement = Endorsement(Some(id), personId, endorsementData.brandId,
              endorsementData.content, endorsementData.name,
              endorsementData.company)
            personService.updateEndorsement(endorsement)
            val url = routes.People.details(personId).url + "#experience"
            Redirect(url)
          })
  }

  /**
   * Returns list of brands for which the given person has licenses
   * @param personId Person identifier
   */
  protected def brands(personId: Long): List[Brand] =
    licenseService.licenses(personId).map(_.brand)

  /**
   * Returns maximum position of endorsements from the given list
   * @param endorsements Endorsements
   */
  protected def maxEndorsementPosition(endorsements: List[Endorsement]): Int = {
    if (endorsements.nonEmpty)
      endorsements.last.position
    else
      0
  }
}
