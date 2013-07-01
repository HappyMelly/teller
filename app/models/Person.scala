package models

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB.withSession
import play.api.Play.current
import scala.slick.lifted.Query

/**
 * A person, such as the owner or employee of an organisation.
 */
case class Person(id: Option[Long], firstName: String, lastName: String, countryCode: String, active: Boolean = true)

object Person {

  def findAll: List[Person] = withSession { implicit session ⇒
    Query(People).sortBy(_.lastName.toLowerCase).list
  }
}

object People extends Table[Person]("PERSON") {

  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def firstName = column[String]("FIRST_NAME")
  def lastName = column[String]("LAST_NAME")
  def countryCode = column[String]("COUNTRY_CODE")
  def active = column[Boolean]("ACTIVE")

  def * = id.? ~ firstName ~ lastName ~ countryCode ~ active <> (Person.apply _, Person.unapply _)

  def forInsert = * returning id
}