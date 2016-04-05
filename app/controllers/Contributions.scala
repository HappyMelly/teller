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

import javax.inject.Inject

import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import models.UserRole.Role._
import models.repository.Repositories
import models.{ActivityRecorder, Contribution}
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.MessagesApi
import services.TellerRuntimeEnvironment

import scala.concurrent.Future

class Contributions @Inject() (override implicit val env: TellerRuntimeEnvironment,
                               override val messagesApi: MessagesApi,
                               val repos: Repositories,
                               deadbolt: DeadboltActions, handlers: HandlerCache, actionBuilder: ActionBuilders)
  extends Security(deadbolt, handlers, actionBuilder, repos)(messagesApi, env)
  with Activities {

  /** HTML form mapping for creating and editing. */
  def contributionForm = Form(mapping(
    "id" -> ignored(Option.empty[Long]),
    "contributorId" -> nonEmptyText.transform(_.toLong, (l: Long) ⇒ l.toString),
    "productId" -> nonEmptyText.transform(_.toLong, (l: Long) ⇒ l.toString),
    "isPerson" -> text.transform(_.toBoolean, (b: Boolean) ⇒ b.toString),
    "role" -> nonEmptyText)(Contribution.apply)(Contribution.unapply))

  /**
   * Add new contribution to a product
    *
    * @param page Label of a page where the action happened
   * @return
   */
  def create(page: String) = DynamicAction(Funder, 0)  { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      val boundForm: Form[Contribution] = contributionForm.bindFromRequest
      val contributorId = boundForm.data("contributorId").toLong
      val route: String = if (page == "organisation") {
        core.routes.Organisations.details(contributorId).url
      } else if (page == "person") {
        core.routes.People.details(contributorId).url + "#contributions"
      } else {
        hm.routes.Products.details(boundForm.data("productId").toLong).url
      }
      boundForm.bindFromRequest.fold(
        formWithErrors ⇒ redirect(route, "error" -> "A role for a contributor cannot be empty"),
        success ⇒ {
          val contributor: Future[Option[ActivityRecorder]] = if (success.isPerson)
            repos.person.find(success.contributorId)
          else
            repos.org.find(success.contributorId)
          contributor flatMap {
            case None => redirect(route, "error" -> "Contributor not found")
            case Some(c) =>
              repos.contribution.insert(success) flatMap { _ =>
                redirect(route, "success" -> "Contributor was added")
              }
          }
        })
  }

  /**
   * Delete a contribution
   *
   * @param id Contribution identifier
   * @param page Label of a page where the action happened
   * @return
   */
  def delete(id: Long, page: String) = DynamicAction(Funder, 0) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      repos.contribution.find(id) flatMap {
        case None => notFound("Contribution not found")
        case Some(contribution) =>
          repos.contribution.delete(id) flatMap { _ =>
            val route: String = if (page == "organisation") {
              core.routes.Organisations.details(contribution.contributorId).url
            } else if (page == "product") {
              hm.routes.Products.details(contribution.productId).url
            } else {
              core.routes.People.details(contribution.contributorId).url + "#contributions"
            }
            redirect(route, "success" -> "Contribution was deleted")
          }
      }
  }

}
