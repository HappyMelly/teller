package models.unit

import models.PasswordIdentity
import org.specs2.mutable.Specification
import securesocial.core.AuthenticationMethod

/**
  * Unit tests for UserIdentity model
  */
class UserIdentitySpec extends Specification {

  "Full name" should {
    "be empty if first or last names are empty" in {
      val identity = PasswordIdentity(None, "test@test.com", "111", None, None, "")
      identity.fullName must_== ""
    }
    "contains both first and last names if they are not empty" in {
      val identity = PasswordIdentity(None, "t@t.com", "111", Some("Britney"), Some("Sparrow"), "")
      identity.fullName must_== "Britney Sparrow"
    }
  }

  "Profile" should {
    "be rightly filled with data from the identity object" in {
      val identity = PasswordIdentity(None, "t@t.com", "111", Some("Sam"), Some("Branson"), "hasher")
      identity.profile.authMethod must_== AuthenticationMethod.UserPassword
      identity.profile.avatarUrl must_== None
      identity.profile.email must_== Some("t@t.com")
      identity.profile.fullName must_== Some(identity.fullName)
      identity.profile.userId must_== "t@t.com"
      identity.profile.passwordInfo map { info =>
        info.password must_== "111"
        info.hasher must_== "hasher"
      } getOrElse ko
    }
  }
}
