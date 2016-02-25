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
 * If you have questions concerning this license or the applicable additional terms, you may contact
 * by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */

package views

import org.joda.time.LocalDate
import views.html.helper.FieldConstructor

object ViewHelpersV2 {

  implicit val fields = FieldConstructor(views.html.v2.html.fieldConstructor.f)
  val asIs = FieldConstructor(views.html.v2.html.asIsConstructor.f)
  val narrow = FieldConstructor(views.html.v2.html.narrowFieldConstructor.f)

  /**
   * Returns well-formatted date interval
   * @param start Start date
   * @param end End date
   */
  def dateInterval(start: LocalDate, end: LocalDate): String = {
    if (start == end)
      start.toString("d MMM yyyy")
    else if (start.year() == end.year())
      if (start.monthOfYear() == end.monthOfYear())
        start.toString("d — ") + end.toString("d MMM yyyy")
      else
        start.toString("d MMM — ") + end.toString("d MMM yyyy")
    else
      start.toString("d MMM yyyy — ") + end.toString("d MMM yyyy")
  }

  def date(value: LocalDate): String = value.toString("d MMM yyyy")
}
