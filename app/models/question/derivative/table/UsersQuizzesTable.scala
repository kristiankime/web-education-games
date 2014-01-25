package models.question.derivative.table

import scala.slick.session.Session
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.ForeignKeyAction
import service.table._
import service._
import models.question.derivative._
import models.id._

case class User2Quiz(userId: UserId, quizId: QuizId, access: Access)

class UsersQuizzesTable extends Table[User2Quiz]("derivative_users_quizzes") {
	def userId = column[UserId]("user_id", O.NotNull)
	def quizId = column[QuizId]("quiz_id", O.NotNull)
	def access = column[Access]("access", O.NotNull) 
	def * = userId ~ quizId ~ access <> (User2Quiz, User2Quiz.unapply _)

	def pk = primaryKey("derivative_users_quiz_pk", (userId, quizId))

	def userIdFK = foreignKey("derivative_users_quizzes_user_fk", userId, new UserTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def quizIdFK = foreignKey("derivative_users_quizzes_quiz_fk", quizId, new QuizzesTable)(_.id, onDelete = ForeignKeyAction.Cascade)

//	def insert(owner: User, quizId: QuizId)(implicit s: Session) { this.insert(User2Quiz(owner.id, quizId, Own)) }
}