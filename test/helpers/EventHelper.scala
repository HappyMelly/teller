/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2014, Happy Melly http://www.happymelly.com
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
package helpers

import models._
import org.joda.time.{ DateTime, LocalDate }

object EventHelper {

  def makeEvent(id: Option[Long] = None, eventTypeId: Option[Long] = None, brandCode: Option[String] = None,
    title: Option[String] = None, spokenLanguage: Option[String] = None,
    secondSpokenLanguage: Option[String] = None, materialsLanguage: Option[String] = None,
    city: Option[String] = None, country: Option[String] = None, startDate: Option[LocalDate] = None,
    endDate: Option[LocalDate] = None, notPublic: Option[Boolean] = None, archived: Option[Boolean] = None,
    confirmed: Option[Boolean] = None, invoice: Option[EventInvoice] = None,
    facilitatorIds: Option[List[Long]] = None): Event = {

    val code = brandCode.getOrElse(BrandHelper.defaultBrand.code)
    val invoice = new EventInvoice(None, None, 1, None, None)
    val language = new Language(spokenLanguage.getOrElse("DE"), secondSpokenLanguage, materialsLanguage)
    var event = new Event(id, eventTypeId.getOrElse(1), code, title.getOrElse("Test event"),
      language, new Location(city.getOrElse("spb"), country.getOrElse("RU")),
      new Details(None, None, None, None),
      new Schedule(startDate.getOrElse(new LocalDate(DateTime.now())), endDate.getOrElse(new LocalDate(DateTime.now())), 1, 1),
      notPublic.getOrElse(false), archived.getOrElse(false), confirmed.getOrElse(false),
      DateTime.now(), "Sergey Kotlov", DateTime.now(), "Sergey Kotlov")
    event.facilitatorIds_=(facilitatorIds.getOrElse(1 :: Nil))

    event
  }
}
