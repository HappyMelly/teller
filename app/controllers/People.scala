package controllers

import play.api.mvc._
import securesocial.core.SecureSocial

object People extends Controller with SecureSocial {

  def index = SecuredAction { implicit request ⇒
    val people = models.Person.findAll
    Ok(views.html.person.index(request.user, people))
  }

}