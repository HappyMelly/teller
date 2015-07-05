/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2015, Happy Melly http://www.happymelly.com
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
package controllers

import models.ActiveUser
import models.UserRole.Role._
import models.service.Services
import play.api.Play.current
import play.api.mvc._
import securesocial.core.RuntimeEnvironment
import scala.concurrent.Future
import views.Countries

class BrandFees(environment: RuntimeEnvironment[ActiveUser])
    extends Controller
    with Services
    with Security {

  override implicit val env: RuntimeEnvironment[ActiveUser] = environment

  /**
   * Renders list of available fees for the given brand
   *
   * @param brandId Brand identifier
   */
  def index(brandId: Long) = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      brandService.find(brandId) map { brand ⇒
        val fees = feeService.findByBrand(brandId)
        val printableFees = fees.
          map(x ⇒ (Countries.name(x.country), x.fee.toString)).
          sortBy(_._1)
        Ok(views.html.fee.index(brand.name, printableFees))
      } getOrElse NotFound("Brand not found")
  }
}
