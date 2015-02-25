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

package models.unit.payment

import models.payment.Payment
import org.specs2.mutable._

class PaymentSpec extends Specification {

  "Minimal and suggested fees" should {
    "be 25/50 for Qatar, Hong Kong and Netherlands" in {
      val qa = Payment.countryBasedFees("QA")
      qa._1 must_== 25
      qa._2 must_== 50
      val hk = Payment.countryBasedFees("HK")
      hk._1 must_== 25
      hk._2 must_== 50
      val nl = Payment.countryBasedFees("NL")
      nl._1 must_== 25
      nl._2 must_== 50
    }
    "be 20/40 for Ireland, Belgium, Italy" in {
      val ie = Payment.countryBasedFees("IE")
      ie._1 must_== 20
      ie._2 must_== 40
      val be = Payment.countryBasedFees("BE")
      be._1 must_== 20
      be._2 must_== 40
      val it = Payment.countryBasedFees("IT")
      it._1 must_== 20
      it._2 must_== 40
    }
    "be 15/30 for New Zealand, Israel and Antigua and Barbuda" in {
      val nz = Payment.countryBasedFees("NZ")
      nz._1 must_== 15
      nz._2 must_== 30
      val il = Payment.countryBasedFees("IL")
      il._1 must_== 15
      il._2 must_== 30
      val ag = Payment.countryBasedFees("AG")
      ag._1 must_== 15
      ag._2 must_== 30
    }
    "be 10/20 for Saint Kitts and Nevis, Jordan and Jamaica" in {
      val kn = Payment.countryBasedFees("KN")
      kn._1 must_== 10
      kn._2 must_== 20
      val jo = Payment.countryBasedFees("JO")
      jo._1 must_== 10
      jo._2 must_== 20
      val jm = Payment.countryBasedFees("JM")
      jm._1 must_== 10
      jm._2 must_== 20
    }
    "be 5/10 for Ukraine, Barbados and Turks and Caicos Islands" in {
      val ua = Payment.countryBasedFees("UA")
      ua._1 must_== 5
      ua._2 must_== 10
      val bb = Payment.countryBasedFees("BB")
      bb._1 must_== 5
      bb._2 must_== 10
      val tc = Payment.countryBasedFees("TC")
      tc._1 must_== 5
      tc._2 must_== 10
    }
  }
}
