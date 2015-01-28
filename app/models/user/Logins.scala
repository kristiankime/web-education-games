package models.user

import models.support._
import play.api.db.slick.Config.driver.simple._
import service.Login
import service.table.LoginsTable

object Logins {
  def apply(id: UserId)(implicit session: Session) = LoginsTable.userTable.where(_.id === id).firstOption

  def consented(login: Login)(implicit session: Session) = {
    val settingsOp = UserSettings(login.id)

    val consented = settingsOp match {
      case None => false
      case Some(setting) => setting.consented
    }

    consented
  }
}
