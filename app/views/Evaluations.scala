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

package views

object Evaluations {

  val impression =
    List(
      ("0", "Terrible (0)"),
      ("1", "Very bad (1)"),
      ("2", "Bad (2)"),
      ("3", "Disappointing (3)"),
      ("4", "Below average (4)"),
      ("5", "Average (5)"),
      ("6", "Above average (6)"),
      ("7", "Fine (7)"),
      ("8", "Good (8)"),
      ("9", "Very good (9)"),
      ("10", "Excellent (10)"))

  val recommendation =
    List(
      ("0", "Certainly not (0%)"),
      ("1", "Highly unlikely (10%)"),
      ("2", "Unlikely (20%)"),
      ("3", "Quite unlikely (30%)"),
      ("4", "Possibly not (40%)"),
      ("5", "Maybe (50%)"),
      ("6", "Yes, possibly (60%)"),
      ("7", "Quite possibly (70%)"),
      ("8", "Likely (80%)"),
      ("9", "Highly likely (90%)"),
      ("10", "Certainly (100%)"))
}
