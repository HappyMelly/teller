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

package models.core

import org.joda.time.LocalDate
import org.specs2.mutable.Specification

/**
  * Tests for Coupon model
  */
class CouponTest extends Specification {

  "Coupon" should {
    val default = Coupon(None, "test", 10)
    val now = LocalDate.now()

    "be active when no start/end dates are set" in {
      default.valid must_== true
    }
    "be active when start date is correct" in {
      val coupon1 = default.copy(start = Some(now.minusDays(1)))
      coupon1.valid must_== true
      val coupon2 = default.copy(start = Some(now))
      coupon2.valid must_== true
    }
    "be inactive when start date is incorrect" in {
      val coupon = default.copy(start = Some(now.plusDays(1)))
      coupon.valid must_== false
    }
    "be active when end date is correct" in {
      val coupon1 = default.copy(end = Some(now.plusDays(1)))
      coupon1.valid must_== true
      val coupon2 = default.copy(start = Some(now))
      coupon2.valid must_== true
    }
    "be inactive when end date is incorrect" in {
      val coupon = default.copy(end = Some(now.minusDays(1)))
      coupon.valid must_== false
    }
    "be active when start and end dates are correct" in {
      val coupon = default.copy(end = Some(now.plusDays(1)), start = Some(now))
      coupon.valid must_== true
    }
    "be inactive when start and end dates are incorrect" in {
      val coupon1 = default.copy(end = Some(now.minusDays(1)), start = Some(now.minusDays(2)))
      coupon1.valid must_== false
      val coupon2 = default.copy(end = Some(now.plusDays(2)), start = Some(now.plusDays(1)))
      coupon2.valid must_== false
    }
  }
}
