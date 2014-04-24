package models.organization

import org.joda.time.DateTime
import models.support.{UserId, CourseId}
import models.organization.assignment.AssignmentTmp

object AssignmentTmpTest {
  def apply(name: String = "section",
            courseId: CourseId,
            owner: UserId,
            date: DateTime = new DateTime(0L),
            startDate : Option[DateTime] = None,
            endDate : Option[DateTime] = None) = AssignmentTmp(name, courseId, owner, date, startDate, endDate)
}
