package models.quiz.answer.table

import models.quiz.answer.{MultipleFunctionAnswer, MultipleChoiceAnswer}
import models.quiz.table.{AnswerIdNext, multipleFunctionQuestionsTable}
import play.api.db.slick.Config.driver.simple._
import service.table.LoginsTable

import scala.slick.model.ForeignKeyAction

class MultipleFunctionAnswersTable(tag: Tag) extends Table[MultipleFunctionAnswer](tag, "multiple_function_answers") with AnswersTable[MultipleFunctionAnswer] {

	def * = (id, ownerId, questionId, comment, correct, creationDate) <> (MultipleFunctionAnswer.tupled,MultipleFunctionAnswer.unapply _)

  def idFK  = foreignKey("multiple_function_answers__id_fk", id, AnswerIdNext.answerIdTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def ownerFK = foreignKey("multiple_function_answers__owner_fk", ownerId, LoginsTable.loginTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def questionFK = foreignKey("multiple_function_answers__question_fk", questionId, multipleFunctionQuestionsTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}
