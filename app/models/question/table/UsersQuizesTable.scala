package models.question.table

import scala.slick.session.Session
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.ForeignKeyAction
import models.question.table._
import service.table._
import service.User

case class User2Quiz(userId: Long, questionId: Long)

object UsersQuuizTable extends Table[User2Question]("derivative_users_quiz") {
	def userId = column[Long]("user_id", O.NotNull)
	def quizId = column[Long]("quiz_id", O.NotNull)
	def * = userId ~ quizId <> (User2Question, User2Question.unapply _)

	def pk = primaryKey("derivative_users_quiz_pk", (userId, quizId))

	def userIdFK = foreignKey("derivative_users_quiz_user_fk", userId, UserTable)(_.uid, onDelete = ForeignKeyAction.Cascade)
	def questionIdFK = foreignKey("derivative_users_quiz_quiz_fk", quizId, QuizesTable)(_.id, onDelete = ForeignKeyAction.Cascade)

	def insert(owner: User, question: Long)(implicit s: Session) { this.insert(User2Question(owner.uid, question)) }
}