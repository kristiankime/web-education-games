package viewsupport.organization.assignment

import models.organization.Section
import models.organization.assignment.Group
import models.organization.assignment.Assignment
import models.organization.Course

case class SectionAssignmentDetails(course: Course, section: Section, assignment: Assignment, groups: List[Group])