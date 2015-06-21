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
 * If you have questions concerning this license or the applicable additional terms, you may contact
 * by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */

package models

import scravatar.Gravatar

case class Photo(id: Option[String], url: Option[String])

object Photo {

  def parse(url: Option[String]): Photo = {
    url map {
      case s if s.contains("facebook") ⇒ Photo(Some("facebook"), url)
      case s if s.contains("gravatar") ⇒ Photo(Some("gravatar"), url)
      case s if s.contains("photo") ⇒ Photo(Some("custom"), url)
      case _ ⇒ Photo.empty
    } getOrElse Photo.empty
  }

  def empty: Photo = Photo(None, None)

  /**
   * Returns url to a Gravatar photo based on the given email
   *
   * @param email Email of interest
   */
  def gravatarUrl(email: String): String =
    Gravatar(email, ssl = true).size(300).avatarUrl

  /**
   * Returns photo object based on the given type
   *
   * @param photoType One of three possible types: facebool, gravatar, nophoto
   * @param profile Social profile containing data for building photo url
   */
  def apply(photoType: String, profile: SocialProfile): Photo = {
    photoType match {
      case "gravatar" ⇒ Photo(Some("gravatar"), Some(gravatarUrl(profile.email)))
      case _ ⇒ Photo.empty
    }
  }
}