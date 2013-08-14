package controllers

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import models._
import models.UserRole.Role.Admin
import play.api.Logger
import play.api.i18n.Messages

/**
 * User administration controller.
 */
object UserAccounts extends Controller with Security {

  val userForm = Form(tuple(
    "personId" -> longNumber,
    "role" -> optional(text)))

  /**
   * Updates a person’s user role.
   */
  def update = SecuredRestrictedAction(Admin) { implicit request ⇒
    implicit handler ⇒
      userForm.bindFromRequest.fold(
        form ⇒ BadRequest("invalid form data"),
        user ⇒ {
          val (personId, role) = user
          Person.find(personId).map { person ⇒
            Logger.debug(s"update role for person (${person.fullName}}) to ${role}")
            val activityObject = Messages("object.UserAccount", person.fullNamePossessive)
            val activity = role.map { newRole ⇒
              if (UserAccount.findRole(personId).isDefined) {
                UserAccount.updateRole(personId, newRole)
                Activity.insert(request.user.fullName, Activity.Predicate.Updated, activityObject)
              } else {
                UserAccount.insert(UserAccount(None, personId, newRole))
                Activity.insert(request.user.fullName, Activity.Predicate.Created, activityObject)
              }
            }.getOrElse {
              // Remove the account
              UserAccount.delete(personId)
              Activity.insert(request.user.fullName, Activity.Predicate.Deleted, activityObject)
            }
            Redirect(routes.People.details(person.id.getOrElse(0))).flashing("success" -> activity.toString)
          }.getOrElse(BadRequest("invalid form data - person not found"))
        })
  }

}