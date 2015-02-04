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

import controllers.Forms._
import models.JodaMoney._
import models.UserRole.Role._
import models._
import org.joda.money.{ CurrencyUnit, Money }
import org.joda.time.{ DateTime, LocalDate }
import play.api.mvc._
import play.api.data.Forms._
import play.api.data.Form
import play.api.i18n.Messages
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import securesocial.core.{ Identity, SecuredRequest }
import fly.play.s3.{ BUCKET_OWNER_FULL_CONTROL }
import play.api.Play
import play.api.Play.current
import java.net.URLDecoder
import scala.Some
import play.api.mvc.SimpleResult
import models.AccountSummary
import securesocial.core.SecuredRequest
import services.{ CurrencyConverter, EmailService, S3Bucket }
import models.BookingEntry.FieldChange

object BookingEntries extends Controller with Security {

  def bookingEntryForm(implicit request: SecuredRequest[_]) = Form(mapping(
    "id" -> ignored(Option.empty[Long]),
    "ownerId" -> ignored(0L),
    "bookingNumber" -> ignored(Option.empty[Int]),
    "summary" -> nonEmptyText(maxLength = 50),
    "source" -> jodaMoney().verifying("error.money.negativeOrZero", (m: Money) ⇒ m.isPositive),
    "sourcePercentage" -> number(min = 0),
    "fromId" -> nonEmptyText.transform(_.toLong, (l: Long) ⇒ l.toString).verifying("error.account.noAccess", isAccessible(request, _)),
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
  def add = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒
      val form = bookingEntryForm.fill(BookingEntry.blank)
      val currentUser = request.user.asInstanceOf[LoginIdentity].userAccount
      val (fromAccounts, toAccounts) = findFromAndToAccounts(currentUser)

      Ok(views.html.booking.form(request.user, form, fromAccounts, toAccounts, Brand.findAllWithCoordinator, TransactionType.findAll))
  }

  /**
   * Creates a booking entry from an ‘add form’ submission.
   */
  def create = AsyncSecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒

      val currentUser = request.user.asInstanceOf[LoginIdentity].userAccount
      val form = bookingEntryForm(request).bindFromRequest

      // Extracted function to handle the error case, either from validation or currency conversion failure,
      // by redisplaying the edit page with error messages.
      val handleFormWithErrors = (formWithErrors: Form[BookingEntry]) ⇒ {
        val (fromAccounts, toAccounts) = findFromAndToAccounts(currentUser)
        val brands = Brand.findAllWithCoordinator
        val transactionTypes = TransactionType.findAll
        BadRequest(views.html.booking.form(request.user, formWithErrors, fromAccounts, toAccounts, brands, transactionTypes))
      }

      form.fold(
        formWithErrors ⇒ Future.successful {
          handleFormWithErrors(formWithErrors)
        },
        entry ⇒ {
          entry.withSourceConverted.map { entry ⇒
            // Create booking entry.
            val insertedEntry = entry.copy(ownerId = currentUser.personId).insert
            val activityObject = Messages("models.BookingEntry.name", insertedEntry.bookingNumber.getOrElse(0).toString)
            val activity = Activity.insert(request.user.fullName, Activity.Predicate.Created, activityObject)
            Activity.link(insertedEntry, activity)
            sendEmailNotification(insertedEntry, List.empty, activity, entry.participants)
            sendEmailNotification(insertedEntry, List.empty, activity, Person.findActiveAdmins -- entry.participants)
            nextPageResult(form("next").value, activity.toString, form, currentUser, request.user)
          }.recover {
            case e: CurrencyConverter.NoExchangeRateException ⇒
              val formWithError = form.withGlobalError(s"On-line currency conversion failed (${e.getMessage}). Please try again.")
              handleFormWithErrors(formWithError)
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
    EmailService.get.send(recipients.filter(_.active), None, None, subject, mail.html.booking(entry, changes).toString,
      richMessage = true)
  }

  def details(bookingNumber: Int) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒
      val attachmentForm = s3Form(bookingNumber)
      BookingEntry.findByBookingNumber(bookingNumber).map { bookingEntry ⇒
        val currentUser = request.user.asInstanceOf[LoginIdentity].userAccount
        val activity = Activity.findForBookingEntry(bookingEntry.id.getOrElse(0))
        Ok(views.html.booking.details(request.user, bookingEntry, currentUser, attachmentForm, activity))
      }.getOrElse(NotFound)
  }

  /** This action exsits only so that there can be a route to `attachFile` without the `key` query parameter **/
  def s3Callback(bookingNumber: Int) = Action {
    NotImplemented
  }

  /**
   * Amazon S3 will redirect here after a successful upload.
   * @param bookingNumber the id of the BookingEntry that the file is being attached to
   * @param key The S3 object key for the uploaded file
   * @return Redirect to the booking entries’ detail page, flashing a success message
   */
  def attachFile(bookingNumber: Int, key: String) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒
      BookingEntry.findByBookingNumber(bookingNumber).map { entry ⇒
        // Update entity
        val decodedKey = URLDecoder.decode(key, "UTF-8")
        val updatedEntry = entry.copy(attachmentKey = Some(decodedKey))
        BookingEntry.update(updatedEntry)

        //Construct activity
        val activityPredicate = entry.attachmentKey.map(s ⇒ Activity.Predicate.Replaced).getOrElse(Activity.Predicate.Added)
        val activityObject = Messages("models.BookingEntry.attachment", bookingNumber.toString)
        val activity = Activity.insert(request.user.fullName, activityPredicate, activityObject)
        Activity.link(entry, activity)
        val changes = List(FieldChange("Attachment", entry.attachmentFilename.getOrElse(""), decodedKey.split("/").last))
        sendEmailNotification(updatedEntry, changes, activity, Person.findActiveAdmins)

        Redirect(routes.BookingEntries.details(bookingNumber)).flashing("success" -> activity.toString)
      }.getOrElse(NotFound)
  }

  /**
   * Removes the attachment form the booking number. Note that the actual file on S3 is not deleted.
   * @param bookingNumber the id of the BookingEntry to remove the attachment from
   * @return Redirect to the booking entries’ detail page, flashing a success message
   */
  def deleteAttachment(bookingNumber: Int) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒
      BookingEntry.findByBookingNumber(bookingNumber).map { entry ⇒
        val updatedEntry: BookingEntry = entry.copy(attachmentKey = None)
        BookingEntry.update(updatedEntry)

        val activityObject = Messages("models.BookingEntry.attachment", bookingNumber.toString)
        val activity = Activity.insert(request.user.fullName, Activity.Predicate.Deleted, activityObject)
        Activity.link(entry, activity)
        val changes = List(FieldChange("Attachment", entry.attachmentFilename.getOrElse(""), ""))
        sendEmailNotification(updatedEntry, changes, activity, Person.findActiveAdmins)

        Redirect(routes.BookingEntries.details(bookingNumber)).flashing("success" -> activity.toString)
      }.getOrElse(NotFound)
  }

  def edit(bookingNumber: Int) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒
      BookingEntry.findByBookingNumber(bookingNumber).map { bookingEntry ⇒
        if (bookingEntry.editable) {
          val form = bookingEntryForm.fill(bookingEntry)
          val currentUser = request.user.asInstanceOf[LoginIdentity].userAccount
          val (fromAccounts, toAccounts) = findFromAndToAccounts(currentUser)
          Ok(views.html.booking.form(request.user, form, fromAccounts, toAccounts, Brand.findAllWithCoordinator, TransactionType.findAll, None, Some(bookingNumber)))
        } else {
          Redirect(routes.BookingEntries.details(bookingNumber)).flashing("error" -> "Cannot edit entry with an inactive account")
        }
      }.getOrElse(NotFound)
  }

  def delete(bookingNumber: Int) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒

      BookingEntry.findByBookingNumber(bookingNumber).map { entry ⇒
        val currentUser = request.user.asInstanceOf[LoginIdentity].userAccount
        if (entry.editableBy(currentUser)) {
          entry.id.map { id ⇒
            val deletedEntry = entry.copy()
            BookingEntry.delete(id)
            val activityObject = Messages("models.BookingEntry.name", bookingNumber.toString)
            val activity = Activity.insert(request.user.fullName, Activity.Predicate.Deleted, activityObject)
            Activity.link(entry, activity)
            sendEmailNotification(deletedEntry, List.empty, activity, Person.findActiveAdmins)
            Redirect(routes.BookingEntries.index).flashing("success" -> activity.toString)
          }.getOrElse(NotFound)
        } else {
          Redirect(routes.BookingEntries.index).flashing("error" -> "Only the owner can delete a booking")
        }
      }.getOrElse(NotFound)
  }

  def index = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒
      Ok(views.html.booking.index(request.user, None, BookingEntry.findAll.map(e ⇒ (e, None))))
  }

  private def findFromAndToAccounts(user: UserAccount): (List[AccountSummary], List[AccountSummary]) = {
    val allActive: List[AccountSummary] = Account.findAllActive
    if (user.editor) {
      (allActive, allActive)
    } else {
      val person: Option[Person] = user.person
      val accessible: List[AccountSummary] = person.map(_.findAccessibleAccounts).toList.flatten
      (accessible, allActive)
    }
  }

  private def isAccessible(request: SecuredRequest[_], accountId: Long): Boolean = {
    val account = request.user.asInstanceOf[LoginIdentity].userAccount
    if (account.admin) {
      true
    } else {
      val accessibleAccountIds = account.person.map(_.findAccessibleAccounts.map(_.id)).toList.flatten
      accessibleAccountIds.contains(accountId)
    }
  }

  /**
   * Updates a booking entry.
   */
  def update(bookingNumber: Int) = AsyncSecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒

      BookingEntry.findByBookingNumber(bookingNumber).map { existingEntry ⇒
        val currentUser = request.user.asInstanceOf[LoginIdentity].userAccount
        if (existingEntry.editableBy(currentUser)) {
          val form = bookingEntryForm(request).bindFromRequest

          // Extracted function to handle the error case, either from validation or currency conversion failure,
          // by redisplaying the edit page with error messages.
          val handleFormWithErrors = (formWithErrors: Form[BookingEntry]) ⇒ {
            val (fromAccounts, toAccounts) = findFromAndToAccounts(currentUser)
            val brands = Brand.findAllWithCoordinator
            val transactionTypes = TransactionType.findAll
            BadRequest(views.html.booking.form(request.user, formWithErrors, fromAccounts, toAccounts, brands, transactionTypes, None, Some(bookingNumber)))
          }

          form.fold(
            formWithErrors ⇒ Future.successful {
              handleFormWithErrors(formWithErrors)
            },
            editedEntry ⇒ {
              // Update the entry with or without currency conversion, depending on whether the source amount changed,
              // to avoid applying a new exchange rate when only the direction changed.
              val sourceChanged = {
                editedEntry.source.abs != existingEntry.source.abs || editedEntry.fromId != existingEntry.fromId || editedEntry.toId != existingEntry.toId
              }
              val futureUpdatedEntry = if (sourceChanged) {
                editedEntry.withSourceConverted.map { editedEntry ⇒
                  val updatedEntry = editedEntry.copy(id = existingEntry.id, attachmentKey = existingEntry.attachmentKey)
                  BookingEntry.update(updatedEntry)
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
                BookingEntry.update(updatedEntry)
                Future.successful(updatedEntry)
              }

              futureUpdatedEntry.map { updatedEntry ⇒
                // Construct a fully-populated entry from the edited entry by adding the missing properties from the
                // existing entry (that are not included in edit/update), for use in the e-mail notification.
                val populatedUpdatedEntry = updatedEntry.copy(bookingNumber = existingEntry.bookingNumber,
                  ownerId = existingEntry.ownerId, fromId = existingEntry.fromId, toId = existingEntry.toId)

                val activityObject = Messages("models.BookingEntry.name", bookingNumber.toString)
                val activity = Activity.insert(request.user.fullName, Activity.Predicate.Updated, activityObject)
                Activity.link(existingEntry, activity)

                val changes = BookingEntry.compare(existingEntry, populatedUpdatedEntry)
                sendEmailNotification(populatedUpdatedEntry, changes, activity, Person.findActiveAdmins)

                nextPageResult(form("next").value, activity.toString, form, currentUser, request.user)
              }.recover {
                case e: CurrencyConverter.NoExchangeRateException ⇒
                  val formWithError = form.withGlobalError(s"On-line currency conversion failed (${e.getMessage}). Please try again.")
                  handleFormWithErrors(formWithError)
              }
            })
        } else {
          Future.successful {
            Redirect(routes.BookingEntries.details(bookingNumber)).flashing("error" -> "Editing entry not allowed")
          }
        }
      }.getOrElse(Future.successful(NotFound))
  }

  // Redirect or re-render according to which submit button was clicked.
  private def nextPageResult(
    next: Option[String],
    successMessage: String,
    form: Form[BookingEntry],
    currentUser: UserAccount,
    user: Identity)(implicit request: SecuredRequest[AnyContent],
      handler: AuthorisationHandler): SimpleResult = {

    next match {
      case Some("add") ⇒ Redirect(routes.BookingEntries.add()).flashing("success" -> successMessage)
      case Some("copy") ⇒ {
        val (fromAccounts, toAccounts) = findFromAndToAccounts(currentUser)
        val brands = Brand.findAllWithCoordinator
        val transactionTypes = TransactionType.findAll
        Ok(views.html.booking.form(user, form, fromAccounts, toAccounts, brands, transactionTypes, Some(successMessage)))
      }
      case _ ⇒ Redirect(routes.BookingEntries.index()).flashing("success" -> successMessage)
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
    import fly.play.s3.upload.Condition._
    import fly.play.s3.upload.Form
    val policy = S3Bucket.uploadPolicy(expiration = DateTime.now().plusHours(1).toDate)
      .withConditions(key startsWith attachmentKeyPath(bookingNumber),
        acl eq BUCKET_OWNER_FULL_CONTROL,
        successActionRedirect eq routes.BookingEntries.s3Callback(bookingNumber).absoluteURL())

    Form(policy)
  }

  /** Constructs the path for an attachmentKey **/
  private def attachmentKeyPath(bookingNumber: Int) = s"bookingentries/$bookingNumber"
}
