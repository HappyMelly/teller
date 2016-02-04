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

import javax.inject.Inject

import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import controllers.Forms._
import fly.play.s3.{BucketFile, S3Exception}
import models.UserRole.Role._
import models._
import models.brand.BrandCoordinator
import org.joda.time._
import play.api.Play.current
import play.api.cache.Cache
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.i18n.{Messages, MessagesApi}
import play.api.libs.json.{JsValue, Json, Writes}
import play.api.mvc._
import play.twirl.api.Html
import services._

import scala.concurrent.Future
import scala.io.Source

class Brands @Inject() (override implicit val env: TellerRuntimeEnvironment,
                        override val messagesApi: MessagesApi,
                        deadbolt: DeadboltActions, handlers: HandlerCache, actionBuilder: ActionBuilders)
  extends Security(deadbolt, handlers, actionBuilder)(messagesApi, env)
  with Activities {

  val contentType = "image/jpeg"
  val encoding = "ISO-8859-1"
  val indexCall: Call = routes.Brands.index()

  /** Shows all brands **/
  def index = AsyncSecuredRestrictedAction(Viewer) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    brandService.findAllWithCoordinator flatMap { brands =>
      ok(views.html.v2.brand.index(user, brands))
    }
  }

  /**
    * Activates/deactivates the given brand
    *
    * @param id Product id
    */
  def activation(id: Long) = AsyncSecuredRestrictedAction(Admin) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    brandService.find(id) flatMap { maybeBrand =>
      maybeBrand map { brand ⇒
        Form("active" -> boolean).bindFromRequest.fold(
          error ⇒ jsonBadRequest("active parameter is not found"),
          active ⇒ {
            if (active) {
              brandService.activate(id)
            } else {
              brandService.deactivate(id)
              productService.findByBrand(id) map { products =>
                for (product <- products) {
                  productService.deactivate(product.id.get)
                }
              }
            }
            jsonSuccess("ok")
          })
      } getOrElse jsonNotFound("Brand is not found")
    }
  }

  /**
    * Render an add form
    *
    * @return
    */
  def add = AsyncSecuredRestrictedAction(Admin) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    personService.findActive.flatMap { people =>
      ok(views.html.v2.brand.form(user, None, people, brandsForm(user.name)))
    }
  }

  /**
    * Create a new brand
    */
  def create = AsyncSecuredRestrictedAction(Admin) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    val form: Form[Brand] = brandsForm(user.name).bindFromRequest
    personService.findActive.flatMap { people =>
      form.fold(
        errors ⇒ badRequest(views.html.v2.brand.form(user, None, people, errors)),
        brand ⇒ {
          (for {
            existance <- brandService.exists(brand.code)
            nameExistance <- brandService.nameExists(brand.uniqueName)
          } yield (existance, nameExistance)) flatMap {
            case (true, _) =>
              badRequest(views.html.v2.brand.form(user, None, people,
                form.withError("code", "constraint.brand.code.exists", brand.code)))
            case (_, true) =>
              badRequest(views.html.v2.brand.form(user, None, people,
                form.withError("uniqueName", "constraint.brand.code.exists", brand.uniqueName)))
            case (false, false) =>
              request.body.asMultipartFormData.get.file("picture").map { picture ⇒
                val filename = Brand.generateImageName(picture.filename)
                val source = Source.fromFile(picture.ref.file.getPath, encoding)
                val byteArray = source.toArray.map(_.toByte)
                source.close()
                S3Bucket.add(BucketFile(filename, contentType, byteArray)).map { unit ⇒
                  brand.copy(picture = Some(filename)).insert
                  val log = activity(brand, user.person).created.insert()
                  Redirect(indexCall).flashing("success" -> log.toString)
                }.recover {
                  case S3Exception(status, code, message, originalXml) ⇒ BadRequest(views.html.v2.brand.form(user, None, people,
                    form.withError("picture", "Image cannot be temporary saved")))
                }
              }.getOrElse {
                brand.insert() flatMap { inserted =>
                  val log = activity(inserted, user.person).created.insert()
                  redirect(indexCall, "success" -> log.toString)
                }
              }
          }
        })

    }
  }

  /**
    * Deletes the given brand
    *
    * @param id Brand identifier
    */
  def delete(id: Long) = AsyncSecuredBrandAction(id) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    brandService.find(id) flatMap { maybeBrand =>
      maybeBrand map { brand =>
        brand.picture.foreach { picture ⇒
          S3Bucket.remove(picture)
          Cache.remove(Brand.cacheId(brand.code))
        }
        brand.delete()
        val log = activity(brand, user.person).deleted.insert()
        redirect(indexCall, "success" -> log.toString)
      } getOrElse notFound(views.html.notFoundPage(request.path))
    }
  }

  /**
    * Deletes picture in the given brand
    *
    * @param id Brand string identifier
    */
  def deletePicture(id: Long) = AsyncSecuredBrandAction(id) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    brandService.find(id) flatMap { maybeBrand =>
      maybeBrand map { brand ⇒
        brand.picture.foreach { picture ⇒
          S3Bucket.remove(picture)
          Cache.remove(Brand.cacheId(brand.code))
        }
        brandService.update(brand, brand, None)
        val log = activity(brand, user.person).deletedImage.insert()
        val call: Call = routes.Brands.details(id)
        redirect(call, "success" -> log.toString)
      } getOrElse notFound(views.html.notFoundPage(request.path))
    }
  }

  /**
    * Renders a brand page of the given brand
    *
    * @param id Brand identifier
    */
  def details(id: Long) = AsyncSecuredRestrictedAction(Viewer) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    brandService.find(id) flatMap { maybeBrand =>
      maybeBrand map { brand ⇒
        (for {
          links <- brandService.links(id)
          coordinator <- personService.find(brand.ownerId)
        } yield (links, coordinator)) flatMap { case (links, coordinator) =>
          ok(views.html.v2.brand.details(user, brand, coordinator, links))
        }
      } getOrElse notFound(views.html.notFoundPage(request.path))
    }
  }

  /**
    * Renders tab for the given brand
    *
    * @param id Brand identifier
    * @param tab Tab identifier
    */
  def renderTabs(id: Long, tab: String) = AsyncSecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      tab match {
        case "testimonials" ⇒
          brandService.testimonials(id) flatMap { testimonials =>
            ok(views.html.v2.brand.tabs.testimonials(id, testimonials))
          }
        case _ ⇒
          productService.findByBrand(id) flatMap { products =>
            ok(views.html.v2.brand.tabs.products(products, viewOnly = true) { _ ⇒ play.twirl.api.Html("") })
          }
      }
  }

  /**
    * Renders coordinator tab for the given brand
    *
    * @param id Brand identifier
    * @param tab Tab identifier
    */
  def renderCoordinatorTabs(id: Long, tab: String)  = AsyncSecuredBrandAction(id) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      tab match {
        case "team" ⇒
          (for {
            coordinators <- brandService.coordinators(id)
            people <- personService.findActive
          } yield (coordinators, people)) flatMap { case (coordinators, people) =>
            val members = coordinators.sortBy(_._1.fullName)
            val filtered = people.filterNot(x ⇒ members.contains(x))
            ok(views.html.v2.brand.tabs.team(id, members, filtered))
          }
        case "templates" ⇒
          certificateService.findByBrand(id) flatMap { templates =>
            ok(views.html.v2.brand.tabs.templates(id, templates))
          }
        case "types" ⇒
          eventTypeService.findByBrand(id) flatMap { eventTypes =>
            ok(views.html.v2.brand.tabs.eventTypes(id, eventTypes.sortBy(_.name)))
          }
        case "badges" ⇒
          brandBadgeService.findByBrand(id) flatMap { badges =>
            ok(views.html.v2.brand.tabs.badges(id, badges.sortBy(_.name)))
          }
        case _ =>
          brandService.findWithSettings(id) flatMap { maybeView =>
            maybeView map { view =>
              ok(views.html.v2.brand.tabs.licenseExpiration(view.settings))
            } getOrElse {
              ok(Html("Unknown brand"))
            }
          }
      }
  }

  /**
    * Adds new coordinator to the given brand
    *
    * @param id Brand identifier
    */
  def addCoordinator(id: Long) = AsyncSecuredBrandAction(id) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    val data = Form(single("personId" -> longNumber(min = 1)))
    data.bindFromRequest.fold(
      hasErrors ⇒ jsonBadRequest("personId should be a positive number"),
      personId ⇒
        (for {
          coordinators <- brandService.coordinators(id)
          person <- personService.find(personId)
        } yield (coordinators, person)) flatMap { case (coordinators, person) =>
          person map { y ⇒
            if (coordinators.exists(_._1 == y)) {
              jsonConflict(Messages("error.brand.alreadyMember"))
            } else {
              val coordinator = BrandCoordinator(None, id, personId)
              brandCoordinatorService.save(coordinator).flatMap { coordinator =>
                val data = Json.obj("personId" -> personId, "brandId" -> id, "name" -> y.fullName)
                jsonSuccess(Messages("success.brand.newMember"), Some(data))
              }
            }
          } getOrElse jsonNotFound(Messages("error.notFound", "person"))
        })
  }

  /**
    * Removes the given coordinator from the given brand
    *
    * @param id Brand id
    * @param personId Person id
    */
  def removeCoordinator(id: Long, personId: Long) = AsyncSecuredBrandAction(id) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒
      brandService.find(id) flatMap {
        case None => jsonNotFound(Messages("error.notFound", "brand"))
        case Some(x) =>
          if (x.ownerId == personId) {
            jsonConflict(Messages("error.brand.removeOwner"))
          } else {
            (for {
              _ <- brandCoordinatorService.delete(id, personId)
              brand <- brandService.findByCoordinator(personId)
            } yield brand.isEmpty) flatMap { noEntries =>
              if (noEntries) {
                userAccountService.findByPerson(personId) map {
                  case None => Future.successful(Unit)
                  case Some(account) =>
                    userAccountService.update(account.copy(coordinator = false, activeRole = false))
                }
              }
              jsonSuccess(Messages("success.brand.deleteMember"))
            }
          }
      }
  }

  /**
    * Turns on a notification of the given type
    *
    * @param id Brand id
    * @param personId Person id
    * @param notification Notification type
    */
  def turnNotificationOff(id: Long, personId: Long, notification: String) = AsyncSecuredBrandAction(id) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒
      brandCoordinatorService.update(id, personId, notification, false).flatMap { _ =>
        jsonSuccess("Changes saved")
      }
  }

  /**
    * Turns off a notification of the given type
    *
    * @param id Brand id
    * @param personId Person id
    * @param notification Notification type
    */
  def turnNotificationOn(id: Long, personId: Long, notification: String) = AsyncSecuredBrandAction(id) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒
      brandCoordinatorService.update(id, personId, notification, true).flatMap { _ =>
        jsonSuccess("Changes saved")
      }
  }

  /**
    * Renders a Brand edit page
    *
    * @param id Brand identifier
    */
  def edit(id: Long) = AsyncSecuredBrandAction(id) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    brandService.find(id) flatMap { maybeBrand =>
      maybeBrand map { brand ⇒
        val filledForm = brandsForm(user.name).fill(brand)
        personService.findActive flatMap { people =>
          ok(views.html.v2.brand.form(user, Some(id), people, filledForm))
        }
      } getOrElse notFound(views.html.notFoundPage(request.path))
    }
  }

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
     recordInfo) ⇒ {
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

  /**
    * Updates the given brand
    *
    * @param id Brand identifier
    */
  def update(id: Long) = AsyncSecuredBrandAction(id) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    (for {
      brand <- brandService.find(id)
      people <- personService.findActive
    } yield (brand, people)) flatMap { case (maybeBrand, people) =>
      maybeBrand map { x ⇒
        val form: Form[Brand] = brandsForm(user.name).bindFromRequest
        form.fold(
          formWithErrors ⇒ badRequest(views.html.v2.brand.form(user, Some(id), people, form)),
          brand ⇒ {
            (for {
              sameCode <- brandService.exists(brand.code, Some(id))
              sameName <- brandService.nameExists(brand.uniqueName, Some(id))
            } yield (sameCode, sameName)) flatMap {
              case (false, _) => badRequest(views.html.v2.brand.form(user, Some(id), people,
                form.withError("code", "constraint.brand.code.exists", brand.code)))
              case (_, false) => badRequest(views.html.v2.brand.form(user, Some(id), people,
                form.withError("uniqueName", "constraint.brand.code.exists", brand.uniqueName)))
              case _ =>
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
                      BadRequest(views.html.v2.brand.form(user, Some(id), people,
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
      } getOrElse notFound(views.html.notFoundPage(request.path))
    }
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
      val image: Future[Array[Byte]] = brandService.find(code) flatMap {
        case None => Future.successful(empty)
        case Some(brand) =>
          brand.picture map { picture ⇒
            val result = S3Bucket.get(brand.picture.get)
            result.map {
              case BucketFile(name, contentType, content, acl, headers) ⇒ content
            }.recover {
              case S3Exception(status, code, message, originalXml) ⇒ empty
            }
          } getOrElse Future.successful(empty)
      }
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
  def events(brandId: Long, future: Option[Boolean] = None) = AsyncSecuredRestrictedAction(Viewer) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒
      implicit val eventWrites = new Writes[Event] {
        def writes(data: Event): JsValue = {
          Json.obj(
            "id" -> data.id.get,
            "title" -> data.longTitle)
        }
      }

      brandService.findWithCoordinators(brandId) flatMap {
        case None => notFound("Brand not found")
        case Some(x) =>
          val account = user.account
          val events = if (x.coordinators.exists(_._1.id == Some(account.personId))) {
            eventService.findByParameters(x.brand.id, future, archived = Some(false))
          } else {
            eventService.findByFacilitator(account.personId, x.brand.id, future, archived = Some(false))
          }
          events.flatMap { value =>
            ok(Json.toJson(value))
          }
      }
  }
}

object Brands {

  /**
    * Returns url to a brand's picture
    *
    * @param brand Brand
    */
  def pictureUrl(brand: Brand): Option[String] = {
    brand.picture.map { path =>
      Utilities.cdnUrl(path).orElse(Some(Utilities.fullUrl(routes.Brands.picture(brand.code).url)))
    } getOrElse None
  }
}