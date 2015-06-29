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

import helpers.PersonHelper
import models._
import org.joda.money.Money
import org.joda.time.{ DateTime, LocalDate }
import org.specs2.mutable._

class PersonSpec extends Specification {

  "Person" should {
    "return well-formed activity data" in {
      val person = PersonHelper.one()
      person.identifier must_== 1
      person.humanIdentifier must_== "First Tester"
      person.objectType must_== Activity.Type.Person
    }
    "change any field on 'copy'" in {
      val person = PersonHelper.one()
      person.copy(id = Some(5L)).id must_== Some(5L)
      person.copy(firstName = "Name").firstName must_== "Name"
      person.copy(lastName = "LastName").lastName must_== "LastName"
      val date = LocalDate.now()
      person.copy(birthday = Some(date)).birthday must_== Some(date)
      val photo = Photo(Some("photo"), Some("url"))
      person.photo must_!= photo
      person.copy(photo = photo).photo must_== photo
      person.copy(signature = true).signature must_== true
      person.copy(addressId = 4L).addressId must_== 4L
      person.copy(bio = Some("bio")).bio must_== Some("bio")
      person.copy(interests = Some("interests")).interests must_== Some("interests")
      person.copy(webSite = Some("url")).webSite must_== Some("url")
      person.copy(blog = Some("blog")).blog must_== Some("blog")
      person.copy(customerId = Some("ID")).customerId must_== Some("ID")
      person.copy(virtual = true).virtual must_== true
      person.copy(active = false).active must_== false
      val dateStamp = DateStamp(DateTime.parse("2015-01-01"),
        "Me",
        DateTime.parse("2015-02-01"),
        "Me")
      person.copy(dateStamp = dateStamp).dateStamp must_== dateStamp
      // check that address and social profile are also copied
      val address = Address(id = Some(7L), countryCode = "GE")
      person.address_=(address)
      person.copy(active = false).address must_== address
      val socialProfile = SocialProfile(email = "indigo@mail.com")
      person.socialProfile_=(socialProfile)
      person.copy(active = false).socialProfile must_== socialProfile
    }
  }

}
