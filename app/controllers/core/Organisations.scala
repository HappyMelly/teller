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
package controllers.core

import javax.inject.Inject

import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import controllers._
import controllers.Forms._
import models.UserRole.Role
import models._
import models.payment.{GatewayWrapper, PaymentException, RequestException}
import models.repository.Repositories
import org.joda.time.DateTime
import play.api.Play.current
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.{MessagesApi, I18nSupport, Messages}
import play.api.libs.json._
import play.api.{Logger, Play}
import services.TellerRuntimeEnvironment

import scala.concurrent.Future

class Organisations @Inject() (override implicit val env: TellerRuntimeEnvironment,
                               override val messagesApi: MessagesApi,
                               val services: Repositories,
                               deadbolt: DeadboltActions, handlers: HandlerCache, actionBuilder: ActionBuilders)
  extends Security(deadbolt, handlers, actionBuilder, services)(messagesApi, env)
    with Activities
    with Files
    with I18nSupport {

  /**
    * Form target for toggling whether an organisation is active.
    */
  def activation(id: Long) = RestrictedAction(Role.Admin) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      val index: String = routes.Organisations.index().url
      services.org.find(id) flatMap {
        case None => redirect(index, "error" -> "Organisation not found")
        case Some(organisation) ⇒
          Form("active" -> boolean).bindFromRequest.fold(
            error ⇒ badRequest("invalid form data"),
            active ⇒ {
              services.org.activate(id, active)
              val activity = Activity.insert(user.name,
                if (active) Activity.Predicate.Activated else Activity.Predicate.Deactivated, organisation.name)(services)
              redirect(routes.Organisations.details(id),"success" -> "Activation status was changed")
            })
      }
  }

  /**
    * Create page.
    */
  def add = RestrictedAction(List(Role.Admin, Role.Coordinator)) { implicit request ⇒ implicit handler ⇒
    implicit user ⇒
      ok(views.html.v2.organisation.form(user, None, Organisations.organisationForm))
  }

  /**
    * Cancels a subscription for yearly-renewing membership
    *
    * @param id Organisation id
    */
  def cancel(id: Long) = DynamicAction(Role.OrgMember, id) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      val url: String = routes.Organisations.details(id).url + "#membership"
      (for {
        o <- services.org.find(id)
        m <- services.org.member(id)
      } yield (o, m)) flatMap {
        case (None, _) => notFound("Organisation not found")
        case (_, None) => redirect(url, "error" -> Messages("error.membership.noSubscription"))
        case (Some(org), Some(member)) =>
          if (member.renewal) {
            val key = Play.configuration.getString("stripe.secret_key").get
            val gateway = new GatewayWrapper(key)
            try {
              gateway.cancel(org.customerId.get)
              services.member.update(member.copy(renewal = false))
            } catch {
              case e: PaymentException ⇒
                redirect(url,"error" -> Messages(e.msg))
              case e: RequestException ⇒
                e.log.foreach(Logger.error(_))
                redirect(url, "error" -> Messages(e.getMessage))
            }
            redirect(url, "success" -> "Subscription was successfully canceled")
          } else {
            redirect(url, "error" -> Messages("error.membership.noSubscription"))
          }
      }

  }

  /**
    * Create form submits to this action.
    */
  def create = RestrictedAction(List(Role.Admin, Role.Coordinator)) { implicit request ⇒ implicit handler ⇒
    implicit user ⇒
      Organisations.organisationForm.bindFromRequest.fold(
        formWithErrors ⇒ badRequest(views.html.v2.organisation.form(user, None, formWithErrors)),
        view ⇒ {
          services.org.insert(view) flatMap { orgView =>
            val activity = Activity.insert(user.name, Activity.Predicate.Created, orgView.org.name)(services)
            redirect(routes.Organisations.index(), "success" -> "Organisation was created")
          }
        })
  }

  /**
    * Adds new organization to the system
    */
  def createOrganizer = RestrictedAction(List(Role.Coordinator, Role.Facilitator)) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒
      Organisations.organisationForm.bindFromRequest.fold(
        formWithErrors ⇒ badRequest(formWithErrors.errorsAsJson),
        view ⇒ {
          services.org.insert(view) flatMap { orgView =>
            activity(orgView.org, user.person).created.insert(services)
            jsonOk(Json.obj("id" -> orgView.org.id, "name" -> orgView.org.name))
          }
        })
  }

  /**
    * Delete an organisation
    *
    * @param id Organisation ID
    */
  def delete(id: Long) = RestrictedAction(List(Role.Admin, Role.Coordinator)) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      services.org.find(id) flatMap {
        case None => notFound("Organisation not found")
        case Some(organisation) ⇒
          services.org.delete(id)
          val activity = Activity.insert(user.name, Activity.Predicate.Deleted, organisation.name)(services)
          redirect(routes.Organisations.index(), "success" -> "Organisation was deleted")
      }
  }

  /**
    * Deletes logo of the given organisation
    *
    * @param id Organisation identifier
    */
  def deleteLogo(id: Long) = DynamicAction(Role.OrgMember, id) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒
      Organisation.logo(id).remove()
      services.org.updateLogo(id, false)
      val route = routes.Organisations.details(id).url
      jsonOk(Json.obj("link" -> controllers.routes.Assets.at("images/happymelly-face-white.png").url))
  }

  /**
    * Renders Details page
    *
    * @param id Organisation ID
    */
  def details(id: Long) = RestrictedAction(Role.Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      (for {
        o <- services.org.findWithProfile(id)
        m <- services.org.people(id)
        _ <- services.person.collection.addresses(m)
        p <- services.person.findActive
        c <- services.contribution.contributions(id, isPerson = false)
        pr <- services.product.findAll
        member <- services.org.member(id)
      } yield (o, m, p, c, pr, member)) flatMap {
        case (None, _, _, _, _, _) => notFound("Organisation not found")
        case (Some(view), members, people, contributions, products, member) =>
          val result = member map { v ⇒
            services.paymentRecord.findByOrganisation(id)
          } getOrElse Future.successful(List())
          result flatMap { payments =>
            val deletable = members.isEmpty && contributions.isEmpty
            ok(views.html.v2.organisation.details(user, view, members, people, contributions,
              products, member, payments, deletable))
          }
      }
  }

  /**
    * Render an Edit page
    *
    * @param id Organisation ID
    */
  def edit(id: Long) = DynamicAction(Role.OrgMember, id) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      services.org.findWithProfile(id) flatMap {
        case None => notFound("Organisation not found")
        case Some(view) =>
          ok(views.html.v2.organisation.form(user, Some(id),
            Organisations.organisationForm.fill(OrgView(view.org, view.profile))))
      }
  }

  /**
    * List page.
    */
  def index = RestrictedAction(List(Role.Admin, Role.Coordinator)) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      services.org.findAll flatMap { organisations =>
        ok(views.html.v2.organisation.index(user, organisations))
      }
  }

  /**
    * Retrieve and cache a logo of the given organisation
    *
    * @param id Organisation identifier
    */
  def logo(id: Long) = file(Organisation.logo(id))

  /**
    * Returns name of the given organisation
    *
    * @param id Organisation id
    */
  def name(id: Long) = RestrictedAction(Role.Viewer) {
    implicit request => implicit handler => implicit user =>
      val name = services.org.find(id) map {
        case None => ""
        case Some(org) => org.name
      }
      name flatMap { name =>
        jsonOk(Json.obj("name" -> name))
      }
  }

  /**
    * Returns list of organisations for the given query
    *
    * @param query Search query
    */
  def search(query: Option[String]) = RestrictedAction(Role.Viewer) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒
      implicit val orgWrites = new Writes[Organisation] {
        def writes(data: Organisation): JsValue = {
          Json.obj(
            "id" -> data.id,
            "name" -> data.name,
            "countryCode" -> data.countryCode)
        }
      }
      val orgs = query map { q ⇒
        if (q.length < 3)
          Future.successful(List())
        else
          services.org.search(q)
      } getOrElse Future.successful(List())
      orgs flatMap { organisations =>
        jsonOk(Json.toJson(organisations))
      }
  }

  /**
    * Updates an organisation
    *
    * @param id Organisation ID
    */
  def update(id: Long) = DynamicAction(Role.OrgMember, id) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒
      services.org.findWithProfile(id) flatMap {
        case None => notFound("Organisation not found")
        case Some(view) ⇒
          Organisations.organisationForm.bindFromRequest.fold(
            formWithErrors ⇒ badRequest(views.html.v2.organisation.form(user, Some(id), formWithErrors)),
            view ⇒ {
              val updatedOrg = view.org.copy(id = Some(id), active = view.org.active, customerId = view.org.customerId)
              val updatedProfile = view.profile.forOrg.copy(objectId = id)
              services.org.update(OrgView(updatedOrg, updatedProfile)) flatMap { _ =>
                val log = activity(updatedOrg, user.person).updated.insert(services)
                redirect(routes.Organisations.details(id), "success" -> "Organisation was updated")
              }
            })
      }
  }

  /**
    * Upload a new logo to Amazon
    *
    * @param id Organisation identifier
    */
  def uploadLogo(id: Long) = DynamicAction(Role.OrgMember, id) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒
      uploadFile(Organisation.logo(id), "logo") flatMap { _ ⇒
        services.org.updateLogo(id, true)
        val route = routes.Organisations.details(id).url
        jsonOk(Json.obj("link" -> Organisations.logoUrl(id)))
      } recover {
        case e: RuntimeException ⇒ BadRequest(Json.obj("message" -> e.getMessage))
      }
  }
}

object Organisations {

  /**
    * HTML form mapping for creating and editing.
    */
  def organisationForm(implicit user: ActiveUser) = Form(mapping(
    "id" -> ignored(Option.empty[Long]),
    "name" -> nonEmptyText,
    "address" -> People.addressMapping,
    "vatNumber" -> optional(text),
    "registrationNumber" -> optional(text),
    "profile" -> SocialProfiles.profileMapping(ProfileType.Organisation),
    "webSite" -> optional(webUrl),
    "blog" -> optional(webUrl),
    "email" -> optional(play.api.data.Forms.email),
    "customerId" -> optional(text),
    "about" -> optional(text),
    "logo" -> ignored(false),
    "active" -> ignored(true),
    "dateStamp" -> mapping(
      "created" -> ignored(DateTime.now()),
      "createdBy" -> ignored(user.name),
      "updated" -> ignored(DateTime.now()),
      "updatedBy" -> ignored(user.name))(DateStamp.apply)(DateStamp.unapply))({
    (id, name, address, vatNumber,
     registrationNumber, profile, webSite, blog, email, customerId, about,
     logo, active, dateStamp) ⇒
      val org = Organisation(id, name, address.street1, address.street2,
        address.city, address.province, address.postCode, address.countryCode,
        vatNumber, registrationNumber, webSite,
        blog, email, customerId, about, logo, active, dateStamp)
      OrgView(org, profile)
  })({
    (v: OrgView) ⇒
      val address = Address(None, v.org.street1, v.org.street2,
        v.org.city, v.org.province, v.org.postCode, v.org.countryCode)
      Some((v.org.id, v.org.name, address, v.org.vatNumber,
        v.org.registrationNumber, v.profile, v.org.webSite,
        v.org.blog, v.org.contactEmail, v.org.customerId, v.org.about, v.org.logo, v.org.active,
        v.org.dateStamp))
  }))

  /**
    * Returns url to an organisation's logo
    *
    * @param orgId Organisation identifier
    */
  def logoUrl(orgId: Long): Option[String] = {
    val logo = Organisation.logo(orgId)
    Utilities.cdnUrl(logo.name).orElse(Some(Utilities.fullUrl(routes.Organisations.logo(orgId).url)))
  }
}