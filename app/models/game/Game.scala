package models.game

import com.artclod.slick.JodaUTC
import models.game.GameRole._
import models.game.mask.GameMask
import models.organization.Courses
import models.quiz.{Quiz, Quizzes}
import models.support._
import models.user.{User, Users}
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import service.{View, Access, Non}

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
                finishedDate: Option[DateTime] = None) extends HasAccess {

  def access(implicit user: User, session: Session) : Access = {
    val courseAccess = course.map(_.access).getOrElse(Non)
    val requestorAccess = if(isRequestor(user)){View}else{Non}
    val requesteeAccess = if(isRequestee(user)){View}else{Non}
    List(courseAccess, requestorAccess, requesteeAccess).max
  }

  def isRequestor(user: User) = user.id match {
    case `requestorId` => true
    case _ => false
  }

  def isRequestee(user: User) = user.id match {
    case `requesteeId` => true
    case _ => false
  }

  def isPlayer(user: User) = isRequestor(user) || isRequestee(user)

  def isTeacher(implicit user: User, session: Session) = {
    val course = courseId.flatMap(Courses(_))
    val maybeAccess = course.map(_.access)
    maybeAccess.getOrElse(Non).write
  }

  def gameRole(user: User) = user.id match {
    case `requesteeId` => Requestee
    case `requestorId` => Requestor
    case _ => Unrelated
  }

  def skillLevel(user: User) = user.id match {
    case `requesteeId` => requesteeSkill
    case `requestorId` => requestorSkill
    case _ => throw new IllegalStateException("user [" + user + "] was not the requestor or the requestee")
  }

  def requestor(implicit session: Session) = Users(requestorId).get

  def requestee(implicit session: Session) = Users(requesteeId).get

  def requestorQuiz(implicit session: Session) = requestorQuizId.flatMap(Quizzes(_))

  def requestorQuizIfAnswered(implicit session: Session) = if(requesteeFinished) requestorQuiz else None

  def requestorQuizIfDone(quizId: QuizId)(implicit session: Session) = (requestorQuizDone, requestorQuizId) match {
    case (true, Some(`quizId`)) => Quizzes(quizId)
    case _ => None
  }

  def requesteeQuiz(implicit session: Session) = requesteeQuizId.flatMap(Quizzes(_))

  def requesteeQuizIfAnswered(implicit session: Session) = if(requestorFinished) requesteeQuiz else None

  def requesteeQuizIfDone(quizId: QuizId)(implicit session: Session) = (requesteeQuizDone, requesteeQuizId) match {
      case (true, Some(`quizId`)) => Quizzes(quizId)
      case _ => None
    }

  def otherPlayer(user: User)(implicit session: Session) = user.id match {
    case `requestorId` => requestee
    case `requesteeId` => requestor
    case _ => throw new IllegalStateException("user [" + user + "] was not the requestor or the requestee")
  }

  def otherPlayerId(userId: UserId) = userId match {
    case `requestorId` => requesteeId
    case `requesteeId` => requestorId
    case _ => throw new IllegalStateException("user [" + userId + "] was not the requestor or the requestee")
  }

  def course(implicit session: Session) = courseId.map(Courses(_).get)

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

  def isFinished = finishedDate.nonEmpty

  def notFinished = finishedDate.isEmpty

  def toState: GameState = (response, requestorQuizId, requestorQuizDone, requesteeQuizId, requesteeQuizDone, requestorFinished, requesteeFinished, finishedDate) match {
    // Response Requested
    case (GameResponseStatus.requested, _,       false, None,    false, false, false, None)    => RequestedNoQuiz(this)
    case (GameResponseStatus.requested, Some(_), true,  None,    false, false, false, None)    => RequestedQuizDone(this)
    // Game Rejected
    case (GameResponseStatus.rejected,  _,       false, None,    false, false, false, Some(_)) => RejectedNoQuiz(this)
    case (GameResponseStatus.rejected,  Some(_), true,  None,    false, false, false, Some(_)) => RejectedQuizDone(this)
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

  def toMask(me: User) : GameMask = toMask(me.id, this.otherPlayerId(me.id))

  def toMask(meId: UserId, otherId: UserId): GameMask =
  (meId, otherId) match {
    // me is requestor
    case (`requestorId`, `requesteeId`) =>
      (response, requestorQuizId, requestorQuizDone, requesteeQuizId, requesteeQuizDone, requestorFinished, requesteeFinished, finishedDate) match {
        // Response Requested
        case (GameResponseStatus.requested,       _, false,    None, false, false, false,    None) => mask.RequestedNoQuiz(this, meId, otherId)
        case (GameResponseStatus.requested, Some(_),  true,    None, false, false, false,    None) => mask.RequestedQuizDone(this, meId, otherId)
        // Game Rejected
        case (GameResponseStatus.rejected,       _,  false,    None, false, false, false, Some(_)) => mask.RejectedNoQuiz(this, meId, otherId)
        case (GameResponseStatus.rejected,  Some(_),  true,    None, false, false, false, Some(_)) => mask.RejectedQuizDone(this, meId, otherId)
        // Game Accepted (both making quizzes)
        case (GameResponseStatus.accepted,       _,  false,       _, false, false, false,    None) => mask.AcceptedMeNoQuizOtherNoQuiz(this, meId, otherId)
        case (GameResponseStatus.accepted,  Some(_),  true,       _, false, false, false,    None) => mask.AcceptedMeQuizDoneOtherNoQuiz(this, meId, otherId)
        case (GameResponseStatus.accepted,       _,  false, Some(_),  true, false, false,    None) => mask.AcceptedMeNoQuizOtherQuizDone(this, meId, otherId)
        // Game Answering
        case (GameResponseStatus.accepted,  Some(_),  true, Some(_),  true, false, false,    None) => mask.QuizzesDoneMeAnsOtherAns(this, meId, otherId)
        case (GameResponseStatus.accepted,  Some(_),  true, Some(_),  true, false,  true,    None) => mask.QuizzesDoneMeAnsOtherDone(this, meId, otherId)
        case (GameResponseStatus.accepted,  Some(_),  true, Some(_),  true,  true, false,    None) => mask.QuizzesDoneMeDoneOtherAns(this, meId, otherId)
        case (GameResponseStatus.accepted,  Some(_),  true, Some(_),  true,  true,  true, Some(_)) => mask.GameDone(this, meId, otherId)
        // Failure == programming error
        case _ => throw new IllegalStateException("Game was not in an allowed state, probably programming error " + this)
      }
    // me is requestee
    case (`requesteeId`, `requestorId`) =>
      (response, requestorQuizId, requestorQuizDone, requesteeQuizId, requesteeQuizDone, requestorFinished, requesteeFinished, finishedDate) match {
        // Response Requested
        case (GameResponseStatus.requested,       _, false,    None, false, false, false,    None) => mask.ResponseRequired(this, meId, otherId)
        case (GameResponseStatus.requested, Some(_),  true,    None, false, false, false,    None) => mask.ResponseRequired(this, meId, otherId)
        // Game Rejected
        case (GameResponseStatus.rejected,       _,  false,    None, false, false, false, Some(_)) => mask.RejectedNoQuiz(this, meId, otherId)
        case (GameResponseStatus.rejected,  Some(_),  true,    None, false, false, false, Some(_)) => mask.RejectedQuizDone(this, meId, otherId)
        // Game Accepted (both making quizzes)
        case (GameResponseStatus.accepted,       _,  false,       _, false, false, false,    None) => mask.AcceptedMeNoQuizOtherNoQuiz(this, meId, otherId)
        case (GameResponseStatus.accepted,  Some(_),  true,       _, false, false, false,    None) => mask.AcceptedMeNoQuizOtherQuizDone(this, meId, otherId)
        case (GameResponseStatus.accepted,       _,  false, Some(_),  true, false, false,    None) => mask.AcceptedMeQuizDoneOtherNoQuiz(this, meId, otherId)
        // Game Answering
        case (GameResponseStatus.accepted,  Some(_),  true, Some(_),  true, false, false,    None) => mask.QuizzesDoneMeAnsOtherAns(this, meId, otherId)
        case (GameResponseStatus.accepted,  Some(_),  true, Some(_),  true, false,  true,    None) => mask.QuizzesDoneMeDoneOtherAns(this, meId, otherId)
        case (GameResponseStatus.accepted,  Some(_),  true, Some(_),  true,  true, false,    None) => mask.QuizzesDoneMeAnsOtherDone(this, meId, otherId)
        case (GameResponseStatus.accepted,  Some(_),  true, Some(_),  true,  true,  true, Some(_)) => mask.GameDone(this, meId, otherId)
        // Failure == programming error
        case _ => throw new IllegalStateException("Game was not in an allowed state, probably programming error " + this)
      }

    case (_, _) => throw new IllegalStateException("me and other must be requestor and requestee, programming error me " + this)
  }

}
