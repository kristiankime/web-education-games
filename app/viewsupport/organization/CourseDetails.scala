package viewsupport.organization

import service.Access

case class CourseDetails(c: Course, a: Access, sections: List[SectionDetails])
