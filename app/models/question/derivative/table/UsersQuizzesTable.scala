package models.question.derivative.table

import scala.slick.session.Session
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.ForeignKeyAction
import service.table._
import service._
import models.question.derivative._

case class User2Quiz(userId: Long, quizId: Long)

object UsersQuizzesTable extends Table[User2Quiz]("derivative_users_quizzes") {
	def userId = column[Long]("user_id", O.NotNull)
	def quizId = column[Long]("quiz_id", O.NotNull)
	def * = userId ~ quizId <> (User2Quiz, User2Quiz.unapply _)

	def pk = primaryKey("derivative_users_quiz_pk", (userId, quizId))

	def userIdFK = foreignKey("derivative_users_quizzes_user_fk", userId, UserTable)(_.uid, onDelete = ForeignKeyAction.Cascade)
	def questionIdFK = foreignKey("derivative_users_quizzes_quiz_fk", quizId, QuizesTable)(_.id, onDelete = ForeignKeyAction.Cascade)

	def insert(owner: User, quiz: Long)(implicit s: Session) { this.insert(User2Quiz(owner.uid, quiz)) }
}