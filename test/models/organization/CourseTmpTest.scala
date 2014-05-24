package models.organization

import models.support._
import org.joda.time.{DateTimeZone, DateTime}

object CourseTmpTest {
	def apply(name: String = "course", 
		owner: UserId, 
		editCode: String = "editCode", 
		viewCode: String = "viewCode", 
		date: DateTime = new DateTime(0L, DateTimeZone.UTC)) = Course(null, name, owner, editCode, viewCode, date, date)

}