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

package models.integration

import helpers.{ PersonHelper, BrandHelper }
import integration.{ TruncateBefore, PlayAppSpec }
import models.service.BrandService

/**
 * Tests for BrandService class
 */
class BrandServiceSpec extends PlayAppSpec {

  val service = new BrandService

  "Method find(_: String)" should {
    "return a brand with code TEST" in new TruncateBefore {
      val person = PersonHelper.one().insert
      val brand = BrandHelper.one.insert
      service.find("TEST") map { x ⇒
        x.code must_== "TEST"
        x.coordinatorId must_== brand.coordinatorId
        x.uniqueName must_== brand.uniqueName
      } getOrElse ko
    }
    "return None" in new TruncateBefore {
      val person = PersonHelper.one().insert
      val one = BrandHelper.one
      val brand = one.copy(code = "One")
      brand.socialProfile_=(one.socialProfile)

      service.find("TEST") must_== None
    }
  }
}
