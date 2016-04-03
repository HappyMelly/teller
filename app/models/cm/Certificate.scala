/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2014, Happy Melly http://www.happymelly.com
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

package models.cm

import java.io.ByteArrayOutputStream

import com.itextpdf.text._
import com.itextpdf.text.pdf.{BaseFont, ColumnText, PdfWriter}
import fly.play.s3.{BucketFile, S3Exception}
import models.cm.event.Attendee
import models.repository.cm.BrandWithCoordinators
import models.{Person, File}
import models.repository.{IRepositories, Repositories}
import org.joda.time.LocalDate
import play.api.i18n.Messages
import play.api.libs.concurrent.Execution.Implicits._
import services.S3Bucket
import services.integrations.EmailComponent

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

/**
 * An certificate which a participant gets after an event
 */
case class Certificate(issued: Option[LocalDate], event: Event, attendee: Attendee,
   renew: Boolean = false)(implicit messages: Messages) {

  val id = issued.map(_.toString("yyMM")).getOrElse("") + f"${attendee.id.get}%03d"

  /**
   * Creates and sends new certificate to a participant
    *
    * @param brand Brand data
   * @param approver Person who generates the certificate
   */
  def generateAndSend(brand: BrandWithCoordinators, approver: Person, email: EmailComponent, services: IRepositories) {
    val contentType = "application/pdf"
    val pdf = if (brand.brand.code == "LCM")
      lcmCertificate(brand.brand.id.get, event, attendee, services)
    else
      m30Certificate(issued, brand.brand.id.get, event, attendee, services)
    if (renew) {
      Certificate.file(id).remove()
    }
    S3Bucket.add(BucketFile(Certificate.fullFileName(id), contentType, pdf)).map { unit ⇒
      sendEmail(brand, approver, pdf, email, services)
    }.recover {
      case S3Exception(status, code, message, originalXml) ⇒ {}
    }
  }

  def send(brand: BrandWithCoordinators, approver: Person, email: EmailComponent, services: IRepositories) {
    val pdf = Certificate.file(id).uploadToCache()
    pdf.foreach { value ⇒
      sendEmail(brand, approver, value, email, services)
    }
  }

  private def sendEmail(brand: BrandWithCoordinators, approver: Person, data: Array[Byte], email: EmailComponent, services: IRepositories) {
    val file = java.io.File.createTempFile("cert", ".pdf")
    (new java.io.FileOutputStream(file)).write(data)
    val brandName = brand.brand.code match {
      case "LCM" ⇒ "lean-change-management-"
      case "MGT30" ⇒ "management-3-0-"
      case _ ⇒ ""
    }
    val name = "your-%scertificate-%s.pdf".format(brandName, LocalDate.now().toString)
    val body = mail.evaluation.html.approved(brand.brand, attendee, approver).toString()
    val subject = s"Your ${brand.brand.name} certificate"
    val bcc = brand.coordinators.filter(_._2.notification.certificate).map(_._1)
    email.send(Seq(attendee), event.facilitators(services), bcc,
      subject, body, brand.brand.sender, attachment = Some((file.getPath, name)))
  }

  /**
   * Returns new generated LCM certificate
   *
   * @param brandId Brand identifier
   * @param event Event
   * @param attendee Attendee
   */
  private def lcmCertificate(brandId: Long, event: Event, attendee: Attendee, services: IRepositories): Array[Byte] = {

    val document = new Document(PageSize.A4.rotate)
    val baseFont = BaseFont.createFont("reports/fonts/SourceSansPro-ExtraLight.ttf",
      BaseFont.IDENTITY_H, BaseFont.EMBEDDED)
    val arialFont = FontFactory.getFont("Arial Bold",
      BaseFont.IDENTITY_H, true, 12, Font.BOLD)

    val output = new ByteArrayOutputStream()
    val writer = PdfWriter.getInstance(document, output)
    document.open()
    val facilitators = event.facilitators(services)
    val cofacilitator = if (facilitators.length > 1) true else false
    val img = template(brandId, event, cofacilitator, services: IRepositories)
    img.setAbsolutePosition(28, 0)
    img.scalePercent(24)
    document.add(img)

    val font = new Font(baseFont, 40)
    font.setColor(0, 0, 0)

    val name = new Phrase(attendee.fullName, font)
    val title = new Phrase(event.title, font)
    val dateString = if (event.schedule.start == event.schedule.end) {
      event.schedule.end.toString("MMMM d yyyy")
    } else {
      event.schedule.start.toString("MMMM d, ") + event.schedule.end.toString("d yyyy")
    }
    val location = "%s in %s, %s".format(dateString, event.location.city,
      Messages("country." + event.location.countryCode))
    val locationPhrase = new Phrase(location, font)

    val canvas = writer.getDirectContent
    canvas.saveState()
    ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, name, PageSize.A4.getHeight / 2, 410, 0)
    font.setSize(30)
    ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, locationPhrase, PageSize.A4.getHeight / 2, 283, 0)
    font.setSize(18)
    val issued = LocalDate.now().toString("MMMM d, yyyy").toUpperCase
    val issueDate = new Phrase(issued, arialFont)

    if (cofacilitator) {
      ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, issueDate, 518, 90, 0)
      val firstFacilitator = facilitators.head
      val secondFacilitator = facilitators.last
      val firstName = new Phrase(firstFacilitator.fullName.toUpperCase, arialFont)
      val secondName = new Phrase(secondFacilitator.fullName.toUpperCase, arialFont)
      ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER,
        firstName, 400, 150, 0)
      ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER,
        secondName, 640, 150, 0)
      if (firstFacilitator.signature) {
        val imageData = Await.result(Person.signature(firstFacilitator.id.get).uploadToCache(),
          5 seconds)
        try {
          val signature = com.itextpdf.text.Image.getInstance(imageData, true)
          signature.setAbsolutePosition(350, 175)
          signature.scaleToFit(90, 80)
          document.add(signature)
        } catch {
          case _: Throwable => Unit
        }
      }
      if (secondFacilitator.signature) {
        val imageData = Await.result(Person.signature(secondFacilitator.id.get).uploadToCache(),
          5 seconds)
        try {
          val signature = com.itextpdf.text.Image.getInstance(imageData, true)
          signature.setAbsolutePosition(590, 175)
          signature.scaleToFit(90, 80)
          document.add(signature)
        } catch {
          case _: Throwable => Unit
        }
      }
    } else {
      ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, issueDate, 480, 90, 0)
      val facilitator = facilitators.head
      val name = new Phrase(facilitator.fullName.toUpperCase, arialFont)
      ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, name, 480, 150, 0)
      if (facilitator.signature) {
        val imageData = Await.result(Person.signature(facilitator.id.get).uploadToCache(),
          5 seconds)
        try {
          val signature = com.itextpdf.text.Image.getInstance(imageData, true)
          signature.scaleToFit(115, 100)
          signature.setAbsolutePosition(410, 170)
          document.add(signature)
        } catch {
          case _: Throwable => Unit
        }
      }
    }
    canvas.restoreState
    document.close()

    output.toByteArray
  }

  /**
   * Returns new generated M30 certificate for the given participant
   *
   * @param brandId Brand identifier
   * @param handledDate The date participant's evaluation was approved
   * @param event Event
   * @param attendee Attendee
   */
  private def m30Certificate(handledDate: Option[LocalDate], brandId: Long, event: Event,
                             attendee: Attendee, services: IRepositories): Array[Byte] = {

    val document = new Document(PageSize.A4.rotate)
    val baseFont = BaseFont.createFont("reports/fonts/DejaVuSerif.ttf",
      BaseFont.IDENTITY_H, BaseFont.EMBEDDED)

    val output = new ByteArrayOutputStream()
    val writer = PdfWriter.getInstance(document, output)
    document.open()
    val facilitators = event.facilitators(services)
    val cofacilitator = if (facilitators.length > 1) true else false
    val img = template(brandId, event, cofacilitator, services: IRepositories)
    img.setAbsolutePosition(7, 2)
    img.scalePercent(48)
    document.add(img)

    val font = new Font(baseFont, 20)
    font.setColor(0, 181, 228)

    val name = new Phrase(attendee.fullName, font)
    val title = new Phrase(event.title, font)
    val dateString = if (event.schedule.start == event.schedule.end) {
      event.schedule.end.toString("d MMMM yyyy")
    } else {
      event.schedule.start.toString("d + ") + event.schedule.end.toString("d MMMM yyyy")
    }
    val eventDate = new Phrase(dateString, font)
    val location = new Phrase(event.location.city + ", " +
      Messages("country." + event.location.countryCode), font)
    val date = new Phrase(handledDate.map(_.toString("dd MMMM yyyy")).getOrElse(""), font)
    val certificateIdBlock = new Phrase(id, font)

    val canvas = writer.getDirectContent
    canvas.saveState()
    ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, name, PageSize.A4.getHeight / 2, 415, 0)
    ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, title, PageSize.A4.getHeight / 2, 330, 0)
    font.setSize(12)
    font.setColor(150, 150, 150)
    ColumnText.showTextAligned(canvas, Element.ALIGN_RIGHT, certificateIdBlock, 680, 450, 0)
    font.setColor(0, 0, 0)
    ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, eventDate, 207, 290, 0)
    ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, location, 208, 235, 0)
    ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, date, 208, 183, 0)

    if (cofacilitator) {
      val firstFacilitator = facilitators.head
      val secondFacilitator = facilitators.last
      val firstName = new Phrase(firstFacilitator.fullName, font)
      val secondName = new Phrase(secondFacilitator.fullName, font)
      ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, firstName, 650, 290, 0)
      ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, secondName, 650, 185, 0)
      if (firstFacilitator.signature) {
        val imageData = Await.result(Person.signature(firstFacilitator.id.get).uploadToCache(),
          5 seconds)
        try {
          val signature = com.itextpdf.text.Image.getInstance(imageData, true)
          signature.setAbsolutePosition(595, 300)
          signature.scaleToFit(100, 95)
          document.add(signature)
        } catch {
          case _: Throwable => Unit
        }
      }
      if (secondFacilitator.signature) {
        val imageData = Await.result(Person.signature(secondFacilitator.id.get).uploadToCache(),
          5 seconds)
        try {
          val signature = com.itextpdf.text.Image.getInstance(imageData, true)
          signature.setAbsolutePosition(595, 180)
          signature.scaleToFit(100, 95)
          document.add(signature)
        } catch {
          case _: Throwable => Unit
        }
      }
    } else {
      val facilitator = facilitators.head
      val name = new Phrase(facilitator.fullName, font)
      ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, name, 650, 185, 0)
      if (facilitator.signature) {
        val imageData = Await.result(Person.signature(facilitator.id.get).uploadToCache(),
          5 seconds)
        try {
          val signature = com.itextpdf.text.Image.getInstance(imageData, true)
          signature.scaleToFit(155, 100)
          signature.setAbsolutePosition(570, 205)
          document.add(signature)
        } catch {
          case _: Throwable => Unit
        }
      }
    }
    canvas.restoreState
    document.close()

    output.toByteArray
  }

  /**
   * Get a raw certificate template
   *
   * @param brandId Brand identifier
   * @param event Event
   * @param twoFacilitators Shows if the event was facilitated by one or more facilitators
   * @return
   */
  private def template(brandId: Long, event: Event, twoFacilitators: Boolean, services: IRepositories): com.itextpdf.text.Image = {
    val templates = Await.result(services.cm.certificate.findByBrand(brandId), 3.seconds)
    val data = templates.find(_.language == event.language.spoken) map { tpl ⇒
      if (twoFacilitators) tpl.twoFacilitators else tpl.oneFacilitator
    } getOrElse {
      templates.find(_.language == "EN") map { tpl ⇒
        if (twoFacilitators) tpl.twoFacilitators else tpl.oneFacilitator
      } getOrElse Array[Byte]()
    }
    com.itextpdf.text.Image.getInstance(data)
  }
}

object Certificate {

  def cacheId(id: String): String = "certificate." + id

  def fileName(id: String): String = id + ".pdf"

  def fullFileName(id: String): String = "certificates/" + fileName(id)

  def file(id: String): File =
    File.pdf(Certificate.fullFileName(id), Certificate.cacheId(id))
}
