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

import _root_.java.util.UUID

import oauth.signpost.exception.OAuthException
import org.joda.time.DateTime
import play.api.Logger
import play.api.libs.json.JsValue
import play.api.libs.oauth.{ConsumerKey, OAuth, RequestToken, ServiceInfo, _}
import play.api.mvc.Results.Redirect
import play.api.mvc.{AnyContent, Request}
import securesocial.core.services.{CacheService, HttpService, RoutesService}
import securesocial.core.{AuthenticationException, AuthenticationResult, BasicProfile, IdentityProvider, OAuth1Info}
import securesocial.core.AuthenticationMethod

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

/**
  * A trait that allows mocking the OAuth 1 client
  */
trait OAuth1Client {

  def retrieveRequestToken(callbackURL: String): Future[RequestToken]

  def retrieveOAuth1Info(token: RequestToken, verifier: String): Future[OAuth1Info]

  def redirectUrl(token: String): String

  def retrieveProfile(url: String, info: OAuth1Info): Future[JsValue]

  implicit def executionContext: ExecutionContext
}


object OAuth1Client {
  /**
    * A default implementation based on the Play client
    * @param serviceInfo
    */
  class Default(val serviceInfo: ServiceInfo, val httpService: HttpService)(implicit val executionContext: ExecutionContext) extends OAuth1Client {
    private val client = OAuth(serviceInfo, use10a = true)
    override def redirectUrl(token: String): String = client.redirectUrl(token)

    private def withFuture(call: => Either[OAuthException, RequestToken]): Future[RequestToken] = Future {
      call match {
        case Left(error) => throw error
        case Right(token) => token
      }
    }

    override def retrieveOAuth1Info(token: RequestToken, verifier: String) = withFuture {
      client.retrieveAccessToken(token, verifier)
    }.map(accessToken => OAuth1Info(accessToken.token, accessToken.secret))

    override def retrieveRequestToken(callbackURL: String) = withFuture {
      client.retrieveRequestToken(callbackURL)
    }

    override def retrieveProfile(url: String, info: OAuth1Info): Future[JsValue] =
      httpService.url(url).sign(OAuthCalculator(serviceInfo.key, RequestToken(info.token, info.secret))).get().map(_.json)
  }
}

object ServiceInfoHelper {
  import securesocial.core.IdentityProvider._

  /**
    * A helper method to create a service info from the properties file
    * @param id
    * @return
    */
  def forProvider(id: String): ServiceInfo = {
    val result = for {
      requestTokenUrl <- loadProperty(id, OAuth1Provider.RequestTokenUrl);
      accessTokenUrl <- loadProperty(id, OAuth1Provider.AccessTokenUrl);
      authorizationUrl <- loadProperty(id, OAuth1Provider.AuthorizationUrl);
      consumerKey <- loadProperty(id, OAuth1Provider.ConsumerKey);
      consumerSecret <- loadProperty(id, OAuth1Provider.ConsumerSecret)
    } yield {
      ServiceInfo(requestTokenUrl, accessTokenUrl, authorizationUrl, ConsumerKey(consumerKey, consumerSecret))
    }

    if (result.isEmpty) {
      throwMissingPropertiesException(id)
    }
    result.get

  }
}

/**
  * Base class for all OAuth1 providers
  */
abstract class OAuth1Provider(
                               routesService: RoutesService,
                               cacheService: CacheService,
                               val client: OAuth1Client)
  extends IdentityProvider {

  protected implicit val executionContext = client.executionContext
  protected val logger = play.api.Logger(this.getClass.getName)

  def authMethod = AuthenticationMethod.OAuth1

  def authenticate()(implicit request: Request[AnyContent]): Future[AuthenticationResult] = {
    if (request.queryString.get("denied").isDefined) {
      // the user did not grant access to the account
      Future.successful(AuthenticationResult.AccessDenied())
    } else {
      val sessionIdentifier = Random.nextLong()
      debug(sessionIdentifier, 134)
      val verifier = request.queryString.get("oauth_verifier").map(_.head)
      if (verifier.isEmpty) {
        // this is the 1st step in the auth flow. We need to get the request tokens
        debug(sessionIdentifier, 138)
        val callbackUrl = routesService.authenticationUrl(id)
        logger.debug("[securesocial] callback url = " + callbackUrl)
        debug(sessionIdentifier, 141)
        client.retrieveRequestToken(callbackUrl).flatMap {
          case accessToken =>
            val cacheKey = UUID.randomUUID().toString
            debug(sessionIdentifier, 145)
            val redirect = Redirect(client.redirectUrl(accessToken.token)).withSession(request.session +
              (OAuth1Provider.CacheKey -> cacheKey))
            // set the cache key timeoutfor 5 minutes, plenty of time to log in
            cacheService.set(cacheKey, accessToken, 300).map {
              u =>
                AuthenticationResult.NavigationFlow(redirect)
            }
        } recover {
          case e =>
            debug(sessionIdentifier, 155)
            logger.error("[securesocial] error retrieving request token", e)
            throw new AuthenticationException()
        }
      } else {
        // 2nd step in the oauth flow
        debug(sessionIdentifier, 161)
        val cacheKey = request.session.get(OAuth1Provider.CacheKey).getOrElse {
          debug(sessionIdentifier, 163)
          logger.error("[securesocial] missing cache key in session during OAuth1 flow")
          throw new AuthenticationException()
        }
        debug(sessionIdentifier, 167)
        for (
          requestToken <- cacheService.getAs[RequestToken](cacheKey).recover {
            case e =>
              debug(sessionIdentifier, 171)
              logger.error("[securesocial] error retrieving entry from cache", e)
              throw new AuthenticationException()
          };
          accessToken <- client.retrieveOAuth1Info(
            RequestToken(requestToken.get.token, requestToken.get.secret), verifier.get
          ).recover {
            case e =>
              debug(sessionIdentifier, 179)
              logger.error("[securesocial] error retrieving access token", e)
              throw new AuthenticationException()
          };
          result <- fillProfile(accessToken)
        ) yield {
          debug(sessionIdentifier, 185)
          AuthenticationResult.Authenticated(result)
        }
      }
    }
  }

  def fillProfile(info: OAuth1Info): Future[BasicProfile]

  protected def debug(identity: Long, line: Int) =
    Logger.info(s"[monitor] $identity: line $line, time: ${DateTime.now()}")
}

object OAuth1Provider {
  val CacheKey = "cacheKey"
  val RequestTokenUrl = "requestTokenUrl"
  val AccessTokenUrl = "accessTokenUrl"
  val AuthorizationUrl = "authorizationUrl"
  val ConsumerKey = "consumerKey"
  val ConsumerSecret = "consumerSecret"
}

