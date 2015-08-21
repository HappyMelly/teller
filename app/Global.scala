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
 * If you have questions concerning this license or the applicable additional terms, you may contact
 * by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */

import java.lang.reflect.Constructor
import java.util.concurrent.TimeUnit

import mail.reminder.{ ProfileStrengthReminder, EventReminder }
import services.{TellerRoutesService, LoginIdentityService}
import models.ActiveUser
import org.joda.time.{ LocalDate, LocalDateTime, LocalTime, Seconds }
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.Results._
import play.api.mvc._
import play.api.{ Application, GlobalSettings, Play }
import play.filters.csrf._
import play.libs.Akka
import securesocial.controllers.ViewTemplates
import securesocial.core._
import securesocial.core.providers.{ FacebookProvider, GoogleProvider, LinkedInProvider, TwitterProvider }
import securesocial.core.services.RoutesService
import templates.SecureSocialTemplates
import scala.collection.immutable.ListMap
import scala.concurrent.Future
import scala.concurrent.duration.Duration

object Global extends WithFilters(CSRFFilter()) with GlobalSettings {

  object TellerRuntimeEnvironment extends RuntimeEnvironment.Default[ActiveUser] {
    override lazy val routes: RoutesService = new TellerRoutesService()
    override lazy val viewTemplates: ViewTemplates = new SecureSocialTemplates(this)
    override lazy val userService: LoginIdentityService = new LoginIdentityService
    override lazy val providers = ListMap(
      include(new TwitterProvider(routes, cacheService, oauth1ClientFor(TwitterProvider.Twitter))),
      include(new FacebookProvider(routes, cacheService, oauth2ClientFor(FacebookProvider.Facebook))),
      include(new GoogleProvider(routes, cacheService, oauth2ClientFor(GoogleProvider.Google))),
      include(new LinkedInProvider(routes, cacheService, oauth1ClientFor(LinkedInProvider.LinkedIn)))
    )
  }

  /**
   * Dependency injection on Controllers using Cake Pattern
   *
   * @param controllerClass
   * @tparam A
   * @return
   */
  override def getControllerInstance[A](controllerClass: Class[A]): A = {
    val instance = controllerClass.getConstructors.find { c ⇒
      val params = c.getParameterTypes
      params.length == 1 && params(0) == classOf[RuntimeEnvironment[ActiveUser]]
    }.map {
      _.asInstanceOf[Constructor[A]].newInstance(TellerRuntimeEnvironment)
    }
    instance.getOrElse(super.getControllerInstance(controllerClass))
  }

  override def onHandlerNotFound(request: RequestHeader): Future[Result] = {
    Future.successful(NotFound(
      views.html.notFoundPage(request.path)))
  }

  /**
   * Force using HTTPS on production
   * @param request Request object
   * @return
   */
  override def onRouteRequest(request: RequestHeader): Option[Handler] = {
    if (Play.isProd && Play.configuration.getBoolean("stage").isEmpty
      && !request.headers.get("x-forwarded-proto").getOrElse("").contains("https")) {
      Some(controllers.HttpsController.redirect)
    } else {
      super.onRouteRequest(request)
    }
  }

  /**
   * Retrieve the (RequestHeader,Handler) to use to serve this request.
   * Default is: route, tag request, then apply filters
   *
   * This function was overided to support POST request through API
   */
  override def onRequestReceived(request: RequestHeader): (RequestHeader, Handler) = {
    val (routedRequest, handler) = onRouteRequest(request) map {
      case handler: RequestTaggingHandler ⇒ (handler.tagRequest(request), handler)
      case otherHandler ⇒ (request, otherHandler)
    } getOrElse {
      (request, Action.async(BodyParsers.parse.empty)(_ ⇒ this.onHandlerNotFound(request)))
    }

    val api = """/api/v""".r findPrefixOf request.path
    if (api.isEmpty) {
      (routedRequest, doFilter(rh ⇒ handler)(routedRequest))
    } else {
      (routedRequest, handler)
    }
  }

  override def onStart(app: Application) {
    // this is a dirty hack as I don't want to pay Heroku additional $30 for only
    // sending notifications through  a separate process
    if (sys.env.contains("DYNO") && sys.env("DYNO").equals("web.1")) {
      scheduleEventConfirmationAlert
      scheduleProfileImprovementAlert
    }
  }

  /**
   * Sends event confirmation alert in the beginning of each day
   */
  private def scheduleEventConfirmationAlert = {
    val now = LocalDateTime.now()
    val targetDate = LocalDate.now.plusDays(1)
    val targetTime = targetDate.toLocalDateTime(new LocalTime(0, 0))
    val waitPeriod = Seconds.secondsBetween(now, targetTime).getSeconds * 1000
    Akka.system.scheduler.schedule(
      Duration.create(waitPeriod, TimeUnit.MILLISECONDS),
      Duration.create(24, TimeUnit.HOURS)) {
        EventReminder.sendConfirmation()
      }
  }

  /**
   * Sends profile improvement alert in the beginning of each month
   */
  private def scheduleProfileImprovementAlert = {
    val now = LocalDateTime.now
    val targetDate = LocalDate.now.withDayOfMonth(1).plusMonths(1)
    val targetTime = targetDate.toLocalDateTime(new LocalTime(0, 0))
    val waitPeriod = Seconds.secondsBetween(now, targetTime).getSeconds * 1000
    Akka.system.scheduler.schedule(
      Duration.create(waitPeriod, TimeUnit.MILLISECONDS),
      Duration.create(30, TimeUnit.DAYS)) {
        ProfileStrengthReminder.sendToFacilitators()
      }
  }
}
