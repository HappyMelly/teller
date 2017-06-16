/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2017, Happy Melly http://www.happymelly.com
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
 * If you have questions concerning this license or the applicable additional
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */

package controllers.core

import javax.inject.Inject

import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import controllers.Security
import models.UserRole.Role
import models.core.TrialCoupon
import models.core.payment.GatewayWrapper
import models.repository.Repositories
import play.api.Configuration
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.MessagesApi
import play.api.mvc.Action
import services.TellerRuntimeEnvironment

/**
  * Manages all trial coupon-related operations
  */
class TrialCoupons @Inject() (override implicit val env: TellerRuntimeEnvironment,
                              override val messagesApi: MessagesApi,
                              val repos: Repositories,
                              configuration: Configuration,
                              deadbolt: DeadboltActions, handlers: HandlerCache, actionBuilder: ActionBuilders)
  extends Security(deadbolt, handlers, actionBuilder, repos)(messagesApi, env) {

  val indexPath: String = controllers.core.routes.TrialCoupons.index().url

  /** Renders trial coupon add form */
  def add() = RestrictedAction(Role.Admin) { implicit request => implicit handler => implicit user =>
    ok(views.html.v2.core.trialCoupon.form(user, TrialCoupons.form))
  }

  /** Handles trial coupon creation */
  def create() = RestrictedAction(Role.Admin) { implicit request => implicit handler => implicit user =>
    val withData = TrialCoupons.form.bindFromRequest()
    withData.fold(
      errors => badRequest(views.html.v2.core.trialCoupon.form(user, errors)),
      data => {
        repos.core.trialCoupon.all flatMap { coupons =>
          if (coupons.exists(_.code == data.code)) {
            val withError = withData.withError("code", "Code is taken. Use another one")
            badRequest(views.html.v2.core.trialCoupon.form(user, withError))
          } else {
            repos.core.trialCoupon.insert(data) flatMap { _ =>
              redirect(indexPath, "success" -> "Coupon was successfully added")
            }
          }
        }
      }
    )
  }

  /**
    * Deletes coupon by its code
    *
    * @param code Coupon code
    */
  def delete(code: String) = RestrictedAction(Role.Admin) { implicit request => implicit handler => implicit user =>
    repos.core.trialCoupon.delete(code) flatMap { _ =>
      redirect(indexPath, "success" -> "Coupon was successfully deleted")
    }
  }

  def get(code: String) = Action.async { implicit request =>
    repos.core.trialCoupon.find(code) flatMap {
      case None => jsonBadRequest("Invalid coupon")
      case Some(coupon) =>
        jsonSuccess("Coupon is valid")
    }
  }

  /** Renders list of coupons */
  def index() = RestrictedAction(Role.Admin) { implicit request => implicit handler => implicit user =>
    repos.core.trialCoupon.all flatMap { coupons =>
      ok(views.html.v2.core.trialCoupon.index(user, coupons))
    }
  }

  protected def gateway(): GatewayWrapper = {
    val key = configuration.getString("stripe.secret_key").get
    new GatewayWrapper(key)
  }
}

object TrialCoupons {

  val form = Form(mapping(
    "id" -> ignored(None.asInstanceOf[Option[Long]]),
    "code" -> nonEmptyText,
    "owner" → nonEmptyText,
    "email" → email
  )(TrialCoupon.apply)(TrialCoupon.unapply))

}

