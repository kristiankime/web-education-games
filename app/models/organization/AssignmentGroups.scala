package models.organization

import org.joda.time.DateTime
import scala.slick.session.Session

import service._
import models.support._
import models.organization.table._

import play.api.db.slick.Config.driver.simple._

case class AssignmentGroupTmp(name: String, assignmentId: AssignmentId, owner: UserId, date: DateTime) {
  def apply(id: AssignmentGroupId) = AssignmentGroup(id, name, assignmentId, owner, date, date)
}

case class AssignmentGroup(id: AssignmentGroupId, name: String, assignmentId: AssignmentId, owner: UserId, creationDate: DateTime, updateDate: DateTime) extends Secured {

  def assignment(implicit session: Session) = Assignments.find(assignmentId).get

  def course(implicit session: Session) = assignment.course

  def access(implicit user: User, session: Session): Access = assignment.access

  protected def linkAccess(implicit user: User, session: Session): Access = Non

}


object AssignmentGroups {

  // ======= CREATE ======
  def create(assignmentGroupTmp: AssignmentGroupTmp)(implicit session: Session) = ((new AssignmentGroupsTable).insert(assignmentGroupTmp))

  // ======= FIND ======
  def find(assignmentGroupId: AssignmentGroupId)(implicit session: Session) = Query(new AssignmentGroupsTable).where(a => a.id === assignmentGroupId).firstOption

}