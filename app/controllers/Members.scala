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
import models.{ Activity, Member }
import org.joda.money.Money
import org.joda.time.{ DateTime, LocalDate }
import play.api.Play.current
import play.api.cache.Cache
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
      "objectId" -> longNumber,
      "person" -> number.transform(
        (i: Int) ⇒ if (i == 0) false else true,
        (b: Boolean) ⇒ if (b) 1 else 0),
      "funder" -> number.transform(
        (i: Int) ⇒ if (i == 0) false else true,
        (b: Boolean) ⇒ if (b) 1 else 0),
      "fee" -> jodaMoney().
        verifying("error.money.negativeOrZero", (m: Money) ⇒ m.isPositive).
        verifying("error.money.onlyEuro", (m: Money) ⇒ m.getCurrencyUnit.getCode == "EUR"),
      "since" -> jodaLocalDate.verifying(
        "error.membership.tooEarly",
        d ⇒ d.isAfter(MEMBERSHIP_EARLIEST_DATE) || d.isEqual(MEMBERSHIP_EARLIEST_DATE)).
        verifying(
          "error.membership.tooLate",
          _.isBefore(LocalDate.now().plusDays(1))),
      "existingObject" -> number.transform(
        (i: Int) ⇒ if (i == 0) false else true,
        (b: Boolean) ⇒ if (b) 1 else 0),
      "created" -> ignored(DateTime.now()),
      "createdBy" -> ignored(modifierId),
      "updated" -> ignored(DateTime.now()),
      "updatedBy" -> ignored(modifierId))(Member.apply)(Member.unapply))
  }

  def existingOrgForm = Form(
    single("id" -> longNumber))

  def existingPersonForm = Form(
    single("id" -> longNumber))

  /** Renders a list of all members */
  def index() = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      val members = memberService.findAll
      val fee = members.find(m ⇒
        m.person && m.objectId == user.person.id.get) map { m ⇒ Some(m.fee) } getOrElse None
      var totalFee = Money.parse("EUR 0")
      members.foreach(m ⇒ totalFee = totalFee.plus(m.fee))
      Ok(views.html.member.index(user, members, fee, totalFee))
  }

  /**
   * Renders detailed info about a member
   * @param id Member identifier
   */
  def details(id: Long) = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      Ok("")
  }

  /** Renders Add form */
  def add() = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      Ok(views.html.member.form(user, None, form(user.person.id.get)))
  }

  /**
   * Records a first block of data about member to database and redirects
   * users to the next step
   */
  def create() = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      form(user.person.id.get).bindFromRequest.fold(
        formWithErrors ⇒ BadRequest(views.html.member.form(user,
          None,
          formWithErrors)),
        member ⇒ {
          val m = member.copy(id = None).copy(objectId = 0)
          Cache.set(Members.cacheId(user.person.id.get), m, 1800)
          (member.person, member.existingObject) match {
            case (true, false) ⇒ Redirect(routes.Members.addPerson())
            case (false, false) ⇒ Redirect(routes.Members.addOrganisation())
            case (false, true) ⇒ Redirect(routes.Members.addExistingOrganisation())
            case (true, true) ⇒ Redirect(routes.Members.addExistingPerson())
          }
        })
  }

  /** Renders Edit form */
  def edit(id: Long) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      memberService.find(id, withObject = true) map { m ⇒
        val formWithData = form(user.person.id.get).fill(m)
        Ok(views.html.member.form(user, Some(m), formWithData))
      } getOrElse NotFound
  }

  /**
   * Updates membership data
   * @param id Member identifier
   */
  def update(id: Long) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      memberService.find(id, withObject = true) map { m ⇒
        form(user.person.id.get).bindFromRequest.fold(
          formWithErrors ⇒ BadRequest(views.html.member.form(user,
            Some(m),
            formWithErrors)),
          member ⇒ {
            val updMember = member.copy(id = m.id).
              copy(person = m.person).
              copy(objectId = m.objectId).update
            val activity = updMember.activity(
              user.person,
              Activity.Predicate.Updated).insert
            val url: String = if (updMember.person) {
              routes.People.details(updMember.objectId).url
            } else {
              routes.Organisations.details(updMember.objectId).url
            }
            Redirect(url).flashing("success" -> activity.toString)
          })
      } getOrElse NotFound
  }

  /** Renders Add new person page */
  def addPerson() = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      Ok(views.html.member.newPerson(user, None, People.personForm(user)))
  }

  /** Renders Add new organisation page */
  def addOrganisation() = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      Ok(views.html.member.newOrg(user, None, Organisations.organisationForm))
  }

  /** Renders Add existing organisation page */
  def addExistingOrganisation() = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      Ok(views.html.member.existingOrg(user, orgsNonMembers, existingOrgForm))
  }

  /** Renders Add existing person page */
  def addExistingPerson() = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      Ok(views.html.member.existingPerson(user, peopleNonMembers, existingPersonForm))
  }

  /** Records a new member-organisation to database */
  def createNewOrganisation() = SecuredRestrictedAction(Editor) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        val orgForm = Organisations.organisationForm.bindFromRequest
        orgForm.fold(
          hasErrors ⇒
            BadRequest(views.html.member.newOrg(user, None, hasErrors)),
          success ⇒ {
            val cached = Cache.getAs[Member](Members.cacheId(user.person.id.get))
            cached map { m ⇒
              val org = success.insert
              org.activity(user.person, Activity.Predicate.Created).insert
              // rewrite 'person' attribute in case if incomplete object was
              //  created for different type of member
              val member = m.copy(objectId = org.id.get).copy(person = false).insert
              Cache.remove(Members.cacheId(user.person.id.get))
              val activity = member.activity(
                user.person,
                Activity.Predicate.Made,
                Some(org)).insert
              Redirect(routes.Organisations.details(org.id.get)).
                flashing("success" -> activity.toString)
            } getOrElse {
              implicit val flash = Flash(Map("error" -> Messages("error.membership.wrongStep")))
              BadRequest(views.html.member.newOrg(user, None, orgForm))
            }
          })
  }

  /** Records a new member-person to database */
  def createNewPerson() = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      val personForm = People.personForm(user).bindFromRequest
      personForm.fold(
        hasErrors ⇒
          BadRequest(views.html.member.newPerson(user, None, hasErrors)),
        success ⇒ {
          val cached = Cache.getAs[Member](Members.cacheId(user.person.id.get))
          cached map { m ⇒
            val person = success.insert
            person.activity(user.person, Activity.Predicate.Created).insert
            val member = m.copy(objectId = person.id.get).copy(person = true).insert
            Cache.remove(Members.cacheId(user.person.id.get))
            val activity = member.activity(
              user.person,
              Activity.Predicate.Made,
              Some(person)).insert
            Redirect(routes.People.details(person.id.get)).
              flashing("success" -> activity.toString)
          } getOrElse {
            implicit val flash = Flash(Map("error" -> Messages("error.membership.wrongStep")))
            BadRequest(views.html.member.newPerson(user, None, personForm))
          }
        })
  }

  /** Records an existing member-person to database */
  def updateExistingPerson() = SecuredRestrictedAction(Editor) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        val personForm = existingPersonForm.bindFromRequest
        personForm.fold(
          hasErrors ⇒
            BadRequest(views.html.member.existingPerson(user,
              peopleNonMembers,
              hasErrors)),
          id ⇒ {
            val cached = Cache.getAs[Member](Members.cacheId(user.person.id.get))
            cached map { m ⇒
              personService.find(id) map { person ⇒
                if (person.member.nonEmpty) {
                  implicit val flash = Flash(Map("error" -> Messages("error.person.member")))
                  BadRequest(views.html.member.existingPerson(user,
                    peopleNonMembers,
                    personForm))
                } else {
                  val member = m.copy(objectId = person.id.get).copy(person = true).insert
                  Cache.remove(Members.cacheId(user.person.id.get))
                  val activity = member.activity(
                    user.person,
                    Activity.Predicate.Made,
                    Some(person)).insert
                  Redirect(routes.People.details(id)).
                    flashing("success" -> activity.toString)
                }
              } getOrElse {
                implicit val flash = Flash(Map("error" -> Messages("error.person.notExist")))
                BadRequest(views.html.member.existingOrg(user,
                  peopleNonMembers,
                  personForm))
              }
            } getOrElse {
              implicit val flash = Flash(Map("error" -> Messages("error.membership.wrongStep")))
              BadRequest(views.html.member.existingPerson(user, peopleNonMembers, personForm))
            }
          })
  }

  /** Records an existing organisation-person to database */
  def updateExistingOrg() = SecuredRestrictedAction(Editor) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        val orgForm = existingOrgForm.bindFromRequest
        orgForm.fold(
          hasErrors ⇒ {
            BadRequest(views.html.member.existingOrg(user,
              orgsNonMembers,
              hasErrors))
          },
          id ⇒ {
            val cached = Cache.getAs[Member](Members.cacheId(user.person.id.get))
            cached map { m ⇒
              orgService.find(id) map { org ⇒
                if (org.member.nonEmpty) {
                  implicit val flash = Flash(Map("error" -> Messages("error.organisation.member")))
                  BadRequest(views.html.member.existingOrg(user,
                    orgsNonMembers,
                    orgForm))
                } else {
                  val member = m.copy(objectId = org.id.get).copy(person = false).insert
                  Cache.remove(Members.cacheId(user.person.id.get))
                  val activity = member.activity(
                    user.person,
                    Activity.Predicate.Made,
                    Some(org)).insert
                  Redirect(routes.Organisations.details(id)).
                    flashing("success" -> activity.toString)
                }
              } getOrElse {
                implicit val flash = Flash(Map("error" -> Messages("error.organisation.notExist")))
                BadRequest(views.html.member.existingOrg(user,
                  orgsNonMembers,
                  orgForm))
              }
            } getOrElse {
              implicit val flash = Flash(Map("error" -> Messages("error.membership.wrongStep")))
              BadRequest(views.html.member.existingOrg(user,
                orgsNonMembers,
                orgForm))
            }
          })
  }

  /** Returns ids and names of organisations which are not members */
  private def orgsNonMembers: List[(String, String)] = orgService.findNonMembers.
    map(o ⇒ (o.id.get.toString, o.name))

  /** Returns ids and names of people which are not members */
  private def peopleNonMembers: List[(String, String)] = personService.findNonMembers.
    map(p ⇒ (p.id.get.toString, p.fullName))
}

object Members extends Members with Security with Services {

  /**
   * Returns cache identifier to store incomplete member object
   * @param id User id
   * @return
   */
  def cacheId(id: Long): String = "incomplete.member." + id.toString
}
