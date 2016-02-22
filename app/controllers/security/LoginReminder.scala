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
 * If you have questions concerning this license or the applicable additional terms, you may contact
 * by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */
package controllers.security

import javax.inject.Inject

import controllers.AsyncController
import models.repository.IRepositories
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, Session}
import play.filters.csrf.{CSRFAddToken, CSRFCheck}
import services.integrations.EmailComponent

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Set of methods for reminding login options
  */
class LoginReminder @Inject() (val repos: IRepositories, val messagesApi: MessagesApi, val email: EmailComponent)
  extends AsyncController {

  val form = Form(single("email" -> play.api.data.Forms.email))

  def handle() = CSRFCheck {
    Action.async { implicit request =>
      form.bindFromRequest.fold(
        errors => badRequest(views.html.v2.unauthorized.remind(errors)),
        emailAddress => {
          val url: String = controllers.routes.LoginPage.login().url
          repos.person.findByEmail(emailAddress) flatMap {
            case None => redirect(url, "error" -> "This email address is not registered")
            case Some(person) =>
              repos.userAccount.findByPerson(person.identifier) flatMap {
                case None => redirect(url, "error" -> "Internal error. Please contact the support team")
                case Some(account) =>
                  val rawOptions = Seq(
                    if (account.facebook.nonEmpty) Some("Facebook") else None,
                    if (account.twitter.nonEmpty) Some("Twitter") else None,
                    if (account.linkedin.nonEmpty) Some("LinkedIn") else None,
                    if (account.google.nonEmpty) Some("G+") else None,
                    if (account.byEmail) Some("Email") else None
                  )
                  val options = rawOptions.filter(_.nonEmpty).map(_.get)
                  val body = mail.templates.password.html.remind(person, options).toString
                  email.send(Set(person), None, None, "Teller Login Reminder", body, richMessage = true)
                  val modifiedSession = request.session - LoginReminder.Key
                  redirect(url, modifiedSession,
                    "success" -> "Thank you. Please check your email for further instructions")
              }
          }
        }
      )
    }
  }

  def page() = CSRFAddToken {
    Action.async { implicit request =>
      ok(views.html.v2.unauthorized.remind(form))
    }
  }

}

object LoginReminder {
  val Key = "login-attempt"

  def updateCounter(session: Session, url: String): (Session, (String, String)) = {
    val counter = session.get(Key).map(_.toInt).getOrElse(0) + 1
    val modifiedSession = session + (Key -> counter.toString)
    if (counter > 1) {
      val msg = s"Forgot what account to use to log into the system? <a href='$url'>Send a reminder</a>"
      (modifiedSession, "warning" -> msg)
    } else {
      (modifiedSession, "error" -> "You are not registered in the system")
    }

  }
}