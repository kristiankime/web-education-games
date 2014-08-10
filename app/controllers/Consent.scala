package controllers

import controllers.support.SecureSocialDB
import models.user.{UserSetting, UserSettings}
import play.api.mvc.Controller
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._

import scala.util.Failure


object Consent extends Controller with SecureSocialDB {

  def consent(goTo: Option[String], error : Option[String]) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    Ok(views.html.user.consent(goTo, error))
  }

  def noConsent() = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    Ok(views.html.user.noConsent())
  }

  def consentSubmit(goTo: Option[String]) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    ConsentForm.values.bindFromRequest.fold(
      errors => {
        Logger("consent").info("error" + errors)
        BadRequest(views.html.errors.formErrorPage(errors))
      },
      consentedForm => {

        val consented = consentedForm

        val settings = (UserSettings(user.id) match {
          case Some(setting) => UserSettings.update(setting.copy(consented = consented))
          case None => UserSettings.create(UserSetting(userId = user.id, consented = consented, name = UserSettings.validName(user.fullName), allowAutoMatch = true, seenHelp = false, emailGameUpdates = true))
        })

        (settings, consented, goTo) match {
          case (Failure(_), _, _) => Redirect(routes.Consent.consent(goTo, Some("Sorry a system error occured please try again")))
          case (_, false, _) => Redirect(routes.Consent.noConsent())
          case (_, true, Some(path)) => Redirect(path)
          case (_, true, None) => Redirect(routes.Home.index())
        }

      })
  }

}

object ConsentForm {
  val agree = "agree"

    val values = Form(agree -> boolean)
}
