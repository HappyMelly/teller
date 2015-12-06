package models

import org.joda.time.DateTime

/**
  * Represents token created during email change process
  */
case class EmailToken(token: String,
                     email: String,
                     userId: Long,
                     create: DateTime,
                     expire: DateTime) {
  def isExpired = expire.isBeforeNow
}
