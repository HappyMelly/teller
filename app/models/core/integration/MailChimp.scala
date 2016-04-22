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

package models.core.integration

import libs.mailchimp.MergeField

/**
  * Contains a set of helper methods for working with MailChimp
  */
object MailChimp {
  val FNAME = "FNAME"
  val LNAME = "LNAME"

  val supportedRequiredFields = Seq(FNAME, LNAME)

  /**
    * Validates the given merge fields and returns description what is wrong with them
    * @param fields List's merge fields
    */
  def validateMergeFields(fields: Seq[MergeField]): Either[String, String] = {
    val withValidTags = fields.filter(_.tag.nonEmpty)
    val unsupportedFields = unsupportedRequiredFields(withValidTags)
    if (unsupportedFields.nonEmpty) {
      val names = unsupportedFields.map(_.tag.get).mkString(", ")
      Left(s"Fields with $names tags are required. No data is provided for them.")
    } else {
      val fnameExists = withValidTags.exists(_.tag.contains(FNAME))
      val lnameExists = withValidTags.exists(_.tag.contains(LNAME))
      (fnameExists, lnameExists) match {
        case (false, false) =>
          Right(s"Fields with $FNAME and $LNAME tags are not found. Only email will be exported for attendee.")
        case (false, true) =>
          Right(s"Field with $FNAME tag is not found. Attendee's first name won't be exported.")
        case (true, false) =>
          Right(s"Field with $LNAME tag is not found. Attendee's last name won't be exported.")
        case _ =>
          Right("")
      }
    }
  }

  protected def unsupportedRequiredFields(fields: Seq[MergeField]): Seq[MergeField] =
    fields.filter(_.required.contains(true)).filterNot(x => supportedRequiredFields.contains(x.tag.get))
}
