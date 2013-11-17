/*
 * Happy Melly Teller
 * Copyright (C) 2013, Happy Melly http://www.happymelly.com
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

import models.database.{ Contributions }
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB.withSession
import play.api.Play.current

/**
 * A contribution - an impact made by a person or an organisation to
 *   a development of a Product
 */
case class Contribution(
  id: Option[Long],
  contributorId: Long,
  productId: Long,
  isPerson: Boolean,
  role: String) {

  def product: Product = withSession { implicit session ⇒
    Product.find(this.productId).get
  }

  def insert: Contribution = withSession { implicit session ⇒
    val id = Contributions.forInsert.insert(this)
    this.copy(id = Some(id))
  }

}

case class PersonContributionView(person: Person, contribution: Contribution)

case class OrganisationContributionView(organisation: Organisation, contribution: Contribution)

case class ContributionView(product: Product, contribution: Contribution)

object Contribution {

  /**
   * Returns a list of all contributions for the given contributor.
   */
  def contributions(contributorId: Long): List[ContributionView] = withSession { implicit session ⇒

    val query = for {
      contribution ← Contributions if contribution.contributorId === contributorId
      product ← contribution.product
    } yield (contribution, product)

    query.sortBy(_._2.title.toLowerCase).list.map {
      case (contribution, product) ⇒ ContributionView(product, contribution)
    }
  }

  /**
   * Finds a contribution by ID.
   */
  def find(id: Long): Option[Contribution] = withSession { implicit session ⇒
    Query(Contributions).filter(_.id === id).firstOption
  }

  def delete(id: Long): Unit = withSession { implicit session ⇒
    Contributions.filter(_.id === id).mutate(_.delete)
  }

  // /**
  //  * Returns a blanks license with default values, for the given licensee, for editing.
  //  */
  // def blank(personId: Long) = {
  //   License(None, 0, personId, "", LocalDate.now, LocalDate.now, LocalDate.now.plusYears(1), false,
  //     Money.zero(CurrencyUnit.EUR), Some(Money.zero(CurrencyUnit.EUR)))
  // }

  // /**
  //  * Finds a license by ID, joined with brand and licensee.
  //  */
  // def findWithBrandAndLicensee(id: Long): Option[LicenseLicenseeBrandView] = withSession { implicit session ⇒
  //   val query = for {
  //     license ← Licenses if license.id === id
  //     brand ← license.brand
  //     licensee ← license.licensee
  //   } yield (license, brand, licensee)

  //   query.mapResult {
  //     case (license, brand, licensee) ⇒
  //       LicenseLicenseeBrandView(license, brand, licensee)
  //   }.firstOption
  // }

  // /**
  //  * Returns the start date for a current license for the given licensee and brand.
  //  * If there are multiple current licenses, the earliest start date is returned.
  //  *
  //  * Start dates for previous licenses that join up with current licenses are not found. For example, if there is a
  //  * license for 1 January to 31 December every year, this function returns 1 January of this year, not the first year.
  //  */
  // def licensedSince(licenseeId: Long, brandCode: String): Option[LocalDate] = withSession { implicit session ⇒
  //   val today = LocalDate.now()
  //   val query = for {
  //     license ← Licenses if license.start <= today && license.end >= today
  //     brand ← license.brand if brand.code === brandCode
  //     licensee ← license.licensee if licensee.id === licenseeId
  //   } yield license.start

  //   Query(query.min).first
  // }

  // def insert(license: License) = withSession { implicit session ⇒
  //   val id = Licenses.forInsert.insert(license)
  //   license.copy(id = Some(id))
  // }

  // /** Finds a licensee by license ID **/
  // def licensee(licenseId: Long): Option[Person] = withSession { implicit session ⇒
  //   val query = for {
  //     license ← Licenses if license.id === licenseId
  //     licensee ← license.licensee
  //   } yield licensee
  //   query.firstOption
  // }

  // /**
  //  * Returns a list of people who are licensed for the given brand on the given date, usually today.
  //  */
  // def licensees(brandCode: String, date: LocalDate = LocalDate.now()): List[Person] = withSession { implicit session ⇒
  //   val query = for {
  //     license ← Licenses if license.start <= date && license.end >= date
  //     brand ← license.brand if brand.code === brandCode
  //     licensee ← license.licensee
  //   } yield licensee
  //   query.sortBy(_.lastName.toLowerCase).list
  // }

  // /**
  //  * Returns a list of content licenses for the given person.
  //  */
  // def licenses(personId: Long): List[LicenseView] = withSession { implicit session ⇒

  //   val query = for {
  //     license ← Licenses if license.licenseeId === personId
  //     brand ← license.brand
  //   } yield (license, brand)

  //   query.sortBy(_._2.name.toLowerCase).list.map {
  //     case (license, brand) ⇒ LicenseView(brand, license)
  //   }
  // }

  // /**
  //  * Updates this license in the database.
  //  */
  // def update(license: License): Unit = withSession { implicit session ⇒
  //   license.id.map { id ⇒
  //     Query(Licenses).filter(_.id === id).update(license)
  //   }
  // }

}
