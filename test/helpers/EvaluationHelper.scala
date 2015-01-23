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
package helpers

import models.{ EvaluationStatus, Evaluation }
import org.joda.time.DateTime

object EvaluationHelper {

  def make(id: Option[Long] = None,
    eventId: Long,
    personId: Long,
    status: EvaluationStatus.Value,
    impression: Int,
    created: DateTime): Evaluation = {
    new Evaluation(id, eventId, personId,
      "question1_" + id.toString,
      "question2_" + id.toString,
      "question3_" + id.toString,
      "question4_" + id.toString,
      "question5_" + id.toString,
      impression,
      impression,
      "question8_" + id.toString,
      status,
      handled = None,
      certificate = None,
      created = created,
      createdBy = "Sergey Kotlov",
      updated = DateTime.now(),
      updatedBy = "Tom Cat")
  }

}
