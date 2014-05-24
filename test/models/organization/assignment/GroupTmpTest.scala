package models.organization.assignment

import models.support.{AssignmentId, SectionId}
import org.joda.time.{DateTimeZone, DateTime}

object GroupTmpTest {
  def apply(name: String = "group",
            sectionId: SectionId,
            assignmentId: AssignmentId,
            date: DateTime = new DateTime(0L, DateTimeZone.UTC)) = Group(null, name, sectionId, assignmentId, date, date)
}
