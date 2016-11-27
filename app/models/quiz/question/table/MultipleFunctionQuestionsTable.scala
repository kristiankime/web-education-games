package models.quiz.question.table

import models.quiz.question.{Question2Quiz, MultipleFunctionQuestion}
import models.quiz.table._
import play.api.db.slick.Config.driver.simple._
import play.api.templates.Html
import service.table.LoginsTable
import models.support._
import scala.slick.model.ForeignKeyAction

class MultipleFunctionQuestionsTable(tag: Tag) extends Table[MultipleFunctionQuestion](tag, "multiple_function_questions") with QuestionsTable[MultipleFunctionQuestion] {
	def description   = column[String]("description")
	def explanation   = column[Html]("explanation")

	def * = (id, ownerId, description, explanation, creationDate, atCreationDifficulty, order) <> (MultipleFunctionQuestion.tupled, MultipleFunctionQuestion.unapply _)

	def idFK = foreignKey("multiple_function_questions__id_fk", id, QuestionIdNext.questionIdTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def ownerFK = foreignKey("multiple_function_questions__owner_fk", ownerId, LoginsTable.loginTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}

class MultipleFunctionQuestion2QuizTable(tag: Tag) extends Table[Question2Quiz](tag, "multiple_function_question_2_quiz") with Question2QuizTable {

	def * = (questionId, quizId, ownerId, creationDate, order) <> (Question2Quiz.tupled, Question2Quiz.unapply _)

	def questionFK = foreignKey("multiple_function_question_2_quiz__question_fk", questionId, multipleFunctionQuestionsTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def quizIdFK = foreignKey("multiple_function_question_2_quiz__quiz_fk", quizId, quizzesTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def questionIndex = index("multiple_function_question_2_quiz__question_index", questionId)
	def quizIndex = index("multiple_function_question_2_quiz__quiz_index", quizId)
}