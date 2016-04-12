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

import play.api.libs.json.{Json, JsValue}
import securesocial.core._
import securesocial.core.services.{CacheService, HttpService, RoutesService}

import scala.concurrent.{ExecutionContext, Future}

/**
 * A MailChimp provider
 */
class MailChimpProvider(routesService: RoutesService,
                        cacheService: CacheService,
                        client: MailChimpClient)
    extends OAuth2Provider(routesService, client, cacheService) {

  val GetMetaData = "https://login.mailchimp.com/oauth2/metadata"
  val Login = "login"

  override val id = MailChimpProvider.MailChimp

  def fillProfile(info: OAuth2Info): Future[BasicProfile] = {
    client.retrieveProfile(GetMetaData, "Authorization" -> s"OAuth ${info.accessToken}").map { metadata =>
      println(metadata)
      (metadata \ "dc").asOpt[String] match {
        case Some(msg) =>
          val dc = (metadata \ "dc").as[String]
          val apiEndPoint = (metadata \ "api_endpoint").as[String]
          val userId = (metadata \ "user_id").as[Long].toString
          val name = (metadata \ Login \ "login_name").asOpt[String]
          val email = (metadata \ Login \ "email").asOpt[String]
          val avatarUrl = (metadata \ Login \ "avatar").asOpt[String]
          BasicProfile(id, userId, name, None, None, email, avatarUrl, authMethod, oAuth2Info = Some(info),
            extraInfo = Some(Json.obj("dc" -> dc, "apiEndPoint" -> apiEndPoint)))
        case _ =>
          logger.error(s"[securesocial] error retrieving metadata from MailChimp. Data: $metadata")
          throw new AuthenticationException()
      }
    } recover {
      case e: AuthenticationException => throw e
      case e: Exception =>
        logger.error("[securesocial] error retrieving metadata from MailChimp", e)
        throw new AuthenticationException()
    }
  }
}

class MailChimpClient(httpService: HttpService, settings: OAuth2Settings)(implicit executionContext: ExecutionContext)
  extends OAuth2Client.Default(httpService, settings) {

  def retrieveProfile(profileUrl: String, hdrs: (String, String)*): Future[JsValue] =
    httpService.url(profileUrl).withHeaders(hdrs:_*).get().map(_.json)
}

object MailChimpProvider {
  val MailChimp = "mailchimp"

  case class ExtraInfo(dc: String, apiEndPoint: String)

  def toExtraInfo(data: Option[JsValue]): Option[ExtraInfo] = {
    implicit val extraInfoReads = Json.reads[ExtraInfo]
    data.map(_.as[ExtraInfo])
  }

}


