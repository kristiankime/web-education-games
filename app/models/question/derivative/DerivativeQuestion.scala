package models.question.derivative

import com.artclod.mathml.scalar.MathMLElem
import com.artclod.mathml.slick.MathMLMapper
import models.support.{UserId, QuestionId}
import com.artclod.mathml.scalar._
import com.google.common.annotations.VisibleForTesting
import models.organization.Course
import models.question._
import models.question.derivative.result.DerivativeQuestionResults
import models.question.derivative.table._
import models.support._
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.Config.driver.simple.Query
import play.api.templates.Html
import service._
import service.table.UsersTable
import models.question.derivative.result.DerivativeQuestionResults
import com.artclod.slick.JodaUTC.timestamp2DateTime
import MathMLMapper.string2mathML
import com.artclod.mathml.scalar.MathMLElem
import scala.slick.lifted

case class DerivativeQuestion(id: QuestionId, ownerId: UserId, mathML: MathMLElem, rawStr: String, creationDate: DateTime, quizIdOp: Option[QuizId] = None, order: Int = 1) extends Question with ViewableMath {

  def answersAndOwners(implicit session: Session) = DerivativeQuestions.answersAndOwners(id)

  def difficulty : Double = QuestionDifficulty(mathML)

  def results(user: User)(implicit session: Session) = DerivativeQuestionResults(user, this, answers(user))

  def answers(user: User)(implicit session: Session) = DerivativeQuestions(id, user)

  // === Methods for viewing
  def display : Html = views.html.question.derivative.questionDisplay(this)

}
