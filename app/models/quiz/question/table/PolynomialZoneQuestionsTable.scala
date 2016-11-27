package models.quiz.question.table

import com.artclod.mathml.scalar._
import models.quiz.question.support.PolynomialZoneType
import models.quiz.question.{Question2Quiz, PolynomialZoneQuestion, DerivativeQuestion}
import models.quiz.table._
import play.api.db.slick.Config.driver.simple._
import service.table.LoginsTable
import models.support._

import scala.slick.model.ForeignKeyAction

class PolynomialZoneQuestionsTable(tag: Tag) extends Table[PolynomialZoneQuestion](tag, "polynomial_zone_questions") with QuestionsTable[PolynomialZoneQuestion] {
	def roots = column[Vector[Int]]("roots")
	def scale = column[Double]("scale")
	def zoneType = column[PolynomialZoneType]("zone_type")

	def * = (id, ownerId, roots, scale, zoneType, creationDate, atCreationDifficulty, order) <> (PolynomialZoneQuestion.tupled, PolynomialZoneQuestion.unapply _)

	def idFK = foreignKey("polynomial_zone_questions__id_fk", id, QuestionIdNext.questionIdTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def ownerFK = foreignKey("polynomial_zone_questions__owner_fk", ownerId, LoginsTable.loginTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}

class PolynomialZoneQuestion2QuizTable(tag: Tag) extends Table[Question2Quiz](tag, "polynomial_zone_question_2_quiz") with Question2QuizTable {

	def * = (questionId, quizId, ownerId, creationDate, order) <> (Question2Quiz.tupled, Question2Quiz.unapply _)

	def questionFK = foreignKey("polynomial_zone_question_2_quiz__question_fk", questionId, polynomialZoneQuestionsTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def quizIdFK = foreignKey("polynomial_zone_question_2_quiz__quiz_fk", quizId, quizzesTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def questionIndex = index("polynomial_zone_question_2_quiz__question_index", questionId)
	def quizIndex = index("polynomial_zone_question_2_quiz__quiz_index", quizId)
}