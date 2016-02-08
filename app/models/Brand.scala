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

package models

import models.brand.Settings
import models.service._
import play.api.libs.Crypto

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.util.Random

case class BrandWithSettings(brand: Brand, settings: Settings)

/**
 * A person, such as the owner or employee of an organisation.
 */
case class Brand(id: Option[Long],
    code: String,
    uniqueName: String,
    name: String,
    ownerId: Long,
    description: Option[String],
    picture: Option[String],
    tagLine: Option[String],
    webSite: Option[String],
    blog: Option[String],
    contactEmail: String,
    evaluationUrl: Option[String] = None,
    evaluationHookUrl: Option[String] = None,
    active: Boolean = true,
    recordInfo: DateStamp) extends ActivityRecorder {

  /**
   * Returns identifier of the object
   */
  def identifier: Long = id.getOrElse(0)

  /**
   * Returns string identifier which can be understood by human
   *
   * For example, for object 'Person' human identifier is "[FirstName] [LastName]"
   */
  def humanIdentifier: String = name

  /**
   * Returns type of this object
   */
  def objectType: String = Activity.Type.Brand
}

case class BrandView(brand: Brand, coordinator: Person, licenses: Seq[Long])

object Brand {

  def cacheId(code: String): String = "brands." + code

  def generateImageName(filename: String): String = "brands/" + Crypto.sign("%s-%s".format(filename, Random.nextInt())) + ".png"
}

