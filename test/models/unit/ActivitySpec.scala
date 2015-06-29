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
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com
 * or in writing
 * Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */
package models

import org.specs2.mutable._

class ActivitySpec extends Specification {

  "Given activity has None action when it's inserted" >> {
    "an exception should be generated" in {
      default.insert must throwA[InvalidActivityPredicate]
    }
  }

  "Activity object with updated action should be returned" >> {
    "when signedUp method is called" in {
      default.signedUp.predicate must_== Activity.Predicate.SignedUp
    }
    "when created method is called" in {
      default.created.predicate must_== Activity.Predicate.Created
    }
    "when updated method is called" in {
      default.updated.predicate must_== Activity.Predicate.Updated
    }
    "when deleted method is called" in {
      default.deleted.predicate must_== Activity.Predicate.Deleted
    }
    "when activated method is called" in {
      default.activated.predicate must_== Activity.Predicate.Activated
    }
    "when deactivated method is called" in {
      default.deactivated.predicate must_== Activity.Predicate.Deactivated
    }
    "when added method is called" in {
      default.added.predicate must_== Activity.Predicate.Added
    }
    "when replaced method is called" in {
      default.replaced.predicate must_== Activity.Predicate.Replaced
    }
    "when balanced method is called" in {
      default.balanced.predicate must_== Activity.Predicate.BalancedAccounts
    }
    "when confirmed method is called" in {
      default.confirmed.predicate must_== Activity.Predicate.Confirmed
    }
    "when approved method is called" in {
      default.approved.predicate must_== Activity.Predicate.Approved
    }
    "when rejected method is called" in {
      default.rejected.predicate must_== Activity.Predicate.Rejected
    }
    "when sent method is called" in {
      default.sent.predicate must_== Activity.Predicate.Sent
    }
    "when connected method is called" in {
      default.connected.predicate must_== Activity.Predicate.Connected
    }
    "when disconnected method is called" in {
      default.disconnected.predicate must_== Activity.Predicate.Disconnected
    }
    "when uploadedSign method is called" in {
      default.uploadedSign.predicate must_== Activity.Predicate.UploadedSign
    }
    "when deletedSign method is called" in {
      default.deletedSign.predicate must_== Activity.Predicate.DeletedSign
    }
    "when deletedImage method is called" in {
      default.deletedImage.predicate must_== Activity.Predicate.DeletedImage
    }
    "when made method is called" in {
      default.made.predicate must_== Activity.Predicate.Made
    }
    "when becameSupporter method is called" in {
      default.becameSupporter.predicate must_== Activity.Predicate.BecameSupporter
    }
  }

  private def default: Activity = Activity.create("Test",
    Activity.Predicate.None, "Test")
}