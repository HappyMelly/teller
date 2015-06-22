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
import services.integrations.Integrations

trait Experiments extends JsonController
  with Services
  with Security
  with Integrations {

  val form = Form(mapping(
    "id" -> ignored(Option.empty[Long]),
    "memberId" -> longNumber,
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
        memberService.find(memberId) map { member ⇒
          Ok(views.html.experiment.form(user, memberId, form))
        } getOrElse NotFound("Member not found")
  }

  def create(memberId: Long) = SecuredRestrictedAction(Viewer) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        memberService.find(memberId) map { member ⇒
          form.bindFromRequest.fold(
            error ⇒ BadRequest(views.html.experiment.form(user, memberId, error)),
            experiment ⇒ {
              experimentService.insert(experiment.copy(memberId = memberId))
              val url = routes.People.details(member.objectId).url + "#experiments"
              Redirect(url)
            })
        } getOrElse NotFound("Member not found")
  }

  /**
   * Deletes the given member experiment if the experiment exists and is
   * belonged to the given member
   *
   * Member identifier is used to check access rights
   *
   * @param memberId Member identifier
   * @param id Testimonial identifier
   */
  def delete(memberId: Long, id: Long) = SecuredRestrictedAction(Viewer) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        experimentService.delete(memberId, id)
        jsonSuccess("ok")
  }

  def edit(memberId: Long, id: Long) = SecuredRestrictedAction(Viewer) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        experimentService.find(id) map { experiment ⇒
          Ok(views.html.experiment.form(user, memberId, form.fill(experiment), Some(id)))
        } getOrElse NotFound("Experiment is not found")
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
   * Updates the given member experiment if it's valid
   *
   * @param memberId Member identifier
   * @param id Experiment identifier
   */
  def update(memberId: Long, id: Long) = SecuredRestrictedAction(Viewer) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        form.bindFromRequest.fold(
          error ⇒ BadRequest(views.html.experiment.form(user, memberId, error)),
          experiment ⇒ {
            experimentService.update(experiment)
            val url = routes.Brands.details(memberId).url + "#testimonials"
            Redirect(url)
          })
  }
}