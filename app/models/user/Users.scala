package models.user

import com.artclod.slick.JodaUTC
import models.organization.table._
import models.support.{CourseId, UserId}
import models.user.table._
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import service.Access

import scala.Option
import scala.util.{Failure, Success, Try}

object Users {

  def nameDisplay(name:String, id: UserId) = name + "-" + id.v

  def create(user: User)(implicit session: Session) =
    Try(session.withTransaction { usersTable.insert(user); user } )

  def update(user: User)(implicit session: Session) =
    Try(session.withTransaction { usersTable.where(_.userId === user.id).update(user); user } )

  def apply(userId: UserId)(implicit session: Session) : Option[User] = usersTable.where(_.userId === userId).firstOption

  def access(userId: UserId)(implicit session: Session) : Try[User] = {
    val userOp = usersTable.where(_.userId === userId).firstOption
    val userTry = userOp match {
      case Some(user) => Success(user)
      case None => Failure(new IllegalArgumentException("no user found for id " + userId))
    }
    userTry.flatMap( user => update(user.copy(lastAccess = JodaUTC.now)))
  }


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

