package models.support

import models.user.User
import play.api.db.slick.Config.driver.simple._
import service.Access

trait HasAccess {

  def access(implicit user: User, session: Session) : Access

}
