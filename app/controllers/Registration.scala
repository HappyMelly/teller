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

package controllers

import models.{ Address, DateStamp, Photo, Person }
import models.UserRole.Role._
import models.payment.Payment
import models.service.Services
import org.joda.time.DateTime
import play.api.Play
import play.api.Play.current
import play.api.cache.Cache
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import securesocial.core.{ IdentityId, SecureSocial }

case class User(firstName: String, lastName: String, email: String, country: String)

/**
 * Contains actions for a registration process
 */
trait Registration extends Controller with Security with Services {

  private def userForm = Form(mapping(
    "firstName" -> nonEmptyText,
    "lastName" -> nonEmptyText,
    "email" -> email,
    "country" -> nonEmptyText)(User.apply)(User.unapply))

  /**
   * The authentication flow for all providers starts here.
   *
   * @param provider The id of the provider that needs to handle the call
   */
  def authenticate(provider: String) = Action { implicit request ⇒
    val session = request.session -
      SecureSocial.OriginalUrlKey +
      (SecureSocial.OriginalUrlKey -> routes.Registration.step2.url)
    val route = securesocial.controllers.routes.ProviderController.authenticate(provider)
    Redirect(route).withSession(session)
  }

  /**
   * Renders welcome page for new users
   */
  def welcome = Action { implicit request ⇒
    Ok(views.html.registration.welcome())
  }
  /**
   * Renders step 1 page of the registration process
   */
  def step1 = Action { implicit request ⇒
    Ok(views.html.registration.step1())
  }

  /**
   * Renders step 2 page of the registration process
   * @return
   */
  def step2 = SecuredRestrictedAction(Unregistered) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      Ok(views.html.registration.step2(user, userForm))
  }

  /**
   * Saves new person to cache
   */
  def savePerson = SecuredRestrictedAction(Unregistered) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      userForm.bindFromRequest.fold(
        hasErrors ⇒ {
          Ok(views.html.registration.step2(user, hasErrors))
        },
        p ⇒ {
          val id = personCacheId(user.identityId)
          Cache.set(id, p, 900)
          Redirect(routes.Registration.step3())
        })
  }
  /**
   * Renders step 3 page of the registration process
   * @return
   */
  def step3 = SecuredRestrictedAction(Unregistered) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      val publicKey = Play.configuration.getString("stripe.public_key").get
      Cache.getAs[User](personCacheId(user.identityId)) map { u ⇒
        val photo = new Photo(None, None)
        val dateStamp = new DateStamp(DateTime.now(), "", DateTime.now(), "")
        val person = new Person(None, u.firstName, u.lastName, None, photo,
          false, 0, None, None, webSite = None, blog = None,
          dateStamp = dateStamp)
        val address = new Address(countryCode = u.country)
        person.address_=(address)

        val code = u.country
        val fee = Payment.countryBasedFees(code)
        Ok(views.html.registration.step3(Membership.form, person, publicKey, fee))
      } getOrElse {
        Ok("Shit")
      }
  }

  /**
   * Returns an unique cache id for a person object of current user
   * @param id Identity object
   */
  private def personCacheId(id: IdentityId): String = {
    "user_" + id.userId
  }
}

object Registration extends Registration with Security with Services
