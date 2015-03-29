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

package models.admin

import models.{ Activity, ActivityRecorder }

/**
 * Represents API token which provides an access to Teller API
 * @param id Record identifier
 * @param token Token itself
 * @param appName Application name (wil be included into logs on WRITE reqeusts)
 * @param appDescription Application description
 * @param appWebsite Application website
 * @param readWrite If true, both read/write requests are allowed; otherwise, read only
 */
case class ApiToken(id: Option[Long],
  token: String,
  appName: String,
  appDescription: String,
  appWebsite: Option[String] = None,
  readWrite: Boolean = true) extends ActivityRecorder {

  /**
   * Returns identifier of the object
   */
  def identifier: Long = id.getOrElse(0)

  /**
   * Returns string identifier of API Token
   */
  def humanIdentifier: String = "for %s app".format(appName)

  /**
   * Returns type of this object
   */
  def objectType: String = Activity.Type.ApiToken

  /**
   * Returns true if token is authorized to run requested action
   * @param readWrite Level of rights for the authorized action
   */
  def authorized(readWrite: Boolean): Boolean = if (readWrite)
    readWrite == this.readWrite
  else
    true

}

object ApiToken {

  /**
   * Returns cache identifier for ApiToken object
   * @param token Token string identifier
   */
  def cacheId(token: String): String = "token_%s".format(token)

}
