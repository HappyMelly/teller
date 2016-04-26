/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2016, Happy Melly http://www.happymelly.com
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

package libs.mailchimp

import org.specs2.matcher.BeEqualValueCheck
import org.specs2.mutable.Specification
import play.api.libs.json.{JsString, JsValue}
import play.api.libs.ws.WSResponse

class ErrorHandlingClient() extends Client("fake", "fake") {

  def callHandleResponse[T](response: WSResponse)(f: JsValue => T) = handleResponse(response)(f)

  def callJsonFormattingError(detail: String) = jsonFormattingError(detail)
}

/**
  * Tests MailChimp client error handling
  */
class ClientErrorHandlingSpec extends Specification {

  "MailChimp client" should {
    val client = new ErrorHandlingClient()

    "correctly handle 200 response" in {
      val response = new FakeResponse(200, JsString("").toString())
      val result = client.callHandleResponse(response) { json => "ok" }
      result must beRight(BeEqualValueCheck("ok"))
    }
    "correctly handle correctly formed non-200 response" in {
      val error = ApiError("http://kb.mailchimp.com/api/error-docs/405-method-not-allowed",
        "Method Not Allowed", 405, "some text", "")
      val response = new FakeResponse(405, Converter.apiErrorFormat.writes(error).toString())
      val result = client.callHandleResponse(response) { json => "ok" }
      result must beLeft(BeEqualValueCheck(error))
    }
    "correctly handle incorrectly formed non-200 response" in {
      val body = JsString("test").toString()
      val response = new FakeResponse(405, body)
      val result = client.callHandleResponse(response) { json => "ok" }
      result must beLeft(BeEqualValueCheck(client.callJsonFormattingError(body)))
    }
    "correctly handle incorrectly formed 200 response" in {
      val body = ""
      val response = new FakeResponse(200, body)
      val result = client.callHandleResponse(response) { json => "ok" }
      result must beLeft(BeEqualValueCheck(client.callJsonFormattingError(body)))
    }
  }
}
