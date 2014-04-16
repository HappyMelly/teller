/*
 * Happy Melly Teller
 * Copyright (C) 2013, Happy Melly http://www.happymelly.com
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

object Evaluations {

  val impression =
    List(
      ("0", "terrible"),
      ("1", "very bad"),
      ("2", "bad"),
      ("3", "disappointing"),
      ("4", "below average"),
      ("5", "average"),
      ("6", "above average"),
      ("7", "fine"),
      ("8", "good"),
      ("9", "very good"),
      ("10", "excellent"))

  val recommendation =
    List(
      ("0", "certainly not"),
      ("1", "highly unlikely"),
      ("2", "unlikely"),
      ("3", "quite unlikely"),
      ("4", "possibly not"),
      ("5", "maybe"),
      ("6", "yes, possibly"),
      ("7", "quite possibly"),
      ("8", "likely"),
      ("9", "highly likely"),
      ("10", "certainly"))
}
