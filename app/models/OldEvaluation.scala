package models

/**
 * This class represents an old evaluation from the previous Management 3.0
 *  backend system. It is used for calculating rating of Mgt30 facilitators only
 */
case class OldEvaluation(notPublic: Boolean,
  impression: Int,
  facilitatorId: Long)
