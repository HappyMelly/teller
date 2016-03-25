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
 * If you have questions concerning this license or the applicable additional terms, you may contact
 * by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */

package controllers

import javax.inject.{Inject, Named}

import akka.actor.ActorRef
import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import controllers.Forms._
import fly.play.s3.{BucketFile, S3Exception}
import models.cm.Event
import models.UserRole.Role._
import models._
import models.cm.brand.BrandCoordinator
import models.repository.Repositories
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
import securesocial.core.providers.MailToken
import services._

import scala.concurrent.Future
import scala.io.Source

case class BrandProfileView(brand: Brand, profile: SocialProfile)

class Brands @Inject() (override implicit val env: TellerRuntimeEnvironment,
                        override val messagesApi: MessagesApi,
                        val repos: Repositories,
                        @Named("peer-credits") creditsConfigurator: ActorRef,
                        deadbolt: DeadboltActions, handlers: HandlerCache, actionBuilder: ActionBuilders)
  extends Security(deadbolt, handlers, actionBuilder, repos)(messagesApi, env)
  with Activities
  with PasswordIdentities {

  val contentType = "image/jpeg"
  val encoding = "ISO-8859-1"
  val indexCall: Call = routes.Brands.index()

  /** Shows all brands **/
  def index = RestrictedAction(Viewer) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    repos.cm.brand.findAllWithCoordinator flatMap { brands =>
      ok(views.html.v2.brand.index(user, brands))
    }
  }

  /**
    * Activates/deactivates the given brand
    *
    * @param id Product id
    */
  def activation(id: Long) = RestrictedAction(Admin) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    repos.cm.brand.find(id) flatMap { maybeBrand =>
      maybeBrand map { brand ⇒
        Form("active" -> boolean).bindFromRequest.fold(
          error ⇒ jsonBadRequest("active parameter is not found"),
          active ⇒ {
            if (active) {
              repos.cm.brand.activate(id)
            } else {
              repos.cm.brand.deactivate(id)
              repos.product.findByBrand(id) map { products =>
                for (product <- products) {
                  repos.product.deactivate(product.id.get)
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
  def add = RestrictedAction(Admin) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    repos.person.findActive.flatMap { people =>
      ok(views.html.v2.brand.form(user, None, people, brandsForm(user.name)))
    }
  }

  /**
    * Create a new brand
    */
  def create = RestrictedAction(Admin) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    val form: Form[BrandProfileView] = brandsForm(user.name).bindFromRequest
    repos.person.findActive.flatMap { people =>
      form.fold(
        errors ⇒ badRequest(views.html.v2.brand.form(user, None, people, errors)),
        view ⇒ {
          (for {
            existance <- repos.cm.brand.exists(view.brand.code)
            nameExistance <- repos.cm.brand.nameExists(view.brand.uniqueName)
          } yield (existance, nameExistance)) flatMap {
            case (true, _) =>
              badRequest(views.html.v2.brand.form(user, None, people,
                form.withError("code", "constraint.brand.code.exists", view.brand.code)))
            case (_, true) =>
              badRequest(views.html.v2.brand.form(user, None, people,
                form.withError("uniqueName", "constraint.brand.code.exists", view.brand.uniqueName)))
            case (false, false) =>
              request.body.asMultipartFormData.get.file("picture").map { picture ⇒
                val filename = Brand.generateImageName(picture.filename)
                val source = Source.fromFile(picture.ref.file.getPath, encoding)
                val byteArray = source.toArray.map(_.toByte)
                source.close()
                S3Bucket.add(BucketFile(filename, contentType, byteArray)).map { unit ⇒
                  repos.cm.brand.insert(BrandProfileView(view.brand.copy(picture = Some(filename)), view.profile))
                  val log = activity(view.brand, user.person).created.insert(repos)
                  Redirect(indexCall).flashing("success" -> "Brand was added")
                }.recover {
                  case S3Exception(status, code, message, originalXml) ⇒ BadRequest(views.html.v2.brand.form(user, None, people,
                    form.withError("picture", "Image cannot be temporary saved")))
                }
              }.getOrElse {
                repos.cm.brand.insert(view) flatMap { inserted =>
                  val log = activity(inserted, user.person).created.insert(repos)
                  redirect(indexCall, "success" -> "Brand was added")
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
  def delete(id: Long) = BrandAction(id) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    repos.cm.brand.find(id) flatMap { maybeBrand =>
      maybeBrand map { brand =>
        brand.picture.foreach { picture ⇒
          S3Bucket.remove(picture)
          Cache.remove(Brand.cacheId(brand.code))
        }
        repos.cm.brand.delete(brand)
        val log = activity(brand, user.person).deleted.insert(repos)
        redirect(indexCall, "success" -> "Brand was deleted")
      } getOrElse notFound(views.html.notFoundPage(request.path))
    }
  }

  /**
    * Deletes picture in the given brand
    *
    * @param id Brand string identifier
    */
  def deletePicture(id: Long) = BrandAction(id) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    repos.cm.brand.find(id) flatMap { maybeBrand =>
      maybeBrand map { brand ⇒
        brand.picture.foreach { picture ⇒
          S3Bucket.remove(picture)
          Cache.remove(Brand.cacheId(brand.code))
        }
        repos.cm.brand.updatePicture(id, None)
        val log = activity(brand, user.person).deletedImage.insert(repos)
        val call: Call = routes.Brands.details(id)
        redirect(call, "success" -> "Brand picture was removed")
      } getOrElse notFound(views.html.notFoundPage(request.path))
    }
  }

  /**
    * Renders a brand page of the given brand
    *
    * @param id Brand identifier
    */
  def details(id: Long) = RestrictedAction(Viewer) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    repos.cm.brand.find(id) flatMap {
      case None => notFound(views.html.notFoundPage(request.path))
      case Some(brand) =>
        (for {
          links <- repos.cm.rep.brand.link.find(id)
          coordinator <- repos.person.find(brand.ownerId)
          p <- repos.socialProfile.find(id, ProfileType.Brand)
          deletable <- repos.cm.brand.deletable(id)
        } yield (links, coordinator, p, deletable)) flatMap { case (links, coordinator, profile, deletable) =>
          ok(views.html.v2.brand.details(user, brand, profile, coordinator, links, deletable))
        }
    }
  }

  /**
    * Renders tab for the given brand
    *
    * @param id Brand identifier
    * @param tab Tab identifier
    */
  def renderTabs(id: Long, tab: String) = RestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      tab match {
        case "testimonials" ⇒
          repos.cm.rep.brand.testimonial.findByBrand(id) flatMap { testimonials =>
            ok(views.html.v2.brand.tabs.testimonials(id, testimonials))
          }
        case _ ⇒
          repos.product.findByBrand(id) flatMap { products =>
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
  def renderCoordinatorTabs(id: Long, tab: String)  = BrandAction(id) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      tab match {
        case "team" ⇒
          (for {
            coordinators <- repos.cm.brand.coordinators(id)
            people <- repos.person.findActive
          } yield (coordinators, people)) flatMap { case (coordinators, people) =>
            val members = coordinators.sortBy(_._1.fullName)
            val filtered = people.filterNot(x ⇒ members.contains(x))
            ok(views.html.v2.brand.tabs.team(id, members, filtered))
          }
        case "templates" ⇒
          repos.cm.certificate.findByBrand(id) flatMap { templates =>
            ok(views.html.v2.brand.tabs.templates(id, templates))
          }
        case "types" ⇒
          repos.cm.rep.brand.eventType.findByBrand(id) flatMap { eventTypes =>
            ok(views.html.v2.brand.tabs.eventTypes(id, eventTypes.sortBy(_.name)))
          }
        case _ =>
          repos.cm.brand.findWithSettings(id) flatMap { maybeView =>
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
  def addCoordinator(id: Long) = BrandAction(id) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    val data = Form(single("personId" -> longNumber(min = 1)))
    data.bindFromRequest.fold(
      hasErrors ⇒ jsonBadRequest("personId should be a positive number"),
      personId ⇒
        (for {
          coordinators <- repos.cm.brand.coordinators(id)
          person <- repos.person.find(personId)
          brand <- repos.cm.brand.get(id)
        } yield (coordinators, person, brand)) flatMap {
          case (coordinators, None, brand) => jsonNotFound(Messages("error.notFound", "person"))
          case (coordinators, Some(person), brand) =>
            if (coordinators.exists(_._1 == person)) {
              jsonConflict(Messages("error.brand.alreadyMember"))
            } else {
              val coordinator = BrandCoordinator(None, id, personId)
              repos.cm.rep.brand.coordinator.save(coordinator).flatMap { coordinator =>
                giveCoordinatorAccess(person, brand)
                creditsConfigurator ! (personId, id, true)
                val data = Json.obj("personId" -> personId, "brandId" -> id, "name" -> person.fullName)
                jsonSuccess(Messages("success.brand.newMember"), Some(data))
              }
            }
        })
  }

  /**
    * Removes the given coordinator from the given brand
    *
    * @param id Brand id
    * @param personId Person id
    */
  def removeCoordinator(id: Long, personId: Long) = BrandAction(id) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒
      repos.cm.brand.find(id) flatMap {
        case None => jsonNotFound(Messages("error.notFound", "brand"))
        case Some(x) =>
          if (x.ownerId == personId) {
            jsonConflict(Messages("error.brand.removeOwner"))
          } else {
            (for {
              _ <- repos.cm.rep.brand.coordinator.delete(id, personId)
              brand <- repos.cm.brand.findByCoordinator(personId)
            } yield brand.isEmpty) flatMap { noEntries =>
              if (noEntries) {
                repos.userAccount.findByPerson(personId) map {
                  case None => Future.successful(Unit)
                  case Some(account) =>
                    repos.userAccount.update(account.copy(coordinator = false, activeRole = false))
                }
              }
              creditsConfigurator ! (personId, id, false)
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
  def turnNotificationOff(id: Long, personId: Long, notification: String) = BrandAction(id) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒
      repos.cm.rep.brand.coordinator.update(id, personId, notification, false).flatMap { _ =>
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
  def turnNotificationOn(id: Long, personId: Long, notification: String) = BrandAction(id) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒
      repos.cm.rep.brand.coordinator.update(id, personId, notification, true).flatMap { _ =>
        jsonSuccess("Changes saved")
      }
  }

  /**
    * Renders a Brand edit page
    *
    * @param id Brand identifier
    */
  def edit(id: Long) = BrandAction(id) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    (for {
      b <- repos.cm.brand.find(id)
      p <- repos.socialProfile.find(id, ProfileType.Brand)
    } yield (b, p)) flatMap {
      case (None, _) => notFound(views.html.notFoundPage(request.path))
      case (Some(brand), profile) =>
        val filledForm = brandsForm(user.name).fill(BrandProfileView(brand, profile))
        repos.person.findActive flatMap { people =>
          ok(views.html.v2.brand.form(user, Some(id), people, filledForm))
        }
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
    "recordInfo" -> mapping(
      "created" -> ignored(DateTime.now()),
      "createdBy" -> ignored(userName),
      "updated" -> ignored(DateTime.now()),
      "updatedBy" -> ignored(userName))(DateStamp.apply)(DateStamp.unapply))({
    (id, code, uniqueName, name, ownerId, description, picture,
     tagLine, webSite, blog, email, profile, recordInfo) ⇒ {
      val brand = Brand(id, code, uniqueName, name, ownerId,
        description, picture, tagLine, webSite, blog, email, active = true, recordInfo = recordInfo)
      BrandProfileView(brand, profile)
    }
  })({ (view: BrandProfileView) ⇒
    Some((view.brand.id, view.brand.code, view.brand.uniqueName, view.brand.name, view.brand.ownerId,
      view.brand.description, view.brand.picture, view.brand.tagLine, view.brand.webSite, view.brand.blog,
      view.brand.contactEmail, view.profile, view.brand.recordInfo))
  }))

  /**
    * Updates the given brand
    *
    * @param id Brand identifier
    */
  def update(id: Long) = BrandAction(id) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    (for {
      brand <- repos.cm.brand.find(id)
      people <- repos.person.findActive
    } yield (brand, people)) flatMap { case (maybeBrand, people) =>
      maybeBrand map { x ⇒
        val form: Form[BrandProfileView] = brandsForm(user.name).bindFromRequest
        form.fold(
          formWithErrors ⇒ badRequest(views.html.v2.brand.form(user, Some(id), people, form)),
          view ⇒ {
            (for {
              sameCode <- repos.cm.brand.exists(view.brand.code, Some(id))
              sameName <- repos.cm.brand.nameExists(view.brand.uniqueName, Some(id))
            } yield (sameCode, sameName)) flatMap {
              case (true, _) => badRequest(views.html.v2.brand.form(user, Some(id), people,
                form.withError("code", "constraint.brand.code.exists", view.brand.code)))
              case (_, true) => badRequest(views.html.v2.brand.form(user, Some(id), people,
                form.withError("uniqueName", "constraint.brand.code.exists", view.brand.uniqueName)))
              case _ =>
                request.body.asMultipartFormData.get.file("picture").map { picture ⇒
                  val filename = Brand.generateImageName(picture.filename)
                  val source = Source.fromFile(picture.ref.file.getPath, encoding)
                  val byteArray = source.toArray.map(_.toByte)
                  source.close()
                  S3Bucket.add(BucketFile(filename, contentType, byteArray)).map { unit ⇒
                    val updatedBrand = repos.cm.brand.update(x, view, Some(filename))
                    Cache.remove(Brand.cacheId(x.code))
                    if (x.code != updatedBrand.code) {
                      Cache.remove(Brand.cacheId(updatedBrand.code))
                    }
                    x.picture.map { oldPicture ⇒
                      S3Bucket.remove(oldPicture)
                    }
                    val log = activity(view.brand, user.person).updated.insert(repos)
                    Redirect(routes.Brands.details(id)).flashing("success" -> "Brand was updated")
                  }.recover {
                    case S3Exception(status, code, message, originalXml) ⇒
                      BadRequest(views.html.v2.brand.form(user, Some(id), people,
                        form.withError("picture", "Image cannot be temporary saved. Please, try again later.")))
                  }
                }.getOrElse {
                  val updatedBrand = repos.cm.brand.update(x, view, x.picture)
                  val log = activity(updatedBrand, user.person).updated.insert(repos)
                  val route = routes.Brands.details(id)
                  Future.successful(Redirect(route).flashing("success" -> "Brand was updated"))
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
      val image: Future[Array[Byte]] = repos.cm.brand.find(code) flatMap {
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
  def events(brandId: Long, future: Option[Boolean] = None) = RestrictedAction(Viewer) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒
      implicit val eventWrites = new Writes[Event] {
        def writes(data: Event): JsValue = {
          Json.obj(
            "id" -> data.id.get,
            "title" -> data.longTitle)
        }
      }

      repos.cm.brand.findWithCoordinators(brandId) flatMap {
        case None => notFound("Brand not found")
        case Some(x) =>
          val account = user.account
          val events = if (x.coordinators.exists(_._1.id == Some(account.personId))) {
            repos.cm.event.findByParameters(x.brand.id, future, archived = Some(false))
          } else {
            repos.cm.event.findByFacilitator(account.personId, x.brand.id, future, archived = Some(false))
          }
          events.flatMap { value =>
            ok(Json.toJson(value))
          }
      }
  }

  /**
    * Creates an account with coordinator access
    * It also sends an email to the user inviting to create new password
    *
    * @param person Person
    * @param brand Brand
    */
  protected def giveCoordinatorAccess(person: Person, brand: Brand)(implicit request: RequestHeader): Unit = {
    createToken(person.email, isSignUp = false).map { token =>
      val unexpirable = unexpirableToken(token)
      repos.userAccount.findByPerson(person.identifier) map {
        case None =>
          val account = UserAccount.empty(person.identifier).copy(
            byEmail = true, coordinator = true, registered = true, activeRole = true)
          repos.userAccount.insert(account)
          setupLoginByEmailEnvironment(person, unexpirable)
          sendCoordinatorWelcomeEmail(person, brand.name, unexpirable.uuid)
        case Some(account) =>
          if (!account.hasAccess) {
            setupLoginByEmailEnvironment(person, unexpirable)
            sendCoordinatorWelcomeEmail(person, brand.name, unexpirable.uuid)
            repos.userAccount.update(account.copy(byEmail = true))
          }
      }
    }
  }

  /**
    * Sends a welcome email to a new coordinator
    *
    * @param person Person
    * @param brand Brand name
    * @param token Unique token for password creation
    */
  protected def sendCoordinatorWelcomeEmail(person: Person, brand: String, token: String)(implicit request: RequestHeader) = {
    env.mailer.sendEmail(s"Your Coordinator Account for $brand",
      person.email,
      (None, Some(mail.password.html.coordinator(person.firstName, token, brand)))
    )
  }

  /**
    * Returns a token which will expire only in 10 years
    *
    * @param token Token
    */
  protected def unexpirableToken(token: MailToken): MailToken =
    token.copy(expirationTime = token.expirationTime.plusYears(10))
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