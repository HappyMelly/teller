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
import models.event.Attendee
import models.service.{ Services, EventService }
import org.joda.time._
import play.api.Play.current
import play.mvc.Controller
import securesocial.core.RuntimeEnvironment

class Reports(environment: RuntimeEnvironment[ActiveUser])
    extends Controller
    with Security
    with Services {

  override implicit val env: RuntimeEnvironment[ActiveUser] = environment

  /**
   * Generate a XLSX report with evaluations (if they're available)
   *
   * @param brandId filter events by a brand
   * @param eventId a selected event
   * @param status  filter events by their statuses
   * @return
   */
  def create(brandId: Long, eventId: Long, status: Int) = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      brandService.find(brandId) map { brand ⇒
        val personId = user.account.personId
        val events =
          if (brand.ownerId == personId) {
            eventService.findByParameters(brand.id)
          } else if (License.licensedSince(personId, brand.id.get).nonEmpty) {
            eventService.findByFacilitator(personId, brand.id, archived = Some(false))
          } else
            List()
        val filteredEvents = if (eventId > 0)
          events.filter(_.identifier == eventId)
        else
          events
        val eventIds = filteredEvents.map(e ⇒ e.id.get)
        val evaluations = evaluationService.findByEventsWithAttendees(eventIds)
        val attendees = if (status >= 0) {
          // no attendee without an evaluation
          evaluations.filter(e ⇒ e._3.status.id == status).map(e ⇒ (e._1, e._2, Option(e._3)))
        } else {
          // add attendees without an evaluation
          val participants = attendeeService.findByEvents(eventIds)
          val noEvaluation = participants.filter(p ⇒ !evaluations.exists(ev ⇒ ev._1.id == p._1.id && ev._2.id == p._2.id))
          evaluations.map(e ⇒ (e._1, e._2, Option(e._3))).union(noEvaluation.map(e ⇒ (e._1, e._2, None)))
        }
        val date = LocalDate.now.toString
        Ok.sendFile(
          content = createXLSXreport(attendees),
          fileName = _ ⇒ s"report-$date-${brand.code}.xlsx")
      } getOrElse NotFound("Unknown brand")
  }

  /**
   * Create XSLX report file for a set of evaluations
   * @param attendees A set of evaluations to be included into the report
   * @return
   */
  private def createXLSXreport(attendees: List[(Event, Attendee, Option[Evaluation])]): java.io.File = {
    import org.apache.poi.xssf.usermodel._
    import play.api._

    val wb = new XSSFWorkbook(Play.application.resourceAsStream("reports/evaluations.xlsx").get)
    val sheet = wb.getSheetAt(0)
    var rowNumber = 0
    attendees.foreach { e ⇒
      rowNumber += 1
      val row = sheet.createRow(rowNumber)
      row.createCell(0).setCellValue(e._1.title)
      row.createCell(1).setCellValue(e._1.schedule.start + " / " + e._1.schedule.end)
      row.createCell(2).setCellValue(e._1.location.city)
      row.createCell(3).setCellValue(e._2.fullName)
      e._3.map { v ⇒
        row.createCell(4).setCellValue(v.facilitatorImpression)
        row.createCell(5).setCellValue(v.recommendationScore)
        row.createCell(6).setCellValue(v.reasonToRegister)
        row.createCell(7).setCellValue(v.actionItems)
        row.createCell(8).setCellValue(v.changesToContent)
        row.createCell(9).setCellValue(v.facilitatorReview)
        row.createCell(10).setCellValue(v.changesToHost)
        row.createCell(11).setCellValue(v.changesToEvent)
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
