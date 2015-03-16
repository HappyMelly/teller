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

package controllers.api

import java.net.URLDecoder

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
        "id" -> member.id.get,
        "name" -> member.name,
        "type" -> readableMemberType(member),
        "funder" -> member.funder,
        "image" -> member.image)
    }
  }

  import controllers.api.PeopleApi.personDetailsWrites

  val personMemberWrites = new Writes[Member] {
    def writes(member: Member): JsValue = {
      Json.obj(
        "id" -> member.id.get,
        "funder" -> member.funder,
        "type" -> readableMemberType(member),
        "person" -> Json.toJson(member.memberObj._1.get)(personDetailsWrites))
    }
  }

  import controllers.api.OrganisationsApi.organisationDetailsWrites

  val orgMemberWrites = new Writes[Member] {
    def writes(member: Member): JsValue = {
      Json.obj(
        "id" -> member.id.get,
        "funder" -> member.funder,
        "type" -> readableMemberType(member),
        "org" -> Json.toJson(member.memberObj._2.get)(organisationDetailsWrites))
    }
  }

  /**
   * Returns a list of active members in JSON format
   * @param funder If true, returns funders; false - supporters; none - all
   */
  def members(funder: Option[Boolean] = None) = TokenSecuredAction { implicit request ⇒
    val members = memberService.findAll.filter(_.active)
    val filteredMembers = funder map { value ⇒ members.filter(_.funder == value)
    } getOrElse members
    Ok(Json.prettyPrint(Json.toJson(filteredMembers.sortBy(_.name))))
  }

  /**
   * Returns member's data in JSON format if it exists
   * @param id Member identifier
   */
  def member(id: Long) = TokenSecuredAction { implicit request ⇒
    memberService.find(id) map { member ⇒
      if (member.person)
        Ok(Json.prettyPrint(Json.toJson(member)(personMemberWrites)))
      else
        Ok(Json.prettyPrint(Json.toJson(member)(orgMemberWrites)))
    } getOrElse BadRequest(Json.obj("error" -> "Not found"))
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
