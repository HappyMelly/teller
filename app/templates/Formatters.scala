package templates

import org.joda.time.{ LocalDate, DateTimeZone, DateTime }
import play.api.templates.Html
import org.pegdown.PegDownProcessor
import org.joda.money.Money

/**
 * Custom formatters for use in templates.
 */
object Formatters {

  val DefaultTimeZone = DateTimeZone.forID("Europe/Brussels")

  implicit class RichDateTime(dateTime: DateTime) {
    def format(): String = format("yyyy-MM-dd HH:mm ZZZ")
    def format(pattern: String) = dateTime.withZone(DefaultTimeZone).toString(pattern)
  }

  implicit class RichLocalDate(date: LocalDate) {
    def format(): String = format("yyyy-MM-dd")
    def format(pattern: String) = date.toString(pattern)
  }

  implicit class RichString(val string: String) extends AnyVal {

    /**
     * Interpret the string as Markdown and convert to HTML.
     */
    def markdown: Html = {
      Html(new PegDownProcessor().markdownToHtml(string))
    }
  }
}