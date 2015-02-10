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

import models.{ Activity, Contribution }
import org.specs2.mutable._

class ContributionSpec extends Specification {

  "Contribution" should {
    "have well-formed activity attributes" in {
      val contribution = new Contribution(Some(1L), 1L, 2L, true, "Tester")
      contribution.objectType must_== Activity.Type.Contribution
      contribution.identifier must_== 1
      contribution.humanIdentifier must_== "product with id = 2 as Tester"
      val contribution2 = new Contribution(Some(2L), 2L, 3L, false, "Funder")
      contribution2.objectType must_== Activity.Type.Contribution
      contribution2.identifier must_== 2
      contribution2.humanIdentifier must_== "product with id = 3 as Funder"
    }
  }

}
