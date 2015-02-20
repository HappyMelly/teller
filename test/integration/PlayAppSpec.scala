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
import org.specs2.specification._
import play.api.cache.Cache
import play.api.db.slick._
import play.api.mvc.AnyContentAsEmpty
import play.api.test.{ PlaySpecification, FakeHeaders, FakeRequest, FakeApplication }
import play.api.Play
import play.api.Play.current
import play.filters.csrf.CSRF
import scala.slick.jdbc.{ StaticQuery ⇒ Q }
import scala.slick.session.Session
import securesocial.core.{ Authenticator, IdentityId }

trait PlayAppSpec extends PlaySpecification with BeforeAllAfterAll {
  sequential
  lazy val app: FakeApplication = {
    val conf = Map(
      "db.default.url" -> "jdbc:mysql://localhost/mellytest",
      "logger.play" -> "ERROR",
      "logger.application" -> "ERROR",
      "ehcacheplugin" -> "enabled")
    val withoutPlugins = List("com.github.mumoshu.play2.memcached.MemcachedPlugin",
      "services.LoginIdentityService")
    val withPlugins = List("stubs.StubLoginIdentityService")
    FakeApplication(
      additionalConfiguration = conf,
      withoutPlugins = withoutPlugins,
      additionalPlugins = withPlugins)
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

  def setupDb()
  def cleanupDb()

  /**
   * Returns a secured GET request object and sets authenticator object to cache
   *
   * @param identity Identity object
   * @param url Path
   */
  def prepareSecuredGetRequest(identity: IdentityId, url: String) = {
    val authenticator = new Authenticator("auth.1", identity,
      DateTime.now().minusHours(1),
      DateTime.now(),
      DateTime.now().plusHours(5))
    Cache.set(authenticator.id, authenticator, Authenticator.absoluteTimeoutInSeconds)
    val csrfTag = Map(CSRF.Token.RequestTag -> CSRF.SignedTokenProvider.generateToken)
    FakeRequest(GET,
      url,
      headers = FakeHeaders(),
      body = AnyContentAsEmpty,
      tags = csrfTag).withCookies(authenticator.toCookie)
  }

  /**
   * Returns a secured request object and sets authenticator object to cache
   *
   * @param identity Identity object
   * @param url Path
   */
  def prepareSecuredPostRequest(identity: IdentityId,
    url: String) = {
    val authenticator = new Authenticator("auth.1", identity,
      DateTime.now().minusHours(1),
      DateTime.now(),
      DateTime.now().plusHours(5))
    Cache.set(authenticator.id, authenticator, Authenticator.absoluteTimeoutInSeconds)
    val csrfTag = Map(CSRF.Token.RequestTag -> CSRF.SignedTokenProvider.generateToken)
    FakeRequest(POST,
      url,
      headers = FakeHeaders(),
      body = AnyContentAsEmpty,
      tags = csrfTag).withCookies(authenticator.toCookie)
  }

  /** Cleans all records from database */
  def truncateTables() = DB.withSession { implicit session: Session ⇒
    Q.updateNA("SET FOREIGN_KEY_CHECKS = 0;").execute
    Q.updateNA("TRUNCATE `BOOKING_ENTRY`").execute
    Q.updateNA("TRUNCATE `BOOKING_ENTRY_ACTIVITY`").execute
    Q.updateNA("TRUNCATE `ACCOUNT`").execute
    Q.updateNA("TRUNCATE `ACTIVITY`").execute
    Q.updateNA("TRUNCATE `ADDRESS`").execute
    Q.updateNA("TRUNCATE `BRAND`").execute
    Q.updateNA("TRUNCATE `CERTIFICATE_TEMPLATE`").execute
    Q.updateNA("TRUNCATE `CONTRIBUTION`").execute
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
import org.specs2.specification.Step

trait BeforeAllAfterAll extends Specification {
  // see http://bit.ly/11I9kFM (specs2 User Guide)
  override def map(fragments: ⇒ Fragments) = {
    Step(beforeAll) ^ fragments ^ Step(afterAll)
  }

  protected def beforeAll()
  protected def afterAll()
}
