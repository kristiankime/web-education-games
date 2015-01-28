package models.support

import models.user.{UserFull, UserSetting}
import service.{Access}
import play.api.db.slick.Config.driver.simple._

trait HasAccess {

  def access(implicit user: UserSetting, session: Session) : Access

}
