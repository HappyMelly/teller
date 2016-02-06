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

import javax.inject.Inject

import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import models.JodaMoney._
import models.UserRole.Role._
import models.{ActiveUser, Member, UserAccount}
import org.joda.money.Money
import org.joda.time.{DateTime, LocalDate}
import play.api.Play.current
import play.api.cache.Cache
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{MessagesApi, I18nSupport, Messages}
import play.api.mvc._
import services.TellerRuntimeEnvironment
import services.integrations.Email
import templates.Formatters._

import scala.concurrent.Future

/** Renders pages and contains actions related to members */
class Members @Inject() (override implicit val env: TellerRuntimeEnvironment,
                         override val messagesApi: MessagesApi,
                         val email: Email,
                         deadbolt: DeadboltActions, handlers: HandlerCache, actionBuilder: ActionBuilders)
  extends Security(deadbolt, handlers, actionBuilder)(messagesApi, env)
  with Enrollment
  with Activities
  with I18nSupport
  with Utilities
  with MemberNotifications {

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
  def index() = AsyncSecuredRestrictedAction(Viewer) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    memberService.findAll flatMap { results =>
      val members = results.filter(_.active)
      val fee = members.find(m ⇒ m.person && m.objectId == user.person.id.get) map { m ⇒ Some(m.fee) } getOrElse None
      var totalFee = Money.parse("EUR 0")
      members.foreach(m ⇒ totalFee = totalFee.plus(m.fee))
      ok(views.html.v2.member.index(user, members, fee, totalFee))
    }
  }

  /** Renders Add form */
  def add() = AsyncSecuredRestrictedAction(Admin) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    ok(views.html.member.form(user, None, form(user.person.identifier)))
  }

  /**
   * Records a first block of data about member to database and redirects
   * users to the next step
   */
  def create() = AsyncSecuredRestrictedAction(Admin) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    form(user.person.identifier).bindFromRequest.fold(
      formWithErrors ⇒ badRequest(views.html.member.form(user, None, formWithErrors)),
      member ⇒ {
        val m = member.copy(id = None).copy(objectId = 0).copy(until = member.since.plusYears(1))
        Cache.set(Members.cacheId(user.person.id.get), m, 1800)
        (member.person, member.existingObject) match {
          case (true, false) ⇒ redirect(routes.Members.addPerson())
          case (false, false) ⇒ redirect(routes.Members.addOrganisation())
          case (false, true) ⇒ redirect(routes.Members.addExistingOrganisation())
          case (true, true) ⇒ redirect(routes.Members.addExistingPerson())
        }
      })
  }

  /** Renders Edit form */
  def edit(id: Long) = AsyncSecuredRestrictedAction(Admin) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    memberService.find(id) flatMap {
      case None => notFound("Member not found")
      case Some(member) =>
        val formWithData = form(user.person.id.get).fill(member)
        ok(views.html.member.form(user, Some(member), formWithData))
    }
  }

  /**
   * Updates membership data
    *
    * @param id Member identifier
   */
  def update(id: Long) = AsyncSecuredRestrictedAction(Admin) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    memberService.find(id) flatMap {
      case None => notFound("Member not found")
      case Some(existing) =>
        form(user.person.id.get).bindFromRequest.fold(
          errors ⇒ badRequest(views.html.member.form(user, Some(existing), errors)),
          data ⇒ {
            val updated = data.copy(id = existing.id).
              copy(person = existing.person, objectId = existing.objectId).
              copy(until = existing.until, reason = existing.reason)
            memberService.update(updated) flatMap { _ =>
              val url: String = profileUrl(updated)
              updatedMemberMsg(existing, updated, url) map { msg ⇒ slack.send(msg) }
              redirect(url, "success" -> "Member was updated")
            }
          })
    }
  }

  /**
   * Updates a reason for the given person
   *
   * @param personId Person identifier
   */
  def updateReason(personId: Long) = AsyncSecuredProfileAction(personId) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      personService.member(personId) flatMap {
        case None => jsonNotFound("Member not found")
        case Some(member) =>
          val form = Form(single("reason" -> optional(text)))
          form.bindFromRequest.fold(
            error ⇒ jsonBadRequest("Reason does not exist"),
            reason ⇒ {
              memberService.update(member.copy(reason = reason))
              profileStrengthService.find(personId, false).filter(_.isDefined).map(_.get) flatMap { strength ⇒
                if (reason.isDefined && reason.get.length > 0) {
                  profileStrengthService.update(strength.markComplete("reason"))
                } else {
                  profileStrengthService.update(strength.markIncomplete("reason"))
                }
              }
              jsonSuccess((reason getOrElse "").markdown.toString)
            })
      }
  }

  /**
   * Removes a membership of the given member
    *
    * @param id Member id
   */
  def delete(id: Long) = AsyncSecuredRestrictedAction(Admin) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    memberService.find(id) flatMap {
      case None => notFound("Member not found")
      case Some(member) =>
        memberService.delete(member.objectId, member.person) flatMap { _ =>
          val url = profileUrl(member)
          val msg = "Hey @channel, %s is not a member anymore. <%s|View profile>".format(member.name, Utilities.fullUrl(url))
          slack.send(msg)
          redirect(url, "success" -> "Membership was cancelled")
        }
    }
  }

  /** Renders Add new person page */
  def addPerson() = AsyncSecuredRestrictedAction(Admin) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    ok(views.html.member.newPerson(user, None, People.personForm(user.name)))
  }

  /** Renders Add new organisation page */
  def addOrganisation() = AsyncSecuredRestrictedAction(Admin) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    ok(views.html.member.newOrg(user, None, Organisations.organisationForm))
  }

  /** Renders Add existing organisation page */
  def addExistingOrganisation() = AsyncSecuredRestrictedAction(Admin) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      orgsNonMembers flatMap { orgs =>
        ok(views.html.member.existingOrg(user, orgs, existingOrgForm))
      }
  }

  /** Renders Add existing person page */
  def addExistingPerson() = AsyncSecuredRestrictedAction(Admin) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      peopleNonMembers flatMap { people =>
        ok(views.html.member.existingPerson(user, people, existingPersonForm))
      }
  }

  /** Records a new member-organisation to database */
  def createNewOrganisation() = AsyncSecuredRestrictedAction(Admin) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      val orgForm = Organisations.organisationForm.bindFromRequest
      orgForm.fold(
        hasErrors ⇒ badRequest(views.html.member.newOrg(user, None, hasErrors)),
        view ⇒ {
          val cached = Cache.getAs[Member](Members.cacheId(user.person.identifier))
          cached map { m ⇒
            val actions = for {
              v <- orgService.insert(view)
              member <- memberService.insert(m.copy(objectId = v.org.identifier, person = false))
            } yield (v.org, member)
            actions flatMap { case (org, member) =>
              Cache.remove(Members.cacheId(user.person.id.get))
              val profileUrl: String = routes.Organisations.details(org.identifier).url
              val text = newMemberMsg(member, org.name, profileUrl)
              slack.send(text)
              redirect(profileUrl, "success" -> "Member was added")
            }
          } getOrElse {
            val formWithError = orgForm.withGlobalError(Messages("error.membership.wrongStep"))
            badRequest(views.html.member.newOrg(user, None, formWithError))
          }
        })
  }

  /** Records a new member-person to database */
  def createNewPerson() = AsyncSecuredRestrictedAction(Admin) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      val personForm = People.personForm(user.name).bindFromRequest
      personForm.fold(
        hasErrors ⇒
          badRequest(views.html.member.newPerson(user, None, hasErrors)),
        success ⇒ {
          val cached = Cache.getAs[Member](Members.cacheId(user.person.id.get))
          cached map { m ⇒
            val actions = for {
              person <- personService.insert(success)
              member <- memberService.insert(m.copy(objectId = person.identifier, person = true))
            } yield (person, member)
            actions flatMap { case (person, member) =>
              userAccountService.findByPerson(person.identifier) map {
                case None =>
                  val account = UserAccount.empty(person.identifier).copy(member = true, registered = true)
                  userAccountService.insert(account)
                case Some(account) =>
                  userAccountService.update(account.copy(member = true))
              }
              Cache.remove(Members.cacheId(user.person.id.get))
              activity(person, user.person).created.insert()
              val log = activity(member, user.person).made.insert()
              notify(person, None, member)
              subscribe(person, member)

              val profileUrl: String = routes.People.details(person.id.get).url
              redirect(profileUrl, "success" -> log.toString)
            }
          } getOrElse {
            val formWithError = personForm.withGlobalError(Messages("error.membership.wrongStep"))
            badRequest(views.html.member.newPerson(user, None, formWithError))
          }
        })
  }

  /** Records an existing member-person to database */
  def updateExistingPerson() = AsyncSecuredRestrictedAction(Admin) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      val personForm = existingPersonForm.bindFromRequest
      personForm.fold(
        hasErrors ⇒ personUpdateError(user, hasErrors),
        id ⇒ {
          val cached = Cache.getAs[Member](Members.cacheId(user.person.id.get))
          cached map { m ⇒
            personService.find(id) flatMap {
              case None =>
                val formWithError = personForm.withGlobalError(Messages("error.person.notExist"))
                personUpdateError(user, formWithError)
              case Some(person) ⇒
                if (person.member.nonEmpty) {
                  val formWithError = personForm.withGlobalError(Messages("error.person.member"))
                  personUpdateError(user, formWithError)
                } else {
                  memberService.insert(m.copy(objectId = person.id.get, person = true)) flatMap { member =>
                    userAccountService.findByPerson(person.identifier) map {
                      case None =>
                        val account = UserAccount.empty(person.identifier).copy(member = true, registered = true)
                        userAccountService.insert(account)
                      case Some(account) =>
                        userAccountService.update(account.copy(member = true))
                    }
                    Cache.remove(Members.cacheId(user.person.id.get))
                    val log = activity(member, user.person).made.insert()
                    notify(person, None, member)
                    subscribe(person, member)

                    val profileUrl: String = routes.People.details(person.id.get).url
                    redirect(profileUrl, "success" -> log.toString)
                  }
                }
            }
          } getOrElse {
            val formWithError = personForm.withGlobalError(Messages("error.membership.wrongStep"))
            personUpdateError(user, formWithError)
          }
        })
  }

  protected def personUpdateError(user: ActiveUser, form: Form[Long])(
      implicit request: Request[AnyContent], handler: be.objectify.deadbolt.scala.DeadboltHandler): Future[Result] = {
    peopleNonMembers flatMap { people =>
      badRequest(views.html.member.existingPerson(user, people, form))
    }
  }

  /** Records an existing organisation-person to database */
  def updateExistingOrg() = AsyncSecuredRestrictedAction(Admin) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    val orgForm = existingOrgForm.bindFromRequest
    orgForm.fold(
      hasErrors ⇒ orgUpdateError(user, hasErrors),
      id ⇒ {
        val cached = Cache.getAs[Member](Members.cacheId(user.person.id.get))
        cached map { m ⇒
          orgService.find(id) flatMap {
            case None =>
              val formWithError = orgForm.withGlobalError(Messages("error.organisation.notExist"))
              orgUpdateError(user, formWithError)
            case Some(org) ⇒
              if (org.member.nonEmpty) {
                val formWithError = orgForm.withGlobalError(Messages("error.organisation.member"))
                orgUpdateError(user, formWithError)
              } else {
                memberService.insert(m.copy(objectId = org.id.get, person = false)) flatMap { member =>
                  Cache.remove(Members.cacheId(user.person.id.get))
                  val log = activity(member, user.person, Some(org)).made.insert()
                  val profileUrl: String = routes.Organisations.details(org.id.get).url
                  val text = newMemberMsg(member, org.name, profileUrl)
                  slack.send(text)
                  redirect(profileUrl, "success" -> log.toString)
                }
              }
          }
        } getOrElse {
          val formWithError = orgForm.withGlobalError(Messages("error.membership.wrongStep"))
          orgUpdateError(user, formWithError)
        }
      })
  }

  protected def orgUpdateError(user: ActiveUser, form: Form[Long])(
    implicit request: Request[AnyContent], handler: be.objectify.deadbolt.scala.DeadboltHandler): Future[Result] = {

    orgsNonMembers flatMap { orgs =>
      badRequest(views.html.member.existingOrg(user, orgs, form))
    }
  }

  /**
   * Returns an update message
    *
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
    if (changedFields.nonEmpty) {
      var msg = "Hey @channel, member %s was updated.".format(before.name)
      changedFields.foreach(v ⇒ msg += " %s.".format(v.get))
      msg += " <%s|View profile>".format(Utilities.fullUrl(url))
      Some(msg)
    } else
      None
  }

  /**
   * Return profile url based on what member is: person or organisation
    *
    * @param member Member object
   */
  protected def profileUrl(member: Member): String = {
    if (member.person)
      routes.People.details(member.objectId).url
    else
      routes.Organisations.details(member.objectId).url
  }

  /**
   * Returns a comparison message if fields are different, otherwise - None
    *
    * @param field Field name
   * @param before Initial field value
   * @param after Updated field value
   */
  private def compareValues(field: String, before: String, after: String): Option[String] = {
    if (before != after)
      Some("Field *%s* has changed from '%s' to '%s'".format(field, before, after))
    else None
  }

  /** Returns ids and names of organisations which are not members */
  private def orgsNonMembers: Future[List[(String, String)]] = orgService.findNonMembers map { orgs =>
    orgs.map(o ⇒ (o.identifier.toString, o.name))
  }

  /** Returns ids and names of people which are not members */
  private def peopleNonMembers: Future[List[(String, String)]] = personService.findNonMembers map { people =>
    people.map(p ⇒ (p.identifier.toString, p.fullName))
  }
}

object Members {

  /**
   * Returns cache identifier to store incomplete member object
    *
    * @param id User id
   * @return
   */
  def cacheId(id: Long): String = "incomplete.member." + id.toString
}
