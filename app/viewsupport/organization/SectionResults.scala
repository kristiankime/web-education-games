package viewsupport.organization

import viewsupport.question.derivative.StudentQuizResult
import models.organization.Section

case class SectionResults(section: Section, studentResults: List[StudentQuizResult])