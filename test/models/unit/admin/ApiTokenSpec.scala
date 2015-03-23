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

package models.unit.admin

import models.admin.ApiToken
import org.specs2.mutable._

class ApiTokenSpec extends Specification {

  "Method 'authorized'" should {
    val token = ApiToken(None, "test", "Test", "Test", None, readWrite = false)
    "return false when token has not enough rights" in {
      token.authorized(readWrite = false) must_== true
      token.authorized(readWrite = true) must_== false
    }
    "return true when toekn has enought rights" in {
      val writeToken = token.copy(readWrite = true)
      writeToken.authorized(readWrite = false) must_== true
      writeToken.authorized(readWrite = true) must_== true
    }
  }
}
