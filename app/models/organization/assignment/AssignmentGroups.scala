package models.organization.assignment

import org.joda.time.DateTime
import service._
import models.support._
import models.organization.table._
import play.api.db.slick.Config.driver.simple._
import models.organization.assignment.table._
import service.table.UserTable

case class AssignmentGroupTmp(name: String, assignmentId: AssignmentId, date: DateTime) {
  def apply(id: AssignmentGroupId) = AssignmentGroup(id, name, assignmentId, date, date)
}

case class AssignmentGroup(id: AssignmentGroupId, name: String, assignmentId: AssignmentId, creationDate: DateTime, updateDate: DateTime) {

  def assignment(implicit session: Session) = Assignments.find(assignmentId).get

  def course(implicit session: Session) = assignment.course

  def students(implicit session: Session) = AssignmentGroups.students(id)

  def access(implicit user: User, session: Session): Access = assignment.access

}

object AssignmentGroups {

  // ======= CREATE ======
  def create(assignmentGroupTmp: AssignmentGroupTmp)(implicit session: Session) = ((new AssignmentGroupsTable).insert(assignmentGroupTmp))

  // ======= FIND ======
  def find(assignmentGroupId: AssignmentGroupId)(implicit session: Session) = Query(new AssignmentGroupsTable).where(a => a.id === assignmentGroupId).firstOption

  def find(assignmentId: AssignmentId)(implicit session: Session) = Query(new AssignmentGroupsTable).where(a => a.assignmentId === assignmentId).sortBy(_.name).list

  def students(assignmentGroupId: AssignmentGroupId)(implicit session: Session) =
    (for (
      u <- (new UserTable);
      ug <- (new UsersAssignmentGroupsTable) if ug.userId === u.id && ug.id === assignmentGroupId
    ) yield u).sortBy(_.lastName).list

  // ======= Update ======
  def join(userId: UserId, assignmentGroupId: AssignmentGroupId)(implicit session: Session) = (new UsersAssignmentGroupsTable).insert(User2AssignmentGroup(userId, assignmentGroupId))
}
