package viewsupport.organization

import models.organization.assignment.AssignmentGroup
import service.User

case class GroupDetails(group: AssignmentGroup, students: List[User])