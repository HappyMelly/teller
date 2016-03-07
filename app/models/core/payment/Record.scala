/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2016, Happy Melly http://www.happymelly.com
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

package models.core.payment

import org.joda.money.Money
import org.joda.time.DateTime

/**
 * Contains data of a successful payment
 */
case class Record(id: Option[Long],
    remoteId: String,
    payerId: Long,
    objectId: Long,
    person: Boolean,
    description: String,
    fee: Money,
    created: DateTime)

object Record {

  /**
   * Returns new PaymentRecord object
 *
   * @param remoteId Remote payment id
   * @param payerId Payer id
   * @param objectId Object of the payment
   * @param person Defines if the object is a person
   * @param description Description of the payment
   * @param fee Amount of the payment
   */
  def apply(remoteId: String,
    payerId: Long,
    objectId: Long,
    person: Boolean,
    description: String,
    fee: Money): Record = {
    new Record(None, remoteId, payerId, objectId, person,
      description, fee, DateTime.now())
  }
}