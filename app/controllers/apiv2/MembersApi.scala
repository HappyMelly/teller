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

import java.net.URLDecoder

import controllers.apiv2.PeopleApi._
import controllers.{Experiments, Organisations, Utilities}
import models._
import models.service.Services
import play.api.libs.json._
import views.Countries

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


trait MembersApi extends ApiAuthentication with Services with Utilities {

  case class MemberView(member: Member, country: String)
  case class MemberPersonView(member: Member, experiments: List[Experiment])
  case class MemberOrgView(member: Member,
                           experiments: List[Experiment],
                           orgView: OrgView)
  
  /**
   * Implicit conversion of MemberView used in lists
   */
  implicit val memberSummaryWrites = new Writes[MemberView] {
    def writes(view: MemberView): JsValue = {
      Json.obj(
        "id" -> view.member.id,
        "name" -> view.member.name,
        "type" -> readableMemberType(view.member),
        "funder" -> view.member.funder,
        "country" -> view.country,
        "image" -> memberImageUrl(view.member))
    }
  }

  implicit val experimentWrites = new Writes[Experiment] {
    def writes(experiment: Experiment): JsValue = {
      Json.obj(
        "name" -> experiment.name,
        "description" -> experiment.description,
        "url" -> experiment.url,
        "image" -> Experiments.pictureUrl(experiment))
    }
  }


  import ContributionsApi.contributionWrites
  import OrganisationsApi.organisationWrites

  val personDetailsWrites = new Writes[Person] {
    def writes(person: Person) = {
      Json.obj(
        "id" -> person.id.get,
        "unique_name" -> person.uniqueName,
        "first_name" -> person.firstName,
        "last_name" -> person.lastName,
        "email_address" -> person.email,
        "image" -> person.photo.url,
        "address" -> person.address,
        "bio" -> person.bio,
        "interests" -> person.interests,
        "twitter_handle" -> person.socialProfile.twitterHandle,
        "facebook_url" -> person.socialProfile.facebookUrl,
        "linkedin_url" -> person.socialProfile.linkedInUrl,
        "google_plus_url" -> person.socialProfile.googlePlusUrl,
        "website" -> person.webSite,
        "blog" -> person.blog,
        "active" -> person.active,
        "organizations" -> person.organisations,
        "contributions" -> person.contributions)
    }
  }
  
  val personMemberWrites = new Writes[MemberPersonView] {
    def writes(view: MemberPersonView): JsValue = {
      Json.obj(
        "id" -> view.member.id,
        "funder" -> view.member.funder,
        "type" -> readableMemberType(view.member),
        "reason" -> view.member.reason,
        "experiments" -> view.experiments,
        "person" -> Json.toJson(view.member.memberObj._1.get)(personDetailsWrites))
    }
  }


  val organisationDetailsWrites = new Writes[OrgView] {
    def writes(view: OrgView): JsValue = {
      val address = Address(None, view.org.street1, view.org.street2,
        view.org.city, view.org.province, view.org.postCode, view.org.countryCode)

      Json.obj(
        "image" -> orgImageUrl(view.org),
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


  val orgMemberWrites = new Writes[MemberOrgView] {
    def writes(view: MemberOrgView): JsValue = {
      Json.obj(
        "id" -> view.member.id,
        "funder" -> view.member.funder,
        "type" -> readableMemberType(view.member),
        "reason" -> view.member.reason,
        "experiments" -> view.experiments,
        "org" -> Json.toJson(view.orgView)(organisationDetailsWrites))
    }
  }

  /**
   * Returns a list of active members in JSON format
   *
   * @param funder If true, returns funders; false - supporters; none - all
   */
  def members(funder: Option[Boolean] = None) = TokenSecuredAction(readWrite = false) { implicit request ⇒
    implicit token ⇒
      memberService.findAll flatMap { members =>
        val activeMembers = members.filter(_.active)
        val filteredMembers = funder map { x ⇒ activeMembers.filter(_.funder == x) } getOrElse members
        val views = filteredMembers.map(member => MemberView(member, Countries.name(member.countryCode)))
        jsonOk(Json.toJson(views.sortBy(_.member.name)))
      }
  }

  /**
   * Returns list of members for the given query
   * @param query List of member names separated by commas
   */
  def membersByNames(query: String) = TokenSecuredAction(readWrite = false) { implicit request => implicit token =>
    val names = query.split(",").map(name => URLDecoder.decode(name, "ASCII"))
    (for {
      p <- personService.findByNames(names.toList)
      m <- memberService.findByObjects(p.map(_.identifier))
    } yield (p, m)) flatMap { case (people, members) =>
      personService.collection.addresses(people)
      val filteredMembers = members.filter(_.person)
      val views = filteredMembers.map { member =>
        people.find(_.identifier == member.objectId) map { person =>
          member.memberObj_=(person)
          MemberView(member, Countries.name(person.address.countryCode))
        } getOrElse MemberView(member, "")
      }
      jsonOk(Json.toJson(views.sortBy(_.member.name)))
    }
  }

  /**
   * Returns member's data in JSON format if it exists
   * @param identifier Member identifier
   * @param person Member is a person if true, otherwise - organisation
   */
  def member(identifier: String, person: Boolean = true) = TokenSecuredAction(readWrite = false) { implicit request ⇒
    implicit token ⇒
      findMemberByIdentifier(identifier, person) flatMap {
        case None => jsonNotFound("Member not found")
        case Some(member) =>
          experimentService.findByMember(member.identifier) flatMap { experiments =>
            if (member.person) {
              jsonOk(Json.toJson(MemberPersonView(member, experiments))(personMemberWrites))
            } else {
              orgService.findWithProfile(member.objectId) flatMap {
                case None => jsonNotFound("Organisation not found")
                case Some(x) =>  jsonOk(Json.toJson(MemberOrgView(member, experiments, x))(orgMemberWrites))
              }
            }

          }
      }
  }

  /**
   * Returns member by the name of
   * @param identifier Object identifier
   * @param person Member is a person if true, otherwise - organisation
   * @return
   */
  protected def findMemberByIdentifier(identifier: String, person: Boolean): Future[Option[Member]] = {
    try {
      val id = identifier.toLong
      memberService.find(id)
    } catch {
      case e: NumberFormatException ⇒ {
        if (person)
          personService.find(URLDecoder.decode(identifier, "ASCII")) flatMap {
            case None => Future.successful(None)
            case Some(value) =>
              memberService.findByObject(value.identifier, person = true) flatMap {
                case None => Future.successful(None)
                case Some(member) =>
                  member.memberObj_=(value)
                  Future.successful(Some(member))
              }
          }
        else
          orgService.find(URLDecoder.decode(identifier, "ASCII")) flatMap {
            case None => Future.successful(None)
            case Some(org) =>
              memberService.findByObject(org.identifier, person = false) flatMap {
                case None => Future.successful(None)
                case Some(member) =>
                  member.memberObj_=(org)
                  Future.successful(Some(member))
              }
          }
      }
    }    
  }

  /**
   * Returns image url for the given member depending on its type
   *
   * @param member Member object
   */
  protected def memberImageUrl(member: Member): Option[String] = {
    if (member.person)
      member.image
    else if (member.memberObj._2.get.logo)
      Organisations.logoUrl(member.objectId)
    else
      None
  }

  /**
   * Returns image url for the given org
   *
   * @param org Organisation
   */
  protected def orgImageUrl(org: Organisation): Option[String] = {
    if (org.logo)
      Organisations.logoUrl(org.identifier)
    else
      None
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
