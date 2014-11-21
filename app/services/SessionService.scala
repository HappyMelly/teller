/*
 * Happy Melly Teller
 * Copyright (C) 2014, Happy Melly http://www.happymelly.com
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
package services

import play.api.Application
import play.api.cache.Cache
import play.api.Play.current

import securesocial.core._
import models.PermanentSession

/**
 * A very simple permanent store service for sessions. Probably it will be rewritten later
 * @param application Application
 */
class SessionService(application: Application) extends AuthenticatorStore(application) {

  def save(authenticator: Authenticator): Either[Error, Unit] = {
    PermanentSession.save(authenticator)
    Cache.set(authenticator.id, authenticator)
    Right(())
  }

  def find(id: String): Either[Error, Option[Authenticator]] = {
    Cache.getAs[Authenticator](id) match {
      case Some(auth) ⇒ Right(Option(auth))
      case None ⇒
        PermanentSession.find(id) match {
          case None ⇒ Right(None)
          case Some(auth) ⇒
            Cache.set(auth.id, auth)
            Right(Option(auth))
        }
    }
  }

  def delete(id: String): Either[Error, Unit] = {
    Cache.remove(id)
    PermanentSession.delete(id)
    Right(())
  }
}
