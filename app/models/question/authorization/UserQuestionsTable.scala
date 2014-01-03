package models.question.authorization

import scala.slick.session.Session
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.ForeignKeyAction
import models.security.UserTable
import models.security.User
import models.question.table.DerivativeQuestionsTable

object UserQuestionsTable extends Table[(Long, Long)]("user_questions") {
	def userId = column[Long]("user_id", O.NotNull)
	def questionId = column[Long]("question_id", O.NotNull)
	def * = userId ~ questionId

	def pk = primaryKey("user_questions_pk", (userId, questionId))

	def userIdFK = foreignKey("user_questions_user_fk", userId, UserTable)(_.uid, onDelete = ForeignKeyAction.Cascade)
	def questionIdFK = foreignKey("user_questions_question_fk", questionId, DerivativeQuestionsTable)(_.id, onDelete = ForeignKeyAction.Cascade)

	def create(owner: User, question: Long)(implicit s: Session) { this.insert((owner.uid, question)) }
}