package viewsupport.organization

import service.Access

case class CourseDetail(c: Course, a: Access, sections: List[SectionDetail])
