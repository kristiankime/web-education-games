package models.question.derivative.table

import play.api.db.slick.Config.driver.simple._
import scala.slick.model.ForeignKeyAction
import models.support._

case class Quiz2Question(quizId: QuizId, questionId: QuestionId)

class QuizzesQuestionsTable(tag: Tag) extends Table[Quiz2Question](tag, "derivative_quizzes_questions") {
	def quizId = column[QuizId]("quiz_id", O.NotNull)
	def questionId = column[QuestionId]("question_id", O.NotNull)
	def * = (quizId, questionId) <> (Quiz2Question.tupled, Quiz2Question.unapply _)

	def pk = primaryKey("derivative_quizzes_question_pk", (questionId, quizId))

	def quizIdFK = foreignKey("derivative_quizzes_question_quiz_fk", quizId, quizzesTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def questionIdFK = foreignKey("derivative_quizzes_question_question_fk", questionId, questionsTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}