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

import javax.inject.Inject

import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import controllers.Forms._
import models.UserRole.Role
import models.repository.Repositories
import models.{DateStamp, Experiment, Member}
import org.joda.time.DateTime
import play.api.Play
import play.api.Play.current
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.MessagesApi
import services.TellerRuntimeEnvironment
import services.integrations.{Email, Integrations}

import scala.concurrent.Future

class Experiments @Inject() (override implicit val env: TellerRuntimeEnvironment,
                             override val messagesApi: MessagesApi,
                             val services: Repositories,
                             val email: Email,
                             deadbolt: DeadboltActions, handlers: HandlerCache, actionBuilder: ActionBuilders)
  extends Security(deadbolt, handlers, actionBuilder, services)(messagesApi, env)
  with BrandAware
  with Integrations
  with Files {

  def form(editorName: String) = Form(mapping(
    "id" -> ignored(Option.empty[Long]),
    "memberId" -> ignored(0L),
    "name" -> nonEmptyText,
    "description" -> nonEmptyText,
    "picture" -> ignored(false),
    "url" -> optional(webUrl),
    "recordInfo" -> mapping(
      "created" -> ignored(DateTime.now()),
      "createdBy" -> ignored(editorName),
      "updated" -> ignored(DateTime.now()),
      "updatedBy" -> ignored(editorName))(DateStamp.apply)(DateStamp.unapply)
    )(Experiment.apply)(Experiment.unapply))

  /**
   * Renders add form for an experiment
   *
   * @param memberId Member identifier
   */
  def add(memberId: Long) = RestrictedAction(Role.Viewer) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒
      Future.successful(
        Ok(views.html.v2.experiment.form(user, memberId, form(user.name))))
  }

  /**
   * Creates new experiment for the given member
   *
   * @param memberId Member identifier
   */
  def create(memberId: Long) = DynamicAction(Role.Member, memberId) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒
      form(user.name).bindFromRequest.fold(
        error ⇒ badRequest(views.html.v2.experiment.form(user, memberId, error)),
        experiment ⇒ {
          services.member.find(memberId) flatMap {
            case None => notFound("Member not found")
            case Some(member) ⇒
              services.experiment.insert(experiment.copy(memberId = memberId)) flatMap { inserted =>
                uploadImage(Experiment.picture(inserted.id.get), "file") map { _ ⇒
                  services.experiment.update(inserted.copy(picture = true))
                } recover {
                  case e: RuntimeException ⇒ Unit
                } map { _ ⇒
                  val url = if (member.person)
                    core.routes.People.details(member.objectId).url
                  else
                    core.routes.Organisations.details(member.objectId).url
                  notifyMembers(member, experiment, url + "#experiments")
                  Redirect(url + "#experiments")
                }
              }
          }
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
  def delete(memberId: Long, id: Long) = DynamicAction(Role.Member, memberId) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      services.experiment.delete(memberId, id) flatMap { _ =>
        jsonSuccess("ok")
      }
  }

  /**
   * Deletes picture of the given experiment
   *
   * Member identifier is used to check access rights
   *
   * @param memberId Member identifier
   * @param id Experiment identifier
   */
  def deletePicture(memberId: Long, id: Long) = DynamicAction(Role.Member, memberId) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒
      services.experiment.find(id) flatMap {
        case None => jsonNotFound("Experiment not found")
        case Some(experiment) =>
          Experiment.picture(id).remove()
          services.experiment.update(experiment.copy(picture = false))
          jsonSuccess("ok")
      }
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
  def edit(memberId: Long, id: Long) = RestrictedAction(Role.Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      services.experiment.find(id) flatMap {
        case None => notFound("Experiment not found")
        case Some(experiment) =>
          ok(views.html.v2.experiment.form(user, memberId, form(user.name).fill(experiment), Some(id)))
      }
  }

  /**
   * Renders list of experiments for the given member
   *
   * @param memberId Member
   */
  def experiments(memberId: Long) = RestrictedAction(Role.Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      services.experiment.findByMember(memberId) flatMap { experiments =>
        ok(views.html.v2.experiment.list(memberId, experiments))
      }
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
  def update(memberId: Long, id: Long) = DynamicAction(Role.Member, memberId) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒
      form(user.name).bindFromRequest.fold(
        error ⇒ badRequest(views.html.v2.experiment.form(user, memberId, error)),
        experiment ⇒ {
          (for {
            existing <- services.experiment.find(id)
            member <- services.member.find(memberId)
          } yield (existing, member)) flatMap {
            case (None, _) => notFound("Experiment not found")
            case (_, None) => notFound("Member not found")
            case (Some(existing), Some(member)) =>
              uploadImage(Experiment.picture(existing.id.get), "file") map { _ ⇒
                services.experiment.update(experiment.copy(id = Some(id),
                  memberId = memberId, picture = true))
              } recover {
                case e: RuntimeException ⇒ services.experiment.update(experiment.copy(id = Some(id),
                  memberId = memberId, picture = existing.picture))
              } map { _ ⇒
                val url = if (member.person)
                  core.routes.People.details(member.objectId).url
                else
                  core.routes.Organisations.details(member.objectId).url
                Redirect(url + "#experiments")
              }
          }
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
    val who = "%s started a new experiment.".format(member.name)
    val hmUrl = s"http://happymelly.com/members/#/${member.identifier}"
    val url = experiment.url.map { value => "Experiment link: " + value } getOrElse ""
    val msg =
      """%s

*%s*
%s

%s

Check it here %s. You may find it useful :wink:
      """.format(who, experiment.name, experiment.description, url, hmUrl)
    Play.configuration.getString("slack.channel") map { name ⇒
      slack.send(msg, Some(name))
    }
  }
}

object Experiments {

  /**
    * Returns url to an experiment's picture
    *
    * @param experiment Experiment
    */
  def pictureUrl(experiment: Experiment): Option[String] = {
    if (experiment.picture) {
      val picture = Experiment.picture(experiment.id.get)
      val url = Utilities.fullUrl(routes.Experiments.picture(experiment.id.get).url)
      Utilities.cdnUrl(picture.name).orElse(Some(url))
    } else {
      None
    }
  }
}