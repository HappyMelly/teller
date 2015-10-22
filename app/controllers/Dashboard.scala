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

import models.service.Services
import org.joda.time.LocalDate
import play.api.i18n.Messages
import play.api.mvc._
import models.UserRole.Role._
import models._
import securesocial.core.RuntimeEnvironment

class Dashboard(environment: RuntimeEnvironment[ActiveUser])
    extends Controller
    with Security
    with Services
    with Utilities {

  override implicit val env: RuntimeEnvironment[ActiveUser] = environment
  /**
   * About page - credits.
   */
  def about = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      Ok(views.html.about(user))
  }

  /**
   * API documentation page.
   */
  def api = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      Ok(views.html.api.index(user))
  }

  /**
   * API v2 documentation page.
   */
  def apiv2 = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      Ok(views.html.apiv2.index(user))
  }

  /**
   * Dashboard page - logged-in home page.
   */
  def index = SecuredRestrictedAction(Unregistered) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      val account = user.account
      if (account.viewer) {
        roleDiffirentiator(user.account) { (brand, brands) =>
          val licenses = licenseService.expiring(List(brand.identifier))
          //          val events = eventService.
          //            findByParameters(Some(activeBrand.identifier), confirmed = Some(true), future = Some(false))
          //          val withInvoices = eventService.withInvoices(events).filter(_.invoice.invoiceBy.nonEmpty)
          val cancellations = eventCancellationService.findByBrands(List(brand.identifier))
          Ok(views.html.v2.dashboard.forBrandCoordinators(user, brand, brands, licenses, cancellations))
        } { (brand, brands) =>
          val events = eventService.findByFacilitator(
            account.personId,
            brandId = None)
          val upcomingEvents = events
            .filter(_.schedule.end.toString >= LocalDate.now().toString)
            .slice(0, 3)
          Ok(views.html.v2.dashboard.forFacilitators(user, brand, brands,
            upcomingEvents,
            pastEvents(events),
            unhandledEvaluations(events)))
        } { Ok(views.html.v2.dashboard.index(user)) }
      } else {
        Redirect(routes.LoginPage.logout(Some(Messages("login.unregistered"))))
      }
  }

  /**
   * Dashboard page for the specific brand
   * @param id Brand identifier
   */
  def overview(id: Long) = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      roleDiffirentiator(user.account, Some(id)) { (brand, brands) =>
        val licenses = licenseService.expiring(List(brand.identifier))
        //          val events = eventService.
        //            findByParameters(Some(activeBrand.identifier), confirmed = Some(true), future = Some(false))
        //          val withInvoices = eventService.withInvoices(events).filter(_.invoice.invoiceBy.nonEmpty)
        val cancellations = eventCancellationService.findByBrands(List(brand.identifier))
        Ok(views.html.v2.dashboard.forBrandCoordinators(user, brand, brands, licenses, cancellations))
      } { (brand, brands) =>
        val events = eventService.findByFacilitator(
          user.account.personId,
          brandId = Some(id))
        val upcomingEvents = events
          .filter(_.schedule.end.toString >= LocalDate.now().toString)
          .slice(0, 3)
        Ok(views.html.v2.dashboard.forFacilitators(user, brand, brands,
          upcomingEvents,
          pastEvents(events),
          unhandledEvaluations(events)))
      } { Ok(views.html.v2.dashboard.index(user)) }
  }

  /**
   * Redirect to the current user’s `Person` details page. This is implemented as a redirect to avoid executing
   * the `LoginIdentity.person` database query for every page, to get the person ID for the details page URL.
   */
  def profile = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      val currentUser = user.person
      Redirect(routes.People.details(currentUser.id.getOrElse(0)))
  }

  /**
   * Returns 3 past events happened in the last 7 days
   * @param events List of all events
   */
  protected def pastEvents(events: List[Event]): List[Event] = {
    val now = LocalDate.now()
    val weekBefore = now.minusDays(7)
    events
      .filter(_.schedule.end.isBefore(now))
      .filter(_.schedule.end.isAfter(weekBefore))
      .sortBy(_.schedule.end.toString)(Ordering[String].reverse).slice(0, 2)
  }

  /**
   * Returns unhandled evaluations for the given events
   * @param events List of events
   */
  protected def unhandledEvaluations(events: List[Event]) = {
    evaluationService
      .findUnhandled(events)
      .sortBy(_._3.recordInfo.created.toString())(Ordering[String].reverse)
  }

}

