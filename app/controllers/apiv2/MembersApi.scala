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

package controllers.apiv2

import models._
import models.service.Services
import play.api.libs.json._
import play.mvc.Controller

trait MembersApi extends Controller with ApiAuthentication with Services {

  /**
   * Implicit conversion of Member used in lists
   */
  implicit val memberSummaryWrites = new Writes[Member] {
    def writes(member: Member): JsValue = {
      Json.obj(
        "id" -> member.id,
        "name" -> member.name,
        "type" -> readableMemberType(member),
        "funder" -> member.funder,
        "image" -> {
          if (member.person)
            member.image
          else
            controllers.routes.Organisations.logo(member.objectId).url
        })
    }
  }

  import PeopleApi.personDetailsWrites

  val personMemberWrites = new Writes[Member] {
    def writes(member: Member): JsValue = {
      Json.obj(
        "id" -> member.id.get,
        "funder" -> member.funder,
        "type" -> readableMemberType(member),
        "reason" -> member.reason,
        "person" -> Json.toJson(member.memberObj._1.get)(personDetailsWrites))
    }
  }

  import PeopleApi.{ personWrites, addressWrites }
  import ContributionsApi.contributionWrites

  val organisationDetailsWrites = new Writes[OrgView] {
    def writes(view: OrgView): JsValue = {
      val address = Address(None, view.org.street1, view.org.street2,
        view.org.city, view.org.province, view.org.postCode, view.org.countryCode)

      Json.obj(
        "logo" -> controllers.routes.Organisations.logo(view.org.id.get).url,
        "name" -> view.org.name,
        "about" -> view.org.about,
        "address" -> Json.toJson(address),
        "vat_number" -> view.org.vatNumber,
        "registration_number" -> view.org.registrationNumber,
        "website" -> view.org.webSite,
        "twitter_handle" -> view.profile.twitterHandle,
        "facebook_url" -> view.profile.facebookUrl,
        "linkedin_url" -> view.profile.linkedInUrl,
        "google_plus_url" -> view.profile.googlePlusUrl,
        "members" -> view.org.people,
        "contributions" -> view.org.contributions)
    }
  }

  case class MemberOrgView(member: Member, orgView: OrgView)

  val orgMemberWrites = new Writes[MemberOrgView] {
    def writes(view: MemberOrgView): JsValue = {
      Json.obj(
        "id" -> view.member.id,
        "funder" -> view.member.funder,
        "type" -> readableMemberType(view.member),
        "org" -> Json.toJson(view.orgView)(organisationDetailsWrites))
    }
  }

  /**
   * Returns a list of active members in JSON format
   *
   * @param funder If true, returns funders; false - supporters; none - all
   */
  def members(funder: Option[Boolean] = None) = TokenSecuredAction(readWrite = false) {
    implicit request ⇒
      implicit token ⇒
        val members = memberService.findAll.filter(_.active)
        val filteredMembers = funder map { x ⇒ members.filter(_.funder == x)
        } getOrElse members
        jsonOk(Json.toJson(filteredMembers.sortBy(_.name)))
  }

  /**
   * Returns member's data in JSON format if it exists
   * @param id Member identifier
   */
  def member(id: Long) = TokenSecuredAction(readWrite = false) {
    implicit request ⇒
      implicit token ⇒
        memberService.find(id) map { member ⇒
          if (member.person) {
            jsonOk(Json.toJson(member)(personMemberWrites))
          } else {
            orgService.findWithProfile(member.objectId) map { x ⇒
              jsonOk(Json.toJson(MemberOrgView(member, x))(orgMemberWrites))
            } getOrElse jsonNotFound("Organisation does not exist")
          }
        } getOrElse jsonNotFound("Member does not exist")
  }

  /**
   * Returns 'person' if the given member is a person, otherwise - 'org'
   * @param member Member object
   */
  private def readableMemberType(member: Member): String = {
    if (member.person)
      "person"
    else
      "org"
  }
}

object MembersApi extends MembersApi with ApiAuthentication with Services
