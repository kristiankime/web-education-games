package models.user.table

import com.artclod.slick.JodaUTC._
import models.support._
import models.user.{Friend, User}
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import service.table.LoginsTable

import scala.slick.model.ForeignKeyAction


class FriendsTable(tag: Tag) extends Table[Friend](tag, "friends") {
  def userId = column[UserId]("user_id")
  def friendId = column[UserId]("friend_id")
  def requestDate = column[DateTime]("request_date")
  def acceptDate = column[Option[DateTime]]("accept_date")

  def * = (userId, friendId, requestDate, acceptDate) <> (Friend.tupled, Friend.unapply _)

  def tablePK = primaryKey("friends__primary_key", (userId, friendId))

  def userFK = foreignKey("friends__user_fk", userId, LoginsTable.loginTable)(_.id, onDelete = ForeignKeyAction.Cascade)
  def friendFK = foreignKey("friends__friend_fk", friendId, LoginsTable.loginTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}

