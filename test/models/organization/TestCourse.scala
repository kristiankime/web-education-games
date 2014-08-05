package models.organization

import com.artclod.slick.JodaUTC
import models.support._
import org.joda.time.{DateTimeZone, DateTime}

object TestCourse {
	def apply(name: String = "course", 
		owner: UserId, 
		editCode: String = "editCode", 
		viewCode: String = "viewCode", 
		date: DateTime = JodaUTC.zero) = Course(null, name, owner, editCode, viewCode, date, date)

}