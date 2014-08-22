package models.game

import models.organization.Courses
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
                requestorQuizDone: Boolean = false,
                requesteeQuizId: Option[QuizId] = None,
                requesteeQuizDone: Boolean = false,
                requesteeFinished: Boolean = false,
                requestorFinished: Boolean = false,
                finishedDate: Option[DateTime] = None) {

  def isRequestor(user: User) = user.id match {
    case `requestorId` => true
    case `requesteeId` => false
    case _ => throw new IllegalStateException("user [" + user + "] was not the requestor or the requestee")
  }

  def requestor(implicit session: Session) = UsersTable.findById(requestorId).get

  def requestee(implicit session: Session) = UsersTable.findById(requesteeId).get

  def otherPlayer(user: User)(implicit session: Session) = user.id match {
    case `requestorId` => requestee
    case `requesteeId` => requestor
    case _ => throw new IllegalStateException("user [" + user + "] was not the requestor or the requestee")
  }

  def course(implicit session: Session) = courseId.map(Courses(_).get)

//  case class RequestedNoQuiz(val game: Game) extends GameState with GameRequested with RequestorNoQuiz with RequestorQuizUnfinished with RequesteeNoQuiz with RequesteeQuizUnfinished { checks }
//  case class RequestedQuiz(val game: Game) extends GameState with  GameRequested with RequestorQuiz with RequestorQuizUnfinished with RequesteeNoQuiz with RequesteeQuizUnfinished { checks }
//  case class RequestedQuizDone(val game: Game) extends GameState with GameRequested with RequestorQuiz with RequestorQuizFinished with RequesteeNoQuiz with RequesteeQuizUnfinished { checks }
//
//  case class RejectedNoQuiz(val game: Game) extends GameState with GameRejected with RequestorNoQuiz with RequestorQuizUnfinished with RequesteeNoQuiz with RequesteeQuizUnfinished { checks }
//  case class RejectedQuiz(val game: Game) extends GameState with GameRejected with RequestorQuiz with RequestorQuizUnfinished with RequesteeNoQuiz with RequesteeQuizUnfinished { checks }
//  case class RejectedQuizDone(val game: Game) extends GameState with GameRejected with RequestorQuiz with RequestorQuizFinished with RequesteeNoQuiz with RequesteeQuizUnfinished { checks }
//

//  case class AcceptedTorNoQuizTeeNoQuiz(val game: Game) extends GameState with GameAccepted with RequestorNoQuiz with RequestorQuizUnfinished with RequesteeNoQuiz with RequesteeQuizUnfinished { checks }
//  case class AcceptedTorQuizTeeNoQuiz(val game: Game) extends GameState with GameAccepted with RequestorQuiz with RequestorQuizUnfinished with RequesteeNoQuiz with RequesteeQuizUnfinished { checks }
//  case class AcceptedTorQuizDoneTeeNoQuiz(val game: Game) extends GameState with GameAccepted with RequestorQuiz with RequestorQuizFinished with RequesteeNoQuiz with RequesteeQuizUnfinished { checks }

//  case class AcceptedTorNoQuizTeeQuiz(val game: Game) extends GameState with GameAccepted with RequestorNoQuiz with RequestorQuizUnfinished with RequesteeQuiz with RequesteeQuizUnfinished { checks }
//  case class AcceptedTorQuizTeeQuiz(val game: Game) extends GameState with GameAccepted with RequestorQuiz with RequestorQuizUnfinished with RequesteeQuiz with RequesteeQuizUnfinished { checks }
//  case class AcceptedTorQuizDoneTeeQuiz(val game: Game) extends GameState with GameAccepted with RequestorQuiz with RequestorQuizFinished with RequesteeQuiz with RequesteeQuizUnfinished { checks }

//  case class AcceptedTorNoQuizTeeQuizDone(val game: Game) extends GameState with GameAccepted with RequestorNoQuiz with RequestorQuizUnfinished with RequesteeQuiz with RequesteeQuizFinished { checks }
//  case class AcceptedTorQuizTeeQuizDone(val game: Game) extends GameState with GameAccepted with RequestorQuiz with RequestorQuizUnfinished with RequesteeQuiz with RequesteeQuizFinished{ checks }
//  case class AcceptedTorQuizDoneTeeQuizDone(val game: Game) extends GameState with GameAccepted with RequestorQuiz with RequestorQuizFinished with RequesteeQuiz with RequesteeQuizFinished { checks }



  def toState: GameState = (response, requestorQuizId, requestorQuizDone, requesteeQuizId, requesteeQuizDone, requestorFinished, requesteeFinished, finishedDate) match {
    // Response Requested
    case (GameResponseStatus.requested, None,    false, None,    false, false, false, None) => RequestedNoQuiz(this)
    case (GameResponseStatus.requested, Some(_), false, None,    false, false, false, None) => RequestedQuiz(this)
    case (GameResponseStatus.requested, Some(_), true,  None,    false, false, false, None) => RequestedQuizDone(this)
    // Game Rejected
    case (GameResponseStatus.rejected, None,     false, None,    false, false, false, None) => RejectedNoQuiz(this)
    case (GameResponseStatus.rejected, Some(_),  false, None,    false, false, false, None) => RejectedQuiz(this)
    case (GameResponseStatus.rejected, Some(_),  true,  None,    false, false, false, None) => RejectedQuizDone(this)
    // Game Accepted (both making quizzes, Tor == Requestor, Tee == Requestee)
    case (GameResponseStatus.accepted, None,     false, None,    false, false, false, None) => AcceptedTorNoQuizTeeNoQuiz(this)
    case (GameResponseStatus.accepted, Some(_),  false, None,    false, false, false, None) => AcceptedTorQuizTeeNoQuiz(this)
    case (GameResponseStatus.accepted, Some(_),  true,  None,    false, false, false, None) => AcceptedTorQuizDoneTeeNoQuiz(this)
    case (GameResponseStatus.accepted, None,     false, Some(_), false, false, false, None) => AcceptedTorNoQuizTeeQuiz(this)
    case (GameResponseStatus.accepted, Some(_),  false, Some(_), false, false, false, None) => AcceptedTorQuizTeeQuiz(this)
    case (GameResponseStatus.accepted, Some(_),  true,  Some(_), false, false, false, None) => AcceptedTorQuizDoneTeeQuiz(this)
    case (GameResponseStatus.accepted, None,     false, Some(_), true,  false, false, None) => AcceptedTorNoQuizTeeQuizDone(this)
    case (GameResponseStatus.accepted, Some(_),  false, Some(_), true,  false, false, None) => AcceptedTorQuizTeeQuizDone(this)
    case (GameResponseStatus.accepted, Some(_),  true,  Some(_), true,  false, false, None) => AcceptedTorQuizDoneTeeQuizDone(this)
    // Failure == programming error
    case _ => throw new IllegalStateException("Game was not in an allowed state programming error " + this)
  }

//  def toRequested = (response, requestorQuizId, requesteeQuizId, requestorFinished, requesteeFinished, finishedDate) match {
//    case (GameResponseStatus.requested, None, None, false, false, None) => GameRequested(id = id, requestDate = requestDate, requestorId = requestorId, requesteeId = requesteeId, courseId = courseId)
//    case _ => throw new IllegalStateException("Game was not in GameRequested state programming error " + this)
//  }

}
