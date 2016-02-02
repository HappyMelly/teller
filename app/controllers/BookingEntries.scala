/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2014, Happy Melly http://www.happymelly.com
 *
 * This file is part of the Happy Melly Teller.
 *
 * Happy Melly Teller is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Happy Melly Teller is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Happy Melly Teller.  If not, see <http://www.gnu.org/licenses/>.
 *
 * If you have questions concerning this license or the applicable additional terms, you may contact
 * by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */

package controllers

import java.net.URLDecoder

import be.objectify.deadbolt.scala.cache.HandlerCache
import controllers.Forms._
import fly.play.s3.BUCKET_OWNER_FULL_CONTROL
import models.BookingEntry.FieldChange
import models.JodaMoney._
import models.UserRole.Role._
import models.service.Services
import models.{AccountSummary, _}
import org.joda.money.{CurrencyUnit, Money}
import org.joda.time.{DateTime, LocalDate}
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{MessagesApi, I18nSupport, Messages}
import play.api.mvc.{Result, _}
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions, DeadboltHandler}
import services.integrations.Integrations
import services.{CurrencyConverter, S3Bucket, TellerRuntimeEnvironment}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class BookingEntries @javax.inject.Inject() (override implicit val env: TellerRuntimeEnvironment,
                                             val messagesApi: MessagesApi,
                                             deadbolt: DeadboltActions,
                                             handlers: HandlerCache,
                                             actionBuilder: ActionBuilders)
  extends Security(deadbolt, handlers, actionBuilder)
  with Integrations
  with Services
  with I18nSupport {

  def bookingEntryForm(implicit user: ActiveUser) = Form(mapping(
    "id" -> ignored(Option.empty[Long]),
    "ownerId" -> ignored(0L),
    "bookingNumber" -> ignored(Option.empty[Int]),
    "summary" -> nonEmptyText(maxLength = 50),
    "source" -> jodaMoney().verifying("error.money.negativeOrZero", (m: Money) ⇒ m.isPositive),
    "sourcePercentage" -> number(min = 0),
    "fromId" -> nonEmptyText.transform(_.toLong, (l: Long) ⇒ l.toString).verifying("error.account.noAccess", isAccessible(user, _)),
    "fromAmount" -> ignored(Money.zero(CurrencyUnit.EUR)),
    "toId" -> nonEmptyText.transform(_.toLong, (l: Long) ⇒ l.toString),
    "toAmount" -> ignored(Money.zero(CurrencyUnit.EUR)),
    "brandId" -> nonEmptyText.transform(_.toLong, (l: Long) ⇒ l.toString),
    "reference" -> optional(text(maxLength = 16)),
    "referenceDate" -> jodaLocalDate.verifying("error.date.future", (d: LocalDate) ⇒ d.isBefore(LocalDate.now.plusDays(1))),
    "description" -> optional(text(maxLength = 250)),
    "url" -> optional(webUrl),
    "transactionTypeId" -> optional(longNumber),
    "owes" -> boolean,
    "next" -> optional(text))(fromForm)(toForm))

  /**
   * Creates a `BookingEntry` from form data.
   */
  def fromForm(id: Option[Long], ownerId: Long, bookingNumber: Option[Int], summary: String, source: Money, sourcePercentage: Int,
    fromId: Long, fromAmount: Money, toId: Long, toAmount: Money,
    brandId: Long, reference: Option[String], referenceDate: LocalDate,
    description: Option[String], url: Option[String], transactionTypeId: Option[Long], owes: Boolean, next: Option[String]) = {

    BookingEntry(id, ownerId, LocalDate.now, bookingNumber, summary, if (owes) source else source.negated,
      sourcePercentage, fromId, fromAmount, toId, toAmount, Some(brandId), reference, referenceDate, description, url,
      transactionTypeId)
  }

  /**
   * Creates a tuple of form data from a `BookingEntry`.
   */
  def toForm(e: BookingEntry) = Some((e.id, e.ownerId, e.bookingNumber, e.summary,
    if (e.source.isNegative) e.source.multipliedBy(-1L) else e.source,
    e.sourcePercentage, e.fromId, e.fromAmount, e.toId, e.toAmount, e.brandId.getOrElse(0L), e.reference, e.referenceDate,
    e.description, e.url, e.transactionTypeId, e.source.isPositiveOrZero, None))

  /**
   * Renders the page for adding a new booking entry.
   */
  def add = AsyncSecuredRestrictedAction(Admin) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    (for {
      b <- brandService.findAllWithCoordinator
      t <- transactionTypeService.findAll
    } yield (b, t)) flatMap { case (brands, types) =>
      val form = bookingEntryForm.fill(BookingEntry.blank)
      val (fromAccounts, toAccounts) = findFromAndToAccounts(user)

      ok(views.html.booking.form(user, form, fromAccounts, toAccounts, brands, types))
    }
  }

  /**
   * Creates a booking entry from an ‘add form’ submission.
   */
  def create = AsyncSecuredRestrictedAction(Admin) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    val currentUser = user.account
    val form = bookingEntryForm(user).bindFromRequest

    // Extracted function to handle the error case, either from validation or currency conversion failure,
    // by redisplaying the edit page with error messages.
    def handleFormWithErrors(formWithErrors: Form[BookingEntry]): Future[Result] = {
      val (fromAccounts, toAccounts) = findFromAndToAccounts(user)
      (for {
        brands <- brandService.findAllWithCoordinator
        types <- transactionTypeService.findAll
      } yield (brands, types)) flatMap { case (brands, types) =>
        badRequest(views.html.booking.form(user, formWithErrors, fromAccounts, toAccounts, brands, types))
      }
    }

    form.fold(
      formWithErrors ⇒ handleFormWithErrors(formWithErrors),
      entry ⇒ {
        entry.withSourceConverted.flatMap { entry ⇒
          // Create booking entry.
          accountService.insertEntry(entry.copy(ownerId = currentUser.personId)) flatMap { insertedEntry =>
            val activityObject = Messages("models.BookingEntry.name", insertedEntry.bookingNumber.getOrElse(0).toString)
            val activity = Activity.insert(user.name, Activity.Predicate.Created, activityObject)
            activityService.link(insertedEntry, activity)
            sendEmailNotification(insertedEntry, List.empty, activity, entry.participants)
            personService.findActiveAdmins map { admins =>
              sendEmailNotification(insertedEntry, List.empty, activity, admins -- entry.participants)
            }
            nextPageResult(form("next").value, activity.toString, form, currentUser, user)
          }
        }.recover {
          case e: CurrencyConverter.NoExchangeRateException ⇒
            val formWithError = form.withGlobalError(s"On-line currency conversion failed (${e.getMessage}). Please try again.")
            import scala.concurrent.duration._
            Await.result(handleFormWithErrors(formWithError), 3.seconds)
        }
      })
  }

  /**
   * Sends an e-mail notification for a booking entry to the given recipients (active only).
   *
   * If this becomes more complex, refactor to a new `BookingEntryNotification` actor that handles notifications
   * asynchronously, delegating to concrete notifiers, such as the `EmailServiceActor`.
   */
  def sendEmailNotification(entry: BookingEntry, changes: List[BookingEntry.FieldChange], activity: Activity,
    recipients: Set[Person])(implicit request: RequestHeader): Unit = {
    val subject = s"${activity.description} - ${entry.summary}"
    email.send(recipients.filter(_.active), None, None, subject,
      mail.templates.html.booking(entry, changes).toString,
      richMessage = true)
  }

  def details(bookingNumber: Int) = AsyncSecuredRestrictedAction(Admin) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      val attachmentForm = s3Form(bookingNumber)
      bookingEntryService.findByBookingNumber(bookingNumber) flatMap {
        case None => notFound("Booking entry not found")
        case Some(bookingEntry) =>
          val currentUser = user.account
          activityService.findForBookingEntry(bookingEntry.id.getOrElse(0)) flatMap { activity =>
            ok(views.html.booking.details(user, bookingEntry, currentUser, attachmentForm, activity))
          }
      }
  }

  /** This action exsits only so that there can be a route to `attachFile` without the `key` query parameter **/
  def s3Callback(bookingNumber: Int) = Action {
    NotImplemented
  }

  /**
   * Amazon S3 will redirect here after a successful upload.
 *
   * @param bookingNumber the id of the BookingEntry that the file is being attached to
   * @param key The S3 object key for the uploaded file
   * @return Redirect to the booking entries’ detail page, flashing a success message
   */
  def attachFile(bookingNumber: Int, key: String) = AsyncSecuredRestrictedAction(Admin) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      bookingEntryService.findByBookingNumber(bookingNumber) flatMap {
        case None => notFound("Booking entry not found")
        case Some(entry) =>
          // Update entity
          val decodedKey = URLDecoder.decode(key, "UTF-8")
          val updatedEntry = entry.copy(attachmentKey = Some(decodedKey))
          bookingEntryService.update(updatedEntry)

          //Construct activity
          val activityPredicate = entry.attachmentKey.map(s ⇒ Activity.Predicate.Replaced).getOrElse(Activity.Predicate.Added)
          val activityObject = Messages("models.BookingEntry.attachment", bookingNumber.toString)
          val activity = Activity.insert(user.name, activityPredicate, activityObject)
          activityService.link(entry, activity)
          val changes = List(FieldChange("Attachment", entry.attachmentFilename.getOrElse(""), decodedKey.split("/").last))
          personService.findActiveAdmins map { admins =>
            sendEmailNotification(updatedEntry, changes, activity, admins)
          }

          redirect(routes.BookingEntries.details(bookingNumber), "success" -> activity.toString)
      }
  }

  /**
   * Removes the attachment form the booking number. Note that the actual file on S3 is not deleted.
 *
   * @param bookingNumber the id of the BookingEntry to remove the attachment from
   * @return Redirect to the booking entries’ detail page, flashing a success message
   */
  def deleteAttachment(bookingNumber: Int) = AsyncSecuredRestrictedAction(Admin) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      bookingEntryService.findByBookingNumber(bookingNumber) flatMap {
        case None => notFound("Booking entry not found")
        case Some(entry) =>
          val updatedEntry: BookingEntry = entry.copy(attachmentKey = None)
          bookingEntryService.update(updatedEntry)

          val activityObject = Messages("models.BookingEntry.attachment", bookingNumber.toString)
          val activity = Activity.insert(user.name, Activity.Predicate.Deleted, activityObject)
          activityService.link(entry, activity)
          val changes = List(FieldChange("Attachment", entry.attachmentFilename.getOrElse(""), ""))
          personService.findActiveAdmins map { admins =>
            sendEmailNotification(updatedEntry, changes, activity, admins)
          }

          redirect(routes.BookingEntries.details(bookingNumber), "success" -> activity.toString)
      }
  }

  def edit(bookingNumber: Int) = AsyncSecuredRestrictedAction(Admin) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      bookingEntryService.findByBookingNumber(bookingNumber) flatMap {
        case None => notFound("Booking entry not found")
        case Some(bookingEntry) =>
          if (bookingEntry.editable) {
            val form = bookingEntryForm.fill(bookingEntry)
            val (fromAccounts, toAccounts) = findFromAndToAccounts(user)
            (for {
              brands <- brandService.findAllWithCoordinator
              types <- transactionTypeService.findAll
            } yield (brands, types)) flatMap { case (brands, types) =>
              ok(views.html.booking.form(user, form, fromAccounts, toAccounts, brands, types, None, Some(bookingNumber)))
            }
          } else {
            val msg = "Cannot edit entry with an inactive account"
            redirect(routes.BookingEntries.details(bookingNumber), "error" -> msg)
          }
      }
  }

  def delete(bookingNumber: Int) = AsyncSecuredRestrictedAction(Admin) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      bookingEntryService.findByBookingNumber(bookingNumber) flatMap {
        case None => notFound("Booking entry not found")
        case Some(entry) =>
          val currentUser = user.account
          if (entry.editableBy(currentUser)) {
            entry.id.map { id ⇒
              val deletedEntry = entry.copy()
              bookingEntryService.delete(id)
              val activityObject = Messages("models.BookingEntry.name", bookingNumber.toString)
              val activity = Activity.insert(user.name, Activity.Predicate.Deleted, activityObject)
              activityService.link(entry, activity)
              personService.findActiveAdmins map { admins =>
                sendEmailNotification(deletedEntry, List.empty, activity, admins)
              }
              redirect(routes.BookingEntries.index(), "success" -> activity.toString)
            }.getOrElse(notFound("Entry not found"))
          } else {
            redirect(routes.BookingEntries.index(), "error" -> "Only the owner can delete a booking")
          }
      }
  }

  def index = AsyncSecuredRestrictedAction(Admin) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    bookingEntryService.findAll flatMap { entries =>
      ok(views.html.booking.index(user, None, entries.map(e ⇒ (e, None))))
    }
  }

  private def findFromAndToAccounts(user: ActiveUser): (List[AccountSummary], List[AccountSummary]) = {
    import scala.concurrent.duration._
    val allActive: List[AccountSummary] = Await.result(accountService.findAllActive, 3.seconds)
    if (user.account.admin) {
      (allActive, allActive)
    } else {
      //val accessible: List[AccountSummary] = user.person.findAccessibleAccounts
      // This is a stub
      val accessible = List()
      (accessible, allActive)
    }
  }

  private def isAccessible(user: ActiveUser, accountId: Long): Boolean = {
    if (user.account.admin) {
      true
    } else {
//      val accessibleAccountIds = user.person.findAccessibleAccounts.map(_.id)
      // This is a stub
      val accessibleAccountIds = List()
      accessibleAccountIds.contains(accountId)
    }
  }

  /**
   * Updates a booking entry.
   */
  def update(bookingNumber: Int) = AsyncSecuredRestrictedAction(Admin) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      bookingEntryService.findByBookingNumber(bookingNumber) flatMap {
        case None => notFound("Booking entry not found")
        case Some(existingEntry) =>
          val currentUser = user.account
          if (existingEntry.editableBy(currentUser)) {
            val form = bookingEntryForm(user).bindFromRequest

            // Extracted function to handle the error case, either from validation or currency conversion failure,
            // by redisplaying the edit page with error messages.
            def handleFormWithErrors(formWithErrors: Form[BookingEntry]): Future[Result] = {
              val (fromAccounts, toAccounts) = findFromAndToAccounts(user)
              (for {
                brands <- brandService.findAllWithCoordinator
                types <- transactionTypeService.findAll
              } yield (brands, types)) flatMap { case (brands, types) =>
                badRequest(views.html.booking.form(user, formWithErrors, fromAccounts, toAccounts, brands, types, None, Some(bookingNumber)))
              }
            }

            form.fold(
              formWithErrors ⇒ handleFormWithErrors(formWithErrors),
              editedEntry ⇒ {
                // Update the entry with or without currency conversion, depending on whether the source amount changed,
                // to avoid applying a new exchange rate when only the direction changed.
                val sourceChanged = {
                  editedEntry.source.abs != existingEntry.source.abs || editedEntry.fromId != existingEntry.fromId || editedEntry.toId != existingEntry.toId
                }
                val futureUpdatedEntry = if (sourceChanged) {
                  editedEntry.withSourceConverted.map { editedEntry ⇒
                    val updatedEntry = editedEntry.copy(id = existingEntry.id, attachmentKey = existingEntry.attachmentKey)
                    bookingEntryService.update(updatedEntry)
                    updatedEntry
                  }
                } else {
                  val directionChanged = editedEntry.source == existingEntry.source.multipliedBy(-1L)
                  val (fromAmount, toAmount) = if (directionChanged) {
                    (existingEntry.fromAmount.multipliedBy(-1L), existingEntry.toAmount.multipliedBy(-1L))
                  } else {
                    (existingEntry.fromAmount, existingEntry.toAmount)
                  }
                  val updatedEntry = editedEntry.copy(id = existingEntry.id, attachmentKey = existingEntry.attachmentKey,
                    fromAmount = fromAmount, toAmount = toAmount)
                  bookingEntryService.update(updatedEntry)
                  Future.successful(updatedEntry)
                }

                futureUpdatedEntry.flatMap { updatedEntry ⇒
                  // Construct a fully-populated entry from the edited entry by adding the missing properties from the
                  // existing entry (that are not included in edit/update), for use in the e-mail notification.
                  val populatedUpdatedEntry = updatedEntry.copy(bookingNumber = existingEntry.bookingNumber,
                    ownerId = existingEntry.ownerId, fromId = existingEntry.fromId, toId = existingEntry.toId)

                  val activityObject = Messages("models.BookingEntry.name", bookingNumber.toString)
                  val activity = Activity.insert(user.name, Activity.Predicate.Updated, activityObject)
                  activityService.link(existingEntry, activity)

                  val changes = BookingEntry.compare(existingEntry, populatedUpdatedEntry)
                  personService.findActiveAdmins map { admins =>
                    sendEmailNotification(populatedUpdatedEntry, changes, activity, admins)
                  }

                  nextPageResult(form("next").value, activity.toString, form, currentUser, user)
                }.recover {
                  case e: CurrencyConverter.NoExchangeRateException ⇒
                    val formWithError = form.withGlobalError(s"On-line currency conversion failed (${e.getMessage}). Please try again.")
                    Await.result(handleFormWithErrors(formWithError), 3.seconds)
                }
              })
          } else {
            Future.successful {
              Redirect(routes.BookingEntries.details(bookingNumber)).flashing("error" -> "Editing entry not allowed")
            }
          }
      }
  }

  // Redirect or re-render according to which submit button was clicked.
  private def nextPageResult(
    next: Option[String],
    successMessage: String,
    form: Form[BookingEntry],
    currentUser: UserAccount,
    user: ActiveUser)(implicit request: Request[AnyContent],
      handler: be.objectify.deadbolt.scala.DeadboltHandler): Future[Result] = {

    next match {
      case Some("add") ⇒ redirect(routes.BookingEntries.add(), "success" -> successMessage)
      case Some("copy") ⇒
        val (fromAccounts, toAccounts) = findFromAndToAccounts(user)
        (for {
          brands <- brandService.findAllWithCoordinator
          types <- transactionTypeService.findAll
        } yield (brands, types)) flatMap { case (brands, types) =>
          badRequest(views.html.booking.form(user, form, fromAccounts, toAccounts, brands, types, Some(successMessage)))
        }
      case _ ⇒ redirect(routes.BookingEntries.index(), "success" -> successMessage)
    }
  }

  /**
   * Creates an S3 form for a file attachment.
   * The form is usable for an hour, and the resulting S3 object will have the BUCKET_OWNER_FULL_CONTROL acl.
   *
   * The callback for the upload is BookingEntries.attachFile(bookingNumber, key).
   *
   * @see http://docs.aws.amazon.com/AmazonS3/latest/dev/HTTPPOSTForms.html
   * @see http://docs.aws.amazon.com/AmazonS3/latest/dev/ACLOverview.html?CannedACL
   */
  private def s3Form(bookingNumber: Int)(implicit request: RequestHeader) = {
    import fly.play.aws.policy.Condition._
    import fly.play.s3.upload.Form
    val builder = S3Bucket.uploadPolicy(expiration = DateTime.now().plusHours(1).toDate)
      .withConditions(key startsWith attachmentKeyPath(bookingNumber),
        acl eq BUCKET_OWNER_FULL_CONTROL,
        successActionRedirect eq routes.BookingEntries.s3Callback(bookingNumber).absoluteURL())

    Form(builder.toPolicy)
  }

  /** Constructs the path for an attachmentKey **/
  private def attachmentKeyPath(bookingNumber: Int) = s"bookingentries/$bookingNumber"
}
