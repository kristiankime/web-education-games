package models.organization

import com.artclod.slick.JodaUTC
import models.support._
import org.joda.time.{DateTimeZone, DateTime}

object TestCourse {
	def apply(name: String = "course",
    organizationId: OrganizationId,
		owner: UserId, 
		editCode: String = "editCode", 
		viewCode: Option[String] = Some("viewCode"),
		date: DateTime = JodaUTC.zero) = Course(null, name, organizationId, owner, editCode, viewCode, date, date)

}