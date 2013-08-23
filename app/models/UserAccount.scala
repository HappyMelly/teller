package models

import be.objectify.deadbolt.core.models.{ Permission, Subject }
import models.database.UserAccounts
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB.withSession
import play.api.Play.current
import play.libs.Scala

/**
 * A log-in user account.
 */
case class UserAccount(id: Option[Long], personId: Long, twitterHandle: String, role: String) extends Subject {

  /**
   * Returns a string list of role names.
   */
  def getRoles: java.util.List[UserRole] = {
    val accountRole = UserAccount.findRole(personId).map(role ⇒ UserRole(role))
    accountRole.map(_.list).getOrElse(java.util.Collections.emptyList())
  }

  def getIdentifier = personId.toString
  def getPermissions: java.util.List[Permission] = Scala.asJava(List.empty[Permission])
}

object UserAccount {

  def delete(personId: Long) = withSession { implicit session ⇒
    UserAccounts.where(_.personId === personId).mutate(_.delete)
  }

  /**
   * Returns the account for the person with the given Twitter handle.
   */
  def findByTwitterHandle(twitterHandle: String): Option[UserAccount] = withSession { implicit session ⇒
    val query = for {
      account ← UserAccounts if account.twitterHandle.toLowerCase === twitterHandle.toLowerCase
    } yield account
    query.firstOption
  }

  def insert(account: UserAccount) = withSession { implicit session ⇒
    val id = UserAccounts.forInsert.insert(account)
    account.copy(id = Some(id))
  }

  /**
   * Returns the given person’s role.
   */
  def findRole(personId: Long): Option[UserRole.Role.Role] = withSession { implicit session ⇒
    val query = for {
      account ← UserAccounts if account.personId === personId
    } yield account.role
    query.firstOption.map(role ⇒ UserRole.Role.withName(role))
  }

  /**
   * Returns the given person’s role.
   */
  def findRoleByTwitterHandle(twitterHandle: String): Option[UserRole] = withSession { implicit session ⇒
    val query = for {
      account ← UserAccounts
      person ← account.person if person.twitterHandle === twitterHandle
    } yield account.role
    query.firstOption.map(role ⇒ UserRole.forName(role))
  }

  /**
   * Updates the user’s role.
   */
  def updateRole(personId: Long, role: String): Unit = withSession { implicit session ⇒
    val query = for {
      account ← UserAccounts if account.personId === personId
    } yield account.role
    query.update(role)
  }
}