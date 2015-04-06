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
 * If you have questions concerning this license or the applicable additional terms, you may contact
 * by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */

package models.database.brand

import models.brand.CertificateTemplate
import play.api.db.slick.Config.driver.simple._

/**
 * `CertificateTemplates` database table mapping.
 */
private[models] object CertificateTemplates extends Table[CertificateTemplate]("CERTIFICATE_TEMPLATE") {

  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def brandId = column[Long]("BRAND_ID")
  def language = column[String]("LANGUAGE")
  def oneFacilitator = column[Array[Byte]]("ONE_FACILITATOR")
  def twoFacilitators = column[Array[Byte]]("TWO_FACILITATORS")

  def * = id.? ~ brandId ~ language ~ oneFacilitator ~
    twoFacilitators <> (CertificateTemplate.apply _, CertificateTemplate.unapply _)

  def forInsert = * returning id

}
