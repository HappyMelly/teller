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

package controllers.core

import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import controllers.{Security, BrandAware}
import controllers.security.LoginReminder
import models.UserRole.Role._
import models._
import models.cm.Event
import models.repository.Repositories
import org.joda.time.LocalDate
import play.api.i18n.{I18nSupport, MessagesApi}
import services.TellerRuntimeEnvironment

import scala.concurrent.Future

class Dashboard @javax.inject.Inject() (override implicit val env: TellerRuntimeEnvironment,
                                        override val messagesApi: MessagesApi,
                                        val repos: Repositories,
                                        deadbolt: DeadboltActions, handlers: HandlerCache, actionBuilder: ActionBuilders)
  extends Security(deadbolt, handlers, actionBuilder, repos)(messagesApi, env)
    with BrandAware
    with I18nSupport {

  /**
    * About page - credits.
    */
  def about = RestrictedAction(Admin) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    ok(views.html.about(user))
  }

  /**
    * API v2 documentation page.
    */
  def apiv2 = RestrictedAction(Viewer) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    ok(views.html.apiv2.index(user))
  }

  /**
    * Dashboard page - logged-in home page.
    */
  def index = RestrictedAction(List(Viewer, Unregistered)) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      if (user.account.registered) {
        if (user.account.member || user.account.admin)
          ok(views.html.v2.dashboard.forMembers(user))
        else
          redirect(routes.LoginPage.logout(Some("error"), Some("We moved to https://workshopbutler.com")))
      } else {
        val url = controllers.security.routes.LoginReminder.page().url
        val (session, (typ, msg)) = LoginReminder.updateCounter(request.session, url)
        val call = routes.LoginPage.logout(Some(typ), Some(msg))
        redirect(call.url, session)
      }
  }


  /**
    * Redirect to the current user’s `Person` details page. This is implemented as a redirect to avoid executing
    * the `LoginIdentity.person` database query for every page, to get the person ID for the details page URL.
    */
  def profile = RestrictedAction(Viewer) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    val currentUser = user.person
    redirect(routes.People.details(currentUser.id.getOrElse(0)))
  }
}

