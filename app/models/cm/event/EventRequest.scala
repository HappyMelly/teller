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

package models.cm.event

import models.{DateStamp, Recipient}
import org.joda.time.LocalDate

import scala.util.Random

/**
 * Represents request for an event
 *
 * @param id Request identifier
 * @param brandId Brand this event belongs to
 * @param countryCode Two-letter country code, ex. RU
 * @param city List of cities in any format
 * @param language List of language , separated by commas. Ex. Russian,English
 * @param start The earliest possible date
 * @param end The latest possible date
 * @param participantsNumber Number of participants
 * @param comment Comment
 * @param name Name of the person who makes this request
 * @param email Email of the person who makes this request
 * @param hashedId Unique hashed identifier
 * @param unsubscribed If true the user doesn't receive upcoming event notifications
 * @param recordInfo Record info
 */
case class EventRequest(id: Option[Long],
  brandId: Long,
  countryCode: String,
  city: Option[String],
  language: String,
  start: Option[LocalDate],
  end: Option[LocalDate],
  participantsNumber: Int,
  comment: Option[String],
  name: String,
  email: String,
  hashedId: String = Random.alphanumeric.take(64).mkString,
  unsubscribed: Boolean = false,
  recordInfo: DateStamp) extends Recipient {

  def fullName: String = name

  def participants: String = participantsNumber match {
    case 1 => "1"
    case 4 => "2-4"
    case 9 => "5-9"
    case 10 => "10+"
    case value => value.toString
  }
}
