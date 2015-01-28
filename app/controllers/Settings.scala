package controllers

import controllers.support.SecureSocialConsented
import models.user.UserSettings
import play.api.data._
import play.api.data.Forms._
import play.api.mvc.Controller

object Settings extends Controller with SecureSocialConsented {

  def toggleGameEmails() = ConsentedAction { implicit request => implicit user => implicit session =>
    UserSettings.update(user.copy(emailGameUpdates = !user.emailGameUpdates))
    Ok(views.html.user.userInfo())
  }

  private def ifSomeUpdate[C, O](copy : C, option : Option[O])(f : (C, O) => C) = if(option.isEmpty) { copy } else { f(copy, option.get) }

  def updateSettings() = ConsentedAction { implicit request => implicit user => implicit session =>
    SettingsForm.values.bindFromRequest.fold(
      errors => {
        BadRequest(views.html.user.userInfo(Some(errors)))
      },
      updates => {
        var settings = user.copy(emailGameUpdates = updates.emailGameUpdates)
        settings = ifSomeUpdate(settings, updates.name){ (s, v) => s.copy(name = v.trim) }
        UserSettings.update(settings)
        Redirect(routes.Home.userInfo())
      })
  }

}

case class SettingsData(name: Option[String], emailGameUpdates: Boolean)

object SettingsForm {
  val name = "name"
  val emailGameUpdates = "emailGameUpdates"

  val values = Form(mapping(
      name -> optional(text.verifying("Name must not be blank", _.trim != "")),
      emailGameUpdates -> boolean
    )(SettingsData.apply)(SettingsData.unapply))

  private def validName(name: String) = { name.trim != "" }
}