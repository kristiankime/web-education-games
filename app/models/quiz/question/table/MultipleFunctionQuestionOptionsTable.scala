package models.quiz.question.table

import com.artclod.mathml.scalar.MathMLElem
import models.quiz.question.MultipleFunctionQuestionOption
import models.quiz.table.multipleFunctionQuestionsTable
import models.support._
import play.api.db.slick.Config.driver.simple._
import play.api.templates.Html
import scala.slick.model.ForeignKeyAction


class MultipleFunctionQuestionOptionsTable(tag: Tag) extends Table[MultipleFunctionQuestionOption](tag, "multiple_function_question_options") {
	def id         = column[Long]("id", O.AutoInc, O.PrimaryKey)
	def questionId = column[QuestionId]("question_id")
	def optionRaw  = column[String]("option_raw")
	def optionHtml = column[Html]("option_html")
	def mathML     = column[MathMLElem]("mathml")
	def rawStr     = column[String]("rawstr")

	def * = (id, questionId, optionRaw, optionHtml, mathML, rawStr) <> (MultipleFunctionQuestionOption.tupled, MultipleFunctionQuestionOption.unapply _)

	def questionIdFK = foreignKey("multiple_function_question_options__question_id_fk", questionId, multipleFunctionQuestionsTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}
