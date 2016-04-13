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

package controllers.cm.facilitator

import javax.inject.Inject

import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import controllers.{Utilities, Security}
import libs.mailchimp.Client
import models.Brand
import models.UserRole.Role._
import models.cm.facilitator.MailChimpList
import models.repository.Repositories
import play.api.i18n.MessagesApi
import play.api.libs.json._
import play.api.data.Form
import play.api.data.Forms._
import securesocial.core.SecureSocial
import security.MailChimpProvider
import services.TellerRuntimeEnvironment

import scala.concurrent.Future

/**
  * Contains methods for managing MailChimp integrations for facilitators
  */
class Mailchimp @Inject() (override implicit val env: TellerRuntimeEnvironment,
                           override val messagesApi: MessagesApi,
                           val repos: Repositories,
                           deadbolt: DeadboltActions, handlers: HandlerCache, actionBuilder: ActionBuilders)
  extends Security(deadbolt, handlers, actionBuilder, repos)(messagesApi, env) {

  /**
    * Authenticates current user through MailChimp and links MailChimp to his account
    */
  def activate = RestrictedAction(Facilitator) { implicit request => implicit handler => implicit user =>
    val url = controllers.core.routes.People.details(user.person.identifier).url + "#mailchimp"
    val session = request.session -
      SecureSocial.OriginalUrlKey +
      (SecureSocial.OriginalUrlKey -> url)
    val route = env.routes.authenticationUrl(MailChimpProvider.MailChimp)
    redirect(route, session)
  }

  /**
    * Connects MailChimp list with a set of given brands
    */
  def connect = RestrictedAction(Facilitator) { implicit request => implicit handler => implicit user =>
    case class FormData(id: String, name: String, brands: List[Long])
    val form = Form(mapping(
      "list_id" -> nonEmptyText,
      "list_name" -> nonEmptyText,
      "brands" -> list(longNumber)
    )(FormData.apply)(FormData.unapply))

    form.bindFromRequest().fold(
      errors => jsonFormError(Utilities.errorsToJson(errors)),
      data => {
        repos.cm.facilitator.findByPerson(user.person.identifier) flatMap { records =>
          val validBrands = records.map(_.brandId).filter(x => data.brands.contains(x))
          val results = validBrands.map { brandId =>
            val list = MailChimpList(None, data.name, data.id, brandId, user.person.identifier)
            repos.cm.facilitatorSettings.insertList(list)
          }
          Future.sequence(results).flatMap { list =>
            jsonSuccess(s"MailChimp list was successfully connected to selected brand(s)")
          }
        }
      }
    )
  }

  /**
    * Breaks MailChimp connection for current user
    */
  def deactivate = RestrictedAction(Facilitator) { implicit request => implicit handler => implicit user =>
    if (user.account.isMailChimpActive) {
      val account = user.account.copy(mailchimp = None)
      repos.userAccount.update(account) flatMap { _ =>
        env.updateCurrentUser(user.copy(account = account))
        jsonSuccess("MailChimp integration was successfully deactivated")
      }
    } else {
      jsonBadRequest("You cannot deactivate MailChimp integration as it is not active")
    }
  }

  /**
    * Disconnects MailChimp list with a set of given brands
    */
  def disconnect = RestrictedAction(Facilitator) { implicit request => implicit handler => implicit user =>
    case class FormData(id: String, brands: List[Long])
    val form = Form(mapping(
      "list_id" -> nonEmptyText,
      "brands" -> list(longNumber)
    )(FormData.apply)(FormData.unapply))

    form.bindFromRequest().fold(
      errors => jsonFormError(Utilities.errorsToJson(errors)),
      data => {
        repos.cm.facilitatorSettings.lists(user.person.identifier) flatMap { lists =>
          val validLists = lists.filter(_.listId == data.id).filter(x => data.brands.contains(x.brandId))
          val result = validLists.map { list =>
            repos.cm.facilitatorSettings.deleteList(list.personId, list.id.get)
          }
          Future.sequence(result) flatMap { _ =>
            jsonSuccess("MailChimp list was successfully disconnected from selected brands")
          }
        }
      }
    )
  }

  /**
    * Returns list of MailChimp lists for current user
    */
  def lists = RestrictedAction(Facilitator) { implicit request => implicit handler => implicit user =>
    implicit val listWrites = new Writes[libs.mailchimp.List] {
      def writes(list: libs.mailchimp.List): JsValue = Json.obj(
        "id" -> list.id,
        "name" -> list.name
      )
    }

    user.account.mailchimp match {
      case None => jsonBadRequest("MailChimp account is not connected")
      case Some(remoteUserId) =>
        repos.identity.findByUserId(remoteUserId, MailChimpProvider.MailChimp) flatMap { mayBeIdentity =>
          val data = for {
            identity <- mayBeIdentity
            info <- MailChimpProvider.toExtraInfo(identity.profile.extraInfo)
          } yield (identity, info)
          data match {
            case None => jsonInternalError("Internal error. Please contact the support")
            case Some((identity, info)) =>
              val mc = new Client(info.apiEndPoint, identity.profile.oAuth2Info.get.accessToken)
              mc.lists().flatMap { lists =>
                jsonOk(Json.obj("lists" -> lists.sortBy(_.name)))
              }
          }
        }
    }
  }

  /**
    * Renders settings screen for current user
    */
  def settings(id: Long) = RestrictedAction(Facilitator) { implicit request => implicit handler => implicit user =>
    val personId = user.account.personId
    val query = for {
      f <- repos.cm.facilitator.findByPerson(personId)
      l <- repos.cm.facilitatorSettings.lists(personId)
      b <- repos.cm.brand.find(f.map(_.brandId))
    } yield (f, l, b.map(_.brand))
    query flatMap { case (facilitators, lists, brands) =>
      val oneBrandFacilitator = facilitators.length == 1
      val data = lists.groupBy(_.listId).map { case (listId, sublists) =>
          val currentList = sublists.head
          val relatedBrands = brands.filter(x => sublists.exists(_.brandId == x.identifier))
          if (oneBrandFacilitator)
            getOneBrandBlockMessages(currentList)
          else
            getMultipleBrandsBlockMessages(currentList, relatedBrands, facilitators.length)
      }
      ok(views.html.v2.person.tabs.mailchimp(user.account.isMailChimpActive, data.toList))
    }
  }

  /**
    * Returns list name and supportive message about connection type
    * @param list List of interest
    */
  protected def getOneBrandBlockMessages(list: MailChimpList): (String, String) =
    (list.listName, importType(list))

  /**
    * Returns list name and supportive message about connection typ
    * @param list List of interest
    * @param brands Related brands
    * @param numberOfBrands Total number of brands this facilitator works with
    */
  protected def getMultipleBrandsBlockMessages(list: MailChimpList,
                                               brands: Seq[Brand],
                                               numberOfBrands: Int): (String, String) = {
    val brandCaption = if (numberOfBrands == brands.length)
      "all"
    else
      brands.map(_.name).mkString(" and ")
    (list.listName, s"${importType(list)} from $brandCaption brands")
  }

  protected def importType(list: MailChimpList): String = if (list.allAttendees)
    "all attendees"
  else
    "only attendees with evaluations"
}
