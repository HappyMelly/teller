/*
* Happy Melly Teller
* Copyright (C) 2013 - 2015, Happy Melly http -> //www.happymelly.com
*
* This file is part of the Happy Melly Teller.
*
* Happy Melly Teller is free software ->  you can redistribute it and/or modify
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
* along with Happy Melly Teller.  If not, see <http -> //www.gnu.org/licenses/>.
*
* If you have questions concerning this license or the applicable additional
* terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com
* or in writing Happy Melly One, Handelsplein 37, Rotterdam,
* The Netherlands, 3071 PR
*/
package stubs.services

import _root_.services.notifiers.{ Notifiers, Slack, Email }
import models.Person

/**
 * Stub class for Slack service
 */
class FakeSlack
  extends Slack("testhook", "#test", "usertest") {
  var message: String = ""

  override def send(message: String,
    channel: Option[String],
    username: Option[String]): Boolean = {
    this.message = message
    true
  }
}

/**
 * Stub class for Email service
 */
class FakeEmail extends Email {
  var to: Set[Person] = Set()
  var cc: Option[Set[Person]] = None
  var bcc: Option[Set[Person]] = None
  var subject: String = ""
  var body: String = ""

  override def send(to: Set[Person],
    cc: Option[Set[Person]] = None,
    bcc: Option[Set[Person]] = None,
    subject: String,
    body: String,
    richMessage: Boolean = false,
    attachment: Option[(String, String)] = None): Unit = {
    this.to = to
    this.cc = cc
    this.bcc = bcc
    this.subject = subject
    this.body = body
  }
}

trait FakeNotifiers extends Notifiers {
  val slackInstance = new FakeSlack
  val emailInstance = new FakeEmail

  /** Returns a fake Slack notifier */
  override def slack: FakeSlack = slackInstance

  /** Returns a fake Email notifier */
  override def email: FakeEmail = emailInstance
}
