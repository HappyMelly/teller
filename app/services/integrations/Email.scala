/*
 * Happy Melly Teller
 * Copyright (C) 2014, Happy Melly http://www.happymelly.com
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
 * If you have questions concerning this license or the applicable additional terms, you may contact
 * by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */
package services.integrations

import akka.actor.{Actor, Props}
import com.typesafe.plugin._
import models.Person
import play.api.Play.current
import play.api.libs.concurrent.Akka
import play.api.libs.mailer.AttachmentFile
import play.api.{Logger, Play}
import services.integrations.EmailService.EmailMessage

import scala.util.Try

/**
 * Service to asynchronously send e-mail using an Akka actor.
 */
class Email {

  /**
   * Sends an e-mail message asynchronously using an actor.
   */
  def send(to: Set[Person],
    cc: Option[Set[Person]] = None,
    bcc: Option[Set[Person]] = None,
    subject: String,
    body: String,
    from: String = "Happy Melly",
    richMessage: Boolean = false,
    attachment: Option[(String, String)] = None): Unit = {
    val toAddresses = to.map(p ⇒ s"${p.fullName} <${p.email}>")
    val ccAddresses = cc.map(_.map(p ⇒ s"${p.fullName} <${p.email}>"))
    val bccAddresses = bcc.map(_.map(p ⇒ s"${p.fullName} <${p.email}>"))
    val mailFrom = from + " " + EmailService.from
    val message = EmailMessage(toAddresses.toList,
      ccAddresses.map(_.toList).getOrElse(List[String]()),
      bccAddresses.map(_.toList).getOrElse(List[String]()),
      mailFrom, subject, body, richMessage, attachment)
    EmailService.emailServiceActor ! message
  }

}

object EmailService {
  val emailServiceActor = Akka.system.actorOf(Props[EmailServiceActor])
  val from = Play.configuration.getString("mail.from").getOrElse(sys.error("mail.from not configured"))

  case class EmailMessage(to: List[String],
    cc: List[String],
    bcc: List[String],
    from: String,
    subject: String,
    body: String,
    richMessage: Boolean = false,
    attachment: Option[(String, String)] = None)

  /**
   * Actor that sends an e-mail message synchronously.
   */
  class EmailServiceActor extends Actor {

    def receive = {
      case message: EmailMessage ⇒ {

        import java.io.File

        import play.api.libs.mailer

        val mail = use[MailerPlugin].email

        val emptyMail = mailer.Email(message.subject, message.from, charset = Some("UTF-8"),
          to = message.to, cc = message.cc, bcc = message.bcc)
        val withAttachments = message.attachment.map { attachment =>
          emptyMail.copy(attachments = Seq(
            AttachmentFile(attachment._2, new File(attachment._1))
          ))
        } getOrElse emptyMail
        val withBody = if (message.richMessage)
          withAttachments.copy(bodyHtml = Some(message.body.trim))
        else
          withAttachments.copy(bodyText = Some(message.body.trim))

        Logger.debug(s"Sending e-mail with subject: ${message.subject}")

        if (Play.isDev && Play.configuration.getBoolean("mail.stub").exists(_ == true)) {
          Logger.debug(s"${message.body}")
        } else {
          Try(mailer.MailerPlugin.send(withBody)).isSuccess
        }
      }
    }
  }

}
