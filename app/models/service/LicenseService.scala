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
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com
 * or in writing
 * Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */
package models.service

import models.LicenseView
import models.database.Licenses
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.Play.current

class LicenseService {

  /**
   * Returns a list of content licenses for the given person
   * @param personId Person identifier
   */
  def licenses(personId: Long): List[LicenseView] = DB.withSession { implicit session: Session ⇒

    val query = for {
      license ← Licenses if license.licenseeId === personId
      brand ← license.brand
    } yield (license, brand)

    query.sortBy(_._2.name.toLowerCase).list.map {
      case (license, brand) ⇒ LicenseView(brand, license)
    }
  }
}

object LicenseService {
  private val instance = new LicenseService

  def get: LicenseService = instance
}