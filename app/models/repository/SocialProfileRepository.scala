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

package models.repository

import models.database.SocialProfileTable
import models.{ProfileType, SocialProfile}
import play.api.Application
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SocialProfileRepository(app: Application) extends HasDatabaseConfig[JdbcProfile]
  with SocialProfileTable {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](app)
  import driver.api._
  private val profiles = TableQuery[SocialProfiles]

  /**
   * @TEST
   * @param objectId
   * @param objectType
   * @return
   */
  def find(objectId: Long, objectType: ProfileType.Value): Future[SocialProfile] = {
    import SocialProfilesStatic._

    db.run(profiles.filter(_.objectId === objectId).filter(_.objectType === objectType).result).map(_.head)
  }

  /**
   * Find a profile associated with Twitter
   * @param twitterHandle Twitter name
   * @return
   */
  def findByTwitter(twitterHandle: String): Future[Option[SocialProfile]] =
    db.run(profiles.filter(_.twitterHandle === twitterHandle).result).map(_.headOption)

  /**
   * Find a profile associated with Facebook
   * @param facebookUrl Facebook profile
   * @return
   */
  def findByFacebook(facebookUrl: String): Future[Option[SocialProfile]] =
    db.run(profiles.filter(_.facebookUrl === facebookUrl).result).map(_.headOption)

  /**
   * Find a profile associated with LinkedIn
   * @param linkedInUrl LinkedIn profile
   * @return
   */
  def findByLinkedin(linkedInUrl: String): Future[Option[SocialProfile]] =
    db.run(profiles.filter(_.linkedInUrl === linkedInUrl).result).map(_.headOption)

  /**
   * Find a profile associated with Google+
   * @param googlePlusUrl Google+ profile
   * @return
   */
  def findByGooglePlus(googlePlusUrl: String): Future[Option[SocialProfile]] =
    db.run(profiles.filter(_.googlePlusUrl === googlePlusUrl).result).map(_.headOption)

  /**
   * Returns the social profile which has a duplicate social network
   * identity, if there is one
   *
   * @param profile Social profile object
   */
  def findDuplicate(profile: SocialProfile): Future[Option[SocialProfile]] = {
    import SocialProfilesStatic._

    val query = profiles.
      filter(_.objectId =!= profile.objectId).
      filter(_.objectType === profile.objectType).
      filter { p ⇒
        p.twitterHandle.toLowerCase === profile.twitterHandle.map(_.toLowerCase) ||
          p.googlePlusUrl === profile.googlePlusUrl ||
          (p.facebookUrl like "https?".r.replaceFirstIn(profile.facebookUrl.getOrElse(""), "%")) ||
          (p.linkedInUrl like "https?".r.replaceFirstIn(profile.linkedInUrl.getOrElse(""), "%"))
      }
    db.run(query.result).map(_.headOption)
  }

  def insert(socialProfile: SocialProfile): Future[SocialProfile] =
    db.run(profiles += socialProfile).map(_ => socialProfile)

  def update(socialProfile: SocialProfile, objectType: ProfileType.Value): Future[Int] = {
    import SocialProfilesStatic._

    val query = profiles.filter(_.objectId === socialProfile.objectId).filter(_.objectType === objectType)
    db.run(query.update(socialProfile))
  }

  /**
   * Delete a social profile
   *
   * @param objectId Id of the object the social profile associated with
   * @param objectType Type of the object the social profile associated with
   */
  def delete(objectId: Long, objectType: ProfileType.Value): Unit = {
    import SocialProfilesStatic._

    db.run(profiles.filter(_.objectId === objectId).filter(_.objectType === objectType).delete)
  }

  /**
   * Inserts social profile into database
   *
   * Requires session object so it can be used inside withTransaction
   * @param profile Profile object
   */
  def _insert(profile: SocialProfile): SocialProfile = {
    profiles += profile
    profile
  }
}