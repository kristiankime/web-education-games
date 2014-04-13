package viewsupport.organization

import service.Access
import viewsupport.question.derivative.UserQuizResults

case class SectionDetails(section: Section, course: Course, a: Access)

case class SectionResults(section: Section, studentResults: List[UserQuizResults])