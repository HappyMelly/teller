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
import controllers.{Security, Utilities}
import libs.mailchimp.Client
import models.UserRole.Role
import models.cm.Facilitator
import models.cm.facilitator.{SlackChannel, SlackChannelBlock}
import models.repository.Repositories
import models.{ActiveUser, Brand}
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{Messages, MessagesApi}
import play.api.libs.json.{JsValue, Json, Writes}
import play.api.mvc.{AnyContent, Request, Result}
import securesocial.core.SecureSocial
import securesocial.core.providers.SlackProvider
import services.TellerRuntimeEnvironment
import slack.api.{ApiError, SlackApiClient}

import scala.concurrent.Future

/**
  *  Contains methods for managing Slack integration for facilitators
  */
class Slack @Inject()(override implicit val env: TellerRuntimeEnvironment,
                      override val messagesApi: MessagesApi,
                      val repos: Repositories,
                      @Named("slack-servant") slackServant: ActorRef,
                      deadbolt: DeadboltActions, handlers: HandlerCache, actionBuilder: ActionBuilders)
  extends Security(deadbolt, handlers, actionBuilder, repos)(messagesApi, env) {

  /**
    * Authenticates current user through Slack and links Slack to her account
    */
  def activate = RestrictedAction(Role.Facilitator) { implicit request => implicit handler => implicit user =>
    val url = controllers.core.routes.People.details(user.person.identifier).url + "#slack"
    val session = request.session -
      SecureSocial.OriginalUrlKey +
      (SecureSocial.OriginalUrlKey -> url)
    val route = env.routes.authenticationUrl(SlackProvider.Slack)
    redirect(route, session)
  }

  /**
    * Connects Slack channel with a set of given brands
    */
  def connect = withSlackIntegration { slackId => implicit request => implicit handler => implicit user =>
    Slack.connectForm.bindFromRequest().fold(
      errors => jsonFormError(Utilities.errorsToJson(errors)),
      data => {
        withSlackClient(slackId) { client =>
          (for {
            f <- repos.cm.facilitator.findByPerson(user.person.identifier)
            b <- repos.cm.brand.find(f.map(_.brandId))
          } yield (f, b.map(_.brand))) flatMap { case (records, brands) =>
            val validBrands = records.map(_.brandId).filter(x => data.brands.contains(x))
            val results = validBrands.map { brandId =>
              val channel = data.channel(brandId, user.person.identifier)
              repos.cm.facilitatorSettings.insertChannel(channel)
            }
            Future.sequence(results).flatMap { channels =>
              slackServant ! (user.person.identifier, channels.head.remoteId)
              val msg = "Slack channel was successfully connected to selected brand(s)"
              renderChannelBlock(records, channels, brands, msg)
            }
          }
        }
      }
    )
  }

  def create = withSlackIntegration { mailChimpId => implicit request => implicit handler => implicit user =>
    Slack.newChannelForm.bindFromRequest().fold(
      errors => jsonFormError(Utilities.errorsToJson(errors)),
      data => {
        withSlackClient(mailChimpId) { client =>
          val mayBeChannelId = createChannel(data.name, data.public, client)
          (for {
            c <- mayBeChannelId
            r <- settingsQuery(user.person.identifier)
          } yield (c, r)) flatMap {
            case (Left(error), _) => jsonBadRequest(error)
            case (Right(channelId), (records, _, brands)) =>
              val validBrands = records.map(_.brandId).filter(x => data.brands.contains(x))
              val results = validBrands.map { brandId =>
                val channel = SlackChannel(None, data.name, channelId, data.public, brandId,
                  user.person.identifier, data.allAttendees, data.oldEventAttendees)
                repos.cm.facilitatorSettings.insertChannel(channel)
              }
              Future.sequence(results).flatMap { channels =>
                slackServant ! (user.person.identifier, channelId)
                val msg = "Slack channel was successfully created and connected to selected brand(s)"
                renderChannelBlock(records, channels, brands, msg)
              }
          }
        }
      })
  }

  /**
    * Breaks MailChimp connection for current user
    */
  def deactivate = withSlackIntegration { mailChimpId => implicit request => implicit handler => implicit user =>
    val account = user.account.copy(slack = None)
    repos.userAccount.update(account) flatMap { _ =>
      env.updateCurrentUser(user.copy(account = account))
      jsonSuccess("Slack integration was successfully deactivated")
    }
  }

  /**
    * Disconnects Slack channel with a set of given brands
    */
  def disconnect = RestrictedAction(Role.Facilitator) { implicit request => implicit handler => implicit user =>
    val form = Form(single("list_id" -> nonEmptyText))

    form.bindFromRequest().fold(
      errors => jsonFormError(Utilities.errorsToJson(errors)),
      remoteId => {
        repos.cm.facilitatorSettings.channels(user.person.identifier) flatMap { channels =>
          val validChannels = channels.filter(_.remoteId == remoteId)
          val result = validChannels.map { channel =>
            repos.cm.facilitatorSettings.deleteChannel(channel.personId, channel.id.get)
          }
          Future.sequence(result) flatMap { _ =>
            jsonSuccess("Slack channel was successfully disconnected from selected brands")
          }
        }
      })
  }

  /**
    * Updates connected Slack channel with a set of given brands
    */
  def update = withSlackIntegration { slackId => implicit request => implicit handler => implicit user =>
    Slack.connectForm.bindFromRequest().fold(
      errors => jsonBadRequest(errors.errors.map(_.message).mkString(",")),
      data => {
        settingsQuery(user.person.identifier) flatMap { case (records, channels, brands) =>
          val removed = channels.filter(_.remoteId == data.id).map { channel =>
            repos.cm.facilitatorSettings.deleteChannel(channel.personId, channel.id.get)
          }
          Future.sequence(removed).flatMap { _ =>
            val validBrands = records.map(_.brandId).filter(x => data.brands.contains(x))
            val results = validBrands.map { brandId =>
              val channel = data.channel(brandId, user.person.identifier)
              repos.cm.facilitatorSettings.insertChannel(channel)
            }
            Future.sequence(results).flatMap { channels =>
              slackServant ! (user.person.identifier, channels.head.remoteId)
              val msg = "Slack list settings were successfully updated"
              renderChannelBlock(records, channels, brands, msg)
            }
          }
        }
      })
  }

  /**
    * Returns list of MailChimp lists for current user
    */
  def channels = withSlackIntegration { slackId => implicit request => implicit handler => implicit user =>
    case class SlackChanneInfo(id: String, name: String, public: Boolean = true)
    implicit val channelWrites = new Writes[SlackChanneInfo] {
      def writes(channel: SlackChanneInfo): JsValue = Json.obj(
        "id" -> channel.id,
        "name" -> channel.name,
        "public" -> channel.public
      )
    }
    withSlackClient(slackId) { client =>
      try {
        val request = for {
          g <- client.listGroups(excludeArchived = 1)
          c <- client.listChannels(excludeArchived = 1)
        } yield (g, c)
        request.flatMap { case (groups, channels) =>
          val allChannels = channels.map(c => SlackChanneInfo(c.id, c.name)).toList :::
            groups.map(g => SlackChanneInfo(g.id, g.name, public = false)).toList
          jsonOk(Json.obj("lists" -> allChannels.sortBy(_.name)))
        }
      } catch {
        case e: ApiError => jsonBadRequest(e.code)
      }
    }
  }

  /**
    * Renders settings screen for current user
    */
  def settings(id: Long) = RestrictedAction(Role.Facilitator) { implicit request => implicit handler => implicit user =>
    val personId = user.account.personId
    settingsQuery(personId) flatMap { case (facilitators, channels, brands) =>
      val blocks = channels.groupBy(_.remoteId).map { case (channelId, subchannels) =>
        val relatedBrands = brands.filter(x => subchannels.exists(_.brandId == x.identifier))
        SlackChannelBlock(subchannels.head, relatedBrands, facilitators.length)
      }
      ok(views.html.v2.person.tabs.slack(user.account.isSlackActive, blocks.toSeq, brands))
    }
  }

  protected def checkMergeFields(client: Client, listId: String)(f: String => Future[Result]): Future[Result] = {
    client.mergeFields(listId).flatMap {
      case Left(e) => jsonBadRequest(e.detail)
      case Right(fields) =>
        models.core.integration.MailChimp.validateMergeFields(fields) match {
          case Left(msg) => jsonBadRequest(msg)
          case Right(msg) => f(msg)
        }
    }
  }

  protected def renderChannelBlock(records: List[Facilitator],
                                   channels: Seq[SlackChannel],
                                   brands: Seq[Brand],
                                   msg: String): Future[Result] = {
    val relatedBrands = brands.filter(b => channels.exists(_.brandId == b.identifier))
    val block = SlackChannelBlock(channels.head, relatedBrands, records.length)
    val body = views.html.v2.person.blocks.slack.channel(block, brands)
    jsonSuccess(msg, body = Some(body))
  }

  protected def settingsQuery(personId: Long) = for {
    f <- repos.cm.facilitator.findByPerson(personId)
    c <- repos.cm.facilitatorSettings.channels(personId)
    b <- repos.cm.brand.find(f.map(_.brandId))
  } yield (f, c, b.map(_.brand))

  protected def withSlackClient(slackId: String)(f: SlackApiClient => Future[Result])(
                               implicit request: Request[AnyContent], user: ActiveUser) = {
    repos.identity.findByUserId(slackId, SlackProvider.Slack) flatMap {
      case None => jsonInternalError("Internal error. Please contact the support")
      case Some(identity) =>
        val sc = new SlackApiClient(identity.profile.oAuth2Info.get.accessToken)
        f(sc)
    }
  }

  protected def withSlackIntegration(f: String => Request[AnyContent] =>
    be.objectify.deadbolt.scala.DeadboltHandler => ActiveUser => Future[Result]) =

    RestrictedAction(Role.Facilitator) { implicit request => implicit handler => implicit user =>
      user.account.slack match {
        case None => jsonBadRequest("Slack integration is not active")
        case Some(slackId) => f(slackId)(request)(handler)(user)
      }
    }

  protected def createChannel(name: String, public: Boolean, client: SlackApiClient): Future[Either[String, String]] = {
    val channelId = if (public)
      client.createChannel(name).map(_.id)
    else
      client.createGroup(name).map(_.id)
    channelId.map(x => Right(x)).recoverWith {
      case e: ApiError =>
        Future.successful(Left(Messages("api.slack." + e.code, name)))
    }
  }
}

object Slack {

  case class ConnectFormData(id: String,
                             name: String,
                             public: Boolean,
                             brandIds: List[Long],
                             activeBrandIds: List[Boolean],
                             allAttendees: Boolean,
                             oldEventAttendees: Boolean) {

    def brands: List[Long] = brandIds.zip(activeBrandIds).filter(_._2).map(_._1)

    def channel(brandId: Long, personId: Long): SlackChannel =
      SlackChannel(None, name, id, public, brandId, personId, allAttendees, oldEventAttendees)
  }


  val connectForm = Form(mapping(
    "list_id" -> nonEmptyText,
    "list_name" -> nonEmptyText,
    "public" -> boolean,
    "brands" -> list(longNumber),
    "brand_flags" -> list(boolean).verifying("Select at least one brand", l => l.contains(true)),
    "all_attendees" -> boolean,
    "include_previous_events" -> boolean
  )(ConnectFormData.apply)(ConnectFormData.unapply))

  case class NewChannelData(name: String,
                            public: Boolean,
                            allAttendees: Boolean,
                            oldEventAttendees: Boolean,
                            brandIds: List[Long],
                            activeBrandIds: List[Boolean]) {

    def brands: List[Long] = brandIds.zip(activeBrandIds).filter(_._2).map(_._1)
  }

  val newChannelForm = Form(mapping(
    "name" -> nonEmptyText,
    "type" -> boolean,
    "all_attendees" -> boolean,
    "include_previous_events" -> boolean,
    "brands" -> list(longNumber(min = 1)),
    "brand_flags" -> list(boolean).verifying("Select at least one brand", l => l.contains(true))
  )(NewChannelData.apply)(NewChannelData.unapply))
}
