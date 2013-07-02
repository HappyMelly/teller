package controllers

import play.api.mvc._
import models.{ Organisation, Activity }
import securesocial.core.SecureSocial
import play.api.i18n.Messages

object Organisations extends Controller with SecureSocial {

  /**
   * List page.
   */
  def index = SecuredAction { implicit request ⇒
    val organisations = models.Organisation.findAll
    Ok(views.html.organisation.index(request.user, organisations))
  }

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