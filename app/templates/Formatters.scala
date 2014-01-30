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

package templates

import org.joda.time.{ LocalDate, DateTimeZone, DateTime }
import play.api.templates.Html
import org.pegdown.PegDownProcessor
import org.joda.money.Money
import org.jsoup.Jsoup
import org.jsoup.safety.Whitelist

/**
 * Custom formatters for use in templates.
 */
object Formatters {

  val DatePattern = "yyyy-MM-dd"
  val DefaultTimeZone = DateTimeZone.forID("Europe/Brussels")

  implicit class RichDateTime(dateTime: DateTime) {
    def format(): String = format("yyyy-MM-dd HH:mm ZZZ")
    def format(pattern: String) = dateTime.withZone(DefaultTimeZone).toString(pattern)
  }

  implicit class RichLocalDate(date: LocalDate) {
    def format(): String = format(DatePattern)
    def format(pattern: String) = date.toString(pattern)
  }

  implicit class RichMoney(money: Money) {
    def format: Html = Html(s"<small>${money.getCurrencyUnit.getCode}</small>&nbsp;${money.getAmount}")
    def formatText = s"${money.getCurrencyUnit.getCode} ${money.getAmount}"
  }

  implicit class RichString(val string: String) extends AnyVal {

    /**
     * Interpret the string as Markdown and convert to HTML.
     */
    def markdown: Html = {
      val html = new PegDownProcessor().markdownToHtml(string)
      val cleanHtml = Jsoup.clean(html, Whitelist.basic())
      Html(cleanHtml)
    }
  }
}
