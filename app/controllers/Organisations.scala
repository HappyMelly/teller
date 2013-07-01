package controllers

import play.api.mvc._
import models.Activity
import securesocial.core.SecureSocial

object Organisations extends Controller with SecureSocial {

  def index = SecuredAction { implicit request ⇒
    val organisations = models.Organisation.findAll
    Ok(views.html.organisation.index(request.user, organisations))
  }

}