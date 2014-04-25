package viewsupport.organization

import service.Access
import models.organization.assignment.{AssignmentGroup, Assignment}

case class AssignmentDetails(assignment: Assignment, course: Course, access: Access, groups: List[AssignmentGroup])
