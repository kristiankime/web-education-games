package models.question.derivative.table

import play.api.db.slick.Config.driver.simple._
import scala.slick.model.ForeignKeyAction
import service.table._
import service._
import models.support._

case class QuestionFor(questionId: QuestionId, userId: UserId)

class QuestionForTable(tag: Tag) extends Table[QuestionFor](tag, "derivative_question_for") {
  def questionId = column[QuestionId]("question_id", O.NotNull)
	def userId = column[UserId]("user_id", O.NotNull)
	def * = (questionId, userId)  <> (QuestionFor.tupled, QuestionFor.unapply _)

	def pk = primaryKey("derivative_question_for_pk", (questionId, userId))

	def userIdFK = foreignKey("derivative_question_for_user_fk", userId, UserTable.userTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def questionIdFK = foreignKey("derivative_question_for_question_fk", questionId, questionsTable)(_.id, onDelete = ForeignKeyAction.Cascade)

//	def insert(question: QuestionId, user: User)(implicit s: Session) { this.insert(QuestionFor(question, user.id)) }
}