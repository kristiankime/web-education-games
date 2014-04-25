package models.organization.assignment

import play.api.db.slick.Config.driver.simple._
import models.organization.table._
import service._
import models.support._
import org.joda.time.DateTime
import viewsupport.organization._
import models.organization._
import models.organization.assignment.table._

case class AssignmentTmp(name: String, courseId: CourseId, owner: UserId, date: DateTime, startDate: Option[DateTime], endDate: Option[DateTime]) {
  def apply(id: AssignmentId) = Assignment(id, name, courseId, owner, date, date, startDate, endDate)
}

case class Assignment(id: AssignmentId, name: String, courseId: CourseId, owner: UserId, creationDate: DateTime, updateDate: DateTime, startDate: Option[DateTime], endDate: Option[DateTime]) extends Secured {

  def startAndEnd = (startDate, endDate)

  def details(implicit user: User, session: Session) = AssignmentDetails(this, course, access, groups)

  def course(implicit session: Session) = Courses.find(courseId).get

  def groups(implicit session: Session) = AssignmentGroups.find(id)

  protected def linkAccess(implicit user: User, session: Session): Access = Non

  /**
   * In terms of access level Users can:
   *     1) Own the Section which grants Own access
   *     2) Have access to the Section's Course which provides a maximum of Edit access to the section
   */
  def access(implicit user: User, session: Session) = Seq(course.access.ceilEdit, directAccess).max
}

object Assignments {

  // ======= CREATE ======
  def create(assignmentTmp: AssignmentTmp)(implicit session: Session) = assignmentTmp((new AssignmentsTable).insert(assignmentTmp))

  // ======= FIND ======
//  def find(assignmentId: AssignmentId, courseId: CourseId)(implicit session: Session) = Query(new AssignmentsTable).where(a => a.id === assignmentId && a.courseId === courseId).firstOption
  def find(assignmentId: AssignmentId)(implicit session: Session) = Query(new AssignmentsTable).where(a => a.id === assignmentId).firstOption

  def find(courseId: CourseId)(implicit session: Session) = Query(new AssignmentsTable).where(_.courseId === courseId).list

}
