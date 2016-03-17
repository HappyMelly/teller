package controllers.api.json

import controllers.Brands
import controllers.hm.Products
import models.{Brand, Product, ProductCategory, ProductView}
import play.api.i18n.Messages
import play.api.libs.json.{JsValue, Json, Writes}

/**
  * Converts product to JSON
  */
class ProductConverter(implicit val messages: Messages) {

  implicit val contributorWrites = (new ContributorConverter).contributorWrites

  implicit val brandWrites = new Writes[Brand] {
    def writes(brand: Brand): JsValue = {
      Json.obj(
        "code" -> brand.code,
        "unique_name" -> brand.uniqueName,
        "name" -> brand.name,
        "image" -> Brands.pictureUrl(brand),
        "tagline" -> brand.tagLine)
    }
  }

  val productWrites: Writes[Product] = new Writes[Product] {
    override def writes(obj: Product): JsValue = {
      Json.obj(
        "id" -> obj.id,
        "title" -> obj.title,
        "subtitle" -> obj.subtitle,
        "image" -> Products.pictureUrl(obj),
        "category" -> category(obj.category))
    }
  }

  implicit val productWithBrandsWrites = new Writes[ProductView] {
    def writes(obj: ProductView): JsValue = {
      Json.obj(
        "id" -> obj.product.id,
        "title" -> obj.product.title,
        "subtitle" -> obj.product.subtitle,
        "image" -> Products.pictureUrl(obj.product),
        "brands" -> obj.brands,
        "category" -> category(obj.product.category)
      )
    }
  }

  val productDetailsWrites = new Writes[ProductView] {
    def writes(obj: ProductView): JsValue = {
      Json.obj(
        "id" -> obj.product.id,
        "title" -> obj.product.title,
        "subtitle" -> obj.product.subtitle,
        "url" -> obj.product.url,
        "description" -> obj.product.description,
        "cta_url" -> obj.product.callToActionUrl,
        "cta_text" -> obj.product.callToActionText,
        "image" -> Products.pictureUrl(obj.product),
        "brands" -> obj.brands,
        "category" -> category(obj.product.category),
        "parent" -> obj.product.parentId,
        "contributors" -> obj.contributors)
    }
  }

  private def category(value: Option[ProductCategory.Value]): String =
    value.map(name ⇒ Messages(s"models.ProductCategory.$name")).getOrElse("")
}
