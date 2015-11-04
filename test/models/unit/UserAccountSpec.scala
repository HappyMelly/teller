package models.unit

import models.UserAccount
import org.specs2.mutable.Specification
import scala.collection.JavaConversions._

/**
 * Tests for user account model
 */
class UserAccountSpec extends Specification {

  "User" should {
    "have only Unregistered role if he is not registed" in {
      val account = UserAccount(None, 1, "viewer", None, None, None, None,
        isCoordinator = false, isFacilitator = true, admin = true,
        member = true, registered = false)
      account.getRoles.size() must_== 1
      account.getRoles.get(0).unregistered must_== true
    }
    "have Viewer and Member roles if she is a registered member" in {
      val account = UserAccount(None, 1, "viewer", None, None, None, None,
        isCoordinator = false, isFacilitator = true, admin = false,
        member = true, registered = true)
      account.getRoles.size() must_== 2
      account.getRoles.toList.exists(_.viewer) must_== true
      account.getRoles.toList.exists(_.member) must_== true
    }
    "have Viewer, Member and Admin roles if she is a registered member and admin" in {
      val account = UserAccount(None, 1, "viewer", None, None, None, None,
        isCoordinator = false, isFacilitator = true, admin = true,
        member = true, registered = true)
      account.getRoles.size() must_== 3
      account.getRoles.toList.exists(_.viewer) must_== true
      account.getRoles.toList.exists(_.member) must_== true
      account.getRoles.toList.exists(_.admin) must_== true
    }
  }
}
