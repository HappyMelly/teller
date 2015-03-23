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

package controllers.admin

import controllers.Security
import models.UserRole.Role._
import models._
import models.admin._
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages
import play.api.mvc._
import views.Languages

object Translations extends Controller with Security {

  /**
   * HTML form mapping for creating and editing translations
   *
   * @return
   */
  def translationForm = Form(mapping(
    "language" -> nonEmptyText.verifying("error.language.unknown", (lang: String) ⇒ Languages.all.get(lang).nonEmpty),
    "questions" -> mapping(
      "language" -> nonEmptyText,
      "question1" -> nonEmptyText,
      "question2" -> nonEmptyText,
      "question3" -> nonEmptyText,
      "question4" -> nonEmptyText,
      "question5" -> nonEmptyText,
      "question6" -> nonEmptyText,
      "question7" -> nonEmptyText,
      "question8" -> nonEmptyText)(EvaluationQuestion.apply)(EvaluationQuestion.unapply),
    "recommendations" -> mapping(
      "language" -> nonEmptyText,
      "score0" -> nonEmptyText,
      "score1" -> nonEmptyText,
      "score2" -> nonEmptyText,
      "score3" -> nonEmptyText,
      "score4" -> nonEmptyText,
      "score5" -> nonEmptyText,
      "score6" -> nonEmptyText,
      "score7" -> nonEmptyText,
      "score8" -> nonEmptyText,
      "score9" -> nonEmptyText,
      "score10" -> nonEmptyText)(EvaluationRecommendation.apply)(EvaluationRecommendation.unapply),
    "impressions" -> mapping(
      "language" -> nonEmptyText,
      "score0" -> nonEmptyText,
      "score1" -> nonEmptyText,
      "score2" -> nonEmptyText,
      "score3" -> nonEmptyText,
      "score4" -> nonEmptyText,
      "score5" -> nonEmptyText,
      "score6" -> nonEmptyText,
      "score7" -> nonEmptyText,
      "score8" -> nonEmptyText,
      "score9" -> nonEmptyText,
      "score10" -> nonEmptyText)(EvaluationImpression.apply)(EvaluationImpression.unapply))(Translation.apply)(Translation.unapply))

  /** Shows all translation **/
  def index = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      val translations = Translation.findAll
      Ok(views.html.translation.index(user, translations))
  }

  /**
   * Add page
   * @param lang Two-letter unique language identifier
   * @return
   */
  def add(lang: String) = SecuredRestrictedAction(Editor) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        val en = Translation.find("EN")
        en.map { value ⇒
          Ok(views.html.translation.form(user, None, lang, value, translationForm))
        }.getOrElse(InternalServerError)
  }

  /**
   * Add form submits to this action
   *
   * @param lang Two-letters unique language identifier
   * @return
   */
  def create(lang: String) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      val form: Form[Translation] = translationForm.bindFromRequest
      form.fold(
        formWithErrors ⇒ {
          val en = Translation.find("EN")
          en.map { value ⇒
            BadRequest(views.html.translation.form(user, None, lang, value, formWithErrors))
          }.getOrElse(InternalServerError)
        },
        translation ⇒ {
          Translation.find(lang).map { v ⇒
            val en = Translation.find("EN")
            en.map { value ⇒
              BadRequest(views.html.translation.form(user, None, lang, value, form))
                .flashing("error" -> Messages("error.translation.exist"))
            }.getOrElse(InternalServerError)
          }.getOrElse {
            translation.create

            val activity = Activity.insert(user.fullName, Activity.Predicate.Created, "new translation")
            Redirect(routes.Translations.index()).flashing("success" -> activity.toString)
          }

        })
  }

  /**
   * Details page
   *
   * @param lang Two-letter unique language identifier
   * @return
   */
  def details(lang: String) = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      Translation.find(lang).map { translation ⇒
        Translation.find("EN").map { en ⇒
          Ok(views.html.translation.details(user, translation, en))
        }.getOrElse(InternalServerError)
      }.getOrElse(NotFound)

  }

  /**
   * Edit page
   *
   * @param lang Two-letter unique language identifier
   * @return
   */
  def edit(lang: String) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      Translation.find(lang).map { translation ⇒
        Translation.find("EN").map { en ⇒
          Ok(views.html.translation.form(user, Some(translation), lang, en, translationForm.fill(translation)))
        }.getOrElse(InternalServerError)
      }.getOrElse(NotFound)

  }

  /**
   * Edit form submits to this action
   *
   * @param lang Two-letters unique language identifier
   * @return
   */
  def update(lang: String) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      Translation.find(lang).map { existingTranslation ⇒
        if (existingTranslation.changeable) {
          val form: Form[Translation] = translationForm.bindFromRequest
          form.fold(
            formWithErrors ⇒ {
              val en = Translation.find("EN")
              en.map { value ⇒
                BadRequest(views.html.translation.form(user, Some(existingTranslation), lang, value, formWithErrors))
              }.getOrElse(InternalServerError)
            },
            translation ⇒ {
              translation.update
              val activity = Activity.insert(user.fullName, Activity.Predicate.Updated, "translation")
              Redirect(routes.Translations.index()).flashing("success" -> activity.toString)
            })
        } else {
          Redirect(routes.Translations.index()).flashing("error" -> Messages("error.translation.notChangeable"))
        }
      }.getOrElse(NotFound)
  }

  /**
   * Delete a translation
   *
   * @param lang Two-letters unique language identifier
   * @return
   */
  def delete(lang: String) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      Translation.find(lang).map { translation ⇒
        if (translation.changeable) {
          translation.delete()
          val activity = Activity.insert(user.fullName, Activity.Predicate.Deleted, "translation")
          Redirect(routes.Translations.index()).flashing("success" -> activity.toString)
        } else {
          Redirect(routes.Translations.index()).flashing("error" -> Messages("error.translation.notChangeable"))
        }
      }.getOrElse(NotFound)
  }
}
