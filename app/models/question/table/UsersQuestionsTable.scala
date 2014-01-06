package models.question.table

import scala.slick.session.Session
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.ForeignKeyAction
import models.question.table._
import service.User
import service.table.UserTable

case class User2Question(userId: Long, questionId: Long)

object UsersQuestionsTable extends Table[User2Question]("derivative_users_questions") {
	def userId = column[Long]("user_id", O.NotNull)
	def questionId = column[Long]("question_id", O.NotNull)
	def * = userId ~ questionId <> (User2Question, User2Question.unapply _)

	def pk = primaryKey("derivative_users_questions_pk", (userId, questionId))

	def userIdFK = foreignKey("derivative_users_questions_user_fk", userId, UserTable)(_.uid, onDelete = ForeignKeyAction.Cascade)
	def questionIdFK = foreignKey("derivative_users_questions_question_fk", questionId, QuestionsTable)(_.id, onDelete = ForeignKeyAction.Cascade)

	def insert(owner: User, question: Long)(implicit s: Session) { this.insert(User2Question(owner.uid, question)) }
}