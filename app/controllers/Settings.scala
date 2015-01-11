package controllers

import com.google.common.annotations.VisibleForTesting
import controllers.Home._
import controllers.support.SecureSocialDB
import models.user.{UserSetting, UserSettings}
import play.api.mvc.Controller
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import service.User
import scala.util.Failure
import play.api.db.slick.Config.driver.simple.Session

object Settings extends Controller with SecureSocialDB {

  def toggleGameEmails() = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    val settings = (UserSettings(user.id) match {
      case Some(setting) => UserSettings.update(setting.copy(emailGameUpdates = !setting.emailGameUpdates))
      case None => throw new IllegalStateException("Attempted to toggle email updates for [" + user.id + "] but user had no settings")
    })
    Ok(views.html.user.userInfo())
  }

}