package models.user

import models.support._
import play.api.db.slick.Config.driver.simple._
import service.table.UsersTable

object Users {
  def apply(id: UserId)(implicit session: Session) = UsersTable.userTable.where(_.id === id).firstOption
}
