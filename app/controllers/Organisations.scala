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

import controllers.Forms._
import models.UserRole.DynamicRole
import models.UserRole.Role._
import models._
import models.payment.{ GatewayWrapper, PaymentException, RequestException }
import models.service.Services
import org.joda.time.DateTime
import play.api.Play.current
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.Messages
import play.api.libs.json._
import play.api.{ Logger, Play }
import securesocial.core.RuntimeEnvironment

import scala.concurrent.Future

class Organisations(environment: RuntimeEnvironment[ActiveUser])
    extends JsonController
    with Security
    with Services
    with Files
    with Activities {

  override implicit val env: RuntimeEnvironment[ActiveUser] = environment

  /**
   * Form target for toggling whether an organisation is active.
   */
  def activation(id: Long) = SecuredRestrictedAction(Admin) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      orgService.find(id).map { organisation ⇒
        Form("active" -> boolean).bindFromRequest.fold(
          form ⇒ {
            BadRequest("invalid form data")
          },
          active ⇒ {
            orgService.activate(id, active)
            val activity = Activity.insert(user.name,
              if (active) Activity.Predicate.Activated else Activity.Predicate.Deactivated, organisation.name)
            Redirect(routes.Organisations.details(id)).flashing("success" -> activity.toString)
          })
      } getOrElse {
        Redirect(routes.Organisations.index).flashing("error" -> Messages("error.notFound", Messages("models.Organisation")))
      }
  }

  /**
   * Create page.
   */
  def add = SecuredRestrictedAction(Admin) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      Ok(views.html.v2.organisation.form(user, None, Organisations.organisationForm))
  }

  /**
   * Cancels a subscription for yearly-renewing membership
   * @param id Organisation id
   */
  def cancel(id: Long) = AsyncSecuredDynamicAction(DynamicRole.OrgMember, id) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        val url = routes.Organisations.details(id).url + "#membership"
        orgService.find(id) map { org ⇒
          org.member map { m ⇒
            if (m.renewal) {
              val key = Play.configuration.getString("stripe.secret_key").get
              val gateway = new GatewayWrapper(key)
              try {
                gateway.cancel(org.customerId.get)
                m.copy(renewal = false).update
              } catch {
                case e: PaymentException ⇒
                  Future.successful(Redirect(url).flashing("error" -> Messages(e.msg)))
                case e: RequestException ⇒
                  e.log.foreach(Logger.error(_))
                  Future.successful(Redirect(url).flashing("error" -> Messages(e.getMessage)))
              }
              Future.successful(
                Redirect(url).flashing("success" -> "Subscription was successfully canceled"))
            } else {
              Future.successful(
                Redirect(url).flashing("error" -> Messages("error.membership.noSubscription")))
            }
          } getOrElse {
            Future.successful(
              Redirect(url).flashing("error" -> Messages("error.membership.noSubscription")))
          }
        } getOrElse Future.successful(NotFound)
  }

  /**
   * Create form submits to this action.
   */
  def create = SecuredRestrictedAction(Admin) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      Organisations.organisationForm.bindFromRequest.fold(
        formWithErrors ⇒
          BadRequest(views.html.v2.organisation.form(user, None, formWithErrors)),
        view ⇒ {
          val org = orgService.insert(view)
          val activity = Activity.insert(user.name, Activity.Predicate.Created, view.org.name)
          Redirect(routes.Organisations.index()).flashing("success" -> activity.toString)
        })
  }

  /**
   * Adds new organization to the system
   */
  def createOrganizer = AsyncSecuredRestrictedAction(BrandViewer) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒
      Organisations.organisationForm.bindFromRequest.fold(
        formWithErrors ⇒ Future.successful(BadRequest(formWithErrors.errorsAsJson)),
        view ⇒ {
          val org = orgService.insert(view).org
          activity(org, user.person).created.insert()
          Future.successful(jsonOk(Json.obj("id" -> org.id, "name" -> org.name)))
        })
  }

  /**
   * Delete an organisation
   * @param id Organisation ID
   */
  def delete(id: Long) = SecuredRestrictedAction(Admin) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      orgService.find(id).map { organisation ⇒
        orgService.delete(id)
        val activity = Activity.insert(user.name, Activity.Predicate.Deleted, organisation.name)
        Redirect(routes.Organisations.index()).flashing("success" -> activity.toString)
      }.getOrElse(NotFound)
  }

  /**
   * Deletes logo of the given organisation
   *
   * @param id Organisation identifier
   */
  def deleteLogo(id: Long) = AsyncSecuredDynamicAction(DynamicRole.OrgMember, id) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        Organisation.logo(id).remove()
        orgService.updateLogo(id, false)
        val route = routes.Organisations.details(id).url
        Future.successful(
          jsonOk(Json.obj("link" -> routes.Assets.at("images/happymelly-face-white.png").url)))
  }

  /**
   * Renders Details page
   * @param id Organisation ID
   */
  def details(id: Long) = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      orgService.findWithProfile(id).map { view ⇒
        val members = view.org.people
        val otherPeople = personService.findActive.filterNot(person ⇒ members.contains(person))
        val contributions = contributionService.contributions(id, isPerson = false)
        val products = productService.findAll
        val payments = view.org.member map { v ⇒
          paymentRecordService.findByOrganisation(id)
        } getOrElse List()
        Ok(views.html.v2.organisation.details(user, view, members, otherPeople,
          contributions, products, payments))
      }.getOrElse(NotFound)
  }

  /**
   * Render an Edit page
   * @param id Organisation ID
   */
  def edit(id: Long) = AsyncSecuredDynamicAction(DynamicRole.OrgMember, id) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      orgService.findWithProfile(id).map { view ⇒
        Future.successful(
          Ok(views.html.v2.organisation.form(user, Some(id),
            Organisations.organisationForm.fill(OrgView(view.org, view.profile)))))
      } getOrElse Future.successful(NotFound)
  }

  /**
   * List page.
   */
  def index = SecuredRestrictedAction(Admin) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      val organisations = orgService.findAll
      Ok(views.html.v2.organisation.index(user, organisations))
  }

  /**
   * Retrieve and cache a logo of the given organisation
   *
   * @param id Organisation identifier
   */
  def logo(id: Long) = file(Organisation.logo(id))

  /**
   * Returns name of the given organisation
   * @param id Organisation id
   */
  def name(id: Long) = AsyncSecuredRestrictedAction(Viewer) {
    implicit request => implicit handler => implicit user =>
      val name = if (id != 0)
        orgService.find(id) map { _.name } getOrElse ""
       else
        ""
      Future.successful(jsonOk(Json.obj("name" -> name)))
  }

  /**
   * Returns list of organisations for the given query
   *
   * @param query Search query
   */
  def search(query: Option[String]) = AsyncSecuredRestrictedAction(Viewer) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        implicit val orgWrites = new Writes[Organisation] {
          def writes(data: Organisation): JsValue = {
            Json.obj(
              "id" -> data.id,
              "name" -> data.name,
              "countryCode" -> data.countryCode)
          }
        }
        val orgs: List[Organisation] = query map { q ⇒
          if (q.length < 3)
            List()
          else
            orgService.search(q)
        } getOrElse List()
        Future.successful(jsonOk(Json.toJson(orgs)))
  }

  /**
   * Updates an organisation
   * @param id Organisation ID
   */
  def update(id: Long) = AsyncSecuredDynamicAction(DynamicRole.OrgMember, id) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        orgService.findWithProfile(id).map { view ⇒
          Organisations.organisationForm.bindFromRequest.fold(
            formWithErrors ⇒
              Future.successful(
                BadRequest(views.html.v2.organisation.form(user, Some(id), formWithErrors))),
            view ⇒ {
              val updatedOrg = view.org.
                copy(id = Some(id), active = view.org.active).
                copy(customerId = view.org.customerId)
              val updatedProfile = view.profile.forOrg.copy(objectId = id)
              orgService.update(OrgView(updatedOrg, updatedProfile))
              val log = activity(updatedOrg, user.person).updated.insert()
              Future.successful(
                Redirect(routes.Organisations.details(id)).flashing("success" -> log.toString))
            })
        } getOrElse Future.successful(NotFound)
  }

  /**
   * Upload a new logo to Amazon
   *
   * @param id Organisation identifier
   */
  def uploadLogo(id: Long) = AsyncSecuredDynamicAction(DynamicRole.OrgMember, id) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        uploadFile(Organisation.logo(id), "logo") map { _ ⇒
          orgService.updateLogo(id, true)
          val route = routes.Organisations.details(id).url
          jsonOk(Json.obj("link" -> routes.Organisations.logo(id).url))
        } recover {
          case e: RuntimeException ⇒ jsonBadRequest(e.getMessage)
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
      registrationNumber, profile, webSite, blog, customerId, about,
      logo, active, dateStamp) ⇒
        val org = Organisation(id, name, address.street1, address.street2,
          address.city, address.province, address.postCode, address.countryCode,
          vatNumber, registrationNumber, webSite,
          blog, customerId, about, logo, active, dateStamp)
        OrgView(org, profile)
    })({
      (v: OrgView) ⇒
        val address = Address(None, v.org.street1, v.org.street2,
          v.org.city, v.org.province, v.org.postCode, v.org.countryCode)
        Some((v.org.id, v.org.name, address, v.org.vatNumber,
          v.org.registrationNumber, v.profile, v.org.webSite,
          v.org.blog, v.org.customerId, v.org.about, v.org.logo, v.org.active,
          v.org.dateStamp))
    }))
}