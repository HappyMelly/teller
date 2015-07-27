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
 * or in writing Happy Melly One, Handelsplein 37, Rotterdam,
 * The Netherlands, 3071 PR
 */
package integration

import org.joda.time.DateTime
import org.specs2.mutable._
import org.specs2.specification.Fragments
import play.api.cache.Cache
import play.api.db.slick._
import play.api.mvc.AnyContentAsEmpty
import play.api.test.{ PlaySpecification, FakeHeaders, FakeRequest, FakeApplication }
import play.api.Play
import play.api.Play.current
import play.filters.csrf.CSRF
import scala.slick.jdbc.{ StaticQuery ⇒ Q }
import stubs.FakeUserIdentity

trait PlayAppSpec extends PlaySpecification with BeforeAllAfterAll {
  sequential
  lazy val app: FakeApplication = {
    val conf = Map(
      "db.default.url" -> "jdbc:mysql://localhost/teller_test?reconnect=true&characterEncoding=UTF-8",
      "db.default.user" -> "root",
      "db.default.password" -> "",
      "logger.play" -> "ERROR",
      "logger.application" -> "ERROR",
      "ehcacheplugin" -> "enabled",
      "stripe.public_key" -> "none",
      "mailchimp.listId" -> "testId")
    val withoutPlugins = List("com.github.mumoshu.play2.memcached.MemcachedPlugin")
    FakeApplication(
      additionalConfiguration = conf,
      withoutPlugins = withoutPlugins)
  }

  def beforeAll() {
    Play.start(app)
    truncateTables()
    setupDb()
  }

  def afterAll() {
    cleanupDb()
    Play.stop()
  }

  def truncateTables() = PlayAppSpec.truncate()

  def setupDb() {}
  def cleanupDb() {}

  def fakeGetRequest(url: String = "") = {
    val csrfTag = Map(CSRF.Token.RequestTag -> CSRF.SignedTokenProvider.generateToken)
    FakeRequest(GET,
      url,
      headers = FakeHeaders(),
      body = AnyContentAsEmpty,
      tags = csrfTag)
  }

  def fakePostRequest(url: String = "") = {
    val csrfTag = Map(CSRF.Token.RequestTag -> CSRF.SignedTokenProvider.generateToken)
    FakeRequest(POST,
      url,
      headers = FakeHeaders(),
      body = AnyContentAsEmpty,
      tags = csrfTag)
  }

  def fakeDeleteRequest(url: String = "") = {
    val csrfTag = Map(CSRF.Token.RequestTag -> CSRF.SignedTokenProvider.generateToken)
    FakeRequest(DELETE,
      url,
      headers = FakeHeaders(),
      body = AnyContentAsEmpty,
      tags = csrfTag)
  }
}
import org.specs2.specification.Step

trait BeforeAllAfterAll extends Specification {
  // see http://bit.ly/11I9kFM (specs2 User Guide)
  override def map(fragments: ⇒ Fragments) = {
    Step(beforeAll) ^ fragments ^ Step(afterAll)
  }

  protected def beforeAll()
  protected def afterAll()
}

trait TruncateBefore extends Before {
  def before = {
    PlayAppSpec.truncate()
  }
}

object PlayAppSpec {

  /** Cleans all records from database */
  def truncate() = DB.withSession { implicit session ⇒
    Q.updateNA("SET FOREIGN_KEY_CHECKS = 0;").execute
    Q.updateNA("TRUNCATE `ACCOUNT`").execute
    Q.updateNA("TRUNCATE `ACTIVITY`").execute
    Q.updateNA("TRUNCATE `ADDRESS`").execute
    Q.updateNA("TRUNCATE `API_TOKEN`").execute
    Q.updateNA("TRUNCATE `BOOKING_ENTRY`").execute
    Q.updateNA("TRUNCATE `BOOKING_ENTRY_ACTIVITY`").execute
    Q.updateNA("TRUNCATE `BRAND`").execute
    Q.updateNA("TRUNCATE `BRAND_FEE`").execute
    Q.updateNA("TRUNCATE `BRAND_COORDINATOR`").execute
    Q.updateNA("TRUNCATE `BRAND_LINK`").execute
    Q.updateNA("TRUNCATE `BRAND_TESTIMONIAL`").execute
    Q.updateNA("TRUNCATE `CERTIFICATE_TEMPLATE`").execute
    Q.updateNA("TRUNCATE `CONTRIBUTION`").execute
    Q.updateNA("TRUNCATE `ENDORSEMENT`").execute
    Q.updateNA("TRUNCATE `EVALUATION`").execute
    Q.updateNA("TRUNCATE `EVALUATION_IMPRESSION`").execute
    Q.updateNA("TRUNCATE `EVALUATION_QUESTION`").execute
    Q.updateNA("TRUNCATE `EVALUATION_RECOMMENDATION`").execute
    Q.updateNA("TRUNCATE `EVENT`").execute
    Q.updateNA("TRUNCATE `EVENT_FACILITATOR`").execute
    Q.updateNA("TRUNCATE `EVENT_INVOICE`").execute
    Q.updateNA("TRUNCATE `EVENT_PARTICIPANT`").execute
    Q.updateNA("TRUNCATE `EVENT_TYPE`").execute
    Q.updateNA("TRUNCATE `EXCHANGE_RATE`").execute
    Q.updateNA("TRUNCATE `EXPERIENCE`").execute
    Q.updateNA("TRUNCATE `EXPERIMENT`").execute
    Q.updateNA("TRUNCATE `FACILITATOR`").execute
    Q.updateNA("TRUNCATE `FACILITATOR_COUNTRY`").execute
    Q.updateNA("TRUNCATE `FACILITATOR_LANGUAGE`").execute
    Q.updateNA("TRUNCATE `LICENSE`").execute
    Q.updateNA("TRUNCATE `LOGIN_IDENTITY`").execute
    Q.updateNA("TRUNCATE `MEMBER`").execute
    Q.updateNA("TRUNCATE `ORGANISATION`").execute
    Q.updateNA("TRUNCATE `ORGANISATION_MEMBERSHIPS`").execute
    Q.updateNA("TRUNCATE `PAYMENT_RECORD`").execute
    Q.updateNA("TRUNCATE `PERMANENT_SESSION`").execute
    Q.updateNA("TRUNCATE `PERSON`").execute
    Q.updateNA("TRUNCATE `PRODUCT`").execute
    Q.updateNA("TRUNCATE `PRODUCT_BRAND_ASSOCIATION`").execute
    Q.updateNA("TRUNCATE `SOCIAL_PROFILE`").execute
    Q.updateNA("TRUNCATE `TRANSACTION_TYPE`").execute
    Q.updateNA("TRUNCATE `USER_ACCOUNT`").execute
    Q.updateNA("SET FOREIGN_KEY_CHECKS = 1;").execute
  }
}
