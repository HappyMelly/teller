package controllers

import models.{ UserAccount, LoginIdentity, UserRole }
import securesocial.core.{ SecureSocial, SecuredRequest }
import play.api.mvc.{ Action, Result, AnyContent }
import be.objectify.deadbolt.scala.DeadboltActions

/**
 * Integrates SecureSocial authentication with Deadbolt.
 */
trait Security extends SecureSocial with DeadboltActions {

  /**
   * Defines an action that authenticates using SecureSocial, and uses Deadbolt to restrict access to the given role.
   */
  def SecuredRestrictedAction(role: UserRole.Role.Role)(f: SecuredRequest[AnyContent] ⇒ AuthorisationHandler ⇒ Result): Action[AnyContent] = {
    SecuredAction { request ⇒

      // Look-up the authenticated user’s account details.
      val twitterHandle = request.user.asInstanceOf[LoginIdentity].twitterHandle
      val account = UserAccount.findByTwitterHandle(twitterHandle)

      // Use the account details to construct a handler (to look up account role) for Deadbolt authorisation.
      val handler = new AuthorisationHandler(account)
      val restrictedAction = Restrict(Array(role.toString), handler)(SecuredAction(f(_)(handler)))
      restrictedAction(request)
    }
  }
}