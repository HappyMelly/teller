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
 * If you have questions concerning this license or the applicable additional
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */

package services.notifiers

import play.api.libs.json._
import play.api.libs.ws.WS
import scala.util.Try

/**
 * Notifies using Slack
 */
class Slack(webhookUrl: String, channel: String, username: String) {

  /**
   * Sends a message to Slach channel
   * @param message Message itself
   * @param channel Channel to send. If empty, default channel is used
   * @param username Name of the sender. If empty, default username is used
   */
  def send(message: String,
    channel: Option[String] = None,
    username: Option[String] = None): Boolean = {
    val ch = channel getOrElse this.channel
    val name = username getOrElse this.username
    val text = Json.obj("text" -> message,
      "channel" -> ch,
      "username" -> name).toString()
    Try(WS.url(webhookUrl).post(text)).isSuccess
  }
}
