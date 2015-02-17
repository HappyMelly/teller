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

import _root_.integration.PlayAppSpec
import helpers.PersonHelper
import models.service.PersonService
import org.joda.money.CurrencyUnit._
import org.joda.money.Money

class PersonSpec extends PlayAppSpec {

  def setupDb() {}
  def cleanupDb() {}

  "Person" should {
    "become a well-formed supporter" in {
      val person = PersonHelper.two()
      val fee = Money.of(EUR, 155)
      person.becomeMember(funder = false, fee)

      val member = PersonService.get.member(person.id.get)
      member map { m ⇒
        m.person must_== true
        m.funder must_== false
        m.fee must_== fee
        m.createdBy must_== 2L
      } getOrElse ko
    }
    "become a well-formed funder" in {
      val person = PersonHelper.one()
      val fee = Money.of(EUR, 255)
      person.becomeMember(funder = true, fee)

      val member = PersonService.get.member(person.id.get)
      member map { m ⇒
        m.person must_== true
        m.funder must_== true
        m.fee must_== fee
        m.createdBy must_== 1L
      } getOrElse ko
    }
  }
}
