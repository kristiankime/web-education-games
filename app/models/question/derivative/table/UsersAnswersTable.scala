package models.question.derivative.table

import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.ForeignKeyAction
import service._
import service.table._
import models.support._

case class User2Answer(userId: UserId, answerId: AnswerId, access: Access)

class UsersAnswersTable extends Table[User2Answer]("derivative_users_answers") {
	def userId = column[UserId]("user_id", O.NotNull)
	def answerId = column[AnswerId]("answer_id", O.NotNull)
	def access = column[Access]("access", O.NotNull)
	def * = userId ~ answerId ~ access <> (User2Answer, User2Answer.unapply _)

	def pk = primaryKey("derivative_users_answers_pk", (userId, answerId))

	def userIdFK = foreignKey("derivative_users_answers_user_fk", userId, new UserTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def answerIdFK = foreignKey("derivative_users_answers_question_fk", answerId, new AnswersTable)(_.id, onDelete = ForeignKeyAction.Cascade)

	def insert(answerer: User, answerId: AnswerId)(implicit s: Session) { this.insert(User2Answer(answerer.id, answerId, Own)) }
}