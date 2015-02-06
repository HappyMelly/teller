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
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com
 * or in writing
 * Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */
package unit.models

import helpers.{ PersonHelper, EventHelper, BrandHelper }
import _root_.models.service.EventService
import _root_.models.Person
import org.scalamock.specs2.MockContext
import org.specs2.mutable.Specification
import stubs.{ FakeServices, FakeBrandService }

class TestEventService extends EventService with FakeServices

class EventServiceSpec extends Specification {

  "Method sendConfirmationAlert" should {
    "send 1 email" in new MockContext {
      val service = new TestEventService
      val brand = mock[FakeBrandService]
      (brand.findAll _).expects().returning(List(BrandHelper.defaultBrand))
      service.brandService_=(brand)
      (service.findByParameters _).expects(
        Some(BrandHelper.defaultBrand.code),
        Some(false),
        None,
        None,
        Some(false),
        None,
        None).returning(List(EventHelper.one))

      val people: Set[Person] = Set(PersonHelper.one(), PersonHelper.two())
      (service.send _).expects(
        people,
        None,
        None,
        "Confirm your event Two",
        *,
        false,
        None).once()

    }
  }
}
