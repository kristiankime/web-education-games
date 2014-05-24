package models.organization

import org.joda.time.{DateTimeZone, DateTime}
import models.support._

object SectionTmpTest {
	def apply(name: String = "section",
		courseId: CourseId,
		owner: UserId,
		editCode: String = "editCode",
		viewCode: String = "viewCode",
		date: DateTime = new DateTime(0L, DateTimeZone.UTC)) = Section(null, name, courseId, owner, editCode, viewCode, date, date)
}
