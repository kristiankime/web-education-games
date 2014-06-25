package viewsupport.organization

import viewsupport.question.derivative.StudentQuizResults
import models.organization.Section

case class SectionResults(section: Section, studentResults: List[StudentQuizResults])