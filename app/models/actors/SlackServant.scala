package models.actors

import javax.inject.{Inject, Singleton}

import akka.actor.Actor
import models.actors.SlackServant.InviteMember
import models.repository.IRepositories
import play.api.Play
import play.api.Play.current
import slack.api.SlackApiClient

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Invites attendees to Slack channels
  */
@Singleton
class SlackServant @Inject()(repos: IRepositories) extends Actor {

  def receive = {
    case InviteMember(personId) =>
      Play.configuration.getString("slack.token") match {
        case Some(token) =>
          repos.person.find(personId) map {
            case Some(person) =>
              val client = SlackApiClient(token)
              val (hmSupportersId, hmGetToKnowYouId, hmCoffeeId) = ("C04QY3M75", "C052YRSCX", "C0CBWL2RK")
              val channels = Seq(hmSupportersId, hmGetToKnowYouId, hmCoffeeId)
              client.inviteUser(person.email, channels, person.firstName)
            case None => Nil
          }
        case None => Nil
      }
  }
}

object SlackServant {
  final case class InviteMember(personId: Long)
}
