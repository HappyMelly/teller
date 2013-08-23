package services

import play.api.{ Logger, Application }
import securesocial.core._
import models.{ UserAccount, LoginIdentity }
import play.api.libs.ws.WS
import LoginIdentityService._
import play.api.libs.oauth.{ RequestToken, OAuthCalculator }
import securesocial.core.IdentityId
import securesocial.core.providers.Token
import scala.concurrent.Await
import play.api.libs.concurrent.Execution.Implicits._

/**
 * Used by SecureSocial to look up and save authentication data.
 */
class LoginIdentityService(application: Application) extends UserServicePlugin(application) {

  def find(id: IdentityId) = LoginIdentity.findByUserId(id)

  def save(user: Identity) = {
    val loginIdentity = user match {
      case su: SocialUser ⇒ LoginIdentity(su)(findTwitterHandle(su))
      case li: LoginIdentity ⇒ li
    }

    UserAccount.findRoleByTwitterHandle(loginIdentity.twitterHandle).map { userRole ⇒
      if (userRole.viewer) {
        LoginIdentity.save(loginIdentity)
      } else {
        Logger.info(s"Denying authentication to Twitter user (@${loginIdentity.twitterHandle}}) without Viewer role")
        throw new AuthenticationException
      }
    }.getOrElse {
      Logger.info(s"Denying authentication to Twitter user (@${loginIdentity.twitterHandle}}) with no user account")
      throw new AuthenticationException
    }
  }

  // Since we're not using username/password login, we don't need the methods below
  def findByEmailAndProvider(email: String, providerId: String) = None
  def save(token: Token) {}

  def findToken(token: String) = None

  def deleteToken(uuid: String) {}

  def deleteExpiredTokens() {}

  private def findTwitterHandle(identity: Identity) = {
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
}
