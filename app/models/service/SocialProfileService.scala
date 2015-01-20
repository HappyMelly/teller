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

package models.service

import models.database.SocialProfiles
import models.database.SocialProfiles._
import models.{ ProfileType, SocialProfile }
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

object SocialProfileService {

  def find(objectId: Long, objectType: ProfileType.Value): SocialProfile = DB.withSession { implicit session: Session ⇒
    Query(SocialProfiles).filter(_.objectId === objectId).filter(_.objectType === objectType).first
  }

  /**
   * Find a profile associated with Twitter
   * @param twitterHandle Twitter name
   * @return
   */
  def findByTwitter(twitterHandle: String): Option[SocialProfile] = DB.withSession { implicit session: Session ⇒
    Query(SocialProfiles).filter(_.twitterHandle === twitterHandle).firstOption
  }

  /**
   * Find a profile associated with Facebook
   * @param facebookUrl Facebook profile
   * @return
   */
  def findByFacebook(facebookUrl: String): Option[SocialProfile] = DB.withSession { implicit session: Session ⇒
    Query(SocialProfiles).filter(_.facebookUrl === facebookUrl).firstOption
  }

  /**
   * Find a profile associated with LinkedIn
   * @param linkedInUrl LinkedIn profile
   * @return
   */
  def findByLinkedin(linkedInUrl: String): Option[SocialProfile] = DB.withSession { implicit session: Session ⇒
    Query(SocialProfiles).filter(_.linkedInUrl === linkedInUrl).firstOption
  }

  /**
   * Find a profile associated with Google+
   * @param googlePlusUrl Google+ profile
   * @return
   */
  def findByGooglePlus(googlePlusUrl: String): Option[SocialProfile] = DB.withSession { implicit session: Session ⇒
    Query(SocialProfiles).filter(_.googlePlusUrl === googlePlusUrl).firstOption
  }

  def insert(socialProfile: SocialProfile): SocialProfile = DB.withSession { implicit session: Session ⇒
    SocialProfiles.insert(socialProfile)
    socialProfile
  }

  def update(socialProfile: SocialProfile, objectType: ProfileType.Value): Unit = DB.withSession {
    implicit session: Session ⇒
      SocialProfiles
        .filter(_.objectId === socialProfile.objectId)
        .filter(_.objectType === objectType).update(socialProfile)
  }

  /**
   * Delete a social profile
   *
   * @param objectId Id of the object the social profile associated with
   * @param objectType Type of the object the social profile associated with
   */
  def delete(objectId: Long, objectType: ProfileType.Value): Unit = DB.withSession { implicit session: Session ⇒
    Query(SocialProfiles).filter(_.objectId === objectId).filter(_.objectType === objectType).delete
  }

}

