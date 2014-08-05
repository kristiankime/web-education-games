package models.user

import play.api.db.slick.Config.driver.simple._
import models.support.UserId
import models.user.table._
import scala.util.Try

case class UserSetting(userId: UserId, consented: Boolean = true, name: String, allowAutoMatch: Boolean = true, seenHelp: Boolean = false, emailGameUpdates: Boolean = true)

object UserSettings {

  def create(userSetting: UserSetting)(implicit session: Session) =
    Try(session.withTransaction { userInfosTable.insert(userSetting); userSetting })

  def update(userSetting: UserSetting)(implicit session: Session) =
    Try(session.withTransaction {userInfosTable.where(_.userId === userSetting.userId).update(userSetting); userSetting })

  def apply(userId: UserId)(implicit session: Session) = userInfosTable.where(_.userId === userId).firstOption

  /**
   * Produces a name that was unique at the time that this call was made.
   * Note that this name may not unique if an update/insert is done later.
   *
   * @param startingName
   * @param session
   * @return
   */
  def validName(startingName: String)(implicit session: Session) = {
    userInfosTable.where(_.name === startingName).firstOption match {
      case None => startingName
      case Some(_) => {
        val similarNames = userInfosTable.where(_.name like (startingName + "%")).list.map(_.name).toSet

        if(!similarNames(startingName)) startingName
        else {
          var count = 0
          while (similarNames(startingName + count)) { count += 1 }
          startingName + count
        }
      }
    }
  }

}

