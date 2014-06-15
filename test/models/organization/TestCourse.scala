package models.organization

import com.artclod.slick.Joda
import models.support._
import org.joda.time.{DateTimeZone, DateTime}

object TestCourse {
	def apply(name: String = "course", 
		owner: UserId, 
		editCode: String = "editCode", 
		viewCode: String = "viewCode", 
		date: DateTime = Joda.zero) = Course(null, name, owner, editCode, viewCode, date, date)

}