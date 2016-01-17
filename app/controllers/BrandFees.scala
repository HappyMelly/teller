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

import models.UserRole.Role._
import models.service.Services
import play.api.mvc._
import services.TellerRuntimeEnvironment
import views.Countries

class BrandFees @javax.inject.Inject() (override implicit val env: TellerRuntimeEnvironment)
    extends AsyncController
    with Services
    with Security {

  /**
   * Renders list of available fees for the given brand
   *
   * @param brandId Brand identifier
   */
  def index(brandId: Long) = AsyncSecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      (for {
        brand <- brandService.find(brandId)
        fees <- feeService.findByBrand(brandId)
      } yield (brand, fees)) flatMap {
        case (None, _) => notFound("Brand not found")
        case (Some(brand), fees) =>
          val printableFees = fees.map(x ⇒ (Countries.name(x.country), x.fee.toString)).sortBy(_._1)
          ok(views.html.fee.index(brand.name, printableFees))
      }
  }
}
