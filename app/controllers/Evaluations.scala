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
import org.joda.time._
import play.api.data._
import play.api.data.Forms._
import play.api.Play.current
import services.EmailService

object Evaluations extends EvaluationsController with Security {

  /** HTML form mapping for creating and editing. */
  def evaluationForm(userName: String, edit: Boolean = false) = Form(mapping(
    "id" -> ignored(Option.empty[Long]),
    "eventId" -> longNumber.verifying(
      "An event doesn't exist", (eventId: Long) ⇒ Event.find(eventId).isDefined),
    "participantId" -> {
      if (edit) of(participantIdOnEditFormatter) else of(participantIdFormatter)
    },
    "question1" -> nonEmptyText,
    "question2" -> nonEmptyText,
    "question3" -> nonEmptyText,
    "question4" -> nonEmptyText,
    "question5" -> nonEmptyText,
    "question6" -> number(min = 0, max = 10),
    "question7" -> number(min = 0, max = 10),
    "question8" -> nonEmptyText,
    "status" -> statusMapping,
    "handled" -> optional(jodaLocalDate),
    "certificate" -> optional(nonEmptyText),
    "created" -> ignored(DateTime.now),
    "createdBy" -> ignored(userName),
    "updated" -> ignored(DateTime.now),
    "updatedBy" -> ignored(userName))(Evaluation.apply)(Evaluation.unapply))

  /**
   * Show add page
   *
   * @param eventId Optional unique event identifier to create evaluation for
   * @param participantId Optional unique person identifier to create evaluation for
   * @return
   */
  def add(eventId: Option[Long], participantId: Option[Long]) = SecuredDynamicAction("evaluation", "add") {
    implicit request ⇒
      implicit handler ⇒
        val account = request.user.asInstanceOf[LoginIdentity].userAccount
        val events = findEvents(account)
        val en = Translation.find("EN").get
        Ok(views.html.evaluation.form(request.user, None, evaluationForm(request.user.fullName), events, eventId, participantId, en))
  }

  /**
   * Add form submits to this action
   * @return
   */
  def create = SecuredDynamicAction("evaluation", "add") { implicit request ⇒
    implicit handler ⇒

      val form: Form[Evaluation] = evaluationForm(request.user.fullName).bindFromRequest
      form.fold(
        formWithErrors ⇒ {
          val account = request.user.asInstanceOf[LoginIdentity].userAccount
          val events = findEvents(account)
          val en = Translation.find("EN").get
          BadRequest(views.html.evaluation.form(request.user, None, formWithErrors, events, None, None, en))
        },
        evaluation ⇒ {
          evaluation.create

          val activity = Activity.insert(request.user.fullName, Activity.Predicate.Created, "new evaluation")
          Redirect(routes.Participants.index()).flashing("success" -> activity.toString)
        })
  }

  /**
   * Delete an evaluation
   * @param id Unique evaluation identifier
   * @return
   */
  def delete(id: Long) = SecuredDynamicAction("evaluation", "manage") { implicit request ⇒
    implicit handler ⇒

      Evaluation.find(id).map {
        evaluation ⇒
          evaluation.delete()
          val activity = Activity.insert(request.user.fullName, Activity.Predicate.Deleted, "evaluation")
          Redirect(routes.Participants.index()).flashing("success" -> activity.toString)
      }.getOrElse(NotFound)
  }

  /**
   * Move an evaluation to another event
   * @param id Unique evaluation identifier
   * @return
   */
  def move(id: Long) = SecuredDynamicAction("evaluation", "manage") { implicit request ⇒
    implicit handler ⇒

      Evaluation.find(id).map { evaluation ⇒
        val form = Form(single(
          "eventId" -> longNumber))
        val (eventId) = form.bindFromRequest.get
        if (eventId == evaluation.eventId) {
          val activity = Activity.insert(request.user.fullName, Activity.Predicate.Updated, "evaluation")
          Redirect(routes.Participants.index()).flashing("success" -> activity.toString)
        } else {
          Event.find(eventId).map { event ⇒
            Participant.find(evaluation.personId, evaluation.eventId).map { oldParticipant ⇒
              // first we need to check if this event has already this participant
              Participant.find(evaluation.personId, eventId).map { participant ⇒
                // if yes, we reassign an evaluation
                participant.copy(evaluationId = Some(id)).update
                oldParticipant.copy(evaluationId = None).update
              }.getOrElse {
                // if no, we move a participant
                oldParticipant.copy(eventId = eventId).update
              }
              evaluation.copy(eventId = eventId).update
              val activity = Activity.insert(request.user.fullName, Activity.Predicate.Updated, "evaluation")
              Redirect(routes.Participants.index()).flashing("success" -> activity.toString)
            }.getOrElse(NotFound)
          }.getOrElse(NotFound)
        }
      }.getOrElse(NotFound)
  }

  /** Details page **/
  def details(id: Long) = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒

      Evaluation.find(id).map { evaluation ⇒
        val brand = Brand.find(evaluation.event.brandCode).get
        val en = Translation.find("EN").get
        Ok(views.html.evaluation.details(request.user, evaluation, en, brand.brand))
      }.getOrElse(NotFound)

  }

  /** Edit page **/
  def edit(id: Long) = SecuredDynamicAction("evaluation", "edit") { implicit request ⇒
    implicit handler ⇒

      Evaluation.find(id).map { evaluation ⇒
        val account = request.user.asInstanceOf[LoginIdentity].userAccount
        val events = findEvents(account)
        val en = Translation.find("EN").get

        Ok(views.html.evaluation.form(request.user, Some(evaluation),
          evaluationForm(request.user.fullName).fill(evaluation), events, None, None, en))
      }.getOrElse(NotFound)

  }

  /** Edit form submits to this action **/
  def update(id: Long) = SecuredDynamicAction("evaluation", "edit") { implicit request ⇒
    implicit handler ⇒

      Evaluation.find(id).map { existingEvaluation ⇒
        val form: Form[Evaluation] = evaluationForm(request.user.fullName, edit = true).bindFromRequest
        form.fold(
          formWithErrors ⇒ {
            val account = request.user.asInstanceOf[LoginIdentity].userAccount
            val events = findEvents(account)
            val en = Translation.find("EN").get

            BadRequest(views.html.evaluation.form(request.user, Some(existingEvaluation), form, events, None, None, en))
          },
          evaluation ⇒ {
            evaluation.copy(id = Some(id)).update
            val activity = Activity.insert(request.user.fullName, Activity.Predicate.Updated, "evaluation")
            Redirect(routes.Participants.index()).flashing("success" -> activity.toString)
          })
      }.getOrElse(NotFound)
  }

  /** Approve form submits to this action **/
  def approve(id: Long) = SecuredDynamicAction("evaluation", "manage") { implicit request ⇒
    implicit handler ⇒
      Evaluation.find(id).map { ev ⇒

        val approver = request.user.asInstanceOf[LoginIdentity].userAccount.person.get
        ev.approve(approver)

        val activity = Activity.insert(request.user.fullName, Activity.Predicate.Approved,
          ev.participant.fullName)
        Redirect(routes.Participants.index()).flashing("success" -> activity.toString)
      }.getOrElse(NotFound)
  }

  /** Reject form submits to this action **/
  def reject(id: Long) = SecuredDynamicAction("evaluation", "manage") { implicit request ⇒
    implicit handler ⇒
      Evaluation.find(id).map { existingEvaluation ⇒
        existingEvaluation.reject()
        val activity = Activity.insert(request.user.fullName, Activity.Predicate.Rejected,
          existingEvaluation.participant.fullName)

        val facilitator = request.user.asInstanceOf[LoginIdentity].userAccount.person.get
        val brand = Brand.find(existingEvaluation.event.brandCode).get
        val participant = existingEvaluation.participant
        val subject = s"Your ${brand.brand.name} certificate"
        EmailService.send(Set(participant),
          Some(existingEvaluation.event.facilitators.toSet),
          Some(Set(brand.coordinator)), subject,
          mail.html.rejected(brand.brand, participant, facilitator).toString(), richMessage = true)

        Redirect(routes.Participants.index()).flashing("success" -> activity.toString)
      }.getOrElse(NotFound)
  }

  /**
   * Generate a XLSX report with evaluations
   *
   * @param brandCode filter events by a brand
   * @param eventId a selected event
   * @param status  filter events by their statuses
   * @param byMe only the events where the user is a facilitator will be retrieved
   * @return
   */
  def export(brandCode: String, eventId: Long, status: Int, byMe: Boolean) = SecuredRestrictedAction(Viewer) {
    implicit request ⇒
      implicit handler ⇒
        Brand.find(brandCode).map { brand ⇒
          val account = request.user.asInstanceOf[LoginIdentity].userAccount
          val events = if (eventId > 0) {
            Event.find(eventId).map { event ⇒
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
              Event.findByFacilitator(account.personId, brandCode)
            } else {
              Event.findByParameters(brandCode)
            }
          }
          val evaluations = Evaluation.findByEvents(events.map(e ⇒ e.id.get))
          val filteredEvaluations = if (status >= 0) {
            evaluations.filter(e ⇒ e._3.status.id == status)
          } else {
            evaluations
          }
          val date = LocalDate.now.toString
          Ok.sendFile(
            content = createXLSXreport(filteredEvaluations),
            fileName = _ ⇒ s"report-$date-$brandCode.xlsx")
        }.getOrElse(NotFound("Unknown brand"))
  }

  /**
   * Create XSLX report file for a set of evaluations
   * @param evaluations A set of evaluations to be included into the report
   * @return
   */
  private def createXLSXreport(evaluations: List[(Event, Person, Evaluation)]): java.io.File = {
    import org.apache.poi.ss.util._
    import org.apache.poi.xssf.usermodel._
    import play.api._

    val wb = new XSSFWorkbook(Play.application.resourceAsStream("reports/evaluations.xlsx").get)
    val sheet = wb.getSheetAt(0)
    var rowNumber = 0
    evaluations.foreach { e ⇒
      rowNumber += 1
      val row = sheet.createRow(rowNumber)
      row.createCell(0).setCellValue(e._1.title)
      row.createCell(1).setCellValue(e._1.schedule.start + " / " + e._1.schedule.end)
      row.createCell(2).setCellValue(e._1.location.city)
      row.createCell(3).setCellValue(e._2.fullName)
      row.createCell(4).setCellValue(e._3.question6)
      row.createCell(5).setCellValue(e._3.question7)
      row.createCell(6).setCellValue(e._3.question1)
      row.createCell(7).setCellValue(e._3.question2)
      row.createCell(8).setCellValue(e._3.question3)
      row.createCell(9).setCellValue(e._3.question4)
      row.createCell(10).setCellValue(e._3.question5)
      row.createCell(11).setCellValue(e._3.question8)
    }
    val tmpFile = File.createTempFile("report", ".xlsx")
    tmpFile.deleteOnExit()
    val os = new FileOutputStream(tmpFile)
    wb.write(os)
    os.close()
    tmpFile
  }

  private def findEvents(account: UserAccount): List[Event] = {
    if (account.editor) {
      Event.findActive
    } else {
      Event.findByCoordinator(account.personId)
    }
  }
}
