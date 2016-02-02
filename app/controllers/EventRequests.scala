package controllers

import javax.inject.Inject

import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import be.objectify.deadbolt.scala.cache.HandlerCache
import models.UserRole.Role
import models.service.Services
import play.api.i18n.MessagesApi
import services.TellerRuntimeEnvironment

/**
  * Contains methods for managing event requests UI
  */
class EventRequests @Inject() (override implicit val env: TellerRuntimeEnvironment,
                               override val messagesApi: MessagesApi,
                               deadbolt: DeadboltActions, handlers: HandlerCache, actionBuilder: ActionBuilders)
  extends Security(deadbolt, handlers, actionBuilder)(messagesApi, env)
  with Utilities {

  /**
    * Renders details info for the given request
    *
    * @param brandId Brand identifier
    * @param requestId Request identifier
    */
  def details(brandId: Long, requestId: Long) = AsyncSecuredBrandAction(brandId) { implicit request =>
    implicit handler => implicit user =>
      eventRequestService.find(requestId) flatMap {
        case None => jsonBadRequest("Event request not found")
        case Some(eventRequest) => ok(views.html.v2.eventRequest.details(eventRequest))
      }
  }

  /**
    * Returns list of event requests for the given brand
    *
    * @param brandId Brand identifier
    */
  def index(brandId: Long) = AsyncSecuredRestrictedAction(List(Role.Facilitator, Role.Coordinator)) {
    implicit request => implicit handler => implicit user =>
      eventRequestService.findByBrand(brandId) flatMap { requests =>
        roleDiffirentiator(user.account, Some(brandId)) { (view, brands) =>
          ok(views.html.v2.eventRequest.index(user, view.brand, brands, requests))
        } { (brand, brands) =>
          redirect(routes.Dashboard.index())
        } {
          redirect(routes.Dashboard.index())
        }
      }
  }
}
