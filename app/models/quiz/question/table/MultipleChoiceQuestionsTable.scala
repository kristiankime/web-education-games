package models.quiz.question.table

import models.quiz.question.MultipleChoiceQuestion
import models.quiz.table.{QuestionIdNext, quizzesTable}
import play.api.db.slick.Config.driver.simple._
import service.table.LoginsTable

import scala.slick.model.ForeignKeyAction

class MultipleChoiceQuestionsTable(tag: Tag) extends Table[MultipleChoiceQuestion](tag, "multiple_choice_questions") with QuestionsTable[MultipleChoiceQuestion] {
	def description   = column[String]("description")
	def explanation   = column[String]("explanation")
	def correctAnswer = column[Short]("correct_answer")

	def * = (id, ownerId, description, explanation, correctAnswer, creationDate, atCreationDifficulty, quizId, order) <> (MultipleChoiceQuestion.tupled, MultipleChoiceQuestion.unapply _)

	def idFK = foreignKey("multiple_choice_questions__id_fk", id, QuestionIdNext.questionIdTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def ownerFK = foreignKey("multiple_choice_questions__owner_fk", ownerId, LoginsTable.loginTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def quizIdFK = foreignKey("multiple_choice_questions__quiz_fk", quizId, quizzesTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}
