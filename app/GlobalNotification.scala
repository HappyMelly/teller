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

import java.util.concurrent.TimeUnit
import models.Event
import org.joda.time.{ LocalDateTime, LocalDate, LocalTime, Seconds }
import play.api._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc._
import play.api.mvc.Results._
import play.api.Play.current
import play.filters.csrf._
import play.libs.Akka
import scala.concurrent.duration.Duration
import scala.concurrent.Future

object GlobalNotification extends WithFilters(CSRFFilter()) with GlobalSettings {

  override def onHandlerNotFound(request: RequestHeader): Future[SimpleResult] = {
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

    val api = """/api/v1""".r findPrefixOf request.path
    if (api.isEmpty) {
      (routedRequest, doFilter(rh ⇒ handler)(routedRequest))
    } else {
      (routedRequest, handler)
    }
  }

  override def onStart(app: Application) {
    val now = LocalDateTime.now()
    val targetDate = LocalDate.now.plusDays(1)
    val targetTime = targetDate.toLocalDateTime(new LocalTime(0, 0))
    val waitPeriod = Seconds.secondsBetween(now, targetTime).getSeconds * 1000
    Akka.system.scheduler.schedule(Duration.create(waitPeriod, TimeUnit.MILLISECONDS), Duration.create(24, TimeUnit.HOURS)) {
      Event.sendConfirmationAlert()
    }
  }

}
