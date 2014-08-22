package models.organization

import models.support._
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import service.User
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

  def otherPlayer(user: User)(implicit session: Session) = user.id match {
    case `requestorId` => requestee
    case `requesteeId` => requestor
    case _ => throw new IllegalStateException("user [" + user + "] was not the requestor or the requestee")
  }
  def course(implicit session: Session) = courseId.map(Courses(_).get)

  def toState: GameState = (response, requestorQuizId, requesteeQuizId, requestorFinished, requesteeFinished, finishedDate) match {
    case (GameResponseStatus.requested, None, None, false, false, None) => GameRequested(id = id, requestDate = requestDate, requestorId = requestorId, requesteeId = requesteeId, courseId = courseId)
    case (GameResponseStatus.rejected, None, None, false, false, None) => GameRejected(id = id, requestDate = requestDate, requestorId = requestorId, requesteeId = requesteeId, courseId = courseId)
    case (GameResponseStatus.accepted, None, None, false, false, None) => GameAccepted(id = id, requestDate = requestDate, requestorId = requestorId, requesteeId = requesteeId, courseId = courseId)
    case (GameResponseStatus.accepted, Some(torQuiz), None, false, false, None) => GameQuizRequestor(id = id, requestDate = requestDate, requestorId = requestorId, requesteeId = requesteeId, courseId = courseId, requestorQuizId = torQuiz)
    case (GameResponseStatus.accepted, None, Some(teeQuiz), false, false, None) => GameQuizRequestee(id = id, requestDate = requestDate, requestorId = requestorId, requesteeId = requesteeId, courseId = courseId, requesteeQuizId = teeQuiz)
    case (GameResponseStatus.accepted, Some(torQuiz), Some(teeQuiz), false, false, None) => GameQuizBoth(id = id, requestDate = requestDate, requestorId = requestorId, requesteeId = requesteeId, courseId = courseId, requestorQuizId = torQuiz, requesteeQuizId = teeQuiz)
    case _ => throw new IllegalStateException("Game was not in an allowed state programming error " + this)
  }

  def toRequested = (response, requestorQuizId, requesteeQuizId, requestorFinished, requesteeFinished, finishedDate) match {
    case (GameResponseStatus.requested, None, None, false, false, None) => GameRequested(id = id, requestDate = requestDate, requestorId = requestorId, requesteeId = requesteeId, courseId = courseId)
    case _ => throw new IllegalStateException("Game was not in GameRequested state programming error " + this)
  }


  //
  //  def accept(requestee: User) = {
  //    c("requestee.id", requesteeId, requestee.id)
  //    c("response", response, requested)
  //    c("requestorQuizId", requestorQuizId, None)
  //    c("requesteeQuizId", requesteeQuizId, None)
  //    c("requesteeFinished", requesteeFinished, false)
  //    c("requestorFinished", requestorFinished, false)
  //    c("finishedDate", finishedDate, None)
  //    this.copy(response = GameResponseStatus.accepted)
  //  }
  //
  //  def reject(requestee: User) = {
  //    c("requestee.id", requesteeId, requestee.id)
  //    c("response", response, requested)
  //    c("requestorQuizId", requestorQuizId, None)
  //    c("requesteeQuizId", requesteeQuizId, None)
  //    c("requesteeFinished", requesteeFinished, false)
  //    c("requestorFinished", requestorFinished, false)
  //    c("finishedDate", finishedDate, None)
  //    this.copy(response = GameResponseStatus.rejected)
  //  }
  //
  //  private def checkState(expectedResponse: GameResponseStatus,
  //                         hasRequestorQuizId: Boolean,
  //                         hasRequesteeQuizId: Boolean,
  //                         expectedRequesteeFinished: Boolean,
  //                         expectedRequestorFinished: Boolean,
  //                         hasRinishedDate: Boolean): Unit = {
  //
  //  }
  //
  //
  //  private def c[T](field: String, v: T, e: T) = if (v != e) throw new IllegalArgumentException(field + " was supposed to be [" + e + "] but was [" + v + "]")
  //
  //  def status(playerId: UserId): GameStatus =
  //    playerId match {
  //      case `requestorId` =>
  //        (response, requesteeQuizId, requestorQuizId, requesteeFinished, requestorFinished, finishedDate) match {
  //          case (GameResponseStatus.requested, None, None, false, false, None) => AwaitingReponseStatus
  //          case (GameResponseStatus.rejected, None, None, false, false, None) => GameRejectedStatus
  //          case (GameResponseStatus.accepted, _, None, false, false, None) => CreateQuizStatus
  //          case (GameResponseStatus.accepted, None, Some(torQuiz), false, false, None) => AwaitingQuizStatus
  //          case (GameResponseStatus.accepted, Some(eeQuiz), Some(torQuiz), _, false, None) => AnswerQuizStatus
  //          case (GameResponseStatus.accepted, Some(eeQuiz), Some(torQuiz), false, true, None) => AwaitingAnswerStatus
  //          case (GameResponseStatus.accepted, Some(eeQuiz), Some(torQuiz), true, true, Some(done)) => GameDoneStatus
  //          case _ => throw new IllegalStateException("Game was not in an allowed state programming error " + this)
  //        }
  //      case `requesteeId` =>
  //        (response, requesteeQuizId, requestorQuizId, requesteeFinished, requestorFinished, finishedDate) match {
  //          case (GameResponseStatus.requested, None, None, false, false, None) => RespondRequestStatus
  //          case (GameResponseStatus.rejected, None, None, false, false, None) => GameRejectedStatus
  //          case (GameResponseStatus.accepted, None, _, false, false, None) => CreateQuizStatus
  //          case (GameResponseStatus.accepted, Some(eeQuiz), None, false, false, None) => AwaitingQuizStatus
  //          case (GameResponseStatus.accepted, Some(eeQuiz), Some(torQuiz), _, false, None) => AnswerQuizStatus
  //          case (GameResponseStatus.accepted, Some(eeQuiz), Some(torQuiz), false, true, None) => AwaitingAnswerStatus
  //          case (GameResponseStatus.accepted, Some(eeQuiz), Some(torQuiz), true, true, Some(done)) => GameDoneStatus
  //          case _ => throw new IllegalStateException("Game was not in an allowed state programming error " + this)
  //        }
  //      case _ => throw new IllegalArgumentException("User was not the requestor or the requestee")
  //    }

}
