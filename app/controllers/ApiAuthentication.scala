package controllers

import play.api.mvc._
import models.LoginIdentity

/**
 * Provides token-based authentication for API actions.
 */
trait ApiAuthentication extends Controller {

  /** Make an action require token authentication **/
  def TokenSecuredAction(f: Request[AnyContent] ⇒ Result) = Action { implicit request ⇒
    request.getQueryString(ApiToken).flatMap(token ⇒
      LoginIdentity.findBytoken(token).map(identity ⇒ f(request))).getOrElse(Unauthorized("Unauthorized"))
  }

  val ApiToken = "api_token"

}
