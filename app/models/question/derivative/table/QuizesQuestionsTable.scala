package models.question.derivative.table

import mathml._
import mathml.scalar._
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.ForeignKeyAction
import models.question.derivative._

case class Quiz2Question(quizId: Long, questionId: Long)

object QuizesQuestionsTable extends Table[Quiz2Question]("derivative_quizes_question") {
	def quizId = column[Long]("quiz_id", O.NotNull)
	def questionId = column[Long]("question_id", O.NotNull)
	def * = quizId ~ questionId <> (Quiz2Question, Quiz2Question.unapply _)

	def pk = primaryKey("derivative_quizes_question_pk", (questionId, quizId))

	def questionIdFK = foreignKey("derivative_quizes_question_question_fk", questionId, QuestionsTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def questionSetIdFK = foreignKey("derivative_quizes_question_quiz_fk", quizId, QuizesTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}