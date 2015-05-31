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
import models.{ Person, ProfileCompletion, CompletionStep, Photo }
import models.service.{ PersonService, ProfileCompletionService }
import org.specs2.mutable._
import org.scalamock.specs2.{ IsolatedMockFactory, MockContext }
import play.api.libs.json._
import stubs._

class PersonServiceSpec extends Specification with IsolatedMockFactory {

  class TestPersonService extends PersonService with FakeServices {

    def callUpdateProfileCompletion(person: Person): Unit =
      updateProfileCompletion(person)
  }

  val service = new TestPersonService
  val profileCompletionService = mock[ProfileCompletionService]
  service.profileCompletionService_=(profileCompletionService)

  override def is = s2"""

  When a person has a valid photo a photo step should be marked as Complete
    if a photo is Facebook photo is added                                   $e1
    if a photo is Gravatar photo is added                                   $e2

  When a person has no valid photo
    a photo step should be marked as Incomplete                             $e3

  When a person has no bio
    an about step should be marked as Incomplete                            $e4

  When a person has a bio
    an about step should be marked as Complete                              $e5

  A social step should be marked as Incomplete
    when a person has no social network                                     $e6
    when a person has 1 social network                                      $e7

  A social step should be marked as Complete
    when a person has at least 2 social networks                            $e8

  A signature step should be marked as Incomplete
    when a person has no signature                                          $e9

  A signature step should be marked as Complete
    when a person has a signature                                          $e10
  """

  def e1 = {
    val facebookPhoto = Photo(Some("facebook"),
      Some("http://graph.facebook.com/skotlov/picture?type=large"))
    val person = PersonHelper.one().copy(photo = facebookPhoto, bio = None)
    (profileCompletionService.find _) expects (1L, false) returning Some(completion())
    (profileCompletionService.update _) expects completion(photo = true)
    service.callUpdateProfileCompletion(person)
    ok
  }

  def e2 = {
    val gravatarPhoto = Photo(Some("gravatar"),
      Some("https://secure.gravatar.com/avatar/b642b4217b34b1e8d3bd915fc65c4452?s=300"))
    val person = PersonHelper.one().copy(photo = gravatarPhoto, bio = None)
    (profileCompletionService.find _) expects (1L, false) returning Some(completion())
    (profileCompletionService.update _) expects completion(photo = true)
    service.callUpdateProfileCompletion(person)
    ok
  }

  def e3 = {
    val person = PersonHelper.one().copy(bio = None)
    (profileCompletionService.find _) expects (1L, false) returning Some(completion(photo = true))
    (profileCompletionService.update _) expects completion(photo = false)
    service.callUpdateProfileCompletion(person)
    ok
  }

  def e4 = {
    val person = PersonHelper.one().copy(bio = None)
    (profileCompletionService.find _) expects (1L, false) returning Some(completion(about = true))
    (profileCompletionService.update _) expects completion()
    service.callUpdateProfileCompletion(person)
    ok
  }

  def e5 = {
    val person = PersonHelper.one().copy(bio = Some("test"))
    (profileCompletionService.find _) expects (1L, false) returning Some(completion())
    (profileCompletionService.update _) expects completion(about = true)
    service.callUpdateProfileCompletion(person)
    ok
  }

  def e6 = {
    val person = PersonHelper.one().copy(bio = None)
    (profileCompletionService.find _) expects (1L, false) returning Some(completion(social = true))
    (profileCompletionService.update _) expects completion()
    service.callUpdateProfileCompletion(person)
    ok
  }

  def e7 = {
    val person = PersonHelper.one().copy(bio = None)
    person.socialProfile_=(person.socialProfile.copy(twitterHandle = Some("test")))
    (profileCompletionService.find _) expects (1L, false) returning Some(completion(social = true))
    (profileCompletionService.update _) expects completion()
    service.callUpdateProfileCompletion(person)
    ok
  }

  def e8 = {
    val person = PersonHelper.one().copy(bio = None)
    person.socialProfile_=(person.socialProfile.copy(twitterHandle = Some("test"),
      facebookUrl = Some("test")))
    (profileCompletionService.find _) expects (1L, false) returning Some(completion())
    (profileCompletionService.update _) expects completion(social = true)
    service.callUpdateProfileCompletion(person)
    ok
  }

  def e9 = {
    val person = PersonHelper.one().copy(bio = None)
    (profileCompletionService.find _) expects (1L, false) returning Some(completion(signature = true))
    (profileCompletionService.update _) expects completion(signature = false)
    service.callUpdateProfileCompletion(person)
    ok
  }

  def e10 = {
    val person = PersonHelper.one().copy(bio = None, signature = true)
    (profileCompletionService.find _) expects (1L, false) returning Some(completion(signature = false))
    (profileCompletionService.update _) expects completion(signature = true)
    service.callUpdateProfileCompletion(person)
    ok
  }

  /**
   * Returns profile completion steps
   *
   * @param about Sets completion value for 'about' step
   * @param photo Sets completion value for 'photo' step
   * @param social Sets completion value for 'social' step
   * @param signature Sets completion value for 'signature' step
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
   * Returns profile completion steps
   *
   * @param about Sets completion value for 'about' step
   * @param photo Sets completion value for 'photo' step
   * @param social Sets completion value for 'social' step
   * @param signature Sets completion value for 'signature' step
   */
  private def completion(about: Boolean = false,
    photo: Boolean = false,
    social: Boolean = false,
    signature: Boolean = false): ProfileCompletion =
    ProfileCompletion(None, 1L, false, steps(about, photo, social, signature))
}