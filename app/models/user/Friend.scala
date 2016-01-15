package models.user

import models.support.UserId
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._

case class Friend(userId: UserId, friendId: UserId, requestDate: Option[DateTime], acceptDate: Option[DateTime]) {

  def user(implicit session: Session) = Users(userId).get

  def friend(implicit session: Session) = Users(friendId).get

  def otherId(user: User) : UserId = otherId(user.id);

  def otherId(id: UserId) : UserId =
    id match  {
      case `userId` => friendId
      case `friendId` => userId
      case _ => throw new IllegalArgumentException("Id ")
    }

}
