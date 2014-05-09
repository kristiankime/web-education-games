package models.organization.assignment

import models.support.{UserId, CourseId}
import org.joda.time.DateTime

object AssignmentTmpTest {
  def apply(name: String = "section",
            courseId: CourseId,
            owner: UserId,
            date: DateTime = new DateTime(0L),
            startDate : Option[DateTime] = None,
            endDate : Option[DateTime] = None) = AssignmentTmp(name, courseId, owner, date, startDate, endDate)
}