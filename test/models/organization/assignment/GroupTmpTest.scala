package models.organization.assignment

import models.support.{AssignmentId, SectionId, UserId, CourseId}
import org.joda.time.DateTime

object GroupTmpTest {
  def apply(name: String = "group",
            sectionId: SectionId,
            assignmentId: AssignmentId,
            date: DateTime = new DateTime(0L)) = GroupTmp(name, sectionId, assignmentId, date, date)
}
