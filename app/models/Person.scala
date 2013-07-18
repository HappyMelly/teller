package models

import com.github.tototoshi.slick.JodaSupport._
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB.withSession
import play.api.Play.current
import scala.slick.lifted.Query
import org.joda.time.DateTime
import play.api.db.slick.DB

case class Address(street1: Option[String], street2: Option[String], city: Option[String], province: Option[String],
  postCode: Option[String], countryCode: String)

/**
 * A person, such as the owner or employee of an organisation.
 */
case class Person(
  id: Option[Long],
  firstName: String,
  lastName: String,
  emailAddress: String,
  address: Address,
  bio: Option[String],
  interests: Option[String],
  twitterHandle: Option[String],
  facebookUrl: Option[String],
  linkedInUrl: Option[String],
  googlePlusUrl: Option[String],
  boardMember: Boolean = false,
  stakeholder: Boolean = true,
  active: Boolean = true,
  created: DateTime = DateTime.now(),
  createdBy: String) {

  def fullName: String = firstName + " " + lastName

  /**
   * Returns a list of the organisations this person is a member of.
   */
  def membership: List[Organisation] = withSession { implicit session ⇒
    val query = for {
      membership ← OrganisationMemberships if membership.personId === this.id
      organisation ← membership.organisation
    } yield organisation
    query.sortBy(_.name.toLowerCase).list
  }

  /**
   * Inserts or updates this person into the database.
   * @return The Person as it is saved (with the ID added if this was an insert)
   */
  def save: Person = DB.withSession { implicit session ⇒
    if (id.isDefined) { // Update
      Query(People).filter(_.id === id).update(this)
      this
    } else { // Insert
      val id = People.forInsert.insert(this)
      this.copy(id = Some(id))
    }
  }
}

object Person {

  /**
   * Activates the organisation, if the parameter is true, or deactivates it.
   */
  def activate(id: Long, active: Boolean): Unit = withSession { implicit session ⇒
    val query = for {
      person ← People if person.id === id
    } yield person.active
    query.update(active)
  }

  def delete(id: Long) {
    withSession { implicit session ⇒
      People.where(_.id === id).delete
    }
  }

  def find(id: Long): Option[Person] = withSession { implicit session ⇒
    Query(People).filter(_.id === id).list.headOption
  }

  def findAll: List[Person] = withSession { implicit session ⇒
    Query(People).sortBy(_.lastName.toLowerCase).list
  }

}

object People extends Table[Person]("PERSON") {

  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def firstName = column[String]("FIRST_NAME")
  def lastName = column[String]("LAST_NAME")
  def emailAddress = column[String]("EMAIL_ADDRESS")

  def street1 = column[Option[String]]("STREET_1")
  def street2 = column[Option[String]]("STREET_2")
  def city = column[Option[String]]("CITY")
  def province = column[Option[String]]("PROVINCE")
  def postCode = column[Option[String]]("POST_CODE")
  def countryCode = column[String]("COUNTRY_CODE")

  def bio = column[Option[String]]("BIO", O.DBType("TEXT"))
  def interests = column[Option[String]]("INTERESTS", O.DBType("TEXT"))

  def twitterHandle = column[Option[String]]("TWITTER_HANDLE")
  def facebookUrl = column[Option[String]]("FACEBOOK_URL")
  def linkedInUrl = column[Option[String]]("LINKEDIN_URL")
  def googlePlusUrl = column[Option[String]]("GOOGLE_PLUS_URL")
  def boardMember = column[Boolean]("BOARD_MEMBER")
  def stakeholder = column[Boolean]("STAKEHOLDER")

  def active = column[Boolean]("ACTIVE")
  def created = column[DateTime]("CREATED")
  def createdBy = column[String]("CREATED_BY")

  def * = id.? ~ firstName ~ lastName ~ emailAddress ~ street1 ~ street2 ~ city ~ province ~ postCode ~ countryCode ~ bio ~ interests ~
    twitterHandle ~ facebookUrl ~ linkedInUrl ~ googlePlusUrl ~ boardMember ~ stakeholder ~ active ~ created ~ createdBy <> (
      { p ⇒ Person(p._1, p._2, p._3, p._4, Address(p._5, p._6, p._7, p._8, p._9, p._10), p._11, p._12, p._13, p._14, p._15, p._16, p._17, p._18, p._19, p._20, p._21) },
      { (p: Person) ⇒
        Some((p.id, p.firstName, p.lastName, p.emailAddress, p.address.street1, p.address.street2, p.address.city,
          p.address.province, p.address.postCode, p.address.countryCode, p.bio, p.interests, p.twitterHandle, p.facebookUrl, p.linkedInUrl,
          p.googlePlusUrl, p.boardMember, p.stakeholder, p.active, p.created, p.createdBy))
      })

  def forInsert = * returning id
}
