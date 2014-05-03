package models.organization.assignment

import org.joda.time.DateTime
import service._
import models.support._
import models.organization.table._
import play.api.db.slick.Config.driver.simple._
import models.organization.assignment.table._
import service.table.UserTable
import viewsupport.organization.assignment.GroupDetails
import viewsupport.organization.SectionGroupDetails
import models.organization.Sections
import viewsupport.organization.assignment.SectionAssignmentDetails

case class GroupTmp(name: String, sectionId: SectionId, assignmentId: AssignmentId, creationDate: DateTime, updateDate: DateTime) {
  def apply(id: GroupId) = Group(id, name, sectionId, assignmentId, creationDate, updateDate)
}

case class Group(id: GroupId, name: String, sectionId: SectionId, assignmentId: AssignmentId, creationDate: DateTime, updateDate: DateTime) {

  def assignment(implicit session: Session) = Assignments.find(assignmentId).get

  def course(implicit session: Session) = assignment.course

  def details(implicit session: Session) = GroupDetails(this, students)

  def students(implicit session: Session) = Groups.students(id)

  def access(implicit user: User, session: Session): Access = assignment.access

}

object Groups {

  // ======= CREATE ======
  def create(assignmentGroupTmp: GroupTmp)(implicit session: Session) = ((new AssignmentGroupsTable).insert(assignmentGroupTmp))

  // ======= FIND ======
  def find(assignmentGroupId: GroupId)(implicit session: Session) = Query(new AssignmentGroupsTable).where(a => a.id === assignmentGroupId).firstOption

  def find(assignmentId: AssignmentId)(implicit session: Session) = Query(new AssignmentGroupsTable).where(a => a.assignmentId === assignmentId).sortBy(_.name).list

  def find(sectionId: SectionId, assignmentId: AssignmentId)(implicit session: Session) = Query(new AssignmentGroupsTable).where(a => a.sectionId === sectionId && a.assignmentId === assignmentId).sortBy(_.name).list

  def students(assignmentGroupId: GroupId)(implicit session: Session) =
    (for (
      u <- (new UserTable);
      ug <- (new UsersAssignmentGroupsTable) if ug.userId === u.id && ug.id === assignmentGroupId
    ) yield u).sortBy(_.lastName).list

  def details(sectionId: SectionId, assignmentId: AssignmentId)(implicit session: Session) =
    (Sections.find(sectionId), Assignments.find(assignmentId)) match {
      case (Some(section), Some(assignment)) => {
        if (section.courseId != assignment.courseId) None
        else {
          val groups = Query(new AssignmentGroupsTable).where(r => r.sectionId === sectionId && r.assignmentId === assignmentId).list
          Some(SectionAssignmentDetails(section.course, section, assignment, groups))
        }
      }
      case _ => None
    }

  // ======= Update ======
  def join(userId: UserId, assignmentGroupId: GroupId)(implicit session: Session) = (new UsersAssignmentGroupsTable).insert(User2AssignmentGroup(userId, assignmentGroupId))

}
