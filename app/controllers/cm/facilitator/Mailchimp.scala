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

import javax.inject.{Inject, Named}

import akka.actor.ActorRef
import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import controllers.Forms._
import controllers.cm.facilitator.MailChimp.NewListData
import controllers.{Security, Utilities}
import libs.mailchimp.{CampaignDefaults, Client, ListContact}
import models.UserRole.Role._
import models.cm.facilitator.{MailChimpListBlock, MailChimpList}
import models.repository.Repositories
import models.{ActiveUser, Brand}
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.MessagesApi
import play.api.libs.json._
import play.api.mvc.{AnyContent, Request, Result}
import securesocial.core.SecureSocial
import security.MailChimpProvider
import services.TellerRuntimeEnvironment

import scala.concurrent.Future

/**
  * Contains methods for managing MailChimp integrations for facilitators
  */
class MailChimp @Inject()(override implicit val env: TellerRuntimeEnvironment,
                          override val messagesApi: MessagesApi,
                          val repos: Repositories,
                          @Named("mailchimp-subscriber") subscriber: ActorRef,
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

  def add = withMailChimpIntegration { mailChimpId => implicit request => implicit handler => implicit user =>
    (for {
      r <- repos.cm.facilitator.findByPerson(user.person.identifier)
      o <- repos.person.memberships(user.person.identifier)
      b <- repos.cm.brand.find(r.map(_.brandId))
    } yield (r, o, b.map(_.brand))) flatMap { case (records, organisations, brands) =>
      val defaults = CampaignDefaults(user.person.fullName, user.person.email, "", "")
      val contact = if (organisations.nonEmpty) {
        val org = organisations.head
        ListContact(org.name, org.street1.getOrElse(""), org.street2, org.postCode.getOrElse(""),
          org.city.getOrElse(""), org.province.getOrElse(""), org.countryCode)
      } else {
        ListContact("", "", None, "", "", "", "")
      }
      val formData = NewListData("", defaults, "", contact, allAttendees = true, includePreviousEvents = true, List())
      ok(views.html.v2.mailchimp.form(user, MailChimp.newListForm.fill(formData), brands))
    }
  }

  /**
    * Connects MailChimp list with a set of given brands
    */
  def connect = withMailChimpIntegration { mailChimpId => implicit request => implicit handler => implicit user =>
    MailChimp.connectForm.bindFromRequest().fold(
      errors => jsonFormError(Utilities.errorsToJson(errors)),
      data => {
        withMailChimpClient(mailChimpId) { client =>
          (for {
            f <- repos.cm.facilitator.findByPerson(user.person.identifier)
            b <- repos.cm.brand.find(f.map(_.brandId))
          } yield (f, b.map(_.brand))) flatMap { case (records, brands) =>
            client.mergeFields(data.id).map(models.core.integration.MailChimp.validateMergeFields) flatMap {
              case Left(msg) => jsonBadRequest(msg)
              case Right(msg) =>
                val validBrands = records.map(_.brandId).filter(x => data.brands.contains(x))
                val results = validBrands.map { brandId =>
                  val list = MailChimpList(None, data.name, data.id, brandId, user.person.identifier, data.allAttendees)
                  repos.cm.facilitatorSettings.insertList(list)
                }
                Future.sequence(results).flatMap { lists =>
                  val relatedBrands = brands.filter(b => lists.exists(_.brandId == b.identifier))
                  val block = MailChimpListBlock(lists.head, relatedBrands, records.length)
                  val body = views.html.v2.person.blocks.mailchimp.list(block, brands)
                  jsonSuccess(s"MailChimp list was successfully connected to selected brand(s)", body = Some(body))
                }
            }
          }
        }
      }
    )
  }

  def create = withMailChimpIntegration { mailChimpId => implicit request => implicit handler => implicit user =>
    MailChimp.newListForm.bindFromRequest().fold(
      errors => jsonFormError(Utilities.errorsToJson(errors)),
      data => {
        withMailChimpClient(mailChimpId) { client =>
          val list = libs.mailchimp.List(None, data.name, data.contact, data.reminder,
            campaignDefaults = data.defaults, emailTypeOption = false)
          (for {
            l <- client.createList(list)
            r <- repos.cm.facilitator.findByPerson(user.person.identifier)
          } yield (l, r)) flatMap { case (remoteList, records) =>
            val validBrands = records.map(_.brandId).filter(x => data.brands.contains(x))
            val results = validBrands.map { brandId =>
              val list = MailChimpList(None, remoteList.name, remoteList.id.get, brandId,
                user.person.identifier, data.allAttendees)
              repos.cm.facilitatorSettings.insertList(list)
            }
            Future.sequence(results).flatMap { _ =>
              subscriber ! (user.person.identifier, remoteList.id.get)
              jsonSuccess(s"MailChimp list was successfully created and connected to selected brand(s)")
            }
          }
        }
      }
    )
  }

  /**
    * Breaks MailChimp connection for current user
    */
  def deactivate = withMailChimpIntegration { mailChimpId => implicit request => implicit handler => implicit user =>
    val account = user.account.copy(mailchimp = None)
    repos.userAccount.update(account) flatMap { _ =>
      env.updateCurrentUser(user.copy(account = account))
      jsonSuccess("MailChimp integration was successfully deactivated")
    }
  }

  /**
    * Disconnects MailChimp list with a set of given brands
    */
  def disconnect = RestrictedAction(Facilitator) { implicit request => implicit handler => implicit user =>
    val form = Form(single("list_id" -> nonEmptyText))

    form.bindFromRequest().fold(
      errors => jsonFormError(Utilities.errorsToJson(errors)),
      listId => {
        repos.cm.facilitatorSettings.lists(user.person.identifier) flatMap { lists =>
          val validLists = lists.filter(_.listId == listId)
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
      * Updates connected MailChimp list with a set of given brands
      */
    def update = withMailChimpIntegration { mailChimpId => implicit request => implicit handler => implicit user =>
      MailChimp.connectForm.bindFromRequest().fold(
        errors => jsonFormError(Utilities.errorsToJson(errors)),
        data => {
          (for {
            f <- repos.cm.facilitator.findByPerson(user.person.identifier)
            l <- repos.cm.facilitatorSettings.lists(user.person.identifier)
            b <- repos.cm.brand.find(f.map(_.brandId))
          } yield (f, l, b.map(_.brand))) flatMap { case (records, lists, brands) =>
            val removed = lists.filter(_.listId == data.id).map { list =>
              repos.cm.facilitatorSettings.deleteList(list.personId, list.id.get)
            }
            Future.sequence(removed).flatMap { _ =>
              val validBrands = records.map(_.brandId).filter(x => data.brands.contains(x))
              val results = validBrands.map { brandId =>
                val list = MailChimpList(None, data.name, data.id, brandId, user.person.identifier, data.allAttendees)
                repos.cm.facilitatorSettings.insertList(list)
              }
              Future.sequence(results).flatMap { lists =>
                val relatedBrands = brands.filter(b => lists.exists(_.brandId == b.identifier))
                val block = MailChimpListBlock(lists.head, relatedBrands, records.length)
                val body = views.html.v2.person.blocks.mailchimp.list(block, brands)
                jsonSuccess(s"MailChimp list settings were successfully updated", body = Some(body))
              }
            }
          }
      }
    )
  }

  /**
    * Returns list of MailChimp lists for current user
    */
  def lists = withMailChimpIntegration { mailChimpId => implicit request => implicit handler => implicit user =>
    implicit val listWrites = new Writes[libs.mailchimp.List] {
      def writes(list: libs.mailchimp.List): JsValue = Json.obj(
        "id" -> list.id,
        "name" -> list.name
      )
    }
    withMailChimpClient(mailChimpId) { client =>
      client.lists().flatMap { lists =>
        jsonOk(Json.obj("lists" -> lists.sortBy(_.name)))
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
      val blocks = lists.groupBy(_.listId).map { case (listId, sublists) =>
          val relatedBrands = brands.filter(x => sublists.exists(_.brandId == x.identifier))
          MailChimpListBlock(sublists.head, relatedBrands, facilitators.length)
      }
      ok(views.html.v2.person.tabs.mailchimp(user.account.isMailChimpActive, blocks.toSeq, brands))
    }
  }

  protected def withMailChimpClient(mailChimpId: String)
                                   (f: libs.mailchimp.Client => Future[Result])(
    implicit request: Request[AnyContent], user: ActiveUser) = {
    repos.identity.findByUserId(mailChimpId, MailChimpProvider.MailChimp) flatMap { mayBeIdentity =>
      val data = for {
        identity <- mayBeIdentity
        info <- MailChimpProvider.toExtraInfo(identity.profile.extraInfo)
      } yield (identity, info)
      data match {
        case None => jsonInternalError("Internal error. Please contact the support")
        case Some((identity, info)) =>
          val mc = new Client(info.apiEndPoint, identity.profile.oAuth2Info.get.accessToken)
          f(mc)
      }
    }
  }

  protected def withMailChimpIntegration(f: String => Request[AnyContent] =>
    be.objectify.deadbolt.scala.DeadboltHandler => ActiveUser => Future[Result]) =

    RestrictedAction(Facilitator) { implicit request => implicit handler => implicit user =>
      user.account.mailchimp match {
        case None => jsonBadRequest("MailChimp integration is not active")
        case Some(mailChimpId) => f(mailChimpId)(request)(handler)(user)
      }
  }

}

object MailChimp {

  case class ConnectFormData(id: String,
                             name: String,
                             brandIds: List[Long],
                             activeBrandIds: List[Boolean],
                             allAttendees: Boolean,
                             includePreviousEvents: Boolean) {

    def brands: List[Long] = brandIds.zip(activeBrandIds).filter(_._2).map(_._1)
  }


  val connectForm = Form(mapping(
    "list_id" -> nonEmptyText,
    "list_name" -> nonEmptyText,
    "brands" -> list(longNumber),
    "brand_flags" -> list(boolean).verifying("Select at least one brand", l => l.contains(true)),
    "all_attendees" -> boolean,
    "include_previous_events" -> boolean
  )(ConnectFormData.apply)(ConnectFormData.unapply))

  case class NewListData(name: String,
                         defaults: CampaignDefaults,
                         reminder: String,
                         contact: ListContact,
                         allAttendees: Boolean,
                         includePreviousEvents: Boolean,
                         brands: List[Long])

  val newListForm = Form(mapping(
    "name" -> nonEmptyText,
    "defaults" -> mapping(
      "fromName" -> nonEmptyText,
      "fromEmail" -> email,
      "subject" -> nonEmptyText,
      "language" -> language)(CampaignDefaults.apply)(CampaignDefaults.unapply),
    "reminder" -> nonEmptyText,
    "company" -> mapping(
      "name" -> nonEmptyText,
      "address1" -> nonEmptyText,
      "address2" -> optional(nonEmptyText),
      "zip" -> nonEmptyText,
      "city" -> nonEmptyText,
      "state" -> nonEmptyText,
      "countryCode" -> country,
      "phone" -> ignored(None.asInstanceOf[Option[String]]))(ListContact.apply)(ListContact.unapply),
    "allAttendees" -> boolean,
    "includePreviousEvents" -> boolean,
    "brands" -> list(longNumber(min = 1))
  )(NewListData.apply)(NewListData.unapply))
}
