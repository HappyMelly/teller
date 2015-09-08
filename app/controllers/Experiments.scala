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
import models.{ ActiveUser, Experiment, Member }
import models.UserRole.Role._
import models.service.Services
import play.api.Play
import play.api.Play.current
import play.api.data.Form
import play.api.data.Forms._
import securesocial.core.RuntimeEnvironment
import scala.concurrent.Future
import services.integrations.Integrations

class Experiments(environment: RuntimeEnvironment[ActiveUser])
    extends JsonController
    with Services
    with Security
    with Integrations
    with Files
    with Utilities {

  override implicit val env: RuntimeEnvironment[ActiveUser] = environment

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
  def add(memberId: Long) = AsyncSecuredRestrictedAction(Viewer) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        Future.successful(Ok(views.html.v2.experiment.form(user, memberId, form)))
  }

  /**
   * Creates new experiment for the given member
   *
   * @param memberId Member identifier
   */
  def create(memberId: Long) = AsyncSecuredDynamicAction("member", "editor") {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        form.bindFromRequest.fold(
          error ⇒ Future.successful(
            BadRequest(views.html.v2.experiment.form(user, memberId, error))),
          experiment ⇒ {
            memberService.find(memberId) map { member ⇒
              val inserted = experimentService.insert(experiment.copy(memberId = memberId))
              uploadImage(Experiment.picture(inserted.id.get), "file") map { _ ⇒
                experimentService.update(inserted.copy(picture = true))
              } recover {
                case e: RuntimeException ⇒ Unit
              } map { _ ⇒
                val url = if (member.person)
                  routes.People.details(member.objectId).url
                else
                  routes.Organisations.details(member.objectId).url
                notifyMembers(member, experiment, url + "#experiments")
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
  def delete(memberId: Long, id: Long) = AsyncSecuredDynamicAction("member", "editor") {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        experimentService.delete(memberId, id)
        Future.successful(jsonSuccess("ok"))
  }

  /**
   * Deletes picture of the given experiment
   *
   * Member identifier is used to check access rights
   *
   * @param memberId Member identifier
   * @param id Experiment identifier
   */
  def deletePicture(memberId: Long, id: Long) = AsyncSecuredDynamicAction("member", "editor") {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        experimentService.find(id) map { experiment ⇒
          Experiment.picture(id).remove()
          experimentService.update(experiment.copy(picture = false))
          Future.successful(jsonSuccess("ok"))
        } getOrElse Future.successful(jsonNotFound("Experiment not found"))
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
  def edit(memberId: Long, id: Long) = AsyncSecuredRestrictedAction(Viewer) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        experimentService.find(id) map { experiment ⇒
          Future.successful(
            Ok(views.html.v2.experiment.form(user, memberId, form.fill(experiment), Some(id))))
        } getOrElse Future.successful(NotFound("Experiment not found"))
  }

  /**
   * Renders list of experiments for the given member
   *
   * @param memberId Member
   */
  def experiments(memberId: Long) = AsyncSecuredRestrictedAction(Viewer) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        val experiments = experimentService.findByMember(memberId)
        Future.successful(Ok(views.html.v2.experiment.list(memberId, experiments)))
  }

  /**
   * Retrieve and cache a picture of the given experiment
   *
   * @param id Experiment identifier
   */
  def picture(id: Long, size: String = "") = {
    file(Experiment.picture(id).file(size))
  }

  /**
   * Updates the given member experiment if it's valid
   *
   * @param memberId Member identifier
   * @param id Experiment identifier
   */
  def update(memberId: Long, id: Long) = AsyncSecuredDynamicAction("member", "editor") {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        form.bindFromRequest.fold(
          error ⇒ Future.successful(
            BadRequest(views.html.v2.experiment.form(user, memberId, error))),
          experiment ⇒ {
            experimentService.find(id) map { existing ⇒
              memberService.find(memberId) map { member ⇒
                uploadImage(Experiment.picture(existing.id.get), "file") map { _ ⇒
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

  /**
   * Sends a message to Slack about new experiment
   *
   * @param member Member who created the experiment
   * @param experiment Experiment
   * @param url Link to the member's profile
   */
  protected def notifyMembers(member: Member, experiment: Experiment, url: String) {
    val who = "%s started a new experiment,".format(member.name)
    val what = "%s *%s* and it's awesome!".format(who, experiment.name)
    val msg = "%s Check it here %s. You may find it useful :wink:".format(what,
      fullUrl(url))
    Play.configuration.getString("slack.additional_channel") map { name ⇒
      slack.send(msg, Some(name))
    }
  }
}
