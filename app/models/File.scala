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
package models

import fly.play.s3.{ BucketFile, S3Exception }
import play.api.Play.current
import play.api.cache.Cache
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent.Future
import services.S3Bucket

case class File(fileType: String,
    name: String,
    cacheKey: String) {

  def remove() {
    Cache.remove(cacheKey)
    S3Bucket.remove(name)
  }

  def uploadToCache(): Future[Array[Byte]] = {
    val result = S3Bucket.get(name)
    val file: Future[Array[Byte]] = result.map {
      case BucketFile(name, contentType, content, acl, headers) ⇒ content
    }.recover {
      case S3Exception(status, code, message, originalXml) ⇒ Array[Byte]()
    }
    file.map { value ⇒
      Cache.set(cacheKey, value)
      value
    }
  }
}

object File {

  def image(name: String, cacheKey: String): File =
    File("image/jpeg", name, cacheKey)

  def pdf(name: String, cacheKey: String): File =
    File("application/pdf", name, cacheKey)
}