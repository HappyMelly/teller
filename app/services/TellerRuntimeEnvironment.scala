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

package services

import javax.inject.Inject

import models.repository.{IRepositories, Repositories}
import models.{ActiveUser, Recipient}
import play.api.i18n.MessagesApi
import play.twirl.api.{Html, Txt}
import securesocial.controllers.ViewTemplates
import securesocial.core.RuntimeEnvironment
import securesocial.core.providers._
import securesocial.core.providers.utils.Mailer
import securesocial.core.services.RoutesService
import services.integrations.{EmailComponent, Email}
import templates.{MailTemplates, SecureSocialTemplates}

import scala.collection.immutable.ListMap

/**
  * Runtime environment for SecureSocial library to work
  */
class TellerRuntimeEnvironment @Inject() (val messagesApi: MessagesApi,
                                          val email: EmailComponent,
                                          val services: IRepositories) extends RuntimeEnvironment.Default {
  type U = ActiveUser

  override lazy val routes: RoutesService = new TellerRoutesService()
  override lazy val viewTemplates: ViewTemplates = new SecureSocialTemplates(this, messagesApi)
  override lazy val mailTemplates: MailTemplates = new MailTemplates(this)
  override lazy val mailer: Mailer = new MailerTest(mailTemplates, email)
  override lazy val userService: LoginIdentityService = new LoginIdentityService(services)
  override lazy val providers = ListMap(
    include(new TwitterProvider(routes, cacheService, oauth1ClientFor(TwitterProvider.Twitter))),
    include(new FacebookProvider(routes, cacheService, oauth2ClientFor(FacebookProvider.Facebook))),
    include(new GoogleProvider(routes, cacheService, oauth2ClientFor(GoogleProvider.Google))),
    include(new LinkedInProvider(routes, cacheService, oauth1ClientFor(LinkedInProvider.LinkedIn))),
    include(new UsernamePasswordProvider[ActiveUser](userService, None, viewTemplates, passwordHashers)(executionContext))
  )
}

class MailerTest(mailTemplates: securesocial.controllers.MailTemplates, email: EmailComponent) extends Mailer.Default(mailTemplates) {
  private val logger = play.api.Logger("securesocial.core.providers.utils.Mailer.Default")

  override def sendEmail(subject: String, emailAddress: String, body: (Option[Txt], Option[Html])) {

    logger.debug(s"[securesocial] sending email to $emailAddress")
    logger.debug(s"[securesocial] mail = [$body]")

    case class NoNameRecipient(email: String) extends Recipient {
      def fullName: String = ""
    }

    val recipient = NoNameRecipient(emailAddress)
    email.send(Set(recipient), None, None, subject, body._2.map(html => html.body).getOrElse(""), fromAddress, true)
  }
}