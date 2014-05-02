package viewsupport.organization.assignment

import service.Access
import models.organization.assignment.Assignment
import models.organization.Course

case class AssignmentDetails(assignment: Assignment, course: Course, access: Access)
