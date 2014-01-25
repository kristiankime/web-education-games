package models.question.derivative.table

import scala.slick.session.Session
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.ForeignKeyAction
import service.table._
import service._
import models.id._

case class User2Question(userId: UserId, questionId: QuestionId, access: Access)

class UsersQuestionsTable extends Table[User2Question]("derivative_users_questions") {
	def userId = column[UserId]("user_id", O.NotNull)
	def questionId = column[QuestionId]("question_id", O.NotNull)
	def access = column[Access]("access", O.NotNull) 
	def * = userId ~ questionId ~ access <> (User2Question, User2Question.unapply _)

	def pk = primaryKey("derivative_users_questions_pk", (userId, questionId))

	def userIdFK = foreignKey("derivative_users_questions_user_fk", userId, new UserTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def questionIdFK = foreignKey("derivative_users_questions_question_fk", questionId, new QuestionsTable)(_.id, onDelete = ForeignKeyAction.Cascade)

	def insert(owner: User, question: QuestionId)(implicit s: Session) { this.insert(User2Question(owner.id, question, Own)) }
}