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

import models.database.{ Evaluations }
import org.joda.time.{ DateTime, LocalDate }
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.Play.current
import play.api.libs.Crypto
import scala.util.Random
import services.EmailService

/**
 * A status of an evaluation which a participant gives to an event
 */
object EvaluationStatus extends Enumeration {
  val Pending = Value("1")
  val Approved = Value("2")
  val Rejected = Value("3")
}

/**
 * An evaluation which a participant gives to an event
 */
case class Evaluation(
  id: Option[Long],
  eventId: Long,
  participantId: Option[Long],
  question1: String,
  question2: String,
  question3: String,
  question4: String,
  question5: String,
  question6: Int,
  question7: Int,
  question8: String,
  status: EvaluationStatus.Value,
  handled: Option[LocalDate],
  certificate: Option[String],
  created: DateTime,
  createdBy: String,
  updated: DateTime,
  updatedBy: String) {

  lazy val event: Event = Event.find(eventId).get

  lazy val participant: Person = Person.find(participantId.get).get

  def insert: Evaluation = DB.withSession { implicit session: Session ⇒
    val id = Evaluations.forInsert.insert(this)
    this.copy(id = Some(id))
  }

  def delete(): Unit = Evaluation.delete(this.id.get)

  def update: Evaluation = DB.withSession { implicit session: Session ⇒
    val updateTuple = (eventId, participantId, question1, question2, question3, question4, question5,
      question6, question7, question8, status, handled, certificate, updated, updatedBy)
    val updateQuery = Evaluations.filter(_.id === this.id).map(_.forUpdate)
    updateQuery.update(updateTuple)
    this
  }

  def approve(approver: Person): Evaluation = {
    import java.io.File

    val evaluation = this.copy(status = EvaluationStatus.Approved).copy(handled = Some(LocalDate.now)).update
    evaluation.generateCertificate
    val attachment = new File("test.pdf")
    val name = "your-management-3-0-certificate-" + LocalDate.now().toString + ".pdf"
    val brand = Brand.find(event.brandCode).get
    val recipients = participant :: brand.coordinator :: event.facilitators
    val subject = s"Your ${brand.brand.name} certificate"
    EmailService.send(recipients.toSet, subject,
      mail.html.approved(brand.brand, participant, approver).toString, true, Some((attachment, name)))

    evaluation
  }

  def reject(): Evaluation = this.copy(status = EvaluationStatus.Rejected).copy(handled = Some(LocalDate.now)).update

  /** Generate a Management 3.0 certificate (the only one supported right now) */
  def generateCertificate() {
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

    import java.io.FileOutputStream
    import play.api._

    val document = new Document(PageSize.A4.rotate);
    val baseFont = BaseFont.createFont("reports/MGT30/DejaVuSerif.ttf",
      BaseFont.IDENTITY_H, BaseFont.EMBEDDED)

    val writer = PdfWriter.getInstance(document, new FileOutputStream("test.pdf"))
    document.open
    val facilitators = event.facilitators
    val cofacilitator = if (facilitators.length > 1) true else false
    val imagePath = if (cofacilitator) "cert-body-new-co.png" else "cert-body-new.png"
    val img = Image.getInstance(Play.application.resource("reports/MGT30/" + imagePath).get)
    img.setAbsolutePosition(7, 10)
    img.scalePercent(55)
    document.add(img)

    val font = new Font(baseFont, 20)
    font.setColor(0, 181, 228)

    val name = new Phrase(participant.fullName, font)
    val title = new Phrase(event.title, font)
    val dateString = event.schedule.start.toString("d + ") + event.schedule.end.toString("d MMMM yyyy")
    val eventDate = new Phrase(dateString, font)
    val location = new Phrase(event.location.city + ", " + Messages("country." + event.location.countryCode), font)
    val date = new Phrase(handled.map(_.toString("dd MMMM yyyy")).getOrElse(""), font)
    val participantId = participant.id.get
    val generatedId = handled.map(_.toString("yyMM")).getOrElse("") + f"$participantId%03d"
    val certificateId = new Phrase(generatedId, font)

    val canvas = writer.getDirectContent()
    canvas.saveState
    ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, name, PageSize.A4.getHeight / 2, 450, 0)
    ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, title, PageSize.A4.getHeight / 2, 340, 0)
    font.setSize(12)
    font.setColor(150, 150, 150)
    ColumnText.showTextAligned(canvas, Element.ALIGN_RIGHT, certificateId, 560, 490, 0)
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
  }
}

object Evaluation {

  def findByEventAndPerson(participantId: Long, eventId: Long) = DB.withSession { implicit session: Session ⇒
    Query(Evaluations).filter(_.participantId === participantId).filter(_.eventId === eventId).firstOption
  }

  def findByEvent(eventId: Long): List[Evaluation] = DB.withSession { implicit session: Session ⇒
    Query(Evaluations).filter(_.eventId === eventId).list
  }

  def find(id: Long) = DB.withSession { implicit session: Session ⇒
    Query(Evaluations).filter(_.id === id).firstOption
  }

  def findAll: List[Evaluation] = DB.withSession { implicit session: Session ⇒
    Query(Evaluations).sortBy(_.created).list
  }

  def delete(id: Long): Unit = DB.withSession { implicit session: Session ⇒
    Evaluations.where(_.id === id).mutate(_.delete())
  }

}
