package models.quiz.answer.table

import com.artclod.mathml.scalar._
import com.artclod.slick.JodaUTC._
import models.quiz.answer.TangentAnswer
import models.quiz.table.{AnswerIdNext, tangentQuestionsTable}
import models.support._
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import service.table.LoginsTable

import scala.slick.model.ForeignKeyAction

class TangentAnswersTable(tag: Tag) extends Table[TangentAnswer](tag, "tangent_answers") with AnswersTable[TangentAnswer] {
  def slopeMathML = column[MathMLElem]("slope_mathml")
	def slopeRawStr = column[String]("slope_rawstr")
  def interceptMathML = column[MathMLElem]("intercept_mathml")
  def interceptRawStr = column[String]("intercept_rawstr")

	def * = (id, ownerId, questionId, slopeMathML, slopeRawStr, interceptMathML, interceptRawStr, comment, correct, creationDate) <> (TangentAnswer.tupled, TangentAnswer.unapply _)

  def idFK  = foreignKey("tangent_answers__id_fk", id, AnswerIdNext.answerIdTable)(_.id, onDelete = ForeignKeyAction.Cascade)
  def ownerFK = foreignKey("tangent_answers__owner_fk", ownerId, LoginsTable.loginTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def questionFK = foreignKey("tangent_answers__question_fk", questionId, tangentQuestionsTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}
