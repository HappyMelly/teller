package controllers.apiv2

import controllers.Utilities
import models.Experiment
import play.api.libs.json._
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
        val experiments = experimentService.findAll().sortBy(_.recordInfo.updated.toString).reverse
        val members = memberService.find(experiments.map(_.memberId).distinct)
        val people = personService.find(members.filter(_.person).map(_.objectId))
        val orgs = orgService.find(members.filterNot(_.person).map(_.objectId))
        jsonOk(JsArray(Json.toJson(experiments).as[List[JsObject]].map { experiment =>
          val memberId = (experiment \ "memberId").as[Long]
          val member = members.find(_.identifier == memberId).get
          val memberName = if (member.person) {
            people.find(_._1.identifier == member.objectId).get._1.fullName
          } else {
            orgs.find(_.identifier == member.objectId).get.name
          }
          experiment.as[JsObject] ++ Json.obj("memberName" -> memberName)
        }))
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
