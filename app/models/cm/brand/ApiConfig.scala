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

package models.cm.brand

import models.{Activity, ActivityRecorder}

import scala.util.Random

/**
  * Represents API token which provides an access to Teller API
 *
 * @param id Record identifier
 * @param token Token itself
 * @param readWrite If true, both read/write requests are allowed; otherwise, read only
 */
case class ApiConfig(id: Option[Long],
                     brandId: Long,
                     token: String = Random.alphanumeric.take(64).mkString,
                     readWrite: Boolean = false,
                     active: Boolean = false,
                     events: Option[String] = None,
                     event: Option[String] = None,
                     facilitators: Option[String] = None,
                     facilitator: Option[String] = None,
                     generalEvaluation: Option[String] = None,
                     specificEventEvaluation: Option[String] = None) extends ActivityRecorder {

  val EVENT = "*EventId*"
  val FACILITATOR = "*FacilitatorId*"

  val identifier: Long = id.getOrElse(0)
  val humanIdentifier: String = "app for brand %s".format(brandId)
  val objectType: String = Activity.Type.ApiToken

  /**
    * Returns true if token is authorized to run requested action
    *
    * @param readWrite Level of rights for the authorized action
    */
  def authorized(readWrite: Boolean): Boolean = if (readWrite)
    readWrite == this.readWrite
  else
    true

  /**
    * Returns event url pattern filled with data
    * @param eventId Event identifier
    * @param facilitatorId Facilitator identifier
    */
  def eventUrl(eventId: Option[Long], facilitatorId: Option[Long]): String = {
    event.map { pattern =>
      val withEvent = eventId.map(value => pattern.replace(EVENT, value.toString)).getOrElse(pattern)
      facilitatorId.map(value => withEvent.replace(FACILITATOR, value.toString)).getOrElse(withEvent)
    }.getOrElse("")
  }

  /**
    * Returns facilitator url pattern filled with data
    * @param facilitatorId Facilitator identifier
    */
  def facilitatorUrl(facilitatorId: Long): String = activityCheck {
    facilitator.map { pattern =>
      pattern.replace(FACILITATOR, facilitatorId.toString)
    }.getOrElse("")
  }

  protected def activityCheck(f: => String): String = if (active) f else ""
}
