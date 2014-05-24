package models.question.derivative.table

import play.api.db.slick.Config.driver.simple._
import scala.slick.model.ForeignKeyAction
import service.table._
import service._
import models.support._
import models.organization.assignment.table._

case class QuestionFor(groupId :GroupId, questionId: QuestionId, userId: UserId)

class QuestionForTable(tag: Tag) extends Table[QuestionFor](tag, "derivative_question_for") {
  def groupId = column[GroupId]("group_id", O.NotNull)
  def questionId = column[QuestionId]("question_id", O.NotNull)
	def userId = column[UserId]("user_id", O.NotNull)
	def * = (groupId, questionId, userId)  <> (QuestionFor.tupled, QuestionFor.unapply _)

	def pk = primaryKey("derivative_question_for_pk", (groupId, questionId, userId))

  def groupIdFK = foreignKey("derivative_question_for_group_fk", groupId, assignmentGroupsTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def userIdFK = foreignKey("derivative_question_for_user_fk", userId, UserTable.userTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def questionIdFK = foreignKey("derivative_question_for_question_fk", questionId, questionsTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}