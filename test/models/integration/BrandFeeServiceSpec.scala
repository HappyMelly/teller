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

import integration.PlayAppSpec
import models.brand.BrandFee
import models.service.brand.BrandFeeService
import org.joda.money.Money

class BrandFeeServiceSpec extends PlayAppSpec {

  "Method 'findByBrand'" should {
    "return 2 fees for the brand id = 1" in {
      val m1 = Money.parse("RUB 100")
      val m2 = Money.parse("EUR 300")
      Seq(
        (1L, "RU", m1),
        (1L, "DE", m2),
        (2L, "US", Money.parse("USD 100")),
        (2L, "HK", Money.parse("USD 100"))).foreach {
          case (brand, country, fee) ⇒
            BrandFee(None, brand, country, fee).insert()
        }

      val service = new BrandFeeService
      val result = service.findByBrand(1L)
      result.length must_== 2
      result.exists(x ⇒ x.country == "RU" && x.fee == m1) must_== true
      result.exists(x ⇒ x.country == "DE" && x.fee == m2) must_== true
    }
  }
}
