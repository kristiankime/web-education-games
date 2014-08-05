package models.organization

import com.artclod.slick.JodaUTC
import org.joda.time.{DateTimeZone, DateTime}
import models.support._

object TestSection {
	def apply(name: String = "section",
		courseId: CourseId,
		owner: UserId,
		editCode: String = "editCode",
		viewCode: String = "viewCode",
		date: DateTime = JodaUTC.zero) = Section(null, name, courseId, owner, editCode, viewCode, date, date)
}
