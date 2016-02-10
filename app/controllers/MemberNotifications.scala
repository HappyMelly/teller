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
package controllers

import models.{Member, SocialProfile}

/**
 * Contains methods for notifying members about changes of other members
 */
trait MemberNotifications extends Utilities {

  /**
   * Returns a well-formed Slack notification message
   * @param member Member object
   * @param name Name of a new member either an organisation or a person
   * @param url Profile url
   */
  protected def newMemberMsg(member: Member, name: String, url: String): String = {
    val typeName = if (member.funder) "Funder" else "Supporter"
    "Hooray!! We have *new %s*, %s. <%s|View profile>".format(
      typeName, name, Utilities.fullUrl(url))
  }

  /**
   * Compares social profiles and returns a list of errors for a form
   *
   * @param existing Existing social profile
   * @param updated Updated social profile
   */
  protected def connectMeMessage(existing: SocialProfile,
                                          updated: SocialProfile): Option[String] = {
    val messages = List(
      fieldMessage("twitter", existing.twitterHandle, updated.twitterHandle),
      fieldMessage("facebook", existing.facebookUrl, updated.facebookUrl),
      fieldMessage("google", existing.googlePlusUrl, updated.googlePlusUrl),
      fieldMessage("linkedin", existing.linkedInUrl, updated.linkedInUrl))
    val nonEmptyMessages = messages.filterNot(_.isEmpty)
    nonEmptyMessages.headOption map { first ⇒
      val prefix = "Let's show them some love by linking on "
      val msg = nonEmptyMessages.tail.foldLeft(first.get)(_ + ", " + _.get)
      Some(prefix + " " + msg)
    } getOrElse None
  }

  /**
   * Composes notification if the given value was updated
   *
   * @param msgType Notification type
   * @param existing Old value
   * @param updated New value
   */
  protected def fieldMessage(msgType: String,
                                    existing: Option[String],
                                    updated: Option[String]): Option[String] = {
    if (updated.isDefined && existing != updated)
      Some(typedFieldMessage(msgType, updated.get))
    else
      None
  }

  protected def typedFieldMessage(msgType: String, value: String): String =
    msgType match {
      case "twitter" ⇒ "<http://twitter.com/%s|Twitter>".format(value)
      case "facebook" ⇒ "<%s|Facebook>".format(value)
      case "google" ⇒ "<%s|G+>".format(value)
      case "linkedin" ⇒ "<%s|LinkedIn>".format(value)
      case "blog" ⇒ "and reading his/her blog <%s|here>".format(value)
      case _ ⇒ ""
    }

}
