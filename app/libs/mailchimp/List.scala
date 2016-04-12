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

package libs.mailchimp

import play.api.libs.json._
import play.api.libs.functional.syntax._

/**
  * Represents MailChimp CampaignDefaults object
  */
case class CampaignDefaults(fromName: String,
                            fromEmail: String,
                            subject: String,
                            language: String)

/**
  * Represents MailChimp ListContact object
  */
case class ListContact(company: String,
                       address1: String,
                       address2: Option[String] = None,
                       city: String,
                       state: String,
                       zip: String,
                       country: String,
                       phone: Option[String] = None)

/**
  * Represents MailChimp List object
  */
case class List(id: Option[String] = None,
                name: String,
                contact: ListContact,
                permissionReminder: String,
                useArchiveBar: Option[Boolean] = None,
                campaignDefaults: CampaignDefaults,
                notifyOnSubscribe: Option[String] = None,
                notifyOnUnsubscribe: Option[String] = None,
                dateCreated: Option[String] = None,
                listRating: Option[Int] = None,
                emailTypeOption: Boolean,
                subscribeUrlShort: Option[String] = None,
                subscribeUrlLong: Option[String] = None,
                beamerAddress: Option[String] = None,
                visibility: Option[String] = None)

object Reads {

  implicit val campaignDefaults: Reads[CampaignDefaults] = (
    (__ \ "from_name").read[String] and
      (__ \ "from_email").read[String] and
      (__ \ "subject").read[String] and
      (__ \ "language").read[String]
    )(CampaignDefaults)

  implicit val contact: Reads[ListContact] = Json.reads[ListContact]

  implicit val list: Reads[List] = (
    (__ \ "id").readNullable[String] and
      (__ \ "name").read[String] and
      (__ \ "contact").read[ListContact] and
      (__ \ "permission_reminder").read[String] and
      (__ \ "use_archive_bar").readNullable[Boolean] and
      (__ \ "campaign_defaults").read[CampaignDefaults] and
      (__ \ "notify_on_subscribe").readNullable[String] and
      (__ \ "notify_on_unsubscribe").readNullable[String] and
      (__ \ "date_created").readNullable[String] and
      (__ \ "list_rating").readNullable[Int] and
      (__ \ "email_type_option").read[Boolean] and
      (__ \ "subscribe_url_short").readNullable[String] and
      (__ \ "subscribe_url_long").readNullable[String] and
      (__ \ "beamer_address").readNullable[String] and
      (__ \ "visibility").readNullable[String]
    )(List)
}