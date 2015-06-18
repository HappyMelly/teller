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
 * If you have questions concerning this license or the applicable additional terms, you may contact
 * by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */
package models.unit

import models.{ SocialProfile, ProfileType }
import org.specs2.mutable._

class SocialProfileSpec extends Specification {

  val profile = SocialProfile(email = "test@test.com")

  "Social profile should be defined" >> {
    "when only twitter is set" in {
      profile.copy(twitterHandle = Some("skotlov")).defined must_== true
    }
    "when only facebook is set" in {
      profile.copy(facebookUrl = Some("facebook.com/skotlov")).defined must_== true
    }
    "when only google is set" in {
      profile.copy(googlePlusUrl = Some("google.com/skotlov")).defined must_== true
    }
    "when only linkedin is set" in {
      profile.copy(linkedInUrl = Some("linkedin.com/skotlov")).defined must_== true
    }
  }
  "Social profile should be undefined" >> {
    "when no social network is set" in {
      profile.defined must_== false
    }
  }

  "Social profile should be complete" >> {
    "when 2 social networks are set" in {
      profile.
        copy(twitterHandle = Some("skotlov")).
        copy(facebookUrl = Some("facebook.com/skotlov")).complete must_== true
    }
  }
  "Social profile should be incomplete" >> {
    "when 1 social network is set" in {
      profile.copy(twitterHandle = Some("skotlov")).complete must_== false
    }
    "when no social network is set" in {
      profile.complete must_== false
    }
  }
  "Method 'forBrand'" should {
    "convert current profile to the one of Brand type" in {
      profile.forBrand.objectType must_== ProfileType.Brand
    }
  }
  "Method 'forOrg'" should {
    "convert current profile to the one of Organisation type" in {
      profile.forOrg.objectType must_== ProfileType.Organisation
    }
  }
  "Method 'forPerson'" should {
    "convert current profile to the one of Person type" in {
      profile.forOrg.forPerson.objectType must_== ProfileType.Person
    }
  }
}