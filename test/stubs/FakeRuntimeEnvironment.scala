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

package stubs

import models.ActiveUser
import securesocial.core.RuntimeEnvironment
import securesocial.core.providers.{ FacebookProvider, GoogleProvider, LinkedInProvider, TwitterProvider }
import securesocial.core.services.RoutesService

import scala.collection.immutable.ListMap

object FakeRuntimeEnvironment extends RuntimeEnvironment.Default[ActiveUser] {

  override lazy val routes: RoutesService = new FakeRoutesService()

  val userService: StubLoginIdentityService = new StubLoginIdentityService

  override lazy val providers = ListMap(
    include(new TwitterProvider(routes, cacheService, oauth1ClientFor(TwitterProvider.Twitter))),
    include(new FacebookProvider(routes, cacheService, oauth2ClientFor(FacebookProvider.Facebook))),
    include(new GoogleProvider(routes, cacheService, oauth2ClientFor(GoogleProvider.Google))),
    include(new LinkedInProvider(routes, cacheService, oauth1ClientFor(LinkedInProvider.LinkedIn)))
  )
}

class FakeRoutesService extends RoutesService.Default {

  override protected def valueFor(key: String, default: String) = {
    val value = conf.getString(key).getOrElse(default)
    _root_.controllers.routes.Assets.at(value)
  }

}