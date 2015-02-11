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

import helpers.{ OrganisationHelper, PersonHelper }
import models.Activity
import org.specs2.mutable._

class OrganisationSpec extends Specification {

  "Organisation" should {
    "have well-formed activity attributes" in {
      val org = OrganisationHelper.one.copy(id = Some(1L))
      org.objectType must_== Activity.Type.Org
      org.identifier must_== 1
      org.humanIdentifier must_== "One"
      val org2 = OrganisationHelper.two.copy(id = Some(2L))
      org2.objectType must_== Activity.Type.Org
      org2.identifier must_== 2
      org2.humanIdentifier must_== "Two"
    }
  }

}
