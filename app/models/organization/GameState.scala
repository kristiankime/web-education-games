//package models.organization
//
//import com.artclod.slick.JodaUTC
//import models.organization.GameResponseStatus._
//import models.question.derivative._
//import models.support._
//import models.user.Users
//import org.joda.time.DateTime
//import service.User
//import play.api.db.slick.Config.driver.simple._
//
//
//sealed trait GameState {
//  def toGame: Game
//
//  val requestorId: UserId
//
//  val requesteeId: UserId
//
//  def requestor(implicit session: Session) = Users(requestorId).get
//
//  def requestee(implicit session: Session) = Users(requesteeId).get
//
//}
//
//// ================= GAME REQUEST ==============
//// Game can be requested, rejected or accepted
//case class GameRequested(id: GameId, requestDate: DateTime, requestorId: UserId, requesteeId: UserId, courseId: Option[CourseId]) extends GameState {
//  def toGame = Game(id = id, requestDate = requestDate, requestorId = requestorId, requesteeId = requesteeId, response = requested, courseId = courseId)
//
//  def reject(requestee: User) = {
//    if (requestee.id != requesteeId) throw new IllegalStateException("user [" + requestee + "] attempted to reject game but was not requestee [" + requestee.id + "]")
//    GameRejected(id = id, requestDate = requestDate, requestorId = requestorId, requesteeId = requesteeId, courseId = courseId)
//  }
//
//  def accept(requestee: User) = {
//    if (requestee.id != requesteeId) throw new IllegalStateException("user [" + requestee + "] attempted to accept game but was not requestee [" + requestee.id + "]")
//    GameAccepted(id = id, requestDate = requestDate, requestorId = requestorId, requesteeId = requesteeId, courseId = courseId)
//  }
//}
//
//case class GameRejected(id: GameId, requestDate: DateTime, requestorId: UserId, requesteeId: UserId, courseId: Option[CourseId]) extends GameState {
//  def toGame = Game(id = id, requestDate = requestDate, requestorId = requestorId, requesteeId = requesteeId, response = rejected, courseId = courseId, finishedDate = Some(JodaUTC.now))
//}
//
//case class GameAccepted(id: GameId, requestDate: DateTime, requestorId: UserId, requesteeId: UserId, courseId: Option[CourseId], requestorQuizId: QuizId, requesteeQuizId: QuizId) extends GameState {
//  def toGame = Game(id = id, requestDate = requestDate, requestorId = requestorId, requesteeId = requesteeId, response = accepted, courseId = courseId)
//
//  def addQuiz(user: User, quiz: Quiz): GameState = user.id match {
//    case `requestorId` => GameQuizRequestor(id = id, requestDate = requestDate, requestorId = requestorId, requesteeId = requesteeId, courseId = courseId, requestorQuizId = quiz.id)
//    case `requesteeId` => GameQuizRequestee(id = id, requestDate = requestDate, requestorId = requestorId, requesteeId = requesteeId, courseId = courseId, requesteeQuizId = quiz.id)
//    case _ => throw new IllegalStateException("user [" + user + "] was not the requestor or the requestee")
//  }
//}
//
//// ================= GAME QUIZ CREATION ==============
//// Either or Both can have created Quizzes
//case class GameQuizRequestor(id: GameId, requestDate: DateTime, requestorId: UserId, requesteeId: UserId, courseId: Option[CourseId], requestorQuizId: QuizId) extends GameState {
//  def toGame = Game(id = id, requestDate = requestDate, requestorId = requestorId, requesteeId = requesteeId, response = accepted, courseId = courseId, requestorQuizId = Some(requestorQuizId))
//
//  def addQuiz(user: User, quiz: Quiz): GameState = user.id match {
//    case `requestorId` => throw new IllegalStateException("user [" + user + "] was the requestor and has already added a quiz")
//    case `requesteeId` => GameQuizBoth(id = id, requestDate = requestDate, requestorId = requestorId, requesteeId = requesteeId, courseId = courseId, requestorQuizId = requestorQuizId, requesteeQuizId = quiz.id)
//    case _ => throw new IllegalStateException("user [" + user + "] was no the requestor or the requestee")
//  }
//}
//
//case class GameQuizRequestee(id: GameId, requestDate: DateTime, requestorId: UserId, requesteeId: UserId, courseId: Option[CourseId], requesteeQuizId: QuizId) extends GameState {
//  def toGame = Game(id = id, requestDate = requestDate, requestorId = requestorId, requesteeId = requesteeId, response = accepted, courseId = courseId, requesteeQuizId = Some(requesteeQuizId))
//
//  def addQuiz(user: User, quiz: Quiz): GameState = user.id match {
//    case `requestorId` => GameQuizBoth(id = id, requestDate = requestDate, requestorId = requestorId, requesteeId = requesteeId, courseId = courseId, requestorQuizId = quiz.id, requesteeQuizId = requesteeQuizId)
//    case `requesteeId` => throw new IllegalStateException("user [" + user + "] was the requestee and has already added a quiz")
//    case _ => throw new IllegalStateException("user [" + user + "] was no the requestor or the requestee")
//  }
//}
//
//case class GameQuizBoth(id: GameId, requestDate: DateTime, requestorId: UserId, requesteeId: UserId, courseId: Option[CourseId], requestorQuizId: QuizId, requesteeQuizId: QuizId) extends GameState {
//  def toGame = Game(id = id, requestDate = requestDate, requestorId = requestorId, requesteeId = requesteeId, response = accepted, courseId = courseId, requestorQuizId = Some(requestorQuizId), requesteeQuizId = Some(requesteeQuizId))
//}
//
//// ================= GAME ANSWERING ==============