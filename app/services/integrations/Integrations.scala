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

package services.integrations

import play.api._
import play.api.Play.current

/**
 * Contains all integrations available in the system
 */
trait Integrations {

  /** Returns Email notifier */
  def email: Email = new Email

  /** Returns Slack notifier */
  def slack: Slack = {
    val webhook = Play.configuration.getString("slack.webhook").getOrElse("")
    val channel = Play.configuration.getString("slack.channel").getOrElse("")
    val username = Play.configuration.getString("slack.username").getOrElse("")
    new Slack(webhook, channel, username)
  }

  /** Returns MailChimp service */
  def mailChimp: MailChimp = {
    val apiUrl = Play.configuration.getString("mailchimp.url").getOrElse("")
    val apiToken = Play.configuration.getString("mailchimp.token").getOrElse("")
    new MailChimp(apiUrl, apiToken)
  }
}
