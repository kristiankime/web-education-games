package models.user

import models.support.UserId
import models.user.table._
import play.api.db.slick.Config.driver.simple._

import scala.util.Try

object Users {

  def nameDisplay(name:String, id: UserId) = name + "-" + id.v

  def create(userSetting: User)(implicit session: Session) =
    Try(session.withTransaction { usersTable.insert(userSetting); userSetting })

  def update(userSetting: User)(implicit session: Session) =
    Try(session.withTransaction {usersTable.where(_.userId === userSetting.id).update(userSetting); userSetting })

  def apply(userId: UserId)(implicit session: Session) : Option[User] = usersTable.where(_.userId === userId).firstOption

  /**
   * Produces a name that was unique at the time that this call was made.
   * Note that this name may not unique if an update/insert is done later.
   */
  def validName(startingName: String)(implicit session: Session) = {
    usersTable.where(_.name === startingName).firstOption match {
      case None => startingName
      case Some(_) => {
        val similarNames = usersTable.where(_.name like (startingName + "%")).list.map(_.name).toSet

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

