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

import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import models.UserRole.Role
import models._
import models.repository.Repositories
import org.joda.time.LocalDate
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.MessagesApi
import play.api.libs.json._
import services.TellerRuntimeEnvironment

import scala.concurrent.Future

/**
 * Facilitators pages
 */
class Facilitators @Inject() (override implicit val env: TellerRuntimeEnvironment,
                              override val messagesApi: MessagesApi,
                              val services: Repositories,
                              deadbolt: DeadboltActions, handlers: HandlerCache, actionBuilder: ActionBuilders)
  extends Security(deadbolt, handlers, actionBuilder, services)(messagesApi, env)
  with BrandAware {

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
        "memberships" -> data.organisations(services))
    }
  }

  /**
    * Add a new country to a facilitator
    *
    * @param id Person identifier
    */
  def addCountry(id: Long) = ProfileAction(id) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    val membershipForm = Form(single("country" -> nonEmptyText))

    membershipForm.bindFromRequest.fold(
      errors ⇒ badRequest("Country is not chosen"),
      country ⇒
        (for {
          person <- services.person.find(id)
          countries <- services.facilitator.countries(id)
        } yield (person, countries)) flatMap {
          case (None, _) => notFound("Person not found")
          case (Some(person), countries) =>
            if (!countries.exists(_.country == country)) {
              services.facilitator.insertCountry(FacilitatorCountry(id, country))
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
  def addLanguage(id: Long) = ProfileAction(id) { implicit request ⇒ implicit handler ⇒ implicit user ⇒

    val membershipForm = Form(single("language" -> nonEmptyText))
    membershipForm.bindFromRequest.fold(
      errors ⇒ badRequest("Language is not chosen"),
      language ⇒
        (for {
          person <- services.person.find(id)
          languages <- services.facilitator.languages(id)
        } yield (person, languages)) flatMap {
          case (None, _) => notFound("Person not found")
          case (Some(person), languages) ⇒
            if (!languages.exists(_.language == language)) {
              services.facilitator.insertLanguage(FacilitatorLanguage(id, language))
              val query = for {
                profile <- services.profileStrength.find(id, false) if profile.isDefined
              } yield profile.get
              query map { profile =>
                services.profileStrength.update(profile.markComplete("language"))
              }
            }
            val msg = "New language for facilitator was added"
            val url: String = routes.People.details(id).url + "#facilitation"
            redirect(url, "success" -> msg)
        }
      )
  }

  /**
    * Retrieves badges for the given facilitator
    *
    * @param personId Facilitator identifier
    * @param brandId Brand identifier
    */
  def badges(personId: Long, brandId: Long) = RestrictedAction(Role.Viewer) { implicit request =>
    implicit handler => implicit user =>
      (for {
        f <- services.facilitator.find(brandId, personId)
        b <- services.brandBadge.findByBrand(brandId)
      } yield (f, b)) flatMap {
        case (None, _) => notFound("Facilitator not found")
        case (Some(facilitator), badges) =>
          val filteredBadges = badges.filter(badge => facilitator.badges.contains(badge.id.get))
          ok(views.html.v2.facilitator.badges(filteredBadges))
      }
  }

  /**
    * Remove a country from a facilitator
    *
    * @param id Person identifier
    * @param country Two-letters country identifier
    */
  def deleteCountry(id: Long, country: String) = ProfileAction(id) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      services.facilitator.deleteCountry(id, country) flatMap { _ =>
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
  def deleteLanguage(id: Long, language: String) = ProfileAction(id) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      services.facilitator.deleteLanguage(id, language) flatMap { _ =>
        services.profileStrength.find(id, org = false) map {
          case None => Future.successful(None)
          case Some(profile) =>
            services.profileStrength.update(profile.markIncomplete("language"))
        }
        val url: String = routes.People.details(id).url + "#facilitation"
        redirect(url, "success" -> "Language was deleted from facilitator")
      }
  }

  def details(id: Long, brandId: Long) = RestrictedAction(Role.Coordinator) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      (for {
        facilitator <- services.facilitator.find(brandId, id)
        badges <- services.brandBadge.findByBrand(brandId)
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
  def index(brandId: Long) = RestrictedAction(List(Role.Facilitator, Role.Coordinator)) {
    implicit request => implicit handler => implicit user =>
      (for {
        licenses <- services.license.findByBrand(brandId)
        facilitators <- services.facilitator.findByBrand(brandId)
        people <- services.person.find(licenses.map(_.licenseeId))
        _ <- services.person.collection.addresses(people)
        badges <- services.brandBadge.findByBrand(brandId)
      } yield (licenses, facilitators, people, badges)) flatMap { case (licenses, facilitatorData, people, badges) =>
        roleDiffirentiator(user.account, Some(brandId)) { (view, brands) =>
          val facilitators = licenses.map { license =>
            val person = people.find(_.identifier == license.licenseeId).get
            val lastMonth = LocalDate.now().minusMonths(1)
            val joinedLastMonth = equalMonths(license.start, lastMonth)
            val leftLastMonth = equalMonths(license.end, lastMonth)
            val data = facilitatorData.find(_.personId == license.licenseeId).getOrElse {
              Facilitator(None, license.licenseeId, brandId)
            }
            val facilitatorBadges = badges.filter(x => data.badges.contains(x.id.get))
            (license, person, data, joinedLastMonth, leftLastMonth, facilitatorBadges)
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
            val facilitatorBadges = badges.filter(x => data.badges.contains(x.id.get))
            (license, person, data, sameCountry, isNew, facilitatorBadges)
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
  def list(brandId: Long) = RestrictedAction(Role.Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      val collator = Collator.getInstance(Locale.ENGLISH)
      val ord = new Ordering[String] {
        def compare(x: String, y: String) = collator.compare(x, y)
      }
      services.license.licensees(brandId, LocalDate.now()) flatMap { facilitators =>
        if (facilitators.nonEmpty) {
          services.person.collection.organisations(facilitators)
        }
        ok(Json.toJson(facilitators.sortBy(_.fullName.toLowerCase)(ord)))
      }
  }

  /**
    * Updates badges for the given facilitator
    *
    * @param personId Facilitator identifier
    * @param brandId Brand identifier
    */
  def updateBadges(personId: Long, brandId: Long) = BrandAction(brandId) { implicit request =>
    implicit handler => implicit user =>
      val form = Form(single("badges" -> play.api.data.Forms.list(longNumber)))
      form.bindFromRequest.fold(
        errors => jsonBadRequest("'badges' field doesn't exist"),
        badges =>
          (for {
            f <- services.facilitator.find(brandId, personId)
            b <- services.brandBadge.findByBrand(brandId)
          } yield (f, b.map(_.id.get))) flatMap {
            case (None, _) => jsonNotFound("Facilitator not found")
            case (Some(facilitator), brandBadges) =>
              val valueToUpdate = facilitator.copy(badges = badges.filter(x => brandBadges.contains(x)))
              services.facilitator.updateBadges(valueToUpdate) flatMap { _ =>
                jsonSuccess("Badges were updated")
              }

          }
      )
  }

  protected def equalMonths(left: LocalDate, right: LocalDate): Boolean = {
    left.getYear == right.getYear && left.getMonthOfYear == right.getMonthOfYear
  }
}
