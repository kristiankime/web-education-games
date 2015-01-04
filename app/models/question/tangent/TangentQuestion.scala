package models.question.tangent

import com.artclod.mathml.scalar.MathMLElem
import models.organization.Course
import models.question._
import models.question.derivative.result.DerivativeQuestionResults
import models.support.{QuestionId, UserId, _}
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import play.api.templates.Html
import service._

case class TangentQuestion(id: QuestionId, ownerId: UserId, function: MathMLElem, functionStr: String, atPointX: Double, creationDate: DateTime, quizIdOp: Option[QuizId] = None, order: Int = 1) extends Question {

  def results(user: User)(implicit session: Session) : QuestionResults = null // TODO

  def answersAndOwners(implicit session: Session) : List[(Answer, User)] = null // TODO

  def difficulty : Double = 1d // TODO

  def display : Html = null // TODO

}
