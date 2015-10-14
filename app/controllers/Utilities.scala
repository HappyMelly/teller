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
 * If you have questions concerning this license or the applicable additional terms, you may contact
 * by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */
package controllers

import models.{Brand, UserAccount}
import models.service.Services
import play.api.Play
import play.api.Play.current
import play.api.mvc._

trait Utilities extends Controller with Services {

  /**
   * Returns an url with domain
   * @param url Domain-less part of url
   */
  protected def fullUrl(url: String): String = {
    Play.configuration.getString("application.baseUrl").getOrElse("") + url
  }
  
  protected def roleDiffirentiator(account: UserAccount, brandId: Option[Long] = None)
                                  (coordinator: (Brand, List[Brand]) => Result)
                                  (facilitator: (Brand, List[Brand]) => Result)
                                  (ordinaryUser: Result): Result = {
    if (account.isCoordinatorNow) {
      val brands = brandService.findByCoordinator(account.personId).sortBy(_.name)
      brandId map { identifier =>
        brands.find(_.identifier == identifier) map { brand =>
          coordinator(brand, brands)
        } getOrElse Redirect(routes.Dashboard.index())    
      } getOrElse {
        if (brands.nonEmpty) {
          coordinator(brands.head, brands)
        } else Redirect(routes.Dashboard.index())
      }
    } else if (account.isFacilitatorNow) {
      val licenses = licenseService.activeLicenses(account.personId)
      val brands = licenses.map(_.brand)
      brandId map { identifier =>
        brands.find(_.identifier == identifier) map { brand =>
          facilitator(brand, brands)
        } getOrElse Redirect(routes.Dashboard.index())
      } getOrElse {
        if (brands.nonEmpty) {
          facilitator(brands.head, brands)
        } else Redirect(routes.Dashboard.index())
      }
    } else ordinaryUser
  }
}