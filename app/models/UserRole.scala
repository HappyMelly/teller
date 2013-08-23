package models

import be.objectify.deadbolt.core.models.Role
import play.libs.Scala
import scala.collection.mutable.ListBuffer

/**
 *
 */
case class UserRole(role: UserRole.Role.Role) extends Role {
  def getName: String = role.toString

  import UserRole.Role._

  def admin: Boolean = role == Admin
  def editor: Boolean = role == Editor || admin
  def viewer: Boolean = role == Viewer || editor

  /**
   * Returns the list of rules implied by this role.
   */
  def list: java.util.List[UserRole] = {
    val roles = ListBuffer[UserRole]()
    if (viewer) roles += UserRole(Viewer)
    if (editor) roles += UserRole(Editor)
    if (admin) roles += UserRole(Admin)
    Scala.asJava(roles)
  }
}

object UserRole {

  object Role extends Enumeration {
    type Role = Value
    val Viewer = Value("viewer")
    val Editor = Value("editor")
    val Admin = Value("admin")
  }

  def forName(name: String): UserRole = UserRole(Role.withName(name))
}
