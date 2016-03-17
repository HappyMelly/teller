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
 * If you have questions concerning this license or the applicable additional terms,
 * you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */
package controllers.brand

import javax.inject.Inject

import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import be.objectify.deadbolt.scala.cache.HandlerCache
import controllers.{Utilities, Security}
import models.Person
import models.brand.PeerCredit
import models.repository.Repositories
import models.UserRole.Role._
import play.api.i18n.MessagesApi
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import services.TellerRuntimeEnvironment

import scala.concurrent.Future

/**
  * Contains a set of methods for managing peer credits
  */
class Credits @Inject() (override implicit val env: TellerRuntimeEnvironment,
                          override val messagesApi: MessagesApi,
                          val repos: Repositories,
                          deadbolt: DeadboltActions, handlers: HandlerCache, actionBuilder: ActionBuilders)
  extends Security(deadbolt, handlers, actionBuilder, repos)(messagesApi, env) {

  /**
    * Activates credits for the given brand
    *
    * @param brandId Brand identifier
    */
  def activate(brandId: Long) = BrandAction(brandId) { implicit request => implicit handler => implicit user =>
    repos.brand.findWithSettings(brandId) flatMap {
      case None => jsonNotFound("Brand not found")
      case Some(view) =>
        if (view.settings.credits)
          jsonSuccess("Credits were activated")
        else
          repos.brand.updateSettings(view.settings.copy(credits = true)) flatMap { _ =>
            jsonSuccess("Credits were activated")
          }
    }
  }

  /**
    * Deactivates credits for the given brand
    *
    * @param brandId Brand identifier
    */
  def deactivate(brandId: Long) = BrandAction(brandId) { implicit request => implicit handler => implicit user =>
    repos.brand.findWithSettings(brandId) flatMap {
      case None => jsonNotFound("Brand not found")
      case Some(view) =>
        if (!view.settings.credits)
          jsonSuccess("Credits were deactivated")
        else
          repos.brand.updateSettings(view.settings.copy(credits = false)) flatMap { _ =>
            jsonSuccess("Credits were deactivated")
          }
    }
  }

  /**
    * Renders screen with peer credits for the given brand
    *
    * @param brandId Brand identifier
    */
  def credits(brandId: Long) = RestrictedAction(Viewer) { implicit request => implicit handler => implicit user =>
    val msg = "This page is accessible for brand coordinator and facilitators only"
    if (!user.account.facilitator && !user.account.coordinator) {
      ok(msg)
    } else {
      val limit = 50
      val result = for {
        c <- repos.brand.isCoordinator(brandId, user.person.identifier)
        f <- repos.license.activeLicense(brandId, user.person.identifier)
        pc <- repos.peerCredit.find(brandId)
      } yield (c, f.isDefined, pc.take(limit))
      result flatMap {
        case (false, false, _) => ok(msg)
        case (true, false, credits) => creditFeed(credits) flatMap { feed =>
          ok(views.html.v2.peerCredit.forCoordinator(user, feed))
        }
        case (_, true, credits) =>
          (for {
            feed <- creditFeed(credits)
            facilitator <- repos.facilitator.find(brandId, user.person.identifier)
          } yield (feed, facilitator)) flatMap {
            case (_ , None) => ok("Internal error. Please contact the system support")
            case (feed, Some(facilitator)) =>
              ok(views.html.v2.peerCredit.forFacilitator(user, facilitator, feed))
          }
      }
    }
  }

  /**
    * Handles peer credit distribution for the given brand
    * @param brandId Brand identifier
    */
  def give(brandId: Long) = RestrictedAction(Viewer) { implicit request => implicit handler => implicit user =>
    case class FormData(amount: Int, reason: String)
    val form = Form(mapping(
      "amount" -> number(min = 1),
      "reason" -> nonEmptyText)(FormData.apply)(FormData.unapply))
    form.bindFromRequest.fold(
      errors => badRequest(Json.obj("data" -> Utilities.errorsToJson(errors))),
      data => jsonSuccess("")
    )
  }

  /**
    * Renders main screen with peer credits
    */
  def index() = RestrictedAction(Viewer) { implicit request => implicit handler => implicit user =>
    if (!user.account.facilitator && !user.account.coordinator) {
      val msg = "This page is accessible for brand coordinator and facilitators only"
      redirect(controllers.core.routes.Dashboard.index(), "info" -> msg)
    } else {
      val result = for {
        c <- repos.brand.findByCoordinator(user.person.identifier)
        f <- repos.brand.findByLicense(user.person.identifier, onlyActive = true)
      } yield (c ++ f).distinct
      result flatMap { brands =>
        val filteredBrands = brands.filter(_.settings.credits).map(_.brand)
        ok(views.html.v2.peerCredit.index(user, filteredBrands))
      }
    }
  }

  /**
    * Renders credit settings tab for the given brand
    *
    * @param brandId Brand identifier
    */
  def settings(brandId: Long) = BrandAction(brandId) { implicit request => implicit handler => implicit user =>
    repos.brand.findWithSettings(brandId) flatMap {
      case None => notFound("Brand not found")
      case Some(view) =>
        ok(views.html.v2.brand.tabs.credits(view.settings.credits, view.settings.creditLimit))
    }
  }

  /**
    * Updates monthly limits for the given brand
    *
    * @param brandId Brand identifier
    */
  def update(brandId: Long) = BrandAction(brandId) { implicit request => implicit handler => implicit user =>
    repos.brand.findWithSettings(brandId) flatMap {
      case None => jsonNotFound("Brand not found")
      case Some(view) =>
        val form = Form(single("limit" -> number(min = 1)))
        form.bindFromRequest().fold(
          errors => badRequest(Json.obj("data" -> Utilities.errorsToJson(errors))),
          limit => repos.brand.updateSettings(view.settings.copy(creditLimit = limit)) flatMap { _ =>
            jsonSuccess("Monthly limits were updated")
          }
        )
    }
  }

  /**
    * Returns data required for rendering credit feed
    * @param credits List of peer credits
    */
  protected def creditFeed(credits: Seq[PeerCredit]): Future[Seq[(PeerCredit, Person, Person)]] = {
    val ids = (credits.map(_.giverId) ++ credits.map(_.receiverId)).distinct
    repos.person.find(ids.toList) map { people =>
      val feed = credits.map { credit =>
        val giver = people.find(_.identifier == credit.giverId)
        val receiver = people.find(_.identifier == credit.receiverId)
        (credit, giver, receiver)
      }
      val filteredFeed = feed.filter(credit => credit._2.nonEmpty && credit._3.nonEmpty)
      filteredFeed.map(credit => (credit._1, credit._2.get, credit._3.get))
    }
  }
}
