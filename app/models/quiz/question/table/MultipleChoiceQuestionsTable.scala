package models.quiz.question.table

import models.quiz.question.{Question2Quiz, MultipleChoiceQuestion}
import models.quiz.table._
import play.api.db.slick.Config.driver.simple._
import service.table.LoginsTable

import scala.slick.model.ForeignKeyAction

class MultipleChoiceQuestionsTable(tag: Tag) extends Table[MultipleChoiceQuestion](tag, "multiple_choice_questions") with QuestionsTable[MultipleChoiceQuestion] {
	def description   = column[String]("description")
	def explanation   = column[String]("explanation")
	def correctAnswer = column[Short]("correct_answer")

	def * = (id, ownerId, description, explanation, correctAnswer, creationDate, atCreationDifficulty, order) <> (MultipleChoiceQuestion.tupled, MultipleChoiceQuestion.unapply _)

	def idFK = foreignKey("multiple_choice_questions__id_fk", id, QuestionIdNext.questionIdTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def ownerFK = foreignKey("multiple_choice_questions__owner_fk", ownerId, LoginsTable.loginTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}

class MultipleChoiceQuestion2QuizTable(tag: Tag) extends Table[Question2Quiz](tag, "multiple_choice_question_2_quiz") with Question2QuizTable {

	def * = (questionId, quizId, ownerId, creationDate, order) <> (Question2Quiz.tupled, Question2Quiz.unapply _)

	def questionFK = foreignKey("multiple_choice_question_2_quiz__question_fk", questionId, multipleChoiceQuestionsTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def quizIdFK = foreignKey("multiple_choice_question_2_quiz__quiz_fk", quizId, quizzesTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def questionIndex = index("multiple_choice_question_2_quiz__question_index", questionId)
	def quizIndex = index("multiple_choice_question_2_quiz__quiz_index", quizId)
}