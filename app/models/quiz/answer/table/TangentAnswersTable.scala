package models.quiz.answer.table

import com.artclod.mathml.scalar._
import com.artclod.mathml.slick.MathMLMapper.string2mathML
import com.artclod.slick.JodaUTC._
import models.quiz.answer.TangentAnswer
import models.quiz.table.tangentQuestionsTable
import models.support._
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import service.table.LoginsTable

import scala.slick.model.ForeignKeyAction

class TangentAnswersTable(tag: Tag) extends Table[TangentAnswer](tag, "tangent_answers") {
	def id = column[AnswerId]("id", O.PrimaryKey, O.AutoInc)
	def questionId = column[QuestionId]("question_id")
	def ownerId = column[UserId]("owner")
  def slopeMathML = column[MathMLElem]("slope_mathml")
	def slopeRawStr = column[String]("slope_rawstr")
  def interceptMathML = column[MathMLElem]("intercept_mathml")
  def interceptRawStr = column[String]("intercept_rawstr")
  def correct = column[Short]("correct") // Note this represent a Boolean in the Answers Class, kept as a number for aggregation purposes
	def creationDate = column[DateTime]("creation_date")

	def * = (id, ownerId, questionId, slopeMathML, slopeRawStr, interceptMathML, interceptRawStr, correct, creationDate) <> (TangentAnswer.tupled, TangentAnswer.unapply _)

	def ownerFK = foreignKey("tangent_answers__owner_fk", ownerId, LoginsTable.loginTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def questionFK = foreignKey("tangent_answers_question_fk", questionId, tangentQuestionsTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}
