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
import fly.play.s3.{ BucketFile, S3Exception }
import models.UserRole.DynamicRole
import models.UserRole.Role._
import models._
import models.brand.BrandCoordinator
import models.service.Services
import org.joda.time._
import play.api.Play.current
import play.api.cache.Cache
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.i18n.Messages
import play.api.libs.json.{ JsValue, Json, Writes }
import play.api.mvc._
import securesocial.core.RuntimeEnvironment
import services._

import scala.concurrent.Future
import scala.io.Source

class Brands(environment: RuntimeEnvironment[ActiveUser])
    extends JsonController
    with Security
    with Services
    with Activities {

  override implicit val env: RuntimeEnvironment[ActiveUser] = environment
  val contentType = "image/jpeg"
  val encoding = "ISO-8859-1"

  /** HTML form mapping for creating and editing. */
  def brandsForm(userName: String) = Form(mapping(
    "id" -> ignored(Option.empty[Long]),
    "code" -> nonEmptyText.verifying(
      pattern("[A-Z0-9]*".r,
        "constraint.brand.code",
        "constraint.brand.code.error"),
      maxLength(5)),
    "uniqueName" -> nonEmptyText.verifying(
      pattern("[A-Za-z0-9._]*".r,
        "constraint.brand.code",
        "constraint.brand.uniqueName.error"),
      maxLength(25)),
    "name" -> nonEmptyText,
    "ownerId" -> nonEmptyText.transform(_.toLong, (l: Long) ⇒ l.toString),
    "description" -> optional(text),
    "picture" -> optional(text),
    "tagLine" -> optional(text),
    "webSite" -> optional(webUrl),
    "blog" -> optional(webUrl),
    "email" -> play.api.data.Forms.email,
    "profile" -> SocialProfiles.profileMapping(ProfileType.Brand),
    "evaluationUrl" -> optional(webUrl),
    "evaluationHookUrl" -> optional(webUrl),
    "recordInfo" -> mapping(
      "created" -> ignored(DateTime.now()),
      "createdBy" -> ignored(userName),
      "updated" -> ignored(DateTime.now()),
      "updatedBy" -> ignored(userName))(DateStamp.apply)(DateStamp.unapply))({
      (id, code, uniqueName, name, ownerId, description, picture,
      tagLine, webSite, blog, email, profile, evaluationUrl, evaluationHookUrl,
      recordInfo) ⇒
        {
          val brand = Brand(id, code, uniqueName, name, ownerId,
            description, picture, tagLine, webSite, blog, email, evaluationUrl,
            evaluationHookUrl, true, recordInfo)
          brand.socialProfile_=(profile)
          brand
        }
    })({ (b: Brand) ⇒
      Some((b.id, b.code, b.uniqueName, b.name, b.ownerId, b.description, b.picture,
        b.tagLine, b.webSite, b.blog, b.contactEmail, b.socialProfile, b.evaluationUrl, b.evaluationHookUrl,
        b.recordInfo))
    }))

  /** Shows all brands **/
  def index = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      val brands = models.Brand.findAllWithCoordinator
      Ok(views.html.brand.index(user, brands))
  }

  /**
   * Activates/deactivates the given brand
   *
   * @param id Product id
   */
  def activation(id: Long) = SecuredRestrictedAction(Admin) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      brandService.find(id).map { brand ⇒
        Form("active" -> boolean).bindFromRequest.fold(
          error ⇒ jsonBadRequest("active parameter is not found"),
          active ⇒ {
            if (active) {
              brandService.activate(id)
            } else {
              brandService.deactivate(id)
              for (product ← productService.findByBrand(id)) {
                productService.deactivate(product.id.get)
              }
            }
            jsonSuccess("ok")
          })
      } getOrElse jsonNotFound("Brand is not found")
  }

  /**
   * Render an add form
   *
   * @return
   */
  def add = SecuredRestrictedAction(Admin) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      val people = personService.findActive
      Ok(views.html.v2.brand.form(user, None, people, brandsForm(user.name)))
  }

  /**
   * Create a new brand
   */
  def create = AsyncSecuredRestrictedAction(Admin) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      val form: Form[Brand] = brandsForm(user.name).bindFromRequest
      val people = personService.findActive
      form.fold(
        formWithErrors ⇒ Future.successful(
          BadRequest(views.html.v2.brand.form(user, None, people, formWithErrors))),
        brand ⇒ {
          if (Brand.exists(brand.code))
            Future.successful(BadRequest(views.html.v2.brand.form(user, None, people,
              form.withError("code", "constraint.brand.code.exists", brand.code))))
          else if (Brand.nameExists(brand.uniqueName))
            Future.successful(BadRequest(views.html.v2.brand.form(user, None, people,
              form.withError("uniqueName", "constraint.brand.code.exists", brand.uniqueName))))
          else {
            request.body.asMultipartFormData.get.file("picture").map { picture ⇒
              val filename = Brand.generateImageName(picture.filename)
              val source = Source.fromFile(picture.ref.file.getPath, encoding)
              val byteArray = source.toArray.map(_.toByte)
              source.close()
              S3Bucket.add(BucketFile(filename, contentType, byteArray)).map { unit ⇒
                brand.copy(picture = Some(filename)).insert
                val log = activity(brand, user.person).created.insert()
                Redirect(routes.Brands.index()).flashing("success" -> log.toString)
              }.recover {
                case S3Exception(status, code, message, originalXml) ⇒ BadRequest(views.html.brand.form(user, None, people,
                  form.withError("picture", "Image cannot be temporary saved")))
              }
            }.getOrElse {
              val b = brand.insert()
              val log = activity(b, user.person).created.insert()
              Future.successful(Redirect(routes.Brands.index()).flashing("success" -> log.toString))
            }
          }
        })
  }

  /**
   * Deletes the given brand
   *
   * @param id Brand identifier
   */
  def delete(id: Long) = SecuredBrandAction(id) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      brandService.find(id) map { brand ⇒
        brand.picture.foreach { picture ⇒
          S3Bucket.remove(picture)
          Cache.remove(Brand.cacheId(brand.code))
        }
        brand.delete()
        val log = activity(brand, user.person).deleted.insert()
        Redirect(routes.Brands.index()).flashing("success" -> log.toString)
      } getOrElse NotFound(views.html.notFoundPage(request.path))
  }

  /**
   * Deletes picture in the given brand
   *
   * @param id Brand string identifier
   */
  def deletePicture(id: Long) = SecuredBrandAction(id) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      brandService.find(id).map { brand ⇒
        brand.picture.foreach { picture ⇒
          S3Bucket.remove(picture)
          Cache.remove(Brand.cacheId(brand.code))
        }
        brandService.update(brand, brand, None)
        val log = activity(brand, user.person).deletedImage.insert()
        Redirect(routes.Brands.details(id)).flashing("success" -> log.toString)
      }.getOrElse(NotFound(views.html.notFoundPage(request.path)))
  }

  /**
   * Renders a brand page of the given brand
   *
   * @param id Brand identifier
   */
  def details(id: Long) = SecuredRestrictedAction(Viewer) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    brandService.find(id) map { brand ⇒
      val links = brandService.links(id)
      val coordinator = personService.find(brand.ownerId)
      Ok(views.html.v2.brand.details(user, brand, coordinator, links))
    } getOrElse NotFound(views.html.notFoundPage(request.path))
  }

  /**
   * Renders tab for the given brand
   * @param id Brand identifier
   * @param tab Tab identifier
   */
  def renderTabs(id: Long, tab: String) = SecuredRestrictedAction(Viewer) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        tab match {
          case "team" ⇒
            val members = brandService.coordinators(id).sortBy(_._1.fullName)
            val people = personService.findActive.filterNot(x ⇒ members.contains(x))
            Ok(views.html.v2.brand.tabs.team(id, members, people))
          case "templates" ⇒
            val templates = certificateService.findByBrand(id)
            Ok(views.html.v2.brand.tabs.templates(id, templates))
          case "testimonials" ⇒
            val testimonials = brandService.testimonials(id)
            Ok(views.html.v2.brand.tabs.testimonials(id, testimonials))
          case "types" ⇒
            val eventTypes = eventTypeService.findByBrand(id).sortBy(_.name)
            Ok(views.html.v2.brand.tabs.eventTypes(id, eventTypes))
          case _ ⇒
            val products = productService.findByBrand(id)
            Ok(views.html.v2.brand.tabs.products(products, viewOnly = true) { _ ⇒ play.twirl.api.Html("") })
        }
  }

  /**
   * Adds new coordinator to the given brand
   * @param id Brand identifier
   */
  def addCoordinator(id: Long) = SecuredBrandAction(id) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    val data = Form(single("personId" -> longNumber(min = 1)))
    data.bindFromRequest.fold(
      hasErrors ⇒ jsonBadRequest("personId should be a positive number"),
      personId ⇒
        brandService.find(id) map { x ⇒
          personService.find(personId) map { y ⇒
            if (brandService.coordinators(id).exists(_._1 == y)) {
              jsonConflict(Messages("error.brand.alreadyMember"))
            } else {
              val coordinator = BrandCoordinator(None, id, personId)
              brandCoordinatorService.insert(coordinator)
              userAccountService.findByPerson(personId) map { account =>
                userAccountService.update(account.copy(coordinator = true, activeRole = true))
              } getOrElse {
                val account = UserAccount.empty(personId).copy(coordinator = true, registered = true)
                userAccountService.insert(account)
              }
              val data = Json.obj("personId" -> personId, "brandId" -> id, "name" -> y.fullName)
              jsonSuccess(Messages("success.brand.newMember"), Some(data))
            }
          } getOrElse jsonNotFound(Messages("error.notFound", "person"))
        } getOrElse jsonNotFound(Messages("error.notFound", "brand")))
  }

  /**
   * Removes the given coordinator from the given brand
   * @param id Brand id
   * @param personId Person id
   */
  def removeCoordinator(id: Long, personId: Long) = AsyncSecuredBrandAction(id) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒
      brandService.find(id) map { x ⇒
        if (x.ownerId == personId) {
          Future.successful(jsonConflict(Messages("error.brand.removeOwner")))
        } else {
          brandCoordinatorService.delete(id, personId)
          if (brandService.findByCoordinator(personId).isEmpty) {
            userAccountService.findByPerson(personId) foreach { account =>
              userAccountService.update(account.copy(coordinator = false, activeRole = false))
            }
          }
          Future.successful(jsonSuccess(Messages("success.brand.deleteMember")))
        }
      } getOrElse Future.successful(jsonNotFound(Messages("error.notFound", "brand")))
  }

  /**
   * Turns on a notification of the given type
   * @param id Brand id
   * @param personId Person id
   * @param notification Notification type
   */
  def turnNotificationOff(id: Long,
                          personId: Long,
                          notification: String) = SecuredBrandAction(id) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        brandCoordinatorService.update(id, personId, notification, false)
        jsonSuccess("Changes saved")
  }

  /**
   * Turns off a notification of the given type
   * @param id Brand id
   * @param personId Person id
   * @param notification Notification type
   */
  def turnNotificationOn(id: Long,
                         personId: Long,
                         notification: String) = SecuredBrandAction(id) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        brandCoordinatorService.update(id, personId, notification, true)
        jsonSuccess("Changes saved")
  }

  /**
   * Renders a Brand edit page
   *
   * @param id Brand identifier
   */
  def edit(id: Long) = SecuredBrandAction(id) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      brandService.find(id) map { brand ⇒
        val filledForm = brandsForm(user.name).fill(brand)
        val people = personService.findActive
        Ok(views.html.v2.brand.form(user, Some(id), people, filledForm))
      } getOrElse NotFound(views.html.notFoundPage(request.path))
  }

  /**
   * Updates the given brand
   *
   * @param id Brand identifier
   */
  def update(id: Long) = AsyncSecuredBrandAction(id) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      brandService.find(id).map { x ⇒
        val people = personService.findActive
        val form: Form[Brand] = brandsForm(user.name).bindFromRequest
        form.fold(
          formWithErrors ⇒ Future.successful(BadRequest(views.html.v2.brand.form(user, Some(id), people, form))),
          brand ⇒ {
            if (Brand.exists(brand.code, x.id))
              Future.successful(BadRequest(views.html.v2.brand.form(user, Some(id), people,
                form.withError("code", "constraint.brand.code.exists", brand.code))))
            else if (Brand.nameExists(brand.uniqueName, x.id))
              Future.successful(BadRequest(views.html.v2.brand.form(user, Some(id), people,
                form.withError("uniqueName", "constraint.brand.code.exists", brand.uniqueName))))
            else {
              request.body.asMultipartFormData.get.file("picture").map { picture ⇒
                val filename = Brand.generateImageName(picture.filename)
                val source = Source.fromFile(picture.ref.file.getPath, encoding)
                val byteArray = source.toArray.map(_.toByte)
                source.close()
                S3Bucket.add(BucketFile(filename, contentType, byteArray)).map { unit ⇒
                  val updatedBrand = brandService.update(x, brand, Some(filename))
                  Cache.remove(Brand.cacheId(x.code))
                  if (x.code != updatedBrand.code) {
                    Cache.remove(Brand.cacheId(updatedBrand.code))
                  }
                  x.picture.map { oldPicture ⇒
                    S3Bucket.remove(oldPicture)
                  }
                  val log = activity(brand, user.person).updated.insert()
                  Redirect(routes.Brands.details(id)).flashing(
                    "success" -> log.toString)
                }.recover {
                  case S3Exception(status, code, message, originalXml) ⇒
                    BadRequest(views.html.brand.form(user, Some(id), people,
                      form.withError("picture", "Image cannot be temporary saved. Please, try again later.")))
                }
              }.getOrElse {
                val updatedBrand = brandService.update(x, brand, x.picture)
                val log = activity(updatedBrand, user.person).updated.insert()
                val route = routes.Brands.details(id)
                Future.successful(Redirect(route).flashing("success" -> log.toString))
              }
            }
          })
      }.getOrElse(Future.successful(NotFound(views.html.notFoundPage(request.path))))
  }

  /**
   * Retrieves and caches an image of the given brand
   *
   * @param code Brand string identifier
   */
  def picture(code: String) = Action.async {
    val cached = Cache.getAs[Array[Byte]](Brand.cacheId(code))
    if (cached.isDefined) {
      Future.successful(Ok(cached.get).as(contentType))
    } else {
      val empty = Array[Byte]()
      val image: Future[Array[Byte]] = brandService.find(code) map { brand ⇒
        brand.picture map { picture ⇒
          val result = S3Bucket.get(brand.picture.get)
          result.map {
            case BucketFile(name, contentType, content, acl, headers) ⇒ content
          }.recover {
            case S3Exception(status, code, message, originalXml) ⇒ empty
          }
        } getOrElse Future.successful(empty)
      } getOrElse Future.successful(empty)
      image.map {
        case value ⇒
          Cache.set(Brand.cacheId(code), value)
          Ok(value).as(contentType)
      }
    }
  }

  /**
   * Returns a list of managed events for the given brand and current user
   *
   * @param brandId Brand id
   * @param future If true, returns only future events; if false, only past
   */
  def events(brandId: Long,
    future: Option[Boolean] = None) = SecuredRestrictedAction(Viewer) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        implicit val eventWrites = new Writes[Event] {
          def writes(data: Event): JsValue = {
            Json.obj(
              "id" -> data.id.get,
              "title" -> data.longTitle)
          }
        }

        brandService.findWithCoordinators(brandId) map { x ⇒
          val account = user.account
          val events = if (
            x.coordinators.exists(_._1.id == Some(account.personId))) {
            eventService.findByParameters(x.brand.id, future, archived = Some(false))
          } else {
            eventService.findByFacilitator(
              account.personId,
              x.brand.id,
              future,
              archived = Some(false))
          }
          Ok(Json.toJson(events))
        } getOrElse NotFound("Unknown brand")
  }

}