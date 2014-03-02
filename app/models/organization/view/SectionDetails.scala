package models.organization.view

import models.organization.Section
import service.User
import service.Access
import models.question.derivative.view.StudentResults

case class SectionDetails(section: Section, a: Access)

case class SectionResults(section: Section, studentResults: List[StudentResults])