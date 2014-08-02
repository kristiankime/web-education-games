package models.user.table

import models.support.UserId
import models.user.UserSetting
import play.api.db.slick.Config.driver.simple._
import service.table.UsersTable
import models.support._

import scala.slick.model.ForeignKeyAction

class UserSettingsTable(tag: Tag) extends Table[UserSetting](tag, "application_user_settings") {
  def userId = column[UserId]("user_id", O.PrimaryKey)
  def name = column[String]("name")
  def consented = column[Boolean]("consented")
  def seenHelp = column[Boolean]("seen_help")
  def allowAutoMatch = column[Boolean]("allow_auto_match")
  def emailGameUpdates = column[Boolean]("email_game_updates")

  def * = (userId, consented, name, allowAutoMatch, seenHelp, emailGameUpdates) <> (UserSetting.tupled, UserSetting.unapply _)

  def userFK = foreignKey("application_user_settings_user_fk", userId, UsersTable.userTable)(_.id, onDelete = ForeignKeyAction.Cascade)

  def nameIndex = index("application_user_settings", name, unique = true)
}

