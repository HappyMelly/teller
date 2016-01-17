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
 * If you have questions concerning this license or the applicable additional terms, you may contact
 * by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */
package controllers

import models.{BrandWithSettings, Brand, UserAccount}
import models.service.Services
import play.api.Play
import play.api.Play.current
import play.api.mvc._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait Utilities extends AsyncController with Services {

  /**
    * Returns CDN url to image if CDN is set
    *
    * @param path Path to image in Amazon S3 bucket
    */
  protected def cdnUrl(path: String): Option[String] = {
    Play.configuration.getString("cdn.url").map(url => Some(url + path)).getOrElse(None)
  }

  /**
   * Returns an url with domain
    *
    * @param url Domain-less part of url
   */
  protected def fullUrl(url: String): String = {
    Play.configuration.getString("application.baseUrl").getOrElse("") + url
  }
  
  protected def roleDiffirentiator(account: UserAccount, brandId: Option[Long] = None)
                                  (coordinator: (BrandWithSettings, List[Brand]) => Future[Result])
                                  (facilitator: (Option[BrandWithSettings], List[Brand]) => Future[Result])
                                  (ordinaryUser: Future[Result]): Future[Result] = {
    if (account.isCoordinatorNow) {
      brandService.findByCoordinator(account.personId) flatMap { brands =>
        val sorted = brands.sortBy(_.brand.name)
        brandId map { identifier =>
          sorted.find(_.brand.identifier == identifier) map { view =>
            coordinator(view, sorted.map(_.brand))
          } getOrElse redirect(routes.Dashboard.index())
        } getOrElse {
          if (sorted.nonEmpty) {
            coordinator(sorted.head, sorted.map(_.brand))
          } else redirect(routes.Dashboard.index())
        }
      }
    } else if (account.isFacilitatorNow) {
      brandService.findByLicense(account.personId) flatMap { brands =>
        brandId map { identifier =>
          brands.find(_.brand.identifier == identifier) map { view =>
            facilitator(Some(view), brands.map(_.brand))
          } getOrElse redirect(routes.Dashboard.index())
        } getOrElse {
          brands.length match {
            case 1 => facilitator(Some(brands.head), brands.map(_.brand))
            case 0 => redirect(routes.Dashboard.index())
            case _ => facilitator(None, brands.map(_.brand))
          }
        }
      }
    } else ordinaryUser
  }
}