package models.user

import models.support.UserId
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._

case class Friend(userId: UserId, friendId: UserId, requestDate: DateTime, acceptDate: Option[DateTime]) {

  def user(implicit session: Session) = Users(userId).get

  def friend(implicit session: Session) = Users(friendId).get


}
