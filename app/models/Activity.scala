package models

import com.github.tototoshi.slick.JodaSupport._
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB.withSession
import play.api.Play.current
import scala.slick.lifted.Query

/**
 * An activity stream entry, with is essentially a triple of (subject, predicate, object), in the grammatical sense of the words,
 * such as (Peter, created, organisation Acme Corp).
 *
 * @param id Database primary key
 * @param subject The name of the user who performed the action
 * @param predicate The action performed from the possible `Activity.Predicate` values
 * @param activityObject The name of the data the action was
 */
case class Activity(id: Option[Long], subject: String, predicate: String, activityObject: Option[String], created: DateTime = DateTime.now())

/**
 * The possible activity stream actions, e.g. ‘deleted’.
 */
object Activity {

  object Predicate extends Enumeration {
    val SignedUp = "signup"
    val Created = "create"
    val Updated = "update"
    val Deleted = "delete"
    val Activated = "activate"
    val Deactivated = "deactivate"
  }

  /**
   * Returns all activity stream entries in reverse chronological order.
   */
  def findAll: List[Activity] = withSession { implicit session ⇒
    Query(Activities).sortBy(_.created.desc).list
  }

  /**
   * Inserts a new activity stream entry.
   */
  def insert(subject: String, predicate: String) {
    withSession { implicit session ⇒
      Activities.forInsert.insert(Activity(None, subject, predicate, None))
    }
  }

  /**
   * Inserts a new activity stream entry.
   */
  def insert(subject: String, predicate: String, activityObject: String) {
    withSession { implicit session ⇒
      Activities.forInsert.insert(Activity(None, subject, predicate, Some(activityObject)))
    }
  }
}

object Activities extends Table[Activity]("ACTIVITY") {

  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def subject = column[String]("SUBJECT")
  def predicate = column[String]("PREDICATE")
  def activityObject = column[Option[String]]("OBJECT")
  def created = column[DateTime]("CREATED")
  def * = id.? ~ subject ~ predicate ~ activityObject ~ created <> (Activity.apply _, Activity.unapply _)

  //  def forInsert = subject ~ predicate ~ activityObject ~ created <> (
  //    t ⇒ Activity(None, t._1, t._2, t._3, t._4),
  //    (a: Activity) ⇒ Some((a.subject, a.predicate, a.activityObject, a.created)))
  def forInsert = * returning id
}