package models.user

import play.api.db.slick.Config.driver.simple._
import models.support.UserId
import models.user.table._

import scala.util.control.NonFatal
import scala.util.{Failure, Success, Try}
import play.api.Logger

case class UserSetting(userId: UserId, consented: Boolean = true, name: String, allowAutoMatch: Boolean = true, seenHelp: Boolean = false, emailGameUpdates: Boolean = true)

object UserSettings {
//  def allTry[T](r: => T): Try[T] =
//    try Success(r) catch {
//      case e : org.h2.jdbc.JdbcSQLException => Failure(e)
//    }

  def create(userSetting: UserSetting)(implicit session: Session) ={
    Logger.debug("attempting to create a user with settings " + userSetting)
    Try(session.withTransaction { userInfosTable.insert(userSetting); userSetting })

  }

//  def create(userSetting: UserSetting)(implicit session: Session) = {
//      try {
//        session.withTransaction {
//          userInfosTable.insert(userSetting);
//          Success(userSetting)
//        }
//      } catch {
//        case e => Failure(e)
//      }
//    }


  def update(userSetting: UserSetting)(implicit session: Session) =
    Try(session.withTransaction {userInfosTable.where(_.userId === userSetting.userId).update(userSetting); userSetting })

  def apply(userId: UserId)(implicit session: Session) = userInfosTable.where(_.userId === userId).firstOption

  def validName(startingName: String)(implicit session: Session) = {
    userInfosTable.where(_.name === startingName).firstOption match {
      case None => startingName
      case Some(_) => {
        val similarNames = userInfosTable.where(_.name like (startingName + "%")).list.map(_.name).toSet

        var count = 0
        while(similarNames(startingName + count)){ count += 1 }

        startingName + count
      }
    }
  }

}

