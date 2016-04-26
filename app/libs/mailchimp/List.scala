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

case class MergeField(id: Option[Int] = None,
                      tag: Option[String] = None,
                      name: String,
                      typ: String,
                      required: Option[Boolean] = None,
                      defaultValue: Option[String] = None,
                      public: Option[Boolean] = None,
                      displayOrder: Option[Int] = None)

