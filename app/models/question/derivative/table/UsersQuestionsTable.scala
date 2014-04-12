package models.question.derivative.table

import scala.slick.session.Session
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.ForeignKeyAction
import service.table._
import service._
import models.support._

case class User2Question(userId: UserId, questionId: QuestionId, access: Access) extends UserLinkRow

class UsersQuestionsTable extends Table[User2Question]("derivative_users_questions") with UserLink[User2Question, QuestionId] {
	def userId = column[UserId]("user_id", O.NotNull)
	def id = column[QuestionId]("question_id", O.NotNull)
	def access = column[Access]("access", O.NotNull) 
	def * = userId ~ id ~ access <> (User2Question, User2Question.unapply _)

	def pk = primaryKey("derivative_users_questions_pk", (userId, id))

	def userIdFK = foreignKey("derivative_users_questions_user_fk", userId, new UserTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def questionIdFK = foreignKey("derivative_users_questions_question_fk", id, new QuestionsTable)(_.id, onDelete = ForeignKeyAction.Cascade)

	def insert(owner: User, question: QuestionId)(implicit s: Session) { this.insert(User2Question(owner.id, question, Own)) }
}