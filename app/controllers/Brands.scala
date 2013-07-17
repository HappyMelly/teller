package controllers

import play.api.mvc._
import securesocial.core.{ SecuredRequest, SecureSocial }
import models.{ Activity, Brand, Person }
import play.api.data.Form
import play.api.data.Forms._
import org.joda.time._
import play.api.i18n.Messages

object Brands extends Controller with SecureSocial {

  /** HTML form mapping for creating and editing. */
  def brandsForm(implicit request: SecuredRequest[_]) = Form(mapping(
    "id" -> ignored(Option.empty[Long]),
    "code" -> nonEmptyText,
    "name" -> nonEmptyText,
    "coordinatorId" -> longNumber,
    "created" -> ignored(DateTime.now()),
    "createdBy" -> ignored(request.user.fullName),
    "updated" -> ignored(DateTime.now()),
    "updatedBy" -> ignored(request.user.fullName))(Brand.apply)(Brand.unapply))

  def index = SecuredAction { implicit request ⇒
    val brands = models.Brand.findAll
    Ok(views.html.brand.index(request.user, brands))
  }

  def add = SecuredAction { implicit request ⇒
    Ok(views.html.brand.form(request.user, None, brandsForm))
  }

  def create = SecuredAction { implicit request ⇒
    brandsForm.bindFromRequest.fold(
      formWithErrors ⇒ BadRequest(views.html.brand.form(request.user, None, formWithErrors)),
      brand ⇒ {
        val savedBrand = brand.insert
        Activity.insert(request.user.fullName, Activity.Predicate.Created, brand.name)
        val message = Messages("success.insert", Messages("models.Brand"), savedBrand.name)
        Redirect(routes.Brands.index()).flashing("success" -> message)
      })
  }

}
