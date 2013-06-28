package services

import play.api.Application
import securesocial.core.{UserId, Identity, UserServicePlugin}
import securesocial.core.providers.Token
import models.{LoginIdentities, LoginIdentity}

/**
 * Used by SecureSocial to look up and save authentication data.
 * @param application
 */
class LoginIdentityService(application: Application) extends UserServicePlugin(application) {

  def find(id: UserId) = LoginIdentities.findByUserId(id)
  def save(user: Identity) = LoginIdentities.save(user)

  // Since we're not using username/password login, we don't need the methods below
  def findByEmailAndProvider(email: String, providerId: String) = None
  def save(token: Token) {}

  def findToken(token: String) = None

  def deleteToken(uuid: String) {}

  def deleteExpiredTokens() {}
}
