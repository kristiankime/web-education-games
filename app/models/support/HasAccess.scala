package models.support

import service.{HasUserId, Access, Login$}
import play.api.db.slick.Config.driver.simple._

trait HasAccess {

  def access(implicit user: HasUserId, session: Session): Access

}
