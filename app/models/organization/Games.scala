package models.organization

import com.artclod.slick.JodaUTC
import models.organization.table._
import models.support.{CourseId, GameId, UserId, QuizId}
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import service.table.UsersTable

case class Game(id: GameId = null,
                requestDate: DateTime,
                requestorId: UserId,
                requesteeId: UserId,
                requestAccepted: Boolean = false,
                courseId: Option[CourseId],
                requestorQuizId: Option[QuizId] = None,
                requesteeQuizId: Option[QuizId] = None,
                requesteeFinished: Boolean = false,
                requestorFinished: Boolean = false,
                finishedDate: Option[DateTime] = None){

  def requestor(implicit session: Session) = UsersTable.findById(requestorId).get

  def requestee(implicit session: Session) = UsersTable.findById(requesteeId).get

  def course(implicit session: Session) = courseId.map(Courses(_).get)
}

object Games {
  // ======= Request Game ======
  def requestCourse(requestorId: UserId, requesteeId: UserId, courseId: CourseId)(implicit session: Session) = {
    val game = Game(requestDate = JodaUTC.now, requestorId = requestorId, requesteeId = requesteeId, courseId = Some(courseId))
    val gameId = (gamesTable returning gamesTable.map(_.id)) += game
    game.copy(id = gameId)
  }

  // ======= Find a Request ======
  def requests(userId: UserId, courseId: CourseId)(implicit session: Session) =
    gamesTable.where(g => g.requestee === userId && g.course === courseId && g.requestAccepted === false).sortBy(_.requestDate).list

}


