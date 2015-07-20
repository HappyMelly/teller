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

package services.integrations

import models.Person
import play.api.Play.current
import play.api.libs.json.Json
import play.api.libs.ws.WS
import scala.util.Try

/**
 * MailChimp integration
 */
class MailChimp(apiUrl: String, apiToken: String) {

  /**
   * Subscribes the given person to the given list and assigns the person
   *  to "Funders" or "Supporters" group
   * @param listId Mailchimp list id
   * @param person Person
   * @param funder if true, the person is added to "Funder" group; otherwise, to "Supporters"
   * @return Returns true if the person was successfully subscribed
   */
  def subscribe(listId: String, person: Person, funder: Boolean): Boolean = {
    val group = if (funder) "Funder" else "Supporter"
    val url = apiUrl + "lists/subscribe.json"
    val request = Json.obj("apikey" -> apiToken,
      "id" -> listId,
      "email" -> Json.obj("email" -> person.socialProfile.email),
      "merge_vars" -> Json.obj("FNAME" -> person.firstName,
        "LNAME" -> person.lastName,
        "groupings" -> Json.arr(
          Json.obj("name" -> "Membership type", "groups" -> Json.arr(group)))),
      "double_optin" -> false,
      "update_existing" -> true,
      "replace_interests" -> false).toString()
    Try(WS.url(url).post(request)).isSuccess
  }
}
