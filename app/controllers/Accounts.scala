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

import javax.inject.Inject

import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import models.UserRole.Role._
import models._
import org.joda.money.{CurrencyUnit, Money}
import org.joda.time.LocalDate
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import services.CurrencyConverter.NoExchangeRateException
import services.TellerRuntimeEnvironment

import scala.concurrent.Future

class Accounts @Inject() (override implicit val env: TellerRuntimeEnvironment,
                          override val messagesApi: MessagesApi,
                          deadbolt: DeadboltActions, handlers: HandlerCache, actionBuilder: ActionBuilders)
  extends Security(deadbolt, handlers, actionBuilder)(messagesApi, env)
  with Activities
  with I18nSupport {

  val currencyForm = Form(mapping("currency" -> text(3, 3))(CurrencyUnit.of)(t ⇒ Some(t.toString)))

  /**
   * Balance accounts - every now and then all accounts in the system will have to be balanced, because of the currency
   * exchange rate fluctuations. This creates booking entries, and redirect to the accounts page. The user-interface
   * should not execute this action (the button is disabled) if accounts do not require balancing.
   */
  def balanceAccounts = AsyncSecuredRestrictedAction(Admin) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    val currentUser = user.account
    Account.balanceAccounts(currentUser.personId).map { bookingEntries ⇒
      val activity = new Activity(None,
        user.person.id.get,
        user.person.fullName,
        Activity.Predicate.BalancedAccounts,
        "",
        0L,
        Some(bookingEntries.size.toString))
      activity.insert
      Redirect(routes.Accounts.index()).flashing(
        "success" -> activity.toString)
    }
  }

  /**
   * An overview of bookings for the given account.
   */
  def bookings(id: Long, from: Option[LocalDate], to: Option[LocalDate]) = AsyncSecuredRestrictedAction(Admin) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒
      (for {
        e <- bookingEntryService.find(id, from, to)
        a <- accountService.get(id)
      } yield (e, a)) flatMap { case (entries, account) =>
        var balance = Money.zero(account.balance.getCurrencyUnit)
        val entriesWithBalance = entries.reverse.map { e ⇒
          if (e.fromId == id) {
            balance = balance.plus(e.fromAmount)
            (e, Some(balance))
          } else if (e.toId == id) {
            balance = balance.minus(e.toAmount)
            (e, Some(balance))
          } else {
            (e, None)
          }
        }.reverse
        ok(views.html.booking.index(user, Some(account), entriesWithBalance, from, to))
      }
  }

  def details(id: Long) = AsyncSecuredRestrictedAction(Admin) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    accountService.find(id) flatMap {
      case None => notFound("Account not found")
      case Some(account) =>
        ok(views.html.account.details(user, account, currencyForm))
    }
  }

  def activate(id: Long) = AsyncSecuredRestrictedAction(Admin) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    accountService.find(id) flatMap {
      case None => notFound("Account not found")
      case Some(account) =>
        if (account.editableBy(user.account)) {
          currencyForm.bindFromRequest().fold(
            form ⇒ badRequest(views.html.account.details(user, account, form)),
            currency ⇒ {
              account.activate(currency)
              val log = activity(account, user.person).activated.insert()
              account.accountHolder.updated(user.name)
              redirect(routes.Accounts.details(id), "success" -> log.toString)
            })
        } else {
          Future.successful(Unauthorized("You are not allowed to activate this account"))
        }
    }
  }

  def deactivate(id: Long) = AsyncSecuredRestrictedAction(Admin) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    accountService.find(id) flatMap {
      case None => notFound("Account not found")
      case Some(account) =>
        if (account.editableBy(user.account)) {
          account.deactivate()
          account.accountHolder.updated(user.name)
          val log = activity(account, user.person).deactivated.insert()
          redirect(routes.Accounts.details(id), "success" -> log.toString)
        } else {
          Future.successful(Unauthorized("You are not allowed to deactivate this account"))
        }
    }
  }

  def index = AsyncSecuredRestrictedAction(Admin) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    accountService.findAllActiveWithBalance flatMap { accounts =>
      ok(views.html.account.index(user, accounts))
    }
  }

  def previewBalance = AsyncSecuredRestrictedAction(Admin) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    (for {
      levy <- accountService.get(Levy)
      accounts <- accountService.findAllForAdjustment(levy.currency)
    } yield (levy, accounts)) flatMap { case (levy, accounts) =>
      val totalBalance = Account.calculateTotalBalance(levy.currency, accounts)
      val canBalanceAccounts = accounts.exists(!_.adjustment.isZero)
      ok(views.html.account.balance(user, totalBalance, accounts, canBalanceAccounts))
    } recover {
      case e: NoExchangeRateException ⇒ {
        Redirect(routes.Accounts.index()).flashing("error" -> Messages("error.retry", e.getMessage))
      }
    }
  }
}
