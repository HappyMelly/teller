package controllers

import play.api._
import play.api.mvc._
import models.{ Organisation, Activity }
import securesocial.core.SecureSocial
import play.api.data._
import play.api.data.Forms._
import models.Organisation
import play.api.i18n.Messages

object Organisations extends Controller with SecureSocial {

  val organisationForm = Form(mapping(
    "id" -> ignored(Option.empty[Long]),
    "name" -> nonEmptyText,
    "street1" -> optional(text),
    "street2" -> optional(text),
    "city" -> optional(text),
    "province" -> optional(text),
    "postCode" -> optional(text),
    "country" -> nonEmptyText(2, 2),
    "vatNumber" -> optional(text),
    "registrationNumber" -> optional(text),
    "legalEntity" -> default(boolean, false),
    "active" -> ignored(true))(Organisation.apply)(Organisation.unapply))

  /**
   * List page.
   */
  def index = SecuredAction { implicit request ⇒
    val organisations = models.Organisation.findAll
    Ok(views.html.organisation.index(request.user, organisations))
  }

  /**
   * Create page.
   */
  def add = SecuredAction { implicit request ⇒
    Ok(views.html.organisation.form(request.user, None, organisationForm))
  }

  /**
   * Edit page.
   * @param id The id of the organisation to edit.
   */
  def edit(id: Long) = TODO

  /**
   * Create form submits to this action.
   */
  def create = SecuredAction { implicit request ⇒
    organisationForm.bindFromRequest.fold(
      formWithErrors ⇒
        BadRequest(views.html.organisation.form(request.user, None, formWithErrors))
          .flashing("error" -> Messages("organisation.errors")),
      organisation ⇒ {
        val org = organisation.save
        Logger.info(org.toString)
        Redirect(routes.Organisations.index()).flashing("success" -> Messages("organisation.created", org.name))
      })
  }

  /**
   * Edit form submits to this action.
   * @param id
   * @return
   */
  def update(id: Long) = TODO

  /**
   * Deletes an organisation.
   */
  def delete(id: Long) = SecuredAction { request ⇒
    Organisation.find(id).map { organisation ⇒
      Organisation.delete(id)
      Activity.insert(request.user.fullName, Activity.Predicate.Deleted, organisation.name)
      val message = Messages("success.delete", Messages("models.Organisation"), organisation.name)
      Redirect(routes.Organisations.index).flashing("success" -> message)
    }.getOrElse(NotFound)
  }

  /**
   * Details page.
   */
  def details(id: Long) = SecuredAction { implicit request ⇒
    models.Organisation.find(id).map { organisation ⇒
      val members = Organisation.members(organisation)
      Ok(views.html.organisation.details(request.user, organisation, members))
    } getOrElse {
      Redirect(routes.Organisations.index).flashing("error" -> Messages("error.organisation.notFound"))
    }
  }

}
