package models.quiz.answer.table

import models.quiz.answer.MultipleChoiceAnswer
import models.quiz.table.{AnswerIdNext, multipleChoiceQuestionsTable}
import play.api.db.slick.Config.driver.simple._
import service.table.LoginsTable

import scala.slick.model.ForeignKeyAction

class MultipleChoiceAnswersTable(tag: Tag) extends Table[MultipleChoiceAnswer](tag, "multiple_choice_answers") with AnswersTable[MultipleChoiceAnswer] {
	def guessIndex = column[Short]("guess_index")

	def * = (id, ownerId, questionId, guessIndex, comment, correct, creationDate) <> (MultipleChoiceAnswer.tupled, MultipleChoiceAnswer.unapply _)

  def idFK  = foreignKey("multiple_choice_answers__id_fk", id, AnswerIdNext.answerIdTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def ownerFK = foreignKey("multiple_choice_answers__owner_fk", ownerId, LoginsTable.loginTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def questionFK = foreignKey("multiple_choice_answers__question_fk", questionId, multipleChoiceQuestionsTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}
