package models.user.table

import models.support.{UserId, _}
import models.user.User
import play.api.db.slick.Config.driver.simple._
import service.table.LoginsTable

import scala.slick.model.ForeignKeyAction

class UserSettingsTable(tag: Tag) extends Table[User](tag, "application_user_settings") {
  def userId = column[UserId]("user_id", O.PrimaryKey)
  def consented = column[Boolean]("consented")
  def name = column[String]("name")
  def allowAutoMatch = column[Boolean]("allow_auto_match")
  def seenHelp = column[Boolean]("seen_help")
  def emailGameUpdates = column[Boolean]("email_game_updates")

  def * = (userId, consented, name, allowAutoMatch, seenHelp, emailGameUpdates) <> (User.tupled, User.unapply _)

  def userFK = foreignKey("application_user_settings__user_fk", userId, LoginsTable.loginTable)(_.id, onDelete = ForeignKeyAction.Cascade)

//  def nameIndex = index("application_user_settings__name_index", name, unique = true)
  def nameIndex = index("application_user_settings__name_index", name)
}

