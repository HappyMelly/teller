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

import java.text.Collator
import java.util.Locale
import javax.inject.Inject

import models.UserRole.Role
import models._
import models.service.Services
import org.joda.time.LocalDate
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json._
import services.TellerRuntimeEnvironment

import scala.concurrent.Future


/**
 * Facilitators pages
 */
class Facilitators @Inject() (override implicit val env: TellerRuntimeEnvironment)
    extends AsyncController
    with Security
    with Services
    with Utilities {

  implicit val organizationWrites = new Writes[Organisation] {
    def writes(data: Organisation): JsValue = {
      Json.obj(
        "id" -> data.id.get,
        "name" -> data.name)
    }
  }

  implicit val personWrites = new Writes[Person] {
    def writes(data: Person): JsValue = {
      Json.obj(
        "first_name" -> data.firstName,
        "last_name" -> data.lastName,
        "id" -> data.id.get,
        "memberships" -> data.organisations)
    }
  }

  /**
    * Adds new badge to the given facilitator
    *
    * @param personId Facilitator identifier
    * @param brandId Brand identifier
    * @param badgeId Badge identifier
    */
  def addBadge(personId: Long, brandId: Long, badgeId: Long) = AsyncSecuredBrandAction(brandId) { implicit request =>
    implicit handler => implicit user =>
      brandBadgeService.find(badgeId) flatMap {
        case None => jsonNotFound("Badge not found")
        case Some(badge) =>
          if (badge.brandId == brandId) {
            facilitatorService.find(brandId, personId) flatMap {
              case None => jsonBadRequest("Impossible to add badge to a person without license")
              case Some(facilitator) =>
                if (!facilitator.badges.contains(badgeId)) {
                  facilitatorService.update(facilitator.copy(badges = facilitator.badges :+ badgeId))
                }
                jsonSuccess("Badge was added")
            }
          } else {
            jsonNotFound("Badge not found")
          }
      }
    }

  /**
    * Add a new country to a facilitator
    *
    * @param id Person identifier
    */
  def addCountry(id: Long) = AsyncSecuredProfileAction(id) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    val membershipForm = Form(single("country" -> nonEmptyText))

    membershipForm.bindFromRequest.fold(
      errors ⇒ badRequest("Country is not chosen"),
      country ⇒
        (for {
          person <- personService.find(id)
          countries <- facilitatorService.countries(id)
        } yield (person, countries)) flatMap {
          case (None, _) => notFound("Person not found")
          case (Some(person), countries) =>
            if (!countries.exists(_.country == country)) {
              facilitatorService.insertCountry(FacilitatorCountry(id, country))
            }
            val msg ="New country for facilitator was added"
            val url: String = routes.People.details(id).url + "#facilitation"
            redirect(url, "success" -> msg)
        }
      )
  }

  /**
   * Add a new language to a facilitator
   *
   * @param id Person identifier
   */
  def addLanguage(id: Long) = AsyncSecuredProfileAction(id) { implicit request ⇒ implicit handler ⇒ implicit user ⇒

    val membershipForm = Form(single("language" -> nonEmptyText))
    membershipForm.bindFromRequest.fold(
      errors ⇒ badRequest("Language is not chosen"),
      language ⇒
        (for {
          person <- personService.find(id)
          languages <- facilitatorService.languages(id)
        } yield (person, languages)) flatMap {
          case (None, _) => notFound("Person not found")
          case (Some(person), languages) ⇒
            if (!languages.exists(_.language == language)) {
              facilitatorService.insertLanguage(FacilitatorLanguage(id, language))
              profileStrengthService.find(id, false) flatMap { case Some(profile) ⇒
                profileStrengthService.update(profile.markComplete("language"))
              }
            }
            val msg = "New language for facilitator was added"
            val url: String = routes.People.details(id).url + "#facilitation"
            redirect(url, "success" -> msg)
        }
      )
  }

  /**
    * Deletes badge from the given facilitator
    *
    * @param personId Facilitator identifier
    * @param brandId Brand identifier
    * @param badgeId Badge identifier
    */
  def deleteBadge(personId: Long, brandId: Long, badgeId: Long) = AsyncSecuredBrandAction(brandId) { implicit request =>
    implicit handler => implicit user =>
      (for {
        badge <- brandBadgeService.find(badgeId)
        facilitator <- facilitatorService.find(brandId, personId)
      } yield (badge, facilitator)) flatMap {
        case (None, _) => jsonNotFound("Badge not found")
        case (_, None) => jsonBadRequest("Impossible to remove badge to a person without license")
        case (Some(badge), Some(facilitator)) =>
          if (badge.brandId == brandId) {
            if (facilitator.badges.contains(badgeId)) {
              facilitatorService.update(facilitator.copy(badges = facilitator.badges.filterNot(_ == badgeId)))
            }
            jsonSuccess("Badge was removed")
          } else {
            jsonNotFound("Badge not found")
          }
      }
  }

  /**
    * Remove a country from a facilitator
    *
    * @param id Person identifier
    * @param country Two-letters country identifier
    */
  def deleteCountry(id: Long, country: String) = AsyncSecuredProfileAction(id) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      facilitatorService.deleteCountry(id, country) flatMap { _ =>
        val url: String = routes.People.details(id).url + "#facilitation"
        redirect(url, "success" -> "Country was deleted from facilitator")
      }
  }

  /**
   * Remove the given language from the given facilitator
   *
   * @param id Person identifier
   * @param language Two-letters language identifier
   */
  def deleteLanguage(id: Long, language: String) = AsyncSecuredProfileAction(id) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      facilitatorService.deleteLanguage(id, language) flatMap { _ =>
        val url: String = routes.People.details(id).url + "#facilitation"
        redirect(url, "success" -> "Language was deleted from facilitator")
      }
  }

  def details(id: Long, brandId: Long) = AsyncSecuredRestrictedAction(Role.Coordinator) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      (for {
        facilitator <- facilitatorService.find(brandId, id)
        badges <- brandBadgeService.findByBrand(brandId)
      } yield (facilitator, badges)) flatMap {
        case (None, _) => notFound("Facilitator not found")
        case (Some(facilitator), badges) =>
          ok(views.html.v2.facilitator.details(badges, facilitator))
      }
  }

  /**
    * Returns list of facilitators for the given brand
    *
    * @param brandId Brand identifier
    */
  def index(brandId: Long) = AsyncSecuredRestrictedAction(List(Role.Facilitator, Role.Coordinator)) {
    implicit request => implicit handler => implicit user =>
      (for {
        licenses <- licenseService.findByBrand(brandId)
        facilitators <- facilitatorService.findByBrand(brandId)
        people <- personService.find(licenses.map(_.licenseeId))
      } yield (licenses, facilitators, people)) flatMap { case (licenses, facilitatorData, people) =>
        personService.collection.addresses(people)
        roleDiffirentiator(user.account, Some(brandId)) { (view, brands) =>
          val facilitators = licenses.map { license =>
            val person = people.find(_.identifier == license.licenseeId).get
            val lastMonth = LocalDate.now().minusMonths(1)
            val joinedLastMonth = equalMonths(license.start, lastMonth)
            val leftLastMonth = equalMonths(license.end, lastMonth)
            val data = facilitatorData.find(_.personId == license.licenseeId).getOrElse {
              Facilitator(None, license.licenseeId, brandId)
            }
            (license, person, data, joinedLastMonth, leftLastMonth)
          }
          ok(views.html.v2.facilitator.forBrandCoordinators(user, view.brand, brands, facilitators))
        } { (view, brands) =>
          val facilitators = licenses.map { license =>
            val person = people.find(_.identifier == license.licenseeId).get
            val sameCountry = person.address.countryCode == user.person.address.countryCode
            val isNew = license.start.isAfter(LocalDate.now.minusMonths(3))
            val data = facilitatorData.find(_.personId == license.licenseeId).getOrElse {
              Facilitator(None, license.licenseeId, brandId)
            }
            (license, person, data, sameCountry, isNew)
          }
          ok(views.html.v2.facilitator.forFacilitators(user, view.get.brand, brands, facilitators))
        } {
          redirect(routes.Dashboard.index())
        }
      }
  }

  /**
    * Returns a list of facilitators for the given brand on today,
    * including the coordinator of the brand
    */
  def list(brandId: Long) = AsyncSecuredRestrictedAction(Role.Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      val collator = Collator.getInstance(Locale.ENGLISH)
      val ord = new Ordering[String] {
        def compare(x: String, y: String) = collator.compare(x, y)
      }
      licenseService.licensees(brandId, LocalDate.now()) flatMap { facilitators =>
        if (facilitators.nonEmpty) {
          personService.collection.organisations(facilitators)
        }
        ok(Json.toJson(facilitators.sortBy(_.fullName.toLowerCase)(ord)))
      }
  }

  protected def equalMonths(left: LocalDate, right: LocalDate): Boolean = {
    left.getYear == right.getYear && left.getMonthOfYear == right.getMonthOfYear
  }
}
