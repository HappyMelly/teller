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

package controllers.acceptance

import controllers.Registration
import integration.PlayAppSpec
import play.api.mvc.SimpleResult
import play.api.test.FakeRequest

import scala.concurrent.Future

class RegistrationSpec extends PlayAppSpec {
  class TestRegistration() extends Registration

  def setupDb() {}
  def cleanupDb() {}

  override def is = s2"""

  Step 1 should
    be visible to anyone                           $e1
    contain all social login buttons               $e2
  """

  val controller = new TestRegistration()

  def e1 = {
    val result: Future[SimpleResult] = controller.step1().apply(FakeRequest())
    status(result) must equalTo(OK)
  }

  def e2 = {
    val result: Future[SimpleResult] = controller.step1().apply(FakeRequest())
    status(result) must equalTo(OK)
    contentAsString(result) must contain("Log in with Twitter")
    contentAsString(result) must contain("Log in with Facebook")
    contentAsString(result) must contain("Log in with Google")
    contentAsString(result) must contain("Log in with Linkedin")
  }

}
