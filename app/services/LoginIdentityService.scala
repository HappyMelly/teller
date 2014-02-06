/*
 * Happy Melly Teller
 * Copyright (C) 2013, Happy Melly http://www.happymelly.com
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
import models.LoginIdentity
import play.api.{ Logger, Application }
import play.api.libs.ws.WS
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.oauth.{ RequestToken, OAuthCalculator }
import securesocial.core._
import securesocial.core.providers.{ FacebookProvider, GoogleProvider, LinkedInProvider, TwitterProvider, Token }
import scala.concurrent.Await

/**
 * Used by SecureSocial to look up and save authentication data.
 */
class LoginIdentityService(application: Application) extends UserServicePlugin(application) {

  def find(id: IdentityId) = LoginIdentity.findByUserId(id)

  def save(user: Identity) = {
    play.api.Logger.debug(s"user = $user")
    val loginIdentity = user match {
      case su: SocialUser ⇒ su.identityId.providerId match {
        case TwitterProvider.Twitter ⇒ LoginIdentity.forTwitterHandle(su, findTwitterHandle(su))
        case FacebookProvider.Facebook ⇒ LoginIdentity.forFacebookUrl(su, findFacebookUrl(su))
        case GoogleProvider.Google ⇒ LoginIdentity.forGooglePlusUrl(su, findGooglePlusUrl(su))
        case LinkedInProvider.LinkedIn ⇒ LoginIdentity.forLinkedInUrl(su, findLinkedInUrl(su))
      }
      case li: LoginIdentity ⇒ li
    }

    if (loginIdentity.userAccount.viewer) {
      LoginIdentity.save(loginIdentity)
    } else {
      Logger.info(s"Denying authentication to user (@${loginIdentity.name}}) without Viewer role")
      throw new AuthenticationException
    }
  }

  // Since we're not using username/password login, we don't need the methods below
  def findByEmailAndProvider(email: String, providerId: String) = None
  def save(token: Token) {}
  def findToken(token: String) = None
  def deleteToken(uuid: String) {}
  def deleteExpiredTokens() {}

  /**
   * Returns the Facebook profile URL for the Secure Social identity being used to log in, or throws an authentication error.
   */
  private def findFacebookUrl(identity: Identity): String = {
    assert(identity.identityId.providerId == FacebookProvider.Facebook, "Facebook identity required")
    identity.oAuth2Info.map { oAuth2Info ⇒
      val facebookId = identity.identityId.userId
      val url = FacebookProfile format (facebookId, oAuth2Info.accessToken)
      val call = WS.url(url).get()

      try {
        Await.result(call.map(response ⇒ (response.json \ Link).as[String]), IdentityProvider.secondsToWait)
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

      try {
        Await.result(call.map(response ⇒ (response.json \ URL).as[String]), IdentityProvider.secondsToWait)
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
    play.api.Logger.debug(s"identity = $identity")

    val info = identity.oAuth1Info.get
    val calculator = OAuthCalculator(SecureSocial.serviceInfoFor(identity).get.key, RequestToken(info.token, info.secret))
    val call = WS.url(LinkedInProfile).sign(calculator).get()

    try {
      Await.result(call.map(response ⇒ (response.xml \ PublicProfileUrl).text), IdentityProvider.secondsToWait)
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
    val call = WS.url(TwitterSettings).sign(calculator).get()

    try {
      Await.result(call.map(response ⇒ (response.json \ ScreenName).as[String]), IdentityProvider.secondsToWait)
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
