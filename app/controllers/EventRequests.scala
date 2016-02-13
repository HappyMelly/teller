package controllers

import javax.inject.Inject

import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import models.UserRole.Role
import models.repository.Repositories
import play.api.i18n.MessagesApi
import play.api.mvc.Action
import services.TellerRuntimeEnvironment

/**
  * Contains methods for managing event requests UI
  */
class EventRequests @Inject() (override implicit val env: TellerRuntimeEnvironment,
                               override val messagesApi: MessagesApi,
                               val services: Repositories,
                               deadbolt: DeadboltActions, handlers: HandlerCache, actionBuilder: ActionBuilders)
  extends Security(deadbolt, handlers, actionBuilder, services)(messagesApi, env)
  with BrandAware {

  /**
    * Renders details info for the given request
    *
    * @param brandId Brand identifier
    * @param requestId Request identifier
    */
  def details(brandId: Long, requestId: Long) = AsyncSecuredBrandAction(brandId) { implicit request =>
    implicit handler => implicit user =>
      services.eventRequest.find(requestId) flatMap {
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
      services.eventRequest.findByBrand(brandId) flatMap { requests =>
        roleDiffirentiator(user.account, Some(brandId)) { (view, brands) =>
          ok(views.html.v2.eventRequest.index(user, view.brand, brands, requests))
        } { (brand, brands) =>
          redirect(routes.Dashboard.index())
        } {
          redirect(routes.Dashboard.index())
        }
      }
  }

  /**
    * Unsubscribes from automatic upcoming event notifications
 *
    * @param hashedId Hashed unique id
    */
  def unsubscribe(hashedId: String) = Action.async { implicit request ⇒
    services.eventRequest.find(hashedId) flatMap {
      case None => notFound(views.html.v2.eventRequest.notfound())
      case Some(eventRequest) =>
        services.eventRequest.update(eventRequest.copy(unsubscribed = true)) flatMap { _ =>
          ok(views.html.v2.eventRequest.unsubscribed())
        }
    }
  }


}
