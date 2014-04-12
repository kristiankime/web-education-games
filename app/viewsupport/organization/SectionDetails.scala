package models.organization.view

import models.organization._
import service.User
import service.Access
import models.question.derivative.view.UserQuizResults

case class SectionDetails(section: Section, course: Course, a: Access)

case class SectionResults(section: Section, studentResults: List[UserQuizResults])