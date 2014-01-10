package models.question.derivative.table

import scala.slick.session.Session
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.ForeignKeyAction
import service.table._
import service._
import models.id.Ids._
import models.id._

case class User2Question(userId: UID, questionId: QuestionId)

object UsersQuestionsTable extends Table[User2Question]("derivative_users_questions") {
	def userId = column[UID]("user_id", O.NotNull)
	def questionId = column[QuestionId]("question_id", O.NotNull)
	def * = userId ~ questionId <> (User2Question, User2Question.unapply _)

	def pk = primaryKey("derivative_users_questions_pk", (userId, questionId))

	def userIdFK = foreignKey("derivative_users_questions_user_fk", userId, UserTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def questionIdFK = foreignKey("derivative_users_questions_question_fk", questionId, QuestionsTable)(_.id, onDelete = ForeignKeyAction.Cascade)

	def insert(owner: User, question: QuestionId)(implicit s: Session) { this.insert(User2Question(owner.id, question)) }
}