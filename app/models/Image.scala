package models

import play.api.Play.current
import play.api.cache.Cache
import services._

case class ScaledImageCopy(file: File, width: Int)

/**
 * Represents an image scaled to several sizes
 */
case class Image(name: String, cacheKey: String) {

  val fileType = "image/jpeg"

  val files = List(
    ScaledImageCopy(File(fileType, name + "_icone", cacheKey + "_icon"), 64),
    ScaledImageCopy(File(fileType, name + "_thumbnail", cacheKey + "_thumbnail"), 160),
    ScaledImageCopy(File(fileType, name + "_small", cacheKey + "_small"), 300),
    ScaledImageCopy(File(fileType, name, cacheKey), 500),
    ScaledImageCopy(File(fileType, name + "_large", cacheKey + "_large"), 1024))

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
    files.foreach(f => {
      Cache.remove(f.file.cacheKey)
      S3Bucket.remove(f.file.name)
    })
  }
}
