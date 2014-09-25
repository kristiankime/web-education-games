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
                requestorSkill: Double,
                requesteeId: UserId,
                requesteeSkill: Double,
                response: GameResponseStatus = Requested,
                courseId: Option[CourseId] = None,
                requestorQuizId: Option[QuizId] = None,
                requestorQuizDone: Boolean = false,
                requesteeQuizId: Option[QuizId] = None,
                requesteeQuizDone: Boolean = false,
                requesteeFinished: Boolean = false,
                requestorFinished: Boolean = false,
                requesteeStudentPoints: Option[Double] = None,
                requesteeTeacherPoints: Option[Double] = None,
                requestorStudentPoints: Option[Double] = None,
                requestorTeacherPoints: Option[Double] = None,
                finishedDate: Option[DateTime] = None) {

  def isRequestor(user: User) = user.id match {
    case `requestorId` => true
    case _ => false
  }

  def isRequestee(user: User) = user.id match {
    case `requesteeId` => true
    case _ => false
  }

  def requestor(implicit session: Session) = UsersTable.findById(requestorId).get

  def requestee(implicit session: Session) = UsersTable.findById(requesteeId).get

  def requestorQuiz(implicit session: Session) = requestorQuizId.flatMap(Quizzes(_))

  def requesteeQuiz(implicit session: Session) = requesteeQuizId.flatMap(Quizzes(_))

  def otherPlayer(user: User)(implicit session: Session) = user.id match {
    case `requestorId` => requestee
    case `requesteeId` => requestor
    case _ => throw new IllegalStateException("user [" + user + "] was not the requestor or the requestee")
  }

  def course(implicit session: Session) = courseId.map(Courses(_).get)

  def toState: GameState = (response, requestorQuizId, requestorQuizDone, requesteeQuizId, requesteeQuizDone, requestorFinished, requesteeFinished, finishedDate) match {
    // Response Requested
    case (GameResponseStatus.requested, _,       false, None,    false, false, false, None)    => RequestedNoQuiz(this)
    case (GameResponseStatus.requested, Some(_), true,  None,    false, false, false, None)    => RequestedQuizDone(this)
    // Game Rejected
    case (GameResponseStatus.rejected,  _,       false, None,    false, false, false, None)    => RejectedNoQuiz(this)
    case (GameResponseStatus.rejected,  Some(_), true,  None,    false, false, false, None)    => RejectedQuizDone(this)
    // Game Accepted (both making quizzes, Tor == Requestor, Tee == Requestee)
    case (GameResponseStatus.accepted,  _,       false, _,       false, false, false, None)    => AcceptedTorNoQuizTeeNoQuiz(this)
    case (GameResponseStatus.accepted,  Some(_), true,  _,       false, false, false, None)    => AcceptedTorQuizDoneTeeNoQuiz(this)
    case (GameResponseStatus.accepted,  _,       false, Some(_), true,  false, false, None)    => AcceptedNoTorQuizTeeQuizDone(this)
    // Game Answering
    case (GameResponseStatus.accepted,  Some(_), true,  Some(_), true,  false, false, None)    => QuizzesDoneTorAnsTeeAns(this)
    case (GameResponseStatus.accepted,  Some(_), true,  Some(_), true,  false, true,  None)    => QuizzesDoneTorAnsTeeDone(this)
    case (GameResponseStatus.accepted,  Some(_), true,  Some(_), true,  true,  false, None)    => QuizzesDoneTorDoneTeeAnd(this)
    case (GameResponseStatus.accepted,  Some(_), true,  Some(_), true,  true,  true,  Some(_)) => GameDone(this)
    // Failure == programming error
    case _ => throw new IllegalStateException("Game was not in an allowed state, probably programming error " + this)
  }

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

  private def blankQuiz(user: User)(implicit session: Session): Quiz = {
    val now = JodaUTC.now
    Quizzes.create(Quiz(null, user.id, "Game Quiz", now, now))
  }

  def maybeUpdateForGameDone =
    if(requesteeFinished && requestorFinished) this.copy(finishedDate = Some(JodaUTC.now))
    else this

  def gameDone = finishedDate.nonEmpty
}
