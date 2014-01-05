package models.question.table

import mathml._
import mathml.scalar._
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.ForeignKeyAction

case class Question2Quiz(questionId: Long, quizId: Long)

object QuizQuestionsTable extends Table[Question2Quiz]("derivative_question_set_links") {
	def questionId = column[Long]("question_id", O.NotNull)
	def questionSetId = column[Long]("question_set_id", O.NotNull)
	def * = questionId ~ questionSetId <> (Question2Quiz, Question2Quiz.unapply _)

	def pk = primaryKey("derivative_question_set_links_pk", (questionId, questionSetId))

	def questionIdFK = foreignKey("derivative_question_set_links_question_fk", questionId, QuestionsTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def questionSetIdFK = foreignKey("derivative_question_set_links_question_set_fk", questionSetId, QuizesTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}