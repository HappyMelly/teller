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

import controllers.{AuthorisationHandler, Security}
import helpers.PersonHelper
import models._
import org.joda.time.DateTime
import play.api.mvc.{Action, AnyContent, Request, Result}
import securesocial.core.authenticator.Authenticator

import scala.concurrent.Future

case class FakeAuthenticator(user: ActiveUser) extends Authenticator[ActiveUser] {

  /**
   * An id for this authenticator
   */
  val id: String = "fake"

  /**
   * The creation time
   */
  val creationDate: DateTime = DateTime.now()

  /**
   * The last used time
   */
  val lastUsed: DateTime = DateTime.now()

  /**
   * The expiration date
   */
  val expirationDate: DateTime = DateTime.now().plusHours(1)

  /**
   * Checks if this authenticator is valid.
   *
   * @return true if the authenticator is valid, false otherwise
   */
  def isValid: Boolean = true

  /**
   * Touches the authenticator. This is invoked every time a protected action is
   * executed.  Depending on the implementation this can be used to update the last time
   * used timestamp
   *
   * @return an updated instance
   */
  def touch: Future[Authenticator[ActiveUser]] = Future.successful(this)

  /**
   * Updated the user associated with this authenticator. This method can be used
   * by authenticators that store user information on the client side.
   *
   * @param user the user object
   * @return an updated instance
   */
  def updateUser(user: ActiveUser): Future[Authenticator[ActiveUser]] =
    Future.successful(this)

  /**
   * Starts an authenticator session. This is invoked when the user logs in.
   *
   * @param result the result that is about to be sent to the client
   * @return the result modified to signal a new session has been created.
   */
  def starting(result: Result): Future[Result] = Future.successful(result)

  /**
   * Ends an authenticator session.  This is invoked when the user logs out or if the
   * authenticator becomes invalid (maybe due to a timeout)
   *
   * @param result the result that is about to be sent to the client.
   * @return the result modified to signal the authenticator is no longer valid
   */
  def discarding(result: Result): Future[Result] = Future.successful(result)

  /**
   * Invoked after a protected action is executed.  This can be used to
   * alter the result in implementations that need to update the information sent to the client
   * after the authenticator is used.
   *
   * @param result the result that is about to be sent to the client.
   * @return the result modified with the updated authenticator
   */
  def touching(result: Result): Future[Result] = Future.successful(result)

  // java results
  /**
   * Invoked after a protected Java action is executed.  This can be used to
   * alter the result in implementations that need to update the information sent to the client
   * after the authenticator is used.
   *
   * @param javaContext the current http context
   * @return the http context modified with the updated authenticator
   */
  def touching(javaContext: play.mvc.Http.Context): Future[Unit] =
    Future.successful(None)

  /**
   * Ends an authenticator session.  This is invoked when the authenticator becomes invalid (for Java actions)
   *
   * @param javaContext the current http context
   * @return the current http context modified to signal the authenticator is no longer valid
   */
  def discarding(javaContext: play.mvc.Http.Context): Future[Unit] =
    Future.successful(None)
}

trait FakeSecurity extends Security {

  /** Used to replace user object which is passed to action */
  private var _activeUser: Option[Person] = None
  private var _identity: (String, String) = FakeUserIdentity.viewer

  def activeUser_=(user: Person) = _activeUser = Some(user)
  def identity_=(identity: (String, String)) = _identity = identity

  val authenticator = FakeAuthenticator(user)

  override def AsyncSecuredRestrictedAction(role: UserRole.Role.Role)(
    f: Request[AnyContent] ⇒ AuthorisationHandler ⇒ ActiveUser ⇒ Future[Result]): Action[AnyContent] = {
    Action.async { implicit req ⇒
      val handler = new AuthorisationHandler(user)
      Action.async(f(_)(handler)(user))(SecuredRequest(user, authenticator, req))
    }
  }

  override def AsyncSecuredDynamicAction(role: String, id: Long)(
    f: Request[AnyContent] ⇒ AuthorisationHandler ⇒ ActiveUser ⇒ Future[Result]): Action[AnyContent] = {
    Action.async { implicit req ⇒
      val handler = new AuthorisationHandler(user)
      Action.async(f(_)(handler)(user))(SecuredRequest(user, authenticator, req))
    }
  }

  override def SecuredBrandAction(brandId: Long)(
    f: Request[AnyContent] ⇒ AuthorisationHandler ⇒ ActiveUser => Result): Action[AnyContent] = {
    Action.async { implicit req ⇒
      val handler = new AuthorisationHandler(user)
      Action(f(_)(handler)(user))(SecuredRequest(user, authenticator, req))
    }
  }

//  override def AsyncSecuredEventAction(role: UserRole.Role.Role, eventId: Long)(
//    f: Request[AnyContent] ⇒ AuthorisationHandler ⇒ ActiveUser ⇒ models.Event => Future[Result]): Action[AnyContent] = {
//    Action.async { implicit req ⇒
//      val handler = new AuthorisationHandler(user)
//      val event = Event(Some(eventId), 1, 1, "ets", Language("EN", None, None), )
//      Action(f(_)(handler)(user))(SecuredRequest(user, authenticator, req))
//    }
//  }

  override def SecuredProfileAction(personId: Long)(
    f: Request[AnyContent] ⇒ AuthorisationHandler ⇒ ActiveUser => Result): Action[AnyContent] = {
    Action.async { implicit req ⇒
      val handler = new AuthorisationHandler(user)
      Action(f(_)(handler)(user))(SecuredRequest(user, authenticator, req))
    }
  }

  override def SecuredRestrictedAction(role: UserRole.Role.Role)(
    f: Request[AnyContent] ⇒ AuthorisationHandler ⇒ ActiveUser ⇒ Result): Action[AnyContent] = {
    Action.async { implicit req ⇒
      val handler = new AuthorisationHandler(user)
      Action(f(_)(handler)(user))(SecuredRequest(user, authenticator, req))
    }
  }

  override def SecuredRestrictedAction(roles: List[UserRole.Role.Role])(
    f: Request[AnyContent] ⇒ AuthorisationHandler ⇒ ActiveUser ⇒ Result): Action[AnyContent] = {
    Action.async { implicit req ⇒
      val handler = new AuthorisationHandler(user)
      Action(f(_)(handler)(user))(SecuredRequest(user, authenticator, req))
    }
  }

  private def user: ActiveUser = {
    val identity = new FakeUserIdentity(Some(123213L),
      _identity, "Sergey", "Kotlov", "Sergey Kotlov", None)
    val account = _identity match {
      case FakeUserIdentity.unregistered ⇒
        UserAccount(Some(1L), 1L, None, None, None, None)
      case FakeUserIdentity.admin ⇒
        UserAccount(Some(1L), 1L, None, None, None, None, admin = true, registered = true)
      case FakeUserIdentity.coordinator ⇒
        UserAccount(Some(1L), 1L, None, None, None, None, coordinator = true, registered = true, activeRole = true)
      case FakeUserIdentity.facilitator ⇒
        UserAccount(Some(1L), 1L, None, None, None, None, facilitator = true, registered = true)
      case _ ⇒
        UserAccount(Some(1L), 1L, None, None, None, None, registered = true)
    }
    val person = _activeUser getOrElse PersonHelper.one()
    ActiveUser(identity, account, person)
  }
}

/**
 * This trait is used only to record which security actions were called.
 */
trait AccessCheckSecurity extends Security {

  var checkedRole: Option[UserRole.Role.Role] = None
  var checkedRoles: List[UserRole.Role.Role] = List()
  var checkedDynamicRole: Option[String] = None
  var checkedObjectId: Option[Long] = None
  var checkedMethod: Option[String] = None

  override def AsyncSecuredDynamicAction(role: String, id: Long)(
    f: Request[AnyContent] ⇒ AuthorisationHandler ⇒ ActiveUser ⇒ Future[Result]): Action[AnyContent] = {
    cleanTrace()
    checkedDynamicRole = Some(role)
    checkedObjectId = Some(id)
    Action({ Ok("") })
  }

  override def AsyncSecuredRestrictedAction(role: UserRole.Role.Role)(
    f: Request[AnyContent] ⇒ AuthorisationHandler ⇒ ActiveUser ⇒ Future[Result]): Action[AnyContent] = {
    cleanTrace()
    checkedRole = Some(role)
    Action({ Ok("") })
  }

  override def AsyncSecuredEventAction(roles: List[UserRole.Role.Role], eventId: Long)(
    f: Request[AnyContent] ⇒ AuthorisationHandler ⇒ ActiveUser ⇒ models.Event => Future[Result]): Action[AnyContent] = {
    cleanTrace()
    checkedRoles = roles
    checkedObjectId = Some(eventId)
    Action({ Ok("") })
  }

  override def AsyncSecuredProfileAction(personId: Long)(
    f: Request[AnyContent] ⇒ AuthorisationHandler ⇒ ActiveUser => Future[Result]): Action[AnyContent] = {
    cleanTrace()
    checkedObjectId = Some(personId)
    checkedMethod = Some("profile")
    Action({ Ok("") })
  }

  override def SecuredBrandAction(brandId: Long)(
    f: Request[AnyContent] ⇒ AuthorisationHandler ⇒ ActiveUser => Result): Action[AnyContent] = {
    cleanTrace()
    checkedObjectId = Some(brandId)
    checkedMethod = Some("brand")
    Action({ Ok("") })
  }

  override def SecuredProfileAction(personId: Long)(
    f: Request[AnyContent] ⇒ AuthorisationHandler ⇒ ActiveUser => Result): Action[AnyContent] = {
    cleanTrace()
    checkedObjectId = Some(personId)
    checkedMethod = Some("profile")
    Action({ Ok("") })
  }

  override def SecuredRestrictedAction(roles: List[UserRole.Role.Role])(
    f: Request[AnyContent] ⇒ AuthorisationHandler ⇒ ActiveUser ⇒ Result): Action[AnyContent] = {
    cleanTrace()
    checkedRoles = roles
    Action({
      Ok("")
    })
  }
  override def SecuredRestrictedAction(role: UserRole.Role.Role)(
    f: Request[AnyContent] ⇒ AuthorisationHandler ⇒ ActiveUser ⇒ Result): Action[AnyContent] = {
    cleanTrace()
    checkedRole = Some(role)
    Action({ Ok("") })
  }

  /** Clean side-effects of previous calls */
  private def cleanTrace(): Unit = {
    checkedRole = None
    checkedObjectId = None
    checkedDynamicRole = None
  }
}