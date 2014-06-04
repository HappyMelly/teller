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
package services

import models.Person
import play.api.mvc.RequestHeader
import play.api.{ Logger, Play }
import akka.actor.{ Props, Actor }
import play.api.libs.concurrent.Akka
import play.api.Play.current

/**
 * Service to asynchronously send e-mail using an Akka actor.
 */
object EmailService {

  val emailServiceActor = Akka.system.actorOf(Props[EmailServiceActor])
  val from = Play.configuration.getString("mail.from").getOrElse(sys.error("mail.from not configured"))

  case class EmailMessage(to: List[String],
    from: String,
    subject: String,
    body: String,
    richMessage: Boolean = false,
    attachment: Option[(String, String)] = None)

  /**
   * Sends an e-mail message asynchronously using an actor.
   */
  def send(recipients: Set[Person],
    subject: String,
    body: String,
    richMessage: Boolean = false,
    attachment: Option[(String, String)] = None): Unit = {
    val recipientAddresses = recipients.map(p ⇒ s"${p.fullName} <${p.emailAddress}>")
    val message = EmailMessage(recipientAddresses.toList, from, subject, body, richMessage, attachment)
    emailServiceActor ! message
  }

  /**
   * Actor that sends an e-mail message synchronously.
   */
  class EmailServiceActor extends Actor {

    def receive = {
      case message: EmailMessage ⇒ {
        import org.apache.commons.mail._
        import scala.util.Try
        import java.net.URL

        val commonsMail: Email = if (message.attachment.isDefined) {
          val attachment = new EmailAttachment()
          // ".pdf" -> dirty hack to make Apache Common library handle
          // urls correctly (it doesn't understand urls without extension)
          attachment.setURL(new URL(message.attachment.get._1 + ".pdf"))
          attachment.setDisposition(EmailAttachment.ATTACHMENT)
          attachment.setName(message.attachment.get._2)
          new HtmlEmail().attach(attachment).setMsg(message.body.trim)
        } else if (message.richMessage) {
          new HtmlEmail().setHtmlMsg(message.body.trim)
        } else {
          new SimpleEmail().setMsg(message.body.trim)
        }

        message.to.foreach(commonsMail.addTo(_))
        // cc.foreach(commonsMail.addCc(_))
        // bcc.foreach(commonsMail.addBcc(_))

        val preparedMail = commonsMail.
          setFrom(message.from).
          setSubject(message.subject)
        Logger.debug(s"Sending e-mail with subject: ${message.subject}")

        if (Play.configuration.getBoolean("development").exists(_ == true)) {
          Logger.debug(s"${message.body}")
          true
        } else {
          preparedMail.setSSL(true)
          preparedMail.setHostName(Play.configuration.getString("smtp.host").get)
          preparedMail.setAuthenticator(new DefaultAuthenticator(
            Play.configuration.getString("smtp.user").get,
            Play.configuration.getString("smtp.password").get));
          // Send the email and check for exceptions
          Try(preparedMail.send).isSuccess
        }
      }
    }
  }
}
