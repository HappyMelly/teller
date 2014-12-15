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
import models._
import org.joda.time._
import play.api.data.validation.Constraints
import play.api.mvc._
import play.api.data.Form
import play.api.data.validation.Constraints._
import play.api.data.Forms._
import models.UserRole.Role._
import securesocial.core.SecuredRequest
import play.api.cache.Cache
import play.api.libs.json.{ JsValue, Writes, Json }
import services._
import fly.play.s3.{ BucketFile, S3Exception }
import play.api.Play.current
import scala.io.Source
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits._

object Brands extends Controller with Security {

  val contentType = "image/jpeg"
  val encoding = "ISO-8859-1"

  /**
   * HTML form mapping for a brand’s social profile.
   */
  val socialProfileMapping = mapping(
    "email" -> nonEmptyText,
    "twitterHandle" -> optional(text.verifying(Constraints.pattern("""[A-Za-z0-9_]{1,16}""".r, error = "error.twitter"))),
    "facebookUrl" -> optional(facebookProfileUrl),
    "linkedInUrl" -> optional(linkedInProfileUrl),
    "googlePlusUrl" -> optional(googlePlusProfileUrl),
    "skype" -> optional(nonEmptyText),
    "phone" -> optional(nonEmptyText)) (
      {
        (email, twitterHandle, facebookUrl, linkedInUrl, googlePlusUrl, skype, phone) ⇒
          SocialProfile(0, ProfileType.Brand, email, twitterHandle, facebookUrl, linkedInUrl, googlePlusUrl, skype, phone)
      })(
        {
          (s: SocialProfile) ⇒ Some(s.email, s.twitterHandle, s.facebookUrl, s.linkedInUrl, s.googlePlusUrl, s.skype, s.phone)
        })

  /** HTML form mapping for creating and editing. */
  def brandsForm(implicit request: SecuredRequest[_]) = Form(mapping(
    "id" -> ignored(Option.empty[Long]),
    "code" -> nonEmptyText.verifying(pattern("[A-Z0-9]*".r, "constraint.brand.code", "constraint.brand.code.error"), maxLength(5)),
    "uniqueName" -> nonEmptyText.verifying(pattern("[A-Za-z0-9._]*".r, "constraint.brand.code", "constraint.brand.uniqueName.error"), maxLength(25)),
    "name" -> nonEmptyText,
    "coordinatorId" -> nonEmptyText.transform(_.toLong, (l: Long) ⇒ l.toString),
    "description" -> optional(text),
    "picture" -> optional(text),
    "generateCert" -> boolean,
    "tagLine" -> optional(text),
    "webSite" -> optional(webUrl),
    "blog" -> optional(webUrl),
    "profile" -> socialProfileMapping,
    "created" -> ignored(DateTime.now()),
    "createdBy" -> ignored(request.user.fullName),
    "updated" -> ignored(DateTime.now()),
    "updatedBy" -> ignored(request.user.fullName))({
      (id, code, uniqueName, name, coordinatorId, description, picture, generateCert, tagLine, webSite, blog,
      profile, created, createdBy, updated, updatedBy) ⇒
        Brand(id, code, uniqueName, name, coordinatorId, description, picture, generateCert, tagLine, webSite, blog,
          profile, created, createdBy, updated, updatedBy)
    })({ (b: Brand) ⇒
      Some((b.id, b.code, b.uniqueName, b.name, b.coordinatorId, b.description, b.picture, b.generateCert, b.tagLine,
        b.webSite, b.blog, b.socialProfile, b.created, b.createdBy, b.updated, b.updatedBy))
    }))

  /** Shows all brands **/
  def index = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒

      val brands = models.Brand.findAllWithCoordinator
      Ok(views.html.brand.index(request.user, brands))
  }

  /**
   * Render an add form
   *
   * @return
   */
  def add = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒
      Ok(views.html.brand.form(request.user, None, brandsForm))
  }

  /**
   * Create a new brand
   * @return
   */
  def create = AsyncSecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒

      val form: Form[Brand] = brandsForm.bindFromRequest
      form.fold(
        formWithErrors ⇒ Future.successful(
          BadRequest(views.html.brand.form(request.user, None, formWithErrors))),
        brand ⇒ {
          if (Brand.exists(brand.code))
            Future.successful(BadRequest(views.html.brand.form(request.user, None,
              form.withError("code", "constraint.brand.code.exists", brand.code))))
          else if (Brand.nameExists(brand.uniqueName))
            Future.successful(BadRequest(views.html.brand.form(request.user, None,
              form.withError("uniqueName", "constraint.brand.code.exists", brand.uniqueName))))
          else {
            request.body.asMultipartFormData.get.file("picture").map { picture ⇒
              val filename = Brand.generateImageName(picture.filename)
              val source = Source.fromFile(picture.ref.file.getPath, encoding)
              val byteArray = source.toArray.map(_.toByte)
              source.close()
              S3Bucket.add(BucketFile(filename, contentType, byteArray)).map { unit ⇒
                brand.copy(picture = Some(filename)).insert
                val activity = Activity.insert(request.user.fullName, Activity.Predicate.Created, brand.name)
                Redirect(routes.Brands.index()).flashing("success" -> activity.toString)
              }.recover {
                case S3Exception(status, code, message, originalXml) ⇒ BadRequest(views.html.brand.form(request.user, None,
                  form.withError("picture", "Image cannot be temporary saved")))
              }
            }.getOrElse {
              brand.insert
              val activity = Activity.insert(request.user.fullName, Activity.Predicate.Created, brand.name)
              Future.successful(Redirect(routes.Brands.index()).flashing("success" -> activity.toString))
            }
          }
        })
  }

  /**
   * Delete a brand
   *
   * @param code Brand identifier
   * @return
   */
  def delete(code: String) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒
      Brand.find(code).map {
        case BrandView(brand, _, _) ⇒
          brand.picture.map { picture ⇒
            S3Bucket.remove(picture)
            Cache.remove(Brand.cacheId(brand.code))
          }
          brand.delete()
          val activity = Activity.insert(request.user.fullName, Activity.Predicate.Deleted, brand.name)
          Redirect(routes.Brands.index()).flashing("success" -> activity.toString)
      }.getOrElse(NotFound(views.html.notFoundPage(request.path)))
  }

  /**
   * Delete picture form submits to this action
   *
   * @param code Brand string identifier
   * @return
   */
  def deletePicture(code: String) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒
      Brand.find(code).map { brandView ⇒
        brandView.brand.picture.map { picture ⇒
          S3Bucket.remove(picture)
          Cache.remove(Brand.cacheId(brandView.brand.code))
        }
        Brand.update(brandView.brand, brandView.brand, None)
        val activity = Activity.insert(request.user.fullName, Activity.Predicate.Deleted,
          "image from the brand " + brandView.brand.name)
        Redirect(routes.Brands.details(code)).flashing("success" -> activity.toString)
      }.getOrElse(NotFound(views.html.notFoundPage(request.path)))
  }

  /**
   * Render a brand's details page
   *
   * @param code Brand string identifier
   * @return
   */
  def details(code: String) = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒
      Brand.find(code).map {
        case BrandView(brand, coordinator, licenseIds) ⇒ {
          val eventTypes = EventType.findByBrand(brand.id.get)
          Ok(views.html.brand.details(request.user, brand, coordinator, eventTypes))
        }
      }.getOrElse(NotFound(views.html.notFoundPage(request.path)))

  }

  /**
   * Edit page
   *
   * @param code Brand string identifier
   * @return
   */
  def edit(code: String) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒
      Brand.find(code).map { brandView ⇒
        val filledForm: Form[Brand] = brandsForm.fill(brandView.brand)
        Ok(views.html.brand.form(request.user, Some(code), filledForm))
      }.getOrElse(NotFound(views.html.notFoundPage(request.path)))
  }

  /**
   * Edit form submits to this action
   *
   * @param code Unique text identifier of a brand
   * @return
   */
  def update(code: String) = AsyncSecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒

      Brand.find(code).map { existingBrandView ⇒
        val form: Form[Brand] = brandsForm.bindFromRequest
        form.fold(
          formWithErrors ⇒ Future.successful(BadRequest(views.html.brand.form(request.user, Some(code), form))),
          brand ⇒ {
            if (Brand.exists(brand.code, existingBrandView.brand.id))
              Future.successful(BadRequest(views.html.brand.form(request.user, Some(code),
                form.withError("code", "constraint.brand.code.exists", brand.code))))
            else if (Brand.nameExists(brand.uniqueName, existingBrandView.brand.id))
              Future.successful(BadRequest(views.html.brand.form(request.user, Some(code),
                form.withError("uniqueName", "constraint.brand.code.exists", brand.uniqueName))))
            else {
              request.body.asMultipartFormData.get.file("picture").map { picture ⇒
                val filename = Brand.generateImageName(picture.filename)
                val source = Source.fromFile(picture.ref.file.getPath, encoding)
                val byteArray = source.toArray.map(_.toByte)
                source.close()
                S3Bucket.add(BucketFile(filename, contentType, byteArray)).map { unit ⇒
                  val updatedBrand = Brand.update(existingBrandView.brand, brand, Some(filename))
                  Cache.remove(Brand.cacheId(code))
                  if (code != updatedBrand.code) {
                    Cache.remove(Brand.cacheId(updatedBrand.code))
                  }
                  existingBrandView.brand.picture.map { oldPicture ⇒
                    S3Bucket.remove(oldPicture)
                  }
                  val activity = Activity.insert(request.user.fullName, Activity.Predicate.Updated, brand.name)
                  Redirect(routes.Brands.details(updatedBrand.code)).flashing("success" -> activity.toString)
                }.recover {
                  case S3Exception(status, code, message, originalXml) ⇒
                    BadRequest(views.html.brand.form(request.user, Some(brand.code),
                      form.withError("picture", "Image cannot be temporary saved. Please, try again later.")))
                }
              }.getOrElse {
                val updatedBrand = Brand.update(existingBrandView.brand, brand, existingBrandView.brand.picture)
                val activity = Activity.insert(request.user.fullName, Activity.Predicate.Updated, brand.name)
                Future.successful(Redirect(routes.Brands.details(updatedBrand.code)).flashing("success" -> activity.toString))
              }
            }
          })
      }.getOrElse(Future.successful(NotFound(views.html.notFoundPage(request.path))))
  }

  /**
   * Retrieve and cache a product's image
   *
   * @param code Brand string identifier
   * @return
   */
  def picture(code: String) = Action.async {
    val cached = Cache.getAs[Array[Byte]](Brand.cacheId(code))
    if (cached.isDefined) {
      Future.successful(Ok(cached.get).as(contentType))
    } else {
      val empty = Array[Byte]()
      val image: Future[Array[Byte]] = Brand.find(code).map { entry ⇒
        entry.brand.picture.map { picture ⇒
          val result = S3Bucket.get(entry.brand.picture.get)
          result.map {
            case BucketFile(name, contentType, content, acl, headers) ⇒ content
          }.recover {
            case S3Exception(status, code, message, originalXml) ⇒ empty
          }
        }.getOrElse(Future.successful(empty))
      }.getOrElse(Future.successful(empty))
      image.map {
        case value ⇒
          Cache.set(Brand.cacheId(code), value)
          Ok(value).as(contentType)
      }
    }
  }

  implicit val eventWrites = new Writes[Event] {
    def writes(data: Event): JsValue = {
      Json.obj(
        "id" -> data.id.get,
        "title" -> data.longTitle)
    }
  }

  /**
   * Returns a list of managed events for the given brand and current user
   */
  def events(brandCode: String, future: Option[Boolean] = None) = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒
      Brand.find(brandCode).map { brand ⇒
        val account = request.user.asInstanceOf[LoginIdentity].userAccount
        val events = if (account.editor || brand.brand.coordinatorId == account.personId) {
          Event.findByParameters(brandCode, future)
        } else {
          Event.findByFacilitator(account.personId, brandCode, future)
        }
        Ok(Json.toJson(events))
      }.getOrElse(NotFound("Unknown brand"))
  }

}
