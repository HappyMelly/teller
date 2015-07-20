/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2014, Happy Melly http://www.happymelly.com
 *
 * This file is part of the Happy Melly Teller.
 *
 * Happy Melly Teller is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Happy Melly Teller is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Happy Melly Teller.  If not, see <http://www.gnu.org/licenses/>.
 *
 * If you have scores concerning this license or the applicable additional terms, you may contact
 * by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */

package models.database.admin

import models.admin.EvaluationRecommendation
import play.api.db.slick.Config.driver.simple._

/**
 * `EvaluationQuestions` database table mapping.
 */
private[models] class EvaluationRecommendations(tag: Tag)
    extends Table[EvaluationRecommendation](tag, "EVALUATION_RECOMMENDATION") {

  def language = column[String]("LANGUAGE", O.PrimaryKey)
  def score0 = column[String]("SCORE_0")
  def score1 = column[String]("SCORE_1")
  def score2 = column[String]("SCORE_2")
  def score3 = column[String]("SCORE_3")
  def score4 = column[String]("SCORE_4")
  def score5 = column[String]("SCORE_5")
  def score6 = column[String]("SCORE_6")
  def score7 = column[String]("SCORE_7")
  def score8 = column[String]("SCORE_8")
  def score9 = column[String]("SCORE_9")
  def score10 = column[String]("SCORE_10")

  def * = (language, score0, score1, score2, score3, score4, score5,
    score6, score7, score8, score9, score10) <> (EvaluationRecommendation.tupled, EvaluationRecommendation.unapply)

  def forUpdate = (score0, score1, score2, score3, score4, score5,
    score6, score7, score8, score9, score10)

}
