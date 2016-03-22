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
package cron.cleaners

import models.repository.Repositories
import play.api.Logger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

/**
  * Removes expired email and password tokens from database
  */
class TokenCleaner(repos: Repositories) {

  def clean() = {
    Logger.info(msg("start"))
    val request = for {
      p <- repos.mailToken.deleteExpiredTokens()
      e <- repos.emailToken.deleteExpiredTokens()
    } yield (p, e)
    request onComplete {
      case Success((passwordTokens, emailTokens)) =>
        Logger.info(msg(s"$passwordTokens password and $emailTokens email tokens were deleted"))
        Logger.info(msg("end"))
      case Failure(t) =>
        Logger.error(msg(t.getMessage))
        Logger.info(msg("end"))
    }
  }

  protected def msg(str: String): String = s"TokenCleaner: $str"
}
