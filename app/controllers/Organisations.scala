package controllers

import play.api._
import play.api.i18n.Messages
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import models.{ Organisation, Activity }
import securesocial.core.{ SecuredRequest, SecureSocial }
import play.api.data._
import play.api.data.Forms._
import models.Organisation
import play.api.i18n.Messages
import org.joda.time.DateTime

object Organisations extends Controller with SecureSocial {

  /**
   * HTML form mapping for creating and editing.
   */
  def organisationForm(request: SecuredRequest[_]) = Form(mapping(
    "id" -> ignored(Option.empty[Long]),
    "name" -> nonEmptyText,
    "street1" -> optional(text),
    "street2" -> optional(text),
    "city" -> optional(text),
    "province" -> optional(text),
    "postCode" -> optional(text),
    "country" -> nonEmptyText,
    "vatNumber" -> optional(text),
    "registrationNumber" -> optional(text),
    "legalEntity" -> default(boolean, false),
    "active" -> ignored(true),
    "created" -> ignored(DateTime.now()),
    "createdBy" -> ignored(request.user.fullName))(Organisation.apply)(Organisation.unapply))

  /**
   * Form target for toggling whether an organisation is active.
   */
  def activation(id: Long) = SecuredAction { implicit request ⇒

    models.Organisation.find(id).map { organisation ⇒
      Form("active" -> boolean).bindFromRequest.fold(
        form ⇒ {
          BadRequest("invalid form data")
        },
        active ⇒ {
          models.Organisation.activate(id, active)
          Activity.insert(request.user.fullName, if (active) Activity.Predicate.Activated else Activity.Predicate.Deactivated, organisation.name)
          val message = Messages("success.activate." + active.toString, Messages("models.Organisation"), organisation.name)
          Redirect(routes.Organisations.details(id)).flashing("success" -> message)
        })
    } getOrElse {
      Redirect(routes.Organisations.index).flashing("error" -> Messages("error.organisation.notFound"))
    }
  }

  /**
   * Create page.
   */
  def add = SecuredAction { implicit request ⇒
    Ok(views.html.organisation.form(request.user, None, organisationForm(request)))
  }

  /**
   * Create form submits to this action.
   */
  def create = SecuredAction { implicit request ⇒
    organisationForm(request).bindFromRequest.fold(
      formWithErrors ⇒
        BadRequest(views.html.organisation.form(request.user, None, formWithErrors)),
      organisation ⇒ {
        val org = organisation.save
        Activity.insert(request.user.fullName, Activity.Predicate.Created, organisation.name)
        val message = Messages("success.insert", Messages("models.Organisation"), organisation.name)
        Redirect(routes.Organisations.index()).flashing("success" -> message)
      })
  }

  /**
   * Deletes an organisation.
   * @param id Organisation ID
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
   * @param id Organisation ID
   */
  def details(id: Long) = SecuredAction { implicit request ⇒
    models.Organisation.find(id).map { organisation ⇒
      val members = Organisation.members(organisation)
      Ok(views.html.organisation.details(request.user, organisation, members))
    } getOrElse {
      Redirect(routes.Organisations.index).flashing("error" -> Messages("error.organisation.notFound"))
    }
  }

  /**
   * Edit page.
   * @param id Organisation ID
   */
  def edit(id: Long) = TODO

  /**
   * List page.
   */
  def index = SecuredAction { implicit request ⇒
    val organisations = models.Organisation.findAll
    Ok(views.html.organisation.index(request.user, organisations))
  }

  /**
   * Edit form submits to this action.
   * @param id Organisation ID
   */
  def update(id: Long) = TODO

}