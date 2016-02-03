/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2014, Happy Melly http://www.happymelly.com
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

package controllers

import javax.inject.Inject

import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import controllers.Forms._
import models.UserRole._
import models._
import models.payment.{GatewayWrapper, PaymentException, RequestException}
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
                               deadbolt: DeadboltActions, handlers: HandlerCache, actionBuilder: ActionBuilders)
  extends Security(deadbolt, handlers, actionBuilder)(messagesApi, env)
  with Activities
  with Files
  with I18nSupport {

  /**
   * Form target for toggling whether an organisation is active.
   */
  def activation(id: Long) = AsyncSecuredRestrictedAction(Role.Admin) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      val index: String = routes.Organisations.index().url
      orgService.find(id) flatMap {
        case None => redirect(index, "error" -> "Organisation not found")
        case Some(organisation) ⇒
        Form("active" -> boolean).bindFromRequest.fold(
          error ⇒ badRequest("invalid form data"),
          active ⇒ {
            orgService.activate(id, active)
            val activity = Activity.insert(user.name,
              if (active) Activity.Predicate.Activated else Activity.Predicate.Deactivated, organisation.name)
            redirect(routes.Organisations.details(id),"success" -> activity.toString)
          })
      }
  }

  /**
   * Create page.
   */
  def add = AsyncSecuredRestrictedAction(Role.Admin, Role.Coordinator) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    ok(views.html.v2.organisation.form(user, None, Organisations.organisationForm))
  }

  /**
   * Cancels a subscription for yearly-renewing membership
    *
    * @param id Organisation id
   */
  def cancel(id: Long) = AsyncSecuredDynamicAction(DynamicRole.OrgMember, id) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      val url: String = routes.Organisations.details(id).url + "#membership"
      orgService.find(id) flatMap {
        case None => notFound("Organisation not found")
        case Some(org) =>
          org.member map { m ⇒
            if (m.renewal) {
              val key = Play.configuration.getString("stripe.secret_key").get
              val gateway = new GatewayWrapper(key)
              try {
                gateway.cancel(org.customerId.get)
                m.copy(renewal = false).update
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
          } getOrElse {
            redirect(url, "error" -> Messages("error.membership.noSubscription"))
          }
      }
  }

  /**
   * Create form submits to this action.
   */
  def create = AsyncSecuredRestrictedAction(Role.Admin, Role.Coordinator) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    Organisations.organisationForm.bindFromRequest.fold(
      formWithErrors ⇒ badRequest(views.html.v2.organisation.form(user, None, formWithErrors)),
      view ⇒ {
        orgService.insert(view) flatMap { orgView =>
          val activity = Activity.insert(user.name, Activity.Predicate.Created, orgView.org.name)
          redirect(routes.Organisations.index(), "success" -> activity.toString)
        }
      })
  }

  /**
   * Adds new organization to the system
   */
  def createOrganizer = AsyncSecuredRestrictedAction(List(Role.Coordinator, Role.Facilitator)) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒
      Organisations.organisationForm.bindFromRequest.fold(
        formWithErrors ⇒ badRequest(formWithErrors.errorsAsJson),
        view ⇒ {
          orgService.insert(view) flatMap { orgView =>
            activity(orgView.org, user.person).created.insert()
            jsonOk(Json.obj("id" -> orgView.org.id, "name" -> orgView.org.name))
          }
        })
  }

  /**
   * Delete an organisation
    *
    * @param id Organisation ID
   */
  def delete(id: Long) = AsyncSecuredRestrictedAction(Role.Admin, Role.Coordinator) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      orgService.find(id) flatMap {
        case None => notFound("Organisation not found")
        case Some(organisation) ⇒
          orgService.delete(id)
          val activity = Activity.insert(user.name, Activity.Predicate.Deleted, organisation.name)
          redirect(routes.Organisations.index(), "success" -> activity.toString)
      }
  }

  /**
   * Deletes logo of the given organisation
   *
   * @param id Organisation identifier
   */
  def deleteLogo(id: Long) = AsyncSecuredDynamicAction(DynamicRole.OrgMember, id) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒
      Organisation.logo(id).remove()
      orgService.updateLogo(id, false)
      val route = routes.Organisations.details(id).url
      jsonOk(Json.obj("link" -> routes.Assets.at("images/happymelly-face-white.png").url))
  }

  /**
   * Renders Details page
    *
    * @param id Organisation ID
   */
  def details(id: Long) = AsyncSecuredRestrictedAction(Role.Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      (for {
        o <- orgService.findWithProfile(id)
        m <- orgService.people(id)
        p <- personService.findActive
        c <- contributionService.contributions(id, isPerson = false)
        pr <- productService.findAll
      } yield (o, m, p, c, pr)) flatMap {
        case (None, _, _, _, _) => notFound("Organisation not found")
        case (Some(view), members, people, contributions, products) =>
          val result = view.org.member map { v ⇒
            paymentRecordService.findByOrganisation(id)
          } getOrElse Future.successful(List())
          result flatMap { payments =>
            ok(views.html.v2.organisation.details(user, view, members, people, contributions, products, payments))
          }
      }
  }

  /**
   * Render an Edit page
    *
    * @param id Organisation ID
   */
  def edit(id: Long) = AsyncSecuredDynamicAction(DynamicRole.OrgMember, id) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      orgService.findWithProfile(id) flatMap {
        case None => notFound("Organisation not found")
        case Some(view) =>
          ok(views.html.v2.organisation.form(user, Some(id),
            Organisations.organisationForm.fill(OrgView(view.org, view.profile))))
      }
  }

  /**
   * List page.
   */
  def index = AsyncSecuredRestrictedAction(Role.Admin, Role.Coordinator) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    orgService.findAll flatMap { organisations =>
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
  def name(id: Long) = AsyncSecuredRestrictedAction(Role.Viewer) {
    implicit request => implicit handler => implicit user =>
      val name = orgService.find(id) map {
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
  def search(query: Option[String]) = AsyncSecuredRestrictedAction(Role.Viewer) {
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
          orgService.search(q)
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
  def update(id: Long) = AsyncSecuredDynamicAction(DynamicRole.OrgMember, id) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒
      orgService.findWithProfile(id) flatMap {
        case None => notFound("Organisation not found")
        case Some(view) ⇒
          Organisations.organisationForm.bindFromRequest.fold(
            formWithErrors ⇒ badRequest(views.html.v2.organisation.form(user, Some(id), formWithErrors)),
            view ⇒ {
              val updatedOrg = view.org.copy(id = Some(id), active = view.org.active, customerId = view.org.customerId)
              val updatedProfile = view.profile.forOrg.copy(objectId = id)
              orgService.update(OrgView(updatedOrg, updatedProfile)) flatMap { _ =>
                val log = activity(updatedOrg, user.person).updated.insert()
                redirect(routes.Organisations.details(id), "success" -> log.toString)
              }
            })
      }
  }

  /**
   * Upload a new logo to Amazon
   *
   * @param id Organisation identifier
   */
  def uploadLogo(id: Long) = AsyncSecuredDynamicAction(DynamicRole.OrgMember, id) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒
      uploadFile(Organisation.logo(id), "logo") flatMap { _ ⇒
        orgService.updateLogo(id, true)
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
    Utilities.cdnUrl(logo.name).orElse(Some(Utilities.fullUrl(controllers.routes.Organisations.logo(orgId).url)))
  }
}