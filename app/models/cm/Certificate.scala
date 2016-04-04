/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2016, Happy Melly http://www.happymelly.com
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

import models.cm.event.Attendee
import models.repository.IRepositories
import models.{Brand, File}
import org.joda.time.LocalDate
import play.api.i18n.Messages

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

case class CertificateFile(name: String, file: File) {

  def path: String = {
    val tmpFile = java.io.File.createTempFile("cert", ".pdf")
    (new java.io.FileOutputStream(tmpFile)).write(file.data)
    tmpFile.getPath
  }
}

/**
 * An certificate which a participant gets after event
 */
case class Certificate(issued: Option[LocalDate], event: Event, attendee: Attendee)(implicit messages: Messages) {

  val id = event.schedule.start.toString("yyMM") + f"${attendee.id.get}%03d"

  def file(name: String, repos: IRepositories): Option[CertificateFile] = {
    val facilitators = event.facilitators(repos)
    val cofacilitator = if (facilitators.length > 1) true else false
    val img = template(event, cofacilitator, repos)
    val data = event.brandId match {
      case 1 => Some(CertificateRenderer.m30Certificate(issued, id, event, attendee, facilitators, img))
      case 14 => Some(CertificateRenderer.lcmCertificate(event, attendee, facilitators, img))
      case _ => None
    }
    data.map(v => CertificateFile(name, Certificate.file(id).copy(data = v)))
  }

  /**
   * Get a raw certificate template
   *
   * @param event Event
   * @param twoFacilitators Shows if the event was facilitated by one or more facilitators
   * @return
   */
  private def template(event: Event, twoFacilitators: Boolean, repos: IRepositories): com.itextpdf.text.Image = {
    val templates = Await.result(repos.cm.certificate.findByBrand(event.brandId), 3.seconds)
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

  /**
    * Returns generated certificate name based on brand name
    *
    * @param brand Brand
    */
  def name(brand: Brand): String = {
    val brandName = brand.code match {
      case "LCM" ⇒ "lean-change-management-"
      case "MGT30" ⇒ "management-3-0-"
      case _ ⇒ ""
    }
    "your-%scertificate-%s.pdf".format(brandName, LocalDate.now().toString)
  }

  def cacheId(id: String): String = "certificate." + id

  def fileName(id: String): String = id + ".pdf"

  def fullFileName(id: String): String = "certificates/" + fileName(id)

  def file(id: String): File = File.pdf(Certificate.fullFileName(id), Certificate.cacheId(id))
}
