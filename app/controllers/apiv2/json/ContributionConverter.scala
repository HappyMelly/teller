package controllers.apiv2.json

import javax.inject.Singleton

import models.ContributionView
import play.api.i18n.Messages
import play.api.libs.json.{Json, Writes}

/**
  * Converts a contribution to JSON
  */
class ContributionConverter(implicit val messages: Messages) {

  implicit val productWrites = (new ProductConverter).productWrites

  val contributionWrites = new Writes[ContributionView] {
    def writes(contribution: ContributionView) = Json.obj(
      "product" -> contribution.product,
      "role" -> contribution.contribution.role)
  }

}
