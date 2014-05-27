package models.organization.assignment

import play.api.db.slick.Config.driver.simple._
import service._
import models.support._
import org.joda.time.DateTime
import models.organization._
import models.organization.assignment.table._

case class Assignment(id: AssignmentId, name: String, courseId: CourseId, ownerId: UserId, creationDate: DateTime, updateDate: DateTime, startDate: Option[DateTime], endDate: Option[DateTime]) extends Secured {

  def startAndEnd = (startDate, endDate)

  def course(implicit session: Session) = Courses(courseId).get

  def groups(implicit session: Session) = Groups(id)

  def enrolled(implicit user: User, session: Session) = Groups.enrolled(user.id, id)

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
  def create(assignment: Assignment)(implicit session: Session) = {
    val assignmentId = (assignmentsTable returning assignmentsTable.map(_.id)) += assignment
    assignment.copy(id = assignmentId)
  }

  // ======= FIND ======
  def apply(assignmentId: AssignmentId)(implicit session: Session) = assignmentsTable.where(a => a.id === assignmentId).firstOption

  def apply(courseId: CourseId)(implicit session: Session) = assignmentsTable.where(_.courseId === courseId).list

}
