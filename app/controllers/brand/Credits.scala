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
import controllers.Security
import models.repository.Repositories
import models.UserRole.Role._
import play.api.i18n.MessagesApi
import services.TellerRuntimeEnvironment

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
    * @param brandId Brand identifier
    */
  def activate(brandId: Long) = BrandAction(brandId) { implicit request => implicit handler => implicit user =>
    jsonSuccess("Credits were activated")
  }

  /**
    * Deactivates credits for the given brand
    * @param brandId Brand identifier
    */
  def deactivate(brandId: Long) = BrandAction(brandId) { implicit request => implicit handler => implicit user =>
    jsonSuccess("Credits were deactivated")
  }

  /**
    * Renders main screen with peer credits
    */
  def index() = RestrictedAction(Viewer) { implicit request => implicit handler => implicit user =>
    ok(views.html.v2.person.credit(user))
  }

  /**
    * Renders credit settings tab for the given brand
    * @param brandId Brand identifier
    */
  def settings(brandId: Long) = BrandAction(brandId) { implicit request => implicit handler => implicit user =>
    ok(views.html.v2.brand.tabs.credits(true, 100))
  }

  /**
    * Updates montly limits for the given brand
    * @param brandId Brand identifier
    */
  def update(brandId: Long)= BrandAction(brandId) { implicit request => implicit handler => implicit user =>
    jsonSuccess("Monthly limits were updated")
  }
}
