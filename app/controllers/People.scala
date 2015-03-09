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

package controllers

import controllers.Forms._
import fly.play.s3.{ BucketFile, S3Exception }
import models.UserRole.Role._
import models._
import models.payment.{ GatewayWrapper, PaymentException, RequestException }
import models.service.Services
import org.joda.time.DateTime
import play.api.{ Logger, Play }
import play.api.Play.current
import play.api.cache.Cache
import play.api.data.Forms._
import play.api.data.format.Formatter
import play.api.data.validation.Constraints
import play.api.data.{ Form, FormError }
import play.api.i18n.Messages
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc._
import scravatar.Gravatar
import services.S3Bucket

import scala.concurrent.Future
import scala.io.Source

trait People extends Controller with Security with Services {

  val contentType = "image/jpeg"

  /**
   * This formatter is used to create a photo object based on its type
   */
  val photoFormatter = new Formatter[Photo] {

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Photo] = {
      data.getOrElse("photo", "") match {
        case "facebook" ⇒
          ("""[\w\.]+$""".r findFirstIn data.getOrElse("profile.facebookUrl", "")).map { userId ⇒
            Right(Photo(Some("facebook"), Some("http://graph.facebook.com/" + userId + "/picture?type=large")))
          }.getOrElse(Left(List(FormError("profile.facebookUrl", "Profile URL is invalid. It can't be used to retrieve a photo"))))
        case "gravatar" ⇒
          data.get("emailAddress").map { email ⇒
            Right(Photo(Some("gravatar"), Some(Gravatar(email, ssl = true).size(300).avatarUrl)))
          }.getOrElse(Right(Photo(None, None)))
        case _ ⇒ Right(Photo(None, None))
      }
    }

    override def unbind(key: String, value: Photo): Map[String, String] = {
      Map(key -> value.id.getOrElse(""))
    }
  }

  /**
   * Formatter used to define a form mapping for the `PersonRole` enumeration.
   */
  implicit def personRoleFormat: Formatter[PersonRole.Value] = new Formatter[PersonRole.Value] {

    def bind(key: String, data: Map[String, String]) = {
      try {
        data.get(key).map(PersonRole.withName).toRight(Seq.empty)
      } catch {
        case e: NoSuchElementException ⇒ Left(Seq(FormError(key, "error.invalid")))
      }
    }

    def unbind(key: String, value: PersonRole.Value) = Map(key -> value.toString)
  }

  val roleMapping = of[PersonRole.Value]

  /**
   * HTML form mapping for a person’s address.
   */
  val addressMapping = mapping(
    "id" -> ignored(Option.empty[Long]),
    "street1" -> optional(text),
    "street2" -> optional(text),
    "city" -> optional(text),
    "province" -> optional(text),
    "postCode" -> optional(text),
    "country" -> nonEmptyText)(Address.apply)(Address.unapply)

  /**
   * HTML form mapping for a person’s social profile.
   */
  val socialProfileMapping = mapping(
    "twitterHandle" -> optional(text.verifying(Constraints.pattern("""[A-Za-z0-9_]{1,16}""".r, error = "error.twitter"))),
    "facebookUrl" -> optional(facebookProfileUrl),
    "linkedInUrl" -> optional(linkedInProfileUrl),
    "googlePlusUrl" -> optional(googlePlusProfileUrl)) (
      {
        (twitterHandle, facebookUrl, linkedInUrl, googlePlusUrl) ⇒
          SocialProfile(0, ProfileType.Person, "", twitterHandle, facebookUrl, linkedInUrl, googlePlusUrl)
      })(
        {
          (s: SocialProfile) ⇒ Some(s.twitterHandle, s.facebookUrl, s.linkedInUrl, s.googlePlusUrl)
        })

  /**
   * HTML form mapping for creating and editing.
   */
  def personForm(user: UserIdentity) = {
    Form(mapping(
      "id" -> ignored(Option.empty[Long]),
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "emailAddress" -> email,
      "birthday" -> optional(jodaLocalDate),
      "photo" -> of(photoFormatter),
      "signature" -> boolean,
      "address" -> addressMapping,
      "bio" -> optional(text),
      "interests" -> optional(text),
      "profile" -> socialProfileMapping,
      "role" -> roleMapping,
      "webSite" -> optional(webUrl),
      "blog" -> optional(webUrl),
      "active" -> ignored(true),
      "dateStamp" -> mapping(
        "created" -> ignored(DateTime.now()),
        "createdBy" -> ignored(user.fullName),
        "updated" -> ignored(DateTime.now()),
        "updatedBy" -> ignored(user.fullName))(DateStamp.apply)(DateStamp.unapply)) (
        { (id, firstName, lastName, emailAddress, birthday, photo, signature, address, bio, interests, profile, role,
          webSite, blog, active, dateStamp) ⇒
          {
            val person = Person(id, firstName, lastName, birthday, photo, signature, address.id.getOrElse(0), bio, interests, role,
              webSite, blog, customerId = None, virtual = false, active, dateStamp)
            person.socialProfile_=(profile.copy(email = emailAddress))
            person.address_=(address)
            person
          }
        })(
          { (p: Person) ⇒
            Some(
              (p.id, p.firstName, p.lastName, p.socialProfile.email, p.birthday, p.photo, p.signature, p.address, p.bio, p.interests,
                p.socialProfile, p.role, p.webSite, p.blog, p.active, p.dateStamp))
          }))
  }

  /**
   * Form target for toggling whether a person is active
   *
   * @param id Person identifier
   */
  def activation(id: Long) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      personService.find(id).map { person ⇒
        Form("active" -> boolean).bindFromRequest.fold(
          form ⇒ BadRequest("invalid form data"),
          active ⇒ {
            Person.activate(id, active)
            val activity = person.activity(
              user.person,
              if (active)
                Activity.Predicate.Activated
              else
                Activity.Predicate.Deactivated).insert
            Redirect(routes.People.details(id)).flashing("success" -> activity.toString)
          })
      } getOrElse {
        Redirect(routes.People.index()).flashing(
          "error" -> Messages("error.notFound", Messages("models.Person")))
      }
  }

  /**
   * Render a Create page
   */
  def add = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      Ok(views.html.person.form(user, None, personForm(user)))
  }

  /**
   * Assign a person to an organisation
   */
  def addRelationship() = SecuredDynamicAction("person", "edit") { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      val relationshipForm = Form(tuple("page" -> text,
        "personId" -> longNumber,
        "organisationId" -> longNumber))

      relationshipForm.bindFromRequest.fold(
        errors ⇒ BadRequest("organisationId missing"),
        {
          case (page, personId, organisationId) ⇒
            personService.find(personId).map { person ⇒
              orgService.find(organisationId).map { organisation ⇒
                person.addRelation(organisationId)

                val activity = person.activity(
                  user.person,
                  Activity.Predicate.Connected,
                  Some(organisation)).insert

                // Redirect to the page we came from - either the person or organisation details page.
                val action = if (page == "person")
                  routes.People.details(personId).url
                else
                  routes.Organisations.details(organisationId).url
                Redirect(action).flashing("success" -> activity.toString)
              }.getOrElse(NotFound)
            }.getOrElse(NotFound)
        })
  }

  /**
   * Create form submits to this action.
   */
  def create = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      personForm(user).bindFromRequest.fold(
        formWithErrors ⇒ {
          BadRequest(views.html.person.form(user, None, formWithErrors))
        },
        person ⇒ {
          val updatedPerson = person.insert
          val activity = updatedPerson.activity(
            user.person,
            Activity.Predicate.Created).insert
          Redirect(routes.People.index()).flashing("success" -> activity.toString)
        })
  }

  /**
   * Delete a person
   *
   * @param id Person identifier
   */
  def delete(id: Long) = SecuredDynamicAction("person", "delete") { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      personService.find(id).map { person ⇒
        if (!person.deletable) {
          Redirect(routes.People.index()).flashing("error" -> Messages("error.person.nonDeletable"))
        } else {
          personService.delete(id)
          val activity = person.activity(
            user.person,
            Activity.Predicate.Deleted).insert
          Redirect(routes.People.index()).flashing("success" -> activity.toString)
        }
      }.getOrElse(NotFound)
  }

  /**
   * Delete a relationthip of a person and an organisation
   *
   * @param page Page identifier where the action was requested from
   * @param personId Person identifier
   * @param organisationId Org identifier
   */
  def deleteRelationship(page: String,
    personId: Long,
    organisationId: Long) = SecuredDynamicAction("person", "edit") {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒

        personService.find(personId).map { person ⇒
          orgService.find(organisationId).map { organisation ⇒
            person.deleteRelation(organisationId)

            val activity = person.activity(
              user.person,
              Activity.Predicate.Disconnected,
              Some(organisation)).insert

            // Redirect to the page we came from - either the person or
            // organisation details page.
            val action = if (page == "person")
              routes.People.details(personId).url
            else
              routes.Organisations.details(organisationId).url
            Redirect(action).flashing("success" -> activity.toString)
          }
        }.flatten.getOrElse(NotFound)
  }

  /**
   * Render Details page
   *
   * @param id Person identifier
   */
  def details(id: Long) = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      personService.find(id) map { person ⇒
        val memberships = person.organisations
        val otherOrganisations = orgService.findActive.filterNot(organisation ⇒
          memberships.contains(organisation))
        val licenses = licenseService.licenses(id)
        val accountRole = if (user.account.editor)
          userAccountService.findRole(id) else None
        val duplicated = if (user.account.editor)
          userAccountService.findDuplicateIdentity(person)
        else None
        val contributions = contributionService.contributions(id, isPerson = true)
        val payments = person.member map { v ⇒
          Some(paymentRecordService.findByPerson(id))
        } getOrElse None
        Ok(views.html.person.details(user, person,
          memberships, otherOrganisations,
          contributions,
          licenses, accountRole, duplicated, payments))
      } getOrElse {
        Redirect(routes.People.index()).flashing(
          "error" -> Messages("error.notFound", Messages("models.Person")))
      }
  }

  /**
   * Render an Edit page
   *
   * @param id Person identifier
   */
  def edit(id: Long) = SecuredDynamicAction("person", "edit") { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      personService.find(id).map { person ⇒
        Ok(views.html.person.form(user, Some(id), personForm(user).fill(person)))
      }.getOrElse(NotFound)
  }

  /**
   * Edit form submits to this action
   *
   * @param id Person identifier
   */
  def update(id: Long) = SecuredDynamicAction("person", "edit") {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        personService.find(id) map { p ⇒
          personForm(user).bindFromRequest.fold(
            formWithErrors ⇒
              BadRequest(views.html.person.form(user, Some(id), formWithErrors)),
            person ⇒ {
              val updatedPerson = person
                .copy(id = Some(id))
                .copy(customerId = p.customerId)
              updatedPerson.socialProfile_=(person.socialProfile)
              updatedPerson.address_=(person.address)
              updatedPerson.update
              val activity = updatedPerson.activity(
                user.person,
                Activity.Predicate.Updated).insert
              Redirect(routes.People.details(id)).flashing(
                "success" -> activity.toString)
            })
        } getOrElse NotFound
  }

  /**
   * Render a list of people in the network
   *
   * @return
   */
  def index = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      val people = models.Person.findAll
      Ok(views.html.person.index(user, people))
  }

  /**
   * Upload a new signature to Amazon
   *
   * @param id Person identifier
   */
  def uploadSignature(id: Long) = AsyncSecuredDynamicAction("person", "edit") { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      val encoding = "ISO-8859-1"
      personService.find(id).map { person ⇒
        val route = routes.People.details(person.id.get).url + "#facilitation"

        request.body.asMultipartFormData.get.file("signature").map { picture ⇒
          val filename = Person.fullFileName(person.id.get)
          val source = Source.fromFile(picture.ref.file.getPath, encoding)
          val byteArray = source.toArray.map(_.toByte)
          source.close()
          S3Bucket.add(BucketFile(filename, contentType, byteArray)).map { unit ⇒
            person.copy(signature = true).update
            Cache.remove(Person.cacheId(id))
            val activity = person.activity(
              user.person,
              Activity.Predicate.UploadedSign).insert
            Redirect(route).flashing("success" -> activity.toString)
          }.recover {
            case S3Exception(status, code, message, originalXml) ⇒
              Redirect(route).flashing("error" -> "Image cannot be temporary saved")
          }
        }.getOrElse {
          Future.successful(Redirect(route).flashing("error" -> "Please choose an image file"))
        }
      }.getOrElse(Future.successful(NotFound))
  }

  /**
   * Delete signature form submits to this action
   *
   * @param personId Person identifier
   */
  def deleteSignature(personId: Long) = SecuredDynamicAction("person", "edit") { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      personService.find(personId.toLong).map { person ⇒
        if (person.signature) {
          S3Bucket.remove(Person.fullFileName(personId))
          Cache.remove(Person.cacheId(personId))
        }
        person.copy(signature = false).update
        val activity = person.activity(
          user.person,
          Activity.Predicate.DeletedSign).insert
        val route = routes.People.details(personId).url + "#facilitation"
        Redirect(route).flashing("success" -> activity.toString)
      }.getOrElse(NotFound)
  }

  /**
   * Retrieve and cache a signature of a person
   *
   * @param personId Person identifier
   */
  def signature(personId: Long) = Action.async {
    val cached = Cache.getAs[Array[Byte]](Person.cacheId(personId))
    if (cached.isDefined) {
      Future.successful(Ok(cached.get).as(contentType))
    } else {
      val empty = Array[Byte]()
      val image: Future[Array[Byte]] = personService.find(personId).map { person ⇒
        if (person.signature) {
          Person.downloadFromCloud(personId)
        } else Future.successful(empty)
      }.getOrElse(Future.successful(empty))
      image.map {
        case value ⇒
          Ok(value).as(contentType)
      }
    }
  }

  /**
   * Cancels a subscription for yearly-renewing membership
   * @param id Person id
   */
  def cancel(id: Long) = SecuredDynamicAction("person", "edit") { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      val url = routes.People.details(id).url + "#membership"
      personService.find(id) map { person ⇒
        person.member map { m ⇒
          if (m.renewal) {
            val key = Play.configuration.getString("stripe.secret_key").get
            val gateway = new GatewayWrapper(key)
            try {
              gateway.cancel(person.customerId.get)
              m.copy(renewal = false).update
            } catch {
              case e: PaymentException ⇒
                Redirect(url).flashing("error" -> Messages(e.msg))
              case e: RequestException ⇒
                e.log.foreach(Logger.error(_))
                Redirect(url).flashing("error" -> Messages(e.getMessage))
            }
            Redirect(url).
              flashing("success" -> "Subscription was successfully canceled")
          } else {
            Redirect(url).
              flashing("error" -> Messages("error.membership.noSubscription"))
          }
        } getOrElse {
          Redirect(url).
            flashing("error" -> Messages("error.membership.noSubscription"))
        }
      } getOrElse NotFound
  }
}

object People extends People with Security
