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

package controllers.cm

import javax.inject.Inject

import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import controllers.{BrandAware, Security, core}
import models.UserRole.Role
import models.repository.Repositories
import play.api.i18n.MessagesApi
import play.api.mvc.Action
import services.TellerRuntimeEnvironment

/**
  * Contains methods for managing event requests UI
  */
class EventRequests @Inject() (override implicit val env: TellerRuntimeEnvironment,
                               override val messagesApi: MessagesApi,
                               val repos: Repositories,
                               deadbolt: DeadboltActions, handlers: HandlerCache, actionBuilder: ActionBuilders)
  extends Security(deadbolt, handlers, actionBuilder, repos)(messagesApi, env)
  with BrandAware {

  /**
    * Renders details info for the given request
    *
    * @param brandId Brand identifier
    * @param requestId Request identifier
    */
  def details(brandId: Long, requestId: Long) = BrandAction(brandId) { implicit request =>
    implicit handler => implicit user =>
      repos.cm.rep.event.request.find(requestId) flatMap {
        case None => jsonBadRequest("Event request not found")
        case Some(eventRequest) => ok(views.html.v2.eventRequest.details(eventRequest))
      }
  }

  /**
    * Returns list of event requests for the given brand
    *
    * @param brandId Brand identifier
    */
  def index(brandId: Long) = RestrictedAction(List(Role.Facilitator, Role.Coordinator)) {
    implicit request => implicit handler => implicit user =>
      repos.cm.rep.event.request.findByBrand(brandId) flatMap { requests =>
        roleDiffirentiator(user.account, Some(brandId)) { (view, brands) =>
          ok(views.html.v2.eventRequest.index(user, view.brand, brands, requests))
        } { (brand, brands) =>
          redirect(core.routes.Dashboard.index())
        } {
          redirect(core.routes.Dashboard.index())
        }
      }
  }

  /**
    * Unsubscribes from automatic upcoming event notifications
 *
    * @param hashedId Hashed unique id
    */
  def unsubscribe(hashedId: String) = Action.async { implicit request ⇒
    repos.cm.rep.event.request.find(hashedId) flatMap {
      case None => notFound(views.html.v2.eventRequest.notfound())
      case Some(eventRequest) =>
        repos.cm.rep.event.request.update(eventRequest.copy(unsubscribed = true)) flatMap { _ =>
          ok(views.html.v2.eventRequest.unsubscribed())
        }
    }
  }


}
