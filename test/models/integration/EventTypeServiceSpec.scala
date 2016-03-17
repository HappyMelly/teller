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

import integration.PlayAppSpec
import models.cm.brand.EventType
import helpers.{ BrandHelper, PersonHelper }
import models.repository.cm.brand.EventTypeRepository

class EventTypeServiceSpec extends PlayAppSpec {

  val service = new EventTypeRepository

  override def setupDb() {
    PersonHelper.one().insert
    BrandHelper.one.insert
    BrandHelper.make("two", Some(2L)).insert
    BrandHelper.make("three", Some(3L)).insert
    Seq(
      (1L, "One"),
      (1L, "Two"),
      (1L, "Three"),
      (2L, "Four"),
      (1L, "Five"),
      (3L, "Six")).foreach {
        case (brandId, name) ⇒
          val eventType = EventType(None, brandId, name, None, 8, false)
          service.insert(eventType)
      }
  }

  "Method findByBrand" should {
    "return 4 eventTypes belonged to brand 1" in {
      val eventTypes = service.findByBrand(1L)
      eventTypes.length must_== 4
      eventTypes.exists(_.name == "One") must_== true
      eventTypes.exists(_.name == "Two") must_== true
      eventTypes.exists(_.name == "Three") must_== true
      eventTypes.exists(_.name == "Five") must_== true
    }
    "return 1 event belonged to brand 2" in {
      val eventTypes = service.findByBrand(2L)
      eventTypes.length must_== 1
      eventTypes.exists(_.name == "Four") must_== true
    }
    "return 0 eventTypes belonged to brand 4" in {
      val eventTypes = service.findByBrand(4L)
      eventTypes.length must_== 0
    }
  }

  "Method find" should {
    "return event type object with id = 1" in {
      val eventType = service.find(1L)
      eventType map { x ⇒
        x.name must_== "One"
        x.id.get must_== 1L
      } getOrElse ko
    }
  }

  "Method update" should {
    "record name, defaultTitle, max hours and free flag to database" in {
      val one = service.find(1L)
      one map { x ⇒
        val updated = x.copy(name = "Updated",
          defaultTitle = Some("Updated title"), maxHours = 20, free = true)
        service.update(updated)
        val retrieved = service.find(1L)
        retrieved map { y ⇒
          y.name must_== "Updated"
          y.defaultTitle must_== Some("Updated title")
          y.maxHours must_== 20
          y.free must_== true
        } getOrElse ko
      } getOrElse ko
    }
  }

  "Method exist" should {
    "return true for the event type with id = 1" in {
      service.exists(1L) must_== true
    }
    "return false for the event type with id = 1" in {
      service.exists(8L) must_== false
    }
  }

  "Method delete" should {
    "remove the event type with id = 1 from database" in {
      service.delete(1L)
      service.exists(1L) must_== false
    }
    "not throw errors if an event type doesn't exist" in {
      service.exists(10L) must_== false
      service.delete(10L)
      service.exists(10L) must_== false
    }
  }
}