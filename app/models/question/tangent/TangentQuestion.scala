package models.question.tangent

import com.artclod.mathml.scalar.MathMLElem
import models.organization.Course
import models.question._
import models.question.derivative.result.DerivativeQuestionResults
import models.question.tangent.result.TangentQuestionResults
import models.support.{QuestionId, UserId, _}
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import play.api.templates.Html
import service._

case class TangentQuestion(id: QuestionId, ownerId: UserId, function: MathMLElem, functionStr: String, atPointX: MathMLElem, atPointXStr: String, creationDate: DateTime, quizIdOp: Option[QuizId] = None, order: Int = 1) extends Question {

  def results(user: User)(implicit session: Session) = TangentQuestionResults(user, this, List()) // TODO

  def answersAndOwners(implicit session: Session) : List[(TangentAnswer, User)] = List() // TODO

  def difficulty : Double = 1d // TODO

  def display : Html = views.html.tag.twoHtml(
    views.html.mathml.mathmlDisplay(functionViewableMath),
    views.html.mathml.mathmlDisplay(atPointXViewableMath)
  ) // TODO

  def functionViewableMath = new ViewableMath { val mathML = function; val rawStr = functionStr }

  def atPointXViewableMath = new ViewableMath { val mathML = atPointX; val rawStr = atPointXStr }
}
