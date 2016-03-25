package templates

import play.api.i18n.Lang
import play.api.mvc.RequestHeader
import play.twirl.api.{Html, Txt}
import securesocial.core.{BasicProfile, RuntimeEnvironment}

/**
  * Created by sery0ga on 03/12/15.
  */
class MailTemplates(env: RuntimeEnvironment) extends securesocial.controllers.MailTemplates.Default(env) {

  override def getPasswordChangedNoticeEmail(user: BasicProfile)(implicit request: RequestHeader, lang: Lang): (Option[Txt], Option[Html]) = {
    (None, Some(mail.password.html.changeNotice(user)(request, env)))
  }

  override def getSendPasswordResetEmail(user: BasicProfile, token: String)(implicit request: RequestHeader, lang: Lang): (Option[Txt], Option[Html]) = {
    (None, Some(mail.password.html.reset(user, token)(request, env)))
  }

  override def getUnknownEmailNotice()(implicit request: RequestHeader, lang: Lang): (Option[Txt], Option[Html]) = {
    (None, Some(mail.password.html.unknownEmail()))
  }
}
