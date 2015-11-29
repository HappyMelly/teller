package models.unit

import models.UserAccount
import org.specs2.mutable.Specification

import scala.collection.JavaConversions._

/**
  * Tests for user account model
  */
class UserAccountSpec extends Specification {

  val raw = UserAccount.empty(1L)

  "User" should {
    "have only Unregistered role if he is not registed" in {
      val account = raw.copy(coordinator = false, facilitator = true, admin = true, member = true, registered = false)
      account.getRoles.size() must_== 1
      account.getRoles.get(0).unregistered must_== true
    }
    "have Viewer and Member roles if she is a registered member" in {
      val account = raw.copy(coordinator = false, facilitator = false, admin = false, member = true, registered = true)
      account.getRoles.size() must_== 2
      account.getRoles.toList.exists(_.viewer) must_== true
      account.getRoles.toList.exists(_.member) must_== true
    }
    "have Viewer, Member and Facilitator roles if she is a registered member and facilitator" in {
      val account = raw.copy(coordinator = false, facilitator = true, admin = false, member = true, registered = true)
      account.getRoles.size() must_== 3
      account.getRoles.toList.exists(_.viewer) must_== true
      account.getRoles.toList.exists(_.member) must_== true
      account.getRoles.toList.exists(_.facilitator) must_== true
    }
    "have Viewer and  Coordinator roles if she is a registered coordinator" in {
      val account = raw.copy(coordinator = true, facilitator = false, admin = false, member = false, registered = true)
      account.getRoles.size() must_== 2
      account.getRoles.toList.exists(_.viewer) must_== true
      account.getRoles.toList.exists(_.member) must_== false
      account.getRoles.toList.exists(_.coordinator) must_== true
    }
  }
}
