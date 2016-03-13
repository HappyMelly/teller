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
 * If you have questions concerning this license or the applicable additional
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */
package controllers.api

import java.net.URLDecoder
import javax.inject.Inject

import controllers.api.json.{ContributionConverter, PersonConverter}
import controllers.community.Experiments
import controllers.core.Organisations
import models._
import models.repository.Repositories
import play.api.i18n.MessagesApi
import play.api.libs.json._
import views.Countries

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class MembersApi @Inject() (val services: Repositories,
                            override val messagesApi: MessagesApi)
  extends ApiAuthentication(services, messagesApi) {

  case class MemberView(member: Member, country: String)
  case class MemberPersonView(member: Member, experiments: List[Experiment], person: PersonView)
  case class PersonView(person: Person, organisations: List[Organisation], contributions: List[ContributionView])
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
        "since" -> view.member.since,
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

  implicit val addressWrites = (new PersonConverter).addressWrites
  implicit val contributionWrites = (new ContributionConverter).contributionWrites
  implicit val personWrites = (new PersonConverter).personWrites
  import OrganisationsApi.organisationWrites

  val personDetailsWrites = new Writes[PersonView] {
    def writes(view: PersonView) = {
      Json.obj(
        "id" -> view.person.id.get,
        "unique_name" -> view.person.uniqueName,
        "first_name" -> view.person.firstName,
        "last_name" -> view.person.lastName,
        "email_address" -> view.person.email,
        "image" -> view.person.photo.url,
        "address" -> view.person.address,
        "bio" -> view.person.bio,
        "interests" -> view.person.interests,
        "twitter_handle" -> view.person.profile.twitterHandle,
        "facebook_url" -> view.person.profile.facebookUrl,
        "linkedin_url" -> view.person.profile.linkedInUrl,
        "google_plus_url" -> view.person.profile.googlePlusUrl,
        "website" -> view.person.webSite,
        "blog" -> view.person.blog,
        "active" -> view.person.active,
        "organizations" -> view.organisations,
        "contributions" -> view.contributions)
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
        "since" -> view.member.since,
        "person" -> Json.toJson(view.person)(personDetailsWrites))
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
        "members" -> view.members,
        "contributions" -> view.contributions)
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
        "since" -> view.member.since,
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
      services.member.findAll flatMap { members =>
        val activeMembers = members.filter(_.active)
        val filteredMembers = funder map { x ⇒ activeMembers.filter(_.funder == x) } getOrElse activeMembers
        val views = filteredMembers.map(member => MemberView(member, Countries.name(member.countryCode)))
        jsonOk(Json.toJson(views.sortBy(_.member.name)))
      }
  }

  /**
   * Returns list of members for the given query
    *
    * @param query List of member names separated by commas
   */
  def membersByNames(query: String) = TokenSecuredAction(readWrite = false) { implicit request => implicit token =>
    val names = query.split(",").map(name => URLDecoder.decode(name, "ASCII"))
    (for {
      p <- services.person.findByNames(names.toList)
      m <- services.member.findByObjects(p.map(_.identifier))
    } yield (p, m)) flatMap { case (people, members) =>
      services.person.collection.addresses(people) flatMap { _ =>
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
  }

  /**
   * Returns member's data in JSON format if it exists
    *
    * @param identifier Member identifier
   * @param isPerson Member is a person if true, otherwise - organisation
   */
  def member(identifier: String, isPerson: Boolean = true) = TokenSecuredAction(readWrite = false) { implicit request ⇒
    implicit token ⇒
      findMemberByIdentifier(identifier, isPerson) flatMap {
        case None => jsonNotFound("Member not found")
        case Some(member) =>
          services.experiment.findByMember(member.identifier) flatMap { experiments =>
            if (member.person) {
              (for {
                p <- services.person.findComplete(member.objectId)
                o <- services.person.memberships(member.objectId)
                c <- services.contribution.contributions(member.objectId, isPerson = true)
              } yield (p, o, c)) flatMap {
                case (None, _, _) => jsonNotFound("Person not found")
                case (Some(person), organisations, contributions) =>
                  val view = MemberPersonView(member, experiments, PersonView(person, organisations, contributions))
                  jsonOk(Json.toJson(view)(personMemberWrites))
              }
            } else {
              (for {
                o <- services.org.findWithProfile(member.objectId)
                m <- services.org.people(member.objectId)
                c <- services.contribution.contributions(member.objectId, isPerson = false)
                _ <- services.person.collection.addresses(m)
              } yield (o, m, c)) flatMap {
                case (None, _, _) => jsonNotFound("Organisation not found")
                case (Some(view), members, contributions) =>
                  val org = view.copy(members = members, contributions = contributions)
                  jsonOk(Json.toJson(MemberOrgView(member, experiments, org))(orgMemberWrites))
              }
            }

          }
      }
  }

  /**
   * Returns member by the name of
    *
    * @param identifier Object identifier
   * @param person Member is a person if true, otherwise - organisation
   * @return
   */
  protected def findMemberByIdentifier(identifier: String, person: Boolean): Future[Option[Member]] = {
    try {
      val id = identifier.toLong
      services.member.find(id)
    } catch {
      case e: NumberFormatException ⇒ {
        if (person)
          services.person.find(URLDecoder.decode(identifier, "ASCII")) flatMap {
            case None => Future.successful(None)
            case Some(value) =>
              services.member.findByObject(value.identifier, person = true) flatMap {
                case None => Future.successful(None)
                case Some(member) =>
                  member.memberObj_=(value)
                  Future.successful(Some(member))
              }
          }
        else
          services.org.find(URLDecoder.decode(identifier, "ASCII")) flatMap {
            case None => Future.successful(None)
            case Some(org) =>
              services.member.findByObject(org.identifier, person = false) flatMap {
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
    *
    * @param member Member object
   */
  private def readableMemberType(member: Member): String = {
    if (member.person)
      "person"
    else
      "org"
  }

}

