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

package controllers.apiv2

import models.{ ContributionView, ContributorView }
import play.api.libs.json._

object ContributionsApi {

  import ProductsApi.productWrites

  implicit val contributionWrites = new Writes[ContributionView] {
    def writes(contribution: ContributionView) = Json.obj(
      "product" -> contribution.product,
      "role" -> contribution.contribution.role)
  }

  implicit val contributorWrites = new Writes[ContributorView] {
    def writes(contributor: ContributorView) = Json.obj(
      "id" -> contributor.id,
      "name" -> contributor.name,
      "unique_name" -> contributor.uniqueName,
      "photo" -> contributor.photo,
      "role" -> contributor.contribution.role)
  }
}
