/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2014, Happy Melly http://www.happymelly.com
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
 * or in writing Happy Melly One, Handelsplein 37, Rotterdam,
 * The Netherlands, 3071 PR
 */
package models.integration

import _root_.integration.PlayAppSpec
import helpers.PersonHelper
import models.{ UserIdentity, UserAccount }
import models.database.{ UserAccounts, UserIdentities }
import models.service.UserIdentityService
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import securesocial.core.{ AuthenticationMethod, IdentityId }

class UserIdentityServiceSpec extends PlayAppSpec {
  def setupDb() {}
  def cleanupDb() {}

  val twitterIdentity = new IdentityId("1", "twitter")

  "Method findByUserId" should {
    "return None if account data are not available" in {
      truncateTables()
      DB.withSession { implicit session: Session ⇒
        UserIdentities.forInsert.insert(
          user(twitterIdentity, twitter = Some("tester")))
      }

      val result = UserIdentityService.get.findByUserId(twitterIdentity)
      result must_== None
    }
    "return None if person data are not available" in {
      truncateTables()
      val account = new UserAccount(None, 1L, "viewer", Some("tester"),
        None, None, None)
      DB.withSession { implicit session: Session ⇒
        UserIdentities.forInsert.insert(
          user(twitterIdentity, twitter = Some("tester")))
        UserAccounts.forInsert.insert(account)
      }

      val result = UserIdentityService.get.findByUserId(twitterIdentity)
      result must_== None
    }
    "return identity with Twitter if all data are available" in {
      truncateTables()
      val account = new UserAccount(None, 1L, "viewer", Some("tester"),
        None, None, None)
      DB.withSession { implicit session: Session ⇒
        UserIdentities.forInsert.insert(
          user(twitterIdentity, twitter = Some("tester")))
        UserAccounts.forInsert.insert(account)
      }
      PersonHelper.one().insert

      val result = UserIdentityService.get.findByUserId(twitterIdentity)
      result map { i ⇒
        i.firstName must_== "First"
        i.lastName must_== "Tester"
        i.email must_!= None
        i.email.get must_== "t@t.com"
        i.account.twitterHandle must_== Some("tester")
        i.person.fullName must_== "First Tester"
      } getOrElse ko
    }

    "return identity with Facebook if all data are available" in {
      truncateTables()
      val facebookIdentity = new IdentityId("1", "facebook")
      val account = new UserAccount(None, 1L, "viewer", None,
        Some("tester"), None, None)
      DB.withSession { implicit session: Session ⇒
        UserIdentities.forInsert.insert(
          user(facebookIdentity, facebook = Some("tester")))
        UserAccounts.forInsert.insert(account)
      }
      PersonHelper.one().insert

      val result = UserIdentityService.get.findByUserId(facebookIdentity)
      result map { i ⇒
        i.firstName must_== "First"
        i.lastName must_== "Tester"
        i.email must_!= None
        i.email.get must_== "t@t.com"
        i.account.facebookUrl must_== Some("tester")
        i.person.fullName must_== "First Tester"
      } getOrElse ko
    }

    "return identity with Google if all data are available" in {
      truncateTables()
      val googleIdentity = new IdentityId("1", "google")
      val url = "https://plus.google.com/tester"
      val account = new UserAccount(None, 1L, "viewer", None,
        None, None, Some(url))
      println(account.googlePlusUrl.toString)
      println(account.linkedInUrl.toString)
      DB.withSession { implicit session: Session ⇒
        UserIdentities.forInsert.insert(
          user(googleIdentity, google = Some(url)))
        UserAccounts.forInsert.insert(account)
      }
      PersonHelper.one().insert

      val result = UserIdentityService.get.findByUserId(googleIdentity)
      result map { i ⇒
        i.firstName must_== "First"
        i.lastName must_== "Tester"
        i.email must_!= None
        i.email.get must_== "t@t.com"
        i.account.googlePlusUrl must_== Some(url)
        i.person.fullName must_== "First Tester"
      } getOrElse ko
    }

    "return identity with LinkedIn if all data are available" in {
      truncateTables()
      val linkedinIdentity = new IdentityId("1", "linkedin")
      val account = new UserAccount(None, 1L, "viewer", None,
        None, Some("tester"), None)
      DB.withSession { implicit session: Session ⇒
        UserIdentities.forInsert.insert(
          user(linkedinIdentity, linkedin = Some("tester")))
        UserAccounts.forInsert.insert(account)
      }
      PersonHelper.one().insert

      val result = UserIdentityService.get.findByUserId(linkedinIdentity)
      result map { i ⇒
        i.firstName must_== "First"
        i.lastName must_== "Tester"
        i.email must_!= None
        i.email.get must_== "t@t.com"
        i.account.linkedInUrl must_== Some("tester")
        i.person.fullName must_== "First Tester"
      } getOrElse ko
    }
  }

  private def user(identity: IdentityId,
    twitter: Option[String] = None,
    facebook: Option[String] = None,
    google: Option[String] = None,
    linkedin: Option[String] = None): UserIdentity = {
    new UserIdentity(None, identity, "First", "Tester",
      "First Tester", Some("t@t.com"), None, AuthenticationMethod.OAuth2,
      None, None, None, "token123", twitter, facebook, google, linkedin)
  }
}
