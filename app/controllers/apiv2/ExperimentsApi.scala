package controllers.apiv2

import controllers.Utilities
import models.Experiment
import play.api.libs.json.{Json, JsValue, Writes}
import play.mvc.Controller

/**
 * Experiments API
 */
trait ExperimentsApi extends Controller with ApiAuthentication with Utilities {

  implicit val experimentWrites = new Writes[Experiment] {
    def writes(experiment: Experiment): JsValue = {
      Json.obj(
        "memberId" -> experiment.memberId,
        "name" -> experiment.name,
        "description" -> experiment.description,
        "url" -> experiment.url,
        "image" -> experimentImageUrl(experiment))
    }
  }

  /**
   * Returns list of experiments
   * @return
   */
  def experiments() = TokenSecuredAction(readWrite = false) {
    implicit request =>
      implicit token =>
        jsonOk(Json.toJson(experimentService.findAll()))
  }

  /**
   * Returns image url for the given experiment
   *
   * @param experiment Experiment
   */
  protected def experimentImageUrl(experiment: Experiment): Option[String] = {
    if (experiment.picture)
      Some(fullUrl(controllers.routes.Experiments.picture(experiment.id.get).url))
    else
      None
  }
}

object ExperimentsApi extends ExperimentsApi
