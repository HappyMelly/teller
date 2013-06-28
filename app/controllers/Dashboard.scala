package controllers

import play.api.mvc._
import models.Activity
import securesocial.core.SecureSocial

object Dashboard extends Controller with SecureSocial {

  def index = SecuredAction { implicit request ⇒
    val activity = Activity.findAll
    Ok(views.html.dashboard(request.user, activity))
  }

}