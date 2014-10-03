package models.question.derivative.table

import play.api.db.slick.Config.driver.simple._
import scala.slick.model.ForeignKeyAction
import service.table._
import service._
import models.support._
import models.support.table.{UserLinkRow, UserLink}

case class User2Quiz(userId: UserId, quizId: QuizId, access: Access) extends UserLinkRow

class Users2QuizzesTable(tag: Tag) extends Table[User2Quiz](tag, "users_2_derivative_quizzes") {
	def userId = column[UserId]("user_id")
	def quizId = column[QuizId]("quiz_id")
	def access = column[Access]("access")
	def * = (userId, quizId, access) <> (User2Quiz.tupled, User2Quiz.unapply _)

	def pk = primaryKey("users_2_derivative_quizzes_pk", (userId, quizId))

	def userIdFK = foreignKey("users_2_derivative_quizzes_user_fk", userId, UsersTable.userTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def quizIdFK = foreignKey("users_2_derivative_quizzes_quiz_fk", quizId, quizzesTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}