package viewsupport.organization

import service.Access
import viewsupport.question.derivative.UserQuizResults
import models.organization.Section

case class SectionResults(section: Section, studentResults: List[UserQuizResults])