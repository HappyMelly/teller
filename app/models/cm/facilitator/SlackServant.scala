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

package models.cm.facilitator

import javax.inject.Inject

import akka.actor.Actor
import models.cm.Event
import models.cm.event.Attendee
import models.repository.IRepositories
import models.{Person, UserAccount}
import play.api.Logger
import play.api.i18n.Messages
import securesocial.core.providers.SlackProvider
import slack.api.{ApiError, SlackApiClient}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Invites attendees to Slack channels
  */
class   SlackServant @Inject()(repos: IRepositories) extends Actor {

  def receive = {
    case (attendeeId: Long, eventId: Long, evaluation: Boolean) => inviteAttendee(attendeeId, eventId, evaluation)
    case (personId: Long, listId: String) => inviteOldAttendees(personId, listId)
  }

  def inviteAttendee(attendeeId: Long, eventId: Long, evaluation: Boolean) = {
    attendeeWithEvent(attendeeId, eventId) map {
      case (None, _, _) => Nil
      case (Some(attendee), facilitators, event) =>
        facilitators.foreach { facilitator =>
          accountWithSlackChannels(facilitator.identifier) map { case (account, channels) =>
            val validChannels = brandChannels(channels, event.brandId, evaluation)
            if (account.slack.nonEmpty && validChannels.nonEmpty) {
              slackClient(account.slack.get) map {
                case None =>
                  val msg = s"Invitation of attendee $attendeeId failed. Couldn't create Slack client ${account.slack.get}"
                  Logger.error(msg)
                case Some(client) =>
                  client.inviteUser(attendee.email, validChannels.map(_.remoteId), attendee.firstName)
              }
            }
          }
        }
    }
  }

  def inviteOldAttendees(personId: Long, channelId: String) = {
    accountWithSlackChannels(personId) map { case (account, channels) =>
      if (account.slack.nonEmpty && channels.exists(_.remoteId == channelId)) {
        slackClient(account.slack.get) map {
          case None =>
            val msg = s"Invitation to channel $channelId failed. Couldn't create Slack client ${account.slack.get}"
            Logger.error(msg)
          case Some(client) =>
            val validLists = channels.filter(_.remoteId == channelId)
            validLists.foreach { channel =>
              (for {
                e <- repos.cm.event.findByFacilitator(personId, Some(channel.brandId), future = Some(false))
                a <- repos.cm.rep.event.attendee.findByEvents(e.map(_.identifier))
              } yield a.map(_._2)) map { attendees =>
                val validAttendees = if (channel.allAttendees) attendees else attendees.filter(_.evaluationId.nonEmpty)
                validAttendees.foreach { attendee =>
                  println(attendee.email)
                  client.inviteUser(attendee.email, Seq(channelId), attendee.firstName).recoverWith {
                    case e: ApiError =>
                      println(e.getMessage)
                      Future.successful(e)
                  }
                }
              }
            }
        }
      }
    }
  }

  protected def attendeeWithEvent(attendeeId: Long, eventId: Long): Future[(Option[Attendee], List[Person], Event)] =
    for {
      a <- repos.cm.rep.event.attendee.find(attendeeId, eventId)
      e <- repos.cm.event.get(eventId)
      f <- repos.cm.event.facilitators(eventId)
    } yield (a, f, e)

  protected def accountWithSlackChannels(personId: Long): Future[(UserAccount, Seq[SlackChannel])] =
    for {
      a <- repos.userAccount.findByPerson(personId) if a.nonEmpty
      l <- repos.cm.facilitatorSettings.channels(personId)
    } yield (a.get, l)

  protected def slackClient(slackId: String): Future[Option[SlackApiClient]] =
    repos.identity.findByUserId(slackId, SlackProvider.Slack) map {
      case None => None
      case Some(identity) => Some(SlackApiClient(identity.profile.oAuth2Info.get.accessToken))
    }

  protected def brandChannels(channels: Seq[SlackChannel], brandId: Long, evaluation: Boolean): Seq[SlackChannel] =
    if (evaluation)
      channels.filter(_.brandId == brandId)
    else
      channels.filter(_.brandId == brandId).filter(_.allAttendees)
}
