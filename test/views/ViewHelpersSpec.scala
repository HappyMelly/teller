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

package views

import org.joda.time.LocalDate
import org.specs2.mutable._

class ViewHelpersSpec extends Specification {

  "dateInterval" should {
    "return the only date if start and end are equal" in {
      val start, end = LocalDate.parse("2015-01-01")
      ViewHelpers.dateInterval(start, end) must_== "1 Jan 2015"
    }
    "return two days, month and year if start and end are in one month" in {
      val start = LocalDate.parse("2015-03-15")
      val end = LocalDate.parse("2015-03-16")
      ViewHelpers.dateInterval(start, end) must_== "15 — 16 Mar 2015"
    }
    "return two days and months, one year if start and end are in different months" in {
      val start = LocalDate.parse("2015-03-31")
      val end = LocalDate.parse("2015-04-01")
      ViewHelpers.dateInterval(start, end) must_== "31 Mar — 1 Apr 2015"
    }
    "return two dates if start and end are in different years" in {
      val start = LocalDate.parse("2014-12-31")
      val end = LocalDate.parse("2015-01-01")
      ViewHelpers.dateInterval(start, end) must_== "31 Dec 2014 — 1 Jan 2015"
    }
  }
}
