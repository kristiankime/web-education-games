package models.question.derivative.table

import play.api.db.slick.Config.driver.simple._
import scala.slick.model.ForeignKeyAction
import service._
import service.table._
import models.support._

case class User2DerivativeAnswer(userId: UserId, answerId: AnswerId, access: Access)

class Users2DerivativeAnswersTable(tag: Tag) extends Table[User2DerivativeAnswer](tag, "users_2_derivative_answers") {
	def userId = column[UserId]("user_id")
	def answerId = column[AnswerId]("answer_id")
	def access = column[Access]("access")
	def * = (userId, answerId, access) <> (User2DerivativeAnswer.tupled, User2DerivativeAnswer.unapply _)

	def pk = primaryKey("users_2_derivative_answers_pk", (userId, answerId))

	def userIdFK = foreignKey("users_2_derivative_answers_user_fk", userId, UsersTable.userTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def answerIdFK = foreignKey("users_2_derivative_answers_question_fk", answerId, answersTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}