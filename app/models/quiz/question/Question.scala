package models.quiz.question

import com.artclod.mathml.scalar.MathMLElem
import com.artclod.slick.JodaUTC
import controllers.quiz.derivative.DerivativeQuestionForm
import controllers.quiz.tangent.TangentQuestionForm
import models.organization.Course
import models.quiz._
import models.quiz.answer.{Answer, TangentAnswer}
import models.support.{Owned, QuestionId, QuizId, UserId}
import models.user.User
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple.Session
import play.api.templates.Html
import service.Access

sealed trait Question extends Owned {
  val id: QuestionId
  val ownerId: UserId
  val creationDate: DateTime
  val quizIdOp: Option[QuizId]
  val atCreationDifficulty : Double
  val order: Int

  def quiz(implicit session: Session) = quizIdOp.flatMap(Quizzes(_))

  def access(course: Course)(implicit user: User, session: Session) = {
    val courseAccess = course.access
    val ownerAccess = Access(user, ownerId)
    Seq(courseAccess, ownerAccess).max
  }

  def results(user: User)(implicit session: Session) : QuestionResults

  def answersAndOwners(implicit session: Session) : List[(Answer, User)]

  def difficulty : Double

  def display : Html
}

case class DerivativeQuestion(id: QuestionId, ownerId: UserId, mathML: MathMLElem, rawStr: String, creationDate: DateTime, atCreationDifficulty : Double, quizIdOp: Option[QuizId] = None, order: Int = 1) extends Question with ViewableMath {

  def answersAndOwners(implicit session: Session) = DerivativeQuestions.answersAndOwners(id)

  def difficulty : Double = DerivativeDifficulty(mathML)

  def results(user: User)(implicit session: Session) = DerivativeQuestionResults(user, this, answers(user))

  def answers(user: User)(implicit session: Session) = DerivativeQuestions(id, user)

  def display : Html = views.html.quiz.derivative.questionDisplay(this)
}

case class TangentQuestion(id: QuestionId, ownerId: UserId, function: MathMLElem, functionStr: String, atPointX: MathMLElem, atPointXStr: String, creationDate: DateTime, atCreationDifficulty : Double, quizIdOp: Option[QuizId] = None, order: Int = 1) extends Question {

  def answersAndOwners(implicit session: Session) : List[(TangentAnswer, User)] = TangentQuestions.answersAndOwners(id)

  def difficulty : Double = 1d // TODO

  def results(user: User)(implicit session: Session) = TangentQuestionResults(user, this, answers(user))

  def answers(user: User)(implicit session: Session) = TangentQuestions(id, user)

  def display : Html = views.html.quiz.tangent.questionDisplay(this)

  def functionViewableMath = new ViewableMath { val mathML = function; val rawStr = functionStr }

  def atPointXViewableMath = new ViewableMath { val mathML = atPointX; val rawStr = atPointXStr }
}
