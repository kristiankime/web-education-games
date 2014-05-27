package models.question.derivative.table

import play.api.db.slick.Config.driver.simple._
import scala.slick.model.ForeignKeyAction
import service.table._
import service._
import models.support._
import models.organization.assignment.table._

case class GroupQuestion2User(groupId :GroupId, questionId: QuestionId, userId: UserId)

class GroupQuestion2UserTable(tag: Tag) extends Table[GroupQuestion2User](tag, "group_question_2_user") {
  def groupId = column[GroupId]("group_id", O.NotNull)
  def questionId = column[QuestionId]("question_id", O.NotNull)
	def userId = column[UserId]("user_id", O.NotNull)
	def * = (groupId, questionId, userId)  <> (GroupQuestion2User.tupled, GroupQuestion2User.unapply _)

	def pk = primaryKey("group_question_2_user_pk", (groupId, questionId, userId))

  def groupIdFK = foreignKey("group_question_2_user_group_fk", groupId, assignmentGroupsTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def userIdFK = foreignKey("group_question_2_user_user_fk", userId, UsersTable.userTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def questionIdFK = foreignKey("group_question_2_user_question_fk", questionId, questionsTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}