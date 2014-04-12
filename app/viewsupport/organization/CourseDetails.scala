package viewsupport.organization

import models.organization.Course
import service.Access
import service.User

case class CourseDetails(c: Course, a: Access, sections: List[SectionDetails])
