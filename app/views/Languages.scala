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

object Languages {

  /**
    * Returns language name by its code
    * @param code Language code
    */
  def name(code: String): String = all.getOrElse(code, "")

  val all =
    Map("BG" -> "Bulgarian",
      "ZH" -> "Chinese",
      "HR" -> "Croatian",
      "CS" -> "Czech",
      "DA" -> "Danish",
      "NL" -> "Dutch",
      "EN" -> "English",
      "FI" -> "Finnish",
      "FR" -> "French",
      "DE" -> "German",
      "IT" -> "Italian",
      "SL" -> "Slovenian",
      "JA" -> "Japanese",
      "NO" -> "Norwegian",
      "PL" -> "Polish",
      "PT" -> "Portuguese",
      "RU" -> "Russian",
      "ES" -> "Spanish",
      "SV" -> "Swedish",
      "TR" -> "Turkish",
      "VI" -> "Vietnamese")

}
