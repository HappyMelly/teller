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

package controllers

import java.io.{ File, FileOutputStream }
import models._
import models.UserRole.Role._
import models.service.{ Services, EventService }
import org.joda.time._
import play.api.Play.current
import play.mvc.Controller

trait Reports extends Controller with Security with Services {

  /**
   * Generate a XLSX report with evaluations (if they're available)
   *
   * @param brandCode filter events by a brand
   * @param eventId a selected event
   * @param status  filter events by their statuses
   * @param byMe only the events where the user is a facilitator will be retrieved
   * @return
   */
  def create(brandCode: String, eventId: Long, status: Int, byMe: Boolean) = SecuredRestrictedAction(Viewer) {
    implicit request ⇒
      implicit handler ⇒
        Brand.find(brandCode).map { brand ⇒
          val account = request.user.asInstanceOf[LoginIdentity].userAccount
          val events = if (eventId > 0) {
            EventService.find(eventId).map { event ⇒
              if (byMe) {
                if (event.facilitatorIds.contains(account.personId)) {
                  event :: Nil
                } else {
                  Nil
                }
              } else {
                event :: Nil
              }
              event :: Nil
            }.getOrElse(Nil)
          } else {
            if (byMe) {
              EventService.findByFacilitator(account.personId, Some(brandCode), archived = Some(false))
            } else {
              EventService.findByParameters(Some(brandCode))
            }
          }
          val eventIds = events.map(e ⇒ e.id.get)
          val evaluations = evaluationService.findByEvents(eventIds)
          val participants = if (status >= 0) {
            // no participant without an evaluation
            evaluations.filter(e ⇒ e._3.status.id == status).map(e ⇒ (e._1, e._2, Option(e._3)))
          } else {
            // add participants without an evaluation
            val participants = Participant.findByEvents(eventIds)
            val noEvaluation = participants.filter(p ⇒ !evaluations.exists(ev ⇒ ev._1.id == p._1.id && ev._2.id == p._2.id))
            evaluations.map(e ⇒ (e._1, e._2, Option(e._3))).union(noEvaluation.map(e ⇒ (e._1, e._2, None)))
          }
          val date = LocalDate.now.toString
          Ok.sendFile(
            content = createXLSXreport(participants),
            fileName = _ ⇒ s"report-$date-$brandCode.xlsx")
        }.getOrElse(NotFound("Unknown brand"))
  }

  /**
   * Create XSLX report file for a set of evaluations
   * @param participants A set of evaluations to be included into the report
   * @return
   */
  private def createXLSXreport(participants: List[(Event, Person, Option[Evaluation])]): java.io.File = {
    import org.apache.poi.xssf.usermodel._
    import play.api._

    val wb = new XSSFWorkbook(Play.application.resourceAsStream("reports/evaluations.xlsx").get)
    val sheet = wb.getSheetAt(0)
    var rowNumber = 0
    participants.foreach { e ⇒
      rowNumber += 1
      val row = sheet.createRow(rowNumber)
      row.createCell(0).setCellValue(e._1.title)
      row.createCell(1).setCellValue(e._1.schedule.start + " / " + e._1.schedule.end)
      row.createCell(2).setCellValue(e._1.location.city)
      row.createCell(3).setCellValue(e._2.fullName)
      e._3.map { v ⇒
        row.createCell(4).setCellValue(v.question6)
        row.createCell(5).setCellValue(v.question7)
        row.createCell(6).setCellValue(v.question1)
        row.createCell(7).setCellValue(v.question2)
        row.createCell(8).setCellValue(v.question3)
        row.createCell(9).setCellValue(v.question4)
        row.createCell(10).setCellValue(v.question5)
        row.createCell(11).setCellValue(v.question8)
      }
    }
    val tmpFile = File.createTempFile("report", ".xlsx")
    tmpFile.deleteOnExit()
    val os = new FileOutputStream(tmpFile)
    wb.write(os)
    os.close()
    tmpFile
  }

}

object Reports extends Reports with Security with Services
