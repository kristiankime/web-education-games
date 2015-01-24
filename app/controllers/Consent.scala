package controllers

import com.google.common.annotations.VisibleForTesting
import controllers.support.SecureSocialDB
import models.user.{UserSetting, UserSettings}
import play.api.mvc.Controller
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import service.User
import scala.util.Failure
import play.api.db.slick.Config.driver.simple.Session

object Consent extends Controller with SecureSocialDB {
  val splitEmailOnAt = """([^@]+)@([^@]+)""".r

  def consent(goTo: Option[String], error : Option[String]) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    Ok(views.html.user.consent(goTo, error))
  }

  def noConsent() = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    Ok(views.html.user.noConsent())
  }

  def consentSubmit(goTo: Option[String]) = SecuredUserDBAction("TODO REMOVE ME WHEN INTELLIJ 14 CAN PARSE WITHOUT THIS") { implicit request => implicit user => implicit session =>
    ConsentForm.values.bindFromRequest.fold(
      errors => {
        Logger("consent").info("error" + errors)
        BadRequest(views.html.errors.formErrorPage(errors))
      },
      consented => {

        val settings = (UserSettings(user.id) match {
          case Some(setting) => UserSettings.update(setting.copy(consented = consented))
          case None => UserSettings.create(UserSetting(userId = user.id, consented = consented, name = defaultName(user), allowAutoMatch = true, seenHelp = false, emailGameUpdates = true))
        })

        (settings, consented, goTo) match {
          case (Failure(_), _, _) => Redirect(routes.Consent.consent(goTo, Some("Sorry a system error occured please try again")))
          case (_, false, _) => Redirect(routes.Consent.noConsent())
          case (_, true, Some(path)) => Redirect(path)
          case (_, true, None) => Redirect(routes.Home.index())
        }

      })
  }

  private def defaultName(user: User)(implicit session: Session) : String = "Player" // UserSettings.validName(startingName(user))

  @VisibleForTesting
  def startingName(user: User) =
    user.email match {
      case Some(splitEmailOnAt(before, after)) => before
      case _ => user.fullName
    }

  def revokeConsent() = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    val settings = (UserSettings(user.id) match {
      case Some(setting) => UserSettings.update(setting.copy(consented = false))
      case None => throw new IllegalStateException("Attempted to revoke consent for [" + user.id + "] but user had no settings")
    })
    Ok(views.html.user.noConsent())
  }

}

object ConsentForm {
  val agree = "agree"

  val values = Form(agree -> boolean)
}
