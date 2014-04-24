package viewsupport.organization

import service.Access
import models.organization.assignment.Assignment

case class AssignmentDetails(assignment: Assignment, course: Course, access: Access)
