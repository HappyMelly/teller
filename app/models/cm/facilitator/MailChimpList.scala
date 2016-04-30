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

package models.cm.facilitator

import models.Brand

/**
  * Represents MailChimp's list integration for brand facilitators
  *
  * All valid attendees from the given brand will be subscribed to the given list
  */
case class MailChimpList(id: Option[Long],
                         listName: String,
                         listId: String,
                         brandId: Long,
                         personId: Long,
                         allAttendees: Boolean = true,
                         oldEventAttendees: Boolean = true)

case class MailChimpListBlock(list: MailChimpList,
                              brands: Seq[Brand],
                              totalBrandsNumber: Int) {

  val caption: String = if (totalBrandsNumber == 1)
    getOneBrandCaption
  else
    getMultipleBrandsCaption

  protected def getOneBrandCaption: String = importType(list)

  protected def getMultipleBrandsCaption: String = {
    val brandCaption = if (totalBrandsNumber == brands.length)
      "all"
    else
      brands.map(_.name).mkString(" and ")
    val suffix = if (brands.length == 1)
      "brand"
    else
      "brands"
    s"${importType(list)} from $brandCaption $suffix"
  }

  protected def importType(list: MailChimpList): String = if (list.allAttendees)
    "all attendees"
  else
    "only attendees with evaluations"
}
