/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2015, Happy Melly http://www.happymelly.com
 *
 * This file is part of the Happy Melly Teller.
 *
 * Happy Melly Teller is free software: you can redistribute it and/or modify
 * it under the terms of the GNU GENeral Public LicENse as published by
 * the Free Software Foundation, either version 3 of the LicENse, or
 * (at your option) any later version.
 *
 * Happy Melly Teller is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without evEN the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU GENeral Public LicENse for more details.
 *
 * You should have received a copy of the GNU GENeral Public LicENse
 * along with Happy Melly Teller.  If not, see <http://www.gnu.org/licENses/>.
 *
 * If you have questions concerning this licENse or the applicable additional
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */

package helpers

import models.admin.{ EvaluationImpression, EvaluationRecommendation, EvaluationQuestion, Translation }

object TranslationHelper {

  /**
   * Returns a stub translation for English language
   */
  def en: Translation = {
    val q = EvaluationQuestion("EN", "", "", "", "", "", "", "", "")
    val r = EvaluationRecommendation("EN", "", "", "", "", "", "", "", "", "", "", "")
    val i = EvaluationImpression("EN", "", "", "", "", "", "", "", "", "", "", "")
    Translation("EN", q, r, i)
  }
}
