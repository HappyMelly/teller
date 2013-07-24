package controllers

import play.api.mvc._
import securesocial.core.{ SecuredRequest, SecureSocial }
import models.{ Activity, Brand, Person }
import play.api.data.Form
import play.api.data.validation.Constraints._
import play.api.data.Forms._
import org.joda.time._
import play.api.i18n.Messages

object Brands extends Controller with SecureSocial {

  /** HTML form mapping for creating and editing. */
  def brandsForm(implicit request: SecuredRequest[_]) = Form(mapping(
    "id" -> ignored(Option.empty[Long]),
    "code" -> nonEmptyText.verifying(pattern("[A-Z0-9]*".r, "constraint.brand.code", "constraint.brand.code.error"), maxLength(5)),
    "name" -> nonEmptyText,
    "coordinatorId" -> nonEmptyText.transform(_.toLong, (l: Long) ⇒ l.toString),
    "created" -> ignored(DateTime.now()),
    "createdBy" -> ignored(request.user.fullName),
    "updated" -> ignored(DateTime.now()),
    "updatedBy" -> ignored(request.user.fullName))(Brand.apply)(Brand.unapply))

  def index = SecuredAction { implicit request ⇒
    val brands = models.Brand.findAll
    Ok(views.html.brand.index(request.user, brands))
  }

  /** Show all brands **/
  def add = SecuredAction { implicit request ⇒
    Ok(views.html.brand.form(request.user, None, brandsForm))
  }

  def create = SecuredAction { implicit request ⇒
    val boundForm: Form[Brand] = brandsForm.bindFromRequest
    boundForm.fold(
      formWithErrors ⇒ BadRequest(views.html.brand.form(request.user, None, formWithErrors)),
      brand ⇒ {
        if (Brand.exists(brand.code)) BadRequest(views.html.brand.form(request.user, None,
          boundForm.withError("code", "constraint.brand.code.exists", brand.code)))

        val savedBrand = brand.insert
        Activity.insert(request.user.fullName, Activity.Predicate.Created, brand.name)
        val message = Messages("success.insert", Messages("models.Brand"), savedBrand.name)
        Redirect(routes.Brands.index()).flashing("success" -> message)
      })
  }

  /** Delete a brand **/
  def delete(id: Long) = SecuredAction { implicit request ⇒
    Brand.find(id).map { brand ⇒
      brand.delete
      Activity.insert(request.user.fullName, Activity.Predicate.Deleted, brand.name)
      val message = Messages("success.delete", Messages("models.Brand"), brand.name)
      Redirect(routes.Brands.index()).flashing("success" -> message)
    }.getOrElse(NotFound)
  }

}
