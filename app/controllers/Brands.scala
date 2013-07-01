package controllers

import play.api.mvc._
import securesocial.core.SecureSocial

object Brands extends Controller with SecureSocial {

  def index = SecuredAction { implicit request ⇒
    val brands = models.Brand.findAll
    Ok(views.html.brand.index(request.user, brands))
  }

}