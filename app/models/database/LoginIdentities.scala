package models.database

import models.LoginIdentity
import play.api.db.slick.Config.driver.simple._
import securesocial.core.{ UserId, OAuth2Info, OAuth1Info, AuthenticationMethod }

/**
 * `LoginIdentity` database table mapping.
 */
private[models] object LoginIdentities extends Table[LoginIdentity]("LOGIN_IDENTITY") {

  implicit def string2AuthenticationMethod: TypeMapper[AuthenticationMethod] = MappedTypeMapper.base[AuthenticationMethod, String](
    authenticationMethod ⇒ authenticationMethod.method,
    string ⇒ AuthenticationMethod(string))

  implicit def tuple2OAuth1Info(tuple: (Option[String], Option[String])) = tuple match {
    case (Some(token), Some(secret)) ⇒ Some(OAuth1Info(token, secret))
    case _ ⇒ None
  }

  implicit def tuple2OAuth2Info(tuple: (Option[String], Option[String], Option[Int], Option[String])) = tuple match {
    case (Some(token), tokenType, expiresIn, refreshToken) ⇒ Some(OAuth2Info(token, tokenType, expiresIn, refreshToken))
    case _ ⇒ None
  }

  implicit def tuple2UserId(tuple: (String, String)) = tuple match {
    case (userId, providerId) ⇒ UserId(userId, providerId)
  }

  def uid = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def userId = column[String]("USER_ID")
  def providerId = column[String]("PROVIDER_ID")
  def email = column[Option[String]]("EMAIL")
  def firstName = column[String]("FIRST_NAME")
  def lastName = column[String]("LAST_NAME")
  def fullName = column[String]("FULL_NAME")
  def authMethod = column[AuthenticationMethod]("AUTH_METHOD")
  def avatarUrl = column[Option[String]]("AVATAR_URL")
  def secret = column[Option[String]]("SECRET")
  def tokenType = column[Option[String]]("TOKEN_TYPE")
  def expiresIn = column[Option[Int]]("EXPIRES_IN")
  def refreshToken = column[Option[String]]("REFRESH_TOKEN")
  def twitterHandle = column[String]("TWITTER_HANDLE")
  def apiToken = column[String]("API_TOKEN")

  // oAuth 1
  def token = column[Option[String]]("TOKEN")

  // oAuth 2
  def accessToken = column[Option[String]]("ACCESS_TOKEN")

  def * = uid.? ~ userId ~ providerId ~ firstName ~ lastName ~ fullName ~ email ~ avatarUrl ~ authMethod ~ token ~ secret ~ accessToken ~ tokenType ~ expiresIn ~ refreshToken ~ twitterHandle ~ apiToken <> (
    u ⇒ LoginIdentity(u._1, (u._2, u._3), u._4, u._5, u._6, u._7, u._8, u._9, (u._10, u._11), (u._12, u._13, u._14, u._15), None, u._16, u._17),
    (u: LoginIdentity) ⇒ Some((u.uid, u.id.id, u.id.providerId, u.firstName, u.lastName, u.fullName, u.email, u.avatarUrl, u.authMethod, u.oAuth1Info.map(_.token), u.oAuth1Info.map(_.secret), u.oAuth2Info.map(_.accessToken), u.oAuth2Info.flatMap(_.tokenType), u.oAuth2Info.flatMap(_.expiresIn), u.oAuth2Info.flatMap(_.refreshToken), u.twitterHandle, u.apiToken)))

  def forInsert = * returning uid

}