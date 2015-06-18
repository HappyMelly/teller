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
package controllers

import controllers.Forms._
import models.{ SocialProfile, Photo, ProfileType }
import models.UserRole.Role._
import models.service.{ SocialProfileService, Services }
import play.api.data.Forms._
import play.api.data.validation.Constraints
import play.api.data.validation.Constraints._

trait SocialProfiles extends JsonController with Security with Services {

  /**
   * Returns an url to a facebook profile photo if this profile is not used
   *
   * @param personId Person identifier
   * @param name Facebook identifier
   */
  def facebookUrl(personId: Long, name: String) = SecuredRestrictedAction(Viewer) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        val profileUrl = "https://www.facebook.com/" + name
        val profile = SocialProfile(objectId = personId, email = "dummy",
          facebookUrl = Some(profileUrl))
        socialProfileService.findDuplicate(profile) map { x ⇒
          jsonConflict("This Facebook profile is already taken")
        } getOrElse jsonSuccess(Photo.facebookUrl(profileUrl))
  }
}

object SocialProfiles extends SocialProfiles {

  /**
   * Returns html form mapping for social profile
   *
   * @param profileType Sets of which profile type mapping is
   */
  def profileMapping(profileType: ProfileType.Value) = mapping(
    "email" -> nonEmptyText,
    "twitterHandle" -> optional(text.verifying(Constraints.pattern("""[A-Za-z0-9_]{1,16}""".r, error = "error.twitter"))),
    "facebookUrl" -> optional(facebookProfileUrl),
    "linkedInUrl" -> optional(linkedInProfileUrl),
    "googlePlusUrl" -> optional(googlePlusProfileUrl),
    "skype" -> optional(nonEmptyText),
    "phone" -> optional(nonEmptyText),
    "contactForm" -> optional(webUrl))(
      {
        (email, twitterHandle, facebookUrl, linkedInUrl, googlePlusUrl, skype,
        phone, contactForm) ⇒
          SocialProfile(0, profileType, email, twitterHandle,
            facebookUrl, linkedInUrl, googlePlusUrl, skype, phone, contactForm)
      })(
        {
          (s: SocialProfile) ⇒
            Some(s.email, s.twitterHandle, s.facebookUrl,
              s.linkedInUrl, s.googlePlusUrl, s.skype, s.phone, s.contactForm)
        })
}