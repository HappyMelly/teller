/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2015, Happy Melly http://www.happymelly.com
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
 * If you have questions concerning this license or the applicable additional
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */

package models.integration

import integration.PlayAppSpec
import models.brand.CertificateTemplate
import models.service.brand.CertificateTemplateService

class CertificateTemplateServiceSpec extends PlayAppSpec {

  override def setupDb(): Unit = {
    Seq(
      CertificateTemplate(None, 1L, "EN", Array[Byte](), Array[Byte]()),
      CertificateTemplate(None, 1L, "RU", Array[Byte](), Array[Byte]()),
      CertificateTemplate(None, 1L, "DE", Array[Byte](), Array[Byte]()),
      CertificateTemplate(None, 3L, "EN", Array[Byte](), Array[Byte]())).foreach(x ⇒ x.insert)
  }

  val service = new CertificateTemplateService

  "The service " should {
    "all templates for the brand TEST, available in database" in {
      service.findByBrand(1L).length must_== 3
    }
    "return no templates for the brand TWO as there's no templates in database" in {
      service.findByBrand(2L).length must_== 0
    }
  }
}
