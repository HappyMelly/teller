package controllers

import models.repository.Repositories
import models.{ActiveUser, PasswordIdentity, Person}
import play.api.mvc.Controller
import securesocial.controllers.BasePasswordReset
import securesocial.core.providers.MailToken

import scala.util.Random

/**
  * Contains a set of methods for setting up new user account with access by email
  */
trait PasswordIdentities extends BasePasswordReset with AsyncController {

  val services: Repositories

  /**
    * Creates dummy password and adds all required records to resetting password for newly created account
    *
    * @param person Person
    * @param token Token
    */
  protected def setupLoginByEmailEnvironment(person: Person, token: MailToken): Unit = {
    val dummyPassword = env.currentHasher.hash(Random.nextFloat().toString)
    val identity = PasswordIdentity(person.id,
      person.email,
      dummyPassword.password,
      Some(person.firstName),
      Some(person.lastName), dummyPassword.hasher)
    services.identity.insert(identity)
    env.userService.saveToken(token)
  }

}
