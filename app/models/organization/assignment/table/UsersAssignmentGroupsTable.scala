package models.organization.assignment.table

import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.ForeignKeyAction
import service.table._
import service._
import models.support._
import models.organization.table._

case class User2AssignmentGroup(userId: UserId, assignmentGroupId: AssignmentGroupId)


class UsersAssignmentGroupsTable extends Table[User2AssignmentGroup]("users_assignment_groups") {
  def userId = column[UserId]("user_id", O.NotNull)
  def id = column[AssignmentGroupId]("assignment_group_id", O.NotNull)

  def * = userId ~ id <> (User2AssignmentGroup, User2AssignmentGroup.unapply _)

  def pk = primaryKey("users_assignment_groups_pk", (userId, id))

  def userIdFK = foreignKey("users_assignment_groups_user_fk", userId, new UserTable)(_.id, onDelete = ForeignKeyAction.Cascade)
  def assignmentGroupIdFK = foreignKey("users_assignment_groups_assignment_group_fk", id, new AssignmentGroupsTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}
