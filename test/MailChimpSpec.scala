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

import libs.mailchimp.MergeField
import models.core.integration.MailChimp
import org.specs2.matcher.BeEqualValueCheck
import org.specs2.mutable.Specification

/**
  * Tests for MailChimp supporting methods
  */
class MailChimpSpec extends Specification {

  "List merge fields should" >> {
    "contain EMAIL tag (-)" >> {
      val fields = Seq(
        MergeField(None, Some("MAIL"), "email", "email_address"),
        MergeField(None, Some("NAME"), "name", "name"))
      MailChimp.validateMergeFields(fields) must beLeft
    }
    "contain EMAIL tag (+)" >> {
      val fields = Seq(
        MergeField(None, Some("EMAIL"), "email", "email_address"),
        MergeField(None, Some("NAME"), "name", "name"))
      MailChimp.validateMergeFields(fields) must beRight
    }
    "NOT contain any required tags except supported one (-)" >> {
      val fields = Seq(
        MergeField(None, Some("EMAIL"), "email", "email_address"),
        MergeField(None, Some("NAME"), "name", "name", required = Some(true)))
      val err = "Fields with NAME tags are required. No data is provided for them."
      MailChimp.validateMergeFields(fields) must beLeft(BeEqualValueCheck(err))
    }
    "NOT contain any required tags except supported one (+)" >> {
      val fields = Seq(
        MergeField(None, Some("EMAIL"), "email", "email_address", required = Some(true)),
        MergeField(None, Some("FNAME"), "email", "email_address", required = Some(true)),
        MergeField(None, Some("LNAME"), "name", "name", required = Some(true)))
      MailChimp.validateMergeFields(fields) must beRight
    }
    "notify when FNAME and LNAME fields are not found" >> {
      val fields = Seq(MergeField(None, Some("EMAIL"), "email", "email_address"))
      val msg = "Fields with FNAME and LNAME tags are not found. Only email will be exported for attendee."
      MailChimp.validateMergeFields(fields) must beRight(BeEqualValueCheck(msg))
    }
    "notify when FNAME field is not found" >> {
      val fields = Seq(
        MergeField(None, Some("EMAIL"), "email", "email_address"),
        MergeField(None, Some("LNAME"), "email", "email_address"))
      val msg = "Field with FNAME tag is not found. Attendee's first name won't be exported."
      MailChimp.validateMergeFields(fields) must beRight(BeEqualValueCheck(msg))
    }
    "notify when LNAME field is not found" >> {
      val fields = Seq(
        MergeField(None, Some("EMAIL"), "email", "email_address"),
        MergeField(None, Some("FNAME"), "email", "email_address"))
      val msg = "Field with LNAME tag is not found. Attendee's last name won't be exported."
      MailChimp.validateMergeFields(fields) must beRight(BeEqualValueCheck(msg))
    }
    "pass validation with empty message when everything is okay" >> {
      val fields = Seq(
        MergeField(None, Some("EMAIL"), "email", "email_address"),
        MergeField(None, Some("FNAME"), "email", "email_address"),
        MergeField(None, Some("LNAME"), "name", "name"))
      MailChimp.validateMergeFields(fields) must beRight(BeEqualValueCheck(""))
    }
  }
}
