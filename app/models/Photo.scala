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

case class Photo(typ: Option[String], url: Option[String], photoId: Option[String] = None)

object Photo {

  def parse(url: Option[String], photoId: Option[String]): Photo = {
    url map {
      case s if s.contains("gravatar") ⇒ Photo(Some("gravatar"), url, photoId)
      case s if s.contains("photo") ⇒ Photo(Some("custom"), url, photoId)
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
   * @param photoType One of two possible types: gravatar or nophoto
   * @param email Email address for the gravatar
   */
  def apply(photoType: String, email: String): Photo = {
    photoType match {
      case "gravatar" ⇒ Photo(Some("gravatar"), Some(gravatarUrl(email)))
      case _ ⇒ Photo.empty
    }
  }
}