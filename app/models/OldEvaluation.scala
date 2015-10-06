package models

import models.database.OldEvaluations
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

/**
 * This class represents an old evaluation from the previous Management 3.0
 *  backend system. It is used for calculating rating of Mgt30 facilitators only
 */
case class OldEvaluation(notPublic: Boolean,
  impression: Int,
  facilitatorId: Long)


object OldEvaluation {
  private val evaluations = TableQuery[OldEvaluations]

  /**
   * Returns evaluation for the given facilitator
   * @param id Facilitator id
   * @return
   */
  def findByFacilitator(id: Long): List[OldEvaluation] = DB.withSession {
    implicit session ⇒
      evaluations.filter(_.facilitatorId === id).list
  }
}