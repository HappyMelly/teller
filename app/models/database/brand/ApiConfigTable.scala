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

package models.database.brand

import models.cm.brand.ApiConfig
import slick.driver.JdbcProfile

private[models] trait ApiConfigTable {

  protected val driver: JdbcProfile
  import driver.api._

  /**
    * Connects ApiConfig object with its database representation
    */
  class ApiConfigs(tag: Tag) extends Table[ApiConfig](tag, "API_CONFIG") {

    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def brandId = column[Long]("BRAND_ID")
    def token = column[String]("TOKEN", O.Length(64, varying = false))
    def readWrite = column[Boolean]("WRITE_CALLS")
    def active = column[Boolean]("ACTIVE")
    def event = column[Option[String]]("EVENT")
    def facilitator = column[Option[String]]("FACILITATOR")
    def generalEvaluation = column[Option[String]]("GENERAL_EVALUATION_FORM")
    def specificEventEvaluation = column[Option[String]]("SPECIFIC_EVENT_EVALUATION_FORM")

    def * = (id.?, brandId, token, readWrite, active, event, facilitator, generalEvaluation,
      specificEventEvaluation) <>((ApiConfig.apply _).tupled, ApiConfig.unapply)

    def forUpdate = (event, facilitator, generalEvaluation, specificEventEvaluation)
  }

}
