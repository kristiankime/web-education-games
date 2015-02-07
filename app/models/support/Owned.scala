package models.support

import play.api.db.slick.Config.driver.simple.Session
import service.table.LoginsTable

trait Owned {

  def ownerId : UserId

  def owner(implicit s: Session) = LoginsTable.findById(ownerId).get

}
