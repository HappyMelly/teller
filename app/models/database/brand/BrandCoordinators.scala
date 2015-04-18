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
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */

package models.database.brand

import models.brand.{ BrandNotifications, BrandCoordinator }
import play.api.db.slick.Config.driver.simple._

/**
 * Database table mapping for the association between brand and team members
 */
private[models] object BrandCoordinators extends Table[BrandCoordinator]("BRAND_COORDINATOR") {

  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def brandId = column[Long]("BRAND_ID")
  def personId = column[Long]("PERSON_ID")
  def event = column[Boolean]("EVENT")
  def evaluation = column[Boolean]("EVALUATION")
  def certificate = column[Boolean]("CERTIFICATE")

  def * = id.? ~ brandId ~ personId ~ event ~ evaluation ~
    certificate <> ({
      x ⇒ BrandCoordinator(x._1, x._2, x._3, BrandNotifications(x._4, x._5, x._6))
    }, { (x: BrandCoordinator) ⇒
      Some(x.id, x.brandId, x.personId,
        x.notification.event, x.notification.evaluation, x.notification.certificate)
    })

  def forInsert = * returning id
  def forUpdate = event ~ evaluation ~ certificate
}
