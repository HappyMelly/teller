package controllers.apiv2

import javax.inject.Inject

import controllers.{Experiments, Utilities}
import models.Experiment
import play.api.i18n.MessagesApi
import play.api.libs.json._

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Experiments API
 */
class ExperimentsApi @Inject() (val messagesApi: MessagesApi) extends ApiAuthentication with Utilities {

  implicit val experimentWrites = new Writes[Experiment] {
    def writes(experiment: Experiment): JsValue = {
      Json.obj(
        "memberId" -> experiment.memberId,
        "name" -> experiment.name,
        "description" -> experiment.description,
        "url" -> experiment.url,
        "image" -> Experiments.pictureUrl(experiment))
    }
  }

  /**
   * Returns list of experiments
   */
  def experiments() = TokenSecuredAction(readWrite = false) { implicit request =>  implicit token =>
    (for {
      experiments <- experimentService.findAll()
      members <- memberService.find(experiments.map(_.memberId).distinct)
      people <- personService.find(members.filter(_.person).map(_.objectId))
      orgs <- orgService.find(members.filterNot(_.person).map(_.objectId))
    } yield (experiments, members, people, orgs)) flatMap { case (experiments, members, people, orgs) =>
      val filteredExperiments = experiments.sortBy(_.recordInfo.updated.toString).reverse
      jsonOk(JsArray(Json.toJson(filteredExperiments).as[List[JsObject]].map { experiment =>
        val memberId = (experiment \ "memberId").as[Long]
        val member = members.find(_.identifier == memberId).get
        val memberName = if (member.person) {
          people.find(_.identifier == member.objectId).get.fullName
        } else {
          orgs.find(_.identifier == member.objectId).get.name
        }
        experiment.as[JsObject] ++ Json.obj("memberName" -> memberName)
      }))
    }
  }
}
