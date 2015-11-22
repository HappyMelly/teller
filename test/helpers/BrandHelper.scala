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
package helpers

import models.{ SocialProfile, Brand, ProfileType }
import org.joda.time.DateTime

object BrandHelper {

  def make(code: String, id: Option[Long] = None): Brand = {
    val brandUniqueName = code.toLowerCase + ".brand"
    val socialProfile = new SocialProfile(0, ProfileType.Brand, "test@happymelly.com")

    var brand = new Brand(id, code, brandUniqueName,
      code + " Brand", ownerId = 1, None, None, generateCert = false, None,
      None, None, None, true, DateTime.now(), "Sergey Kotlov",
      DateTime.now(), "Sergey Kotlov")
    brand.socialProfile_=(socialProfile)
    brand
  }

  def one: Brand = {
    val brandCode = "TEST"
    val brandUniqueName = "test.brand"
    val socialProfile = new SocialProfile(0, ProfileType.Brand, "test@happymelly.com")

    var brand = new Brand(Some(1L), brandCode, brandUniqueName,
      "Test Brand", ownerId = 1, None, None, generateCert = false, None,
      None, None, None, true, DateTime.now(), "Sergey Kotlov",
      DateTime.now(), "Sergey Kotlov")
    brand.socialProfile_=(socialProfile)

    brand
  }

  def two: Brand = {
    val brandCode = "TEST2"
    val brandUniqueName = "test2.brand"
    val socialProfile = new SocialProfile(0, ProfileType.Brand, "test@happymelly.com")

    var brand = new Brand(Some(2L), brandCode, brandUniqueName,
      "Test Brand", ownerId = 1, None, None, generateCert = false, None,
      None, None, None, true, DateTime.now(), "Sergey Kotlov",
      DateTime.now(), "Sergey Kotlov")
    brand.socialProfile_=(socialProfile)

    brand
  }
}
