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
import integration.PlayAppSpec
import models.brand.{ BrandCoordinator, BrandNotifications }
import models.service.BrandService
import models.service.brand.BrandCoordinatorService

/**
 * Tests for BrandService class
 */
class BrandServiceSpec extends PlayAppSpec {

  override def setupDb(): Unit = {
    List(PersonHelper.one(),
      PersonHelper.two(),
      PersonHelper.make(Some(3), "Third", "Tester")).foreach { x ⇒
        x.insert
      }
    List(BrandHelper.one, BrandHelper.make("TWO")).foreach(_.insert())
    // Persons with id = 1 is a brand owner for both brands. Its coordinator
    // records are added automatically
    val coordinator = BrandCoordinator(None, 1L, 2L, BrandNotifications())
    BrandCoordinatorService.get.insert(coordinator)
  }

  val service = new BrandService

  "Method find(_: String)" should {
    "return a brand with code TEST" in {
      val brand = BrandHelper.one
      service.find("TEST") map { x ⇒
        x.code must_== "TEST"
        x.ownerId must_== brand.ownerId
        x.uniqueName must_== brand.uniqueName
      } getOrElse ko
    }
    "return None" in {
      service.find("THREE") must_== None
    }
  }
  "The service should return" >> {

    "2 team members for brand id = 1 available in database" in {
      val members = service.coordinators(1L)
      members.length must_== 2
      members.exists(_._1.id == Some(1L))
      members.exists(_._1.id == Some(2L))
    }
    "no members for brand id = 3 as no members are available in database" in {
      service.coordinators(3L).length must_== 0
    }
  }
  "Method findByCoordinator" should {
    "return 2 brands for coordinator 1" in {
      val res = service.findByCoordinator(1L)
      res.length must_== 2
      res.exists(_.id == Some(1))
      res.exists(_.id == Some(2))
    }
    "return 1 brand for coordinator 2" in {
      service.findByCoordinator(2L).exists(_.id == Some(1))
    }
    "return 0 brand for coordinator 3" in {
      service.findByCoordinator(3L).length must_== 0
    }
  }

  "Method findWithCoordinators" should {
    "return a brand object along with related person objects" in {
      val res = service.findWithCoordinators(1L)
      res map { x ⇒
        x.brand.id must_== Some(1L)
        x.coordinators.length must_== 2
        x.coordinators.exists(_._1.firstName == "First") must_== true
        x.coordinators.exists(_._1.firstName == "Second") must_== true
      } getOrElse ko
    }
  }

  "Method isCoordinator" should {
    "return true if a person is a brand coordinator" in {
      service.isCoordinator(1L, 1L) must_== true
      service.isCoordinator(1L, 2L) must_== true
    }
    "return false if a person is not a brand coordinator" in {
      service.isCoordinator(1L, 3L) must_== false
      service.isCoordinator(2L, 2L) must_== false
    }
  }
}
