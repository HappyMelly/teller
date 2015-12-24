package controllers

import models.ActiveUser
import models.UserRole.Role
import models.service.Services
import securesocial.core.RuntimeEnvironment
import scala.concurrent.Future

/**
  * Contains methods for managing event requests UI
  */
class EventRequests(environment: RuntimeEnvironment[ActiveUser]) extends JsonController
with Security
with Services
with Utilities {

  override implicit val env: RuntimeEnvironment[ActiveUser] = environment

  /**
    * Renders details info for the given request
    * @param brandId Brand identifier
    * @param requestId Request identifier
    */
  def details(brandId: Long, requestId: Long) = AsyncSecuredBrandAction(brandId) { implicit request =>
    implicit handler => implicit user => Future.successful {
      eventRequestService.find(requestId) map { request =>
        Ok(views.html.v2.eventRequest.details(request))
      } getOrElse jsonBadRequest("Event request not found")
    }
  }

  /**
    * Returns list of event requests for the given brand
    * @param brandId Brand identifier
    */
  def index(brandId: Long) = AsyncSecuredRestrictedAction(List(Role.Facilitator, Role.Coordinator)) {
    implicit request => implicit handler => implicit user => Future.successful {
      val requests = eventRequestService.findByBrand(brandId)
      roleDiffirentiator(user.account, Some(brandId)) { (view, brands) =>
        Ok(views.html.v2.eventRequest.index(user, view.brand, brands, requests))
      } { (brand, brands) =>
        Redirect(routes.Dashboard.index())
      } {
        Redirect(routes.Dashboard.index())
      }
    }
  }
}
