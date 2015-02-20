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
import models.PaymentRecord
import models.service.PaymentRecordService
import org.joda.money.CurrencyUnit._
import org.joda.money.Money
import org.joda.time.DateTime

class PaymentRecordServiceSpec extends PlayAppSpec {

  def setupDb(): Unit = {
    Seq(
      ("1", 1L, true, Money.of(EUR, 4)),
      ("2", 1L, true, Money.of(EUR, 5)),
      ("3", 1L, false, Money.of(EUR, 6)),
      ("4", 1L, false, Money.of(EUR, 8)),
      ("5", 2L, true, Money.of(EUR, 10)),
      ("6", 3L, true, Money.of(EUR, 12))).foreach {
        case (remoteId, objectId, person, fee) ⇒
          val r = new PaymentRecord(None, remoteId, 1L, objectId, person, "",
            fee, DateTime.now())
          r.insert
      }
  }
  def cleanupDb() {}

  "Method 'findByPerson`" should {
    "return 2 records belonged to the person with id = 1" in {
      val payments = PaymentRecordService.get.findByPerson(1L)
      payments.length must_== 2
      payments.exists(_.fee == Money.of(EUR, 4)) must_== true
      payments.exists(_.fee == Money.of(EUR, 5)) must_== true
    }
    "return 1 record belonged to the person with id = 2" in {
      val payments = PaymentRecordService.get.findByPerson(2L)
      payments.length must_== 1
      payments.exists(_.fee == Money.of(EUR, 10)) must_== true
    }
    "return 0 records belonged to the person with id = 5" in {
      val payments = PaymentRecordService.get.findByPerson(5)
      payments.length must_== 0
    }
  }
}
