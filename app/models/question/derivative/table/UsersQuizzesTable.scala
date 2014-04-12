package models.question.derivative.table

import scala.slick.session.Session
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.ForeignKeyAction
import service.table._
import service._
import models.question.derivative._
import models.support._

case class User2Quiz(userId: UserId, quizId: QuizId, access: Access) extends UserLinkRow

class UsersQuizzesTable extends Table[User2Quiz]("derivative_users_quizzes") with UserLink[User2Quiz, QuizId] {
	def userId = column[UserId]("user_id", O.NotNull)
	def id = column[QuizId]("quiz_id", O.NotNull)
	def access = column[Access]("access", O.NotNull) 
	def * = userId ~ id ~ access <> (User2Quiz, User2Quiz.unapply _)

	def pk = primaryKey("derivative_users_quiz_pk", (userId, id))

	def userIdFK = foreignKey("derivative_users_quizzes_user_fk", userId, new UserTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def quizIdFK = foreignKey("derivative_users_quizzes_quiz_fk", id, new QuizzesTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}