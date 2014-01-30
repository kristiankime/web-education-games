package models.organization.view

import models.organization.Course
import service.Access
import service.User

case class CourseDetails(c: Course, owner: User, a: Access, sections: List[SectionDetails])

object CourseDetails {
	def apply(v: (Course, User, Access), sections: List[SectionDetails]): CourseDetails = CourseDetails(v._1, v._2, v._3, sections)
}