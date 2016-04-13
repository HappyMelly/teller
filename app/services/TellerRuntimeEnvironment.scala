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

import akka.actor.ActorSystem
import models.repository.IRepositories
import models.{ActiveUser, Recipient}
import play.api.i18n.MessagesApi
import play.api.mvc.RequestHeader
import play.api.{Environment, Configuration, Mode}
import play.twirl.api.{Html, Txt}
import securesocial.controllers.ViewTemplates
import securesocial.core.providers.utils.Mailer
import securesocial.core.providers.{FacebookProvider, GoogleProvider, LinkedInProvider, UsernamePasswordProvider}
import securesocial.core.services.RoutesService
import securesocial.core.{OAuth2Settings, RuntimeEnvironment}
import security.{MailChimpClient, MailChimpProvider, TwitterProvider}
import services.integrations.EmailComponent
import templates.{MailTemplates, SecureSocialTemplates}

import scala.collection.immutable.ListMap
import scala.concurrent.Future

/**
  * Runtime environment for SecureSocial library to work
  */
class TellerRuntimeEnvironment @Inject() (messagesApi: MessagesApi,
                                          email: EmailComponent,
                                          repos: IRepositories,
                                          actors: ActorSystem,
                                          environment: Environment,
                                          configuration: Configuration) extends RuntimeEnvironment.Default {
  type U = ActiveUser

  override lazy val routes: RoutesService = new TellerRoutesService()
  override lazy val viewTemplates: ViewTemplates = new SecureSocialTemplates(this, messagesApi)
  override lazy val mailTemplates: MailTemplates = new MailTemplates(this)
  override lazy val mailer: Mailer = new MailerTest(mailTemplates, email)
  override lazy val userService: LoginIdentityService = new LoginIdentityService(repos)
  override lazy val providers = ListMap(
    include(new TwitterProvider(routes, cacheService, TwitterProvider.authClient(httpService))),
    include(new FacebookProvider(routes, cacheService, oauth2ClientFor(FacebookProvider.Facebook))),
    include(new GoogleProvider(routes, cacheService, oauth2ClientFor(GoogleProvider.Google))),
    include(new LinkedInProvider(routes, cacheService, oauth1ClientFor(LinkedInProvider.LinkedIn))),
    include(new MailChimpProvider(routes, cacheService,
      new MailChimpClient(httpService, OAuth2Settings.forProvider(MailChimpProvider.MailChimp)))),
    include(new UsernamePasswordProvider[ActiveUser](userService, None, viewTemplates, passwordHashers)(executionContext))
  )

  def isDev: Boolean = environment.mode == Mode.Dev

  def isNotProd: Boolean = isDev || isStage

  def isStage: Boolean = environment.mode == Mode.Prod && configuration.getString("mode").contains("stage")

  def isProd: Boolean = environment.mode == Mode.Prod && !isStage

  /**
    * Updates cached object for active user
    *
    * @param user Active user
    */
  def updateCurrentUser(user: ActiveUser)(implicit request: RequestHeader): Future[Unit] = {
    this.authenticatorService.fromRequest.map(auth ⇒ auth.foreach {
      _.updateUser(user)
    })
  }
}

class MailerTest(mailTemplates: securesocial.controllers.MailTemplates, email: EmailComponent)
  extends Mailer.Default(mailTemplates) {

  private val logger = play.api.Logger("securesocial.core.providers.utils.Mailer.Default")

  override def sendEmail(subject: String, emailAddress: String, body: (Option[Txt], Option[Html])) {

    logger.debug(s"[securesocial] sending email to $emailAddress")
    logger.debug(s"[securesocial] mail = [$body]")

    case class NoNameRecipient(email: String) extends Recipient {
      def fullName: String = ""
    }

    val recipient = NoNameRecipient(emailAddress)
    email.sendSystem(Seq(recipient), subject, body._2.map(html => html.body).getOrElse(""), fromAddress)
  }
}