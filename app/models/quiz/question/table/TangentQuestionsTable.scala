package models.quiz.question.table

import com.artclod.mathml.scalar._
import com.artclod.mathml.slick.MathMLMapper._
import com.artclod.slick.JodaUTC._
import models.quiz.question.{Question2Quiz, TangentQuestion}
import models.quiz.table._
import models.support._
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import service.table.LoginsTable

import scala.slick.model.ForeignKeyAction

class TangentQuestionsTable(tag: Tag) extends Table[TangentQuestion](tag, "tangent_questions") with QuestionsTable[TangentQuestion] {
	def function = column[MathMLElem]("function")
	def functionStr = column[String]("function_str")
	def atPointX = column[MathMLElem]("at_point_x")
	def atPointXStr = column[String]("at_point_x_str")

	def * = (id, ownerId, function, functionStr, atPointX, atPointXStr, creationDate, atCreationDifficulty, order) <> (TangentQuestion.tupled, TangentQuestion.unapply _)

	def idFK = foreignKey("tangent_questions__id_fk", id, QuestionIdNext.questionIdTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def ownerFK = foreignKey("tangent_questions__owner_fk", ownerId, LoginsTable.loginTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}

class TangentQuestion2QuizTable(tag: Tag) extends Table[Question2Quiz](tag, "tangent_question_2_quiz") with Question2QuizTable {

	def * = (questionId, quizId, ownerId, creationDate, order) <> (Question2Quiz.tupled, Question2Quiz.unapply _)

	def questionFK = foreignKey("tangent_question_2_quiz__question_fk", questionId, tangentQuestionsTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def quizIdFK = foreignKey("tangent_question_2_quiz__quiz_fk", quizId, quizzesTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def questionIndex = index("tangent_question_question_2_quiz__question_index", questionId)
	def quizIndex = index("tangent_question_question_2_quiz__quiz_index", quizId)
}