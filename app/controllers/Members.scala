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

import models.JodaMoney._
import models.UserRole.Role._
import models.service.Services
import models.{ Activity, LoginIdentity, Member }
import org.joda.money.Money
import org.joda.time.{ LocalDate, DateTime }
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages
import play.api.mvc._

/** Renders pages and contains actions related to members */
trait Members extends Controller with Security with Services {

  def form(modifierId: Long) = {
    val MEMBERSHIP_EARLIEST_DATE = LocalDate.parse("2015-01-01")
    Form(mapping(
      "id" -> ignored(Option.empty[Long]),
      "objectId" -> optional(longNumber),
      "person" -> number.transform(
        (i: Int) ⇒ if (i == 0) false else true,
        (b: Boolean) ⇒ if (b) 1 else 0),
      "funder" -> boolean,
      "fee" -> jodaMoney().verifying("error.money.negativeOrZero", (m: Money) ⇒ m.isPositive),
      "since" -> jodaLocalDate.verifying(
        "error.membership.tooEarly",
        _.isAfter(MEMBERSHIP_EARLIEST_DATE)).verifying(
          "error.membership.tooLate",
          _.isBefore(LocalDate.now().plusDays(1))),
      "created" -> ignored(DateTime.now()),
      "createdBy" -> ignored(modifierId),
      "updated" -> ignored(DateTime.now()),
      "updatedBy" -> ignored(modifierId))(Member.apply)(Member.unapply))
  }

  /** Renders a list of all members */
  def index() = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒
      val members = memberService.findAll
      Ok(views.html.member.index(request.user, members))
  }

  /**
   * Renders detailed info about a member
   * @param id Member identifier
   */
  def details(id: Long) = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒
      Ok("")
  }

  /** Renders Add form */
  def add() = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒
      val user = request.user.asInstanceOf[LoginIdentity].person
      Ok(views.html.member.form(request.user, None, form(user.id.get)))
  }

  /**
   * Records a first block of data about member to database and redirects
   * users to the next step
   */
  def create() = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒
      val user = request.user.asInstanceOf[LoginIdentity].person
      form(user.id.get).bindFromRequest.fold(
        formWithErrors ⇒ BadRequest(views.html.member.form(request.user,
          None,
          formWithErrors)),
        member ⇒ {
          member.copy(id = None).copy(objectId = None).insert
          Redirect(routes.Members.addOrganisation())
        })
  }

  /** Renders Add new organisation page */
  def addOrganisation() = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒
      Ok(views.html.member.newOrg(request.user, None, Organisations.organisationForm))
  }

  /** Records a new member-organisation to database */
  def createNewOrganisation() = SecuredRestrictedAction(Editor) {
    implicit request ⇒
      implicit handler ⇒
        val orgForm = Organisations.organisationForm.bindFromRequest
        orgForm.fold(
          hasErrors ⇒
            BadRequest(views.html.member.newOrg(request.user, None, hasErrors)),
          success ⇒ {
            val user = request.user.asInstanceOf[LoginIdentity].person
            val member = memberService.findIncompleteMember(
              isPerson = false,
              user.id.get)
            member map { m ⇒
              val org = success.insert
              m.copy(objectId = Some(org.id.get)).update
              val activity = Activity.insert(request.user.fullName,
                Activity.Predicate.Created, "new member " + success.name)
              //@TODO redirect to details
              Redirect(routes.Members.index()).flashing("success" -> activity.toString)
            } getOrElse {
              implicit val flash = Flash(Map("error" -> Messages("error.membership.wrongStep")))
              BadRequest(views.html.member.newOrg(request.user, None, orgForm))
            }
          })
  }

}

object Members extends Members with Security with Services
