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

import org.joda.time.LocalDate
import play.api.mvc._
import models.UserRole.Role._
import models._

trait Dashboard extends Controller with Security with Services {

  /**
   * About page - credits.
   */
  def about = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒
      Ok(views.html.about(request.user.asInstanceOf[LoginIdentity]))
  }

  /**
   * API documentation page.
   */
  def api = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒
      Ok(views.html.api.index(request.user.asInstanceOf[LoginIdentity]))
  }

  /**
   * Dashboard page - logged-in home page.
   */
  def index = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒
      val account = request.user.asInstanceOf[LoginIdentity].userAccount
      val activity = if (account.editor)
        Some(Activity.findAll)
      else
        None
      val events = eventService.findByFacilitator(
        account.personId,
        brand = None)
      val upcomingEvents = events.
        filter(_.schedule.end.toString >= LocalDate.now().toString).
        slice(0, 3)
      val pastEvents = events.
        filter(_.schedule.end.toString < LocalDate.now().toString)
      val evaluations = Evaluation.
        findByEvents(pastEvents.map(_.id.get)).
        sortBy(_._3.created.toString())(Ordering[String].reverse).
        slice(0, 10)
      Ok(views.html.dashboard(request.user,
        upcomingEvents,
        evaluations,
        activity))
  }

  /**
   * Redirect to the current user’s `Person` details page. This is implemented as a redirect to avoid executing
   * the `LoginIdentity.person` database query for every page, to get the person ID for the details page URL.
   */
  def profile = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒
      val currentUser = request.user.asInstanceOf[LoginIdentity].person
      Redirect(routes.People.details(currentUser.id.getOrElse(0)))
  }

}

object Dashboard extends Dashboard with Security with Services

