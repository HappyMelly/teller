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
package integration

import models.Member
import org.joda.money.Money
import org.joda.money.CurrencyUnit._
import org.joda.time.{ DateTime, LocalDate }
import org.specs2.matcher.DataTables
import play.api.db.slick.DB
import play.api.Play.current
import scala.slick.jdbc.{ StaticQuery ⇒ Q }
import scala.slick.session.Session

class MemberServiceSpec extends PlayAppSpec with DataTables {
  def setupDb(): Unit = {
    add()
  }
  def cleanupDb(): Unit = DB.withSession { implicit session: Session ⇒
    Q.updateNA("TRUNCATE `MEMBER`").execute
  }

  private def add() = {
    Seq(
      (1L, false, false, Money.of(EUR, 100), LocalDate.now(), 1L),
      (2L, false, true, Money.of(EUR, 200), LocalDate.now(), 1L),
      (1L, true, false, Money.of(EUR, 50), LocalDate.now(), 1L),
      (2L, true, true, Money.of(EUR, 1000), LocalDate.now(), 1L)).foreach {
        case (objectId, person, funder, fee, since, createdBy) ⇒ {
          val member = new Member(None, objectId, person, funder, fee, since,
            existingObject = false,
            DateTime.now(), createdBy, DateTime.now(), createdBy)
          member.insert
        }
      }
  }
}
