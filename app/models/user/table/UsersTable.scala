package models.user.table

import models.support._
import org.joda.time.DateTime
import models.user.User
import play.api.db.slick.Config.driver.simple._
import service.table.LoginsTable
import com.artclod.slick.JodaUTC._

import scala.slick.model.ForeignKeyAction

class UsersTable(tag: Tag) extends Table[User](tag, "application_users") {
  def userId = column[UserId]("user_id", O.PrimaryKey)
  def consented = column[Boolean]("consented")
  def name = column[String]("name")
  def allowAutoMatch = column[Boolean]("allow_auto_match")
  def seenHelp = column[Boolean]("seen_help")
  def emailUpdates = column[Boolean]("email_updates")
  def lastAccess = column[DateTime]("last_access")

  def * = (userId, consented, name, allowAutoMatch, seenHelp, emailUpdates, lastAccess) <> (User.tupled, User.unapply _)

  def userFK = foreignKey("application_users__user_fk", userId, LoginsTable.loginTable)(_.id, onDelete = ForeignKeyAction.Cascade)

  def nameIndex = index("application_users__name_index", name)
}

