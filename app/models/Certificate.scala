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

package models

import java.io.ByteArrayOutputStream

import com.itextpdf.text.pdf.{ BaseFont, ColumnText, PdfWriter }
import com.itextpdf.text.{ Document, Element, Font, Image, PageSize, Phrase }
import fly.play.s3.{ BucketFile, S3Exception }
import models.brand.CertificateTemplate
import models.service.BrandWithCoordinators
import models.service.brand.CertificateTemplateService
import org.joda.time.LocalDate
import play.api.Play.current
import play.api.cache.Cache
import play.api.i18n.Messages
import play.api.libs.concurrent.Execution.Implicits._
import services.S3Bucket
import services.integrations.Integrations

import scala.concurrent.{ Await, Future }
import scala.concurrent.duration._
import scala.language.postfixOps

/**
 * An certificate which a participant gets after an event
 */
case class Certificate(
  issued: Option[LocalDate],
  event: Event,
  participant: Person,
  renew: Boolean = false) extends Integrations {

  val id =
    issued.map(_.toString("yyMM")).getOrElse("") + f"${participant.id.get}%03d"

  /**
   * Creates and sends new certificate to a participant
   * @param brand Brand data
   * @param approver Person who generates the certificate
   */
  def generateAndSend(brand: BrandWithCoordinators, approver: Person) {
    val contentType = "application/pdf"
    val pdf = if (brand.brand.code == "LCM")
      lcmCertificate(brand.brand.id.get, event, participant)
    else
      m30Certificate(issued, brand.brand.id.get, event, participant)
    if (renew) {
      Certificate.removeFromCloud(id)
    }
    S3Bucket.add(BucketFile(Certificate.fullFileName(id), contentType, pdf)).map { unit ⇒
      sendEmail(brand, approver, pdf)
    }.recover {
      case S3Exception(status, code, message, originalXml) ⇒ {}
    }
  }

  def send(brand: BrandWithCoordinators, approver: Person) {
    val pdf = Certificate.downloadFromCloud(id)
    pdf.map {
      case value ⇒
        sendEmail(brand, approver, value)
    }
  }

  private def sendEmail(brand: BrandWithCoordinators, approver: Person, data: Array[Byte]) {
    val file = java.io.File.createTempFile("cert", ".pdf")
    (new java.io.FileOutputStream(file)).write(data)
    val brandName = brand.brand.code match {
      case "LCM" ⇒ "lean-change-management-"
      case "MGT30" ⇒ "management-3-0-"
      case _ ⇒ ""
    }
    val name = "your-%scertificate-%s.pdf".format(brandName, LocalDate.now().toString)
    val body = mail.evaluation.html.approved(brand.brand, participant, approver).toString()
    val subject = s"Your ${brand.brand.name} certificate"
    email.send(Set(participant),
      Some(event.facilitators.toSet),
      Some(brand.coordinators.toSet),
      subject,
      body,
      richMessage = true,
      Some((file.getPath, name)))
  }

  /**
   * Returns new generated LCM certificate
   *
   * @param brandId Brand identifier
   * @param event Event
   * @param participant Participant
   */
  private def lcmCertificate(brandId: Long,
    event: Event,
    participant: Person): Array[Byte] = {

    val document = new Document(PageSize.A4.rotate)
    val baseFont = BaseFont.createFont("reports/fonts/SourceSansPro-ExtraLight.ttf",
      BaseFont.IDENTITY_H, BaseFont.EMBEDDED)

    val output = new ByteArrayOutputStream()
    val writer = PdfWriter.getInstance(document, output)
    document.open()
    val facilitators = event.facilitators
    val cofacilitator = if (facilitators.length > 1) true else false
    val img = template(brandId, event, cofacilitator)
    img.setAbsolutePosition(28, 0)
    img.scalePercent(77)
    document.add(img)

    val font = new Font(baseFont, 40)
    font.setColor(0, 0, 0)

    val name = new Phrase(participant.fullName, font)
    val title = new Phrase(event.title, font)
    val dateString = if (event.schedule.start == event.schedule.end) {
      event.schedule.end.toString("MMMM d")
    } else {
      event.schedule.start.toString("MMMM d, ") + event.schedule.end.toString("d")
    }
    val location = "%s in %s, %s".format(dateString, event.location.city,
      Messages("country." + event.location.countryCode))
    val locationPhrase = new Phrase(location, font)

    val canvas = writer.getDirectContent
    canvas.saveState()
    ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, name, PageSize.A4.getHeight / 2, 320, 0)
    font.setSize(30)
    ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, locationPhrase, PageSize.A4.getHeight / 2, 178, 0)
    font.setSize(18)

    if (cofacilitator) {
      val firstFacilitator = facilitators.head
      val secondFacilitator = facilitators.last
      val firstName = new Phrase(firstFacilitator.fullName, font)
      val secondName = new Phrase(secondFacilitator.fullName, font)
      ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, firstName, 160, 15, 0)
      ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, secondName, 685, 15, 0)
      if (firstFacilitator.signature) {
        val imageData = Await.result(Person.downloadFromCloud(firstFacilitator.id.get),
          5 seconds)
        val signature = Image.getInstance(imageData, true)
        signature.setAbsolutePosition(100, 45)
        signature.scaleToFit(130, 110)
        document.add(signature)
      }
      if (secondFacilitator.signature) {
        val imageData = Await.result(Person.downloadFromCloud(secondFacilitator.id.get),
          5 seconds)
        val signature = Image.getInstance(imageData, true)
        signature.setAbsolutePosition(630, 45)
        signature.scaleToFit(130, 110)
        document.add(signature)
      }
    } else {
      val facilitator = facilitators.head
      val name = new Phrase(facilitator.fullName, font)
      ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, name, 685, 15, 0)
      if (facilitator.signature) {
        val imageData = Await.result(Person.downloadFromCloud(facilitator.id.get),
          5 seconds)
        val signature = Image.getInstance(imageData, true)
        signature.scaleToFit(130, 110)
        signature.setAbsolutePosition(630, 45)
        document.add(signature)
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
   * @param participant Participant
   */
  private def m30Certificate(handledDate: Option[LocalDate],
    brandId: Long,
    event: Event,
    participant: Person): Array[Byte] = {

    val document = new Document(PageSize.A4.rotate)
    val baseFont = BaseFont.createFont("reports/fonts/DejaVuSerif.ttf",
      BaseFont.IDENTITY_H, BaseFont.EMBEDDED)

    val output = new ByteArrayOutputStream()
    val writer = PdfWriter.getInstance(document, output)
    document.open()
    val facilitators = event.facilitators
    val cofacilitator = if (facilitators.length > 1) true else false
    val img = template(brandId, event, cofacilitator)
    img.setAbsolutePosition(7, 10)
    img.scalePercent(55)
    document.add(img)

    val font = new Font(baseFont, 20)
    font.setColor(0, 181, 228)

    val name = new Phrase(participant.fullName, font)
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
    ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, name, PageSize.A4.getHeight / 2, 450, 0)
    ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, title, PageSize.A4.getHeight / 2, 340, 0)
    font.setSize(12)
    font.setColor(150, 150, 150)
    ColumnText.showTextAligned(canvas, Element.ALIGN_RIGHT, certificateIdBlock, 560, 490, 0)
    font.setColor(0, 0, 0)
    ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, eventDate, 190, 275, 0)
    ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, location, 190, 220, 0)
    ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, date, 190, 165, 0)

    if (cofacilitator) {
      val firstFacilitator = facilitators.head
      val secondFacilitator = facilitators.last
      val firstName = new Phrase(firstFacilitator.fullName, font)
      val secondName = new Phrase(secondFacilitator.fullName, font)
      ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, firstName, 595, 165, 0)
      ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, secondName, 725, 165, 0)
      if (firstFacilitator.signature) {
        val imageData = Await.result(Person.downloadFromCloud(firstFacilitator.id.get),
          5 seconds)
        val signature = Image.getInstance(imageData, true)
        signature.setAbsolutePosition(535, 185)
        signature.scaleToFit(115, 100)
        document.add(signature)
      }
      if (secondFacilitator.signature) {
        val imageData = Await.result(Person.downloadFromCloud(secondFacilitator.id.get),
          5 seconds)
        val signature = Image.getInstance(imageData, true)
        signature.setAbsolutePosition(665, 185)
        signature.scaleToFit(115, 100)
        document.add(signature)
      }
    } else {
      val facilitator = facilitators.head
      val name = new Phrase(facilitator.fullName, font)
      ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, name, 650, 165, 0)
      if (facilitator.signature) {
        val imageData = Await.result(Person.downloadFromCloud(facilitator.id.get),
          5 seconds)
        val signature = Image.getInstance(imageData, true)
        signature.scaleToFit(155, 100)
        signature.setAbsolutePosition(570, 185)
        document.add(signature)
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
  private def template(brandId: Long, event: Event, twoFacilitators: Boolean): Image = {
    val templates = CertificateTemplateService.get.findByBrand(brandId)
    val data = templates.find(_.language == event.language.spoken) map { tpl ⇒
      if (twoFacilitators) tpl.twoFacilitators else tpl.oneFacilitator
    } getOrElse {
      templates.find(_.language == "EN") map { tpl ⇒
        if (twoFacilitators) tpl.twoFacilitators else tpl.oneFacilitator
      } getOrElse Array[Byte]()
    }
    Image.getInstance(data)
  }
}

object Certificate {

  def cacheId(id: String): String = "certificate." + id
  def fileName(id: String): String = id + ".pdf"
  def fullFileName(id: String): String = "certificates/" + fileName(id)

  def removeFromCloud(id: String) {
    Cache.remove(Certificate.cacheId(id))
    S3Bucket.remove(Certificate.fullFileName(id))
  }

  def downloadFromCloud(id: String): Future[Array[Byte]] = {
    val contentType = "application/pdf"
    val result = S3Bucket.get(Certificate.fullFileName(id))
    val pdf: Future[Array[Byte]] = result.map {
      case BucketFile(name, contentType, content, acl, headers) ⇒ content
    }.recover {
      case S3Exception(status, code, message, originalXml) ⇒ Array[Byte]()
    }
    pdf.map {
      case value ⇒
        Cache.set(Certificate.cacheId(id), value)
        value
    }
  }
}
