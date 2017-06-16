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
import models.core.Coupon
import models.core.payment.{RequestException, GatewayWrapper}
import models.repository.Repositories
import org.joda.time.LocalDate
import play.api.Configuration
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.MessagesApi
import play.api.libs.json.Json
import services.TellerRuntimeEnvironment

/**
  * Manages all coupon-related operations
  */
class Coupons @Inject() (override implicit val env: TellerRuntimeEnvironment,
                         override val messagesApi: MessagesApi,
                         val repos: Repositories,
                         configuration: Configuration,
                         deadbolt: DeadboltActions, handlers: HandlerCache, actionBuilder: ActionBuilders)
  extends Security(deadbolt, handlers, actionBuilder, repos)(messagesApi, env) {

  val indexPath: String = controllers.core.routes.Coupons.index().url

  /** Renders coupon add form */
  def add() = RestrictedAction(Role.Admin) { implicit request => implicit handler => implicit user =>
    ok(views.html.v2.core.coupon.form(user, Coupons.form))
  }

  /** Handles coupon creation */
  def create() = RestrictedAction(Role.Admin) { implicit request => implicit handler => implicit user =>
    val withData = Coupons.form.bindFromRequest()
    withData.fold(
      errors => badRequest(views.html.v2.core.coupon.form(user, errors)),
      data => {
        repos.core.coupon.all flatMap { coupons =>
          if (coupons.exists(_.code == data.code)) {
            val withError = withData.withError("code", "Code is taken. Use another one")
            badRequest(views.html.v2.core.coupon.form(user, withError))
          } else {
            try {
              gateway().createCoupon(data)
              repos.core.coupon.insert(data) flatMap { _ =>
                redirect(indexPath, "success" -> "Coupon was successfully added")
              }
            } catch {
              case e: RequestException =>
                val withError = withData.withGlobalError(e.log.get)
                badRequest(views.html.v2.core.coupon.form(user, withError))
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
    try {
      gateway().deleteCoupon(code)
      repos.core.coupon.delete(code) flatMap { _ =>
        redirect(indexPath, "success" -> "Coupon was successfully deleted")
      }
    } catch {
      case e: RequestException =>
        redirect(indexPath, "error" -> "e.log.get")
    }
  }

  def get(code: String) = RestrictedAction(List(Role.Unregistered, Role.Viewer)) { implicit request => implicit handler => implicit user =>
    repos.core.coupon.find(code) flatMap {
      case None => jsonBadRequest("Invalid coupon")
      case Some(coupon) =>
        if (coupon.valid)
          jsonSuccess(s"${coupon.discount}% discount", Some(Json.obj("discount" -> coupon.discount)))
        else
          jsonBadRequest("Invalid coupon")
    }
  }

  /** Renders list of coupons */
  def index() = RestrictedAction(Role.Admin) { implicit request => implicit handler => implicit user =>
    repos.core.coupon.all flatMap { coupons =>
      ok(views.html.v2.core.coupon.index(user, coupons))
    }
  }

  protected def gateway(): GatewayWrapper = {
    val key = configuration.getString("stripe.secret_key").get
    new GatewayWrapper(key)
  }
}

object Coupons {

  val form = Form(mapping(
    "id" -> ignored(None.asInstanceOf[Option[Long]]),
    "code" -> nonEmptyText,
    "discount" -> number(min = 0, max = 100),
    "start" -> optional(jodaLocalDate),
    "end" -> optional(jodaLocalDate)
  )(Coupon.apply)(Coupon.unapply).verifying("Start date is after end date",
    coupon => checkDates(coupon.start, coupon.end)))

  protected def checkDates(start: Option[LocalDate], end: Option[LocalDate]): Boolean = (start, end) match {
    case (Some(startDate), Some(endDate)) => startDate.toString <= endDate.toString
    case (_, _) => true
  }
}
