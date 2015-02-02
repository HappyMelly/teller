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

import models.service.Services
import org.joda.time.LocalDate
import play.api.mvc.Controller
import play.api.libs.json.{ JsValue, Writes, Json }
import models.{ License, Brand, Person }
import scala.util.{ Failure, Success, Try }

/**
 * Content licenses API.
 */
trait LicensesApi extends Controller with ApiAuthentication with Services {

  case class LicenseeView(brandCode: String, licensee: Person)

  implicit def licenseeWrites = new Writes[LicenseeView] {
    def writes(view: LicenseeView): JsValue = {
      Json.obj(
        "href" -> view.licensee.id.map(licenseeId ⇒ routes.LicensesApi.licensee(licenseeId, view.brandCode).url),
        "first_name" -> view.licensee.firstName,
        "last_name" -> view.licensee.lastName,
        "country" -> view.licensee.address.countryCode)
    }
  }

  case class LicensedSinceView(licensee: Person, since: Option[LocalDate])

  implicit def licenseeDetailsWrites = new Writes[LicensedSinceView] {
    def writes(view: LicensedSinceView): JsValue = {
      Json.obj(
        "first_name" -> view.licensee.firstName,
        "last_name" -> view.licensee.lastName,
        "country" -> view.licensee.address.countryCode,
        "email_address" -> view.licensee.socialProfile.email,
        "twitter_handle" -> view.licensee.socialProfile.twitterHandle,
        "facebook_url" -> view.licensee.socialProfile.facebookUrl,
        "linkedin_url" -> view.licensee.socialProfile.linkedInUrl,
        "google_plus_url" -> view.licensee.socialProfile.googlePlusUrl,
        "bio" -> view.licensee.bio,
        "interests" -> view.licensee.interests,
        "valid" -> view.since.isDefined,
        "licensed_since" -> view.since)
    }
  }

  /**
   * API that returns a list of licensees for the given brand on the given date.
   */
  def licensees(brandCode: String, dateString: Option[String]) = TokenSecuredAction { implicit request ⇒
    if (Brand.exists(brandCode)) {
      val date = Try(dateString.map(d ⇒ new LocalDate(d)).getOrElse(LocalDate.now()))
      date match {
        case Success(d) ⇒ {
          val licensees = License.licensees(brandCode, d).map(licensee ⇒ LicenseeView(brandCode, licensee))
          Ok(Json.toJson(licensees))
        }
        case Failure(e) ⇒ BadRequest("Invalid date")
      }

    } else NotFound("Unknown brand")
  }

  /**
   * API that returns details of a licensee for a brand.
   */
  def licensee(licenseeId: Long, brandCode: String) = TokenSecuredAction { implicit request ⇒
    personService.find(licenseeId).map { person ⇒
      if (Brand.exists(brandCode)) {
        val view = LicensedSinceView(person, License.licensedSince(licenseeId, brandCode))
        Ok(Json.toJson(view))
      } else NotFound("Unknown brand")
    }.getOrElse(NotFound("Unknown licensee"))
  }
}

object LicensesApi extends LicensesApi with ApiAuthentication with Services
