package models.question.table

import scala.slick.session.Session
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.ForeignKeyAction
import models.question.table._
import service.table.User
import service.table.UserTable

case class User2Answer(userId: Long, answerId: Long)

object UsersAnswersTable extends Table[User2Answer]("derivative_users_answers") {
	def userId = column[Long]("user_id", O.NotNull)
	def answerId = column[Long]("answer_id", O.NotNull)
	def * = userId ~ answerId <> (User2Answer, User2Answer.unapply _)

	def pk = primaryKey("derivative_users_answers_pk", (userId, answerId))

	def userIdFK = foreignKey("derivative_users_answers_user_fk", userId, UserTable)(_.uid, onDelete = ForeignKeyAction.Cascade)
	def questionIdFK = foreignKey("derivative_users_answers_question_fk", answerId, QuestionsTable)(_.id, onDelete = ForeignKeyAction.Cascade)

	def insert(answerer: User, answerId: Long)(implicit s: Session) { this.insert(User2Answer(answerer.uid, answerId)) }
}