package models.quiz.question.table

import models.quiz.question.MultipleFunctionQuestion
import models.quiz.table.{QuestionIdNext, quizzesTable}
import play.api.db.slick.Config.driver.simple._
import service.table.LoginsTable

import scala.slick.model.ForeignKeyAction

class MultipleFunctionQuestionsTable(tag: Tag) extends Table[MultipleFunctionQuestion](tag, "multiple_function_questions") with QuestionsTable[MultipleFunctionQuestion] {
	def description   = column[String]("description")
	def explanation   = column[String]("explanation")

	def * = (id, ownerId, description, explanation, creationDate, atCreationDifficulty, quizId, order) <> (MultipleFunctionQuestion.tupled, MultipleFunctionQuestion.unapply _)

	def idFK = foreignKey("multiple_function_questions__id_fk", id, QuestionIdNext.questionIdTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def ownerFK = foreignKey("multiple_function_questions__owner_fk", ownerId, LoginsTable.loginTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def quizIdFK = foreignKey("multiple_function_questions__quiz_fk", quizId, quizzesTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}
