package models.organization

import models.user.UserPimped
import com.artclod.slick.JodaUTC
import models.organization.table._
import models.support.{CourseId, GameId, UserId, QuizId}
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import service.table.UsersTable._
import service.{Access, View}
import service.table.UsersTable
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import models.support._
import models.organization.table._
import models.question.derivative._
import service._
import service.table.UsersTable.userTable

case class Game(id: GameId = null,
                requestDate: DateTime,
                requestorId: UserId,
                requesteeId: UserId,
                requestAccepted: Boolean = false,
                courseId: Option[CourseId] = None,
                requestorQuizId: Option[QuizId] = None,
                requesteeQuizId: Option[QuizId] = None,
                requesteeFinished: Boolean = false,
                requestorFinished: Boolean = false,
                finishedDate: Option[DateTime] = None){

  def requestor(implicit session: Session) = UsersTable.findById(requestorId).get

  def requestee(implicit session: Session) = UsersTable.findById(requesteeId).get

  def course(implicit session: Session) = courseId.map(Courses(_).get)

  def finished = finishedDate.nonEmpty
}

object Games {

  // ======= Who to Play a Game with ======
  def studentsToPlayWith(requestorId: UserId, courseId: CourseId)(implicit session: Session) =
    Courses.studentsExcept(courseId, requestorId).filter(u => activeGame(requestorId, u.id).nonEmpty)

  // ======= Request Game ======
  def request(requestorId: UserId, requesteeId: UserId, courseId: CourseId)(implicit session: Session) = {
    val game = Game(requestDate = JodaUTC.now, requestorId = requestorId, requesteeId = requesteeId, courseId = Some(courseId))
    val gameId = (gamesTable returning gamesTable.map(_.id)) += game
    game.copy(id = gameId)
  }

  def request(requestorId: UserId, requesteeId: UserId)(implicit session: Session) = {
    val game = Game(requestDate = JodaUTC.now, requestorId = requestorId, requesteeId = requesteeId)
    val gameId = (gamesTable returning gamesTable.map(_.id)) += game
    game.copy(id = gameId)
  }

  // ======= Find a Request ======
  def requests(userId: UserId, courseId: CourseId)(implicit session: Session) =
    gamesTable.where(g => g.requestee === userId && g.course === courseId && g.requestAccepted === false).sortBy(_.requestDate).list

  def requests(userId: UserId)(implicit session: Session) =
    gamesTable.where(g => g.requestee === userId && g.requestAccepted === false).sortBy(_.requestDate).list

  // ======= Find an Active Game ======
  def activeGame(player1Id: UserId, player2Id: UserId)(implicit session: Session) : Option[Game] =
    ( gamesTable.where(g => g.requestee === player1Id && g.requestor === player2Id && (g.finishedDate isNull)) union
      gamesTable.where(g => g.requestee === player2Id && g.requestor === player1Id && (g.finishedDate isNull))       ).firstOption

}


