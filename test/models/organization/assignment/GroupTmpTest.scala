package models.organization.assignment

import models.organization._
import models.support.{AssignmentId, SectionId, UserId}
import org.joda.time.{DateTime, DateTimeZone}
import play.api.db.slick.Config.driver.simple._

object GroupTmpTest {
  def apply(name: String = "group",
            sectionId: SectionId,
            assignmentId: AssignmentId,
            date: DateTime = new DateTime(0L, DateTimeZone.UTC)) : Group = Group(null, name, sectionId, assignmentId, date, date)

  def apply(userId: UserId)(implicit session: Session) : (Course, Section, Assignment, Group) = {
    val course = Courses.create(CourseTmpTest(owner = userId))
    val section = Sections.create(SectionTmpTest(owner = userId, courseId = course.id))
    val assignment = Assignments.create(AssignmentTmpTest(owner = userId, courseId = course.id))
    val group = Groups.create(GroupTmpTest(sectionId = section.id, assignmentId = assignment.id))
    (course, section, assignment, group)
  }
}
