package models.organization.assignment

import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import service._
import models.support._
import models.organization.assignment.table._
import models.organization.Sections
import service.table.UserTable

case class GroupTmp(name: String, sectionId: SectionId, assignmentId: AssignmentId, creationDate: DateTime, updateDate: DateTime) {
  def apply(id: GroupId) = Group(id, name, sectionId, assignmentId, creationDate, updateDate)
}

case class Group(id: GroupId, name: String, sectionId: SectionId, assignmentId: AssignmentId, creationDate: DateTime, updateDate: DateTime) {

  def assignment(implicit session: Session) = Assignments(assignmentId).get

  def course(implicit session: Session) = assignment.course

  def students(implicit session: Session) = Groups.students(id)

  def access(implicit user: User, session: Session): Access = assignment.access

  def join(implicit user: User, session: Session) = Groups.join(user.id, id)

  def leave(implicit user: User, session: Session) = Groups.leave(user.id, id)

  def enrolled(implicit user: User, session: Session) = students.contains(user)
}

object Groups {

  // ======= CREATE ======
  def create(assignmentGroupTmp: GroupTmp)(implicit session: Session) = assignmentGroupTmp((new AssignmentGroupsTable).insert(assignmentGroupTmp))

  // ======= FIND ======
  def apply(assignmentGroupId: GroupId)(implicit session: Session) = Query(new AssignmentGroupsTable).where(a => a.id === assignmentGroupId).firstOption

  def apply(assignmentId: AssignmentId)(implicit session: Session) = Query(new AssignmentGroupsTable).where(a => a.assignmentId === assignmentId).sortBy(_.name).list

  def apply(sectionId: SectionId, assignmentId: AssignmentId)(implicit session: Session) = Query(new AssignmentGroupsTable).where(a => a.sectionId === sectionId && a.assignmentId === assignmentId).sortBy(_.name).list

  def students(assignmentGroupId: GroupId)(implicit session: Session) =
    (for (
      u <- (new UserTable);
      ug <- (new UsersAssignmentGroupsTable) if ug.userId === u.id && ug.id === assignmentGroupId
    ) yield u).sortBy(_.lastName).list

  // ======= ENROLLMENT ======
  def enrolled(userId: UserId, assignmentId: AssignmentId)(implicit session: Session): Option[Group] =
    (for (
      g <- (new AssignmentGroupsTable) if g.assignmentId === assignmentId;
      ug <- (new UsersAssignmentGroupsTable) if ug.id === g.id && ug.userId === userId
    ) yield g).firstOption

  def join(userId: UserId, assignmentGroupId: GroupId)(implicit session: Session) = (new UsersAssignmentGroupsTable).insert(User2AssignmentGroup(userId, assignmentGroupId))

  def leave(userId: UserId, assignmentGroupId: GroupId)(implicit session: Session) = (new UsersAssignmentGroupsTable).where(r => r.userId === userId && r.id === assignmentGroupId).delete
}
