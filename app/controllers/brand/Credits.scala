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

import javax.inject.{Inject, Named}

import akka.actor.ActorRef
import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import controllers.{Security, Utilities}
import models.UserRole.Role._
import models.cm.Facilitator
import models.cm.brand.PeerCredit
import models.core.notification.CreditReceived
import models.repository.Repositories
import models.{ActiveUser, Person}
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.MessagesApi
import services.TellerRuntimeEnvironment

import scala.concurrent.Future

/**
  * Contains a set of methods for managing peer credits
  */
class Credits @Inject() (override implicit val env: TellerRuntimeEnvironment,
                          override val messagesApi: MessagesApi,
                          val repos: Repositories,
                         @Named("notification") notificationDispatcher: ActorRef,
                         @Named("peer-credits") creditsConfigurator: ActorRef,
                          deadbolt: DeadboltActions, handlers: HandlerCache, actionBuilder: ActionBuilders)
  extends Security(deadbolt, handlers, actionBuilder, repos)(messagesApi, env) {

  case class FormData(receiverId: Long, amount: Int, reason: String)

  /**
    * Activates credits for the given brand
    *
    * @param brandId Brand identifier
    */
  def activate(brandId: Long) = BrandAction(brandId) { implicit request => implicit handler => implicit user =>
    repos.cm.brand.findWithSettings(brandId) flatMap {
      case None => jsonNotFound("Brand not found")
      case Some(view) =>
        if (view.settings.credits)
          jsonSuccess("Credits were activated")
        else
          repos.cm.brand.updateSettings(view.settings.copy(credits = true)) flatMap { _ =>
            creditsConfigurator ! (brandId, true)
            env.authenticatorService.fromRequest.map(auth ⇒ auth.foreach {
              _.updateUser(user.copy(account = user.account.copy(credits = Some(0))))
            })
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
    repos.cm.brand.findWithSettings(brandId) flatMap {
      case None => jsonNotFound("Brand not found")
      case Some(view) =>
        if (!view.settings.credits)
          jsonSuccess("Credits were deactivated")
        else
          repos.cm.brand.updateSettings(view.settings.copy(credits = false)) flatMap { _ =>
            creditsConfigurator ! (brandId, false)
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
        c <- repos.cm.brand.isCoordinator(brandId, user.person.identifier)
        f <- repos.cm.license.activeLicense(brandId, user.person.identifier)
        pc <- repos.cm.rep.brand.peerCredit.find(brandId)
      } yield (c, f.isDefined, pc.take(limit))
      result flatMap {
        case (false, false, _) => ok(msg)
        case (true, false, credits) => creditFeed(credits) flatMap { feed =>
          ok(views.html.v2.credit.forCoordinator(user, feed))
        }
        case (_, true, credits) => renderFacilitatorFeed(user, brandId, credits)
      }
    }
  }

  /**
    * Handles peer credit distribution for the given brand
    *
    * @param brandId Brand identifier
    */
  def give(brandId: Long) = RestrictedAction(Viewer) { implicit request => implicit handler => implicit user =>
    val form = Form(mapping(
      "to" -> longNumber(min = 1).
        verifying("You cannot give credits to yourself", value => value != user.person.identifier),
      "amount" -> number(min = 1),
      "reason" -> nonEmptyText)(FormData.apply)(FormData.unapply))

    form.bindFromRequest.fold(
      errors => jsonFormError(Utilities.errorsToJson(errors)),
      data => {
        val result = for {
          v <- repos.cm.brand.findWithSettings(brandId) if v.nonEmpty
          g <- repos.cm.facilitator.find(brandId, user.person.identifier) if g.nonEmpty
          r <- repos.cm.facilitator.find(brandId, data.receiverId) if r.nonEmpty
        } yield (v.get.settings, g.get, r.get)
        result flatMap { case (settings, giver, receiver) =>
          val creditsLeft = settings.creditLimit - giver.creditsGiven
          if (data.amount > creditsLeft) {
            val msg = "You try to give more credits when you have"
            jsonFormError(Utilities.errorsToJson(form.withError("amount", msg)))
          } else {
            giveCredit(brandId, giver, receiver, data, user.person.fullName)
          }
        } fallbackTo {
          jsonNotFound("Either brand or giver or receiver is not found")
        }
      }
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
        c <- repos.cm.brand.findByCoordinator(user.person.identifier)
        f <- repos.cm.brand.findByLicense(user.person.identifier, onlyActive = true)
        a <- repos.userAccount.update(user.account.copy(credits = user.account.credits.map(_ => 0)))
      } yield ((c ++ f).distinct, a)
      result flatMap { case (brands, account) =>
        val updatedUser = user.copy(account = account)
        env.authenticatorService.fromRequest.map(auth ⇒ auth.foreach {
          _.updateUser(updatedUser)
        }) flatMap { _ =>
          val filteredBrands = brands.filter(_.settings.credits).map(_.brand)
          if (filteredBrands.isEmpty) {
            val msg = "None of your brands has peer credit support activated"
            redirect(controllers.core.routes.Dashboard.index(), "error" -> msg)
          } else {
            ok(views.html.v2.credit.index(updatedUser, filteredBrands))
          }
        }
      }
    }
  }

  /**
    * Renders credit settings tab for the given brand
    *
    * @param brandId Brand identifier
    */
  def settings(brandId: Long) = BrandAction(brandId) { implicit request => implicit handler => implicit user =>
    repos.cm.brand.findWithSettings(brandId) flatMap {
      case None => notFound("Brand not found")
      case Some(view) =>
        ok(views.html.v2.brand.tabs.credits(brandId, view.settings.credits, view.settings.creditLimit))
    }
  }

  /**
    * Updates monthly limits for the given brand
    *
    * @param brandId Brand identifier
    */
  def update(brandId: Long) = BrandAction(brandId) { implicit request => implicit handler => implicit user =>
    repos.cm.brand.findWithSettings(brandId) flatMap {
      case None => jsonNotFound("Brand not found")
      case Some(view) =>
        val form = Form(single("limit" -> number(min = 1)))
        form.bindFromRequest().fold(
          errors => jsonFormError(Utilities.errorsToJson(errors)),
          limit => repos.cm.brand.updateSettings(view.settings.copy(creditLimit = limit)) flatMap { _ =>
            jsonSuccess("Monthly limits were updated")
          }
        )
    }
  }

  /**
    * Returns data required for rendering credit feed
    *
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

  protected def giveCredit(brandId: Long, giver: Facilitator, receiver: Facilitator, data: FormData, giverName: String) = {
    val credit = PeerCredit(None, brandId, receiver.personId, giver.personId, data.amount, data.reason)
    (for {
      _ <- repos.cm.facilitator.update(giver.copy(creditsGiven = giver.creditsGiven + data.amount))
      _ <- repos.cm.facilitator.update(receiver.copy(creditsReceived = receiver.creditsReceived + data.amount))
      _ <- repos.cm.rep.brand.peerCredit.insert(credit)
      r <- repos.person.get(receiver.personId)
    } yield r) flatMap { person =>
      notificationDispatcher ! CreditReceived(credit, giverName, person)
      ok("Credit was saved")
    }
  }

  protected def renderFacilitatorFeed(user: ActiveUser, brandId: Long, credits: Seq[PeerCredit]) = {
    (for {
      brand <- repos.cm.brand.findWithSettings(brandId) if brand.nonEmpty
      feed <- creditFeed(credits)
      facilitator <- repos.cm.facilitator.find(brandId, user.person.identifier)
    } yield (brand.get, feed, facilitator)) flatMap {
      case (_, _ , None) => ok("Internal error. Please contact the system support")
      case (view, feed, Some(facilitator)) =>
        val creditsLeft = view.settings.creditLimit - facilitator.creditsGiven
        ok(views.html.v2.credit.forFacilitator(user, facilitator, creditsLeft, feed))
    }
  }
}
