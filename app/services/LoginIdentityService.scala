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

package services

import LoginIdentityService._
import models.{ Person, UserRole, UserAccount, UserIdentity }
import models.service.UserIdentityService
import play.api.{ Logger, Application }
import play.api.cache.Cache
import play.api.Play.current
import play.api.libs.ws.{ Response, WS }
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.oauth.{ RequestToken, OAuthCalculator }
import securesocial.core._
import securesocial.core.providers.{ FacebookProvider, GoogleProvider, LinkedInProvider, TwitterProvider, Token }
import scala.concurrent.Await

/**
 * Used by SecureSocial to look up and save authentication data.
 */
class LoginIdentityService(application: Application) extends UserServicePlugin(application) {

  /**
   * Returns login identity if it exists, otherwise - None
   * @param id Identity identifier
   * @return
   */
  def find(id: IdentityId): Option[UserIdentity] = {
    val identity: Option[UserIdentity] = Cache.getAs[UserIdentity](cacheId(id))
    identity map { i ⇒
      val account = new UserAccount(None, 0, "", None, None, None, None)
      account.roles_=(List(UserRole.forName(UserRole.Role.Unregistered.toString)))
      i.account_=(Some(account))
      Some(i)
    } getOrElse {
      UserIdentityService.get.findByUserId(id)
    }
  }

  def save(user: Identity) = {
    val loginIdentity = user match {
      case su: SocialUser ⇒ su.identityId.providerId match {
        case TwitterProvider.Twitter ⇒ UserIdentity.forTwitterHandle(su, findTwitterHandle(su))
        case FacebookProvider.Facebook ⇒ UserIdentity.forFacebookUrl(su, findFacebookUrl(su))
        case GoogleProvider.Google ⇒ UserIdentity.forGooglePlusUrl(su, findGooglePlusUrl(su))
        case LinkedInProvider.LinkedIn ⇒ UserIdentity.forLinkedInUrl(su, findLinkedInUrl(su))
      }
      case li: UserIdentity ⇒ li
    }
    try {
      if (loginIdentity.account.viewer) {
        UserIdentity.save(loginIdentity)
      } else {
        Logger.info(s"Denying authentication to user (@${loginIdentity.name}}) without Viewer role")
        throw new AccessDeniedException
      }
    } catch {
      case e: NoSuchElementException ⇒
        val id = cacheId(loginIdentity.identityId)
        Cache.set(id, loginIdentity, 600)
        loginIdentity
    }
  }

  // Since we're not using username/password login, we don't need the methods below
  def findByEmailAndProvider(email: String, providerId: String) = None
  def save(token: Token) {}
  def findToken(token: String) = None
  def deleteToken(uuid: String) {}
  def deleteExpiredTokens() {}

  /**
   * Returns cache identifier for IdentityId object
   * @param id IdentityId object
   */
  private def cacheId(id: IdentityId): String = {
    id.providerId + "_" + id.userId
  }

  /**
   * Returns the Facebook profile URL for the Secure Social identity being used to log in, or throws an authentication error.
   */
  private def findFacebookUrl(identity: Identity): String = {
    assert(identity.identityId.providerId == FacebookProvider.Facebook, "Facebook identity required")
    identity.oAuth2Info.map { oAuth2Info ⇒
      val facebookId = identity.identityId.userId
      val url = FacebookProfile format (facebookId, oAuth2Info.accessToken)
      val call = WS.url(url).get()
      Logger.debug(s"GET $url")

      try {
        val parseResponse = (response: Response) ⇒ {
          Logger.debug(s"${response.status} ${response.statusText}\n${response.json}")
          (response.json \ Link).as[String]
        }
        Await.result(call.map(parseResponse), IdentityProvider.secondsToWait)
      } catch {
        case e: Exception ⇒ {
          Logger.error("Error retrieving Facebook profile information", e)
          throw new AuthenticationException()
        }
      }
    }.getOrElse {
      Logger.error("Missing Facebook OAuth2 information")
      throw new AuthenticationException()
    }
  }

  /**
   * Returns the Google+ profile URL for the Secure Social identity being used to log in, or throws an authentication error.
   */
  private def findGooglePlusUrl(identity: Identity): String = {
    assert(identity.identityId.providerId == GoogleProvider.Google, "Google identity required")
    identity.oAuth2Info.map { oAuth2Info ⇒
      val googleId = identity.identityId.userId
      val url = GoogleProfile format (googleId)
      val call = WS.url(url).withHeaders("Authorization" -> "Bearer %s".format(oAuth2Info.accessToken)).get()
      Logger.debug(s"GET $url")

      try {
        val parseResponse = (response: Response) ⇒ {
          Logger.debug(s"${response.status} ${response.statusText}\n${response.json}")
          (response.json \ URL).as[String]
        }
        Await.result(call.map(parseResponse), IdentityProvider.secondsToWait)
      } catch {
        case e: Exception ⇒ {
          Logger.error("Error retrieving Google profile information", e)
          throw new AuthenticationException()
        }
      }
    }.getOrElse {
      Logger.error("Missing Google OAuth2 information")
      throw new AuthenticationException()
    }
  }

  /**
   * Returns the LinkedIn profile URL for the Secure Social identity being used to log in, or throws an authentication error.
   */
  private def findLinkedInUrl(identity: Identity): String = {
    assert(identity.identityId.providerId == LinkedInProvider.LinkedIn, "LinkedIn identity required")

    val info = identity.oAuth1Info.get
    val calculator = OAuthCalculator(SecureSocial.serviceInfoFor(identity).get.key, RequestToken(info.token, info.secret))
    val call = WS.url(LinkedInProfile).sign(calculator).get()
    Logger.debug(s"GET $LinkedInProfile")

    try {
      val parseResponse = (response: Response) ⇒ {
        Logger.debug(s"${response.status} ${response.statusText}\n${response.xml}")
        (response.xml \ PublicProfileUrl).text
      }
      Await.result(call.map(parseResponse), IdentityProvider.secondsToWait)
    } catch {
      case e: Exception ⇒ {
        Logger.error("Error retrieving LinkedIn profile information", e)
        throw new AuthenticationException()
      }
    }
  }

  /**
   * Returns the Twitter handle for the Secure Social identity being used to log in, or throws an authentication error.
   */
  private def findTwitterHandle(identity: Identity): String = {
    assert(identity.identityId.providerId == TwitterProvider.Twitter, "Twitter identity required")
    val info = identity.oAuth1Info.get
    val calculator = OAuthCalculator(SecureSocial.serviceInfoFor(identity).get.key, RequestToken(info.token, info.secret))
    val call = WS.url(TwitterSettings).sign(calculator).get
    Logger.debug(s"GET $TwitterSettings")

    try {
      val parseResponse = (response: Response) ⇒ {
        Logger.debug(s"${response.status} ${response.statusText}\n${response.json}")
        (response.json \ ScreenName).as[String]
      }
      Await.result(call.map(parseResponse), IdentityProvider.secondsToWait)
    } catch {
      case e: Exception ⇒ {
        Logger.error("Error retrieving Twitter profile information", e)
        throw new AuthenticationException()
      }
    }
  }

}

object LoginIdentityService {

  val TwitterSettings = "https://api.twitter.com/1.1/account/settings.json"
  val ScreenName = "screen_name"

  val FacebookProfile = "https://graph.facebook.com/%s?access_token=%s"
  val Link = "link"

  val GoogleProfile = "https://www.googleapis.com/plus/v1/people/%s"
  val URL = "url"

  val LinkedInProfile = "http://api.linkedin.com/v1/people/~:(public-profile-url)"
  val PublicProfileUrl = "public-profile-url"
}
