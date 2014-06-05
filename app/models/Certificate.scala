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

import fly.play.s3.{ BucketFile, S3Exception }
import org.joda.time.LocalDate
import play.api.cache.Cache
import play.api.libs.concurrent.Execution.Implicits._
import play.api.Play.current
import scala.concurrent.Future
import services.{ S3Bucket, EmailService }

/**
 * An certificate which a participant gets after an event
 */
case class Certificate(
  evaluation: Evaluation,
  oldEvaluation: Option[Evaluation] = None) {

  val id = evaluation.certificateId

  def generateAndSend(brand: BrandView, approver: Person) {
    val contentType = "application/pdf"
    val pdf = generate(evaluation)
    S3Bucket.add(BucketFile(Certificate.fullFileName(id), contentType, pdf)).map { unit ⇒
      evaluation.copy(certificate = Some(id)).update
      oldEvaluation.map(oldEv ⇒ oldEv.certificate.map(
        oldId ⇒ if (oldId != id) Certificate.removeFromCloud(oldId)))
      sendEmail(brand, approver, pdf)
    }.recover {
      case S3Exception(status, code, message, originalXml) ⇒ {}
    }
  }

  def send(brand: BrandView, approver: Person) {
    val pdf = Certificate.downloadFromCloud(evaluation.certificateId)
    pdf.map {
      case value ⇒
        sendEmail(brand, approver, value)
    }
  }

  private def sendEmail(brand: BrandView, approver: Person, data: Array[Byte]) {
    val file = java.io.File.createTempFile("cert", ".pdf")
    (new java.io.FileOutputStream(file)).write(data)
    val name = "your-management-3-0-certificate-" + LocalDate.now().toString + ".pdf"
    val body = mail.html.approved(brand.brand, evaluation.participant, approver).toString
    val subject = s"Your ${brand.brand.name} certificate"
    EmailService.send(Set(evaluation.participant),
      Some(evaluation.event.facilitators.toSet),
      Some(Set(brand.coordinator)),
      subject,
      body,
      richMessage = true,
      Some((file.getPath, name)))
  }

  /** Generate a Management 3.0 certificate (the only one supported right now) */
  private def generate(ev: Evaluation): Array[Byte] = {
    import com.itextpdf.text.Document
    import com.itextpdf.text.pdf.PdfWriter
    import com.itextpdf.text.pdf.BaseFont
    import com.itextpdf.text.Font
    import com.itextpdf.text.Element
    import com.itextpdf.text.PageSize
    import com.itextpdf.text.Phrase
    import com.itextpdf.text.pdf.ColumnText
    import com.itextpdf.text.Image
    import play.api.i18n.Messages

    import java.io.ByteArrayOutputStream
    import play.api._

    val document = new Document(PageSize.A4.rotate);
    val baseFont = BaseFont.createFont("reports/MGT30/DejaVuSerif.ttf",
      BaseFont.IDENTITY_H, BaseFont.EMBEDDED)

    val output = new ByteArrayOutputStream()
    val writer = PdfWriter.getInstance(document, output)
    document.open
    val facilitators = ev.event.facilitators
    val cofacilitator = if (facilitators.length > 1) true else false
    val imagePath = if (cofacilitator) "cert-body-new-co.png" else "cert-body-new.png"
    val img = Image.getInstance(Play.application.resource("reports/MGT30/" + imagePath).get)
    img.setAbsolutePosition(7, 10)
    img.scalePercent(55)
    document.add(img)

    val font = new Font(baseFont, 20)
    font.setColor(0, 181, 228)

    val name = new Phrase(ev.participant.fullName, font)
    val title = new Phrase(ev.event.title, font)
    val dateString = ev.event.schedule.start.toString("d + ") +
      ev.event.schedule.end.toString("d MMMM yyyy")
    val eventDate = new Phrase(dateString, font)
    val location = new Phrase(ev.event.location.city + ", " +
      Messages("country." + ev.event.location.countryCode), font)
    val date = new Phrase(ev.handled.map(_.toString("dd MMMM yyyy")).getOrElse(""), font)
    val certificateIdBlock = new Phrase(ev.certificateId, font)

    val canvas = writer.getDirectContent()
    canvas.saveState
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
      val firstName = new Phrase(facilitators.head.fullName, font)
      val secondName = new Phrase(facilitators.last.fullName, font)
      ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, firstName, 595, 165, 0)
      ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, secondName, 725, 165, 0)
    } else {
      val name = new Phrase(facilitators.head.fullName, font)
      ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, name, 650, 165, 0)
    }
    canvas.restoreState
    document.close();

    output.toByteArray
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
