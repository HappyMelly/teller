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

import helpers.{ BrandHelper, PersonHelper }
import integration.{ TruncateBefore, PlayAppSpec }
import models.{ Facilitator, License }
import models.service.{ FacilitatorService, LicenseService }
import org.joda.money.Money
import org.joda.money.CurrencyUnit._
import org.joda.time.LocalDate

class LicenseServiceSpec extends PlayAppSpec {

  val service = new LicenseService

  "Method `expiring`" should {
    "return only licenses expiring this month" in {
      val facilitators = Map(1L -> PersonHelper.one(),
        2L -> PersonHelper.two(),
        3L -> PersonHelper.make(Some(3L), "Third", "Tester"),
        4L -> PersonHelper.make(Some(4L), "Fourth", "Tester"),
        5L -> PersonHelper.make(Some(5L), "Fifth", "Tester"))
      facilitators.foreach(v ⇒ v._2.insert)

      BrandHelper.one.insert()
      BrandHelper.make("two", Some(2L)).insert()
      BrandHelper.make("three", Some(3L)).insert()
      val now = LocalDate.now()
      Seq(
        (4L, 1L, now.withDayOfMonth(1), now.dayOfMonth().withMinimumValue()),
        (5L, 3L, now.minusYears(1), now.dayOfMonth().withMaximumValue()),
        (3L, 2L, now.minusYears(1), now.dayOfMonth().withMaximumValue()),
        (1L, 2L, now.minusMonths(6), now.plusMonths(4)),
        (2L, 1L, now.minusMonths(4), now.plusMonths(1))).foreach {
          case (licenseeId, brandId, start, end) ⇒
            val license = new License(None, licenseeId, brandId,
              "1", LocalDate.now().minusYears(1),
              start, end, true, Money.of(EUR, 100), Some(Money.of(EUR, 100)))
            service.add(license)
        }
      val licenses = service.expiring(List(1, 3))
      licenses.length must_== 2
      licenses.exists(_.license.licenseeId == 1) must beFalse
      licenses.exists(_.license.licenseeId == 2) must beFalse
      licenses.exists(_.license.licenseeId == 3) must beFalse
      licenses.exists(_.license.licenseeId == 4) must beTrue
      licenses.exists(_.license.licenseeId == 5) must beTrue
      licenses.exists(_.licensee.fullName == "Fifth Tester") must beTrue
      licenses.exists(_.licensee.fullName == "Fourth Tester") must beTrue
    }
  }
  "Method 'add'" should {
    "add facilitator record the record it does not exist" in new TruncateBefore {
      val person = PersonHelper.one().insert
      val brand = BrandHelper.one.insert()
      val now = LocalDate.now()
      val license = new License(None, person.id.get, brand.id.get,
        "1", LocalDate.now().minusYears(1), now.minusDays(1), now.plusDays(2),
        true, Money.of(EUR, 100), Some(Money.of(EUR, 100)))
      FacilitatorService.get.find(brand.id.get, person.id.get) must_== None
      service.add(license)
      FacilitatorService.get.find(brand.id.get, person.id.get) must_!= None
    }
    "not add facilitator record and successfully execute" in new TruncateBefore {
      val person = PersonHelper.one().insert
      val brand = BrandHelper.one.insert()
      val now = LocalDate.now()
      val facilitator = Facilitator(None, person.id.get, brand.id.get)
      FacilitatorService.get.insert(facilitator)
      val license = new License(None, person.id.get, brand.id.get,
        "1", LocalDate.now().minusYears(1), now.minusDays(1), now.plusDays(2),
        true, Money.of(EUR, 100), Some(Money.of(EUR, 100)))
      service.add(license)
      ok
    }
  }
}
