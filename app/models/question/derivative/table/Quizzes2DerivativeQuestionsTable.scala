package models.question.derivative.table

import play.api.db.slick.Config.driver.simple._
import scala.slick.model.ForeignKeyAction
import models.support._

case class Quiz2DerivativeQuestion(quizId: QuizId, questionId: QuestionId, order: Int = 1)

class Quizzes2DerivativeQuestionsTable(tag: Tag) extends Table[Quiz2DerivativeQuestion](tag, "quizzes_2_derivative_questions") {
	def quizId = column[QuizId]("quiz_id")
	def questionId = column[QuestionId]("question_id")
  def order = column[Int]("order")
	def * = (quizId, questionId, order) <> (Quiz2DerivativeQuestion.tupled, Quiz2DerivativeQuestion.unapply _)

	def pk = primaryKey("derivative_quizzes_2_questions_pk", (questionId, quizId))

	def quizIdFK = foreignKey("derivative_quizzes_2_questions_quiz_fk", quizId, quizzesTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def questionIdFK = foreignKey("derivative_quizzes_2_questions_question_fk", questionId, questionsTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}