package models.organization.assignment.table

import play.api.db.slick.Config.driver.simple._
import scala.slick.model.ForeignKeyAction
import service.table._
import service._
import models.support._
import models.organization.table._

case class User2Group(userId: UserId, groupId: GroupId)


class Users2GroupsTable(tag: Tag) extends Table[User2Group](tag, "users_2_assignment_groups") {
  def userId = column[UserId]("user_id", O.NotNull)
  def id = column[GroupId]("assignment_group_id", O.NotNull)

  def * = (userId, id) <> (User2Group.tupled, User2Group.unapply _)

  def pk = primaryKey("users_2_assignment_groups_pk", (userId, id))

  def userIdFK = foreignKey("users_2_assignment_groups_user_fk", userId, UsersTable.userTable)(_.id, onDelete = ForeignKeyAction.Cascade)
  def assignmentGroupIdFK = foreignKey("users_2_assignment_groups_assignment_group_fk", id, assignmentGroupsTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}
