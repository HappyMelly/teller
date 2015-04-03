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

package services

import helpers.PersonHelper
import org.specs2.mutable.Specification
import play.api.libs.json._
import play.api.libs.ws._
import services.integrations.MailChimp

import scala.concurrent.duration._

class MailChimpSpec extends Specification {

  "Method 'subscribe'" should {
    "add a new subscriber to 'Funder' group" in {
      val apiUrl = System.getenv("MAILCHIMP_URL")
      val apiToken = System.getenv("MAILCHIMP_TOKEN")
      val listId = System.getenv("MAILCHIMP_LIST_ID")
      val mailChimp = new MailChimp(apiUrl, apiToken)
      mailChimp.subscribe(listId, PersonHelper.one(), funder = true) must_== true

      val listUrl = apiUrl + "lists/members.json"
      val data = Json.obj("apikey" -> apiToken, "id" -> listId).toString()
      implicit val concurrentExecutionContext = play.api.libs.concurrent.Execution.Implicits.defaultContext
      val res = WS.url(listUrl).post(data).map { x ⇒
        (x.json \ "total").as[Int]
      }
      res must be_==(1).await(timeout = FiniteDuration(5, SECONDS))
    }
  }
}
