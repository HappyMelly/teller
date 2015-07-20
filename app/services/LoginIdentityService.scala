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

import _root_.java.util.concurrent.TimeUnit

import models._
import models.service.Services
import play.api.Logger
import play.api.libs.json.JsObject
import securesocial.core._
import securesocial.core.providers._
import securesocial.core.services._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{ Await, Future }

/**
 * Used by SecureSocial to look up and save authentication data.
 */
class LoginIdentityService extends UserService[ActiveUser] with Services {

  /**
   * Returns login identity if it exists, otherwise - None
   * @param providerId Provider type
   * @param userId User identifier from a social network
   * @return
   */
  def find(providerId: String, userId: String): Future[Option[BasicProfile]] = {
    userIdentityService.findByUserId(userId, providerId) map { identity ⇒
      Future.successful(Some(identity.profile))
    } getOrElse Future.successful(None)
  }

  /**
   * Saves a profile. This method gets called when a user logs in, registers or changes his password.
   * This is your chance to save the user information in your backing store.
   *
   * @param profile the user profile
   * @param mode a mode that tells you why the save method was called
   */
  def save(profile: BasicProfile, mode: SaveMode): Future[ActiveUser] = {
    val user = mode match {
      case SaveMode.LoggedIn ⇒ retrieveLoggedInUser(profile)
      case SaveMode.SignUp ⇒ createUser(profile)
    }
    Future.successful(user)
  }

  // Since we're not using username/password login, we don't need the methods below
  def findByEmailAndProvider(email: String, providerId: String) = ???
  def saveToken(token: MailToken): Future[MailToken] = ???
  def findToken(token: String) = ???
  def deleteToken(uuid: String): Future[Option[MailToken]] = ???
  def deleteExpiredTokens() {}

  /**
   * Links the current user to another profile
   *
   * @param current The current user instance
   * @param to the profile that needs to be linked to
   */
  def link(current: ActiveUser, to: BasicProfile): Future[ActiveUser] =
    Future.successful(current)

  /**
   * Returns an optional PasswordInfo instance for a given user
   *
   * @param user a user instance
   * @return returns an optional PasswordInfo
   */
  def passwordInfoFor(user: ActiveUser): Future[Option[PasswordInfo]] =
    Future.successful(None)

  /**
   * Updates the PasswordInfo for a given user
   *
   * @param user a user instance
   * @param info the password info
   * @return
   */
  def updatePasswordInfo(user: ActiveUser, info: PasswordInfo): Future[Option[BasicProfile]] =
    Future.successful(None)

  /**
   * Creates new user and adds its data to database
   * @param profile User profile
   * @return Created user
   */
  protected def createUser(profile: BasicProfile): ActiveUser = {
    val identity = identityFromProfile(profile)
    userIdentityService.findActiveUserData(identity) map { userData ⇒
      userIdentityService.insert(identity)
      ActiveUser(identity, userData._1, userData._2)
    } getOrElse unregisteredActiveUser(identity)
  }

  /**
   * Returns newly created identity for the given profile
   * @param profile User profile
   */
  protected def identityFromProfile(profile: BasicProfile): UserIdentity = profile.providerId match {
    case FacebookProvider.Facebook ⇒ UserIdentity.forFacebookUrl(profile, findFacebookUrl(profile))
    case GoogleProvider.Google ⇒ UserIdentity.forGooglePlusUrl(profile, findGooglePlusUrl(profile))
    case LinkedInProvider.LinkedIn ⇒ UserIdentity.forLinkedInUrl(profile, findLinkedInUrl(profile))
    case TwitterProvider.Twitter ⇒ UserIdentity.forTwitterHandle(profile, findTwitterHandle(profile))
  }

  /**
   * Returns active user for the given profile
   * @param profile User profile
   * @throws AuthenticationException
   */
  protected def retrieveLoggedInUser(profile: BasicProfile): ActiveUser = {
    userIdentityService.findActiveUser(profile.userId, profile.providerId) getOrElse {
      throw new AuthenticationException
    }
  }

  /**
   * Returns active user with unregistered role for the given identity
   * @param identity User identity
   */
  protected def unregisteredActiveUser(identity: UserIdentity): ActiveUser = {
    val account = new UserAccount(None, 0, "", None, None, None, None)
    account.roles_=(List(UserRole.forName(UserRole.Role.Unregistered.toString)))
    val person = Person(identity.profile.firstName.getOrElse(""),
      identity.profile.lastName.getOrElse(""))
    ActiveUser(identity, account, person)
  }

  /**
   * Returns the Facebook profile URL for the Secure Social identity being used to log in, or throws an authentication error.
   */
  private def findFacebookUrl(profile: BasicProfile): String = {
    assert(profile.providerId == FacebookProvider.Facebook, "Facebook identity required")
    profile.oAuth2Info.map { info ⇒
      val client = new OAuth2Client.Default(new HttpService.Default,
        OAuth2Settings.forProvider(FacebookProvider.Facebook))
      val accessToken = info.accessToken
      val response = client.retrieveProfile(LoginIdentityService.FacebookProfile + accessToken).map { me ⇒
        (me \ "error").asOpt[JsObject] match {
          case Some(error) ⇒
            val message = (error \ "message").as[String]
            val errorType = (error \ "type").as[String]
            Logger.error(
              "[securesocial] error retrieving profile information from Facebook. Error type: %s, message: %s".
                format(errorType, message)
            )
            throw new AuthenticationException()
          case _ ⇒
            (me \ LoginIdentityService.Link).as[String]
        }
      } recover {
        case e: AuthenticationException ⇒ throw e
        case e ⇒
          Logger.error("[securesocial] error retrieving profile information from Facebook", e)
          throw new AuthenticationException()
      }
      Await.result(response, Duration.create(5, TimeUnit.SECONDS))
    }.getOrElse {
      Logger.error("Missing Facebook OAuth2 information")
      throw new AuthenticationException()
    }
  }

  /**
   * Returns the Google+ profile URL for the Secure Social identity being used to log in, or throws an authentication error.
   */
  private def findGooglePlusUrl(profile: BasicProfile): String = {
    assert(profile.providerId == GoogleProvider.Google, "Google identity required")
    profile.oAuth2Info.map { info ⇒
      val client = new OAuth2Client.Default(new HttpService.Default,
        OAuth2Settings.forProvider(GoogleProvider.Google))
      val accessToken = info.accessToken
      val response = client.retrieveProfile(LoginIdentityService.GoogleProfile + accessToken).map { me ⇒
        (me \ "error").asOpt[JsObject] match {
          case Some(error) ⇒
            val message = (error \ "message").as[String]
            val errorCode = (error \ "code").as[String]
            Logger.error(s"[securesocial] error retrieving profile information from Google. Error type = $errorCode, message = $message")
            throw new AuthenticationException()
          case _ ⇒
            (me \ LoginIdentityService.URL).as[String]
        }
      } recover {
        case e: AuthenticationException ⇒ throw e
        case e ⇒
          Logger.error("[securesocial] error retrieving profile information from Google", e)
          throw new AuthenticationException()
      }
      Await.result(response, Duration.create(5, TimeUnit.SECONDS))
    }.getOrElse {
      Logger.error("Missing Google OAuth2 information")
      throw new AuthenticationException()
    }
  }

  /**
   * Returns the LinkedIn profile URL for the Secure Social identity being used to log in, or throws an authentication error.
   */
  private def findLinkedInUrl(profile: BasicProfile): String = {
    assert(profile.providerId == LinkedInProvider.LinkedIn, "LinkedIn identity required")
    val info = profile.oAuth1Info.get
    val client = new OAuth1Client.Default(ServiceInfoHelper.forProvider(LinkedInProvider.LinkedIn),
      new HttpService.Default)
    val response = client.retrieveProfile(LoginIdentityService.LinkedInProfile, info).map { me ⇒
      (me \ LinkedInProvider.ErrorCode).asOpt[Int] match {
        case Some(error) ⇒ {
          val message = (me \ LinkedInProvider.Message).asOpt[String]
          val requestId = (me \ LinkedInProvider.RequestId).asOpt[String]
          val timestamp = (me \ LinkedInProvider.Timestamp).asOpt[String]
          Logger.error(
            s"Error retrieving information from LinkedIn. Error code: $error, requestId: $requestId, message: $message, timestamp: $timestamp"
          )
          throw new AuthenticationException()
        }
        case _ ⇒
          (me \ LoginIdentityService.PublicProfileUrl).as[String]
      }
    } recover {
      case e: AuthenticationException ⇒ throw e
      case e ⇒
        Logger.error("[securesocial] error retrieving profile information from LinkedIn", e)
        throw new AuthenticationException()
    }
    Await.result(response, Duration.create(5, TimeUnit.SECONDS))
  }

  /**
   * Returns the Twitter handle for the Secure Social identity being used to log in, or throws an authentication error.
   */
  private def findTwitterHandle(profile: BasicProfile): String = {
    assert(profile.providerId == TwitterProvider.Twitter, "Twitter identity required")
    val info = profile.oAuth1Info.get
    val client = new OAuth1Client.Default(ServiceInfoHelper.forProvider(TwitterProvider.Twitter),
      new HttpService.Default)
    val response = client.retrieveProfile(TwitterProvider.VerifyCredentials, info).map { me ⇒
      (me \ LoginIdentityService.ScreenName).as[String]
    } recover {
      case e ⇒
        Logger.error("[securesocial] error retrieving profile information from Twitter", e)
        throw new AuthenticationException()
    }
    Await.result(response, Duration.create(5, TimeUnit.SECONDS))
  }
}

object LoginIdentityService {

  val TwitterSettings = "https://api.twitter.com/1.1/account/settings.json"
  val ScreenName = "screen_name"

  val FacebookProfile = "https://graph.facebook.com/me?fields=link&return_ssl_resources=1&access_token="
  val Link = "link"

  val GoogleProfile = "https://www.googleapis.com/plus/v1/people/me?fields=url&access_token="
  val URL = "url"

  val LinkedInProfile = "http://api.linkedin.com/v1/people/~:(public-profile-url)?format=json"
  val PublicProfileUrl = "publicProfileUrl"
}
