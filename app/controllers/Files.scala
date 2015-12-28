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
package controllers

import fly.play.s3.BucketFile
import models.{ File, Image }
import play.api.Play.current
import play.api.cache.Cache
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc._
import scala.concurrent.Future
import scala.io.Source
import services.S3Bucket

class FileNotExist(msg: String) extends RuntimeException(msg)

trait Files extends Controller {

  /**
   * Retrieves the given file from Amazon cloud and loads it to cache
   *
   * @param file File
   */
  protected def file(file: File) = Action.async {
    val cached = Cache.getAs[Array[Byte]](file.cacheKey)
    if (cached.isDefined) {
      Future.successful(Ok(cached.get).as(file.fileType))
    } else {
      file.uploadToCache() map {
        case value ⇒ Ok(value).as(file.fileType)
      }
    }
  }

  /**
   * Uploads file to Amazon cloud
   *
   * @param file File object
   * @param fieldName Name of a file field on the form
   */
  protected def uploadFile(file: File, fieldName: String)(
    implicit request: Request[AnyContent]): Future[Any] = {
    request.body.asMultipartFormData.map { data ⇒
      data.file(fieldName).map { picture ⇒
        val encoding = "ISO-8859-1"
        val source = Source.fromFile(picture.ref.file.getPath, encoding)
        val byteArray = source.toArray.map(_.toByte)
        source.close()
        S3Bucket.add(BucketFile(file.name, file.fileType, byteArray)).map { unit ⇒
          Cache.remove(file.cacheKey)
          true
        }.recover {
          case _ ⇒ Future.failed(new RuntimeException("File cannot be temporary saved"))
        }
      } getOrElse Future.failed(new FileNotExist("File field does not exist"))
    } getOrElse Future.failed(new RuntimeException("Please choose a file"))
  }


  /**
   * Uploads picture to Amazon cloud
   *
   * @param image File object
   * @param fieldName Name of a file field on the form
   */
  protected def uploadImage(image: Image, fieldName: String)(
    implicit request: Request[AnyContent]): Future[Any] = {
    request.body.asMultipartFormData.map { data ⇒
      data.file(fieldName).map { picture ⇒
        val source = com.sksamuel.scrimage.Image.fromFile(picture.ref.file)
        val buckets = image.files.map { f =>
          val height = if (source.ratio == 0.0)
            0
          else
            (1 / source.ratio * f.width).toInt
          val bytes = if (f.width > 0)
            source.fit(f.width, height).bytes
          else
            source.bytes
          S3Bucket.add(BucketFile(f.file.name, f.file.fileType, bytes))
        }
        Future.sequence(buckets).map { unit =>
          image.files.foreach(f => Cache.remove(f.file.cacheKey))
          true
        }.recover {
          case _ ⇒ Future.failed(new RuntimeException("File cannot be temporary saved"))
        }
      } getOrElse Future.failed(new FileNotExist("Please choose a file"))
    } getOrElse Future.failed(new FileNotExist("Please choose a file"))
  }
}