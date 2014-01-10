package models.question.derivative.table

import scala.slick.session.Session
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.ForeignKeyAction
import service.table._
import service._
import models.question.derivative._
import models.id.Ids._
import models.id._

case class User2Quiz(userId: UID, quizId: Long)

object UsersQuizzesTable extends Table[User2Quiz]("derivative_users_quizzes") {
	def userId = column[UID]("user_id", O.NotNull)
	def quizId = column[Long]("quiz_id", O.NotNull)
	def * = userId ~ quizId <> (User2Quiz, User2Quiz.unapply _)

	def pk = primaryKey("derivative_users_quiz_pk", (userId, quizId))

	def userIdFK = foreignKey("derivative_users_quizzes_user_fk", userId, UserTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def quizIdFK = foreignKey("derivative_users_quizzes_quiz_fk", quizId, QuizesTable)(_.id, onDelete = ForeignKeyAction.Cascade)

	def insert(owner: User, quizId: Long)(implicit s: Session) { this.insert(User2Quiz(owner.id, quizId)) }
}