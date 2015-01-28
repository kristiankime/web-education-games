package models.support

import models.user.{User}
import service.{Access}
import play.api.db.slick.Config.driver.simple._

trait HasAccess {

  def access(implicit user: User, session: Session) : Access

}
