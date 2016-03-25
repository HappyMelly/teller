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

package controllers.cm.brand

import javax.inject.Inject

import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import controllers.Security
import models.cm.brand.ApiConfig
import models.repository.Repositories
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.MessagesApi
import services.TellerRuntimeEnvironment

/**
  * Contains methods for managing brand API settings
  */
class API @Inject() (override implicit val env: TellerRuntimeEnvironment,
                      override val messagesApi: MessagesApi,
                      val repos: Repositories,
                      deadbolt: DeadboltActions, handlers: HandlerCache, actionBuilder: ActionBuilders)
  extends Security(deadbolt, handlers, actionBuilder, repos)(messagesApi, env) {

  /**
    * Activates API for the given brand
    *
    * @param brandId Brand identifier
    */
  def activate(brandId: Long) = BrandAction(brandId) { implicit request => implicit handler => implicit user =>
    val msg = "API was activated"
    repos.cm.rep.brand.config.findByBrand(brandId) flatMap {
      case None => repos.cm.rep.brand.config.insert(ApiConfig(None, brandId, active = true)) flatMap {
        _ => jsonSuccess(msg)
      }
      case Some(config) =>
        if (config.active)
          jsonSuccess(msg)
        else
          repos.cm.rep.brand.config.activate(brandId, active = true) flatMap (_ => jsonSuccess(msg))
    }
  }

  /**
    * Deactivates API for the given brand
    *
    * @param brandId Brand identifier
    */
  def deactivate(brandId: Long) = BrandAction(brandId) { implicit request => implicit handler => implicit user =>
    val msg = "API was deactivated"
    repos.cm.rep.brand.config.findByBrand(brandId) flatMap {
      case None => repos.cm.rep.brand.config.insert(ApiConfig(None, brandId)) flatMap (_ => jsonSuccess(msg))
      case Some(config) =>
        if (!config.active)
          jsonSuccess(msg)
        else
          repos.cm.rep.brand.config.activate(brandId, active = false) flatMap (_ => jsonSuccess(msg))
    }
  }

  /**
    * Renders API settings page for the given brand
    *
    * @param brandId Brand identifier
    */
  def settings(brandId: Long) = BrandAction(brandId) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    repos.cm.rep.brand.config.findByBrand(brandId) flatMap { apiConfig =>
      val config = apiConfig.getOrElse(ApiConfig(None, brandId))
      ok(views.html.v2.brand.tabs.api(config))
    }
  }

  /**
    * Handles API config form input
    * @param brandId Brand identifier
    */
  def update(brandId: Long) = BrandAction(brandId) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    repos.cm.rep.brand.config.findByBrand(brandId) flatMap {
      case None => jsonNotFound("Brand not found")
      case Some(apiConfig) =>
        form.bindFromRequest.fold(
          errors => jsonFormError(errors.errorsAsJson),
          { case (event, facilitator, generalEval, specificEventEval) =>
            val config = apiConfig.copy(event = event,
              facilitator = facilitator,
              generalEvaluation = generalEval,
              specificEventEvaluation = specificEventEval)
            repos.cm.rep.brand.config.update(config) flatMap { _ =>
              jsonSuccess("API config was updated")
            }
          }
        )
    }
  }

  protected val form = Form(tuple(
    "event" -> optional(nonEmptyText),
    "facilitator" -> optional(nonEmptyText),
    "generalEvaluation" -> optional(nonEmptyText),
    "specificEventEvaluation" -> optional(nonEmptyText)
  ))
}
