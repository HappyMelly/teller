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

import models._
import models.service.Services
import play.api.mvc._
import org.joda.time._
import play.api.data._
import play.api.data.validation.Constraints._
import play.api.data.Forms._
import models.UserRole.Role._
import play.api.data.format.Formatter
import play.api.i18n.Messages
import play.api.cache.Cache
import services._
import play.api.data.FormError
import securesocial.core.SecuredRequest
import fly.play.s3.{ BucketFile, S3Exception }
import play.api.Play.current
import scala.io.Source
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits._

trait Products extends JsonController with Security with Services {

  val contentType = "image/jpeg"
  val encoding = "ISO-8859-1"

  /**
   * Formatter used to define a form mapping for the `ProductCategory` enumeration.
   */
  implicit def categoryFormat: Formatter[ProductCategory.Value] = new Formatter[ProductCategory.Value] {

    def bind(key: String, data: Map[String, String]) = {
      try {
        data.get(key).map(ProductCategory.withName).toRight(Seq.empty)
      } catch {
        case e: NoSuchElementException ⇒ Left(Seq(FormError(key, "error.invalid")))
      }
    }

    def unbind(key: String, value: ProductCategory.Value) = Map(key -> value.toString)
  }

  val categoryMapping = of[ProductCategory.Value]

  /** HTML form mapping for creating and editing. */
  def productForm(implicit user: UserIdentity) = Form(mapping(
    "id" -> ignored(Option.empty[Long]),
    "title" -> text.verifying(nonEmpty),
    "subtitle" -> optional(text),
    "url" -> optional(text),
    "description" -> optional(text),
    "callToActionUrl" -> optional(text),
    "callToActionText" -> optional(text),
    "picture" -> optional(text),
    "category" -> optional(categoryMapping),
    "parentId" -> optional(nonEmptyText.transform(_.toLong, (l: Long) ⇒ l.toString)),
    "active" -> ignored(true),
    "created" -> ignored(DateTime.now),
    "createdBy" -> ignored(user.fullName),
    "updated" -> ignored(DateTime.now),
    "updatedBy" -> ignored(user.fullName))(Product.apply)(Product.unapply))

  /** Show all products **/
  def index = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      val products = productService.findAll
      Ok(views.html.product.index(user, products))
  }

  /** Add page **/
  def add = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      Ok(views.html.product.form(user, None, None, productForm))
  }

  /** Add form submits to this action **/
  def create = AsyncSecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      val form: Form[Product] = productForm.bindFromRequest
      form.fold(
        formWithErrors ⇒ Future.successful(BadRequest(views.html.product.form(user, None, None, formWithErrors))),
        product ⇒ {
          if (productService.titleExists(product.title))
            Future.successful(BadRequest(views.html.product.form(user, None, None,
              form.withError("title", "constraint.product.title.exists", product.title))))
          else {
            request.body.asMultipartFormData.get.file("picture").map { picture ⇒
              val filename = Product.generateImageName(picture.filename)
              val source = Source.fromFile(picture.ref.file.getPath, encoding)
              val byteArray = source.toArray.map(_.toByte)
              source.close()
              S3Bucket.add(BucketFile(filename, contentType, byteArray)).map { unit ⇒
                product.copy(picture = Some(filename)).insert
                val activity = Activity.insert(user.fullName, Activity.Predicate.Created, product.title)
                Redirect(routes.Products.index()).flashing("success" -> activity.toString)
              }.recover {
                case S3Exception(status, code, message, originalXml) ⇒ BadRequest(views.html.product.form(user, None, None,
                  form.withError("picture", "Image cannot be temporary saved")))
              }
            }.getOrElse {
              product.insert
              val activity = Activity.insert(user.fullName, Activity.Predicate.Created, product.title)
              Future.successful(Redirect(routes.Products.index()).flashing("success" -> activity.toString))
            }
          }
        })
  }

  /**
   * Assign the product to a brand
   */
  def addBrand() = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      val assignForm = Form(tuple("page" -> text, "productId" -> longNumber, "brandId" -> longNumber))

      assignForm.bindFromRequest.fold(
        errors ⇒ BadRequest("brandId missing"),
        {
          case (page, productId, brandId) ⇒ {
            productService.find(productId) map { product ⇒
              brandService.find(brandId) map { brand ⇒
                product.addBrand(brandId)
                val activityObject = Messages("activity.relationship.create", product.title, brand.name)
                val activity = Activity.insert(user.fullName, Activity.Predicate.Created, activityObject)

                // Redirect to the page we came from - either the product or brand details page.
                val action = if (page == "product")
                  routes.Products.details(productId)
                else
                  routes.Brands.details(brand.id.get)
                Redirect(action).flashing("success" -> activity.toString)
              } getOrElse NotFound
            } getOrElse NotFound
          }
        })
  }

  /**
   * Activates/deactivates the given product
   *
   * @param id Product id
   */
  def activation(id: Long) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      productService.find(id).map { product ⇒
        Form("active" -> boolean).bindFromRequest.fold(
          error ⇒ jsonBadRequest("'active' parameter is not found"),
          active ⇒ {
            if (active)
              productService.activate(id)
            else
              productService.deactivate(id)
            jsonSuccess("ok")
          })
      } getOrElse jsonNotFound("Product is not found")
  }

  /**
   * Unassign the product from the brand
   */
  def deleteBrand(page: String, productId: Long, brandId: Long) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      productService.find(productId) map { product: Product ⇒
        brandService.find(brandId).map { brand ⇒
          product.deleteBrand(brandId)
          val activityObject = Messages("activity.relationship.delete", product.title, brand.name)
          val activity = Activity.insert(user.fullName, Activity.Predicate.Deleted, activityObject)

          // Redirect to the page we came from - either the product or brand details page.
          val action = if (page == "product")
            routes.Products.details(productId)
          else
            routes.Brands.details(brand.id.get)
          Redirect(action).flashing("success" -> activity.toString)
        } getOrElse NotFound
      } getOrElse NotFound
  }

  /** Delete a product **/
  def delete(id: Long) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      productService.find(id) map { product ⇒
        product.picture.map { picture ⇒
          S3Bucket.remove(picture)
          Cache.remove(Product.cacheId(product.id.get))
        }
        productService.delete(id)
        val activity = Activity.insert(user.fullName, Activity.Predicate.Deleted, product.title)
        Redirect(routes.Products.index).flashing("success" -> activity.toString)
      } getOrElse NotFound
  }

  /** Delete picture form submits to this action **/
  def deletePicture(id: Long) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      productService.find(id) map { product ⇒
        product.picture.map { picture ⇒
          S3Bucket.remove(picture)
          Cache.remove(Product.cacheId(product.id.get))
        }
        product.copy(picture = None).update
        val activity = Activity.insert(user.fullName, Activity.Predicate.Deleted, "image from the product " + product.title)
        Redirect(routes.Products.details(id)).flashing("success" -> activity.toString)
      } getOrElse NotFound
  }

  /** Details page **/
  def details(id: Long) = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      productService.find(id) map { product ⇒
        val view = ProductView(product, productService.brands(id))
        val derivatives = productService.findDerivatives(id)
        val parent = if (product.parentId.isDefined)
          productService.find(product.parentId.get)
        else
          None
        val brands = Brand.findAllWithCoordinator
        val contributors = Contribution.contributors(id)
        val people = Person.findAll
        val organisations = orgService.findAll

        Ok(views.html.product.details(user, view, derivatives, parent, brands,
          contributors, people, organisations))
      } getOrElse NotFound

  }

  /** Edit page **/
  def edit(id: Long) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      productService.find(id) map {
        product ⇒
          Ok(views.html.product.form(user, Some(id), Some(product.title), productForm.fill(product)))
      } getOrElse NotFound

  }

  /** Edit form submits to this action **/
  def update(id: Long) = AsyncSecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      productService.find(id) map { existingProduct ⇒
        val form: Form[Product] = productForm.bindFromRequest
        val title = Some(existingProduct.title)
        form.fold(
          formWithErrors ⇒ Future.successful(BadRequest(views.html.product.form(user, Some(id), title, form))),
          product ⇒ {
            if (productService.isTitleTaken(product.title, id))
              Future.successful(BadRequest(views.html.product.form(user, Some(id), title,
                form.withError("title", "constraint.product.title.exists", product.title))))
            else {
              request.body.asMultipartFormData.get.file("picture").map { picture ⇒
                val filename = Product.generateImageName(picture.filename)
                val source = Source.fromFile(picture.ref.file.getPath, encoding)
                val byteArray = source.toArray.map(_.toByte)
                source.close()
                S3Bucket.add(BucketFile(filename, contentType, byteArray)).map { unit ⇒
                  product.copy(id = Some(id)).copy(picture = Some(filename)).update
                  Cache.remove(Product.cacheId(id))
                  existingProduct.picture.map { oldPicture ⇒
                    S3Bucket.remove(oldPicture)
                  }
                  val activity = Activity.insert(user.fullName, Activity.Predicate.Updated, product.title)
                  Redirect(routes.Products.details(id)).flashing("success" -> activity.toString)
                }.recover {
                  case S3Exception(status, code, message, originalXml) ⇒
                    BadRequest(views.html.product.form(user, Some(id), title,
                      form.withError("picture", "Image cannot be temporary saved. Please, try again later.")))
                }
              }.getOrElse {
                product.copy(id = Some(id)).copy(picture = existingProduct.picture).update
                val activity = Activity.insert(user.fullName, Activity.Predicate.Updated, product.title)
                Future.successful(Redirect(routes.Products.details(id)).flashing("success" -> activity.toString))
              }
            }
          })
      } getOrElse Future.successful(NotFound)
  }

  /**
   * Retrieve and cache a product's image
   */
  def picture(id: Long) = Action.async {
    val cached = Cache.getAs[Array[Byte]](Product.cacheId(id))
    if (cached.isDefined) {
      Future.successful(Ok(cached.get).as(contentType))
    } else {
      val empty = Array[Byte]()
      val image: Future[Array[Byte]] = productService.find(id) map { entry ⇒
        entry.picture.map { picture ⇒
          val result = S3Bucket.get(entry.picture.get)
          result.map {
            case BucketFile(name, contentType, content, acl, headers) ⇒ content
          }.recover {
            case S3Exception(status, code, message, originalXml) ⇒ empty
          }
        } getOrElse Future.successful(empty)
      } getOrElse Future.successful(empty)
      image.map {
        case value ⇒
          Cache.set(Product.cacheId(id), value)
          Ok(value).as(contentType)
      }
    }
  }

}

object Products extends Products with Security with Services

