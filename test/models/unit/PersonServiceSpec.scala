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
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com
 * or in writing Happy Melly One, Handelsplein 37, Rotterdam,
 * The Netherlands, 3071 PR
 */
package models.unit

import helpers._
import models.{ Person, ProfileStrength, CompletionStep }
import models.repository.{ PersonRepository, ProfileStrengthRepository }
import org.specs2.mutable._
import org.scalamock.specs2.{ IsolatedMockFactory, MockContext }
import play.api.libs.json._
import stubs._

class PersonServiceSpec extends Specification with IsolatedMockFactory {

  class TestPersonRepository extends PersonRepository with FakeRepositories {

    def callUpdateProfileStrength(person: Person): Unit =
      updateProfileStrength(person)
  }

  val service = new TestPersonRepository
  val profileStrengthService = mock[ProfileStrengthRepository]
  service.profileStrengthService_=(profileStrengthService)

  override def is = s2"""
  A signature step should be marked as Incomplete
    when a person has no signature                                          $e1
  """

  def e1 = {
    val person = PersonHelper.one().copy(bio = None)
    (services.profileStrengthService.find(_: Long, _: Boolean)) expects (1L, false) returning Some(strength(signature = true))
    (services.profileStrengthService.update _) expects strength(signature = false)
    service.callUpdateProfileStrength(person)
    ok
  }

  /**
   * Returns profile strength steps
   *
   * @param about Sets strength value for 'about' step
   * @param photo Sets strength value for 'photo' step
   * @param social Sets strength value for 'social' step
   * @param signature Sets strength value for 'signature' step
   */
  private def steps(about: Boolean = false,
    photo: Boolean = false,
    social: Boolean = false,
    signature: Boolean = false): JsArray = {
    Json.arr(
      Json.obj(
        "name" -> "about",
        "weight" -> 5,
        "done" -> about), Json.obj(
        "name" -> "photo",
        "weight" -> 10,
        "done" -> photo), Json.obj(
        "name" -> "signature",
        "weight" -> 5,
        "done" -> signature), Json.obj(
        "name" -> "social",
        "weight" -> 5,
        "done" -> social))
  }

  /**
   * Returns profile strength steps
   *
   * @param about Sets strength value for 'about' step
   * @param photo Sets strength value for 'photo' step
   * @param social Sets strength value for 'social' step
   * @param signature Sets strength value for 'signature' step
   */
  private def strength(about: Boolean = false,
    photo: Boolean = false,
    social: Boolean = false,
    signature: Boolean = false): ProfileStrength =
    ProfileStrength(None, 1L, false, steps(about, photo, social, signature))
}