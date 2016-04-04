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
package models

case class ScaledImageCopy(file: File, width: Int)

/**
 * Represents an image scaled to several sizes
 */
case class ScaledImage(name: String, cacheKey: String) {

  val files = List(
    ScaledImageCopy(File.image(name + "_icon", cacheKey + "_icon"), 64),
    ScaledImageCopy(File.image(name + "_thumbnail", cacheKey + "_thumbnail"), 160),
    ScaledImageCopy(File.image(name + "_small", cacheKey + "_small"), 300),
    ScaledImageCopy(File.image(name, cacheKey), 500),
    ScaledImageCopy(File.image(name + "_large", cacheKey + "_large"), 1024))

  /**
   * Returns file copy by its size or thumbnail if the size doesn't exist
   * @param size Name of size
   */
  def file(size: String): File = {
    val token = if (size.nonEmpty)
      name + "_" + size
    else
      name
    files.find(_.file.name == token).map(_.file).getOrElse(files.head.file)
  }

  def remove() {
    files.foreach(f => f.file.remove())
  }
}
