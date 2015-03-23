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
 * If you have questions concerning this license or the applicable additional terms, you may contact
 * by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */

package models.admin

import models.database.admin._
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

case class EvaluationQuestion(language: String,
  question1: String,
  question2: String,
  question3: String,
  question4: String,
  question5: String,
  question6: String,
  question7: String,
  question8: String)

case class EvaluationRecommendation(language: String,
  score0: String,
  score1: String,
  score2: String,
  score3: String,
  score4: String,
  score5: String,
  score6: String,
  score7: String,
  score8: String,
  score9: String,
  score10: String) {

  def value(index: Long): String = index match {
    case 0 ⇒ this.score0 + " (0%)"
    case 1 ⇒ this.score1 + " (10%)"
    case 2 ⇒ this.score2 + " (20%)"
    case 3 ⇒ this.score3 + " (30%)"
    case 4 ⇒ this.score4 + " (40%)"
    case 5 ⇒ this.score5 + " (50%)"
    case 6 ⇒ this.score6 + " (60%)"
    case 7 ⇒ this.score7 + " (70%)"
    case 8 ⇒ this.score8 + " (80%)"
    case 9 ⇒ this.score9 + " (90%)"
    case 10 ⇒ this.score10 + " (100%)"
    case _ ⇒ ""
  }
}

case class EvaluationImpression(language: String,
  score0: String,
  score1: String,
  score2: String,
  score3: String,
  score4: String,
  score5: String,
  score6: String,
  score7: String,
  score8: String,
  score9: String,
  score10: String) {

  def value(index: Long): String = index match {
    case 0 ⇒ this.score0 + " (0)"
    case 1 ⇒ this.score1 + " (1)"
    case 2 ⇒ this.score2 + " (2)"
    case 3 ⇒ this.score3 + " (3)"
    case 4 ⇒ this.score4 + " (4)"
    case 5 ⇒ this.score5 + " (5)"
    case 6 ⇒ this.score6 + " (6)"
    case 7 ⇒ this.score7 + " (7)"
    case 8 ⇒ this.score8 + " (8)"
    case 9 ⇒ this.score9 + " (9)"
    case 10 ⇒ this.score10 + " (10)"
    case _ ⇒ ""
  }

}

case class Translation(language: String,
  questions: EvaluationQuestion,
  recommendations: EvaluationRecommendation,
  impressions: EvaluationImpression) {

  lazy val changeable: Boolean = language != "EN"

  def create: Translation = DB.withSession { implicit session: Session ⇒
    EvaluationQuestions.insert(this.questions)
    EvaluationRecommendations.insert(this.recommendations)
    EvaluationImpressions.insert(this.impressions)

    this
  }

  def update: Translation = DB.withSession { implicit session: Session ⇒
    val q = this.questions
    val qTuple = (q.question1, q.question2, q.question3, q.question4, q.question5, q.question6, q.question7, q.question8)
    val qQuery = EvaluationQuestions.filter(_.language === this.language).map(_.forUpdate)
    qQuery.update(qTuple)

    val r = this.recommendations
    val rTuple = (r.score0, r.score1, r.score2, r.score3, r.score4, r.score5, r.score6, r.score7, r.score8, r.score9, r.score10)
    val rQuery = EvaluationRecommendations.filter(_.language === this.language).map(_.forUpdate)
    rQuery.update(rTuple)

    val i = this.impressions
    val iTuple = (i.score0, i.score1, i.score2, i.score3, i.score4, i.score5, i.score6, i.score7, i.score8, i.score9, i.score10)
    val iQuery = EvaluationRecommendations.filter(_.language === this.language).map(_.forUpdate)
    iQuery.update(iTuple)

    this
  }

  def delete(): Unit = DB.withSession { implicit session: Session ⇒
    EvaluationQuestions.where(_.language === this.language).mutate(_.delete())
    EvaluationRecommendations.where(_.language === this.language).mutate(_.delete())
    EvaluationImpressions.where(_.language === this.language).mutate(_.delete())
  }
}

object Translation {

  /**
   * Find a translation object related to a particular language
   *
   * @param language An unique two-letters language identifier
   * @return
   */
  def find(language: String): Option[Translation] = DB.withSession { implicit session: Session ⇒
    val query = for {
      question ← EvaluationQuestions if question.language === language
      recommendation ← EvaluationRecommendations if recommendation.language === language
      impression ← EvaluationImpressions if impression.language === language
    } yield (question, recommendation, impression)
    query.firstOption.map { v ⇒
      Translation(language, v._1, v._2, v._3)
    }
  }

  /**
   * Find all translation objects
   *
   * @return
   */
  def findAll: Map[String, Translation] = DB.withSession { implicit session: Session ⇒
    val query = for {
      question ← EvaluationQuestions
      recommendation ← EvaluationRecommendations if recommendation.language === question.language
      impression ← EvaluationImpressions if impression.language === question.language
    } yield (question, recommendation, impression)
    query.list.map { v ⇒ (v._1.language, Translation(v._1.language, v._1, v._2, v._3)) }.toMap
  }
}