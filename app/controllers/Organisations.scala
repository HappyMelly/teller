package controllers

import models.{ Person, Activity, Organisation }
import play.api.mvc._
import securesocial.core.{ SecuredRequest, SecureSocial }
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.Messages
import org.joda.time.DateTime
import models.UserRole.Role._
import securesocial.core.SecuredRequest
import scala.Some

object Organisations extends Controller with Security {

  /**
   * HTML form mapping for creating and editing.
   */
  def organisationForm(implicit request: SecuredRequest[_]) = Form(mapping(
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
    "createdBy" -> ignored(request.user.fullName),
    "updated" -> ignored(DateTime.now()),
    "updatedBy" -> ignored(request.user.fullName))(Organisation.apply)(Organisation.unapply))

  /**
   * Form target for toggling whether an organisation is active.
   */
  def activation(id: Long) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒

      Organisation.find(id).map { organisation ⇒
        Form("active" -> boolean).bindFromRequest.fold(
          form ⇒ {
            BadRequest("invalid form data")
          },
          active ⇒ {
            Organisation.activate(id, active)
            val activity = Activity.insert(request.user.fullName, if (active) Activity.Predicate.Activated else Activity.Predicate.Deactivated, organisation.name)
            Redirect(routes.Organisations.details(id)).flashing("success" -> activity.toString)
          })
      } getOrElse {
        Redirect(routes.Organisations.index).flashing("error" -> Messages("error.notFound", Messages("models.Organisation")))
      }
  }

  /**
   * Create page.
   */
  def add = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒

      Ok(views.html.organisation.form(request.user, None, organisationForm))
  }

  /**
   * Create form submits to this action.
   */
  def create = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒

      organisationForm.bindFromRequest.fold(
        formWithErrors ⇒
          BadRequest(views.html.organisation.form(request.user, None, formWithErrors)),
        organisation ⇒ {
          val org = organisation.save
          val activity = Activity.insert(request.user.fullName, Activity.Predicate.Created, organisation.name)
          Redirect(routes.Organisations.index()).flashing("success" -> activity.toString)
        })
  }

  /**
   * Deletes an organisation.
   * @param id Organisation ID
   */
  def delete(id: Long) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒

      Organisation.find(id).map {
        organisation ⇒
          Organisation.delete(id)
          val activity = Activity.insert(request.user.fullName, Activity.Predicate.Deleted, organisation.name)
          Redirect(routes.Organisations.index).flashing("success" -> activity.toString)
      }.getOrElse(NotFound)
  }

  /**
   * Details page.
   * @param id Organisation ID
   */
  def details(id: Long) = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒

      Organisation.find(id).map {
        organisation ⇒
          val members = organisation.members
          val otherPeople = Person.findActive.filterNot(person ⇒ members.contains(person))
          Ok(views.html.organisation.details(request.user, organisation, members, otherPeople))
      } getOrElse {
        //TODO return 404
        Redirect(routes.Organisations.index).flashing("error" -> Messages("error.notFound", Messages("models.Organisation")))
      }
  }

  /**
   * Edit page.
   * @param id Organisation ID
   */
  def edit(id: Long) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒

      Organisation.find(id).map {
        organisation ⇒
          Ok(views.html.organisation.form(request.user, Some(id), organisationForm.fill(organisation)))
      }.getOrElse(NotFound)
  }

  /**
   * List page.
   */
  def index = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒

      val organisations = Organisation.findAll
      Ok(views.html.organisation.index(request.user, organisations))
  }

  /**
   * Edit form submits to this action.
   * @param id Organisation ID
   */
  def update(id: Long) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒

      organisationForm.bindFromRequest.fold(
        formWithErrors ⇒
          BadRequest(views.html.organisation.form(request.user, Some(id), formWithErrors)),
        organisation ⇒ {
          organisation.copy(id = Some(id)).save
          val activity = Activity.insert(request.user.fullName, Activity.Predicate.Updated, organisation.name)
          Redirect(routes.Organisations.details(id)).flashing("success" -> activity.toString)
        })
  }

}
