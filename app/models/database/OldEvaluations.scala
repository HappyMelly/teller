package models.database

import models.OldEvaluation
import play.api.db.slick.Config.driver.simple._

/**
 * Connects Material object with its database representation
 */
private[models] class OldEvaluations(tag: Tag) extends Table[OldEvaluation](tag, "MGT30_OLD_EVALUATION") {
  def eventId = column[Long]("EVENT_ID")
  def notPublic = column[Boolean]("NOT_PUBLIC")
  def impression = column[Int]("IMPRESSION")
  def facilitatorId = column[Long]("FACILITATOR_ID")

  def * = (eventId, notPublic, impression, facilitatorId) <> (
    (OldEvaluation.apply _).tupled, OldEvaluation.unapply)
}
