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

package controllers.cm.facilitator

import javax.inject.Inject

import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import controllers.Security
import models.UserRole.Role._
import models.repository.Repositories
import play.api.i18n.MessagesApi
import securesocial.core.SecureSocial
import security.MailChimpProvider
import services.TellerRuntimeEnvironment

/**
  * Contains methods for managing MailChimp integrations for facilitators
  */
class Mailchimp @Inject() (override implicit val env: TellerRuntimeEnvironment,
                           override val messagesApi: MessagesApi,
                           val repos: Repositories,
                           deadbolt: DeadboltActions, handlers: HandlerCache, actionBuilder: ActionBuilders)
  extends Security(deadbolt, handlers, actionBuilder, repos)(messagesApi, env) {

  def authenticate = RestrictedAction(Viewer) { implicit request => implicit handler => implicit user =>
    if (!user.account.facilitator) {
      forbidden("This page is accessible to facilitators only")
    } else {
      val url = controllers.core.routes.People.details(user.person.identifier).url + "#mailchimp"
      val session = request.session -
        SecureSocial.OriginalUrlKey +
        (SecureSocial.OriginalUrlKey -> url)
      val route = env.routes.authenticationUrl(MailChimpProvider.MailChimp)
      redirect(route, session)
    }
  }

  def settings(personId: Long) = RestrictedAction(Viewer) { implicit request => implicit handler => implicit user =>
    if (!user.account.facilitator) {
      forbidden("This page is accessible to facilitators only")
    } else {
      ok(views.html.v2.person.tabs.mailchimp(1, false))
    }
  }
}
