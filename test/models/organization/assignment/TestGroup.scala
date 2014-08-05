package models.organization.assignment

import com.artclod.slick.JodaUTC
import models.organization._
import models.support.{AssignmentId, SectionId, UserId}
import org.joda.time.{DateTime, DateTimeZone}
import play.api.db.slick.Config.driver.simple._

object TestGroup {
  def apply(name: String = "group",
            sectionId: SectionId,
            assignmentId: AssignmentId,
            date: DateTime = JodaUTC.zero) : Group = Group(null, name, sectionId, assignmentId, date, date)

  def apply(userId: UserId)(implicit session: Session) : (Course, Section, Assignment, Group) = {
    val course = Courses.create(TestCourse(owner = userId))
    val section = Sections.create(TestSection(owner = userId, courseId = course.id))
    val assignment = Assignments.create(TestAssignment(owner = userId, courseId = course.id))
    val group = Groups.create(TestGroup(sectionId = section.id, assignmentId = assignment.id))
    (course, section, assignment, group)
  }
}
