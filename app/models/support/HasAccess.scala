package models.support

import service.{Access, User}
import play.api.db.slick.Config.driver.simple._

trait HasAccess {

  def access(implicit user: User, session: Session): Access

}
