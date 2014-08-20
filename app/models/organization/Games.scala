package models.organization

import com.artclod.slick.JodaUTC
import models.organization.table._
import models.support._
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import service.table.UsersTable

case class Game(id: GameId = null,
                requestDate: DateTime,
                requestorId: UserId,
                requesteeId: UserId,
                response: GameResponseStatus = Requested,
                courseId: Option[CourseId] = None,
                requestorQuizId: Option[QuizId] = None,
                requesteeQuizId: Option[QuizId] = None,
                requesteeFinished: Boolean = false,
                requestorFinished: Boolean = false,
                finishedDate: Option[DateTime] = None) {

  def requestor(implicit session: Session) = UsersTable.findById(requestorId).get

  def requestee(implicit session: Session) = UsersTable.findById(requesteeId).get

  def course(implicit session: Session) = courseId.map(Courses(_).get)

  def status(playerId: UserId): GameStatus =
    playerId match {
    case `requestorId` =>
      (response, requesteeQuizId, requestorQuizId, requesteeFinished, requestorFinished, finishedDate) match {
        case (GameResponseStatus.requested, None, None, false, false, None) => AwaitingReponse
        case (GameResponseStatus.rejected, None, None, false, false, None) => GameRejected
        case (GameResponseStatus.accepted, _, None, false, false, None) => CreateQuiz
        case (GameResponseStatus.accepted, None, Some(torQuiz), false, false, None) => AwaitingQuiz
        case (GameResponseStatus.accepted, Some(eeQuiz), Some(torQuiz), _, false, None) => AnswerQuiz
        case (GameResponseStatus.accepted, Some(eeQuiz), Some(torQuiz), false, true, None) => AwaitingAnswer
        case (GameResponseStatus.accepted, Some(eeQuiz), Some(torQuiz), true, true, Some(done)) => GameDone
        case _ => throw new IllegalStateException("Game was not in an allowed state programming error " + this)
      }
    case `requesteeId` =>
      (response, requesteeQuizId, requestorQuizId, requesteeFinished, requestorFinished, finishedDate) match {
        case (GameResponseStatus.requested, None, None, false, false, None) => RespondRequest
        case (GameResponseStatus.rejected, None, None, false, false, None) => GameRejected
        case (GameResponseStatus.accepted, None, _, false, false, None) => CreateQuiz
        case (GameResponseStatus.accepted, Some(eeQuiz), None, false, false, None) => AwaitingQuiz
        case (GameResponseStatus.accepted, Some(eeQuiz), Some(torQuiz), _, false, None) => AnswerQuiz
        case (GameResponseStatus.accepted, Some(eeQuiz), Some(torQuiz), false, true, None) => AwaitingAnswer
        case (GameResponseStatus.accepted, Some(eeQuiz), Some(torQuiz), true, true, Some(done)) => GameDone
        case _ => throw new IllegalStateException("Game was not in an allowed state programming error " + this)
      }
    case _ => throw new IllegalArgumentException("User was not the requestor or the requestee")
  }


}

object Games {

  // ======= Who to Play a Game with ======
  def studentsToPlayWith(requestorId: UserId, courseId: CourseId)(implicit session: Session) =
    Courses.studentsExcept(courseId, requestorId).filter(u => activeGame(requestorId, u.id).nonEmpty)

  // ======= Find an Active Game ======
  def activeGame(player1Id: UserId, player2Id: UserId)(implicit session: Session): Option[Game] =
    (gamesTable.where(g => ((g.finishedDate isNull) &&
      (g.requestee === player1Id && g.requestor === player2Id) || (g.requestee === player2Id && g.requestor === player1Id)))).firstOption

  // ======= Request Game ======
  def request(requestorId: UserId, requesteeId: UserId, courseId: CourseId)(implicit session: Session): Game =
    request(Game(requestDate = JodaUTC.now, requestorId = requestorId, requesteeId = requesteeId, courseId = Some(courseId)))

  private def request(game: Game)(implicit session: Session): Game = {
    val gameId = (gamesTable returning gamesTable.map(_.id)) += game
    game.copy(id = gameId)
  }

  def request(requestorId: UserId, requesteeId: UserId)(implicit session: Session): Game =
    request(Game(requestDate = JodaUTC.now, requestorId = requestorId, requesteeId = requesteeId))

  // ======= Find a Request ======
  def requests(userId: UserId, courseId: CourseId)(implicit session: Session) =
    gamesTable.where(g => g.requestee === userId && g.course === courseId && g.response === GameResponseStatus.requested).sortBy(_.requestDate).list

  def requests(userId: UserId)(implicit session: Session) =
    gamesTable.where(g => g.requestee === userId && g.response === GameResponseStatus.requested).sortBy(_.requestDate).list

}
