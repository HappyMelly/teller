package controllers.api.json

import models.ContributorView
import play.api.i18n.Messages
import play.api.libs.json.{Json, Writes}

/**
  * Created by sery0ga on 03/02/16.
  */
class ContributorConverter(implicit val messages: Messages) {

  val contributorWrites = new Writes[ContributorView] {
    def writes(contributor: ContributorView) = Json.obj(
      "id" -> contributor.id,
      "name" -> contributor.name,
      "unique_name" -> contributor.uniqueName,
      "photo" -> contributor.photo,
      "role" -> contributor.contribution.role)
  }
}
