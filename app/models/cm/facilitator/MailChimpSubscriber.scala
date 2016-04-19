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
import libs.mailchimp.Client
import models.{UserAccount, Person}
import models.cm.Event
import models.cm.event.Attendee
import models.repository.IRepositories
import play.api.Logger
import security.MailChimpProvider

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Subscribes attendees to MailChimp lists
  */
class MailChimpSubscriber @Inject() (repos: IRepositories) extends Actor {

  def receive = {
    case (attendeeId: Long, eventId: Long, evaluation: Boolean) => subscribeAttendee(attendeeId, eventId, evaluation)
    case (personId: Long, listId: String) => subscribeOldAttendees(personId, listId)
  }

  def subscribeAttendee(attendeeId: Long, eventId: Long, evaluation: Boolean) = {
    attendeeWithEvent(attendeeId, eventId) map {
      case (None, _, _) => Nil
      case (Some(attendee), facilitators, event) =>
        facilitators.foreach { facilitator =>
          accountWithMailChimpLists(facilitator.identifier) map { case (account, lists) =>
            val validLists = brandLists(lists, event.brandId, evaluation)
            if (account.mailchimp.nonEmpty && validLists.nonEmpty) {
              mailChimpClient(account.mailchimp.get) map {
                case None =>
                  val msg = s"Subscription for attendee $attendeeId failed. Couldn't create MailChimp client ${account.mailchimp.get}"
                  Logger.error(msg)
                case Some(client) =>
                  validLists.foreach { list =>
                    client.subscribe(list.listId, attendee.email, attendee.firstName, attendee.lastName)
                  }
              }
            }
          }
        }
    }
  }

  def subscribeOldAttendees(personId: Long, listId: String) = {
    accountWithMailChimpLists(personId) map { case (account, lists) =>
      if (account.mailchimp.nonEmpty && lists.exists(_.listId == listId)) {
        mailChimpClient(account.mailchimp.get) map {
          case None =>
            val msg = s"Subscription for list $listId failed. Couldn't create MailChimp client ${account.mailchimp.get}"
            Logger.error(msg)
          case Some(client) =>
            val validLists = lists.filter(_.listId == listId)
            validLists.foreach { list =>
              (for {
                e <- repos.cm.event.findByFacilitator(personId, Some(list.brandId), future = Some(false))
                a <- repos.cm.rep.event.attendee.findByEvents(e.map(_.identifier))
              } yield a.map(_._2)) map { attendees =>
                val validAttendees = if (list.allAttendees) attendees else attendees.filter(_.evaluationId.nonEmpty)
                validAttendees.foreach { attendee =>
                  client.subscribe(listId, attendee.email, attendee.firstName, attendee.lastName)
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

  protected def accountWithMailChimpLists(personId: Long): Future[(UserAccount, Seq[MailChimpList])] =
    for {
      a <- repos.userAccount.findByPerson(personId) if a.nonEmpty
      l <- repos.cm.facilitatorSettings.lists(personId)
    } yield (a.get, l)

  protected def mailChimpClient(mailChimpId: String): Future[Option[Client]] =
    repos.identity.findByUserId(mailChimpId, MailChimpProvider.MailChimp) map { mayBeIdentity =>
      val data = for {
        identity <- mayBeIdentity
        info <- MailChimpProvider.toExtraInfo(identity.profile.extraInfo)
      } yield (identity, info)
      data match {
        case None => None
        case Some((identity, info)) =>
          Some(new Client(info.apiEndPoint, identity.profile.oAuth2Info.get.accessToken))
      }
    }

  protected def brandLists(lists: Seq[MailChimpList], brandId: Long, evaluation: Boolean): Seq[MailChimpList] =
    if (evaluation)
      lists.filter(_.brandId == brandId)
    else
      lists.filter(_.brandId == brandId).filter(_.allAttendees)
}
