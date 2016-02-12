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

import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import models.UserRole.Role._
import models._
import models.service.Services
import org.joda.time.LocalDate
import play.api.i18n.{I18nSupport, MessagesApi}
import services.TellerRuntimeEnvironment

import scala.concurrent.Future

class Dashboard @javax.inject.Inject() (override implicit val env: TellerRuntimeEnvironment,
                                        override val messagesApi: MessagesApi,
                                        val services: Services,
                                        deadbolt: DeadboltActions, handlers: HandlerCache, actionBuilder: ActionBuilders)
  extends Security(deadbolt, handlers, actionBuilder, services)(messagesApi, env)
  with BrandAware
  with I18nSupport {

  /**
   * About page - credits.
   */
  def about = AsyncSecuredRestrictedAction(Admin) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    ok(views.html.about(user))
  }

  /**
   * API v2 documentation page.
   */
  def apiv2 = AsyncSecuredRestrictedAction(Viewer) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    ok(views.html.apiv2.index(user))
  }

  /**
   * Dashboard page - logged-in home page.
   */
  def index = AsyncSecuredRestrictedAction(List(Viewer, Unregistered)) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      if (user.account.registered) {
        roleDiffirentiator(user.account) { (view, brands) =>
          (for {
            l <- services.licenseService.expiring(List(view.brand.identifier))
            e <- unbilledEvents(view.brand)
          } yield (l, e)) flatMap { case (licenses, events) =>
            ok(views.html.v2.dashboard.forBrandCoordinators(user, view.brand, brands, licenses, events))
          }
        } { (view, brands) =>
          (for {
            events <- services.eventService.findByFacilitator(user.account.personId, brandId = None)
            evals <- unhandledEvaluations(events, brands)
          } yield (events, evals)) flatMap { case (events, evaluations) =>
            ok(views.html.v2.dashboard.forFacilitators(user, view, brands,
              upcomingEvents(events, brands),
              pastEvents(events, brands),
              evaluations))
          }
        } {
          ok(views.html.v2.dashboard.forMembers(user))
        }
      } else {
        redirect(routes.LoginPage.logout(error = Some("You are not registered in the system")))
      }
  }

  /**
   * Dashboard page for the specific brand
    *
    * @param id Brand identifier
   */
  def overview(id: Long) = AsyncSecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      roleDiffirentiator(user.account, Some(id)) { (view, brands) =>
        (for {
          l <- services.licenseService.expiring(List(view.brand.identifier))
          e <- unbilledEvents(view.brand)
        } yield (l, e)) flatMap { case (licenses, events) =>
          ok(views.html.v2.dashboard.forBrandCoordinators(user, view.brand, brands,
            licenses, events))
        }
      } { (view, brands) =>
        (for {
          events <- services.eventService.findByFacilitator(user.account.personId, brandId = Some(id))
          evaluations <- unhandledEvaluations(events, brands)
        } yield (events, evaluations)) flatMap { case (events, evaluations) =>
          ok(views.html.v2.dashboard.forFacilitators(user, view, brands,
            upcomingEvents(events, brands),
            pastEvents(events, brands),
            evaluations))
        }
      } { ok(views.html.v2.dashboard.index(user)) }
  }

  /**
   * Redirect to the current user’s `Person` details page. This is implemented as a redirect to avoid executing
   * the `LoginIdentity.person` database query for every page, to get the person ID for the details page URL.
   */
  def profile = AsyncSecuredRestrictedAction(Viewer) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    val currentUser = user.person
    redirect(routes.People.details(currentUser.id.getOrElse(0)))
  }

  /**
   * Returns 3 past events happened in the last 7 days
    *
    * @param events List of all events
   */
  protected def pastEvents(events: List[Event], brands: List[Brand]) = {
    val now = LocalDate.now()
    val weekBefore = now.minusDays(7)
    events
      .filter(_.schedule.end.isBefore(now))
      .filter(_.schedule.end.isAfter(weekBefore))
      .sortBy(_.schedule.end.toString)(Ordering[String].reverse).slice(0, 2)
      .map(event => (event, brands.find(_.identifier == event.brandId)))
  }

  /**
   * Returns unhandled evaluations for the given events
    *
    * @param events List of events
   */
  protected def unhandledEvaluations(events: List[Event], brands: List[Brand]) = {
    services.evaluationService.findUnhandled(events) map { evaluations =>
      evaluations.sortBy(_._3.recordInfo.created.toString())(Ordering[String].reverse)
        .map(value => (value, brands.find(_.identifier == value._1.brandId)))

    }
  }

  /**
   * Returns list of upcoming events (not more than 3)
    *
    * @param events List of all events
   * @param brands List of related brands
   */
  protected def upcomingEvents(events: List[Event], brands: List[Brand]) = {
    events
      .filter(_.schedule.end.toString >= LocalDate.now().toString)
      .slice(0, 3).map(event => (event, brands.find(_.identifier == event.brandId)))
  }

  /**
   * Returns list of past confirmed events without invoices
    *
    * @param brand Brand of interest
   */
  protected def unbilledEvents(brand: Brand): Future[List[Event]] = {
    (for {
      e <- services.eventService.findByParameters(Some(brand.identifier), confirmed = Some(true), future = Some(false))
      i <- services.eventService.withInvoices(e)
    } yield i) map { withInvoices =>
      val TELLER_LAUNCHED_DATE = LocalDate.parse("2015-01-01")
      withInvoices.filter(_.invoice.invoiceBy.isEmpty).
        map(_.event).filterNot(_.free).
        filter(_.schedule.start.isAfter(TELLER_LAUNCHED_DATE)).
        sortBy(_.schedule.start.toString)

    }
  }
}

