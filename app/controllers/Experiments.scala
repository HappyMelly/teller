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
package controllers

import controllers.Forms._
import models.Experiment
import models.UserRole.Role._
import models.service.Services
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent.Future
import services.integrations.Integrations

trait Experiments extends JsonController
  with Services
  with Security
  with Integrations
  with Files {

  val form = Form(mapping(
    "id" -> ignored(Option.empty[Long]),
    "memberId" -> ignored(0L),
    "name" -> nonEmptyText,
    "description" -> nonEmptyText,
    "picture" -> ignored(false),
    "url" -> optional(webUrl))(Experiment.apply)(Experiment.unapply))

  /**
   * Renders add form for an experiment
   *
   * @param memberId Member identifier
   */
  def add(memberId: Long) = SecuredRestrictedAction(Viewer) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        Ok(views.html.experiment.form(user, memberId, form))
  }

  /**
   * Creates new experiment for the given member
   *
   * @param memberId Member identifier
   */
  def create(memberId: Long) = AsyncSecuredRestrictedAction(Viewer) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        form.bindFromRequest.fold(
          error ⇒ Future.successful(
            BadRequest(views.html.experiment.form(user, memberId, error))),
          experiment ⇒ {
            memberService.find(memberId) map { member ⇒
              val inserted = experimentService.insert(experiment.copy(memberId = memberId))
              uploadFile(Experiment.picture(inserted.id.get), "file") map { _ ⇒
                experimentService.update(inserted.copy(picture = true))
              } recover {
                case e: RuntimeException ⇒ Unit
              } map { _ ⇒
                val url = if (member.person)
                  routes.People.details(member.objectId).url
                else
                  routes.Organisations.details(member.objectId).url
                Redirect(url + "#experiments")
              }
            } getOrElse Future.successful(NotFound("Member not found"))
          })
  }

  /**
   * Deletes the given member experiment if the experiment exists and is
   * belonged to the given member
   *
   * Member identifier is used to check access rights
   *
   * @param memberId Member identifier
   * @param id Experiment identifier
   */
  def delete(memberId: Long, id: Long) = SecuredRestrictedAction(Viewer) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        experimentService.delete(memberId, id)
        jsonSuccess("ok")
  }

  /**
   * Deletes picture of the given experiment
   *
   * Member identifier is used to check access rights
   *
   * @param memberId Member identifier
   * @param id Experiment identifier
   */
  def deletePicture(memberId: Long, id: Long) = SecuredRestrictedAction(Viewer) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        experimentService.find(id) map { experiment ⇒
          Experiment.picture(id).remove()
          experimentService.update(experiment.copy(picture = false))
          jsonSuccess("ok")
        } getOrElse jsonNotFound("Experiment not found")
  }

  /**
   * Renders an edit form for the given experiment if the experiment exists and is
   * belonged to the given member
   *
   * Member identifier is used to check access rights
   *
   * @param memberId Member identifier
   * @param id Experiment identifier
   */
  def edit(memberId: Long, id: Long) = SecuredRestrictedAction(Viewer) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        experimentService.find(id) map { experiment ⇒
          Ok(views.html.experiment.form(user, memberId, form.fill(experiment), Some(id)))
        } getOrElse NotFound("Experiment not found")
  }

  /**
   * Renders list of experiments for the given member
   *
   * @param memberId Member
   */
  def experiments(memberId: Long) = SecuredRestrictedAction(Viewer) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        val experiments = experimentService.findByMember(memberId)
        Ok(views.html.experiment.list(memberId, experiments))
  }

  /**
   * Retrieve and cache a picture of the given experiment
   *
   * @param id Experiment identifier
   */
  def picture(id: Long) = file(Experiment.picture(id))

  /**
   * Updates the given member experiment if it's valid
   *
   * @param memberId Member identifier
   * @param id Experiment identifier
   */
  def update(memberId: Long, id: Long) = AsyncSecuredRestrictedAction(Viewer) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        form.bindFromRequest.fold(
          error ⇒ Future.successful(
            BadRequest(views.html.experiment.form(user, memberId, error))),
          experiment ⇒ {
            experimentService.find(id) map { existing ⇒
              memberService.find(memberId) map { member ⇒
                uploadFile(Experiment.picture(existing.id.get), "file") map { _ ⇒
                  experimentService.update(experiment.copy(id = Some(id),
                    memberId = memberId, picture = true))
                } recover {
                  case e: RuntimeException ⇒ experimentService.update(experiment.copy(id = Some(id),
                    memberId = memberId, picture = existing.picture))
                } map { _ ⇒
                  val url = if (member.person)
                    routes.People.details(member.objectId).url
                  else
                    routes.Organisations.details(member.objectId).url
                  Redirect(url + "#experiments")
                }
              } getOrElse Future.successful(NotFound("Member not found"))
            } getOrElse Future.successful(NotFound("Experiment not found"))
          })
  }
}

object Experiments extends Experiments