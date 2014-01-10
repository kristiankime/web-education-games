package models.question.derivative.table

import scala.slick.session.Session
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.ForeignKeyAction
import service._
import service.table._
import models.question.derivative.table.MathMLMapper._
import models.question.derivative._
import models.id.Ids._
import models.id._

case class User2Answer(userId: UID, answerId: Long)

object UsersAnswersTable extends Table[User2Answer]("derivative_users_answers") {
	def userId = column[UID]("user_id", O.NotNull)
	def answerId = column[Long]("answer_id", O.NotNull)
	def * = userId ~ answerId <> (User2Answer, User2Answer.unapply _)

	def pk = primaryKey("derivative_users_answers_pk", (userId, answerId))

	def userIdFK = foreignKey("derivative_users_answers_user_fk", userId, UserTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def answerIdFK = foreignKey("derivative_users_answers_question_fk", answerId, AnswersTable)(_.id, onDelete = ForeignKeyAction.Cascade)

	def insert(answerer: User, answerId: Long)(implicit s: Session) { this.insert(User2Answer(answerer.id, answerId)) }
}