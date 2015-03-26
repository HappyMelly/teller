/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2015, Happy Melly http://www.happymelly.com
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
 * If you have questions concerning this license or the applicable additional
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */

package models.service

import models.admin.Translation
import models.database.admin.{ EvaluationImpressions, EvaluationRecommendations, EvaluationQuestions }
import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
import play.api.Play.current

class TranslationService {

  /**
   * Find a translation object related to a particular language
   *
   * @param language An unique two-letters language identifier
   * @return
   */
  def find(language: String): Option[Translation] = DB.withSession {
    implicit session ⇒
      val query = for {
        q ← EvaluationQuestions if q.language === language
        r ← EvaluationRecommendations if r.language === language
        i ← EvaluationImpressions if i.language === language
      } yield (q, r, i)
      query.firstOption.map { v ⇒
        Translation(language, v._1, v._2, v._3)
      }
  }
}

object TranslationService {
  private val instance = new TranslationService
  def get: TranslationService = instance
}