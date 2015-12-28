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

package models.database

import models.Facilitator
import play.api.db.slick.Config.driver.simple._

/**
 * `Facilitator` database table mapping
 */
private[models] class Facilitators(tag: Tag) extends Table[Facilitator](tag, "FACILITATOR") {

  def id = column[Option[Long]]("ID", O.PrimaryKey, O.AutoInc)
  def personId = column[Long]("PERSON_ID")
  def brandId = column[Long]("BRAND_ID")
  def yearsOfExperience = column[Int]("YEARS_OF_EXPERIENCE")
  def numberOfEvents = column[Int]("NUMBER_OF_EVENTS")
  def publicRating = column[Float]("PUBLIC_RATING", O.DBType("FLOAT(6,2)"))
  def privateRating = column[Float]("PRIVATE_RATING", O.DBType("FLOAT(6,2)"))
  def publicMedian = column[Float]("PUBLIC_MEDIAN", O.DBType("FLOAT(6,2)"))
  def privateMedian = column[Float]("PRIVATE_MEDIAN", O.DBType("FLOAT(6,2)"))
  def publicNps = column[Float]("PUBLIC_NPS", O.DBType("FLOAT(6,2)"))
  def privateNps = column[Float]("PRIVATE_NPS", O.DBType("FLOAT(6,2)"))
  def numberOfPublicEvaluations = column[Int]("NUMBER_OF_PUBLIC_EVALUATIONS")
  def numberOfPrivateEvaluations = column[Int]("NUMBER_OF_PRIVATE_EVALUATIONS")
  def badges = column[Option[String]]("BADGES")

  def person = foreignKey("FACILITATOR_PERSON_FK", personId, TableQuery[People])(_.id)
  def brand = foreignKey("FACILITATOR_BRAND_FK", brandId, TableQuery[Brands])(_.id)

  type FacilitatorFields = (Option[Long], Long, Long, Int, Int, Float, Float, Float, Float, Float, Float,
    Int, Int, Option[String])

  def * = (id, personId, brandId, yearsOfExperience, numberOfEvents,
    publicRating, privateRating, publicMedian, privateMedian, publicNps,
    privateNps, numberOfPublicEvaluations, numberOfPrivateEvaluations, badges) <> (
      (f: FacilitatorFields) => Facilitator(f._1, f._2, f._3, f._4, f._5, f._6, f._7, f._8, f._9, f._10, f._11,
        f._12, f._13, f._14.map(x => x.split(",").map(_.toLong).toList).getOrElse(List[Long]())),
      (f: Facilitator) => Some((f.id, f.personId, f.brandId, f.yearsOfExperience, f.numberOfEvents, f.publicRating,
        f.privateRating, f.publicMedian, f.privateMedian, f.publicNps, f.privateNps, f.numberOfPublicEvaluations,
        f.numberOfPublicEvaluations, Option[String](f.badges.mkString(","))))
    )

  def forUpdate = (publicRating, privateRating, publicMedian, privateMedian,
    publicNps, privateNps, numberOfPublicEvaluations, numberOfPrivateEvaluations, badges)
}
