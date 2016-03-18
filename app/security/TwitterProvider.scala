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

package security

import securesocial.core.{AuthenticationException, BasicProfile, OAuth1Info}
import securesocial.core.services.{CacheService, HttpService, RoutesService}
import security.TwitterProvider._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * A Twitter Provider
  */
class TwitterProvider(routesService: RoutesService,
                      cacheService: CacheService,
                      client: OAuth1Client) extends OAuth1Provider(
  routesService,
  cacheService,
  client
) {
  override val id = TwitterProvider.Twitter

  override def fillProfile(info: OAuth1Info): Future[BasicProfile] = {
    logger.debug("fill profile method")
    client.retrieveProfile(TwitterProvider.VerifyCredentials, info).map { me =>
      val userId = (me \ Id).as[String]
      val name = (me \ Name).asOpt[String]
      val avatar = (me \ ProfileImage).asOpt[String]
      BasicProfile(id, userId, None, None, name, None, avatar, authMethod, Some(info))
    } recover {
      case e =>
        logger.error("[securesocial] error retrieving profile information from Twitter", e)
        throw new AuthenticationException()
    }
  }
}

object TwitterProvider {
  val VerifyCredentials = "https://api.twitter.com/1.1/account/verify_credentials.json"
  val Twitter = "twitter"
  val Id = "id_str"
  val Name = "name"
  val ProfileImage = "profile_image_url_https"

  def authClient(httpService: HttpService) = new OAuth1Client.Default(ServiceInfoHelper.forProvider(Twitter), httpService)
}

