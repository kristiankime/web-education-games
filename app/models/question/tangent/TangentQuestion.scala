package models.question.tangent

import com.artclod.mathml.scalar.MathMLElem
import models.organization.Course
import models.question._
import models.question.derivative.result.QuestionResults
import models.support.{QuestionId, UserId, _}
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import service._

case class TangentQuestion(id: QuestionId, ownerId: UserId, function: MathMLElem, functionStr: String, atPointX: Double, creationDate: DateTime, quizIdOp: Option[QuizId] = None, order: Int = 1) extends Question {

}
