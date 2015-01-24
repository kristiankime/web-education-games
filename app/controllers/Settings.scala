package controllers

import controllers.support.SecureSocialConsented
import models.user.{UserSetting, UserSettings}
import play.api.mvc.Controller
import play.api.db.slick.Config.driver.simple.Session

object Settings extends Controller with SecureSocialConsented {

  def toggleGameEmails() = ConsentedAction { implicit request => implicit user => implicit session =>
    val settings = (UserSettings(user.id) match {
      case Some(setting) => UserSettings.update(setting.copy(emailGameUpdates = !setting.emailGameUpdates))
      case None => throw new IllegalStateException("Attempted to toggle email updates for [" + user.id + "] but user had no settings")
    })
    Ok(views.html.user.userInfo())
  }

}