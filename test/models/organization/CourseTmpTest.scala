package models.organization

import models.support._
import org.joda.time.DateTime

object CourseTmpTest {
	def apply(name: String = "course", 
		owner: UserId, 
		editCode: String = "editCode", 
		viewCode: String = "viewCode", 
		date: DateTime = new DateTime(0L)) = CourseTmp(name, owner, editCode, viewCode, date)

}