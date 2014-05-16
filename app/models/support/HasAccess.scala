package models.support

import service.{Access, User}
import scala.slick.session.Session

trait HasAccess {

  def access(implicit user: User, session: Session) : Access

}
