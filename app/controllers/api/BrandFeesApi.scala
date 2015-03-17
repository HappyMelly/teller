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

package controllers.api

import models.brand.BrandFee
import models.service.Services
import play.api.mvc.Controller
import play.api.libs.json._
import views.Countries

/**
 * Provides API for working with event fees
 */
trait BrandFeesApi extends Controller with ApiAuthentication with Services {

  /**
   * EventFee to JSON converter
   */
  implicit val feeWrites = new Writes[BrandFee] {
    def writes(fee: BrandFee): JsValue = {
      Json.obj(
        "id" -> fee.id.get,
        "brand" -> fee.brand,
        "country" -> fee.country,
        "fee" -> fee.fee.toString)
    }
  }

  /**
   * Returns list of fees for the given brand in JSON format
   * @param brand Brand code
   */
  def fees(brand: String) = TokenSecuredAction { implicit request ⇒
    val fees = feeService.findByBrand(brand)
    Ok(Json.prettyPrint(Json.toJson(fees)))
  }
}

object BrandFeesApi extends BrandFeesApi with ApiAuthentication with Services
