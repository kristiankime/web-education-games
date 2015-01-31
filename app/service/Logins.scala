package service

import models.support._
import play.api.db.slick.Config.driver.simple._
import service.table.LoginsTable

object Logins {

  def apply(id: UserId)(implicit session: Session) = LoginsTable.loginTable.where(_.id === id).firstOption

}
