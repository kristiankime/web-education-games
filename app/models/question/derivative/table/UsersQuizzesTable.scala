package models.question.derivative.table

import play.api.db.slick.Config.driver.simple._
import scala.slick.model.ForeignKeyAction
import service.table._
import service._
import models.support._

case class User2Quiz(userId: UserId, quizId: QuizId, access: Access) extends UserLinkRow

class UsersQuizzesTable(tag: Tag) extends Table[User2Quiz](tag, "derivative_users_quizzes") with UserLink[User2Quiz, QuizId] {
	def userId = column[UserId]("user_id", O.NotNull)
	def id = column[QuizId]("quiz_id", O.NotNull)
	def access = column[Access]("access", O.NotNull) 
	def * = (userId, id, access) <> (User2Quiz.tupled, User2Quiz.unapply _)

	def pk = primaryKey("derivative_users_quiz_pk", (userId, id))

	def userIdFK = foreignKey("derivative_users_quizzes_user_fk", userId, UsersTable.userTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def quizIdFK = foreignKey("derivative_users_quizzes_quiz_fk", id, quizzesTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}