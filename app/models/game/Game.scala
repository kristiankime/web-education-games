package models.game

import com.artclod.slick.JodaUTC
import models.organization.Courses
import models.question.derivative.{Quiz, Quizzes}
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
    case _ => throw new IllegalStateException("Game was not in an allowed state, probably programming error " + this)
  }

//  def toRequested = (response, requestorQuizId, requesteeQuizId, requestorFinished, requesteeFinished, finishedDate) match {
//    case (GameResponseStatus.requested, None, None, false, false, None) => GameRequested(id = id, requestDate = requestDate, requestorId = requestorId, requesteeId = requesteeId, courseId = courseId)
//    case _ => throw new IllegalStateException("Game was not in GameRequested state programming error " + this)
//  }


  def ensureRequestorQuiz(implicit user: User, session: Session) : (Game, Quiz) = requestorQuizId match {
    case Some(quizId) => (this, Quizzes(quizId).get)
    case None => {
      val quiz = blankQuiz(user)
      val game = this.copy(requestorQuizId = Some(quiz.id))
      Games.update(game)
      (game, quiz)
    }
  }

  def ensureRequesteeQuiz(implicit user: User, session: Session) : (Game, Quiz) = requesteeQuizId match {
    case Some(quizId) => (this, Quizzes(quizId).get)
    case None => {
      val quiz = blankQuiz(user)
      val game = this.copy(requesteeQuizId = Some(quiz.id))
      Games.update(game)
      (game, quiz)
    }
  }

  def blankQuiz(user: User)(implicit session: Session): Quiz = {
    val now = JodaUTC.now
    Quizzes.create(Quiz(null, user.id, "Game Quiz", now, now))
  }
}