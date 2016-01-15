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

import models.UserRole.Role._
import models._
import org.joda.money.{CurrencyUnit, Money}
import org.joda.time.LocalDate
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.Controller
import services.CurrencyConverter.NoExchangeRateException
import services.TellerRuntimeEnvironment

class Accounts @Inject() (override implicit val env: TellerRuntimeEnvironment)
    extends Controller
    with Security
    with Activities
    with I18nSupport {

  val currencyForm = Form(mapping("currency" -> text(3, 3))(CurrencyUnit.of)(t ⇒ Some(t.toString)))

  /**
   * Balance accounts - every now and then all accounts in the system will have to be balanced, because of the currency
   * exchange rate fluctuations. This creates booking entries, and redirect to the accounts page. The user-interface
   * should not execute this action (the button is disabled) if accounts do not require balancing.
   */
  def balanceAccounts = AsyncSecuredRestrictedAction(Admin) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
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
  def bookings(id: Long, from: Option[LocalDate], to: Option[LocalDate]) = SecuredRestrictedAction(Admin) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        val entries = BookingEntry.findByAccountId(id, from, to)
        val account = Account.find(id)
        var balance = Money.zero(account.get.balance.getCurrencyUnit)
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
        }.toList.reverse
        Ok(views.html.booking.index(user, account, entriesWithBalance, from, to))
  }

  def details(id: Long) = SecuredRestrictedAction(Admin) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        Account.find(id).map { account ⇒
          Ok(views.html.account.details(user, account, currencyForm))
        }.getOrElse(NotFound)
  }

  def activate(id: Long) = SecuredRestrictedAction(Admin) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        Account.find(id).map { account ⇒
          if (account.editableBy(user.account)) {
            currencyForm.bindFromRequest().fold(
              form ⇒ BadRequest(views.html.account.details(user, account, form)),
              currency ⇒ {
                account.activate(currency)
                val log = activity(account, user.person).activated.insert()
                account.accountHolder.updated(user.name)
                Redirect(routes.Accounts.details(id)).flashing(
                  "success" -> log.toString)
              })
          } else {
            Unauthorized("You are not allowed to activate this account")
          }
        }.getOrElse(NotFound)

  }

  def deactivate(id: Long) = SecuredRestrictedAction(Admin) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        Account.find(id).map(account ⇒
          if (account.editableBy(user.account)) {
            account.deactivate()
            account.accountHolder.updated(user.name)
            val log = activity(account, user.person).deactivated.insert()
            Redirect(routes.Accounts.details(id)).flashing(
              "success" -> log.toString)
          } else {
            Unauthorized("You are not allowed to deactivate this account")
          }).getOrElse(NotFound)
  }

  def index = SecuredRestrictedAction(Admin) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        Ok(views.html.account.index(user, Account.findAllActiveWithBalance))
  }

  def previewBalance = AsyncSecuredRestrictedAction(Admin) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        val levy = Account.find(Levy)
        Account.findAllForAdjustment(levy.currency).map { accounts ⇒
          val totalBalance = Account.calculateTotalBalance(levy.currency, accounts)
          val canBalanceAccounts = accounts.exists(!_.adjustment.isZero)
          Ok(views.html.account.balance(user, totalBalance, accounts, canBalanceAccounts))
        }.recover {
          case e: NoExchangeRateException ⇒ {
            Redirect(routes.Accounts.index()).flashing("error" -> Messages("error.retry", e.getMessage))
          }
        }
  }
}
