package models.support

import service.table.LoginsTable
import play.api.db.slick.Config.driver.simple.Session

trait Owned {

  def ownerId : UserId

  def owner(implicit s: Session) = LoginsTable.findById(ownerId).get

}
