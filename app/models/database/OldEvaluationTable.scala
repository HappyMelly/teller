package models.database

import models.OldEvaluation
import slick.driver.JdbcProfile

private[models] trait OldEvaluationTable {

  protected val driver: JdbcProfile
  import driver.api._

  /**
    * Connects Material object with its database representation
    */
  class OldEvaluations(tag: Tag) extends Table[OldEvaluation](tag, "MGT30_OLD_EVALUATION") {

    def notPublic = column[Boolean]("NOT_PUBLIC")
    def impression = column[Int]("IMPRESSION")
    def facilitatorId = column[Long]("FACILITATOR_ID")

    def * = (notPublic, impression, facilitatorId) <>(
      (OldEvaluation.apply _).tupled, OldEvaluation.unapply)
  }

}