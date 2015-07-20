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
import models.{ ActiveUser, Member }
import org.joda.money.Money
import org.joda.time.{ DateTime, LocalDate }
import play.api.Play.current
import play.api.cache.Cache
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages
import play.api.mvc._
import securesocial.core.RuntimeEnvironment
import templates.Formatters._

/** Renders pages and contains actions related to members */
class Members(environment: RuntimeEnvironment[ActiveUser])
    extends Enrollment
    with JsonController
    with Security
    with Activities
    with Utilities {

  override implicit val env: RuntimeEnvironment[ActiveUser] = environment

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
      "renewal" -> boolean,
      "since" -> jodaLocalDate.verifying(
        "error.membership.tooEarly",
        d ⇒ d.isAfter(MEMBERSHIP_EARLIEST_DATE) || d.isEqual(MEMBERSHIP_EARLIEST_DATE)).
        verifying(
          "error.membership.tooLate",
          _.isBefore(LocalDate.now().dayOfMonth().withMaximumValue().plusDays(2))),
      "until" -> ignored(LocalDate.now()), // we do not care about this value as on update it will rewritten
      "existingObject" -> number.transform(
        (i: Int) ⇒ if (i == 0) false else true,
        (b: Boolean) ⇒ if (b) 1 else 0),
      "reason" -> ignored(None.asInstanceOf[Option[String]]),
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
      val members = memberService.findAll.filter(_.active)
      val fee = members.find(m ⇒
        m.person && m.objectId == user.person.id.get) map { m ⇒ Some(m.fee) } getOrElse None
      var totalFee = Money.parse("EUR 0")
      members.foreach(m ⇒ totalFee = totalFee.plus(m.fee))
      Ok(views.html.member.index(user, members, fee, totalFee))
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
          val m = member.
            copy(id = None).
            copy(objectId = 0).
            copy(until = member.since.plusYears(1))
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
      memberService.find(id) map { m ⇒
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
      memberService.find(id) map { existing ⇒
        form(user.person.id.get).bindFromRequest.fold(
          formWithErrors ⇒ BadRequest(views.html.member.form(user,
            Some(existing),
            formWithErrors)),
          data ⇒ {
            val updated = data.copy(id = existing.id).
              copy(person = existing.person, objectId = existing.objectId).
              copy(until = existing.until, reason = existing.reason)
            memberService.update(updated)
            val log = activity(updated, user.person).updated.insert()
            val url = profileUrl(updated)
            updatedMemberMsg(existing, updated, url) map { msg ⇒
              slack.send(msg)
            }
            Redirect(url).flashing("success" -> log.toString)
          })
      } getOrElse NotFound
  }

  /**
   * Updates a reason for the given person
   *
   * @param personId Person identifier
   */
  def updateReason(personId: Long) = SecuredDynamicAction("person", "edit") {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        personService.member(personId) map { member ⇒
          val form = Form(single("reason" -> optional(text)))
          form.bindFromRequest.fold(
            error ⇒ jsonBadRequest("Reason does not exist"),
            reason ⇒ {
              memberService.update(member.copy(reason = reason))
              profileStrengthService.find(personId, false) map { strength ⇒
                if (reason.isDefined && reason.get.length > 0) {
                  profileStrengthService.update(strength.markComplete("reason"))
                } else {
                  profileStrengthService.update(strength.markIncomplete("reason"))
                }
              }
              jsonSuccess((reason getOrElse "").markdown.toString)
            })
        } getOrElse jsonNotFound("Person is not a member")
  }

  /**
   * Removes a membership of the given member
   * @param id Member id
   */
  def delete(id: Long) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      memberService.find(id) map { m ⇒
        memberService.delete(m.objectId, m.person)
        val log = activity(m, user.person).deleted.insert()
        val url = profileUrl(m)
        val msg = "Hey @channel, %s is not a member anymore. <%s|View profile>".format(
          m.name, fullUrl(url))
        slack.send(msg)
        Redirect(url).flashing("success" -> log.toString)
      } getOrElse NotFound
  }

  /** Renders Add new person page */
  def addPerson() = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      Ok(views.html.member.newPerson(user, None, People.personForm(user.name)))
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
          view ⇒ {
            val cached = Cache.getAs[Member](Members.cacheId(user.person.id.get))
            cached map { m ⇒
              val org = orgService.insert(view).org
              activity(org, user.person).created.insert()
              // rewrite 'person' attribute in case if incomplete object was
              //  created for different type of member
              val member = memberService.insert(m.copy(objectId = org.id.get,
                person = false))
              Cache.remove(Members.cacheId(user.person.id.get))
              val log = activity(member, user.person, Some(org)).made.insert()
              val profileUrl = routes.Organisations.details(org.id.get).url
              val text = newMemberMsg(member, org.name, profileUrl)
              slack.send(text)
              Redirect(profileUrl).flashing("success" -> log.toString)
            } getOrElse {
              val formWithError = orgForm.withGlobalError(Messages("error.membership.wrongStep"))
              BadRequest(views.html.member.newOrg(user, None, formWithError))
            }
          })
  }

  /** Records a new member-person to database */
  def createNewPerson() = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      val personForm = People.personForm(user.name).bindFromRequest
      personForm.fold(
        hasErrors ⇒
          BadRequest(views.html.member.newPerson(user, None, hasErrors)),
        success ⇒ {
          val cached = Cache.getAs[Member](Members.cacheId(user.person.id.get))
          cached map { m ⇒
            val person = personService.insert(success)
            val member = memberService.insert(m.copy(objectId = person.id.get, person = true))
            Cache.remove(Members.cacheId(user.person.id.get))
            activity(person, user.person).created.insert()
            val log = activity(member, user.person).made.insert()
            notify(person, None, member.fee, member)
            subscribe(person, member)

            val profileUrl = routes.People.details(person.id.get).url
            Redirect(profileUrl).flashing("success" -> log.toString)
          } getOrElse {
            val formWithError = personForm.withGlobalError(Messages("error.membership.wrongStep"))
            BadRequest(views.html.member.newPerson(user, None, formWithError))
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
                  val formWithError = personForm.withGlobalError(Messages("error.person.member"))
                  BadRequest(views.html.member.existingPerson(user,
                    peopleNonMembers, formWithError))
                } else {
                  val member = memberService.insert(m.copy(objectId = person.id.get, person = true))
                  Cache.remove(Members.cacheId(user.person.id.get))
                  val log = activity(member, user.person).made.insert()
                  notify(person, None, member.fee, member)
                  subscribe(person, member)

                  val profileUrl = routes.People.details(person.id.get).url
                  Redirect(profileUrl).flashing("success" -> log.toString)
                }
              } getOrElse {
                val formWithError = personForm.withGlobalError(Messages("error.person.notExist"))
                BadRequest(views.html.member.existingPerson(user,
                  peopleNonMembers, formWithError))
              }
            } getOrElse {
              val formWithError = personForm.withGlobalError(Messages("error.membership.wrongStep"))
              BadRequest(views.html.member.existingPerson(user,
                peopleNonMembers, formWithError))
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
                  val formWithError = orgForm.withGlobalError(Messages("error.organisation.member"))
                  BadRequest(views.html.member.existingOrg(user,
                    orgsNonMembers, formWithError))
                } else {
                  val member = memberService.insert(m.copy(objectId = org.id.get, person = false))
                  Cache.remove(Members.cacheId(user.person.id.get))
                  val log = activity(member, user.person, Some(org)).made.insert()
                  val profileUrl = routes.Organisations.details(org.id.get).url
                  val text = newMemberMsg(member, org.name, profileUrl)
                  slack.send(text)
                  Redirect(profileUrl).flashing("success" -> log.toString)
                }
              } getOrElse {
                val formWithError = orgForm.withGlobalError(Messages("error.organisation.notExist"))
                BadRequest(views.html.member.existingOrg(user,
                  orgsNonMembers, formWithError))
              }
            } getOrElse {
              val formWithError = orgForm.withGlobalError(Messages("error.membership.wrongStep"))
              BadRequest(views.html.member.existingOrg(user, orgsNonMembers, formWithError))
            }
          })
  }

  /**
   * Returns an update message
   * @param before Initial member object
   * @param after Updated member object
   * @param url Profile url
   */
  protected def updatedMemberMsg(before: Member, after: Member, url: String): Option[String] = {
    val fields = List(
      compareValues("Since", before.since.toString, after.since.toString),
      compareValues("Funder",
        if (before.funder) "funder" else "supporter",
        if (after.funder) "funder" else "supporter"),
      compareValues("Fee", before.fee.toString, after.fee.toString))
    val changedFields = fields.filter(_.nonEmpty)
    if (changedFields.length > 0) {
      var msg = "Hey @channel, member %s was updated.".format(before.name)
      changedFields.foreach(v ⇒ msg += " %s.".format(v.get))
      msg += " <%s|View profile>".format(fullUrl(url))
      Some(msg)
    } else
      None
  }

  /**
   * Return profile url based on what member is: person or organisation
   * @param member Member object
   */
  protected def profileUrl(member: Member): String = {
    if (member.person)
      routes.People.details(member.objectId).url
    else
      routes.Organisations.details(member.objectId).url
  }

  /**
   * Returns a well-formed Slack notification message
   * @param member Member object
   * @param name Name of a new member either an organisation or a person
   * @param url Profile url
   */
  protected def newMemberMsg(member: Member, name: String, url: String): String = {
    val typeName = if (member.funder) "Funder" else "Supporter"
    "Hey @channel, we have *new %s*. %s, %s. <%s|View profile>".format(
      typeName, name, member.fee.toString, fullUrl(url))
  }

  /**
   * Returns a comparison message if fields are different, otherwise - None
   * @param field Field name
   * @param before Initial field value
   * @param after Updated field value
   */
  private def compareValues(field: String,
    before: String,
    after: String): Option[String] = {
    if (before != after)
      Some("Field *%s* has changed from '%s' to '%s'".format(field, before, after))
    else None
  }

  /** Returns ids and names of organisations which are not members */
  private def orgsNonMembers: List[(String, String)] = orgService.findNonMembers.
    map(o ⇒ (o.id.get.toString, o.name))

  /** Returns ids and names of people which are not members */
  private def peopleNonMembers: List[(String, String)] = personService.findNonMembers.
    map(p ⇒ (p.id.get.toString, p.fullName))
}

object Members {

  /**
   * Returns cache identifier to store incomplete member object
   * @param id User id
   * @return
   */
  def cacheId(id: Long): String = "incomplete.member." + id.toString
}
