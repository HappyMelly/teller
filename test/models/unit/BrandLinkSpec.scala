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
package models.unit

import models.brand._
import org.specs2.mutable._

class BrandLinkSpec extends Specification {

  "When link type is valid" >> {
    "it's not changed" in {
      val link = BrandLink(None, 1L, "video", "http://test.com")
      BrandLink.updateType(link).linkType must_== "video"
      BrandLink.updateType(link.copy(linkType = "blog")).linkType must_== "blog"
      BrandLink.updateType(link.copy(linkType = "photo")).linkType must_== "photo"
      BrandLink.updateType(link.copy(linkType = "website")).linkType must_== "website"
    }
  }
  "When link type is invalid" >> {
    "it should be converted to 'other'" in {
      val link = BrandLink(None, 1L, "invalid", "http://test.com")
      BrandLink.updateType(link).linkType must_== "other"
    }
  }
}