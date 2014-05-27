package models.question.derivative.table

import play.api.db.slick.Config.driver.simple._
import scala.slick.model.ForeignKeyAction
import service._
import service.table._
import models.support._

case class User2Answer(userId: UserId, answerId: AnswerId, access: Access)

class UsersAnswersTable(tag: Tag) extends Table[User2Answer](tag, "users_2_derivative_answers") {
	def userId = column[UserId]("user_id", O.NotNull)
	def answerId = column[AnswerId]("answer_id", O.NotNull)
	def access = column[Access]("access", O.NotNull)
	def * = (userId, answerId, access) <> (User2Answer.tupled, User2Answer.unapply _)

	def pk = primaryKey("users_2_derivative_answers_pk", (userId, answerId))

	def userIdFK = foreignKey("users_2_derivative_answers_user_fk", userId, UsersTable.userTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def answerIdFK = foreignKey("users_2_derivative_answers_question_fk", answerId, answersTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}