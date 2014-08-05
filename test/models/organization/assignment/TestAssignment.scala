package models.organization.assignment

import com.artclod.slick.JodaUTC
import models.support.{UserId, CourseId}
import org.joda.time.{DateTimeZone, DateTime}

object TestAssignment {
  def apply(name: String = "section",
            courseId: CourseId,
            owner: UserId,
            date: DateTime = JodaUTC.zero,
            startDate : Option[DateTime] = None,
            endDate : Option[DateTime] = None) = Assignment(null, name, courseId, owner, date, date, startDate, endDate)
}
