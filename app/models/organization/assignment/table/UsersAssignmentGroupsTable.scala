package models.organization.assignment.table

import play.api.db.slick.Config.driver.simple._
import scala.slick.model.ForeignKeyAction
import service.table._
import service._
import models.support._
import models.organization.table._

case class User2AssignmentGroup(userId: UserId, assignmentGroupId: GroupId)


class UsersAssignmentGroupsTable(tag: Tag) extends Table[User2AssignmentGroup](tag, "users_assignment_groups") {
  def userId = column[UserId]("user_id", O.NotNull)
  def id = column[GroupId]("assignment_group_id", O.NotNull)

  def * = (userId, id) <> (User2AssignmentGroup.tupled, User2AssignmentGroup.unapply _)

  def pk = primaryKey("users_assignment_groups_pk", (userId, id))

  def userIdFK = foreignKey("users_assignment_groups_user_fk", userId, UserTable.userTable)(_.id, onDelete = ForeignKeyAction.Cascade)
  def assignmentGroupIdFK = foreignKey("users_assignment_groups_assignment_group_fk", id, assignmentGroupsTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}
