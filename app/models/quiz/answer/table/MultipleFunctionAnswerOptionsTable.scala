package models.quiz.answer.table

import com.artclod.mathml.scalar.MathMLElem
import com.artclod.mathml.slick.MathMLMapper._
import models.quiz.answer.MultipleFunctionAnswerOption
import models.quiz.question.MultipleFunctionQuestionOption
import models.quiz.table.multipleFunctionAnswersTable
import models.support.{AnswerId, QuestionId}
import play.api.db.slick.Config.driver.simple._

import scala.slick.model.ForeignKeyAction


class MultipleFunctionAnswerOptionsTable(tag: Tag) extends Table[MultipleFunctionAnswerOption](tag, "multiple_function_answer_options") {
	def id         = column[Long]("id", O.AutoInc, O.PrimaryKey)
	def answerId   = column[AnswerId]("answer_id")
	def mathML     = column[MathMLElem]("mathml")
	def rawStr     = column[String]("rawstr")
	def correct    = column[Short]("correct") // Note this represent a Match in the Answers Option Class, kept as a number for aggregation purposes

	def * = (id, answerId, mathML, rawStr, correct) <> (MultipleFunctionAnswerOption.tupled, MultipleFunctionAnswerOption.unapply _)

	def questionIdFK = foreignKey("multiple_function_answer_options__answer_id_fk", answerId, multipleFunctionAnswersTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}
