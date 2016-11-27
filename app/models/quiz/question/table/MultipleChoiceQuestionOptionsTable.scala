package models.quiz.question.table

import models.quiz.question.{MultipleChoiceQuestionOption, MultipleChoiceQuestion}
import models.quiz.table.{QuestionIdNext, quizzesTable}
import models.support.QuestionId
import play.api.db.slick.Config.driver.simple._
import play.api.templates.Html
import service.table.LoginsTable
import models.support._
import scala.slick.direct.order
import scala.slick.model.ForeignKeyAction
import models.quiz.table.{QuestionIdNext, multipleChoiceAnswersTable, multipleChoiceQuestionsTable, quizzesTable}

class MultipleChoiceQuestionOptionsTable(tag: Tag) extends Table[MultipleChoiceQuestionOption](tag, "multiple_choice_question_options") {
	def id         = column[Long]("id", O.AutoInc, O.PrimaryKey)
	def questionId = column[QuestionId]("question_id")
	def option     = column[Html]("option")

	def * = (id, questionId, option) <> (MultipleChoiceQuestionOption.tupled, MultipleChoiceQuestionOption.unapply _)

	def questionIdFK = foreignKey("multiple_choice_question_options__question_id_fk", questionId, multipleChoiceQuestionsTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}

