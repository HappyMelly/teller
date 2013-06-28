package templates

import org.joda.time.{ DateTimeZone, DateTime }

/**
 * Custom formatters for use in templates.
 */
object Formatters {

  val DefaultTimeZone = DateTimeZone.forID("Australia/Sydney")

  implicit class RichDateTime(dateTime: DateTime) {
    def format(): String = format("yyyy-MM-dd HH:mm ZZZ")
    def format(pattern: String) = dateTime.withZone(DefaultTimeZone).toString(pattern)
  }

}