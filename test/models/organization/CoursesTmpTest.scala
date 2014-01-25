package models.organization

import models.id._
import org.joda.time.DateTime

object CoursesTmpTest {
	def apply(name: String = "course",
		owner: UserId,
		editCode: String = "editCode",
		viewCode: String = "viewCode",
		date: DateTime = new DateTime(0L)) = CourseTmp(name, owner, editCode, viewCode, date)

}