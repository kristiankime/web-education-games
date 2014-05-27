package models.support

import service.table.UsersTable
import play.api.db.slick.Config.driver.simple.Session

trait Owned {

  def ownerId : UserId

  def owner(implicit s: Session) = UsersTable.findById(ownerId).get

}
