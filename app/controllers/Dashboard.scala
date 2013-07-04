package controllers

import play.api.mvc._
import models.{ LoginIdentity, Activity }
import securesocial.core.SecureSocial

object Dashboard extends Controller with SecureSocial {

  /**
   * About page - credits.
   */
  def about = SecuredAction { implicit request ⇒
    Ok(views.html.about(request.user.asInstanceOf[LoginIdentity]))
  }

  /**
   * API documentation page.
   */
  def api = SecuredAction { implicit request ⇒
    Ok(views.html.api(request.user))
  }

  /**
   * Dashboard page - logged-in home page.
   */
  def index = SecuredAction { implicit request ⇒
    val activity = Activity.findAll
    Ok(views.html.dashboard(request.user, activity))
  }

}
